package com.sogou.qadev.service.cynthia.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sogou.qadev.cache.impl.FieldNameCache;
import com.sogou.qadev.cache.impl.FlowCache;
import com.sogou.qadev.cache.impl.TemplateCache;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.Filter;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Pair;
import com.sogou.qadev.service.cynthia.bean.QueryCondition;
import com.sogou.qadev.service.cynthia.bean.Right;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.TableRuleManager;
import com.sogou.qadev.service.cynthia.util.CommonUtil;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;
import com.sogou.qadev.service.cynthia.util.Date;
import com.sogou.qadev.service.cynthia.util.FilterUtil;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

public class DataFilterMemory extends AbstractDataFilter
{
	/**
	 * @param dataAccessSession
	 * @param modifiedDatas
	 */
	public DataFilterMemory(DataAccessSession dataAccessSession)
	{
		super(dataAccessSession);
	}
	
	protected static DataAccessFactory dataAccessFactory = DataAccessFactory.getInstance();
	
	public static final String timeRegex = "今天|昨天|本周|上周|本月|上月|本季|上季|本年|去年|(过去|未来)[1-9][0-9]*(天|周|月|季|年)";
	
	public static final String DATA_TABLE_REPLATE_STRING = "$data_table$";  //替换sql语句
	public static final String DATALOG_TABLE_REPLATE_STRING = "$data_log_table$";
	
	
	protected static Set<String> baseSet = new HashSet<String>();
	protected static Map<String, String> baseMap = new HashMap<String, String>();
	static
	{
		baseSet.add("id");
		baseSet.add("title");
		baseSet.add("description");
		baseSet.add("create_user");
		baseSet.add("create_time");
		baseSet.add("assign_user");
		baseSet.add("last_modify_time");
		baseSet.add("status_id");
		baseSet.add("action_id");
		baseSet.add("action_user");
		baseSet.add("action_comment");
		baseSet.add("action_index");
	}
	static{
		baseMap.put("id", "id");
		baseMap.put("node_id", "templateId");
		baseMap.put("template_id", "templateId");
		baseMap.put("templateId", "templateId");
		baseMap.put("create_user", "createUser");
		baseMap.put("createUser", "createUser");
		baseMap.put("template_type_id", "tempalteTypeId");
		baseMap.put("title", "title");
		baseMap.put("description","description");   
		baseMap.put("status_id", "statusId");
		baseMap.put("statusId", "statusId");
		baseMap.put("create_time", "createTime");
		baseMap.put("createTime", "createTime");
		baseMap.put("last_modify_time", "lastModifyTime");
		baseMap.put("lastModifyTime", "lastModifyTime");
		baseMap.put("assign_user", "assignUser");
		baseMap.put("assignUser", "assignUser");
		baseMap.put("action_id", "logActionId");
		baseMap.put("action_index", "logActionIndex");
		baseMap.put("action_time_range", "logcreateTime");
		baseMap.put("action_user", "logcreateUser");
		baseMap.put("log_create_user", "logcreateUser");
		baseMap.put("priority", "fieldCom_1");
	}
	

	/**
	 * (non-Javadoc)
	 * <p> Title:queryDatasInternal</p>
	 * @param xml
	 * @param pageNumber
	 * @param lineAccount
	 * @param sort
	 * @param dir
	 * @param queryConList
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.impl.AbstractDataFilter#queryDatasInternal(java.lang.String, int, int, java.lang.String, java.lang.String, java.util.List)
	 */
	protected Data[] queryDatasInternal(String xml, int pageNumber, int lineAccount, String sort,String dir ,List<QueryCondition> queryConList)
	{
		return filter(xml, pageNumber, lineAccount, sort, dir, getDataAccessSession(),queryConList);
	}

	/**
	 * @description:query datas from xml and other query conditions
	 * @date:2014-5-6 下午6:22:13
	 * @version:v1.0
	 * @param xml
	 * @param pageNumber
	 * @param lineAccount
	 * @param queryConList
	 * @return
	 */
	public static Data[] filter(String xml, int pageNumber, int lineAccount ,List<QueryCondition> queryConList)
	{
		return filter(xml, pageNumber, lineAccount, null, queryConList);
	}
	
	/**
	 * @description:query datas from xml and other query conditions
	 * @date:2014-5-6 下午6:23:06
	 * @version:v1.0
	 * @param xml
	 * @param pageNumber
	 * @param lineAccount
	 * @param das
	 * @param queryConList
	 * @return
	 */
	public static Data[] filter(String xml, int pageNumber, int lineAccount, DataAccessSession das, List<QueryCondition> queryConList)
	{
		return filter(xml, pageNumber, lineAccount, null,null, das,queryConList);
	}
	
