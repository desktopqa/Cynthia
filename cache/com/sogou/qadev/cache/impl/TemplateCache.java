package com.sogou.qadev.cache.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.sogou.qadev.cache.Cache;
import com.sogou.qadev.cache.EhcacheHandler;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.dao.TemplateAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @description:template cache implements, process the template cache
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午7:53:29
 * @version:v1.0
 */
public class TemplateCache implements Cache<Template> {

	private static Logger logger = Logger.getLogger(TemplateCache.class.getName());

	private static class SingletonHolder{
		private static TemplateCache instance = new TemplateCache();
	}
	
	/**
	 * @Title:getInstance
	 * @Type:TemplateCache
	 * @description:Singleton instance
	 * @date:2014-5-5 下午7:54:06
	 * @version:v1.0
	 * @return
	 */
	public static final TemplateCache getInstance()
	{
		return SingletonHolder.instance;
	}

	private TemplateCache()
	{
		super();
	}

	public Template get(UUID id){
		if (id == null) {
			return null;
		}
		return get(id.getValue());
	}

	@SuppressWarnings("unused")
	public Template get(String id){
		if (id == null || id.equals("")) {
			return null;
		}
		Template tmp = null;
		Object template = EhcacheHandler.getInstance().get(EhcacheHandler.FOREVER_CACHE,id);
		if (template != null){
			if (template instanceof Template) {
				tmp = (Template)template;
			}
		}
		else{
			tmp = new TemplateAccessSessionMySQL().queryTemplateById(DataAccessFactory.getInstance().createUUID(id));
			if (tmp != null) {
				EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,tmp.getId().getValue(), tmp);
			}
		}

		if (tmp == null) {
			logger.error("template is not in cache!");
		}
		return tmp;
	}

	/**
	 * @Title:getAll
	 * @Type:TemplateCache
	 * @description:get all templates from cache, if not in cache ,query from DB
	 * @date:2014-5-5 下午7:54:59
	 * @version:v1.0
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Template> getAll(){
		Object allTemplate = EhcacheHandler.getInstance().get(EhcacheHandler.FOREVER_CACHE,"allTemplate");

		if (allTemplate != null) {
			return (List<Template>)allTemplate;
		}else {
			List<Template> all = new TemplateAccessSessionMySQL().queryAllTemplate();
			if (all != null && all.size() > 0) {
				setAll(all);
			}
			return all;
		}
	}

	/**
	 * @Title:queryField
	 * @Type:TemplateCache
	 * @description:query field by fieldId
	 * @date:2014-5-5 下午7:55:44
	 * @version:v1.0
	 * @param fieldId
	 * @return
	 */
	public Field queryField(UUID fieldId)
	{
		List<Template> allTemplates = getAll();
		for (Template template : allTemplates) {
			Field field = template.getField(fieldId);
			if(field != null)
				return field;
		}

		return null;
	}

	/**
	 * @Title:putAllDataToCache
	 * @Type:TemplateCache
	 * @description:query all Templates from DB, then put to the cache
	 * @date:2014-5-5 下午7:56:03
	 * @version:v1.0
	 */
	public void putAllDataToCache(){
		List<Template> allTemplates = new TemplateAccessSessionMySQL().queryAllTemplate();
		
		EhcacheHandler ehcacheHanler = EhcacheHandler.getInstance();

		for (Template template : allTemplates) {
			ehcacheHanler.set(EhcacheHandler.FOREVER_CACHE,template.getId().getValue(), template);
		}
		setAll(allTemplates);
	}

	/**
	 * @Title:getAll
	 * @Type:TemplateCache
	 * @description:return templates by templateTypeId
	 * @date:2014-5-5 下午7:56:24
	 * @version:v1.0
	 * @param templateTypeId
	 * @return
	 */
	public List<Template> getAll(UUID templateTypeId) {
		if (templateTypeId == null) {
			return null;
		}
		List<Template> allTemplates = getAll();
		List<Template> allTypeTemplate = new ArrayList<Template>();
		for (Template template : allTemplates) {
			if (template.getTemplateTypeId().equals(templateTypeId)) {
				allTypeTemplate.add(template);
			}
		}
		return allTypeTemplate;
	}


	@SuppressWarnings("unchecked")
	public void remove(UUID[] uuids) {
		for (UUID uuid : uuids) {
			EhcacheHandler.getInstance().delete(EhcacheHandler.FOREVER_CACHE,uuid.getValue());
		}

		List<UUID> deleteTempalteList = Arrays.asList(uuids);

		List<Template> allTempaltes = getAll();
		Iterator<Template> it = allTempaltes.iterator();
		while (it.hasNext()) {
			if (deleteTempalteList.contains(it.next().getId())) {
				it.remove();
			}
		}

		setAll(allTempaltes);
	}

	@Override
	public void set(String key, Object value) {
		if (value == null) {
			return;
		}
		EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,key, value);
		List<Template> allTemplates = getAll();
		Iterator<Template> it = allTemplates.iterator();
		while (it.hasNext()) {
			if (it.next().getId().getValue().equals(((Template)value).getId().getValue())) {
				it.remove();
			}
		}
		allTemplates.add(0, (Template)value);
		setAll(allTemplates);
	}

	private void setAll(List<Template> allTemplates){
		EhcacheHandler.getInstance().set(EhcacheHandler.FOREVER_CACHE,"allTemplate", allTemplates);
	}

	private void remove(String key) {
		EhcacheHandler.getInstance().delete(EhcacheHandler.FOREVER_CACHE,key);

		List<Template> allTempaltes = getAll();
		Iterator<Template> it = allTempaltes.iterator();
		while (it.hasNext()) {
			if (it.next().getId().getValue().equals(key)) {
				it.remove();
			}
		}
		setAll(allTempaltes);
	}

}
