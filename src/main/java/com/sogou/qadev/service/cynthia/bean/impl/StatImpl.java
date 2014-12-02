package com.sogou.qadev.service.cynthia.bean.impl;

import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.UUID;

/**
 * @description:stat implements
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:26:06
 * @version:v1.0
 */
public class StatImpl implements Stat{
	/**
	 * @Fields:serialVersionUID
	 * @Fields_Type:long
	 * @description:TODO
	 * @date:2014-5-6 下午4:26:13
	 */
	private static final long serialVersionUID = -6882341231746292087L;
	
	private UUID id = null;
	private	UUID flowId	= null;
	private String name = null;

	/**
	 * <h1> Title:</h1>
	 * <p> Description:init stat</p>
	 * @date：2014-5-6 
	 * @param id
	 * @param flowId
	 */
	public StatImpl(UUID id, UUID flowId){
		this.id	= id;
		this.flowId	= flowId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Stat#getId()
	 */
	public UUID getId(){
		return this.id;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getFlowId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Stat#getFlowId()
	 */
	public UUID getFlowId(){
		return this.flowId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getName</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Stat#getName()
	 */
	public String getName(){
		return this.name;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setName</p>
	 * @param name
	 * @see com.sogou.qadev.service.cynthia.bean.Stat#setName(java.lang.String)
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:clone</p>
	 * @return
	 * @see java.lang.Object#clone()
	 */
	public Stat clone(){
		StatImpl statImpl = new StatImpl(this.id, this.flowId);
		statImpl.setName(this.name);

		return statImpl;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:equals</p>
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj){
		StatImpl statImpl = (StatImpl)obj;
		return (this.id.equals(statImpl.id) && this.flowId.equals(statImpl.flowId));
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:hashCode</p>
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return this.id.hashCode() ^ this.flowId.hashCode();
	}
}
