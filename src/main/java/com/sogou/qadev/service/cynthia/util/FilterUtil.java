package com.sogou.qadev.service.cynthia.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sogou.qadev.cache.impl.FlowCache;
import com.sogou.qadev.cache.impl.TemplateCache;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Right;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.impl.DataFilterMemory;

public class FilterUtil {

	/**
	 * system filter
	 */
	public static Set<String> systemFilter = new HashSet<String>();
	
	static
	{
		systemFilter.add("119695");
		systemFilter.add("119891");
		systemFilter.add("119892");
		systemFilter.add("119893");
	}
	
	//从日志中查询的字段
	private static Set<String> logFieldSet = new HashSet<String>();
	static
	{
		logFieldSet.add("action_user");
		logFieldSet.add("action_id");
		logFieldSet.add("action_index");
		logFieldSet.add("logcreateTime");
		logFieldSet.add("action_time_range");
		logFieldSet.add("logActionIndex");
		logFieldSet.add("logcreateUser");
		logFieldSet.add("log_create_user");
		logFieldSet.add("logActionId");
		logFieldSet.add("logActionComment");
	}
	
	//时间类型字段
	private static Set<String> timeTypeFieldSet = new HashSet<String>();
	static
	{
		timeTypeFieldSet.add("create_time");
		timeTypeFieldSet.add("last_modify_time");
		timeTypeFieldSet.add("action_time_range");
	}
	
	//人员类型字段
	private static Set<String> userTypeFieldSet = new HashSet<String>();
	static
	{
		userTypeFieldSet.add("create_user");
		userTypeFieldSet.add("assign_user");
		userTypeFieldSet.add("log_create_user");
	}
	
	/**
	 * 根据查询条件返回是否需要从日志中查询
	 * @param whereNode
	 * @return
	 */
	public static boolean getIsQueryLog(Node whereNode){
		boolean isCurrent = true;
		List<Node> whereFieldNodeList = XMLUtil.getNodes(whereNode, "field");
		for (Node whereFieldNode : whereFieldNodeList) {
			String fieldId = XMLUtil.getAttribute(whereFieldNode, "id");
			if(fieldId != null && logFieldSet.contains(fieldId)) 
			{
				isCurrent = false;
				break;
			}
		}
		return isCurrent;
	}
	
