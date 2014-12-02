package com.sogou.qadev.service.cynthia.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

/**
 * @description:database table rule processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午12:13:28
 * @version:v1.0
 */
public class TableRuleManager {
	
	private static TableRuleManager manager = null;

	private static String defaultDataTableName = "data";
	private static String defaultDataLogTableName = "data_log";

	private static Map<UUID, String> specDataTableMap = new LinkedHashMap<UUID,String>();
	private static Map<UUID, String> specDataLogTableMap   = new LinkedHashMap<UUID,String>();

	private static List<String> freeDataTables = new ArrayList<String>();
	private static List<String> freeDataLogTables = new ArrayList<String>();

	private TableRuleManager()
	{

	}
	
	private static class SingletonHolder{
		private static TableRuleManager tableRuleManager = new TableRuleManager();
	}

	public static TableRuleManager getInstance()
	{
		return SingletonHolder.tableRuleManager;
	}

	/**
	 * @description:return database table name by templateid
	 * @date:2014-5-6 下午12:13:52
	 * @version:v1.0
	 * @param templateId
	 * @return
	 */
	public String getDataTableName(UUID templateId)
	{
		String tableName = defaultDataTableName;
		return tableName;
	}

	/**
	 * @description:get all database table names involves data
	 * @date:2014-5-6 下午12:14:08
	 * @version:v1.0
	 * @return
	 */
	public List<String> getAllDataTables(){
		
		List<String> all = new ArrayList<String>();
		all.add(defaultDataTableName);
		return all;
	}

	/**
	 * @description:TODO
	 * @date:2014-5-6 下午12:14:33
	 * @version:v1.0
	 * @return
	 */
	public Map<String, String> getAllDataLogMap(){
		Map<String, String> dataLogMap = new HashMap<String, String>();
		dataLogMap.put(defaultDataTableName, defaultDataLogTableName);
		return dataLogMap;
	}
	
	/**
	 * @description:get all database table names involves data log
	 * @date:2014-5-6 下午12:14:50
	 * @version:v1.0
	 * @return
	 */
	public List<String> getAllDataLogTables(){
		List<String> all = new ArrayList<String>();
		all.add(defaultDataLogTableName);
		return all;
	}
	
	/**
	 * @description:get data log table name by template id
	 * @date:2014-5-6 下午12:15:02
	 * @version:v1.0
	 * @param templateId
	 * @return
	 */
	public String getDataLogTableName(UUID templateId)
	{
		String tableName = defaultDataLogTableName;
		return defaultDataLogTableName;
	}

	/**
	 * @description:init table rule from file
	 * @date:2014-5-6 下午12:15:17
	 * @version:v1.0
	 * @param fileName
	 */
	public static void loadTableManagerConfig(String fileName)
	{
		String webRootPath = null;
		if (null == fileName || fileName.equals(""))
			throw new IllegalArgumentException(
					"Properties file path can not be null : " + fileName);
		webRootPath = DbPoolConnection.class.getClassLoader().getResource("").getPath();
		webRootPath = new File(webRootPath).getPath();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(new File(webRootPath
					+ File.separator + fileName));
			Document doc = XMLUtil.inputStream2Document(inputStream);
			Node dataTableRuleNode = XMLUtil.getSingleNode(doc, "tableRule/dataTableRule");
			Node dataLogTableRuleNode = XMLUtil.getSingleNode(doc, "tableRule/dataLogTableRule");

			//data table
			List<Node> specDataTableNodes = XMLUtil.getNodes(dataTableRuleNode, "specs/spec");
			for(Node specDataTableNode : specDataTableNodes)
			{
				Node templateNode = XMLUtil.getSingleNode(specDataTableNode, "template");
				Node tableNameNode = XMLUtil.getSingleNode(specDataTableNode, "tableName");
				specDataTableMap.put(DataAccessFactory.getInstance().createUUID(templateNode.getTextContent()), tableNameNode.getTextContent());
			}

			List<Node> freeTableNodes = XMLUtil.getNodes(dataTableRuleNode, "frees/free");
			for(Node freeTableNode : freeTableNodes)
			{
				Node tableNameNode = XMLUtil.getSingleNode(freeTableNode, "tableName");
				freeDataTables.add(tableNameNode.getTextContent());
			}

			//data log table
			List<Node> specDataLogTableNodes = XMLUtil.getNodes(dataLogTableRuleNode, "specs/spec");
			for(Node specDataLogTableNode : specDataLogTableNodes)
			{
				Node templateNode = XMLUtil.getSingleNode(specDataLogTableNode, "template");
				Node tableNameNode = XMLUtil.getSingleNode(specDataLogTableNode, "tableName");
				specDataLogTableMap.put(DataAccessFactory.getInstance().createUUID(templateNode.getTextContent()), tableNameNode.getTextContent());
			}

			List<Node> freeDtaLogTableNodes = XMLUtil.getNodes(dataLogTableRuleNode, "frees/free");
			for(Node freeDataLogTableNode : freeDtaLogTableNodes){
				Node tableNameNode = XMLUtil.getSingleNode(freeDataLogTableNode, "tableName");
				freeDataLogTables.add(tableNameNode.getTextContent());
			}

		} catch(Exception e){
			e.printStackTrace();
		}finally{
			StreamCloserManager.closeInputStream(inputStream);
		}

	}
}
