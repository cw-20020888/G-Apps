package com.kcube.cloud.pub;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

import com.kcube.cloud.util.CommonUtils;

public class VelocityLocaleFilter implements ReferenceInsertionEventHandler
{
	@Override
	public Object referenceInsert(String reference, Object value)
	{
		if (value == null)
		{
			return null;
		}
		if (value instanceof String)
		{
			return CommonUtils.getLocaleStr((String) value);
		}
		return value;
	}
}