package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.sogou.qadev.service.cynthia.service.DbPoolConnection;

/**
 * @description:event user db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:29:03
 * @version:v1.0
 */
public class EventUserAccessSessionMySQL {

	public EventUserAccessSessionMySQL() {
	}

	/**
	 * @description:add event to db
	 * @date:2014-5-6 下午5:29:14
	 * @version:v1.0
	 * @param eventName
	 * @return
	 */
	public boolean addEvent(String eventName)
	{
		PreparedStatement pstm = null;
		Connection conn = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "insert into event (name) values (?)";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, eventName);
			return pstm.execute();

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
		return false;
	}

	/**
	 * @description:update event name
	 * @date:2014-5-6 下午5:29:23
	 * @version:v1.0
	 * @param eventName
	 * @return
	 */
	public boolean updateEvent(String eventName)
	{
		PreparedStatement pstm = null;
		Connection conn = null;
		try
		{
			String sql = "update event set name=?";
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, eventName);
			return pstm.execute();

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
	 * @description:add event user to db
	 * @date:2014-5-6 下午5:29:33
	 * @version:v1.0
	 * @param userName
	 * @param eventId
	 * @return
	 */
	public boolean addEventUser(String userName,int eventId)
	{
		PreparedStatement pstm = null;
		Connection conn = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "insert into event_user (user_name,event_id) values (?,?)";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, userName);
			pstm.setInt(2, eventId);
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
	 * @description:return if user is valid in event id
	 * @date:2014-5-6 下午5:29:44
	 * @version:v1.0
	 * @param userName
	 * @param eventId
	 * @return
	 */
	public boolean isValidUser(String userName,int eventId)
	{
		PreparedStatement pstm = null;
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			String sql = "select count(*) from event_user where user_name=? and event_id=?";
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, userName);
			pstm.setInt(2, eventId);
			rs = pstm.executeQuery();
			if(rs.next())
			{
				int result = rs.getInt(1);
				if(result>0)
					return true;
			}

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return false;

	}

	/**
	 * @description:delete event user 
	 * @date:2014-5-6 下午5:30:00
	 * @version:v1.0
	 * @param userName
	 * @param eventId
	 * @return
	 */
	public boolean deleteEventUser(String userName,int eventId)
	{
		PreparedStatement pstm = null;
		Connection conn = null;
		try
		{
			String sql = "delete from event_user where user_name=? and event_id=?)";
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, userName);
			pstm.setInt(2, eventId);
			return pstm.execute();

		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
		return false;
	}

}
