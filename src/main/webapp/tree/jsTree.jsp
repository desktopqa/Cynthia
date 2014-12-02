<%@page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Timer"%>
<%@ page language="java" contentType="application/x-json; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="java.util.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.JSTree"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
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

String operation = request.getParameter("operation");
String nodeIdStr = request.getParameter("id");

if(operation==null||(!"updateFavorites".equals(operation)&&nodeIdStr==null))
{
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}

DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
String success = "{\"status\":1}";
String error = "{\"status\":0}";

if("get_children".equals(operation)){
	
	if(nodeIdStr.indexOf("filter")>=0)
		return;
	
	List<JSTree> list = null;
	JSTree localNode = null;
	List<Filter> folderFilters = null;
	Filter[] userFocusFilters = null;
	List<Filter> userFocusFiltersList = null;
	
	//经常访问
	if(nodeIdStr.indexOf("favorite")>=0)
	{
		String[] favoriteFilters = das.queryFavoriteFilters(key.getUsername());
		StringBuffer favJson = new StringBuffer();
		boolean flag = true;
		favJson.append("[");
		for(int i=0;i<favoriteFilters.length;i++)
		{
			Filter tempFilter = das.queryFilter(DataAccessFactory.getInstance().createUUID(favoriteFilters[i]));
			if(tempFilter!=null)
			{
				if(flag)
				{
					if(tempFilter.getCreateUser().equals(key.getUsername()))
					{
						favJson.append("{\"attr\":{\"id\":\"node_filter_"+tempFilter.getId()+"\",\"rel\":\"default\"},\"data\":\""+tempFilter.getName()+"\",\"state\":\"opened\"}");
					}else
					{
						favJson.append("{\"attr\":{\"id\":\"node_filter_"+tempFilter.getId()+"\",\"rel\":\"others\"},\"data\":\""+tempFilter.getName()+"\",\"state\":\"opened\"}");
					}
					
					flag = false;
				}else
				{
					if(tempFilter.getCreateUser().equals(key.getUsername()))
					{
						favJson.append(",{\"attr\":{\"id\":\"node_filter_"+tempFilter.getId()+"\",\"rel\":\"default\"},\"data\":\""+tempFilter.getName()+"\",\"state\":\"opened\"}");
					}else
					{
						favJson.append(",{\"attr\":{\"id\":\"node_filter_"+tempFilter.getId()+"\",\"rel\":\"others\"},\"data\":\""+tempFilter.getName()+"\",\"state\":\"opened\"}");
					}
				}
			}
		}
		favJson.append("]");
		out.println(favJson.toString());
		return;
	}
	
	int nodeId = Integer.parseInt(nodeIdStr);
	list = das.queryChilderNodes(nodeId,key.getUsername());  //子目录
	localNode = das.queryJSTreeNodeById(nodeId); //当前目录
	folderFilters = new ArrayList<Filter>();
	userFocusFilters = das.queryFocusFilters(key.getUsername());
	userFocusFiltersList = Arrays.asList(userFocusFilters);
	if(nodeId==1)
	{
		list = das.queryRootNode(nodeId);
		JSTree rootNode = list.get(0);
		StringBuffer rootJson = new StringBuffer();
		rootJson.append("[");
		rootJson.append("{\"attr\":{\"id\":\"node_"+rootNode.getId()+"\",\"rel\":\"folder\"},\"data\":\""+rootNode.getTitle()+"\",\"state\":\"closed\"}");
		rootJson.append(",{\"attr\":{\"id\":\"node_favorite\",\"rel\":\"folder\"},\"data\":\"最常访问\",\"state\":\"closed\"}");
		rootJson.append("]");
		out.println(rootJson.toString());
		return;
	}else if(localNode.getParentId()==1)
	{
		List<String> allFolderFilters = das.queryAllFolderFilters(key.getUsername());
		List<String> defaultFilters = das.queryDefaultFilters(key.getUsername());
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
		
		for(Filter filter:userFocusFilters)
		{
			if(!allFolderFilters.contains(filter.getId().toString())&&!defaultFilters.contains(filter.getId().toString()))
			{
				folderFilters.add(filter);
			}
		}
		
	}else
	{
		List<String> folderFiltersStr = das.queryFolderFilters(nodeId);
		
		for(String filterIdStr : folderFiltersStr)
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
		
	}
	
	int count = list.size()+folderFilters.size();
	int i = 0;
	StringBuffer json = new StringBuffer();
	json.append("[");
	for(JSTree treeNode:list)
	{
		if(i==0)
		{
			json.append("{\"attr\":{\"id\":\"node_"+treeNode.getId()+"\",\"rel\":\"folder\"},\"data\":\""+treeNode.getTitle()+"\",\"state\":\"closed\"}");
		}else
		{
			json.append(",{\"attr\":{\"id\":\"node_"+treeNode.getId()+"\",\"rel\":\"folder\"},\"data\":\""+treeNode.getTitle()+"\",\"state\":\"closed\"}");
		}
		i++;
	}
	for(Filter folderFilter : folderFilters)
	{
		if(i==0)
		{	
			if(folderFilter.getCreateUser().equals(key.getUsername()))
			{
				json.append("{\"attr\":{\"id\":\"node_filter_"+folderFilter.getId()+"\",\"rel\":\"default\"},\"data\":\""+folderFilter.getName()+"\",\"state\":\"opened\"}");
			}else
			{
				json.append("{\"attr\":{\"id\":\"node_filter_"+folderFilter.getId()+"\",\"rel\":\"others\"},\"data\":\""+folderFilter.getName()+"\",\"state\":\"opened\"}");
			}
		}else
		{
			if(folderFilter.getCreateUser().equals(key.getUsername()))
			{
				json.append(",{\"attr\":{\"id\":\"node_filter_"+folderFilter.getId()+"\",\"rel\":\"default\"},\"data\":\""+folderFilter.getName()+"\",\"state\":\"opened\"}");
			}else
			{
				json.append(",{\"attr\":{\"id\":\"node_filter_"+folderFilter.getId()+"\",\"rel\":\"others\"},\"data\":\""+folderFilter.getName()+"\",\"state\":\"opened\"}");
			}		
		}
		i++;
	}
	
	json.append("]");
	out.println(json.toString());
	
}else if("create_node".equals(operation))
{
	String positionStr = request.getParameter("position");
	String title  = request.getParameter("title");
	String type = request.getParameter("type");
	
	if(nodeIdStr == null||"".equals(nodeIdStr))
	{
		out.println("{\"status\":0,\"msg\":\"ID不合法,请刷新后尝试！\"}");
		return;
	}
	//filter以及最长访问下不允许新建目录
	if(nodeIdStr.indexOf("filter")>=0||nodeIdStr.indexOf("favorite")>=0)
	{
		out.println("{\"status\":0,\"msg\":\"该项目下无法新建目录\"}");
		return;
	}
	if("default".equals(type))
	{   //表示新建过滤器
		Filter filter = null;
		filter = das.createFilter(key.getUsername(), new Timestamp(System.currentTimeMillis()), null);
		filter.setName(title);
		filter.setXml("");
		filter = das.addFilter(filter);//das.updateFilter(filter);
		
		if(filter!=null)
		{
			if(!"6".equals(nodeIdStr))
			{
				das.addFilterToFolder(filter.getId().toString(),Integer.parseInt(nodeIdStr));
			}
			das.addUserFocusFilter(key.getUsername(), filter.getId());
			out.println("{\"status\":1,\"id\":"+filter.getId()+"}");
		}else
		{
			out.println("{\"status\":0,\"id\":0}");
		}
		
	}else if(positionStr!=null&&!"".equals(positionStr))
	{
		int position = Integer.parseInt(positionStr);
		int parentId = Integer.parseInt(nodeIdStr);
		int result = das.addJSTreeNode(parentId,position,title,key.getUsername());
		if(result>0){
			out.println("{\"status\":1,\"id\":"+result+"}");
		}else{
			out.println("{\"status\":0,\"id\":0}");
		}
		
	}else
	{
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;	
	} 
	
}else if("remove_node".equals(operation))
{
	boolean result;
	String parentIdStr = request.getParameter("parentId");
	if(parentIdStr!=null&&(parentIdStr.indexOf("favorite")>=0))
	{//如果删除的是最常访问的filter
		nodeIdStr = nodeIdStr.replace("filter_", "");
		result = das.removeFavoriteFilter(key.getUsername(), nodeIdStr);
		if(result)
		{
			out.println("{\"status\":1,\"msg\":\"删除成功\"}");
		}else
		{
			out.println("{\"status\":0,\"msg\":\"删除失败\"}");
		}
		
	}else if(nodeIdStr.indexOf("filter")>=0)
	{
		//表示删除的是筛选器
		String filterIdStr = nodeIdStr.replace("filter_", "");
		UUID filterId = DataAccessFactory.getInstance().createUUID(filterIdStr);
		Filter filter = das.queryFilter(filterId);
		if(filter.getCreateUser().equals(key.getUsername()))
		{
				das.removeUserFocusFilter(filterId);
 				ErrorCode errorCode = das.removeFilter(filterId);
				
				if(errorCode.equals(ErrorCode.success))
				{
					out.println("{\"status\":1,\"msg\":\"删除成功\"}");
				}else
				{
					out.println("{\"status\":0,\"msg\":\"删除过滤器失败\"}");
				}
		}else
		{
			das.removeUserFocusFilter(key.getUsername(),filterId);
			das.removeFavoriteFilter(key.getUsername(),filterIdStr);
			out.println("{\"status\":1,\"msg\":\"取消成功\"}");
		}
		
		
	}else
	{
		int nodeId = Integer.parseInt(nodeIdStr);
		result = das.removJSTreeNode(nodeId,key.getUsername());
		if(result)
		{
			out.println("{\"status\":1,\"msg\"\"删除成功\"}");
		}else
		{
			out.println("{\"status\":0,\"msg\":\"删除失败\"}");
		}
	}
	
	
}else if("move_node".equals(operation))
{
	String refIdStr = request.getParameter("ref");
	String positionStr = request.getParameter("position");
	String title = request.getParameter("title");
	String copy = request.getParameter("copy");
	String parentIdStr = request.getParameter("parentId");
	String parentChildrenIdsStr = request.getParameter("parentChildrenIds");
	String refChildrenIdsStr = request.getParameter("refChildrenIds");
	boolean result = false;
	
	 if(refIdStr==null||positionStr==null)
	{
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;	
	}
	 if(refIdStr.indexOf("filter")>=0)
	 {
		 result = false;
	 }else if(refIdStr.indexOf("left_tree")>=0)
	 {
		 result = false;
	 }else if(nodeIdStr.indexOf("favorite")>=0)
	 {
		 result = false;
	 }else if(nodeIdStr.indexOf("filter")>=0&&(parentIdStr.indexOf("favorite")>=0))
	 {
		 result = false;
	 }else if(nodeIdStr.indexOf("favorite")>=0)
	 {
		 result = false;
	 }else if(refIdStr.indexOf("favorite")>=0)
	 {//新增最常访问
		 //result = das.addFavoriteFilter(key.getUsername(),nodeIdStr.replace("filter_", ""));
		 result = das.updataFavoriteFilters(refChildrenIdsStr,key.getUsername());
	 }else if(nodeIdStr.indexOf("filter")>=0)
	 {
		 nodeIdStr = nodeIdStr.replace("filter_", "");
		 int nodeId = Integer.parseInt(nodeIdStr);
		 int refId = Integer.parseInt(refIdStr);
		 int parentId = Integer.parseInt(parentIdStr);
		 if(refId == 6)
		 {
	 		//result = das.removeFilterId(nodeId, parentId);
			 result = das.updateDefaultFilters(key.getUsername(),refChildrenIdsStr);
			 result = das.updateFilterOrders(parentId,key.getUsername(),parentChildrenIdsStr)&&result;
		 }else if(parentId == 6)
		 {
		 	result = das.updateFilterOrders(refId,key.getUsername(),refChildrenIdsStr);
		 	result = das.updateDefaultFilters(key.getUsername(),parentChildrenIdsStr);
		 }else
		 {
			 //result = das.moveFilterNode(nodeId, refId, parentId);
			 result = das.updateFilterOrders(refId,key.getUsername(),refChildrenIdsStr);
			 result = das.updateFilterOrders(parentId,key.getUsername(),parentChildrenIdsStr)&&result;
		 }
	 }else
	 {
		 	int nodeId = Integer.parseInt(nodeIdStr);
			int position = Integer.parseInt(positionStr);
			int refId = Integer.parseInt(refIdStr);
			if("0".equals(copy))
			{
				result = das.moveJSTreeNode(nodeId,refId,position,title,false,key.getUsername());
			}else if("1".equals(copy))
			{
				result = das.moveJSTreeNode(nodeId,refId,position,title,true,key.getUsername());
			}	 
	 }
	 
	out.println("{\"status\":1,\"msg\":\"更新成功\"}");
	
}else if("rename_node".equals(operation))
{
	String title = request.getParameter("title");
	if(nodeIdStr.indexOf("filter")>=0)
	{
		String filterIdStr = nodeIdStr.replace("filter_","");
		UUID filterId = DataAccessFactory.getInstance().createUUID(filterIdStr);
		Filter filter = das.queryFilter(filterId);
		filter.setName(title);
		das.updateFilter(filter);
		out.println("{\"status\":1}");
	}else
	{
		int nodeId = Integer.parseInt(nodeIdStr);
   	 	boolean result = das.updateJSTreeNode(nodeId,title);
    	if(result)
		{
			out.println("{\"status\":1}");
		}else
		{
			out.println("{\"status\":0}");
		}
	}
}else if("verify".equals(operation))
{
    boolean result = true;
    if(nodeIdStr == null||"".equals(nodeIdStr))
    {
    	result = false;
    }else
    {
    	UUID filterId = DataAccessFactory.getInstance().createUUID(nodeIdStr);
    	Filter filter = das.queryFilter(filterId);
    	if(filter == null)
    	{
    		result = false;
    	}else if(filter.getXml()==null||"".equals(filter.getXml()))
    	{
    		result = true;
    	}else
    	{
    		Document	xmlDoc	= XMLUtil.string2Document(filter.getXml(), "UTF-8");
        	List<Node>	tempNodeList = XMLUtil.getNodes( xmlDoc, "/query/template" );
        	if(tempNodeList == null||tempNodeList.size()!=1)
        	{
        		result = false;
       		}else
        	{
        		result = true;
        	}
    	}
    }
    
    if(result)
    {
    	out.println(success);
    }else
    {
    	out.println(error);
    }
}else if("updateFavorites".equals(operation))
{
	String[] data = request.getParameterValues("id[]");
	boolean result = true;
	String filters = ArrayUtil.strArray2String(data);
	if(data == null)
		result = false;
	else
	{
		result = das.updataFavoriteFilters(filters,key.getUsername());
	}
	if(result)
		out.println(success);
	else
	 	out.println(error);
}else if("update_favorites".equals(operation))
{
	String positionStr = request.getParameter("position");
	String childrenIds = request.getParameter("childrenIds");
	if(positionStr==null)
	{
		out.println(error);
	}else
	{
		boolean result = das.updataFavoriteFilters(childrenIds,key.getUsername());
		//boolean result = das.updateFavoritesFilters(nodeIdStr,position,key.getUsername());
		if(result)
			out.println(success);
		else
	 		out.println(error);
	}
}else if("add_to_favorites".equals(operation))
{
	String filterId = request.getParameter("filterId");
	boolean result = das.addFavoriteFilter(key.getUsername(),nodeIdStr);
	if(result)
		out.println(success);
	else
	 	out.println(error);
}else if("update_filter_order".equals(operation))
{
	String parentIdStr = request.getParameter("parentId");
	String newOrders = request.getParameter("childrenIds");
	if(parentIdStr==null||"".equals(parentIdStr))
	{
		out.println(error);
		return;
	}
	if("6".equals(parentIdStr))
	{
		boolean result = das.updateDefaultFilters(key.getUsername(),newOrders);
		if(result)
			out.println(success);
		else
	 		out.println(error);
	}else
	{
		int parentId = Integer.parseInt(parentIdStr);
		boolean result = das.updateFilterOrders(parentId,key.getUsername(),newOrders);
		if(result)
			out.println(success);
		else
	 		out.println(error);
	}
}else if("copy".equals(operation))
{
	Filter srcFilter = das.queryFilter(DataAccessFactory.getInstance().createUUID(nodeIdStr));
	Filter newFilter = das.createFilter(key.getUsername(), new Timestamp(System.currentTimeMillis()), null);
	newFilter.setName(srcFilter.getName()+"new");
	newFilter.setXml(srcFilter.getXml());
	newFilter.setPublic(srcFilter.isPublic());
	newFilter.setVisible(srcFilter.isVisible());
	newFilter.setAnd(srcFilter.isAnd());
	newFilter = das.addFilter(newFilter); //das.updateFilter(filter);
	List<String> focusUsers = das.queryFocusUsersByFilter(srcFilter.getId());
	
	//复制关注人
	if(focusUsers != null && focusUsers.size() > 0){
		for(String user: focusUsers){
			if(user != null && user.length() > 0)
				das.addUserFocusFilter(user, newFilter.getId());
		}
	}
	
	//复制定时器部分
	Timer[] allTimer = das.queryTimerByFilterId(srcFilter.getId());
	if(allTimer != null && allTimer.length > 0){
		Timer newTimer = das.createTimer(key.getUsername());
		Timer srcTimer = allTimer[0];
		newTimer.setActionId(srcTimer.getActionId());
		newTimer.setActionParam(srcTimer.getActionParam());
		newTimer.setDay(srcTimer.getDay());
		newTimer.setFilterId(newFilter.getId());
		newTimer.setHour(srcTimer.getHour());
		newTimer.setMinute(srcTimer.getMinute());
		newTimer.setMonth(srcTimer.getMonth());
		newTimer.setName(srcTimer.getName());
		newTimer.setRetry(srcTimer.getRetry());
		newTimer.setRetryAccount(srcTimer.getRetryAccount());
		newTimer.setRetryDelay(srcTimer.getRetryDelay());
		newTimer.setSecond(srcTimer.getSecond());
		newTimer.setStart(srcTimer.getStart());
		newTimer.setStatisticerId(srcTimer.getStatisticerId());
		newTimer.setWeek(srcTimer.getWeek());
		newTimer.setYear(srcTimer.getYear());
		
		ErrorCode errorCode = das.addTimer(newTimer);	
		if(!errorCode.equals(ErrorCode.success)){
			out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
			return;
		}
	}
	out.println("{\"status\":1,\"id\":"+newFilter.getId()+"}");
}
%>