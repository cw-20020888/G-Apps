package com.kcube.cloud.view;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

public class JsonView extends MappingJackson2JsonView
{
	@Override
	protected void prepareResponse(HttpServletRequest request, HttpServletResponse response)
	{
		String userAgent = request.getHeader("User-Agent");
		Pattern p = Pattern.compile(".+Trident\\/(\\d)\\.\\d\\;.+");
		Matcher m = p.matcher(userAgent);
		int tridentMajorVersion = m.matches() ? Integer.parseInt(m.group(1)) : -1;
		if (userAgent.indexOf("MSIE") > -1 && tridentMajorVersion < 6)
		{
			// below ie9
			response.setContentType("text/plain");
		}
		else
		{
			response.setContentType(getContentType());
		}
//		Spring Security cache-control 사용
//		response.setCharacterEncoding(getEncoding().getJavaName());
//		response.addHeader("Pragma", "no-cache");
//		response.addHeader("Cache-Control", "no-cache, no-store, max-age=0");
//		response.addDateHeader("Expires", 1L);
	}
}
