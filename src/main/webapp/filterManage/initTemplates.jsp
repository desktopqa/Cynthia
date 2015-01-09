<%@page import="com.sogou.qadev.service.cynthia.service.DataManager"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ConfigManager"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="java.util.*"%>

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
	
	String templateTypeIdStr = request.getParameter("templateTypeId");
	if(templateTypeIdStr == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.param_error));
		return;
	}
	
	UUID templateTypeId = DataAccessFactory.getInstance().createUUID(templateTypeIdStr);
	
	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	
	xmlb.append("<root>");
	
	xmlb.append("<isError>false</isError>");
	
	Set<Template> templateSet = new LinkedHashSet<Template>();
	
	if(ConfigManager.getProjectInvolved()){
		//项目管理
		templateSet.addAll(Arrays.asList(DataManager.getInstance().queryUserTemplates(templateTypeId, key.getUsername())));
	}else{
		List<Template> templateArray = das.queryTemplates(templateTypeId);
		for(Template template : templateArray)
		{
			Flow flow = das.queryFlow(template.getFlowId());
			if(flow == null)
				continue;
			
			if(flow.isRoleEditAction(Role.everyoneUUID) || flow.isRoleReadAction(Role.everyoneUUID))
			{
				templateSet.add(template);
				continue;
			}
			
			boolean isAdd = false;
			Action[] actionArray = flow.getActions();
			if(actionArray != null)
			{
				for(Action action : actionArray)
				{
					if(flow.isActionEveryoneRole(action.getId()))
					{
						isAdd = true;
						break;
					}
				}
			}
			
			if(isAdd)
			{
				templateSet.add(template);
				continue;
			}
			
			Role[] roleArray = flow.queryUserNodeRoles(key.getUsername(), template.getId());
			if(roleArray != null && roleArray.length > 0)
				templateSet.add(template);
		}
	}
	
	if(templateSet.size() == 0){
		xmlb.append("<templates/>");
	}else{
		xmlb.append("<templates>");
		
		for(Template template : templateSet)
		{
			xmlb.append("<template>");
			xmlb.append("<id>").append(template.getId()).append("</id>");
			xmlb.append("<name>").append(XMLUtil.toSafeXMLString(template.getName())).append("</name>");
			xmlb.append("</template>");
		}
		
		xmlb.append("</templates>");
	}
	
	xmlb.append("</root>");
		
	out.println(xmlb);
%>