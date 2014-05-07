package com.sogou.qadev.cache;

import org.apache.log4j.Logger;


import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * ehcache缓存，本地缓存，缓存常用的 template、flow、node
 * @author Administrator
 */
public class EhcacheHandler {
	
	public static final String FOREVER_CACHE = "foreverCache";  //两种缓存策略，永久缓存
	public static final String EXPIRES_CACHE = "expiresCache";  //过期缓存
	
	private static Logger logger = Logger.getLogger(EhcacheHandler.class);
    private static CacheManager cacheManager = null;
    private static EhcacheHandler ehcacheHandler = new EhcacheHandler();
    
    private EhcacheHandler(){
        init();
    }
    public static EhcacheHandler getInstance(){
    	return ehcacheHandler;
    }
    
    /**
     * 缓存实始化
     */
    public void init(){
        
        if(cacheManager == null){
            logger.info("初始化CacheManager");
            
            String localEhcacheXmlName = "ehcache.xml";
            cacheManager = CacheManager.create(this.getClass().getClassLoader().getResource(localEhcacheXmlName));
            if (cacheManager == null) {
            	throw new RuntimeException("无法创建ehcache对象实例,检测ehcache配置文件");
			}
        }
    }
    
    /**
     * 设置缓存内容
     * @param cacheName：缓存实例名
     * @param key：缓存内容主键
     * @param obj：缓存对象
     * @return
     */
    public boolean set(String cacheName, String key, Object obj) {
    	cacheManager.getCache(cacheName).put(new Element(key, obj));
    	return true;
    }

    /**
     * 设置缓存内容
     * @param cacheName：缓存实例名
     * @param key：缓存内容主键
     * @param obj：缓存对象
     * @param liveTime:过期天数
     * @return
     */
    @SuppressWarnings("deprecation")
	public void set(String cacheName, String key, Object obj, int liveTime) {
    	cacheManager.getCache(cacheName).put(new Element(key, obj, false, liveTime, liveTime));
    }

    /**
     * 获取缓存对象
     * @param cacheName：缓存实例名
     * @param key：缓存主键id
     * @return
     */
    public Object get(String cacheName, String key) {
        Element ele = cacheManager.getCache(cacheName).get(key);
        return ele == null ? null : ele.getObjectValue();
    }

    /**
     * 删除缓存对象
     * @param cacheName：缓存实例名
     * @param key：缓存主键id
     * @return
     */
    public boolean delete(String cacheName, String key) {
        return cacheManager.getCache(cacheName).remove(key);
    }
    
    /**
     * 关闭时缓存清理
     */
    public void shutdown() {
        try{
            if(cacheManager.getCache(EXPIRES_CACHE)!=null){
            	cacheManager.getCache(EXPIRES_CACHE).dispose();
            }
            
            if(cacheManager.getCache(FOREVER_CACHE)!=null){
            	cacheManager.getCache(FOREVER_CACHE).dispose();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        if(cacheManager!=null){
            cacheManager.shutdown();
        }
    }
    
    
    public void destroy() {
        shutdown();
    }
}
