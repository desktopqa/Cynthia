<%@ page language="java" contentType="text/html; charset=UTF-8"%>

<%@page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
<%@page import="com.sogou.qadev.service.cynthia.util.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@page import="java.util.*"%>
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
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%
	
		boolean reloadTree = false;
		boolean reloadManage = false;
		
		String filterName = request.getParameter("filterName");
		String searchConfig = request.getParameter( "searchConfig" );
		boolean isSave = true;
		boolean isExecute = false;
		String isPublic = request.getParameter( "isPublic" );
		String isFocus = request.getParameter( "isFocus" );
		String betweenField = request.getParameter( "betweenField" );
		String focusUsersStr = request.getParameter("focusUsers");
		String unFocusUsersStr = request.getParameter("unFocusUsers");
		
		unFocusUsersStr = unFocusUsersStr.trim();
		focusUsersStr = focusUsersStr.trim();
		String [] focusUsers = focusUsersStr.split("\\|");
		String [] unFocusUsers = unFocusUsersStr.split("\\|");
		
		if( searchConfig.startsWith( "<query>" ) )
			searchConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + searchConfig;
		
		Filter filter = null;
		
		UUID filterId = null;
		String filterIdStr = request.getParameter("id");
		if(filterIdStr != null && filterIdStr.trim().length() > 0)
			filterId = DataAccessFactory.getInstance().createUUID(filterIdStr.trim());
		
		UUID fatherId = null;
		String relateIdStr = request.getParameter("fatherId");
		if(relateIdStr != null)
			fatherId = DataAccessFactory.getInstance().createUUID(relateIdStr);
		
		if(filterId != null)
		{
			filter = das.queryFilter(filterId);
			
			if(!filter.getName().equals(filterName) || filter.isPublic() != (isPublic != null))
				reloadManage = true;
			
			filter.setXml(searchConfig);
			filter.setPublic( isPublic != null );
			filter.setName( filterName );
			filter.setAnd(betweenField != null && betweenField.equals("and"));
			filter.setVisible(true);
		}
		else
		{
			reloadManage = true;
			
			filter = das.createFilter(key.getUsername(), new Timestamp(System.currentTimeMillis()), fatherId);
			filter.setName(filterName);
			filter.setXml(searchConfig);
			filter.setAnd(betweenField != null && betweenField.equals("and"));
			filter.setPublic(isPublic != null);
			filter.setVisible(true);
		}
		
		boolean isSucc = false;
		if( isSave )
		{	
			if(filterId != null)
			{
				ErrorCode errorCode = das.updateFilter(filter);
				if(errorCode.equals(ErrorCode.success))
					isSucc = true;
			}
			else
			{
				filter = das.addFilter(filter);
				if(filter != null)
					isSucc = true;
			}
			
			if(isSucc)
			{
				if(filterId == null)
				{
					reloadTree = true;
					reloadManage = true;
				}
				else
				{
					boolean isFocusOld = false;
					
					UUID[] focusFilterIdArray = das.queryUserFocusFilters(key.getUsername());
					for(int i = 0; focusFilterIdArray != null && i < focusFilterIdArray.length; i++)
					{
						if(focusFilterIdArray[i].equals(filterId))
							isFocusOld = true;
					}
					
					if(isFocusOld != (isFocus != null))
					{
						reloadTree = true;
						reloadManage = true;
					}
				}
				
				if(reloadTree)
				{
					if(isFocus != null)
						das.addUserFocusFilter(key.getUsername(), filter.getId());
					else
						das.removeUserFocusFilter(key.getUsername(), filter.getId());
				}
				
				List<String> focusUsersOld = das.queryFocusUsersByFilter(filter.getId());
				for(String user : focusUsers){
					if(!focusUsersOld.contains(user)){
						das.addUserFocusFilter(user,filter.getId());
					}
				}
				
			}
		}
		
		if(isSave && !isSucc)
		{
			%>
			<script>
				alert("保存失败！");
				history.back(1);
			</script>
			<%
		}
		else if( !isExecute && isSucc )
		{
			%>
			<script>
				window.close();
				window.opener.Ext.Msg.alert('消息框', '<%=filterId != null ? "修改" : "新建"%>筛选器成功！');
		<%
			if(filterId != null)
			{
		%>
				var filterResultPanel = window.opener.Ext.getCmp('filter_result_<%=filterId%>');
				if(filterResultPanel)
					window.opener.loadFilterResult_core('<%=filterId%>', '<%=XMLUtil.toSafeXMLString(filter.getName())%>');
		<%
			}
		%>	
			window.opener.filterTree.getRootNode().reload();
			
			var filterManagementPanel = window.opener.Ext.getCmp('filter_management');
			if(filterManagementPanel)
				filterManagementPanel.items.get(0).getStore().reload();
			</script>
			<%
		}
		else if( isExecute && filter != null )
		{
			response.sendRedirect( "../filter/filter.jsp?filterId=" + URLUtil.toSafeURLString(filter.getId().toString()) + "&isSession=true" + (isSave ? "&refresh=true" : ""));
			return;
		}
		%>

</body>
</html>