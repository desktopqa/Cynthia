<%@page import="com.sogou.qadev.service.cynthia.service.ProjectInvolveManager"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ConfigManager"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashSet"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UserInfo"%>
<%@page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.LinkedHashMap"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Stat"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="java.util.TreeMap"%>

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
	
	boolean initFlow = false;
	if(request.getParameter("initFlow") != null)
		initFlow = Boolean.parseBoolean(request.getParameter("initFlow"));
	
	boolean initNode = false;
	if(request.getParameter("initNode") != null)
		initNode = Boolean.parseBoolean(request.getParameter("initNode"));
	
	boolean initStat = false;
	if(request.getParameter("initStat") != null)
		initStat = Boolean.parseBoolean(request.getParameter("initStat"));
	
	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	xmlb.append("<root>");
	xmlb.append("<isError>false</isError>");
	
	UserInfo userInfo = das.queryUserInfoByUserName(key.getUsername());
	if(userInfo != null)
	{
		xmlb.append("<userRole>"+userInfo.getUserRole()+"</userRole>");
	}
	
	if(initFlow)
	{
		Flow[] flowArray = null;
		flowArray = das.queryAllFlows(key.getUsername());
		
		if(flowArray.length == 0)
			xmlb.append("<flows/>");
		else
		{
			xmlb.append("<flows>");
			
			for(Flow flow : flowArray)
			{
				xmlb.append("<flow>");
				xmlb.append("<id>").append(flow.getId()).append("</id>");
				xmlb.append("<name>").append(XMLUtil.toSafeXMLString(flow.getName())).append("</name>");
				xmlb.append("<isProFlow>").append(XMLUtil.toSafeXMLString(String.valueOf(flow.isProFlow()))).append("</isProFlow>");
				xmlb.append("</flow>");
			}
			
			xmlb.append("</flows>");
		}
		
		if(initStat)
		{
			for(Flow flow : flowArray)
			{
				TreeMap<String, Stat> statMap = new TreeMap<String, Stat>();
				
				Stat[] statArray = flow.getStats();
				if(statArray != null)
				{
					for(Stat stat : statArray)
					{
						if (null == stat.getName()){
							continue;
						}
						statMap.put(stat.getName(), stat);
					}
				}
				
				if(statMap.size() == 0)
					xmlb.append("<stats/>");
				else
				{
					xmlb.append("<stats>");
					
					for(Stat stat : statMap.values())
					{
						xmlb.append("<stat>");
						xmlb.append("<id>").append(stat.getId()).append("</id>");
						xmlb.append("<name>").append(XMLUtil.toSafeXMLString(stat.getName())).append("</name>");
						xmlb.append("<flowId>").append(flow.getId()).append("</flowId>");
						xmlb.append("</stat>");
					}
					
					xmlb.append("</stats>");
				}
			}
		}
	}
	
	xmlb.append("</root>");
		
	out.println(xmlb.toString());
%>