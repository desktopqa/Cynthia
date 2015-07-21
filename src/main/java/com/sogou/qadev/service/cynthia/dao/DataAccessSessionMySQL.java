package com.sogou.qadev.service.cynthia.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sogou.qadev.cache.impl.FieldNameCache;
import com.sogou.qadev.cache.impl.TemplateCache;
import com.sogou.qadev.service.cynthia.bean.ChangeLog;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Field.DataType;
import com.sogou.qadev.service.cynthia.bean.Field.Type;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.impl.DataImpl;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;
import com.sogou.qadev.service.cynthia.service.TableRuleManager;
import com.sogou.qadev.service.cynthia.util.CommonUtil;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;
import com.sogou.qadev.service.cynthia.util.Date;

/**
 * @description:data db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:17:30
 * @version:v1.0
 */
public class DataAccessSessionMySQL {

	private static Logger logger = Logger.getLogger(DataAccessSessionMySQL.class.getName());

	private Map<String, String> templateFieldNameCache = null;

	public DataAccessSessionMySQL(UUID templateId)
	{
		setTemplateFieldNameCache(templateId);
	}

	/**
	 * @description:set all template field colname cache
	 * @date:2014-5-6 下午5:17:45
	 * @version:v1.0
	 * @param templateId
	 */
	public void setTemplateFieldNameCache(UUID templateId){
		templateFieldNameCache = new FieldNameAccessSessionMySQL().queryTemplateFieldMap(templateId.getValue());
	}

	public DataAccessSessionMySQL(){

	}

	/**
	 * @description:insert data to db
	 * @date:2014-5-6 下午5:18:09
	 * @version:v1.0
	 * @param fieldValueMap
	 * @param tableName
	 * @param conn
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public synchronized boolean insertDataToDB(Map<String, String> fieldValueMap,String tableName,Connection conn) throws IOException, SQLException{

		if (tableName == null || conn ==null) {
			return false;
		}
		boolean result = false;
		PreparedStatement pStat = null;

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("replace into ").append(tableName).append(" (");

		Iterator<Map.Entry<String, String>> iterator = fieldValueMap.entrySet().iterator();  //去掉空列表
		while (iterator.hasNext()) {
			Map.Entry<String,String> entry = iterator.next();
			if (entry == null || entry.getKey() == null || entry.getKey().equals("")) {
				iterator.remove();
			}
		}
		if (fieldValueMap.keySet().size() == 0) {
			return false;
		}

		for (String fieldName : fieldValueMap.keySet()) {
			sqlBuffer.append(fieldName).append(",");
		}
		sqlBuffer.deleteCharAt(sqlBuffer.length() -1);

		sqlBuffer.append(") values(");

		for (String fieldName : fieldValueMap.keySet()) {
			
			sqlBuffer.append("?,");
		}

		sqlBuffer.deleteCharAt(sqlBuffer.length() -1);
		sqlBuffer.append(")");

		String sql = sqlBuffer.toString();
		try {
			pStat = conn.prepareStatement(sql);
			int i = 1;
			for (String fieldName : fieldValueMap.keySet()) {
				if (fieldValueMap.get(fieldName) == null)
					pStat.setString(i++, null);
				else
					pStat.setString(i++, fieldValueMap.get(fieldName));
			}
			return pStat.executeUpdate() > 0;

		} catch (Exception e) {
			String content = "------------------------------------\r\n";
			if (fieldValueMap.get("dataId") != null) {
				content += "id:" + fieldValueMap.get("dataId") +"\r\n";
			}else {
				content += "id:" + fieldValueMap.get("id") +"\r\n";
			}
			content += "sql:" + sql +"\r\n";
			content += "error reason:" + e.getMessage() +"\r\n\r\n\r\n\r\n\r\n";
			logger.error(content);
			e.printStackTrace();
		}finally{
			DbPoolConnection.getInstance().closeStatment(pStat);
		}
		return result;
	}

	/**
	 * @description:updata data from db
	 * @date:2014-5-6 下午5:18:31
	 * @version:v1.0
	 * @param fieldValueMap:data value map
	 * @param tableName
	 * @param conn
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public synchronized boolean updateDataToDB(Map<String, String> fieldValueMap,String tableName,Connection conn) throws IOException, SQLException{
		PreparedStatement pStat = null;

		Iterator<Map.Entry<String, String>> iterator = fieldValueMap.entrySet().iterator();  //去掉空列表
		while (iterator.hasNext()) {
			Map.Entry<String,String> entry = iterator.next();
			if (entry.getKey().equals("")) {
				iterator.remove();
			}
		}

		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update ").append(tableName).append(" Set ");

		for (String fieldName : fieldValueMap.keySet()) {
			if (fieldName.equals("id") ) {
				continue;
			}
			sqlBuffer.append(fieldName).append(" = ").append( "?").append(" ,");
		}
		String sql = sqlBuffer.toString();
		if (sql.endsWith(",")) {
			sql = sql.substring(0,sql.length()-1);
		}

		sql += " where id = " + fieldValueMap.get("id");

		try {
			pStat = conn.prepareStatement(sql);
			int i = 1;
			for (String fieldName : fieldValueMap.keySet()) {
				if (fieldName.equals("id") )
					continue;
				if (fieldValueMap.get(fieldName) == null || fieldValueMap.get(fieldName).equals(""))
					pStat.setNull(i++, Types.VARCHAR);
				else
					pStat.setString(i++, fieldValueMap.get(fieldName));
			}
			return pStat.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			DbPoolConnection.getInstance().closeStatment(pStat);
		}
	}

	/**
	 * @description:get if data is exist
	 * @date:2014-5-6 下午5:18:50
	 * @version:v1.0
	 * @param dataId
	 * @return
	 */
	public boolean isExist(UUID dataId){
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try
		{
			List<String> queryFieldList = new ArrayList<String>();
			queryFieldList.add("id");
			Map<String, String> whereFieldsMap = new HashMap<String, String>();
			whereFieldsMap.put("id", dataId.getValue());

			String sql = DbPoolConnection.getInstance().getDataQuerySQL(TableRuleManager.getInstance().getAllDataTables(),queryFieldList, whereFieldsMap, null,null);
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();

			rs = stat.executeQuery(sql);
			return rs.next();
		}catch(Exception e){
			logger.error("", e);
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(stat);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return false;
	}

	/**
	 * @description:get if data exist
	 * @date:2014-5-6 下午5:19:04
	 * @version:v1.0
	 * @param dataId
	 * @param templateId
	 * @return
	 */
	public boolean isExist(UUID dataId,UUID templateId)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{

			String tableName = TableRuleManager.getInstance().getDataTableName(templateId);
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT id FROM " + tableName + " WHERE id = ? and is_valid = ?");
			pstm.setLong(1, Long.parseLong(dataId.getValue()));
			pstm.setString(2, "1");

			rs = pstm.executeQuery();
			return rs.next();
		}catch(Exception e){
			logger.error("",e);
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return false;
	}

	/**
	 * @description:query field values by templatetype id
	 * @date:2014-5-6 下午5:19:14
	 * @version:v1.0
	 * @param templateTypeId
	 * @param queryField
	 * @return
	 */
	public String[] queryFieldByTemplateType(UUID templateTypeId , String queryField){
		if (templateTypeId == null || queryField == null) {
			return new String[0];
		}
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		Set<String> resultSet = new HashSet<String>();

		try
		{
			List<String> queryFieldList = new ArrayList<String>();
			queryFieldList.add(queryField);
			Map<String, String> whereFieldsMap = new HashMap<String, String>();
			whereFieldsMap.put("templateTypeId", templateTypeId.getValue());
			List<String> tableList = TableRuleManager.getInstance().getAllDataTables();

			String sql = DbPoolConnection.getInstance().getDataQuerySQL(tableList,queryFieldList, whereFieldsMap,null,null);

			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();

			rs = stat.executeQuery(sql);
			while (rs.next()) {
				String tmp = rs.getString(queryField);
				if (tmp != null && tmp.length() > 0) {
					resultSet.add(tmp);
				}
			}
		}catch(Exception e){
			logger.error("",e);
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, stat, conn);
		}
		return resultSet.toArray(new String[resultSet.size()]);
	}

