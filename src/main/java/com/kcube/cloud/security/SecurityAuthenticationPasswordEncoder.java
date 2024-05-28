package com.kcube.cloud.security;

import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

public class SecurityAuthenticationPasswordEncoder extends MessageDigestPasswordEncoder
{
	public SecurityAuthenticationPasswordEncoder(String algorithm)
	{
		super(algorithm);
	}

	public SecurityAuthenticationPasswordEncoder(String algorithm, boolean encodeHashAsBase64)
	{
		super(algorithm, encodeHashAsBase64);
	}
	
	/*
	 * @Override public boolean isPasswordValid(String encPass, String rawPass, Object
	 * salt) { String pass1 = "" + encPass; String pass2 = encodePassword(rawPass, salt);
	 * return pass1.equals(pass2); }
	 * @Override public String encodePassword(String rawPass, Object salt) { try {
	 * MessageDigest messageDigest = getMessageDigest();
	 * messageDigest.update(rawPass.getBytes("UTF-8")); rawPass return
	 * EncodingUtils.Base64.encodeString(new String(messageDigest.digest())); } catch
	 * (UnsupportedEncodingException e) { throw new DefaultException(e); } }
	 */
}
