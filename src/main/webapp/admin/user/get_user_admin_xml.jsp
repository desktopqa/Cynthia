<%@page import="java.util.ArrayList"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UserInfo"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Stat"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="java.util.TreeMap"%>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.net.*" %>

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
	
	boolean initUser = false;
	if(request.getParameter("initUser") != null)
		initUser = Boolean.parseBoolean(request.getParameter("initUser"));
	
	UserInfo userInfo = das.queryUserInfoByUserName(key.getUsername());
	
	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	xmlb.append("<root>");
	xmlb.append("<isError>false</isError>");
	xmlb.append("<curUserRole>"+userInfo.getUserRole()+"</curUserRole>");
	
	if(initUser)
	{
		List<UserInfo> userList = new ArrayList<UserInfo>();
		
		String userStat = request.getParameter("userStat");
		String queryUserName = request.getParameter("userName");
		
		userList.addAll(das.queryAllUsersByStatAndName(key.getUsername(), userStat,queryUserName));
		
		if(userList.size() == 0)
			xmlb.append("<users/>");
		else
		{
			xmlb.append("<users>");
			
			for(UserInfo userinfo : userList){
				xmlb.append("<user>");
				xmlb.append("<id>").append(userinfo.getId()).append("</id>");
				xmlb.append("<name>").append(userinfo.getNickName()).append("</name>");
				xmlb.append("<email>").append(userinfo.getUserName()).append("</email>");
				xmlb.append("<password>").append(userinfo.getUserPassword()).append("</password>");
				xmlb.append("<userRole>").append(userinfo.getUserRole()).append("</userRole>");
				xmlb.append("<userStat>").append(userinfo.getUserStat()).append("</userStat>");
				xmlb.append("<createTime>").append(userinfo.getCreateTime()).append("</createTime>");
				xmlb.append("<lastLoginTime>").append(userinfo.getLastLoginTime() == null ?  "" : userInfo.getLastLoginTime()).append("</lastLoginTime>");
				xmlb.append("</user>");
			}
			xmlb.append("</users>");
		}	
	}
	xmlb.append("</root>");
	out.println(xmlb.toString());
%>