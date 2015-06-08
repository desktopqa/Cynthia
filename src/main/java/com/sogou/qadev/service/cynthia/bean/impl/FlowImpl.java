package com.sogou.qadev.service.cynthia.bean.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import bsh.This;

import com.sogou.qadev.service.cynthia.bean.Action;
import com.sogou.qadev.service.cynthia.bean.ActionRole;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Right;
import com.sogou.qadev.service.cynthia.bean.Role;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.ConfigManager;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.ProjectInvolveManager;
import com.sogou.qadev.service.cynthia.util.ArrayUtil;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

/**
 * @description:flow
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午2:15:06
 * @version:v1.0
 */
public class FlowImpl implements Flow{
	
	private static final long serialVersionUID = -1L;
	private Map<UUID, Action> actionMap = new HashMap<UUID, Action>();
	private Set<ActionRole> actionRoleSet = new HashSet<ActionRole>();
	/**
	 * flow createuser
	 */
	private String createUser = "";
	/**
	 * flow id
	 */
	private UUID id = null;
	private boolean isProFlow = false;  //是否项目管理相关流程
	/**
	 * flow name
	 */
	private String name = null;
	
	private Set<Right> rightSet = new HashSet<Right>();

	private Map<UUID, Role> roleMap = new HashMap<UUID, Role>();

	private Map<UUID, Stat> statMap = new HashMap<UUID, Stat>();
	/**
	 * 
	 * <h1> Title:</h1>
	 * <p> Description:init flow from root</p>
	 * @date：2014-5-6 
	 * @param root
	 * @param createUser
	 */
	public FlowImpl(org.w3c.dom.Node root,String createUser){
		this.createUser = createUser;
		DataAccessFactory daf = DataAccessFactory.getInstance();

		Node flowNode = XMLUtil.getSingleNode(root, "flow");

		this.id = daf.createUUID(XMLUtil.getSingleNodeTextContent(flowNode, "id"));
		this.name = XMLUtil.getSingleNodeTextContent(flowNode, "name");
		this.isProFlow = Boolean.parseBoolean(XMLUtil.getSingleNodeTextContent(flowNode, "isProFlow"));
		List<org.w3c.dom.Node> statNodeList = XMLUtil.getNodes(flowNode, "stats/stat");
		for(org.w3c.dom.Node statNode : statNodeList){
			UUID statId = daf.createUUID(XMLUtil.getSingleNodeTextContent(statNode, "id"));
			String statName = XMLUtil.getSingleNodeTextContent(statNode, "name");

			StatImpl statImpl = new StatImpl(statId, this.id);
			statImpl.setName(statName);

			this.statMap.put(statId, statImpl);
		}

		List<org.w3c.dom.Node> actionNodeList = XMLUtil.getNodes(flowNode, "actions/action");
		for(org.w3c.dom.Node actionNode : actionNodeList){
			UUID actionId = daf.createUUID(XMLUtil.getSingleNodeTextContent(actionNode, "id"));
			String actionName = XMLUtil.getSingleNodeTextContent(actionNode, "name");
			String assignToMore = XMLUtil.getSingleNodeTextContent(actionNode, "assignToMore");
			UUID beginStatId = daf.createUUID(XMLUtil.getSingleNodeTextContent(actionNode, "startStatId"));
			UUID endStatId = daf.createUUID(XMLUtil.getSingleNodeTextContent(actionNode, "endStatId"));

			ActionImpl actionImpl = new ActionImpl(actionId, this.id);
			actionImpl.setName(actionName);
			actionImpl.setBeginStatId(beginStatId);
			actionImpl.setEndStatId(endStatId);
			actionImpl.setAssignToMore(CynthiaUtil.isNull(assignToMore) ? false : assignToMore.equals("true"));
			
			this.actionMap.put(actionId, actionImpl);
		}

		List<org.w3c.dom.Node> roleNodeList = XMLUtil.getNodes(flowNode, "roles/role");
		for(org.w3c.dom.Node roleNode : roleNodeList){
			UUID roleId	= daf.createUUID(XMLUtil.getSingleNodeTextContent(roleNode, "id"));
			String roleName = XMLUtil.getSingleNodeTextContent(roleNode, "name");

			RoleImpl roleImpl = new RoleImpl(roleId, this.id);
			roleImpl.setName(roleName);

			this.roleMap.put(roleId, roleImpl);
		}

		List<org.w3c.dom.Node> actionRoleNodeList = XMLUtil.getNodes(flowNode, "actionRoles/actionRole");
		for(org.w3c.dom.Node actionRoleNode : actionRoleNodeList){
			UUID actionId = daf.createUUID(XMLUtil.getSingleNodeTextContent(actionRoleNode, "actionId"));
			UUID roleId = daf.createUUID(XMLUtil.getSingleNodeTextContent(actionRoleNode, "roleId"));

			this.actionRoleSet.add(new ActionRole(actionId, roleId));
		}

		List<org.w3c.dom.Node> rightNodeList = XMLUtil.getNodes(flowNode, "rights/right");
		for(org.w3c.dom.Node rightNode : rightNodeList){
			String username = XMLUtil.getSingleNodeTextContent(rightNode, "username");
			String nickname = CynthiaUtil.getUserAlias(username);
			UUID roleId = daf.createUUID(XMLUtil.getSingleNodeTextContent(rightNode, "roleId"));
			UUID templateId = daf.createUUID(XMLUtil.getSingleNodeTextContent(rightNode, "templateId"));
			if (templateId == null) {
				continue;
			}
			this.rightSet.add(new Right(username, templateId, roleId,nickname));
		}
	}

