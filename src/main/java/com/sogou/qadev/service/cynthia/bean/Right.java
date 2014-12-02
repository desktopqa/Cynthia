package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;


/**
 * @description:right bean
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:05:05
 * @version:v1.0
 */
public class Right implements Serializable,Comparable<Right>{
	
	/**
	 * @Fields:serialVersionUID
	 * @Fields_Type:long
	 * @description:TODO
	 * @date:2014-5-6 下午4:05:12
	 */
	private static final long serialVersionUID = 8733903539117725929L;
	
	/**
	 * right username
	 */
	public String username = null;
	/**
	 * right tempalte id
	 */
	public UUID templateId = null;
	
	public String nickname = null;
	
	/**
	 * right role id
	 */
	public UUID roleId = null;

	public Right(String username, UUID nodeId, UUID roleId,String nickname){
		this.username = username;
		this.templateId = nodeId;
		this.roleId = roleId;
		this.nickname = nickname;
	}

	public String getUsername()
	{
		return this.username;
	}

	public UUID getTemplateId()
	{
		return this.templateId;
	}

	public UUID getRoleId()
	{
		return this.roleId;
	}
	
	public String getNickname() {
		return nickname == null ? this.username : nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:equals</p>
	 * @param o
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		Right r = (Right)o;
		return (this.username.equals(r.username) && this.templateId.equals(r.templateId) && this.roleId.equals(r.roleId));
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:hashCode</p>
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode(){
		return this.username.hashCode() ^ this.templateId.hashCode() ^ this.roleId.hashCode();
	}

	@Override
	public int compareTo(Right o) {
		if (o == null) {
			return 1;
		}
		return this.username.compareTo(o.username);
	}
}
