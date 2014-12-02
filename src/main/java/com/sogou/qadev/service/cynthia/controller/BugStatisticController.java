package com.sogou.qadev.service.cynthia.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sogou.qadev.cache.impl.FieldNameCache;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Field.Type;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Key;
import com.sogou.qadev.service.cynthia.bean.Option;
import com.sogou.qadev.service.cynthia.bean.Option.Forbidden;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;
import com.sogou.qadev.service.cynthia.service.TableRuleManager;
import com.sogou.qadev.service.cynthia.util.CommonUtil;
import com.sogou.qadev.service.cynthia.util.Date;

/**
 * 
 * @description:bug statistic manager
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午8:09:17
 * @version:v1.0
 */
@Controller
@RequestMapping("/bugstatistic")
public class BugStatisticController extends BaseController{
	
	private class StatisticField implements Comparable<StatisticField>{
		private String fieldId;
		private String fieldName;
		
		public StatisticField(String fieldId, String fieldName){
			this.fieldId = fieldId;
			this.fieldName = fieldName;
		}
		public String getFieldId() {
			return fieldId;
		}
		public void setFieldId(String fieldId) {
			this.fieldId = fieldId;
		}
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		@Override
		public int compareTo(StatisticField o) {
			//创建人指派人按邮箱排序
			if (this.getFieldId().equals("createUser") || this.getFieldId().equals("assignUser") ) {
				return this.getFieldId().compareTo(o.getFieldId());
			}else {
				//其它按显示内容排序
				return this.getFieldName().compareTo(o.getFieldName());
			}
		}
	}
	
