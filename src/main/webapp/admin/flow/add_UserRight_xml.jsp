<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>

<%@ page import="javax.naming.Context" %>
<%@ page import="javax.naming.NamingEnumeration" %>
<%@ page import="javax.naming.directory.Attribute" %>
<%@ page import="javax.naming.directory.Attributes" %>
<%@ page import="javax.naming.directory.DirContext" %>
<%@ page import="javax.naming.directory.InitialDirContext" %>
<%@ page import="javax.naming.directory.SearchControls" %>
<%@ page import="javax.naming.directory.SearchResult" %>
<%@ page import="java.util.Hashtable" %>


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
	
	//备份flow
// 	flow = (Flow)flow.clone();
	
	UUID templateId = DataAccessFactory.getInstance().createUUID(request.getParameter("templateId"));
	UUID roleId = DataAccessFactory.getInstance().createUUID(request.getParameter("roleId"));
	String[] addUsers = request.getParameterValues("user[]");
	
	if(addUsers != null && addUsers.length > 0){
		for(String user : addUsers)
			flow.addRight(user, templateId, roleId);
		
		ErrorCode errorCode = das.updateFlow(flow);
		if(errorCode.equals(ErrorCode.success)){
			das.updateCache(DataAccessAction.update, flow.getId().getValue(), flow);
			out.println(ErrorManager.getCorrectXml());
		}else{
			out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
		}
	}else{
		out.println(ErrorManager.getCorrectXml());
	}
	
%>