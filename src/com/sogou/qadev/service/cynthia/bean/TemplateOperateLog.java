package com.sogou.qadev.service.cynthia.bean;

import java.sql.Timestamp;

/**
 * @description:template opreate log
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:42:30
 * @version:v1.0
 */
public class TemplateOperateLog {
	
	public static final String ADD = "add";
	public static final String DELETE = "delete";
	public static final String MODIFY = "modify";
	
	private int id;
	private String templateId;
	private String fieldId;
	private String fieldName;
	private String operateType;  //日志类型  增删改
	private Timestamp createTime;
	private String createUser;
	private String before;  //修改之前xml
	private String after;   //修改之后xml
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getFieldId() {
		return fieldId;
	}
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getOperateType() {
		return operateType;
	}
	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getBefore() {
		return before;
	}
	public void setBefore(String before) {
		this.before = before;
	}
	public String getAfter() {
		return after;
	}
	public void setAfter(String after) {
		this.after = after;
	}
	
}
