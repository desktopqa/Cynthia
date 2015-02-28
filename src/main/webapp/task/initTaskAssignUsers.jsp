<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="java.util.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Pair"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>

<%!
static DataAccessFactory daf = DataAccessFactory.getInstance();
%>

<%
	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
	response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
	response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility

	out.clear();

	Long keyId = (Long)session.getAttribute("kid");
	Key key = (Key)session.getAttribute("key");

	if(keyId == null || keyId <= 0 || key == null){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}

	DataAccessSession das = daf.createDataAccessSession(key.getUsername(), keyId);
	
	Data data = null;
	if(request.getParameter("taskId") != null){
		UUID taskId = daf.createUUID(request.getParameter("taskId"));
		data = das.queryData(taskId);
		if(data == null){
			out.println(ErrorManager.getErrorXml(ErrorType.data_not_found_inDb));
			return;
		}
	}
	
	UUID statId = daf.createUUID(request.getParameter("statId"));
	UUID actionId = daf.createUUID(request.getParameter("actionId"));
	Template template = null;
	if(data == null){
		UUID templateId = daf.createUUID(request.getParameter("templateId"));
		template = das.queryTemplate(templateId);
	}else{
		template = das.queryTemplate(data.getTemplateId());
	}
	
	if(template == null){
		out.println(ErrorManager.getErrorXml(ErrorType.template_not_found));
		return;
	}
	
	Flow flow = das.queryFlow(template.getFlowId());
	if(flow == null){
		out.println(ErrorManager.getErrorXml(ErrorType.flow_not_found));
		return;
	}
	
	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	
	xmlb.append("<root>");
	
	xmlb.append("<needUser>");
	Action[] actionArray = flow.queryStatActions(statId);
	if(actionArray == null || actionArray.length == 0){
		xmlb.append(false);
	}else{
		xmlb.append(true);
	}
	xmlb.append("</needUser>");
	
	Set<String> userSet = new LinkedHashSet<String>();
	
	String[] userArray = flow.queryNodeStatAssignUsers(template.getId(), statId);
	
	if(userArray != null && userArray.length > 0){
		userSet.addAll(Arrays.asList(userArray));
	}
	
	if(data != null){
 		// everyone可新建的表单 默认加上创建人员
 		for(Action action : flow.queryBeginActions()){
 			if(flow.isActionEveryoneRole(action.getId())){
 				userSet.add(data.getCreateUsername());
 				break;
 			}
 		}
 	}
	
	if(userSet.size() == 0){
		xmlb.append("<users/>");
	}
	else{
		xmlb.append("<users>");
		
		List<String> firstUsers = new ArrayList<String>();
		
		if(data != null){
			if(data.getAssignUsername() != null && data.getStatusId().toString().equals(statId.toString()))//当前是编辑动作
				firstUsers = Arrays.asList(data.getAssignUsername().split(","));
			
// 			if(firstUsers.size() == 0){
// 				for(int i = data.getChangeLogs().length - 1; i >= 0; i--){
// 					if(data.getChangeLogs()[i].getActionId() != null && userSet.contains(data.getChangeLogs()[i].getCreateUser())){
// 						firstUsers.add(data.getChangeLogs()[i].getCreateUser());
// 						break;
// 					}
// 				}
// 			}
		}
		
		if(firstUsers.size() > 1){
			xmlb.append("<assignToMore>true</assignToMore>");
		}
		
		if(firstUsers.size() > 0){
			StringBuffer firstUserRoles = new StringBuffer();
			for(String firstUser : firstUsers){
				Role[] firstUserRoleArray = flow.queryUserNodeRoles(firstUser, template.getId());
				for(Role firstUserRole : firstUserRoleArray){
					if(firstUserRoles.length() > 0){
						firstUserRoles.append(",");
					}
					
					firstUserRoles.append(firstUserRole.getName());
				}
				
				String userAlias = CynthiaUtil.getUserAlias(firstUser);
				xmlb.append("<user" + (userAlias != null ? " alias=\"" + XMLUtil.toSafeXMLString(userAlias) + "\"" : "") + " first=\"true\">").append(firstUser + "[" + firstUserRoles + "]").append("</user>");
			}
		}
		
		for(String user : userSet){
			if(!firstUsers.contains(user)){
				StringBuffer userRoles = new StringBuffer();
				
				Role[] userRoleArray = flow.queryUserNodeRoles(user, template.getId());
				for(Role userRole : userRoleArray){
					if(userRoles.length() > 0){
						userRoles.append(",");
					}
					userRoles.append(userRole.getName());
				}
				
				String userAlias = CynthiaUtil.getUserAlias(user);
				xmlb.append("<user" + (userAlias != null ? " alias=\"" + XMLUtil.toSafeXMLString(userAlias) + "\"" : "") + ">").append(user + "[" + userRoles + "]").append("</user>");
			}
		}
		
		xmlb.append("</users>");
	}
	
	xmlb.append("</root>");
	
	out.println(xmlb);
%>
