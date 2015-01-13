<%@page import="com.sogou.qadev.service.cynthia.dao.FieldNameMapMySQL"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.FieldNameMapMySQL"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.TemplateOperateLog"%>
<%@page import="com.sogou.qadev.cache.impl.FieldNameCache"%>
<%@ page contentType="text/xml; charset=UTF-8" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
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
		UUID fieldId = DataAccessFactory.getInstance().createUUID(request.getParameter("fieldId"));
		Field field = template.getField(fieldId);
		Field beforeField = field.clone();
		template.removeField(fieldId , templateId);
		ErrorCode errorCode = das.updateTemplate(template);
		if(errorCode.equals(ErrorCode.success)){
			String fieldColName = FieldNameCache.getInstance().getFieldName(fieldId , templateId);
			FieldNameCache.getInstance().remove(fieldId.getValue(),templateId.getValue());
			das.updateCache(DataAccessAction.update, template.getId().getValue(), template);
			//记录修改日志
			TemplateOperateLog tol = new TemplateOperateLog();
			tol.setTemplateId(templateId.getValue());
			tol.setFieldId(beforeField.getId().getValue());
			tol.setFieldName(beforeField.getName());
			tol.setOperateType(TemplateOperateLog.DELETE);
			tol.setCreateTime(new Timestamp(System.currentTimeMillis()));
			tol.setCreateUser(key.getUsername());
			tol.setBefore(beforeField.toXMLString());
			tol.setAfter("");
			das.addTemplateOpreateLog(tol);
			out.println(ErrorManager.getCorrectXml());
		}
		else{
			out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
		}
		return;
	}
	
	if("move".equals(flag))
	{
		int rowIndex = Integer.parseInt(request.getParameter("rowIndex"));
		int columnIndex = Integer.parseInt(request.getParameter("columnIndex"));
		int positionIndex = Integer.parseInt(request.getParameter("positionIndex"));
		UUID fieldId = DataAccessFactory.getInstance().createUUID(request.getParameter("fieldId"));
		Field field = template.getField(fieldId);
		template.moveField(field, rowIndex, columnIndex, positionIndex);
		ErrorCode errorCode = das.updateTemplate(template);
		if(errorCode.equals(ErrorCode.success)){
			das.updateCache(DataAccessAction.update, template.getId().getValue(), template);
			out.println(ErrorManager.getCorrectXml());
		}
		else{
			out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
		}
		return;
	}
	
	Type type = Type.valueOf(request.getParameter("fieldType"));
	DataType dataType = null;
	if(!type.equals(Type.t_attachment))
	{
		dataType = DataType.valueOf(request.getParameter("fieldDataType"));
	}
	
	String fieldName = request.getParameter("name");
	if("add".equals(flag))
	{
		//增加一个字段
		Field field = template.addField(type, dataType);
		if(field == null)
		{
			out.println(ErrorManager.getErrorXml(ErrorType.field_update_error));
			return;
		}
		int rowIndex = Integer.parseInt(request.getParameter("rowIndex"));
		int columnIndex = Integer.parseInt(request.getParameter("columnIndex"));
		int positionIndex = Integer.parseInt(request.getParameter("positionIndex"));
		template.addField(field, rowIndex, columnIndex, positionIndex);
		
		field.setName(request.getParameter("fieldName"));
		field.setDescription(request.getParameter("fieldDescription"));
		field.setFieldTip(request.getParameter("fieldTip"));
		field.setTimestampFormat(request.getParameter("timestampFormat"));
		field.setDateCurTime(Boolean.parseBoolean(request.getParameter("dateCurTime")));
		field.setFieldSize(request.getParameter("fieldSize"));
		field.setDefaultValue(request.getParameter("defaultValue"));
		String controlFieldIdStr = request.getParameter("controlFieldId");
		
		if(controlFieldIdStr != null)
			field.setControlFieldId(DataAccessFactory.getInstance().createUUID(controlFieldIdStr));
		
		
		//control option ids
		Set<UUID> controlOptionIdSet = new LinkedHashSet<UUID>();
		
		String[] controlOptionIdStrArray = (String[])ArrayUtil.format(request.getParameterValues("controlOptionId"), new String[0]);
		for(String controlOptionIdStr : controlOptionIdStrArray)
			controlOptionIdSet.add(DataAccessFactory.getInstance().createUUID(controlOptionIdStr));
	
		field.setControlOptionIds(controlOptionIdSet);
		
		//control role ids
		String[] controlRoleIdArray = (String[])ArrayUtil.format(request.getParameterValues("controlRoleId"), new String[0]);
		field.setControlRoleIds(new LinkedHashSet<String>(Arrays.asList(controlRoleIdArray)));
		
		//control action ids
		String[] controlActionIdArray = (String[])ArrayUtil.format(request.getParameterValues("controlActionId"), new String[0]);
		field.setControlActionIds(new LinkedHashSet<String>(Arrays.asList(controlActionIdArray)));
		
		//action ids
		Set<UUID> actionIdSet = new LinkedHashSet<UUID>();
		
		String[] actionIdStrArray = (String[])ArrayUtil.format(request.getParameterValues("actionId"), new String[0]);
		for(String actionIdStr : actionIdStrArray)
			actionIdSet.add(DataAccessFactory.getInstance().createUUID(actionIdStr));
		
		field.setActionIds(actionIdSet);
		
		//先添加字段，再更新表单
		String fieldColName = FieldNameMapMySQL.getInstance().getOneFieldName(field, template.getId().getValue());
		if(fieldColName == null || fieldColName.length() == 0 ){
			out.println(ErrorManager.getErrorXml(ErrorType.field_update_error));
			return;			
		}
		else{
			if(das.addFieldColName(template.getId().getValue(), fieldColName, field.getId().getValue(),  FieldNameMapMySQL.getInstance().getFieldColNameType(field))){
				FieldNameCache.getInstance().set(field.getId().getValue(), fieldColName);
				ErrorCode errorCode = das.updateTemplate(template);
				if(errorCode.equals(ErrorCode.success)){
			das.updateCache(DataAccessAction.update, template.getId().getValue(),template);
			
			//记录修改日志
			TemplateOperateLog tol = new TemplateOperateLog();
			tol.setTemplateId(templateId.getValue());
			tol.setFieldId(field.getId().getValue());
			tol.setFieldName(field.getName());
			tol.setOperateType(TemplateOperateLog.ADD);
			tol.setCreateTime(new Timestamp(System.currentTimeMillis()));
			tol.setCreateUser(key.getUsername());
			tol.setBefore("");
			tol.setAfter(field.toXMLString());
			das.addTemplateOpreateLog(tol);
				}else{
			out.println(ErrorManager.getErrorXml(ErrorType.field_update_error));
			return;	
				}
			}else{
				out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
				return;
			}
			if(das.addFieldColName(template.getId().getValue(), fieldColName, field.getId().getValue(),  FieldNameMapMySQL.getInstance().getFieldColNameType(field))){
				FieldNameCache.getInstance().set(field.getId().getValue(), fieldColName);
				ErrorCode errorCode = das.updateTemplate(template);
				if(errorCode.equals(ErrorCode.success)){
			das.updateCache(DataAccessAction.update, template.getId().getValue(),template);
			
			//记录修改日志
			TemplateOperateLog tol = new TemplateOperateLog();
			tol.setTemplateId(templateId.getValue());
			tol.setFieldId(field.getId().getValue());
			tol.setFieldName(field.getName());
			tol.setOperateType(TemplateOperateLog.ADD);
			tol.setCreateTime(new Timestamp(System.currentTimeMillis()));
			tol.setCreateUser(key.getUsername());
			tol.setBefore("");
			tol.setAfter(field.toXMLString());
			das.addTemplateOpreateLog(tol);
			out.println(ErrorManager.getCorrectXml());
				}else{
			out.println(ErrorManager.getErrorXml(ErrorType.field_update_error));
			return;	
				}
			}else{
				out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
				return;
			}
		}
	}else if("update".equals(flag))
	{
		//修改一个字段
		UUID fieldId = DataAccessFactory.getInstance().createUUID(request.getParameter("fieldId"));
		Field field = template.getField(fieldId);
		String fieldXmlBefore = field.toXMLString();
		if(field == null)
		{
			out.println(ErrorManager.getErrorXml(ErrorType.field_update_error));
			return;
		}
		field.setName(request.getParameter("fieldName"));
		field.setTimestampFormat(request.getParameter("timestampFormat"));
		field.setDescription(request.getParameter("fieldDescription"));
		field.setFieldTip(request.getParameter("fieldTip"));
		field.setFieldSize(request.getParameter("fieldSize"));
		field.setDateCurTime(Boolean.parseBoolean(request.getParameter("dateCurTime")));
		
		field.setDefaultValue(request.getParameter("defaultValue"));
		String controlFieldIdStr = request.getParameter("controlFieldId");
		if(controlFieldIdStr != null)
			field.setControlFieldId(DataAccessFactory.getInstance().createUUID(controlFieldIdStr));
		
		//control option ids
		Set<UUID> controlOptionIdSet = new LinkedHashSet<UUID>();
		
		String[] controlOptionIdStrArray = (String[])ArrayUtil.format(request.getParameterValues("controlOptionId"), new String[0]);
		for(String controlOptionIdStr : controlOptionIdStrArray)
			controlOptionIdSet.add(DataAccessFactory.getInstance().createUUID(controlOptionIdStr));
	
		field.setControlOptionIds(controlOptionIdSet);
		
		//control role ids
		String[] controlRoleIdArray = (String[])ArrayUtil.format(request.getParameterValues("controlRoleId"), new String[0]);
		field.setControlRoleIds(new LinkedHashSet<String>(Arrays.asList(controlRoleIdArray)));
		
		//control action ids
		String[] controlActionIdArray = (String[])ArrayUtil.format(request.getParameterValues("controlActionId"), new String[0]);
		field.setControlActionIds(new LinkedHashSet<String>(Arrays.asList(controlActionIdArray)));
		
		//action ids
		Set<UUID> actionIdSet = new LinkedHashSet<UUID>();
		
		String[] actionIdStrArray = (String[])ArrayUtil.format(request.getParameterValues("actionId"), new String[0]);
		for(String actionIdStr : actionIdStrArray)
			actionIdSet.add(DataAccessFactory.getInstance().createUUID(actionIdStr));
		
		field.setActionIds(actionIdSet);
		ErrorCode errorCode = das.updateTemplate(template);
		if(errorCode.equals(ErrorCode.success)){
			das.updateCache(DataAccessAction.update, template.getId().getValue(),template);
			//记录修改日志
			TemplateOperateLog tol = new TemplateOperateLog();
			tol.setTemplateId(templateId.getValue());
			tol.setFieldId(field.getId().getValue());
			tol.setFieldName(field.getName());
			tol.setOperateType(TemplateOperateLog.MODIFY);
			tol.setCreateTime(new Timestamp(System.currentTimeMillis()));
			tol.setCreateUser(key.getUsername());
			tol.setBefore(fieldXmlBefore);
			tol.setAfter(field.toXMLString());
			das.addTemplateOpreateLog(tol);
			out.println(ErrorManager.getCorrectXml());
		}else{
			out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
		}
	}
%>