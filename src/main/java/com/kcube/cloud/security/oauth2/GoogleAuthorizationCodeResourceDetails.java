package com.kcube.cloud.security.oauth2;

import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

public class GoogleAuthorizationCodeResourceDetails extends AuthorizationCodeResourceDetails
{
	@Override
	public String getRedirectUri(AccessTokenRequest request)
	{
		String redirectUri = super.getRedirectUri(request);
		return (redirectUri.indexOf("?") > -1) ? redirectUri.substring(0, redirectUri.indexOf("?")) : redirectUri;
	}
}
