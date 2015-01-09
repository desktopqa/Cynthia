<%@ page contentType="text/json;charset=utf-8"  %>
<%@page import="com.sogou.qadev.service.cynthia.service.FilterQueryManager"%>
<%@page import="com.sogou.qadev.service.cynthia.service.impl.DataFilterMemory"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.HashSet"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DbPoolConnection"%>
<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@page import="java.util.LinkedHashSet"%>
<%@page import="java.util.Set"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.QueryCondition"%>
<%@page import="com.sogou.qadev.service.cynthia.util.FilterDataAssembleUtil"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Data" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Stat" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template" %>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.CommonUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.EscapeUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="org.w3c.dom.Node"%>
<%@ page import="org.w3c.dom.Element"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.List"%>
<%@page import="java.net.URLDecoder"%>

<%
	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
	response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
	response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility    
	
	out.clear();
	
	request.setCharacterEncoding("utf-8");
	
	Key key = (Key)session.getAttribute("key");
	Long keyId = (Long)session.getAttribute("kid");
	String username = request.getParameter("username");
	
	if(keyId == null || keyId <= 0 || key == null){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	if(key!=null)
		username = key.getUsername();
	
	String filterXml = request.getParameter("searchConfig"); 
	String searchType = request.getParameter("searchType");
	String searchData = request.getParameter("searchData");
	String templateId = request.getParameter("templateId");
	
	filterXml = EscapeUtil.decodeAll(filterXml);  
	int start = request.getParameter("start")==null?0:Integer.parseInt(request.getParameter("start"));
	int limit = request.getParameter("limit")==null?50:Integer.parseInt(request.getParameter("limit"));
	
	int	pagenum        = (start/limit) + 1;
	int	count          = limit;
	
	String sort = request.getParameter("sort");
	String dir = request.getParameter("dir");
	
	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
	List<Data> dataList = new ArrayList<Data>();
	Set<String> notNewTaskIdSet = new HashSet<String>();
	
	List<QueryCondition> queryConditionList = new ArrayList<QueryCondition>();
	
	if(searchType != null && !searchType.equals("null") && searchType.length() > 0 && searchData != null && searchData.length() > 0){
		QueryCondition qc = null;
		if(searchType.equals("id"))
			qc = new QueryCondition(searchType , "=" , searchData);
		else
			qc = new QueryCondition(searchType , "like" , "'%" + searchData + "%'");
		queryConditionList.add(qc);
	}
	
	if(!CynthiaUtil.isNull(templateId)){
		queryConditionList.add(new QueryCondition("templateId" , "=" , templateId));
	}
	
	String sql = DataFilterMemory.getFilterSql(filterXml, null , queryConditionList);
	int totalCount = 0;
	
	totalCount = DbPoolConnection.getInstance().getSearchResultCount(sql);
	
	sql = DataFilterMemory.getQuerySql(sql, pagenum,count,sort,dir,null);
	
	dataList = new DataAccessSessionMySQL().queryDatas(sql, false, null);
	
	Set<String> displayFieldsName = new HashSet(Arrays.asList(new String[]{"标题","描述","创建时间","指派人","状态","修改时间","创建人"}));
	
	Map<String,String> userClassifyDataMap = new HashMap<String,String>();
	
	StringBuffer result = new StringBuffer();
	result.append("{");
	result.append("\"totalCount\":").append(totalCount).append(",\"rows\":[");
	
	result.append(FilterQueryManager.assembleFilterDataJson(displayFieldsName.toArray(new String[0]),dataList,notNewTaskIdSet,userClassifyDataMap , das , false));
	
	result.append("]");
	result.append("}");
	
	response.getWriter().write(result.toString());
%>