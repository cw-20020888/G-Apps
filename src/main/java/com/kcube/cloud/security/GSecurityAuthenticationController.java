package com.kcube.cloud.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GSecurityAuthenticationController
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@RequestMapping(value = "/logoutSuccess")
	public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		redirectStrategy
			.sendRedirect(request, response, "/cwlogin");
	}
}
