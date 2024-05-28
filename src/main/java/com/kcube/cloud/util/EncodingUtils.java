package com.kcube.cloud.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.kcube.cloud.error.DefaultException;

public class EncodingUtils
{
	public static class Base64
	{
		public static String encodeString(String str)
		{
			return org.apache.commons.codec.binary.Base64.encodeBase64String(str.getBytes());
		}

		public static byte[] encodeByteArray(String str)
		{
			return org.apache.commons.codec.binary.Base64.encodeBase64(str.getBytes());
		}

		public static String decodeString(String str)
		{
			return new String(org.apache.commons.codec.binary.Base64.decodeBase64(str.getBytes()));
		}

		public static byte[] decodeByteArray(String str)
		{
			return org.apache.commons.codec.binary.Base64.decodeBase64(str);
		}
	}

	public static class URL
	{
		public static String encode(String str)
		{
			try
			{
				return URLEncoder.encode(str, "utf-8");
			}
			catch (UnsupportedEncodingException e)
			{
				throw new DefaultException(e);
			}
		}

		public static String decode(String str)
		{
			try
			{
				return URLDecoder.decode(str, "utf-8");
			}
			catch (UnsupportedEncodingException e)
			{
				throw new DefaultException(e);
			}
		}
	}
}
