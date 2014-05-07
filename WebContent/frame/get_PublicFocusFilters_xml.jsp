<%@ page language="java" contentType="text/xml; charset=UTF-8"%>

<%@page import="com.sogou.qadev.service.login.bean.Key"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
  
<%
  	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
  response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
  response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
  response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility

  out.clear();
  	
  Long keyId = (Long)session.getAttribute("kid");
  Key key = (Key)session.getAttribute("key");

  if(keyId == null || keyId <= 0 || key == null){
  	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
  	return;
  }

  String[] isPublicFocusArray = request.getParameterValues("isPublicFocus");
  if(isPublicFocusArray == null || isPublicFocusArray.length == 0){
  	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
  	return;
  }

  DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);

  for(String isPublicFocus : isPublicFocusArray){
  	String[] isPublicFocusElemArray = isPublicFocus.split("\\|");
  	
  	UUID filterId = DataAccessFactory.getInstance().createUUID(isPublicFocusElemArray[0]);
  	String type = isPublicFocusElemArray[1];
  	boolean value = Boolean.parseBoolean(isPublicFocusElemArray[2]);
  	
  	Filter filter = das.queryFilter(filterId);
  	if(filter == null){
  		continue;
  	}
  	
  	if(type.equals("public")){
  		filter.setPublic(value);
  		das.updateFilter(filter);
  	}
  	else if(type.equals("focus")){
  		if(value){
  	das.addUserFocusFilter(key.getUsername(), filterId);
  		}
  		else{
  			das.removeUserFocusFilter(key.getUsername(), filterId);
  		}
  	}
  }

  out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><success>true</success>");
  %>