<%@page import="com.sogou.qadev.service.cynthia.util.FilterUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ConfigManager"%>
<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="org.w3c.dom.*" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Set"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.TemplateType"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Timer"%>

<%@ include file="initMain.function.jsp"%>

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
	
	UUID filterId = DataAccessFactory.getInstance().createUUID(request.getParameter("filterId"));;
	if(filterId == null)
	{
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	//edit
	Filter filter = das.queryFilter(filterId);
	if(filter == null || filter.getXml()==null || "".equals(filter.getXml()))
	{	//表示还未初始化
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version='1.0' encoding='UTF-8' ?><query>");
		xml.append("<init>true</init>");
		xml.append("<filtername>");
		xml.append(filter == null ? "" : filter.getName());
		xml.append("</filtername>");
		xml.append("<filterid>");
		xml.append(filter == null ? "" : filter.getId());
		xml.append("</filterid>");
		if(filter !=null && filter.getCreateUser().equals(key.getUsername()))
		{
			xml.append("<readonly>false</readonly>");
		}else
		{
			xml.append("<readonly>true</readonly>");
		}
		xml.append("</query>");
		out.println(xml.toString());
		return;
	}
	
	Document document = XMLUtil.string2Document(filter.getXml(), "UTF-8");
	Node queryNode = XMLUtil.getSingleNode(document, "query");
	
	Node filterNameNode = document.createElement("filterName");
	filterNameNode.setTextContent(filter.getName());
	queryNode.appendChild(filterNameNode);
	
	Node fieldRelationNode = document.createElement("isAnd");
	fieldRelationNode.setTextContent(Boolean.toString(filter.isAnd()));
	queryNode.appendChild(fieldRelationNode);
	
	boolean isFocus = false;
	
	UUID[] focusFilterIdArray = das.queryUserFocusFilters(key.getUsername());
	for(int i = 0; focusFilterIdArray != null && i < focusFilterIdArray.length; i++)
	{
		if(focusFilterIdArray[i].equals(filterId))
		{
			isFocus = true;
			break;
		}
	}
	
	Node isFocusNode = document.createElement("isFocus");
	isFocusNode.setTextContent(Boolean.toString(isFocus));
	queryNode.appendChild(isFocusNode);
	
	Node isPublicNode = document.createElement("isPublic");
	isPublicNode.setTextContent(Boolean.toString(filter.isPublic()));
	queryNode.appendChild(isPublicNode);
	
	Node modifyAuthNode = document.createElement("modifyAuth");
	modifyAuthNode.setTextContent(key.getUsername().equals(filter.getCreateUser()) ? "true" : "false");
	queryNode.appendChild(modifyAuthNode);
	
	Node templateNode = XMLUtil.getSingleNode(queryNode,"template");
	Node templateTypeNode = XMLUtil.getSingleNode(queryNode,"templateType");
	
	Set<String> allUsersSet = new TreeSet<String>();
	List<String> focusUserList = das.queryFocusUsersByFilter(filterId);
	Set<String> focusUsers = new TreeSet<String>();
	focusUsers.addAll(focusUserList);

	UUID templateId = null;
	UUID templateTypeId = null;
	
	try
	{
		templateId = DataAccessFactory.getInstance().createUUID(XMLUtil.getAttribute(templateNode, "id"));
		templateTypeId = DataAccessFactory.getInstance().createUUID(XMLUtil.getAttribute(templateTypeNode,"id"));	
	}catch(Exception e){
		e.printStackTrace();
		return;
	}
	
	if(templateId == null || templateTypeId == null){
		return;
	}
	
	Node whereNode = XMLUtil.getSingleNode(templateNode, "where");
	Node displayNode = XMLUtil.getSingleNode(templateNode, "display");
	Node orderNode = XMLUtil.getSingleNode(templateNode, "order");
	
	Map<String, Set<Object>> whereFieldMap = getWhereFieldMap(whereNode);
	Map<String, CommonField> displayFieldMap = getDisplayFieldMap(displayNode);
	Map<String, CommonField> orderFieldMap = getOrderFieldMap(orderNode);

	int orderIndent = 0;
	String orderIndentStr = XMLUtil.getAttribute(orderNode, "indent");
	if(orderIndentStr != null)
	{
		try
		{
			orderIndent = Integer.parseInt(orderIndentStr);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//初始化templates
	Node templatesNode = document.createElement("templates");
	Set<Template> templateSet = new LinkedHashSet<Template>();
	
	templateSet.addAll(Arrays.asList(DataManager.getInstance().queryUserTemplates(templateTypeId, key.getUsername())));
	
	if(templateSet==null||templateSet.size()==0)
	{
		queryNode.appendChild(templatesNode);
	}else
	{
		for(Template template : templateSet)
		{
			Node tempNode = document.createElement("template");
			Node tempIdNode = document.createElement("id");
			Node tempNameNode = document.createElement("name");
			tempIdNode.setTextContent(template.getId().toString());
			tempNameNode.setTextContent(template.getName());
			tempNode.appendChild(tempIdNode);
			tempNode.appendChild(tempNameNode);
			templatesNode.appendChild(tempNode);
		}
	}
	queryNode.appendChild(templatesNode);
	
	templateNode.setTextContent("");
	
	//初始化template的域
	Node fieldsNode = document.createElement("fields");
	Template localTemplate = das.queryTemplate(templateId);
	Map<String,String> fields = getTemplateFields(das,localTemplate);
	for(String fieldId:fields.keySet())
	{
		Node tempFieldNode = document.createElement("field");
		Node tempIdNode = document.createElement("id");
		Node tempNameNode = document.createElement("name");
		tempIdNode.setTextContent(fieldId);
		tempNameNode.setTextContent(fields.get(fieldId));
		tempFieldNode.appendChild(tempIdNode);
		tempFieldNode.appendChild(tempNameNode);
		fieldsNode.appendChild(tempFieldNode);
	}
	
	queryNode.appendChild(fieldsNode);
	
	Node conditionsNode = document.createElement("conditions");
	for(String fieldId : whereFieldMap.keySet())
	{
		if(fieldId == null)
			continue;
		Node fieldNode = document.createElement("field");
		String fieldContent = queryFieldHTML(das,templateId,fieldId,whereFieldMap,displayFieldMap,orderFieldMap,orderIndent);
		Node fieldIdNode =document.createElement("fieldId");
		Node fieldContentNode = document.createElement("fieldContent");
		fieldIdNode.setTextContent(fieldId);
		fieldContentNode.setTextContent(fieldContent);
		fieldNode.appendChild(fieldIdNode);
		fieldNode.appendChild(fieldContentNode);
		conditionsNode.appendChild(fieldNode);	

	}
	templateNode.appendChild(conditionsNode);
	
	Node orderContentNode = document.createElement("order");
	String orderContent = "";
	try{
		orderContent = queryOrderContentHTML(das,templateId, orderFieldMap, orderIndent);
	}catch(Exception e){
	}
	
	orderContentNode.setTextContent(orderContent);
	templateNode.appendChild(orderContentNode);
	
	String[] users = queryFlowUsersByTemplateId(das,templateId);
	if(users!=null)
		allUsersSet.addAll(Arrays.asList(users));

	for(String user : focusUsers){
		if(allUsersSet.contains(user))
			allUsersSet.remove(user);
	}
	
	
	Node unSelectedUsers = document.createElement("unselectedusers");
	Node selectedUsers = document.createElement("selectedusers");
	for(String user : allUsersSet)
	{
		Node userNode = document.createElement("user");
		Node userNameNode = document.createElement("username");
		Node userAliasNode = document.createElement("useralias");
		userNameNode.setTextContent(user);
		String userAlias = CynthiaUtil.getUserAlias(user);
		userNameNode.setTextContent(user);
		userAliasNode.setTextContent(userAlias);
		userNode.appendChild(userNameNode);
		userNode.appendChild(userAliasNode);	
		unSelectedUsers.appendChild(userNode);
	}
	
	for(String user : focusUsers)
	{
		Node userNode = document.createElement("user");
		Node userNameNode = document.createElement("username");
		Node userAliasNode = document.createElement("useralias");
		userNameNode.setTextContent(user);
		String userAlias = CynthiaUtil.getUserAlias(user);
		userNameNode.setTextContent(user);
		userAliasNode.setTextContent(userAlias);
		userNode.appendChild(userNameNode);
		userNode.appendChild(userAliasNode);	
		selectedUsers.appendChild(userNode);
	}
	
	queryNode.appendChild(unSelectedUsers);
	queryNode.appendChild(selectedUsers);
	
	Timer[] timer = das.queryTimerByFilterId(filterId);
	Node timerNode = document.createElement("timer");
	Node isTimerNode = document.createElement("istimer");
	if(timer!=null&&timer.length>0)
	{
		isTimerNode.setTextContent("true");
	}else
	{
		isTimerNode.setTextContent("false");
	}
	queryNode.appendChild(isTimerNode);
	
	Timer thisTimer = null;
	if(timer!=null&&timer.length>0)
	{
		Node paramsNode = document.createElement("params");
		thisTimer = timer[0];
		String paramString = thisTimer.getActionParam();
		Document thisDocument = XMLUtil.string2Document(paramString,"UTF-8");
		NodeList paramList = thisDocument.getElementsByTagName("param");
		for(int i=0;i<paramList.getLength();i++)
		{
			Element element = (Element)paramList.item( i );
			String elementType = element.getAttribute( "type" );
			String elementName = element.getAttribute( "name" );
			String value = element.getAttribute( "value" );
			Node paramNode = document.createElement(elementName);
			paramNode.setTextContent(value);
			paramsNode.appendChild(paramNode);			
		}
		
		Node timerIdNode = document.createElement("timerId");
		timerIdNode.setTextContent(thisTimer.getId().toString());
		Node timerNameNode = document.createElement("timername");
		timerNameNode.setTextContent(thisTimer.getName());
		Node monthNode = document.createElement("month");
		monthNode.setTextContent(thisTimer.getMonth());
		Node dateNode = document.createElement("date");
		dateNode.setTextContent(thisTimer.getDay());
		Node dayNode = document.createElement("day");
		dayNode.setTextContent(thisTimer.getWeek());
		Node hourNode = document.createElement("hour");
		hourNode.setTextContent(thisTimer.getHour());
		Node minuteNode = document.createElement("minute");
		minuteNode.setTextContent(thisTimer.getMinute());
		
		timerNode.appendChild(timerIdNode);
		timerNode.appendChild(timerNameNode);
		timerNode.appendChild(monthNode);
		timerNode.appendChild(dateNode);
		timerNode.appendChild(dayNode);
		timerNode.appendChild(hourNode);
		timerNode.appendChild(minuteNode);
		
		timerNode.appendChild(paramsNode);

	}
	queryNode.appendChild(timerNode);
	Node readOnlyNode = document.createElement("readonly");
	if(filter.getCreateUser().equals(key.getUsername()))
	{
		readOnlyNode.setTextContent("false");
	}else
	{
		readOnlyNode.setTextContent("true");
	}
	queryNode.appendChild(readOnlyNode);
	out.println(XMLUtil.document2String(document, "UTF-8"));
%>