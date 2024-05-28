package com.kcube.cloud.admin.mail;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.gmail.model.Message;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.app.gapi.directory.DirectoryUsersApi;
import com.kcube.cloud.app.gapi.gmail.GMailLabelsApi;
import com.kcube.cloud.error.DefaultException;
import com.kcube.cloud.mail.MailService;
import com.kcube.cloud.pub.enumer.ConstantValueEnum;

@Controller
@RequestMapping(value = "/admin/mail")
public class AdminMailController
{
	@Autowired
	private MailService mailService;

	@Autowired
	@Qualifier("appProp")
	private Properties googleAuthProp;

	@Autowired
	private MessageSourceAccessor messageSource;

	@RequestMapping(value = "")
	public String mail(ModelMap map)
	{
		map.addAttribute("division", "mail");

		return "admin/mail";
	}

	@RequestMapping(value = "/items")
	public void items(
		ModelMap map,
		@RequestParam(value = "mail", required = false) String mail,
		@RequestParam(value = "maxResults", defaultValue = "10") Long maxResults,
		@RequestParam(value = "nextPageToken", required = false) String nextPageToken)
	{
		try
		{
			List<Message> items = new ArrayList<Message>();
			if (StringUtils.isNotEmpty(mail))
			{
				GMailLabelsApi service = new GMailLabelsApi(mail, GoogleAuthMethod.SERVICE_ACCOUNT);

				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.DATE, -7);

				nextPageToken = StringUtils.isNotEmpty(nextPageToken) ? nextPageToken : null;
				String q = "in:sent after:"
					+ new SimpleDateFormat("yyyy/MM/dd").format(c.getTime())
					+ " to:*@"
					+ messageSource.getMessage("sys.domains").replaceAll("\\|", "|| to:*@");

				items = service.list(mail, q, Arrays.asList("SENT"), nextPageToken, maxResults);

				nextPageToken = service.getNextPageToken();

				map.addAttribute("jsonArray", mailService.findMailLog(mail));
			}
			map.addAttribute("mail", mail);
			map.addAttribute("items", items);
			map.addAttribute("maxResults", maxResults);
			map.addAttribute("nextPageToken", nextPageToken);
			map.addAttribute(ConstantValueEnum.JSON_DATA.value(), new ObjectMapper().writeValueAsString(map));
		}
		catch (JsonProcessingException e)
		{
			throw new DefaultException(e);
		}
	}

	@RequestMapping(value = "/withdrawMail")
	public void deleteMail(
		@RequestParam(value = "jsonStr") String jsonStr,
		@RequestParam(value = "opinion", required = false) String opinion)
	{
		try
		{
			Queue queue = QueueFactory.getQueue("deleteMailQueue");

			List<Map<String, String>> items = new ObjectMapper()
				.readValue(jsonStr, new TypeReference<List<Map<String, String>>>()
				{
				});

			mailService.insertMailLog(items, opinion);

			queue.add(
				TaskOptions.Builder
					.withUrl("/worker/withdrawMail").param("opinion", opinion).param("jsonStr", jsonStr));
		}
		catch (IOException e)
		{
			throw new DefaultException(e);
		}
	}

	@RequestMapping(value = "/search")
	public void users(ModelMap map, @RequestParam(value = "name") String name)
	{
		try
		{
			DirectoryUsersApi service = new DirectoryUsersApi(
				googleAuthProp.getProperty("google.adminEmail"), GoogleAuthMethod.SERVICE_ACCOUNT);

			map.addAttribute(ConstantValueEnum.JSON_RESULT.value(), service.list(name, "my_customer").getUsers());
			map.addAttribute(ConstantValueEnum.JSON_DATA.value(), new ObjectMapper().writeValueAsString(map));
		}
		catch (JsonProcessingException e)
		{
			throw new DefaultException(e);
		}
	}
}