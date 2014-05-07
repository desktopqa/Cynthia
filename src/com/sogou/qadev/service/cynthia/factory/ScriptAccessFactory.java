package com.sogou.qadev.service.cynthia.factory;

import com.sogou.qadev.service.cynthia.dao.ScriptAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.service.ScriptAccessSession;

/**
 * @description:script process factory
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午12:00:46
 * @version:v1.0
 */
public class ScriptAccessFactory
{
	private static ScriptAccessFactory instance = null;

	public static synchronized final ScriptAccessFactory getInstance()
	{
		if (instance == null)
			instance = new ScriptAccessFactory();

		return instance;
	}

	private ScriptAccessFactory()
	{
		super();
	}

	/**
	 * @description:return script process interface
	 * @date:2014-5-6 下午12:01:05
	 * @version:v1.0
	 * @param username
	 * @param keyId
	 * @return
	 */
	public synchronized ScriptAccessSession createScriptAccessSession(String username, long keyId)
	{
		return new ScriptAccessSessionMySQL(username, keyId);
	}
}
