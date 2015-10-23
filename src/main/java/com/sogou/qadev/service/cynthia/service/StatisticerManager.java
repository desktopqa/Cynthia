package com.sogou.qadev.service.cynthia.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sogou.qadev.cache.impl.FieldNameCache;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Pair;
import com.sogou.qadev.service.cynthia.bean.QueryCondition;
import com.sogou.qadev.service.cynthia.bean.Right;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.TimerAction;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.dao.UserInfoAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.impl.DataFilterMemory;
import com.sogou.qadev.service.cynthia.util.FilterUtil;
import com.sogou.qadev.service.cynthia.util.QueryUtil;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

public class StatisticerManager
{
	private static StatisticerManager instance = null;
	
	private static DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
	
	public static StatisticerManager getInstance()
	{
		if (instance == null)
		{
			instance = new StatisticerManager();
		}
		return instance;
	}
	
	public boolean execute(String id, String content, String username, String xml)
	{
		return executeInternal(id, username, xml);
	}
	
	public boolean execute(String id, Data[] dataArray, String username, String xml)
	{
		return executeInternal(id,username, xml);
	}
	
	/**
	 * @description:execute script
	 * @date:2014-5-6 下午6:30:56
	 * @version:v1.0
	 * @param id
	 * @param username
	 * @param xml
	 * @return
	 */
	protected boolean executeInternal(String id,String username, String xml)
	{
		try
		{
			Document doc = XMLUtil.string2Document(xml, "UTF-8");
			Node rootNode = XMLUtil.getSingleNode(doc, "root");
			Node receiverNode = XMLUtil.getSingleNode(rootNode, "reciever");
			
			StringBuffer content = new StringBuffer();
			StringBuffer subject = new StringBuffer();
			
			Map<String, Object> dataMap = getStatisticResultByXml(xml, username);
			Map<String, Map<String, Integer>> resultMap = (Map<String, Map<String, Integer>>)dataMap.get("data");
			subject.append("[Cynthia][数据统计]").append(dataMap.get("name").toString());
			content.append(BugTrendManager.drawImage(resultMap, dataMap.get("chartType").toString(), dataMap.get("name").toString(),id));
			
			Set<String> receiverSet = new LinkedHashSet<String>();
			if(receiverNode == null)
				receiverSet.add(username);
			else
			{
				String[] receiverArray = receiverNode.getTextContent().split(";");
				for(String receiver : receiverArray){
					receiverSet.add(receiver);
				}
			}
			
			MailSender sender = new MailSender();
			sender.setSmtp("transport.mail.sogou-inc.com" );
			sender.setFromUser(username);
			sender.setSubject(subject.toString());
			sender.setContent(content.toString());
			sender.setHtml( true );
			sender.setEncode( "GBK" );
			sender.setToUsers(receiverSet.toArray(new String[receiverSet.size()]));
			sender.sendHtmlEx("GBK");
			return true;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * @description:get all stat map from stats node
	 * @date:2014-5-6 下午6:31:13
	 * @version:v1.0
	 * @param statsNode
	 * @return
	 */
	private static Map<String, String> getStatOptionMap(Node statsNode){
		List<Node> statNodeList = XMLUtil.getNodes(statsNode,"stat");
		
		Map<String, String> statMap = new LinkedHashMap<String, String>();
		for(Node statNode : statNodeList){
			String[] statElemArray = statNode.getTextContent().split("\\|");
			for(int i = 1; i < statElemArray.length; i++){
				statMap.put(statElemArray[i], statElemArray[0]);
			}
		}
		return statMap;
	}
	
	/**
	 * @description:get stat id and option name from statsnode
	 * @date:2014-5-6 下午6:31:35
	 * @version:v1.0
	 * @param statsNode
	 * @return
	 */
	private static Map<String, String> getStatIdOptionmap(Node statsNode){
		List<Node> statNodeList = XMLUtil.getNodes(statsNode,"stat");
		
		Map<String, String> statIdMap = new LinkedHashMap<String, String>();
		for(Node statNode : statNodeList){
			String[] statElemArray = statNode.getTextContent().split("\\|");
			String idValue = XMLUtil.getAttribute(statNode, "statId");
			if (statElemArray.length > 0 && idValue != null && idValue.length() >0) {
				StringBuffer allIdBuffer = new StringBuffer();
				String[] tmpArr = idValue.split(",");
				for (int i = 0; i < tmpArr.length; i++) {
					allIdBuffer.append(allIdBuffer.length() > 0 ? ",":"");
					String[] idValArr = tmpArr[i].split("\\|");
					allIdBuffer.append(idValArr[0]);
				}
				statIdMap.put(statElemArray[0], allIdBuffer.toString());
			}
		}
		return statIdMap;
	}
	
	
	/**
	 * @description:get statistic map data from statistic id
	 * @date:2014-5-6 下午6:31:53
	 * @version:v1.0
	 * @param statisticId
	 * @param userName
	 * @return
	 */
	public static Map<String, Object> getStatisticResultById(UUID statisticId,String userName){
		if (statisticId == null) 
			return new HashMap<String, Object>();
		
		TimerAction timerAction = das.queryTimerAction(statisticId);
		if (timerAction == null) {
			return new HashMap<String, Object>();
		}else {
			Map<String, Object> result = getStatisticResultByXml(timerAction.getParam(), userName);
			result.put("statisticId", statisticId.getValue());
			return result;
		}
	} 
	
	/**
	 * @description:get statistic map data from statistic xml
	 * @date:2014-5-6 下午6:32:11
	 * @version:v1.0
	 * @param xml
	 * @param username
	 * @return
	 */
	public static Map<String, Object> getStatisticResultByXml(String xml,String username){
		Map<String,Object> result = new HashMap<String, Object>();
		
		Document doc = null;
		try {
			doc = XMLUtil.string2Document(xml, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (doc == null) {
			return result;
		}
		
		Node rootNode = XMLUtil.getSingleNode(doc, "root");
		
		String type = XMLUtil.getSingleNode(rootNode, "type").getTextContent();
		String chartType = XMLUtil.getSingleNode(rootNode, "graph").getTextContent();
		String templateIdStr = XMLUtil.getSingleNode(rootNode, "templateId").getTextContent();
		UUID templateId = null;
		templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
		
		String statName = XMLUtil.getSingleNode(rootNode, "name").getTextContent();
		String startTimeStr = XMLUtil.getSingleNode(rootNode, "timeRange/startTime").getTextContent();
		String endTimeStr = XMLUtil.getSingleNode(rootNode, "timeRange/endTime").getTextContent();
		String timeType = XMLUtil.getSingleNode(rootNode, "timeRange/timeType").getTextContent();
		
		if(timeType != null && !timeType.equals("")){
			Pair<String, String> timePair = DataFilterMemory.getTimeSpan(timeType);
			if (timePair != null) {
				startTimeStr = timePair.getFirst();
				endTimeStr = timePair.getSecond();
			}
		}
		
		Timestamp startTimestamp = null;
		Timestamp endTimestamp = null;
		try {
			if (startTimeStr != null && !startTimeStr.equals("")) {
				startTimestamp = Timestamp.valueOf(startTimeStr);
			}
			
			if (endTimeStr != null && !endTimeStr.equals("")) {
				endTimestamp = Timestamp.valueOf(endTimeStr);
			}else {
				endTimestamp = new Timestamp(System.currentTimeMillis());
			}
		} catch (Exception e) {
		}
		
		//获取过滤器查询where条件、
		Node whereNode = XMLUtil.getSingleNode(rootNode, "queryCondition/where");
		//判断是否需要从日志中查询
		boolean isCurrent = FilterUtil.getIsQueryLog(whereNode);
		if (type.equals("task")) {
			isCurrent = false;
		}
		String whereStr = FilterUtil.getWhereConditionStr(doc,whereNode,isCurrent,templateIdStr,username);
		
		//获取统计选项Map
		Map<String, String> statMap = getStatOptionMap(XMLUtil.getSingleNode(rootNode, "stats"));
		//用于链接数据
		Map<String, String> statIdMap = getStatIdOptionmap(XMLUtil.getSingleNode(rootNode, "stats"));
		
		Map<String, Map<String, Integer>> dataMap = null;
		
		if (type.equals("task")) {
			//按任务统计
			String taskIdStr = XMLUtil.getSingleNode(rootNode, "task/taskId").getTextContent();
			String taskFieldIdStr = XMLUtil.getSingleNode(rootNode, "task/taskFieldId").getTextContent();
			dataMap = getBugMapByTask(taskIdStr, taskFieldIdStr, templateId, startTimestamp, endTimestamp, whereStr, statMap,isCurrent);
		}else if (type.equals("person")) {
			//按角色解决问题统计
			String roleIdStr = XMLUtil.getSingleNode(rootNode, "person/roleId").getTextContent();
			String roleActionIds = XMLUtil.getSingleNode(rootNode, "person/roleActionIds").getTextContent();
			String containCurAssignStr = XMLUtil.getSingleNode(rootNode, "person/containCurAssign").getTextContent();
			boolean containCurAssign = (containCurAssignStr != null && containCurAssignStr.equals("yes")) ? true:false;
			dataMap = getBugMapByPersonSolve(roleIdStr, roleActionIds, templateId, startTimestamp, endTimestamp, whereStr,containCurAssign);
		}else if (type.equals("model")) {
			//公共统计器默认按模块统计
			String modelfieldIdStr = XMLUtil.getSingleNode(rootNode, "model/modelfieldId").getTextContent();
			dataMap = getBugMapByModel(modelfieldIdStr, templateId, startTimestamp, endTimestamp, whereStr,statMap,statIdMap,isCurrent);
		}else {
			//公共统计器
			String modelfieldIdStr = "templateId";
			Template[] allTemplates = das.queryAllTemplates();
			if (allTemplates != null && allTemplates.length > 0) {
				for (Template template : allTemplates) {
					statMap.put(template.getId().getValue(), template.getName());
					statIdMap.put(template.getName(), template.getId().getValue());
				}
			}
			dataMap = getBugMapByModel(modelfieldIdStr, templateId, startTimestamp, endTimestamp, whereStr,statMap,statIdMap,isCurrent);
		}
		
		//dataMap 排序 
		result.put("chartType", chartType);
		result.put("name", statName);
		result.put("data", dataMap);
		return result;
	} 
	
	/**
	 * @description:statistic by model
	 * @date:2014-5-6 下午6:32:28
	 * @version:v1.0
	 * @param modelfieldIdStr
	 * @param templateId
	 * @param startTimestamp
	 * @param endTimestamp
	 * @param whereStr
	 * @param statMap
	 * @param statIdMap
	 * @param isCurrent
	 * @return
	 */
	private static Map<String, Map<String, Integer>> getBugMapByModel(String modelfieldIdStr, UUID templateId, Timestamp startTimestamp, Timestamp endTimestamp, String whereStr,Map<String, String> statMap, Map<String, String> statIdMap, boolean isCurrent) {
		Map<String, Map<String, Integer>> resultMap = new LinkedHashMap<String, Map<String, Integer>>();
		if (statMap == null || statMap.keySet().size() == 0 || modelfieldIdStr == null || modelfieldIdStr.equals("")) {
			return resultMap;
		}
		
		String fieldColName = DataFilterMemory.getDbColName(modelfieldIdStr, templateId == null ? null : templateId.getValue());
		fieldColName = fieldColName == null ? modelfieldIdStr : fieldColName;
		
		StringBuffer sqlBuffer = new StringBuffer();
		List<String> tablesList = new ArrayList<String>();
		
		if (templateId != null) {
			tablesList.add(TableRuleManager.getInstance().getDataTableName(templateId));
		}else {
			tablesList.addAll(TableRuleManager.getInstance().getAllDataTables());
		}
		
		Set<String> queryFieldSet = new HashSet<String>();
		//查询所有id;
		queryFieldSet.add("id");
		List<QueryCondition> queryConList = new ArrayList<QueryCondition>();
		if (startTimestamp != null) {
			queryConList.add(new QueryCondition("createTime",">=","'"+startTimestamp.toString() + "'"));
		}
		if (endTimestamp != null) {
			queryConList.add(new QueryCondition("createTime","<=","'"+endTimestamp.toString() + "'"));
		}
		if (templateId != null) {
			queryConList.add(new QueryCondition("templateId","=","'"+templateId.getValue() + "'"));
		}
		queryConList.add(new QueryCondition("is_valid","=","1"));
		
		String idSql = DataFilterMemory.getQuerySql(tablesList, queryFieldSet, whereStr, null, isCurrent, templateId, queryConList);
		StringBuffer idBuffer = new StringBuffer();
		List<Map<String, String>> allIdList = DbPoolConnection.getInstance().getResultSetListBySql(idSql);
		
		if (allIdList.size() == 0) {
			return resultMap;
		}
		for (Map<String, String> map : allIdList) {
			idBuffer.append("'" + map.get("id") + "',");
		}
		if (idBuffer.length() > 0) {
			idBuffer = idBuffer.deleteCharAt(idBuffer.length() -1 );
		}
		
		//根据id对字段进行分组查询总数
		for (String dataTable : tablesList) {
			sqlBuffer.append(sqlBuffer.length() > 0  ? " union " : " ");
			sqlBuffer.append("select count(*) as count,").append(fieldColName).append(" from ").append(dataTable).append(" where id in (").append(idBuffer.toString()).append(") ").append(" group by ").append(fieldColName);;
		}
		
		System.out.println("query all statistic sql :" + sqlBuffer.toString());
		
		List<Map<String , String>> allCountMap = DbPoolConnection.getInstance().getResultSetListBySql(sqlBuffer.toString());
		
		for (Map<String, String> map : allCountMap) {
			if (statMap.get(map.get(fieldColName)) != null) {
				int count = map.get("count") == null ? 0 : Integer.parseInt(map.get("count"));
				String mapKey = statMap.get(map.get(fieldColName));
				String mapValue = statIdMap.get(mapKey);
				if (resultMap.get(mapKey) == null) {
					resultMap.put(mapKey, new HashMap<String, Integer>());
					resultMap.get(mapKey).put(mapValue, 0);
				}
				resultMap.get(mapKey).put(mapValue, count + resultMap.get(mapKey).get(mapValue));
			}
		}
		return resultMap;
	}

	/**
	 * @description:statistic by person solve
	 * @date:2014-5-6 下午6:32:44
	 * @version:v1.0
	 * @param roleIdStr
	 * @param roleActionIds
	 * @param templateId
	 * @param startTimestamp
	 * @param endTimestamp
	 * @param whereStr
	 * @param containCurAssign
	 * @return
	 */
	private static Map<String, Map<String, Integer>> getBugMapByPersonSolve(String roleIdStr, String roleActionIds, UUID templateId, Timestamp startTimestamp, Timestamp endTimestamp, String whereStr,boolean containCurAssign) {
		Map<String, Map<String, Integer>> resultMap = new LinkedHashMap<String, Map<String, Integer>>();
		if (templateId == null || roleIdStr == null || roleActionIds.equals("")) {
			return resultMap;
		}
		
		Template template = das.queryTemplate(templateId);
		if (template == null) {
			return resultMap;
		}
		
		Flow flow = das.queryFlow(template.getFlowId());
		if (flow == null) {
			return resultMap;
		}
		
		Map<String, String> userMap = new LinkedHashMap<String, String>();
		
		//用于链接数据
		Map<String, String> userIdMap = new HashMap<String, String>();
		
		UUID roleId = DataAccessFactory.getInstance().createUUID(roleIdStr);
		
		Set<Right> allRights = flow.queryRightsByRole(roleId);
		Set<String> userSet = new HashSet<String>();
		for (Right right : allRights) {
			if (right != null) {
				userSet.add(right.getUsername());
			}
		}
		
		if (userSet.size() == 0) {
			return resultMap;
		}
		
		List<UserInfo> allUserList = new UserInfoAccessSessionMySQL().queryAllUserInfo(userSet.toArray(new String[0]));
		
		for (UserInfo userInfo : allUserList) {
			userMap.put(userInfo.getUserName(), userInfo.getNickName());
			userIdMap.put(userInfo.getNickName(), userInfo.getUserName());
		}
		
		//人员解决bug集合
		Map<String, Set<String>> roleSolveIdSet = new HashMap<String, Set<String>>();

		String dataLogTable = TableRuleManager.getInstance().getDataLogTableName(templateId);
		
		whereStr = whereConProcess(whereStr);
		
		StringBuffer logActionIdBuffer = new StringBuffer();
		String[] actionArr = roleActionIds.split(",");
		if (actionArr == null || actionArr.length == 0) {
			return resultMap;
		}
		for (String actionId : actionArr) {
			logActionIdBuffer.append(logActionIdBuffer.length() > 0 ? "," : "").append("'" + actionId + "'");
		}
		
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select dataid,logcreateUser from ").append(dataLogTable).append(" where is_valid=1 and templateId =").append(templateId.getValue()).append(" and ").append(whereStr).append(" and logActionId in (")
				.append(logActionIdBuffer.toString()).append(")");
		
		if (startTimestamp != null) {
			sqlBuffer.append(" and logcreateTime >= '"+startTimestamp.toString() + "'");
		}
		if (endTimestamp != null) {
			sqlBuffer.append(" and logcreateTime <= '"+endTimestamp.toString() + "'");
		}
		
		List<Map<String , String>> allIdMap = DbPoolConnection.getInstance().getResultSetListBySql(sqlBuffer.toString());
		
		System.out.println("getBugMapByPersonSolve sql:" + sqlBuffer.toString());
		
		for (Map<String, String> map : allIdMap) {
			if (roleSolveIdSet.get(map.get("logcreateUser")) == null) {
				roleSolveIdSet.put(map.get("logcreateUser"), new HashSet<String>());
			}
			roleSolveIdSet.get(map.get("logcreateUser")).add(map.get("dataId"));
		}
		
		if (containCurAssign) {
			try {
				String dataTable = TableRuleManager.getInstance().getDataTableName(templateId);
				//查询当前指派的数据
				sqlBuffer = new StringBuffer();
				sqlBuffer.append("select id,assignUser from ").append(dataTable).append(" where is_valid=1 and templateId=").append(templateId.getValue()).append(" and ").append(whereStr);
		
				if (startTimestamp != null) {
					sqlBuffer.append(" and createTime >= '"+startTimestamp.toString() + "'");
				}
				if (endTimestamp != null) {
					sqlBuffer.append(" and createTime <= '"+endTimestamp.toString() + "'");
				}
				
				allIdMap = DbPoolConnection.getInstance().getResultSetListBySql(sqlBuffer.toString());
				
				for (Map<String, String> map : allIdMap) {
					if (roleSolveIdSet.get(map.get("assignUser")) == null) {
						roleSolveIdSet.put(map.get("assignUser"), new HashSet<String>());
					}
					roleSolveIdSet.get(map.get("assignUser")).add(map.get("id"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for (String userMail : roleSolveIdSet.keySet()) {
			if (userMap.get(userMail) == null) {
				continue;
			}
			resultMap.put(userMap.get(userMail), new HashMap<String, Integer>());
			resultMap.get(userMap.get(userMail)).put(userMail, roleSolveIdSet.get(userMail) == null ? 0 : roleSolveIdSet.get(userMail).size());
		}
		return resultMap;
	}


	private static String whereConProcess(String whereStr)
	{
		return whereStr.replace(DataFilterMemory.DATA_TABLE_REPLATE_STRING+".", "").replace(DataFilterMemory.DATALOG_TABLE_REPLATE_STRING+".", "");
	}


	/**
	 * @description:statistic by task 
	 * @date:2014-5-6 下午6:32:59
	 * @version:v1.0
	 * @param taskIdStr
	 * @param taskFieldIdStr
	 * @param templateId
	 * @param startTimestamp
	 * @param endTimestamp
	 * @param whereStr
	 * @param statMap
	 * @param isCurrent
	 * @return
	 */
	public static Map<String, Map<String, Integer>> getBugMapByTask(String taskIdStr , String taskFieldIdStr, UUID templateId , Timestamp startTimestamp, Timestamp endTimestamp, String whereStr, Map<String, String> statMap,boolean isCurrent){
		
		Map<String, Map<String, Integer>> resultMap = new LinkedHashMap<String, Map<String, Integer>>();
		if (templateId == null || taskFieldIdStr == null || taskFieldIdStr.equals("") || statMap == null) {
			return resultMap;
		}
		
		/*画图结果Map
		  -- String 日期
		  	-- Map String 统计字段名
		  	-- Map Integer 统计数据
		 */
		for(long i = startTimestamp.getTime(); i <= endTimestamp.getTime(); i += 86400000l){
			String date = new Timestamp(i).toString().split(" ")[0];
			resultMap.put(date, new LinkedHashMap<String, Integer>());
		}
		//初始化
		for(String keyDate : resultMap.keySet()){
			resultMap.get(keyDate).put("总数", 0); 
			for(String statName : statMap.values()){
				resultMap.get(keyDate).put(statName, 0);
			}
		}
				
		//数据库中统计字段列名
		String fieldStaticColName = "statusId";
		
		Map<String, String> orderFieldMap = new HashMap<String, String>();
		
		//查询任务下的缺陷bugId
		List<String> tablesList = new ArrayList<String>();
		tablesList.add(templateId.getValue());
		Set<String> queryFieldSet = new HashSet<String>();
		queryFieldSet.add(FieldNameCache.getInstance().getFieldName(taskFieldIdStr, templateId.getValue()));
		List<QueryCondition> queryConList = new ArrayList<QueryCondition>();
		//指定单条任务
		if (taskIdStr != null && !taskIdStr.equals("")) {
			queryConList.add(new QueryCondition("id","=","'" + taskIdStr + "'"));
		}
		
		queryConList.add(new QueryCondition("templateId","=","'" + templateId.getValue() + "'"));
		queryConList.add(new QueryCondition("is_valid","=","1"));
		
		String idSql = DataFilterMemory.getQuerySql(tablesList, queryFieldSet, whereStr, orderFieldMap, isCurrent,templateId, queryConList);
		List<Map<String , String>> allIdMap = DbPoolConnection.getInstance().getResultSetListBySql(idSql);
		StringBuffer idBuffer = new StringBuffer();
		
		Template bugTemplate = null;  //任务对应缺陷bugId,目前默认任务与bug一对一关系 
		for (Map<String, String> map : allIdMap) {
			for (String value : map.values()) {
				if (value == null) 
					continue;
				if (bugTemplate == null) {
					String[] valueArr = value.split(",");
					if (valueArr != null && valueArr.length > 0) {
						try {
							Data data  = das.queryData(DataAccessFactory.getInstance().createUUID(valueArr[0]));
							if (data != null) {
								bugTemplate = das.queryTemplate(data.getTemplateId());
							}
						} catch (Exception e) {
						}
					}
				}
				idBuffer.append( idBuffer.length() > 0 ? ",":"").append(value);
			}
		}
		
		if (bugTemplate == null || idBuffer.equals("")) {
			return resultMap;
		}
		
		//数据库名
		String dataLogTable = TableRuleManager.getInstance().getDataLogTableName(bugTemplate.getId());
				
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("SELECT dataId, ").append(fieldStaticColName).append(", date_format(logcreateTime,'%Y-%m-%d') as logcreateTime from ")
		.append(dataLogTable).append(" where is_valid = 1 and templateId=").append(bugTemplate.getId().getValue()).append(" and dataId in (").append(idBuffer.toString()).append(")").append(" and logcreateTime >= '").append(startTimestamp.toString().split(" ")[0])
		.append("' and logcreateTime <= '").append(endTimestamp.toString().split(" ")[0]).append("' order by dataid,logActionIndex");
		
		resultMap = BugTrendManager.getStatStaticMap(sqlBuffer.toString(), statMap , resultMap);
		
		String whereString = " id in (" + idBuffer.toString() + ")" ;
		BugTrendManager.getTotalOfStat(whereString , startTimestamp, endTimestamp, bugTemplate,resultMap);
		
		Iterator<Map.Entry<String, Map<String, Integer>>> iterator = resultMap.entrySet().iterator();
		while(iterator.hasNext()){  
			Map.Entry<String, Map<String, Integer>> entry=iterator.next();  
            String key=entry.getKey();  
            if (entry.getValue().get("总数") == null || entry.getValue().get("总数") == 0) {
				iterator.remove();
			}
        }  
		return resultMap;
	}
	
	/**
	 * @description:get query id sql from statistic
	 * @date:2014-5-6 下午6:33:25
	 * @version:v1.0
	 * @param statisticId
	 * @param statisticVal
	 * @param username
	 * @return
	 */
	public static String getSqlOfStat(String statisticId, String statisticVal,String username){
		if (statisticId == null || statisticId.length() == 0 || statisticVal == null || statisticVal.length() == 0)  {
			return null;
		}
		
		TimerAction timerAction = das.queryTimerAction(DataAccessFactory.getInstance().createUUID(statisticId));
		if (timerAction == null) {
			return null;
		}
		
		Document doc = null;
		try {
			doc = XMLUtil.string2Document(timerAction.getParam(),"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (doc == null) {
			return null;
		}
		
		
		Node rootNode = XMLUtil.getSingleNode(doc, "root");
		String type = XMLUtil.getSingleNode(rootNode, "type").getTextContent();
		if (type.equals("task")) {
			return null;
		}
		String templateIdStr = XMLUtil.getSingleNode(rootNode, "templateId").getTextContent();
		UUID templateId = null;
		templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
		
		String startTimeStr = XMLUtil.getSingleNode(rootNode, "timeRange/startTime").getTextContent();
		String endTimeStr = XMLUtil.getSingleNode(rootNode, "timeRange/endTime").getTextContent();
		String timeType = XMLUtil.getSingleNode(rootNode, "timeRange/timeType").getTextContent();
		
		if(timeType != null && !timeType.equals("")){
			Pair<String, String> timePair = DataFilterMemory.getTimeSpan(timeType);
			if (timePair != null) {
				startTimeStr = timePair.getFirst();
				endTimeStr = timePair.getSecond();
			}
		}
		
		Timestamp startTimestamp = null;
		Timestamp endTimestamp = null;
		try {
			if (startTimeStr != null && !startTimeStr.equals("")) {
				startTimestamp = Timestamp.valueOf(startTimeStr);
			}
			
			if (endTimeStr != null && !endTimeStr.equals("")) {
				endTimestamp = Timestamp.valueOf(endTimeStr);
			}else {
				endTimestamp = new Timestamp(System.currentTimeMillis());
			}
		} catch (Exception e) {
		}
		
		//获取过滤器查询where条件、
		Node whereNode = XMLUtil.getSingleNode(rootNode, "queryCondition/where");
		//判断是否需要从日志中查询
		boolean isCurrent = FilterUtil.getIsQueryLog(whereNode);
		
		String whereStr = FilterUtil.getWhereConditionStr(doc,whereNode,isCurrent,templateIdStr,username);
		
		Set<String> allIdSet = new HashSet<String>();  //所有id集合
		
		if (type.equals("person")) {
			String roleActionIds = XMLUtil.getSingleNode(rootNode, "person/roleActionIds").getTextContent();
			String containCurAssignStr = XMLUtil.getSingleNode(rootNode, "person/containCurAssign").getTextContent();
			boolean containCurAssign = (containCurAssignStr != null && containCurAssignStr.equals("yes")) ? true:false;
			
			String dataLogTable = TableRuleManager.getInstance().getDataLogTableName(templateId);
			
			whereStr = whereConProcess(whereStr);
			
			StringBuffer logActionIdBuffer = new StringBuffer();
			String[] actionArr = roleActionIds.split(",");
			if (actionArr == null || actionArr.length == 0) {
				return null;
			}
			
			for (String actionId : actionArr) {
				logActionIdBuffer.append(logActionIdBuffer.length() > 0 ? "," : "").append("'" + actionId + "'");
			}
			
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select dataId from ").append(dataLogTable).append(" where is_valid=1 and logcreateUser='").append(statisticVal).append("' and templateId =").append(templateId.getValue()).append(" and ").append(whereStr).append(" and logActionId in (")
					.append(logActionIdBuffer.toString()).append(")");
			
			if (startTimestamp != null) {
				sqlBuffer.append(" and logcreateTime >= '"+startTimestamp.toString() + "'");
			}
			if (endTimestamp != null) {
				sqlBuffer.append(" and logcreateTime <= '"+endTimestamp.toString() + "'");
			}
			
			List<Map<String, String>> allIdMap = DbPoolConnection.getInstance().getResultSetListBySql(sqlBuffer.toString());
			
			System.out.println("log role action id query sql:" + sqlBuffer.toString());
			
			for (Map<String, String> map : allIdMap) {
				allIdSet.add(map.get("dataId"));
			}
			
			if (containCurAssign) {
				//包含当前指派数据
				try {
					String dataTable = TableRuleManager.getInstance().getDataTableName(templateId);
					//查询当前指派的数据
					sqlBuffer = new StringBuffer();
					sqlBuffer.append("select id from ").append(dataTable).append(" where is_valid=1 and assignUser = '").append(statisticVal).append("' and templateId=").append(templateId.getValue()).append(" and ").append(whereStr);
			
					if (startTimestamp != null) {
						sqlBuffer.append(" and createTime >= '"+startTimestamp.toString() + "'");
					}
					if (endTimestamp != null) {
						sqlBuffer.append(" and createTime <= '"+endTimestamp.toString() + "'");
					}
					
					allIdMap = DbPoolConnection.getInstance().getResultSetListBySql(sqlBuffer.toString());
					
					for (Map<String, String> map : allIdMap) {
						allIdSet.add(map.get("id"));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else {
			
			String modelFieldIdStr = "";
			if (type.equals("model")) {
				modelFieldIdStr = XMLUtil.getSingleNode(rootNode, "model/modelfieldId").getTextContent();
			}else if (type.equals("public")) {
				modelFieldIdStr = "templateId";
			}
			
			String fieldColName = DataFilterMemory.getDbColName(modelFieldIdStr, templateId == null ? null : templateId.getValue());
			fieldColName = fieldColName == null ? modelFieldIdStr : fieldColName;
			
			List<String> tablesList = new ArrayList<String>();
			
			if (templateId != null) {
				tablesList.add(TableRuleManager.getInstance().getDataTableName(templateId));
			}else {
				tablesList.addAll(TableRuleManager.getInstance().getAllDataTables());
			}
			
			Set<String> queryFieldSet = new HashSet<String>();
			//查询所有id;
			queryFieldSet.add("id");
			List<QueryCondition> queryConList = new ArrayList<QueryCondition>();
			if (startTimestamp != null) {
				queryConList.add(new QueryCondition("createTime",">=","'"+startTimestamp.toString() + "'"));
			}
			if (endTimestamp != null) {
				queryConList.add(new QueryCondition("createTime","<=","'"+endTimestamp.toString() + "'"));
			}
			if (templateId != null) {
				queryConList.add(new QueryCondition("templateId","=","'"+templateId.getValue() + "'"));
			}
			
			
			if (statisticVal.indexOf(",") != -1) {
				StringBuffer queryValBuffer = new StringBuffer();
				String[] valArr = statisticVal.split(",");
				for (String val : valArr) {
					queryValBuffer.append(queryValBuffer.length() > 0 ? "," : "").append("'" + val + "'");
				}
				queryConList.add(new QueryCondition(fieldColName,"in"," (" + queryValBuffer.toString() + ")"));
			}else {
				queryConList.add(new QueryCondition(fieldColName,"=","'"+statisticVal + "'"));
			}
			
			
			String idSql = DataFilterMemory.getQuerySql(tablesList, queryFieldSet, whereStr, null, isCurrent, templateId, queryConList);
			List<Map<String, String>> allIdList = DbPoolConnection.getInstance().getResultSetListBySql(idSql);
			
			for (Map<String, String> map : allIdList) {
				allIdSet.add(map.get("id"));
			}
		}
		
		StringBuffer idBuffer = new StringBuffer();
		for (String id : allIdSet) {
			idBuffer.append(idBuffer.length() > 0 ? "," : "").append("'" + id + "'");
		}
		
		List<QueryCondition> allQueryList = new ArrayList<QueryCondition>();
		QueryCondition queryCondition = new QueryCondition("id","in"," (" + idBuffer.toString() + ") ");
		allQueryList.add(queryCondition);
		String sql = QueryUtil.getQuerySql(templateId,allQueryList);
		return sql;
	}
}
