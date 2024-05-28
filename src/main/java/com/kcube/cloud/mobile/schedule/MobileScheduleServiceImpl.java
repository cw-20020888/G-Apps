package com.kcube.cloud.mobile.schedule;

import java.io.IOException;
import java.text.DateFormat;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.cxf.common.util.UrlUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Creator;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.BlobValue;
import com.google.cloud.datastore.Entity;
import com.kcube.cloud.app.gapi.CommonGoogleDataStoreApi;
import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.app.gapi.calendar.CalendarEventsApi;
import com.kcube.cloud.app.gapi.directory.DirectoryUsersApi;
import com.kcube.cloud.error.DefaultException;
import com.kcube.cloud.pub.enumer.DataStoreKindEnum;
import com.kcube.cloud.schedule.ScheduleException;
import com.kcube.cloud.user.User;
import com.kcube.cloud.user.User.Resource;
import com.kcube.cloud.user.UserSession;
import com.kcube.cloud.util.HttpSslWrapUtils;
import com.kcube.cloud.util.NetmarbleApiUtils;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

@Service("mobileScheduleService")
public class MobileScheduleServiceImpl implements MobileScheduleService
{
	@Autowired
	@Qualifier("appProp")
	private Properties googleAuthProp;

	@Override
	public List<Map<String, Object>> findReservation(int timeOffset, String resourceEmail, Date date)
	{
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

		DirectoryUsersApi service = new DirectoryUsersApi(
			googleAuthProp.getProperty("google.pubEmail"), GoogleAuthMethod.SERVICE_ACCOUNT);

		List<Resource> resources = service.getUserResources("my_customer");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Event> events = findGoogleCalendarEvents(resourceEmail, setTime(date, 0, 0, 0), setTime(date, 23, 59, 59));

		for (Resource resource : resources)
		{
			if (resourceEmail.equals(resource.getEmail()))
			{
				resultMap.put("id", resource.getId());
				resultMap.put("name", resource.getName());
				resultMap.put("capacity", resource.getCapacity());
				resultMap.put("floorName", resource.getFloorName());
				resultMap.put("building", resource.getBuildingId());
				resultMap.put("resourceEmail", resourceEmail);

				break;
			}
		}

		resultMap.put("resourceEmail", resourceEmail);
		resultMap.put("items", convertEventItems(events, timeOffset));

		results.add(resultMap);

		return results;
	}

	@Override
	public void delete(String pwd, String eventId, String resourceEmail)
	{
		try
		{
			CalendarEventsApi service = new CalendarEventsApi(
				googleAuthProp.getProperty("google.pubEmail"), GoogleAuthMethod.SERVICE_ACCOUNT);
			Event event = service.get("primary", eventId);

			if (event != null)
			{
				String description = event.getDescription();

				if (pwd.equals(description))
				{
					service.delete("primary", eventId);
				}
				else
				{
					throw new ScheduleException.ValidationPassWordException("패스워드를 다시 확인하여 주세요.");
				}
			}
			else
			{
				throw new ScheduleException.CannotCalendarEventDeleteException("삭제하실 예약내역을 찾을 수 없습니다.");
			}
		}
		catch (DefaultException e)
		{
			throw new ScheduleException.CannotCalendarEventDeleteException("삭제하실 예약내역을 찾을 수 없습니다.");
		}
		finally
		{
			updateDataStore(resourceEmail, eventId);
		}
	}

	private com.google.api.services.calendar.Calendar.Events.List createListExecutor(String email, String calendarId)
	{
		return new CalendarEventsApi(email, GoogleAuthMethod.SERVICE_ACCOUNT)
			.getListExecutor(calendarId).setTimeZone("UTC");
	}

