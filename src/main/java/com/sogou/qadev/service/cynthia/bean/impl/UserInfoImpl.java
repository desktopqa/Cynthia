package com.sogou.qadev.service.cynthia.bean.impl;

import java.sql.Timestamp;
import com.sogou.qadev.service.cynthia.bean.UserInfo;

/**
 * @description:user info bean
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午5:07:13
 * @version:v1.0
 */
public class UserInfoImpl implements UserInfo{
	
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 * @Description: TODO
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;       
	private String userName     = null;  
	private String nickName     = null;  
	private String userPassword = "";  
	private UserRole userRole = UserRole.normal;  
	private UserStat userStat = UserStat.not_auth;  
	private Timestamp lastLoginTime = null; 
	private Timestamp createTime = null; 
	private String picUrl = ""; //user photo url
	private String picId;
	
	public String getPicId() {
		return picId;
	}
	
	public void setPicId(String picId) {
		this.picId = picId;
	}
	
	public String getPicUrl() {
		return picUrl;
	}
	
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	
	public int getId() {
		return this.id;
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
		return this.userRole;
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
