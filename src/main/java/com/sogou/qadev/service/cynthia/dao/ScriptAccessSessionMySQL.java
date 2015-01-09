package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.sogou.qadev.service.cynthia.bean.Action;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.ExecuteTime;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Script;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.impl.ScriptImpl;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;
import com.sogou.qadev.service.cynthia.service.impl.AbstractScriptAccessSession;
import com.sogou.qadev.service.cynthia.util.ArrayUtil;

/**
 * @description:script db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:47:10
 * @version:v1.0
 */
public class ScriptAccessSessionMySQL extends AbstractScriptAccessSession
{
	private static Logger logger = Logger.getLogger(ScriptAccessSessionMySQL.class.getName());

	public ScriptAccessSessionMySQL(String username, long keyId)
	{
		super(username, keyId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:addScriptInternal</p>
	 * @param script
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.impl.AbstractScriptAccessSession#addScriptInternal(com.sogou.qadev.service.cynthia.bean.Script)
	 */
	protected UUID addScriptInternal(Script script)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("INSERT INTO script_new"
					+ " SET id = ?"
					+ ", name = ?"
					+ ", create_user = ?"
					+ ", create_time = ?"
					+ ", template_type_ids = ?"
					+ ", template_ids = ?"
					+ ", flow_ids = ?"
					+ ", begin_stat_ids = ?"
					+ ", end_stat_ids = ?"
					+ ", action_ids = ?"
					+ ", is_async = ?"
					+ ", is_before_commit = ?"
					+ ", is_after_success = ?"
					+ ", is_after_fail = ?"
					+ ", is_after_query = ?"
					+ ", xml = ?"
					+ ", is_stat_edit = ?"
					+ ", is_action_edit = ?"
					+ ", is_valid = ?"
					+ ", allowed_template_ids = ?");
			
			pstm.setLong(1, Long.parseLong(script.getId().getValue()));
			pstm.setString(2, script.getName());
			pstm.setString(3, script.getCreateUser());
			pstm.setTimestamp(4, script.getCreateTime());
			
			if(script.getTemplateTypeIds() == null)
				pstm.setNull(5, java.sql.Types.NULL);
			else
				pstm.setString(5, ArrayUtil.idArray2String(script.getTemplateTypeIds()));
			
			if(script.getTemplateIds() == null)
				pstm.setNull(6, java.sql.Types.NULL);
			else
				pstm.setString(6, ArrayUtil.idArray2String(script.getTemplateIds()));
			
			if(script.getFlowIds() == null)
				pstm.setNull(7, java.sql.Types.NULL);
			else
				pstm.setString(7, ArrayUtil.idArray2String(script.getFlowIds()));
			
			if(script.getBeginStatIds() == null)
				pstm.setNull(8, java.sql.Types.NULL);
			else
				pstm.setString(8, ArrayUtil.idArray2String(script.getBeginStatIds()));
			
			if(script.getEndStatIds() == null)
				pstm.setNull(9, java.sql.Types.NULL);
			else
				pstm.setString(9, ArrayUtil.idArray2String(script.getEndStatIds()));
			
			if(script.getActionIds() == null)
				pstm.setNull(10, java.sql.Types.NULL);
			else
				pstm.setString(10, ArrayUtil.idArray2String(script.getActionIds()));
			
			pstm.setBoolean(11, script.isAsync());
			pstm.setBoolean(12, script.isBeforeCommit());
			pstm.setBoolean(13, script.isAfterSuccess());
			pstm.setBoolean(14, script.isAfterFail());
			pstm.setBoolean(15, script.isAfterQuery());
			pstm.setString(16, script.getScript());
			pstm.setBoolean(17, script.isStatEdit());
			pstm.setBoolean(18, script.isActionEdit());
			pstm.setBoolean(19, script.isValid());
			
			if(script.getAllowedTemplateIds() == null)
				pstm.setNull(20, java.sql.Types.NULL);
			else
				pstm.setString(20, ArrayUtil.idArray2String(script.getAllowedTemplateIds()));
			
			pstm.executeUpdate();
			
			return script.getId();
		}
		catch(Exception e)
		{
			logger.error("",e);
		}
		finally
		{
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		
		return null;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:removeScriptInternal</p>
	 * @param scriptId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.impl.AbstractScriptAccessSession#removeScriptInternal(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	protected ErrorCode removeScriptInternal(UUID scriptId)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("DELETE FROM script_new WHERE id = ?");
			pstm.setLong(1, Long.parseLong(scriptId.getValue()));
			
			if(pstm.executeUpdate()>0)
				return ErrorCode.success;
			
		}
		catch(Exception e)
		{
			logger.error("", e);
		}
		finally
		{
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		
		return ErrorCode.dbFail;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:updateScriptInternal</p>
	 * @param script
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.impl.AbstractScriptAccessSession#updateScriptInternal(com.sogou.qadev.service.cynthia.bean.Script)
	 */
	protected ErrorCode updateScriptInternal(Script script)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("update script_new"
					+ " SET name = ?"
					+ ", create_user = ?"
					+ ", create_time = ?"
					+ ", template_type_ids = ?"
					+ ", template_ids = ?"
					+ ", flow_ids = ?"
					+ ", begin_stat_ids = ?"
					+ ", end_stat_ids = ?"
					+ ", action_ids = ?"
					+ ", is_async = ?"
					+ ", is_before_commit = ?"
					+ ", is_after_success = ?"
					+ ", is_after_fail = ?"
					+ ", is_after_query = ?"
					+ ", xml = ?"
					+ ", is_stat_edit = ?"
					+ ", is_action_edit = ?"
					+ ", is_valid = ?"
					+ ", allowed_template_ids = ?"
					+ " WHERE id = ?");
			
			pstm.setString(1, script.getName());
			pstm.setString(2, script.getCreateUser());
			pstm.setTimestamp(3, script.getCreateTime());
			
			if(script.getTemplateTypeIds() == null)
				pstm.setNull(4, java.sql.Types.NULL);
			else
				pstm.setString(4, ArrayUtil.idArray2String(script.getTemplateTypeIds()));
			
			if(script.getTemplateIds() == null)
				pstm.setNull(5, java.sql.Types.NULL);
			else
				pstm.setString(5, ArrayUtil.idArray2String(script.getTemplateIds()));
			
			if(script.getFlowIds() == null)
				pstm.setNull(6, java.sql.Types.NULL);
			else
				pstm.setString(6, ArrayUtil.idArray2String(script.getFlowIds()));
			
			if(script.getBeginStatIds() == null)
				pstm.setNull(7, java.sql.Types.NULL);
			else
				pstm.setString(7, ArrayUtil.idArray2String(script.getBeginStatIds()));
			
			if(script.getEndStatIds() == null)
				pstm.setNull(8, java.sql.Types.NULL);
			else
				pstm.setString(8, ArrayUtil.idArray2String(script.getEndStatIds()));
			
			if(script.getActionIds() == null)
				pstm.setNull(9, java.sql.Types.NULL);
			else
				pstm.setString(9, ArrayUtil.idArray2String(script.getActionIds()));
			
			pstm.setBoolean(10, script.isAsync());
			pstm.setBoolean(11, script.isBeforeCommit());
			pstm.setBoolean(12, script.isAfterSuccess());
			pstm.setBoolean(13, script.isAfterFail());
			pstm.setBoolean(14, script.isAfterQuery());
			pstm.setString(15, script.getScript());
			pstm.setBoolean(16, script.isStatEdit());
			pstm.setBoolean(17, script.isActionEdit());
			pstm.setBoolean(18, script.isValid());
			
			System.out.println(script.getAllowedTemplateIds()+"--------");
			
			if(script.getAllowedTemplateIds() != null)
				System.out.println(script.getAllowedTemplateIds().length);
			if(script.getAllowedTemplateIds() == null||script.getAllowedTemplateIds().length==0)
				pstm.setString(19, "*");
			else
				pstm.setString(19, ArrayUtil.idArray2String(script.getAllowedTemplateIds()));
			pstm.setLong(20, Long.parseLong(script.getId().getValue()));
			if(pstm.executeUpdate()>0)
				return ErrorCode.success;
		}
		catch(Exception e)
		{
			logger.error("", e);
		}
		finally
		{
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		
		return ErrorCode.dbFail;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryScriptInternal</p>
	 * @param scriptId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.impl.AbstractScriptAccessSession#queryScriptInternal(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	protected Script queryScriptInternal(UUID scriptId)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM script_new"
					+ " WHERE id = ?");
			
			pstm.setLong(1, Long.parseLong(scriptId.getValue()));
			
			rs = pstm.executeQuery();
			if(rs.next())
			{
				String scriptImportStr = getScriptImportStr();
				
				String createUser = rs.getString("create_user");
				Timestamp createTime = rs.getTimestamp("create_time");
				
				Script script = new ScriptImpl(scriptId, createUser, createTime);
				
				script.setName(rs.getString("name"));
				script.setTemplateTypeIds(ArrayUtil.string2IdArray(rs.getString("template_type_ids")));
				script.setTemplateIds(ArrayUtil.string2IdArray(rs.getString("template_ids")));
				script.setFlowIds(ArrayUtil.string2IdArray(rs.getString("flow_ids")));
				script.setBeginStatIds(ArrayUtil.string2IdArray(rs.getString("begin_stat_ids")));
				script.setEndStatIds(ArrayUtil.string2IdArray(rs.getString("end_stat_ids")));
				script.setActionIds(ArrayUtil.string2IdArray(rs.getString("action_ids")));
				script.setAsync(rs.getBoolean("is_async"));
				script.setBeforeCommit(rs.getBoolean("is_before_commit"));
				script.setAfterSuccess(rs.getBoolean("is_after_success"));
				script.setAfterFail(rs.getBoolean("is_after_fail"));
				script.setAfterQuery(rs.getBoolean("is_after_query"));
				script.setScript(scriptImportStr + rs.getString("xml"));
				script.setStatEdit(rs.getBoolean("is_stat_edit"));
				script.setActionEdit(rs.getBoolean("is_action_edit"));
				script.setValid(rs.getBoolean("is_valid"));
				script.setAllowedTemplateIds(ArrayUtil.string2IdArray(rs.getString("allowed_template_ids")));
				
				return script;
			}
		}
		catch(Exception e)
		{
			logger.error("", e);
		}
		finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		
		return null;
	}
	
	/**
	 * @description:query all scripts
	 * @date:2014-5-6 下午5:47:45
	 * @version:v1.0
	 * @return
	 */
	public List<Script> queryAllScripts()
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List<Script> result = new ArrayList<Script>();
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM script_new");
			rs = pstm.executeQuery();
			String scriptImportStr = getScriptImportStr();
			while(rs.next())
			{
				String createUser = rs.getString("create_user");
				Timestamp createTime = rs.getTimestamp("create_time");
				UUID id = DataAccessFactory.getInstance().createUUID(rs.getObject("id").toString());
				Script script = new ScriptImpl(id, createUser, createTime);
				
				script.setName(rs.getString("name"));
				script.setTemplateTypeIds(ArrayUtil.string2IdArray(rs.getString("template_type_ids")));
				script.setTemplateIds(ArrayUtil.string2IdArray(rs.getString("template_ids")));
				script.setFlowIds(ArrayUtil.string2IdArray(rs.getString("flow_ids")));
				script.setBeginStatIds(ArrayUtil.string2IdArray(rs.getString("begin_stat_ids")));
				script.setEndStatIds(ArrayUtil.string2IdArray(rs.getString("end_stat_ids")));
				script.setActionIds(ArrayUtil.string2IdArray(rs.getString("action_ids")));
				script.setAsync(rs.getBoolean("is_async"));
				script.setBeforeCommit(rs.getBoolean("is_before_commit"));
				script.setAfterSuccess(rs.getBoolean("is_after_success"));
				script.setAfterFail(rs.getBoolean("is_after_fail"));
				script.setAfterQuery(rs.getBoolean("is_after_query"));
				script.setScript(scriptImportStr + rs.getString("xml"));
				script.setStatEdit(rs.getBoolean("is_stat_edit"));
				script.setActionEdit(rs.getBoolean("is_action_edit"));
				script.setValid(rs.getBoolean("is_valid"));
				script.setAllowedTemplateIds(ArrayUtil.string2IdArray(rs.getString("allowed_template_ids")));
				
				result.add(script);
				
			}
		}
		catch(Exception e)
		{
			logger.error("", e);
		}
		finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		
		return result;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:queryTemplateScriptsInternal</p>
	 * @param templateId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.impl.AbstractScriptAccessSession#queryTemplateScriptsInternal(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	protected Script[] queryTemplateScriptsInternal(UUID templateId)
	{
		Set<Script> scriptSet = new LinkedHashSet<Script>();
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select * from script_new where template_ids like '%"+templateId.toString()+"%' ";
			pstm = conn.prepareStatement(sql);
			
			rs = pstm.executeQuery();
			String scriptImportStr = getScriptImportStr();
			while(rs.next())
			{
				UUID id = DataAccessFactory.getInstance().createUUID(rs.getObject("id").toString());
				Timestamp createTime = rs.getTimestamp("create_time");
				Script script = new ScriptImpl(id, rs.getString("create_user"), createTime);
				script.setName(rs.getString("name"));
				script.setTemplateTypeIds(ArrayUtil.string2IdArray(rs.getString("template_type_ids")));
				script.setTemplateIds(ArrayUtil.string2IdArray(rs.getString("template_ids")));
				script.setFlowIds(ArrayUtil.string2IdArray(rs.getString("flow_ids")));
				script.setBeginStatIds(ArrayUtil.string2IdArray(rs.getString("begin_stat_ids")));
				script.setEndStatIds(ArrayUtil.string2IdArray(rs.getString("end_stat_ids")));
				script.setActionIds(ArrayUtil.string2IdArray(rs.getString("action_ids")));
				script.setAsync(rs.getBoolean("is_async"));
				script.setBeforeCommit(rs.getBoolean("is_before_commit"));
				script.setAfterSuccess(rs.getBoolean("is_after_success"));
				script.setAfterFail(rs.getBoolean("is_after_fail"));
				script.setAfterQuery(rs.getBoolean("is_after_query"));
				script.setScript(scriptImportStr + rs.getString("xml"));
				script.setStatEdit(rs.getBoolean("is_stat_edit"));
				script.setActionEdit(rs.getBoolean("is_action_edit"));
				script.setValid(rs.getBoolean("is_valid"));
				script.setAllowedTemplateIds(ArrayUtil.string2IdArray(rs.getString("allowed_template_ids")));
				scriptSet.add(script);
			}
		}catch(Exception e)
		{
			logger.error("",e);
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return scriptSet.toArray(new Script[0]);
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:queryAllowedTemplateScriptsInternal</p>
	 * @param templateId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.impl.AbstractScriptAccessSession#queryAllowedTemplateScriptsInternal(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	protected Script[] queryAllowedTemplateScriptsInternal(UUID templateId) {
		Set<Script> scriptSet = new LinkedHashSet<Script>();
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select * from script_new where (allowed_template_ids like '%"+templateId.toString()+"%' ) or (allowed_template_ids = '*')";
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			String scriptImportStr = getScriptImportStr();
			while(rs.next())
			{
				UUID id = DataAccessFactory.getInstance().createUUID(rs.getObject("id").toString());
				Timestamp createTime = rs.getTimestamp("create_time");
				
				Script script = new ScriptImpl(id, rs.getString("create_user"), createTime);
				
				script.setName(rs.getString("name"));
				script.setTemplateTypeIds(ArrayUtil.string2IdArray(rs.getString("template_type_ids")));
				script.setTemplateIds(ArrayUtil.string2IdArray(rs.getString("template_ids")));
				script.setFlowIds(ArrayUtil.string2IdArray(rs.getString("flow_ids")));
				script.setBeginStatIds(ArrayUtil.string2IdArray(rs.getString("begin_stat_ids")));
				script.setEndStatIds(ArrayUtil.string2IdArray(rs.getString("end_stat_ids")));
				script.setActionIds(ArrayUtil.string2IdArray(rs.getString("action_ids")));
				script.setAsync(rs.getBoolean("is_async"));
				script.setBeforeCommit(rs.getBoolean("is_before_commit"));
				script.setAfterSuccess(rs.getBoolean("is_after_success"));
				script.setAfterFail(rs.getBoolean("is_after_fail"));
				script.setAfterQuery(rs.getBoolean("is_after_query"));
				script.setScript(scriptImportStr + rs.getString("xml"));
				script.setStatEdit(rs.getBoolean("is_stat_edit"));
				script.setActionEdit(rs.getBoolean("is_action_edit"));
				script.setValid(rs.getBoolean("is_valid"));
				script.setAllowedTemplateIds(ArrayUtil.string2IdArray(rs.getString("allowed_template_ids")));
				
				scriptSet.add(script);
			}
		}catch(Exception e)
		{
			logger.error("",e);
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return scriptSet.toArray(new Script[0]);
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:queryScriptsInternal</p>
	 * @param createUser
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.impl.AbstractScriptAccessSession#queryScriptsInternal(java.lang.String)
	 */
	protected Script[] queryScriptsInternal(String createUser)
	{
		Set<Script> scriptSet = new LinkedHashSet<Script>();
		
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM script_new"
					+ " WHERE create_user = ?");
			
			pstm.setString(1, createUser);
			
			rs = pstm.executeQuery();
			String scriptImportStr = getScriptImportStr();
			while(rs.next())
			{
				UUID id = DataAccessFactory.getInstance().createUUID(rs.getObject("id").toString());
				Timestamp createTime = rs.getTimestamp("create_time");
				
				Script script = new ScriptImpl(id, createUser, createTime);
				
				script.setName(rs.getString("name"));
				script.setTemplateTypeIds(ArrayUtil.string2IdArray(rs.getString("template_type_ids")));
				script.setTemplateIds(ArrayUtil.string2IdArray(rs.getString("template_ids")));
				script.setFlowIds(ArrayUtil.string2IdArray(rs.getString("flow_ids")));
				script.setBeginStatIds(ArrayUtil.string2IdArray(rs.getString("begin_stat_ids")));
				script.setEndStatIds(ArrayUtil.string2IdArray(rs.getString("end_stat_ids")));
				script.setActionIds(ArrayUtil.string2IdArray(rs.getString("action_ids")));
				script.setAsync(rs.getBoolean("is_async"));
				script.setBeforeCommit(rs.getBoolean("is_before_commit"));
				script.setAfterSuccess(rs.getBoolean("is_after_success"));
				script.setAfterFail(rs.getBoolean("is_after_fail"));
				script.setAfterQuery(rs.getBoolean("is_after_query"));
				script.setScript(scriptImportStr + rs.getString("xml"));
				script.setStatEdit(rs.getBoolean("is_stat_edit"));
				script.setActionEdit(rs.getBoolean("is_action_edit"));
				script.setValid(rs.getBoolean("is_valid"));
				script.setAllowedTemplateIds(ArrayUtil.string2IdArray(rs.getString("allowed_template_ids")));
				
				scriptSet.add(script);
			}
		}
		catch(Exception e)
		{
			logger.error("", e);
		}
		finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		
		return scriptSet.toArray(new Script[0]);
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:queryScriptsInternal</p>
	 * @param data
	 * @param executeTime
	 * @param das
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.impl.AbstractScriptAccessSession#queryScriptsInternal(com.sogou.qadev.service.cynthia.bean.Data, com.sogou.qadev.service.cynthia.bean.ExecuteTime, com.sogou.qadev.service.cynthia.service.DataAccessSession)
	 */
	protected Script[] queryScriptsInternal(Data data, ExecuteTime executeTime, DataAccessSession das)
	{
		Template template = das.queryTemplate(data.getTemplateId());
		if(template == null)
			return null;
		
		Flow flow = das.queryFlow(template.getFlowId());
		if(flow == null)
			return null;
		
		Action action = null;
		if(data.getObject("logActionId") != null)
		{
			action = flow.getAction((UUID)data.getObject("logActionId"));
			if(action == null)
				return null;
		}
		
		StringBuffer sqlStrb = new StringBuffer();
		sqlStrb.append("SELECT * FROM script_new WHERE 1=1");
		
		sqlStrb.append(" AND (template_type_ids IS NULL");
		sqlStrb.append(" OR template_type_ids LIKE '%").append(template.getTemplateTypeId()).append("%')");
		
		sqlStrb.append(" AND (template_ids IS NULL");
		sqlStrb.append(" OR template_ids LIKE '%").append(template.getId()).append("%')");
		
		sqlStrb.append(" AND (flow_ids IS NULL");
		sqlStrb.append(" OR flow_ids LIKE '%").append(flow.getId()).append("%')");
		
		sqlStrb.append(" AND (end_stat_ids IS NULL");
		sqlStrb.append(" OR end_stat_ids LIKE '%").append(data.getStatusId()).append("%')");
		
		if(action == null)
			sqlStrb.append(" AND action_ids IS NULL");
		else
		{	
			sqlStrb.append(" AND (action_ids IS NULL");
			sqlStrb.append(" OR action_ids LIKE '%").append(action.getId()).append("%')");
		}
		
		if(executeTime.equals(ExecuteTime.beforeCommit))
			sqlStrb.append(" AND is_before_commit IS TRUE");
		if(executeTime.equals(ExecuteTime.afterSuccess))
			sqlStrb.append(" AND is_after_success IS TRUE");
		if(executeTime.equals(ExecuteTime.afterFail))
			sqlStrb.append(" AND is_after_fail IS TRUE");
		if(executeTime.equals(ExecuteTime.afterQuery))
			sqlStrb.append(" AND is_after_query IS TRUE");
		
		sqlStrb.append(" AND is_valid IS TRUE");
		
		Set<Script> scriptSet = new LinkedHashSet<Script>();
		
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sqlStrb.toString());
			String scriptImportStr = getScriptImportStr();
			while(rs.next())
			{
				UUID id = DataAccessFactory.getInstance().createUUID(rs.getObject("id").toString());
				String createUser = rs.getString("create_user");
				Timestamp createTime = rs.getTimestamp("create_time");
				
				Script script = new ScriptImpl(id, createUser, createTime);
				
				script.setName(rs.getString("name"));
				script.setTemplateTypeIds(ArrayUtil.string2IdArray(rs.getString("template_type_ids")));
				script.setTemplateIds(ArrayUtil.string2IdArray(rs.getString("template_ids")));
				script.setFlowIds(ArrayUtil.string2IdArray(rs.getString("flow_ids")));
				script.setBeginStatIds(ArrayUtil.string2IdArray(rs.getString("begin_stat_ids")));
				script.setEndStatIds(ArrayUtil.string2IdArray(rs.getString("end_stat_ids")));
				script.setActionIds(ArrayUtil.string2IdArray(rs.getString("action_ids")));
				script.setAsync(rs.getBoolean("is_async"));
				script.setBeforeCommit(rs.getBoolean("is_before_commit"));
				script.setAfterSuccess(rs.getBoolean("is_after_success"));
				script.setAfterFail(rs.getBoolean("is_after_fail"));
				script.setAfterQuery(rs.getBoolean("is_after_query"));
				script.setScript(scriptImportStr + rs.getString("xml"));
				script.setStatEdit(rs.getBoolean("is_stat_edit"));
				script.setActionEdit(rs.getBoolean("is_action_edit"));
				script.setValid(rs.getBoolean("is_valid"));
				script.setAllowedTemplateIds(ArrayUtil.string2IdArray(rs.getString("allowed_template_ids")));
				
				scriptSet.add(script);
			}
		}
		catch(Exception e)
		{
			logger.error("",e);
		}
		finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(stat);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		
		return scriptSet.toArray(new Script[0]);
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:queryScripts</p>
	 * @param data
	 * @param executeTime
	 * @param das
	 * @param template
	 * @param flow
	 * @return
	 * @see com.sogou.qadev.service.cynthia.service.ScriptAccessSession#queryScripts(com.sogou.qadev.service.cynthia.bean.Data, com.sogou.qadev.service.cynthia.bean.ExecuteTime, com.sogou.qadev.service.cynthia.service.DataAccessSession, com.sogou.qadev.service.cynthia.bean.Template, com.sogou.qadev.service.cynthia.bean.Flow)
	 */
	@Override
	public Script[] queryScripts(Data data, ExecuteTime executeTime, DataAccessSession das , Template template , Flow flow) {
		Action action = null;
		if(data.getObject("logActionId") != null)
		{
			action = flow.getAction((UUID)data.getObject("logActionId"));
			if(action == null)
				return null;
		}
		
		StringBuffer sqlStrb = new StringBuffer();
		sqlStrb.append("SELECT * FROM script_new WHERE 1=1");
		
		sqlStrb.append(" AND (template_type_ids IS NULL");
		sqlStrb.append(" OR template_type_ids LIKE '%").append(template.getTemplateTypeId()).append("%')");
		
		sqlStrb.append(" AND (template_ids IS NULL");
		sqlStrb.append(" OR template_ids LIKE '%").append(template.getId()).append("%')");
		
		sqlStrb.append(" AND (flow_ids IS NULL");
		sqlStrb.append(" OR flow_ids LIKE '%").append(flow.getId()).append("%')");
		
		sqlStrb.append(" AND (end_stat_ids IS NULL");
		sqlStrb.append(" OR end_stat_ids LIKE '%").append(data.getStatusId()).append("%')");
		
		if(action == null)
			sqlStrb.append(" AND action_ids IS NULL");
		else
		{	
			sqlStrb.append(" AND (action_ids IS NULL");
			sqlStrb.append(" OR action_ids LIKE '%").append(action.getId()).append("%')");
		}
		
		if(executeTime.equals(ExecuteTime.beforeCommit))
			sqlStrb.append(" AND is_before_commit IS TRUE");
		if(executeTime.equals(ExecuteTime.afterSuccess))
			sqlStrb.append(" AND is_after_success IS TRUE");
		if(executeTime.equals(ExecuteTime.afterFail))
			sqlStrb.append(" AND is_after_fail IS TRUE");
		if(executeTime.equals(ExecuteTime.afterQuery))
			sqlStrb.append(" AND is_after_query IS TRUE");
		
		sqlStrb.append(" AND is_valid IS TRUE");
		
		Set<Script> scriptSet = new LinkedHashSet<Script>();
		
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sqlStrb.toString());
			
			String scriptImportStr = getScriptImportStr();
			
			while(rs.next())
			{
				
				UUID id = DataAccessFactory.getInstance().createUUID(rs.getObject("id").toString());
				String createUser = rs.getString("create_user");
				Timestamp createTime = rs.getTimestamp("create_time");
				
				Script script = new ScriptImpl(id, createUser, createTime);
				
				script.setName(rs.getString("name"));
				script.setTemplateTypeIds(ArrayUtil.string2IdArray(rs.getString("template_type_ids")));
				script.setTemplateIds(ArrayUtil.string2IdArray(rs.getString("template_ids")));
				script.setFlowIds(ArrayUtil.string2IdArray(rs.getString("flow_ids")));
				script.setBeginStatIds(ArrayUtil.string2IdArray(rs.getString("begin_stat_ids")));
				script.setEndStatIds(ArrayUtil.string2IdArray(rs.getString("end_stat_ids")));
				script.setActionIds(ArrayUtil.string2IdArray(rs.getString("action_ids")));
				script.setAsync(rs.getBoolean("is_async"));
				script.setBeforeCommit(rs.getBoolean("is_before_commit"));
				script.setAfterSuccess(rs.getBoolean("is_after_success"));
				script.setAfterFail(rs.getBoolean("is_after_fail"));
				script.setAfterQuery(rs.getBoolean("is_after_query"));
				
				//import信息统一在数据库中存储，方便类文件修改后不用一一个脚本引包，所以在读取脚本xml后需要统一加上引包信息
				script.setScript(scriptImportStr + rs.getString("xml"));   
				script.setStatEdit(rs.getBoolean("is_stat_edit"));
				script.setActionEdit(rs.getBoolean("is_action_edit"));
				script.setValid(rs.getBoolean("is_valid"));
				script.setAllowedTemplateIds(ArrayUtil.string2IdArray(rs.getString("allowed_template_ids")));
				
				scriptSet.add(script);
			}
		}
		catch(Exception e)
		{
			logger.error("",e);
		}
		finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(stat);
			DbPoolConnection.getInstance().closeConn(conn);
		}
		
		return scriptSet.toArray(new Script[0]);
	}

	/**
	 * @description:query script not contain import info
	 * @date:2014-5-6 下午5:51:23
	 * @version:v1.0
	 * @param scriptId
	 * @return
	 */
	public Script queryScriptNoImport(UUID scriptId) {
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM script_new"
					+ " WHERE id = ?");
			
			pstm.setLong(1, Long.parseLong(scriptId.getValue()));
			
			rs = pstm.executeQuery();
			if(rs.next())
			{
				String createUser = rs.getString("create_user");
				Timestamp createTime = rs.getTimestamp("create_time");
				
				Script script = new ScriptImpl(scriptId, createUser, createTime);
				
				script.setName(rs.getString("name"));
				script.setTemplateTypeIds(ArrayUtil.string2IdArray(rs.getString("template_type_ids")));
				script.setTemplateIds(ArrayUtil.string2IdArray(rs.getString("template_ids")));
				script.setFlowIds(ArrayUtil.string2IdArray(rs.getString("flow_ids")));
				script.setBeginStatIds(ArrayUtil.string2IdArray(rs.getString("begin_stat_ids")));
				script.setEndStatIds(ArrayUtil.string2IdArray(rs.getString("end_stat_ids")));
				script.setActionIds(ArrayUtil.string2IdArray(rs.getString("action_ids")));
				script.setAsync(rs.getBoolean("is_async"));
				script.setBeforeCommit(rs.getBoolean("is_before_commit"));
				script.setAfterSuccess(rs.getBoolean("is_after_success"));
				script.setAfterFail(rs.getBoolean("is_after_fail"));
				script.setAfterQuery(rs.getBoolean("is_after_query"));
				script.setScript(rs.getString("xml"));
				script.setStatEdit(rs.getBoolean("is_stat_edit"));
				script.setActionEdit(rs.getBoolean("is_action_edit"));
				script.setValid(rs.getBoolean("is_valid"));
				script.setAllowedTemplateIds(ArrayUtil.string2IdArray(rs.getString("allowed_template_ids")));
				
				return script;
			}
		}
		catch(Exception e)
		{
			logger.error("", e);
		}
		finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		
		return null;
	}
}
