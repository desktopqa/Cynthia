<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.TemplateOperateLog"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.FieldNameAccessSessionMySQL"%>
<%@page import="com.sogou.qadev.cache.impl.FieldNameCache"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.FieldNameMapMySQL"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>
<%@ page contentType="text/xml; charset=UTF-8" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field.Type"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field.DataType"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.LinkedHashSet"%>
<%@ page import="java.util.Arrays"%>

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
	
	UUID templateId = DataAccessFactory.getInstance().createUUID(request.getParameter("templateId"));
	
	Template template = das.queryTemplate(templateId);
	
	if(template == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.template_not_found));
		return;
	}
	
	//备份template
// 	template = (Template)template.clone();
	String flag = request.getParameter("flag");
	if("remove".equals(flag))
	{
		int rowIndex = Integer.parseInt(request.getParameter("rowIndex"));
		template.removeFieldRow(rowIndex);
	}else if("add".equals(flag))
	{
		int rowIndex = Integer.parseInt(request.getParameter("rowIndex"));
		int columnCount = Integer.parseInt(request.getParameter("columnCount"));
		template.addFieldRow(rowIndex, columnCount);
	}
	
	ErrorCode errorCode = das.updateTemplate(template);
	if(errorCode.equals(ErrorCode.success)){
		das.updateCache(DataAccessAction.update, template.getId().getValue(), template);
		out.println(ErrorManager.getCorrectXml());
	}
	else{
		out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
	}
	
	
%>