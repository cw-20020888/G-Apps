package com.kcube.cloud.pub;

import java.io.IOException;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.kcube.cloud.util.ioc.CipherUtils;

public class DecryptSerializer extends JsonSerializer<String>
{
	@Override
	public void serialize(String value, JsonGenerator jgen, SerializerProvider provider)
		throws IOException,
			JsonProcessingException
	{
		WebApplicationContext cc = ContextLoader.getCurrentWebApplicationContext();
		CipherUtils cipherUtils = null;
		cipherUtils = cc.getBean(CipherUtils.class);
		jgen.writeString(cipherUtils.hexToDecrypt(value));
	}
}
