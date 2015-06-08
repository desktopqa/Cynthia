<%@page import="com.sogou.qadev.service.cynthia.service.ConfigManager"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Pair"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataManager"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>
<%@page import="java.util.HashMap"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Data"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Stat"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.LinkedHashMap"%>
<%@ page import="java.util.Set"%>
<%@ page import="java.util.HashSet"%>
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
	
	String actionName = request.getParameter("actionName");
	if(actionName == null){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	if(actionName.startsWith("激活--")){
		actionName = actionName.split("\\-\\-")[1];
	}
	
	String assignUser = request.getParameter("assignUser");
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
		
		Action[] statActionArray = flow.queryStatActions(stat.getId());
		
		//判断用户是否具备执行该动作的权限
		boolean isAllow = false;

		Action action = null;
		if(!actionName.equals("编辑")){
			action = flow.getAction(actionName);
			if(action == null){
				resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), data.getAssignUsername(), false));
				continue;
			}
		}
		
		Stat lastStat = stat;
		String oldAssignUser = data.getAssignUsername();
		
		if(action == null){
			boolean isEditAllow = flow.isEditActionAllow(key.getUsername(), template.getId(), data.getAssignUsername(), data.getActionUser());
			if(!isEditAllow){
				resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), data.getAssignUsername(), false));
				continue;
			}
			
			if(statActionArray == null || statActionArray.length == 0){
				if(assignUser != null){
					resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), data.getAssignUsername(), false));
					continue;
				}
			}
			else{
				if(assignUser == null){
					resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), data.getAssignUsername(), false));
					continue;
				}
				
				String[] assignUserArray = flow.queryNodeStatAssignUsers(template.getId(), stat.getId());
				if(assignUserArray != null){
					Set<String> assignUserSet = new HashSet<String>(Arrays.asList(assignUserArray));
					if(!assignUserSet.contains(assignUser)){
						resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), data.getAssignUsername(), false));
						continue;
					}
				}
			}
		}else{
			//批量关闭的情况
			Action[] endActions = flow.getEndActions();
			Stat[] endStats = flow.getEndStats();
			
			boolean batchClose = false;
			boolean isEndStat = false;
			if(endStats != null&&endStats.length>0){
				for(Stat endStat : endStats){
					if(data.getStatusId()!=null&&(data.getStatusId().equals(endStat.getId()))){
						isEndStat = true;
						break;
					}
				}
			}
			if(!isEndStat&&endActions!=null){
				boolean isEditAllow = flow.isEditActionAllow(key.getUsername(), template.getId(), data.getAssignUsername(), data.getActionUser());
				for(Action endAction : endActions){
					if(isEditAllow&&action.getId().equals(endAction.getId())){
						isAllow = true;
						batchClose = true;
						break;						
					}
				}
			}
			//批量关闭结束
			
			
			if(statActionArray == null || statActionArray.length == 0){
				Action[] userNodeBeginActionArray = flow.queryUserNodeBeginActions(key.getUsername(), template.getId());
				if(userNodeBeginActionArray != null){
					for(Action userNodeBeginAction : userNodeBeginActionArray){
						if(userNodeBeginAction.getId().equals(action.getId())){
							isAllow = true;
							break;
						}
					}
				}
			}
			else{
				Action[] userNodeStatActionArray = flow.queryUserNodeStatActions(key.getUsername(), template.getId(), stat.getId());
				if(userNodeStatActionArray != null){
					for(Action userNodeStatAction : userNodeStatActionArray){
						if(userNodeStatAction.getId().equals(action.getId())){
							isAllow = true;
							break;
						}
					}
				}
			}
			
			if(!isAllow){
				resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), data.getAssignUsername(), false));
				continue;
			}
			
			Stat newStat = flow.getStat(action.getEndStatId());
			if(newStat == null){
				resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), data.getAssignUsername(), false));
				continue;
			}
			
			if(action.getBeginStatId() == null){
				if(statActionArray != null && statActionArray.length > 0){
					resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), data.getAssignUsername(), false));
					continue;
				}
			}
			else{
				if(!stat.getId().equals(action.getBeginStatId())&&!batchClose){
					resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), data.getAssignUsername(), false));
					continue;
				}
			}
			
			
			if(!ConfigManager.getProjectInvolved()){
				//非项目管理验证
				String[] assignUserArray = flow.queryNodeStatAssignUsers(template.getId(), action.getEndStatId());
				if(assignUserArray == null || assignUserArray.length == 0){
					if(assignUser != null){
						resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), data.getAssignUsername(), false));
						continue;
					}
				}
				else{
					if(assignUser == null&&!batchClose){
						resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), data.getAssignUsername(), false));
						continue;
					}
					
					Set<String> assignUserSet = new HashSet<String>(Arrays.asList(assignUserArray));
					if(!assignUserSet.contains(assignUser)){
						resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), data.getAssignUsername(), false));
						continue;
					}
				}
			}
			
			lastStat = newStat;
		}
		
		//备份data
		data = (Data)data.clone();
		
		Map<String, Pair<Object, Object>> baseValueMap = new LinkedHashMap<String, Pair<Object, Object>>();
		Map<UUID, Pair<Object, Object>> extValueMap = new LinkedHashMap<UUID, Pair<Object, Object>>();
		
		//assignUser
		if(data.getAssignUsername() == null && assignUser != null
		|| data.getAssignUsername() != null && assignUser == null
			|| data.getAssignUsername() != null && assignUser != null && !data.getAssignUsername().equals(assignUser)){
				baseValueMap.put("assignUser", new Pair<Object, Object>(data.getAssignUsername(), assignUser));
		}

		data.setAssignUsername(assignUser);
		
		//statusId
		if(action != null){
			baseValueMap.put("statusId", new Pair<Object, Object>(data.getStatusId(), action.getEndStatId()));
			data.setStatusId(action.getEndStatId());
		}
		
		//logCreateUser
		data.setObject("logCreateUser", key.getUsername());
		
		//logActionId
		if(action != null){
			data.setObject("logActionId", action.getId());
		}
		
		//logActionComment
		data.setObject("logActionComment", actionDesc);
		
		data.setObject("logBaseValueMap", baseValueMap);
		data.setObject("logExtValueMap", extValueMap);
		
		ErrorCode errorCode = das.modifyData(data).getFirst();
		if(errorCode.equals(ErrorCode.success)){
			ErrorCode errorCode1 = das.commitTranscation();
			if(errorCode1.equals(ErrorCode.success)){
				das.updateCache(DataAccessAction.update, data.getId().getValue(),data);
				resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), lastStat.getName(), assignUser, true));
			}else{
				das.rollbackTranscation();
				resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), oldAssignUser, false));
			}
		}else{
			das.rollbackTranscation();
			resultXml.append(DataManager.getInstance().makeResult(data.getTitle(), stat.getName(), oldAssignUser, false));
		}
	}
	
	resultXml.append("</results>");
	resultXml.append("</root>");
	
	out.println(resultXml.toString());
%>