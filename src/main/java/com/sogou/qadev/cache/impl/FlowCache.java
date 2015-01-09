package com.sogou.qadev.cache.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.sogou.qadev.cache.Cache;
import com.sogou.qadev.cache.EhcacheHandler;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.dao.FlowAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;

/**
 * @description:flow cache,process the set and get cache
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午7:49:28
 * @version:v1.0
 */
public class FlowCache implements Cache<Flow> {

	private static Logger logger = Logger.getLogger(FlowCache.class.getName());
	
	private static class SingletonHolder{
		private static FlowCache instance = new FlowCache();
	}
	
	/**
	 * @Title:getInstance
	 * @Type:FlowCache
	 * @description:single instance
	 * @date:2014-5-5 下午7:50:14
	 * @version:v1.0
	 * @return
	 */
	public static final FlowCache getInstance()
	{
		return SingletonHolder.instance;
	}

	private FlowCache()
	{
		super();
	}

	/**
	 * 
	 * (non-Javadoc)
	 * <p> Title:get</p>
	 * <p> Description:get flow from cache by id</p>
	 * @param id:flow id
	 * @return
	 * @see com.sogou.qadev.cache.Cache#get(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	@Override
	public Flow get(UUID id){
		if (id == null) {
			return null;
		}
		return get(id.getValue());
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:get</p>
	 * @param id
	 * @return
	 * @see com.sogou.qadev.cache.Cache#get(java.lang.String)
	 */
	public Flow get(String id){
		Flow tmp = null;
		Object flow = EhcacheHandler.getInstance().get(EhcacheHandler.FOREVER_CACHE,id);
		if (flow != null){
			if (flow instanceof Flow) {
				tmp = (Flow)flow;
			}
		}
		else{
			tmp = new FlowAccessSessionMySQL().queryFlowById(DataAccessFactory.getInstance().createUUID(id));
			if (tmp != null) {
				EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,tmp.getId().getValue(), tmp);
			}
		}
		if (tmp == null) {
			logger.info("flow id : " + id + " is not in cache");
		}
		return tmp;
	}

	/**
	 * 
	 * @Title:getAll
	 * @Type:FlowCache
	 * @description:get all flows from cache
	 * @date:2014-5-5 下午7:51:32
	 * @version:v1.0
	 * @return:all flows 
	 */
	@SuppressWarnings("unchecked")
	public Vector<Flow> getAll(){
		Object allFlow = EhcacheHandler.getInstance().get(EhcacheHandler.FOREVER_CACHE,"allFlow");
		if (allFlow != null) {
			return (Vector<Flow>)allFlow;
		}else {
			logger.info("all flow is not in cache");
			Vector<Flow> allFlowList = new FlowAccessSessionMySQL().queryAllFlow();
			EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE, EhcacheHandler.FOREVER_CACHE, allFlowList);
			return allFlowList;
		}
	}

	/**
	 * @Title:putAllDataToCache
	 * @Type:FlowCache
	 * @description:query all flows from DB, put to the cache
	 * @date:2014-5-5 下午7:52:04
	 * @version:v1.0
	 */
	public void putAllDataToCache(){
		List<Flow> allFlows = new FlowAccessSessionMySQL().queryAllFlow();
		EhcacheHandler ehcacheHandler = EhcacheHandler.getInstance();

		for (Flow flow : allFlows) {
			ehcacheHandler.set(EhcacheHandler.FOREVER_CACHE,flow.getId().getValue(), flow);
		}
		setAll(allFlows);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:remove</p>
	 * <p> Description:remove flows from cache by uuids</p>
	 * @param uuids
	 * @see com.sogou.qadev.cache.Cache#remove(com.sogou.qadev.service.cynthia.bean.UUID[])
	 */
	public void remove(UUID[] uuids) {
		for (UUID uuid : uuids) {
			EhcacheHandler.getInstance().delete(EhcacheHandler.FOREVER_CACHE,uuid.getValue());
		}

		List<UUID> deleteFlowList = Arrays.asList(uuids);

		List<Flow> allFlows = getAll();
		Iterator<Flow> it = allFlows.iterator();
		while (it.hasNext()) {
			if (deleteFlowList.contains(it.next().getId())) {
				it.remove();
			}
		}

		setAll(allFlows);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:set</p>
	 * @param key
	 * @param value
	 * @see com.sogou.qadev.cache.Cache#set(java.lang.String, java.lang.Object)
	 */
	@Override
	public void set(String key, Object value) {
		if (value == null) {
			return;
		}
		EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,key, value);
		Vector<Flow> allFlows = getAll();
		
		Iterator<Flow> it = allFlows.iterator();
		while (it.hasNext()) {
			if (it.next().getId().getValue().equals(((Flow)value).getId().getValue())) {
				it.remove();
			}
		}
		
		allFlows.add(0, (Flow)value);  //刚插入放在第一位
		setAll(allFlows);
	}

	private void setAll(List<Flow> allFlows){
		EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,"allFlow", allFlows);
	}

	/**
	 * @description:remove cache by key
	 * @date:2014-5-6 下午5:10:31
	 * @version:v1.0
	 * @param key
	 */
	private void remove(String key) {
		EhcacheHandler.getInstance().delete(EhcacheHandler.FOREVER_CACHE,key);
	}
}
