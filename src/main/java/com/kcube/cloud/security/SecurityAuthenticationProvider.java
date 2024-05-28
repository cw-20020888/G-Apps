package com.kcube.cloud.security;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityAuthenticationProvider extends DaoAuthenticationProvider
{
	@Override
	protected void additionalAuthenticationChecks(
		UserDetails userDetails,
		UsernamePasswordAuthenticationToken authentication) throws AuthenticationException
	{
		if (authentication instanceof SecurityAuthenticationSSOToken)
		{
			this.additionalAuthenticationSSOChecks(userDetails, authentication);
		}
		else
		{
			super.additionalAuthenticationChecks(userDetails, authentication);
		}
	}

	private void additionalAuthenticationSSOChecks(
		UserDetails userDetails,
		UsernamePasswordAuthenticationToken authentication) throws AuthenticationException
	{
		if (authentication.getPrincipal() == null)
		{
			logger.debug("Authentication failed: no principal provided");

			throw new InternalAuthenticationServiceException(
				messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}
	}
}
