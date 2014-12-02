package com.sogou.qadev.service.cynthia.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.sogou.qadev.service.cynthia.bean.ChangeLog;
import com.sogou.qadev.service.cynthia.bean.Pair;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.impl.ChangeLogImpl;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;

/**
 * @description:data log processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午12:11:05
 * @version:v1.0
 */
public class LogManager
{
	private static LogManager instance = null;
	
	public static LogManager getInstance()
	{
		if (instance == null)
			instance = new LogManager();

		return instance;
	}
	
	
	/**
	 * @function：根据数据库查询结果Map 组装Change日志
	 * @modifyTime：2013-9-5 下午12:23:22
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param current 当前Map
	 * @param before 上一条Map
	 * @return
	 */
	public ChangeLog getChangeLog(Map<String, String> current,Map<String, String> before,Map<String, String> templateFieldNameCache){
		if (current == null && before == null) {
			return null;
		}
		
		Map<String, Pair<Object, Object>> baseValueMap = new HashMap<String, Pair<Object,Object>>();
		Map<UUID, Pair<Object, Object>> extValueMap = new HashMap<UUID, Pair<Object,Object>>();
		
		if (before == null) {
			//新建数据
			for (String fieldColName : current.keySet()) {
				if (fieldColName.equals("title") || fieldColName.equals("description") || fieldColName.equals("assignUser") || fieldColName.equals("statusId")) {
					
					if( fieldColName.equals("statusId")){
						baseValueMap.put(fieldColName, new Pair<Object, Object>(null, DataAccessFactory.getInstance().createUUID(current.get(fieldColName))));
					}else {
						baseValueMap.put(fieldColName, new Pair<Object, Object>(null, current.get(fieldColName)));
					}
					
				}else if (fieldColName.startsWith("field") && current.get(fieldColName) != null && current.get(fieldColName).length() > 0) {
					//普通字段
					String fieldStr = templateFieldNameCache.get(fieldColName);
					UUID fieldId = DataAccessFactory.getInstance().createUUID(fieldStr);
					extValueMap.put(fieldId, new Pair<Object, Object>(null, current.get(fieldColName)));
				}
			}
		}else {
			for (String fieldColName : current.keySet()) {
				if (current.get(fieldColName) == null && before.get(fieldColName) == null) {
					continue;
				}
				if ((current.get(fieldColName) == null && before.get(fieldColName) != null)
						||(current.get(fieldColName) != null && before.get(fieldColName) == null) 
						|| !current.get(fieldColName).equals(before.get(fieldColName))) {
					
					if (fieldColName.equals("title") || fieldColName.equals("description") || fieldColName.equals("assignUser") || fieldColName.equals("statusId")) {
						if( fieldColName.equals("statusId")){
							baseValueMap.put(fieldColName, new Pair<Object, Object>(DataAccessFactory.getInstance().createUUID(before.get(fieldColName)), DataAccessFactory.getInstance().createUUID(current.get(fieldColName))));
						}else {
							baseValueMap.put(fieldColName, new Pair<Object, Object>(before.get(fieldColName), current.get(fieldColName)));
						}
					}else if (fieldColName.startsWith("field") && (current.get(fieldColName) != null || before.get(fieldColName) != null) && ((current.get(fieldColName) != null && current.get(fieldColName).length() > 0 )|| before.get(fieldColName) != null && before.get(fieldColName).length() > 0)) {
						//普通字段
						String fieldStr = templateFieldNameCache.get(fieldColName);
						UUID fieldId = null;
						if (fieldColName.equalsIgnoreCase("logActionId") && fieldStr.equals("48")) {  //编辑
							fieldId = null;
						}else {
							fieldId	= DataAccessFactory.getInstance().createUUID(fieldStr);
						}
						extValueMap.put(fieldId, new Pair<Object, Object>(before.get(fieldColName), current.get(fieldColName)));
					}
				}
			}
		}
		
		
		ChangeLog changeLog = new ChangeLogImpl(
				DataAccessFactory.getInstance().createUUID(current.get("dataId")), 
				current.get("logcreateUser") == null ? "" :current.get("logcreateUser").toString(), 
				Timestamp.valueOf(current.get("logcreateTime")),
				DataAccessFactory.getInstance().createUUID(current.get("logActionId")), 
				current.get("logActionComment") == null ? "" :current.get("logActionComment").toString(), 
				baseValueMap, 
				extValueMap
				);
		
		return changeLog;
	}
	
}
