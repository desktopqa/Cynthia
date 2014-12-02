<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="java.util.TreeMap"%>

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
	
	UUID flowId = DataAccessFactory.getInstance().createUUID(request.getParameter("flowId"));
	
	Flow flow = das.queryFlow(flowId);
	if(flow == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.flow_not_found));
		return;
	}
	
	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	xmlb.append("<root>");
	xmlb.append("<isError>false</isError>");
	xmlb.append("<roles>");
	
	xmlb.append("<role>");
	xmlb.append("<id>").append(Role.everyoneUUID).append("</id>");
	xmlb.append("<name>").append(XMLUtil.toSafeXMLString(Role.everyoneName)).append("</name>");
	xmlb.append("</role>");
	
	TreeMap<String, Role> roleMap = new TreeMap<String, Role>();
	Role[] roleArray = flow.getRoles();
	if(roleArray != null)
	{
		for(Role role : roleArray)
	roleMap.put(role.getName(), role);
	}

	for(Role role : roleMap.values())
	{
		xmlb.append("<role>");	
		xmlb.append("<id>").append(role.getId()).append("</id>");
		xmlb.append("<name>").append(XMLUtil.toSafeXMLString(role.getName())).append("</name>");
		xmlb.append("</role>");
	}
	
	xmlb.append("</roles>");
	
	xmlb.append("</root>");
		
	out.println(xmlb.toString());
%>