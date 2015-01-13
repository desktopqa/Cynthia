package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.sogou.qadev.service.cynthia.bean.JSTree;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;
import com.sogou.qadev.service.cynthia.util.ArrayUtil;
import com.sogou.qadev.service.cynthia.util.FilterUtil;

/**
 * @description:jstree db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:38:58
 * @version:v1.0
 */
public class JSTreeAccessSessionMySQL {

	public JSTreeAccessSessionMySQL()
	{
	}

	/**
	 * @description:get jstree node from node id
	 * @date:2014-5-6 下午5:38:45
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public JSTree getNodeById(int id)
	{
		JSTree jsTree = new JSTree();
		PreparedStatement pstm = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			pstm = conn.prepareStatement("SELECT * FROM tree WHERE id =?");
			pstm.setInt(1, id);
			rs = pstm.executeQuery();
			if(rs.next()){
				jsTree.setId(rs.getInt("id"));
				jsTree.setParentId(rs.getInt("parent_id"));
				jsTree.setPosition(rs.getInt("position"));
				jsTree.setUserName(rs.getString("user_name"));
				jsTree.setTitle(rs.getString("title"));
				jsTree.setFilters(rs.getString("filters"));
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);

		}
		return jsTree;
	}

	/**
	 * @description:get all root node by node id
	 * @date:2014-5-6 下午5:39:12
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public List<JSTree> getRootNode(int id)
	{
		PreparedStatement pstm = null;
		JSTree rootNode = new JSTree();
		List<JSTree> result = new ArrayList<JSTree>();
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select * from tree where parent_id="+id+"";
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			if(rs.next()){
				rootNode.setId(rs.getInt("id"));
				rootNode.setParentId(rs.getInt("parent_id"));
				rootNode.setPosition(rs.getInt("position"));
				rootNode.setUserName(rs.getString("user_name"));
				rootNode.setTitle(rs.getString("title"));
				result.add(rootNode);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);

		}
		return result;
	}

	/**
	 * @description:get children jstree node from node id
	 * @date:2014-5-6 下午5:39:27
	 * @version:v1.0
	 * @param id
	 * @param userName
	 * @return
	 */
	public List<JSTree> getNodeChilden(int id,String userName)
	{
		List<JSTree> result = new ArrayList<JSTree>();

		PreparedStatement pstm = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select * from tree where parent_id="+id+" and user_name='"+userName+"' order by title";
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			while(rs.next()){
				JSTree jsTree = new JSTree();
				jsTree.setId(rs.getInt("id"));
				jsTree.setParentId(rs.getInt("parent_id"));
				jsTree.setPosition(rs.getInt("position"));
				jsTree.setUserName(rs.getString("user_name"));
				jsTree.setTitle(rs.getString("title"));
				jsTree.setFilters(rs.getString("filters"));
				result.add(jsTree);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return result;
	}

	/**
	 * @description:get all filters from folder
	 * @date:2014-5-6 下午5:39:48
	 * @version:v1.0
	 * @param userName
	 * @return
	 */
	public List<String> getAllFolderFilters(String userName)
	{
		List<String> results = new ArrayList<String>();
		PreparedStatement pstm = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select * from tree where user_name='"+userName+"'";
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			while(rs.next()){
				String filters = rs.getString("filters");
				if(filters != null&& !"".equals(filters))
				{
					results.addAll(Arrays.asList(filters.split(",")));
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);

		}
		return results;
	}

	/**
	 * @description:get all folder filters
	 * @date:2014-5-6 下午5:40:10
	 * @version:v1.0
	 * @param nodeId
	 * @return
	 */
	public List<String> getFolderFilters(int nodeId)
	{
		List<String> folderFilterList = new ArrayList<String>();
		PreparedStatement pstm = null;
		Connection conn = null;
		ResultSet rs = null;
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String sql = "select * from tree where id= "+nodeId;
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			while(rs.next()){
				String filters = rs.getString("filters");
				if(filters!=null&&!"".equals(filters))
				{
					folderFilterList.addAll(Arrays.asList(filters.split(",")));
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return folderFilterList;
	}

	/**
	 * @description:add jstree node 
	 * @date:2014-5-6 下午5:40:22
	 * @version:v1.0
	 * @param parentId
	 * @param position
	 * @param title
	 * @param userName
	 * @return
	 */
	public int addNode(int parentId,int position,String title,String userName)
	{
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int result = -1;
		Connection conn = null;
		try{
			conn = DbPoolConnection.getInstance().getConnection();

			//更新位置信息
			String updatePosition = "update tree set position=(position+1) where parent_id=? and position >=? and user_name=?";
			pstm = conn.prepareStatement(updatePosition);
			pstm.setInt(1, parentId);
			pstm.setInt(2, position);
			pstm.setString(3, userName);
			pstm.execute();

			String sql = "insert into tree (parent_id,position,title,user_name) values (?,?,?,?)";
			pstm = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			pstm.setInt(1, parentId);
			pstm.setInt(2, position);
			pstm.setString(3, title);
			pstm.setString(4, userName);
			pstm.execute();
			rs = pstm.getGeneratedKeys();
			if(rs.next())
				result = rs.getInt(1);
			return result;
		}catch(Exception e)
		{
			e.printStackTrace();
			return result;
		}finally{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
	}

	/**
	 * @description:remove all childern node from node id
	 * @date:2014-5-6 下午5:40:44
	 * @version:v1.0
	 * @param id
	 * @param userName
	 */
	public void removeChildNode(int id, String userName){
		Connection conn = null;
		PreparedStatement pstm = null;
		try {
			conn = DbPoolConnection.getInstance().getConnection();
			List<JSTree> children = this.getNodeChilden(id,userName);
			if(children!=null&&children.size()>0) {
				for (JSTree jsTree : children) {
					removeChildNode(jsTree.getId(), userName);
				}
				String ids = "(";
				int i=0;
				for(JSTree treeNode : children)
				{
					if(i==0)
						ids+=treeNode.getId();
					else
						ids+=(","+treeNode.getId());
				}
				ids+=")";
				//删除孩子节点
				String deleteChildrenSql = "delete from tree where id in "+ids;
				pstm = conn.prepareStatement(deleteChildrenSql);
				pstm.execute();
			}
			
			JSTree node = this.getNodeById(id);

			//删除关注节点
			String filterIdsStr = node.getFilters();
			if (filterIdsStr != null) {
				String[] filterArray = filterIdsStr.split(",");
				if(filterArray != null && filterArray.length >0){
					for (String filterIdStr : filterArray) {
						DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
						das.removeUserFocusFilter(DataAccessFactory.getInstance().createUUID(filterIdStr));  
					}
				}
			}
			
			//删除定时器
			String deleteTimerSql = "delete from timer where filter_id in (" + filterIdsStr + ")" ;
			pstm = conn.prepareStatement(deleteTimerSql);
			pstm.execute();
			
			//更新同事节点的位置信息
			String updatePositionSql = "update tree set position=(position-1) where parent_id=? and position >?";
			pstm = conn.prepareStatement(updatePositionSql);
			pstm.setInt(1, node.getParentId());
			pstm.setInt(2, node.getPosition());
			pstm.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
		
	}
	
	/**
	 * @description:remove jstree node by node id
	 * @date:2014-5-6 下午5:41:07
	 * @version:v1.0
	 * @param id
	 * @param userName
	 * @return
	 */
	public boolean removeNode(int id,String userName)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		try{
			conn = DbPoolConnection.getInstance().getConnection();
			removeChildNode(id, userName);  //删除文件夹下过滤器
			String sql = "delete from tree where id = ? ";
			pstm = conn.prepareStatement(sql);
			pstm.setInt(1, id);
			if(pstm.executeUpdate() >0)
				return true;
			else 
				return false;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
	}

	/**
	 * @description:remove filter id from jstree
	 * @date:2014-5-6 下午5:41:27
	 * @version:v1.0
	 * @param filterId
	 * @param parentId
	 * @return
	 */
	public boolean removeFilterId(int filterId,int parentId)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		JSTree parentNode = this.getNodeById(parentId);
		try{
			conn = DbPoolConnection.getInstance().getConnection();
			String oldFiltersStr = parentNode.getFilters();
			if(oldFiltersStr!=null&&!"".equals(oldFiltersStr))
			{
				String[] oldFiltersOld = oldFiltersStr.split(",");
				List<String> oldFiltersList= new ArrayList<String>();
				for(String str : oldFiltersOld)
				{
					if(!str.equals(Integer.toString(filterId)))
						oldFiltersList.add(str);
				}

				StringBuffer sb = new StringBuffer();
				for(int i=0;i<oldFiltersList.size();i++)
				{
					if(i==0)
						sb.append(oldFiltersList.get(i));
					else
					{
						sb.append(",");
						sb.append(oldFiltersList.get(i));
					}

				}
				oldFiltersStr = sb.toString();
			}

			String updateOldFiltersSql = "update tree set filters = ? where id=?";

			pstm = conn.prepareStatement(updateOldFiltersSql);
			pstm.setString(1, oldFiltersStr);
			pstm.setInt(2, parentId);
			if (pstm.executeUpdate() >0) {
				return true;
			}else {
				return false;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
	}

	/**
	 * @description:add filter to jstree node
	 * @date:2014-5-6 下午5:41:45
	 * @version:v1.0
	 * @param filterId
	 * @param nodeId
	 * @return
	 */
	public boolean addFilterToFolder(String filterId,int nodeId)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		JSTree node = this.getNodeById(nodeId);
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String oldFilters = node.getFilters();
			String newFilters = null;
			if(oldFilters==null||"".equals(oldFilters))
			{
				newFilters = filterId;
			}else
			{
				newFilters = oldFilters+","+filterId;
			}

			String updateSql = "update tree set filters = ? where id=?";
			pstm = conn.prepareStatement(updateSql);
			pstm.setString(1, newFilters);
			pstm.setInt(2, nodeId);
			if (pstm.executeUpdate() >0) {
				return true;
			}else {
				return false;
			}
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
	 * @description:move filter to new position
	 * @date:2014-5-6 下午5:41:57
	 * @version:v1.0
	 * @param filterId
	 * @param refId
	 * @param parentId
	 * @return
	 */
	public boolean moveFilter(int filterId,int refId,int parentId)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		JSTree refNode = this.getNodeById(refId);
		JSTree parentNode = this.getNodeById(parentId);
		try{
			if(refId == parentId)
				return true;
			conn = DbPoolConnection.getInstance().getConnection();

			String oldFiltersStr = parentNode.getFilters();
			String newFiltersStr = refNode.getFilters();
			//先加入新的文件夹
			if(newFiltersStr!=null&&!"".equals(newFiltersStr))
			{
				if(newFiltersStr.contains(Integer.toString(filterId)))
					return true;
				String[] newFiltersOld = newFiltersStr.split(",");
				StringBuffer sb = new StringBuffer();
				for(String str : newFiltersOld)
				{
					sb.append(str+",");
				}
				sb.append(Integer.toString(filterId));
				newFiltersStr = sb.toString();

			}else{
				newFiltersStr = Integer.toString(filterId);
			}
			//获取原目录的文件夹filter
			if(oldFiltersStr!=null&&!"".equals(oldFiltersStr))
			{
				String[] oldFiltersOld = oldFiltersStr.split(",");
				List<String> oldFiltersList= new ArrayList<String>();
				for(String str : oldFiltersOld)
				{
					if(!str.equals(Integer.toString(filterId)))
						oldFiltersList.add(str);
					else
						System.out.println("shan chu le"+str);
				}

				StringBuffer sb = new StringBuffer();
				for(int i=0;i<oldFiltersList.size();i++)
				{
					if(i==0)
						sb.append(oldFiltersList.get(i));
					else
					{
						sb.append(",");
						sb.append(oldFiltersList.get(i));
					}

				}
				oldFiltersStr = sb.toString();
			}

			String updateNewFiltersSql = "update tree set filters = ? where id=?";
			String updateOldFiltersSql = "update tree set filters = ? where id=?";

			pstm = conn.prepareStatement(updateNewFiltersSql);
			pstm.setString(1, newFiltersStr);
			pstm.setInt(2, refId);
			pstm.execute();

			pstm = conn.prepareStatement(updateOldFiltersSql);
			pstm.setString(1, oldFiltersStr);
			pstm.setInt(2, parentId);
			if (pstm.executeUpdate() >0) {
				return true;
			}else {
				return false;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
	}

	/**
	 * @description:move node to new position
	 * @date:2014-5-6 下午5:42:13
	 * @version:v1.0
	 * @param id
	 * @param refId
	 * @param position
	 * @param title
	 * @param copy
	 * @param userName
	 * @return
	 */
	public boolean moveNode(int id,int refId,int position,String title,boolean copy,String userName){

		Connection conn = null;
		PreparedStatement pstm = null;

		JSTree node = this.getNodeById(id);
		JSTree refNode = this.getNodeById(refId);
		
		try{
			conn = DbPoolConnection.getInstance().getConnection();
			if(!copy)
			{

				if(node.getParentId()==refId)
				{
					int oldPosition = node.getPosition();
					int newPosition = position;
					if(newPosition>oldPosition)
					{
						String updatePositionSql = "update tree set position=(position-1) where position>? and position <=? and parent_id=? and user_name=?";
						pstm = conn.prepareStatement(updatePositionSql);
						pstm.setInt(1, oldPosition);
						pstm.setInt(2, newPosition);
						pstm.setInt(3, node.getParentId());
						pstm.setString(4, userName);
						pstm.execute();
					}else if(newPosition<oldPosition)
					{
						String updatePositionSql = "update tree set position=(position+1) where position>=? and position<? and parent_id=? and user_name=?";
						pstm = conn.prepareStatement(updatePositionSql);
						pstm.setInt(1, newPosition);
						pstm.setInt(2, oldPosition);
						pstm.setInt(3, node.getParentId());
						pstm.setString(4, userName);
						pstm.execute();
					}
					//update the node
					String updateSql = "update tree set position=? where id=?";
					pstm = conn.prepareStatement(updateSql);
					pstm.setInt(1, newPosition);
					pstm.setInt(2, id);
					if (pstm.executeUpdate() >0) {
						return true;
					}else {
						return false;
					}
				}else{
					String updateOldNodePositionSql = "update tree set position=(position-1) where parent_id=? and position>? and user_name=?";
					pstm = conn.prepareStatement(updateOldNodePositionSql);
					pstm.setInt(1, node.getParentId());
					pstm.setInt(2, node.getPosition());
					pstm.setString(3, userName);
					pstm.execute();

					String updateNewNodePositionSql = "update tree set position=(position+1) where parent_id=? and position>=? and user_name=?";
					pstm = conn.prepareStatement(updateNewNodePositionSql);
					pstm.setInt(1, refId);
					pstm.setInt(2, position);
					pstm.setString(3, userName);
					pstm.execute();

					//update node
					String updateSql = "update tree set position=? ,parent_id=? where id=?";
					pstm = conn.prepareStatement(updateSql);
					pstm.setInt(1, position);
					pstm.setInt(2, refId);
					pstm.setInt(3, id);
					if (pstm.executeUpdate() >0) {
						return true;
					}else {
						return false;
					}
				}
			}

		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
		return true;
	}

	/**
	 * @description:update node name
	 * @date:2014-5-6 下午5:42:31
	 * @version:v1.0
	 * @param id
	 * @param title
	 * @return
	 */
	public boolean updateNodeName(int id,String title){
		Connection conn = null;
		PreparedStatement pstm = null;
		try{
			conn = DbPoolConnection.getInstance().getConnection();
			String updateSql = "update tree set title=? where id=?";
			pstm = conn.prepareStatement(updateSql);
			pstm.setString(1, title);
			pstm.setInt(2, id);
			if (pstm.executeUpdate() >0) {
				return true;
			}else {
				return false;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally{
			DbPoolConnection.getInstance().closeAll(pstm, conn);
		}
	}

	/**
	 * @description:get favarote filters of user
	 * @date:2014-5-6 下午5:42:42
	 * @version:v1.0
	 * @param userName
	 * @return
	 */
	public String[] getFavorateFilters(String userName)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int count = 0;
		String[] favoriteFilters = new String[0];
		String favoriteFiltersStr = null;
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String isExistsSql = "select count(*) from favorite_filters where user_name=?";
			pstm = conn.prepareStatement(isExistsSql);
			pstm.setString(1, userName);
			rs = pstm.executeQuery();
			if(rs.next())
			{
				count = rs.getInt(1);
			}
			if(count == 0)//表示没有记录存在需要先创建一个记录并添加默认系统过滤器
			{
				String createSql = "insert into favorite_filters (user_name,filters) values (?,?)";
				pstm = conn.prepareStatement(createSql);
				pstm.setString(1, userName);
				pstm.setString(2, ArrayUtil.strArray2String(FilterUtil.systemFilter.toArray(new String[0])));
				pstm.execute();
			}

			String querySql = "select * from favorite_filters where user_name=?";
			pstm = conn.prepareStatement(querySql);
			pstm.setString(1, userName);
			rs = pstm.executeQuery();
			while(rs.next())
			{
				favoriteFiltersStr = rs.getString("filters");
			}

			if(favoriteFiltersStr!=null)
				favoriteFilters = favoriteFiltersStr.split(",");
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}

		return favoriteFilters;
	}

	/**
	 * @description:add favorite filter
	 * @date:2014-5-6 下午5:42:58
	 * @version:v1.0
	 * @param userName
	 * @param filterId
	 * @return
	 */
	public boolean addFavoriteFilters(String userName,String filterId)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		String favoriteFiltersStr = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String querySql = "select * from favorite_filters where user_name=?";
			pstm = conn.prepareStatement(querySql);
			pstm.setString(1, userName);
			rs = pstm.executeQuery();
			while(rs.next())
			{
				favoriteFiltersStr = rs.getString("filters");
			}
			if(favoriteFiltersStr!=null&&!"".equals(favoriteFiltersStr))
			{
				if(favoriteFiltersStr.indexOf(filterId)>=0)
				{
					return true;
				}
				String[] newFiltersOld = favoriteFiltersStr.split(",");
				StringBuffer sb = new StringBuffer();
				for(String str : newFiltersOld)
				{
					sb.append(str+",");
				}
				sb.append(filterId);
				favoriteFiltersStr = sb.toString();

			}else{
				favoriteFiltersStr = filterId;
			}

			String updateSql = "update favorite_filters set filters=? where user_name=?";
			pstm = conn.prepareStatement(updateSql);
			pstm.setString(1, favoriteFiltersStr);
			pstm.setString(2, userName);
			if (pstm.executeUpdate() >0) {
				return true;
			}else {
				return false;
			}

		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
	}

	/**
	 * @description:remove favoriter filter of user
	 * @date:2014-5-6 下午5:43:11
	 * @version:v1.0
	 * @param userName
	 * @param filterId
	 * @return
	 */
	public boolean removeFavoriteFilters(String userName,String filterId)
	{

		Connection conn = null;
		PreparedStatement pstm = null;
		String favoriteFiltersStr = null;
		ResultSet rs = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			String querySql = "select * from favorite_filters where user_name=?";
			pstm = conn.prepareStatement(querySql);
			pstm.setString(1, userName);
			rs = pstm.executeQuery();
			while(rs.next())
			{
				favoriteFiltersStr = rs.getString("filters");
			}
			if(favoriteFiltersStr!=null&&!"".equals(favoriteFiltersStr))
			{
				String[] oldFiltersOld = favoriteFiltersStr.split(",");
				List<String> oldFiltersList= new ArrayList<String>();
				for(String str : oldFiltersOld)
				{
					if(!str.equals(filterId))
						oldFiltersList.add(str);
				}

				StringBuffer sb = new StringBuffer();
				for(int i=0;i<oldFiltersList.size();i++)
				{
					if(i==0)
						sb.append(oldFiltersList.get(i));
					else
					{
						sb.append(",");
						sb.append(oldFiltersList.get(i));
					}

				}
				favoriteFiltersStr = sb.toString();
			}

			String updateSql = "update favorite_filters set filters=? where user_name=?";
			pstm = conn.prepareStatement(updateSql);
			pstm.setString(1, favoriteFiltersStr);
			pstm.setString(2, userName);
			if (pstm.executeUpdate() >0) {
				return true;
			}else {
				return false;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
	}

	/**
	 * @description:update favorite filters of user
	 * @date:2014-5-6 下午5:43:23
	 * @version:v1.0
	 * @param filterarrays
	 * @param userName
	 * @return
	 */
	public boolean updateFavoriteFilters(String filterarrays,String userName)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		if(filterarrays!=null&&!"".equals(filterarrays))
		{
			String[] filterIds = filterarrays.split(",");
			Set<String> filterIdsSet = new LinkedHashSet<String>();
			for(String filterId : filterIds)
			{
				if(filterId!=null&&!"".equals(filterId))
				{
					filterIdsSet.add(filterId);
				}
			}
			filterarrays = ArrayUtil.strArray2String(filterIdsSet.toArray(new String[0]));
		}
		try{
			conn = DbPoolConnection.getInstance().getConnection();
			String updateSql = "update favorite_filters set filters=? where user_name=?";
			pstm = conn.prepareStatement(updateSql);
			pstm.setString(1, filterarrays);
			pstm.setString(2, userName);
			if (pstm.executeUpdate() >0) {
				return true;
			}else {
				return false;
			}

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
	 * @description:update favorite position of user
	 * @date:2014-5-6 下午5:43:39
	 * @version:v1.0
	 * @param filterId
	 * @param position
	 * @param userName
	 * @return
	 */
	public boolean updateFavorites(String filterId,int position,String userName)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		try{
			conn = DbPoolConnection.getInstance().getConnection();
			String[] favorites = this.getFavorateFilters(userName);
			if(favorites == null||favorites.length==0)
			{
				return false;
			}
			if(position<0||position>=favorites.length)
				return false;
			int oldPosition = -1;
			for(int i=0;i<favorites.length;i++)
			{
				if(favorites[i].equals(filterId))
				{
					oldPosition = i;
				}
			}
			if(oldPosition == -1)
			{
				return false;
			}
			String positionFilterId = favorites[position];
			favorites[oldPosition] = positionFilterId;
			favorites[position] = filterId;

			String newData = ArrayUtil.strArray2String(favorites);
			return this.updateFavoriteFilters(newData, userName);
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
	 * @description:update all filter orders
	 * @date:2014-5-6 下午5:44:00
	 * @version:v1.0
	 * @param folderId
	 * @param userName
	 * @param newOrders
	 * @return
	 */
	public boolean updateFiltersOrder(int folderId,String userName,String newOrders)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		if(newOrders!=null&&!"".equals(newOrders))
		{
			String[] filterIds = newOrders.split(",");
			Set<String> filterIdsSet = new LinkedHashSet<String>();
			for(String filterId : filterIds)
			{
				if(filterId!=null&&!"".equals(filterId))
				{
					filterIdsSet.add(filterId);
				}
			}
			newOrders = ArrayUtil.strArray2String(filterIdsSet.toArray(new String[0]));
		}
		try{
			conn = DbPoolConnection.getInstance().getConnection();
			String sql = "update tree set filters =? where id=?";
			pstm = conn.prepareStatement(sql);
			pstm.setString(1, newOrders);
			pstm.setInt(2, folderId);
			if (pstm.executeUpdate() >0) {
				return true;
			}else {
				return false;
			}
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
	 * @description:update user default filters
	 * @date:2014-5-6 下午5:44:15
	 * @version:v1.0
	 * @param userName
	 * @param filters
	 * @return
	 */
	public boolean updateDefaultFilters(String userName,String filters)
	{
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		if(filters!=null&&!"".equals(filters))
		{
			String[] filterIds = filters.split(",");
			Set<String> filterIdsSet = new LinkedHashSet<String>();
			for(String filterId : filterIds)
			{
				if(filterId!=null&&!"".equals(filterId))
				{
					filterIdsSet.add(filterId);
				}
			}
			filters = ArrayUtil.strArray2String(filterIdsSet.toArray(new String[0]));
		}
		try{
			conn = DbPoolConnection.getInstance().getConnection();
			String countsSql = "select count(*) from default_filters where user_name=?";
			pstm = conn.prepareStatement(countsSql);
			pstm.setString(1, userName);
			rs = pstm.executeQuery();
			boolean exists = false;
			while(rs.next())
			{
				if(rs.getInt(1)>0)
					exists = true;
			}
			if(exists)
			{
				String updateSql = "update default_filters set filters=? where user_name=?";
				pstm = conn.prepareStatement(updateSql);
				pstm.setString(1, filters);
				pstm.setString(2, userName);
				if (pstm.executeUpdate() >0) {
					return true;
				}else {
					return false;
				}
			}else
			{
				String insertSql = "insert into default_filters (user_name,filters) values(?,?)";
				pstm = conn.prepareStatement(insertSql);
				pstm.setString(1, userName);
				pstm.setString(2, filters);
				if (pstm.executeUpdate() >0) {
					return true;
				}else {
					return false;
				}
			}


		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
	}

	/**
	 * @description:get all default filters of user
	 * @date:2014-5-6 下午5:44:28
	 * @version:v1.0
	 * @param userName
	 * @return
	 */
	public List<String> getDefaultFilters(String userName)
	{
		List<String> defaultFilters = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try{
			conn = DbPoolConnection.getInstance().getReadConnection();
			String querySql = "select * from default_filters where user_name=?";
			pstm = conn.prepareStatement(querySql);
			pstm.setString(1, userName);
			rs = pstm.executeQuery();
			while(rs.next())
			{
				String defaultFiltersStr = rs.getString("filters");
				if(defaultFiltersStr!=null&&!"".equals(defaultFiltersStr))
				{
					defaultFilters.addAll(Arrays.asList(defaultFiltersStr.split(",")));
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			DbPoolConnection.getInstance().closeAll(rs, pstm, conn);
		}
		return defaultFilters;
	}

}
