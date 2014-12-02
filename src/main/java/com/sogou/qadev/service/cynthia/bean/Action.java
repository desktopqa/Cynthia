package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;

import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;

/**
 * @description:action interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午2:25:40
 * @version:v1.0
 */
public interface Action extends Serializable{
	
	/**
	 * @description:get action id
	 * @date:2014-5-6 下午2:25:52
	 * @version:v1.0
	 * @return
	 */
	public UUID getId();

	/**
	 * @description:return can assign to more users
	 * @date:2014-11-13 下午4:10:39
	 * @version:v1.0
	 * @return
	 */
	public boolean getAssignToMore();
	
	/**
	 * @description:set can assign to more users
	 * @date:2014-11-13 下午4:11:09
	 * @version:v1.0
	 * @param assignToMore
	 */
	public void setAssignToMore(boolean assignToMore);
	/**
	 * @description:get action flow id
	 * @date:2014-5-6 下午2:26:02
	 * @version:v1.0
	 * @return
	 */
	public UUID getFlowId();

	/**
	 * @description:get action name
	 * @date:2014-5-6 下午2:26:21
	 * @version:v1.0
	 * @return
	 */
	public String getName();

	/**
	 * @description:set action name
	 * @date:2014-5-6 下午2:26:30
	 * @version:v1.0
	 * @param name
	 */
	public void setName(String name);

	/**
	 * @description:get action begin stat id
	 * @date:2014-5-6 下午2:26:38
	 * @version:v1.0
	 * @return
	 */
	public UUID getBeginStatId();

	/**
	 * @description:set action begin stat id
	 * @date:2014-5-6 下午2:26:49
	 * @version:v1.0
	 * @param statId
	 */
	public void setBeginStatId(UUID statId);

	/**
	 * @description:get action end stat id
	 * @date:2014-5-6 下午2:26:59
	 * @version:v1.0
	 * @return
	 */
	public UUID getEndStatId();

	/**
	 * @description:set action end stat id
	 * @date:2014-5-6 下午2:27:12
	 * @version:v1.0
	 * @param statId
	 */
	public void setEndStatId(UUID statId);
	
	public Action clone();

	/**
	 * edit action id
	 */
	public static UUID editUUID = DataAccessFactory.getInstance().createUUID("48");
	public static String editName = "编辑";

	/**
	 * read action id
	 */
	public static UUID readUUID = DataAccessFactory.getInstance().createUUID("47");
	public static String readName = "查看";

	/**
	 * delete action id
	 */
	public static UUID deleteUUID = DataAccessFactory.getInstance().createUUID("51");
	public static String deleteName = "删除";


}
