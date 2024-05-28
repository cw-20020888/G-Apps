package com.kcube.cloud.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import com.kcube.cloud.pub.enumer.ConstantValueEnum;
import com.kcube.cloud.user.User;
import com.kcube.cloud.user.UserSession;

public class SecurityAuthenticationSuccessHandler implements AuthenticationSuccessHandler
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication)
		throws IOException,
			ServletException
	{
		onAuthenticationSuccess(authentication);

		RequestCache requestCache = new HttpSessionRequestCache();
		SavedRequest savedRequest = requestCache.getRequest(request, response);

		response.setHeader("P3P", "CP=&#39;CURa ADMa DEVa CONo HISa OUR IND DSP ALL COR&#39;");
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(
			(savedRequest != null)
				? savedRequest.getRedirectUrl().matches("/googleLogin|/nauth")
					? request.getContextPath() + "/"
					: savedRequest.getRedirectUrl()
				: request.getContextPath() + "/");
	}

	public void onAuthenticationSuccess(Authentication authentication) throws IOException, ServletException
	{
		User user = (User) authentication.getPrincipal();

		List<Long> authorities = new ArrayList<Long>();
		Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : grantedAuthorities)
		{
			authorities.add(Long.parseLong(grantedAuthority.getAuthority()));
		}
		user.setAuthorities(authorities);

		// Set Session Attribute
		UserSession
			.addAttribute(ConstantValueEnum.SESN_ATTR_JSESSIONID.value(), UserSession.getNativeHttpSession().getId());
		UserSession.setCurrentUser(user);
	}
}