package com.sogou.qadev.cache;

import com.sogou.qadev.service.cynthia.bean.BaseType;
import com.sogou.qadev.service.cynthia.bean.UUID;

/**
 * @description:cache interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午7:48:16
 * @version:v1.0
 * @param <T>
 */
public interface Cache<T extends BaseType> {
	/**
	 * @description:set cache value
	 * @date:2014-5-6 下午5:09:05
	 * @version:v1.0
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value);
	
	/**
	 * @description:get cache data by id
	 * @date:2014-5-6 下午5:09:17
	 * @version:v1.0
	 * @param uuid
	 * @return
	 */
	public T get(UUID uuid);
	
	/**
	 * @description:get cache data by id
	 * @date:2014-5-6 下午5:09:28
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public T get(String id);
	
	/**
	 * @description:remove data from cache by ids
	 * @date:2014-5-6 下午5:09:40
	 * @version:v1.0
	 * @param uuids
	 */
	public void remove(UUID[] uuids);
}
