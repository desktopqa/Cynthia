package com.sogou.qadev.service.cynthia.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import bsh.This;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;

/**
 * @description:database pool processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午12:01:29
 * @version:v1.0
 */
public class DbPoolConnection {
	private static DruidDataSource ddsWrite = null;   //写数据库连接池
	private static DruidDataSource ddsread = null;    //读数据库连接池
	private static Logger logger = Logger.getLogger(DbPoolConnection.class.getName());

	static {
		Properties properties = null;
		properties = ConfigManager.getDataBaseProperty();
		try {
			ddsread = ddsWrite = (DruidDataSource) DruidDataSourceFactory
					.createDataSource(properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private DbPoolConnection() {}

	private static class SingletonHolder{
		private static DbPoolConnection databasePool = new DbPoolConnection();
	}

	public static DbPoolConnection getInstance() {
		return SingletonHolder.databasePool;
	}

	
	/**
	 * @Title: executeUpdateSql
	 * @Description: 执行更新数据库操作
	 * @return
	 * @return: boolean
	 */
	public boolean executeUpdateSql(String sql){
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = getConnection();
			pstm = conn.prepareStatement(sql);
			pstm.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			closeAll(pstm,conn);
		}
	}

	/**
	 * @description:return write connection
	 * @date:2014-5-6 下午12:01:54
	 * @version:v1.0
	 * @return
	 */
	public DruidPooledConnection getConnection() {
		try
		{
			return ddsWrite.getConnection();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @description:return read connection
	 * @date:2014-5-6 下午12:02:15
	 * @version:v1.0
	 * @return
	 */
	public DruidPooledConnection getReadConnection(){
		try
		{
			return ddsread.getConnection();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @description:close resultset,statement,connection
	 * @date:2014-5-6 下午12:02:25
	 * @version:v1.0
	 * @param rs
	 * @param stat
	 * @param conn
	 */
	public void closeAll(ResultSet rs , Statement stat, Connection conn){
		closeResultSet(rs);
		closeStatment(stat);
		closeConn(conn);
	}

	/**
	 * @description:close statement,connection
	 * @date:2014-5-6 下午12:02:46
	 * @version:v1.0
	 * @param stat
	 * @param conn
	 */
	public void closeAll(Statement stat, Connection conn){
		closeStatment(stat);
		closeConn(conn);
	}

	/**
	 * @description:close connection
	 * @date:2014-5-6 下午12:03:01
	 * @version:v1.0
	 * @param conn
	 */
	public void closeConn(Connection conn){
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				conn = null;
			}
		}
	}

	/**
	 * @description:close statement
	 * @date:2014-5-6 下午12:03:10
	 * @version:v1.0
	 * @param stat
	 */
	public void closeStatment(Statement stat){
		if (stat != null) {
			try {
				stat.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				stat = null;
			}
		}
	}

	/**
	 * @description:close resultset
	 * @date:2014-5-6 下午12:03:21
	 * @version:v1.0
	 * @param rs
	 */
	public void closeResultSet(ResultSet rs){
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				rs = null;
			}
		}
	}

	/**
	 * @description:get sql query count
	 * @date:2014-5-6 下午12:03:38
	 * @version:v1.0
	 * @param sql
	 * @return
	 */
	public int getCountOfSQL(String sql){
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int total = 0;
		try {
			conn = getReadConnection();
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			while (rs.next()) {
				total += rs.getInt(1);
			}
			return total;
		} catch (Exception e) {
			System.out.println("error sql :" + sql);
			return 0;
		}finally{
			closeAll(rs,pstm, conn);
		}
	}

	/**
	 * @function：返回任务搜索总结果数
	 * @modifyTime：2013-12-17 下午5:02:49
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param searchSql
	 * @return
	 */
	public int getSearchResultCount(String searchSql){
		if (CynthiaUtil.isNull(searchSql)) {
			return 0;
		}
		
		searchSql = searchSql.trim();
		searchSql = CynthiaUtil.cancelGroupOrder(searchSql);
		int count = 0;
		
		if (searchSql.indexOf("union") != -1 ) {
			String[] allSql = searchSql.split("union");
			for (String sql : allSql) {
				sql = sql.trim();
				String realSql = "select count(" + sql.substring(6, sql.indexOf("as id") == -1 ? sql.indexOf(","):sql.indexOf("as id")) + " ) " + sql.substring(sql.indexOf("from"));
				count += getCountOfSQL(realSql);
			}
		}else {
			String realSql = "select count(" + searchSql.substring(6, searchSql.indexOf("as id") == -1 ? searchSql.indexOf(","):searchSql.indexOf("as id")) + " ) " + searchSql.substring(searchSql.indexOf("from"));
			count += getCountOfSQL(realSql);
		}
		
		return count ;
	}
	
	/**
	 * @description:return resultset list of sql
	 * @date:2014-5-6 下午12:04:00
	 * @version:v1.0
	 * @param sql
	 * @return
	 */
	public List<Map<String, String>> getResultSetListBySql(String sql){
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;

		try {
			conn = this.getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
			return getDataMapFromRs(rs);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Map<String,String>>();
		}finally{
			closeAll(rs, stat, conn);
		}
	}
	
	/**
	 * @function：get data query sql
	 * @modifyTime：2013-9-6 下午2:11:33
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param tablesList:all tables name query from
	 * @param queryFieldsList:all query fields
	 * @param whereFieldsMap:where condition map
	 * @param orderFieldsMap:order condition map
	 * @param groupFieldMap:group conditionmap
	 * @return
	 */
	public String getDataQuerySQL(List<String> tablesList ,List<String> queryFieldsList , Map<String, String> whereFieldsMap , Map<String, String> orderFieldsMap , Map<String, String> groupFieldMap){

		if (tablesList.size() == 0) {
			return "";
		}

		StringBuffer queryFields = new StringBuffer();   //查询字段拼装
		if (queryFieldsList == null || queryFieldsList.size() == 0) {
			queryFields.append(" * ");
		}else {
			for (String fieldName : queryFieldsList) {
				if (fieldName != null && fieldName.length() >0) {
					queryFields.append(queryFields.length() > 0 ? " , " + fieldName : fieldName);
				}
			}
		}

		StringBuffer whereFields = null;    //where条件字段拼装
		if (whereFieldsMap != null && whereFieldsMap.keySet().size() > 0) {
			whereFields = new StringBuffer();
			for (String fieldName : whereFieldsMap.keySet()) {
				if (fieldName != null && fieldName.length() >0) {
					whereFields.append(whereFields.length() > 0 ? " and " + fieldName + " = " + whereFieldsMap.get(fieldName) : fieldName + " = " + whereFieldsMap.get(fieldName));
				}
			}
		}


		StringBuffer orderFields = null;   //order排序条件字段拼装
		if (orderFieldsMap != null && orderFieldsMap.keySet().size() > 0) {
			orderFields = new StringBuffer();
			for (String fieldName : orderFieldsMap.keySet()) {
				if (fieldName != null && fieldName.length() >0) {
					orderFields.append(" order by " + fieldName + orderFieldsMap.get(fieldName) == null ? "" : orderFieldsMap.get(fieldName) );
					break; //默认只按一列排序
				}
			}
		}

		StringBuffer groupFields = null;   //order排序条件字段拼装
		if (groupFieldMap != null && groupFieldMap.keySet().size() > 0) {
			groupFields = new StringBuffer();
			for (String fieldName : groupFieldMap.keySet()) {
				if (fieldName != null && fieldName.length() >0) {
					groupFields.append(" group by " + fieldName + groupFieldMap.get(fieldName) == null ? "" : groupFieldMap.get(fieldName) );
					break; //默认只按一列分组
				}
			}
		}


		StringBuffer sqlBuffer = new StringBuffer();
		for (String tableNames : tablesList) {
			if (sqlBuffer.length() > 0) {
				sqlBuffer.append(" union ");
			}
			sqlBuffer.append("select ").append(queryFields.toString()).append(" from ")
					 .append(tableNames).append(" where is_valid=1" )
					 .append(whereFields == null ? "" : " and " + whereFields.toString());

		}
		sqlBuffer.append(orderFields == null ? "" : orderFields.toString());
		sqlBuffer.append(groupFields == null ? "" : groupFields.toString());
		return sqlBuffer.toString();
	}

	/**
	 * @function：根据查询结果集，返回字段名与值Map集合
	 * @modifyTime：2013-9-5 下午12:01:10
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param rs
	 * @return
	 */
	public List<Map<String,String>> getDataMapFromRs(ResultSet rs){
		List<Map<String,String>> allDataList = new ArrayList<Map<String,String>>();

		ResultSetMetaData rsMeta = null;
		try {
			rsMeta = rs.getMetaData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (rsMeta != null && rsMeta.getColumnCount() > 0) {
				while (rs.next()) {

					Map<String, String> oneMap = new HashMap<String, String>();

					for(int i = 0; i < rsMeta.getColumnCount(); i++){
						String columnName = rsMeta.getColumnName(i+1);
						oneMap.put(columnName, rs.getString(columnName));
					}

					allDataList.add(oneMap);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return allDataList;
	}

	/**
	 * @description:init database pool from file
	 * @date:2014-5-6 下午12:05:15
	 * @version:v1.0
	 * @param fullFile
	 * @return
	 */
	public static Properties loadPropertyFile(String fullFile) {
		String webRootPath = null;
		if (null == fullFile || fullFile.equals(""))
			throw new IllegalArgumentException(
					"Properties file path can not be null : " + fullFile);
		webRootPath = DbPoolConnection.class.getClassLoader().getResource("")
				.getPath();
		webRootPath = new File(webRootPath).getPath();
		InputStream inputStream = null;
		Properties p = null;
		try {
			inputStream = new FileInputStream(new File(webRootPath
					+ File.separator + fullFile));
			p = new Properties();
			p.load(inputStream);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Properties file not found: "
					+ fullFile);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Properties file can not be loading: " + fullFile);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return p;
	}


	/**
	 * @function：query field count result
	 * @modifyTime：2014-1-9 下午3:29:23
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param sql
	 * @return
	 */
	public Map<String, String> getCountMap(String sql) {
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		Map<String, String> dbMap = new HashMap<String, String>();

		try {
			conn = this.getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
			while (rs.next()) {
				dbMap.put(rs.getString(1), rs.getString(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			closeAll(rs, stat, conn);
		}
		return dbMap;
	}
}