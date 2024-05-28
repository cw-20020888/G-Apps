package com.kcube.cloud.schedule;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcube.cloud.error.DefaultException;
import com.kcube.cloud.pub.enumer.ConstantValueEnum;
import com.kcube.cloud.user.UserSession;

@Controller
@RequestMapping(value = "/schedule")
public class ScheduleController
{
	@Autowired
	private ScheduleService scheduleService;

	@RequestMapping(value = "")
	public String schedule(ModelMap map)
	{
		try
		{
			map.addAttribute("division", "schedule");
			map.addAttribute("user", UserSession.getCurrentUser());
			map.addAttribute(ConstantValueEnum.JSON_DATA.value(), new ObjectMapper().writeValueAsString(map));
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}

		return "schedule";
	}

	@RequestMapping(value = "/person")
	public void netPerson(ModelMap map, @RequestParam(value = "mail") String mail)
	{
		try
		{
			map.addAttribute(ConstantValueEnum.JSON_RESULT.value(), scheduleService.getNetmarblePersonInfo(mail));
			map.addAttribute(ConstantValueEnum.JSON_DATA.value(), new ObjectMapper().writeValueAsString(map));
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	@RequestMapping(value = "/floor/reservationAjax")
	public void floorReservationAjax(
		ModelMap map,
		@RequestParam(value = "date") Date date,
		@RequestParam(value = "timeOffset") int timeOffset,
		@RequestParam(value = "calendarId") String calendarId)
	{
		try
		{
			List<String> calendarIds = Arrays.asList(calendarId.split(","));

			map.addAttribute(
				ConstantValueEnum.JSON_RESULT.value(),
				scheduleService.findReservation(calendarIds, timeOffset, date));
			map.addAttribute(ConstantValueEnum.JSON_DATA.value(), new ObjectMapper().writeValueAsString(map));
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	@RequestMapping(value = "/insert")
	public void insert(
		@RequestParam(value = "timeOffset") int timeOffset,
		@RequestParam(value = "userName") String userName,
		@RequestParam(value = "userEmail") String userEmail,
		@RequestParam(value = "allDay") boolean isAllDay,
		@RequestParam(value = "sDate") Date sDate,
		@RequestParam(value = "eDate") Date eDate,
		@RequestParam(value = "summary") String summary,
		@RequestParam(value = "displayName") String displayName,
		@RequestParam(value = "pwd") String pwd,
		@RequestParam(value = "resourceEmail") String resourceEmail)
	{
		// client TimeZone - Server TimeZone
		int serverTimeOffset = (TimeZone.getDefault().getOffset(new Date().getTime()) / 60 / 1000) * -1;

		if (!isAllDay)
		{
			sDate = DateUtils.addMinutes(sDate, timeOffset - serverTimeOffset);
			eDate = DateUtils.addMinutes(eDate, timeOffset - serverTimeOffset);
		}

		String sDateText = "" + sDate.getYear() + sDate.getMonth() + sDate.getDate();
		String eDateText = "" + eDate.getYear() + eDate.getMonth() + eDate.getDate();
		if(!sDateText.equals(eDateText)){
			throw new ScheduleException.CannotReservationException("공용 회의실은 반복예약이 불가능합니다.");
		}

		scheduleService.insertScheduleToCalendar(
			timeOffset,
			userName,
			userEmail,
			summary,
			displayName,
			pwd,
			resourceEmail,
			sDate,
			eDate,
			isAllDay);
	}

	@RequestMapping(value = "/delete")
	public void delete(@RequestParam(value = "pwd") String pwd, @RequestParam(value = "eventId") String eventId)
	{
		scheduleService.delete(pwd, eventId);
	}

	@RequestMapping(value = "/updateDataStore")
	public void updateDataStore(@RequestParam(value = "resourceEmail") String resourceEmail)
	{
		scheduleService.updateDataStore(resourceEmail);
	}
}