	/**
	 * @description:adjust the where node
	 * @date:2014-5-6 下午6:22:39
	 * @version:v1.0
	 * @param doc
	 * @param node
	 */
	public static void adjustWhereNode(Document doc , Node node){
		
		Node envNode = XMLUtil.getSingleNode(doc, "query/env");
		String idStr = XMLUtil.getAttribute(node, "id");
		UUID id = DataAccessFactory.getInstance().createUUID(idStr);
		String type = XMLUtil.getAttribute(node, "type");
		List<Flow> allFlows = null;
		//where fields
		Node whereNode = XMLUtil.getSingleNode(node, "where");
		
		if(whereNode != null){
		
			List<Node> whereFieldNodeList = XMLUtil.getNodes(whereNode, "field");
			for(Node whereFieldNode : whereFieldNodeList){
				String fieldId = XMLUtil.getAttribute(whereFieldNode, "id");
				String fieldMethod = XMLUtil.getAttribute(whereFieldNode, "method");
				String fieldType = XMLUtil.getAttribute(whereFieldNode, "type");
				String fieldDataType = XMLUtil.getAttribute(whereFieldNode, "dataType");
				String fieldValue = whereFieldNode.getTextContent();
				
				if(fieldValue.startsWith("$") && fieldValue.endsWith("$")){
					List<Node> defineNodeList = XMLUtil.getNodes(envNode, fieldValue.substring(1, fieldValue.length() - 1));
					if(defineNodeList.size() == 0){
						continue;
					}
					
					if(defineNodeList.size() == 1){
						whereFieldNode.setTextContent(defineNodeList.get(0).getTextContent());
					}
					else{
						Node leftNode = doc.createElement("condition");
						leftNode.setTextContent("(");
						whereNode.insertBefore(leftNode, whereFieldNode);
						
						if(fieldMethod.equals("!=")){
							Node newFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(newFieldNode, "method", "is null");
							whereNode.insertBefore(newFieldNode, whereFieldNode);
							
							Node orNode = doc.createElement("condition");
							orNode.setTextContent("or");
							whereNode.insertBefore(orNode, whereFieldNode);
						}
						
						for(int i = 0; i < defineNodeList.size(); i++){
							if(i > 0){
								Node relationNode = doc.createElement("condition");
								if(fieldMethod.equals("=")){
									relationNode.setTextContent("or");
								}
								else if(fieldMethod.equals("!=")){
									relationNode.setTextContent("and");
								}
								
								whereNode.insertBefore(relationNode, whereFieldNode);
							}
							
							Node newFieldNode = whereFieldNode.cloneNode(true);
							newFieldNode.setTextContent(defineNodeList.get(i).getTextContent());
							whereNode.insertBefore(newFieldNode, whereFieldNode);
						}
						
						//(
						Node rightNode = doc.createElement("condition");
						rightNode.setTextContent(")");
						whereNode.insertBefore(rightNode, whereFieldNode);
						
						whereNode.removeChild(whereFieldNode);
					}
				}
				
				if(fieldId.equals("status_id") && (fieldValue.equals("[逻辑开始]") || fieldValue.equals("[逻辑关闭]"))){
					StringBuffer statusIdStrb = new StringBuffer();
					List<Flow> flowList = new ArrayList<Flow>();
					
					if(type.equals("tt")){
						if (allFlows == null) {
							allFlows = FlowCache.getInstance().getAll();
						}
						if (allFlows == null) {
							continue;
						}
						
						flowList.addAll(allFlows);
						
					}else if(type.equals("t")){
						Template template = TemplateCache.getInstance().get(id);
						if(template == null){
							continue;
						}
						Flow flow = FlowCache.getInstance().get(template.getFlowId());
						if (flow == null) {
							continue;
						}
						flowList.add(flow);
					}
					
					for(Flow flow : flowList){
						if(flow == null){
							continue;
						}
						
						if(fieldValue.equals("[逻辑开始]") && flow.getBeginStats() != null){
							for(Stat stat : flow.getBeginStats()){
								if(statusIdStrb.length() > 0){
									statusIdStrb.append(",");
								}
								
								statusIdStrb.append(stat.getId());
							}
						}
						else if(fieldValue.equals("[逻辑关闭]") && flow.getEndStats() != null){
							for(Stat stat : flow.getEndStats()){
								if(statusIdStrb.length() > 0){
									statusIdStrb.append(",");
								}
								
								statusIdStrb.append(stat.getId());
							}
						}
					}
					
					if(statusIdStrb.length() == 0){
						continue;
					}
					
					if(fieldMethod.equals("=")){
						XMLUtil.setAttribute(whereFieldNode, "method", "in");
					}
					else if(fieldMethod.equals("!=")){
						XMLUtil.setAttribute(whereFieldNode, "method", "not in");
					}
					
					whereFieldNode.setTextContent(statusIdStrb.toString());
				}
				
				
				//创建人 指派人可以指派给角色
				if((fieldId.equals("create_user") || fieldId.equals("assign_user") || fieldId.equals("log_create_user")) && (fieldValue.startsWith("role_"))){
					
					String roleIdStr = fieldValue.substring(5);
					
					if (!CommonUtil.isPosNum(roleIdStr)) 
						continue;
					
					StringBuffer roleUsers = new StringBuffer();
					if (type.equals("t")) {
						Template template = TemplateCache.getInstance().get(id);
						if(template == null){
							continue;
						}
						Flow flow = FlowCache.getInstance().get(template.getFlowId());
						if (flow == null) {
							continue;
						}
						UUID roleId = DataAccessFactory.getInstance().createUUID(roleIdStr);
						Set<Right> allRoleRight = flow.queryRightsByRole(roleId,template.getId());
						
						for (Right right : allRoleRight) {
							roleUsers.append(roleUsers.length() > 0 ? "," : "").append(right.getUsername());
						}
					}
					
					if(roleUsers.length() == 0){
						continue;
					}
					
					if(fieldMethod.equals("=")){
						XMLUtil.setAttribute(whereFieldNode, "method", "in");
					}
					else if(fieldMethod.equals("!=")){
						XMLUtil.setAttribute(whereFieldNode, "method", "not in");
					}
					
					whereFieldNode.setTextContent(roleUsers.toString());
				}
				
				
				if(!fieldMethod.equalsIgnoreCase("is null")
						&& !fieldMethod.equalsIgnoreCase("is not null")
						&& !fieldValue.equals("")
						&& (fieldId.equals("create_time")
								|| fieldId.equals("last_modify_time")
								  ||fieldId.equals("action_time_range")
									|| CommonUtil.isPosNum(fieldId) && fieldType.equals("input") && fieldDataType.equals("timestamp"))){
					if(fieldValue.matches(timeRegex) || fieldValue.indexOf(" ") < 0){
						if(fieldMethod.equals("=")){
							Node leftNode = doc.createElement("condition");
							leftNode.setTextContent("(");
							whereNode.insertBefore(leftNode, whereFieldNode);
									
							Node firstFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(firstFieldNode, "method", ">=");
							firstFieldNode.setTextContent(getTimeSpan(fieldValue).getFirst());
							whereNode.insertBefore(firstFieldNode, whereFieldNode);
									
							Node andNode = doc.createElement("condition");
							andNode.setTextContent("and");
							whereNode.insertBefore(andNode, whereFieldNode);
									
							Node secondFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(secondFieldNode, "method", "<=");
							secondFieldNode.setTextContent(getTimeSpan(fieldValue).getSecond());
							whereNode.insertBefore(secondFieldNode, whereFieldNode);
									
							Node rightNode = doc.createElement("condition");
							rightNode.setTextContent(")");
							whereNode.insertBefore(rightNode, whereFieldNode);
									
							whereNode.removeChild(whereFieldNode);
						}
						else if(fieldMethod.equals("!=")){
							Node leftNode = doc.createElement("condition");
							leftNode.setTextContent("(");
							whereNode.insertBefore(leftNode, whereFieldNode);
									
							Node firstFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(firstFieldNode, "method", "<");
							firstFieldNode.setTextContent(getTimeSpan(fieldValue).getFirst());
							whereNode.insertBefore(firstFieldNode, whereFieldNode);
									
							Node andNode = doc.createElement("condition");
							andNode.setTextContent("or");
							whereNode.insertBefore(andNode, whereFieldNode);
									
							Node secondFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(secondFieldNode, "method", ">");
							secondFieldNode.setTextContent(getTimeSpan(fieldValue).getSecond());
							whereNode.insertBefore(secondFieldNode, whereFieldNode);
									
							Node rightNode = doc.createElement("condition");
							rightNode.setTextContent(")");
							whereNode.insertBefore(rightNode, whereFieldNode);
									
							whereNode.removeChild(whereFieldNode);
						}
						else if(fieldMethod.equals(">")){
							Node secondFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(secondFieldNode, "method", ">");
							secondFieldNode.setTextContent(getTimeSpan(fieldValue).getSecond());
							whereNode.insertBefore(secondFieldNode, whereFieldNode);
									
							whereNode.removeChild(whereFieldNode);	
						}
						else if(fieldMethod.equals(">=")){
							Node secondFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(secondFieldNode, "method", ">=");
							secondFieldNode.setTextContent(getTimeSpan(fieldValue).getFirst());
							whereNode.insertBefore(secondFieldNode, whereFieldNode);
									
							whereNode.removeChild(whereFieldNode);	
						}
						else if(fieldMethod.equals("<")){
							Node secondFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(secondFieldNode, "method", "<");
							secondFieldNode.setTextContent(getTimeSpan(fieldValue).getFirst());
							whereNode.insertBefore(secondFieldNode, whereFieldNode);
									
							whereNode.removeChild(whereFieldNode);	
						}
						else if(fieldMethod.equals("<=")){
							Node secondFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(secondFieldNode, "method", "<=");
							secondFieldNode.setTextContent(getTimeSpan(fieldValue).getSecond());
							whereNode.insertBefore(secondFieldNode, whereFieldNode);
									
							whereNode.removeChild(whereFieldNode);
						}
					}
				}
			}
		}
	}
	
