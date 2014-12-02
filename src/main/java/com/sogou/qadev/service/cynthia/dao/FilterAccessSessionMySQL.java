package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sogou.qadev.service.cynthia.bean.Action;
import com.sogou.qadev.service.cynthia.bean.Filter;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Role;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.impl.FilterImpl;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

/**
 * @author jinjing
 *
 */
public class FilterAccessSessionMySQL
{
	private static DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
	
	public FilterAccessSessionMySQL()
	{
	}

	public Filter addFilter(Filter filter)
	{
		Filter newFilter = null;
		PreparedStatement pstm = null;
		Connection conn = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("INSERT INTO filter"
					+ " SET id = ?"
					+ ", name = ?"
					+ ", xml = ?"
					+ ", create_user = ?"
					+ ", create_time = ?"
					+ ", is_and = ?"
					+ ", is_public = ?"
					+ ", is_visible = ?"
					+ ", father_id = ?");

			pstm.setLong(1, Long.parseLong(filter.getId().getValue()));
			pstm.setString(2, filter.getName());
			pstm.setString(3, filter.getXml());
			pstm.setString(4, filter.getCreateUser());
			pstm.setTimestamp(5, filter.getCreateTime());
			pstm.setBoolean(6, filter.isAnd());
			pstm.setBoolean(7, filter.isPublic());
			pstm.setBoolean(8, filter.isVisible());

			if(filter.getFatherId() != null)
				pstm.setLong(9, Long.parseLong(filter.getFatherId().getValue()));
			else
				pstm.setNull(9, java.sql.Types.NULL);

			pstm.executeUpdate();
			newFilter = filter;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return newFilter;
	}

	public Filter[] queryFilters(String user)
	{
		DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
		
		List<Filter> filterList = new ArrayList<Filter>();

		PreparedStatement pstm = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM filter WHERE (create_user = ? OR is_public = true AND create_user != ?)"
					+ " AND father_id IS NULL AND is_valid = true ORDER BY name");
			pstm.setString(1, user);
			pstm.setString(2, DataAccessFactory.sysUser);

			rs = pstm.executeQuery();
			while(rs.next()){
				UUID id = DataAccessFactory.getInstance().createUUID(rs.getObject("id").toString());
				String createUser = rs.getString("create_user");
				Timestamp createTime = rs.getTimestamp("create_time");

				Filter filter = new FilterImpl(id, createUser, createTime, null);
				filter.setName(rs.getString("name"));
				filter.setXml(rs.getString("xml"));
				filter.setAnd(rs.getBoolean("is_and"));
				filter.setPublic(rs.getBoolean("is_public"));
				filter.setVisible(rs.getBoolean("is_visible"));

				filterList.add(filter);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		Map<UUID, Boolean> templateAllowMap = new HashMap<UUID, Boolean>();
		Map<UUID, List<Template>> allTemplateTypeMap = new HashMap<UUID, List<Template>>();
		Map<UUID, Template> allTemplateMap = new HashMap<UUID, Template>();
		Map<UUID, Flow> allFlowMap = new HashMap<UUID, Flow>();
		
		Iterator<Filter> filterItr = filterList.iterator();
		while(filterItr.hasNext()){
			Filter filter = filterItr.next();

			Document xmlDoc = null;
			try{
				xmlDoc = XMLUtil.string2Document(filter.getXml(), "UTF-8");
			}
			catch(Exception e){
				e.printStackTrace();
				System.err.println("error filter id: " + filter.getId());
			}

			if(xmlDoc == null){
				filterItr.remove();
				continue;
			}

			Node queryNode = XMLUtil.getSingleNode(xmlDoc, "query");
			Node templateTypeNode = XMLUtil.getSingleNode(queryNode, "templateType");
			List<Node> templateNodeList = XMLUtil.getNodes(queryNode, "template");

			List<Template> templateList = new ArrayList<Template>();

			if(templateNodeList.size() == 0){
				String templateTypeIdStr = XMLUtil.getAttribute(templateTypeNode, "id");
				UUID templateTypeId = DataAccessFactory.getInstance().createUUID(templateTypeIdStr);

				if (allTemplateTypeMap.get(templateTypeId) == null) {
					List<Template> templateTypeList = das.queryTemplates(templateTypeId); 
					for (Template template : templateTypeList) {
						allTemplateMap.put(template.getId(), template);
					}
				}
				templateList.addAll(allTemplateTypeMap.get(templateTypeId));
			}
			else{
				for(Node templateNode : templateNodeList){
					String templateIdStr = XMLUtil.getAttribute(templateNode, "id");
					UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);

					if (allTemplateMap.get(templateId) == null) {
						Template tmp = das.queryTemplate(templateId);
						allTemplateMap.put(templateId, tmp);
					}
					Template template = allTemplateMap.get(templateId);
					if(template != null){
						templateList.add(template);
					}
				}
			}

			boolean filterAllow = false;

			for(Template template : templateList){
				if(templateAllowMap.containsKey(template.getId())){
					if(templateAllowMap.get(template.getId())){
						filterAllow = true;
						break;
					}

					continue;
				}

				if (allFlowMap.get(template.getFlowId()) == null) {
					Flow tmp = das.queryFlow(template.getFlowId());
					allFlowMap.put(template.getFlowId(), tmp);
				}
				
				Flow flow = allFlowMap.get(template.getFlowId());
				
				if(flow == null){
					templateAllowMap.put(template.getId(), false);
					continue;
				}

				Role[] roleArray = flow.queryUserNodeRoles(user, template.getId());
				if(roleArray != null && roleArray.length > 0){
					filterAllow = true;
					templateAllowMap.put(template.getId(), true);
					break;
				}

				if(flow.isActionEveryoneRole(Action.readUUID) || flow.isActionEveryoneRole(Action.editUUID)){
					filterAllow = true;
					templateAllowMap.put(template.getId(), true);
					break;
				}

				Action[] actionArray = flow.getActions();
				if(actionArray != null){
					for(Action action : actionArray){
						if(flow.isActionEveryoneRole(action.getId())){
							filterAllow = true;
							templateAllowMap.put(template.getId(), true);
							break;
						}
					}
				}

				if(filterAllow){
					break;
				}

				templateAllowMap.put(template.getId(), false);
			}

			if(!filterAllow)
				filterItr.remove();
		}

