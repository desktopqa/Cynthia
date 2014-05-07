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
	
	public static String deployHost = null;
	public static String deployPort = null;
	public static String deployPath = null;
	
	static{
		properties = loadPropertyFile("config.properties");
		deployHost = properties.getProperty("deploy.host");
		deployPort = properties.getProperty("deploy.port");
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
	 * @description:return database config
	 * @date:2014-5-6 上午10:07:00
	 * @version:v1.0
	 * @return
	 */
	public static Properties getDataBaseProperty()
	{
		Properties prop = new Properties();
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
		String webRootPath = null;
		if (null == fullFile || fullFile.equals(""))
			throw new IllegalArgumentException(
					"Properties file path can not be null : " + fullFile);
		webRootPath = ConfigManager.class.getClassLoader().getResource("")
				.getPath();
		webRootPath = new File(webRootPath).getPath();
		InputStream inputStream = null;
		Properties p = null;
		try {
			inputStream = new FileInputStream(new File(webRootPath
					+ File.separator + fullFile));
			p = new Properties();
			p.load(inputStream);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Properties file not found: "
					+ fullFile);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Properties file can not be loading: " + fullFile);
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return p;
	}
	

}
