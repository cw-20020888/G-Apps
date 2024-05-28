package com.kcube.cloud.http;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class HttpSecurityFilter extends OncePerRequestFilter
{
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException,
			IOException
	{
		if (excludeUrl(request))
		{
			filterChain.doFilter(request, response);
		}
		else
		{
			filterChain.doFilter(new HttpSecurityRequestWrapper(request), new HttpSecurityResponseWrapper(response));

		}
	}

	private boolean excludeUrl(HttpServletRequest request)
	{
		return request.getRequestURI().replace(request.getContextPath(), "").startsWith("/webdav");
	}
}
