package com.kcube.cloud.mail;

import java.util.List;
import java.util.Map;

import com.kcube.cloud.error.DefaultException;

import net.sf.json.JSONArray;

public interface MailService
{
	public void deleteMailItems(List<Map<String, String>> items);

	public void insertMailLog(List<Map<String, String>> items, String opinion) throws DefaultException;

	public JSONArray findMailLog(String email);
}
