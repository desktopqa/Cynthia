<%@page import="java.util.Map"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Data"%>
<%@page import="com.sogou.qadev.service.cynthia.service.FilterQueryManager"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@page import="com.sogou.qadev.cache.impl.TemplateCache"%>
<%@page import="com.sogou.qadev.service.cynthia.util.FilterDataAssembleUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.util.FileUtil"%>
<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil" %>
<%@ page import="com.sogou.qadev.service.cynthia.util.URLUtil" %>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession" %>
<%@ page import="com.sogou.qadev.service.cynthia.util.CommonUtil" %>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="javax.xml.parsers.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>

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
	
	String filterIdStr = request.getParameter("filterId");
	String startStr    = request.getParameter("start");
	String limitStr    = request.getParameter("limit");
	String sort        = request.getParameter("sort");
	String dir         = request.getParameter("dir");
	
	int start          = startStr == null?0:Integer.parseInt(startStr);
	int limit          = limitStr == null?50:Integer.parseInt(limitStr);
	int	pagenum        = (start/limit) + 1;
	int	count          = limit;
	
	String username    = key.getUsername();
	
	if(filterIdStr == null)
		return;
	
	UUID filterId = DataAccessFactory.getInstance().createUUID(filterIdStr);
	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(username,keyId);
	
	Filter filter = das.queryFilter(filterId);
	if(filter == null)
		return;
	
	StringBuffer result = new StringBuffer();
	
	try
	{
		Document filterDocument = XMLUtil.string2Document(filter.getXml(),"UTF-8");
		Node templateTypeNode   = XMLUtil.getSingleNode(filterDocument,"query/templateType");
		UUID templateTypeId     = null;
		UUID templateId         = null;
		
		List<Data> dataList= new ArrayList<Data>();
		HashSet<String>	notNewTaskIdSet	= new HashSet<String>();
		
		int totalTaskAccount = 0;	
		
		boolean isSysFilter = FilterQueryManager.isSysFilter(filterIdStr);
			
		if(!isSysFilter){
			Node templateNode = XMLUtil.getSingleNode(filterDocument, "query/template");
			templateId = DataAccessFactory.getInstance().createUUID(XMLUtil.getAttribute(templateNode, "id"));
		}
		
		dataList = FilterQueryManager.queryDataList(das,filter,username,keyId,sort,dir,pagenum,count,null);  //查询数据
		
		UUID[]	filterIdArray = new UUID[]{filterId};
		String xmlString = das.getNewTaskIdsByFilterAndUser(filterIdArray, username);
		
		Document xmlDoc	= XMLUtil.string2Document(xmlString, "UTF-8");
		Node filterNode = XMLUtil.getNodes(xmlDoc,"filters/filter").get(0);
		String oldIdStrs = XMLUtil.getSingleNodeTextContent(filterNode,"oldTasks");
		totalTaskAccount += Integer.parseInt(XMLUtil.getAttribute(filterNode, "totalAccount"));
		
		if(oldIdStrs != null){
			String[] oldIdStrArray = oldIdStrs.split(",");
			notNewTaskIdSet.addAll(Arrays.asList(oldIdStrArray));
		}
		
		//开始拼装数据
		//获得要显示的字段
		String[] displayFieldsName = FilterQueryManager.getDisplayNamesFilter(filter.getXml(), das);
		
		Map<String,String> userClassifyDataMap = das.getUserClassifyDataMap(username);
		
		result.append("{").append("\"totalCount\":\"").append(totalTaskAccount).append("\"")
			  .append(",\"newCount\":\"").append(totalTaskAccount - notNewTaskIdSet.size()).append("\"")
			  .append(",\"rows\":[");
		
		result.append(FilterQueryManager.assembleFilterDataJson(displayFieldsName,dataList,notNewTaskIdSet,userClassifyDataMap , das , isSysFilter));
		result.append("]").append("}");
		
	}catch(Exception e)
	{
		result.append("{").append("\"isError\":\"true\"}");
	}
	
	out.print(result.toString());
%>
