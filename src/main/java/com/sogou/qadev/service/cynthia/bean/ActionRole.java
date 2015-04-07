/**
 * filename : ActionRole.java
 * date     : 2012-11-27
 * author   : afrous
 */
package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

/**
 * @description:actionRole interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午2:31:04
 * @version:v1.0
 */
public class ActionRole implements Serializable{
	
	/**
	 * @Fields:serialVersionUID
	 * @Fields_Type:long
	 * @description:TODO
	 * @date:2014-5-6 下午2:32:19
	 */
	private static final long serialVersionUID = 1L;

	public UUID actionId = null;
	public UUID roleId = null;
	
	/**
	 * <h1> Title:</h1>
	 * <p> Description:init action role</p>
	 * @date：2014-5-6 
	 * @param actionId
	 * @param roleId
	 */
	public ActionRole(UUID actionId, UUID roleId){
		this.actionId = actionId;
		this.roleId = roleId;
	}

	/**
	 * @description:get action id
	 * @date:2014-5-6 下午2:30:48
	 * @version:v1.0
	 * @return
	 */
	public UUID getActionId()
	{
		return this.actionId;
	}

	/**
	 * @description:get role id
	 * @date:2014-5-6 下午2:30:56
	 * @version:v1.0
	 * @return
	 */
	public UUID getRoleId()
	{
		return this.roleId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:equals</p>
	 * @param o
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		ActionRole ar = (ActionRole)o;
		return (this.actionId.equals(ar.actionId) && this.roleId.equals(ar.roleId));
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:hashCode</p>
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return this.actionId.hashCode() ^ this.roleId.hashCode();
	}
}
