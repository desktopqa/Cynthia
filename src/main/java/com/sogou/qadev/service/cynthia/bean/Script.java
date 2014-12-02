package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @description:script interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:08:49
 * @version:v1.0
 */
public interface Script extends Serializable
{

	/**
	 * @description:get script action ids
	 * @date:2014-5-6 下午4:09:04
	 * @version:v1.0
	 * @return
	 */
	public UUID[] getActionIds();

	/**
	 * @description:set script action ids
	 * @date:2014-5-6 下午4:09:17
	 * @version:v1.0
	 * @param actionIdArray
	 */
	public void setActionIds(UUID[] actionIdArray);

	/**
	 * @description:get script create time
	 * @date:2014-5-6 下午4:09:30
	 * @version:v1.0
	 * @return
	 */
	public Timestamp getCreateTime();

	/**
	 * @description:get script create user
	 * @date:2014-5-6 下午4:09:45
	 * @version:v1.0
	 * @return
	 */
	public String getCreateUser();

	/**
	 * @description:get script end stat ids(after these stats execute script)
	 * @date:2014-5-6 下午4:09:56
	 * @version:v1.0
	 * @return
	 */
	public UUID[] getEndStatIds();

	/**
	 * @description:set script end stat ids
	 * @date:2014-5-6 下午4:10:36
	 * @version:v1.0
	 * @param endStatIdArray
	 */
	public void setEndStatIds(UUID[] endStatIdArray);

	/**
	 * @description:get script flow id
	 * @date:2014-5-6 下午4:10:48
	 * @version:v1.0
	 * @return
	 */
	public UUID[] getFlowIds();

	/**
	 * @description:set script flow id
	 * @date:2014-5-6 下午4:10:59
	 * @version:v1.0
	 * @param flowIdArray
	 */
	public void setFlowIds(UUID[] flowIdArray);

	/**
	 * @description:get script id
	 * @date:2014-5-6 下午4:11:09
	 * @version:v1.0
	 * @return
	 */
	public UUID getId();
	
	/**
	 * @description:get script name
	 * @date:2014-5-6 下午4:11:20
	 * @version:v1.0
	 * @param name
	 */
	public void setName(String name);

	/**
	 * @description:get if after data modify fail execute
	 * @date:2014-5-6 下午4:11:30
	 * @version:v1.0
	 * @return
	 */
	public boolean isAfterFail();

	/**
	 * @description:set after fail execute
	 * @date:2014-5-6 下午4:11:56
	 * @version:v1.0
	 * @param isAfterFail
	 */
	public void setAfterFail(boolean isAfterFail);

	/**
	 * @description:get if after query
	 * @date:2014-5-6 下午4:12:10
	 * @version:v1.0
	 * @return
	 */
	public boolean isAfterQuery();

	/**
	 * @description:set after query execute script
	 * @date:2014-5-6 下午4:12:24
	 * @version:v1.0
	 * @param isAfterQuery
	 */
	public void setAfterQuery(boolean isAfterQuery);

	/**
	 * @description:get after success
	 * @date:2014-5-6 下午4:12:44
	 * @version:v1.0
	 * @return
	 */
	public boolean isAfterSuccess();

	/**
	 * @description:set after success execute script
	 * @date:2014-5-6 下午4:13:02
	 * @version:v1.0
	 * @param isAfterSuccess
	 */
	public void setAfterSuccess(boolean isAfterSuccess);

	/**
	 * @description:get if async execute
	 * @date:2014-5-6 下午4:13:15
	 * @version:v1.0
	 * @return
	 */
	public boolean isAsync();

	/**
	 * @description:set if async execute
	 * @date:2014-5-6 下午4:13:28
	 * @version:v1.0
	 * @param isAsync
	 */
	public void setAsync(boolean isAsync);

	/**
	 * @description:get if before commit execute
	 * @date:2014-5-6 下午4:13:45
	 * @version:v1.0
	 * @return
	 */
	public boolean isBeforeCommit();

