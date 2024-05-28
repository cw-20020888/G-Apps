/*
 * Cloud Foundry 2012.02.03 Beta Copyright (c) [2009-2012] VMware, Inc. All Rights
 * Reserved. This product is licensed to you under the Apache License, Version 2.0 (the
 * "License"). You may not use this product except in compliance with the License. This
 * product includes a number of subcomponents with separate copyright notices and license
 * terms. Your use of these subcomponents is subject to the terms and conditions of the
 * subcomponent's license, as noted in the LICENSE file.
 */

package com.kcube.cloud.security.oauth2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.kcube.cloud.app.gapi.GoogleAuthMethod;
import com.kcube.cloud.app.gapi.directory.DirectoryUsersApi;
import com.kcube.cloud.pub.enumer.AuthorityEnum;
import com.kcube.cloud.user.User;
import com.kcube.cloud.user.User.Building;
import com.kcube.cloud.user.User.Resource;

import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * Copied from the original implementation of the
 * <code>DefaultUserAuthenticationConverter</code> to fix a bug in the
 * <code>getAuthorities</code> method. Rest all unchanged. Class with the original bug
 * <code>org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter</code>
 */
public class DefaultUserAuthenticationConverter
	extends org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter
{

	@Autowired
	@Qualifier("appProp")
	private Properties googleProp;

	@Autowired
	private HttpServletRequest request;

	@Override
	public Authentication extractAuthentication(Map<String, ?> map)
	{
		// if (map.containsKey(USERNAME) && map.containsKey("email"))
		// {
		// return getUsernamePasswordAuthenticationToken((String) map.get("email"));
		// }
		// throw new SessionAuthenticationException("not choice a tenant !!!!!!");
		return getUsernamePasswordAuthenticationToken((String) map.get("email"));
	}

	@SuppressWarnings("unchecked")
	public UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String email)
	{
		String exclusion = googleProp.getProperty("exclusion");

		DirectoryUsersApi adminService = new DirectoryUsersApi(
			googleProp.getProperty("google.adminEmail"), GoogleAuthMethod.SERVICE_ACCOUNT);
		com.google.api.services.admin.directory.model.User gUser = adminService.get(email);

		DirectoryUsersApi service = new DirectoryUsersApi(email, GoogleAuthMethod.SERVICE_ACCOUNT);

		List<Building> buildings = service.getUserBuildings("my_customer", exclusion);
		List<Resource> resources = service.getUserResources("my_customer");

		User.Location location = new User.Location();
		List<Map<String, String>> gLocations = (List<Map<String, String>>) gUser.getLocations();

		if (CollectionUtils.isNotEmpty(gLocations))
		{
			for (Map<String, String> gLocation : gLocations)
			{
				location.setFloorName(gLocation.get("floorName"));
				location.setBuildingId(gLocation.get("buildingId"));
			}
		}

		User user = new User();
		user.setId(gUser.getPrimaryEmail().split("@")[0]);
		user.setFullName(gUser.getName().getFullName());
		user.setPrimaryEmail(gUser.getPrimaryEmail());
		user.setIntraEmail(gUser.getPrimaryEmail());
		user.setGoogleLogin(true);
		user.setLocation(location);
		user.setBuildings(buildings);
		user.setResources(resources);

		List<GrantedAuthority> grantedAuthoritys = new ArrayList<GrantedAuthority>();

		UserAgent agent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		if (DeviceType.MOBILE.getName().equals(agent.getOperatingSystem().getDeviceType().getName()))
		{
			grantedAuthoritys.add(new SimpleGrantedAuthority(AuthorityEnum.makeAdminXid("3")));
		}
		else
		{
			String systemAdminEmails = googleProp.getProperty("system.adminEmails");
			if (systemAdminEmails != null)
			{
				for (String systemAdminEmail : systemAdminEmails.split(","))
				{
					if (systemAdminEmail.equals(gUser.getPrimaryEmail()))
					{
						grantedAuthoritys.add(new SimpleGrantedAuthority(AuthorityEnum.makeAdminXid("1")));
						break;
					}
				}
			}
			if (grantedAuthoritys.isEmpty())
			{
				grantedAuthoritys.add(new SimpleGrantedAuthority(AuthorityEnum.makeAdminXid("0")));
			}
		}

		return new UsernamePasswordAuthenticationToken(user, gUser.getPassword(), grantedAuthoritys);
	}
}
