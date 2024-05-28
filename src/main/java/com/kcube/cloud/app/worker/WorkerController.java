package com.kcube.cloud.app.worker;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcube.cloud.error.DefaultException;
import com.kcube.cloud.mail.MailService;

@Controller
@RequestMapping("/worker")
public class WorkerController
{
	@Autowired
	private MailService mailService;

	@RequestMapping("/withdrawMail")
	public void execute(HttpServletRequest req, HttpServletResponse res)
	{
		try
		{
			String jsonStr = req.getParameter("jsonStr");

			List<Map<String, String>> items = new ObjectMapper()
				.readValue(jsonStr, new TypeReference<List<Map<String, String>>>()
				{
				});

			mailService.deleteMailItems(items);

			res.setStatus(HttpServletResponse.SC_OK);
		}
		catch (IOException ie)
		{
			res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);

			throw new DefaultException(ie);
		}
	}
}