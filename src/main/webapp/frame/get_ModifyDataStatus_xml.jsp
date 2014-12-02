<%@page import="com.sogou.qadev.service.cynthia.service.DataManager"%>
<%@page import="java.util.HashMap"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.HashSet"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Pair"%>
<%@ page import="java.util.Arrays"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
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
	
	String statName = request.getParameter("statName");
	if(statName == null){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	
	String actionDesc = request.getParameter("actionDesc");
	
	StringBuffer resultXml = new StringBuffer();
	resultXml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	resultXml.append("<root>");
	resultXml.append("<isError>false</isError>");
	resultXml.append("<results>");
	
	String[] taskIdStrArray = request.getParameterValues("dataId");
	if(taskIdStrArray == null || taskIdStrArray.length == 0){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
	
	Map<UUID,Template> templateMap = new HashMap<UUID,Template>();
	Map<UUID,Flow> flowMap = new HashMap<UUID,Flow>();
	
	for(String taskIdStr : taskIdStrArray){
		UUID taskId = DataAccessFactory.getInstance().createUUID(taskIdStr);
		Data data = das.queryData(taskId);
		
		if(data == null){
			continue;
		}
		
		if(data.getChangeLogs().length == 0){
			continue;
		}
		
		if(templateMap.get(data.getTemplateId()) == null){
			Template template = das.queryTemplate(data.getTemplateId());
			if(template != null)
				templateMap.put(template.getId(), template);
		}
			
		Template template = templateMap.get(data.getTemplateId());
		
		if(template == null){
			continue;
		}
		
		if(flowMap.get(template.getFlowId()) == null){
			Flow flow = das.queryFlow(template.getFlowId());
			if(flow != null)
				flowMap.put(flow.getId(), flow);
		}
			
		Flow flow = flowMap.get(template.getFlowId());
		
		if(flow == null){
			continue;
		}
		
		Stat stat = flow.getStat(data.getStatusId());
		if(stat == null){
			continue;
		}
		//备份data
		data = (Data)data.clone();
		
		Map<String, Pair<Object, Object>> baseValueMap = new LinkedHashMap<String, Pair<Object, Object>>();
		Map<UUID, Pair<Object, Object>> extValueMap = new LinkedHashMap<UUID, Pair<Object, Object>>();
		
		
		Stat newStat = flow.getStat(statName);
		//statusId
		if(newStat != null){
			baseValueMap.put("statusId", new Pair<Object, Object>(data.getStatusId(), newStat.getId()));
			data.setStatusId(newStat.getId());
		}else{
			resultXml.append(DataManager.getInstance().makeModifyStatResult(data.getTitle(), false));
			continue;
		}
		
		//logCreateUser
		data.setObject("logCreateUser", key.getUsername());
		
		//logActionId
		data.setObject("logActionId", null);
		
		//logActionComment
		
		data.setObject("logActionComment", "状态批量修改" + (actionDesc == null ? "" : ":"+actionDesc));
		
		data.setObject("logBaseValueMap", baseValueMap);
		data.setObject("logExtValueMap", extValueMap);
		
		ErrorCode errorCode = das.modifyData(data).getFirst();
		if(errorCode.equals(ErrorCode.success)){
			ErrorCode errorCode1 = das.commitTranscation();
			if(errorCode1.equals(ErrorCode.success)){
				das.updateCache(DataAccessAction.update, data.getId().getValue(),data);
				resultXml.append(DataManager.getInstance().makeModifyStatResult(data.getTitle(), true));
			}else{
				das.rollbackTranscation();
				resultXml.append(DataManager.getInstance().makeModifyStatResult(data.getTitle(), false));
			}
		}else{
			das.rollbackTranscation();
			resultXml.append(DataManager.getInstance().makeModifyStatResult(data.getTitle(), false));
		}
	}
	
	resultXml.append("</results>");
	resultXml.append("</root>");
	
	out.println(resultXml.toString());
%>