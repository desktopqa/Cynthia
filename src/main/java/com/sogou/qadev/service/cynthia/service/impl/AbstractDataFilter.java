package com.sogou.qadev.service.cynthia.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sogou.qadev.cache.impl.FieldNameCache;
import com.sogou.qadev.cache.impl.FlowCache;
import com.sogou.qadev.cache.impl.TemplateCache;
import com.sogou.qadev.service.cynthia.bean.Attachment;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.DataAccessAction;
import com.sogou.qadev.service.cynthia.bean.DataAccessEntry;
import com.sogou.qadev.service.cynthia.bean.ExecuteTime;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Field.DataType;
import com.sogou.qadev.service.cynthia.bean.Field.Type;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.QueryCondition;
import com.sogou.qadev.service.cynthia.bean.Script;
import com.sogou.qadev.service.cynthia.bean.Single;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.dao.LogAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.DataFilter;
import com.sogou.qadev.service.cynthia.service.ScriptAccessSession;
import com.sogou.qadev.service.cynthia.service.ScriptExecuteManager;
import com.sogou.qadev.service.cynthia.service.TableRuleManager;
import com.sogou.qadev.service.cynthia.util.QueryUtil;

/**
 * @description:TODO
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午6:20:09
 * @version:v1.0
 */
abstract public class AbstractDataFilter implements DataFilter
{
	public DataAccessSession getDataAccessSession()
	{
		return dataAccessSession;
	}

	protected DataAccessSession dataAccessSession = null;
	
	protected HashMap<UUID, DataAccessEntry<Data>> modifiedDatas = null;

	public AbstractDataFilter(DataAccessSession dataAccessSession)
	{
		super();

		this.dataAccessSession = dataAccessSession;

		checkUserPrivilege();
	}

