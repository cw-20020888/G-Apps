package com.kcube.cloud.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Deprecated
public class GoogleAuthenticationSuccessHandler implements AuthenticationSuccessHandler
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SecurityAuthenticationSuccessHandler securityAuthenticationSuccessHandler;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException
	{
		securityAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
	}
}