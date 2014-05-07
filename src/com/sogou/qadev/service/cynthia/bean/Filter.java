package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @description:filter interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午3:48:24
 * @version:v1.0
 */
public interface Filter extends Serializable
{
	/**
	 * @description:get filter id
	 * @date:2014-5-6 下午3:48:34
	 * @version:v1.0
	 * @return
	 */
	public UUID getId();
	
	/**
	 * @description:get filter name
	 * @date:2014-5-6 下午3:48:42
	 * @version:v1.0
	 * @return
	 */
	public String getName();
	
	/**
	 * @description:get filter is public
	 * @date:2014-5-6 下午3:48:50
	 * @version:v1.0
	 * @return
	 */
	public boolean isPublic();
	
	/**
	 * @description:get filter is force
	 * @date:2014-5-6 下午3:49:14
	 * @version:v1.0
	 * @return
	 */
	public boolean isForce();
	
	/**
	 * @description:get filter is and
	 * @date:2014-5-6 下午3:49:36
	 * @version:v1.0
	 * @return
	 */
	public boolean isAnd();
	
	/**
	 * @description:get filter create user
	 * @date:2014-5-6 下午3:49:47
	 * @version:v1.0
	 * @return
	 */
	public String getCreateUser();
	
	/**
	 * @description:get filter create time
	 * @date:2014-5-6 下午3:49:56
	 * @version:v1.0
	 * @return
	 */
	public Timestamp getCreateTime();
	
	/**
	 * @description:get filter xml
	 * @date:2014-5-6 下午3:50:06
	 * @version:v1.0
	 * @return
	 */
	public String getXml();
	
	/**
	 * @description:get filter father folder id
	 * @date:2014-5-6 下午3:50:18
	 * @version:v1.0
	 * @return
	 */
	public UUID getFatherId();
	
	/**
	 * @description:set filter name
	 * @date:2014-5-6 下午3:50:31
	 * @version:v1.0
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * @description:set filter public
	 * @date:2014-5-6 下午3:50:40
	 * @version:v1.0
	 * @param isPublic
	 */
	public void setPublic(boolean isPublic);
	
	/**
	 * @description:set filter force
	 * @date:2014-5-6 下午3:50:52
	 * @version:v1.0
	 * @param isForce
	 */
	public void setForce(boolean isForce);
	
	/**
	 * @description:set filter and
	 * @date:2014-5-6 下午3:51:01
	 * @version:v1.0
	 * @param isAnd
	 */
	public void setAnd(boolean isAnd);
	
	/**
	 * @description:set filter xml
	 * @date:2014-5-6 下午3:51:10
	 * @version:v1.0
	 * @param xml
	 */
	public void setXml(String xml);
	
	/**
	 * @description:get filter isvisible
	 * @date:2014-5-6 下午3:51:18
	 * @version:v1.0
	 * @return
	 */
	public boolean isVisible();
	
	/**
	 * @description:set filter visible
	 * @date:2014-5-6 下午3:51:30
	 * @version:v1.0
	 * @param isVisible
	 */
	public void setVisible(boolean isVisible);
	
	/**
	 * @description:get filter isvalid
	 * @date:2014-5-6 下午3:51:41
	 * @version:v1.0
	 * @return
	 */
	public boolean isValid();
	
	/**
	 * @description:set filter isvalid
	 * @date:2014-5-6 下午3:51:55
	 * @version:v1.0
	 * @param isValid
	 */
	public void setValid(boolean isValid);
}
