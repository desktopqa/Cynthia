package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;

import com.sogou.qadev.service.cynthia.bean.Timer;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.impl.TimerImpl;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;

/**
 * @description:timer db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:57:44
 * @version:v1.0
 */
public class TimerAccessSessionMySQL
{

	public TimerAccessSessionMySQL()
	{
	}

	/**
	 * @description:add timer to db
	 * @date:2014-5-6 下午5:57:54
	 * @version:v1.0
	 * @param timer
	 * @return
	 */
	public ErrorCode addTimer(Timer timer)
	{
		Connection conn = null;
		PreparedStatement pstm = null;

		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("INSERT INTO timer"
					+ " SET id = ?"
					+ ", name = ?"
					+ ", create_user = ?"
					+ ", create_time = ?"
					+ ", action_id = ?"
					+ ", action_param = ?"
					+ ", year = ?"
					+ ", month = ?"
					+ ", week = ?"
					+ ", day = ?"
					+ ", hour = ?"
					+ ", minute = ?"
					+ ", second = ?"
					+ ", filter_id = ?"
					+ ", statisticer_id = ?"
					+ ", is_start = ?"
					+ ", retry_account = ?"
					+ ", retry_delay = ?"
					+ ", is_send_null = ?");

			pstm.setLong(1, Long.parseLong(timer.getId().getValue()));
			pstm.setString(2, timer.getName());
			pstm.setString(3, timer.getCreateUser());
			pstm.setTimestamp(4, timer.getCreateTime());
			pstm.setLong(5, Long.parseLong(timer.getActionId().getValue()));

			if(timer.getActionParam() != null)
				pstm.setString(6, timer.getActionParam());
			else
				pstm.setNull(6, java.sql.Types.NULL);

			if(timer.getYear() != null)
				pstm.setString(7, timer.getYear());
			else
				pstm.setNull(7, java.sql.Types.NULL);

			if(timer.getMonth() != null)
				pstm.setString(8, timer.getMonth());
			else
				pstm.setNull(8, java.sql.Types.NULL);

			if(timer.getWeek() != null)
				pstm.setString(9, timer.getWeek());
			else
				pstm.setNull(9, java.sql.Types.NULL);

			if(timer.getDay() != null)
				pstm.setString(10, timer.getDay());
			else
				pstm.setNull(10, java.sql.Types.NULL);

			if(timer.getHour() != null)
				pstm.setString(11, timer.getHour());
			else
				pstm.setNull(11, java.sql.Types.NULL);

			if(timer.getMinute() != null)
				pstm.setString(12, timer.getMinute());
			else
				pstm.setNull(12, java.sql.Types.NULL);

			if(timer.getSecond() != null)
				pstm.setString(13, timer.getSecond());
			else
				pstm.setNull(13, java.sql.Types.NULL);

			if(timer.getFilterId() != null)
				pstm.setLong(14, Long.parseLong(timer.getFilterId().getValue()));
			else
				pstm.setNull(14, java.sql.Types.NULL);

			if(timer.getStatisticerId() != null)
				pstm.setLong(15, Long.parseLong(timer.getStatisticerId().getValue()));
			else
				pstm.setNull(15, java.sql.Types.NULL);

			pstm.setBoolean(16, timer.isStart());
			pstm.setLong(17, timer.getRetryAccount());
			pstm.setLong(18, timer.getRetryDelay());
			pstm.setBoolean(19, timer.isSendNull());

			if(pstm.executeUpdate()>0)
				return ErrorCode.success;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}

