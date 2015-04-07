/**  
 * Copyright © 2015 sogou. All rights reserved.
 *
 * @Title: TemplateConfig.java
 * @Prject: Cynthia_File_Upload
 * @Package: com.sogou.qadev.service.cynthia.bean
 * @Description: TODO
 * @author: liming  
 * @date: 2015-3-11 上午11:32:07
 * @version: V1.0  
 */
package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;

/**
 * @ClassName: TemplateConfig
 * @Description: TODO
 * @author: liming
 * @date: 2015-3-11 上午11:32:07
 */
public class TemplateConfig implements Serializable{

	private boolean isProjectInvolve = false;  //是否与项目管理关联
	private String productInvolveId; //产品关联字段Id
	private String projectInvolveId; //项目关联字段ID
	
	public boolean isProjectInvolve() {
		return isProjectInvolve;
	}
	public void setIsProjectInvolve(boolean isProjectInvolve) {
		this.isProjectInvolve = isProjectInvolve;
	}
	public String getProductInvolveId() {
		return productInvolveId;
	}
	public void setProductInvolveId(String productInvolveId) {
		this.productInvolveId = productInvolveId;
	}
	public String getProjectInvolveId() {
		return projectInvolveId;
	}
	public void setProjectInvolveId(String projectInvolveId) {
		this.projectInvolveId = projectInvolveId;
	}
	
}
