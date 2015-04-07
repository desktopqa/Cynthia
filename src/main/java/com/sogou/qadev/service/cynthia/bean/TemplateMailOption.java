package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sogou.qadev.service.cynthia.bean.impl.FieldImpl;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.ProjectInvolveManager;

/**
 * @ClassName: TemplateMailOption
 * @Description: 表单邮件配置
 * @author: liming
 * @date: 2014-11-17 上午11:37:12
 */
public class TemplateMailOption implements Serializable {
	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 * @Description: TODO
	 */
	private static final long serialVersionUID = 1L;

	private UUID templateId;
	
	/**
	 * 邮件主题
	 */
	private String mailSubject = ""; 
	
	private boolean sendMail = false;
	

	/**
	 * 动作 收信人员
	 */
	private Map<String, String> actionUsers = new HashMap<String, String>();
	

	public UUID getTemplateId() {
		return templateId;
	}

	public void setTemplateId(UUID templateId) {
		this.templateId = templateId;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public Map<String, String> getActionUsers() {
		return actionUsers;
	}

	public void setActionUsers(Map<String, String> actionUsers) {
		this.actionUsers = actionUsers;
	}
	
	public void setActionUser(String actionId,String users){
		this.actionUsers.put(actionId, users);
	}
	
	public boolean isSendMail() {
		return sendMail;
	}

	public void setSendMail(boolean sendMail) {
		this.sendMail = sendMail;
	}
	
	/**
	 * @Title: getActionUser
	 * @Description: return all actionId Users
	 * @param actionId
	 * @return
	 * @return: Set<String>
	 */
	public Set<String> getActionUser(UUID actionId,Data data)
	{
		DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
		Set<String> allUserSet = new HashSet<String>();
		Template template = das.queryTemplate(this.templateId);
		if (template == null) {
			return allUserSet;
		}
		
		Flow flow = das.queryFlow(template.getFlowId());
		if (flow == null) {
			return allUserSet;
		}
		
		String users = this.actionUsers.get(actionId.getValue());
		//如果没有配置单独动作，尝试采用全局动作
		users = users == null ? this.actionUsers.get("all") : users;
		if (users == null || users.equals("")) {
			return allUserSet;
		}else {
			String[] allUserArr = users.split(",");
			if (allUserArr != null && allUserArr.length > 0) {
				for (String user : allUserArr) {
					if (user.startsWith("role_")) {
						//角色
						UUID roleId = DataAccessFactory.getInstance().createUUID(user.replace("role_", ""));
						
						if (template.getTemplateConfig().isProjectInvolve()) {
							//根据data 中项目Id查询用户
							Field field = template.getField(DataAccessFactory.getInstance().createUUID(template.getTemplateConfig().getProjectInvolveId()));
							if (field != null) {
								UUID productId = data.getSingleSelection(field.getId());
								if (productId != null) {
									List<UserInfo> allUsers = ProjectInvolveManager.getInstance().getUserInfoByProjectAndRole(template.getCreateUser(), productId.getValue(), roleId.getValue());
									for (UserInfo userInfo : allUsers) {
										allUserSet.add(userInfo.getUserName());
									}
								}
							}
						}else {
							Set<Right> allRoleRights = flow.queryRightsByRole(roleId, this.templateId);
							for (Right right : allRoleRights) {
								allUserSet.add(right.getUsername());
							}
						}
						
					}else {
						allUserSet.add(user);
					}
				}
			}
		}
		return allUserSet;
	}

	
	public TemplateMailOption clone()
	{
		TemplateMailOption tmo = new TemplateMailOption();
		tmo.templateId = this.templateId;
		tmo.setMailSubject(this.getMailSubject());
		tmo.sendMail = this.sendMail;
		for(String actionId : this.actionUsers.keySet()){
			tmo.setActionUser(actionId, this.actionUsers.get(actionId));
		}
		return tmo;
	}
}