	/**
	 * @description:query field values by template id
	 * @date:2014-5-6 下午5:19:33
	 * @version:v1.0
	 * @param templateId
	 * @param queryField
	 * @return
	 */
	public String[] queryFieldByTemplate(UUID templateId , String queryField){
		if (templateId == null || queryField == null) {
			return new String[0];
		}
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		Set<String> resultSet = new HashSet<String>();

		try
		{
			StringBuffer sqlBuffer = new StringBuffer();
			
			sqlBuffer.append("select ").append(queryField).append(" from ")
				.append(TableRuleManager.getInstance().getDataTableName(templateId)).append(" where templateId = ").append(templateId.getValue()).append(" and ").append(queryField).append(" is not null ");

			if (queryField!= null && (queryField.equals("assignUser") || queryField.equals("createUser")) ) {
				sqlBuffer.append(" and ").append(queryField).append("!='--请选择--'");
			}
			sqlBuffer.append(" and is_valid=1");
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();

			rs = stat.executeQuery(sqlBuffer.toString());
			while (rs.next()) {
				String tmp = rs.getString(queryField);
				if (tmp != null && tmp.length() > 0) {
					resultSet.add(tmp);
				}
			}
		}catch(Exception e){
			logger.error("",e);
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, stat, conn);
		}
		return resultSet.toArray(new String[resultSet.size()]);
	}

	/**
	 * @description:query data from db
	 * @date:2014-5-6 下午5:20:07
	 * @version:v1.0
	 * @param dataId
	 * @return
	 */
	public Data queryData(UUID dataId){
		if (dataId == null) {
			return null;
		}
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		Data data = null;

		try
		{
			List<String> queryFieldList = new ArrayList<String>();
			Map<String, String> whereFieldsMap = new HashMap<String, String>();
			whereFieldsMap.put("id", dataId.getValue());

			String sql = DbPoolConnection.getInstance().getDataQuerySQL(TableRuleManager.getInstance().getAllDataTables(),queryFieldList, whereFieldsMap,null,null);

			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();

			rs = stat.executeQuery(sql);

			List<Map<String, String>> colValueMapList = DbPoolConnection.getInstance().getDataMapFromRs(rs);

			if (colValueMapList.size() > 0) {
				UUID templateId = DataAccessFactory.getInstance().createUUID(colValueMapList.get(0).get("templateId"));
				if (templateId == null) {
					return data;
				}

				setTemplateFieldNameCache(templateId);

				List<Data> allDatas = assembleDatas(colValueMapList,true);
				if (allDatas.size() > 0) {
					data = allDatas.get(0);
				}
			}

		}catch(Exception e){
			logger.error("",e);
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, stat, conn);
		}
		return data;
	}

	/**
	 * @description:get limit sort sql
	 * @date:2014-5-6 下午5:20:19
	 * @version:v1.0
	 * @param tablesList
	 * @param queryFieldsList
	 * @param whereFieldsMap
	 * @param orderFieldsMap
	 * @param groupFieldMap
	 * @param start
	 * @param limit
	 * @return
	 */
	private String getDataQuerySQL(List<String> tablesList ,List<String> queryFieldsList , Map<String, String> whereFieldsMap , Map<String, String> orderFieldsMap , Map<String, String> groupFieldMap , int start,int limit){
		String sql = DbPoolConnection.getInstance().getDataQuerySQL(tablesList, queryFieldsList, whereFieldsMap, orderFieldsMap, groupFieldMap);
		sql += " limit " + start + "," + limit;
		return sql;
	}

	/**
	 * @description:assemble datas 
	 * @date:2014-5-6 下午5:20:32
	 * @version:v1.0
	 * @param colValueMapList
	 * @param needLog
	 * @return
	 * @throws ParseException
	 */
	public List<Data> assembleDatas(List<Map<String, String>> colValueMapList , boolean needLog) throws ParseException{
		List<Data> allDatas = new ArrayList<Data>();

		Map<UUID, Template> allTemplateMap = new HashMap<UUID, Template>();
		Map<UUID, Map<String, String>> templateFieldNameMap = new HashMap<UUID, Map<String,String>>();

		for (Map<String, String> colValueMap : colValueMapList) {
			Data data = new DataImpl();
			UUID dataId = DataAccessFactory.getInstance().createUUID(colValueMap.get("id"));
			UUID templateId = DataAccessFactory.getInstance().createUUID(colValueMap.get("templateId"));

			Map<UUID, Object> objectMapUUID = new HashMap<UUID, Object>();
			Map<String, Object> objectMapName = new HashMap<String, Object>();

			data.setId(dataId);
			data.setTemplateId(templateId);
			data.setTemplateTypeId(DataAccessFactory.getInstance().createUUID(colValueMap.get("templateTypeId")));
			data.setTitle(colValueMap.get("title"));
			data.setDescription(colValueMap.get("description"));
			data.setCreateUser(colValueMap.get("createUser"));
			data.setCreateTime(colValueMap.get("createTime") == null ? null:Timestamp.valueOf(colValueMap.get("createTime")));
			data.setLastModifyTime(colValueMap.get("lastModifyTime") == null ? null : Timestamp.valueOf(colValueMap.get("lastModifyTime")));
			data.setAssignUser(colValueMap.get("assignUser"));
			data.setStatusId(DataAccessFactory.getInstance().createUUID(colValueMap.get("statusId")));

			if (allTemplateMap.get(templateId) == null) {
				Template template = TemplateCache.getInstance().get(templateId);
				if (template != null) {
					allTemplateMap.put(templateId, template);
				}
			}

			Template template = allTemplateMap.get(templateId);

			for (String colName : colValueMap.keySet()) {
				Field validField = null;
				try {
					if (colName.startsWith("field") && colValueMap.get(colName) != null && colValueMap.get(colName).length() > 0) {

						String fieldIdStr = "";

						if (templateFieldNameCache == null ) {
							if (templateFieldNameMap.get(templateId) == null) {
								Map<String, String> fieldNameMap = new FieldNameAccessSessionMySQL().queryTemplateFieldMap(templateId.getValue());
								templateFieldNameMap.put(templateId, fieldNameMap);
							}
							fieldIdStr = templateFieldNameMap.get(templateId).get(colName);
						}else {
							fieldIdStr = templateFieldNameCache.get(colName);
						}

						UUID fieldId = DataAccessFactory.getInstance().createUUID(fieldIdStr);

						validField = template.getField(fieldId);

						if (validField == null) {
//							System.out.println("can not find field in assembleData ,fieldIdStr :" + fieldIdStr );
							continue;
						}
						
						if (validField.getType().equals(Type.t_selection))
						{
							if (validField.getDataType().equals(DataType.dt_single))
							{
								UUID dataValidId = DataAccessFactory.getInstance().createUUID(colValueMap.get(colName));
								objectMapUUID.put(fieldId, dataValidId);
								objectMapName.put(validField.getName(), dataValidId);
							}
							else if (validField.getDataType().equals(DataType.dt_multiple))
							{
								String [] alldatas = colValueMap.get(colName).split(",");
								UUID[] dataValidIds = new UUID[alldatas.length];

								for (int i = 0; i < alldatas.length; i++) {
									dataValidIds[i] = DataAccessFactory.getInstance().createUUID(alldatas[i]);
								}

								objectMapUUID.put(fieldId, dataValidIds);
								objectMapName.put(validField.getName(), dataValidIds);
							}
						}
						else if (validField.getType().equals(Type.t_reference))
						{
							if (validField.getDataType().equals(DataType.dt_single))
							{
								UUID dataValidId = DataAccessFactory.getInstance().createUUID(colValueMap.get(colName));
								objectMapUUID.put(fieldId, dataValidId);
								objectMapName.put(validField.getName(), dataValidId);
							}
							else if (validField.getDataType().equals(DataType.dt_multiple))
							{
								String [] alldatas = colValueMap.get(colName).split(",");
								UUID[] dataValidIds = new UUID[alldatas.length];

								for (int i = 0; i < alldatas.length; i++) {
									dataValidIds[i] = DataAccessFactory.getInstance().createUUID(alldatas[i]);
								}

								objectMapUUID.put(fieldId, dataValidIds);
								objectMapName.put(validField.getName(), dataValidIds);
							}
						}
						else if (validField.getType().equals(Type.t_input))
						{
							if (validField.getDataType().equals(DataType.dt_integer))
							{
								objectMapUUID.put(fieldId, Integer.parseInt(colValueMap.get(colName)));
								objectMapName.put(validField.getName(), Integer.parseInt(colValueMap.get(colName)));
							}
							else if (validField.getDataType().equals(DataType.dt_double))
							{
								objectMapUUID.put(fieldId, Double.parseDouble(colValueMap.get(colName)));
								objectMapName.put(validField.getName(), Double.parseDouble(colValueMap.get(colName)));
							}
							else if (validField.getDataType().equals(DataType.dt_float))
							{
								objectMapUUID.put(fieldId, Float.parseFloat(colValueMap.get(colName)));
								objectMapName.put(validField.getName(), Float.parseFloat(colValueMap.get(colName)));
							}
							else if (validField.getDataType().equals(DataType.dt_long))
							{
								objectMapUUID.put(fieldId, Long.parseLong(colValueMap.get(colName)));
								objectMapName.put(validField.getName(), Long.parseLong(colValueMap.get(colName)));
							}
							else if (validField.getDataType().equals(DataType.dt_string) || validField.getDataType().equals(DataType.dt_text) || validField.getDataType().equals(DataType.dt_editor))
							{
								objectMapUUID.put(fieldId, colValueMap.get(colName));
								objectMapName.put(validField.getName(), colValueMap.get(colName));
							}
							else if(validField.getDataType().equals(DataType.dt_timestamp))
							{
								Date date = Date.valueOf(colValueMap.get(colName),validField.getTimestampFormat());
								objectMapUUID.put(fieldId, date);
								objectMapName.put(validField.getName(), date);
							}
						}
						else if (validField.getType().equals(Type.t_attachment))
						{
							String[] alldatas = colValueMap.get(colName).split(",");
							UUID[] dataValidIds = new UUID[alldatas.length];

							for (int i = 0; i < alldatas.length; i++) {
								dataValidIds[i] = DataAccessFactory.getInstance().createUUID(alldatas[i]);
							}

							objectMapUUID.put(fieldId, dataValidIds);
							objectMapName.put(validField.getName(), dataValidIds);
						}
					}
				} catch (Exception e) {
					System.out.println("data assemble error! dataid:" + data.getId().getValue() + " templateid:" + data.getTemplateId().getValue() + " fieldid:" + validField.getId().getValue() );
					e.printStackTrace();
				}
			}

			data.setObjectMapUUID(objectMapUUID);
			data.setObjectMapName(objectMapName);

			if (needLog) {
				List<ChangeLog> changeLogs = new LogAccessSessionMySQL().queryAllChangeLogs(dataId,templateId,templateFieldNameCache);
				data.setChangeLogs(changeLogs);
			}
			allDatas.add(data);
		}

		return allDatas;
	}

	/**
	 * @description:query data from db
	 * @date:2014-5-6 下午5:20:49
	 * @version:v1.0
	 * @param dataId
	 * @param templateId
	 * @return
	 */
	public Data queryData(UUID dataId,UUID templateId){
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Data data = null;
		try
		{
			String tableName = TableRuleManager.getInstance().getDataTableName(templateId);
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE id = ? and is_valid=?");
			pstm.setLong(1, Long.parseLong(dataId.getValue()));
			pstm.setString(2, "1");
			rs = pstm.executeQuery();

			List<Map<String, String>> colValueMapList = DbPoolConnection.getInstance().getDataMapFromRs(rs);

			if (colValueMapList.size() > 0) {
				List<Data> allDatas = assembleDatas(colValueMapList,true);
				if (allDatas.size() > 0) {
					data = allDatas.get(0);
				}
			}
		}catch(Exception e){
			logger.error("",e);
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return data;
	}

	/**
	 * @description:query data 
	 * @date:2014-5-6 下午5:20:58
	 * @version:v1.0
	 * @param tablesList
	 * @param queryFieldsList
	 * @param whereFieldsMap
	 * @param orderFieldsMap
	 * @param groupFieldMap
	 * @param needLog
	 * @param templateId
	 * @return
	 */
	public List<Data> queryDatas(List<String> tablesList ,List<String> queryFieldsList , Map<String, String> whereFieldsMap , Map<String, String> orderFieldsMap , Map<String, String> groupFieldMap , boolean needLog,UUID templateId){
		String sql = DbPoolConnection.getInstance().getDataQuerySQL(tablesList, queryFieldsList, whereFieldsMap, orderFieldsMap, groupFieldMap);
		return queryDatas(sql, needLog,templateId);
	}

	/**
	 * @description:query template id from data id
	 * @date:2014-5-6 下午5:21:12
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public String queryTemplateId(UUID id){
		List<String> queryFieldsList = new ArrayList<String>();
		queryFieldsList.add("templateId");
		Map<String, String> whereMap = new HashMap<String, String>();
		whereMap.put("id", id.getValue());
		String sql = DbPoolConnection.getInstance().getDataQuerySQL(TableRuleManager.getInstance().getAllDataTables(),
									queryFieldsList,
									whereMap,null,null
									);

		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
			if (rs.next()) {
				return rs.getString("templateId");
			}

		}catch(Exception e){
			logger.error("",e);
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(stat);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return "";
	}

	/**
	 * @description:remove data from db 
	 * @date:2014-5-6 下午5:22:26
	 * @version:v1.0
	 * @param ids
	 * @return
	 */
	public synchronized boolean remove(UUID[] ids)
	{
		Connection conn = null;
		Statement stat = null;
		String sql = "";
		String tableName = "";
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			stat = conn.createStatement();

			for (UUID uuid : ids) {
				UUID templateId = DataAccessFactory.getInstance().createUUID(queryTemplateId(uuid));
				tableName = TableRuleManager.getInstance().getDataTableName(templateId);
				sql = "update " + tableName + " set is_valid=0 where id = " + uuid.getValue();
				stat.execute(sql);

				tableName = TableRuleManager.getInstance().getDataLogTableName(templateId);
				sql = "update " + tableName + " set is_valid=0 where dataId = " + uuid.getValue();
				stat.execute(sql);
			}
			return true;
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeStatment(stat);
			DbPoolConnection.getInstance().closeConn(conn);
		}
	}

	private void removeIdFromDb(String dataId,String templateId) {
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			
			conn = DbPoolConnection.getInstance().getConnection();
			String tableName = TableRuleManager.getInstance().getDataTableName(DataAccessFactory.getInstance().createUUID(templateId));
			pstm = conn.prepareStatement("delete from " + tableName + " where id = ?");
			pstm.setString(1, dataId);
			pstm.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbPoolConnection.getInstance().closeAll(pstm,conn);
		}
	}
	
	/**
	 * @description:add data to db
	 * @date:2014-5-6 下午5:22:37
	 * @version:v1.0
	 * @param data
	 * @return
	 */
	public synchronized boolean add(Data data) {
		Map<String, String> dataSaveDBMap = new LinkedHashMap<String, String>();
		getDataValueMap(data, dataSaveDBMap);

		Connection conn = null;
		try {
			conn = DbPoolConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			String templateId = dataSaveDBMap.get("templateId");
			String tableName = TableRuleManager.getInstance().getDataTableName(DataAccessFactory.getInstance().createUUID(templateId));

			boolean isSuccess = insertDataToDB(dataSaveDBMap, tableName, conn);
			if (!isSuccess) {
				logger.error("数据无法存储： dataid=" + data.getId().getValue());
				removeIdFromDb(data.getId().getValue(),data.getTemplateId().getValue());
				throw new Exception("无法存储");
			}

			//存日志
			dataSaveDBMap.put("dataId", dataSaveDBMap.get("id"));
			dataSaveDBMap.remove("id");
			dataSaveDBMap.put("logcreateUser", data.getObject("logCreateUser") == null?"":data.getObject("logCreateUser").toString());
			dataSaveDBMap.put("logActionIndex", String.valueOf(data.getChangeLogs() == null ? 1 : data.getChangeLogs().length));
			dataSaveDBMap.put("logcreateTime", dataSaveDBMap.get("lastModifyTime"));
			dataSaveDBMap.put("logActionId", data.getObject("logActionId") == null?"":data.getObject("logActionId").toString());
			dataSaveDBMap.put("logActionComment", data.getObject("logActionComment") == null?"":data.getObject("logActionComment").toString());

			tableName = TableRuleManager.getInstance().getDataLogTableName(DataAccessFactory.getInstance().createUUID(templateId));

			isSuccess = insertDataToDB(dataSaveDBMap, tableName, conn);
			if (!isSuccess) {
				logger.error("日志无法存储： dataid=" + data.getId().getValue());
				throw new Exception("无法存储");
			}
			conn.commit();
			conn.setAutoCommit(true);
			logger.info("add a data!");
			return true;
		} catch (Exception e) {
			try {
				if (!conn.isClosed()) {
					conn.rollback();
					conn.setAutoCommit(true);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			logger.error(e.getMessage());
			return false;
		}finally{
			DbPoolConnection.getInstance().closeConn(conn);
		}

	}

	/**
	 * @description:modify data from db
	 * @date:2014-5-6 下午5:22:48
	 * @version:v1.0
	 * @param data
	 * @return
	 */
	public synchronized boolean modify(Data data) {
		Map<String, String> dataSaveDBMap = new LinkedHashMap<String, String>();
		try {
			getDataValueMap(data, dataSaveDBMap);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		Connection conn = null;
		try {
			conn = DbPoolConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			String templateId = dataSaveDBMap.get("templateId");
			String tableName = TableRuleManager.getInstance().getDataTableName(DataAccessFactory.getInstance().createUUID(templateId));

			boolean isSuccess = updateDataToDB(dataSaveDBMap, tableName, conn);
			if (!isSuccess) {
				logger.error("数据无法更数： dataid=" + data.getId().getValue());
				throw new Exception("无法存储");
			}

			//存日志
			dataSaveDBMap.put("dataId", dataSaveDBMap.get("id"));
			dataSaveDBMap.remove("id");
			dataSaveDBMap.put("logcreateUser", data.getObject("logCreateUser") == null?"":data.getObject("logCreateUser").toString());
			dataSaveDBMap.put("logcreateTime", dataSaveDBMap.get("lastModifyTime"));
			dataSaveDBMap.put("logActionIndex", String.valueOf(data.getChangeLogs() == null ? 1 : data.getChangeLogs().length));
			dataSaveDBMap.put("logActionId", data.getObject("logActionId") == null? "48" :data.getObject("logActionId").toString()); //48为编辑
			dataSaveDBMap.put("logActionComment", data.getObject("logActionComment") == null?"":data.getObject("logActionComment").toString());

			tableName = TableRuleManager.getInstance().getDataLogTableName(DataAccessFactory.getInstance().createUUID(templateId));

			isSuccess = insertDataToDB(dataSaveDBMap, tableName, conn);
			if (!isSuccess) {
				logger.error("日志无法存储： dataid=" + data.getId().getValue());
				throw new Exception("无法存储");
			}
			conn.commit();
			conn.setAutoCommit(true);
			return true;
		} catch (Exception e) {
			try {
				if (!conn.isClosed()) {
					conn.rollback();
					conn.setAutoCommit(true);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			logger.error("", e);
			return false;
		}finally{
			DbPoolConnection.getInstance().closeConn(conn);
		}

	}

	/**
	 * @description:get data values map
	 * @date:2014-5-6 下午5:22:58
	 * @version:v1.0
	 * @param data
	 * @param dataSaveDBMap
	 * @return
	 */
	public Map<String,String> getDataValueMap(Data data ,Map<String, String> dataSaveDBMap){

		Template template = TemplateCache.getInstance().get(data.getTemplateId());

		if(template == null){
			return null;
		}

		dataSaveDBMap.put("id", data.getId().getValue());
		dataSaveDBMap.put("templateId", template.getId().getValue());
		dataSaveDBMap.put("createUser", data.getCreateUsername());
		dataSaveDBMap.put("templateTypeId", template.getTemplateTypeId().getValue());
		dataSaveDBMap.put("title", data.getTitle());
		dataSaveDBMap.put("description", data.getDescription());
		dataSaveDBMap.put("createTime", data.getCreateTime().toString());
		dataSaveDBMap.put("lastModifyTime", data.getLastModifyTime().toString());
		dataSaveDBMap.put("assignUser", data.getAssignUsername());
		dataSaveDBMap.put("statusId", data.getStatusId().getValue());

		Set<Field> allFields = template.getFields();
		for(Field field : allFields){
			if (field == null) {
				continue;
			}
			String fieldValue = "";
			Type type = field.getType();

			DataType dataType = field.getDataType();

			if (type == Type.t_selection) {
				if (dataType == DataType.dt_single) {
					fieldValue = data.getSingleSelection(field.getId()) == null ? "" : data.getSingleSelection(field.getId()).getValue();
				}else {
					if (data.getMultiSelection(field.getId()) != null) {
						for (UUID uuid : data.getMultiSelection(field.getId())) {
							if (uuid == null) {
								continue;
							}
							fieldValue += "".equals(fieldValue) ?  uuid.getValue() : "," +  uuid.getValue();
						}
					}
				}
			}else if (type == Type.t_reference) {
				if (dataType == DataType.dt_single) {
					fieldValue = data.getSingleReference(field.getId()) == null ? null : data.getSingleReference(field.getId()).getValue();
				}else {
					if (data.getMultiReference(field.getId()) != null) {
						for (UUID uuid : data.getMultiReference(field.getId())) {
							if (uuid == null) {
								continue;
							}
							fieldValue += "".equals(fieldValue) ?  uuid.getValue() : "," +   uuid.getValue();
						}
					}
				}
			}else if (type == Type.t_attachment) {
				if (data.getAttachments(field.getId()) != null) {
					for (UUID uuid : data.getAttachments(field.getId())) {
						if (uuid == null) {
							continue;
						}
						fieldValue += "".equals(fieldValue) ? uuid.getValue() : "," +  uuid.getValue();
					}
				}

			}else if (type == Type.t_input) {

				if (dataType.equals(DataType.dt_integer))
				{
					fieldValue = data.getInteger(field.getId()) == null ? null : data.getInteger(field.getId()).toString();
				}else if (dataType.equals(DataType.dt_double))
				{
					fieldValue = data.getDouble(field.getId()) == null ? null : data.getDouble(field.getId()).toString();
				}else if (dataType.equals(DataType.dt_float))
				{
					fieldValue = data.getFloat(field.getId()) == null ? null : data.getFloat(field.getId()).toString();
				}else if (dataType.equals(DataType.dt_long))
				{
					fieldValue = data.getLong(field.getId()) == null ? null : data.getLong(field.getId()).toString();
				}else if (dataType.equals(DataType.dt_string) || dataType.equals(DataType.dt_text) || dataType.equals(DataType.dt_editor))
				{
					fieldValue = data.getString(field.getId()) == null ? null : data.getString(field.getId()).toString();
				}else if(dataType.equals(DataType.dt_timestamp))
				{
					fieldValue = data.getDate(field.getId()) == null ? null : data.getDate(field.getId()).toTimestamp().toString();
				}
			}

			String fieldColName = FieldNameCache.getInstance().getFieldName(field.getId(),template.getId());
			if (fieldColName != null && fieldColName.length() > 0) {
				if (field.getType().equals(Type.t_selection) && field.getDataType().equals(DataType.dt_single)
						&& fieldValue != null && fieldValue.equals("")) {
					dataSaveDBMap.put(fieldColName, null);
				}else {
					dataSaveDBMap.put(fieldColName, fieldValue);
				}
			}
		}

		return dataSaveDBMap;

	}

	/**
	 * @description:remove field datas when field removed
	 * @date:2014-5-6 下午5:23:14
	 * @version:v1.0
	 * @param templateId
	 * @param fieldId
	 */
	public void removeFieldData(UUID templateId, UUID fieldId) {
		if (templateId == null || fieldId == null) {
			throw new RuntimeException("templateID or fieldId is null!");
		}
		String dataTableName = TableRuleManager.getInstance().getDataTableName(templateId);
		String logTableName = TableRuleManager.getInstance().getDataLogTableName(templateId);

		String fieldColName = FieldNameCache.getInstance().getFieldName(fieldId ,templateId);
		if (fieldColName == null) {
			return;
		}
		String sql = "update " + dataTableName + " set " + fieldColName + "=? where templateId=?";

		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = DbPoolConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, null);
			pstm.setString(2, templateId.getValue());
			if (!(pstm.executeUpdate() >0))
				throw new Exception("数据库更新错误");
			
			sql = "update " + logTableName + " set " + fieldColName + "=? where templateId=?";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, null);
			pstm.setString(2, templateId.getValue());
			
			int affectRow = pstm.executeUpdate();
			conn.commit();
			if (!(affectRow >0))
				throw new Exception("数据库更新错误");
		} catch (Exception e) {
			logger.error("", e);
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}

	}

	/**
	 * @description:query is_valid=1
	 * @date:2014-5-6 下午5:23:52
	 * @version:v1.0
	 * @param sql
	 * @return
	 */
	private String getVaildDataSql(String sql){
		if (sql == null) {
			return "";
		}
		StringBuffer sqlBuffer = new StringBuffer();
		
		String orderByStr = "";
		if (sql.indexOf("order by") != -1) {
			orderByStr = sql.substring(sql.indexOf("order by"));
			sql = sql.substring(0,sql.indexOf("order by"));
		}
		
		String[] allSql = sql.split("union");
		
		for (String sqlStr : allSql) {
			sqlBuffer.append(sqlBuffer.length() > 0 ? " union " : "");
			sqlBuffer.append(sqlStr).append(sqlStr.indexOf("where") != -1 ? " and " : " where ").append(" is_valid = 1 ");
		}
		
		sqlBuffer.append(" ").append(orderByStr);
		return sqlBuffer.toString();
	}

	/**
	 * @description:query data by sql
	 * @date:2014-5-6 下午5:24:13
	 * @version:v1.0
	 * @param sql
	 * @param needLog
	 * @param templateId
	 * @return
	 */
	public List<Data> queryDatas(String sql,  boolean needLog , UUID templateId) {
		
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		List<Data> allDatas = new ArrayList<Data>();
		
		if (CynthiaUtil.isNull(sql)) {
			return allDatas;
		}
		
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			System.out.println("query sql:" + sql);
			rs = stat.executeQuery(sql);
			if (templateId != null) {
				setTemplateFieldNameCache (templateId);
			}

			List<Map<String, String>> colValueMapList = DbPoolConnection.getInstance().getDataMapFromRs(rs);

			allDatas = assembleDatas(colValueMapList , needLog);

		}catch(Exception e){
			logger.error("", e);
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(stat);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return allDatas;
	}

	/**
	 * @description:query data ids from sql
	 * @date:2014-5-6 下午5:24:29
	 * @version:v1.0
	 * @param sql
	 * @return
	 */
	public String[] queryDataIdArray(String sql) {
		sql = CynthiaUtil.cancelGroupOrder(sql);
		String[] sqlArray = sql.split("union");  //每个表单独处理，避免union组合将所有表都锁定
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		List<String> allDataIdList = new ArrayList<String>();
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			
			for (String sqlStr:sqlArray) {
				sqlStr += (sqlStr.indexOf("where") != -1 ? " and " : " where ") + " is_valid=1";
				stat = conn.createStatement();
				rs = stat.executeQuery(sqlStr);
				while (rs.next()) {
					allDataIdList.add(rs.getString("id"));
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbPoolConnection.getInstance().closeAll(rs, stat, conn);
		}
		return allDataIdList.toArray(new String[allDataIdList.size()]);
	}

	/**
	 * @description:query id and last modify time from sql
	 * @date:2014-5-6 下午5:24:45
	 * @version:v1.0
	 * @param sql
	 * @return
	 */
	public Map<String, String> queryDataIdAndLastModifyTime(String sql) {
		sql = CynthiaUtil.cancelGroupOrder(sql);
		String[] sqlArray = sql.split("union");  //每个表单独处理，避免union组合将所有表都锁定
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		Map<String, String> idAndModifyTimeMap = new HashMap<String, String>();
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			for (String sqlStr:sqlArray) {
				sqlStr += (sqlStr.indexOf("where") != -1 ? " and " : " where ") + " is_valid=1";
				stat = conn.createStatement();
				rs = stat.executeQuery(sqlStr);
				while (rs.next()) {
					idAndModifyTimeMap.put(rs.getString("id"), rs.getString("lastModifyTime"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, stat, conn);
		}
		return idAndModifyTimeMap;
	}

	/**
	 * @description:query data id and template id from sql
	 * @date:2014-5-6 下午5:24:58
	 * @version:v1.0
	 * @param sql
	 * @return
	 */
	public Map<String, String> queryDataIdAndTemplate(String sql) {
		sql = CynthiaUtil.cancelGroupOrder(sql);
		String[] sqlArray = sql.split("union");  //每个表单独处理，避免union组合将所有表都锁定
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		Map<String, String> idAndTemplateMap = new HashMap<String, String>();
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			for (String sqlStr:sqlArray) {
				sqlStr += (sqlStr.indexOf("where") != -1 ? " and " : " where ") + " is_valid=1";
				stat = conn.createStatement();
				rs = stat.executeQuery(sqlStr);
				while (rs.next()) {
					idAndTemplateMap.put(rs.getString("id"), rs.getString("templateId"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, stat, conn);
		}
		return idAndTemplateMap;
	}
	
	/**
	 * @description:query data id and field by template
	 * @date:2014-5-6 下午5:25:13
	 * @version:v1.0
	 * @param templateId
	 * @param queryField
	 * @return
	 */
	public Map<String, String> queryIdAndFieldOfTemplate(String templateId, String queryField){
		Map<String, String> fieldResultMap = new HashMap<String, String>();

		if (templateId == null ) {
			return fieldResultMap;
		}

		String tableName = TableRuleManager.getInstance().getDataTableName(DataAccessFactory.getInstance().createUUID(templateId));
		
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select id ,").append(queryField).append(" from ").append(tableName).append(" where templateId=? and is_valid=?");

		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement(sqlBuffer.toString());
			pstm.setString(1, templateId);
			pstm.setString(2, "1");
			rs = pstm.executeQuery();
			while (rs.next()) {
				fieldResultMap.put(rs.getString("id"), rs.getString(queryField));
			}

		}catch(Exception e){
			logger.error("",e);
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return fieldResultMap;
	}
	
	/**
	 * @description:query field by data ids and template
	 * @date:2014-5-6 下午5:25:33
	 * @version:v1.0
	 * @param dataIdArray
	 * @param queryField
	 * @param templateId
	 * @return
	 */
	public String[] queryFieldByIds(String[] dataIdArray , String queryField , UUID templateId) {
		
		if (dataIdArray == null || dataIdArray.length == 0) {
			return new String[0];
		}
		
		Set<String> fieldResultSet = new HashSet<String>();

		List<String> tableList = new ArrayList<String>();
		if (templateId != null) {
			tableList.add(TableRuleManager.getInstance().getDataTableName(templateId));
		}else {
			tableList.addAll(TableRuleManager.getInstance().getAllDataTables());
		}

		StringBuffer idBuffer = new StringBuffer();
		idBuffer.append(" ( ");
		for (String dataId : dataIdArray) {
			idBuffer.append(dataId).append(",");
		}
		idBuffer.deleteCharAt(idBuffer.length() -1 );
		idBuffer.append(")");


		StringBuffer sqlBuffer = new StringBuffer();
		for (String tableName : tableList) {
			if (sqlBuffer.length() > 0) {
				sqlBuffer.append(" union ");
			}
			sqlBuffer.append(" select ").append(queryField).append(" from ").append(tableName).append(" where id in ").append(idBuffer).append(" and is_valid=1");
		}

		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sqlBuffer.toString());
			while (rs.next()) {
				fieldResultSet.add(rs.getString(queryField));
			}

		}catch(Exception e){
			logger.error("",e);
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(stat);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return fieldResultSet.toArray(new String[fieldResultSet.size()]);
		
		
	}

	/**
	 * @description:query field values from data ids 
	 * @date:2014-5-6 下午5:25:51
	 * @version:v1.0
	 * @param dataIdArray
	 * @param queryField
	 * @param templateId
	 * @return
	 */
	public String[] queryFieldByIds(UUID[] dataIdArray , String queryField , UUID templateId) {

		if (dataIdArray == null || dataIdArray.length == 0) {
			return new String[0];
		}
		
		String[] dataIdStrArray = new String[dataIdArray.length];
		for (int i = 0; i < dataIdArray.length; i++) {
			dataIdStrArray[i] = dataIdArray[i].getValue();
		}
		
		return queryFieldByIds(dataIdStrArray, queryField, templateId);
	}

	/**
	 * @description:query id and field from dataIds
	 * @date:2014-5-6 下午5:26:33
	 * @version:v1.0
	 * @param dataIdArray
	 * @param fieldName
	 * @return
	 */
	public Map<String,String> queryIdAndFieldByIds(UUID[] dataIdArray , String fieldName) {

		if (dataIdArray == null || dataIdArray.length == 0) {
			return new HashMap<String, String>();
		}

		Map<String , String > fieldResultMap = new HashMap<String, String>();

		List<String> tableList = TableRuleManager.getInstance().getAllDataTables();
		StringBuffer idBuffer = new StringBuffer();
		idBuffer.append(" ( ");
		for (UUID dataId : dataIdArray) {
			idBuffer.append(dataId.getValue()).append(",");
		}
		idBuffer.deleteCharAt(idBuffer.length() -1 );
		idBuffer.append(")");

		StringBuffer sqlBuffer = new StringBuffer();
		for (String tableName : tableList) {
			if (sqlBuffer.length() > 0) {
				sqlBuffer.append(" union ");
			}
			sqlBuffer.append(" select id , ").append(fieldName).append(" from ").append(tableName).append(" where id in ").append(idBuffer).append(" and is_valid=1");
		}

		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sqlBuffer.toString());
			while(rs.next()) {
				fieldResultMap.put(rs.getString("id"), rs.getString(fieldName));
			}

		}catch(Exception e){
			logger.error("",e);
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(stat);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return fieldResultMap;
	}


	/**
	 * @description:query data exist like from db
	 * @date:2014-5-6 下午5:27:39
	 * @version:v1.0
	 * @param dataValue
	 * @param templateId
	 * @param fieldColName
	 * @return
	 */
	public boolean isDataExist(String dataValue, UUID templateId,String fieldColName) {
		boolean isExist = false;
		
		if (templateId == null) {
			return isExist;
		}
		
		String tableName = TableRuleManager.getInstance().getDataTableName(templateId);
		
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		
		try {
			conn = DbPoolConnection.getInstance().getReadConnection();
			
			pstm = conn.prepareStatement("select id from " + tableName + " where " + fieldColName + " like ? and is_valid=?" );
			pstm.setString(1, "%" + dataValue + "%");
			pstm.setString(2, "1");
			rs = pstm.executeQuery();
			isExist = rs.next();
			
		} catch (Exception e) {
			logger.error("",e);
		}finally{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
			return isExist;
		}
	}
	
	/**
	 * @description:query field values from sql
	 * @date:2014-5-6 下午5:27:59
	 * @version:v1.0
	 * @param sql
	 * @return
	 */
	public String[] queryFieldBySql(String sql) {
		Set<String> dataSet = new HashSet<String>();
		
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		
		try {
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
			
			while(rs.next()){
				dataSet.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbPoolConnection.getInstance().closeAll(rs,stat, conn);
		}
		return dataSet.toArray(new String[0]);
	}

	/** 
	 * @description:set tempate data valid
	 * @date:2014-8-7 下午5:11:02
	 * @version:v1.0
	 * @param templateId
	 * @param isValid
	 * @return
	 */
	public boolean setValidDataOfTemplate(UUID templateId, boolean isValid) {
		if(templateId == null){
			return false;
		}
		
		String tableName = TableRuleManager.getInstance().getDataTableName(templateId);
		String logTableName = TableRuleManager.getInstance().getDataLogTableName(templateId);
		
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = DbPoolConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			//更新data表
			
			pstm = conn.prepareStatement("update " + tableName + " set is_valid = ? where templateId = ?");
			pstm.setBoolean(1, isValid);
			pstm.setString(2, templateId.getValue());
			pstm.executeUpdate();
			
			//更新log表
			pstm = conn.prepareStatement("update " + logTableName + " set is_valid = ? where templateId = ?");
			pstm.setBoolean(1, isValid);
			pstm.setString(2, templateId.getValue());
			pstm.executeUpdate();
			
			conn.commit();
			conn.setAutoCommit(true);
			return true;
		} catch (Exception e) {
			try {
				if (!conn.isClosed()) {
					conn.rollback();
					conn.setAutoCommit(true);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return false;
		} finally{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
	}
	
	public List<Data> queryDataByDataIds(String[] dataId,boolean needLog,UUID templateId){
		Set<String> tableSet = new HashSet<String>();
		if (templateId != null) {
			tableSet.add(TableRuleManager.getInstance().getDataTableName(templateId));
		}else {
			tableSet.addAll(TableRuleManager.getInstance().getAllDataTables());
		}
		
		String dataIds = CommonUtil.arrayToStr(dataId);
		
		StringBuffer sqlBuffer = new StringBuffer();
		for (String dataTable : tableSet) {
			sqlBuffer.append(sqlBuffer.length() > 0 ? " union " : "");
			sqlBuffer.append(" select id , templateId, createUser,templateTypeId,title,createTime,lastModifyTime,assignUser,statusId from " + dataTable + " where is_valid = 1 and id in (" + dataIds + ") ");
		}
		
		return queryDatas(sqlBuffer.toString(), needLog, templateId);
	}

	/**
	 * @description:get a new uuid
	 * @date:2014-5-6 下午6:05:08
	 * @version:v1.0
	 * @param templateId 表单id
	 * @return
	 */
	public synchronized String createUUID(String templateId) {
		Connection conn = null;
		PreparedStatement ptmt = null;
		ResultSet rs = null;
		try {
			conn = DbPoolConnection.getInstance().getConnection();
			ptmt = conn.prepareStatement("insert into data (templateId) values(?)",Statement.RETURN_GENERATED_KEYS);
			ptmt.setString(1, templateId);
			ptmt.execute();
			rs = ptmt.getGeneratedKeys();
			if (rs.next()) {
				return String.valueOf(rs.getInt(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(ptmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return "";
	}
}
