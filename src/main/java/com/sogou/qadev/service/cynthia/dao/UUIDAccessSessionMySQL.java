/**
* @Title: FlowAccessSessionMySQL.java
* @Package : com.sogou.qadev.service.cynthia.mysql
* @Description : 
* @author : liming
* @date : 2013-8-26
* @version : v1.0
*/
package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Statement;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;

/**
 * @ClassName : FlowAccessSessionMySQL
 * @Description : 
 * @author : liming
 * @date 2013-8-26
 */
public class UUIDAccessSessionMySQL {
		private static Logger logger = Logger.getLogger(UUIDAccessSessionMySQL.class.getName());
	
	/**
	 * @description:get a new uuid by type
	 * @date:2014-5-6 下午6:05:08
	 * @version:v1.0
	 * @param type
	 * @return
	 */
	public synchronized String add(String type){
		Connection conn = null;
		PreparedStatement ptmt = null;
		ResultSet rs = null;
		try {
			conn = DbPoolConnection.getInstance().getConnection();
			ptmt = conn.prepareStatement("insert into uuid (type) values(?)",Statement.RETURN_GENERATED_KEYS);
			ptmt.setString(1, type);
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
