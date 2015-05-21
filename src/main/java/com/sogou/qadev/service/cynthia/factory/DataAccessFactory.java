package com.sogou.qadev.service.cynthia.factory;

import org.apache.log4j.Logger;

import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.impl.UUIDImpl;
import com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.dao.UUIDAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.impl.DataAccessSessionMemory;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;


/**
 * @description: data process factory:get date process interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午6:54:36
 * @version:v1.0
 */
public class DataAccessFactory
{
	public static final long magic = 0x8f67e3a;

	private static Logger logger = Logger.getLogger(DataAccessFactory.class.getName());
	
	private static class SingletonHolder{
		private static DataAccessFactory instance = new DataAccessFactory();
	}

	public static final DataAccessFactory getInstance()
	{
		return SingletonHolder.instance;
	}

	private DataAccessFactory()
	{
		super();
	}
	
	/**
	 * @description:get a new UUID
	 * @date:2014-5-5 下午6:56:25
	 * @version:v1.0
	 * @param str
	 * @return
	 */
	public synchronized UUID newUUID(String str)
	{
		String newUUIDStr = new UUIDAccessSessionMySQL().add(str);
		return createUUID(newUUIDStr);
	}
	
	public synchronized UUID newDataUUID(String templateId)
	{
		String newUUIDStr = new DataAccessSessionMySQL().createUUID(templateId);
		return createUUID(newUUIDStr);
	}
	
	/**
	 * @description:get the data process interface by username
	 * @date:2014-5-5 下午6:56:41
	 * @version:v1.0
	 * @param username:current login user
	 * @param keyId
	 * @return:data process interface
	 */
	public synchronized DataAccessSession createDataAccessSession(String username, long keyId)
	{
		return createDataAccessSession(username, null, keyId);
	}

	public synchronized DataAccessSession createDataAccessSession(String username, String agent, long keyId)
	{
		return new DataAccessSessionMemory(username, agent, keyId);
	}

	/**
	 * @description: return the UUID by string
	 * @date:2014-5-5 下午6:58:06
	 * @version:v1.0
	 * @param str:uuid string
	 * @return:UUID
	 */
	public UUID createUUID(String str)
	{
		try{
			return new UUIDImpl(Integer.parseInt(str));
		}
		catch(Exception e){
			return null;
		}
	}
	
	/**
	 * @description:return the system data process interface
	 * @date:2014-5-5 下午6:58:35
	 * @version:v1.0
	 * @return
	 */
	public DataAccessSession getSysDas()
	{
		return this.createDataAccessSession(ConfigUtil.sysEmail, ConfigUtil.magic);
	}
	
	/**
	 * system user
	 */
	public static final String sysUser = "admin@sohu-rd.com";
}
