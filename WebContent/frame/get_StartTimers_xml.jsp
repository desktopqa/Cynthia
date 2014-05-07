<%@ page language="java" contentType="text/xml; charset=UTF-8"%>

<%@page import="com.sogou.qadev.service.login.bean.Key"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Timer"%>
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

  String[] isStartStrArray = request.getParameterValues("isStart");
  if(isStartStrArray == null || isStartStrArray.length == 0){
  	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
  	return;
  }

  DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);

  for(String isStartStr : isStartStrArray){
  	UUID timerId = DataAccessFactory.getInstance().createUUID(isStartStr.split("\\|")[0]);
  	boolean isStart = Boolean.parseBoolean(isStartStr.split("\\|")[1]);
  	
  	Timer timer = das.queryTimer(timerId);
  	if(timer == null){
  		continue;
  	}
  	
  	timer.setStart(isStart);
  	das.modifyTimer(timer);
  }

  out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><success>true</success>");
  %>