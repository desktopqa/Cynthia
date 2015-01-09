<%@page import="com.sogou.qadev.service.cynthia.service.FilterQueryManager"%>
<%@page language="java" contentType="text/xml; charset=UTF-8"%>

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
<%@page import="org.w3c.dom.*"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Timer"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.TimerAction"%>

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

String filterIdStr = request.getParameter("id");

DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
boolean reloadTree = false;
boolean reloadManage = false;
String isPublic = "";
String isFocus = "";
		
String filterName = request.getParameter("filterName");
String searchConfig = request.getParameter( "searchConfig" );

boolean isSave = true;
boolean isExecute = false;
String betweenField = request.getParameter( "betweenField" );
String[] focusUsers = request.getParameterValues("focusUsers[]");
List<String> focusUsersList = new ArrayList<String>();

if(focusUsers != null){
	for(String user: focusUsers)
		focusUsersList.add(user);
}

String[] unFocusUsers = request.getParameterValues("unfocusUsers[]");
List<String> unFocusUsersList = new ArrayList<String>();
if(unFocusUsers != null){
	for(String user: unFocusUsers)
		unFocusUsersList.add(user);
}

String nodeIdStr = request.getParameter("nodeId");
		
if( searchConfig.startsWith( "<query>" ) )
	searchConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + searchConfig;
		
Filter filter = null;
		
UUID filterId = null;
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
	if(!"".equals(filter.getXml()))
	{
		Document filterXmlDoc = XMLUtil.string2Document(filter.getXml(), "UTF-8");
		Document searchConfigDoc = XMLUtil.string2Document(searchConfig, "UTF-8");
		List<Node> displayNodes = XMLUtil.getNodes(filterXmlDoc,"query/template/display/field");
		Node newDisplayNode = XMLUtil.getSingleNode(searchConfigDoc,"query/template/display");
		newDisplayNode.setTextContent("");
		Map<String,String> fieldWidthMap = FilterQueryManager.getDisplayFieldAndWidth(filter.getXml(), das);
		for(Node node : displayNodes)
		{
			Node newNode = searchConfigDoc.createElement("field");
			String name = XMLUtil.getAttribute(node,"name");
			XMLUtil.setAttribute(newNode,"id",XMLUtil.getAttribute(node,"id"));
			XMLUtil.setAttribute(newNode,"name",XMLUtil.getAttribute(node,"name"));
			XMLUtil.setAttribute(newNode,"type",XMLUtil.getAttribute(node,"type"));
			if(fieldWidthMap.get(name) != null){
				XMLUtil.setAttribute(newNode,"width",fieldWidthMap.get(name));
			}
			if(XMLUtil.getAttribute(node,"datatype")!=null)
			{
				XMLUtil.setAttribute(newNode,"datatype",XMLUtil.getAttribute(node,"datatype"));
			}
			newDisplayNode.appendChild(newNode);
		}
		searchConfig = XMLUtil.document2String(searchConfigDoc,"UTF-8");
	}
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
	filter.setAnd(betweenField != null && betweenField.trim().equals("and"));
	filter.setPublic(isPublic != null);
	filter.setVisible(true);
}

if(nodeIdStr!=null&&!"".equals(nodeIdStr)&&!"null".equals(nodeIdStr)&&!"6".equals(nodeIdStr))
{
	das.addFilterToFolder(filter.getId().toString(),Integer.parseInt(nodeIdStr));
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
			das.addUserFocusFilter(key.getUsername(), filter.getId());
		}
		
		List<String> oldFocusUsers = das.queryFocusUsersByFilter(filter.getId());  //当前关注的用户列表
		oldFocusUsers.remove(filter.getCreateUser());
		unFocusUsersList.retainAll(oldFocusUsers); //取出所有取消关注人员
	
		for(String user: unFocusUsersList){
			das.removeUserFocusFilter(user, filter.getId());
		}
		
		//设置默认关注
		if(focusUsers!=null){
			for(String user : focusUsers){
				if(!oldFocusUsers.contains(user) && !filter.getCreateUser().equals(user)){
					das.addUserFocusFilter(user,filter.getId());
				}
			}
		}
	}
}
		
