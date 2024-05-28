package com.kcube.cloud.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringUtils;

import com.kcube.cloud.util.SecureUtils;

public class HttpSecurityRequestWrapper extends HttpServletRequestWrapper
{
	public HttpSecurityRequestWrapper(HttpServletRequest request)
	{
		super(request);
	}

	@Override
	public String[] getParameterValues(String parameter)
	{
		String[] values = super.getParameterValues(parameter);
		if (values == null)
		{
			return null;
		}

		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++)
		{
			encodedValues[i] = stripValue(values[i]);
		}

		return encodedValues;
	}

	@Override
	public String getParameter(String parameter)
	{
		String value = super.getParameter(parameter);

		return stripValue(value);
	}

	@Override
	public String getHeader(String name)
	{
		String value = super.getHeader(name);

		return stripValue(value);
	}

	private String stripValue(String value)
	{
		if (StringUtils.isNotEmpty(value) && !isJson(value))
		{
			value = value.replaceAll("\0", "");
			value = SecureUtils.XSSFilter(value);
			value = SecureUtils.FileSystemFilter(value);
			value = SecureUtils.HttpResponseFilter(value);
		}
		return value;
	}

	private boolean isJson(String value)
	{
		return value.startsWith("{") || value.startsWith("[");
	}
}