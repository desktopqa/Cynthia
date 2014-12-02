<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Data"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="java.util.*"%>

<%@ include file="initMain.function.jsp"%>

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

	String templateIdStr = request.getParameter("templateId");
	if(templateIdStr == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.param_error));
		return;
	}

	UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
	Template template = das.queryTemplate(templateId);
	if(template == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.template_not_found));
		return;
	}
	Flow flow = das.queryFlow(template.getFlowId());
	String[] relatedUsers  =flow.queryNodeUsers(template.getId());

	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

	xmlb.append("<root>");

	xmlb.append("<isError>false</isError>");

	Map<String,String> fields = getTemplateFields(das,template);
	if(fields.size() == 0)
		xmlb.append("<fields/>");
	else
	{
		xmlb.append("<fields>");

		for(String fieldId : fields.keySet())
		{
			if(fields.get(fieldId)!=null&&fields.get(fieldId).indexOf("废弃")>=0)
				continue;
			xmlb.append("<field>");
			xmlb.append("<id>").append(fieldId).append("</id>");
			xmlb.append("<name>").append(XMLUtil.toSafeXMLString(fields.get(fieldId))).append("</name>");
			xmlb.append("</field>");
		}

		xmlb.append("</fields>");
	}
	if(relatedUsers == null||relatedUsers.length==0)
	{
		xmlb.append("<users/>");
	}else
	{
		xmlb.append("<users>");
		for(String user : relatedUsers)
		{
			xmlb.append("<user>");
			xmlb.append(user);
			xmlb.append("</user>");
		}
		xmlb.append("</users>");
	}

	String orderContent = XMLUtil.toSafeXMLString(queryOrderContentHTML(das,templateId, null, 0));
	xmlb.append("<order>");
	xmlb.append(orderContent);
	xmlb.append("</order>");
	xmlb.append("</root>");

	out.println(xmlb);
%>