<%@page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.FilterAccessSessionMySQL"%>
<%@page import="java.sql.Timestamp"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.JSTree"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="java.util.*"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="javax.xml.parsers.*"%>
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
	
	List<UUID> filterIdList = new ArrayList<UUID>();
	
	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
	int rootId = 6;
	List<JSTree> rootChildren = das.queryChilderNodes(rootId, key.getUsername());
	List<String> allFolderFilters = das.queryAllFolderFilters(key.getUsername());
	Filter[] userFocusFilters = das.queryFocusFilters(key.getUsername());
	List<String> defaultFilters = das.queryDefaultFilters(key.getUsername());
	
	List<Filter> folderFilters = new ArrayList<Filter>();
	UUID sysFilterId = DataAccessFactory.getInstance().createUUID("119695");
	UUID sysFilterId1 = DataAccessFactory.getInstance().createUUID("119891");
	UUID sysFilterId2 = DataAccessFactory.getInstance().createUUID("119892");
	UUID sysFilterId3 = DataAccessFactory.getInstance().createUUID("119893");
	
	filterIdList.add(sysFilterId);
	filterIdList.add(sysFilterId1);
	filterIdList.add(sysFilterId2);
	filterIdList.add(sysFilterId3);
	
	for(String filterIdStr : defaultFilters)
	{
		for(Filter filter : userFocusFilters)
		{
			if(filter.getId().toString().equals(filterIdStr))
			{
				folderFilters.add(filter);
				break;
			}
		}
	}
	
	for(Filter filter : userFocusFilters)
	{
		filterIdList.add(filter.getId());
		if(!allFolderFilters.contains(filter.getId().toString())&&!defaultFilters.contains(filter.getId().toString()))
		{
			folderFilters.add(filter);
		}
	}
	
	String resultXml = das.getNewTaskIdsByFilterAndUser(filterIdList.toArray(new UUID[filterIdList.size()]), key.getUsername());

	Document filterXml = XMLUtil.string2Document(resultXml,"UTF-8");
	List<Node> filtersNode = XMLUtil.getNodes(filterXml,"filters/filter");
	Map<String,String> filterDatas = new HashMap<String,String>();
	for(Node node : filtersNode)
	{
		//只显示总数
		String tempString = "("+XMLUtil.getAttribute(node,"totalAccount")+")";
		filterDatas.put(XMLUtil.getAttribute(node,"id"),tempString);
	}

	StringBuffer xmlb = new StringBuffer();
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	xmlb.append("<root>");
	xmlb.append("<filters>");
	
	Map<String,String> filterIdNameMap = das.queryFilterIdNameMap(key.getUsername());
	
	for(String filterId : filterDatas.keySet()){
		if(filterIdNameMap.get(filterId) == null)
			continue;
	 	xmlb.append("<filter>");
	 	xmlb.append("<id>#").append(filterId).append("</id><datas>");
	 	xmlb.append(filterDatas.get(filterId));
	 	xmlb.append("</datas>");
	 	xmlb.append("<name>").append(XMLUtil.toSafeXMLString(filterIdNameMap.get(filterId))).append("</name>");
	 	xmlb.append("</filter>");
	}
	xmlb.append("</filters>");
	xmlb.append("</root>");
	
	out.println(xmlb.toString());
%>