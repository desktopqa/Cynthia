<%@page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ProjectInvolveManager"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ConfigManager"%>
<%@page import="com.sogou.qadev.service.cynthia.service.impl.DataFilterMemory"%>
<%@page import="com.sogou.qadev.service.cynthia.service.TableRuleManager"%>
<%@page import="java.util.LinkedHashSet"%>
<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.util.FileUtil"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.util.Map"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.QueryCondition"%>
<%@page import="com.sogou.qadev.service.cynthia.util.FilterDataAssembleUtil"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.HashSet"%>
<%@page import="java.util.Set"%>
<%@page import="com.sogou.qadev.service.cynthia.service.FilterQueryManager"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Attachment"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Option"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Field.DataType"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Field.Type"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DbPoolConnection"%>
<%@ page contentType="text/json;charset=utf-8"  %>

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

	String searchKey = URLDecoder.decode(request.getParameter("searchKey"),"UTF-8");
	String filterIdStr = request.getParameter("filterId");
	String searchType = request.getParameter("searchType");
	String templateIdStr = request.getParameter("template");

	int start = Integer.parseInt(request.getParameter("start"));
	int limit = Integer.parseInt(request.getParameter("limit"));
	String sort = request.getParameter("sort");
	String dir = request.getParameter("dir");
	
	int	pagenum        = (start/limit) + 1;
	int	count          = limit;

	if(searchType==null||"".equals(searchType))
	{
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}

	Set<String> displayFieldsName = new HashSet<String>();
	int totalCount = 0;
	List<Data> dataList = new ArrayList<Data>();
	Set<String> notNewTaskIdSet = new HashSet<String>();
	Map<String,String> userClassifyDataMap = new HashMap<String,String>();
	boolean isSysFilter = true;
	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(username,keyId);
	
	if(filterIdStr == null || filterIdStr.length() == 0){
		displayFieldsName.addAll(Arrays.asList(new String[]{"标题","描述","创建时间","指派人","状态","修改时间","创建人"}));
		//没设置过滤器，则全局查询
		List<String> dataTable = TableRuleManager.getInstance().getAllDataTables();
		StringBuffer sqlBuffer = new StringBuffer();
		StringBuffer whereBuffer = new StringBuffer();
		if(searchKey != null && searchKey.length() >0 ){
			if(searchType.equals("id")){
				whereBuffer.append(" where ").append(searchType).append(" in (").append(searchKey).append(")");
			}else{
				whereBuffer.append(" where ").append(searchType).append(" like '%").append(searchKey).append("%'");
			}
			
			if(ConfigManager.getProjectInvolved()){
				Set<String> allCompanyUser = ProjectInvolveManager.getInstance().getCompanyUserMails(key.getUsername());
				StringBuffer userBuffer = new StringBuffer();
				if(allCompanyUser.size() > 0){
					for(String user : allCompanyUser){
						userBuffer.append(userBuffer.length() > 0 ? "," : "").append("'").append(user).append("'");
					}
					whereBuffer.append(" and createUser in (").append(userBuffer.toString()).append(") ");
				}
			}
		}
		
		for(String tableName : dataTable){
			sqlBuffer.append(sqlBuffer.length() > 0 ? " union " :"").append(" select id , templateId,title,description,createTime, assignUser,statusId,lastModifyTime,createUser from ").append(tableName)
			.append(whereBuffer);
		}
		String orderField = DataFilterMemory.getDbColName(sort, null);
		if(orderField != null && orderField.length() > 0)
			sqlBuffer.append(" order by ").append(DataFilterMemory.getDbColName(sort, null)).append( " " + dir);
		
		List<Data> allData = new DataAccessSessionMySQL().queryDatas(sqlBuffer.toString(), false, null);
		
		totalCount = allData.size();
		if(totalCount > 0){
			int endIndex = start + limit;
			if(endIndex > allData.size())
				endIndex = allData.size();
			dataList = allData.subList(start, endIndex);
		}
		
	}else{
		UUID filterId = DataAccessFactory.getInstance().createUUID(filterIdStr);
		UUID templateTypeId =null;
		UUID templateId = null;
		
		Filter filter = das.queryFilter(filterId);

		if(filter == null)
			return;
		
		displayFieldsName.addAll(Arrays.asList(FilterQueryManager.getDisplayFields(filter.getXml(), das)));
		isSysFilter = FilterQueryManager.isSysFilter(filterIdStr);

		if(!isSysFilter){
			templateId = FilterQueryManager.getFilterTemplateId(filter);
		}

		List<QueryCondition> queryConList = new ArrayList<QueryCondition>();
		QueryCondition queryCon = new QueryCondition(searchType,"like", "'%" +searchKey + "%'");
		queryConList.add(queryCon);

		if (isSysFilter) {
			FilterQueryManager.initFilterEnv(filter,keyId, key.getUsername(), null, das);
		}

		String sql = DataFilterMemory.getFilterSql(filter.getXml(), null , queryConList);

		sql = CynthiaUtil.cancelGroupOrder(sql);
		
		totalCount = DbPoolConnection.getInstance().getSearchResultCount(sql);

		sql = DataFilterMemory.getQuerySql(sql, pagenum,count,sort,dir,templateId == null ? null : templateId.getValue());
		dataList = new DataAccessSessionMySQL().queryDatas(sql, false, templateId);

	}
	
	userClassifyDataMap = das.getUserClassifyDataMap(username);
	StringBuffer result = new StringBuffer();
	result.append("{");
	result.append("\"totalCount\":\"").append(totalCount).append("\",\"rows\":[");

	result.append(FilterQueryManager.assembleFilterDataJson(displayFieldsName.toArray(new String[0]),dataList,notNewTaskIdSet,userClassifyDataMap , das , isSysFilter));

	result.append("]");
	result.append("}");

	out.print(result.toString());
%>
