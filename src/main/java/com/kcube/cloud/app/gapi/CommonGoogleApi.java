package com.kcube.cloud.app.gapi;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.kcube.cloud.error.DefaultException;

public class CommonGoogleApi
{
	private static final HttpTransport HTTP_TRANSPORT;
	private static final JsonFactory JSON_FACTORY;

	private WebApplicationContext cc = ContextLoader.getCurrentWebApplicationContext();
	private Properties property = cc.getBean("appProp", Properties.class);

	private String email;

	static
	{
		try
		{
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			JSON_FACTORY = JacksonFactory.getDefaultInstance();
		}
		catch (GeneralSecurityException | IOException e)
		{
			throw new DefaultException(e);
		}
	}

	public CommonGoogleApi(String eamil, GoogleAuthMethod googleAuthMethod)
	{
		this.email = eamil;
	}

	protected String getEmail()
	{
		return this.email;
	}

	protected Gmail createGmailService()
	{
		Set<String> scopes = new HashSet<>();
		scopes.add(GmailScopes.GMAIL_LABELS);
		scopes.add(GmailScopes.MAIL_GOOGLE_COM);
		scopes.add(GmailScopes.GMAIL_MODIFY);
		scopes.add(GmailScopes.GMAIL_READONLY);
		return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredential(scopes))
			.setApplicationName(property.getProperty("google.api.applicationName")).build();
	}

	protected Calendar createCalendarService()
	{
		Set<String> scopes = new HashSet<>();
		scopes.add(CalendarScopes.CALENDAR);
		return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredential(scopes))
			.setApplicationName(property.getProperty("google.api.applicationName")).build();
	}

	protected Directory createDirectoryService()
	{
		Set<String> scopes = new HashSet<>();
		scopes.add(DirectoryScopes.ADMIN_DIRECTORY_USER);
		scopes.add(DirectoryScopes.ADMIN_DIRECTORY_RESOURCE_CALENDAR);
		scopes.add(DirectoryScopes.ADMIN_DIRECTORY_RESOURCE_CALENDAR_READONLY);
		scopes.add(DirectoryScopes.ADMIN_DIRECTORY_RESOURCE_CALENDAR);
		scopes.add(DirectoryScopes.ADMIN_DIRECTORY_RESOURCE_CALENDAR_READONLY);
		scopes.add(DirectoryScopes.ADMIN_DIRECTORY_GROUP);
		scopes.add(DirectoryScopes.ADMIN_DIRECTORY_GROUP_MEMBER);
		return new Directory.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredential(scopes))
			.setApplicationName(property.getProperty("google.api.applicationName")).build();
	}

	private Credential getCredential(Collection<String> scopes)
	{
		return getCredentialWithServiceAccount(scopes);
	}

	private GoogleCredential.Builder getCredentialBuilder()
	{
		return new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT).setJsonFactory(JSON_FACTORY);
	}

	private GoogleCredential getCredentialWithServiceAccount(Collection<String> scopes)
	{
		try
		{
			Collection<CredentialRefreshListener> serviceAccountListeners = new ArrayList<CredentialRefreshListener>();
			serviceAccountListeners.add(new ServiceAccountListener(email));

			GoogleCredential credential = getCredentialBuilder()
				.setServiceAccountId(property.getProperty("google.serviceAccount.id"))
				.setServiceAccountPrivateKeyFromP12File(
					new File(
						this.getClass().getResource(property.getProperty("google.serviceAccount.p12File")).toURI()))
				.setServiceAccountScopes(scopes).setServiceAccountUser(email)
				.setRefreshListeners(serviceAccountListeners).build();

			return credential;
		}
		catch (IOException | URISyntaxException | GeneralSecurityException e)
		{
			throw new DefaultException(e);
		}
	}
}
