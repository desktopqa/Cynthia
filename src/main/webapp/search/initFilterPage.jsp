<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="org.w3c.dom.*" %>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.TemplateType"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action"%>

<%@ include file="initMain.function.jsp"%>

<%
	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
	response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
	response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility

	out.clear();

	Key key = (Key)session.getAttribute("key");
	Long keyId = (Long)session.getAttribute("kid");

	if(keyId == null || keyId <= 0 || key == null){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}

	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);

	UUID filterId = null;
	String filterIdStr = request.getParameter("filterId");
	if(filterIdStr != null)
		filterId = DataAccessFactory.getInstance().createUUID(filterIdStr);

	if(filterId == null)
	{
		//create
		StringBuffer xmlb = new StringBuffer(64);
		xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

		xmlb.append("<query>");

		xmlb.append("<operate>create</operate>");

		TemplateType[] templateTypeArray = das.queryAllTemplateTypes();
		if(templateTypeArray == null || templateTypeArray.length == 0)
	xmlb.append("<templateTypes/>");
		else
		{
	xmlb.append("<templateTypes>");

	for(TemplateType templateType : templateTypeArray)
	{
		xmlb.append("<templateType>");
		xmlb.append("<id>").append(templateType.getId()).append("</id>");
		xmlb.append("<name>").append(XMLUtil.toSafeXMLString(templateType.getName())).append("</name>");
		xmlb.append("</templateType>");
	}

	xmlb.append("</templateTypes>");
		}

		xmlb.append("</query>");

		out.println(xmlb);
	}
	else
	{
		//edit
		Filter filter = das.queryFilter(filterId);

		Document document = XMLUtil.string2Document(filter.getXml(), "UTF-8");

		Node queryNode = XMLUtil.getSingleNode(document, "query");

		Node filterNameNode = document.createElement("filterName");
		filterNameNode.setTextContent(filter.getName());
		queryNode.appendChild(filterNameNode);

		Node fieldRelationNode = document.createElement("isAnd");
		fieldRelationNode.setTextContent(Boolean.toString(filter.isAnd()));
		queryNode.appendChild(fieldRelationNode);

		boolean isFocus = false;

		UUID[] focusFilterIdArray = das.queryUserFocusFilters(key.getUsername());
		for(int i = 0; focusFilterIdArray != null && i < focusFilterIdArray.length; i++)
		{
	if(focusFilterIdArray[i].equals(filterId))
	{
		isFocus = true;
		break;
	}
		}

		Node isFocusNode = document.createElement("isFocus");
		isFocusNode.setTextContent(Boolean.toString(isFocus));
		queryNode.appendChild(isFocusNode);

		Node isPublicNode = document.createElement("isPublic");
		isPublicNode.setTextContent(Boolean.toString(filter.isPublic()));
		queryNode.appendChild(isPublicNode);

		Node modifyAuthNode = document.createElement("modifyAuth");
		modifyAuthNode.setTextContent(key.getUsername().equals(filter.getCreateUser()) ? "true" : "false");
		queryNode.appendChild(modifyAuthNode);

		Node templateTypesNode = document.createElement("templateTypes");
		TemplateType[] templateTypeArray = das.queryAllTemplateTypes();
		for(TemplateType templateType : templateTypeArray)
		{
	Node templateTypeNode = document.createElement("templateType");
	XMLUtil.setAttribute(templateTypeNode, "id", templateType.getId().toString());
	XMLUtil.setAttribute(templateTypeNode, "name", templateType.getName());

	templateTypesNode.appendChild(templateTypeNode);
		}
		queryNode.appendChild(templateTypesNode);

		Node templateTypeNode = XMLUtil.getSingleNode(queryNode, "templateType");
		UUID templateTypeId = DataAccessFactory.getInstance().createUUID(XMLUtil.getAttribute(templateTypeNode, "id"));

		Node templatesNode = document.createElement("templates");
		queryNode.appendChild(templatesNode);

		Template[] templateArray = das.queryAllTemplates();
		for(Template template : templateArray)
		{
	if(!template.getTemplateTypeId().equals(templateTypeId))
		continue;

	Flow flow = das.queryFlow(template.getFlowId());
	if(flow == null)
		continue;

	boolean isAdd = false;

	if(flow.isRoleEditAction(Role.everyoneUUID) || flow.isRoleReadAction(Role.everyoneUUID))
		isAdd = true;

	if(!isAdd)
	{
		Action[] actionArray = flow.getActions();
		if(actionArray != null)
		{
			for(Action action : actionArray)
			{
				if(flow.isActionEveryoneRole(action.getId()))
				{
					isAdd = true;
					break;
				}
			}
		}
	}

	if(!isAdd)
	{
		Role[] roleArray = flow.queryUserNodeRoles(key.getUsername(), template.getId());
		if(roleArray != null && roleArray.length > 0)
			isAdd = true;
	}

	if(!isAdd)
		continue;

	Node templateNode = document.createElement("template");
	templatesNode.appendChild(templateNode);

	Node idNode = document.createElement("id");
	templateNode.appendChild(idNode);
	idNode.setTextContent(template.getId().toString());

	Node nameNode = document.createElement("name");
	templateNode.appendChild(nameNode);
	nameNode.setTextContent(template.getName());
		}

		List<Node> templateNodeList = XMLUtil.getNodes(queryNode, "template");
		Set<String> allUsersSet = new HashSet<String>();
		List<String> focusUsers = das.queryFocusUsersByFilter(filterId);

		//use template type
		if(templateNodeList.size() == 0)
		{
	Node whereNode = XMLUtil.getSingleNode(templateTypeNode, "where");
	Node displayNode = XMLUtil.getSingleNode(templateTypeNode, "display");
	Node orderNode = XMLUtil.getSingleNode(templateTypeNode, "order");

	Map<String, Set<Object>> whereFieldMap = getWhereFieldMap(whereNode);
	Map<String, CommonField> displayFieldMap = getDisplayFieldMap(displayNode);
	Map<String, CommonField> orderFieldMap = getOrderFieldMap(orderNode);

	int orderIndent = 0;
	String orderIndentStr = XMLUtil.getAttribute(orderNode, "indent");
	if(orderIndentStr != null)
	{
		try
		{
			orderIndent = Integer.parseInt(orderIndentStr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	templateTypeNode.setTextContent("");
	String content = queryTemplateTypeHTML(das, templateTypeId, whereFieldMap, displayFieldMap, orderFieldMap, orderIndent);
	if(content != null)
		templateTypeNode.setTextContent(content);
		}
		//use templates
		else
		{
	for(Node templateNode : templateNodeList)
	{
		UUID templateId = DataAccessFactory.getInstance().createUUID(XMLUtil.getAttribute(templateNode, "id"));

		Node whereNode = XMLUtil.getSingleNode(templateNode, "where");
		Node displayNode = XMLUtil.getSingleNode(templateNode, "display");
		Node orderNode = XMLUtil.getSingleNode(templateNode, "order");

		Map<String, Set<Object>> whereFieldMap = getWhereFieldMap(whereNode);
		Map<String, CommonField> displayFieldMap = getDisplayFieldMap(displayNode);
		Map<String, CommonField> orderFieldMap = getOrderFieldMap(orderNode);

		int orderIndent = 0;
		String orderIndentStr = XMLUtil.getAttribute(orderNode, "indent");
		if(orderIndentStr != null)
		{
			try
			{
				orderIndent = Integer.parseInt(orderIndentStr);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		templateNode.setTextContent("");
		String content = queryTemplateNodeHTML(das, templateId, whereFieldMap, displayFieldMap, orderFieldMap, orderIndent);
		/*String[] users = queryFlowUsersByTemplateId(das,templateId);
		if(users!=null)
			allUsersSet.addAll(Arrays.asList(users));*/

		if(content != null)
			templateNode.setTextContent(content);
	}
		}

		/*for(String user : focusUsers){
	if(allUsersSet.contains(user))
		allUsersSet.remove(user);
		}*/

		/*Node unSelectedUsers = document.createElement("unselectedusers");
		Node selectedUsers = document.createElement("selectedusers");
		String[] relatedAlias = null;
		String userAlias = null;
		for(String user : allUsersSet)
		{
	Node userNode = document.createElement("user");
	Node userNameNode = document.createElement("username");
	Node userAliasNode = document.createElement("useralias");
	userNameNode.setTextContent(user);
	relatedAlias = das.queryRelatedUsers(user);
	if(relatedAlias!=null&&relatedAlias.length>0){
		userAlias = relatedAlias[0];
	}else{
		userAlias = user;
	}
	userNameNode.setTextContent(user);
	userAliasNode.setTextContent(userAlias);
	userNode.appendChild(userNameNode);
	userNode.appendChild(userAliasNode);
	unSelectedUsers.appendChild(userNode);
		}
		for(String user : focusUsers)
		{
	Node userNode = document.createElement("user");
	Node userNameNode = document.createElement("username");
	Node userAliasNode = document.createElement("useralias");
	userNameNode.setTextContent(user);
	relatedAlias = das.queryRelatedUsers(user);
	if(relatedAlias!=null&&relatedAlias.length>0){
		userAlias = relatedAlias[0];
	}else{
		userAlias = user;
	}
	userNameNode.setTextContent(user);
	userAliasNode.setTextContent(userAlias);
	userNode.appendChild(userNameNode);
	userNode.appendChild(userAliasNode);
	selectedUsers.appendChild(userNode);
		}

		queryNode.appendChild(unSelectedUsers);
		queryNode.appendChild(selectedUsers);*/

		out.println(XMLUtil.document2String(document, "UTF-8"));
	}
%>