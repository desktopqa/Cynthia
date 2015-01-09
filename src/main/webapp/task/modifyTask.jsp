<%@page import="java.util.Enumeration"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Field.Type"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Data"%>
<%@page import="com.sogou.qadev.service.cynthia.util.Date"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Field.DataType"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Pair"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.HashSet"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>

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
	
	UUID templateId = DataAccessFactory.getInstance().createUUID(request.getParameter("templateId"));
	
	UUID taskId = DataAccessFactory.getInstance().createUUID(request.getParameter("id"));
	
	Data data = das.queryData(taskId,templateId);
	if(data == null){
		out.println(ErrorManager.getErrorXml(ErrorType.data_not_found_inDb));
		return;
	}
	
	Template template = das.queryTemplate(data.getTemplateId());
	
	if(template == null){
		out.println(ErrorManager.getErrorXml(ErrorType.template_not_found));
		return;
	}
	
	//备份data
	data = (Data)data.clone();
	
	Map<String, Pair<Object, Object>> baseValueMap = new LinkedHashMap<String, Pair<Object, Object>>();
	Map<UUID, Pair<Object, Object>> extValueMap = new LinkedHashMap<UUID, Pair<Object, Object>>();
	
	//set title
	String title = request.getParameter("title");
	if(!data.getTitle().equals(title)){
		baseValueMap.put("title", new Pair<Object, Object>(data.getTitle(), title));
	}
	
	data.setTitle(title);
	
	//set description
	String description = request.getParameter("description");
	if(description != null && description.length() == 0){
		description = null;
	}
	
	if(data.getDescription() == null && description != null
	|| data.getDescription() != null && description == null
		|| data.getDescription() != null && description != null && !data.getDescription().equals(description)){
		baseValueMap.put("description", new Pair<Object, Object>(data.getDescription(), description));
	}
	
	data.setDescription(description);
	
	//set assignUser
	String assignUser = request.getParameter("assignUser");
	if(assignUser != null && assignUser.length() == 0){
		assignUser = null;
	}
	
	if(assignUser != null && assignUser.equals("null")){
		assignUser = "";
	}
	
	if(data.getAssignUsername() == null && assignUser != null
	|| data.getAssignUsername() != null && assignUser == null
		|| data.getAssignUsername() != null && assignUser != null && !data.getAssignUsername().equals(assignUser)){
		baseValueMap.put("assignUser", new Pair<Object, Object>(data.getAssignUsername(), assignUser));
	}
	
	data.setAssignUsername(assignUser);
	
	//set statusId
	UUID statusId = DataAccessFactory.getInstance().createUUID(request.getParameter("statusId"));
	if(!data.getStatusId().equals(statusId)){
		baseValueMap.put("statusId", new Pair<Object, Object>(data.getStatusId(), statusId));
	}
	
	data.setStatusId(statusId);
	
	//set logCreateUser
	data.setObject("logCreateUser", key.getUsername());
	
	//set logActionId
	String actionIdStr = request.getParameter("actionId");
	if(actionIdStr != null && actionIdStr.length() == 0){
		actionIdStr = null;
	}
	
	if(actionIdStr != null){
		data.setObject("logActionId", DataAccessFactory.getInstance().createUUID(actionIdStr));
	}
	else{
		data.setObject("logActionId", null);
	}
	
	//set logActionComment
	String actionComment = request.getParameter("actionComment");
	if(actionComment != null && actionComment.length() == 0){
		actionComment = null;
	}
	
	data.setObject("logActionComment", actionComment);
	
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
				
				if(field.getType().equals(Type.t_selection)){
					if(field.getDataType().equals(DataType.dt_single)){
						UUID oldOptionId = data.getSingleSelection(field.getId());
						
						UUID newOptionId = null;
						if(fieldValue != null){
							newOptionId = DataAccessFactory.getInstance().createUUID(fieldValue);
						}
						
						if(oldOptionId == null && newOptionId != null
								|| oldOptionId != null && newOptionId == null
									|| oldOptionId != null && newOptionId != null && !oldOptionId.equals(newOptionId)){
							extValueMap.put(field.getId(), new Pair<Object, Object>(oldOptionId, newOptionId));
						}
						data.setSingleSelection(field.getId(), newOptionId);
					}else if(field.getDataType().equals(DataType.dt_multiple)){
						UUID[] oldOptionIdArray = data.getMultiSelection(field.getId());
						
						UUID[] newOptionIdArray = null;
						if(fieldValue != null){
							String[] newOptionIdStrArray = fieldValue.split("\\,");
							newOptionIdArray = new UUID[newOptionIdStrArray.length];
							for(int i = 0; i < newOptionIdStrArray.length; i++){
								newOptionIdArray[i] = DataAccessFactory.getInstance().createUUID(newOptionIdStrArray[i]);
							}
						}
						
						if(oldOptionIdArray == null && newOptionIdArray != null
								|| oldOptionIdArray != null && newOptionIdArray == null
								|| oldOptionIdArray != null && newOptionIdArray != null && !new HashSet<UUID>(Arrays.asList(oldOptionIdArray)).equals(new HashSet<UUID>(Arrays.asList(newOptionIdArray)))){
							extValueMap.put(field.getId(), new Pair<Object, Object>(oldOptionIdArray, newOptionIdArray));
						}
						
						data.setMultiSelection(field.getId(), newOptionIdArray);
					}
				}
				else if(field.getType().equals(Type.t_reference)){
					if(field.getDataType().equals(DataType.dt_single)){
						UUID oldReferenceId = data.getSingleReference(field.getId());
						
						UUID newReferenceId = null;
						if(fieldValue != null){
							newReferenceId = DataAccessFactory.getInstance().createUUID(fieldValue);
						}
						
						if(oldReferenceId == null && newReferenceId != null
								|| oldReferenceId != null && newReferenceId == null
									|| oldReferenceId != null && newReferenceId != null && !oldReferenceId.equals(newReferenceId)){
							extValueMap.put(field.getId(), new Pair<Object, Object>(oldReferenceId, newReferenceId));
						}
						
						data.setSingleReference(field.getId(), newReferenceId);
				
					}
					else if(field.getDataType().equals(DataType.dt_multiple)){
						UUID[] oldReferIdArray = data.getMultiReference(field.getId());
						
						UUID[] newReferIdArray = null;
						if(fieldValue != null){
							String[] newReferIdStrArray = fieldValue.split("\\,");
							newReferIdArray = new UUID[newReferIdStrArray.length];
							for(int i = 0; i < newReferIdStrArray.length; i++){
								newReferIdArray[i] = DataAccessFactory.getInstance().createUUID(newReferIdStrArray[i]);
							}
						}
						
						if(oldReferIdArray == null && newReferIdArray != null
								|| oldReferIdArray != null && newReferIdArray == null
								|| oldReferIdArray != null && newReferIdArray != null && !new HashSet<UUID>(Arrays.asList(oldReferIdArray)).equals(new HashSet<UUID>(Arrays.asList(newReferIdArray)))){
							extValueMap.put(field.getId(), new Pair<Object, Object>(oldReferIdArray, newReferIdArray));
						}
						
						data.setMultiReference(field.getId(), newReferIdArray);
					}
				}
				else if(field.getType().equals(Type.t_attachment)){
					UUID[] oldAttachIdArray = data.getAttachments(field.getId());
					
					UUID[] newAttachIdArray = null;
					if(fieldValue != null){
						String[] newAttachIdStrArray = fieldValue.split("\\,");
						newAttachIdArray = new UUID[newAttachIdStrArray.length];
						for(int i = 0; i < newAttachIdStrArray.length; i++){
							newAttachIdArray[i] = DataAccessFactory.getInstance().createUUID(newAttachIdStrArray[i]);
						}
					}
					
					if(oldAttachIdArray == null && newAttachIdArray != null
					|| oldAttachIdArray != null && newAttachIdArray == null
					|| oldAttachIdArray != null && newAttachIdArray != null && !new HashSet<UUID>(Arrays.asList(oldAttachIdArray)).equals(new HashSet<UUID>(Arrays.asList(newAttachIdArray)))){
				extValueMap.put(field.getId(), new Pair<Object, Object>(oldAttachIdArray, newAttachIdArray));
					}
					
					data.setMultiReference(field.getId(), newAttachIdArray);
				}else if(field.getType().equals(Type.t_input)){
					if(field.getDataType().equals(DataType.dt_integer)){
						Integer oldInteger = data.getInteger(field.getId());
						
						Integer newInteger = null;
						if(fieldValue != null)	{
							try{
								newInteger = Integer.valueOf(fieldValue);
							}
							catch(Exception e){}
						}
						
						if(oldInteger == null && newInteger != null
								|| oldInteger != null && newInteger == null
								||oldInteger != null && newInteger != null && !oldInteger.equals(newInteger)){
							extValueMap.put(field.getId(), new Pair<Object, Object>(oldInteger, newInteger));
						}
						
						data.setInteger(field.getId(), newInteger);
					}
					else if(field.getDataType().equals(DataType.dt_long)){
						Long oldLong = data.getLong(field.getId());
						
						Long newLong = null;
						if(fieldValue != null){
							try{
								newLong = Long.valueOf(fieldValue);
							}
							catch(Exception e){}
						}
						
						if(oldLong == null && newLong != null
								|| oldLong != null && newLong == null
								||oldLong != null && newLong != null && !oldLong.equals(newLong)){
							extValueMap.put(field.getId(), new Pair<Object, Object>(oldLong, newLong));
						}
						
						data.setLong(field.getId(), newLong);
					}
					else if(field.getDataType().equals(DataType.dt_float)){
						Float oldFloat = data.getFloat(field.getId());
						
						Float newFloat = null;
						if(fieldValue != null){
							try{
								newFloat = Float.valueOf(fieldValue);
							}
							catch(Exception e){}
						}
						
						if(oldFloat == null && newFloat != null
								|| oldFloat != null && newFloat == null
								||oldFloat != null && newFloat != null && !oldFloat.equals(newFloat)){
							extValueMap.put(field.getId(), new Pair<Object, Object>(oldFloat, newFloat));
						}
						
						data.setFloat(field.getId(), newFloat);
					}
					else if(field.getDataType().equals(DataType.dt_double)){
						Double oldDouble = data.getDouble(field.getId());
						
						Double newDouble = null;
						if(fieldValue != null){
							try{
								newDouble = Double.valueOf(fieldValue);
							}
							catch(Exception e){}
						}
						
						if(oldDouble == null && newDouble != null
								|| oldDouble != null && newDouble == null
								||oldDouble != null && newDouble != null && !oldDouble.equals(newDouble)){
							extValueMap.put(field.getId(), new Pair<Object, Object>(oldDouble, newDouble));
						}
						
						data.setDouble(field.getId(), newDouble);
					}
					else if(field.getDataType().equals(DataType.dt_string) || field.getDataType().equals(DataType.dt_text) || field.getDataType().equals(DataType.dt_editor)){
						String oldString = data.getString(field.getId());
						
						if(oldString == null && fieldValue != null
								|| oldString != null && fieldValue == null
								||oldString != null && fieldValue != null && !oldString.equals(fieldValue)){
							extValueMap.put(field.getId(), new Pair<Object, Object>(oldString, fieldValue));
						}
				
						data.setString(field.getId(), fieldValue);
					}
					else if(field.getDataType().equals(DataType.dt_timestamp)){
						Date oldDate = data.getDate(field.getId());
						Date newDate = null;
						if(fieldValue != null){
							try{
								newDate = Date.valueOf(fieldValue,field.getTimestampFormat());
							}
							catch(Exception e){
								e.printStackTrace();
							}
						}
						System.out.println(newDate);
						if(oldDate == null && newDate != null
								|| oldDate != null && newDate == null
								||oldDate != null && newDate != null && !oldDate.equals(newDate)){
							extValueMap.put(field.getId(), new Pair<Object, Object>(oldDate, newDate));
						}
						data.setDate(field.getId(), newDate);
					}
				}
			}
	}
	
	data.setObject("logBaseValueMap", baseValueMap);
	data.setObject("logExtValueMap", extValueMap);
	long startModifyTaskTime = System.currentTimeMillis();
	Pair<ErrorCode, String> result = das.modifyData(data);
	
	if(result.getFirst().equals(ErrorCode.success)){
		das.commitTranscation();
		long endModifyTaskTime = System.currentTimeMillis();
		long spendTime = endModifyTaskTime-startModifyTaskTime;
		System.out.println(key.getUsername()+" modify a data :"+data.getId()+" spend time :"+spendTime);
		out.println(ErrorManager.getCorrectXml());
	}
	else{
		das.rollbackTranscation();
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><isError>true</isError>");
		out.println(result.getSecond());
		out.println("</root>");
	}
%>