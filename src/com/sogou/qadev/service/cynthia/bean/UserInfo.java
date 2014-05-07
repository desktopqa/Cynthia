package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @description:user info bean
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:07:13
 * @version:v1.0
 */
public class UserInfo implements Serializable{
	
	/**
	 * @Fields:serialVersionUID
	 * @Fields_Type:long
	 * @description:TODO
	 * @date:2014-5-6 下午5:07:39
	 */
	private static final long serialVersionUID = -8824206790588532722L;
	
	/**
	 * user role
	 * normal：normal user
	 * admin:normal admin
	 * super_admin:super admin
	 * @author liming
	 */
	public enum UserRole
	{
		normal, admin, super_admin
	}
	
	/**
	 * user status
	 * normal:noraml
	 * lock:lock can not login
	 * not_auth:register not auth
	 * quit:quit
	 * @author liming
	 */
	public enum UserStat
	{
		normal,lock,not_auth,quit
	}
	
	private int id;       
	private String userName     = null;  
	private String nickName     = null;  
	private String userPassword = "";  
	private UserRole userRole = UserRole.normal;  
	private UserStat userStat = UserStat.not_auth;  
	private Timestamp lastLoginTime = null; 
	private Timestamp createTime = null; 
	private String picId = ""; //user photo file id (attachment id)
	
	public String getPicId() {
		return picId;
	}
	public void setPicId(String picId) {
		this.picId = picId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public UserRole getUserRole() {
		return userRole;
	}
	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}
	public UserStat getUserStat() {
		return userStat;
	}
	public void setUserStat(UserStat userStat) {
		this.userStat = userStat;
	}
	public Timestamp getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Timestamp lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
}
