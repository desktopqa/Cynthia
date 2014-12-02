package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sogou.qadev.service.cynthia.bean.TimerAction;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.impl.TimerActionImpl;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;

/**
 * @description:timer action db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:59:51
 * @version:v1.0
 */
public class TimerActionAccessSessionMySQL
{

	public TimerActionAccessSessionMySQL()
	{
	}

	/**
	 * @description:create timer action
	 * @date:2014-5-6 下午6:00:03
	 * @version:v1.0
	 * @return
	 */
	public TimerAction createTimerAction()
	{
		UUID timerActionId = DataAccessFactory.getInstance().newUUID("TIAC");
		return new TimerActionImpl(timerActionId);
	}

	/**
	 * @description:add timer action to db
	 * @date:2014-5-6 下午6:00:12
	 * @version:v1.0
	 * @param timerAction
	 * @return
	 */
	public ErrorCode addTimerAction(TimerAction timerAction)
	{
		Connection conn = null;
		PreparedStatement pstm = null;

		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("INSERT INTO timer_action"
					+ " SET id = ?"
					+ ", name = ?"
					+ ", class_name = ?"
					+ ", method = ?"
					+ ", create_user = ?"
					+ ", param = ?");

			pstm.setLong(1, Long.parseLong(timerAction.getId().getValue()));
			pstm.setString(2, timerAction.getName());
			pstm.setString(3, timerAction.getClassName());
			pstm.setString(4, timerAction.getMethod());
			pstm.setString(5, timerAction.getCreateUser());
			if(timerAction.getParam() != null)
				pstm.setString(6, timerAction.getParam());
			else
				pstm.setNull(6, java.sql.Types.NULL);

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
	 * @description:remove timer action from db
	 * @date:2014-5-6 下午6:00:22
	 * @version:v1.0
	 * @param timerActionId
	 * @return
	 */
	public ErrorCode removeTimerAction(UUID timerActionId)
	{
		ErrorCode errorCode = ErrorCode.unknownFail;

		Connection conn = null;
		PreparedStatement pstm = null;

		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("DELETE FROM timer_action"
					+ " WHERE id = ?");
			pstm.setLong(1, Long.parseLong(timerActionId.getValue()));

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
	 * @description:modify timer action from db
	 * @date:2014-5-6 下午6:00:32
	 * @version:v1.0
	 * @param timerAction
	 * @return
	 */
	public ErrorCode modifyTimerAction(TimerAction timerAction)
	{
		ErrorCode errorCode = ErrorCode.unknownFail;

		Connection conn = null;
		PreparedStatement pstm = null;

		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("update timer_action"
					+ " SET name = ?"
					+ ", class_name = ?"
					+ ", method = ?"
					+ ", param = ?"
					+ " WHERE id = ?");

			pstm.setString(1, timerAction.getName());
			pstm.setString(2, timerAction.getClassName());
			pstm.setString(3, timerAction.getMethod());

			if(timerAction.getParam() != null)
				pstm.setString(4, timerAction.getParam());
			else
				pstm.setNull(4, java.sql.Types.NULL);

			pstm.setLong(5, Long.parseLong(timerAction.getId().getValue()));

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
	 * @description:query timer action by timer action id
	 * @date:2014-5-6 下午6:00:44
	 * @version:v1.0
	 * @param timerActionId
	 * @return
	 */
	public TimerAction queryTimerAction(UUID timerActionId)
	{
		TimerAction timerAction = null;

		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rst = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM timer_action"
					+ " WHERE id = ?");
			pstm.setLong(1, Long.parseLong(timerActionId.getValue()));

			rst = pstm.executeQuery();
			if(rst.next())
			{
				timerAction = new TimerActionImpl(timerActionId);

				timerAction.setName(rst.getString("name"));
				timerAction.setClassName(rst.getString("class_name"));
				timerAction.setMethod(rst.getString("method"));
				timerAction.setParam(rst.getString("param"));
				timerAction.setCreateUser(rst.getString("create_user"));
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

		return timerAction;
	}

	/**
	 * @description:query all timer actions
	 * @date:2014-5-6 下午6:00:56
	 * @version:v1.0
	 * @return
	 */
	public TimerAction[] queryTimerActions()
	{
		Set<TimerAction> timerActionSet = new LinkedHashSet<TimerAction>();

		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rst = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM timer_action");

			rst = pstm.executeQuery();
			while(rst.next())
			{
				UUID id = DataAccessFactory.getInstance().createUUID(rst.getObject("id").toString());
				TimerAction timerAction = new TimerActionImpl(id);

				timerAction.setName(rst.getString("name"));
				timerAction.setClassName(rst.getString("class_name"));
				timerAction.setMethod(rst.getString("method"));
				timerAction.setParam(rst.getString("param"));
				timerAction.setCreateUser(rst.getString("create_user"));
				timerActionSet.add(timerAction);
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

		return timerActionSet.toArray(new TimerAction[0]);
	}

	/**
	 * @description:query all timeractions public or create by user
	 * @date:2014-5-6 下午6:01:06
	 * @version:v1.0
	 * @param userName
	 * @return
	 */
	public TimerAction[] queryStatisticByUser(String userName) {
		List<TimerAction> timerActionSet = new ArrayList<TimerAction>();

		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rst = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM timer_action where is_public = 1 or create_user = ? order by is_public desc , id");

			pstm.setString(1, userName);
			
			rst = pstm.executeQuery();
			while(rst.next())
			{
				UUID id = DataAccessFactory.getInstance().createUUID(rst.getObject("id").toString());
				TimerAction timerAction = new TimerActionImpl(id);

				timerAction.setName(rst.getString("name"));
				timerAction.setClassName(rst.getString("class_name"));
				timerAction.setMethod(rst.getString("method"));
				timerAction.setParam(rst.getString("param"));
				timerAction.setCreateUser(rst.getString("create_user"));
				timerAction.setIsPublic(rst.getBoolean("is_public"));
				timerActionSet.add(timerAction);
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

		return timerActionSet.toArray(new TimerAction[0]);
	}
}
