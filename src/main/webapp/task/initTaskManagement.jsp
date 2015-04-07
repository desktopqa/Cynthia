<%@page import="com.sogou.qadev.service.cynthia.service.ProjectInvolveManager"%>
<%@page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="java.sql.Types"%>
<%@page import="com.sogou.qadev.cache.impl.FieldNameCache"%>
<%@page import="com.sogou.qadev.service.cynthia.service.TableRuleManager"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DbPoolConnection"%>
<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.AttachmentAccessSessionMySQL"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL"%>
<%@page import="com.sogou.qadev.cache.impl.TemplateCache"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataManager"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Data"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Pair"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Stat"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Attachment"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field.Type"%>
<%@ page import="org.w3c.dom.Node"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="java.util.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option.Forbidden"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%
	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
	response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
	response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility
	
	out.clear();

	DataAccessFactory daf = DataAccessFactory.getInstance();
	Long keyId = (Long)session.getAttribute("kid");
	Key key = (Key)session.getAttribute("key");

	if(keyId == null || keyId <= 0 || key == null){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	String operation = request.getParameter("operation");
	if(operation == null || !operation.equals("create") && !operation.equals("read")){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	DataAccessSession das = daf.createDataAccessSession(key.getUsername(), keyId);
	
	String xml = null;
	
	if(operation.equals("create")){
		UUID templateTypeId = daf.createUUID(request.getParameter("templateTypeId"));
		
		Template[] templateArray = DataManager.getInstance().queryUserTemplates(key.getUsername());
		
		StringBuffer xmlb = new StringBuffer(64);
		xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		
		xmlb.append("<root>");
		
		xmlb.append("<isError>false</isError>");
		if(templateArray.length == 0){
	xmlb.append("<templates/>");
		}else{
	xmlb.append("<templates>");
	
	for(Template template : templateArray){
		xmlb.append("<template>");	
		
		xmlb.append("<id>").append(template.getId()).append("</id>");
		xmlb.append("<name>").append(XMLUtil.toSafeXMLString(template.getName())).append("</name>");
		
		xmlb.append("</template>");
	}
		
	xmlb.append("</templates>");
		}
		
		xmlb.append("</root>");
		
		xml = xmlb.toString();
	}else{
		String dataIdStr = request.getParameter("taskId");
		String idStr = request.getParameter("id");
		
		if(dataIdStr == null && idStr == null){
	out.println(ErrorManager.getErrorXml(ErrorType.param_error));
	return;
		}
		
		UUID dataId = daf.createUUID(dataIdStr);
		if(dataId == null)
	dataId = daf.createUUID(idStr);
		Data task = null;
		String templateIdStr = request.getParameter("templateId");
		
		if(templateIdStr != null && templateIdStr.length() > 0){
	UUID templateUUID = daf.createUUID(templateIdStr);
	task = das.queryData(dataId,templateUUID);
		}else{
	task = das.queryData(dataId);
		}
		
		//数据不存在
		if(task == null){
	out.println(ErrorManager.getErrorXml(ErrorType.data_not_found_inDb));
	return;
		}
		
		//没有可读权限
		if(!das.checkUserPrivilege(task, DataAccessAction.read)){
	out.println(ErrorManager.getErrorXml(ErrorType.not_read_right));
	return;
		}
		
		Template template = das.queryTemplate(task.getTemplateId());
		if(template == null){
	out.println(ErrorManager.getErrorXml(ErrorType.template_not_found));
	return;
		}
		
		Flow flow = das.queryFlow(template.getFlowId());
		
		if(flow == null){
	out.println(ErrorManager.getErrorXml(ErrorType.flow_not_found));
	return;
		}
		
		Stat stat = flow.getStat(task.getStatusId());
		if(stat == null){
	out.println(ErrorManager.getErrorXml(ErrorType.stat_not_found));
	return;
		}
		
		xml = CynthiaUtil.checkXML(task.toXMLString(), "UTF-8");
		
		Document doc = XMLUtil.string2Document(xml, "UTF-8");
		
		Node taskNode = XMLUtil.getSingleNode(doc, "task");
		doc.removeChild(taskNode);
		
		Node rootNode = doc.createElement("root");
		doc.appendChild(rootNode);
		
		//isError
		Node isErrorNode = doc.createElement("isError");
		rootNode.appendChild(isErrorNode);
		isErrorNode.setTextContent("false");
		
		//actionArray
		Set<Action> actionSet = new LinkedHashSet<Action>();
		
		Action[] actionArray = flow.queryStatActions(stat.getId());
		
		//isClose
		if(actionArray == null || actionArray.length == 0){
	Action[] userNodeBeginActionArray = flow.queryUserNodeBeginActions(key.getUsername(), template.getId());
	for(int i = 0; userNodeBeginActionArray != null && i < userNodeBeginActionArray.length; i++){
		actionSet.add(userNodeBeginActionArray[i]);
	}
		}else
		{
	//not close
	Action[] userNodeStatActionArray = flow.queryUserNodeStatActions(key.getUsername(), template.getId(), stat.getId());
	for(int i = 0; userNodeStatActionArray != null && i < userNodeStatActionArray.length; i++){
		actionSet.add(userNodeStatActionArray[i]);
	}
		}
		
		//templateId
		Node templateIdNode = doc.createElement("templateId");
		rootNode.appendChild(templateIdNode);
		templateIdNode.setTextContent(template.getId().toString());
		
		//lastActionRoles
		Node lastActionRolesNode = doc.createElement("lastActionRoles");
		rootNode.appendChild(lastActionRolesNode);
		lastActionRolesNode.setTextContent(flow.queryNextActionRoleIdsByStatId(task.getStatusId()));
		
		//templateTypeId
		Node templateTypeIdNode = doc.createElement("templateTypeId");
		rootNode.appendChild(templateTypeIdNode);
		templateTypeIdNode.setTextContent(template.getTemplateTypeId().toString());
		
		//isProTemplate
		Node isProTemplateNode = doc.createElement("isProTemplate");
		rootNode.appendChild(isProTemplateNode);
		isProTemplateNode.setTextContent(String.valueOf(template.getTemplateConfig().isProjectInvolve()));
		
		//productInvolvedId
		Node productInvolvedIdNode = doc.createElement("productInvolvedId");
		rootNode.appendChild(productInvolvedIdNode);
		productInvolvedIdNode.setTextContent(String.valueOf(template.getTemplateConfig().getProductInvolveId()));
		
		//projectInvolvedId
		Node projectInvolvedIdNode = doc.createElement("projectInvolvedId");
		rootNode.appendChild(projectInvolvedIdNode);
		projectInvolvedIdNode.setTextContent(String.valueOf(template.getTemplateConfig().getProjectInvolveId()));
		
		//statusName
		Node statusNameNode = doc.createElement("statusName");
		rootNode.appendChild(statusNameNode);
		statusNameNode.setTextContent(stat.getName());
		
		//roles
		Node rolesNode = doc.createElement("roles");
		rootNode.appendChild(rolesNode);
		
		Node everyoneRoleNode = doc.createElement("role");
		rolesNode.appendChild(everyoneRoleNode);
		
		Node everyoneRoleIdNode = doc.createElement("id");
		everyoneRoleNode.appendChild(everyoneRoleIdNode);
		everyoneRoleIdNode.setTextContent(Role.everyoneUUID.toString());
		
		Node everyoneRoleNameNode = doc.createElement("name");
		everyoneRoleNode.appendChild(everyoneRoleNameNode);
		everyoneRoleNameNode.setTextContent(Role.everyoneName);
		
		Role[] roleArray = flow.queryUserNodeRoles(key.getUsername(), template.getId());
		
		if(roleArray != null){
		Set<UUID> roleIdSet = new HashSet<UUID>();
		for(Role role : roleArray){
	roleIdSet.add(role.getId());
	
	Node roleNode = doc.createElement("role");
	rolesNode.appendChild(roleNode);
	
	Node roleIdNode = doc.createElement("id");
	roleNode.appendChild(roleIdNode);
	roleIdNode.setTextContent(role.getId().toString());
	
	Node roleNameNode = doc.createElement("name");
	roleNode.appendChild(roleNameNode);
	roleNameNode.setTextContent(role.getName());
		}
		}
		Long beforeTime = System.currentTimeMillis();
		Node isEditNode = doc.createElement("isEdit");
		rootNode.appendChild(isEditNode);
		isEditNode.setTextContent(Boolean.toString(flow.isEditActionAllow(key.getUsername(), template.getId(), task.getAssignUsername(), task.getActionUser())));
		
		Node isReadNode = doc.createElement("isRead");
		rootNode.appendChild(isReadNode);
		isReadNode.setTextContent("true");
		System.out.println("after time : " + ( System.currentTimeMillis() - beforeTime));
		//actions
		Node actionsNode = doc.createElement("actions");
		rootNode.appendChild(actionsNode);
		
		for(Action action : actionSet){

	Node actionNode = doc.createElement("action");
	actionsNode.appendChild(actionNode);
	
	Node actionIdNode = doc.createElement("id");
	actionNode.appendChild(actionIdNode);
	actionIdNode.setTextContent(action.getId().toString());
	
	Node actionNameNode = doc.createElement("name");
	actionNode.appendChild(actionNameNode);
	actionNameNode.setTextContent(action.getName());
	
	Node beginStatIdNode = doc.createElement("beginStatId");
	actionNode.appendChild(beginStatIdNode);
	if(action.getBeginStatId() != null){
		beginStatIdNode.setTextContent(action.getBeginStatId().toString());
	}
	
	Node endStatIdNode = doc.createElement("endStatId");
	actionNode.appendChild(endStatIdNode);
	endStatIdNode.setTextContent(action.getEndStatId().toString());
	
	Node actionRolesNode = doc.createElement("nextActionRoles");
	actionNode.appendChild(actionRolesNode);
	actionRolesNode.setTextContent(flow.queryNextActionRoleIdsByActionId(action.getId()));
	
	Node assignToMoreNode = doc.createElement("assignToMore");
	actionNode.appendChild(assignToMoreNode);
	assignToMoreNode.setTextContent(String.valueOf(action.getAssignToMore()));
	
	Node isEndActionNode = doc.createElement("isEndAction");
	actionNode.appendChild(isEndActionNode);
	isEndActionNode.setTextContent(String.valueOf(flow.isEndAction(action.getId())));
	
		}
		
		
		//task
		rootNode.appendChild(taskNode);
		
		//设置创建人的昵称
		Node createUserNode = XMLUtil.getSingleNode(taskNode, "createUser");
		XMLUtil.setAttribute(createUserNode, "alias", CynthiaUtil.getAssignUserAlias(createUserNode.getTextContent()));
		
		//设置指派人的昵称
		Node assignUserNode = XMLUtil.getSingleNode(taskNode, "assignUser");
		XMLUtil.setAttribute(assignUserNode, "alias", CynthiaUtil.getAssignUserAlias(assignUserNode.getTextContent()));
		
		Node numberNode = doc.createElement("number");
		numberNode.setTextContent(task.getId().getValue());
		taskNode.insertBefore(numberNode, XMLUtil.getSingleNode(taskNode, "id"));
		
		//templateName
		Node templateNameNode = doc.createElement("templateName");
		taskNode.appendChild(templateNameNode);
		templateNameNode.setTextContent(template.getName());
		
		Node logsNode = XMLUtil.getSingleNode(taskNode, "logs");
		List<Node> logNodeList = XMLUtil.getNodes(logsNode, "log");
		for(Node logNode : logNodeList){
	Node logCreateUserNode = XMLUtil.getSingleNode(logNode, "createUser");
	
	Node logCreateUserMail = doc.createElement("createUserMail");
	logCreateUserMail.setTextContent(logCreateUserNode.getTextContent());
	logCreateUserNode.setTextContent(CynthiaUtil.getAssignUserAlias(logCreateUserNode.getTextContent()));
	
	Node actionNameNode = doc.createElement("actionName");
	
	Node actionIdNode = XMLUtil.getSingleNode(logNode, "actionId");
	if(actionIdNode.getTextContent().length() > 0){
		UUID actionId = DataAccessFactory.getInstance().createUUID(actionIdNode.getTextContent());
		
		Action action = flow.getAction(actionId);
		if(action != null){
	actionNameNode.setTextContent(action.getName());
		}else{
	actionNameNode.setTextContent(""); //编辑
		}
	}
	
	logNode.insertBefore(actionNameNode, actionIdNode);
	logNode.insertBefore(logCreateUserMail, actionNameNode);
	logNode.removeChild(actionIdNode);
	
	Node baseValuesNode = XMLUtil.getSingleNode(logNode, "baseValues");
	List<Node> baseValueNodeList = XMLUtil.getNodes(baseValuesNode, "baseValue");
	for(Node baseValueNode : baseValueNodeList){
		Node previousNode = XMLUtil.getSingleNode(baseValueNode, "previous");
		Node currentNode = XMLUtil.getSingleNode(baseValueNode, "current");
		
		if(previousNode.getTextContent().equals(currentNode.getTextContent())){
	baseValuesNode.removeChild(baseValueNode);
	continue;
		}
		
		Node baseNode = XMLUtil.getSingleNode(baseValueNode, "base");
		
		if(baseNode.getTextContent().equals("title")){
	baseNode.setTextContent("标题");
		}
		else if(baseNode.getTextContent().equals("description")){
	baseNode.setTextContent("描述");
		}
		else if(baseNode.getTextContent().equals("assignUser")){
	baseNode.setTextContent("指派人");
	
	//设置原指派人的昵称
	if(!previousNode.getTextContent().equals("")){
		previousNode.setTextContent(CynthiaUtil.getAssignUserAlias(previousNode.getTextContent()));
	}
	
	//设置现指派人的昵称
	if(!currentNode.getTextContent().equals("")){
		currentNode.setTextContent(CynthiaUtil.getAssignUserAlias(currentNode.getTextContent()));
	}
		}
		else if(baseNode.getTextContent().equals("statusId")){
	baseNode.setTextContent("状态");	
	
	if(!previousNode.getTextContent().equals("")){
		UUID previousStatId = DataAccessFactory.getInstance().createUUID(previousNode.getTextContent());
		Stat previousStat = flow.getStat(previousStatId);
		if(previousStat != null){
			previousNode.setTextContent(previousStat.getName());
		}
	}
	
	if(!currentNode.getTextContent().equals("")){
		UUID currentStatId = DataAccessFactory.getInstance().createUUID(currentNode.getTextContent());
		Stat currentStat = flow.getStat(currentStatId);
		if(currentStat != null){
			currentNode.setTextContent(currentStat.getName());
		}
	}
		}
	}
	
	
	//加载日志
	Node extValuesNode = XMLUtil.getSingleNode(logNode, "extValues");
	List<Node> extValueNodeList = XMLUtil.getNodes(extValuesNode, "extValue");
	for(Node extValueNode : extValueNodeList){
		Node previousNode = XMLUtil.getSingleNode(extValueNode, "previous");
		Node currentNode = XMLUtil.getSingleNode(extValueNode, "current");
		
		if(previousNode.getTextContent().equals(currentNode.getTextContent())){
	extValuesNode.removeChild(extValueNode);
	continue;
		}
		
		Node extNode = XMLUtil.getSingleNode(extValueNode, "ext");
		
		UUID ext = DataAccessFactory.getInstance().createUUID(extNode.getTextContent());
		Field field = template.getField(ext);
		if(field == null){
	extValuesNode.removeChild(extValueNode);
	continue;
		}
		
		extNode.setTextContent(field.getName());
		
		if(field.getType().equals(Type.t_input)){
	continue;
		}
		
		if(previousNode.getTextContent().length() > 0){
	StringBuffer value = new StringBuffer();
	
	if(field.getType().equals(Type.t_selection)){
		String[] optionIdStrArray = previousNode.getTextContent().split("\\,");
		for(String optionIdStr : optionIdStrArray){
			UUID optionId = DataAccessFactory.getInstance().createUUID(optionIdStr);
			
			Option option = field.getOption(optionId);
			if(option == null){
				continue;
			}
			
			if(value.length() > 0){
				value.append("VALUE_SPLIT");
			}
			
			value.append(option.getName());
		}
	}
	else if(field.getType().equals(Type.t_reference)){
		String[] referIdStrArray = previousNode.getTextContent().split("\\,");
		
		String[] referTitleArray = new DataAccessSessionMySQL().queryFieldByIds(referIdStrArray, "title", null);
				
		for(String referTitleStr : referTitleArray){
			if(referTitleStr == null){
				continue;
			}
			if(value.length() > 0){
				value.append("VALUE_SPLIT");
			}
			value.append(referTitleStr);
		}
	}
	else if(field.getType().equals(Type.t_attachment)){
		String[] attachIdStrArray = previousNode.getTextContent().split("\\,");
	    Set<UUID> allAttachSet = new HashSet<UUID>();
	    
		for(int i = 0; i < attachIdStrArray.length; i++){
			UUID uuid =  DataAccessFactory.getInstance().createUUID(attachIdStrArray[i]);
			if(uuid != null)
				allAttachSet.add(uuid);
		}
		
		Map<String , String> attachIdNameMap = new AttachmentAccessSessionMySQL().queryAttachmentIdNames(allAttachSet.toArray(new UUID[0]));
		
		for(String id : attachIdNameMap.keySet()){
			if(value.length() > 0){
				value.append("VALUE_SPLIT");
			}
			
			value.append(attachIdNameMap.get(id));
		}
	}
	previousNode.setTextContent(value.toString());
		}
		
		if(currentNode.getTextContent().length() > 0){
	StringBuffer value = new StringBuffer();
	
	if(field.getType().equals(Type.t_selection)){
		String[] optionIdStrArray = currentNode.getTextContent().split("\\,");
		for(String optionIdStr : optionIdStrArray){
			UUID optionId = DataAccessFactory.getInstance().createUUID(optionIdStr);
			
			Option option = field.getOption(optionId);
			if(option == null){
				continue;
			}
			
			if(value.length() > 0){
				value.append("VALUE_SPLIT");
			}
			
			value.append(option.getName());
		}
	}
	else if(field.getType().equals(Type.t_reference)){
		String[] referIdStrArray = currentNode.getTextContent().split("\\,");
		
		String[] referTitleArray = new DataAccessSessionMySQL().queryFieldByIds(referIdStrArray, "title", null);
		
		for(String referTitleStr : referTitleArray){
			if(referTitleStr == null){
				continue;
			}
			if(value.length() > 0){
				value.append("VALUE_SPLIT");
			}
			value.append(referTitleStr);
		}
		
	}
	else if(field.getType().equals(Type.t_attachment)){
		String[] attachIdStrArray = currentNode.getTextContent().split("\\,");
		 Set<UUID> allAttachSet = new HashSet<UUID>();
		    
		for(int i = 0; i < attachIdStrArray.length; i++){
			UUID uuid =  DataAccessFactory.getInstance().createUUID(attachIdStrArray[i]);
			if(uuid != null)
				allAttachSet.add(uuid);
		}
		
		Map<String , String> attachIdNameMap = new AttachmentAccessSessionMySQL().queryAttachmentIdNames(allAttachSet.toArray(new UUID[0]));

		for(String id : attachIdNameMap.keySet()){
			if(value.length() > 0){
				value.append("VALUE_SPLIT");
			}
			
			value.append(attachIdNameMap.get(id));
		}
		
	}
	currentNode.setTextContent(value.toString());
		}
	}
	
	baseValueNodeList = XMLUtil.getNodes(baseValuesNode, "baseValue");
	extValueNodeList = XMLUtil.getNodes(extValuesNode, "extValue");
		}
		
		//加载数据
		Map<String, Set<String>> fieldDataMap = new LinkedHashMap<String, Set<String>>();
		
		Node taskFieldsNode = XMLUtil.getSingleNode(taskNode, "fields");
		
		List<Node> taskFieldNodeList = XMLUtil.getNodes(taskFieldsNode, "field");
		for(Node taskFieldNode : taskFieldNodeList){
	String fieldIdStr = XMLUtil.getSingleNodeTextContent(taskFieldNode, "id");
	fieldDataMap.put(fieldIdStr, new LinkedHashSet<String>());
	
	List<Node> fieldDataNodeList = XMLUtil.getNodes(taskFieldNode, "data");
	for(Node fieldDataNode : fieldDataNodeList){
		fieldDataMap.get(fieldIdStr).add(fieldDataNode.getTextContent());
	}
		}

		taskNode.removeChild(taskFieldsNode);
		
		xml = XMLUtil.document2String(doc, "UTF-8");
		
		//fields & validColumns
		String templateXml = template.toXMLString();
		
		xml = xml.substring(0, xml.indexOf("</root>"));
		xml += templateXml.substring(templateXml.indexOf("<layout>"), templateXml.indexOf("</layout>"));
		xml += "</layout></root>";
		
		
		doc = XMLUtil.string2Document(xml, "UTF-8");
		
		Map<String, String> statusMap = new HashMap<String, String>();
		Map<String, String> priorityMap = new HashMap<String, String>();
		
		Map<String, Map<String, Map<String, String>>> referenceMap = new TreeMap<String, Map<String, Map<String, String>>>();
		Map<String, String> attachmentMap = new TreeMap<String, String>();
		Map<UUID,Template> templateMap = new HashMap<UUID, Template>();
		Map<UUID,Flow> flowMap = new HashMap<UUID, Flow>();
		
		
		List<Node> fieldRowNodeList = XMLUtil.getNodes(doc, "root/layout/rows/row");
		for(Node fieldRowNode : fieldRowNodeList)
		{
	List<Node> fieldColumnNodeList = XMLUtil.getNodes(fieldRowNode, "column");
	for(Node fieldColumnNode : fieldColumnNodeList)
	{
		List<Node> fieldNodeList = XMLUtil.getNodes(fieldColumnNode, "field");
		
		for(Node fieldNode : fieldNodeList){
	Node typeNode = XMLUtil.getSingleNode(fieldNode, "type");
	typeNode.setTextContent(typeNode.getTextContent().split("\\_")[1]);
	
	Node dataTypeNode = XMLUtil.getSingleNode(fieldNode, "dataType");
	if(dataTypeNode.getTextContent().length() > 0)
		dataTypeNode.setTextContent(dataTypeNode.getTextContent().split("\\_")[1]);
	
	List<Node> optionNodeList = XMLUtil.getNodes(fieldNode, "options/option");
	Node fieldIdNode = XMLUtil.getSingleNode(fieldNode,"id");
	String fieldId   = fieldIdNode.getTextContent();
	
	for(Node optionNode : optionNodeList){
		Node forbiddenNode = XMLUtil.getSingleNode(optionNode, "forbidden");
		forbiddenNode.setTextContent(forbiddenNode.getTextContent().split("\\_")[1]);
	}
	
	///////////////////////////////////设置对应项目////////////////////////////////////////////////////////////////////
	if(template.getTemplateConfig().isProjectInvolve() && XMLUtil.getSingleNodeTextContent(fieldNode, "id").equals(template.getTemplateConfig().getProjectInvolveId())){
		XMLUtil.removeAll(XMLUtil.getSingleNode(fieldNode, "options"));
		Field productField = template.getField(DataAccessFactory.getInstance().createUUID(template.getTemplateConfig().getProductInvolveId()));
		if(productField != null){
			UUID optionId = task.getSingleSelection(productField.getId());
			if(optionId != null){
				Map<String,String> allProjects = ProjectInvolveManager.getInstance().getProjectMap(key.getUsername(), optionId.getValue());
				
				Node optionsNode = XMLUtil.getSingleNode(fieldNode,"options");
				int i = 0;
				for(String productId : allProjects.keySet())
				{
					Node optionNode = doc.createElement("option");
					Node optionIdNode = doc.createElement("id");
					optionIdNode.setTextContent(productId);
					optionNode.appendChild(optionIdNode);
		
					Node optionNameNode = doc.createElement("name");
					optionNameNode.setTextContent(allProjects.get(productId));
					optionNode.appendChild(optionNameNode);
		
					Node controlOptionIdNode = doc.createElement("controlOptionId");
					optionNode.appendChild(controlOptionIdNode);
		
					Node forbiddenNode = doc.createElement("forbidden");
					forbiddenNode.setTextContent("permit");
					optionNode.appendChild(forbiddenNode);
		
					Node indexOrderNode = doc.createElement("indexOrder");
					indexOrderNode.setTextContent(Integer.toString(++i));
					optionNode.appendChild(indexOrderNode);
					optionsNode.appendChild(optionNode);
				}
			}
		}
	}
	///////////////////////////////////设置对应项目结束////////////////////////////////////////////////////////////////
		
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	StringBuffer controlHiddenFields = new StringBuffer();
	Node controlHiddenFieldsIdsNode = XMLUtil.getSingleNode(fieldNode, "controlHiddenFields");
	List<Node> controlHiddenFieldsIdList =  XMLUtil.getNodes(controlHiddenFieldsIdsNode, "controlHiddenField");
	for(Node controlHiddenField : controlHiddenFieldsIdList){
		if(controlHiddenFields.length() > 0){
			controlHiddenFields.append(",");
		}
		
		controlHiddenFields.append(controlHiddenField.getTextContent());
	}
	XMLUtil.removeAll(controlHiddenFieldsIdsNode);
	controlHiddenFieldsIdsNode.setTextContent(controlHiddenFields.toString());
	
	StringBuffer controlHiddenStates = new StringBuffer();
	Node controlHiddenStatesIdsNode = XMLUtil.getSingleNode(fieldNode, "controlHiddenStates");
	List<Node> controlHiddenStatesIdList =  XMLUtil.getNodes(controlHiddenStatesIdsNode, "controlHiddenState");
	for(Node controlHiddenState : controlHiddenStatesIdList){
		if(controlHiddenStates.length() > 0){
			controlHiddenStates.append(",");
		}
		
		controlHiddenStates.append(controlHiddenState.getTextContent());
	}
	XMLUtil.removeAll(controlHiddenStatesIdsNode);
	controlHiddenStatesIdsNode.setTextContent(controlHiddenStates.toString());
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//controlActionIds
	StringBuffer controlActionIds = new StringBuffer();
	Node controlActionIdsNode = XMLUtil.getSingleNode(fieldNode, "controlActionIds");
	List<Node> controlActionIdNodeList = XMLUtil.getNodes(controlActionIdsNode, "controlActionId");
	for(Node controlActionIdNode : controlActionIdNodeList){
		if(controlActionIds.length() > 0){
			controlActionIds.append(",");
		}
		
		controlActionIds.append(controlActionIdNode.getTextContent());
	}
	
	XMLUtil.removeAll(controlActionIdsNode);
	controlActionIdsNode.setTextContent(controlActionIds.toString());
	
	//controlRoleIds
	StringBuffer controlRoleIds = new StringBuffer();
	Node controlRoleIdsNode = XMLUtil.getSingleNode(fieldNode, "controlRoleIds");
	List<Node> controlRoleIdNodeList = XMLUtil.getNodes(controlRoleIdsNode, "controlRoleId");
	for(Node controlRoleIdNode : controlRoleIdNodeList){
		if(controlRoleIds.length() > 0){
			controlRoleIds.append(",");
		}
		
		controlRoleIds.append(controlRoleIdNode.getTextContent());
	}
	
	XMLUtil.removeAll(controlRoleIdsNode);
	controlRoleIdsNode.setTextContent(controlRoleIds.toString());
	
	Node datasNode = doc.createElement("datas");
	fieldNode.appendChild(datasNode);
	
	String fieldIdStr = XMLUtil.getSingleNodeTextContent(fieldNode, "id");
	if(fieldDataMap.containsKey(fieldIdStr)){
		Set<String> attachIdSet = new HashSet<String>();
		Map<UUID,Set<String>> referTemplateDataMap = new HashMap<UUID,Set<String>>();
		for(String data : fieldDataMap.get(fieldIdStr)){	
			if(typeNode.getTextContent().equals("reference")){
				UUID referenceId = DataAccessFactory.getInstance().createUUID(data);
				Data reference = das.queryData(referenceId);
				if(reference == null)
					continue;
				
				if(templateMap.get(reference.getTemplateId()) == null){
					Template temp = das.queryTemplate(reference.getTemplateId());
					if(temp != null)
						templateMap.put(temp.getId(), temp);
					Flow flow1 = das.queryFlow(temp.getFlowId());
					if(flow1 != null)
						flowMap.put(flow1.getId(), flow1);
				}
				
				if(referTemplateDataMap.get(reference.getTemplateId()) == null)
					referTemplateDataMap.put(reference.getTemplateId(), new HashSet<String>());
				
				referTemplateDataMap.get(reference.getTemplateId()).add(data);
			}
			else if(typeNode.getTextContent().equals("attachment")){
				attachIdSet.add(data);
			}else{
				Node dataNode = doc.createElement("data");
				datasNode.appendChild(dataNode);
				dataNode.setTextContent(data);
			}
		}
		
		if(referTemplateDataMap.keySet().size() > 0){
			Map<UUID,Field> templatePiorityField = new HashMap<UUID,Field>();
			String piorityFieldColName = "";
			List<Map<String,String>> allReferDataList = new ArrayList<Map<String,String>>();
			for(UUID templateIdTmp : referTemplateDataMap.keySet()){
				StringBuffer referSql = new StringBuffer();
				referSql.append("select id,templateId, statusId,title ");
				Field piorityField = templateMap.get(templateIdTmp).getField("修改优先级");
				if(piorityField != null){
					templatePiorityField.put(templateIdTmp, piorityField);
					piorityFieldColName = FieldNameCache.getInstance().getFieldName(piorityField.getId(), templateIdTmp);
					if(piorityFieldColName != null)
						referSql.append(",").append(piorityFieldColName);
				}
				referSql.append(" from ").append(TableRuleManager.getInstance().getDataTableName(templateIdTmp));
				
				referSql.append(" where id in ( ");
				for (String referDataId : referTemplateDataMap.get(templateIdTmp)) {
					referSql.append(referDataId).append(",");
				}
				referSql.deleteCharAt(referSql.length() -1 );
				referSql.append(")");
				referSql.append(" order by statusId ");
				if(piorityFieldColName != "")
					referSql.append(",").append(piorityFieldColName);
				
				allReferDataList.addAll(DbPoolConnection.getInstance().getResultSetListBySql(referSql.toString()));
			}
			
			
			for(Map<String,String> dataMap : allReferDataList){
				Node dataNode = doc.createElement("data");
				datasNode.appendChild(dataNode);
				String statusName = "";
				statusName = statusMap.get(dataMap.get("statusId"));
				UUID templateUUID = DataAccessFactory.getInstance().createUUID(dataMap.get("templateId"));
				Flow tmpFlow = flowMap.get(templateMap.get(templateUUID).getFlowId());
				if(statusName == null){
					Stat status = tmpFlow.getStat(DataAccessFactory.getInstance().createUUID(dataMap.get("statusId")));
					if(status != null){
						statusName = status.getName();
						statusMap.put(status.getId().toString(), statusName);
					}
				}
				
				String priorityName = "";
				if(piorityFieldColName != ""){
					priorityName = priorityMap.get(dataMap.get(piorityFieldColName));
					if(priorityName == null){
						Option priority = templatePiorityField.get(templateUUID).getOption(DataAccessFactory.getInstance().createUUID(dataMap.get(piorityFieldColName)));
						if(priority != null){
							priorityName = priority.getName();
							priorityMap.put(dataMap.get(piorityFieldColName), priorityName);
						}
					}
				}
				
				priorityName = priorityName == null ? "" : priorityName;
				statusName = statusName == null ? "" : statusName;
				dataNode.setTextContent(dataMap.get("id") + "&|;" + dataMap.get("title") + "&|;" + (statusName.equals("") ? null : statusName) + "&|;" + (priorityName.equals("") ? null : priorityName));
			}
			
		}
		
		Map<String , String> attachIdNameMap = new AttachmentAccessSessionMySQL().getAttachIdAndNameByIds(attachIdSet);
		
		for(String attachId : attachIdNameMap.keySet()){
			Node dataNode = doc.createElement("data");
			datasNode.appendChild(dataNode);
			dataNode.setTextContent(attachId + "&|;" + attachIdNameMap.get(attachId));
		}
	}
		}
		try
		{
	xml = XMLUtil.document2String(doc, "UTF-8");
		}catch(Exception e)
		{
	e.printStackTrace();
		}
	}
		}
		
	}
	if(xml == null){
		out.println(ErrorManager.getErrorXml("没有任何数据!"));
	}else{
		out.println(xml);
	}
%>