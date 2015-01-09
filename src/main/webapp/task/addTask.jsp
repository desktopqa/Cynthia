<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Field.DataType"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Data"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field.Type"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field.DataType"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Pair"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.LinkedHashMap"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.Date"%>

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
	
	//获取templateId
	UUID templateId = DataAccessFactory.getInstance().createUUID(request.getParameter("templateId"));
	//获取actionId
	UUID actionId = DataAccessFactory.getInstance().createUUID(request.getParameter("actionId"));
	//获取statusId
	UUID statusId = DataAccessFactory.getInstance().createUUID(request.getParameter("statusId"));
	//获取title
	String title = request.getParameter("title");
	//获取description
	String description = request.getParameter("description");
	//获取assignUser
	String assignUser = request.getParameter("assignUser");
	//获取actionComment
	String actionComment = request.getParameter("actionComment");
	
	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
	
	Data data = das.addData(templateId);
	if(data == null){
		out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
		return;
	}
	
	Map<String, Pair<Object, Object>> baseValueMap = new LinkedHashMap<String, Pair<Object, Object>>();
	Map<UUID, Pair<Object, Object>> extValueMap = new LinkedHashMap<UUID, Pair<Object, Object>>();
	
	data.setTitle(title);
	baseValueMap.put("title", new Pair<Object, Object>(null, title));
	
	if(description != null && description.length() == 0){
		description = null;
	}
	
	if(description != null){
		data.setDescription(description);
		baseValueMap.put("description", new Pair<Object, Object>(null, description));
	}
	
	data.setAssignUsername(assignUser);
	baseValueMap.put("assignUser", new Pair<Object, Object>(null, assignUser));
	
	data.setStatusId(statusId);
	baseValueMap.put("statusId", new Pair<Object, Object>(null, statusId));
	
	data.setObject("logCreateUser", key.getUsername());
	data.setObject("logActionId", actionId);
	
	if(actionComment != null && actionComment.length() == 0){
		actionComment = null;
	}
	
	if(actionComment != null){
		data.setObject("logActionComment", actionComment);
	}
	
	Template template = das.queryTemplate(templateId);
	if(template == null){
		out.println(ErrorManager.getErrorXml(ErrorType.template_not_found));
		return;
	}
	
	//获取fieldValues
	Enumeration enumeration = request.getParameterNames();
	while(enumeration.hasMoreElements()){
			String param = (String)enumeration.nextElement();
			if(param.startsWith("field")){	
		UUID fieldId = DataAccessFactory.getInstance().createUUID(param.substring(5));
		
		Field field = template.getField(fieldId);
		if(field == null){
			continue;
		}
		
		String fieldValue = request.getParameter(param);
		if(fieldValue != null && fieldValue.length() == 0){
			fieldValue = null;
		}
		
		if(fieldValue == null){
			continue;
		}
		
		if(field.getType().equals(Type.t_selection)){
			if(field.getDataType().equals(DataType.dt_single)){
		UUID optionId = DataAccessFactory.getInstance().createUUID(fieldValue);
		
		data.setSingleSelection(fieldId, optionId);
		extValueMap.put(fieldId, new Pair<Object, Object>(null, optionId));
			}
			else if(field.getDataType().equals(DataType.dt_multiple)){
		String[] optionIdStrArray = fieldValue.split(",");
		UUID[] optionIdArray = new UUID[optionIdStrArray.length];
		for(int i = 0; i < optionIdArray.length; i++){
			optionIdArray[i] = DataAccessFactory.getInstance().createUUID(optionIdStrArray[i]);
		}
		
		data.setMultiSelection(fieldId, optionIdArray);
		extValueMap.put(fieldId, new Pair<Object, Object>(null, optionIdArray));
			}
		}
		else if(field.getType().equals(Type.t_reference)){
			if(field.getDataType().equals(DataType.dt_single)){
		UUID referenceId = DataAccessFactory.getInstance().createUUID(fieldValue);
		
		data.setSingleReference(fieldId, referenceId);
		extValueMap.put(fieldId, new Pair<Object, Object>(null, referenceId));
			}
			else if(field.getDataType().equals(DataType.dt_multiple)){
		String[] referenceIdStrArray = fieldValue.split(",");
		UUID[] referenceIdArray = new UUID[referenceIdStrArray.length];
		for(int i = 0; i < referenceIdArray.length; i++){
			referenceIdArray[i] = DataAccessFactory.getInstance().createUUID(referenceIdStrArray[i]);
		}
		
		data.setMultiReference(fieldId, referenceIdArray);
		extValueMap.put(fieldId, new Pair<Object, Object>(null, referenceIdArray));
			}
		}
		else if(field.getType().equals(Type.t_attachment)){
			String[] attachmentIdStrArray = fieldValue.split(",");
			UUID[] attachmentIdArray = new UUID[attachmentIdStrArray.length];
			for(int i = 0; i < attachmentIdArray.length; i++){
		attachmentIdArray[i] = DataAccessFactory.getInstance().createUUID(attachmentIdStrArray[i]);
			}
			
			data.setAttachments(fieldId, attachmentIdArray);
			extValueMap.put(fieldId, new Pair<Object, Object>(null, attachmentIdArray));
		}
		else if(field.getType().equals(Type.t_input)){
			if(field.getDataType().equals(DataType.dt_integer)){
		Integer newInteger = null;
		try{
			newInteger = Integer.valueOf(fieldValue);
		}
		catch(Exception e){}
		
		if(newInteger != null){
			data.setInteger(fieldId, newInteger);
			extValueMap.put(fieldId, new Pair<Object, Object>(null, newInteger));
		}
			}
			if(field.getDataType().equals(DataType.dt_long)){
		Long newLong = null;
		try{
			newLong = Long.valueOf(fieldValue);
		}
		catch(Exception e){}
		
		if(newLong != null){
			data.setLong(fieldId, newLong);
			extValueMap.put(fieldId, new Pair<Object, Object>(null, newLong));
		}
			}
			if(field.getDataType().equals(DataType.dt_float)){
		Float newFloat = null;
		try{
			newFloat = Float.valueOf(fieldValue);
		}
		catch(Exception e){}
		
		if(newFloat != null){
			data.setFloat(fieldId, newFloat);
			extValueMap.put(fieldId, new Pair<Object, Object>(null, newFloat));
		}
			}
			if(field.getDataType().equals(DataType.dt_double)){
		Double newDouble = null;
		try{
			newDouble = Double.valueOf(fieldValue);
		}
		catch(Exception e){}
		
		if(newDouble != null){
			data.setDouble(fieldId, newDouble);
			extValueMap.put(fieldId, new Pair<Object, Object>(null, newDouble));
		}
			}
			if(field.getDataType().equals(DataType.dt_string) || field.getDataType().equals(DataType.dt_text) || field.getDataType().equals(DataType.dt_editor)){
		data.setString(fieldId, fieldValue);
		extValueMap.put(fieldId, new Pair<Object, Object>(null, fieldValue));
			}
			if(field.getDataType().equals(DataType.dt_timestamp)){
		Date newDate = null;
		try{
			newDate = Date.valueOf(fieldValue);
		}
		catch(Exception e){}
		
		if(newDate != null){
			data.setDate(fieldId, newDate);
			extValueMap.put(fieldId, new Pair<Object, Object>(null, newDate));
		}
			}
		}		
			}
	}
	
	data.setObject("logBaseValueMap", baseValueMap);
	data.setObject("logExtValueMap", extValueMap);
	
	long statAddTaskTime = System.currentTimeMillis();
	Pair<ErrorCode, String> result = das.modifyData(data);
	long endAddTaskTime = System.currentTimeMillis();
	long spendTime = endAddTaskTime-statAddTaskTime;
	
	if(result.getFirst().equals(ErrorCode.success)){
		try{
	ErrorCode eCode = das.commitTranscation();
	if(!eCode.equals(ErrorCode.success))
		throw new Exception("error");
	else{
		das.updateCache(DataAccessAction.update, data.getId().getValue(),data);
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><isError>false</isError>";
		xml += "<taskId>" + data.getId() + "</taskId><taskName>" +XMLUtil.toSafeXMLString(data.getTitle())+ "</taskName></root>";
		out.println(xml);
	}
		}catch(Exception e)
		{
	das.rollbackTranscation();
	System.err.println("数据提交失败");
	e.printStackTrace();
	out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><isError>true</isError>");
	out.println(result.getSecond());
	out.println("</root>");
		}
	}
	else{
		try{
	System.err.println("数据修改失败");
	das.rollbackTranscation();
		}catch(Exception e)
		{
	e.printStackTrace();
		}
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><isError>true</isError>");
		out.println(result.getSecond());
		out.println("</root>");
	}
%>