package com.kcube.cloud.app.gapi.calendar;

import java.io.IOException;

import com.google.api.services.calendar.model.Calendar;
import com.kcube.cloud.app.gapi.CommonGoogleApi;
import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.error.DefaultException;

public class CalendarsApi extends CommonGoogleApi
{
	public CalendarsApi(String email, GoogleAuthMethod googleOauth2Method)
	{
		super(email, googleOauth2Method);
	}

	public Calendar get(String calendarId)
	{
		try
		{
			return createCalendarService().calendars().get(calendarId).execute();
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

	public Calendar insert(Calendar content)
	{
		try
		{
			return createCalendarService().calendars().insert(content).execute();
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

	public void delete(String calendarId)
	{
		try
		{
			createCalendarService().calendars().delete(calendarId).execute();
		}
		// catch (TokenResponseException te)
		// {
		// throw new CommonGoogleTokenException.InvalidRefreshToken(te);
		// }
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}
}
