package com.kcube.cloud.mobile.schedule;

import java.io.IOException;
import java.util.Date;
import java.util.TimeZone;

import com.kcube.cloud.schedule.ScheduleException;
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
@RequestMapping(value = "/mobile/schedule")
public class MobileScheduleController
{
	@Autowired
	private MobileScheduleService mobileScheduleService;

	@RequestMapping(value = "")
	public String schedule(ModelMap map)
	{
		try
		{
			map.addAttribute("user", UserSession.getCurrentUser());
			map.addAttribute(ConstantValueEnum.JSON_DATA.value(), new ObjectMapper().writeValueAsString(map));
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}

		return "mobile/schedule";
	}

	@RequestMapping(value = "/reservation")
	public String reservation(
		ModelMap map,
		@RequestParam(value = "date") Date date,
		@RequestParam(value = "email") String email,
		@RequestParam(value = "timeOffset") int timeOffset)
	{
		try
		{
			map.addAttribute("date", date);
			map.addAttribute("user", UserSession.getCurrentUser());
			map.addAttribute(
				ConstantValueEnum.JSON_RESULT.value(),
				mobileScheduleService.findReservation(timeOffset, email, date));
			map.addAttribute(ConstantValueEnum.JSON_DATA.value(), new ObjectMapper().writeValueAsString(map));
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}

		return "mobile/reservation";
	}

	@RequestMapping(value = "/info")
	public String info(
		ModelMap map,
		@RequestParam(value = "id") String id,
		@RequestParam(value = "name") String name,
		@RequestParam(value = "email") String email,
		@RequestParam(value = "timeInfo") String timeInfo,
		@RequestParam(value = "cellPhone") String cellPhone,
		@RequestParam(value = "room") String room,
		@RequestParam(value = "floor") String floor,
		@RequestParam(value = "buildingId") String buildingId)
	{
		try
		{
			map.addAttribute("id", id);
			map.addAttribute("name", name);
			map.addAttribute("email", email);
			map.addAttribute("timeInfo", timeInfo);
			map.addAttribute("cellPhone", cellPhone);
			map.addAttribute("room", room);
			map.addAttribute("floor", floor);
			map.addAttribute("buildingId", buildingId);
			map.addAttribute("user", UserSession.getCurrentUser());
			map.addAttribute(ConstantValueEnum.JSON_DATA.value(), new ObjectMapper().writeValueAsString(map));
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
		return "mobile/info";
	}

	@RequestMapping(value = "/create")
	public String create(
		ModelMap map,
		@RequestParam(value = "room", required = false) String room,
		@RequestParam(value = "floor", required = false) String floor,
		@RequestParam(value = "buildingId", required = false) String buildingId,
		@RequestParam(value = "hour", required = false, defaultValue = "09") String hour,
		@RequestParam(value = "min", required = false, defaultValue = "00") String min)
	{
		try
		{
			map.addAttribute("user", UserSession.getCurrentUser());
			map.addAttribute("room", room);
			map.addAttribute("floor", floor);
			map.addAttribute("buildingId", buildingId);
			map.addAttribute("hour", hour);
			map.addAttribute("min", min);
			map.addAttribute(ConstantValueEnum.JSON_DATA.value(), new ObjectMapper().writeValueAsString(map));
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}

		return "mobile/create";
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

		mobileScheduleService.insertScheduleToCalendar(
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
	public void delete(
		@RequestParam(value = "pwd") String pwd,
		@RequestParam(value = "eventId") String eventId,
		@RequestParam(value = "resourceEmail") String resourceEmail)
	{
		mobileScheduleService.delete(pwd, eventId, resourceEmail);
	}
}
