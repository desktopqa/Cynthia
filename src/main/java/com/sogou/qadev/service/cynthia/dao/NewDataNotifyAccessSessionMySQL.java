package com.sogou.qadev.service.cynthia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sogou.qadev.service.cynthia.bean.Filter;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.DbPoolConnection;
import com.sogou.qadev.service.cynthia.service.impl.DataFilterMemory;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;
import com.sogou.qadev.service.cynthia.util.XMLUtil;
/**
 * @description:new old data db processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:46:39
 * @version:v1.0
 */
public class NewDataNotifyAccessSessionMySQL
{
	private DataAccessSession das = null;

	private class FilterDataQueryWorker implements Runnable
	{
		private List<UUID> filterIdList;
		private Map<UUID, Integer> filterDataCountMap;
		private String username; 
		private CountDownLatch countDown;

		public FilterDataQueryWorker(List<UUID> filterIdList, Map<UUID, Integer> filterDataCountMap , String username , CountDownLatch countDown) {
			super();
			this.filterIdList = filterIdList;
			this.filterDataCountMap = filterDataCountMap;
			this.username = username;
			this.countDown = countDown;
		}


		public void run(){
			while (true) {
				UUID newfilterId = null;
				int filterCount = 0;
				
				synchronized (filterIdList) {
					if (filterIdList.size() == 0) {
						break;
					}
					newfilterId = filterIdList.remove(0);
				}
				
				if (newfilterId != null) {
					try {
						filterCount = getFilterCount(newfilterId, username);
					} catch (TransformerException e) {
						e.printStackTrace();
					}
				}
				
				synchronized (filterIdList) {
					filterDataCountMap.put(newfilterId, filterCount);
				}
			}
			countDown.countDown();
			
		}
	}

	private static ExecutorService threadPool = null;

	public static ExecutorService getThreadPool(int threadCount)
	{
		if(threadPool == null)
			threadPool = Executors.newFixedThreadPool(threadCount);

		return threadPool;
	}
	
	
	public NewDataNotifyAccessSessionMySQL(DataAccessSession das)
	{
		super();
		this.das = das;
	}

	public String getNewTaskIdsByFilterAndUser(UUID[] filterIdArray, String username)
	{
		Map<UUID, Set<String>> filterDataMap = getFilterNewOldTasks(filterIdArray, username); //过滤器对应旧数据集合

		String retXML = turnFilterDataMapToXMLForQuery(filterDataMap, username);

		return retXML;
	}

