package com.sogou.qadev.service.cynthia.service;

import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.ExecuteTime;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Script;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode;

/**
 * @description:script access interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午6:14:03
 * @version:v1.0
 */
public interface ScriptAccessSession
{
	/**
	 * @description:create script
	 * @date:2014-5-6 下午6:14:19
	 * @version:v1.0
	 * @param createUser
	 * @return
	 */
	public Script createScript(String createUser);
	
	/**
	 * @description:get script import string
	 * @date:2014-5-6 下午6:14:30
	 * @version:v1.0
	 * @return
	 */
	public String getScriptImportStr();

	/**
	 * @description:add script to db
	 * @date:2014-5-6 下午6:14:46
	 * @version:v1.0
	 * @param script
	 * @return
	 */
	public UUID addScript(Script script);

	/**
	 * @description:update script
	 * @date:2014-5-6 下午6:14:56
	 * @version:v1.0
	 * @param script
	 * @return
	 */
	public ErrorCode updateScript(Script script);

	/**
	 * @description:remove script
	 * @date:2014-5-6 下午6:15:06
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public ErrorCode removeScript(UUID id);

	/**
	 * @description:query script by script id
	 * @date:2014-5-6 下午6:15:17
	 * @version:v1.0
	 * @param scriptId
	 * @return
	 */
	public Script queryScript(UUID scriptId);

	/**
	 * @description:query all script create by user
	 * @date:2014-5-6 下午6:15:30
	 * @version:v1.0
	 * @param createUser
	 * @return
	 */
	public Script[] queryScripts(String createUser);

	/**
	 * @description:query all executable scripts
	 * @date:2014-5-6 下午6:15:44
	 * @version:v1.0
	 * @param data
	 * @param executeTime
	 * @param das
	 * @param template
	 * @param flow
	 * @return
	 */
	public Script[] queryScripts(Data data, ExecuteTime executeTime, DataAccessSession das , Template template , Flow flow);
}