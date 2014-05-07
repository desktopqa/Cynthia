package com.sogou.qadev.service.cynthia.vo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


public class ScriptVO implements Serializable {
	private String id;
	private String name;
	private String createUser;
	private Timestamp createTime;
	private Map<String,String> mountTemplates;
	private Map<String,String> allTemplates;
	private boolean isAsync;
	private boolean isBeforeCommit;
	private boolean isAfterSuccess;
	private boolean isAfterFail;
	private boolean isAfterQuery;
	private String script;
	private boolean isStatEdit;
	private boolean isActionEdit;
	private boolean isValid;
	private List<String> statIds;
	private List<String> actionIds;
	private List<String> allowedTemplates;
	private String scriptId;
	
	public List<String> getAllowedTemplates() {
		return allowedTemplates;
	}
	public void setAllowedTemplates(List<String> allowedTemplates) {
		this.allowedTemplates = allowedTemplates;
	}
	public List<String> getStatIds() {
		return statIds;
	}
	public void setStatIds(List<String> statIds) {
		this.statIds = statIds;
	}
	public List<String> getActionIds() {
		return actionIds;
	}
	public void setActionIds(List<String> actionIds) {
		this.actionIds = actionIds;
	}
	public boolean isStatEdit() {
		return isStatEdit;
	}
	public void setStatEdit(boolean isStatEdit) {
		this.isStatEdit = isStatEdit;
	}
	public boolean isActionEdit() {
		return isActionEdit;
	}
	public void setActionEdit(boolean isActionEdit) {
		this.isActionEdit = isActionEdit;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public Map<String, String> getAllTemplates() {
		return allTemplates;
	}
	public void setAllTemplates(Map<String, String> allTemplates) {
		this.allTemplates = allTemplates;
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
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Map<String, String> getMountTemplates() {
		return mountTemplates;
	}
	public void setMountTemplates(Map<String, String> mountTemplates) {
		this.mountTemplates = mountTemplates;
	}
	public boolean isAsync() {
		return isAsync;
	}
	public void setAsync(boolean isAsync) {
		this.isAsync = isAsync;
	}
	public boolean isBeforeCommit() {
		return isBeforeCommit;
	}
	public void setBeforeCommit(boolean isBeforeCommit) {
		this.isBeforeCommit = isBeforeCommit;
	}
	public boolean isAfterSuccess() {
		return isAfterSuccess;
	}
	public void setAfterSuccess(boolean isAfterSuccess) {
		this.isAfterSuccess = isAfterSuccess;
	}
	public boolean isAfterFail() {
		return isAfterFail;
	}
	public void setAfterFail(boolean isAfterFail) {
		this.isAfterFail = isAfterFail;
	}
	public boolean isAfterQuery() {
		return isAfterQuery;
	}
	public void setAfterQuery(boolean isAfterQuery) {
		this.isAfterQuery = isAfterQuery;
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getScriptId() {
		return scriptId;
	}
	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}
	
}
