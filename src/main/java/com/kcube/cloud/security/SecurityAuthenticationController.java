package com.kcube.cloud.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.cxf.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kcube.cloud.error.CustomException;

@Controller
public class SecurityAuthenticationController
{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/login")
	public String login(
		ModelMap map,
		HttpServletRequest req,
		HttpServletResponse res,
		@RequestParam(value = "error", required = false) String error,
		@RequestParam(value = "logout", required = false) String logout)
	{
		if (error != null)
		{
			HttpSession session = req.getSession(false);
			if (session != null)
			{
				session.invalidate();
			}
			map.addAttribute("error", "Invalid username and password!");
		}

		if (logout != null)
		{
			map.addAttribute("msg", "You've been logged out successfully.");
		}

		return "login/login";
	}

	@RequestMapping(value = "/expired")
	public String expired()
	{
		throw new CustomException.ExpiredException();
	}

	@RequestMapping(value = "/accessDenied")
	public String accessDenied(HttpServletResponse response)
	{
		throw new CustomException.ForbiddenException();
	}

	@RequestMapping(value = "/pub/{page}")
	public String pub(@PathVariable String page)
	{
		if (StringUtils.isEmpty(page))
		{
			throw new CustomException.PageNotFoundException();
		}
		return "pub/" + page;
	}
}
