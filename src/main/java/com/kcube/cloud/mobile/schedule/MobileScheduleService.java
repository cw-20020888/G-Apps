package com.kcube.cloud.mobile.schedule;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MobileScheduleService
{
	public List<Map<String, Object>> findReservation(int timeOffset, String resourceEmails, Date date);
	
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

	public void delete(String pwd, String eventId, String resourceEmail);
}
