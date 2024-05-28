package com.kcube.cloud.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.kcube.cloud.util.ioc.CipherUtils;

public class SecurityAuthenticationSSOFilter extends AbstractAuthenticationProcessingFilter
{
	private static String tokenParameter = "token";

	public final static String subDutyUserIdParameter = "userId";

	@Autowired
	private CipherUtils cipherUtils;

	public SecurityAuthenticationSSOFilter()
	{
		super(new RequestMatcher()
		{
			@Override
			public boolean matches(HttpServletRequest request)
			{
				return true;
			}
		});
	}

	public void setTokenParameter(String tokenParameter)
	{
		SecurityAuthenticationSSOFilter.tokenParameter = tokenParameter;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException
	{
		String token = request.getParameter(tokenParameter);
		SecurityAuthenticationSSOToken authRequest = new SecurityAuthenticationSSOToken(
			cipherUtils.hexToDecrypt(token));

		return this.getAuthenticationManager().authenticate(authRequest);
	}
}
