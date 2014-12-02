package com.sogou.qadev.cache.impl;

import java.util.Map;

import com.sogou.qadev.cache.EhcacheHandler;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.dao.FieldNameAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;

/**
 * 表单中字段在数据库中对应关系的缓存处理
 * @author liming
 *
 */
public class FieldNameCache{
	private static class SingletonHolder{
		private static FieldNameCache instance = new FieldNameCache();
	}

	public static final FieldNameCache getInstance()
	{
		return SingletonHolder.instance;
	}
	private FieldNameCache()
	{
		super();
	}

	public void set(String key, Object object){
		EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE, key, object);
		putAllDataToCache();
	}
	
	public void remove(String fieldId,String templateId){
		String key = templateId + "|" + fieldId;
		EhcacheHandler.getInstance().delete(EhcacheHandler.FOREVER_CACHE,key);
		putAllDataToCache();
	}
	
	/**
	 * @function：
	 * @modifyTime：2013-9-12 下午4:14:04
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param fieldId
	 * @return
	 */
	public String getFieldName(UUID fieldId , UUID templateId){
		if (fieldId == null || templateId == null) {
			return null;
		}
		return getFieldName(fieldId.getValue() , templateId.getValue());
	}
	
	/**
	 *
	 * 功能：根据fieldId查询field列名
	 * 更改时间：2013-8-15 下午9:50:16
	 * 作者：李明
	 * 版本：1.0
	 * 邮箱:liming@sogou-inc.com
	 * @param fieldId
	 * @return
	 */
	public String getFieldName(String fieldId , String templateId){
		String key = templateId + "|" + fieldId; 
		Object fieldName = EhcacheHandler.getInstance().get(EhcacheHandler.FOREVER_CACHE,key);
		if (fieldName != null)
			return fieldName.toString();
		else{
			String fName = new FieldNameAccessSessionMySQL().queryFieldColNameById(fieldId , templateId);
			if (fName != null && fName.length() > 0) {
				EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,key, fName);
				return fName;
			}
		}
		return null;
	}

	public Map<String, String> getTemplateFieldCache(String templateId){
		Object templateFieldMap = EhcacheHandler.getInstance().get(EhcacheHandler.FOREVER_CACHE,ConfigUtil.templateFieldCacheprefix + templateId);
		if (templateFieldMap != null)
			return (Map<String, String>)templateFieldMap;
		else{
			Map<String, String> tmp = new FieldNameAccessSessionMySQL().queryTemplateFieldMap(templateId);
			if (tmp != null && tmp.keySet().size() > 0) {
				EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,ConfigUtil.templateFieldCacheprefix + templateId, tmp);
				return tmp;
			}
		}
		return null; 
	}

	/**
	 * @function：根据fieldColName templateId查询fieldId
	 * @modifyTime：2013-9-5 上午11:47:17
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param fieldColName
	 * @param templateId
	 * @return
	 */
	public String getFieldId(String fieldColName, String templateId){
		String key = templateId + "|"+ fieldColName;
		Object fieldId = EhcacheHandler.getInstance().get(EhcacheHandler.FOREVER_CACHE,key);
		if (fieldId != null)
			return fieldId.toString();
		else{
			String fName = new FieldNameAccessSessionMySQL().queryFieldIdByFieldColName(fieldColName,templateId);
			if (fName != null && fName.length() > 0) {
				EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,key, fName);
				return fName;
			}
		}
		return null;
	}

	public String getFieldId(String fieldColName, UUID templateId){
		return getFieldId(fieldColName, templateId.getValue());
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

		Map<String, String> allFieldColName = new FieldNameAccessSessionMySQL().queryCacheAllFieldColName();

		for (String key : allFieldColName.keySet()) {
			EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,key, allFieldColName.get(key));
		}

		Map<String, Map<String, String>> allTemplateIdMap = new FieldNameAccessSessionMySQL().queryCacheAllFieldIds();

		for (String key : allTemplateIdMap.keySet()) {
			EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,key, allTemplateIdMap.get(key));
		}
		
		Map<String,String> allSingleMap = new FieldNameAccessSessionMySQL().queryCacheSingleFieldIds();

		for (String key : allSingleMap.keySet()) {
			EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,key, allSingleMap.get(key));
		}
	}

}