	private List<Event> findGoogleCalendarEvents(String calendarId, DateTime minDateTime, DateTime maxDateTime)
	{
		List<Event> events = new ArrayList<Event>();
		try
		{
			com.google.api.services.calendar.Calendar.Events.List listExecutor = createListExecutor(
				googleAuthProp.getProperty("google.pubEmail"),
				calendarId);

			listExecutor.setSingleEvents(true);
			listExecutor.setOrderBy("startTime");
			listExecutor.setTimeMin(minDateTime);
			listExecutor.setTimeMax(maxDateTime);

			events.addAll(listExecutor.execute().getItems());
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}

		return events;
	}

	@Override
	@SuppressWarnings("deprecation")
	public void insertScheduleToCalendar(
		int timeOffset,
		String userName,
		String userEmail,
		String summary,
		String displayName,
		String pwd,
		String resourceEmail,
		Date sDate,
		Date eDate,
		boolean isAllDay)
	{
		CalendarEventsApi service = new CalendarEventsApi(userEmail, GoogleAuthMethod.SERVICE_ACCOUNT);

		DateTime sDateTime = new DateTime(sDate, TimeZone.getTimeZone("UTC"));
		DateTime eDateTime = setTime(eDate, eDate.getHours(), eDate.getMinutes(), eDate.getSeconds() - 1);

		List<Event> events = findGoogleCalendarEvents(resourceEmail, sDateTime, eDateTime);
		if (events.size() > 0)
		{
			throw new ScheduleException.CannotReservationException("회의실은 중복예약 하실 수 없습니다.");
		}
		else
		{
			Event event = createEvent(
				timeOffset,
				userName,
				userEmail,
				summary,
				sDate,
				eDate,
				resourceEmail,
				displayName,
				pwd,
				isAllDay);

			service.insert(userEmail, event);

			updateDataStore(resourceEmail, null);
		}
	}

