package com.sogou.qadev.service.cynthia.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sogou.qadev.cache.Cache;
import com.sogou.qadev.cache.impl.FieldNameCache;
import com.sogou.qadev.cache.impl.FlowCache;
import com.sogou.qadev.cache.impl.TemplateCache;
import com.sogou.qadev.cache.impl.TemplateTypeCache;
import com.sogou.qadev.cache.impl.UserInfoCache;
import com.sogou.qadev.service.cynthia.bean.Action;
import com.sogou.qadev.service.cynthia.bean.Attachment;
import com.sogou.qadev.service.cynthia.bean.ChangeLog;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.DataAccessAction;
import com.sogou.qadev.service.cynthia.bean.DataAccessEntry;
import com.sogou.qadev.service.cynthia.bean.ExecuteTime;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Field.Type;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Option;
import com.sogou.qadev.service.cynthia.bean.Pair;
import com.sogou.qadev.service.cynthia.bean.QueryCondition;
import com.sogou.qadev.service.cynthia.bean.Role;
import com.sogou.qadev.service.cynthia.bean.Script;
import com.sogou.qadev.service.cynthia.bean.Single;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.TemplateOperateLog;
import com.sogou.qadev.service.cynthia.bean.TemplateType;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.bean.impl.DataImpl;
import com.sogou.qadev.service.cynthia.bean.impl.FlowImpl;
import com.sogou.qadev.service.cynthia.bean.impl.TemplateImpl;
import com.sogou.qadev.service.cynthia.bean.impl.UserInfoImpl;
import com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.dao.FlowAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.dao.TemplateAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.dao.TemplateLogAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.dao.UserInfoAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.ConfigManager;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.DataFilter;
import com.sogou.qadev.service.cynthia.service.MailManager;
import com.sogou.qadev.service.cynthia.service.ProjectInvolveManager;
import com.sogou.qadev.service.cynthia.service.ScriptAccessSession;
import com.sogou.qadev.service.cynthia.service.ScriptExecuteManager;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;

abstract public class AbstractDataAccessSession implements DataAccessSession
{
	public static final int maxTransactionCount = 1024;
	
	private static Logger logger = Logger.getLogger(AbstractDataAccessSession.class);

	protected String username = null;
	
	protected String agent = null;
	protected long keyId = 0;

	protected boolean isAutoCommit = false;

	protected boolean isInTransaction = false;

	protected boolean inScript = false;

	// <id,inserted>
	protected HashMap<UUID, Boolean> newUUIDTable = new HashMap<UUID, Boolean>();

	protected HashMap<UUID, DataAccessEntry<Data>> dataStatus = new LinkedHashMap<UUID, DataAccessEntry<Data>>();
	
	
	protected DataFilter dataFilter = null;
	
	public AbstractDataAccessSession(String username, String agent, long keyId)
	{
		checkCreateSessionPrivilege(username, agent, keyId);
		this.username = username;
	}