	/**
	 * @description:return the fields which could be used for statistic
	 * @date:2014-5-5 下午8:09:38
	 * @version:v1.0
	 * @param templateIdStr
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getStatisticField.do")
	public String getStatisticField(@RequestParam("templateId") String templateIdStr, HttpSession httpSession) throws Exception {
		Key key   = ((Key)httpSession.getAttribute("key"));
		Long keyId = (Long)httpSession.getAttribute("kid");
		
		das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
		
		List<StatisticField> allFieldList = new ArrayList<BugStatisticController.StatisticField>();
 		Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateIdStr));
		
 		allFieldList.add(new StatisticField("statusId", "状态"));
 		allFieldList.add(new StatisticField("createUser", "创建人"));
 		allFieldList.add(new StatisticField("assignUser", "指派人"));
		
		for (Field field : template.getFields()) {
			//只统计单选字段
			if (field.getName().contains("废弃") || !field.getType().equals(Type.t_selection)) {
				continue;
			}
			StatisticField sf = new StatisticField(field.getId().getValue(), field.getName());
			allFieldList.add(sf);
		}
		
		return com.alibaba.fastjson.JSONArray.toJSONString(allFieldList);
	}

	/**
	 * @description:return the options of statistic field
	 * @date:2014-5-5 下午8:10:19
	 * @version:v1.0
	 * @param templateIdStr:statistic template id
	 * @param fieldIdStr: statistic field id string
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getFieldOption.do")
	public String getFieldOption(@RequestParam("templateId") String templateIdStr, @RequestParam("fieldId") String fieldIdStr, HttpSession httpSession) throws Exception {
		Key key   = ((Key)httpSession.getAttribute("key"));
		Long keyId = (Long)httpSession.getAttribute("kid");
		
		das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
		
		Set<StatisticField> allFieldSet = new HashSet<BugStatisticController.StatisticField>();
		
 		Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateIdStr));
		
 		Flow flow = das.queryFlow(template.getFlowId());
 		if(fieldIdStr.equals("statusId")){
 	 		for (Stat stat : flow.getStats()) {
 	 			allFieldSet.add(new StatisticField(stat.getId().getValue(), stat.getName()));
 			}
 		}else if (fieldIdStr.equals("createUser") || fieldIdStr.equals("assignUser")) {
 	 		List<UserInfo> allUsers = flow.queryAllUserInfo();
 	 		for (UserInfo userInfo : allUsers) {
 	 			allFieldSet.add(new StatisticField(userInfo.getUserName(), userInfo.getNickName()));
 			}
		}else {
			Field field = template.getField(DataAccessFactory.getInstance().createUUID(fieldIdStr));
			if(field != null){
				for (Option option : field.getOptions()) {
					if (option != null && option.getForbidden().equals(Forbidden.f_permit)) {
						allFieldSet.add(new StatisticField(option.getId().getValue(), option.getName()));
					}
				}
			}
		}
 		
 		StatisticField[] allFields = allFieldSet.toArray(new StatisticField[0]);
 		Arrays.sort(allFields);
		return com.alibaba.fastjson.JSONArray.toJSONString(allFields);
	}
	
	/**
	 * @description:return the statistic name and data
	 * @date:2014-5-5 下午8:10:58
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getBugData.do")
	public String getBugData(HttpServletRequest request, HttpServletResponse response, HttpSession httpSession) throws Exception {
		Key key   = ((Key)httpSession.getAttribute("key"));
		Long keyId = (Long)httpSession.getAttribute("kid");
		
		das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
		
		String templateIdStr = request.getParameter("templateId");
		String fieldIdStr = request.getParameter("fieldId");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String[] statisticOption = request.getParameterValues("statisticOption[]");
		
 		Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateIdStr));
 		
 		Flow flow = das.queryFlow(template.getFlowId());
 		
 		Timestamp startTimestamp = null;
 		if (startTime != null && !startTime.equals("null") && startTime.length() > 0) {
			startTimestamp = Date.valueOf(startTime).toTimestamp();
		}
 		
 		Timestamp endTimestamp = null;
 		if (endTime != null && !endTime.equals("null") && endTime.length() > 0) {
 			endTimestamp = Date.valueOf(endTime).toTimestamp();
		}
 		
 		String name = template.getName();
 		
 		if (fieldIdStr.equals("createUser")) {
			name += ":创建人";
		}else if (fieldIdStr.equals("assignUser")) {
			name += ":指派人";
		}else if (fieldIdStr.equals("statusId")) {
			name += ":状态";
		}else if (CommonUtil.isPosNum(fieldIdStr)) {
			//表单字段
			Field field = template.getField(DataAccessFactory.getInstance().createUUID(fieldIdStr));
			if (field == null) {
				return "";
			}
			name += ":"+field.getName();
		}
 		
 		Map<String, String> dataMap = getTemplateFieldStatistic(template, flow, fieldIdStr, startTimestamp, endTimestamp, statisticOption);
 		
 		Map<String, Object> resultMap = new HashMap<String, Object>();
 		resultMap.put("name", name);
 		resultMap.put("datas", dataMap);
		return com.alibaba.fastjson.JSONArray.toJSONString(resultMap);
	}
	
	/**
	 * @description:return the statistic data
	 * @date:2014-5-5 下午8:13:00
	 * @version:v1.0
	 * @param template
	 * @param flow
	 * @param fieldId
	 * @param startTimestamp
	 * @param endTimestamp
	 * @param options
	 * @return
	 */
	public Map<String, String> getTemplateFieldStatistic(Template template , Flow flow,  String fieldId , Timestamp startTimestamp, Timestamp endTimestamp, String[] options){
		Map<String, String> resultMap = new HashMap<String, String>();
		Map<String, String> fieldNameMap = new HashMap<String, String>();  //字段id对应名字
		String dbColName = fieldId;
		
		if (fieldId.equals("createUser") || fieldId.equals("assignUser")) {
			List<UserInfo> allUserList = flow.queryAllUserInfo();
			for (UserInfo userInfo : allUserList) {
				fieldNameMap.put(userInfo.getUserName(), userInfo.getNickName());
			}
		}else if (fieldId.equals("statusId")) {
			for (Stat stat : flow.getStats()) {
				fieldNameMap.put(stat.getId().getValue(), stat.getName());
			}
		}else if (CommonUtil.isPosNum(fieldId)) {
			//表单字段
			dbColName = FieldNameCache.getInstance().getFieldName(fieldId, template.getId().getValue());
			Field field = template.getField(DataAccessFactory.getInstance().createUUID(fieldId));
			if (field == null) {
				return resultMap;
			}
			for (Option option : field.getOptions()) {
				if (option != null && option.getForbidden().equals(Forbidden.f_permit)) {
					fieldNameMap.put(option.getId().getValue(), option.getName());
				}
			}
		}
		
		
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append("select ").append(dbColName).append(" , count(*) as count from ").append(TableRuleManager.getInstance().getDataTableName(template.getId()));
		if (startTimestamp != null) {
			sqlBuffer.append(" where createTime>='").append(startTimestamp.toString()).append("'");
		}
		if (endTimestamp != null) {
			sqlBuffer.append(sqlBuffer.indexOf("where") != -1 ? " and " : " where ").append(" createTime<='").append(endTimestamp.toString()).append("'");;
		}
		
		if (options != null && options.length > 0) {
			StringBuffer optionBuffer = new StringBuffer();
			for (String option : options) {
				optionBuffer.append(optionBuffer.length() > 0 ? "," :"").append("'").append(option).append("'");
			}
			sqlBuffer.append(sqlBuffer.indexOf("where") != -1 ? " and " : " where ").append(dbColName).append(" in (").append(optionBuffer.toString()).append(") ");
		}
		
		//查询有效数据
		sqlBuffer.append(sqlBuffer.indexOf("where") != -1 ? " and " : " where ").append(" is_valid = 1 ");
		
		//查询表单
		sqlBuffer.append(" and templateId = ").append(template.getId().getValue() + " ");
		
		sqlBuffer.append(" group by ").append(dbColName);
		
		Map<String, String> dbMap = DbPoolConnection.getInstance().getCountMap(sqlBuffer.toString());
	
		for (String key : dbMap.keySet()) {
			resultMap.put(fieldNameMap.get(key) == null ? key : fieldNameMap.get(key), dbMap.get(key));
		}
		
		return resultMap;
		
	}
	
