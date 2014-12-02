package com.sogou.qadev.service.cynthia.bean;

public class CommonOption implements Comparable<CommonOption>
{
	private String id = null;
	private String name = null;
	
	public CommonOption()
	{
		super();
	}
	
	public CommonOption(String id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public int hashCode()
	{
		return id.hashCode();
	}
	
	public boolean equals(Object obj)
	{
		return id.equals(((CommonOption)obj).id);
	}
	
	public int compareTo(CommonOption option)
	{
		if(id.equals(option.id))
			return 0;
		
		int cmp = name.compareTo(option.name);
		if(cmp >= 0)
			return 1;
		
		return -1;
	}
}