		return filterList.toArray(new Filter[filterList.size()]);
	}
	
	public Map<String, String> getFilterIdNameMap(String userName){
		Map<String, String> idNameMap = new HashMap<String, String>();

		PreparedStatement pstm = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT id,name FROM filter WHERE create_user = ? and xml !=null order by name");
			pstm.setString(1, userName);
			rs = pstm.executeQuery();
			while(rs.next()){
				idNameMap.put(rs.getString("id"), rs.getString("name"));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return idNameMap;
	}
		
	public Filter[] querySysFilters(String user, DataAccessSession das)
	{
		List<Filter> filterList = new ArrayList<Filter>();

		PreparedStatement pstm = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM filter WHERE create_user = ?");
			pstm.setString(1, DataAccessFactory.sysUser);
			rs = pstm.executeQuery();
			while(rs.next()){
				UUID id = DataAccessFactory.getInstance().createUUID(Long.toString(rs.getLong("id")));
				Timestamp createTime = rs.getTimestamp("create_time");

				Filter filter = new FilterImpl(id, DataAccessFactory.sysUser, createTime, null);
				filter.setName(rs.getString("name"));
				filter.setXml(rs.getString("xml"));
				filter.setAnd(rs.getBoolean("is_and"));
				filter.setPublic(rs.getBoolean("is_public"));
				filter.setVisible(rs.getBoolean("is_visible"));

				filterList.add(filter);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return filterList.toArray(new Filter[filterList.size()]);
	}

	public List<Filter> queryAllFilters()
	{
		List<Filter> filterList = new ArrayList<Filter>();
		PreparedStatement pstm = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM filter WHERE is_valid = true");
			rs = pstm.executeQuery();
			while(rs.next()){
				UUID id = DataAccessFactory.getInstance().createUUID(Long.toString(rs.getLong("id")));
				Timestamp createTime = rs.getTimestamp("create_time");
				String createUser = rs.getString("create_user");
				Filter filter = new FilterImpl(id, createUser, createTime, null);
				filter.setName(rs.getString("name"));
				filter.setXml(rs.getString("xml"));
				filter.setAnd(rs.getBoolean("is_and"));
				filter.setPublic(rs.getBoolean("is_public"));
				filter.setVisible(rs.getBoolean("is_visible"));
				filterList.add(filter);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return filterList;
	}

	public Filter[] queryFocusFilters(String user, DataAccessSession das)
	{
		List<Filter> filterList = new ArrayList<Filter>();
		PreparedStatement pstm = null;
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM  filter WHERE is_valid = true AND (create_user = ? OR is_public = true AND create_user != ?)"
					+ " AND father_id IS NULL AND id IN(SELECT filter_id FROM user_focus_filter where user = ?)  ORDER BY name");
			pstm.setString(1, user);
			pstm.setString(2, DataAccessFactory.sysUser);
			pstm.setString(3, user);

			rs = pstm.executeQuery();
			while(rs.next())
			{
				UUID id = DataAccessFactory.getInstance().createUUID(Long.toString(rs.getLong("id")));
				String createUser = rs.getString("create_user");
				Timestamp createTime = rs.getTimestamp("create_time");

				Filter filter = new FilterImpl(id, createUser, createTime, null);
				filter.setName(rs.getString("name"));
				filter.setXml(rs.getString("xml"));
				filter.setAnd(rs.getBoolean("is_and"));
				filter.setPublic(rs.getBoolean("is_public"));
				filter.setVisible(rs.getBoolean("is_visible"));

				filterList.add(filter);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		
		//暂时不需要验证权限
		/*
		Map<UUID, Boolean> templateAllowMap = new HashMap<UUID, Boolean>();

		Iterator<Filter> filterItr = filterList.iterator();
		while(filterItr.hasNext()){
			Filter filter = filterItr.next();

			if(filter.getXml()==null||"".endsWith(filter.getXml()))
			{//add by lyl for filter init
				continue;
			}
			Document xmlDoc = null;
			try{
				xmlDoc = XMLUtil.string2Document(filter.getXml(), "UTF-8");
			}
			catch(Exception e){
				e.printStackTrace();
				System.err.println("error filter id: " + filter.getId());
			}

			if(xmlDoc == null){
				filterItr.remove();
				continue;
			}

			Node queryNode = XMLUtil.getSingleNode(xmlDoc, "query");
			Node templateTypeNode = XMLUtil.getSingleNode(queryNode, "templateType");
			List<Node> templateNodeList = XMLUtil.getNodes(queryNode, "template");

			List<Template> templateList = new ArrayList<Template>();

			if(templateNodeList.size() == 0){
				String templateTypeIdStr = XMLUtil.getAttribute(templateTypeNode, "id");
				UUID templateTypeId = DataAccessFactory.getInstance().createUUID(templateTypeIdStr);

				templateList.addAll(das.queryTemplates(templateTypeId));
			}
			else{
				for(Node templateNode : templateNodeList){
					String templateIdStr = XMLUtil.getAttribute(templateNode, "id");
					UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);

					Template template = das.queryTemplate(templateId);
					if(template != null){
						templateList.add(template);
					}
				}
			}

			boolean filterAllow = false;

			for(Template template : templateList){
				if(templateAllowMap.containsKey(template.getId())){
					if(templateAllowMap.get(template.getId())){
						filterAllow = true;
						break;
					}

					continue;
				}

				Flow flow = das.queryFlow(template.getFlowId());
				if(flow == null){
					templateAllowMap.put(template.getId(), false);
					continue;
				}

				Role[] roleArray = flow.queryUserNodeRoles(user, template.getNodeId());
				if(roleArray != null && roleArray.length > 0){
					filterAllow = true;
					templateAllowMap.put(template.getId(), true);
					break;
				}

				if(flow.isActionEveryoneRole(Action.readUUID) || flow.isActionEveryoneRole(Action.editUUID)){
					filterAllow = true;
					templateAllowMap.put(template.getId(), true);
					break;
				}

				Action[] actionArray = flow.getActions();
				if(actionArray != null){
					for(Action action : actionArray){
						if(flow.isActionEveryoneRole(action.getId())){
							filterAllow = true;
							templateAllowMap.put(template.getId(), true);
							break;
						}
					}
				}

				if(filterAllow){
					break;
				}

				templateAllowMap.put(template.getId(), false);
			}

			if(!filterAllow)
				filterItr.remove();
		}
		*/
		return filterList.toArray(new Filter[filterList.size()]);
	}

	public Map<String, String> queryFilterIdAndName(String userName)
	{
		Map<String, String> idNameMap = new LinkedHashMap<String, String>();
		Statement stat = null;
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			
			String sql = "select id ,name from filter where is_valid=1 ";
			if (userName != null && userName.length() > 0) {
				sql += " and id in (select DISTINCT filter_id from user_focus_filter where user = '" + userName + "') ";
			}
			sql += " order by name";
			
			rs = stat.executeQuery(sql);
			while(rs.next())
			{
				idNameMap.put(rs.getString("id"), rs.getString("name"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbPoolConnection.getInstance().closeAll(rs, stat, conn);
		}
		
		//添加系统过滤器
		idNameMap.putAll(ConfigUtil.allSysFilterMap);
		
		return idNameMap;
	}
	public Filter queryFilter(UUID id)
	{
		if(id == null)
		{
			return null;
		}
		Filter filter = null;

		PreparedStatement pstm = null;
		Connection conn = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM filter"
					+ " WHERE id = ?");
			pstm.setLong(1, Long.parseLong(id.getValue()));

			rs = pstm.executeQuery();
			if(rs.next())
			{
				String createUser = rs.getString("create_user");
				Timestamp createTime = rs.getTimestamp("create_time");
				UUID fatherId = null;
				if(rs.getObject("father_id") != null)
					fatherId = DataAccessFactory.getInstance().createUUID(rs.getObject("father_id").toString());

				filter = new FilterImpl(id, createUser, createTime, fatherId);
				filter.setName(rs.getString("name"));
				filter.setXml(rs.getString("xml"));
				filter.setAnd(rs.getBoolean("is_and"));
				filter.setPublic(rs.getBoolean("is_public"));
				filter.setVisible(rs.getBoolean("is_visible"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return filter;
	}

	public ErrorCode removeFilter(UUID id)
	{
		ErrorCode errorCode = ErrorCode.unknownFail;

		PreparedStatement pstm = null;
		Connection conn = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("DELETE FROM filter"
					+ " WHERE id = ?");
			pstm.setLong(1, Long.parseLong(id.getValue()));

			if (pstm.executeUpdate() > 0)
				errorCode = ErrorCode.success;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			errorCode = ErrorCode.dbFail;
		}
		finally
		{
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return errorCode;
	}

	public ErrorCode updateFilter(Filter filter)
	{
		ErrorCode errorCode = ErrorCode.unknownFail;

		PreparedStatement pstm = null;
		Connection conn = null;

		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("update filter"
					+ " SET	name = ?"
					+ ", xml = ?"
					+ ", is_and = ?"
					+ ", is_public = ?"
					+ ", is_visible = ?"
					+ ", is_valid = ?"
					+ " WHERE id = ?");
			pstm.setString(1, filter.getName());
			pstm.setString(2, filter.getXml());
			pstm.setBoolean(3, filter.isAnd());
			pstm.setBoolean(4, filter.isPublic());
			pstm.setBoolean(5, filter.isVisible());
			pstm.setBoolean(6, filter.isValid());
			pstm.setLong(7, Long.parseLong(filter.getId().getValue()));

			if(pstm.executeUpdate() > 0)
				errorCode = ErrorCode.success;

		}
		catch(Exception e)
		{
			e.printStackTrace();
			errorCode = ErrorCode.dbFail;
		}
		finally
		{
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return errorCode;
	}

	public Filter createFilter(String createUser, Timestamp createTime, UUID fatherId)
	{
		UUID id = DataAccessFactory.getInstance().newUUID("FILT");
		return new FilterImpl(id, createUser, createTime, fatherId);
	}

	public Filter creatTempFilter(String createUser, Timestamp createTime, UUID fatherId)
	{
		UUID id = DataAccessFactory.getInstance().createUUID("0");
		return new FilterImpl(id, createUser, createTime, fatherId);
	}

	public UUID[] queryUserFocusFilters(String user)
	{
		Set<UUID> filterIdSet = new LinkedHashSet<UUID>();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Connection conn = null;
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT filter_id FROM user_focus_filter"
					+ " WHERE user = ?");
			pstm.setString(1, user);

			rs = pstm.executeQuery();
			while(rs.next())
			{
				UUID id = DataAccessFactory.getInstance().createUUID(rs.getObject("filter_id").toString());
				filterIdSet.add(id);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return filterIdSet.toArray(new UUID[0]);
	}

	public ErrorCode removeFilterFocusUser(UUID filterId) {
		ErrorCode errorCode = ErrorCode.unknownFail;

		PreparedStatement pstm = null;
		Connection conn = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("DELETE FROM user_focus_filter"
					+ " WHERE filter_id = ?");
			pstm.setLong(1, Long.parseLong(filterId.getValue()));

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
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return errorCode;
	}
	
	public ErrorCode removeUserFocusFilter(UUID filterId)
	{
		ErrorCode errorCode = ErrorCode.unknownFail;

		PreparedStatement pstm = null;
		Connection conn = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("DELETE FROM user_focus_filter WHERE filter_id = ?");
			pstm.setLong(1, Long.parseLong(filterId.getValue()));

			if (pstm.executeUpdate() >0)
				errorCode = ErrorCode.success;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			errorCode = ErrorCode.dbFail;
		}
		finally
		{
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return errorCode;
	}
	
	public ErrorCode removeUserFocusFilter(String user, UUID filterId)
	{
		ErrorCode errorCode = ErrorCode.unknownFail;

		PreparedStatement pstm = null;
		Connection conn = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("DELETE FROM user_focus_filter"
					+ " WHERE user = ? AND filter_id = ?");
			pstm.setString(1, user);
			pstm.setLong(2, Long.parseLong(filterId.getValue()));

			if (pstm.executeUpdate() >0)
				errorCode = ErrorCode.success;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			errorCode = ErrorCode.dbFail;
		}
		finally
		{
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return errorCode;
	}

	public ErrorCode addUserFocusFilter(String user, UUID filterId)
	{
		ErrorCode errorCode = ErrorCode.unknownFail;

		PreparedStatement pstm = null;
		Connection conn = null;

		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			pstm = conn.prepareStatement("INSERT INTO user_focus_filter SET user = ?, filter_id = ?");
			pstm.setString(1, user);
			pstm.setLong(2, Long.parseLong(filterId.getValue()));

			if(pstm.executeUpdate() > 0)
				errorCode = ErrorCode.success;

		}
		catch(Exception e)
		{
			e.printStackTrace();

			errorCode = ErrorCode.dbFail;
		}
		finally
		{
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return errorCode;
	}

//	public HashMap<String,String> queryAllValidRelateUsers()
//	{
//		PreparedStatement pstm = null;
//		Connection conn = null;
//		ResultSet rs = null;
//		Map<String,String> relatedUserMap = new HashMap<String,String>();
//		try
//		{
//			conn = DbPoolConnection.getInstance().getReadConnection();
//			pstm = conn.prepareStatement("SELECT * FROM user_info"
//					+ " WHERE isValid = ?");
//			pstm.setInt(1, 1);
//
//			rs = pstm.executeQuery();
//			while(rs.next())
//			{
//				String relatedUser = rs.getString("nick_name");
//				String email = rs.getString("user_name");
//				relatedUserMap.put(email, relatedUser);
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			DbPoolConnection.getInstance().closeResultSet(rs);
//			DbPoolConnection.getInstance().closeStatment(pstm);
//			DbPoolConnection.getInstance().closeConn(conn);
//		}
//
//		return (HashMap<String, String>) relatedUserMap;
//	}
//
//	public HashMap<String,String> queryValidRelatedUser(String email)
//	{
//		PreparedStatement pstm = null;
//		Connection conn        = null;
//		ResultSet rs = null;
//		//Set<String> relatedUserSet = new LinkedHashSet<String>();
//		Map<String,String>relatedUserMap = new HashMap<String,String>();
//		try
//		{
//			conn = DbPoolConnection.getInstance().getReadConnection();
//			pstm = conn.prepareStatement("SELECT * FROM user_info"
//					+ " WHERE isValid = ? and user_name = ?");
//			pstm.setInt(1, 1);
//			pstm.setString(2, email);
//
//			rs = pstm.executeQuery();
//			while(rs.next())
//			{
//				String relatedUser = rs.getString("nick_name");
//				relatedUserMap.put(email, relatedUser);
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			DbPoolConnection.getInstance().closeResultSet(rs);
//			DbPoolConnection.getInstance().closeStatment(pstm);
//			DbPoolConnection.getInstance().closeConn(conn);
//		}
//
//		return (HashMap<String, String>) relatedUserMap;
//	}

//	public String[] queryRelatedUsers(String user)
//	{
//		PreparedStatement pstm = null;
//		Connection conn = null;
//		ResultSet rs = null;
//		Set<String> relatedUserSet = new LinkedHashSet<String>();
//		try
//		{
//			conn = DbPoolConnection.getInstance().getReadConnection();
//			pstm = conn.prepareStatement("SELECT * FROM user_info"
//					+ " WHERE user_name = ?");
//			pstm.setString(1, user);
//
//			rs = pstm.executeQuery();
//			while(rs.next())
//			{
//				String relatedUser = rs.getString("nick_name");
//				relatedUserSet.add(relatedUser);
//			}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			DbPoolConnection.getInstance().closeResultSet(rs);
//			DbPoolConnection.getInstance().closeStatment(pstm);
//			DbPoolConnection.getInstance().closeConn(conn);
//		}
//
//		return relatedUserSet.toArray(new String[0]);
//	}
//
//	public ErrorCode updateUser(String user,String relatedUser)
//	{
//		ErrorCode errorCode = ErrorCode.unknownFail;
//
//		PreparedStatement pstm = null;
//		Connection conn = null;
//		try
//		{
//			conn = DbPoolConnection.getInstance().getConnection();
//			pstm = conn.prepareStatement("update user_info"
//					+ " SET nick_name = ? where user_name = ?");
//			pstm.setString(1, relatedUser);
//			pstm.setString(2, user);
//
//			if(pstm.executeUpdate() >0)
//				errorCode = ErrorCode.success;
//
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//
//			errorCode = ErrorCode.dbFail;
//		}
//		finally
//		{
//			DbPoolConnection.getInstance().closeStatment(pstm);
//			DbPoolConnection.getInstance().closeConn(conn);
//		}
//
//		return errorCode;
//	}
//
//	public ErrorCode removeValidRelatedUser(String user,String relatedUser)
//	{
//		ErrorCode errorCode = ErrorCode.unknownFail;
//
//		PreparedStatement pstm = null;
//		Connection conn = null;
//		try
//		{
//			conn = DbPoolConnection.getInstance().getConnection();
//			pstm = conn.prepareStatement("update user_info"
//					+ " set isValid = ? WHERE user_name = ? AND nick_name = ?");
//			pstm.setInt(1, 0);
//			pstm.setString(2, user);
//			pstm.setString(3, relatedUser);
//			pstm.execute();
//			errorCode = ErrorCode.success;
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//
//			errorCode = ErrorCode.dbFail;
//		}
//		finally
//		{
//			DbPoolConnection.getInstance().closeStatment(pstm);
//			DbPoolConnection.getInstance().closeConn(conn);
//		}
//
//		return errorCode;
//	}
//
//	public ErrorCode addRelatedUser(String user, String relatedUser)
//	{
//		ErrorCode errorCode = ErrorCode.unknownFail;
//		PreparedStatement pstm = null;
//		Connection conn = null;
//		try
//		{
//			conn = DbPoolConnection.getInstance().getConnection();
//			pstm = conn.prepareStatement("INSERT INTO user_info"
//					+ " SET user_name = ?, nick_name = ?");
//			pstm.setString(1, user);
//			pstm.setString(2, relatedUser);
//			if(pstm.executeUpdate()>0)
//				errorCode = ErrorCode.success;
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			errorCode = ErrorCode.dbFail;
//		}
//		finally
//		{
//			DbPoolConnection.getInstance().closeStatment(pstm);
//			DbPoolConnection.getInstance().closeConn(conn);
//		}
//
//		return errorCode;
//	}
//
//	public ErrorCode removeRelatedUser(String user, String relatedUser)
//	{
//		ErrorCode errorCode = ErrorCode.unknownFail;
//		PreparedStatement pstm = null;
//		Connection conn = null;
//		try
//		{
//			conn = DbPoolConnection.getInstance().getConnection();
//			pstm = conn.prepareStatement("DELETE FROM user_info"
//					+ " WHERE user_name = ? AND nick_name = ?");
//			pstm.setString(1, user);
//			pstm.setString(2, relatedUser);
//
//			if(pstm.executeUpdate() > 0)
//				errorCode = ErrorCode.success;
//
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			errorCode = ErrorCode.dbFail;
//		}
//		finally
//		{
//			DbPoolConnection.getInstance().closeStatment(pstm);
//			DbPoolConnection.getInstance().closeConn(conn);
//		}
//
//		return errorCode;
//	}
//

	public List<String> queryFocusUsersByFilter(UUID filterId){
		List<String> userList = new ArrayList<String>();
		PreparedStatement pstm = null;
		Connection conn = null; 
		ResultSet rs = null;
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT USER FROM user_focus_filter where filter_id=?");
			pstm.setLong(1, Long.parseLong(filterId.getValue()));
			rs = pstm.executeQuery();
			while(rs.next()){
				userList.add(rs.getString("user"));
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DbPoolConnection.getInstance().closeResultSet(rs);
			DbPoolConnection.getInstance().closeStatment(pstm);
			DbPoolConnection.getInstance().closeConn(conn);
		}

		return userList;
	}


}
