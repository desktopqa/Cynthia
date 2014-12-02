<%@ page language="java" contentType="text/xml; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Timer"%>
<%@ page import="java.util.*"%>
<%@page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>

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

String startStr = request.getParameter("start");
String limitStr = request.getParameter("limit");

if(startStr == null || limitStr == null){
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}

int start = Integer.parseInt(startStr);
int limit = Integer.parseInt(limitStr);

DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);

Set<Timer> timerSet = new LinkedHashSet<Timer>();

Timer[] timerArray = das.queryTimers(key.getUsername());
if(timerArray != null && timerArray.length > 0){
	timerSet.addAll(Arrays.asList(timerArray));
}

List<Timer> timerList = new ArrayList<Timer>();
Map<String, Set<Timer>> timerMap = new TreeMap<String, Set<Timer>>();

for(Timer timer : timerSet){
	if(!timerMap.containsKey(timer.getName())){
		timerMap.put(timer.getName(), new LinkedHashSet<Timer>());
	}
	
	timerMap.get(timer.getName()).add(timer);
}

for(String timerName : timerMap.keySet()){
	for(Timer timer : timerMap.get(timerName)){
		timerList.add(timer);
	}
}

StringBuffer xml = new StringBuffer();
xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
xml.append("<root>");
xml.append("<totalCount>").append(timerList.size()).append("</totalCount>");

for(int i = start; i < start + limit && i < timerList.size(); i++){
	xml.append("<timer>");
	xml.append("<id>").append(timerList.get(i).getId()).append("</id>");
	xml.append("<name>").append(XMLUtil.toSafeXMLString(timerList.get(i).getName())).append("</name>");
	xml.append("<createUser>").append(timerList.get(i).getCreateUser()).append("</createUser>");
	xml.append("<createTime>");
	
	if(timerList.get(i).getCreateTime().toString().indexOf(".") > 0){
		xml.append(timerList.get(i).getCreateTime().toString().split("\\.")[0]);
	}
	else{
		xml.append(timerList.get(i).getCreateTime());
	}
	
	xml.append("</createTime>");
	
	if(timerList.get(i).isStart() && timerList.get(i).takeNextAlarmTime() != null){
		xml.append("<nextRunTime>");
		
		if(timerList.get(i).takeNextAlarmTime().toString().indexOf(".") > 0){
	xml.append(timerList.get(i).takeNextAlarmTime().toString().split("\\.")[0]);
		}
		else{
	xml.append(timerList.get(i).takeNextAlarmTime());
		}
		
		xml.append("</nextRunTime>");
	}
	else{
		xml.append("<nextRunTime/>");
	}
	
	xml.append("<isStart>").append(timerList.get(i).isStart()).append("</isStart>");
	xml.append("</timer>");
}

xml.append("</root>");

out.clear();
out.println(xml);
%>