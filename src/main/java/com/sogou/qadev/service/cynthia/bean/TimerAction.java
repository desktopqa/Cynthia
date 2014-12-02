package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;

/**
 * @description:timer action interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:58:44
 * @version:v1.0
 */
public interface TimerAction extends Serializable
{
	/**
	 * @description:get timer action create user
	 * @date:2014-5-6 下午4:59:13
	 * @version:v1.0
	 * @return
	 */
	public String getCreateUser();
	
	/**
	 * @description:set timer action create user
	 * @date:2014-5-6 下午4:59:45
	 * @version:v1.0
	 * @param createUser
	 */
	public void setCreateUser(String createUser);
	
	/**
	 * @description:get timer action class name
	 * @date:2014-5-6 下午5:01:03
	 * @version:v1.0
	 * @return
	 */
	public String getClassName();

	/**
	 * @description:set timer action class name
	 * @date:2014-5-6 下午5:01:16
	 * @version:v1.0
	 * @param className
	 */
	public void setClassName(String className);

	/**
	 * @description:get timer action method
	 * @date:2014-5-6 下午5:01:44
	 * @version:v1.0
	 * @return
	 */
	public String getMethod();

	/**
	 * @description:set timer action  method
	 * @date:2014-5-6 下午5:01:53
	 * @version:v1.0
	 * @param method
	 */
	public void setMethod(String method);

	/**
	 * @description:get timer action  param
	 * @date:2014-5-6 下午5:02:03
	 * @version:v1.0
	 * @return
	 */
	public String getParam();

	/**
	 * @description:set timer action param
	 * @date:2014-5-6 下午5:02:11
	 * @version:v1.0
	 * @param param
	 */
	public void setParam(String param);

	/**
	 * @description:get timer action  id
	 * @date:2014-5-6 下午5:02:18
	 * @version:v1.0
	 * @return
	 */
	public UUID getId();

	/**
	 * @description:get timer action name
	 * @date:2014-5-6 下午5:02:26
	 * @version:v1.0
	 * @return
	 */
	public String getName();

	/**
	 * @description:set timer action name
	 * @date:2014-5-6 下午5:02:36
	 * @version:v1.0
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * @description:get timer action public
	 * @date:2014-5-6 下午5:02:44
	 * @version:v1.0
	 * @return
	 */
	public boolean getIsPublic();
	
	/**
	 * @description:set timer action public
	 * @date:2014-5-6 下午5:02:58
	 * @version:v1.0
	 * @param isPublic
	 */
	public void setIsPublic(boolean isPublic);
}
