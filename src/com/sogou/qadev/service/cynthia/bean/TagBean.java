package com.sogou.qadev.service.cynthia.bean;

/**
 * @description:tag bean
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:27:03
 * @version:v1.0
 */
public class TagBean {
	/**
	 * tag id
	 */
	private String id;
	/**
	 * tag name
	 */
	private String tagName;
	/**
	 * tag datas
	 */
	private String Datas;
	/**
	 * tag create user
	 */
	private String createUsers;
	/**
	 * tag filter id
	 */
	private String filterId;
	/**
	 * tag color
	 */
	private String tagColor;
	
	public String getTagColor() {
		return tagColor;
	}
	public void setTagColor(String tagColor) {
		this.tagColor = tagColor;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getDatas() {
		return Datas;
	}
	public void setDatas(String datas) {
		Datas = datas;
	}
	public String getCreateUsers() {
		return createUsers;
	}
	public void setCreateUsers(String createUsers) {
		this.createUsers = createUsers;
	}
	public String getFilterId() {
		return filterId;
	}
	public void setFilterId(String filterId) {
		this.filterId = filterId;
	}
}
