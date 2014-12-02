<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@ page language="java" contentType="text/xml; charset=UTF-8"%>

<%@ page import="java.sql.Timestamp"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>

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
	
	String filterIdStr = request.getParameter("filterId");
	if(filterIdStr == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.param_error));
		return;
	}
	
	UUID filterId = DataAccessFactory.getInstance().createUUID(filterIdStr);
	
	Filter filter = das.queryFilter(filterId);
	if(filter == null)
		return;
	
	boolean isPublic = false;
	String isPublicStr = request.getParameter("isPublic");
	if(isPublicStr != null)
		isPublic = Boolean.parseBoolean(isPublicStr);
	
	boolean isFocus = false;
	String isFocusStr = request.getParameter("isFocus");
	if(isFocusStr != null)
		isFocus = Boolean.parseBoolean(isFocusStr);
	
	String filterName = request.getParameter("filterName");
	Timestamp createTime = new Timestamp(System.currentTimeMillis());
	
	Filter newFilter = das.createFilter(key.getUsername(), createTime, null);
	newFilter.setName(filterName);
	newFilter.setXml(filter.getXml());
	newFilter.setAnd(filter.isAnd());
	newFilter.setPublic(isPublic);
	newFilter.setVisible(filter.isVisible());
	
	if(isFocus)
		das.addUserFocusFilter(key.getUsername(), newFilter.getId());
	else
		das.removeUserFocusFilter(key.getUsername(), newFilter.getId());
	
	newFilter = das.addFilter(newFilter);
	
	if(newFilter != null)
		out.println(ErrorManager.getCorrectXml());
	else
		out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
%>