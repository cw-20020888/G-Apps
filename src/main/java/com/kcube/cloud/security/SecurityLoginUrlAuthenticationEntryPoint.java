package com.kcube.cloud.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class SecurityLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint
{
	@Deprecated
	public SecurityLoginUrlAuthenticationEntryPoint()
	{
	}

	public SecurityLoginUrlAuthenticationEntryPoint(String loginFormUrl)
	{
		super(loginFormUrl);
	}

	@Override
	public void commence(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException
	{
		if (HttpMethod.OPTIONS.name().equals(request.getMethod()))
		{
			response.setStatus(HttpStatus.OK.value());
		}
		else
		{
			super.commence(request, response, authException);
		}
	}
}
