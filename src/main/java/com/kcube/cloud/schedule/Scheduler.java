package com.kcube.cloud.schedule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.UrlUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.BlobValue;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Entity.Builder;
import com.kcube.cloud.app.gapi.CommonGoogleDataStoreApi;
import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.app.gapi.calendar.CalendarEventsApi;
import com.kcube.cloud.app.gapi.calendar.CalendarsApi;
import com.kcube.cloud.app.gapi.directory.DirectoryUsersApi;
import com.kcube.cloud.error.DefaultException;
import com.kcube.cloud.error.TaskException;
import com.kcube.cloud.pub.PairValue;
import com.kcube.cloud.pub.enumer.DataStoreKindEnum;
import com.kcube.cloud.task.TaskScheduler;
import com.kcube.cloud.user.User.Building;
import com.kcube.cloud.user.User.Resource;
import com.kcube.cloud.util.NetmarbleApiUtils;
import com.kcube.cloud.util.ioc.VelocityUtils;

public class Scheduler
{
	public static class DataStoreSchedule extends TaskScheduler
	{
		@Autowired
		@Qualifier("appProp")
		private Properties googleAuthProp;

		@Override
		public boolean execute() throws TaskException
		{
			double total = 0;
			try
			{
				Date date = new Date();

				DirectoryUsersApi service = new DirectoryUsersApi(
					googleAuthProp.getProperty("google.adminEmail"), GoogleAuthMethod.SERVICE_ACCOUNT);
				CalendarEventsApi executor = new CalendarEventsApi(
					googleAuthProp.getProperty("google.pubEmail"), GoogleAuthMethod.SERVICE_ACCOUNT);

				String exclusion = googleAuthProp.getProperty("exclusion");

				List<Building> buildings = service.getUserBuildings("my_customer", exclusion);
				List<Resource> resources = service.getUserResources("my_customer");

				int addDays = Integer.parseInt(googleAuthProp.getProperty("netmarble.addDays"));

				DateTime minDateTime = setTime(date, 0, 0, 0);
				DateTime maxDateTime = setTime(DateUtils.addDays(date, addDays), 23, 59, 59);

				CommonGoogleDataStoreApi dataStore = new CommonGoogleDataStoreApi(DataStoreKindEnum.SCHEDULE);

				Builder builder = Entity.newBuilder(dataStore.newKey(DataStoreKindEnum.SCHEDULE.value()));

				for (Building building : buildings)
				{
					for (Resource resource : resources)
					{
						if (building.getId().equals(resource.getBuildingId()))
						{
							String email = resource.getEmail();
							if ( !email.equals("nmn.io_18846s33vpbe2hkcknsqud4f2uvmc@resource.calendar.google.com")) {
								String value = new ObjectMapper().writeValueAsString(
										findGoogleCalendarEvents(executor, email, minDateTime, maxDateTime));

								total += value.getBytes().length;

								Blob blob = Blob.copyFrom(value.getBytes());
								builder.set(email, BlobValue.newBuilder(blob).setExcludeFromIndexes(true).build());
							}
						}
					}
				}
				dataStore.put(builder.build());
			}
			catch (IOException e)
			{
				throw new DefaultException(e);
			}
			finally
			{
				// too big Entity Exception...
				System.out.println(" ========================================== ");
				System.out.println(" total = " + total);
			}

			return true;
		}

		private DateTime setTime(Date date, int hour, int min, int sec)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, min);
			cal.set(Calendar.SECOND, sec);

