package com.kcube.cloud.app.gapi.calendar;

import java.io.IOException;

import org.springframework.http.HttpStatus;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.kcube.cloud.app.gapi.CommonGoogleApi;
import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.error.DefaultException;

public class CalendarListApi extends CommonGoogleApi
{
	public CalendarListApi(String email, GoogleAuthMethod googleOauth2Method)
	{
		super(email, googleOauth2Method);
	}

	public CalendarList list(String searchQuery)
	{
		try
		{
			return createCalendarService().calendarList().list().execute();
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

	public CalendarListEntry get(String calendarId)
	{
		try
		{
			return createCalendarService().calendarList().get(calendarId).execute();
		}
//		catch (TokenResponseException te)
//		{
//			throw new CommonGoogleTokenException.InvalidRefreshToken(te);
//		}
		catch (GoogleJsonResponseException e)
		{
			if (e.getStatusCode() == HttpStatus.NOT_FOUND.value())
			{
				return null;
			}
			throw new DefaultException(e);
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	public CalendarListEntry insert(CalendarListEntry content)
	{
		try
		{
			return createCalendarService().calendarList().insert(content).execute();
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
