<%@page import="com.sogou.qadev.service.cynthia.service.ProjectInvolveManager"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Data"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="org.w3c.dom.Node"%>
<%@ page import="java.util.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Pair"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option.Forbidden"%>
<%@ page import="org.apache.commons.httpclient.HttpClient"%>
<%@ page import="org.apache.commons.httpclient.NameValuePair"%>
<%@ page import="org.apache.commons.httpclient.methods.PostMethod"%>
<%@ page import="org.apache.commons.httpclient.params.*"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>

<%!DataAccessFactory daf = DataAccessFactory.getInstance();%>

<%
	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
	response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
	response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility
	
	out.clear();
	
	Long keyId = (Long)session.getAttribute("kid");
	Key key = (Key)session.getAttribute("key");

	if(keyId == null || keyId <= 0 || key == null){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	String templateIdStr = request.getParameter("templateId");
	if(templateIdStr == null){
		out.println(ErrorManager.getErrorXml(ErrorType.param_error));
		return;
	}
	
	UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
	
	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
	
	//get template
	Template template = das.queryTemplate(templateId);
	if(template == null){	
		out.println(ErrorManager.getErrorXml(ErrorType.template_not_found));
		return;
	}
	
	//get templateXml
	String templateXml = template.toXMLString();
	if(templateXml == null){
		out.println(ErrorManager.getErrorXml(ErrorType.template_xml_error));
		return;
	}
	
	//get flow
	Flow flow = das.queryFlow(template.getFlowId());
	if(flow == null){
		out.println(ErrorManager.getErrorXml(ErrorType.flow_not_found));
		return;
	}
	
	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	
	xmlb.append("<root>");
	
	xmlb.append("<isError>false</isError>");
	xmlb.append("<isProTemplate>").append(String.valueOf(template.getTemplateConfig().isProjectInvolve())).append("</isProTemplate>");
	xmlb.append("<productInvolvedId>").append(String.valueOf(template.getTemplateConfig().getProductInvolveId())).append("</productInvolvedId>");
	xmlb.append("<projectInvolvedId>").append(String.valueOf(template.getTemplateConfig().getProjectInvolveId())).append("</projectInvolvedId>");
	
	xmlb.append(templateXml.substring(templateXml.indexOf("<template>")));
	
	//get actionArray
	Action[] actionArray = flow.queryUserNodeBeginActions(das.getUsername(), template.getId());
	
	if(actionArray == null || actionArray.length == 0){
		xmlb.append("<actions/>");
	}else{
		xmlb.append("<actions>");
		
		for(Action action : actionArray){
			xmlb.append("<action>");
			xmlb.append("<id>").append(action.getId()).append("</id>");
			xmlb.append("<name>").append(XMLUtil.toSafeXMLString(action.getName())).append("</name>");
			xmlb.append("<endStatId>").append(action.getEndStatId()).append("</endStatId>");
			xmlb.append("<assignToMore>").append(action.getAssignToMore()).append("</assignToMore>");
			xmlb.append("<nextActionRoles>").append(flow.queryNextActionRoleIdsByActionId(action.getId())).append("</nextActionRoles>");
			xmlb.append("<isEndAction>").append(flow.isEndAction(action.getId())).append("</isEndAction>");
			
			String[] userArray = null;
			userArray = flow.queryNodeStatAssignUsers(template.getId(), action.getEndStatId());
			if(userArray == null || userArray.length == 0){
				xmlb.append("<users/>");
			}
			else{
				xmlb.append("<users>");
				
				for(String user : userArray){
			StringBuffer userRoles = new StringBuffer();
			
			Role[] userRoleArray = flow.queryUserNodeRoles(user, template.getId());
			for(Role userRole : userRoleArray){
				if(userRoles.length() > 0){
					userRoles.append(",");
				}
				
				userRoles.append(userRole.getName());
			}
			
			String userAlias = CynthiaUtil.getUserAlias(user);
			xmlb.append("<user" + (userAlias != null ? " alias=\"" + XMLUtil.toSafeXMLString(userAlias) + "\"" : "") + ">").append(XMLUtil.toSafeXMLString(user + "[" + userRoles + "]")).append("</user>");
				}
				
				xmlb.append("</users>");
			}
			
			xmlb.append("</action>");
	
	}
		
		xmlb.append("</actions>");
	}
	
	//roles
	xmlb.append("<roles>");
	
	xmlb.append("<role>");
	xmlb.append("<id>").append(Role.everyoneUUID).append("</id>");
	xmlb.append("<name>").append(XMLUtil.toSafeXMLString(Role.everyoneName)).append("</name>");
	xmlb.append("</role>");
	
	Role[] roleArray = flow.queryUserNodeRoles(key.getUsername(), template.getId());
	if(roleArray != null){
		for(Role role : roleArray){
	xmlb.append("<role>");
	xmlb.append("<id>").append(role.getId()).append("</id>");
	xmlb.append("<name>").append(XMLUtil.toSafeXMLString(role.getName())).append("</name>");
	xmlb.append("</role>");
		}
	}
	
	xmlb.append("</roles>");
	
	xmlb.append("</root>");
	
	Document doc = XMLUtil.string2Document(xmlb.toString(), "UTF-8");
	
	Node rootNode = XMLUtil.getSingleNode(doc, "root");
	
	Node templateNode =XMLUtil.getSingleNode(rootNode, "template");
	
	Node layoutNode = XMLUtil.getSingleNode(templateNode,"layout");
	
	//Node validColumnsNode =XMLUtil.getSingleNode(templateNode, "validColumns");
	rootNode.removeChild(templateNode);
	
	rootNode.appendChild(layoutNode);
	//rootNode.appendChild(validColumnsNode);
	
	List<Node> rowNodes = XMLUtil.getNodes(layoutNode, "rows/row");
	for(Node rowNode : rowNodes)
	{
		List<Node> columnNodes = XMLUtil.getNodes(rowNode, "column");
		for(Node columnNode : columnNodes)
		{
	List<Node> fieldNodeList = XMLUtil.getNodes(columnNode, "field");
	for(Node fieldNode : fieldNodeList){
		Node typeNode = XMLUtil.getSingleNode(fieldNode, "type");
		typeNode.setTextContent(typeNode.getTextContent().split("\\_")[1]);
		Node fieldIdNode = XMLUtil.getSingleNode(fieldNode,"id");
		Node dataTypeNode = XMLUtil.getSingleNode(fieldNode, "dataType");
		if(dataTypeNode.getTextContent().length() > 0){
	dataTypeNode.setTextContent(dataTypeNode.getTextContent().split("\\_")[1]);
		}
		
		List<Node> optionNodeList = XMLUtil.getNodes(fieldNode, "options/option");
		String fieldId = fieldIdNode.getTextContent();
		
		for(Node optionNode : optionNodeList){
	Node forbiddenNode = XMLUtil.getSingleNode(optionNode, "forbidden");
	forbiddenNode.setTextContent(forbiddenNode.getTextContent().split("\\_")[1]);
		}
	
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
	}
		}
	}
	
	out.println(XMLUtil.document2String(doc, "UTF-8"));
%>