	private void updateDataStore(String resourceEmail, String eventId)
	{
		try
		{
			Date date = new Date();

			int addDays = Integer.parseInt(googleAuthProp.getProperty("netmarble.addDays"));

			DateTime minDateTime = setTime(date, 0, 0, 0);
			DateTime maxDateTime = setTime(DateUtils.addDays(date, addDays), 23, 59, 59);

			List<Event> events = findGoogleCalendarEvents(resourceEmail, minDateTime, maxDateTime);

			if (StringUtils.isNotEmpty(eventId) && CollectionUtils.isNotEmpty(events))
			{
				for (Event event : events)
				{
					if (eventId.equals(event.getId()))
					{
						events.remove(event);
						break;
					}
				}
			}

			String value = new ObjectMapper().writeValueAsString(events);

			Blob blob = Blob.copyFrom(value.getBytes());

			CommonGoogleDataStoreApi dataStore = new CommonGoogleDataStoreApi(DataStoreKindEnum.SCHEDULE);

			dataStore.update(
				Entity
					.newBuilder(dataStore.get(DataStoreKindEnum.SCHEDULE.value()))
					.set(resourceEmail, BlobValue.newBuilder(blob).setExcludeFromIndexes(true).build()).build());
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	private List<Map<String, Object>> convertEventItems(List<Event> events, int timeOffset)
	{
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

		for (Event event : events)
		{
			Map<String, Object> eventMap = new HashMap<String, Object>();
			eventMap.put("id", event.getId());
			eventMap.put("summary", event.getSummary());

			if (event.getCreator() != null)
			{
				String email = event.getOrganizer() != null
					? event.getOrganizer().getEmail()
					: event.getCreator().getEmail();
				String name = event.getOrganizer() != null
					? event.getOrganizer().getDisplayName()
					: event.getCreator().getDisplayName();
				String pubEmail = googleAuthProp.getProperty("google.pubEmail");

				List<EventAttendee> eventAttendees = event.getAttendees();
				if (CollectionUtils.isNotEmpty(eventAttendees))
				{
					boolean organizer = false;
					for (EventAttendee eventAttendee : eventAttendees)
					{
						if (eventAttendee.isSelf())
						{
							eventMap.put("status", eventAttendee.getResponseStatus());
						}
						if (!eventAttendee.isResource() && pubEmail.equals(email) && !organizer)
						{
							email = eventAttendee.getEmail();
							if (BooleanUtils.isTrue(eventAttendee.getOrganizer()))
							{
								organizer = true;
							}
						}
					}
				}

				JSONObject netPerson = getNetmarblePersonInfo(email);

				if (!JSONUtils.isNull(netPerson) && !netPerson.getJSONArray("restRoot").isEmpty())
				{
					name = JSONObject.fromObject(netPerson.getJSONArray("restRoot").get(0)).getString("orgUserName");
					String cellPhone = JSONObject
						.fromObject(netPerson.getJSONArray("restRoot").get(0)).getString("orgUserCellPhone");

					eventMap.put("cellPhone", cellPhone);
				}

				eventMap.put("name", name);
				eventMap.put("email", email);
			}
			eventMap.put("visibility", event.getVisibility());

			if (event.getStart().getDate() != null)
			{
				eventMap.put("isAllDay", true);
				eventMap.put("startTime", event.getStart().getDate().getValue() - (timeOffset * 60 * 1000 * -1));
				eventMap.put("endTime", event.getEnd().getDate().getValue() - (timeOffset * 60 * 1000 * -1));
			}
			if (event.getStart().getDateTime() != null)
			{
				eventMap.put("isAllDay", false);
				eventMap.put("startDate", event.getStart().getDateTime().getValue());
				eventMap.put("startTime", event.getStart().getDateTime().getValue());
				eventMap.put("endTime", event.getEnd().getDateTime().getValue());
			}
			items.add(eventMap);
		}

		return items;
	}

	private JSONObject getNetmarblePersonInfo(String email)
	{
		try
		{
			HttpGet httpGet = NetmarbleApiUtils.getHttpGet(
					"https",
					"tools.coway.do",
					"/api/room/UserInfo/" + UrlUtils.urlEncode(email),
					null);

			JSONObject result = JSONObject.fromObject(
				HttpSslWrapUtils.wrap(new DefaultHttpClient()).execute(httpGet, new BasicResponseHandler()));

			if (result.getInt("resultCode") != 0)
			{
				throw new DefaultException();
			}

			return result;
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	private Event createEvent(
		int timeOffset,
		String userName,
		String userEmail,
		String summary,
		Date sDate,
		Date eDate,
		String resourceMail,
		String displayName,
		String pwd,
		boolean isAllDay)
	{
		Event event = new Event();

		Creator creator = new Creator();
		creator.setDisplayName(userName);
		creator.setEmail(userEmail);
		event.setCreator(creator);
		event.setSummary(summary);
		event.setDescription(pwd);

		if (isAllDay)
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			event.setStart(new EventDateTime().setDate(new DateTime(dateFormat.format(sDate))));
			event.setEnd(new EventDateTime().setDate(new DateTime(dateFormat.format(eDate))));
		}
		else
		{
			event.setStart(new EventDateTime().setDateTime(new DateTime(sDate)));
			event.setEnd(new EventDateTime().setDateTime(new DateTime(eDate)));
		}

		EventAttendee eventAttendee = new EventAttendee();
		eventAttendee.setEmail(resourceMail);
		eventAttendee.setDisplayName(displayName);
		eventAttendee.setResource(true);
		eventAttendee.setResponseStatus("accepted");

		User nUser = UserSession.getCurrentUser();

		EventAttendee userEventAttendee = new EventAttendee();
		userEventAttendee.setEmail(nUser.getIntraEmail());
		userEventAttendee.setDisplayName(nUser.getFullName());
		userEventAttendee.setResponseStatus("accepted");
		userEventAttendee.setOrganizer(true);

		List<EventAttendee> eventAttendees = new ArrayList<EventAttendee>();
		eventAttendees.add(eventAttendee);
		eventAttendees.add(userEventAttendee);

		event.setAttendees(eventAttendees);

		return event;
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

}