		return ErrorCode.dbFail;
	}

	/**
	 * @description:remove timer from db
	 * @date:2014-5-6 下午5:58:04
	 * @version:v1.0
	 * @param timerId
	 * @return
	 */
	public ErrorCode removeTimer(UUID timerId)
	{
		ErrorCode errorCode = ErrorCode.unknownFail;

		Connection conn = null;
		PreparedStatement pstm = null;

		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("DELETE FROM timer"
					+ " WHERE id = ?");
			pstm.setLong(1, Long.parseLong(timerId.getValue()));

			if(pstm.executeUpdate()>0)
				errorCode = ErrorCode.success;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			errorCode = ErrorCode.dbFail;
		}
		finally
		{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}

		return errorCode;
	}
	
	/**
	 * @description:remove all timers create by user
	 * @date:2014-5-6 下午5:58:18
	 * @version:v1.0
	 * @param createUser
	 * @return
	 */
	public ErrorCode removeTimerByCreateUser(String createUser)
	{
		ErrorCode errorCode = ErrorCode.unknownFail;

		Connection conn = null;
		PreparedStatement pstm = null;

		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("DELETE FROM timer"
					+ " WHERE create_user = ?");
			pstm.setString(1, createUser);

			pstm.executeUpdate();
			errorCode = ErrorCode.success;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			errorCode = ErrorCode.dbFail;
		}
		finally
		{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}

		return errorCode;
	}

	/**
	 * @description:modify timer
	 * @date:2014-5-6 下午5:58:31
	 * @version:v1.0
	 * @param timer
	 * @return
	 */
	public ErrorCode modifyTimer(Timer timer)
	{
		ErrorCode errorCode = ErrorCode.unknownFail;

		Connection conn = null;
		PreparedStatement pstm = null;

		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("update timer"
					+ " SET name = ?"
					+ ", action_id = ?"
					+ ", action_param = ?"
					+ ", year = ?"
					+ ", month = ?"
					+ ", week = ?"
					+ ", day = ?"
					+ ", hour = ?"
					+ ", minute = ?"
					+ ", second = ?"
					+ ", filter_id = ?"
					+ ", statisticer_id = ?"
					+ ", is_start = ?"
					+ ", retry_account = ?"
					+ ", retry_delay = ?"
					+ ", is_send_null = ?"
					+ " WHERE id = ?");

			pstm.setString(1, timer.getName());
			pstm.setLong(2, Long.parseLong(timer.getActionId().getValue()));

			if(timer.getActionParam() != null)
				pstm.setString(3, timer.getActionParam());
			else
				pstm.setNull(3, java.sql.Types.NULL);

			if(timer.getYear() != null)
				pstm.setString(4, timer.getYear());
			else
				pstm.setNull(4, java.sql.Types.NULL);

			if(timer.getMonth() != null)
				pstm.setString(5, timer.getMonth());
			else
				pstm.setNull(5, java.sql.Types.NULL);

			if(timer.getWeek() != null)
				pstm.setString(6, timer.getWeek());
			else
				pstm.setNull(6, java.sql.Types.NULL);

			if(timer.getDay() != null)
				pstm.setString(7, timer.getDay());
			else
				pstm.setNull(7, java.sql.Types.NULL);

			if(timer.getHour() != null)
				pstm.setString(8, timer.getHour());
			else
				pstm.setNull(8, java.sql.Types.NULL);

			if(timer.getMinute() != null)
				pstm.setString(9, timer.getMinute());
			else
				pstm.setNull(9, java.sql.Types.NULL);

			if(timer.getSecond() != null)
				pstm.setString(10, timer.getSecond());
			else
				pstm.setNull(10, java.sql.Types.NULL);

			if(timer.getFilterId() != null)
				pstm.setLong(11, Long.parseLong(timer.getFilterId().getValue()));
			else
				pstm.setNull(11, java.sql.Types.NULL);

			if(timer.getStatisticerId() != null)
				pstm.setLong(12, Long.parseLong(timer.getStatisticerId().getValue()));
			else
				pstm.setNull(12, java.sql.Types.NULL);

			pstm.setBoolean(13, timer.isStart());
			pstm.setLong(14, timer.getRetryAccount());
			pstm.setLong(15, timer.getRetryDelay());
			pstm.setBoolean(16, timer.isSendNull());
			pstm.setLong(17, Long.parseLong(timer.getId().getValue()));

			if(pstm.executeUpdate()>0)
				errorCode = ErrorCode.success;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			errorCode = ErrorCode.dbFail;
		}
		finally
		{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}

		return errorCode;
	}

	/**
	 * @description:create timer
	 * @date:2014-5-6 下午5:58:41
	 * @version:v1.0
	 * @param createUser
	 * @return
	 */
	public Timer createTimer(String createUser)
	{
		UUID id = DataAccessFactory.getInstance().newUUID("TIME");
		Timestamp createTime = new Timestamp(System.currentTimeMillis());
		return new TimerImpl(id, createUser, createTime);
	}

	/**
	 * @description:query all timers create by user
	 * @date:2014-5-6 下午5:58:50
	 * @version:v1.0
	 * @param createUser
	 * @return
	 */
	public Timer[] queryTimers(String createUser)
	{
		Set<Timer> timerSet = new LinkedHashSet<Timer>();

		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rst = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM timer"
					+ " WHERE create_user = ?");
			pstm.setString(1, createUser);

			rst = pstm.executeQuery();
			while(rst.next())
			{
				UUID id = DataAccessFactory.getInstance().createUUID(rst.getObject("id").toString());
				Timestamp createTime = rst.getTimestamp("create_time");

				Timer timer = new TimerImpl(id, createUser, createTime);

				timer.setName(rst.getString("name"));
				timer.setActionId(DataAccessFactory.getInstance().createUUID(rst.getObject("action_id").toString()));
				timer.setActionParam(rst.getString("action_param"));
				timer.setYear(rst.getString("year"));
				timer.setMonth(rst.getString("month"));
				timer.setWeek(rst.getString("week"));
				timer.setDay(rst.getString("day"));
				timer.setHour(rst.getString("hour"));
				timer.setMinute(rst.getString("minute"));
				timer.setSecond(rst.getString("second"));
				timer.setStart(rst.getBoolean("is_start"));

				Object filterIdObj = rst.getObject("filter_id");
				if(filterIdObj != null)
					timer.setFilterId(DataAccessFactory.getInstance().createUUID(filterIdObj.toString()));

				Object statisticerIdObj = rst.getObject("statisticer_id");
				if(statisticerIdObj != null)
					timer.setStatisticerId(DataAccessFactory.getInstance().createUUID(statisticerIdObj.toString()));

				timer.setRetryAccount(rst.getLong("retry_account"));
				timer.setRetryDelay(rst.getLong("retry_delay"));
				timer.setSendNull(rst.getBoolean("is_send_null"));

				timerSet.add(timer);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbPoolConnection.getInstance().closeResultSet(rst);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return timerSet.toArray(new Timer[0]);
	}

	/**
	 * @description:query all users
	 * @date:2014-5-6 下午5:59:02
	 * @version:v1.0
	 * @return
	 */
	public Timer[] queryTimers()
	{
		Set<Timer> timerSet = new LinkedHashSet<Timer>();

		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rst = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM timer");

			rst = pstm.executeQuery();
			while(rst.next())
			{
				UUID id = DataAccessFactory.getInstance().createUUID(rst.getObject("id").toString());
				String createUser = rst.getString("create_user");
				Timestamp createTime = rst.getTimestamp("create_time");

				Timer timer = new TimerImpl(id, createUser, createTime);

				timer.setName(rst.getString("name"));
				timer.setActionId(DataAccessFactory.getInstance().createUUID(rst.getObject("action_id").toString()));
				timer.setActionParam(rst.getString("action_param"));
				timer.setYear(rst.getString("year"));
				timer.setMonth(rst.getString("month"));
				timer.setWeek(rst.getString("week"));
				timer.setDay(rst.getString("day"));
				timer.setHour(rst.getString("hour"));
				timer.setMinute(rst.getString("minute"));
				timer.setSecond(rst.getString("second"));
				timer.setStart(rst.getBoolean("is_start"));

				Object filterIdObj = rst.getObject("filter_id");
				if(filterIdObj != null)
					timer.setFilterId(DataAccessFactory.getInstance().createUUID(filterIdObj.toString()));

				Object statisticerIdObj = rst.getObject("statisticer_id");
				if(statisticerIdObj != null)
					timer.setStatisticerId(DataAccessFactory.getInstance().createUUID(statisticerIdObj.toString()));

				timer.setRetryAccount(rst.getLong("retry_account"));
				timer.setRetryDelay(rst.getLong("retry_delay"));
				timer.setSendNull(rst.getBoolean("is_send_null"));

				timerSet.add(timer);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbPoolConnection.getInstance().closeResultSet(rst);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return timerSet.toArray(new Timer[0]);
	}
	
	/**
	 * @description:query timers by filterid
	 * @date:2014-5-6 下午5:59:12
	 * @version:v1.0
	 * @param filterId
	 * @return
	 */
	public Timer[] queryTimersByFilterId(UUID filterId)
	{
		Set<Timer> timerSet = new LinkedHashSet<Timer>();

		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rst = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM timer"
					+ " WHERE filter_id = ?");
			pstm.setLong(1, Long.parseLong(filterId.getValue()));

			rst = pstm.executeQuery();
			while(rst.next())
			{
				UUID id = DataAccessFactory.getInstance().createUUID(rst.getObject("id").toString());
				Timestamp createTime = rst.getTimestamp("create_time");
				String createUser = rst.getString("create_user");

				Timer timer = new TimerImpl(id, createUser, createTime);

				timer.setName(rst.getString("name"));
				timer.setActionId(DataAccessFactory.getInstance().createUUID(rst.getObject("action_id").toString()));
				timer.setActionParam(rst.getString("action_param"));
				timer.setYear(rst.getString("year"));
				timer.setMonth(rst.getString("month"));
				timer.setWeek(rst.getString("week"));
				timer.setDay(rst.getString("day"));
				timer.setHour(rst.getString("hour"));
				timer.setMinute(rst.getString("minute"));
				timer.setSecond(rst.getString("second"));
				timer.setStart(rst.getBoolean("is_start"));

				Object filterIdObj = rst.getObject("filter_id");
				if(filterIdObj != null)
					timer.setFilterId(DataAccessFactory.getInstance().createUUID(filterIdObj.toString()));

				Object statisticerIdObj = rst.getObject("statisticer_id");
				if(statisticerIdObj != null)
					timer.setStatisticerId(DataAccessFactory.getInstance().createUUID(statisticerIdObj.toString()));

				timer.setRetryAccount(rst.getLong("retry_account"));
				timer.setRetryDelay(rst.getLong("retry_delay"));
				timer.setSendNull(rst.getBoolean("is_send_null"));

				timerSet.add(timer);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbPoolConnection.getInstance().closeResultSet(rst);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return timerSet.toArray(new Timer[0]);
	}

	/**
	 * @description:query timer by timer id
	 * @date:2014-5-6 下午5:59:25
	 * @version:v1.0
	 * @param timerId
	 * @return
	 */
	public Timer queryTimer(UUID timerId)
	{
		Timer timer = null;

		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rst = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM timer"
					+ " WHERE id = ?");
			pstm.setLong(1, Long.parseLong(timerId.getValue()));

			rst = pstm.executeQuery();
			if(rst.next())
			{
				String createUser = rst.getString("create_user");
				Timestamp createTime = rst.getTimestamp("create_time");

				timer = new TimerImpl(timerId, createUser, createTime);
				timer.setName(rst.getString("name"));
				timer.setActionId(DataAccessFactory.getInstance().createUUID(rst.getObject("action_id").toString()));
				timer.setActionParam(rst.getString("action_param"));
				timer.setYear(rst.getString("year"));
				timer.setMonth(rst.getString("month"));
				timer.setWeek(rst.getString("week"));
				timer.setDay(rst.getString("day"));
				timer.setHour(rst.getString("hour"));
				timer.setMinute(rst.getString("minute"));
				timer.setSecond(rst.getString("second"));

				Object filterIdObj = rst.getObject("filter_id");
				if(filterIdObj != null)
					timer.setFilterId(DataAccessFactory.getInstance().createUUID(filterIdObj.toString()));

				Object statisticerIdObj = rst.getObject("statisticer_id");
				if(statisticerIdObj != null)
					timer.setStatisticerId(DataAccessFactory.getInstance().createUUID(statisticerIdObj.toString()));

				timer.setStart(rst.getBoolean("is_start"));
				timer.setRetryAccount(rst.getLong("retry_account"));
				timer.setRetryDelay(rst.getLong("retry_delay"));
				timer.setSendNull(rst.getBoolean("is_send_null"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbPoolConnection.getInstance().closeResultSet(rst);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return timer;
	}

	/**
	 * @description:query all timers by timer action id
	 * @date:2014-5-6 下午5:59:35
	 * @version:v1.0
	 * @param timerActionId
	 * @return
	 */
	public Timer[] queryTimersByActionId(UUID timerActionId) {
		
		Set<Timer> timerSet = new LinkedHashSet<Timer>();
		if (timerActionId == null) {
			return timerSet.toArray(new Timer[0]);
		}
		
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rst = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM timer"
					+ " WHERE action_id = ?");
			
			pstm.setLong(1, Long.parseLong(timerActionId.getValue()));

			rst = pstm.executeQuery();
			while(rst.next())
			{
				UUID id = DataAccessFactory.getInstance().createUUID(rst.getObject("id").toString());
				String createUser = rst.getString("create_user");
				Timestamp createTime = rst.getTimestamp("create_time");

				Timer timer = new TimerImpl(id, createUser, createTime);

				timer.setName(rst.getString("name"));
				timer.setActionId(DataAccessFactory.getInstance().createUUID(rst.getObject("action_id").toString()));
				timer.setActionParam(rst.getString("action_param"));
				timer.setYear(rst.getString("year"));
				timer.setMonth(rst.getString("month"));
				timer.setWeek(rst.getString("week"));
				timer.setDay(rst.getString("day"));
				timer.setHour(rst.getString("hour"));
				timer.setMinute(rst.getString("minute"));
				timer.setSecond(rst.getString("second"));
				timer.setStart(rst.getBoolean("is_start"));

				Object filterIdObj = rst.getObject("filter_id");
				if(filterIdObj != null)
					timer.setFilterId(DataAccessFactory.getInstance().createUUID(filterIdObj.toString()));

				Object statisticerIdObj = rst.getObject("statisticer_id");
				if(statisticerIdObj != null)
					timer.setStatisticerId(DataAccessFactory.getInstance().createUUID(statisticerIdObj.toString()));

				timer.setRetryAccount(rst.getLong("retry_account"));
				timer.setRetryDelay(rst.getLong("retry_delay"));
				timer.setSendNull(rst.getBoolean("is_send_null"));

				timerSet.add(timer);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbPoolConnection.getInstance().closeResultSet(rst);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return timerSet.toArray(new Timer[0]);
	}
}
