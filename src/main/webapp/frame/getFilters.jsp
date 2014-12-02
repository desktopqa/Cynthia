<%@ page language="java" contentType="text/xml; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="java.util.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.JSTree"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="java.util.*"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="javax.xml.parsers.*"%>

<%!boolean isEditable(Filter filter,String userName)
{
	boolean result = true;
	if(filter == null)
	{
		result = false;
	}else
	{
		if(!filter.getCreateUser().equals(userName))
		{
			result = false;
		}else
		{
			//XMLUtil.string2Document(filter.getXml(), "UTF-8");
			try{
				Document xmlDoc = XMLUtil.string2Document(filter.getXml(), "UTF-8");
				List<Node>	tempNodeList = XMLUtil.getNodes( xmlDoc, "/query/template" );
        		if(tempNodeList == null||tempNodeList.size()!=1)
        		{
        			result = false;
        		}else
        		{
        			result = true;
        		}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
	}
	
	return result;
}%>

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

String category = request.getParameter("category");
String keyword = request.getParameter("keyword");
int start = Integer.parseInt(request.getParameter("start"));
int limit = Integer.parseInt( request.getParameter("limit"));

DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);

UUID[] focusFilterIdArray = das.queryUserFocusFilters(key.getUsername());
Set<UUID> focusFilterIdSet = new HashSet<UUID>(Arrays.asList(focusFilterIdArray));

List<Filter> filterList = new ArrayList<Filter>();

Filter[] filterArray = das.queryFocusFilters(key.getUsername());


for(Filter filter : filterArray){
	if((category == null&&keyword==null)
	||(category.equals("all")&&(filter.getName().contains(keyword)||filter.getCreateUser().contains(keyword)||filter.getCreateTime().toString().split("\\.")[0].contains(keyword)||(filter.isPublic() && keyword.equals("是") || !filter.isPublic() && keyword.equals("否"))||(focusFilterIdSet.contains(filter.getId()) && keyword.equals("是") || !focusFilterIdSet.contains(filter.getId()) && keyword.equals("否"))))
	|| category.equals("name") && filter.getName().contains(keyword)
	|| category.equals("user") && filter.getCreateUser().contains(keyword)
	|| category.equals("time") && filter.getCreateTime().toString().split("\\.")[0].contains(keyword)
	|| category.equals("public") && (filter.isPublic() && keyword.equals("是") || !filter.isPublic() && keyword.equals("否"))
	|| category.equals("focus") && (focusFilterIdSet.contains(filter.getId()) && keyword.equals("是") || !focusFilterIdSet.contains(filter.getId()) && keyword.equals("否"))){
		filterList.add(filter);
	}
}

StringBuffer xml = new StringBuffer();
xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
xml.append("<root>");
xml.append("<totalCount>").append(filterList.size()).append("</totalCount>");

for(int i = start; i < start + limit && i < filterList.size(); i++){
	xml.append("<filter>");
	xml.append("<id>").append(filterList.get(i).getId()).append("</id>");
	xml.append("<name>").append(XMLUtil.toSafeXMLString(filterList.get(i).getName())).append("</name>");
	xml.append("<createUser>").append(XMLUtil.toSafeXMLString(filterList.get(i).getCreateUser())).append("</createUser>");
	xml.append("<createTime>").append(filterList.get(i).getCreateTime().toString().split("\\.")[0]).append("</createTime>");
	xml.append("<isPublic>").append(filterList.get(i).isPublic()).append("</isPublic>");
	xml.append("<isFocus>").append(focusFilterIdSet.contains(filterList.get(i).getId()) ? true : false).append("</isFocus>");
	xml.append("<isChild>").append(filterList.get(i).getFatherId() != null ? true : false).append("</isChild>");
	xml.append("<isVisible>").append(filterList.get(i).isVisible()).append("</isVisible>");
	xml.append("<isEditable>").append(isEditable(filterList.get(i),key.getUsername())).append("</isEditable>");
	xml.append("</filter>");
}

xml.append("</root>");

out.clear();
out.println(xml);
%>
