package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;

/**
 * @description:option interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午3:57:26
 * @version:v1.0
 */
public interface Option extends Serializable{
	/**
	 * @description:get option id
	 * @date:2014-5-6 下午3:57:36
	 * @version:v1.0
	 * @return
	 */
	public UUID getId();
	
	/**
	 * @description:get option field id
	 * @date:2014-5-6 下午3:57:46
	 * @version:v1.0
	 * @return
	 */
	public UUID getFieldId();
	
	/**
	 * @description:get option name
	 * @date:2014-5-6 下午3:57:57
	 * @version:v1.0
	 * @return
	 */
	public String getName();
	
	/**
	 * @description:set option name
	 * @date:2014-5-6 下午3:58:06
	 * @version:v1.0
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * @description:get option description
	 * @date:2014-5-6 下午3:58:15
	 * @version:v1.0
	 * @return
	 */
	public String getDescription();
	
	/**
	 * @description:set option description
	 * @date:2014-5-6 下午3:58:26
	 * @version:v1.0
	 * @param description
	 */
	public void setDescription(String description);
	
	/**
	 * @description:get option control option id
	 * @date:2014-5-6 下午3:58:38
	 * @version:v1.0
	 * @return
	 */
	public UUID getControlOptionId();
	
	/**
	 * @description:set option control option id
	 * @date:2014-5-6 下午3:58:52
	 * @version:v1.0
	 * @param controlOptionId
	 */
	public void setControlOptionId(UUID controlOptionId);
	
	/**
	 * @description:get option father optionid
	 * @date:2014-5-6 下午3:59:05
	 * @version:v1.0
	 * @return
	 */
	public UUID getFatherOptionId();
	
	/**
	 * @description:set option father option
	 * @date:2014-5-6 下午3:59:21
	 * @version:v1.0
	 * @param fatherOptionId
	 */
	public void setFatherOptionId(UUID fatherOptionId);
	
	/**
	 * @description:get option forbidden
	 * @date:2014-5-6 下午3:59:34
	 * @version:v1.0
	 * @return
	 */
	public Forbidden getForbidden();
	
	/**
	 * @description:set option forbidden
	 * @date:2014-5-6 下午3:59:55
	 * @version:v1.0
	 * @param forbidden
	 */
	public void setForbidden(Forbidden forbidden);
	
	/**
	 * @description:get option index from field
	 * @date:2014-5-6 下午4:00:07
	 * @version:v1.0
	 * @return
	 */
	public int getIndexOrder();
	
	/**
	 * @description:set option index from field
	 * @date:2014-5-6 下午4:00:27
	 * @version:v1.0
	 * @param indexOrder
	 */
	public void setIndexOrder(int indexOrder);
	
	/**
	 * @description:option clone
	 * @date:2014-5-6 下午4:00:40
	 * @version:v1.0
	 * @return
	 */
	public Option clone();
	
	/**
	 * @description:option to xml string
	 * @date:2014-5-6 下午4:03:28
	 * @version:v1.0
	 * @return
	 */
	public String toXMLString();
	
	/**
	 * @description:option forbidden enum
	 * @author:liming
	 * @mail:liming@sogou-inc.com
	 * @date:2014-5-6 下午4:00:54
	 * @version:v1.0
	 */
	public enum Forbidden{
		f_forbidden, f_permit;
	}
}
