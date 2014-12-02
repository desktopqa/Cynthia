<%@ page language="java" contentType="text/xml; charset=UTF-8"%>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.*"%>
<%@page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>

<%
	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility    

out.clear();

Key key = ( Key )session.getAttribute( "key" );
Long keyId = (Long)session.getAttribute("kid");

if(key == null || keyId == null || keyId <= 0){
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}
DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(),keyId);
String filterIdStr = request.getParameter("filterId");
String homeFilterIdStr = das.queryHomeFilter(key.getUsername());

boolean result = false;
if(homeFilterIdStr == null||"".equals(homeFilterIdStr))
{
	result = das.addHomeFilter(key.getUsername(),filterIdStr);
}else
{
	result = das.updateHomeFilter(key.getUsername(),filterIdStr);
}

out.println("首页设置成功");
%>