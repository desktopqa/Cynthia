<%@ page language="java" contentType="text/xml; charset=UTF-8"%>

<%@page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
<%@page import="com.sogou.qadev.service.cynthia.util.*"%>
<%@page import="java.util.*"%>
<%@ page import="org.w3c.dom.*" %>
<%@page import="com.sogou.qadev.service.cynthia.bean.Timer"%>
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

String filterIdStr = request.getParameter("filterId");
String hidden = request.getParameter("hidden");
String fieldIdStr = request.getParameter("fieldId");
String templateIdStr = request.getParameter("templateId");
String type = request.getParameter("type");

if(filterIdStr==null)
{
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}

DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);

UUID filterId = DataAccessFactory.getInstance().createUUID(filterIdStr);
Filter filter = das.queryFilter(filterId);
Document filterXmlDoc = XMLUtil.string2Document(filter.getXml(), "UTF-8");

List<Node> templateNodeList = XMLUtil.getNodes(filterXmlDoc,"query/template");

if(templateNodeList==null||templateNodeList.size()==0)
{
	return;
}

Node templateNode = templateNodeList.get(0);

//List<Node> displayFieldList = XMLUtil.getNodes(templateNode,"display");
if("columnMoved".equals(type))
{
	List<Node> displayFieldsNode = XMLUtil.getNodes(templateNode,"display/field");
	Node displayNode = XMLUtil.getSingleNode(templateNode,"display");
	
// 	for(Node node : displayFieldsNode)
// 	{
// 		System.out.println(XMLUtil.getAttribute(node, "name"));
// 	}
	
	String oldIndexStr = request.getParameter("oldIndex");
	String newIndexStr = request.getParameter("newIndex");
	int oldIndex = Integer.parseInt(oldIndexStr) -1;
	int newIndex = Integer.parseInt(newIndexStr) -1;
	Node oldPositionNode = displayFieldsNode.get(oldIndex);
	Node newPositionNode = displayFieldsNode.get(newIndex);
	//displayFieldsNode.set(newIndex,oldPositionNode);
	//displayFieldsNode.set(oldIndex,newPositionNode);
	displayFieldsNode.remove(oldIndex);
	displayFieldsNode.add(newIndex,oldPositionNode);
	displayNode.setTextContent("");
	for(Node node : displayFieldsNode)
	{
		displayNode.appendChild(node);
	}
	
}else{
		Node displayNode = XMLUtil.getSingleNode(templateNode,"display");
		UUID fieldId = DataAccessFactory.getInstance().createUUID(fieldIdStr);
		UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
		Template template = das.queryTemplate(templateId);
		Field field = template.getField(fieldId);
		
		if(hidden.equals("true"))
		{//需要删除
			List<Node> displayFields = XMLUtil.getNodes(displayNode,"field");
			for(Node node : displayFields)
			{
				if(fieldIdStr.equals(XMLUtil.getAttribute(node,"id")))
				{
					displayNode.removeChild(node);
					break;
				}
			}
		}else{//需要添加
			Node fieldNode = filterXmlDoc.createElement("field");
			if(field==null)
			{
				XMLUtil.setAttribute(fieldNode,"id",fieldIdStr);
				XMLUtil.setAttribute(fieldNode,"name",ConfigUtil.baseFieldNameMap.get(fieldIdStr)==null?fieldIdStr:ConfigUtil.baseFieldNameMap.get(fieldIdStr));
				XMLUtil.setAttribute(fieldNode,"type",fieldIdStr);
			}else
			{
				XMLUtil.setAttribute(fieldNode,"id",fieldIdStr);
				XMLUtil.setAttribute(fieldNode,"name",field.getName());
				XMLUtil.setAttribute(fieldNode,"type",field.getType().toString());
				XMLUtil.setAttribute(fieldNode,"datatype",field.getDataType() == null ? "" : field.getDataType().toString());
			}
			
			displayNode.appendChild(fieldNode);
		}
}

filter.setXml(XMLUtil.document2String(filterXmlDoc,"UTF-8"));
ErrorCode errorCode = das.updateFilter(filter);
StringBuffer xmlb = new StringBuffer(64);
xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
xmlb.append("<root>");
xmlb.append("<filterId>");
xmlb.append(filter.getId().toString());
xmlb.append("</filterId>");
xmlb.append("<isError>false</isError>");
xmlb.append("</root>");
out.println(xmlb.toString());
