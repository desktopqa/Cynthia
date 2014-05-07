package com.sogou.qadev.service.cynthia.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sogou.qadev.service.cynthia.bean.ChangeLog;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;
import com.sogou.qadev.service.cynthia.service.LogManager;
import com.sogou.qadev.service.cynthia.service.TableRuleManager;

/**
 * @description:log db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:44:49
 * @version:v1.0
 */
public class LogAccessSessionMySQL {

	private static Logger logger = Logger.getLogger(LogAccessSessionMySQL.class.getName());
	
	public LogAccessSessionMySQL()
	{
	}

	/**
	 * @description:insert data log into db
	 * @date:2014-5-6 下午5:45:02
	 * @version:v1.0
	 * @param fieldValueMap
	 * @param tableName
	 * @param conn
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public synchronized boolean insertLogToDB(Map<String, String> fieldValueMap,String tableName,Connection conn) throws IOException, SQLException{
		
		if (tableName == null || conn ==null) {
			return false;
		}
		
		boolean result = true;
		PreparedStatement pStat = null;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("insert ignore into ").append(tableName).append(" (");
		
		Iterator<Map.Entry<String, String>> iterator = fieldValueMap.entrySet().iterator();  //去掉空列表
		while (iterator.hasNext()) {
			Map.Entry<String,String> entry = iterator.next();
			if (entry.getKey().equals("")) {
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
			pStat.executeUpdate();
			
		} catch (Exception e) {
			logger.error("",e);
			result = false;
		}finally{
			DbPoolConnection.getInstance().closeStatment(pStat);
		}
		return result;
	}
	
	/**
	 * @description:query fields from data log by templatetype
	 * @date:2014-5-6 下午5:45:17
	 * @version:v1.0
	 * @param templateTypeId
	 * @param queryField
	 * @return
	 */
	public String[] queryField(UUID templateTypeId , String queryField){
		List<String> queryFieldsList = new ArrayList<String>();
		queryFieldsList.add(queryField);
		
		Map<String, String> whereFieldsMap = new HashMap<String, String>();
		whereFieldsMap.put("templateTypeId", templateTypeId.getValue());
		List<String> tablesList = TableRuleManager.getInstance().getAllDataLogTables();
		
		
		String sql = DbPoolConnection.getInstance().getDataQuerySQL(tablesList, queryFieldsList, whereFieldsMap, null, null);
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		Set<String> result = new HashSet<String>();
		try {
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				String tmp = rs.getString(queryField);
				if (tmp != null && tmp.length() > 0) {
					result.add(tmp);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}finally{
			DbPoolConnection.getInstance().closeAll(rs, stat, conn);
		}
		return result.toArray(new String[result.size()]);
		
	}
	
	/**
	 * @description:query all change logs from db by data id
	 * @date:2014-5-6 下午5:45:48
	 * @version:v1.0
	 * @param dataId
	 * @param templateId
	 * @param templateFieldNameCache
	 * @return
	 */
	public  List<ChangeLog> queryAllChangeLogs(UUID dataId,UUID templateId,Map<String, String> templateFieldNameCache){
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List<ChangeLog> allChangeLogs = new ArrayList<ChangeLog>();
		
		try
		{
			String tableName = TableRuleManager.getInstance().getDataLogTableName(templateId);
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE dataId = ? and templateId = ? ORDER BY logcreateTime ASC");
			pstm.setLong(1, Long.parseLong(dataId.getValue()));
			pstm.setLong(2, Long.parseLong(templateId.getValue()));
			rs = pstm.executeQuery();
			
			List<Map<String, String>> colValueMapList = DbPoolConnection.getInstance().getDataMapFromRs(rs);
			
			for(int i = 0 ; i < colValueMapList.size() ; i ++){
				if (i == 0) {  //新建
					allChangeLogs.add(LogManager.getInstance().getChangeLog(colValueMapList.get(i), null,templateFieldNameCache));
				}else {
					allChangeLogs.add(LogManager.getInstance().getChangeLog(colValueMapList.get(i), colValueMapList.get(i-1),templateFieldNameCache));
				}
			}
			

		}catch(Exception e){
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return allChangeLogs;
	}
	
	/**
	 * @description:update log comment of data
	 * @date:2014-5-6 下午5:46:06
	 * @version:v1.0
	 * @param dataId
	 * @param templateId
	 * @param dataIndex
	 * @param logActionComment
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public synchronized boolean updateLogComment(UUID dataId, UUID templateId, int dataIndex , String logActionComment) throws IOException, SQLException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		if (templateId == null || dataId == null) {
			return false;
		}
		
		try {
			conn = DbPoolConnection.getInstance().getConnection();
			String logTableName = TableRuleManager.getInstance().getDataLogTableName(templateId);
			pstmt = conn.prepareStatement("update " + logTableName + " set logActionComment=? where dataId = ? and logActionIndex = ?");
			
			pstmt.setString(1, logActionComment);
			pstmt.setString(2, dataId.getValue());
			pstmt.setInt(3, dataIndex);
			return pstmt.executeUpdate() > 0;
			
		} catch (Exception e) {
			logger.error("",e);
			return false;
		}finally{
			DbPoolConnection.getInstance().closeAll(pstmt,conn);
		}
	}

}
