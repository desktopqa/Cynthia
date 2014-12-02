package com.sogou.qadev.service.cynthia.service.impl;

import java.sql.Timestamp;

import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.ExecuteTime;
import com.sogou.qadev.service.cynthia.bean.Script;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.impl.ScriptImpl;
import com.sogou.qadev.service.cynthia.dao.ScriptImportAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode;
import com.sogou.qadev.service.cynthia.service.ScriptAccessSession;

/**
 * @description:TODO
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午6:16:11
 * @version:v1.0
 */
abstract public class AbstractScriptAccessSession implements ScriptAccessSession
{
	protected String username = null;

	protected long keyId = 0;

	/**
	 * <h1> Title:</h1>
	 * <p> Description:</p>
	 * @date：2014-5-6 
	 * @param username
	 * @param keyId
	 */
	public AbstractScriptAccessSession(String username, long keyId)
	{
		super();

		this.username = username;
		this.keyId = keyId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:createScript</p>
	 * @param createUser
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.ScriptAccessSession#createScript(java.lang.String)
	 */
	public Script createScript(String createUser)
	{
		UUID scriptId = DataAccessFactory.getInstance().newUUID("SCRI");
		Timestamp createTime = new Timestamp(System.currentTimeMillis());
		return new ScriptImpl(scriptId,createUser,createTime);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:addScript</p>
	 * @param script
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.ScriptAccessSession#addScript(com.sogou.qadev.service.cynthia.bean.Script)
	 */
	public UUID addScript(Script script)
	{
		return addScriptInternal(script);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:updateScript</p>
	 * @param script
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.ScriptAccessSession#updateScript(com.sogou.qadev.service.cynthia.bean.Script)
	 */
	public ErrorCode updateScript(Script script)
	{
		return updateScriptInternal(script);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:removeScript</p>
	 * @param id
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.ScriptAccessSession#removeScript(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public ErrorCode removeScript(UUID id)
	{
		return removeScriptInternal(id);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryScript</p>
	 * @param scriptId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.ScriptAccessSession#queryScript(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Script queryScript(UUID scriptId)
	{
		return queryScriptInternal(scriptId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryScripts</p>
	 * @param createUser
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.ScriptAccessSession#queryScripts(java.lang.String)
	 */
	public Script[] queryScripts(String createUser)
	{
		return queryScriptsInternal(createUser);
	}

	/**
	 * @description:query scripts by data and executetime
	 * @date:2014-5-6 下午6:16:37
	 * @version:v1.0
	 * @param data
	 * @param executeTime
	 * @param das
	 * @return
	 */
	public Script[] queryScripts(Data data, ExecuteTime executeTime, DataAccessSession das)
	{
		return queryScriptsInternal(data, executeTime, das);
	}

	/**
	 * @description:query all allowd scripts of template
	 * @date:2014-5-6 下午6:17:02
	 * @version:v1.0
	 * @param templateId
	 * @return
	 */
	public Script[] queryAllowedTemplateScripts(UUID templateId)
	{
		return this.queryAllowedTemplateScriptsInternal(templateId);
	}
	
	/**
	 * @description:query all scripts of template
	 * @date:2014-5-6 下午6:17:17
	 * @version:v1.0
	 * @param templateId
	 * @return
	 */
	public Script[] queryTemplateScripts(UUID templateId)
	{
		return this.queryTemplateScriptsInternal(templateId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getScriptImportStr</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.ScriptAccessSession#getScriptImportStr()
	 */
	@Override
	public String getScriptImportStr()
	{
		return new ScriptImportAccessSessionMySQL().query();
	}

	/**
	 * @description:query all script from db by template
	 * @date:2014-5-6 下午6:17:35
	 * @version:v1.0
	 * @param templateId
	 * @return
	 */
	abstract protected Script[] queryTemplateScriptsInternal(UUID templateId);
	
	/**
	 * @description:query all allowed script by template 
	 * @date:2014-5-6 下午6:18:11
	 * @version:v1.0
	 * @param templateId
	 * @return
	 */
	abstract protected Script[] queryAllowedTemplateScriptsInternal(UUID templateId);
	
	/**
	 * @description:add script to db
	 * @date:2014-5-6 下午6:18:27
	 * @version:v1.0
	 * @param script
	 * @return
	 */
	abstract protected UUID addScriptInternal(Script script);

	/**
	 * @description:update script from db
	 * @date:2014-5-6 下午6:18:35
	 * @version:v1.0
	 * @param script
	 * @return
	 */
	abstract protected ErrorCode updateScriptInternal(Script script);

	/**
	 * @description:remove script from db
	 * @date:2014-5-6 下午6:18:44
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	abstract protected ErrorCode removeScriptInternal(UUID id);

	/**
	 * @description:query script by script id
	 * @date:2014-5-6 下午6:18:54
	 * @version:v1.0
	 * @param scriptId
	 * @return
	 */
	abstract protected Script queryScriptInternal(UUID scriptId);
	
	/**
	 * @description:query all scripts create by user
	 * @date:2014-5-6 下午6:19:05
	 * @version:v1.0
	 * @param createUser
	 * @return
	 */
	abstract protected Script[] queryScriptsInternal(String createUser);
	
	/**
	 * @description:query all scripts executable by data and executeTime
	 * @date:2014-5-6 下午6:19:20
	 * @version:v1.0
	 * @param data
	 * @param executeTime
	 * @param das
	 * @return
	 */
	abstract protected Script[] queryScriptsInternal(Data data, ExecuteTime executeTime, DataAccessSession das);
}