	/**
	 * @description:set if before commit execute
	 * @date:2014-5-6 下午4:14:00
	 * @version:v1.0
	 * @param isBeforeCommit
	 */
	public void setBeforeCommit(boolean isBeforeCommit);

	/**
	 * @description:get script name
	 * @date:2014-5-6 下午4:14:12
	 * @version:v1.0
	 * @return
	 */
	public String getName();

	/**
	 * @description:get script xml string
	 * @date:2014-5-6 下午4:14:22
	 * @version:v1.0
	 * @return
	 */
	public String getScript();

	/**
	 * @description:set script content
	 * @date:2014-5-6 下午4:14:35
	 * @version:v1.0
	 * @param script
	 */
	public void setScript(String script);

	/**
	 * @description:get begin stat ids
	 * @date:2014-5-6 下午4:14:45
	 * @version:v1.0
	 * @return
	 */
	public UUID[] getBeginStatIds();

	/**
	 * @description:set begin stat ids
	 * @date:2014-5-6 下午4:14:55
	 * @version:v1.0
	 * @param startStatIdArray
	 */
	public void setBeginStatIds(UUID[] startStatIdArray);

	/**
	 * @description:get template ids
	 * @date:2014-5-6 下午4:15:05
	 * @version:v1.0
	 * @return
	 */
	public UUID[] getTemplateIds();

	/**
	 * @description:set script template ids
	 * @date:2014-5-6 下午4:15:14
	 * @version:v1.0
	 * @param templateIdArray
	 */
	public void setTemplateIds(UUID[] templateIdArray);

	/**
	 * @description:get script template type ids
	 * @date:2014-5-6 下午4:15:27
	 * @version:v1.0
	 * @return
	 */
	public UUID[] getTemplateTypeIds();

	/**
	 * @description:set script template type ids
	 * @date:2014-5-6 下午4:15:41
	 * @version:v1.0
	 * @param templateTypeIdArray
	 */
	public void setTemplateTypeIds(UUID[] templateTypeIdArray);
	
	/**
	 * @description:set stat edit
	 * @date:2014-5-6 下午4:15:52
	 * @version:v1.0
	 * @param isStatEdit
	 */
	public void setStatEdit(boolean isStatEdit);
	
	/**
	 * @description:get stat edit
	 * @date:2014-5-6 下午4:16:06
	 * @version:v1.0
	 * @return
	 */
	public boolean isStatEdit();
	
	/**
	 * @description:set action edit
	 * @date:2014-5-6 下午4:16:20
	 * @version:v1.0
	 * @param isActionEdit
	 */
	public void setActionEdit(boolean isActionEdit);
	
	/**
	 * @description:get action edit
	 * @date:2014-5-6 下午4:16:28
	 * @version:v1.0
	 * @return
	 */
	public boolean isActionEdit();
	
	/**
	 * @description:set script create time
	 * @date:2014-5-6 下午4:18:50
	 * @version:v1.0
	 * @param createTime
	 */
	public void setCreateTime(Timestamp createTime);
	
	/**
	 * @description:set script create user
	 * @date:2014-5-6 下午4:19:25
	 * @version:v1.0
	 * @param createUser
	 */
	public void setCreateUser(String createUser);
	
	/**
	 * @description:set script valid
	 * @date:2014-5-6 下午4:16:38
	 * @version:v1.0
	 * @param isValid
	 */
	public void setValid(boolean isValid);
	
	/**
	 * @description:get script valid
	 * @date:2014-5-6 下午4:16:48
	 * @version:v1.0
	 * @return
	 */
	public boolean isValid();
	
	/**
	 * @description:set script allowed templates
	 * @date:2014-5-6 下午4:16:58
	 * @version:v1.0
	 * @param templateIdsArray
	 */
	public void setAllowedTemplateIds(UUID[] templateIdsArray);
	
	/**
	 * @description:get script allowed templates
	 * @date:2014-5-6 下午4:17:11
	 * @version:v1.0
	 * @return
	 */
	public UUID[] getAllowedTemplateIds();
}
