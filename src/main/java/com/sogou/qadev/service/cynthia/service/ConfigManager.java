package com.sogou.qadev.service.cynthia.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @description:Config processor(config.properties)
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 上午10:03:58
 * @version:v1.0
 */
public class ConfigManager {
	private static Properties properties = null;
	private static Properties projectProperties = null;
	
	public static String deployUrl = null;
	public static String deployPath = null;
	public static String deployScheme = null;
	
	static{
		properties = loadPropertyFile("config.properties");
		if (properties.get("project_involve") != null && properties.get("project_involve").equals("true")) {
			projectProperties = loadPropertyFile("pro-involve.properties");
		}
	}
	
	/**
	 * @description:return if timer enable
	 * @date:2014-5-6 上午10:04:54
	 * @version:v1.0
	 * @return
	 */
	public static boolean getEnableTimer(){
		try {
			return properties.getProperty("timer.enable").equals("true");
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @description:return if email enable
	 * @date:2014-5-6 上午10:05:22
	 * @version:v1.0
	 * @return
	 */
	public static boolean getEnableEmail(){
		try {
			return Boolean.parseBoolean(properties.getProperty("mail.enable"));
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @Title: getEnableSso
	 * @Description: 是否启用单点登录
	 * @return
	 * @return: boolean
	 */
	public static boolean getEnableSso(){
		try {
			return Boolean.parseBoolean(properties.getProperty("sso.enable"));
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @Title: getEnableFileSystem
	 * @Description: 是否启用文件系统
	 * @return
	 * @return: boolean
	 */
	public static boolean getEnableFileSystem(){
		try {
			return Boolean.parseBoolean(properties.getProperty("fdfs.enable"));
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @Title: getFileSystemProperties
	 * @Description: 返回文件系统配置
	 * @return
	 * @return: boolean
	 */
	public static Properties getFileSystemProperties(){
		Properties prop = new Properties();
		prop.put("fdfs.upload.url", properties.get("fdfs.upload.url"));
		prop.put("fdfs.download.url", properties.get("fdfs.download.url"));
		return prop;
	}
	
	/**
	 * @description:return sso config
	 * @date:2014-5-6 上午10:06:35
	 * @version:v1.0
	 * @return
	 */
	public static Properties getSsoProperties()
	{
		Properties prop = new Properties();
		
		prop.put("sso.login.url", properties.get("sso.login.url"));
		prop.put("sso.logout.url", properties.get("sso.logout.url"));
		
		return prop;
	}
	
	/**
	 * @description:return email config
	 * @date:2014-5-6 上午10:06:35
	 * @version:v1.0
	 * @return
	 */
	public static Properties getEmailProperties()
	{
		Properties prop = new Properties();
		
		prop.put("mail.enable", properties.get("mail.enable"));
		prop.put("mail.user", properties.get("mail.user"));
		prop.put("mail.pass", properties.get("mail.pass"));
		prop.put("mail.smtp.host", properties.get("mail.smtp.host"));
		prop.put("mail.protocal", properties.get("mail.protocal"));
		
		return prop;
	}
	
	/**
	 * @Title: getProjectInvolved
	 * @Description: 是否与项目管理关联
	 * @return
	 * @return: boolean
	 */
	public static boolean getProjectInvolved(){
		try {
			return Boolean.parseBoolean(properties.getProperty("project_involve"));
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @description:返回与项目管理相关的配置信息
	 * @date:2014-5-6 上午10:06:35
	 * @version:v1.0
	 * @return
	 */
	public static Properties getProInvolvedProperties()
	{
		return projectProperties;
	}
	
	/**
	 * @description:return database config
	 * @date:2014-5-6 上午10:07:00
	 * @version:v1.0
	 * @return
	 */
	public static Properties getDataBaseProperty()
	{
		Properties prop = new Properties();
		try{
			prop.put("driverClassName", properties.get("driverClassName"));
			prop.put("url", properties.get("url"));
			prop.put("username", properties.get("username"));
			prop.put("password", properties.get("password"));
			prop.put("initialSize", properties.get("initialSize"));
			prop.put("maxActive", properties.get("maxActive"));
			prop.put("maxWait", properties.get("maxWait"));
			prop.put("timeBetweenEvictionRunsMillis", properties.get("timeBetweenEvictionRunsMillis"));
			prop.put("minEvictableIdleTimeMillis", properties.get("minEvictableIdleTimeMillis"));
			prop.put("validationQuery", properties.get("validationQuery"));
			prop.put("testWhileIdle", properties.get("testWhileIdle"));
			prop.put("testOnReturn", properties.get("testOnReturn"));
			prop.put("testOnBorrow", properties.get("testOnBorrow"));
			prop.put("poolPreparedStatements", properties.get("poolPreparedStatements"));
		}catch(Exception e){
			System.out.println("error!!! 请检查您的config.properties数据库配置文件！");
			e.printStackTrace();
		}
		return prop;
	}
	
	/**
	 * @description:init config from file
	 * @date:2014-5-6 上午10:07:40
	 * @version:v1.0
	 * @param fullFile
	 * @return
	 */
	public static Properties loadPropertyFile(String fullFile) {
		if (null == fullFile || fullFile.equals(""))
			throw new IllegalArgumentException(
					"Properties file path can not be null : " + fullFile);
		InputStream inputStream = null;
		Properties p = null;
		try {
			inputStream = ConfigManager.class.getClassLoader().getResourceAsStream(fullFile);
			p = new Properties();
			p.load(inputStream);
		} catch (Exception e) {
			System.out.println("Error !!!! Properties file can not be loading: " + fullFile);
		} finally {
			try {
				if (inputStream != null){
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return p;
	}
	

}
