package com.kcube.cloud.http;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class HttpCORSFilter extends OncePerRequestFilter
{
	private String allowOrigin;

	public void setAllowOrigin(String allowOrigin)
	{
		this.allowOrigin = allowOrigin;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException,
			IOException
	{
		// TODO Auto-generated method stub
		response.setHeader("Access-Control-Allow-Origin", this.allowOrigin);
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader(
			"Access-Control-Allow-Headers",
			"X-Requested-With, Origin, Content-Type, Accept, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization");

		filterChain.doFilter(request, response);
	}
}
