package com.kcube.cloud.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;

public class OAuth2SecurityAuthenticationProcessingFilter extends OAuth2ClientAuthenticationProcessingFilter
{
//	@Autowired
//	private OAuth2ClientContext oauth2Context;

	public OAuth2SecurityAuthenticationProcessingFilter(String defaultFilterProcessesUrl)
	{
		super(defaultFilterProcessesUrl);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException,
			IOException,
			ServletException
	{
//		String createRefreshToken = request.getParameter("createRefreshToken");
//		if (createRefreshToken != null && Boolean.valueOf(createRefreshToken))
//		{
//			oauth2Context.getAccessTokenRequest().setAuthorizationCode(null);
//			oauth2Context.getAccessTokenRequest().setStateKey(null);
//			oauth2Context.getAccessTokenRequest().set(OAuth2Utils.USER_OAUTH_APPROVAL, "consent");
//			oauth2Context.setAccessToken(null);
//		}
		
		return super.attemptAuthentication(request, response);
	}
}
