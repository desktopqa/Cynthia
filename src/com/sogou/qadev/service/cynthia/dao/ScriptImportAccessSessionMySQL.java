/**
* @Title: ScriptImportAccessSessionMySQL.java
* @Package : com.sogou.qadev.service.cynthia.mysql
* @Description : join script import message
* @author : liming	
* @date : 2013-8-26
* @version : v1.0
*/
package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sogou.qadev.service.cynthia.service.DbPoolConnection;

/**
 * @description:script import info db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:51:51
 * @version:v1.0
 */
public class ScriptImportAccessSessionMySQL {

	/**
	 * @description:modify script import 
	 * @date:2014-5-6 下午5:52:25
	 * @version:v1.0
	 * @param importStr
	 * @return
	 */
	public boolean modify(String importStr)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "update from script_import set import_str = ?";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, importStr);
			return pstm.executeUpdate() > 0;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}
	}
	
	/**
	 * @description:query import info
	 * @date:2014-5-6 下午5:52:13
	 * @version:v1.0
	 * @return
	 */
	public String query()
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		String importStr = "";
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			
			String sql = "select * from script_import";
			pstm = conn.prepareStatement(sql);
			
			rs = pstm.executeQuery();
			while(rs.next())
			{
				importStr = rs.getString("import_str");
				break;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return importStr;
	}

}
