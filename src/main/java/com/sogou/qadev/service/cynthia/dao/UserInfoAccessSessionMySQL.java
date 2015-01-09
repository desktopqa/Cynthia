/**
 * 用户操作类
 */
package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.bean.UserInfo.UserRole;
import com.sogou.qadev.service.cynthia.bean.UserInfo.UserStat;
import com.sogou.qadev.service.cynthia.bean.impl.UserInfoImpl;
import com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;

/**
 * @description:user db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午6:03:09
 * @version:v1.0
 */
public class UserInfoAccessSessionMySQL {

	public UserInfoAccessSessionMySQL() {

	}

	/**
	 * @description:add user
	 * @date:2014-5-6 下午6:03:18
	 * @version:v1.0
	 * @param userInfo
	 * @return
	 */
	public boolean addUserInfo(UserInfo userInfo)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "insert ignore into user_info (user_name,nick_name,user_role,user_stat,password,create_time,pic_id) values (?,?,?,?,?,?,?)";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, userInfo.getUserName());
			pstm.setString(2, userInfo.getNickName());
			pstm.setString(3, userInfo.getUserRole().toString());
			pstm.setString(4, userInfo.getUserStat().toString());
			pstm.setString(5, userInfo.getUserPassword());
			pstm.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			pstm.setString(7, userInfo.getPicId());
			return pstm.executeUpdate() > 0;
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
		return false;
	}

	/**
	 * @description:update user info
	 * @date:2014-5-6 下午6:03:28
	 * @version:v1.0
	 * @param userInfo
	 * @return
	 */
	public boolean updateUserInfo(UserInfo userInfo)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "update user_info set user_name=?,nick_name=?,user_role=?,user_stat=?, password = ?,last_login_time = ? ,pic_id=?  where id=?";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, userInfo.getUserName());
			pstm.setString(2, userInfo.getNickName());
			pstm.setString(3, userInfo.getUserRole().toString());
			pstm.setString(4, userInfo.getUserStat().toString());
			pstm.setString(5, userInfo.getUserPassword());
			pstm.setTimestamp(6, userInfo.getLastLoginTime());
			pstm.setString(7, userInfo.getPicId());
			pstm.setInt(8, userInfo.getId());
			return pstm.executeUpdate()>0;
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
		return false;
	}

	/**
	 * @description:query user by user id
	 * @date:2014-5-6 下午6:03:36
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public UserInfo queryUserInfoById(int id)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select * from user_info where id=?";
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, id);
			rs = pstm.executeQuery();
			if(rs.next())
			{
				UserInfo userInfo = new UserInfoImpl();
				userInfo.setId(rs.getInt("id"));
				userInfo.setCreateTime(rs.getTimestamp("create_time"));
				userInfo.setLastLoginTime(rs.getTimestamp("last_login_time"));
				userInfo.setNickName(rs.getString("nick_name"));
				userInfo.setUserName(rs.getString("user_name"));
				userInfo.setUserRole(UserRole.valueOf(rs.getString("user_role")));
				userInfo.setUserStat(UserStat.valueOf(rs.getString("user_stat")));
				userInfo.setPicId(rs.getString("pic_id"));
				if (CynthiaUtil.isNull(userInfo.getPicId())) {
					userInfo.setPicUrl(ConfigUtil.getCynthiaWebRoot() + "attachment/download.jsp?method=download&id=" + userInfo.getPicId());
				}
				return userInfo;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return null;
	}

	/**
	 * @description:query user by user mail
	 * @date:2014-5-6 下午6:03:45
	 * @version:v1.0
	 * @param userName
	 * @return
	 */
	public UserInfo queryUserInfoByUserName(String userName)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select * from user_info where BINARY user_name=BINARY ?";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, userName);
			rs = pstm.executeQuery();
			if(rs.next())
			{
				UserInfo userInfo = new UserInfoImpl();
				userInfo.setId(rs.getInt("id"));
				userInfo.setUserPassword(rs.getString("password"));
				userInfo.setCreateTime(rs.getTimestamp("create_time"));
				userInfo.setLastLoginTime(rs.getTimestamp("last_login_time"));
				userInfo.setNickName(rs.getString("nick_name"));
				userInfo.setUserName(rs.getString("user_name"));
				userInfo.setUserRole(UserRole.valueOf(rs.getString("user_role")));
				userInfo.setUserStat(UserStat.valueOf(rs.getString("user_stat")));
				userInfo.setPicId(rs.getString("pic_id"));
				if (!CynthiaUtil.isNull(userInfo.getPicId())) {
					userInfo.setPicUrl(ConfigUtil.getCynthiaWebRoot() + "attachment/download.jsp?method=download&id=" + userInfo.getPicId());
				}
				return userInfo;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return null;
	}

	/**
	 * @description:check if user exist
	 * @date:2014-5-6 下午6:03:54
	 * @version:v1.0
	 * @param userName
	 * @return
	 */
	public boolean isUserExisted(String userName)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			//加BINARY 避免 Illegal mix of collations (latin1_general_cs,IMPLICIT) and (utf8_general_ci,COERCIBLE) for operation '=' 错误！！
			String sql = "select * from user_info where BINARY user_name=BINARY ?";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, userName);
			rs = pstm.executeQuery();
			return rs.next();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs,pstm, conn);
		}
		return false;
	}

	/**
	 * @description:remove user from db
	 * @date:2014-5-6 下午6:04:04
	 * @version:v1.0
	 * @param userInfo
	 * @return
	 */
	public boolean removeUserInfo(UserInfo userInfo)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			//加BINARY 避免 Illegal mix of collations (latin1_general_cs,IMPLICIT) and (utf8_general_ci,COERCIBLE) for operation '=' 错误！！
			String sql = "delete from user_info  where id=? ";
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, userInfo.getId());
			return pstm.executeUpdate() > 0;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
	}

	/**
	 * @description:query all users
	 * @date:2014-5-6 下午6:04:14
	 * @version:v1.0
	 * @return
	 */
	public List<UserInfo> queryAllUsers()
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List<UserInfo> relatedUserList = new ArrayList<UserInfo>();
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select * from user_info";
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			while(rs.next())
			{
				UserInfo userInfo = new UserInfoImpl();
				userInfo.setId(rs.getInt("id"));
				userInfo.setUserPassword(rs.getString("password"));
				userInfo.setCreateTime(rs.getTimestamp("create_time"));
				userInfo.setLastLoginTime(rs.getTimestamp("last_login_time"));
				userInfo.setNickName(rs.getString("nick_name"));
				userInfo.setUserName(rs.getString("user_name"));
				userInfo.setUserRole(UserRole.valueOf(rs.getString("user_role")));
				userInfo.setUserStat(UserStat.valueOf(rs.getString("user_stat")));
				userInfo.setPicId(rs.getString("pic_id"));
				if (CynthiaUtil.isNull(userInfo.getPicId())) {
					userInfo.setPicUrl(ConfigUtil.getCynthiaWebRoot() + "attachment/download.jsp?method=download&id=" + userInfo.getPicId());
				}
				relatedUserList.add(userInfo);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return relatedUserList;
	}


	/**
	 * @description:remove user
	 * @date:2014-5-6 下午6:04:27
	 * @version:v1.0
	 * @param username
	 * @return
	 */
	public ErrorCode removeRelatedUser(String username)
	{
		UserInfo userInfo = this.queryUserInfoByUserName(username);
		if(this.removeUserInfo(userInfo))
			return ErrorCode.success;
		return ErrorCode.unknownFail;
	}


	/**
	 * @description:query users from user array
	 * @date:2014-5-6 下午6:04:36
	 * @version:v1.0
	 * @param userArray
	 * @return
	 */
	public List<UserInfo> queryAllUserInfo(String[] userArray) {
		List<UserInfo> allUserList = new ArrayList<UserInfo>();
		if (userArray == null || userArray.length == 0) {
			return allUserList;
		}
		
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		
		StringBuffer userBuffer = new StringBuffer();
		for (String user : userArray) {
			if (user != null && user.length() > 0 && !CynthiaUtil.isChinese(user)) {
				userBuffer.append(userBuffer.length() >0 ?"," :"").append("'").append(user).append("'");
			}
		}
		
		try {
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("select * from user_info where user_name in(" + userBuffer.toString() + ")");
			while (rs.next()) {
				UserInfo userInfo = new UserInfoImpl();
				userInfo.setId(rs.getInt("id"));
				userInfo.setCreateTime(rs.getTimestamp("create_time"));
				userInfo.setUserPassword(rs.getString("password"));
				userInfo.setLastLoginTime(rs.getTimestamp("last_login_time"));
				userInfo.setNickName(rs.getString("nick_name"));
				userInfo.setUserName(rs.getString("user_name"));
				userInfo.setUserRole(UserRole.valueOf(rs.getString("user_role")));
				userInfo.setUserStat(UserStat.valueOf(rs.getString("user_stat")));
				userInfo.setPicId(rs.getString("pic_id"));
				if (CynthiaUtil.isNull(userInfo.getPicId())) {
					userInfo.setPicUrl(ConfigUtil.getCynthiaWebRoot() + "attachment/download.jsp?method=download&id=" + userInfo.getPicId());
				}
				allUserList.add(userInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbPoolConnection.getInstance().closeAll(rs, stat, conn);
		}
		return allUserList;
	}

	/**
	 * @description:query users by user stat and name
	 * @date:2014-5-6 下午6:04:48
	 * @version:v1.0
	 * @param userStat
	 * @param userName
	 * @return
	 */
	public List<UserInfo> queryAllUsersByStatAndName(String userStat, String userName) {
		List<UserInfo> allUserList = new ArrayList<UserInfo>();
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select * from user_info where 1=1 ");
		
		if (userStat != null && userStat.length() >0) {
			sqlBuffer.append(" and user_stat = '" + userStat + "' ");
		}
		if (userName != null && !userName.equals("null") && !userName.equals("")) {
			sqlBuffer.append(" and user_name like '%" + userName + "%' or nick_name like '%" + userName + "' ");
		}
		
		try {
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sqlBuffer.toString());
			while (rs.next()) {
				UserInfo userInfo = new UserInfoImpl();
				userInfo.setId(rs.getInt("id"));
				userInfo.setUserPassword(rs.getString("password"));
				userInfo.setCreateTime(rs.getTimestamp("create_time"));
				userInfo.setLastLoginTime(rs.getTimestamp("last_login_time"));
				userInfo.setNickName(rs.getString("nick_name"));
				userInfo.setUserName(rs.getString("user_name"));
				userInfo.setUserRole(UserRole.valueOf(rs.getString("user_role")));
				userInfo.setUserStat(UserStat.valueOf(rs.getString("user_stat")));
				userInfo.setPicId(rs.getString("pic_id"));
				if (CynthiaUtil.isNull(userInfo.getPicId())) {
					userInfo.setPicUrl(ConfigUtil.getCynthiaWebRoot() + "attachment/download.jsp?method=download&id=" + userInfo.getPicId());
				}
				allUserList.add(userInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbPoolConnection.getInstance().closeAll(rs, stat, conn);
		}
		return allUserList;
	}

	/** 
	 * @description:TODO
	 * @date:2014-8-13 下午8:21:10
	 * @version:v1.0
	 * @param userMails
	 * @return
	 */
	public Map<String, UserInfo> queryUserInfoByUserNames(String[] userArray) {
		Map<String, UserInfo> allUserMap = new LinkedHashMap<String,UserInfo>();
		if (userArray == null || userArray.length == 0) {
			return allUserMap;
		}
		
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		
		StringBuffer userBuffer = new StringBuffer();
		for (String user : userArray) {
			if (user != null && user.length() > 0 && !CynthiaUtil.isChinese(user)) {
				userBuffer.append(userBuffer.length() >0 ?"," :"").append("'").append(user).append("'");
			}
		}
		
		try {
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery("select * from user_info where user_name in(" + userBuffer.toString() + ")");
			while (rs.next()) {
				UserInfo userInfo = new UserInfoImpl();
				userInfo.setId(rs.getInt("id"));
				userInfo.setCreateTime(rs.getTimestamp("create_time"));
				userInfo.setUserPassword(rs.getString("password"));
				userInfo.setLastLoginTime(rs.getTimestamp("last_login_time"));
				userInfo.setNickName(rs.getString("nick_name"));
				userInfo.setUserName(rs.getString("user_name"));
				userInfo.setUserRole(UserRole.valueOf(rs.getString("user_role")));
				userInfo.setUserStat(UserStat.valueOf(rs.getString("user_stat")));
				userInfo.setPicId(rs.getString("pic_id"));
				
				if (CynthiaUtil.isNull(userInfo.getPicId())) {
					userInfo.setPicUrl(ConfigUtil.getCynthiaWebRoot() + "attachment/download.jsp?method=download&id=" + userInfo.getPicId());
				}
				
				allUserMap.put(rs.getString("user_name"), userInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbPoolConnection.getInstance().closeAll(rs, stat, conn);
		}
		return allUserMap;
	}
}
