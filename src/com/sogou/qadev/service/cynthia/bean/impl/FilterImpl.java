package com.sogou.qadev.service.cynthia.bean.impl;

import java.sql.Timestamp;

import com.sogou.qadev.service.cynthia.bean.Filter;
import com.sogou.qadev.service.cynthia.bean.UUID;

/**
 * @description:filter implements
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午3:52:06
 * @version:v1.0
 */
public class FilterImpl implements Filter
{
	private UUID id = null;
	private String name = null;
	private String createUser = null;
	private Timestamp createTime = null;
	private boolean isPublic = false;
	private boolean isForce = false;
	private boolean isAnd = false;
	private String xml = null;
	private UUID fatherId = null;
	private boolean isVisible = false;
	private boolean isValid = true;
	
	/**
	 * <h1> Title:</h1>
	 * <p> Description:init filter</p>
	 * @date：2014-5-6 
	 * @param id
	 * @param createUser
	 * @param createTime
	 * @param fatherId
	 */
	public FilterImpl(UUID id, String createUser, Timestamp createTime, UUID fatherId)
	{
		super();
		this.id = id;
		this.createUser = createUser;
		this.createTime = createTime;
		this.fatherId = fatherId;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#getId()
	 */
	public UUID getId()
	{
		return id;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getName</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#getName()
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getCreateUser</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#getCreateUser()
	 */
	public String getCreateUser()
	{
		return createUser;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getCreateTime</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#getCreateTime()
	 */
	public Timestamp getCreateTime()
	{
		return createTime;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:isPublic</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#isPublic()
	 */
	public boolean isPublic()
	{
		return isPublic;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:isForce</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#isForce()
	 */
	public boolean isForce()
	{
		return isForce;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:isAnd</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#isAnd()
	 */
	public boolean isAnd()
	{
		return isAnd;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getXml</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#getXml()
	 */
	public String getXml()
	{
		return xml;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getFatherId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#getFatherId()
	 */
	public UUID getFatherId()
	{
		return fatherId;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:setName</p>
	 * @param name
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:setPublic</p>
	 * @param isPublic
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#setPublic(boolean)
	 */
	public void setPublic(boolean isPublic)
	{
		this.isPublic = isPublic;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:setForce</p>
	 * @param isForce
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#setForce(boolean)
	 */
	public void setForce(boolean isForce)
	{
		this.isForce = isForce;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:setAnd</p>
	 * @param isAnd
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#setAnd(boolean)
	 */
	public void setAnd(boolean isAnd)
	{
		this.isAnd = isAnd;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:setXml</p>
	 * @param xml
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#setXml(java.lang.String)
	 */
	public void setXml(String xml)
	{
		this.xml = xml;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:isVisible</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#isVisible()
	 */
	public boolean isVisible()
	{
		return isVisible;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:setVisible</p>
	 * @param isVisible
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#setVisible(boolean)
	 */
	public void setVisible(boolean isVisible)
	{
		this.isVisible = isVisible;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:isValid</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#isValid()
	 */
	public boolean isValid()
	{
		return this.isValid;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:setValid</p>
	 * @param isValid
	 * @see com.sogou.qadev.service.cynthia.bean.Filter#setValid(boolean)
	 */
	public void setValid(boolean isValid)
	{
		this.isValid = isValid;
	}
}
