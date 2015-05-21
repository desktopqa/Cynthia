<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.TemplateOperateLog"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option.Forbidden"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>

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
	
	UUID fieldId = DataAccessFactory.getInstance().createUUID(request.getParameter("fieldId"));
	
	Field field = template.getField(fieldId);
	
	String fieldXmlBefore = field.toXMLString();
	
	if(field == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.field_update_error));
		return;
	}
	
	UUID optionId = DataAccessFactory.getInstance().createUUID(request.getParameter("id"));
	
	Option option = field.getOption(optionId);
	if(option == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.field_option_not_fount));
		return;
	}
	
	option.setName(request.getParameter("name"));
	option.setDescription(request.getParameter("description"));
	option.setForbidden(Forbidden.valueOf(request.getParameter("forbidden")));
	
	ErrorCode errorCode = das.updateTemplate(template);
	if(errorCode.equals(ErrorCode.success)){
		das.updateCache(DataAccessAction.update, template.getId().getValue(),template);
		//记录修改日志
		TemplateOperateLog tol = new TemplateOperateLog();
		tol.setTemplateId(templateId.getValue());
		tol.setFieldId(field.getId().getValue());
		tol.setFieldName(field.getName());
		tol.setOperateType(TemplateOperateLog.MODIFY);
		tol.setCreateTime(Timestamp.valueOf(CynthiaUtil.toLocalDateString(null)));
		tol.setCreateUser(key.getUsername());
		tol.setBefore(fieldXmlBefore);
		tol.setAfter(field.toXMLString());
		das.addTemplateOpreateLog(tol);
		out.println(ErrorManager.getCorrectXml());
	}else{
		out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
	}
%>