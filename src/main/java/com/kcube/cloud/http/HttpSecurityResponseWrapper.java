package com.kcube.cloud.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.kcube.cloud.util.SecureUtils;

public class HttpSecurityResponseWrapper extends HttpServletResponseWrapper
{
	public HttpSecurityResponseWrapper(HttpServletResponse response)
	{
		super(response);
	}

	@Override
	public void sendRedirect(String location) throws IOException
	{
		super.sendRedirect(SecureUtils.HttpResponseFilter(location));
	}
}
