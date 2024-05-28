package com.kcube.cloud.pub;

import java.io.Serializable;

public class PairValue<V1, V2> implements Serializable
{
	private static final long serialVersionUID = -638482034955860864L;

	private V1 value1;
	private V2 value2;

	public PairValue()
	{
		this(null, null);
	}

	public PairValue(V1 value1, V2 value2)
	{
		this.value1 = value1;
		this.value2 = value2;
	}

	public V1 getValue1()
	{
		return value1;
	}

	public void setValue1(V1 value1)
	{
		this.value1 = value1;
	}

	public V2 getValue2()
	{
		return value2;
	}

	public void setValue2(V2 value2)
	{
		this.value2 = value2;
	}
}
