<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>

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
	
	
	UUID actionId = DataAccessFactory.getInstance().createUUID(request.getParameter("actionId"));
	Action action = flow.getAction(actionId);
	
	String actionName = request.getParameter("actionName");
	if(action != null){
		if(actionName != "" && actionName != null){
			action.setName(actionName);
		}
		
		String beginStatId = request.getParameter("beginStatId");
		if(beginStatId == null || beginStatId.equals(""))
			action.setBeginStatId(null);
		else
			action.setBeginStatId(DataAccessFactory.getInstance().createUUID(beginStatId));
		
		String endStatId = request.getParameter("endStatId");
		if(endStatId != null)
			action.setEndStatId(DataAccessFactory.getInstance().createUUID(endStatId));
		
		//设置是否指派到多人
		String assignToMore = request.getParameter("assignToMore");
		if(assignToMore != "" && assignToMore != null){
			action.setAssignToMore(assignToMore.equals("true"));
		}
	}
	
		
	String[] roleRightArray = (String[])ArrayUtil.format(request.getParameterValues("roleRight"), new String[0]);
	for(String roleRight : roleRightArray)
	{
		UUID roleId = DataAccessFactory.getInstance().createUUID(roleRight.split("\\|")[0]);
		boolean right = Boolean.parseBoolean(roleRight.split("\\|")[1]);
		
		if(right)
			flow.addActionRole(actionId, roleId);
		else
			flow.removeActionRole(actionId, roleId);
	}
	
	ErrorCode errorCode = das.updateFlow(flow);
	
	if(errorCode.equals(ErrorCode.success)){
		das.updateCache(DataAccessAction.update, flow.getId().getValue(),flow);
		out.println(ErrorManager.getCorrectXml());
	}else{
		out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
	}
%>