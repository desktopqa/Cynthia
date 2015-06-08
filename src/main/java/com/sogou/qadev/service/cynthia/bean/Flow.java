package com.sogou.qadev.service.cynthia.bean;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface Flow extends BaseType{

	/**
	 * @Title: setProFlow
	 * @Description: set is project involve
	 * @param isProFlow
	 * @return: void
	 */
	public void setProFlow(boolean isProFlow);
	
	/**
	 * 
	 * @Title: isProFlow
	 * @Description: get is project involve
	 * @return
	 * @return: boolean
	 */
	public boolean isProFlow();
	
	public String getName();
	
	public void setName(String name);
	
	/**
	 * @description:return all stats
	 * @date:2014-5-6 下午12:30:52
	 * @version:v1.0
	 * @return
	 */
	public Map<UUID,Stat> getStatMap();

	/**
	 * @description:get all actions
	 * @date:2014-5-6 下午12:31:03
	 * @version:v1.0
	 * @return
	 */
	public Map<UUID,Action> getActionMap();

	/**
	 * @description:get all roles
	 * @date:2014-5-6 下午12:31:12
	 * @version:v1.0
	 * @return
	 */
	public Map<UUID,Role> getRoleMap();

	/**
	 * @description:get all actionRole
	 * @date:2014-5-6 下午12:31:21
	 * @version:v1.0
	 * @return
	 */
	public Set<ActionRole> getActionRoleSet();

	/**
	 * @description:TODO
	 * @date:2014-5-6 下午12:32:03
	 * @version:v1.0
	 * @return
	 */
	public String getCreateUser(); 
	
	/**
	 * @description:TODO
	 * @date:2014-5-6 下午12:32:11
	 * @version:v1.0
	 * @param userName
	 */
	public void setCreateUser(String userName);
	
	/**
	 * @description:get all rights
	 * @date:2014-5-6 下午12:31:52
	 * @version:v1.0
	 * @return
	 */
	public Set<Right> getRightSet();

	/**
	 * @description:set all stats
	 * @date:2014-5-6 下午12:32:21
	 * @version:v1.0
	 * @param statMap
	 */
	public void setStatMap(Map<UUID,Stat> statMap);

	/**
	 * @description:set all actions
	 * @date:2014-5-6 下午12:32:29
	 * @version:v1.0
	 * @param actionMap
	 */
	public void setActionMap(Map<UUID,Action> actionMap);

	/**
	 * @description:set all roles
	 * @date:2014-5-6 下午12:32:37
	 * @version:v1.0
	 * @param roleMap
	 */
	public void setRoleMap(Map<UUID,Role> roleMap);

	/**
	 * @description:set all action roles
	 * @date:2014-5-6 下午12:32:44
	 * @version:v1.0
	 * @param actionRoleSet
	 */
	public void setActionRoleSet(Set<ActionRole> actionRoleSet);

	/**
	 * @description:set all rights
	 * @date:2014-5-6 下午12:33:14
	 * @version:v1.0
	 * @param rightSet
	 */
	public void setRightSet(Set<Right> rightSet);

	/**
	 * @description:get all stats
	 * @date:2014-5-6 下午12:33:30
	 * @version:v1.0
	 * @return
	 */
	public Stat[] getStats();

	/**
	 * @description:get stat by id
	 * @date:2014-5-6 下午12:33:37
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public Stat getStat(UUID id);

	/**
	 * @description:get stat by stat name
	 * @date:2014-5-6 下午12:33:47
	 * @version:v1.0
	 * @param name
	 * @return
	 */
	public Stat getStat(String name);

	/**
	 * @description:add stat (add uuid)
	 * @date:2014-5-6 下午12:33:58
	 * @version:v1.0
	 * @return
	 */
	public Stat addStat();

	/**
	 * @description:remove stat by stat id
	 * @date:2014-5-6 下午12:34:28
	 * @version:v1.0
	 * @param id
	 */
	public void removeStat(UUID id);

	/**
	 * @description:get all begin stats
	 * @date:2014-5-6 下午12:34:40
	 * @version:v1.0
	 * @return
	 */
	public Stat[] getBeginStats();

	/**
	 * @description:get all end stats
	 * @date:2014-5-6 下午12:34:50
	 * @version:v1.0
	 * @return
	 */
	public Stat[] getEndStats();

	/**
	 * @description:get all end actions
	 * @date:2014-5-6 下午12:35:03
	 * @version:v1.0
	 * @return
	 */
	public Action[] getEndActions();
	
	/**
	 * @description:get all start actions
	 * @date:2014-5-6 下午12:35:03
	 * @version:v1.0
	 * @return
	 */
	public Set<Action> getStartActions();

	/**
	 * @description:get all actions
	 * @date:2014-5-6 下午12:35:12
	 * @version:v1.0
	 * @return
	 */
	public Action[] getActions();

	/**
	 * @description:get action by id
	 * @date:2014-5-6 下午12:35:22
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public Action getAction(UUID id);

	/**
	 * @description:get action by action name
	 * @date:2014-5-6 下午12:35:30
	 * @version:v1.0
	 * @param name
	 * @return
	 */
	public Action getAction(String name);

	/**
	 * @description:add action to flow 
	 * @date:2014-5-6 下午12:35:41
	 * @version:v1.0
	 * @param beginStat:action begin stat id
	 * @param endStat:action end stat id
	 * @return
	 */
	public Action addAction(UUID beginStat, UUID endStat);

	/**
	 * @description:remove action from flow
	 * @date:2014-5-6 下午12:36:04
	 * @version:v1.0
	 * @param id
	 */
	public void removeAction(UUID id);

	/**
	 * @description:return all roles
	 * @date:2014-5-6 下午12:36:30
	 * @version:v1.0
	 * @return
	 */
	public Role[] getRoles();

	/**
	 * @description:get role by role id
	 * @date:2014-5-6 下午12:36:39
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public Role getRole(UUID id);

	/**
	 * @description:get role by role name
	 * @date:2014-5-6 下午12:36:50
	 * @version:v1.0
	 * @param name
	 * @return
	 */
	public Role getRole(String name);

	/**
	 * @description:add role to flow(uuid)
	 * @date:2014-5-6 下午12:37:07
	 * @version:v1.0
	 * @return
	 */
	public Role addRole();

	/**
	 * @description:remove role from flow
	 * @date:2014-5-6 下午12:37:20
	 * @version:v1.0
	 * @param id
	 */
	public void removeRole(UUID id);

	/**
	 * @description:return if user has right
	 * @date:2014-5-6 下午12:38:32
	 * @version:v1.0
	 * @param username
	 * @param templateId
	 * @param roleId:role id
	 * @return
	 */
	public boolean hasRight(String username, UUID templateId, UUID roleId);

	/**
	 * @description:add user right to flow
	 * @date:2014-5-6 下午12:38:55
	 * @version:v1.0
	 * @param username
	 * @param templateId
	 * @param roleId
	 */
	public void addRight(String username, UUID templateId, UUID roleId);
	
	/**
	 * @description:add right to flow
	 * @date:2014-5-6 下午12:39:08
	 * @version:v1.0
	 * @param right
	 */
	public void addRight(Right right);

	/**
	 * @description:remove user right from flow
	 * @date:2014-5-6 下午12:39:18
	 * @version:v1.0
	 * @param username
	 * @param templateId
	 * @param roleId
	 */
	public void removeRight(String username, UUID templateId, UUID roleId);

	/**
	 * @description:remove all user rights
	 * @date:2014-5-6 下午12:39:35
	 * @version:v1.0
	 * @param username
	 */
	public void removeRight(String username);

	/**
	 * @description:get if has action role
	 * @date:2014-5-6 下午12:40:03
	 * @version:v1.0
	 * @param actionId
	 * @param roleId
	 * @return
	 */
	public boolean hasActionRole(UUID actionId, UUID roleId);

	/**
	 * @description:add action role
	 * @date:2014-5-6 下午12:40:51
	 * @version:v1.0
	 * @param actionId
	 * @param roleId
	 */
	public void addActionRole(UUID actionId, UUID roleId);

	/**
	 * @description:remove action role
	 * @date:2014-5-6 下午12:40:58
	 * @version:v1.0
	 * @param actionId
	 * @param roleId
	 */
	public void removeActionRole(UUID actionId, UUID roleId);

	/**
	 * @description:query all actions by role
	 * @date:2014-5-6 下午12:41:10
	 * @version:v1.0
	 * @param roleId
	 * @return
	 */
	public Action[] queryRoleActions(UUID roleId);

	/**
	 * @description:query all roles by action
	 * @date:2014-5-6 下午12:41:26
	 * @version:v1.0
	 * @param actionId
	 * @return
	 */
	public Role[] queryActionRoles(UUID actionId);

	/**
	 * @Title: queryActionsByStartStatId
	 * @Description: queryActionsByStartStatId
	 * @param statId
	 * @return
	 * @return: Set<Action>
	 */
	public Set<Action> queryActionsByStartStatId(UUID statId);
	
	/**
	 * @Title: queryActionsByEndStatId
	 * @Description: query all actions by end statId 
	 * @param statId
	 * @return
	 * @return: Set<Action>
	 */
	public Set<Action> queryActionsByEndStatId(UUID statId);
	
	/**
	 * @Title: queryActionRoleIds
	 * @Description: query all role ids by action
	 * @param actionId
	 * @return
	 * @return: String
	 */
	public String queryActionRoleIds(UUID actionId);
	
	/**
	 * @Title: queryNextActionRoleIds
	 * @Description: query all next actin role ids by action
	 * @param actionId
	 * @return
	 * @return: String
	 */
	public String queryNextActionRoleIdsByActionId(UUID actionId);
	
	/**
	 * @Title: queryNextActionRoleIdsByStatId
	 * @Description: query all next actin role ids by stat
	 * @param statId
	 * @return
	 * @return: String
	 */
	public String queryNextActionRoleIdsByStatId(UUID statId);
	
	/**
	 * @description:query all actions of stat(after this stat)
	 * @date:2014-5-6 下午12:41:41
	 * @version:v1.0
	 * @param statId
	 * @return
	 */

	/**
	 * @Title: queryActionRoleIds
	 * @Description: query all role ids by action and user
	 * @param userName
	 * @param actionId
	 * @return
	 * @return: String
	 */
	
	public Action[] queryStatActions(UUID statId);

	/**
	 * @description:query user actions start from stat
	 * @date:2014-5-6 下午12:42:30
	 * @version:v1.0
	 * @param username
	 * @param templateId
	 * @param statId
	 * @return
	 */
	public Action[] queryUserNodeStatActions(String username, UUID templateId, UUID statId);

	/**
	 * @description:query user all begin actions
	 * @date:2014-5-6 下午12:42:52
	 * @version:v1.0
	 * @param username
	 * @param templateId
	 * @return
	 */
	public Action[] queryUserNodeBeginActions(String username, UUID templateId);

	/**
	 * @description:check if the action is start action
	 * @date:2014-5-6 下午2:05:34
	 * @version:v1.0
	 * @param username
	 * @param templateId
	 * @param actionId
	 * @return
	 */
	public boolean authenticate(String username, UUID templateId, UUID actionId);

	/**
	 * @description:check if action begin stat 
	 * @date:2014-5-6 下午2:07:29
	 * @version:v1.0
	 * @param username
	 * @param templateId
	 * @param statId
	 * @param actionId
	 * @return
	 */
	public boolean authenticate(String username, UUID templateId, UUID statId, UUID actionId);

	/**
	 * @description:get all users of template
	 * @date:2014-5-6 下午2:08:00
	 * @version:v1.0
	 * @param templateId
	 * @return
	 */
	public String[] queryNodeUsers(UUID templateId);

	/**
	 * @description:query node user rights
	 * @date:2014-8-13 下午5:15:42
	 * @version:v1.0
	 * @param templateId
	 * @return
	 */
	public Right[] queryNodeUserRight(UUID templateId);
	
	/**
	 * @description:get all users of template and role
	 * @date:2014-5-6 下午2:08:42
	 * @version:v1.0
	 * @param templateId
	 * @param roleId
	 * @return
	 */
	public String[] queryNodeRoleUsers(UUID templateId, UUID roleId);

	/**
	 * @description:query users of stat
	 * @date:2014-5-6 下午2:08:59
	 * @version:v1.0
	 * @param templateId
	 * @param statId
	 * @return
	 */
	public String[] queryNodeStatAssignUsers(UUID templateId, UUID statId);

	/**
	 * @description:query all roles of template
	 * @date:2014-5-6 下午2:09:51
	 * @version:v1.0
	 * @param templateId
	 * @return
	 */
	public Role[] queryNodeRoles(UUID templateId);

	/**
	 * @description:query all roles of template and user
	 * @date:2014-5-6 下午2:10:05
	 * @version:v1.0
	 * @param user
	 * @param templateId
	 * @return
	 */
	public Role[] queryUserNodeRoles(String user, UUID templateId);

	/**
	 * @description:query all begin actions
	 * @date:2014-5-6 下午2:10:17
	 * @version:v1.0
	 * @return
	 */
	public Action[] queryBeginActions();
	
	/**
	 * @Title: isEndAction
	 * @Description: judge if is end action
	 * @param actionId
	 * @return
	 * @return: boolean
	 */
	public boolean isEndAction(UUID actionId);

	/**
	 * @description:check if action is everyone has
	 * @date:2014-5-6 下午2:10:27
	 * @version:v1.0
	 * @param actionId
	 * @return
	 */
	public boolean isActionEveryoneRole(UUID actionId);

	/**
	 * @description:check if role has edit right
	 * @date:2014-5-6 下午2:11:04
	 * @version:v1.0
	 * @param roleId
	 * @return
	 */
	public boolean isRoleEditAction(UUID roleId);

	/**
	 * @description:check if role has read right
	 * @date:2014-5-6 下午2:11:20
	 * @version:v1.0
	 * @param roleId
	 * @return
	 */
	public boolean isRoleReadAction(UUID roleId);

	/**
	 * @description:check if role has delete right
	 * @date:2014-5-6 下午2:11:31
	 * @version:v1.0
	 * @param roleId
	 * @return
	 */
	public boolean isRoleDeleteAction(UUID roleId);

	/**
	 * @description:get everyone actions
	 * @date:2014-5-6 下午2:11:42
	 * @version:v1.0
	 * @return
	 */
	public Action[] queryEveryoneRoleActions();

	/**
	 * @description:query roles has edit right
	 * @date:2014-5-6 下午2:12:02
	 * @version:v1.0
	 * @return
	 */
	public Role[] queryEditActionRoles();

	/**
	 * @description:query roles has read right
	 * @date:2014-5-6 下午2:12:13
	 * @version:v1.0
	 * @return
	 */
	public Role[] queryReadActionRoles();

	/**
	 * @description:check if user is allowed to edit
	 * @date:2014-5-6 下午2:12:27
	 * @version:v1.0
	 * @param user
	 * @param nodeId
	 * @param assignUser
	 * @param actionUser
	 * @return
	 */
	public boolean isEditActionAllow(String user, UUID nodeId, String assignUser, String actionUser);

	/**
	 * @description:check if user is allowed to read
	 * @date:2014-5-6 下午2:13:31
	 * @version:v1.0
	 * @param user
	 * @param nodeId
	 * @param assignUser
	 * @param logUserArray
	 * @return
	 */
	public boolean isReadActionAllow(String user, UUID nodeId, String assignUser, String[] logUserArray);

	/**
	 * @description:check if user is allowed to delete
	 * @date:2014-5-6 下午2:13:43
	 * @version:v1.0
	 * @param user
	 * @param nodeId
	 * @return
	 */
	public boolean isDeleteActionAllow(String user, UUID nodeId);
	
	/**
	 * @description:query all quit users by role id
	 * @date:2014-5-6 下午2:13:53
	 * @version:v1.0
	 * @param roleId
	 * @return
	 */
	public List<UserInfo> queryAllQuitUserInfo(UUID roleId);  
	
	/**
	 * @description:query all users by role id
	 * @date:2014-5-6 下午2:14:11
	 * @version:v1.0
	 * @param roleId
	 * @return
	 */
	public List<UserInfo> queryAllUserInfo(UUID roleId);
	
	/**
	 * @description:query all users from flow
	 * @date:2014-5-6 下午2:14:24
	 * @version:v1.0
	 * @return
	 */
	public List<UserInfo> queryAllUserInfo();
	
	/**
	 * @description:query all user from flow by role id
	 * @date:2014-5-6 下午2:14:37
	 * @version:v1.0
	 * @param roleId
	 * @return
	 */
	public Set<Right> queryRightsByRole(UUID roleId); 
	
	/**
	 * query all user from flow by role id and template id
	 * @param roleId
	 * @param templateId
	 * @return
	 */
	public Set<Right> queryRightsByRole(UUID roleId, UUID templateId);
	
	/**
	 * @description:query all users by role id
	 * @date:2014-5-6 下午2:21:14
	 * @version:v1.0
	 * @param roleId
	 * @return
	 */
	public Set<String> queryAllUserByRole(UUID roleId);
	
	/**
	 * @description:query all users
	 * @date:2014-5-6 下午2:22:16
	 * @version:v1.0
	 * @return:set <userMail>
	 */
	public Set<String> queryAllUser();
}
