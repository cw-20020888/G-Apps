package com.kcube.cloud.security;

import org.springframework.security.core.session.SessionRegistryImpl;

public class SecurityAuthenticationSessionRegistry extends SessionRegistryImpl
{
	@Override
	public void registerNewSession(String sessionId, Object principal)
	{
		super.registerNewSession(sessionId, principal);
	}

	@Override
	public void removeSessionInformation(String sessionId)
	{
		super.removeSessionInformation(sessionId);
	}
}
