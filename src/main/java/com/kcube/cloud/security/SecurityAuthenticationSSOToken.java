package com.kcube.cloud.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

public class SecurityAuthenticationSSOToken extends UsernamePasswordAuthenticationToken
{
	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	public SecurityAuthenticationSSOToken(Object principal)
	{
		super(principal, null);
	}

	public SecurityAuthenticationSSOToken(Object principal, Collection<? extends GrantedAuthority> authorities)
	{
		super(principal, null, authorities);
	}
}
