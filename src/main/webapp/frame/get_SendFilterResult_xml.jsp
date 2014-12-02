<%@page import="com.sogou.qadev.service.cynthia.service.ExportDataManager"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.service.MailSender"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page language="java" contentType="text/xml; charset=UTF-8"%>


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

DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);

String receivers = request.getParameter("receiver");
if(receivers == null){
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}

String cc = request.getParameter("cc");
String bc = request.getParameter("bc");
String subject = request.getParameter("subject");
String content = request.getParameter("content");

Filter filter = das.queryFilter(DataAccessFactory.getInstance().createUUID(request.getParameter("filterId")));

if(filter == null){
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}

MailSender sender = new MailSender();
sender.setHtml( true );
sender.setSmtp( "transport.mail.sogou-inc.com" );

sender.setFromUser(key.getUsername());

sender.setToUsers(receivers.replaceAll(",", ";").split(";"));

if(cc != null){
	sender.setCcUsers(cc.replaceAll(",", ";").split(";"));
}

if(bc != null){
	sender.setBccUsers(bc.replaceAll(",", ";").split(";"));
}

if(subject != null){
	sender.setSubject(subject);
}

if(content != null){
	sender.setContent(content);
}

try
{
	String htmlString = ExportDataManager.exportMailHtmlFilter(das, keyId,  filter, key.getUsername());	
	String realContent = XMLUtil.toSafeXMLString(content).replaceAll("\n", "<br>") + "<br>" + htmlString;
	sender.setContent(realContent.replaceAll("utf-8", "GBK").replaceAll("UTF-8", "GBK") );
	sender.setEncode( "GBK" );
	sender.sendHtmlEx("GBK");	
}catch(Exception e)
{
	e.printStackTrace();
}
%>