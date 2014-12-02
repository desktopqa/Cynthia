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
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.sogou.qadev.service.cynthia.service.DbPoolConnection;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;

/**
 * @ClassName : FlowAccessSessionMySQL
 * @Description : 
 * @author : liming
 * @date 2013-8-26
 */
public class FieldNameAccessSessionMySQL {
	
	
	/**
	 * @description query a flow
	 * @author liming
	 * @param flowId
	 * @date 2014-8-26
	 * */
	public String queryFieldColNameById(String fieldId , String templateId)
	{
		String fieldColName = "";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs =  null;
		try {
			conn = DbPoolConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select fieldColName from field_name_map where fieldId = " + fieldId + " and templateId = " + templateId );
			while (rs.next()) {
				fieldColName = rs.getString("fieldColName");
				break;
			}
		} catch (Exception e) {
		}finally{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(stmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return fieldColName;
	}
	
	/**
	 * @function：query all field id,database name by template
	 * @modifyTime：2013-9-5 下午4:35:30
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param templateId
	 * @return
	 */
	public Map<String, String> queryTemplateFieldMap(String templateId){
		
		Map<String,String> allFieldColNames = new HashMap<String, String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "select fieldId,fieldColName from field_name_map where templateId = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, templateId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				allFieldColNames.put(rs.getString("fieldColName"), rs.getString("fieldId"));  //通过fieldId 可找到fieldColName
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return allFieldColNames;
	}
	
	/**
	 * @description:TODO
	 * @date:2014-5-6 下午5:30:58
	 * @version:v1.0
	 * @return
	 */
	public Map<String,String> queryCacheAllFieldColName()
	{
		Map<String,String> allFieldColNames = new HashMap<String, String>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "select * from field_name_map";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				allFieldColNames.put(rs.getString("templateId") + "|" + rs.getString("fieldId"), rs.getString("fieldColName"));  //通过fieldId 可找到fieldColName
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return allFieldColNames;
	}
	
	/**
	 * @description:query single template field colname 
	 * @date:2014-5-6 下午5:31:53
	 * @version:v1.0
	 * @return
	 */
	public Map<String, String> queryCacheSingleFieldIds()
	{
		Map<String, String> allMap = new HashMap<String,String>();
	
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "select * from field_name_map";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				String templateId = rs.getString("templateId");
				
				allMap.put(templateId+"|"+rs.getString("fieldColName"), rs.getString("fieldId"));
				
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return allMap;
	}
	
	/**
	 * @description:query all fieldids
	 * @date:2014-5-6 下午5:31:39
	 * @version:v1.0
	 * @return
	 */
	public Map<String,Map<String, String>> queryCacheAllFieldIds()
	{
		Map<String, Map<String, String>> allTemplateIdMap = new HashMap<String, Map<String,String>>();
	
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "select * from field_name_map";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				String templateId = rs.getString("templateId");
				
				Map<String, String> templateCacheMap = null; 
				
				if (allTemplateIdMap.get(ConfigUtil.templateFieldCacheprefix + templateId) == null) {
					templateCacheMap = new HashMap<String, String>();
					allTemplateIdMap.put(ConfigUtil.templateFieldCacheprefix + templateId, templateCacheMap);
				}else {
					templateCacheMap = allTemplateIdMap.get(ConfigUtil.templateFieldCacheprefix + templateId);
				}
				
				templateCacheMap.put(rs.getString("fieldColName"), rs.getString("fieldId"));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return allTemplateIdMap;
	}
	
	
	/**
	 * @description add a flow
	 * @author liming
	 * @date 2013-08-26
	 * */
	public boolean addFieldColName(String templateId,String fieldColName,String fieldId,String fieldType)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			//判断数据库是否己存在
			String sql = "select * from field_name_map where templateId = ? and fieldColName = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, templateId);
			pstmt.setString(2, fieldId);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				if ( rs.getString("templateId") != null && !rs.getString("templateId").equals("")) {
					return false;
				}
			}
			
			sql = "insert into field_name_map(templateId,fieldColName,fieldId,fieldType) values(?,?,?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, templateId);
			pstmt.setString(2, fieldColName);
			pstmt.setString(3, fieldId);
			pstmt.setString(4, fieldType);
			boolean isSuccess =  pstmt.executeUpdate() >0;
			return isSuccess;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		
	}
	
	/**
	 * @description update a flow
	 * @author liming
	 * @date 2013-08-26
	 * */
	public boolean updateFieldColName(String fieldId,String fieldColName)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "update field_name_map fieldColName  = ? where fieldId = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, fieldColName);
			pstmt.setString(2, fieldId);
			return pstmt.execute();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return false;
	}
	
	
	/**
	 * @description delete a flow
	 * @author liming
	 * @date 2013-08-26
	 * */
	public boolean removeFieldColNameById(String fieldId , String templateId)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "delete from field_name_map where fieldId = ? and templateId = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, fieldId);
			pstmt.setString(2, templateId);
			
			return pstmt.executeUpdate() > 0;
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return false;
	}
	
	
	/**
	 * @function：query field id by field colname and template id
	 * @modifyTime：2013-9-5 上午11:43:23
	 * @email: liming@sogou-inc.com
	 * @param fieldColName
	 * @param templateId
	 * @return
	 */
	public String queryFieldIdByFieldColName(String fieldColName, String templateId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "select fieldId from field_name_map where fieldColName = ? and templateId = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, fieldColName);
			pstmt.setString(2, templateId);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("fieldId");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		return "";
	}


}
