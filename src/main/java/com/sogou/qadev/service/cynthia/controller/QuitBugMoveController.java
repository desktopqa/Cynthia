package com.sogou.qadev.service.cynthia.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.DataAccessAction;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Key;
import com.sogou.qadev.service.cynthia.bean.Pair;
import com.sogou.qadev.service.cynthia.bean.Right;
import com.sogou.qadev.service.cynthia.bean.Role;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.dao.TimerAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode;
import com.sogou.qadev.service.cynthia.service.DataManager;
import com.sogou.qadev.service.cynthia.service.TableRuleManager;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

@Controller
@RequestMapping("/quitBugMove")
public class QuitBugMoveController extends BaseController{
	
	@ResponseBody
	@RequestMapping("/getUserTemplate.do")
	public String getUserTemplate(HttpSession httpSession) throws Exception {
		Key key   = ((Key)httpSession.getAttribute("key"));
		Template[] allTemplates = DataManager.getInstance().queryUserTemplates(key.getUsername());
		return JSONArray.toJSONString(allTemplates);
	}

	
	@ResponseBody
	@RequestMapping("/getTemplateRole.do")
	public String getTemplateRole(@RequestParam("templateId") String templateIdStr) throws Exception {
		UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
		
		Template template = das.queryTemplate(templateId);
		if (template == null) {
			return "";
		}
		Flow flow = das.queryFlow(template.getFlowId());
		if (flow == null) {
			return "";
		}
		
		Role[] allRoles = flow.getRoles();

		return JSONArray.toJSONString(allRoles);
	}
	