	/**
	 * @description:TODO
	 * @date:2014-5-6 下午6:20:17
	 * @version:v1.0
	 * @return
	 */
	protected boolean checkUserPrivilege()
	{
		return true;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryData</p>
	 * @param id
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryData(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Data queryData(UUID id)
	{
		Data data = this.getDataAccessSession().queryData(id);
		if(data != null && !this.getDataAccessSession().checkUserPrivilege(data, DataAccessAction.read))
			data = null;
		return data;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryTemplateTypeCreateUsers</p>
	 * @param templateTypeId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryTemplateTypeCreateUsers(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public String[] queryTemplateTypeCreateUsers(UUID templateTypeId){
		return new DataAccessSessionMySQL().queryFieldByTemplateType(templateTypeId, "createUser");
	}

	/**
	 * 	(non-Javadoc)
	 * <p> Title:queryTemplateTypeAssignUsers</p>
	 * @param templateTypeId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryTemplateTypeAssignUsers(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public String[] queryTemplateTypeAssignUsers(UUID templateTypeId){
		return new LogAccessSessionMySQL().queryField(templateTypeId, "assignUser");
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryTemplateTypeStats</p>
	 * @param templateTypeId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryTemplateTypeStats(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public String[] queryTemplateTypeStats(UUID templateTypeId){
		Set<String> statSet = new LinkedHashSet<String>();
		List<Template> allTemplates = TemplateCache.getInstance().getAll();
		
		for(Template template : allTemplates){
			if(template == null || !template.getTemplateTypeId().equals(templateTypeId)){
				continue;
			}
			
			Flow flow = FlowCache.getInstance().get(template.getFlowId());
			if(flow == null){
				continue;
			}
			
			for(Stat stat : flow.getStats()){
				statSet.add(stat.getName());
			}
		}
		return statSet.toArray(new String[statSet.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryTemplateDatas</p>
	 * @param templateId
	 * @param needLog
	 * @param startTime
	 * @param endTime
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryTemplateDatas(com.sogou.qadev.service.cynthia.bean.UUID, boolean, java.sql.Timestamp, java.sql.Timestamp)
	 */
	public Data[] queryTemplateDatas(UUID templateId , boolean needLog , Timestamp startTime, Timestamp endTime){
		
		List<Data> allDataList = new ArrayList<Data>();
		if (templateId != null) {
			String tableName = TableRuleManager.getInstance().getDataTableName(templateId);
			StringBuffer sqlBuffer = new StringBuffer();
			
			sqlBuffer.append("select * from ").append(tableName).append(" where templateId=").append(templateId.getValue());
			if (startTime != null) {
				sqlBuffer.append(" and createTime >= '").append(startTime.toString()).append("' ");
			}
			if (endTime != null) {
				sqlBuffer.append(" and createTime <= '").append(endTime.toString()).append("' ");
			}
			allDataList = new DataAccessSessionMySQL().queryDatas(sqlBuffer.toString() , needLog, templateId);
		}
		
		return allDataList.toArray(new Data[allDataList.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryTemplateDatas</p>
	 * @param templateId
	 * @param needLog
	 * @param startTime
	 * @param endTime
	 * @param allQueryList
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryTemplateDatas(com.sogou.qadev.service.cynthia.bean.UUID, boolean, java.sql.Timestamp, java.sql.Timestamp, java.util.List)
	 */
	public Data[] queryTemplateDatas(UUID templateId , boolean needLog , Timestamp startTime, Timestamp endTime , List<QueryCondition> allQueryList){
			
			List<Data> allDataList = new ArrayList<Data>();
			if (templateId != null) {
				String tableName = TableRuleManager.getInstance().getDataTableName(templateId);
				StringBuffer sqlBuffer = new StringBuffer();
				
				sqlBuffer.append("select * from ").append(tableName).append(" where templateId=").append(templateId.getValue());
				if (startTime != null) {
					sqlBuffer.append(" and createTime >= '").append(startTime.toString()).append("' ");
				}
				if (endTime != null) {
					sqlBuffer.append(" and createTime <= '").append(endTime.toString()).append("' ");
				}
				
				for (QueryCondition qc : allQueryList) {
					sqlBuffer.append(" and ").append(qc.getQueryField() + " ").append(qc.getQueryMethod()).append(" " + qc.getQueryValue() + " ");
				}
				allDataList = new DataAccessSessionMySQL().queryDatas(sqlBuffer.toString() , needLog, templateId);
			}
			
			return allDataList.toArray(new Data[allDataList.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryTemplateDatasByLastModifyTime</p>
	 * @param templateId
	 * @param needLog
	 * @param startTime
	 * @param endTime
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryTemplateDatasByLastModifyTime(com.sogou.qadev.service.cynthia.bean.UUID, boolean, java.sql.Timestamp, java.sql.Timestamp)
	 */
	public Data[] queryTemplateDatasByLastModifyTime(UUID templateId , boolean needLog , Timestamp startTime, Timestamp endTime){
		
		List<Data> allDataList = new ArrayList<Data>();
		if (templateId != null) {
			String tableName = TableRuleManager.getInstance().getDataTableName(templateId);
			StringBuffer sqlBuffer = new StringBuffer();
			
			sqlBuffer.append("select * from ").append(tableName).append(" where is_valid=1 and templateId=").append(templateId.getValue());
			if (startTime != null) {
				sqlBuffer.append(" and lastModifyTime >= '").append(startTime.toString()).append("' ");
			}
			if (endTime != null) {
				sqlBuffer.append(" and lastModifyTime <= '").append(endTime.toString()).append("' ");
			}
			allDataList = new DataAccessSessionMySQL().queryDatas(sqlBuffer.toString() , needLog, templateId);
		}
		
		return allDataList.toArray(new Data[allDataList.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryDatas</p>
	 * @param templateId
	 * @param queryConditions
	 * @param needLog
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryDatas(com.sogou.qadev.service.cynthia.bean.UUID, java.util.List, boolean)
	 */
	public Data[] queryDatas(UUID templateId, List<QueryCondition> queryConditions, boolean needLog){
		
		List<Data> allDataList = new ArrayList<Data>();
		Set<String> allQueryTables = new HashSet<String>();
		if (templateId != null) {
			allQueryTables.add(TableRuleManager.getInstance().getDataTableName(templateId));
		}else {
			allQueryTables.addAll(TableRuleManager.getInstance().getAllDataTables());
		}
		
		String whereStr = QueryUtil.getQueryWhereStr(queryConditions);
		StringBuffer sqlBuffer = new StringBuffer();
		for (String table : allQueryTables) {
			if (sqlBuffer.length() > 0) 
				sqlBuffer.append(" union ");
			sqlBuffer.append(" select id,templateId,createTime,lastModifyTime,statusId,assignUser,createUser,description,title from ").append(table).append(" where ").append(whereStr + " ");
		}
		
		allDataList = new DataAccessSessionMySQL().queryDatas(sqlBuffer.toString() , needLog, templateId);
		
		return allDataList.toArray(new Data[allDataList.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryTemplateCreateUsers</p>
	 * @param templateId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryTemplateCreateUsers(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public String[] queryTemplateCreateUsers(UUID templateId){
		
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct createUser from ").append(TableRuleManager.getInstance().getDataTableName(templateId)).append(" where templateId = ").append(templateId.getValue()).append(" and createUser is not null and is_valid=1");
		String[] assignUser = new DataAccessSessionMySQL().queryFieldBySql(sql.toString());
		
		return assignUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryTemplateAssignUsers</p>
	 * @param templateId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryTemplateAssignUsers(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public String[] queryTemplateAssignUsers(UUID templateId){
		
		StringBuffer sql = new StringBuffer();
		sql.append("select distinct assignUser from ").append(TableRuleManager.getInstance().getDataLogTableName(templateId)).append(" where templateId = ").append(templateId.getValue()).append(" and assignUser is not null and is_valid=1");
		String[] assignUser = new DataAccessSessionMySQL().queryFieldBySql(sql.toString());
		return assignUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryTemplateStats</p>
	 * @param templateId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryTemplateStats(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Stat[] queryTemplateStats(UUID templateId){
		Template template = TemplateCache.getInstance().get(templateId);
		if(template == null){
			return new Stat[0];
		}
		
		Flow flow = FlowCache.getInstance().get(template.getFlowId());
		if(flow == null){
			return new Stat[0];
		}
		
		return flow.getStats();
	}
	

	/**
	 * TODO:slow
	 * (non-Javadoc)
	 * <p> Title:queryTemplateFieldReferences</p>
	 * @param templateId
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryTemplateFieldReferences(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Data[] queryTemplateFieldReferences(UUID templateId, UUID fieldId){
		Template template = dataAccessSession.queryTemplate(templateId);
		if(template == null){
			return new Data[0];
		}
		
		Field field = template.getField(fieldId);
		if(field == null || !field.getType().equals(Type.t_reference)){
			return new Data[0];
		}
		
		List<String> queryFieldsList = new ArrayList<String>();
		queryFieldsList.add("id");
		queryFieldsList.add("title");
		queryFieldsList.add("templateId");
		Map<String, String> whereMap = new HashMap<String, String>();
		
		Map<UUID, Data> refDataMap = new LinkedHashMap<UUID, Data>();
		
		String fieldColName = FieldNameCache.getInstance().getFieldName(fieldId ,templateId);
		
		String[] allRefValue = new DataAccessSessionMySQL().queryFieldByTemplate(templateId, fieldColName);
		
		for(String refValue : allRefValue){
			if (refValue == null || refValue.length() ==0) {
				continue;
			}
			
			if(field.getDataType().equals(DataType.dt_single)){
				whereMap.put("id", refValue);
				List<Data> refData = new DataAccessSessionMySQL().queryDatas(TableRuleManager.getInstance().getAllDataTables(), queryFieldsList, whereMap, null, null,false,templateId);
				
				if(refData != null && refData.size() > 0){
					refDataMap.put(DataAccessFactory.getInstance().createUUID(refValue), refData.get(0));
				}
			}
			else{
				String[] allRefValus = refValue.split(",");
				if(allRefValus == null){
					continue;
				}
				
				for(String refDataId : allRefValus){
					UUID refId = DataAccessFactory.getInstance().createUUID(refDataId);
					if(refDataMap.containsKey(refId)){
						continue;
					}
					
					whereMap.put("id", refDataId);
					List<Data> refData = new DataAccessSessionMySQL().queryDatas(TableRuleManager.getInstance().getAllDataTables(), queryFieldsList, whereMap, null, null,false,templateId);
					
					if(refData != null && refData.size() > 0){
						refDataMap.put(DataAccessFactory.getInstance().createUUID(refValue), refData.get(0));
					}
				}
			}
		}
		
		return refDataMap.values().toArray(new Data[refDataMap.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryTemplateFieldAttachments</p>
	 * @param templateId
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryTemplateFieldAttachments(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Attachment[] queryTemplateFieldAttachments(UUID templateId, UUID fieldId){
		
		Template template = TemplateCache.getInstance().get(templateId);
		if(template == null){
			return new Attachment[0];
		}
		
		Field field = template.getField(fieldId);
		if(field == null || !field.getType().equals(Type.t_attachment)){
			return new Attachment[0];
		}
		
		String fieldColName = FieldNameCache.getInstance().getFieldName(fieldId , templateId);
		
		String[] allAttaValue = new DataAccessSessionMySQL().queryFieldByTemplate(templateId, fieldColName);
		
		Map<UUID, Attachment> attachmentMap = new LinkedHashMap<UUID, Attachment>();
		
		for(String atta : allAttaValue){
			
			if (atta == null || atta.length() == 0) {
				continue;
			}
			
			String [] allAttaIds = atta.split(",");
			
			UUID[] attachmentIdArray = new UUID[allAttaIds.length];
			
			for (int i = 0; i < attachmentIdArray.length; i++) {
				attachmentIdArray[i] = DataAccessFactory.getInstance().createUUID(allAttaIds[i]);
			}
			
			if(attachmentIdArray == null || attachmentIdArray.length == 0){
				continue;
			}
			
			Attachment[] attachmentArray = this.getDataAccessSession().queryAttachments(attachmentIdArray, false);
			for(Attachment attachment : attachmentArray){
				attachmentMap.put(attachment.getId(), attachment);
			}
		}
		
		return attachmentMap.values().toArray(new Attachment[attachmentMap.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryDatas</p>
	 * @param xml
	 * @param queryConList
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryDatas(java.lang.String, java.util.List)
	 */
	public Data[] queryDatas(String xml ,List<QueryCondition> queryConList)
	{
		return queryDatas(xml, 0, 0,queryConList);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryDatas</p>
	 * @param xml
	 * @param pageNumber
	 * @param lineAccount
	 * @param queryConList
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryDatas(java.lang.String, int, int, java.util.List)
	 */
	public Data[] queryDatas(String xml, int pageNumber, int lineAccount , List<QueryCondition> queryConList)
	{
		return queryDatasInternal(xml, pageNumber, lineAccount, null , null,queryConList);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryDatas</p>
	 * @param xml
	 * @param pageNumber
	 * @param lineAccount
	 * @param sort
	 * @param dir
	 * @param queryConList
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryDatas(java.lang.String, int, int, java.lang.String, java.lang.String, java.util.List)
	 */
	@Override
	public Data[] queryDatas(String xml, int pageNumber, int lineAccount, String sort, String dir, List<QueryCondition> queryConList) {
		return queryDatasInternal(xml, pageNumber, lineAccount, sort,dir,queryConList);
	}

	/**
	 * @description:execute script of data
	 * @date:2014-5-6 下午6:21:33
	 * @version:v1.0
	 * @param data
	 * @return
	 */
	protected boolean executeScript(Data data)
	{
		if (data == null) {
			return false;
		}
		Template template = dataAccessSession.queryTemplate(data.getTemplateId());
		if (template == null) {
			return false;
		}
		
		Flow flow = dataAccessSession.queryFlow(template.getFlowId());
		if (flow == null) {
			return false;
		}
		
		ScriptAccessSession scriptAccessSession = dataAccessSession.createScriptAccessSession();
		Script[] scriptArray = scriptAccessSession.queryScripts(data, ExecuteTime.afterQuery, dataAccessSession , template,flow);
		if (scriptArray == null || scriptArray.length == 0)
			return true;

		Single<Boolean> continueable = new Single<Boolean>();
		continueable.setFirst(true);

		ScriptExecuteManager.getInstance().execute(scriptArray, data, dataAccessSession, scriptAccessSession, continueable, ExecuteTime.afterQuery);

		return continueable.getFirst();
	}

	/**
	 * @description:query datas from xml 
	 * @date:2014-5-6 下午6:21:50
	 * @version:v1.0
	 * @param xml
	 * @param pageNumber
	 * @param lineAccount
	 * @param sort
	 * @param dir
	 * @param queryConList
	 * @return
	 */
	abstract protected Data[] queryDatasInternal(String xml, int pageNumber, int lineAccount, String sort,String dir, List<QueryCondition> queryConList);
}