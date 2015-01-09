package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.bean.impl.UserInfoImpl;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;
/**
 * @description:TODO
 * @author:backright db processor
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:14:18
 * @version:v1.0
 */
public class BackRightAccessSessionMySQL {
	
	/**
	 * @description:get all back right users
	 * @date:2014-5-6 下午5:14:39
	 * @version:v1.0
	 * @return
	 */
	public List<UserInfo> getBackRightUsers() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<UserInfo> allUsers = new ArrayList<UserInfo>();
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "SELECT A.user_name, B.id,B.nick_name from event_user as A JOIN user_info as B on A.user_name = B.user_name order by A.user_name asc";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				UserInfo ui = new UserInfoImpl();
				ui.setId(rs.getInt("id"));
				ui.setUserName(rs.getString("user_name"));
				ui.setNickName(rs.getString("nick_name"));
				allUsers.add(ui);
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
		return allUsers;
	}
	
	/**
	 * @description:add back right user
	 * @date:2014-5-6 下午5:14:55
	 * @version:v1.0
	 * @param userMail
	 * @return
	 */
	public boolean addBackRightUser(String userMail) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "insert ignore into event_user(user_name,event_id) values(?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userMail);
			pstmt.setInt(2, 2);
			boolean isSuccess =  pstmt.executeUpdate() >0;
			return isSuccess;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
	}
	
	/**
	 * @description:delete back right user
	 * @date:2014-5-6 下午5:15:14
	 * @version:v1.0
	 * @param userMail
	 * @return
	 */
	public boolean delBackRightUser(String userMail) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "delete from event_user where user_name = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userMail);
			return pstmt.executeUpdate() > 0;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
	}

	
	public Set<String> getTemplateRightUserMails(String templateId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Set<String> allUsers = new HashSet<String>();
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select admin_user from template_admin_user where template_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, templateId);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				allUsers.add(rs.getString("admin_user"));
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
		return allUsers;
	}
	

	/**
	 * @description:get template right users
	 * @date:2014-5-6 下午5:15:26
	 * @version:v1.0
	 * @param templateId
	 * @return
	 */
	public List<UserInfo> getTemplateRightUser(String templateId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<UserInfo> allUsers = new ArrayList<UserInfo>();
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "SELECT A.admin_user, B.id,B.nick_name from template_admin_user as A left JOIN user_info as B on A.admin_user = B.user_name where A.template_id = ? order by A.admin_user asc";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, templateId);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				UserInfo ui = new UserInfoImpl();
				if (rs.getString("admin_user") != null && rs.getString("admin_user").equals("*")) {
					ui.setUserName("*");
					ui.setNickName("所有人");
				}else {
					ui.setId(rs.getInt("id"));
					ui.setUserName(rs.getString("admin_user"));
					ui.setNickName(rs.getString("nick_name"));
				}
				allUsers.add(ui);
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
		return allUsers;
	}

	/**
	 * @description:delete user template right
	 * @date:2014-5-6 下午5:15:37
	 * @version:v1.0
	 * @param templateId
	 * @param userMail
	 * @return
	 */
	public boolean delUserTemplateRight(String templateId, String userMail) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "delete from template_admin_user where template_id = ? and admin_user=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, templateId);
			pstmt.setString(2, userMail);
			return pstmt.executeUpdate() > 0;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
	}
	
	/**
	 * @description:add user template right
	 * @date:2014-5-6 下午5:15:55
	 * @version:v1.0
	 * @param templateIds
	 * @param userMail
	 * @return
	 */
	public boolean addUserTemplateRight(String[] templateIds, String userMail) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			String sql = "insert ignore into template_admin_user(template_id,admin_user) values(?,?)";
			pstmt = conn.prepareStatement(sql);
			for (String templateId : templateIds) {
				pstmt.setString(1, templateId);
				pstmt.setString(2, userMail);
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();   
			conn.commit();
			conn.setAutoCommit(true);//提交完成后回复现场将Auto commit,还原为true,   
			return true;
		}catch(Exception e)
		{
			e.printStackTrace();
			try {
				if (!conn.isClosed()) {
					conn.rollback();
					conn.setAutoCommit(true);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
	}

	/**
	 * @description:query all template rights of user
	 * @date:2014-5-6 下午5:16:09
	 * @version:v1.0
	 * @param userMail
	 * @return:template id name map
	 */
	public Map<String, String> queryUserTemplateRights(String userMail) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Map<String, String> templateRightMap = new HashMap<String, String>();
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "SELECT A.admin_user, B.id,B.name from template_admin_user as A left JOIN template as B on A.template_id = B.id where A.admin_user = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userMail);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				templateRightMap.put(rs.getString("id"), rs.getString("name"));
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
		return templateRightMap;
	}

	/**
	 * @description:set system setting(json)
	 * @date:2014-5-6 下午5:16:33
	 * @version:v1.0
	 * @param systemJson
	 * @return
	 */
	public boolean setSystemOption(String systemJson) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "update system_set set value=? where set_name='system'";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, systemJson);
			return pstmt.executeUpdate() > 0;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
	}

	/**
	 * @description:get system setting
	 * @date:2014-5-6 下午5:16:46
	 * @version:v1.0
	 * @param userMail
	 * @return
	 */
	public String getSystemOption(String userMail) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String setJson = "";
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "select value from system_set where set_name = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userMail);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				setJson = rs.getString("value");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstmt, conn);
		}
		return setJson;
	}

	/**
	 * @description:get template user right
	 * @date:2014-5-6 下午5:16:56
	 * @version:v1.0
	 * @param templateId
	 * @param userMail
	 * @return
	 */
	public boolean deltemplateUserRight(String templateId, String userMail) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "delete from template_admin_user where template_id = ? and admin_user=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, templateId);
			pstmt.setString(2, userMail);
			return pstmt.executeUpdate() > 0;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
	}

	/**
	 * @description:add template right user
	 * @date:2014-5-6 下午5:17:08
	 * @version:v1.0
	 * @param templateId
	 * @param users
	 * @return
	 */
	public boolean addtemplateUserRight(String templateId, String[] users) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		if(users == null || users.length == 0)
			return false;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			String sql = "insert ignore into template_admin_user(template_id,admin_user) values(?,?)";
			pstmt = conn.prepareStatement(sql);
			for (String user : users) {
				pstmt.setString(1, templateId);
				pstmt.setString(2, user);
				pstmt.addBatch();
			}
			
			pstmt.executeBatch();   
			conn.commit();
			conn.setAutoCommit(true);//提交完成后回复现场将Auto commit,还原为true,   
			return true;
		}catch(Exception e)
		{
			e.printStackTrace();
			try {
				if (!conn.isClosed()) {
					conn.rollback();
					conn.setAutoCommit(true);
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeStatment(pstmt);
			DbPoolConnection.getInstance().closeConn(conn);
		}
	}
}