	/**
	 * @description:get the quit users from template
	 * @date:2014-5-5 下午8:35:55
	 * @version:v1.0
	 * @param templateIdStr
	 * @param roleIdStr
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getQuitUser.do")
	public String getQuitUser(@RequestParam("templateId") String templateIdStr , @RequestParam("roleId") String roleIdStr) throws Exception {
		if (roleIdStr == null || roleIdStr.length() == 0 || templateIdStr == null || templateIdStr.length() == 0) {
			return "";
		}
		UUID roleId = DataAccessFactory.getInstance().createUUID(roleIdStr);
		
		Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateIdStr));
		
		Flow flow = das.queryFlow(template.getFlowId());
		
		List<UserInfo> allQuitUserList = getAllQuitUser(template, flow, roleId);
		
		return JSONArray.toJSONString(allQuitUserList);
	}
	
	private List<UserInfo> getAllQuitUser(Template template , Flow flow , UUID roleId){
		List<UserInfo> allQuitUserList = flow.queryAllQuitUserInfo(roleId);
		return allQuitUserList;
	}
	
	/**
	 * @description:get all users by role
	 * @date:2014-5-5 下午8:36:25
	 * @version:v1.0
	 * @param templateIdStr
	 * @param roleIdStr
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getAllRoleUser.do")
	public String getAllRoleUser(@RequestParam("templateId") String templateIdStr , @RequestParam("roleId") String roleIdStr) throws Exception {
		if (roleIdStr == null || roleIdStr.length() == 0 || templateIdStr == null || templateIdStr.length() == 0) {
			return "";
		}
		UUID roleId = DataAccessFactory.getInstance().createUUID(roleIdStr);
		
		Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateIdStr));
		
		Flow flow = das.queryFlow(template.getFlowId());
		
		List<UserInfo> allQuitUserList = flow.queryAllUserInfo(roleId);
		return JSONArray.toJSONString(allQuitUserList);
	}
	
	/**
	 * @description:get all not closed data by user
	 * @date:2014-5-5 下午8:36:43
	 * @version:v1.0
	 * @param templateIdStr
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getUserNoCloseData.do")
	public String getUserNoCloseData(@RequestParam("templateId") String templateIdStr , @RequestParam("userName") String userName) throws Exception {
		UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
		Template template = das.queryTemplate(templateId);
		if (template == null || userName == null || userName.equals("") ) {
			return "";
		}
		Flow flow = das.queryFlow(template.getFlowId());
		if (flow == null ) {
			return "";
		}
		List<Data> allDatas = getTemplateUserNoCloseData(template, flow, userName);
		return JSONArray.toJSONString(allDatas);
	}
	
	/**
	 * @description:move data from quit user
	 * @date:2014-5-5 下午8:37:02
	 * @version:v1.0
	 * @param httpSession
	 * @param templateIdStr
	 * @param quitUserName
	 * @param newUserName
	 * @param actionComment
	 * @param roleIdStr
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/quitUserTaskMove.do")
	public String getUserNoCloseData(HttpSession httpSession , @RequestParam("templateId") String templateIdStr, @RequestParam("quitUserName") String quitUserName , @RequestParam("newUserName") String newUserName ,@RequestParam("actionComment") String actionComment, @RequestParam("roleId") String roleIdStr) throws Exception {
		Key key   = ((Key)httpSession.getAttribute("key"));
		Long keyId = (Long)httpSession.getAttribute("kid");
		das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
		
		UUID roleId = DataAccessFactory.getInstance().createUUID(roleIdStr);
		if (roleId == null) {
			return "请选择角色";
		}
		
		if (templateIdStr == null || templateIdStr.length() == 0 ) {
			return "表单不能为空";
		}
		UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
		
		
		Template template = das.queryTemplate(templateId);
		if (template == null) {
			return "无法找到该表单";
		}
		Flow flow = das.queryFlow(template.getFlowId());
		if (flow == null) {
			return "无法找到该流程";
		}
		
		List<String> allQuitUser = new ArrayList<String>();
		if (quitUserName != null && quitUserName.equals("all")) {
			List<UserInfo> allQuitUserList =  getAllQuitUser(template, flow, roleId);
			for (UserInfo userInfo : allQuitUserList) {
				allQuitUser.add(userInfo.getUserName());
			}
		}else {
			allQuitUser.add(quitUserName);
		}
		
		List<Data> allNeedMoveDataList = new ArrayList<Data>();
		for (String user : allQuitUser) {
			allNeedMoveDataList.addAll(getTemplateUserNoCloseData(template, flow, user));
		}
		
		StringBuffer errorDataBuffer = new StringBuffer();
		
		for (Data tempData : allNeedMoveDataList) {
			Data data = das.queryData(tempData.getId(), templateId);
			if (data != null) {
				try {
					Map<String, Pair<Object, Object>> baseValueMap = new HashMap<String, Pair<Object,Object>>();
					Map<UUID, Pair<Object, Object>> extValueMap =	new HashMap<UUID, Pair<Object,Object>>();
					
					if(data.getAssignUsername() == null && newUserName != null || data.getAssignUsername() != null && newUserName == null
						|| data.getAssignUsername() != null && newUserName != null && !data.getAssignUsername().equals(newUserName)){
						baseValueMap.put("assignUser", new Pair<Object, Object>(data.getAssignUsername(), newUserName));
					}
					
					data.setAssignUsername(newUserName);
					
					data.setObject("logCreateUser", key.getUsername());
					
					data.setObject("logActionId", null);
					
					if(actionComment != null && actionComment.length() > 0){
						data.setObject("logActionComment", actionComment);
					}else {
						data.setObject("logActionComment", "");
					}
					
					data.setObject("logBaseValueMap", baseValueMap);
					data.setObject("logExtValueMap", extValueMap);
					
					Pair<ErrorCode, String> result = das.modifyData(data);
					
					if(result.getFirst().equals(ErrorCode.success)){
						das.commitTranscation();
					}else{
						errorDataBuffer.append(errorDataBuffer.length() > 0 ?"," :"").append(XMLUtil.toSafeXMLString(data.getTitle()));
						das.rollbackTranscation();
					}
				} catch (Exception e) {
					errorDataBuffer.append(errorDataBuffer.length() > 0 ?"," :"").append(XMLUtil.toSafeXMLString(data.getTitle()));
					e.printStackTrace();
				}
			}
		}
		if (errorDataBuffer.length() > 0) {
			return "转移失败，错误数据有：" + errorDataBuffer.toString();
		}else {
			//从表单中删除人员
			Set<Right> allRights = flow.getRightSet();
			Iterator<Right> iterator = allRights.iterator();
			while (iterator.hasNext()) {
				if (allQuitUser.contains(iterator.next().getUsername())) {
					iterator.remove();
				}
			}
			flow.setRightSet(allRights);
			ErrorCode errorCode = das.updateFlow(flow);
			if (errorCode.equals(ErrorCode.success)) {
				das.updateCache(DataAccessAction.update, flow.getId().getValue(), flow);
			}else {
				das.rollbackTranscation();
			}
			
			//删除定时器数据 
			for (String user : allQuitUser) {
				new TimerAccessSessionMySQL().removeTimerByCreateUser(user);
			}
			return "转移成功";
		}
	}
	
	/**
	 * @function：获取表单下某用户未关闭数据
	 * @modifyTime：2013-12-4 下午5:13:21
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param template
	 * @param flow
	 * @param userName
	 * @return
	 */
	private List<Data> getTemplateUserNoCloseData(Template template , Flow flow , String userName){
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append("select id , title from ").append(TableRuleManager.getInstance().getDataTableName(template.getId()));
		sqlBuffer.append(" where assignUser = '").append(userName).append("'");
		
		StringBuffer statsBuffer = new StringBuffer();
		
		for (Stat endStat: flow.getEndStats()) {
			statsBuffer.append(statsBuffer.length() >0 ? " and " :"").append(" statusId != ").append(endStat.getId().getValue());
		}
		sqlBuffer.append(" and (").append(statsBuffer.toString()).append(")");

		List<Data> allDatas = new DataAccessSessionMySQL().queryDatas(sqlBuffer.toString(), false, template.getId());
		return allDatas;
	}
	
	
	
}
