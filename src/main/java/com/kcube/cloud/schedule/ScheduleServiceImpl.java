package com.kcube.cloud.schedule;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.cxf.common.util.UrlUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Event.Creator;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.BlobValue;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.Entity;
import com.kcube.cloud.app.gapi.CommonGoogleDataStoreApi;
import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.app.gapi.calendar.CalendarEventsApi;
import com.kcube.cloud.app.gapi.directory.DirectoryUsersApi;
import com.kcube.cloud.error.DefaultException;
import com.kcube.cloud.pub.enumer.DataStoreKindEnum;
import com.kcube.cloud.user.User;
import com.kcube.cloud.user.User.Resource;
import com.kcube.cloud.user.UserSession;
import com.kcube.cloud.util.HttpSslWrapUtils;
import com.kcube.cloud.util.NetmarbleApiUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service("scheduleService")
public class ScheduleServiceImpl implements ScheduleService
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("appProp")
	private Properties googleAuthProp;

	@Autowired
	private MessageSourceAccessor messageSource;

	@Override
	public JSONObject getNetmarblePersonInfo(String email)
	{
		JSONObject result = null;
		JSONArray jresult = null;
		String txt = null;
		try
		{
			HttpGet httpGet = NetmarbleApiUtils.getHttpGet(
				"https",
				"tools.coway.do",
				"/api/room/UserInfo/" + UrlUtils.urlEncode(email),
				null);

			txt = HttpSslWrapUtils.wrap(new DefaultHttpClient()).execute(httpGet, new BasicResponseHandler());
			jresult = JSONArray.fromObject(txt);
			result = (JSONObject)jresult.get(0);
//			result = JSONObject.fromObject(jresult);

			if (result.getInt("resultCode") != 0)
			{
				logger.error(result.toString());
			}
		}
		catch (IOException e)
		{
			logger.error(ExceptionUtils.getFullStackTrace(e));
		}

		return result;
	}

	@Override
	public List<Map<String, Object>> monitor(int timeOffset, List<String> resourceEmails)
	{
		Date today = new Date();

		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

		DirectoryUsersApi service = new DirectoryUsersApi(
			googleAuthProp.getProperty("google.pubEmail"), GoogleAuthMethod.SERVICE_ACCOUNT);

		List<Resource> resources = service.getUserResources("my_customer");
		for (String resourceEmail : resourceEmails)
		{
			Map<String, Object> resultMap = new HashMap<String, Object>();
			List<Event> events = findGoogleCalendarEvents(
				resourceEmail,
				setTime(today, 0, 0, 0),
				setTime(today, 23, 59, 59));

			for (Resource resource : resources)
			{
				if (resourceEmail.equals(resource.getEmail()))
				{
					resultMap.put("name", resource.getName());
					resultMap.put("floorName", resource.getFloorName());
					resultMap.put("building", resource.getBuildingId());
					resultMap.put("resourceEmail", resourceEmail);
					break;
				}
			}
			/*
			resultMap.put("name", "넷마블");
			resultMap.put("floorName", "");
			resultMap.put("building", "넷마블(SEL)");

			resultMap.put("resourceEmail", resourceEmail);
			*/
			resultMap.put("items", convertEventItems(events, timeOffset, true));

			results.add(resultMap);
		}

		return results;
	}

	@Override
	public List<Resource> findResources()
	{
		DirectoryUsersApi service = new DirectoryUsersApi(
			googleAuthProp.getProperty("google.pubEmail"), GoogleAuthMethod.SERVICE_ACCOUNT);

		return service.getUserResources("my_customer");
	}

	@Override
	public List<Map<String, Object>> findReservation(String calendarId, int timeOffset, Date date)
	{
		List<Event> events = findGoogleCalendarEvents(calendarId, setTime(date, 0, 0, 0), setTime(date, 23, 59, 59));

		return convertEventItems(events, timeOffset);
	}

	@Override
	public Map<String, List<Map<String, Object>>> findReservation(List<String> calendarIds, int timeOffset, Date date)
	{
		Map<String, List<Map<String, Object>>> results = new HashMap<String, List<Map<String, Object>>>();

		Date today = new Date();

		int addDays = Integer.parseInt(googleAuthProp.getProperty("netmarble.addDays"));

		DateTime sDateTime = setTime(today, 0, 0, 0);
		DateTime eDateTime = setTime(DateUtils.addDays(today, addDays), 23, 59, 59);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		long dateVal = new DateTime(cal.getTime(), TimeZone.getTimeZone("UTC")).getValue();

		if (dateVal >= sDateTime.getValue() && dateVal <= eDateTime.getValue())
		{
			String primaryEmail = UserSession.getCurrentUser().isGoogleLogin()
				? UserSession.getCurrentUser().getPrimaryEmail()
				: googleAuthProp.getProperty("google.pubEmail");

			List<Map<String, Object>> currUserEvents = convertEventItems(
				findGoogleCalendarEvents(primaryEmail, primaryEmail, setTime(date, 0, 0, 0), setTime(date, 23, 59, 59)),
				timeOffset);

			CommonGoogleDataStoreApi dataStore = new CommonGoogleDataStoreApi(DataStoreKindEnum.SCHEDULE);

			Entity entity = dataStore.get(DataStoreKindEnum.SCHEDULE.value());
			for (String calendarId : calendarIds)
			{
				Blob blob = null;
				try
				{
					blob = entity.getBlob(calendarId);
				}
				catch (DatastoreException e)
				{
					blob = null;
				}

				if (blob != null)
				{
					String field = new String(blob.toByteArray());

					List<Map<String, Object>> dataStoreEventItems = convertDataStoreEventItems(
						JSONArray.fromObject(field),
						timeOffset);

					results.put(
						calendarId,
						currUserEventUpdate(calendarId, primaryEmail, currUserEvents, dataStoreEventItems));
				}
			}
		}
		else
		{
			for (String calendarId : calendarIds)
			{
				results.put(calendarId, findReservation(calendarId, timeOffset, date));
			}
		}

		return results;
	}

	private List<Map<String, Object>> currUserEventUpdate(
		String calendarId,
		String primaryEmail,
		List<Map<String, Object>> currUserEvents,
		List<Map<String, Object>> dataStoreEventItems)
	{
		List<Map<String, Object>> resultItems = new ArrayList<Map<String, Object>>();

		for (Map<String, Object> currUserEvent : currUserEvents)
		{
			String currUserResourceEmail = (String) currUserEvent.get("resourceEmail");

			if (calendarId.equals(currUserResourceEmail))
			{
				String currUserEventId = (String) currUserEvent.get("id");
				String currUserEventStatus = (String) currUserEvent.get("status");

				List<String> dataStoreEventItemIds = new ArrayList<String>();
				for (Map<String, Object> dataStoreEventItem : dataStoreEventItems)
				{
					dataStoreEventItemIds.add((String) dataStoreEventItem.get("id"));
				}

				if (dataStoreEventItemIds.indexOf(currUserEventId) < 0
					&& !"declined".equals(currUserEventStatus)
					&& currUserEventStatus != null)
				{
					resultItems.add(currUserEvent);
				}
			}
		}

		for (Map<String, Object> dataStoreEventItem : dataStoreEventItems)
		{
			boolean isDeleteEvent = false;
			String dataStoreEventItemId = (String) dataStoreEventItem.get("id");
			String dataStoreEventItemEmail = (String) dataStoreEventItem.get("email");
			String dataStoreEventResourceEmail = (String) dataStoreEventItem.get("resourceEmail");

			List<String> currUserEventItemIds = new ArrayList<String>();

			for (Map<String, Object> currUserEvent : currUserEvents)
			{
				String currUserEventId = (String) currUserEvent.get("id");
				String currUserResourceEmail = (String) currUserEvent.get("resourceEmail");

				/*
				 * 이관된 데이터중 일부의 데이터는 currUser Calendar Event 에서 resource에 대한 정보가 없음 ......
				 * ( attenddes가 없어서 dataStore데이터 의존해야됨. 현재 캘린더 Event와 DataStore Event의 아이디
				 * 비교)
				 */
				if (dataStoreEventItemId.equals(currUserEventId))
				{
					currUserEventItemIds.add(currUserEventId);
				}

				if (StringUtils.isNotEmpty(dataStoreEventResourceEmail)
					&& dataStoreEventResourceEmail.equals(currUserResourceEmail))
				{
					currUserEventItemIds.add(currUserEventId);
				}
			}

			/**
			 * 현재 사용자의 EventItems에서 없고는 상태에서 dataStroeEventItemEmail이 empty 이거나 작성자의 이메일과
			 * primaryEmail 사용자가 같은경우 deleteEvent로 판단함.
			 */
			if (currUserEventItemIds.indexOf(dataStoreEventItemId) < 0
				&& (StringUtils.isEmpty(dataStoreEventItemEmail) || primaryEmail.equals(dataStoreEventItemEmail)))
			{
				isDeleteEvent = true;
				/**
				 * 본인 캘린더에 존재 하지 않고, 생성자가 본인인 경우 회의실 캘린더에서 EventId를 검색하여 존재하면 삭제하지 않음.
				 * 이슈내역 ) A사용자가 팀 캘린더에 올린 후 본인이 조회하였을때는 본인만 이벤트가 보이지 않음.
				 */
				if (primaryEmail.equals(dataStoreEventItemEmail))
				{
					CalendarEventsApi calendarEventApi = new CalendarEventsApi(
						googleAuthProp.getProperty("google.pubEmail"), GoogleAuthMethod.SERVICE_ACCOUNT);
					Event event = calendarEventApi.get(dataStoreEventResourceEmail, dataStoreEventItemId);
					if (!event.isEmpty() && !"cancelled".equals(event.getStatus()))
					{
						isDeleteEvent = false;
					}
				}
			}
			if (!isDeleteEvent)
			{
				resultItems.add(dataStoreEventItem);
			}
		}

		return resultItems;
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
		}
	}

	@Override
	public void delete(String pwd, String eventId)
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
	}

	@Override
	public void updateDataStore(String resourceEmail)
	{
		try
		{
			Date date = new Date();

			int addDays = Integer.parseInt(googleAuthProp.getProperty("netmarble.addDays"));

			DateTime minDateTime = setTime(date, 0, 0, 0);
			DateTime maxDateTime = setTime(DateUtils.addDays(date, addDays), 23, 59, 59);

			String value = new ObjectMapper()
				.writeValueAsString(findGoogleCalendarEvents(resourceEmail, minDateTime, maxDateTime));

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

	private com.google.api.services.calendar.Calendar.Events.List createListExecutor(String email, String calendarId)
	{
		return new CalendarEventsApi(email, GoogleAuthMethod.SERVICE_ACCOUNT)
			.getListExecutor(calendarId).setTimeZone("UTC");
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> convertDataStoreEventItems(JSONArray jsonArray, int timeOffset)
	{
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < jsonArray.size(); i++)
		{
			Map<String, Object> eventMap = new HashMap<String, Object>();
			Map<String, Object> item = (Map<String, Object>) jsonArray.get(i);

			eventMap.put("id", item.get("id"));
			eventMap.put("summary", item.get("summary"));

			if (!ObjectUtils.isEmpty(item.get("creator")))
			{
				Map<String, Object> creator = ObjectUtils.isEmpty("organizer")
				? (Map<String, Object>) item.get("creator")
				: (Map<String, Object>) item.get("organizer");

				String email = (String) creator.get("email");
				if (!ObjectUtils.isEmpty("organizer")
					&& !Pattern.matches(".*@(" + messageSource.getMessage("sys.domains") + ")", email))
				{
					creator = (Map<String, Object>) item.get("creator");
					email = (String) creator.get("email");
				}
				String name = (String) creator.get("displayName");
				String pubEmail = googleAuthProp.getProperty("google.pubEmail");

				List<Map<String, Object>> eventAttendees = (List<Map<String, Object>>) item.get("attendees");
				if (CollectionUtils.isNotEmpty(eventAttendees))
				{
					boolean organizer = false;
					for (Map<String, Object> eventAttendee : eventAttendees)
					{
						if (eventAttendee.get("self") != null)
						{
							eventMap.put("status", eventAttendee.get("responseStatus"));
						}
						Object isResource = eventAttendee.get("resource");
						if (isResource != null && BooleanUtils.isNotFalse((boolean) isResource))
						{
							eventMap.put("resourceEmail", eventAttendee.get("email"));
						}
						if (isResource == null && pubEmail.equals(email) && !organizer)
						{
							email = (String) eventAttendee.get("email");
							if (eventAttendee.get("organizer") != null
								&& BooleanUtils.isTrue((boolean) eventAttendee.get("organizer")))
							{
								organizer = true;
							}
						}
					}
				}

				eventMap.put("name", name);
				eventMap.put("email", email);
			}
			eventMap.put("visibility", item.get("visibility"));

			Map<String, Object> startMap = (Map<String, Object>) item.get("start");
			Map<String, Object> endMap = (Map<String, Object>) item.get("end");

			if (startMap.get("date") != null)
			{
				Map<String, Object> startDateMap = (Map<String, Object>) startMap.get("date");
				Map<String, Object> endDateMap = (Map<String, Object>) endMap.get("date");

				eventMap.put("isAllDay", true);
				eventMap.put("startTime", (long) startDateMap.get("value") - (timeOffset * 60 * 1000 * -1));
				eventMap.put("endTime", (long) endDateMap.get("value") - (timeOffset * 60 * 1000 * -1));
			}
			if (startMap.get("dateTime") != null)
			{
				Map<String, Object> startDateTimeMap = (Map<String, Object>) startMap.get("dateTime");
				Map<String, Object> endDateTimeMap = (Map<String, Object>) endMap.get("dateTime");

				eventMap.put("isAllDay", false);
				eventMap.put("startDate", startDateTimeMap.get("value"));
				eventMap.put("startTime", startDateTimeMap.get("value"));
				eventMap.put("endTime", endDateTimeMap.get("value"));
			}
			items.add(eventMap);
		}

		return items;
	}

	private List<Map<String, Object>> convertEventItems(List<Event> events, int timeOffset)
	{
		return convertEventItems(events, timeOffset, false);
	}

	private List<Map<String, Object>> convertEventItems(List<Event> events, int timeOffset, boolean isMonitor)
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

				if (event.getOrganizer() != null
					&& !Pattern.matches(".*@(" + messageSource.getMessage("sys.domains") + ")", email))
				{
					email = event.getCreator().getEmail();
					name = event.getCreator().getDisplayName();
				}
				String pubEmail = googleAuthProp.getProperty("google.pubEmail");

				List<EventAttendee> eventAttendees = event.getAttendees();
				if (CollectionUtils.isNotEmpty(eventAttendees))
				{
					boolean organizer = false;
					for (EventAttendee eventAttendee : eventAttendees)
					{
						if (eventAttendee.isResource())
						{
							eventMap.put("resourceEmail", eventAttendee.getEmail());
						}
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

				if (isMonitor)
				{
					eventMap.put("person", getNetmarblePersonInfo(email.split("@")[0]));
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

	/**
	 * @return min, max 시간 사이에 예약된 회의실 목록
	 */
	private List<Event> findGoogleCalendarEvents(String calendarId, DateTime minDateTime, DateTime maxDateTime)
	{
		return findGoogleCalendarEvents(
			calendarId,
			googleAuthProp.getProperty("google.pubEmail"),
			minDateTime,
			maxDateTime);
	}

	private List<Event> findGoogleCalendarEvents(
		String calendarId,
		String email,
		DateTime minDateTime,
		DateTime maxDateTime)
	{
		List<Event> events = new ArrayList<Event>();
		try
		{
			com.google.api.services.calendar.Calendar.Events.List listExecutor = createListExecutor(email, calendarId);

			listExecutor.setSingleEvents(true);
			listExecutor.setOrderBy("startTime");
			listExecutor.setTimeMin(minDateTime);
			listExecutor.setTimeMax(maxDateTime);

			events.addAll(listExecutor.execute().getItems());
		}
		catch (GoogleJsonResponseException ge)
		{
			logger.error(ge.getMessage());
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}

		return events;
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

		/**
		 * 주최자.
		 */
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
		cal.set(Calendar.MILLISECOND, 0);

		return new DateTime(cal.getTime(), TimeZone.getTimeZone("UTC"));
	}

}
