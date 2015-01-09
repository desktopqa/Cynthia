<%@page import="com.sogou.qadev.service.cynthia.service.FilterQueryManager"%>
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
	if(filterIdStr==null)
	{
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	String[] showFieldArray = request.getParameterValues("showFields[]");
	
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
	
	String templateIdStr = XMLUtil.getAttribute(templateNode, "id");
	
	UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
	
	Template template = das.queryTemplate(templateId);
	if(template==null)
	{
		return;
	}
	
	
	Node displayNode = XMLUtil.getSingleNode(templateNode,"display");
	
	//删除目前显示字段
	List<Node> displayFields = XMLUtil.getNodes(displayNode,"field");
	for(Node node : displayFields)
	{
		displayNode.removeChild(node);
	}
	
	Map<String,String> fieldWidthMap = FilterQueryManager.getDisplayFieldAndWidth(filter.getXml(), das);

	Field showField = null;
	for(String fieldId : showFieldArray){
		
		//添加配置字段
		if(fieldId.startsWith("FIEL-"))
			fieldId = fieldId.substring(fieldId.indexOf("FIEL-") + 5);
		
		showField = template.getField(DataAccessFactory.getInstance().createUUID(fieldId)); 
		Node fieldNode = filterXmlDoc.createElement("field");
		if(showField==null){
			String name = ConfigUtil.baseFieldNameMap.get(fieldId);
			if( name != null){
				XMLUtil.setAttribute(fieldNode,"id",fieldId);
				XMLUtil.setAttribute(fieldNode,"name",ConfigUtil.baseFieldNameMap.get(fieldId));
				XMLUtil.setAttribute(fieldNode,"type",fieldId);
				if(fieldWidthMap.get(name) != null){
					XMLUtil.setAttribute(fieldNode,"width",fieldWidthMap.get(name));
				}
			}
		}else{
			XMLUtil.setAttribute(fieldNode,"id",showField.getId().getValue());
			XMLUtil.setAttribute(fieldNode,"name",showField.getName());
			XMLUtil.setAttribute(fieldNode,"type",showField.getType().toString());
			XMLUtil.setAttribute(fieldNode,"datatype",showField.getDataType() == null ? "" : showField.getDataType().toString());
			if(fieldWidthMap.get(showField.getName()) != null ){
				XMLUtil.setAttribute(fieldNode,"width",fieldWidthMap.get(showField.getName()));
			}
		}

		displayNode.appendChild(fieldNode);
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
%>