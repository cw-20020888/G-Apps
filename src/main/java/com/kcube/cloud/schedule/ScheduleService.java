package com.kcube.cloud.schedule;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.kcube.cloud.user.User.Resource;

import net.sf.json.JSONObject;

public interface ScheduleService
{
	public JSONObject getNetmarblePersonInfo(String email);

	public List<Map<String, Object>> monitor(int timeOffset, List<String> resourceEmails);

	public List<Resource> findResources();

	public List<Map<String, Object>> findReservation(String calendarId, int timeOffset, Date date);

	public Map<String, List<Map<String, Object>>> findReservation(List<String> calendarIds, int timeOffset, Date date);

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
		boolean isAllDay);

	public void delete(String pwd, String eventId);

	public void updateDataStore(String resourceEmail);
}
