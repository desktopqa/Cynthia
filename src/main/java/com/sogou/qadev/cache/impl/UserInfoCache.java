package com.sogou.qadev.cache.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.sogou.qadev.cache.EhcacheHandler;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.dao.UserInfoAccessSessionMySQL;

public class UserInfoCache {
	private static Logger logger = Logger.getLogger(UserInfoCache.class.getName());
	
	private static class SingletonHolder{
		private static UserInfoCache instance = new UserInfoCache();
	}
	
	/**
	 * @Title:getInstance
	 * @Type:FlowCache
	 * @description:single instance
	 * @date:2014-5-5 下午7:50:14
	 * @version:v1.0
	 * @return
	 */
	public static final UserInfoCache getInstance()
	{
		return SingletonHolder.instance;
	}

	private UserInfoCache()
	{
		super();
	}


	/**
	 * (non-Javadoc)
	 * <p> Title:get</p>
	 * @param id
	 * @return
	 * @see com.sogou.qadev.cache.Cache#get(java.lang.String)
	 */
	public UserInfo get(String userName){
		UserInfo tmp = null;
		Object user = EhcacheHandler.getInstance().get(EhcacheHandler.FOREVER_CACHE,userName);
		if (user != null){
			if (user instanceof UserInfo) {
				tmp = (UserInfo)user;
			}
		}
		else{
			tmp = new UserInfoAccessSessionMySQL().queryUserInfoByUserName(userName);
			if (tmp != null) {
				EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,userName, tmp);
			}
		}
		if (tmp == null) {
			logger.info("user userName : " + userName + " is not in cache");
		}
		return tmp;
	}


	/**
	 * @Title:putAllDataToCache
	 * @Type:FlowCache
	 * @description:query all users from DB, put to the cache
	 * @date:2014-5-5 下午7:52:04
	 * @version:v1.0
	 */
	public void putAllDataToCache(){
		List<UserInfo> allUsers = new UserInfoAccessSessionMySQL().queryAllUsers();
		EhcacheHandler ehcacheHandler = EhcacheHandler.getInstance();

		for (UserInfo userInfo : allUsers) {
			this.set(userInfo.getUserName(), userInfo);
		}
	}

	/**
	 * @Title: set
	 * @Description: TODO
	 * @param key
	 * @param value
	 * @return: void
	 */
	public void set(String key, Object value) {
		if (value == null) {
			return;
		}
		EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,key, value);
	}

	/**
	 * @description:remove cache by key
	 * @date:2014-5-6 下午5:10:31
	 * @version:v1.0
	 * @param key
	 */
	public void remove(String key) {
		EhcacheHandler.getInstance().delete(EhcacheHandler.FOREVER_CACHE,key);
	}
}
