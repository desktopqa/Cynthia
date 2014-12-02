<%@page import="com.sogou.qadev.service.cynthia.service.CookieManager"%>
<%@page import="com.sogou.qadev.cache.CacheManager"%>
<%@page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@page language="java" contentType="text/html; charset=UTF-8"%>

<%@page import="java.net.URLEncoder"%>

<!DOCTYPE HTML PUBLIC "-//w3c//dtd html 4.0 transitional//en">
<%
response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility

Cookie cookie = new Cookie("jpassport-sp", null);
cookie.setMaxAge(0);
cookie.setPath(request.getContextPath());

CookieManager.delCookie(response, "login_username");
CookieManager.delCookie(response, "jpassport-sp");
CookieManager.delCookie(response, "jpassport-sp");
CookieManager.delCookie(response, "login_nickname");
CookieManager.delCookie(response, "login_password");

session.removeAttribute("key");
session.removeAttribute("userName");

session.invalidate();

String webUrl = ConfigUtil.getCynthiaWebRoot();

String logoutUrl  = ConfigUtil.getCynthiaWebRoot() + "userInfo/login.jsp";

response.sendRedirect(logoutUrl+"?targetUrl="+URLEncoder.encode(webUrl,"UTF-8"));
%>

