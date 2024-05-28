package com.kcube.cloud.pub.enumer;

public enum ConstantValueEnum implements AbstractEnum<String>
{
	SESN_ATTR_USERINFO("userInfo"), SESN_ATTR_USEHEADER("useHeader"), SESN_ATTR_TENANT("tenant"), SESN_ATTR_TOOLTIP(
		"tooltip"), SESN_ATTR_JSERVERNAME("jserverName"), SESN_ATTR_JSESSIONID("jsessionId"),

	JSON_DATA("jsonData"), JSON_RESULT("result"), JSON_RESULT_CNT("resultCnt"), JSON_PARENT_RESULT(
		"parentResult"), JSON_ORG_RESULT("orgResult"), JSON_TABLE_STATE("tableState"),

	ASPECT_ITEMS("items"), ASPECT_ADDFILES("addFiles"), ASPECT_DELFILES("delFiles"), ASPECT_FILE_ROOT("fileRoot"),

	DOWNLOAD_VIEW("download"), DOWNLOAD_FILE_PATH("filePath"), DOWNLOAD_FILE_NAME("fileName"), DOWNLOAD_FILE_SIZE(
		"fileSize"), INLINE_FILE("inline"), DELETE_FILE("delete"),

	DOWNLOAD_EXCEL_VIEW("downloadExcel"), DOWNLOAD_EXCEL_OBJECT("excelObject"),

	DOWNLOAD_PDF_VIEW("downloadPdf"),

	NEW_DOC("NEW_DOCUMENT"), COPY_DOC("COPY"), DOC_NUM_FORMAT("${dprt}-${year}-${seq}"), DEFAULT_ROOT_MAP_ID("-1"),

	ADMIN_CONFIG_REDIS_KEY("ADMIN_CONFIG"), ADMIN_CONFIG_REDIS_TOPIC("ADMIN_CONFIG_TOPIC"),

	SCHEDULE_REDIS_TOPIC("SCHEDULE_TOPIC"), WEB_SOCKET_REDIS_TOPIC("WEB_SOCKET_TOPIC"),

	MENU_REDIS_KEY("MENU"), MENU_REDIS_TOPIC("MENU_TOPIC"),

	SMTP_SECURITY_TLS("TLS"), SMTP_SECURITY_SSL("SSL"),

	TEMP_PAGE("TEMP"), UPDATE_PAGE("UPDATE"), COPY_PAGE("COPY"), REWRITE_PAGE("REWRITE"), WRITE_PAGE(
		"WRITE"), RECEIVE_PAGE("RECEIVE"),

	USER_TYPE("U"), DPRT_TYPE("D"), APPR_LINE("결재"), AGREE_LINE("합의"), COOPERATE_LINE("협조"), AUDITOR_LINE(
		"감사"), RCV_LINE("접수"),

	NUM1("1"), NUM2("2"), NUM3("3"), NUM4("4"), NUM5("5"), NUM6("6"), NUM7("7"), NUM8("8"), NUM9("9"), NUM10("10"),

	JSON("JSON"), XML("XML"), HTML("HTML"),

	YES("Y"), NO("N"), ALL("ALL"),

	WS_TENANT_ID("1000"), WS_SYSTEM_ID("APPR"),

	SUPER_ADMIN_TENANT_ID("1"), DEMO_TENANT_ID("2");

	private String value;

	private ConstantValueEnum(String value)
	{
		this.value = value;
	}

	@Override
	public String value()
	{
		// TODO Auto-generated method stub
		return this.value;
	}
}
