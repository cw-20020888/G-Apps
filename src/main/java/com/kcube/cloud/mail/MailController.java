package com.kcube.cloud.mail;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.app.gapi.gmail.GMailLabelsApi;
import com.kcube.cloud.error.DefaultException;
import com.kcube.cloud.pub.enumer.ConstantValueEnum;
import com.kcube.cloud.user.UserSession;

@Controller
@RequestMapping(value = "/mail")
public class MailController
{
	@Autowired
	private MailService mailService;

	@Autowired
	private MessageSourceAccessor messageSource;

	@RequestMapping(value = "")
	public String mail(ModelMap map)
	{
		map.addAttribute("division", "mail");
		map.addAttribute("jsonArray", mailService.findMailLog(UserSession.getCurrentUser().getPrimaryEmail()));

		return "mail";
	}

	@RequestMapping(value = "/items")
	public void items(
		ModelMap map,
		@RequestParam(value = "maxResults", defaultValue = "10") int maxResults,
		@RequestParam(value = "nextPageToken", required = false) String nextPageToken)
	{
		try
		{
			String mail = UserSession.getCurrentUser().getPrimaryEmail();
			GMailLabelsApi service = new GMailLabelsApi(mail, GoogleAuthMethod.SERVICE_ACCOUNT);

			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.DATE, -7);

			nextPageToken = StringUtils.isNotEmpty(nextPageToken) ? nextPageToken : null;
			String q = "in:sent after:"
				+ new SimpleDateFormat("yyyy/MM/dd").format(c.getTime())
				+ " to:*@"
				+ messageSource.getMessage("sys.domains").replaceAll("\\|", "|| to:*@");

			map.addAttribute("maxResults", maxResults);
			map.addAttribute("currUser", UserSession.getCurrentUser());
			map.addAttribute(
				"items",
				service.list(mail, q, Arrays.asList("SENT"), nextPageToken, Long.valueOf(maxResults)));
			map.addAttribute("nextPageToken", service.getNextPageToken());
			map.addAttribute(ConstantValueEnum.JSON_DATA.value(), new ObjectMapper().writeValueAsString(map));
		}
		catch (JsonProcessingException e)
		{
			throw new DefaultException(e);
		}
	}

	@RequestMapping(value = "/withdrawMail")
	public void withdrawMail(@RequestParam(value = "jsonStr") String jsonStr)
	{
		try
		{
			Queue queue = QueueFactory.getQueue("deleteMailQueue");

			List<Map<String, String>> items = new ObjectMapper()
				.readValue(jsonStr, new TypeReference<List<Map<String, String>>>()
				{
				});

			mailService.insertMailLog(items, "");

			queue.add(TaskOptions.Builder.withUrl("/worker/withdrawMail").param("jsonStr", jsonStr));
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}
}