//定时器部分
StringBuffer xmlb = new StringBuffer(64);
xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
xmlb.append("<root>");
String isTimerStr = request.getParameter("isTimer");
String timerIdStr = request.getParameter("timerId");
UUID actionId = DataAccessFactory.getInstance().createUUID("120846");//发送邮件动作
TimerAction timerAction = das.queryTimerAction(actionId);
if(isTimerStr.equals("true"))
{
	if(timerIdStr==null||"".equals(timerIdStr))
	{
		Timer timer = null;
		NodeList paramList = null;
		Document document = null;
	
		timer = das.createTimer(key.getUsername());
	
		// 获取动作的XML参数表，并填充上相应的参数
		if( timerAction.getParam() != null )
		{
			document = XMLUtil.string2Document(timerAction.getParam(), "UTF-8");
			// 设置属性的值
			paramList = document.getElementsByTagName( "param" );
		}
	
		if( paramList != null )
		{
			for( int inode = 0; inode < paramList.getLength(); inode++ )
			{
				Element node = (Element)paramList.item( inode );
			
				if( request.getParameter( node.getAttribute( "name" ) ) != null )
			node.setAttribute( "value", request.getParameter( node.getAttribute( "name" ) ) );
			}
			
			// 将XML　DOM转化为字符串
			timer.setActionParam(XMLUtil.document2String( document, "UTF-8" ) );
		}
	
		timer.setActionId(actionId);
		timer.setFilterId(filter.getId());
		timer.setMonth( ArrayUtil.strArray2String(request.getParameterValues( "month[]" )));
		timer.setDay( request.getParameterValues( "date[]" ) == null ? "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31" : ArrayUtil.strArray2String( request.getParameterValues( "date[]" )));
		timer.setWeek( ArrayUtil.strArray2String( request.getParameterValues( "day[]" )));
		timer.setHour( ArrayUtil.strArray2String( request.getParameterValues( "hour[]" )));
		timer.setMinute( ArrayUtil.strArray2String( request.getParameterValues( "minute[]" )) );
		timer.setName( request.getParameter( "timerName" ) );
		timer.setRetryAccount(5);
		timer.setRetryDelay(5000);
		timer.setStart(true);
		timer.setSendNull(true);
	
		//执行定时器插入操作
		ErrorCode errorCode = das.addTimer(timer);	
	}else
	{
		UUID timerId = DataAccessFactory.getInstance().createUUID(timerIdStr);
		Timer timer = das.queryTimer(timerId);
		NodeList paramList = null;
		Document document = null;
	
		// 获取动作的XML参数表，并填充上相应的参数
		if( timerAction.getParam() != null )
		{
			document = XMLUtil.string2Document(timerAction.getParam(), "UTF-8");
			// 设置属性的值
			paramList = document.getElementsByTagName( "param" );
		}
	
		if( paramList != null )
		{
			for( int inode = 0; inode < paramList.getLength(); inode++ )
			{
				Element node = (Element)paramList.item( inode );
			
				if( request.getParameter( node.getAttribute( "name" ) ) != null )
			node.setAttribute( "value", request.getParameter( node.getAttribute( "name" ) ) );
			}
			
			// 将XML　DOM转化为字符串
			timer.setActionParam(XMLUtil.document2String( document, "UTF-8" ) );
		}
	
	
		timer.setMonth( ArrayUtil.strArray2String( request.getParameterValues( "month[]" )));
		timer.setDay( request.getParameterValues( "date[]" ) == null ? "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31" : ArrayUtil.strArray2String( request.getParameterValues( "date[]" )));
		timer.setWeek( ArrayUtil.strArray2String( request.getParameterValues( "day[]" )));
		timer.setHour( ArrayUtil.strArray2String( request.getParameterValues( "hour[]" )));
		timer.setMinute( ArrayUtil.strArray2String( request.getParameterValues( "minute[]" )));
		timer.setName( request.getParameter( "timerName" ) );
	
		//执行定时器插入操作
		ErrorCode errorCode = das.modifyTimer(timer);
	}
	
}else if(isTimerStr.equals("false")&&(timerIdStr!=null&&!"".equals(timerIdStr)))
{
	//将定时器删除
	UUID timerId = DataAccessFactory.getInstance().createUUID(timerIdStr);
	das.removeTimer(timerId);
}
xmlb.append("<filterId>");
xmlb.append(filter.getId());
xmlb.append("</filterId>");
xmlb.append("<isError>false</isError>");
xmlb.append("</root>");
out.println(xmlb.toString());

%>