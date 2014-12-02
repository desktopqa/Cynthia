package com.sogou.qadev.service.cynthia.bean;

public enum Method
{
	equals(1),
	notequals(2),
	in(3),
	notin(4),
	like(5),
	notlike(6),
	isnull(7),
	isnotnull(8),
	gt(9),
	ge(10),
	lt(11),
	le(12);
	
	public final int value;
	
	Method(int i)
	{
		value = i;
	}
	
	public static Method convert(String str)
	{
		if(str.equals("="))
			return Method.equals;
		if(str.equals("!="))
			return Method.notequals;
		if(str.equals("in"))
			return Method.in;
		if(str.equals("not in"))
			return Method.notin;
		if(str.equals("like"))
			return Method.like;
		if(str.equals("not like"))
			return Method.notlike;
		if(str.equals("is null"))
			return Method.isnull;
		if(str.equals("is not null"))
			return Method.isnotnull;
		if(str.equals(">"))
			return Method.gt;
		if(str.equals(">="))
			return Method.ge;
		if(str.equals("<"))
			return Method.lt;
		if(str.equals("<="))
			return Method.le;
		
		return null;
	}
}