	public String getUsername()
	{
		return username;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public long getKeyId() {
		return keyId;
	}

	public void setKeyId(long keyId) {
		this.keyId = keyId;
	}

	// work
	protected boolean checkCreateSessionPrivilege(String username, String agent, long keyId)
	{
		if (keyId == DataAccessFactory.magic)
			return true;
		if(username == null)
			return false;
		
		return true;
	}

	@Override
	public synchronized void updateCache(DataAccessAction daa, String key, Object object){
		
		if (object instanceof Template) {
			Cache<Template> cache = TemplateCache.getInstance();
			if (daa.equals(DataAccessAction.insert) || daa.equals(DataAccessAction.update)) {
				cache.set(key, object);
			}else if (daa.equals(DataAccessAction.delete)) {
				cache.remove(new UUID[]{DataAccessFactory.getInstance().createUUID(key)});
			} 
		}else if (object instanceof TemplateType) {
			Cache<TemplateType> cache = TemplateTypeCache.getInstance();
			if (daa.equals(DataAccessAction.insert) || daa.equals(DataAccessAction.update)) {
				cache.set(key, object);
			}else if (daa.equals(DataAccessAction.delete)) {
				cache.remove(new UUID[]{DataAccessFactory.getInstance().createUUID(key)});
			} 
		}else if (object instanceof Flow) {
			Cache<Flow> cache = FlowCache.getInstance();
			if (daa.equals(DataAccessAction.insert) || daa.equals(DataAccessAction.update)) {
				cache.set(key, object);
			}else if (daa.equals(DataAccessAction.delete)) {
				cache.remove(new UUID[]{DataAccessFactory.getInstance().createUUID(key)});
			} 
		}else if (object instanceof UserInfo) {
			if (daa.equals(DataAccessAction.insert) || daa.equals(DataAccessAction.update)) {
				UserInfoCache.getInstance().set(key, object);
			}else if (daa.equals(DataAccessAction.delete)) {
				UserInfoCache.getInstance().remove(key);
			} 
		}
	}
	
	public boolean checkUserPrivilege(Data data, DataAccessAction action){
		if (data == null) {
			return false;
		}
		Template template = queryTemplate(data.getTemplateId());
		if (template == null) {
			return false;
		}
		Flow flow = queryFlow(template.getFlowId());
		if (flow == null) {
			return false;
		}
		return checkUserPrivilege(data, action, template, flow);
	}
	
	public boolean checkUserPrivilege(Data data, DataAccessAction action , Template template , Flow flow)
	{
		if (inScript)
			return true;

		if(action.equals(DataAccessAction.read))
		{
			if (data.getCreateUsername() != null && data.getCreateUsername().equals(getUsername())) {
				//自己创建的数据有可读权限
				return true;
			}else if (ConfigManager.getProjectInvolved()){
				//同公司创建数据有读取权限
				return ProjectInvolveManager.getInstance().getCompanyUserMails(data.getCreateUsername()).contains(getUsername());
			}else {
				String[] logUserArray = new String[data.getChangeLogs().length];
				for(int i = 0; i < data.getChangeLogs().length; i++)
					logUserArray[i] = data.getChangeLogs()[i].getCreateUser();
				
				return flow.isReadActionAllow(getUsername(), template.getId(), data.getAssignUsername(), logUserArray);
			}
		}

		return true;
	}

	public ErrorCode beginTranscation()
	{
		if (isInTransaction)
		{
			ErrorCode commitRet = commitTranscation();
			isInTransaction = true;
			return commitRet;
		}
		else
		{
			isInTransaction = true;
			return ErrorCode.success;
		}
	}

	public ErrorCode rollbackTranscation()
	{
		cleanTranscation();
		if (isAutoCommit)
			isInTransaction = false;

		return ErrorCode.success;
	}

	protected void cleanTranscation()
	{
		newUUIDTable.clear();
		dataStatus.clear();
	}

	public ErrorCode commitTranscation()
	{
		ErrorCode commitRet = null;

		commitRet = commmitData();

		if (isAutoCommit)
			isInTransaction = false;

		return commitRet;
	}

	@SuppressWarnings("unchecked")
	protected ErrorCode commmitData()
	{
		ErrorCode errorCode = ErrorCode.success;
		try
		{
			errorCode = recordDataToSQL();
			cleanTranscation();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			errorCode = ErrorCode.dbFail;
		}

		return errorCode;
	}

	@SuppressWarnings("unchecked")
	protected ErrorCode recordDataToSQL()
	{
		ErrorCode errorCode = ErrorCode.success;
		boolean isSuccess = true;
		for(DataAccessEntry<Data> entry : dataStatus.values())
		{
			if(entry.getAction().equals(DataAccessAction.delete)){
				isSuccess = new DataAccessSessionMySQL().remove(new UUID[]{entry.getData().getId()});
				updateCache(DataAccessAction.delete, entry.getData().getId().getValue(), entry.getData());//删除可以不用事务
				//删除标签中数据
				deleteTagData("", new String[]{entry.getData().getId().getValue()});
			}else if(entry.getAction().equals(DataAccessAction.insert)){
				isSuccess = new DataAccessSessionMySQL().add(entry.getData());  //插入和更新必须等提交成功后才更新缓存
			}else if (entry.getAction().equals(DataAccessAction.update)) {
				isSuccess =new DataAccessSessionMySQL().modify(entry.getData());
				if(isSuccess)
					updateCache(DataAccessAction.update, entry.getData().getId().getValue(), entry.getData());
			}
			if (!isSuccess) {
				errorCode = ErrorCode.dbFail;
			}
		}
		
		
		return errorCode;
	}

	protected ErrorCode checkTransactionCount()
	{
		ErrorCode errorCode = ErrorCode.success;
		if (dataStatus.size() >= maxTransactionCount && (!isAutoCommit || isInTransaction))
		{
			errorCode = commitTranscation();
			dataFilter = null;
			errorCode = beginTranscation();
		}
		return errorCode;
	}

	public boolean isAutoCommit()
	{
		return isAutoCommit;
	}

	public void setAutoCommit(boolean b)
	{
		if (isAutoCommit == b)
			return;
		else
		{
			if (!isInTransaction)
			{
				if (!isAutoCommit)
					commitTranscation();
				else
					beginTranscation();
			}

			isAutoCommit = b;
		}
	}
	
	public Data addData(UUID templateId)
	{
		Template template = queryTemplate(templateId);
		if (template == null)
			return null;
		
		UUID dataId = DataAccessFactory.getInstance().newDataUUID(template.getId().getValue());
		newUUIDTable.put(dataId, false);
		DataImpl data = new DataImpl(dataId, templateId, getUsername(), new Timestamp(System.currentTimeMillis()));

		return data;
	}
	
	public Data copyData(UUID dataId, UUID templateId,UUID actionId, String actionUser, String actionComment,
			Map<String, Pair<Object, Object>> baseValueMap, Map<UUID, Pair<Object, Object>> extValueMap)
	{
		Data data = queryData(dataId,templateId);
		if(data == null)
			return null;
		
		UUID newDataId = DataAccessFactory.getInstance().newDataUUID(templateId.getValue());
		newUUIDTable.put(newDataId, false);
		
		DataImpl newData = new DataImpl(newDataId, data.getTemplateId(), data.getCreateUsername(), data.getCreateTime());
		
		//set title
		if(baseValueMap != null && baseValueMap.get("title") != null)
			newData.setTitle((String)baseValueMap.get("title").getSecond());
		else
			newData.setTitle(data.getTitle());
		
		//set description
		if(baseValueMap != null && baseValueMap.get("description") != null)
			newData.setDescription((String)baseValueMap.get("description").getSecond());
		else
			newData.setDescription(data.getDescription());
		
		//set assignUser
		if(baseValueMap != null && baseValueMap.get("assignUser") != null)
			newData.setAssignUsername((String)baseValueMap.get("assignUser").getSecond());
		else
			newData.setAssignUsername(data.getAssignUsername());
		
		//set lastModifyTime
		newData.setLastModifyTime(new Timestamp(System.currentTimeMillis()));
		
		//set statusId
		if(baseValueMap != null && baseValueMap.get("statusId") != null)
			newData.setStatusId((UUID)baseValueMap.get("statusId").getSecond());
		else
			newData.setStatusId(data.getStatusId());
		
		//set fieldId value
		UUID[] fieldIdArray = data.getValidFieldIds();
		if(fieldIdArray != null)
		{
			for(UUID fieldId : fieldIdArray)
				newData.setObject(fieldId, data.getObject(fieldId));
		}
		
		if(extValueMap != null)
		{
			for(UUID fieldId : extValueMap.keySet())
				newData.setObject(fieldId, extValueMap.get(fieldId).getSecond());
		}
		
		//add old changeLog
		ChangeLog[] logArray = data.getChangeLogs();
		if(logArray != null)
		{
			for(ChangeLog log : logArray)
				newData.addChangeLog(log);
		}
		
		//set new changeLog
		newData.setObject("logActionId", actionId);
		newData.setObject("logCreateUser", actionUser);
		newData.setObject("logActionComment", actionComment);
		newData.setObject("logBaseValueMap", baseValueMap);
		newData.setObject("logExtValueMap", extValueMap);
		
		return newData;
	}
	
	public Data queryData(UUID dataId){
		Data data = null;
		if (dataStatus.containsKey(dataId)){
			DataAccessEntry<Data> entry = dataStatus.get(dataId);
			if (entry.getAction().equals(DataAccessAction.insert) || entry.getAction().equals(DataAccessAction.update))
				data = entry.getData();
		}else{
				data = new DataAccessSessionMySQL().queryData(dataId);
		}
		
		return data;
	}
	
	/**
	 * @function：从数据库查询数据，并组装
	 * @modifyTime：2013-9-5 上午10:59:55
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param dataId 
	 */
	public Data queryData(UUID dataId,UUID templateId)
	{
		Data data = null;
		
		if (dataStatus.containsKey(dataId)){
			DataAccessEntry<Data> entry = dataStatus.get(dataId);
			if (entry.getAction().equals(DataAccessAction.insert) || entry.getAction().equals(DataAccessAction.update))
				data = entry.getData();
		}else{
			if (templateId == null) {
				return queryData(dataId);
			}else {
				data = new DataAccessSessionMySQL(templateId).queryData(dataId, templateId);
			}
		}
		
		return data;
	}
	
	public List<Data> queryDataBySql(String sql, boolean needLog, UUID templateId)
	{
		return new DataAccessSessionMySQL().queryDatas(sql, needLog, templateId);
	}
	
	public String[] queryTemplateTypeCreateUsers(UUID templateTypeId)
	{
		return getDataFilter().queryTemplateTypeCreateUsers(templateTypeId);
	}
	
	public String[] queryTemplateTypeAssignUsers(UUID templateTypeId)
	{
		return getDataFilter().queryTemplateTypeAssignUsers(templateTypeId);
	}
	
	public String[] queryTemplateTypeStats(UUID templateTypeId)
	{
		return getDataFilter().queryTemplateTypeStats(templateTypeId);
	}
	
	public Data[] queryTemplateDatas(UUID templateId)
	{
		return queryTemplateDatas(templateId, true);
	}
	
	public Data[] queryDatas(UUID templateId, List<QueryCondition> queryConditions , boolean needLog){
		return getDataFilter().queryDatas(templateId,queryConditions,needLog);
	}
	
	public Data[] queryTemplateDatas(UUID templateId , boolean needLog)
	{
		return queryTemplateDatas(templateId, needLog, null, null);
	}
	
	public Data[] queryTemplateDatas(UUID templateId , boolean needLog , Timestamp startTime, Timestamp endTime)
	{
		return getDataFilter().queryTemplateDatas(templateId ,needLog ,startTime, endTime);
	}
	
	public Data[] queryTemplateDatas(UUID templateId , boolean needLog , Timestamp startTime, Timestamp endTime , List<QueryCondition> allQueryList){
		return getDataFilter().queryTemplateDatas(templateId, needLog, startTime, endTime,allQueryList);
	}
	
	public Data[] queryTemplateDatasByLastModifyTime(UUID templateId , boolean needLog , Timestamp startTime, Timestamp endTime)
	{
		return getDataFilter().queryTemplateDatasByLastModifyTime(templateId ,needLog ,startTime, endTime);
	}
	
	public String[] queryTemplateCreateUsers(UUID templateId)
	{
		return getDataFilter().queryTemplateCreateUsers(templateId);
	}
	
	public String[] queryTemplateAssignUsers(UUID templateId)
	{
		return getDataFilter().queryTemplateAssignUsers(templateId);
	}
	
	public Stat[] queryTemplateStats(UUID templateId)
	{
		return getDataFilter().queryTemplateStats(templateId);
	}
	
	public Data[] queryTemplateFieldReferences(UUID templateId, UUID fieldId)
	{
		return getDataFilter().queryTemplateFieldReferences(templateId, fieldId);
	}
	
	public Attachment[] queryTemplateFieldAttachments(UUID templateId, UUID fieldId)
	{
		return getDataFilter().queryTemplateFieldAttachments(templateId, fieldId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:removeData</p>
	 * @param data
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataAccessSession#removeData(com.sogou.qadev.service.cynthia.bean.Data)
	 */
	public ErrorCode removeData(Data data)
	{
		// check
		boolean checkPrivilege = checkUserPrivilege(data, DataAccessAction.delete);
		if (!checkPrivilege)
			return ErrorCode.privilegeFail;

		UUID id = data.getId();
		DataAccessEntry<Data> entry = new DataAccessEntry<Data>();

		entry.setAction(DataAccessAction.delete);
		entry.setData(data);

		dataStatus.put(id, entry);

		checkTransactionCount();

		if (isAutoCommit && !isInTransaction && !inScript)
			commitTranscation();

		return ErrorCode.success;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:modifyData</p>
	 * @param data
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataAccessSession#modifyData(com.sogou.qadev.service.cynthia.bean.Data)
	 */
	public Pair<ErrorCode, String> modifyData(Data data)
	{
		UUID id = data.getId();
		DataAccessAction action = null;
		
		Template template = queryTemplate(data.getTemplateId());
		if(template == null)
			return new Pair<ErrorCode, String>(ErrorCode.noSuchTemplateFail, null);
		
		Flow flow = queryFlow(template.getFlowId());
		if(flow == null)
			return new Pair<ErrorCode, String>(ErrorCode.noSuchTemplateFail, null);
		
		if ((newUUIDTable.containsKey(id) && !newUUIDTable.get(id))||(!this.isDataExist(id)))
			action = DataAccessAction.insert;
		else
			action = DataAccessAction.update;
		
		// check
		boolean checkPrivilege = checkUserPrivilege(data, action,template , flow);
		if (!checkPrivilege)
			return new Pair<ErrorCode, String>(ErrorCode.privilegeFail, null);

		// executeScript
		{
			Pair<String, Boolean> pair = executeScript(data, ExecuteTime.beforeCommit, template ,flow);
			if (!pair.getSecond())
				return new Pair<ErrorCode, String>(ErrorCode.scriptBeforeFail, pair.getFirst());
		}

		DataAccessEntry<Data> entry = dataStatus.get(id);
		if (entry != null && entry.getAction().equals(DataAccessAction.delete))
			return new Pair<ErrorCode, String>(ErrorCode.alreadyDeleted, null);

		((DataImpl)data).setLastModifyTime();
		
		entry = new DataAccessEntry<Data>();
		entry.setAction(action);
		entry.setData(data);

		dataStatus.put(id, entry);  

		checkTransactionCount();

		//send action mail 
		MailManager.sendActionMail(data);
		// executeScript
		
		{
			Pair<String, Boolean> pair = executeScript(data, ExecuteTime.afterSuccess, template ,flow);
			if (pair == null || !pair.getSecond() )
				return new Pair<ErrorCode, String>(ErrorCode.scriptAfterFail, pair.getFirst());
		}

		if (isAutoCommit && !isInTransaction && !inScript)
			return new Pair<ErrorCode, String>(commitTranscation(), null);
		return new Pair<ErrorCode, String>(ErrorCode.success, null);
	}

	public Template queryTemplate(UUID templateId)
	{
		return TemplateCache.getInstance().get(templateId);
	}
	
	public List<Template> queryTemplates(UUID templateTypeId){
		return TemplateCache.getInstance().getAll(templateTypeId);
	}
	
	public Field queryField(UUID fieldId )
	{
		return TemplateCache.getInstance().queryField(fieldId);
	}
	
	public Field queryField(UUID fieldId , UUID templateId)
	{
		Template template = TemplateCache.getInstance().get(templateId);
		if (template == null) {
			return null;
		}
		return template.getField(fieldId);
	}
	
	public Option queryOption(UUID optionId ,UUID templateId)
	{
		Template template = TemplateCache.getInstance().get(templateId);
		return queryOption(optionId, template);
	}

	public Option queryOption(UUID optionId ,Template template)
	{
		if (template == null) {
			return null;
		}
		
		for(Field field : template.getFields()){
			if(!field.getType().equals(Type.t_selection))
				continue;
				
			Option option = field.getOption(optionId);
			if(option != null)
				return option;
		}
		return null	;
	}
	
	public TemplateType queryTemplateType(UUID templateTypeId)
	{
		TemplateType templateType = TemplateTypeCache.getInstance().get(templateTypeId);

		if (templateType != null)
			return templateType;

		return null;
	}

	public void close()
	{
		commitTranscation();
	}

	public synchronized ErrorCode removeTemplate(Template template)
	{
		ErrorCode errorCode = ErrorCode.success;
		try
		{
			DataAccessEntry<Template> entry = new DataAccessEntry<Template>();
			entry.setData(template);
			entry.setAction(DataAccessAction.delete);
			if(!new TemplateAccessSessionMySQL().removeTemplateById(template.getId())) {//删除数据库
				errorCode = ErrorCode.dbFail;
			}else{
				//设置data表以及data_log表相应表单数据 is_valid=0 
				setValidDataOfTemplate(template.getId(), false);
			}
		}catch (Exception e)
		{
			e.printStackTrace();
			errorCode = ErrorCode.dbFail;
		}
		return errorCode;
	}

	/**
	 * @param data
	 * @param time
	 */
	protected Pair<String, Boolean> executeScript(Data data, ExecuteTime time , Template template , Flow flow)
	{
		boolean modifyInScript = false;
		if (!inScript)
		{
			inScript = true;
			modifyInScript = true;
		}

		try
		{
			ScriptAccessSession scriptAccessSession = createScriptAccessSession();
			Script[] scriptArray = scriptAccessSession.queryScripts(data, time, this , template,flow);
			
			if (scriptArray == null || scriptArray.length == 0)
				return new Pair<String, Boolean>(null, true);

			Single<Boolean> continueable = new Single<Boolean>();
			continueable.setFirst(true);

			String xml = ScriptExecuteManager.getInstance().execute(scriptArray, data, this, scriptAccessSession, continueable, time);
			return new Pair<String, Boolean>(xml, continueable.getFirst());
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			return new Pair<String, Boolean>("", false);
		}
		finally
		{
			if (modifyInScript)
				inScript = false;
		}
	}

	public synchronized Flow createFlow(String userName)
	{
		UUID flowId = DataAccessFactory.getInstance().newUUID("FLOW");
		Flow flow  = new FlowImpl(flowId);
		flow.setCreateUser(userName);
		return flow;
	}

	@Override
	public synchronized Template createTemplate(UUID templateTypeId)
	{
		if(templateTypeId == null)
			return null;
		
		UUID templateId = DataAccessFactory.getInstance().newUUID("TEMP");
		Template template =  new TemplateImpl(templateId, templateTypeId);
		return template;
	}
	
	public synchronized ErrorCode updateTemplate(Template template)
	{
		if(queryTemplateType(template.getTemplateTypeId()) == null)
			return ErrorCode.noSuchTemplateTypeFail;

		try {
			if (TemplateCache.getInstance().get(template.getId()) == null) {
				new TemplateAccessSessionMySQL().addTemplate(template);
			} else {
				new TemplateAccessSessionMySQL().updateTemplate(template);
			}
		} catch (Exception e) {
			logger.error("",e);
			return ErrorCode.dbFail;
		}
		
		return ErrorCode.success;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:removeFlow</p>
	 * <p> Description:TODO</p>
	 * @param flowId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataAccessSession#removeFlow(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public synchronized ErrorCode removeFlow(UUID flowId)
	{
		ErrorCode errorCode = ErrorCode.success;
		try {
			if(!new FlowAccessSessionMySQL().removeFlowById(flowId))
				errorCode = ErrorCode.dbFail;
		} catch (Exception e) {
			logger.error("",e);
			 errorCode = ErrorCode.dbFail;
		}
		return errorCode;
	}

	public Flow queryFlow(UUID flowId)
	{
		return FlowCache.getInstance().get(flowId);
	}
	
	public Stat queryStat(UUID statId , UUID flowId)
	{
		Flow flow = FlowCache.getInstance().get(flowId);
		return flow.getStat(statId);
	}
	
	public Action queryAction(UUID actionId ,UUID flowId)
	{
		Flow flow = FlowCache.getInstance().get(flowId);
		return flow.getAction(actionId);
	}
	
	public Role queryRole(UUID roleId,UUID flowId)
	{
		Flow flow = FlowCache.getInstance().get(flowId);
		return flow.getRole(roleId);
	}

	public Flow[] queryAllFlows()
	{
		List<Flow> allFlows = FlowCache.getInstance().getAll();
		return allFlows.toArray(new Flow[allFlows.size()]);
	}

	public Flow[] queryAllFlows(String userMail)
	{
		if (CynthiaUtil.isNull(userMail)) {
			return queryAllFlows();
		}else {
			List<Flow> allFlows = FlowCache.getInstance().getAll();
			List<Flow> returnFlows = new ArrayList<Flow>();
			
			if (ConfigManager.getProjectInvolved()) {
				Set<String> companyUsers = ProjectInvolveManager.getInstance().getCompanyUserMails(userMail);
				for (Flow flow : allFlows) {
					if (!companyUsers.contains(flow.getCreateUser())) {
						continue;
					}else {
						returnFlows.add(flow);
					}
				}
			}else {
				for (Flow flow : allFlows) {
					if (flow.isProFlow()) {
						continue;
					}else {
						returnFlows.add(flow);
					}
				}
			}
			return returnFlows.toArray(new Flow[returnFlows.size()]);
		}
	}

	public Template[] queryAllTemplates()
	{
		List<Template> allTemplates = TemplateCache.getInstance().getAll();
		return allTemplates.toArray(new Template[allTemplates.size()]);
	}

	public TemplateType[] queryAllTemplateTypes()
	{
		List<TemplateType> allTemplateTypes = TemplateTypeCache.getInstance().getAll();
		return allTemplateTypes.toArray(new TemplateType[allTemplateTypes.size()]);
	}

	public synchronized ErrorCode updateFlow(Flow flow)
	{	
		ErrorCode errorCode = ErrorCode.success;
		try {
			if (FlowCache.getInstance().get(flow.getId()) == null) {
				if(!new FlowAccessSessionMySQL().addFlow(flow))
					errorCode = ErrorCode.dbFail;
			} else {
				if(!new FlowAccessSessionMySQL().updateFlow(flow))
					errorCode = ErrorCode.dbFail;
			}
			
		} catch (Exception e) {
			logger.error("",e);
			errorCode = ErrorCode.dbFail;
		}
		return errorCode;
	}
	
	public ErrorCode removeDataFromCache(Data data)
	{
		UUID id = data.getId();
		if(dataStatus.containsKey(id))
		{
			dataStatus.remove(id);
		}
		updateCache(DataAccessAction.delete, id.getValue(), data);
		return ErrorCode.success;
	}


	public Flow[] queryFlows(UUID[] flowIdArray)
	{
		List<Flow> list = new ArrayList<Flow>();
		for (UUID id : flowIdArray)
		{
			Flow flow = FlowCache.getInstance().get(id);
			if (flow != null)
				list.add(flow);
		}

		return list.toArray(new Flow[list.size()]);
	}

	public Template[] queryTemplates(UUID[] templateIdArray)
	{
		List<Template> list = new ArrayList<Template>();
		for (UUID id : templateIdArray)
		{
			Template flow = TemplateCache.getInstance().get(id);
			if (flow != null)
				list.add(flow);
		}

		return list.toArray(new Template[list.size()]);
	}
	
	public TemplateType[] queryTemplateTypes(UUID[] templateTypeIdArray)
	{
		List<TemplateType> list = new ArrayList<TemplateType>();
		for (UUID id : templateTypeIdArray)
		{
			TemplateType templateType = TemplateTypeCache.getInstance().get(id);
			if (templateType != null)
				list.add(templateType);
		}

		return list.toArray(new TemplateType[list.size()]);
	}
	
	public Template queryTemplateByName(String templateName)
	{
		Template[] templates = this.queryAllTemplates();
		Template findTemplate = null;
		for(Template template : templates)
		{
			if(template.getName().equals(templateName))
				findTemplate = (Template) template.clone();
			else {
				template = null;
			}
		}
		templates = null;
		return findTemplate;
	}
	
	public List<UserInfo> queryAllUserInfo(String[] userArray , boolean isQuit){
		return new UserInfoAccessSessionMySQL().queryAllUserInfo(userArray);
	}
	
	public List<UserInfo> queryAllUserInfo(String[] userArray){
		if (ConfigManager.getProjectInvolved()) {
			List<UserInfo> allUserInfos = new ArrayList<UserInfo>();
			if (userArray != null && userArray.length > 0) {
				for (String user : userArray) {
					UserInfo userInfo = new UserInfoImpl();
					userInfo.setUserName(user);
					userInfo.setNickName(CynthiaUtil.getUserAlias(user));
					allUserInfos.add(userInfo);
				}
			}
			return allUserInfos;
			
		}else {
			return new UserInfoAccessSessionMySQL().queryAllUserInfo(userArray);
		}
	}
	
	public String getDbFieldName(UUID fieldId, UUID templateId){
		return FieldNameCache.getInstance().getFieldName(fieldId, templateId);
	}
	
	public boolean addTemplateOpreateLog(TemplateOperateLog templateOperateLog){
		return new TemplateLogAccessSessionMySQL().addTemplateAccessLog(templateOperateLog);
	}
}
