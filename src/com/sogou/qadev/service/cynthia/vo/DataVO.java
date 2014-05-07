package com.sogou.qadev.service.cynthia.vo;

import java.sql.Timestamp;

/**
 * @description:Dava Vo
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午6:42:17
 * @version:v1.0
 */
public class DataVO {
	private String id;
	private String name;
	private String title;
	private String description;
	private Timestamp createTime;
	private String createUser;
	private String statId;
	private String statName;
	private String assignUser;
	private Timestamp lastModifyTime;
	
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getStatId() {
		return statId;
	}
	public void setStatId(String statId) {
		this.statId = statId;
	}
	public String getStatName() {
		return statName;
	}
	public void setStatName(String statName) {
		this.statName = statName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAssignUser() {
		return assignUser;
	}
	public void setAssignUser(String assignUser) {
		this.assignUser = assignUser;
	}
	public Timestamp getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(Timestamp lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	
}
