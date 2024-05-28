package com.kcube.cloud.util;

import org.apache.commons.lang.StringUtils;

/**
 * 보안 관련 필터 Utils Class
 * <p>
 * <ul>
 * <li>XSS (Cross-Site Scripting) Filter
 * <li>SQL Injection Filter
 * <li>HTTP Response Splitting
 * </ul>
 */
public class SecureUtils
{
	/**
	 * XSS (Cross-Site Scripting) Filter
	 * @param sInvalid XSS필터 적용전 Object
	 * @return XSS필터가 적용된 문자열
	 */
	public static String XSSFilter(Object sInvalid)
	{
		if (null == sInvalid)
		{
			return null;
		}
		return XSSFilter((String) sInvalid);
	}

	/**
	 * XSS (Cross-Site Scripting) Filter
	 * @param sInvalid XSS필터 적용전 문자열
	 * @return XSS필터가 적용된 문자열
	 */
	public static String XSSFilter(String sInvalid)
	{
		String sValid = sInvalid;

		if (StringUtils.isEmpty(sValid))
		{
			return sValid;
		}

		sValid = sValid.replaceAll("&", "&amp;");
		sValid = sValid.replaceAll("<", "&lt;");
		sValid = sValid.replaceAll(">", "&gt;");
		sValid = sValid.replaceAll("\"", "&quot;");

		return sValid;
	}

	/**
	 * SQL Injection Filter
	 * @param sInvalid SQL Injection 필터 적용전 Object
	 * @return SQL Injection 필터가 적용된 문자열
	 */
	public static String SQLInjectionFilter(Object sInvalid)
	{
		if (null == sInvalid)
		{
			return null;
		}
		return SQLInjectionFilter((String) sInvalid);
	}

	/**
	 * SQL Injection Filter
	 * <p>
	 * 파라미터 변수로 받은 값을 이용하여 쿼리를 구성할 때 필터처리한다. AND, OR, ;, -- 등의 문자가 삭제 처리된다.
	 * @param sInvalid SQL Injection 필터 적용전 문자열
	 * @return SQL Injection 필터가 적용된 문자열
	 */
	public static String SQLInjectionFilter(String sInvalid)
	{
		String sValid = sInvalid;

		if (StringUtils.isEmpty(sValid))
		{
			return sValid;
		}

		// 컬럼명이 OR, AND가 들어갈 경우 문제 발생. 임시주석처리.
		// sValid = sValid.replaceAll("[oO][rR]|[aA][nN][dD]|[;]|[:]|[-][-]", "");
		sValid = sValid.replaceAll("[;]|[:]|[-][-]", "");
		sValid = sValid.replaceAll("'", "''");
		return sValid;
	}

	/**
	 * HTTP Response Splitting
	 * @param sInvalid HTTP Response Splitting 필터 적용전 Object
	 * @return HTTP Response Splitting 필터가 적용된 문자열
	 */
	public static String HttpResponseFilter(Object sInvalid)
	{
		if (null == sInvalid)
		{
			return null;
		}
		return HttpResponseFilter((String) sInvalid);
	}

	/**
	 * HTTP Response Splitting
	 * @param sInvalid HTTP Response Splitting 필터 적용전 문자열
	 * @return HTTP Response Splitting 필터가 적용된 문자열
	 */
	public static String HttpResponseFilter(String sInvalid)
	{
		String sValid = sInvalid;

		if (StringUtils.isEmpty(sValid))
		{
			return sValid;
		}

		sValid = sValid.replaceAll("\\r", "");
		sValid = sValid.replaceAll("%0d", "");
		sValid = sValid.replaceAll("\\n", "");
		sValid = sValid.replaceAll("%0a", "");
		return sValid;
	}

	/**
	 * UNIX/WINDOWS File System Path Filter
	 * @param sInvalid File System 필터 적용전 문자열
	 * @return File System 필터가 적용된 문자열
	 */
	public static String FileSystemFilter(String sInvalid)
	{
		String sValid = sInvalid;

		if (StringUtils.isEmpty(sValid))
		{
			return sValid;
		}
		
		sValid = sValid.replaceAll("\\.\\./", "");
		sValid = sValid.replaceAll("\\./", "");
		sValid = sValid.replaceAll("/?WEB-INF/[\\w\\W]*", "");
		
		return sValid;
	}

}
