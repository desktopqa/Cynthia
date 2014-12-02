package com.sogou.qadev.service.cynthia.bean.impl;

import com.sogou.qadev.service.cynthia.bean.TimerAction;
import com.sogou.qadev.service.cynthia.bean.UUID;

/**
 * @description:timeraction implements
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:03:22
 * @version:v1.0
 */
public class TimerActionImpl implements TimerAction
{
	/**
	 * @Fields:serialVersionUID
	 * @Fields_Type:long
	 * @description:TODO
	 * @date:2014-5-6 下午5:03:33
	 */
	private static final long serialVersionUID = 2297215058155220911L;

	private UUID id = null;
	private String createUser = "";
	private String name = null;
	private String className = null;
	private String method = null;
	private String param = null;
	private boolean isPublic = false;
	
	/**
	 * <h1> Title:</h1>
	 * <p> Description:init timer action</p>
	 * @date：2014-5-6 
	 * @param id
	 */
	public TimerActionImpl(UUID id)
	{
		super();
		
		this.id = id;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getIsPublic</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.TimerAction#getIsPublic()
	 */
	public boolean getIsPublic()
	{
		return isPublic;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:setIsPublic</p>
	 * @param isPublic
	 * @see com.sogou.qadev.service.cynthia.bean.TimerAction#setIsPublic(boolean)
	 */
	public void setIsPublic(boolean isPublic)
	{
		this.isPublic = isPublic;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getClassName</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.TimerAction#getClassName()
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setClassName</p>
	 * @param className
	 * @see com.sogou.qadev.service.cynthia.bean.TimerAction#setClassName(java.lang.String)
	 */
	public void setClassName(String className)
	{
		this.className = className;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getCreateUser</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.TimerAction#getCreateUser()
	 */
	public String getCreateUser()
	{
		return this.createUser;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:setCreateUser</p>
	 * @param createUser
	 * @see com.sogou.qadev.service.cynthia.bean.TimerAction#setCreateUser(java.lang.String)
	 */
	public void setCreateUser(String createUser)
	{
		this.createUser = createUser;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getMethod</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.TimerAction#getMethod()
	 */
	public String getMethod()
	{
		return method;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setMethod</p>
	 * @param method
	 * @see com.sogou.qadev.service.cynthia.bean.TimerAction#setMethod(java.lang.String)
	 */
	public void setMethod(String method)
	{
		this.method = method;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getParam</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.TimerAction#getParam()
	 */
	public String getParam()
	{
		return param;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setParam</p>
	 * @param param
	 * @see com.sogou.qadev.service.cynthia.bean.TimerAction#setParam(java.lang.String)
	 */
	public void setParam(String param)
	{
		this.param = param;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.TimerAction#getId()
	 */
	public UUID getId()
	{
		return id;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getName</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.TimerAction#getName()
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setName</p>
	 * @param name
	 * @see com.sogou.qadev.service.cynthia.bean.TimerAction#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		this.name = name;
	}
}
