package com.kcube.cloud.app.gapi;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.kcube.cloud.user.User;
import com.kcube.cloud.user.UserSession;

public class ServiceAccountListener implements CredentialRefreshListener
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String encryptedEamil;

	public ServiceAccountListener(String encryptedEamil)
	{
		this.encryptedEamil = encryptedEamil;
	}

	@Override
	public void onTokenResponse(Credential paramCredential, TokenResponse paramTokenResponse) throws IOException
	{
		User user = UserSession.getCurrentUser();
		logger.info(
			"credential = success, refreshToken owner = {}, accessToken = {}, refreshToken = {}, currentUserName = {}",
			new Object[] {
				encryptedEamil,
				paramCredential.getAccessToken(),
				paramCredential.getRefreshToken(),
				(user != null) ? user.getFullName() : ""});
	}

	@Override
	public void onTokenErrorResponse(Credential paramCredential, TokenErrorResponse paramTokenErrorResponse)
		throws IOException
	{
		User user = UserSession.getCurrentUser();
		logger.error(
			"credential = fail, refreshToken owner = {}, accessToken = {}, refreshToken = {}, currentUserName = {}",
			new Object[] {
				encryptedEamil,
				paramCredential.getAccessToken(),
				paramCredential.getRefreshToken(),
				(user != null) ? user.getFullName() : ""});
	}
}