	/**
	 * @description:get filter sql of filter
	 * @date:2014-5-6 下午6:23:31
	 * @version:v1.0
	 * @param filter
	 * @return
	 */
	public static String getFilterSql(Filter filter){
		if (filter == null) {
			return "";
		}
		String sql = getFilterSql(filter.getXml() , null ,null);
		return sql;
	}
	
	/**
	 * @description:get filter query sql from filter xml and other query conditions and query fields
	 * @date:2014-5-6 下午6:23:47
	 * @version:v1.0
	 * @param xml
	 * @param querySpeFieldSet
	 * @param queryConList
	 * @return
	 */
	public static String getFilterSql(String xml, Set<String> querySpeFieldSet , List<QueryCondition> queryConList) {
		boolean querySpecial = false;  //查询特殊字段，忽略xml中是display字段
		if (querySpeFieldSet != null && querySpeFieldSet.size() > 0) {
			querySpecial = true;
		}
		
		String type = null;
		List<String> tablesList = new ArrayList<String>();  //查询表单
		String sqlWhereStr = "";
		Map<String,String> orderFieldMap = new HashMap<String, String>();
		String sql = "";
		boolean isCurrent = true;
		UUID id = null; 
		Set<String> queryFieldSet = new LinkedHashSet<String>(); //查询字段
		queryFieldSet.add("id");
		queryFieldSet.add("title");
		queryFieldSet.add("statusId");
		queryFieldSet.add("createUser");
		queryFieldSet.add("assignUser");
		queryFieldSet.add("createTime");
		queryFieldSet.add("templateId");
		queryFieldSet.add("lastModifyTime");
		
		try {
			Document doc = null;
			try{
				doc = XMLUtil.string2Document(xml, "UTF-8");
			}catch(Exception e){
				e.printStackTrace();
			}
			
			Node queryNode = XMLUtil.getSingleNode(doc, "query");
			
			Node templateTypeNode = XMLUtil.getSingleNode(queryNode, "templateType");
			List<Node> templateNodeList = XMLUtil.getNodes(queryNode, "template");
			
			List<Node> nodeList = new ArrayList<Node>();
			
			if(templateNodeList.size() == 0){ //只指定类型,不指定项目的过滤器
				XMLUtil.setAttribute(templateTypeNode, "id", "");
				XMLUtil.setAttribute(templateTypeNode, "type", "tt");
				nodeList.add(templateTypeNode);
			}
			else{
				for(Node templateNode : templateNodeList){
					XMLUtil.setAttribute(templateNode, "type", "t");
					nodeList.add(templateNode);
				}
			}
			
			Node node = nodeList.get(0);
			//判断是否从日志表查询
			isCurrent = FilterUtil.getIsQueryLog(XMLUtil.getSingleNode(node, "where"));
			
			adjustWhereNode(doc,node );
			
			String idStr = XMLUtil.getAttribute(node, "id");
			id = DataAccessFactory.getInstance().createUUID(idStr);
			type = XMLUtil.getAttribute(node, "type");
			String templateId = null;
			if (type.equals("t")) {
				templateId = idStr;
			}
			
			if (!querySpecial) {
				queryFieldSet.addAll(getDisplayFieldSet(node , templateId)); 
				if (templateNodeList.size() == 0) {  //首页
					queryFieldSet.add("fieldCom_1");  //修改优先级
					queryFieldSet.add("description");   //描述
				}
			}else {
				queryFieldSet.addAll(querySpeFieldSet);
			}
			
			if (isCurrent) {
				queryFieldSet.remove("logcreateUser");
			}
			
			sqlWhereStr = getWhereConditionStr(XMLUtil.getSingleNode(node, "where") , isCurrent , templateId);  //组装where 条件
			
			orderFieldMap = getOrderFieldMap(node , templateId);	   //排序
			
			if (querySpecial) {
				queryFieldSet = querySpeFieldSet;
			}
		} catch (Exception e) {
			
		}
		
		if (type != null && type.equals("t")) {
			tablesList.add(TableRuleManager.getInstance().getDataTableName(id));
			sql = getQuerySql(tablesList, queryFieldSet, sqlWhereStr, orderFieldMap, isCurrent, id , queryConList);
		}else {
			tablesList.addAll(TableRuleManager.getInstance().getAllDataTables());
			sql = getQuerySql(tablesList, queryFieldSet, sqlWhereStr, orderFieldMap, isCurrent, null ,queryConList);
		}

		return sql;
	}
	
