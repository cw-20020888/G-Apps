package com.kcube.cloud.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;

public class GoogleLoginUrlAuthenticationEntryPoint extends SecurityLoginUrlAuthenticationEntryPoint
{
	@Deprecated
	public GoogleLoginUrlAuthenticationEntryPoint()
	{
	}

	public GoogleLoginUrlAuthenticationEntryPoint(String loginFormUrl)
	{
		super(loginFormUrl);
	}

	@Override
	public String determineUrlToUseForThisRequest(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException exception)
	{
		return getLoginFormUrl() + ((request.getQueryString() != null) ? "?" + request.getQueryString() : "");
	}
}