			return new DateTime(cal.getTime(), TimeZone.getTimeZone("UTC"));
		}

		private List<Event> findGoogleCalendarEvents(
			CalendarEventsApi executor,
			String calendarId,
			DateTime minDateTime,
			DateTime maxDateTime)
		{
			List<Event> events = new ArrayList<Event>();
			try {
				com.google.api.services.calendar.Calendar.Events.List listExecutor = executor
						.getListExecutor(calendarId).setTimeZone("UTC");
				listExecutor.setFields(
						"items(id), items(attendees), items(creator), items(organizer), items(start), items(end), items(organizer), items(summary)");
				listExecutor.setSingleEvents(true);
				listExecutor.setOrderBy("startTime");
				listExecutor.setTimeMin(minDateTime);
				listExecutor.setTimeMax(maxDateTime);

				events.addAll(listExecutor.execute().getItems());
			}
			catch (IOException e)
			{
				System.out.println("calendarId : " + calendarId);
				throw new DefaultException(e);
			}

			return events;
		}
	}

	public static class NotifyEndSchedule extends TaskScheduler
	{
		@Autowired
		@Qualifier("appProp")
		private Properties googleAuthProp;

		@Autowired
		private VelocityUtils velocityUtils;

		@Override
		public boolean execute() throws TaskException
		{
			try
			{
				Date sDate = new Date();
				Date eDate = DateUtils.addMinutes(sDate, 10);

				DateTime sDateTime = setTime(sDate, 0);
				DateTime eDateTime = setTime(eDate, 59);

				String exclusion = googleAuthProp.getProperty("exclusion");
				String email = googleAuthProp.getProperty("google.adminEmail");

				DirectoryUsersApi service = new DirectoryUsersApi(email, GoogleAuthMethod.SERVICE_ACCOUNT);
				List<Resource> resources = service.getUserResources("my_customer");

				CalendarEventsApi calendarApi = new CalendarEventsApi(email, GoogleAuthMethod.SERVICE_ACCOUNT);
				for (Resource resource : resources)
				{
					if (exclusion.indexOf(resource.getBuildingId()) > -1)
					{
						continue;
					}
					if ( !resource.getEmail().equals("nmn.io_18846s33vpbe2hkcknsqud4f2uvmc@resource.calendar.google.com")){
						com.google.api.services.calendar.model.Calendar calendar = new CalendarsApi(
								email, GoogleAuthMethod.SERVICE_ACCOUNT).get(resource.getEmail());
						String timeZoneId = calendar.getTimeZone();

						com.google.api.services.calendar.Calendar.Events.List listExecutor = calendarApi
								.getListExecutor(resource.getEmail()).setTimeZone("UTC");

						listExecutor.setSingleEvents(true);
						listExecutor.setOrderBy("startTime");
						listExecutor.setTimeMin(sDateTime);
						listExecutor.setTimeMax(eDateTime);

						List<Event> events = listExecutor.execute().getItems();

						if (CollectionUtils.isNotEmpty(events))
						{
							for (Event event : events)
							{
								PairValue<DateTime, DateTime> pairValue = getEventTime(event);

								if (pairValue.getValue1().getValue() >= sDateTime.getValue()
										&& pairValue.getValue1().getValue() <= eDateTime.getValue())
								{
									List<EventAttendee> eventAttendees = event.getAttendees();
									if (CollectionUtils.isNotEmpty(eventAttendees))
									{
										boolean organizer = false;
										for (EventAttendee eventAttendee : eventAttendees)
										{
											if (!eventAttendee.isResource() && !organizer)
											{
												if (BooleanUtils.isTrue(eventAttendee.getOrganizer()))
												{
													notifyMail(event, timeZoneId, eventAttendee.getEmail());
													organizer = true;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			catch (IOException e)
			{
				return false;
			}
			return true;
		}

		private void notifyMail(Event event, String timeZoneId, String receiver)
		{
			// try
			// {
			int users = 0;
			String pattern = "yyyy-MM-dd HH:mm";
			String title = "[ " + event.getSummary() + " ] 일정이 시작예정 입니다.";
			String sender = googleAuthProp.getProperty("google.schedule.sender");
			PairValue<DateTime, DateTime> pairValue = getEventTime(event);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

			Date startDateTime = convertByTimeZone(pairValue.getValue1().getValue(), timeZoneId);
			Date endDateTime = convertByTimeZone(pairValue.getValue2().getValue(), timeZoneId);

			String time = simpleDateFormat.format(
				startDateTime) + " ~ " + simpleDateFormat.format(endDateTime) + " (" + timeZoneId + ")";

			if ("bob2@netmarble.com".equals(receiver))
			{
				receiver = "bob2@apps.netmarble.com";
			}

			String subject = StringUtils.isEmpty(event.getSummary()) ? "제목없음" : event.getSummary();

			Map<String, Object> context = new HashMap<String, Object>();
			context.put("subject", subject);
			context.put("creator", receiver);
			context.put("time", time);
			List<EventAttendee> eventAttendees = event.getAttendees();
			if (CollectionUtils.isNotEmpty(eventAttendees))
			{
				for (EventAttendee eventAttendee : eventAttendees)
				{
					if (!eventAttendee.isResource()
						&& !eventAttendee.getEmail().equals(event.getOrganizer().getEmail()))
					{
						users++;
					}
				}
			}
			context.put("users", users > 0 ? receiver + "외 " + users + "명" : receiver);
			String message = velocityUtils.merge("proceendings.vm", context);

			System.out.println(message);

			List<NameValuePair> arguments = new ArrayList<NameValuePair>();
			arguments.add(new BasicNameValuePair("uri", "msg/v10/email"));
			arguments.add(new BasicNameValuePair("from", sender));
			arguments.add(new BasicNameValuePair("to", receiver));
			arguments.add(new BasicNameValuePair("title", title));
			arguments.add(new BasicNameValuePair("content", UrlUtils.urlEncode(message)));
			arguments.add(new BasicNameValuePair("IsHtml", "true"));

			HttpPost httpPost = NetmarbleApiUtils.getHttpPost(
				googleAuthProp.getProperty("netmarble.schema"),
				googleAuthProp.getProperty("netmarble.host"),
				"/Bridge4HelloAPI.ashx",
				arguments);

			// JSONObject result = JSONObject.fromObject(
			// HttpSslWrapUtils.wrap(new DefaultHttpClient()).execute(httpPost, new
			// BasicResponseHandler()));
			//
			// if (result.getInt("result") != 0)
			// {
			// throw new NotifyMailException("notifyMail Send Result Not Success Code= " +
			// result.toString());
			// }
			// }
			// catch (IOException e)
			// {
			// throw new DefaultException(e);
			// }
		}

		private DateTime setTime(Date date, int sec)
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.SECOND, sec);

			return new DateTime(cal.getTime(), TimeZone.getTimeZone("UTC"));
		}

		private PairValue<DateTime, DateTime> getEventTime(Event event)
		{
			PairValue<DateTime, DateTime> pairValue = new PairValue<DateTime, DateTime>();

			if (event.getStart().getDate() != null)
			{
				pairValue.setValue1(event.getStart().getDate());
				pairValue.setValue2(event.getEnd().getDate());
			}
			if (event.getStart().getDateTime() != null)
			{
				pairValue.setValue1(event.getStart().getDateTime());
				pairValue.setValue2(event.getEnd().getDateTime());
			}

			pairValue.setValue1(setTime(new Date(pairValue.getValue1().getValue()), 0));
			pairValue.setValue2(setTime(new Date(pairValue.getValue2().getValue()), 0));

			return pairValue;
		}

		private Date convertByTimeZone(long dateTimeValue, String timeZoneId)
		{
			org.joda.time.DateTime jDateTime = new org.joda.time.DateTime(dateTimeValue, DateTimeZone.UTC);
			jDateTime.withZone(DateTimeZone.forID(timeZoneId));

			return jDateTime.toDate();
		}
	}

}