	/**
	 * @description:get the status of template related to the task
	 * @date:2014-5-5 下午8:13:15
	 * @version:v1.0
	 * @param templateIdStr
	 * @param taskBugFieldStr
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getTaskBugStatus.do")
	public String getTaskBugStatus(@RequestParam("templateId") String templateIdStr, @RequestParam("taskBugField") String taskBugFieldStr, HttpSession httpSession) throws Exception {
		
		Set<StatisticField> allFieldSet = new HashSet<BugStatisticController.StatisticField>();
		
		String taskBugColName = FieldNameCache.getInstance().getFieldName(taskBugFieldStr, templateIdStr);
		
		String [] allBugArr = new DataAccessSessionMySQL().queryFieldByTemplate(DataAccessFactory.getInstance().createUUID(templateIdStr), taskBugColName);
		
		Template bugTemplate = null;
		if (allBugArr != null) {
			for (String bugIds : allBugArr) {
				if (bugTemplate == null) {
					String[] bugIdArr = bugIds.split(",");
					if (bugIdArr != null && bugIdArr.length > 0) {
						Data data = das.queryData(DataAccessFactory.getInstance().createUUID(bugIdArr[0]));
						if (data != null) {
							bugTemplate = das.queryTemplate(data.getTemplateId());
						}
					}
				}else {
					break;
				}
			}
		}
		
		if (bugTemplate == null) {
			return "";
		}
		
 		Flow flow = das.queryFlow(bugTemplate.getFlowId());
 		if (flow != null) {
 			for (Stat stat : flow.getStats()) {
 	 			allFieldSet.add(new StatisticField(stat.getId().getValue(), stat.getName()));
 			}
		}
 		
		return com.alibaba.fastjson.JSONArray.toJSONString(allFieldSet);
	}
	
}
