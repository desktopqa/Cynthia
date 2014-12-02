<%@ page language="java" contentType="text/xml; charset=UTF-8"%>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>

<%
response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility    

out.clear();

Key key = (Key)session.getAttribute("key");
Long keyId = (Long)session.getAttribute("kid");

if(key == null || keyId == null || keyId <= 0){
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}

String filterIdStr = request.getParameter("filterId");
if(filterIdStr == null){
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}

UUID filterId = DataAccessFactory.getInstance().createUUID(filterIdStr);

String[] dataIdArrayStrArray = request.getParameterValues("dataId");
if(dataIdArrayStrArray == null || dataIdArrayStrArray.length == 0){
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}

UUID[] taskIdArray = new UUID[dataIdArrayStrArray.length];
for(int i = 0; i < taskIdArray.length; i++){
	taskIdArray[i]	= DataAccessFactory.getInstance().createUUID(dataIdArrayStrArray[i]);
}

DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
	
String resultXml = das.cleanNewTagByTaskIds(filterId, taskIdArray, key.getUsername());

out.println(resultXml);
%>