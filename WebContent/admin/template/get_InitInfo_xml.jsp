<%@page import="com.sogou.qadev.service.cynthia.bean.UserInfo"%>
<%@page import="java.util.Map"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.FlowAccessSessionMySQL"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.TemplateType"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.login.bean.Key"%>
<%@ page import="com.sohu.rd.td.util.xml.XMLUtil"%>
<%@ page import="java.util.TreeMap"%>
<%@ page import="java.util.Arrays"%>

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
	
	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	xmlb.append("<root>");
	xmlb.append("<isError>false</isError>");
	
	UserInfo userInfo = das.queryUserInfoByUserName(key.getUsername());
	if(userInfo != null)
	{
		xmlb.append("<userRole>"+userInfo.getUserRole()+"</userRole>");
	}
	
	//set templates
	TreeMap<String, Template> templateMap = new TreeMap<String, Template>();
	
	Template[] templateArray = das.queryAllTemplates();
	if(templateArray != null)
	{
		for(Template template : templateArray)
		{
			if (null == template.getName()){
				continue;
			}
			templateMap.put(template.getId().getValue(), template);
		}
	}
	
	if(templateMap.size() == 0)
		xmlb.append("<templates/>");
	else
	{
		xmlb.append("<templates>");
		
		for(Template template : templateMap.values())
		{
			xmlb.append("<template>");
			
			xmlb.append("<id>").append(template.getId()).append("</id>");
			xmlb.append("<name>").append(XMLUtil.toSafeXMLString(template.getName())).append("</name>");
			xmlb.append("<templateTypeId>").append(template.getTemplateTypeId()).append("</templateTypeId>");
			xmlb.append("<flowId>").append(template.getFlowId()).append("</flowId>");
			xmlb.append("<isFocused>").append("true").append("</isFocused>");
			xmlb.append("<isNew>").append("true").append("</isNew>");
			
			xmlb.append("</template>");
		}
		
		xmlb.append("</templates>");
	}
	
	templateArray = null;
	templateMap = null;
	//set templateTypes
	TemplateType[] templateTypeArray = das.queryAllTemplateTypes();
	if(templateTypeArray == null || templateTypeArray.length == 0)
		xmlb.append("<templateTypes/>");
	else
	{
		xmlb.append("<templateTypes>");
		
		for(TemplateType templateType : templateTypeArray)
		{
			xmlb.append("<templateType>");
			
			xmlb.append("<id>").append(templateType.getId()).append("</id>");
			xmlb.append("<name>").append(XMLUtil.toSafeXMLString(templateType.getName())).append("</name>");
			
			xmlb.append("</templateType>");
		}
		
		xmlb.append("</templateTypes>");
	}
	
	templateTypeArray = null;
	
	//set flows
	Map<String,String> flowMap = new FlowAccessSessionMySQL().queryIdName();
	
	if(flowMap.size() == 0)
		xmlb.append("<flows/>");
	else
	{
		xmlb.append("<flows>");
		
		for(String flowId : flowMap.keySet())
		{
			xmlb.append("<flow>");
			
			xmlb.append("<id>").append(flowId).append("</id>");
			xmlb.append("<name>").append(XMLUtil.toSafeXMLString(flowMap.get(flowId))).append("</name>");
			
			xmlb.append("</flow>");
		}
		
		xmlb.append("</flows>");
	}
	
	xmlb.append("</root>");
		
	out.println(xmlb.toString());
%>