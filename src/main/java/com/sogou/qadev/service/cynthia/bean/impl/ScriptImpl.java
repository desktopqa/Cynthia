package com.sogou.qadev.service.cynthia.bean.impl;

import java.sql.Timestamp;

import com.sogou.qadev.service.cynthia.bean.Script;
import com.sogou.qadev.service.cynthia.bean.UUID;

/**
 * @description:scipt implements
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:17:36
 * @version:v1.0
 */
public class ScriptImpl implements Script
{
	private UUID id = null;
	private String name = null;
	private String createUser = null;
	private Timestamp createTime = null;
	private UUID[] templateTypeIdArray = null;
	private UUID[] templateIdArray = null;
	private UUID[] flowIdArray = null;
	private UUID[] beginStatIdArray = null;
	private UUID[] endStatIdArray = null;
	private UUID[] actionIdArray = null;
	private boolean isAsync = false;
	private boolean isBeforeCommit = false;
	private boolean isAfterSuccess = false;
	private boolean isAfterFail = false;
	private boolean isAfterQuery = false;
	private boolean isStatEdit = false;
	private boolean isActionEdit = false;
	private boolean isValid = false;
	private UUID[]  allowedTemplateIds = null;
	private String script = null;

	/**
	 * <h1> Title:</h1>
	 * <p> Description:init script</p>
	 * @date：2014-5-6 
	 * @param id
	 * @param createUser
	 * @param createTime
	 */
	public ScriptImpl(UUID id, String createUser, Timestamp createTime)
	{
		super();

		this.id = id;
		this.createUser = createUser;
		this.createTime = createTime;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getActionIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#getActionIds()
	 */
	public UUID[] getActionIds()
	{
		return actionIdArray;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:setActionIds</p>
	 * @param actionIdArray
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setActionIds(com.sogou.qadev.service.cynthia.bean.UUID[])
	 */
	public void setActionIds(UUID[] actionIdArray)
	{
		this.actionIdArray = actionIdArray;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getCreateTime</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#getCreateTime()
	 */
	public Timestamp getCreateTime()
	{
		return createTime;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setCreateTime</p>
	 * @param createTime
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setCreateTime(java.sql.Timestamp)
	 */
	public void setCreateTime(Timestamp createTime)
	{
		this.createTime = createTime;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getCreateUser</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#getCreateUser()
	 */
	public String getCreateUser()
	{
		return createUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setCreateUser</p>
	 * @param createUser
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setCreateUser(java.lang.String)
	 */
	public void setCreateUser(String createUser)
	{
		this.createUser = createUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getEndStatIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#getEndStatIds()
	 */
	public UUID[] getEndStatIds()
	{
		return endStatIdArray;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setEndStatIds</p>
	 * @param endStatIdArray
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setEndStatIds(com.sogou.qadev.service.cynthia.bean.UUID[])
	 */
	public void setEndStatIds(UUID[] endStatIdArray)
	{
		this.endStatIdArray = endStatIdArray;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getFlowIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#getFlowIds()
	 */
	public UUID[] getFlowIds()
	{
		return flowIdArray;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setFlowIds</p>
	 * @param flowIdArray
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setFlowIds(com.sogou.qadev.service.cynthia.bean.UUID[])
	 */
	public void setFlowIds(UUID[] flowIdArray)
	{
		this.flowIdArray = flowIdArray;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#getId()
	 */
	public UUID getId()
	{
		return id;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isAfterFail</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#isAfterFail()
	 */
	public boolean isAfterFail()
	{
		return isAfterFail;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setAfterFail</p>
	 * @param isAfterFail
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setAfterFail(boolean)
	 */
	public void setAfterFail(boolean isAfterFail)
	{
		this.isAfterFail = isAfterFail;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isAfterQuery</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#isAfterQuery()
	 */
	public boolean isAfterQuery()
	{
		return isAfterQuery;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setAfterQuery</p>
	 * @param isAfterQuery
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setAfterQuery(boolean)
	 */
	public void setAfterQuery(boolean isAfterQuery)
	{
		this.isAfterQuery = isAfterQuery;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isAfterSuccess</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#isAfterSuccess()
	 */
	public boolean isAfterSuccess()
	{
		return isAfterSuccess;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setAfterSuccess</p>
	 * @param isAfterSuccess
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setAfterSuccess(boolean)
	 */
	public void setAfterSuccess(boolean isAfterSuccess)
	{
		this.isAfterSuccess = isAfterSuccess;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isAsync</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#isAsync()
	 */
	public boolean isAsync()
	{
		return isAsync;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setAsync</p>
	 * @param isAsync
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setAsync(boolean)
	 */
	public void setAsync(boolean isAsync)
	{
		this.isAsync = isAsync;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isBeforeCommit</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#isBeforeCommit()
	 */
	public boolean isBeforeCommit()
	{
		return isBeforeCommit;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setBeforeCommit</p>
	 * @param isBeforeCommit
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setBeforeCommit(boolean)
	 */
	public void setBeforeCommit(boolean isBeforeCommit)
	{
		this.isBeforeCommit = isBeforeCommit;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getName</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#getName()
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setName</p>
	 * @param name
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getScript</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#getScript()
	 */
	public String getScript()
	{
		return script;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setScript</p>
	 * @param script
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setScript(java.lang.String)
	 */
	public void setScript(String script)
	{
		this.script = script;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getBeginStatIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#getBeginStatIds()
	 */
	public UUID[] getBeginStatIds()
	{
		return beginStatIdArray;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setBeginStatIds</p>
	 * @param beginStatIdArray
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setBeginStatIds(com.sogou.qadev.service.cynthia.bean.UUID[])
	 */
	public void setBeginStatIds(UUID[] beginStatIdArray)
	{
		this.beginStatIdArray = beginStatIdArray;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getTemplateIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#getTemplateIds()
	 */
	public UUID[] getTemplateIds()
	{
		return templateIdArray;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setTemplateIds</p>
	 * @param templateIdArray
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setTemplateIds(com.sogou.qadev.service.cynthia.bean.UUID[])
	 */
	public void setTemplateIds(UUID[] templateIdArray)
	{
		this.templateIdArray = templateIdArray;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getTemplateTypeIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#getTemplateTypeIds()
	 */
	public UUID[] getTemplateTypeIds()
	{
		return templateTypeIdArray;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setTemplateTypeIds</p>
	 * @param templateTypeIdArray
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setTemplateTypeIds(com.sogou.qadev.service.cynthia.bean.UUID[])
	 */
	public void setTemplateTypeIds(UUID[] templateTypeIdArray)
	{
		this.templateTypeIdArray = templateTypeIdArray;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isStatEdit</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#isStatEdit()
	 */
	public boolean isStatEdit() {
		return isStatEdit;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setStatEdit</p>
	 * @param isStatEdit
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setStatEdit(boolean)
	 */
	public void setStatEdit(boolean isStatEdit) {
		this.isStatEdit = isStatEdit;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isActionEdit</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#isActionEdit()
	 */
	public boolean isActionEdit() {
		return isActionEdit;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setActionEdit</p>
	 * @param isActionEdit
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setActionEdit(boolean)
	 */
	public void setActionEdit(boolean isActionEdit) {
		this.isActionEdit = isActionEdit;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getAllowedTemplateIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#getAllowedTemplateIds()
	 */
	public UUID[] getAllowedTemplateIds() {
		return allowedTemplateIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setAllowedTemplateIds</p>
	 * @param allowedTemplateIds
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setAllowedTemplateIds(com.sogou.qadev.service.cynthia.bean.UUID[])
	 */
	public void setAllowedTemplateIds(UUID[] allowedTemplateIds) {
		this.allowedTemplateIds = allowedTemplateIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isValid</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Script#isValid()
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setValid</p>
	 * @param isValid
	 * @see com.sogou.qadev.service.cynthia.bean.Script#setValid(boolean)
	 */
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	
}
