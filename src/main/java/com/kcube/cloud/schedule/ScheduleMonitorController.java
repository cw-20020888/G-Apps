package com.kcube.cloud.schedule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcube.cloud.error.DefaultException;
import com.kcube.cloud.pub.enumer.ConstantValueEnum;
import com.kcube.cloud.user.User;
import com.kcube.cloud.user.User.Resource;
import com.kcube.cloud.user.UserSession;

@Controller
@RequestMapping(value = "/pub")
public class ScheduleMonitorController
{
	@Autowired
	private ScheduleService scheduleService;

	@Autowired
	@Qualifier("appProp")
	private Properties googleProp;

	@RequestMapping(value = "/monitorTable")
	public String monitorTable(ModelMap map)
	{
		try
		{
			String exclusion = googleProp.getProperty("exclusion");

			User currUser = UserSession.getCurrentUser();

			List<Resource> result = new ArrayList<Resource>();
			List<Resource> resources = new ArrayList<Resource>();
			if (currUser != null)
			{
				resources = currUser.getResources();
			}
			else
			{
				resources = scheduleService.findResources();
			}

			for (Resource resource : resources)
			{
				if (exclusion.indexOf(resource.getBuildingId()) == -1)
				{
					result.add(resource);
				}
			}

			map.addAttribute("resources", result);
			map.addAttribute(ConstantValueEnum.JSON_DATA.value(), new ObjectMapper().writeValueAsString(map));
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}

		return "pub/monitorTable";
	}

	@RequestMapping(value = "/monitor")
	public String monitor(ModelMap map)
	{
		try
		{
			map.addAttribute("hederFolding", ConstantValueEnum.YES.value());
			map.addAttribute(ConstantValueEnum.JSON_DATA.value(), new ObjectMapper().writeValueAsString(map));
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}

		return "pub/monitor";
	}

	@RequestMapping(value = "/reservation")
	public void reservation(
		ModelMap map,
		@RequestParam(value = "jsonStr") String jsonStr,
		@RequestParam(value = "timeOffset") int timeOffset)
	{
		try
		{
			List<String> resourceEmails = new ObjectMapper().readValue(jsonStr, new TypeReference<List<String>>()
			{
			});

			map.addAttribute(
				ConstantValueEnum.JSON_RESULT.value(),
				scheduleService.monitor(timeOffset, resourceEmails));
			map.addAttribute(ConstantValueEnum.JSON_DATA.value(), new ObjectMapper().writeValueAsString(map));
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}
}
