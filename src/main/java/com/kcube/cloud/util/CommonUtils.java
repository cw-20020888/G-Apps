package com.kcube.cloud.util;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.kcube.cloud.error.DefaultException;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class CommonUtils
{
	private final static String serverDateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

	@SuppressWarnings("unchecked")
	public static String getLocaleStr(String value)
	{
		try
		{
			if (value != null && value.startsWith("{"))
			{
				Map<String, String> localeMap = (Map<String, String>) new ObjectMapper()
					.readValue(value, new TypeReference<Map<String, String>>()
					{
					});

				return !StringUtils.isEmpty(localeMap.get(LocaleContextHolder.getLocale().getLanguage()))
					? localeMap.get(LocaleContextHolder.getLocale().getLanguage())
					: localeMap.get(Locale.KOREAN.getLanguage());
			}
			return value;
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	public static String substring(String value, int beginIndex, int endIndex)
	{
		return value != null ? getLocaleStr(value).substring(beginIndex, endIndex) : value;
	}

	public static String getLocaleLevelNames(String levelNames)
	{
		String[] sArr = levelNames.split(">");
		for (int i = 0; i < sArr.length; i++)
		{
			sArr[i] = getLocaleStr(sArr[i]);
		}
		return Joiner.on(" > ").join(sArr);
	}

	public static boolean isValidJson(String s)
	{
		try
		{
			JSONObject.fromObject(s);
		}
		catch (JSONException e)
		{
			try
			{
				JSONArray.fromObject(s);
			}
			catch (JSONException e1)
			{
				return false;
			}
		}
		return true;
	}

	public static List<?> subList(List<?> list, int fromIndex, int toIndex)
	{
		if (list.size() < toIndex)
		{
			toIndex = list.size();
		}
		return list.subList(fromIndex, toIndex);
	}

	public static Locale toLocale(String localeStr)
	{
		String[] locale = localeStr.split("_");
		return new Locale(locale[0], locale[1]);
	}

	public static String getDomainBy(String email)
	{
		if (email == null)
		{
			throw new NullPointerException("required email.");
		}
		if (!email.contains("@"))
		{
			throw new IllegalArgumentException("missing '@'. email : " + email);
		}
		return email.split("@")[1];
	}

	public static String getServerNameFromMacAddress()
	{
		try
		{
			byte[] mac = null;
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface iface : interfaces)
			{
				if (!iface.isLoopback())
				{
					mac = iface.getHardwareAddress();
					if (mac != null)
					{
						break;
					}
				}
			}

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++)
			{
				sb.append(String.format("%02X%s", mac[i], ""));
			}

			return sb.toString();

		}
		catch (SocketException e)
		{
			throw new DefaultException(e);
		}
	}

	public static String getServerDate()
	{
		return CommonUtils.serverDateStr;
	}
}
