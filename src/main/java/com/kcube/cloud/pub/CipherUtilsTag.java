package com.kcube.cloud.pub;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import com.kcube.cloud.util.ioc.CipherUtils;

public class CipherUtilsTag extends RequestContextAwareTag
{
	private static final long serialVersionUID = 2966389826744328543L;

	private CipherUtils cipherUtils;

	private String value;

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	protected int doStartTagInternal() throws Exception
	{
		if (cipherUtils == null)
		{
			WebApplicationContext wac = getRequestContext().getWebApplicationContext();
			cipherUtils = wac.getBean(CipherUtils.class);
		}

		pageContext.getOut().write(cipherUtils.hexToDecrypt(value));

		return SKIP_BODY;
	}
}
