package com.kcube.cloud.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.net.util.SubnetUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.kcube.cloud.error.CustomException.PermissionDeniedException;

public class ScheduleMonitorServiceSecurity
{
	@Autowired
	private HttpServletRequest request;

	private List<String> allowAddrs;

	public List<String> getAllowAddrs()
	{
		return allowAddrs;
	}

	public void setAllowAddrs(List<String> allowAddrs)
	{
		List<String> tempList = new ArrayList<String>();
		for (String allowAddr : allowAddrs)
		{
			SubnetUtils utils = new SubnetUtils(allowAddr);
			utils.setInclusiveHostCount(true);
			tempList.addAll(Arrays.asList(utils.getInfo().getAllAddresses()));
		}

		this.allowAddrs = tempList;
	}

	public void checkAllowAddr() throws PermissionDeniedException
	{
		String remoteAddr = request.getRemoteAddr();

		if (!getAllowAddrs().contains(remoteAddr))
		{
			throw new PermissionDeniedException("not allowed remoteAddr!!");
		}
	}
}
