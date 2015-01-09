<%@page import="com.sogou.qadev.service.cynthia.service.ExportDataManager"%>
<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@ page language="java" contentType="application/vnd.ms-excel; charset=UTF-8"%>

<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.text.SimpleDateFormat"%>

<%@ page import="org.apache.commons.httpclient.HttpClient"%>
<%@ page import="org.apache.commons.httpclient.NameValuePair"%>
<%@ page import="org.apache.commons.httpclient.methods.PostMethod"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFWorkbook"%>
<%@page import="org.apache.poi.hssf.usermodel.HSSFCell"%>
<%@page import="org.apache.poi.hssf.usermodel.HSSFRow"%>
<%@page import="org.apache.poi.hssf.usermodel.HSSFSheet"%>
<%@page import="org.apache.poi.hssf.usermodel.HSSFWorkbook"%>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Attachment"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.TemplateType"%>
<%
response.setContentType("application/vnd.ms-excel;charset=UTF-8");
response.setHeader("Content-Disposition","attachment; filename="+CynthiaUtil.getToday()+".xls");

out.clear();
Key key = (Key)session.getAttribute("key");
Long keyId = (Long)session.getAttribute("kid");


if(keyId == null || keyId <= 0 || key == null){
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot() + "user/logou.do?targetUrl=" + ConfigUtil.getTargetUrl(request));
	return;
}
String filterIdStr = request.getParameter("filterId");

String beforeNumStr = request.getParameter("beforeNum"); //前N条数据

int beforeNum = 0;
if(beforeNumStr != null && beforeNumStr.length() > 0){
	try{
		beforeNum = Integer.parseInt(beforeNumStr);	
	}catch(Exception e){
		e.printStackTrace();
	}
}


String[] dataIds = request.getParameterValues("dataIds");

if(filterIdStr == null||"".equals(filterIdStr))
{
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}

DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(),keyId);
UUID filterId = DataAccessFactory.getInstance().createUUID(filterIdStr);
Filter filter = das.queryFilter(filterId);

if(filter==null)
{
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}

ExportDataManager.excelExport(das,filter, keyId, key.getUsername(), dataIds, beforeNum , response.getOutputStream());
response.getOutputStream().flush();
response.getOutputStream().close();
%>





