	/**
	 * @description:query data by xml 
	 * @date:2014-5-6 下午6:24:17
	 * @version:v1.0
	 * @param xml
	 * @param pageNumber
	 * @param lineAccount
	 * @param sort
	 * @param dir
	 * @param das
	 * @param queryConList
	 * @return
	 */
	public static Data[] filter(String xml, int pageNumber, int lineAccount, String sort,String dir, DataAccessSession das ,List<QueryCondition> queryConList){
		List<Data> 	dataList  = new ArrayList<Data>();
		String sql = getFilterSql(xml , null , queryConList);
		String templateId = null;
		Document doc = null;
		try{
			doc = XMLUtil.string2Document(xml, "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if (doc != null) {
			Node templateNode = XMLUtil.getSingleNode(doc, "query/template");
			if (templateNode != null) {
				templateId = XMLUtil.getAttribute(templateNode, "id");
			}
		}
		
		sql = getQuerySql(sql, pageNumber, lineAccount, sort, dir , templateId);
		dataList = new DataAccessSessionMySQL().queryDatas(sql, false , null);
		
		return dataList.toArray(new Data[dataList.size()]);
	}
	
	/**
	 * @description:get query sql 
	 * @date:2014-5-6 下午6:24:29
	 * @version:v1.0
	 * @param tablesList
	 * @param queryFieldSet
	 * @param sqlWhereStr
	 * @param orderFieldMap
	 * @param isCurrent
	 * @param templateId
	 * @param queryConList
	 * @return
	 */
	public static String getQuerySql(List<String> tablesList,Set<String> queryFieldSet,String sqlWhereStr,Map<String,String> orderFieldMap,boolean isCurrent,UUID templateId , List<QueryCondition> queryConList){
		
		if (tablesList.size() == 0) {
			return "";
		}
		
		StringBuffer sqlBuffer = new StringBuffer();
		
		StringBuffer queryFields = new StringBuffer();   //查询字段拼装
		queryFields = new StringBuffer();   //查询字段拼装
		if (queryFieldSet == null || queryFieldSet.size() == 0) {
			if (isCurrent) {
				queryFields.append(" * ");
			}else {
				throw new RuntimeException("must config this queryFieldSet!");
			}
		}else {
			for (String fieldName : queryFieldSet) {
				if (fieldName != null && fieldName.length() >0) {
					queryFields.append(queryFields.length() > 0 ? " , " + fieldName : fieldName);
				}
			}
		}
		
		StringBuffer orderFields = null;   //order排序条件字段拼装
		if (orderFieldMap != null && orderFieldMap.keySet().size() > 0) {
			orderFields = new StringBuffer();
			
			for (String fieldName : orderFieldMap.keySet()) {
				if (fieldName != null && fieldName.length() >0) {
					if (orderFields.length() == 0) {
						orderFields.append(" order by " + fieldName).append(" ").append(orderFieldMap.get(fieldName) == null ? "asc" : orderFieldMap.get(fieldName) );
					}else {
						orderFields.append(" , ").append(fieldName).append(" ").append(orderFieldMap.get(fieldName) == null ? " asc " : orderFieldMap.get(fieldName));
					}
				}
			}
		} 
		
		StringBuffer queryConBuffer = null;
		//额外查询条件添加
		if (queryConList != null && queryConList.size() > 0) {
			queryConBuffer = new StringBuffer();
			
			for (int i = 0; i < queryConList.size(); i++) {
				if (i > 0) 
					queryConBuffer.append(" and ");
				queryConBuffer.append(" " + queryConList.get(i).getQueryField()).append(" " + queryConList.get(i).getQueryMethod()).append(" " + queryConList.get(i).getQueryValue());
			}
		}
		
		if (isCurrent) {
			
				if (queryConBuffer!=null && queryConBuffer.length() > 0) {
					StringBuffer sqlWhereBuffer = new StringBuffer();
					if (sqlWhereStr.length() > 0 ) {
						sqlWhereBuffer.append("(").append(sqlWhereStr).append(") and (").append(queryConBuffer.toString()).append(")");
					}else {
						sqlWhereBuffer = queryConBuffer;
					}
					sqlWhereStr = sqlWhereBuffer.toString();
				}
				
				
				for (String tableNames : tablesList) {
					if (sqlBuffer.length() > 0) {
						sqlBuffer.append(" union ");
					}
					sqlBuffer.append("select ").append(queryFields.toString()).append(" from ")
							 .append(tableNames);
							 if (templateId != null) {
								sqlBuffer.append(" where templateId=").append(templateId.getValue());
								
							 }
							 if (sqlWhereStr != null && sqlWhereStr.trim().length() > 0) {
								if (templateId == null ) {
									sqlBuffer.append(" where ");
								}else {
									sqlBuffer.append(" and ");
								}
								sqlBuffer.append(sqlWhereStr.toString());
							}
					
				}
		}else {
			Map<String, String> dataLogMap = new HashMap<String, String>();
			
			if (templateId != null ) {
				String dataTable = TableRuleManager.getInstance().getDataTableName(templateId);
				String dataLogTable =  TableRuleManager.getInstance().getDataLogTableName(templateId);
				dataLogMap.put(dataTable, dataLogTable);
			}else {
				dataLogMap.putAll(TableRuleManager.getInstance().getAllDataLogMap());
			}
			
			for (String dataTable : dataLogMap.keySet()) {
				if (sqlBuffer.length() > 0) {
					sqlBuffer.append(" union ");
				}
				String dataLogTable = dataLogMap.get(dataTable);
				
				/**
				 * 拼装形式
				 * select data_3.id from data_3 where exists (select data_log_3.dataid from data_log_3 where data_log_3.dataid = data_3.id and data_log_3.fieldInt_4 = '192045') order by data_3.id limit 50; 
				 * select data_3.id from data_3 where id in (select dataid as id from (select dataid from data_log_3 where fieldInt_4 = '192045') as newtable) order by data_3.id limit 50; 
				 */
				
				
				sqlBuffer.append("select ").append(getQueryFieldByTable(queryFields.toString() , dataTable)).append(" from ")
				 .append(dataTable).append(" join ").append(dataLogTable).append(" on ").append(dataLogTable+".dataid=").append(dataTable+".id")
				 .append(" where 1=1 ");
				if (templateId != null) {
					sqlBuffer.append(" and ").append(dataTable).append(".templateId=").append(templateId.getValue()).append(" ");
					
				}
		
				if (sqlWhereStr != null && sqlWhereStr.length() > 0) {
					sqlBuffer.append(" and ").append(whereConProcess(sqlWhereStr,dataTable, dataLogTable)).append(" ");
				}
				if (queryConBuffer != null && queryConBuffer.length() > 0) {
					sqlBuffer.append(" and ").append(queryConProcess(queryConBuffer.toString(), dataTable)).append(" ");
				}
			}
		}
		
		sqlBuffer.append(" ").append(orderFields == null ? "" : orderFields.toString());
		
		return sqlBuffer.toString();
	}
	
	/**
	 * @description:query condition process
	 * @date:2014-5-6 下午6:24:40
	 * @version:v1.0
	 * @param queryConStr
	 * @param dataTable
	 * @return
	 */
	private static String queryConProcess(String queryConStr , String dataTable){
		if (queryConStr == null || queryConStr.length() == 0)  {
			return "";
		}
		StringBuffer queryProcessBuf = new StringBuffer();
		String[] allCon = queryConStr.split("and");
		
		for (int i = 0; i < allCon.length; i++) {
			if (i > 0) {
				queryProcessBuf.append(" and ");
			}
			
			queryProcessBuf.append(" " + dataTable + "." + allCon[i].trim());
		}
		return queryProcessBuf.toString();
	}
	
	/**
	 * @description:query where condition process
	 * @date:2014-5-6 下午6:24:51
	 * @version:v1.0
	 * @param whereQueryStr
	 * @param dataTable
	 * @param dataLogTable
	 * @return
	 */
	public static String whereConProcess(String whereQueryStr , String dataTable , String dataLogTable){
		return whereQueryStr.replace(DATA_TABLE_REPLATE_STRING, dataTable).replace(DATALOG_TABLE_REPLATE_STRING, dataLogTable);
	}
	
	/**
	 * @description:get query fields by datatable
	 * @date:2014-5-6 下午6:25:04
	 * @version:v1.0
	 * @param queryFieldStr
	 * @param dataTable
	 * @return
	 */
	private static String getQueryFieldByTable(String queryFieldStr, String dataTable){
		
		StringBuffer queryFieldBuffer = new StringBuffer();
		String[] allFieldArray = queryFieldStr.split(",");
		
		for (String field : allFieldArray) {
			if (field != null && field.length() >0) {
				field = field.trim();
				queryFieldBuffer.append(queryFieldBuffer.length() > 0 ? " , " :"");
				if (field.equals("id")) {
					queryFieldBuffer.append(" distinct ");
				}
				queryFieldBuffer.append(dataTable + "." + field).append(" as ").append(field);
			}
		}
		return queryFieldBuffer.toString();
	}
	
	/**
	 * @description:get query sql,add limit and sort info
	 * @date:2014-5-6 下午6:25:23
	 * @version:v1.0
	 * @param sql
	 * @param pageNumber
	 * @param lineAccount
	 * @param sort
	 * @param dir
	 * @param templateId
	 * @return
	 */
	public static String getQuerySql(String sql ,int pageNumber, int lineAccount, String sort,String dir , String templateId){
		
		StringBuffer sqlBuffer = new StringBuffer();
		if (sql == null || sql.length() ==0 ) {
			return "";
		}
		
		if (sort != null && dir != null && sort.length() > 0 && dir.length() >0) {
			if (sql.indexOf("order") != -1) {  //取消本有的排序字段
				sql = sql.substring(0,sql.indexOf("order"));
			}
			sqlBuffer.append(sql);
			String orderField = getDbColName(sort , templateId);
			if (orderField != null && orderField.length() > 0) {
				sqlBuffer.append(" order by ").append(orderField).append(" ").append(dir).append(" ");
			}
			
		}else {
			sqlBuffer.append(sql);
		}
		if (pageNumber!=0 && lineAccount !=0) {
			sqlBuffer.append(" limit ").append((pageNumber-1)*lineAccount).append(",").append(lineAccount);
		}
		
		return sqlBuffer.toString();
	}
	
	/**
	 * @description:get dbcolName
	 * @date:2014-5-6 下午6:25:46
	 * @version:v1.0
	 * @param fieldName
	 * @param templateId
	 * @return
	 */
	public static String getDbColName(String fieldName , String templateId) {
		String fieldColName = null;
		if (fieldName == null || fieldName.equals("")) {
			return "";
		}
		if (fieldName.startsWith("FIEL-")) {
			fieldName = fieldName.split("-")[1];
		}
		if (CommonUtil.isPosNum(fieldName)) {
			fieldColName = FieldNameCache.getInstance().getFieldName(fieldName ,templateId);
		}else{
			fieldColName = baseMap.get(fieldName);
		}
		if (fieldColName == null) {
			if (!ConfigUtil.abandonTemplateIdSet.contains(templateId)) {
				System.out.println("database dbcolname can not find! templateId:" + templateId + " fieldName:" + fieldName);
				return null;
			}
		}
		return fieldColName;
	}
	
	/**
	 * @description:get query where condition str
	 * @date:2014-5-6 下午6:26:10
	 * @version:v1.0
	 * @param whereNode
	 * @param isCurrent
	 * @param templateId
	 * @return
	 */
	protected static String getWhereConditionStr(Node whereNode , boolean isCurrent , String templateId) {
		if (whereNode == null) {
			return "";
		}
		
		StringBuffer sqlWhereBuffer = new StringBuffer();
		
		for(int i = 0; whereNode != null && i < whereNode.getChildNodes().getLength(); i++)
		{
			Node wherePrivateNode = whereNode.getChildNodes().item(i);
			if(wherePrivateNode.getNodeName().equals("condition"))
				sqlWhereBuffer.append(" ").append(wherePrivateNode.getTextContent()).append(" ");
			else if(wherePrivateNode.getNodeName().equals("field"))
			{
				String fieldId = XMLUtil.getAttribute(wherePrivateNode, "id");
				String fieldType = XMLUtil.getAttribute(wherePrivateNode, "type");
				String method = XMLUtil.getAttribute(wherePrivateNode, "method");
				String valueStr = wherePrivateNode.getTextContent();
				
				if(isCurrent && (fieldId.equals("action_user")||fieldId.equals("action_id") || fieldId.equals("action_index") || fieldId.equals("logcreateTime"))) //当前表里查不到action_user 和 action_id
				{
					sqlWhereBuffer.append(" 1=1 ");
					continue;
				}
				
				//时间处理
				if(valueStr != null && (fieldId.equals("create_time") || fieldId.equals("last_modify_time")||fieldId.equals("action_time_range")))
				{
					try {
						if(valueStr.indexOf("年") > 0)
							valueStr = Date.valueOf(valueStr).toTimestamp().toString();
						else
							valueStr = Date.valueOf(valueStr).toTimestamp().toString();
					} catch (Exception e) {
						System.out.println("timestamp parse error , format must be yyyy-mm-dd hh:mm:ss : timestamp=" + valueStr);
					}
					
				}
				
				String dbColName = getDbColName(fieldId, templateId); //数据库中列
				if(dbColName == null){
					sqlWhereBuffer.append(" ( 1=1 ) ");
					continue;
				}
				
				if (isCurrent) {
					sqlWhereBuffer.append(" ( ").append(dbColName).append(" ");
				}else {  //需要联合日志表进行联合查询
					if (dbColName.equals("logActionId") || dbColName.equals("logcreateUser") || dbColName.equals("logActionIndex") || dbColName.equals("logcreateTime")) {
						sqlWhereBuffer.append(" ( ").append(DATALOG_TABLE_REPLATE_STRING).append(".").append(dbColName).append(" ");
					}else {
						sqlWhereBuffer.append(" ( ").append(DATA_TABLE_REPLATE_STRING).append(".").append(dbColName).append(" ");
					}
				}

				if(method.equalsIgnoreCase("is null") || method.equalsIgnoreCase("is not null"))
					sqlWhereBuffer.append(" ").append(method).append(" ");
				else if (method.equalsIgnoreCase("in") || method.equalsIgnoreCase("not in")) {
					if (dbColName.equals("assignUser")) {
						sqlWhereBuffer.append(method.equals("not in") ? " not " : "").append(" regexp '").append(valueStr.replaceAll(",", "|")).append("' ) ");
						continue;
					}
					StringBuffer inContentBuffer = new StringBuffer();
					String[] allInArray = valueStr.split(",");
					if (allInArray != null) {
						for (String content : allInArray) {
							if (content.startsWith("'") && content.endsWith("'")) {
								inContentBuffer.append(content).append(",");
							}else {
								inContentBuffer.append("'").append(content).append("',");
							}
						}
					}
					if (inContentBuffer.length()!=0 ) {
						inContentBuffer.deleteCharAt(inContentBuffer.length() -1 );
					}
					
					sqlWhereBuffer.append(" ").append(method).append(" ( ").append(inContentBuffer.toString()).append(" ) ");
					
					//对于not in 的情况 并且扩展字段查询  field开头  需要额外添加 or fieldValue is null的条件  
					if (method.equalsIgnoreCase("not in") && dbColName.startsWith("field")) {
						sqlWhereBuffer.append(" or ");
						
						if (isCurrent) {
							sqlWhereBuffer.append(" ").append(dbColName).append(" ");
						}else {  //需要联合日志表进行联合查询
							if (dbColName.equals("logActionId") || dbColName.equals("logcreateUser") || dbColName.equals("logActionIndex") || dbColName.equals("logcreateTime")) {
								sqlWhereBuffer.append(" ").append(DATALOG_TABLE_REPLATE_STRING).append(".").append(dbColName).append(" ");
							}else {
								sqlWhereBuffer.append(" ").append(DATA_TABLE_REPLATE_STRING).append(".").append(dbColName).append(" ");
							}
						}
						
						sqlWhereBuffer.append(" is null ");
					}
					
				}else if (method.equalsIgnoreCase("like") || method.equalsIgnoreCase("not like")) {
					sqlWhereBuffer.append(" ").append(method).append(" '%").append(valueStr).append("%' ");
				}else {
					if (valueStr == null) {
						sqlWhereBuffer.append(" ").append(method).append(" ").append(valueStr);
					}else {
						if (dbColName.equals("assignUser")) {
							sqlWhereBuffer.append(method.trim().equals("!=") ? " not " : "").append(" regexp '").append(valueStr).append("' ");
						}else{
							sqlWhereBuffer.append(" ").append(method).append(" '").append(valueStr).append("' ");
						}
					}
				}
				sqlWhereBuffer.append(" ) "); 
			}
		}
		
		if (isCurrent) {
			sqlWhereBuffer.append(sqlWhereBuffer.length() >0 ? " and " : "").append(" is_valid = 1").append(" ");
		}else {  //需要联合日志表进行联合查询
			sqlWhereBuffer.append(sqlWhereBuffer.length() >0 ? " and " : "").append(" ").append(DATA_TABLE_REPLATE_STRING).append(".").append("is_valid = 1").append(" ");
		}
		
		return sqlWhereBuffer.toString();
	}

	/**
	 * @description:return all show fields
	 * @date:2014-5-6 下午6:26:32
	 * @version:v1.0
	 * @param node
	 * @param templateId
	 * @return
	 */
	protected static Set<String> getDisplayFieldSet(Node node , String templateId) {
		//显示字段--即从数据库中查询字段
		Set<String> displayFieldSet = new HashSet<String>();
		List<Node> displayNodeList = XMLUtil.getNodes(node, "display/field");
		for (Node displayNode : displayNodeList) {
			String displayName = XMLUtil.getAttribute(displayNode, "id");
			if(CommonUtil.isPosNum(displayName))
			{
				String fieldColName = FieldNameCache.getInstance().getFieldName(displayName,templateId);
				if (fieldColName != null) {
					displayFieldSet.add(fieldColName);
				}
			}else {
				displayFieldSet.add(getDbColName(displayName,templateId));
			}
		}
		
		List<Node> orderNodeList = XMLUtil.getNodes(node, "order/field");
		for (Node orderNode : orderNodeList) {
			String displayName = XMLUtil.getAttribute(orderNode, "id");
			if(CommonUtil.isPosNum(displayName))
			{
				String fieldColName = FieldNameCache.getInstance().getFieldName(displayName,templateId);
				if (fieldColName != null) {
					displayFieldSet.add(fieldColName);
				}
			}else {
				displayFieldSet.add(getDbColName(displayName,templateId));
			}
		}
		
		
		return displayFieldSet;
	}


	/**
	 * @description:return all order fields
	 * @date:2014-5-6 下午6:26:46
	 * @version:v1.0
	 * @param node
	 * @param templateId
	 * @return
	 */
	protected static Map<String,String> getOrderFieldMap(Node node , String templateId)
	{
		Map<String,String> orderFieldMap = new LinkedHashMap<String,String>();
		
		List<Node> orderFieldNodeList = XMLUtil.getNodes(node, "order/field");
		
		Map<String, String> groupFieldMap = getGroupConditionMap(node, templateId);
		
		orderFieldMap.putAll(groupFieldMap);  //分组字段最新排序
		
		for(Node orderFieldNode : orderFieldNodeList)
		{
			String fieldIdStr = XMLUtil.getAttribute(orderFieldNode, "id");
			
			String fieldDesc = XMLUtil.getAttribute(orderFieldNode, "desc");
			
			String dir = "";
			if (fieldDesc == null) {
				dir = "asc";
			}else {
				dir = fieldDesc.equals("true") ? "desc" : "asc";
			}
			
			String dbColName = getDbColName(fieldIdStr, templateId);
			if (groupFieldMap.keySet().contains(dbColName))  //字段己在分组字段里不用排序
				continue;
			else 
				orderFieldMap.put(getDbColName(fieldIdStr,templateId), dir);
		}
		return orderFieldMap;
	}

	/**
	 * @description:return all group fields map
	 * @date:2014-5-6 下午6:27:05
	 * @version:v1.0
	 * @param node
	 * @param templateId
	 * @return
	 */
	protected static Map<String, String> getGroupConditionMap(Node node , String templateId) {
		
		Map<String, String> groupFieldMap = new HashMap<String, String>();
		
		List<Node> orderFieldNodeList = XMLUtil.getNodes(node, "order/field");
		if (orderFieldNodeList == null || orderFieldNodeList.size() == 0) {
			return groupFieldMap;
		}
		
		int indent = 0;
		Node orderNode = XMLUtil.getSingleNode(node, "order");
		if(orderNode != null){
			try{
				indent = Integer.parseInt(XMLUtil.getAttribute(orderNode, "indent"));
			}
			catch(Exception e){
			}
		}
		
		if (indent == 0 || orderFieldNodeList.size() < indent) {
			return groupFieldMap;
		}else {
			Node groupFieldNode = orderFieldNodeList.get(indent -1 );
			if (groupFieldNode != null) {
				String fieldIdStr = XMLUtil.getAttribute(groupFieldNode, "id");
				String fieldDesc = XMLUtil.getAttribute(groupFieldNode, "desc");
				String dir = "";
				if (fieldDesc == null) {
					dir = "asc";
				}else {
					dir = fieldDesc.equals("true") ? "desc" : "asc";
				}
				groupFieldMap.put(getDbColName(fieldIdStr , templateId), dir);
			}
		}
		return groupFieldMap;
	}
	
	/**
	 * @description:get start time and end time from time value
	 * @date:2014-5-6 下午6:28:06
	 * @version:v1.0
	 * @param timeValue
	 * @return
	 */
	public static Pair<String, String> getTimeSpan(String timeValue)
	{
		long timeInMillis = Calendar.getInstance().getTimeInMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInMillis);
		
		Pair<String, String> ret = new Pair<String, String>();
		
		if(timeValue.equals("今天")){
			ret.setSecond(new Timestamp(calendar.getTimeInMillis()).toString().split("\\ ")[0] + " 23:59:59");
			ret.setFirst(new Timestamp(calendar.getTimeInMillis()).toString().split("\\ ")[0] + " 00:00:00");
		}
		else if(timeValue.equals("本周")){
			ret.setSecond(new Timestamp(calendar.getTimeInMillis() - (calendar.get(Calendar.DAY_OF_WEEK) - 8) * 86400000L).toString().split("\\ ")[0] + " 23:59:59");
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() - (calendar.get(Calendar.DAY_OF_WEEK) - 2) * 86400000L).toString().split("\\ ")[0] + " 00:00:00");
		}
		else if(timeValue.equals("本月")){
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() - (calendar.get(Calendar.DAY_OF_MONTH) - 1) * 86400000L).toString().split("\\ ")[0] + " 00:00:00");
			
			Calendar tempCalendar = Calendar.getInstance();
			int i = 1;
			while(true){
				tempCalendar.setTimeInMillis(Timestamp.valueOf(ret.getFirst()).getTime() + i * 86400000L);
				if(tempCalendar.get(Calendar.DAY_OF_MONTH) == 1){
					break;
				}
				
				i++;
			}
			
			ret.setSecond(new Timestamp(tempCalendar.getTimeInMillis() - 86400000L).toString().split("\\ ")[0] + " 23:59:59");
		}
		else if(timeValue.equals("本季")){
			int monthIndex = calendar.get(Calendar.MONTH);
			for(int i = 0; i <= monthIndex % 3; i++){
				calendar.setTimeInMillis(calendar.getTimeInMillis() - calendar.get(Calendar.DAY_OF_MONTH) * 86400000L);
			}
			
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() + 86400000L).toString().split("\\ ")[0] + " 00:00:00");
			
			Calendar tempCalendar = Calendar.getInstance();
			int i = 1;
			int j = 1;
			while(true){
				tempCalendar.setTimeInMillis(Timestamp.valueOf(ret.getFirst()).getTime() + i * 86400000L);
				if(tempCalendar.get(Calendar.DAY_OF_MONTH) == 1){
					if(j == 3){
						break;
					}
					
					j++;
				}
				
				i++;
			}
			
			ret.setSecond(new Timestamp(tempCalendar.getTimeInMillis() - 86400000L).toString().split("\\ ")[0] + " 23:59:59");
		}
		else if(timeValue.equals("本年")){
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() - (calendar.get(Calendar.DAY_OF_YEAR) - 1) * 86400000L).toString().split("\\ ")[0] + " 00:00:00");
			Calendar tempCalendar = Calendar.getInstance();
			int i = 1;
			while(true){
				tempCalendar.setTimeInMillis(Timestamp.valueOf(ret.getFirst()).getTime() + i * 86400000L);
				if(tempCalendar.get(Calendar.DAY_OF_YEAR) == 1){
					break;
				}
				
				i++;
			}
			
			ret.setSecond(new Timestamp(tempCalendar.getTimeInMillis() - 86400000L).toString().split("\\ ")[0] + " 23:59:59");
		}
		else if(timeValue.equals("昨天")){
			ret.setSecond(new Timestamp(calendar.getTimeInMillis() - 86400000L).toString().split("\\ ")[0] + " 23:59:59");
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() - 86400000L).toString().split("\\ ")[0] + " 00:00:00");
		}
		else if(timeValue.equals("上周")){
			ret.setSecond(new Timestamp(calendar.getTimeInMillis() - (calendar.get(Calendar.DAY_OF_WEEK) - 1) * 86400000L).toString().split("\\ ")[0] + " 23:59:59");
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() - (calendar.get(Calendar.DAY_OF_WEEK) + 5) * 86400000L).toString().split("\\ ")[0] + " 00:00:00");
		}
		else if(timeValue.equals("上月")){
			calendar.setTimeInMillis(calendar.getTimeInMillis() - calendar.get(Calendar.DAY_OF_MONTH) * 86400000L);
			ret.setSecond(new Timestamp(calendar.getTimeInMillis()).toString().split("\\ ")[0] + " 23:59:59");
			
			calendar.setTimeInMillis(calendar.getTimeInMillis() - calendar.get(Calendar.DAY_OF_MONTH) * 86400000L);
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() + 86400000L).toString().split("\\ ")[0] + " 00:00:00");
		}
		else if(timeValue.equals("上季")){
			int monthIndex = calendar.get(Calendar.MONTH);
			for(int i = 0; i <= monthIndex % 3; i++){
				calendar.setTimeInMillis(calendar.getTimeInMillis() - calendar.get(Calendar.DAY_OF_MONTH) * 86400000L);
			}
			
			ret.setSecond(new Timestamp(calendar.getTimeInMillis()).toString().split("\\ ")[0] + " 23:59:59");
			
			monthIndex = calendar.get(Calendar.MONTH);
			for(int i = 0; i <= monthIndex % 3; i++){
				calendar.setTimeInMillis(calendar.getTimeInMillis() - calendar.get(Calendar.DAY_OF_MONTH) * 86400000L);
			}
			
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() + 86400000L).toString().split("\\ ")[0] + " 00:00:00");
		}
		else if(timeValue.equals("去年")){
			calendar.setTimeInMillis(calendar.getTimeInMillis() - calendar.get(Calendar.DAY_OF_YEAR) * 86400000L);
			ret.setSecond(new Timestamp(calendar.getTimeInMillis()).toString().split("\\ ")[0] + " 23:59:59");
			
			calendar.setTimeInMillis(calendar.getTimeInMillis() - calendar.get(Calendar.DAY_OF_YEAR) * 86400000L);
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() + 86400000L).toString().split("\\ ")[0] + " 00:00:00");
		}
		else if(timeValue.matches("过去[1-9][0-9]*天")){
			int day = getTimeValueNumber(timeValue);
			
			ret.setSecond(new Timestamp(calendar.getTimeInMillis()).toString().split("\\.")[0]);
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() - day * 86400000L).toString().split("\\.")[0]);
		}
		else if(timeValue.matches("过去[1-9][0-9]*周")){
			int week = getTimeValueNumber(timeValue);
			
			ret.setSecond(new Timestamp(calendar.getTimeInMillis()).toString().split("\\.")[0]);
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() - week * 604800000L).toString().split("\\.")[0]);
		}
		else if(timeValue.matches("过去[1-9][0-9]*月")){
			int month = getTimeValueNumber(timeValue);
			
			ret.setSecond(new Timestamp(calendar.getTimeInMillis()).toString().split("\\.")[0]);
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() - month * 2629800000L).toString().split("\\.")[0]);
		}
		else if(timeValue.matches("过去[1-9][0-9]*季")){
			int season = getTimeValueNumber(timeValue);
			
			ret.setSecond(new Timestamp(calendar.getTimeInMillis()).toString().split("\\.")[0]);
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() - season * 7889400000L).toString().split("\\.")[0]);
		}
		else if(timeValue.matches("过去[1-9][0-9]*年")){
			int year = getTimeValueNumber(timeValue);
			
			ret.setSecond(new Timestamp(calendar.getTimeInMillis()).toString().split("\\.")[0]);
			ret.setFirst(new Timestamp(calendar.getTimeInMillis() - year * 31557600000L).toString().split("\\.")[0]);
		}
		else if(timeValue.matches("未来[1-9][0-9]*天")){
			int day = getTimeValueNumber(timeValue);
			
			ret.setSecond(new Timestamp(calendar.getTimeInMillis() + day * 86400000L).toString().split("\\.")[0]);
			ret.setFirst(new Timestamp(calendar.getTimeInMillis()).toString().split("\\.")[0]);
		}
		else if(timeValue.matches("未来[1-9][0-9]*周")){
			int week = getTimeValueNumber(timeValue);
			
			ret.setSecond(new Timestamp(calendar.getTimeInMillis() + week * 604800000L).toString().split("\\.")[0]);
			ret.setFirst(new Timestamp(calendar.getTimeInMillis()).toString().split("\\.")[0]);
		}
		else if(timeValue.matches("未来[1-9][0-9]*月")){
			int month = getTimeValueNumber(timeValue);
			
			ret.setSecond(new Timestamp(calendar.getTimeInMillis() + month * 2629800000L).toString().split("\\.")[0]);
			ret.setFirst(new Timestamp(calendar.getTimeInMillis()).toString().split("\\.")[0]);
		}
		else if(timeValue.matches("未来[1-9][0-9]*季")){
			int season = getTimeValueNumber(timeValue);
			
			ret.setSecond(new Timestamp(calendar.getTimeInMillis() + season * 7889400000L).toString().split("\\.")[0]);
			ret.setFirst(new Timestamp(calendar.getTimeInMillis()).toString().split("\\.")[0]);
		}
		else if(timeValue.matches("未来[1-9][0-9]*年")){
			int year = getTimeValueNumber(timeValue);
			
			ret.setSecond(new Timestamp(calendar.getTimeInMillis() + year * 31557600000L).toString().split("\\.")[0]);
			ret.setFirst(new Timestamp(calendar.getTimeInMillis()).toString().split("\\.")[0]);
		}
		else{
			ret.setSecond(timeValue + " 23:59:59");
			ret.setFirst(timeValue + " 00:00:00");
		}
		
		return ret;
	}
	
	/**
	 * @description:get time span int value
	 * @date:2014-5-6 下午6:29:30
	 * @version:v1.0
	 * @param timeValue
	 * @return
	 */
	private static int getTimeValueNumber(String timeValue){
		try{
			int number = Integer.parseInt(timeValue.substring(2, timeValue.length() - 1));
			if(number > 0){
				return number;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return 1;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryDatas</p>
	 * @param xml
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.DataFilter#queryDatas(java.lang.String)
	 */
	@Override
	public Data[] queryDatas(String xml) {
		return queryDatas(xml, null);
	}

}
