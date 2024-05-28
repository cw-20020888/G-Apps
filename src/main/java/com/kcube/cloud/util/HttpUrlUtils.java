package com.kcube.cloud.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import com.kcube.cloud.error.DefaultException;

public class HttpUrlUtils
{
	public static String makeUrl(HttpServletRequest request, String relativePath)
	{
		String url = (request.isSecure() ? "https" : "http")
			+ "://"
			+ request.getServerName()
			+ ":"
			+ request.getServerPort()
			+ ((relativePath.indexOf(request.getContextPath()) > -1)
				? relativePath
				: (request.getContextPath() + relativePath));

		return url;
	}

	public static String loadUrlToString(HttpServletRequest request, String relativePath)
	{
		return loadUrlToString(makeUrl(request, relativePath), request.getCharacterEncoding());
	}

	public static String loadUrlToString(String targetUrl)
	{
		return loadUrlToString(targetUrl, "UTF-8");
	}

	public static String loadUrlToString(String targetUrl, String charsetName)
	{
		BufferedReader br = null;
		try
		{
			URL url = new URL(targetUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charsetName));

				StringBuffer sb = new StringBuffer();
				String inputLine;
				while ((inputLine = br.readLine()) != null)
				{
					sb.append(inputLine);
				}

				return sb.toString();
			}
		}
		catch (IOException e)
		{
			return null;
		}
		finally
		{
			try
			{
				if (br != null)
				{
					br.close();
				}
			}
			catch (IOException e2)
			{
				throw new DefaultException(e2);
			}
		}

		return null;
	}

	public static InputStream loadUrlToInputStream(HttpServletRequest request, String relativePath)
	{
		return loadUrlToInputStream(makeUrl(request, relativePath));
	}

	public static InputStream loadUrlToInputStream(String connectUrl)
	{
		try
		{
			URL url = new URL(connectUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				return conn.getInputStream();
			}
		}
		catch (IOException e)
		{
			return null;
		}

		return null;
	}
}
