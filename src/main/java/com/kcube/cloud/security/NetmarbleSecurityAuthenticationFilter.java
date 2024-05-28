package com.kcube.cloud.security;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.app.gapi.directory.DirectoryUsersApi;
import com.kcube.cloud.error.DefaultException;
import com.kcube.cloud.pub.enumer.AuthorityEnum;
import com.kcube.cloud.user.User;
import com.kcube.cloud.user.User.Building;
import com.kcube.cloud.user.User.Resource;

import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;
import net.sf.json.JSONObject;

public class NetmarbleSecurityAuthenticationFilter extends AbstractAuthenticationProcessingFilter
{
	@Autowired
	@Qualifier("appProp")
	private Properties googleProp;

	public NetmarbleSecurityAuthenticationFilter(String defaultFilterProcessesUrl)
	{
		super(defaultFilterProcessesUrl);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException
	{
		String accessToken = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
		{
			for (Cookie cookie : cookies)
			{
				if ("IAM".equals(cookie.getName()))
				{
					accessToken = cookie.getValue();
				}
			}
		}
		return getUsernamePasswordAuthenticationToken(
			accessToken,
			request.getRemoteAddr(),
			request.getHeader("User-Agent"));
	}

	@Override
	public void afterPropertiesSet()
	{
	}

	public UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(
		String accessToken,
		String ipAddr,
		String userAgent)
	{
		try
		{
			String exclusion = googleProp.getProperty("exclusion");

			String queryString = "accessToken=" + accessToken + "&requestServer=gapps.coway.com" + "&requestIp=" + ipAddr;
			HttpGet httpGet = new HttpGet(
				URIUtils.createURI("http", "hexaapi.nmn.io", -1, "/users/isSignIn", queryString, null));

			JSONObject result = JSONObject
				.fromObject(new DefaultHttpClient().execute(httpGet, new BasicResponseHandler()));
			if (result.getInt("statusCode") != 200)
			{
				throw new InternalAuthenticationServiceException(result.getString("errorMessage"));
			}

			JSONObject responseObject = (JSONObject) result.get("responseObject");

			DirectoryUsersApi service = new DirectoryUsersApi(
				googleProp.getProperty("google.pubEmail"), GoogleAuthMethod.SERVICE_ACCOUNT);

			List<Building> buildings = service.getUserBuildings("my_customer", exclusion);
			List<Resource> resources = service.getUserResources("my_customer");

			User user = new User();
			user.setId(responseObject.getString("userId"));
			user.setFullName(
				responseObject.getString("name") != null
					? new String(responseObject.getString("name").getBytes("iso-8859-1"))
					: "");
			user.setPrimaryEmail(googleProp.getProperty("google.pubEmail"));
			user.setIntraEmail(responseObject.getString("mail"));
			user.setGoogleLogin(false);
			user.setLocation(null);
			user.setBuildings(buildings);
			user.setResources(resources);

			List<GrantedAuthority> grantedAuthoritys = new ArrayList<GrantedAuthority>();

			UserAgent agent = UserAgent.parseUserAgentString(userAgent);
			if (DeviceType.MOBILE.getName().equals(agent.getOperatingSystem().getDeviceType().getName()))
			{
				grantedAuthoritys.add(new SimpleGrantedAuthority(AuthorityEnum.makeAdminXid("3")));
			}
			else
			{
				grantedAuthoritys.add(new SimpleGrantedAuthority(AuthorityEnum.makeAdminXid("2")));
			}

			return new UsernamePasswordAuthenticationToken(user, "", grantedAuthoritys);
		}
		catch (IOException | URISyntaxException e)
		{
			throw new DefaultException(e);
		}
	}
}
