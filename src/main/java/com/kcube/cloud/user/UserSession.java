package com.kcube.cloud.user;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.kcube.cloud.pub.enumer.ConstantValueEnum;
import com.kcube.cloud.util.CommonUtils;

public class UserSession
{
	public static HttpSession getNativeHttpSession()
	{
		HttpServletRequest req = (RequestContextHolder.getRequestAttributes() != null)
			? ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
			: null;

		return (req != null) ? req.getSession(true) : null;
	}
	
	public static void addAttribute(String key, Object value)
	{
		HttpSession session = getNativeHttpSession();
		if (session != null)
		{
			session.setAttribute(key, value);
		}
	}

	public static void removeAttribute(String key)
	{
		HttpSession session = getNativeHttpSession();
		if (session != null)
		{
			session.removeAttribute(key);
		}
	}

	public static Object getAttribute(String key)
	{
		HttpSession session = getNativeHttpSession();

		return (session != null) ? session.getAttribute(key) : null;
	}

	public static User getCurrentUser()
	{
		Object obj = getAttribute(ConstantValueEnum.SESN_ATTR_USERINFO.value());

		return (obj != null) ? (User) obj : null;
	}

	public static void setCurrentUser(User user)
	{
		addAttribute(ConstantValueEnum.SESN_ATTR_USERINFO.value(), user);
		addAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, CommonUtils.toLocale("ko_KR"));
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCurrentUserForMobile()
	{
		Object obj = getAttribute(ConstantValueEnum.SESN_ATTR_USERINFO.value());

		return (obj != null) ? (Map<String, Object>) obj : null;
	}

	public static void setCurrentUserForMobile(Map<String, Object> user)
	{
		addAttribute(ConstantValueEnum.SESN_ATTR_USERINFO.value(), user);
		addAttribute(
			SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME,
			CommonUtils.toLocale((String) user.get("locale")));
	}
}
