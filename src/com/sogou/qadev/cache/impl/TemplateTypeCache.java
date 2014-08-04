package com.sogou.qadev.cache.impl;

import java.util.Iterator;
import java.util.List;


import com.sogou.qadev.cache.Cache;
import com.sogou.qadev.cache.EhcacheHandler;
import com.sogou.qadev.service.cynthia.bean.TemplateType;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.dao.TemplateTypeAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @description:templateType cache,process the templateType cache
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午7:56:44
 * @version:v1.0
 */
public class TemplateTypeCache implements Cache<TemplateType> {

	private static TemplateTypeCache instance = null;
	
	private static class SingletonHolder{
		private static TemplateTypeCache instance = new TemplateTypeCache();
	}
	public static final TemplateTypeCache getInstance()
	{
		return SingletonHolder.instance;
	}

	private TemplateTypeCache()
	{
		super();
	}

	@Override
	public TemplateType get(UUID id){
		if (id == null) {
			return null;
		}
		return get(id.getValue());
	}

	@Override
	public TemplateType get(String id){
		Object templateType = EhcacheHandler.getInstance().get(EhcacheHandler.FOREVER_CACHE,id);
		if (templateType != null){
			if (templateType instanceof TemplateType) {
				return (TemplateType)templateType;
			}else {
				return null;
			}
		}
		else{
			TemplateType type = new TemplateTypeAccessSessionMySQL().queryTemplateTypeById(DataAccessFactory.getInstance().createUUID(id));
			if (type != null) {
				EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,type.getId().getValue(), type);
			}
			return type;
		}
	}

	public List<TemplateType> getAll(){
		Object allTemplateType = EhcacheHandler.getInstance().get(EhcacheHandler.FOREVER_CACHE,"allTemplateType");

		if (allTemplateType != null) {
			return (List<TemplateType>)allTemplateType;
		}else {
			List<TemplateType> all = new TemplateTypeAccessSessionMySQL().queryAllTemplateType();
			if (all != null && all.size() > 0) {
				setAll(all);
			}
			return all;
		}
	}

	/**
	 *
	 * 功能：从数据库查询 所有数据，放入缓存
	 * 更改时间：2013-8-15 下午9:53:49
	 * 作者：李明
	 * 版本：1.0
	 * 邮箱:liming@sogou-inc.com
	 */
	public void putAllDataToCache(){
		List<TemplateType> allTemplateTypes = new TemplateTypeAccessSessionMySQL().queryAllTemplateType();
		EhcacheHandler ehcachedHanler = EhcacheHandler.getInstance();

		for (TemplateType templateType : allTemplateTypes) {
			ehcachedHanler.set(EhcacheHandler.FOREVER_CACHE,templateType.getId().getValue(), templateType);
		}
		setAll(allTemplateTypes);
	}

	@SuppressWarnings("unchecked")
	public void remove(UUID[] uuids) {
		for (UUID uuid : uuids) {
			EhcacheHandler.getInstance().delete(EhcacheHandler.FOREVER_CACHE,uuid.getValue());
		}
		
		List<UUID> deleteTempalteList = Arrays.asList(uuids);
		
		List<TemplateType> allTemplateTypes = getAll();
		Iterator<TemplateType> it = allTemplateTypes.iterator();
		while (it.hasNext()) {
			if (deleteTempalteList.contains(it.next().getId())) {
				it.remove();
			}
		}
		
		setAll(allTemplateTypes);
	}

	@Override
	public void set(String key, Object value) {
		if (value == null) {
			return;
		}
		EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,key, value);
		List<TemplateType> allTemplateTypes = getAll();
		Iterator<TemplateType> it = allTemplateTypes.iterator();
		while (it.hasNext()) {
			if (it.next().getId().getValue().equals(((TemplateType)value).getId().getValue())) {
				it.remove();
			}
		}
		allTemplateTypes.add((TemplateType)value);
		setAll(allTemplateTypes);
	}
	
	private void setAll(List<TemplateType> allTemplateTypes){
		EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,"allTemplateType", allTemplateTypes);
	}

	private void remove(String key) {
		EhcacheHandler.getInstance().delete(EhcacheHandler.FOREVER_CACHE,key);
	}
}