	public String cleanNewTagByTaskIds(UUID filterId, UUID[] taskIdArray, String username)
	{
		insertFilterUserTasks(filterId, username, taskIdArray);
		return "success";
	}

	
	protected String turnFilterDataMapToXMLForQuery(Map<UUID, Set<String>> filterDataMap, String username)
	{
		StringBuffer xmlBuffer = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		xmlBuffer.append("<filters>");

		Map<UUID, Integer> filterCountMap = new HashMap<UUID, Integer>();
		
		List<UUID> allFilterList = new ArrayList<UUID>(filterDataMap.keySet());
		//开启线程数
		int threadCount = filterDataMap.keySet().size() > 20 ? 5 : 2;

		CountDownLatch countDown = new CountDownLatch(threadCount);
		
		for (int i = 0; i < threadCount; i++) {
			new Thread(new FilterDataQueryWorker(allFilterList , filterCountMap , username , countDown)).start();
		}
		
		try{
			countDown.await();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		for(UUID filterId : filterDataMap.keySet())
		{
			xmlBuffer.append("<filter id=\"").append(filterId.getValue()).append("\"");

			int totalCount = 0;
			int oldTotal = 0;

			Set<String> oldSet = filterDataMap.get(filterId);
			
			oldTotal = oldSet.size();
			
			totalCount = filterCountMap.get(filterId);

			xmlBuffer.append(" oldAccount=\"").append(oldTotal).append("\"");
			int newCount = totalCount - oldTotal ;
			if (newCount < 0) {
				newCount = 0;
			}
			xmlBuffer.append(" newAccount=\"").append(newCount).append("\"");
			xmlBuffer.append(" totalAccount=\"").append(totalCount).append("\"");
			xmlBuffer.append(" maxAccount=\"").append(totalCount).append("\">");

			StringBuffer oldBuffer = new StringBuffer(500);
			
			for(String taskId : oldSet)
			{
				oldBuffer.append(oldBuffer.length() > 0 ? "," : "").append(taskId);
			}

			xmlBuffer.append("<oldTasks>").append(oldBuffer).append("</oldTasks>");
			xmlBuffer.append("</filter>");
		}

		xmlBuffer.append("</filters>");
		return xmlBuffer.toString();
	}
	
	/**
	 * @Title: getFilterCount
	 * @Description: 查询过滤器数据总量
	 * @param filterId
	 * @param username
	 * @return
	 * @throws TransformerException
	 * @return: int
	 */
	private int getFilterCount(UUID filterId , String username) throws TransformerException{
		Set<String> querySpeFieldSet = new HashSet<String>();
		Filter filter = das.queryFilter(filterId);
		if (filter == null || filter.getXml() == null || ("").equals(filter.getXml()) ) {
			return 0 ;
		}
		
		Document filterXMLDoc = null;
		try{
			filterXMLDoc = XMLUtil.string2Document(filter.getXml(), "UTF-8");
		}catch(Exception e){
			System.out.println("filter parse error , filter id :" + filter.getId().getValue());
		}

		if(filterXMLDoc == null){
			return 0;
		}

		Node queryNode = XMLUtil.getSingleNode(filterXMLDoc, "query");
		Node templateTypeNode = XMLUtil.getSingleNode(queryNode, "templateType");
		String templateTypeIdStr = XMLUtil.getAttribute(templateTypeNode, "id");
		if(templateTypeIdStr.equals("$current_template_type$")){
			Node envNode = XMLUtil.getSingleNode(queryNode, "env");
			Node currentUserNode = XMLUtil.getSingleNode(envNode, "current_user");
			if(currentUserNode == null){
				currentUserNode = filterXMLDoc.createElement("current_user");
				envNode.appendChild(currentUserNode);
			}
			currentUserNode.setTextContent(username);
		}
		
		
		int totalCount = 0;
		querySpeFieldSet.add("id");

		String sql = DataFilterMemory.getFilterSql(XMLUtil.document2String(filterXMLDoc, "UTF-8"), querySpeFieldSet ,null);
		
		sql = CynthiaUtil.cancelGroupOrder(sql);
		sql = sql.replace("as id", "");
		String[] allSQLArray = sql.split("union");
		
		for (String sqlStr : allSQLArray) {
			sqlStr = sqlStr.trim();
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select count(").append(sqlStr.substring(sqlStr.indexOf("select") +6, sqlStr.indexOf("from"))).append(" ) ").append(sqlStr.substring(sqlStr.indexOf("from")));
			
			if (filterId.getValue().equals("119695")) {
				System.out.println("待处理 filter count sql:" + sqlBuffer.toString());
			}
			totalCount += DbPoolConnection.getInstance().getCountOfSQL(sqlBuffer.toString());
		}
		
		return totalCount;
	}
	

	public Map<UUID, Set<String>>  getFilterNewOldTasks(UUID[] filterIdArray, String username)
	{
		Map<UUID, Set<String>>  retMap = new HashMap<UUID, Set<String>> ();

		for (UUID filterId : filterIdArray) {
			retMap.put(filterId, new HashSet<String>());
		}

		Connection conn = null;
		Statement stm = null;
		ResultSet rst = null;
		
		try
		{
			conn = DbPoolConnection.getInstance().getReadConnection();

			stm = conn.createStatement();

			StringBuffer sqlStrb = new StringBuffer();
			sqlStrb.append("SELECT filter_id, old_id FROM user_new_data");
			sqlStrb.append(" WHERE user = '").append(username).append("'");
			if(filterIdArray != null && filterIdArray.length > 0)
			{
				sqlStrb.append(" AND (");
				for(int i = 0; i < filterIdArray.length; i++)
				{
					if(i > 0)
						sqlStrb.append(" OR");
					sqlStrb.append(" filter_id = '").append(filterIdArray[i].getValue()).append("'");
				}

				sqlStrb.append(" )");
			}

			rst = stm.executeQuery(sqlStrb.toString());
			while(rst.next())
			{
				UUID filterId = DataAccessFactory.getInstance().createUUID(rst.getString("filter_id"));
				String old_id = rst.getString("old_id");

				if(old_id != null)
					retMap.get(filterId).add(old_id);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbPoolConnection.getInstance().closeAll(rst, stm, conn);
		}

		return retMap;
	}

	
	public void insertFilterUserTasks(UUID filterId, String username, UUID[] oldIdArray)
	{
		PreparedStatement pstm = null;
		Connection conn = null;
		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			
			for (UUID uuid : oldIdArray) {
				
				pstm = conn.prepareStatement("insert ignore into user_new_data"
						+ " SET filter_id = ?"
						+ ", user = ?"
						+ ", old_id = ?");

				pstm.setString(1, filterId.getValue());
				pstm.setString(2, username);
				pstm.setString(3, uuid.getValue());
				pstm.executeUpdate();
			}
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
	}

	public void deleteFilterUserTasks(UUID dataId)
	{
		Connection conn = null;
		Statement stm = null;

		try
		{
			conn = DbPoolConnection.getInstance().getConnection();
			stm = conn.createStatement();
			stm.executeUpdate("DELETE FROM user_new_data WHERE old_id = '" + dataId.getValue() + "'");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DbPoolConnection.getInstance().closeStatment(stm);
			DbPoolConnection.getInstance().closeConn(conn);
		}
	}
}