	public FlowImpl(UUID id){
		this.id = id;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:addAction</p>
	 * <p> Description:TODO</p>
	 * @param beginStatId
	 * @param endStatId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#addAction(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Action addAction(UUID beginStatId, UUID endStatId){
		UUID actionId = DataAccessFactory.getInstance().newUUID("ACTI");
		ActionImpl action = new ActionImpl(actionId, this.id);
		action.setBeginStatId(beginStatId);
		action.setEndStatId(endStatId);
		this.actionMap.put(actionId, action);

		return action;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:addActionRole</p>
	 * <p> Description:TODO</p>
	 * @param actionId
	 * @param roleId
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#addActionRole(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void addActionRole(UUID actionId, UUID roleId){
		this.actionRoleSet.add(new ActionRole(actionId, roleId));
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:addRight</p>
	 * <p> Description:TODO</p>
	 * @param right
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#addRight(com.sogou.qadev.service.cynthia.bean.Right)
	 */
	public void addRight(Right right){
		this.rightSet.add(right);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:addRight</p>
	 * <p> Description:TODO</p>
	 * @param username
	 * @param templateId
	 * @param roleId
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#addRight(java.lang.String, com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void addRight(String username, UUID templateId, UUID roleId){
		String nickname = CynthiaUtil.getUserAlias(username);
		this.rightSet.add(new Right(username, templateId, roleId,nickname));
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:addRole</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#addRole()
	 */
	public Role addRole(){
		UUID roleId = DataAccessFactory.getInstance().newUUID("ROLE");
		RoleImpl role = new RoleImpl(roleId, this.id);
		this.roleMap.put(roleId, role);

		return role;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:addStat</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#addStat()
	 */
	public Stat addStat(){
		UUID statId = DataAccessFactory.getInstance().newUUID("STAT");
		StatImpl statImpl = new StatImpl(statId, this.id);
		this.statMap.put(statId, statImpl);

		return statImpl;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:authenticate</p>
	 * <p> Description:TODO</p>
	 * @param username
	 * @param templateId
	 * @param actionId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#authenticate(java.lang.String, com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public boolean authenticate(String username, UUID templateId, UUID actionId){
		if (username.indexOf(",") != -1) {
			username = username.split(",")[0];
		}
		Action[] actionArray = this.queryUserNodeBeginActions(username, templateId);
		for(Action action : actionArray){
			if(action.getId().equals(actionId)){
				return true;
			}
		}

		return false;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:authenticate</p>
	 * <p> Description:TODO</p>
	 * @param username
	 * @param templateId
	 * @param statId
	 * @param actionId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#authenticate(java.lang.String, com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public boolean authenticate(String username, UUID templateId, UUID statId, UUID actionId){
		if (username.indexOf(",") != -1) {
			username = username.split(",")[0];
		}
		Action[] actionArray = this.queryUserNodeStatActions(username, templateId, statId);
		for(Action action : actionArray){
			if(action.getId().equals(actionId)){
				return true;
			}
		}

		return false;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:clone</p>
	 * <p> Description:flow clone</p>
	 * @return
	 * @see java.lang.Object#clone()
	 */
	public Flow clone(){
		FlowImpl flowImpl = new FlowImpl(this.id);
		flowImpl.name = this.name;
		flowImpl.isProFlow = this.isProFlow;
		flowImpl.createUser = this.createUser;

		for(UUID statId : this.statMap.keySet()){
			flowImpl.statMap.put(statId, this.statMap.get(statId).clone());
		}

		for(UUID actionId : this.actionMap.keySet()){
			flowImpl.actionMap.put(actionId, this.actionMap.get(actionId).clone());
		}

		for(UUID roleId : this.roleMap.keySet()){
			flowImpl.roleMap.put(roleId, this.roleMap.get(roleId).clone());
		}

		for(Right right : this.rightSet){
			flowImpl.rightSet.add(right);
		}

		for(ActionRole actionRole : this.actionRoleSet){
			flowImpl.actionRoleSet.add(actionRole);
		}

		return flowImpl;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:equals</p>
	 * <p> Description:TODO</p>
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj){
		return this.id.equals(((FlowImpl)obj).id);
	}
	/**
	 * (non-Javadoc)
	 * <p> Title:getAction</p>
	 * <p> Description:TODO</p>
	 * @param actionName
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getAction(java.lang.String)
	 */
	public Action getAction(String actionName){
		for(Action action : this.actionMap.values()){
			if(action.getName().equals(actionName)){
				return action;
			}
		}

		return null;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getAction</p>
	 * <p> Description:TODO</p>
	 * @param actionId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getAction(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Action getAction(UUID actionId){
		return this.actionMap.get(actionId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getActionMap</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getActionMap()
	 */
	public Map<UUID, Action> getActionMap() {
		return this.actionMap;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getActionRoleSet</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getActionRoleSet()
	 */
	public Set<ActionRole> getActionRoleSet() {
		return this.actionRoleSet;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getActions</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getActions()
	 */
	public Action[] getActions(){
		return this.actionMap.values().toArray(new Action[this.actionMap.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getBeginStats</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getBeginStats()
	 */
	public Stat[] getBeginStats(){
		HashSet<Stat> statSet = new HashSet<Stat>();
		for(Action action : this.actionMap.values()){
			if(action.getBeginStatId() != null){
				continue;
			}

			Stat endStat = this.statMap.get(action.getEndStatId());
			if(endStat != null){
				statSet.add(endStat);
			}
		}

		return statSet.toArray(new Stat[statSet.size()]);
	}

	@Override
	public String getCreateUser() {
		return this.createUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getEndActions</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getEndActions()
	 */
	public Action[] getEndActions(){
		Stat[] endStats = getEndStats();
		HashSet<Action> actionSet = new HashSet<Action>();
		for(Stat stat : endStats){
			for(Action action : this.actionMap.values()){
				if(action.getEndStatId()!=null&&action.getEndStatId().equals(stat.getId()))
					actionSet.add(action);
			}
		}

		return actionSet.toArray(new Action[actionSet.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getEndStats</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getEndStats()
	 */
	public Stat[] getEndStats(){
		HashSet<Stat> statSet = new HashSet<Stat>();
		for(Stat stat : this.statMap.values()){
			boolean isEnd = true;
			for(Action action : this.actionMap.values()){
				if(action.getBeginStatId() != null && action.getBeginStatId().equals(stat.getId())){
					isEnd = false;
					break;
				}
			}

			if(isEnd){
				statSet.add(stat);
			}
		}

		return statSet.toArray(new Stat[statSet.size()]);
	}

	@Override
	public UUID getId(){
		return this.id;
	}
	
	@Override
	public String getName(){
		return this.name;
	}

	@Override
	public Set<Right> getRightSet() {
		return this.rightSet;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getRole</p>
	 * <p> Description:TODO</p>
	 * @param roleName
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getRole(java.lang.String)
	 */
	public Role getRole(String roleName){
		for(Role role : this.roleMap.values()){
			if(role.getName().equals(roleName)){
				return role;
			}
		}

		return null;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getRole</p>
	 * <p> Description:TODO</p>
	 * @param roleId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getRole(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Role getRole(UUID roleId){
		return this.roleMap.get(roleId);
	}

	@Override
	public Map<UUID, Role> getRoleMap() {
		return this.roleMap;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getRoles</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getRoles()
	 */
	public Role[] getRoles(){
		if (ConfigManager.getProjectInvolved() && this.isProFlow) {
			return ProjectInvolveManager.getInstance().getAllRole(this.createUser).toArray(new Role[0]);
		}else {
			return this.roleMap.values().toArray(new Role[this.roleMap.size()]);
		}
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getStartActions</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getStartActions()
	 */
	public Set<Action> getStartActions(){
		HashSet<Action> actionSet = new HashSet<Action>();
		for(Action action : this.actionMap.values()){
			if(action.getBeginStatId()==null)
				actionSet.add(action);
		}

		return actionSet;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getStat</p>
	 * <p> Description:TODO</p>
	 * @param statName
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getStat(java.lang.String)
	 */
	public Stat getStat(String statName){
		for(Stat stat : this.statMap.values()){
			if(stat.getName().equals(statName)){
				return stat;
			}
		}

		return null;
	}

	public Stat getStat(UUID statId){
		return this.statMap.get(statId);
	}

	@Override
	public Map<UUID, Stat> getStatMap() {
		return this.statMap;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getStats</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#getStats()
	 */
	public Stat[] getStats(){
		return this.statMap.values().toArray(new Stat[this.statMap.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:hasActionRole</p>
	 * <p> Description:TODO</p>
	 * @param actionId
	 * @param roleId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#hasActionRole(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public boolean hasActionRole(UUID actionId, UUID roleId){
		return this.actionRoleSet.contains(new ActionRole(actionId, roleId));
	}

	public int hashCode(){
		return this.id.hashCode();
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:hasRight</p>
	 * <p> Description:TODO</p>
	 * @param username
	 * @param templateId
	 * @param roleId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#hasRight(java.lang.String, com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public boolean hasRight(String username, UUID templateId, UUID roleId){
		if (this.isProFlow) {
			return ProjectInvolveManager.getInstance().isUserInRole(username, roleId);
		}else {
			String nickname = CynthiaUtil.getUserAlias(username);
			if (username.indexOf(",") != -1) {
				username = username.split(",")[0];
			}
			return this.rightSet.contains(new Right(username, templateId, roleId,nickname));
		}
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isActionEveryoneRole</p>
	 * <p> Description:TODO</p>
	 * @param actionId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#isActionEveryoneRole(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public boolean isActionEveryoneRole(UUID actionId){
		for(ActionRole actionRole : this.actionRoleSet){
			if(actionRole.actionId.equals(actionId) && actionRole.roleId.equals(Role.everyoneUUID)){
				return true;
			}
		}

		return false;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isDeleteActionAllow</p>
	 * <p> Description:TODO</p>
	 * @param user
	 * @param templateId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#isDeleteActionAllow(java.lang.String, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	@Override
	public boolean isDeleteActionAllow(String user, UUID templateId) {
		if(isActionEveryoneRole(Action.deleteUUID))
			return true;

		Role[] roleArray = queryUserNodeRoles(user, templateId);
		if(roleArray != null)
		{
			for(Role role : roleArray)
			{
				if(isRoleDeleteAction(role.getId()))
					return true;
			}
		}
		return false;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isEditActionAllow</p>
	 * <p> Description:TODO</p>
	 * @param user
	 * @param templateId
	 * @param assignUser
	 * @param actionUser
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#isEditActionAllow(java.lang.String, com.sogou.qadev.service.cynthia.bean.UUID, java.lang.String, java.lang.String)
	 */
	public boolean isEditActionAllow(String user, UUID templateId, String assignUser, String actionUser)
	{
		if(isActionEveryoneRole(Action.editUUID))
			return true;

		Role[] roleArray = queryUserNodeRoles(user, templateId);
		if(roleArray != null)
		{
			for(Role role : roleArray)
			{
				if(isRoleEditAction(role.getId()))
					return true;
			}
		}

		Set<UUID> roleIdSet = new HashSet<UUID>();
		if(roleArray != null)
		{
			for(Role role : roleArray)
				roleIdSet.add(role.getId());
		}

		return false;
	}
	
	public boolean isProFlow() {
		return isProFlow;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isReadActionAllow</p>
	 * <p> Description:TODO</p>
	 * @param user
	 * @param templateId
	 * @param assignUser
	 * @param logUserArray
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#isReadActionAllow(java.lang.String, com.sogou.qadev.service.cynthia.bean.UUID, java.lang.String, java.lang.String[])
	 */
	public boolean isReadActionAllow(String user, UUID templateId, String assignUser, String[] logUserArray)
	{
		if(isActionEveryoneRole(Action.readUUID))
			return true;

		Role[] roleArray = queryUserNodeRoles(user, templateId);
		if(roleArray != null)
		{
			for(Role role : roleArray)
			{
				if(isRoleReadAction(role.getId()))
					return true;
			}
		}

		Set<UUID> roleIdSet = new HashSet<UUID>();
		if(roleArray != null)
		{
			for(Role role : roleArray)
				roleIdSet.add(role.getId());
		}

		if(assignUser != null)
		{
			Role[] assignUserRoleArray = queryUserNodeRoles(assignUser, templateId);
			if(assignUserRoleArray != null)
			{
				for(Role assignUserRole : assignUserRoleArray)
				{
					if(roleIdSet.contains(assignUserRole.getId()))
						return true;
				}
			}
		}

		for(String logUser : logUserArray)
		{
			Role[] logUserRoleArray = queryUserNodeRoles(logUser, templateId);
			if(logUserRoleArray != null)
			{
				for(Role logUserRole : logUserRoleArray)
				{
					if(roleIdSet.contains(logUserRole.getId()))
						return true;
				}
			}
		}

		return false;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isRoleDeleteAction</p>
	 * <p> Description:TODO</p>
	 * @param roleId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#isRoleDeleteAction(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public boolean isRoleDeleteAction(UUID roleId) {
		for(ActionRole actionRole : this.actionRoleSet){
			if(actionRole.roleId.equals(roleId) && actionRole.actionId.equals(Action.deleteUUID)){
				return true;
			}
		}

		return false;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:isRoleEditAction</p>
	 * <p> Description:TODO</p>
	 * @param roleId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#isRoleEditAction(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public boolean isRoleEditAction(UUID roleId){
		for(ActionRole actionRole : this.actionRoleSet){
			if(actionRole.roleId.equals(roleId) && actionRole.actionId.equals(Action.editUUID)){
				return true;
			}
		}
		return false;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isRoleReadAction</p>
	 * <p> Description:TODO</p>
	 * @param roleId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#isRoleReadAction(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public boolean isRoleReadAction(UUID roleId){
		for(ActionRole actionRole : this.actionRoleSet){
			if(actionRole.roleId.equals(roleId) && actionRole.actionId.equals(Action.readUUID)){
				return true;
			}
		}

		return false;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryActionRoles</p>
	 * <p> Description:TODO</p>
	 * @param actionId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryActionRoles(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Role[] queryActionRoles(UUID actionId){
		if (this.isProFlow) {
			return ProjectInvolveManager.getInstance().queryActionRoles(this.createUser, this, actionId);
		}else {
			Set<Role> roleSet = new HashSet<Role>();

			for(ActionRole actionRole : this.actionRoleSet){
				if(actionRole.actionId.equals(actionId)){
					Role role = this.roleMap.get(actionRole.roleId);
					if(role != null){
						roleSet.add(role);
					}
				}
			}

			return roleSet.toArray(new Role[roleSet.size()]);
		}
	}

	@Override
	public Set<Action> queryActionsByStartStatId(UUID statId){
		Set<Action> allActions = new HashSet<Action>();
		for (Action action : this.getActions()) {
			if (action.getBeginStatId() != null && action.getBeginStatId().equals(statId)) {
				allActions.add(action);
			}
		}
		return allActions;
	}
	
	@Override
	public Set<Action> queryActionsByEndStatId(UUID statId){
		Set<Action> allActions = new HashSet<Action>();
		for (Action action : this.getActions()) {
			if (action.getEndStatId() != null && action.getEndStatId().equals(statId)) {
				allActions.add(action);
			}
		}
		return allActions;
	}
	
	@Override
	public String queryNextActionRoleIdsByActionId(UUID actionId){
		Action action = getAction(actionId);
		if (action == null) {
			return "";
		}
		
		Set<String> allRoleSet = new HashSet<String>();
		
		Set<Action> allActions = queryActionsByStartStatId(action.getEndStatId());
		for (Action action2 : allActions) {
			for(ActionRole actionRole : this.actionRoleSet){
				if(actionRole.actionId.equals(action2.getId())){
					allRoleSet.add(actionRole.getRoleId().getValue());
				}
			}
		}
		return ArrayUtil.strArray2String(allRoleSet.toArray(new String[0]));
	}
	
	
	@Override
	public String queryNextActionRoleIdsByStatId(UUID statId){
		Set<String> allRoleSet = new HashSet<String>();
		Set<Action> allActions = queryActionsByStartStatId(statId);
		for (Action action : allActions) {
			for(ActionRole actionRole : this.actionRoleSet){
				if(actionRole.actionId.equals(action.getId())){
					allRoleSet.add(actionRole.getRoleId().getValue());
				}
			}
		}
		return ArrayUtil.strArray2String(allRoleSet.toArray(new String[0]));
	}
	
	@Override
	public String queryActionRoleIds(UUID actionId){
		StringBuffer roleIds = new StringBuffer();
		
		for(ActionRole actionRole : this.actionRoleSet){
			if(actionRole.actionId.equals(actionId)){
				roleIds.append(roleIds.length() > 0 ? "," : "").append(actionRole.getRoleId().getValue());
			}
		}
		return roleIds.toString();
	}
	
	
	/**
	 * (non-Javadoc)
	 * <p> Title:queryAllQuitUserInfo</p>
	 * <p> Description:TODO</p>
	 * @param roleId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryAllQuitUserInfo(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	@Override
	public List<UserInfo> queryAllQuitUserInfo(UUID roleId) {
		Set<String> allRoleUser = queryAllUserByRole(roleId);
		DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
		return das.queryAllUserInfo(allRoleUser.toArray(new String[0]),true);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryAllUser</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryAllUser()
	 */
	public Set<String> queryAllUser(){
		Set<String> allUser = new HashSet<String>();
		for (Right right : this.rightSet) {
			allUser.add(right.getUsername());
		}
		return allUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryAllUserByRole</p>
	 * <p> Description:TODO</p>
	 * @param roleId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryAllUserByRole(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Set<String> queryAllUserByRole(UUID roleId){
		Set<String> allUser = new HashSet<String>();
		for (Right right : this.rightSet) {
			if (right.roleId.equals(roleId)) {
				allUser.add(right.getUsername());
			}
		}
		return allUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryAllUserInfo</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryAllUserInfo()
	 */
	@Override
	public List<UserInfo> queryAllUserInfo() {
		Set<String> allRoleUser = queryAllUser();
		DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
		return das.queryAllUserInfo(allRoleUser.toArray(new String[0]),false);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryAllUserInfo</p>
	 * <p> Description:TODO</p>
	 * @param roleId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryAllUserInfo(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	@Override
	public List<UserInfo> queryAllUserInfo(UUID roleId) {
		Set<String> allRoleUser = queryAllUserByRole(roleId);
		DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
		return das.queryAllUserInfo(allRoleUser.toArray(new String[0]),false);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryBeginActions</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryBeginActions()
	 */
	public Action[] queryBeginActions(){
		Set<Action> actionSet = new LinkedHashSet<Action>();

		for(Action action : this.actionMap.values()){
			if(action.getBeginStatId() == null){
				actionSet.add(action);
			}
		}

		return actionSet.toArray(new Action[actionSet.size()]);
	}

	
	public boolean isEndAction(UUID actionId){
		for (Action action : this.getEndActions()) {
			if (action != null && action.getId().equals(actionId)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:queryEditActionRoles</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryEditActionRoles()
	 */
	public Role[] queryEditActionRoles()
	{
		if (this.isProFlow) {
			return ProjectInvolveManager.getInstance().getAllRole(this.createUser).toArray(new Role[0]);
		}else {
			if(this.actionRoleSet == null)
				return new Role[0];

			Set<Role> roleSet = new LinkedHashSet<Role>();

			for(ActionRole actionRole : this.actionRoleSet)
			{
				if(!actionRole.actionId.equals(Action.editUUID))
					continue;

				Role role = this.roleMap.get(actionRole.roleId);
				if(role != null)
					roleSet.add(role);
			}

			return roleSet.toArray(new Role[0]);
		}
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryEveryoneRoleActions</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryEveryoneRoleActions()
	 */
	public Action[] queryEveryoneRoleActions()
	{
		if(this.actionRoleSet == null)
			return new Action[0];

		Set<Action> actionSet = new LinkedHashSet<Action>();

		for(ActionRole actionRole : this.actionRoleSet)
		{
			if(!actionRole.roleId.equals(Role.everyoneUUID))
				continue;

			Action action = this.actionMap.get(actionRole.actionId);
			if(action != null)
				actionSet.add(action);
		}

		return actionSet.toArray(new Action[0]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryNodeRoles</p>
	 * <p> Description:TODO</p>
	 * @param templateId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryNodeRoles(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Role[] queryNodeRoles(UUID templateId)
	{
		Map<UUID, Role> roleMap = new LinkedHashMap<UUID, Role>();
		for(Right right: this.rightSet)
		{
			if(right.templateId.equals(templateId))
			{
				Role role = this.roleMap.get(right.roleId);
				if(role != null)
					roleMap.put(role.getId(), role);
			}
		}

		return roleMap.values().toArray(new Role[0]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryNodeRoleUsers</p>
	 * <p> Description:TODO</p>
	 * @param templateId
	 * @param roleId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryNodeRoleUsers(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public String[] queryNodeRoleUsers(UUID templateId, UUID roleId)
	{
		HashSet<String> userSet = new HashSet<String>();
		for (Right right : this.rightSet)
		{
			if(right.templateId.equals(templateId) && right.roleId.equals(roleId))
				userSet.add(right.username);
		}

		return userSet.toArray(new String[0]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryNodeStatAssignUsers</p>
	 * <p> Description:TODO</p>
	 * @param templateId
	 * @param statId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryNodeStatAssignUsers(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public String[] queryNodeStatAssignUsers(UUID templateId, UUID statId){
		Set<UUID> roleIdSet = new HashSet<UUID>();

		Action[] actionArray = this.queryStatActions(statId);
		for(Action action : actionArray){
			Role[] roleArray = this.queryActionRoles(action.getId());
			for(Role role : roleArray){
				roleIdSet.add(role.getId());
			}
		}

		Set<String> userSet = new HashSet<String>();

		for(Right right : this.rightSet){
			if(right.templateId.equals(templateId) && roleIdSet.contains(right.roleId)){
				userSet.add(right.username);
			}
		}

		String[] allUser = userSet.toArray(new String[userSet.size()]);
		
		Arrays.sort(allUser);
		
		return allUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryNodeUserRight</p>
	 * @param templateId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryNodeUserRight(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Right[] queryNodeUserRight(UUID templateId){
		HashSet<Right> userSet = new HashSet<Right>();
		for (Right right : this.rightSet)
		{
			if(right.templateId.equals(templateId))
				userSet.add(right);
		}
		
		Right[] userRights = userSet.toArray(new Right[userSet.size()]);
		Arrays.sort(userRights);
		return userRights;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryNodeUsers</p>
	 * <p> Description:TODO</p>
	 * @param templateId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryNodeUsers(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public String[] queryNodeUsers(UUID templateId)
	{
		HashSet<String> userSet = new HashSet<String>();
		for (Right right : this.rightSet)
		{
			if(right.templateId.equals(templateId))
				userSet.add(right.username);
		}
		
		String[] userArray = userSet.toArray(new String[userSet.size()]);
		Arrays.sort(userArray);
		return userArray;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryReadActionRoles</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryReadActionRoles()
	 */
	public Role[] queryReadActionRoles()
	{
		if(this.actionRoleSet == null)
			return new Role[0];

		Set<Role> roleSet = new LinkedHashSet<Role>();

		for(ActionRole actionRole : this.actionRoleSet)
		{
			if(!actionRole.actionId.equals(Action.readUUID))
				continue;

			Role role = this.roleMap.get(actionRole.roleId);
			if(role != null)
				roleSet.add(role);
		}

		return roleSet.toArray(new Role[0]);
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:queryRightsByRole</p>
	 * <p> Description:TODO</p>
	 * @param roleId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryRightsByRole(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	@Override
	public Set<Right> queryRightsByRole(UUID roleId) {
		Set<Right> allRightSet = new HashSet<Right>();
		for (Right right : this.rightSet) {
			if (right.roleId.equals(roleId)) {
				allRightSet.add(right);
			}
		}
		return allRightSet;
	}

	@Override
	public Set<Right> queryRightsByRole(UUID roleId, UUID templateId) {
		
		Set<Right> allRightSet = new HashSet<Right>();
		for (Right right : this.rightSet) {
			if (right.roleId.equals(roleId) && right.templateId.equals(templateId)) {
				allRightSet.add(right);
			}
		}
		return allRightSet;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryRoleActions</p>
	 * <p> Description:TODO</p>
	 * @param roleId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryRoleActions(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Action[] queryRoleActions(UUID roleId){
		Set<Action> actionSet = new HashSet<Action>();

		for(ActionRole actionRole : this.actionRoleSet){
			if(actionRole.roleId.equals(roleId)){
				Action action = this.actionMap.get(actionRole.actionId);
				if(action != null){
					actionSet.add(action);
				}
			}
		}

		return actionSet.toArray(new Action[actionSet.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryStatActions</p>
	 * <p> Description:TODO</p>
	 * @param statId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryStatActions(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Action[] queryStatActions(UUID statId){
		Set<Action> actionSet = new HashSet<Action>();

		for(Action action : this.actionMap.values()){
			if(action.getBeginStatId() != null && action.getBeginStatId().equals(statId)){
				actionSet.add(action);
			}
		}
		return actionSet.toArray(new Action[actionSet.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryUserNodeBeginActions</p>
	 * <p> Description:TODO</p>
	 * @param username
	 * @param templateId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryUserNodeBeginActions(java.lang.String, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Action[] queryUserNodeBeginActions(String username, UUID templateId)
	{
		if(username == null || templateId == null)
			return new Action[0];

		//TODO 根据角色来处理
		if (isProFlow) {
			return this.getStartActions().toArray(new Action[0]);
		}
		
		Role[] roleArray = this.queryUserNodeRoles(username, templateId);
		if(roleArray == null)
			return new Action[0];

		Map<UUID, Action> actionMap = new LinkedHashMap<UUID, Action>();

		for(Role role : roleArray)
		{
			Action[] actionArray = this.queryRoleActions(role.getId());
			if(actionArray == null)
				continue;

			for(Action action : actionArray)
			{
				if(action.getBeginStatId() == null)
					actionMap.put(action.getId(), action);
			}
		}

		Action[] beginActionArray = queryBeginActions();
		for(int i = 0; beginActionArray != null && i < beginActionArray.length; i++)
		{
			if(isActionEveryoneRole(beginActionArray[i].getId()))   //取消everyone控制
				actionMap.put(beginActionArray[i].getId(), beginActionArray[i]);
		}

		return actionMap.values().toArray(new Action[0]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryUserNodeRoles</p>
	 * <p> Description:TODO</p>
	 * @param user
	 * @param templateId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryUserNodeRoles(java.lang.String, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Role[] queryUserNodeRoles(String user, UUID templateId)
	{
		if (this.isProFlow && ConfigManager.getProjectInvolved()) {
			return ProjectInvolveManager.getInstance().getAllRole(user).toArray(new Role[0]);
		}else {
			if (user.indexOf(",") != -1) {
				user = user.split(",")[0];
			}
			Set<Role> roleSet = new LinkedHashSet<Role>();

			for(Right right: this.rightSet)
			{
				if(right.username.equals(user) && right.templateId.equals(templateId))
				{
					Role role = this.roleMap.get(right.roleId);
					if(role != null)
						roleSet.add(role);
				}
			}

			return roleSet.toArray(new Role[0]);
		}
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:queryUserNodeStatActions</p>
	 * <p> Description:TODO</p>
	 * @param username
	 * @param templateId
	 * @param statId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#queryUserNodeStatActions(java.lang.String, com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Action[] queryUserNodeStatActions(String username, UUID templateId, UUID statId){
		if (username.indexOf(",") != -1) {
			username = username.split(",")[0];
		}
		Set<Action> actionSet = new HashSet<Action>();

		Role[] roleArray = this.queryUserNodeRoles(username, templateId);
		for(Role role : roleArray){
			Action[] actionArray = this.queryRoleActions(role.getId());
			for(Action action : actionArray){
				if(action.getBeginStatId() != null && action.getBeginStatId().equals(statId)){
					actionSet.add(action);
				}
			}
		}

		Action[] statActionArray = this.queryStatActions(statId);
		for(Action action : statActionArray){
			if(this.isActionEveryoneRole(action.getId())){
				actionSet.add(action);
			}
		}

		return actionSet.toArray(new Action[actionSet.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:removeAction</p>
	 * <p> Description:TODO</p>
	 * @param actionId
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#removeAction(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void removeAction(UUID actionId){
		this.actionMap.remove(actionId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:removeActionRole</p>
	 * <p> Description:TODO</p>
	 * @param actionId
	 * @param roleId
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#removeActionRole(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void removeActionRole(UUID actionId, UUID roleId){
		this.actionRoleSet.remove(new ActionRole(actionId, roleId));
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:removeRight</p>
	 * <p> Description:TODO</p>
	 * @param username
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#removeRight(java.lang.String)
	 */
	public void removeRight(String username) {
		Iterator it=this.rightSet.iterator();
		while(it.hasNext())
		{
			Right rt = (Right)it.next();
			if(rt.username.equals(username)){
				it.remove();
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:removeRight</p>
	 * <p> Description:TODO</p>
	 * @param username
	 * @param templateId
	 * @param roleId
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#removeRight(java.lang.String, com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void removeRight(String username, UUID templateId, UUID roleId){
		String nickname = CynthiaUtil.getUserAlias(username);
		this.rightSet.remove(new Right(username, templateId, roleId,nickname));
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:removeRole</p>
	 * <p> Description:TODO</p>
	 * @param roleId
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#removeRole(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void removeRole(UUID roleId){
		this.roleMap.remove(roleId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:removeStat</p>
	 * <p> Description:TODO</p>
	 * @param statId
	 * @see com.sogou.qadev.service.cynthia.bean.Flow#removeStat(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void removeStat(UUID statId){
		this.statMap.remove(statId);
	}

	@Override
	public void setActionMap(Map<UUID, Action> actionMap) {
		this.actionMap = actionMap;
	}

	@Override
	public void setActionRoleSet(Set<ActionRole> actionRoleSet) {
		this.actionRoleSet = actionRoleSet;
	}

	@Override
	public void setCreateUser(String userName) {
		this.createUser = userName;
	}

	@Override
	public void setName(String name){
		this.name = name;
	}

	public void setProFlow(boolean isProFlow) {
		this.isProFlow = isProFlow;
	}
	
	@Override
	public void setRightSet(Set<Right> rightSet) {
		this.rightSet = rightSet;
	}
	
	@Override
	public void setRoleMap(Map<UUID, Role> roleMap) {
		this.roleMap = roleMap;
	}
	
	@Override
	public void setStatMap(Map<UUID, Stat> statMap) {
		this.statMap = statMap;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:toXMLDocument</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @throws Exception
	 * @see com.sogou.qadev.service.cynthia.bean.BaseType#toXMLDocument()
	 */
	public Document toXMLDocument() throws Exception{
		return XMLUtil.string2Document(toXMLString(), "UTF-8");
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:toXMLString</p>
	 * <p> Description:TODO</p>
	 * @return
	 * @throws Exception
	 * @see com.sogou.qadev.service.cynthia.bean.BaseType#toXMLString()
	 */
	public String toXMLString() throws Exception{
		StringBuffer xmlb = new StringBuffer();
		xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xmlb.append("<flow>");
		xmlb.append("<id>").append(this.id).append("</id>");
		xmlb.append("<name>").append(XMLUtil.toSafeXMLString(this.name)).append("</name>");
		xmlb.append("<isProFlow>").append(XMLUtil.toSafeXMLString(String.valueOf(this.isProFlow))).append("</isProFlow>");

		if(this.statMap.size() == 0){
			xmlb.append("<stats/>");
		}
		else{
			xmlb.append("<stats>");

			for(Stat stat : this.statMap.values()){
				xmlb.append("<stat>");
				xmlb.append("<id>").append(stat.getId()).append("</id>");
				xmlb.append("<name>").append(XMLUtil.toSafeXMLString(stat.getName())).append("</name>");
				xmlb.append("</stat>");
			}

			xmlb.append("</stats>");
		}

		if(this.actionMap.size() == 0){
			xmlb.append("<actions/>");
		}else{
			xmlb.append("<actions>");

			for(Action action : this.actionMap.values()){
				xmlb.append("<action>");
				xmlb.append("<id>").append(action.getId()).append("</id>");
				xmlb.append("<name>").append(XMLUtil.toSafeXMLString(action.getName())).append("</name>");

				if(action.getBeginStatId() == null){
					xmlb.append("<startStatId/>");
				}else{
					xmlb.append("<startStatId>").append(action.getBeginStatId()).append("</startStatId>");
				}

				if(action.getEndStatId() == null){
					xmlb.append("<endStatId/>");
				}else{
					xmlb.append("<endStatId>").append(action.getEndStatId()).append("</endStatId>");
				}

				xmlb.append("<assignToMore>").append(action.getAssignToMore()).append("</assignToMore>");
				xmlb.append("</action>");
			}

			xmlb.append("</actions>");
		}

		if(this.roleMap.size() == 0){
			xmlb.append("<roles/>");
		}else{
			xmlb.append("<roles>");

			for(Role role : this.roleMap.values()){
				xmlb.append("<role>");
				xmlb.append("<id>").append(role.getId()).append("</id>");
				xmlb.append("<name>").append(XMLUtil.toSafeXMLString(role.getName())).append("</name>");
				xmlb.append("</role>");
			}

			xmlb.append("</roles>");
		}

		if(this.actionRoleSet.size() == 0){
			xmlb.append("<actionRoles/>");
		}else{
			xmlb.append("<actionRoles>");

			for(ActionRole actionRole : this.actionRoleSet){
				xmlb.append("<actionRole>");
				xmlb.append("<actionId>").append(actionRole.actionId).append("</actionId>");
				xmlb.append("<roleId>").append(actionRole.roleId).append("</roleId>");
				xmlb.append("</actionRole>");
			}

			xmlb.append("</actionRoles>");
		}

		if(this.rightSet.size() == 0){
			xmlb.append("<rights/>");
		}else{
			xmlb.append("<rights>");

			for(Right right : this.rightSet){
				xmlb.append("<right>");
				xmlb.append("<username>").append(XMLUtil.toSafeXMLString(right.username)).append("</username>");
				xmlb.append("<templateId>").append(right.templateId).append("</templateId>");
				xmlb.append("<roleId>").append(right.roleId).append("</roleId>");
				xmlb.append("</right>");
			}

			xmlb.append("</rights>");
		}

		xmlb.append("</flow>");

		return xmlb.toString();
	}

}
