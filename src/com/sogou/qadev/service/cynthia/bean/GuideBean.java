/**
 * 
 */
package com.sogou.qadev.service.cynthia.bean;

/**
 * @className:GuideBean
 * @description:TODO
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-6-10 上午11:13:54
 * @version:v1.0
 */
public class GuideBean {

	private int id;
	
	private String guideId;
	
	private String guideName;
	
	private int parentId;   //父级Id
	
	private String guideHtml;
	
	private boolean isDeleted = false;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGuideId() {
		return guideId;
	}

	public void setGuideId(String guideId) {
		this.guideId = guideId;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getGuideHtml() {
		return guideHtml;
	}

	public void setGuideHtml(String guideHtml) {
		this.guideHtml = guideHtml;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getGuideName() {
		return guideName;
	}

	public void setGuideName(String guideName) {
		this.guideName = guideName;
	}

}
