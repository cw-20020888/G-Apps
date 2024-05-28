package com.kcube.cloud.pub;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

public class JstlLocaleChangeInterceptor extends HandlerInterceptorAdapter
{
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		HttpSession session = request.getSession(false);
		if (session != null)
		{
			Locale springLocale = RequestContextUtils.getLocale(request);
			Locale jstlLocale = (Locale) Config.get(session, Config.FMT_LOCALE);
			if ((springLocale != null && jstlLocale != null) && !springLocale.toString().equals(jstlLocale.toString()))
			{
				Config.set(session, Config.FMT_LOCALE, springLocale);
			}
			Config.set(session, Config.FMT_TIME_ZONE, "Asia/Seoul");
		}
		return true;
	}
}
