<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Stat"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="java.util.HashSet"%>
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
	
	HashSet<UUID> roleActionIdSet = new HashSet<UUID>();
	UUID roleId = DataAccessFactory.getInstance().createUUID(request.getParameter("roleId"));
	Action[] roleActionArray = flow.queryRoleActions(roleId);
	if(roleActionArray != null)
	{
		for(Action action : roleActionArray)
	roleActionIdSet.add(action.getId());
	}
	
	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	xmlb.append("<root>");
	xmlb.append("<isError>false</isError>");
	xmlb.append("<actions>");
	
	xmlb.append("<action>");
	xmlb.append("<id>").append(Action.editUUID).append("</id>");
	xmlb.append("<name>").append(XMLUtil.toSafeXMLString(Action.editName)).append("</name>");
	xmlb.append("<beginStatId/>");
	xmlb.append("<endStatId/>");
	xmlb.append("<right>").append(flow.isRoleEditAction(roleId) ? "yes" : "no").append("</right>");
	xmlb.append("</action>");
	
	xmlb.append("<action>");
	xmlb.append("<id>").append(Action.readUUID).append("</id>");
	xmlb.append("<name>").append(XMLUtil.toSafeXMLString(Action.readName)).append("</name>");
	xmlb.append("<beginStatId/>");
	xmlb.append("<endStatId/>");
	xmlb.append("<right>").append(flow.isRoleReadAction(roleId) ? "yes" : "no").append("</right>");
	xmlb.append("</action>");
	
	xmlb.append("<action>");
	xmlb.append("<id>").append(Action.deleteUUID).append("</id>");
	xmlb.append("<name>").append(XMLUtil.toSafeXMLString(Action.deleteName)).append("</name>");
	xmlb.append("<beginStatId/>");
	xmlb.append("<endStatId/>");
	xmlb.append("<right>").append(flow.isRoleDeleteAction(roleId) ? "yes" : "no").append("</right>");
	xmlb.append("</action>");
	
	TreeMap<String, Action> actionMap = new TreeMap<String, Action>();
	Action[] actionArray = flow.getActions();
	if(actionArray != null)
	{
		for(Action action : actionArray)
	actionMap.put(action.getName(), action);
	}
	
	for(Action action : actionMap.values())
	{
		xmlb.append("<action>");
		
		xmlb.append("<id>").append(action.getId()).append("</id>");
		xmlb.append("<name>").append(XMLUtil.toSafeXMLString(action.getName())).append("</name>");
		if(action.getBeginStatId() != null)
			xmlb.append("<beginStatId>").append(action.getBeginStatId()).append("</beginStatId>");
		else
			xmlb.append("<beginStatId/>");
		xmlb.append("<endStatId>").append(action.getEndStatId()).append("</endStatId>");
		xmlb.append("<right>").append(roleActionIdSet.contains(action.getId()) ? "yes" : "no").append("</right>");
		
		xmlb.append("</action>");
	}
		
	xmlb.append("</actions>");
	
	TreeMap<String, Stat> statMap = new TreeMap<String, Stat>();
	Stat[] statArray = flow.getStats();
	if(statArray != null)
	{
		for(Stat stat : statArray)
	statMap.put(stat.getName(), stat);
	}
	
	if(statMap.size() == 0)
		xmlb.append("<stats/>");
	else
	{
		xmlb.append("<stats>");
		
		for(Stat stat : statMap.values())
		{
	xmlb.append("<stat>");
	
	xmlb.append("<id>").append(stat.getId()).append("</id>");
	xmlb.append("<name>").append(XMLUtil.toSafeXMLString(stat.getName())).append("</name>");
	
	xmlb.append("</stat>");
		}
		
		xmlb.append("</stats>");
	}
	
	xmlb.append("</root>");
		
	out.println(xmlb.toString());
%>