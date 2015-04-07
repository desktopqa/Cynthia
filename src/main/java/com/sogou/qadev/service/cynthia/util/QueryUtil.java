package com.sogou.qadev.service.cynthia.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.sogou.qadev.cache.impl.FieldNameCache;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.QueryCondition;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.ConfigManager;
import com.sogou.qadev.service.cynthia.service.ProjectInvolveManager;
import com.sogou.qadev.service.cynthia.service.TableRuleManager;

public class QueryUtil {

	/**
	 * @description:get query sql
	 * @date:2014-5-6 下午6:40:38
	 * @version:v1.0
	 * @param templateId
	 * @param queryConditions
	 * @return
	 */
	public static String getQuerySql(UUID templateId, List<QueryCondition> queryConditions){
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
		
		return sqlBuffer.toString();
	}
	
	/**
	 * @description:get param and values of request
	 * @date:2014-5-6 下午6:40:48
	 * @version:v1.0
	 * @param request
	 * @return
	 */
	public static Map<String, List<String>> getRequestParams(HttpServletRequest request){
		
		Enumeration<String> allParamName = request.getParameterNames();
		Map<String, List<String>>map=new HashMap<String, List<String>>();
		if (allParamName == null) {
			return map;
		}
		
		while (allParamName.hasMoreElements()) {
			String key = allParamName.nextElement();
			if (key.equals("start")||key.equals("limit")||key.equals("sort")||key.equals("dir")) {
				continue;
			}
			String paramValue = request.getParameter(key);
			try {
				paramValue=java.net.URLDecoder.decode(paramValue,"utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (map.get(key) == null) {
				map.put(key, new ArrayList<String>());
			}
			if (paramValue != null && !paramValue.equals("")) {
				map.get(key).add(paramValue);
			}
		}
		return map;
	}

	/**
	 * @description:get all query conditions of request pair
	 * @date:2014-5-6 下午6:41:06
	 * @version:v1.0
	 * @param requestPair
	 * @param templateId
	 * @return
	 */
	public static List<QueryCondition> getQueryCondition(Map<String, List<String>> requestPair,UUID templateId){
		Template template = null;
		if (templateId != null) {
			template = DataAccessFactory.getInstance().getSysDas().queryTemplate(templateId);
		}
		
		List<QueryCondition> allQueryConditions = new ArrayList<QueryCondition>();
		for (String key : requestPair.keySet()) {
			try {
				if (requestPair.get(key) != null && requestPair.get(key).size() > 0) {
					//其它字段都是等于
					String value = requestPair.get(key).get(0);
					if (ConfigManager.getProjectInvolved()) {
						if (key.equals("productId")) {
							Field productField = template.getField(DataAccessFactory.getInstance().createUUID(template.getTemplateConfig().getProductInvolveId()));
							if (productField != null) {
								key = productField.getId().getValue();
							}
						}else if (key.equals("projectId")) {
							Field projectField = template.getField(DataAccessFactory.getInstance().createUUID(template.getTemplateConfig().getProjectInvolveId()));
							if (projectField != null) {
								key = projectField.getId().getValue();
							}
						}
					}
					
					
					//时间字段可能大于 或小于
					if (key.equals("createTime") || key.equals("lastModifyTime")) {
						List<String> timeList = requestPair.get(key);
						if (timeList.size() > 0) {
							if (timeList.size() == 1) {
								QueryCondition qc = new QueryCondition();
								qc.setQueryField(key);
								qc.setQueryMethod(">=");
								Timestamp t1 = Date.valueOf(timeList.get(0)).toTimestamp();
								qc.setQueryValue("'" + t1.toString() + "'");
								allQueryConditions.add(qc);
							}else if (timeList.size() == 2) {
								Timestamp t1 = Date.valueOf(timeList.get(0)).toTimestamp();
								Timestamp t2 = Date.valueOf(timeList.get(1)).toTimestamp();
								Timestamp tmp = null;
								
								if (t1.after(t2)) {
									tmp = t1;
									t1 = t2;
									t2 = tmp;
								}
								QueryCondition qc = new QueryCondition();
								qc.setQueryField(key);
								qc.setQueryMethod(">=");
								qc.setQueryValue("'" + t1.toString() + "'");
								allQueryConditions.add(qc);
								
								qc = new QueryCondition();
								qc.setQueryField(key);
								qc.setQueryMethod("<=");
								qc.setQueryValue("'" + t2.toString() + "'");
								allQueryConditions.add(qc);
							}
						}
					}else {
						if (CommonUtil.isPosNum(key)) {
							if (template == null) {
								continue;
							}
							key = FieldNameCache.getInstance().getFieldName(key ,templateId.getValue());
						}
						QueryCondition qc = new QueryCondition();
						qc.setQueryField(key);
						String queryValue = "";
						if (key.equals("title") || key.equals("description")) {
							//标题 描述以like查询
							qc.setQueryMethod("like");
							queryValue = "'%" + value + "%'";
						}else {
							if (value.indexOf(",") != -1) {
								//以逗号隔开的为in查询
								qc.setQueryMethod("in");
								StringBuffer valueBuffer = new StringBuffer();
								valueBuffer.append("(");
								String[] allValues = value.split(",");
								for(String v : allValues){
									valueBuffer.append("'").append(v).append("',");
								}
								valueBuffer = valueBuffer.deleteCharAt(valueBuffer.length() -1);
								valueBuffer.append(")");
								queryValue = valueBuffer.toString();
							}else {
								qc.setQueryMethod("=");
								queryValue = "'" + value + "'";
							}
						}
						qc.setQueryValue(queryValue);
						allQueryConditions.add(qc);
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return allQueryConditions;
	}
	
	/**
	 * @description:get query where string from query conditions
	 * @date:2014-5-6 下午6:41:30
	 * @version:v1.0
	 * @param queryConditions
	 * @return
	 */
	public static String getQueryWhereStr(List<QueryCondition> queryConditions){
		StringBuffer whereBuffer = new StringBuffer();
		whereBuffer.append(" is_valid = 1 ");
		for (QueryCondition queryCondition : queryConditions) {
			whereBuffer.append(" and ").append(queryCondition.getQueryField()).append(" " + queryCondition.getQueryMethod() + " ").append(queryCondition.getQueryValue() + " ");
		}
		return whereBuffer.toString();
	}

}
