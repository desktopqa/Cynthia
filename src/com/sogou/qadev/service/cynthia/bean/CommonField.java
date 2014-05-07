package com.sogou.qadev.service.cynthia.bean;

/**
 * @description:common field bean
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午2:45:15
 * @version:v1.0
 */
public class CommonField
{
	/**
	 * field id
	 */
	private String id = null;
	/**
	 * field name
	 */
	private String name = null;
	/**
	 * field type
	 */
	private String type = null;
	/**
	 * field data Type
	 */
	private String dataType = null;
	/**
	 * if description 
	 */
	private boolean isDesc = false;
	
	private String method = null;
	private String value = null;
	private boolean isAll = false;
	
	public CommonField()
	{
		super();
	}
	
	public CommonField(String id, String name, String type, String dataType, boolean isDesc,
			String method, String value, boolean isAll)
	{
		this.id = id;
		this.name = name;
		this.type = type;
		this.dataType = dataType;
		this.isDesc = isDesc;
		this.isAll = isAll;
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
	
	public String getType()
	{
		return type;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public String getDataType()
	{
		return dataType;
	}
	
	public void setDataType(String dataType)
	{
		this.dataType = dataType;
	}
	
	public boolean isDesc()
	{
		return isDesc;
	}
	
	public void setDesc(boolean isDesc)
	{
		this.isDesc = isDesc;
	}
	
	public String getMethod()
	{
		return method;
	}
	
	public void setMethod(String method)
	{
		this.method = method;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	public boolean isAll()
	{
		return isAll;
	}
	
	public void setAll(boolean isAll)
	{
		this.isAll = isAll;
	}
}
