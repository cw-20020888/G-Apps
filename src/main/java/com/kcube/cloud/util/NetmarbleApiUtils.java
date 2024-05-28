package com.kcube.cloud.util;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;

import com.kcube.cloud.error.DefaultException;

public class NetmarbleApiUtils
{
	public static HttpGet getHttpGet(String schema, String host, String path, String query)
	{
		return getHttpGet(schema, host, path, query, "GAPPS", "LFM0YOcXARK8FywjhNJrhJOvhg5+er76NjsJTpaDbKA=");
	}

	public static HttpGet getHttpGet(
		String schema,
		String host,
		String path,
		String query,
		String headerName,
		String headerValue)
	{
		try
		{
			HttpGet httpGet = new HttpGet(URIUtils.createURI(schema, host, -1, path, query, null));

			httpGet.setHeader(headerName, headerValue);

			return httpGet;
		}
		catch (URISyntaxException e)
		{
			throw new DefaultException(e);
		}
	}

	public static HttpPost getHttpPost(String schema, String host, String path, List<NameValuePair> arguments)
	{
		return getHttpPost(schema, host, path, arguments, "GAPPS", "LFM0YOcXARK8FywjhNJrhJOvhg5+er76NjsJTpaDbKA=");
	}

	public static HttpPost getHttpPost(
		String schema,
		String host,
		String path,
		List<NameValuePair> arguments,
		String headerName,
		String headerValue)
	{
		try
		{
			HttpPost httpPost = new HttpPost(URIUtils.createURI(schema, host, -1, path, null, null));

			httpPost.setEntity(new UrlEncodedFormEntity(arguments, "UTF-8"));
			httpPost.setHeader(headerName, headerValue);

			return httpPost;
		}
		catch (UnsupportedEncodingException | URISyntaxException e)
		{
			throw new DefaultException(e);
		}
	}
}
