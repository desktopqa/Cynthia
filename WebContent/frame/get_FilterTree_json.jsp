<%@ page language="java" contentType="application/x-json; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="com.sogou.qadev.service.login.bean.Key"%>
<%@ page import="java.util.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>

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

String node = request.getParameter("node");
if(node == null){
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}

DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);

if(node.equals("filter_tree_root")){
	out.println("[");
	out.println("{'id':'filter_tree_root/filter_tree_sys','text':'系统筛选器','leaf':false,'expanded':true}");
	out.println(",{'id':'filter_tree_root/filter_tree_my','text':'我的筛选器','leaf':false,'expanded':true}");	
	out.println("]");
}
else if (node.equals("filter_tree_root/filter_tree_sys")){
	Filter[] filterArray = das.querySysFilters(key.getUsername());
	if(filterArray.length == 0){
		out.println("[{'id':'filter_tree_root/filter_tree_sys/filter_tree_null','text':'无','leaf':true}]");
		return;
	}
	
	out.println("[");
	
	for(int i = 0; i < filterArray.length; i++){
		out.println((i > 0 ? "," : "") + "{'id':'filter_tree_root/filter_tree_sys/filter_tree_" + filterArray[i].getId() + "','text':'" + filterArray[i].getName().replaceAll("'", "\\\\'") + "','leaf':true}");
	}
	
	out.println("]");
}
else if (node.equals("filter_tree_root/filter_tree_my")){
	Filter[] filterArray = das.queryFocusFilters(key.getUsername());
	if(filterArray.length == 0){
		out.println("[{'id':'filter_tree_root/filter_tree_my/filter_tree_null','text':'无','leaf':true}]");
		return;
	}
	
	out.println("[");
	
	for(int i = 0; i < filterArray.length && i < 50; i++){
		out.println((i > 0 ? "," : "") + "{'id':'filter_tree_root/filter_tree_my/filter_tree_" + filterArray[i].getId() + "','text':'" + filterArray[i].getName().replaceAll("'", "\\\\'") + "','leaf':true}");
	}
	
	out.println("]");
}
%>