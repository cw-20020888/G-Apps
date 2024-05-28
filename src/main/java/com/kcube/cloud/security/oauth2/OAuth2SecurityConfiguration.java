package com.kcube.cloud.security.oauth2;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@Configuration
@EnableOAuth2Client
class OAuth2SecurityConfiguration
{
	@Autowired
	private OAuth2ClientContext oauth2Context;

	@Autowired
	@Qualifier("appProp")
	private Properties googleAuthProp;

	@Bean
	public OAuth2ProtectedResourceDetails googleResource()
	{
		GoogleAuthorizationCodeResourceDetails details = new GoogleAuthorizationCodeResourceDetails();
		details.setId("google-oauth-client");
		details.setClientId(googleAuthProp.getProperty("google.client.id"));
		details.setClientSecret(googleAuthProp.getProperty("google.client.secret"));
		details.setAccessTokenUri(googleAuthProp.getProperty("google.accessTokenUri"));
		details.setUserAuthorizationUri(googleAuthProp.getProperty("google.userAuthorizationUri"));
		details.setTokenName(googleAuthProp.getProperty("google.authorization.code"));
		String commaSeparatedScopes = googleAuthProp.getProperty("google.auth.scope");
		details.setScope(parseScopes(commaSeparatedScopes));
		// details.setPreEstablishedRedirectUri(oAuthClientProp.getProperty("google.preestablished.redirect.url"));
		// details.setUseCurrentUri(false);
		details.setAuthenticationScheme(AuthenticationScheme.query);
		details.setClientAuthenticationScheme(AuthenticationScheme.form);

		return details;
	}

	@Bean
	public GoogleOAuth2RestTemplate googleRestTemplate()
	{
		return new GoogleOAuth2RestTemplate(googleResource(), oauth2Context);
	}

	private List<String> parseScopes(String commaSeparatedScopes)
	{
		List<String> scopes = newArrayList();
		Collections.addAll(scopes, commaSeparatedScopes.split(","));

		return scopes;
	}
}
