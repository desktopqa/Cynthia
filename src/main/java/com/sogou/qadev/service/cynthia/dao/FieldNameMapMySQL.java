package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Field.DataType;
import com.sogou.qadev.service.cynthia.bean.Field.Type;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;

/**
 * @description:field name map db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:32:25
 * @version:v1.0
 */
public class FieldNameMapMySQL {
	
	private static FieldNameMapMySQL instance = null;

	private static Map<String,Integer> fieldTypeCountMap = new HashMap<String, Integer>();
	
	/**
	 * max single type  field count
	 */
	private static int fieldIntCount = 40;
	
	private static int fieldIntMCount = 10;
	/**
	 * max single string field count
	 */
	private static int fieldStrCount = 35;
	/**
	 * max multi string type field count
	 */
	private static int fieldStrMCount = 30;
	private static int fieldEditorCount = 5;
	private static int fieldComCount = 5;
	
	public static synchronized final FieldNameMapMySQL getInstance()
	{
		if (instance == null){
			instance = new FieldNameMapMySQL();
			fieldTypeCountMap.put("Int", fieldIntCount);
			fieldTypeCountMap.put("IntM", fieldIntMCount);
			fieldTypeCountMap.put("Str", fieldStrCount);
			fieldTypeCountMap.put("StrM", fieldStrMCount);
			fieldTypeCountMap.put("Editor", fieldEditorCount);
			fieldTypeCountMap.put("Com", fieldComCount);
		}
		
		return instance;
	}

	private FieldNameMapMySQL()
	{
		super();
	}
	
	/**
	 * @description:get last used field colname by field type and template
	 * @date:2014-5-6 下午5:35:28
	 * @version:v1.0
	 * @param fieldType
	 * @param templateId
	 * @return
	 */
	private String getLastUsedFieldName(String fieldType,String templateId){
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		String fieldName = "";
		try {
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("SELECT fieldColName FROM field_name_map WHERE templateId="+ templateId +" and fieldType = '"+fieldType+"' ORDER BY id DESC LIMIT 1");
			while (rs.next()) {
				fieldName = rs.getString("fieldColName");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(stat);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return fieldName;
	}
	
	/**
	 * @description:get all fieldcolnames by field type
	 * @date:2014-5-6 下午5:35:09
	 * @version:v1.0
	 * @param fieldType
	 * @param templateId
	 * @return
	 */
	private List<String> getAllUsedFieldName(String fieldType,String templateId){
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		List<String> allFieldName = new ArrayList<String>();
		try {
			conn = DbPoolConnection.getInstance().getConnection();
			stat = conn.createStatement();
			String sql = "SELECT fieldColName FROM field_name_map WHERE templateId="+ templateId +" and fieldType = '"+fieldType+"'";
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				allFieldName.add(rs.getString("fieldColName"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}finally{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(stat);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		if (fieldType != null && fieldType.equals("Com")) {  //保留字段
			allFieldName.add("fieldCom_1");  //修改优先级	
		}
		return allFieldName;
	}
	
	private String returnFirstField(String fieldType ,Integer index){
		return "field" + fieldType + "_" + index;
	}
	
	/**
	 * 
	 * 功能：从allFieldNameList获得第一个未用的列名
	 * 更改时间：2013-9-4 下午3:47:01
	 * 作者：李明
	 * 版本：1.0
	 * 邮箱:liming@sogou-inc.com
	 * @param allFieldNameList
	 * @param fieldType
	 * @return
	 */
	private String getOneNotUsed(List<String> allFieldNameList , String fieldType){
		int allLength = fieldTypeCountMap.get(fieldType);
		for (int i = 1; i <= allLength; i++) {
			String fieldColName = "field" + fieldType + "_" + i;
			if (!allFieldNameList.contains(fieldColName)) {
				return returnFirstField(fieldType, i);
			}
		}
		return "";
	}
	
	/**
	 * @description:get one usable field colname
	 * @date:2014-5-6 下午5:34:44
	 * @version:v1.0
	 * @param fieldType
	 * @param templateId
	 * @return
	 */
	public String getOneFieldName(String fieldType,String templateId){
		
		//首先查询中间未使用的
		List<String> allFieldNameList = getAllUsedFieldName(fieldType, templateId);
		String fieldColName = getOneNotUsed(allFieldNameList,fieldType);
		if (fieldColName != "") {
			 return fieldColName;
		}else {
			//查询剩余字段
			allFieldNameList = getAllUsedFieldName("Com", templateId);  //Com为通用字段
			return getOneNotUsed(allFieldNameList, "Com");
		}
	}
	
	/**
	 * @description:get field col name type
	 * @date:2014-5-6 下午5:34:26
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public String getFieldColNameType(Field field){
		String fieldTypeStr = "";
		Type type = field.getType();
		DataType dataType = field.getDataType();
		
		if(field.getName().equals("修改优先级")){
			return "Com";  
		}
		
		if (type == Type.t_selection) {
			if (dataType == DataType.dt_single) {
				fieldTypeStr = "Int";
			}else {
				fieldTypeStr = "IntM";
			}
		}else if (type == Type.t_reference) {
			if (dataType == DataType.dt_single) {
				fieldTypeStr = "Int";
			}else {
				fieldTypeStr = "StrM";
			}
		}else if (type == Type.t_attachment) {
			fieldTypeStr = "Str";
		}else if (type == Type.t_input) {
			if (dataType == DataType.dt_integer || dataType == DataType.dt_long) {
				fieldTypeStr = "Int";
			}else if (dataType == DataType.dt_double || dataType == DataType.dt_float ||dataType == DataType.dt_timestamp ||dataType == DataType.dt_string ) {
				fieldTypeStr = "Str";
			}else if ( dataType == DataType.dt_text) {
				fieldTypeStr = "StrM";
			}else {
				fieldTypeStr = "Editor";
			}
		}
		
		return fieldTypeStr;
	}
	
	/**
	 * @description:return one useable field colname
	 * @date:2014-5-6 下午5:34:04
	 * @version:v1.0
	 * @param field
	 * @param templateId
	 * @return
	 */
	public String getOneFieldName(Field field, String templateId){
		if (field == null || templateId == null) {
			return null;
		}
		String fieldTypeStr = "";
		if(field.getName().equals("修改优先级")){
			return "fieldCom_1";  
		}else {
			fieldTypeStr = getFieldColNameType(field);
			return getOneFieldName(fieldTypeStr, templateId);
		}
	}
}
