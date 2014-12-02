package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;

/**
 * @description:stat interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:25:18
 * @version:v1.0
 */
public interface Stat extends Serializable{
	/**
	 * @description:get stat id
	 * @date:2014-5-6 下午4:25:27
	 * @version:v1.0
	 * @return
	 */
	public UUID getId();
	
	/**
	 * @description:get flow id
	 * @date:2014-5-6 下午4:25:36
	 * @version:v1.0
	 * @return
	 */
	public UUID getFlowId();
	
	/**
	 * @description:get stat name
	 * @date:2014-5-6 下午4:25:43
	 * @version:v1.0
	 * @return
	 */
	public String getName();
	
	/**
	 * @description:set stat name
	 * @date:2014-5-6 下午4:25:50
	 * @version:v1.0
	 * @param name
	 */
	public void setName(String name);
	
	public Stat clone();
}
