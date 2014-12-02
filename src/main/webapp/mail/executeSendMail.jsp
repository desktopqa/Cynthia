<%@page import="com.sogou.qadev.service.cynthia.service.MailSender"%>
<%@page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page contentType="text/html; charset=UTF-8" %>

<%@page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@page import="java.util.LinkedHashSet"%>

<%
	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
	response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
	response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility
	
	out.clear();
	
	Long keyId = (Long)session.getAttribute("kid");
	Key key = (Key)session.getAttribute("key");
	
	if(keyId == null || keyId <= 0 || key == null)
	{
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	String receivers = request.getParameter("sendMailReceivers");
	String subject = request.getParameter("sendMailSubject");
	String content = request.getParameter("sendMailContent");
	
	if(receivers == null || receivers.split(";").length == 0)
	{
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	String[] allReceivers = receivers.split(";");
	
	MailSender sender = new MailSender();
	String fromUser = ((Key)session.getAttribute("key")).getUsername();
	sender.setFromUser(fromUser);
	sender.setToUsers(allReceivers);
	
	sender.setSubject(subject);
	sender.setContent(content);
	boolean  isSuccess = sender.sendHtmlEx("GBK");
	
%>
