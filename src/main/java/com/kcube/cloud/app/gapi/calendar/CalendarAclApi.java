package com.kcube.cloud.app.gapi.calendar;

import java.io.IOException;

import com.google.api.services.calendar.model.AclRule;
import com.kcube.cloud.app.gapi.CommonGoogleApi;
import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.error.DefaultException;

public class CalendarAclApi extends CommonGoogleApi
{
	public CalendarAclApi(String email, GoogleAuthMethod googleOauth2Method)
	{
		super(email, googleOauth2Method);
	}

	public AclRule insert(String calendarId, AclRule content)
	{
		try
		{
			return createCalendarService().acl().insert(calendarId, content).execute();
		}
//		catch (TokenResponseException te)
//		{
//			throw new CommonGoogleTokenException.InvalidRefreshToken(te);
//		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	public AclRule patch(String calendarId, String ruleId, AclRule content)
	{
		try
		{
			return createCalendarService().acl().patch(calendarId, ruleId, content).execute();
		}
//		catch (TokenResponseException te)
//		{
//			throw new CommonGoogleTokenException.InvalidRefreshToken(te);
//		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	public void delete(String calendarId, String ruleId)
	{
		try
		{
			createCalendarService().acl().delete(calendarId, ruleId).execute();
		}
//		catch (TokenResponseException te)
//		{
//			throw new CommonGoogleTokenException.InvalidRefreshToken(te);
//		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}
}
