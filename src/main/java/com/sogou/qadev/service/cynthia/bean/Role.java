package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;

import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;

/**
 * 
 * @description:role interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:05:54
 * @version:v1.0
 */
public interface Role extends Serializable{
	
	/**
	 * @description:get role id
	 * @date:2014-5-6 下午4:06:11
	 * @version:v1.0
	 * @return
	 */
	public UUID getId();

	/**
	 * @description:get flow id
	 * @date:2014-5-6 下午4:06:19
	 * @version:v1.0
	 * @return
	 */
	public UUID getFlowId();

	/**
	 * @description:get role name
	 * @date:2014-5-6 下午4:06:26
	 * @version:v1.0
	 * @return
	 */
	public String getName();

	/**
	 * @description:set role name
	 * @date:2014-5-6 下午4:06:35
	 * @version:v1.0
	 * @param name
	 */
	public void setName(String name);

	/**
	 * @description:role clone
	 * @date:2014-5-6 下午4:06:54
	 * @version:v1.0
	 * @return
	 */
	public Role clone();

	/**
	 * everyone role id
	 */
	public static UUID everyoneUUID = DataAccessFactory.getInstance().createUUID("82");
	/**
	 * everyone role name
	 */
	public static String everyoneName = "everyone";
}
