package com.kcube.cloud.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Deprecated
public class GoogleAuthenticationFailureHandler implements AuthenticationFailureHandler
{
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationFailure(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException exception)
		throws IOException,
			ServletException
	{
		// TODO Auto-generated method stub
		if (exception instanceof InsufficientAuthenticationException)
		{
			redirectStrategy.sendRedirect(request, response, "/googleLogin?createRefreshToken=true");
		}
	}

}
