<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="java.util.Map"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Stat"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@ page import="org.w3c.dom.*"%>
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
	
	String idStr = request.getParameter("id");
	if(idStr == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.param_error));
		return;
	}
	
	UUID id = DataAccessFactory.getInstance().createUUID(idStr);

	Template template = das.queryTemplate(id);
	if(template == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.template_not_found));
		return;
	}
	
	Document doc = XMLUtil.string2Document(template.toXMLString(), "UTF-8");
	
	Node templateNode = XMLUtil.getSingleNode(doc, "template");
	doc.removeChild(templateNode);
	
	Node rootNode = doc.createElement("root");
	doc.appendChild(rootNode);
	
	Node isErrorNode = doc.createElement("isError");
	rootNode.appendChild(isErrorNode);
	isErrorNode.setTextContent("false");
	
	rootNode.appendChild(templateNode);
	
	
	Flow flow = das.queryFlow(template.getFlowId());
	
	if(flow == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.flow_not_found));
		return;
	}
	
	Node actionsNode = doc.createElement("actions");
	rootNode.appendChild(actionsNode);
	
	Action[] actionArray = flow.getActions();

	if(actionArray != null)
	{
		for(Action action : actionArray)
		{
			Node actionNode = doc.createElement("action");
			actionsNode.appendChild(actionNode);
			
			Node actionIdNode = doc.createElement("id");
			actionNode.appendChild(actionIdNode);
			actionIdNode.setTextContent(action.getId().toString());
			
			Node actionNameNode = doc.createElement("name");
			actionNode.appendChild(actionNameNode);
			actionNameNode.setTextContent(action.getName());
			
			Node rolesNode = doc.createElement("roles");
			actionNode.appendChild(rolesNode);
			
			if(flow.isActionEveryoneRole(action.getId()))
			{
				Node everyoneRoleNode = doc.createElement("role");
				rolesNode.appendChild(everyoneRoleNode);
				
				Node everyoneRoleIdNode = doc.createElement("id");
				everyoneRoleNode.appendChild(everyoneRoleIdNode);
				everyoneRoleIdNode.setTextContent(Role.everyoneUUID.toString());
				
				Node everyoneRoleNameNode = doc.createElement("name");
				everyoneRoleNode.appendChild(everyoneRoleNameNode);
				everyoneRoleNameNode.setTextContent(Role.everyoneName);
			}
			
			Role[] roleArray = flow.queryActionRoles(action.getId());
			
			if(roleArray != null)
			{
				for(Role role : roleArray)
				{
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
		}
	}
	
	Stat[] statArray = flow.getStats();
	Node statsNode = doc.createElement("stats");
	rootNode.appendChild(statsNode);
	if(statArray != null)
	{
		for(Stat stat : statArray)
		{
			Node statNode = doc.createElement("stat");
			statsNode.appendChild(statNode);
			
			Node statIdNode = doc.createElement("id");
			statNode.appendChild(statIdNode);
			statIdNode.setTextContent(stat.getId().toString());
			
			Node statNameNode = doc.createElement("name");
			statNode.appendChild(statNameNode);
			statNameNode.setTextContent(stat.getName());
		}
	}
	
	
	Node rolesNode = doc.createElement("roles");
	rootNode.appendChild(rolesNode);
	
	if(flow.isActionEveryoneRole(Action.editUUID))
	{
		Node everyoneRoleNode = doc.createElement("role");
		rolesNode.appendChild(everyoneRoleNode);
		
		Node everyoneRoleIdNode = doc.createElement("id");
		everyoneRoleNode.appendChild(everyoneRoleIdNode);
		everyoneRoleIdNode.setTextContent(Role.everyoneUUID.toString());
		
		Node everyoneRoleNameNode = doc.createElement("name");
		everyoneRoleNode.appendChild(everyoneRoleNameNode);
		everyoneRoleNameNode.setTextContent(Role.everyoneName);
	}
	
	Role[] roleArray = flow.queryEditActionRoles();
	if(roleArray != null)
	{
		for(Role role : roleArray)
		{
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
	
	Node filtersNode = doc.createElement("filters");
	rootNode.appendChild(filtersNode);
	
	Map<String,String> idNameMap = das.queryFilterIdNameMap(key.getUsername());
	
	if(idNameMap.size() > 0)
	{
		for(String filterId : idNameMap.keySet())
		{
			Node filterNode = doc.createElement("filter");
			filtersNode.appendChild(filterNode);
			
			Node filterIdNode = doc.createElement("id");
			filterNode.appendChild(filterIdNode);
			filterIdNode.setTextContent(filterId);
			
			Node filterNameNode = doc.createElement("name");
			filterNode.appendChild(filterNameNode);
			filterNameNode.setTextContent(idNameMap.get(filterId));
		}
	}
	
	out.println(XMLUtil.document2String(doc, "UTF-8"));
%>