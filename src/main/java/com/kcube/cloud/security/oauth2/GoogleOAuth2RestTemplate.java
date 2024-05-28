package com.kcube.cloud.security.oauth2;

import java.util.Arrays;

import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;

public class GoogleOAuth2RestTemplate extends OAuth2RestTemplate
{
	public GoogleOAuth2RestTemplate(OAuth2ProtectedResourceDetails resource)
	{
		super(resource);
		super.setAccessTokenProvider(new AccessTokenProviderChain(Arrays.<AccessTokenProvider> asList(
			new GoogleAuthorizationCodeAccessTokenProvider(),
			new ImplicitAccessTokenProvider(),
			new ResourceOwnerPasswordAccessTokenProvider(),
			new ClientCredentialsAccessTokenProvider())));
	}

	public GoogleOAuth2RestTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context)
	{
		super(resource, context);
		super.setAccessTokenProvider(new AccessTokenProviderChain(Arrays.<AccessTokenProvider> asList(
			new GoogleAuthorizationCodeAccessTokenProvider(),
			new ImplicitAccessTokenProvider(),
			new ResourceOwnerPasswordAccessTokenProvider(),
			new ClientCredentialsAccessTokenProvider())));
	}
}
