<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.FieldNameAccessSessionMySQL"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.TemplateOperateLog"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>
<%@page import="java.util.Map"%>
<%@page import="com.sogou.qadev.cache.impl.FieldNameCache"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
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
	
	UUID fieldId = DataAccessFactory.getInstance().createUUID(request.getParameter("id"));
	
	Field field = template.getField(fieldId);
	
	Field beforeField = field.clone();
	
	template.removeField(fieldId , templateId);
	
	ErrorCode errorCode = das.updateTemplate(template);
	
	if(errorCode.equals(ErrorCode.success)){
		
		//TODO是否需要从field_name_map中删除 
		String fieldColName = FieldNameCache.getInstance().getFieldName(fieldId , templateId);
		if(new FieldNameAccessSessionMySQL().removeFieldColNameById(fieldId.getValue(), templateId.getValue())){
	FieldNameCache.getInstance().remove(fieldId.getValue(),templateId.getValue());
	das.updateCache(DataAccessAction.update, template.getId().getValue(), template);
	//记录修改日志
	TemplateOperateLog tol = new TemplateOperateLog();
	tol.setTemplateId(templateId.getValue());
	tol.setFieldId(beforeField.getId().getValue());
	tol.setFieldName(beforeField.getName());
	tol.setOperateType(TemplateOperateLog.DELETE);
	tol.setCreateTime(Timestamp.valueOf(CynthiaUtil.toLocalDateString(null)));
	tol.setCreateUser(key.getUsername());
	tol.setBefore(beforeField.toXMLString());
	tol.setAfter("");
	das.addTemplateOpreateLog(tol);
	out.println(ErrorManager.getCorrectXml());
		}else{
	out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
		}
	}
	else{
		out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
	}
%>