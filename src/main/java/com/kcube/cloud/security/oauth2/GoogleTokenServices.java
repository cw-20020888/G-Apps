/*******************************************************************************
 * Cloud Foundry Copyright (c) [2009-2014] Pivotal Software, Inc. All Rights Reserved.
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License. This product
 * includes a number of subcomponents with separate copyright notices and license terms.
 * Your use of these subcomponents is subject to the terms and conditions of the
 * subcomponent's license, as noted in the LICENSE file.
 *******************************************************************************/
package com.kcube.cloud.security.oauth2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Copied from Spring Security OAuth2 to support the custom format for a Google Token
 * which is different from what Spring supports
 */
public class GoogleTokenServices extends RemoteTokenServices
{
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private RestOperations restTemplate;

	private String checkTokenEndpointUrl;

	private String clientId;

	private String clientSecret;

	private AccessTokenConverter tokenConverter = new GoogleAccessTokenConverter();

//	@Autowired
//	private OAuth2ClientContext oauth2Context;
//
//	@Autowired
//	private CipherUtils cipherUtils;

	public GoogleTokenServices()
	{
		restTemplate = new RestTemplate();
		((RestTemplate) restTemplate).setErrorHandler(new DefaultResponseErrorHandler()
		{
			@Override
			// Ignore 400
			public void handleError(ClientHttpResponse response) throws IOException
			{
				if (response.getRawStatusCode() != 400)
				{
					super.handleError(response);
				}
			}
		});
	}

	@Override
	public void setRestTemplate(RestOperations restTemplate)
	{
		this.restTemplate = restTemplate;
	}

	@Override
	public void setCheckTokenEndpointUrl(String checkTokenEndpointUrl)
	{
		this.checkTokenEndpointUrl = checkTokenEndpointUrl;
	}

	@Override
	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}

	@Override
	public void setClientSecret(String clientSecret)
	{
		this.clientSecret = clientSecret;
	}

	@Override
	public void setAccessTokenConverter(AccessTokenConverter accessTokenConverter)
	{
		this.tokenConverter = accessTokenConverter;
	}

	@Override
	public OAuth2Authentication loadAuthentication(String accessToken)
		throws AuthenticationException,
			InvalidTokenException
	{
		Map<String, Object> checkTokenResponse = checkToken(accessToken);

		LOGGER.debug("Original map = " + checkTokenResponse);

		if (checkTokenResponse.containsKey("error"))
		{
			logger.debug("check_token returned error: " + checkTokenResponse.get("error"));
			throw new InvalidTokenException(accessToken);
		}

//		google Api 사용에 refreshToken 사용 안함
//		String encryptedEmail = cipherUtils.encryptToHex((String) checkTokenResponse.get("email"));
//		OAuth2AccessToken oAuth2AccessToken = oauth2Context.getAccessToken();
//		if (oAuth2AccessToken != null && !oAuth2AccessToken.isExpired() && oAuth2AccessToken.getRefreshToken() != null)
//		{
//			saveRefreshToken(encryptedEmail, oAuth2AccessToken.getRefreshToken().getValue());
//		}
//		else
//		{
//			String existRefreshToken = retrieveRefreshToken(encryptedEmail);
//			if (StringUtils.isEmpty(existRefreshToken))
//			{
//				throw new InsufficientAuthenticationException("refresh token not exist");
//			}
//		}

		transformNonStandardValuesToStandardValues(checkTokenResponse);

		Assert.state(
			checkTokenResponse.containsKey("client_id"),
			"Client id must be present in response from auth server");

		return tokenConverter.extractAuthentication(checkTokenResponse);
	}

//	private void saveRefreshToken(String encryptedEamil, String refreshToken)
//	{
//		Map<String, String> fields = new HashMap<String, String>();
//		fields.put("refreshToken", refreshToken);
//
//		AppengineDataStoreApi datastore = new AppengineDataStoreApi(DataStoreKindEnum.REFRESH_TOKEN, encryptedEamil);
//		datastore.put(fields);
//	}
//
//	private String retrieveRefreshToken(String encryptedEamil)
//	{
//		AppengineDataStoreApi datastore = new AppengineDataStoreApi(DataStoreKindEnum.REFRESH_TOKEN, encryptedEamil);
//		Entity entity = datastore.get();
//
//		return entity != null ? (String) entity.getProperty("refreshToken") : null;
//	}

	public Map<String, Object> checkToken(String accessToken)
	{
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("token", accessToken);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", getAuthorizationHeader(clientId, clientSecret));
		String accessTokenUrl = new StringBuilder(checkTokenEndpointUrl)
			.append("?access_token=").append(accessToken).toString();
		return postForMap(accessTokenUrl, formData, headers);
	}

	private void transformNonStandardValuesToStandardValues(Map<String, Object> map)
	{
		LOGGER.debug("Original map = " + map);
		map.put("client_id", map.get("issued_to")); // Google sends 'client_id' as
													// 'issued_to'
		map.put("user_name", map.get("user_id")); // Google sends 'user_name' as 'user_id'
		LOGGER.debug("Transformed = " + map);
	}

	private String getAuthorizationHeader(String clientId, String clientSecret)
	{
		String creds = String.format("%s:%s", clientId, clientSecret);
		try
		{
			return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new IllegalStateException("Could not convert String");
		}
	}

	private Map<String, Object> postForMap(String path, MultiValueMap<String, String> formData, HttpHeaders headers)
	{
		if (headers.getContentType() == null)
		{
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		}
		ParameterizedTypeReference<Map<String, Object>> map = new ParameterizedTypeReference<Map<String, Object>>()
		{
		};
		return restTemplate.exchange(path, HttpMethod.POST, new HttpEntity<>(formData, headers), map).getBody();
	}
}
