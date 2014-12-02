<%@page import="com.sogou.qadev.service.cynthia.bean.UserInfo.UserRole"%>
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

String[] dataIdStrArray	= request.getParameterValues("dataId");
String user = key.getUsername();

DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
Template template = null;
Flow flow = null;
boolean isDeleteAllow = false;
boolean deleteAll = true;

for(String dataIdStr : dataIdStrArray){
	UUID dataId = DataAccessFactory.getInstance().createUUID(dataIdStr);
	Data data = das.queryData(dataId);
	if(data != null){
		
		UserInfo userInfo = das.queryUserInfoByUserName(user);
		if(userInfo != null && userInfo.getUserRole().equals(UserRole.super_admin))
		{
			//超级管理员直接删除数据
			das.removeData(data);
		}
		else
		{
			isDeleteAllow = false;
			template = das.queryTemplate(data.getTemplateId());
			if(template!=null)
			{
				flow = das.queryFlow(template.getFlowId());
				isDeleteAllow = flow.isDeleteActionAllow(user,template.getId());
				if(isDeleteAllow){
					das.removeData(data);
					das.deleteFilterUserTasks(data.getId());  //删除新旧数据记录
				}
				else{
					deleteAll = false;
				}
			}
		}
	}
}

ErrorCode errorCode = das.commitTranscation();
if(errorCode.equals(ErrorCode.success)){
	if(deleteAll)
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><success>all</success>");
	else
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><success>notAll</success>");
}else{
	das.rollbackTranscation();
	out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><success>fail</success>");
}
%>