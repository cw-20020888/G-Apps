package com.kcube.cloud.app.gapi.calendar;

import java.io.IOException;

import com.google.api.services.calendar.Calendar.Events.List;
import com.google.api.services.calendar.model.Event;
import com.kcube.cloud.app.gapi.CommonGoogleApi;
import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.error.DefaultException;

public class CalendarEventsApi extends CommonGoogleApi
{
	public CalendarEventsApi(String email, GoogleAuthMethod googleOauth2Method)
	{
		super(email, googleOauth2Method);
	}

	public List getListExecutor(String calendarId)
	{
		try
		{
			return createCalendarService().events().list(calendarId);
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

	public Event get(String calendarId, String eventId)
	{
		try
		{
			return createCalendarService().events().get(calendarId, eventId).execute();
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

	public Event insert(String calendarId, Event content)
	{
		try
		{
			return createCalendarService().events().insert(calendarId, content).execute();
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

	public Event patch(String calendarId, String eventId, Event content)
	{
		try
		{
			return createCalendarService().events().patch(calendarId, eventId, content).execute();
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

	public void delete(String calendarId, String eventId)
	{
		try
		{
			createCalendarService().events().delete(calendarId, eventId).execute();
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