	/**
	 * @function：对查询节点进行组装调整
	 * @modifyTime：2013-9-25 上午11:59:37
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param whereNode
	 */
	public static void adjustWhereNode(Document doc , Node whereNode , String userName , UUID templateId){
		
		List<Flow> allFlows = null;
		
		if(whereNode != null){
		
			List<Node> whereFieldNodeList = XMLUtil.getNodes(whereNode, "field");
			for(Node whereFieldNode : whereFieldNodeList){
				String fieldId = XMLUtil.getAttribute(whereFieldNode, "id");
				String fieldMethod = XMLUtil.getAttribute(whereFieldNode, "method");
				String fieldType = XMLUtil.getAttribute(whereFieldNode, "type");
				String fieldDataType = XMLUtil.getAttribute(whereFieldNode, "dataType");
				String fieldValue = whereFieldNode.getTextContent();
				
				if(fieldValue.equals("$current_user$")){
					whereFieldNode.setTextContent(userName);
				}
				
				if(fieldId.equals("status_id") && (fieldValue.equals("[逻辑开始]") || fieldValue.equals("[逻辑关闭]"))){
					StringBuffer statusIdStrb = new StringBuffer();
					List<Flow> flowList = new ArrayList<Flow>();
					
					if(templateId == null){
						if (allFlows == null) {
							allFlows = FlowCache.getInstance().getAll();
						}
						if (allFlows == null) {
							continue;
						}
						
						flowList.addAll(allFlows);
						
					}else{
						Template template = TemplateCache.getInstance().get(templateId);
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
				if(userTypeFieldSet.contains(fieldId) && fieldValue.startsWith("role_")){
					
					String roleIdStr = fieldValue.substring(5);
					
					if (!CommonUtil.isPosNum(roleIdStr)) 
						continue;
					
					StringBuffer roleUsers = new StringBuffer();
					if (templateId != null) {
						Template template = TemplateCache.getInstance().get(templateId);
						if(template == null){
							continue;
						}
						Flow flow = FlowCache.getInstance().get(template.getFlowId());
						if (flow == null) {
							continue;
						}
						UUID roleId = DataAccessFactory.getInstance().createUUID(roleIdStr);
						Set<Right> allRoleRight = flow.queryRightsByRole(roleId);
						
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
						&& (timeTypeFieldSet.contains(fieldId)
									|| CommonUtil.isPosNum(fieldId) && fieldType.equals("input") && fieldDataType.equals("timestamp"))){
					if(fieldValue.matches(DataFilterMemory.timeRegex) || fieldValue.indexOf(" ") < 0){
						long currentTime = Calendar.getInstance().getTimeInMillis();
						
						if(fieldMethod.equals("=")){
							Node leftNode = doc.createElement("condition");
							leftNode.setTextContent("(");
							whereNode.insertBefore(leftNode, whereFieldNode);
									
							Node firstFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(firstFieldNode, "method", ">=");
							firstFieldNode.setTextContent(DataFilterMemory.getTimeSpan(fieldValue).getFirst());
							whereNode.insertBefore(firstFieldNode, whereFieldNode);
									
							Node andNode = doc.createElement("condition");
							andNode.setTextContent("and");
							whereNode.insertBefore(andNode, whereFieldNode);
									
							Node secondFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(secondFieldNode, "method", "<=");
							secondFieldNode.setTextContent(DataFilterMemory.getTimeSpan(fieldValue).getSecond());
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
							firstFieldNode.setTextContent(DataFilterMemory.getTimeSpan(fieldValue).getFirst());
							whereNode.insertBefore(firstFieldNode, whereFieldNode);
									
							Node andNode = doc.createElement("condition");
							andNode.setTextContent("or");
							whereNode.insertBefore(andNode, whereFieldNode);
									
							Node secondFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(secondFieldNode, "method", ">");
							secondFieldNode.setTextContent(DataFilterMemory.getTimeSpan(fieldValue).getSecond());
							whereNode.insertBefore(secondFieldNode, whereFieldNode);
									
							Node rightNode = doc.createElement("condition");
							rightNode.setTextContent(")");
							whereNode.insertBefore(rightNode, whereFieldNode);
									
							whereNode.removeChild(whereFieldNode);
						}
						else if(fieldMethod.equals(">")){
							Node secondFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(secondFieldNode, "method", ">");
							secondFieldNode.setTextContent(DataFilterMemory.getTimeSpan(fieldValue).getSecond());
							whereNode.insertBefore(secondFieldNode, whereFieldNode);
									
							whereNode.removeChild(whereFieldNode);	
						}
						else if(fieldMethod.equals(">=")){
							Node secondFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(secondFieldNode, "method", ">=");
							secondFieldNode.setTextContent(DataFilterMemory.getTimeSpan(fieldValue).getFirst());
							whereNode.insertBefore(secondFieldNode, whereFieldNode);
									
							whereNode.removeChild(whereFieldNode);	
						}
						else if(fieldMethod.equals("<")){
							Node secondFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(secondFieldNode, "method", "<");
							secondFieldNode.setTextContent(DataFilterMemory.getTimeSpan(fieldValue).getFirst());
							whereNode.insertBefore(secondFieldNode, whereFieldNode);
									
							whereNode.removeChild(whereFieldNode);	
						}
						else if(fieldMethod.equals("<=")){
							Node secondFieldNode = whereFieldNode.cloneNode(true);
							XMLUtil.setAttribute(secondFieldNode, "method", "<=");
							secondFieldNode.setTextContent(DataFilterMemory.getTimeSpan(fieldValue).getSecond());
							whereNode.insertBefore(secondFieldNode, whereFieldNode);
									
							whereNode.removeChild(whereFieldNode);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 拼接查询条件字段
	 */
	public static String getWhereConditionStr(Document doc, Node whereNode , boolean isCurrent , String templateIdStr , String userName) {
		UUID templateId = null;
		if (templateIdStr != null && !templateIdStr.equals("")) {
			templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
		}
		
		//调整节点，填充具体查询条件信息
		adjustWhereNode(doc, whereNode, userName,templateId);
		
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
				
				if(isCurrent && (logFieldSet.contains(fieldId))) //当前表里查不到action_user 和 action_id
				{
					sqlWhereBuffer.append(" 1=1 ");
					continue;
				}
				
				//时间处理
				if(valueStr != null && (timeTypeFieldSet.contains(fieldId)))
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
				
				String dbColName = DataFilterMemory.getDbColName(fieldId, templateIdStr); //数据库中列
				
				if (isCurrent) {
					sqlWhereBuffer.append(" ( ").append(dbColName).append(" ");
				}else {  //需要联合日志表进行联合查询
					if (logFieldSet.contains(dbColName)) {
						sqlWhereBuffer.append(" ( ").append(DataFilterMemory.DATALOG_TABLE_REPLATE_STRING).append(".").append(dbColName).append(" ");
					}else {
						sqlWhereBuffer.append(" ( ").append(DataFilterMemory.DATA_TABLE_REPLATE_STRING).append(".").append(dbColName).append(" ");
					}
				}
				

				if(method.equalsIgnoreCase("is null") || method.equalsIgnoreCase("is not null"))
					sqlWhereBuffer.append(" ").append(method).append(" ");
				else if (method.equalsIgnoreCase("in") || method.equalsIgnoreCase("not in")) {
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
							if (logFieldSet.contains(dbColName)) {
								sqlWhereBuffer.append(" ").append(DataFilterMemory.DATALOG_TABLE_REPLATE_STRING).append(".").append(dbColName).append(" ");
							}else {
								sqlWhereBuffer.append(" ").append(DataFilterMemory.DATA_TABLE_REPLATE_STRING).append(".").append(dbColName).append(" ");
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
						sqlWhereBuffer.append(" ").append(method).append(" '").append(valueStr).append("' ");
					}
				}
				sqlWhereBuffer.append(" ) "); 
			}
		}
		
		if (isCurrent) {
			sqlWhereBuffer.append(sqlWhereBuffer.length() >0 ? " and " : "").append(" is_valid = 1").append(" ");
		}else {  //需要联合日志表进行联合查询
			sqlWhereBuffer.append(sqlWhereBuffer.length() >0 ? " and " : "").append(" ").append(DataFilterMemory.DATA_TABLE_REPLATE_STRING).append(".").append("is_valid = 1").append(" ");
		}
		
		return sqlWhereBuffer.toString();
	}
	
}
