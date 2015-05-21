<%@page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.FilterAccessSessionMySQL"%>
<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UserInfo"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.JSTree"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="javax.xml.parsers.*"%>

<%!
String getFolderXMLString(int nodeId,DataAccessSession das,String userName , Map<String,String> filterIdNameMap)
{
	StringBuffer xml = new StringBuffer();
	List<JSTree> children = das.queryChilderNodes(nodeId, userName);
	JSTree localNode = das.queryJSTreeNodeById(nodeId);
	xml.append("<folder>");
	xml.append("<id>");
	xml.append(localNode.getId());
	xml.append("</id>");
	xml.append("<name>");
	xml.append(XMLUtil.toSafeXMLString(localNode.getTitle()));
	xml.append("</name>");
	if(children!=null&&children.size()>0)
	{
		for(int i=0;i<children.size();i++)
		{
			xml.append(getFolderXMLString(children.get(i).getId(),das,userName , filterIdNameMap));
		}
	}
	String filtersStr = localNode.getFilters();
	if(filtersStr == null||"".equals(filtersStr)){
		xml.append("<filters></filters>");
	}else{
		String[] filters = filtersStr.split(",");
		xml.append("<filters>");
		for(String filterIdStr : filters)
		{
			if(filterIdStr!=null&&!"".equals(filterIdStr))
			{
				if(filterIdNameMap.get(filterIdStr) == null)
					continue;
				xml.append("<filter>");
				xml.append("<id>");
				xml.append(filterIdStr);
				xml.append("</id>");
				xml.append("<name>");
				xml.append(XMLUtil.toSafeXMLString(filterIdNameMap.get(filterIdStr)));
				xml.append("</name>");
// 				xml.append("<datas>(0/0)");
				xml.append("<datas>");
				xml.append("</datas>");
				xml.append("</filter>");
			}
		}
		xml.append("</filters>");
	}
	xml.append("</folder>");
	
	return xml.toString();
}
%>

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
	int rootId = 6;
	Timestamp start = new Timestamp(System.currentTimeMillis());
	List<JSTree> rootChildren = das.queryChilderNodes(rootId, key.getUsername());
	List<String> allFolderFilters = das.queryAllFolderFilters(key.getUsername());
	Filter[] userFocusFilters = das.queryFocusFilters(key.getUsername());
	List<String> defaultFilters = das.queryDefaultFilters(key.getUsername());
	
	List<Filter> folderFilters = new ArrayList<Filter>();
	List<UUID> filterIdList = new ArrayList<UUID>();
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
	filterIdList.addAll(ConfigUtil.getAllSysFilters());
	
	UUID sysFilterId = DataAccessFactory.getInstance().createUUID("119695");
	UUID sysFilterId1 = DataAccessFactory.getInstance().createUUID("119891");
	UUID sysFilterId2 = DataAccessFactory.getInstance().createUUID("119892");
	UUID sysFilterId3 = DataAccessFactory.getInstance().createUUID("119893");

	UserInfo userInfo = das.queryUserInfoByUserName(key.getUsername());
	
	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	
	xmlb.append("<root>");
	xmlb.append("<isError>false</isError>");
	
	Map<String,String> filterIdNameMap = das.queryFilterIdNameMap(key.getUsername());
	
	if(rootChildren == null||rootChildren.size()==0)
	{
		xmlb.append("<folders></folders>");
	}else
	{
		xmlb.append("<folders>");
		for(JSTree treeNode : rootChildren)
		{
			xmlb.append(getFolderXMLString(treeNode.getId(),das,key.getUsername() , filterIdNameMap));
		}
		xmlb.append("</folders>");
	}
	if(folderFilters == null||folderFilters.size()==0)
	{
		xmlb.append("<defaultfilters></defaultfilters>");
	}else
	{
		xmlb.append("<defaultfilters>");
		for(Filter filter : folderFilters)
		{
			xmlb.append("<filter>");
			xmlb.append("<id>");
			xmlb.append(filter.getId());
			xmlb.append("</id>");
			xmlb.append("<name>");
			xmlb.append(XMLUtil.toSafeXMLString(filter.getName()));
			xmlb.append("</name>");
			xmlb.append("<datas>");
			xmlb.append("</datas>");
			xmlb.append("</filter>");
		}
		xmlb.append("</defaultfilters>");
	}
	
	String[] favorites = das.queryFavoriteFilters(key.getUsername());
	List<UUID> favoritesList = new ArrayList<UUID>();
	
	if(favorites!=null)
	{
		for(String filterIdStr : favorites)
		{
			UUID filterId = DataAccessFactory.getInstance().createUUID(filterIdStr);
			if(filterId != null)
				favoritesList.add(filterId);
		}
	}
	//如果没有常用过滤器，则添加系统过滤器
	if(favoritesList.size() == 0){
		favoritesList.add(sysFilterId);
		favoritesList.add(sysFilterId1);
		favoritesList.add(sysFilterId2);
		favoritesList.add(sysFilterId3);
	}
	
	
	xmlb.append("<favorites>");
	for(UUID filterId : favoritesList)
	{
		if(filterId!=null)
		{
			Filter filter = das.queryFilter(filterId);
			if(filter!=null)
			{
				xmlb.append("<filter>");
				xmlb.append("<id>");
				xmlb.append(filter.getId());
				xmlb.append("</id>");
				xmlb.append("<name>");
				xmlb.append(XMLUtil.toSafeXMLString(filter.getName()));
				xmlb.append("</name>");
				xmlb.append("<datas>");
				xmlb.append("</datas>");
				xmlb.append("</filter>");
			}else
			{
				das.removeFavoriteFilter(key.getUsername(),filterId.toString());
			}
		}

	}
	xmlb.append("</favorites>");
	
	xmlb.append("<username>");
	
	System.out.println(key.getUsername());
	
	xmlb.append(CynthiaUtil.getUserAlias(key.getUsername()));
	xmlb.append("</username>");
	xmlb.append("</root>");
		
	out.println(xmlb.toString());
%>