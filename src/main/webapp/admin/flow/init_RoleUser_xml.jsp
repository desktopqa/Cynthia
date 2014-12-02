<%@page import="com.sogou.qadev.service.cynthia.bean.UserInfo"%>
<%@page import="java.util.Map"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="java.util.TreeSet"%>

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
	
	UUID templateId = DataAccessFactory.getInstance().createUUID(request.getParameter("templateId"));
	UUID roleId = DataAccessFactory.getInstance().createUUID(request.getParameter("roleId"));
	
	HashSet<String> roleUserSet = new HashSet<String>();
	
	String[] roleUserArray = flow.queryNodeRoleUsers(templateId, roleId);
	roleUserSet.addAll(Arrays.asList(roleUserArray));
	
	
	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	xmlb.append("<root>");
	xmlb.append("<isError>false</isError>");
	
	TreeSet<String> userSet = new TreeSet<String>();
	String[] userArray = flow.queryNodeUsers(templateId);
	Map<String,UserInfo> userInfoMap = das.queryUserInfoByUserNames(userArray);
	
	if(userArray != null)
		userSet.addAll(Arrays.asList(userArray));
	
	if(userSet.size() == 0)
		xmlb.append("<users/>");
	else
	{
		xmlb.append("<users>");
		
		for(String user : userSet)
		{
			xmlb.append("<user>");
			
			xmlb.append("<name>").append(XMLUtil.toSafeXMLString(user)).append("</name>");
			xmlb.append("<showName>").append( "[" + XMLUtil.toSafeXMLString( userInfoMap.get(user) == null ? user : userInfoMap.get(user).getNickName()) + "]-" + user).append("</showName>");
			xmlb.append("<right>").append(roleUserSet.contains(user) ? "yes" : "no").append("</right>");
			
			xmlb.append("</user>");
		}
		
		xmlb.append("</users>");
	}
	
	xmlb.append("</root>");
		
	out.println(xmlb.toString());
%>