package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

/**
 * @description:data change log interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午2:39:54
 * @version:v1.0
 */
public interface ChangeLog extends Serializable{
	/**
	 * @description:get change log id
	 * @date:2014-5-6 下午2:40:08
	 * @version:v1.0
	 * @return
	 */
	public UUID getId();
	
	/**
	 * @description:get change log create user
	 * @date:2014-5-6 下午2:40:20
	 * @version:v1.0
	 * @return
	 */
	public String getCreateUser();
	
	/**
	 * @Title: setCreateUser
	 * @Description: set change log create user
	 * @param createUser
	 * @return
	 * @return: String
	 */
	public void setCreateUser(String createUser);
	
	/**
	 * @description:get change log create time
	 * @date:2014-5-6 下午2:40:30
	 * @version:v1.0
	 * @return
	 */
	public Timestamp getCreateTime();
	
	/**
	 * @description:get change log action id
	 * @date:2014-5-6 下午2:40:39
	 * @version:v1.0
	 * @return
	 */
	public UUID getActionId();
	
	/**
	 * @description:get change log action comment
	 * @date:2014-5-6 下午2:40:49
	 * @version:v1.0
	 * @return
	 */
	public String getActionComment();
	
	/**
	 * @description:set change log action comment
	 * @date:2014-5-6 下午2:40:59
	 * @version:v1.0
	 * @param actionComment
	 */
	public void setActionComment(String actionComment);
	
	/**
	 * @description:return change log base field map
	 * @date:2014-5-6 下午2:41:11
	 * @version:v1.0
	 * @return
	 */
	public Map<String, Pair<Object, Object>> getBaseValueMap();
	
	/**
	 * @description:return change log extension field map
	 * @date:2014-5-6 下午2:41:30
	 * @version:v1.0
	 * @return
	 */
	public Map<UUID, Pair<Object, Object>> getExtValueMap();
	
	/**
	 * @description:get basic field value by name(such as logcomment ,action id etc.)
	 * @date:2014-5-6 下午2:41:41
	 * @version:v1.0
	 * @param name
	 * @return
	 */
	public Object getObject(String name);
	
	/**
	 * @description:get extension field value by field id
	 * @date:2014-5-6 下午2:42:09
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public Object getObject(UUID id);
	
	/**
	 * @description:check base value map contains field 
	 * @date:2014-5-6 下午2:42:25
	 * @version:v1.0
	 * @param name
	 * @return
	 */
	public boolean containsObject(String name);
	
	/**
	 * @description:check ext value map contains field 
	 * @date:2014-5-6 下午2:42:30
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public boolean containsObject(UUID id);
}
