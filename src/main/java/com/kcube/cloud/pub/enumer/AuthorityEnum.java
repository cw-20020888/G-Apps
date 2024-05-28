package com.kcube.cloud.pub.enumer;

public enum AuthorityEnum implements AbstractEnum<Long>
{
	SINGLE(100000000L), DOUBLE(100000000L * 100000000L),

	USER(1L), BELOW(4L), EXACT(5L), FORM(6L);

	private Long value;

	private AuthorityEnum(Long value)
	{
		this.value = value;
	}

	@Override
	public Long value()
	{
		// TODO Auto-generated method stub
		return this.value;
	}

	public static Long makeXid(Long id, String type)
	{
		if (ConstantValueEnum.USER_TYPE.value().equals(type))
		{
			return AuthorityEnum.makeUserXid(id);
		}
		else if (ConstantValueEnum.DPRT_TYPE.value().equals(type))
		{
			return AuthorityEnum.makeBelowXid(id);
		}
		return null;
	}

	public static Long makeUserXid(Long userId)
	{
		long u = (userId == null) ? 0L : userId.longValue();
		return new Long(USER.value() * DOUBLE.value() + u);
	}

	public static Long makeExactXid(Long dprtId)
	{
		long d = (dprtId == null) ? 0L : dprtId.longValue();
		return new Long(EXACT.value() * DOUBLE.value() + d * SINGLE.value());
	}

	public static Long makeBelowXid(Long dprtId)
	{
		long d = (dprtId == null) ? 0L : dprtId.longValue();
		return new Long(BELOW.value() * DOUBLE.value() + d * SINGLE.value());
	}
	
	public static Long makeFormXid(Long formId)
	{
		long f = (formId == null) ? 0L : formId.longValue();
		return new Long(FORM.value() * DOUBLE.value() + f * SINGLE.value());
	}

	public static String makeUserXid(String userId)
	{
		long u = (userId == null) ? 0L : Long.valueOf(userId).longValue();
		return String.valueOf(USER.value() * DOUBLE.value() + u);
	}

	public static String makeExactXid(String dprtId)
	{
		long d = (dprtId == null) ? 0L : Long.valueOf(dprtId).longValue();
		return String.valueOf(EXACT.value() * DOUBLE.value() + d * SINGLE.value());
	}

	public static String makeBelowXid(String dprtId)
	{
		long d = (dprtId == null) ? 0L : Long.valueOf(dprtId).longValue();
		return String.valueOf(BELOW.value() * DOUBLE.value() + d * SINGLE.value());
	}
	
	public static String makeFormXid(String formId)
	{
		long f = (formId == null) ? 0L : Long.valueOf(formId).longValue();
		return String.valueOf(FORM.value() * DOUBLE.value() + f * SINGLE.value());
	}
	
	public static String makeAdminXid(String admin)
	{
		long u = (admin == null) ? 0L : Long.valueOf(admin).longValue();
		return String.valueOf(USER.value() * DOUBLE.value() + u * SINGLE.value());
	}
	
	public static boolean isFormId(Long xid)
	{
		return (xid / (FORM.value() * DOUBLE.value())) > 0;
	}
	
	public static boolean isFormId(String xid)
	{
		return isFormId(Long.valueOf(xid));
	}
	
	public static boolean isDprtId(Long xid)
	{
		return !isFormId(xid) && (xid / (BELOW.value() * DOUBLE.value())) > 0;
	}

	public static boolean isDprtId(String xid)
	{
		return isDprtId(Long.valueOf(xid));
	}

	public static Long getUserId(Long xid)
	{
		return new Long((xid % (USER.value() * DOUBLE.value())));
	}

	public static Long getUserId(String xid)
	{
		return (Long.valueOf(xid) % (USER.value() * DOUBLE.value()));
	}
	
	public static Long getDprtId(Long xid)
	{
		return new Long((xid % (BELOW.value() * DOUBLE.value())) / SINGLE.value());
	}

	public static Long getDprtId(String xid)
	{
		return (Long.valueOf(xid) % (BELOW.value() * DOUBLE.value())) / SINGLE.value();
	}
	
	public static Long getFormId(Long xid)
	{
		return new Long((xid % (FORM.value() * DOUBLE.value())) / SINGLE.value());
	}

	public static Long getFormId(String xid)
	{
		return (Long.valueOf(xid) % (FORM.value() * DOUBLE.value())) / SINGLE.value();
	}
}
