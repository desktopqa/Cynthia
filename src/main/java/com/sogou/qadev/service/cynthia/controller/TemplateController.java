package com.sogou.qadev.service.cynthia.controller;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sogou.qadev.service.cynthia.bean.DataAccessAction;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Field.DataType;
import com.sogou.qadev.service.cynthia.bean.Field.Type;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Key;
import com.sogou.qadev.service.cynthia.bean.Pair;
import com.sogou.qadev.service.cynthia.bean.Right;
import com.sogou.qadev.service.cynthia.bean.Role;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.TemplateMailOption;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode;
import com.sogou.qadev.service.cynthia.service.DataManager;
import com.sogou.qadev.service.cynthia.service.ProjectInvolveManager;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;

/**
 * @description:template processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午8:42:42
 * @version:v1.0
 */
@Controller
@RequestMapping("/template")
public class TemplateController extends BaseController{

	/**
	 * @description:get all templates
	 * @date:2014-5-5 下午8:42:54
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getAllTemplates.do")
	@ResponseBody
	public String getAllTemplate(HttpServletRequest request, HttpServletResponse response ,HttpSession httpSession) throws Exception {
		String queryUser = request.getParameter("userMail");
		String isProTemplate = request.getParameter("isProTemplate");
		if (CynthiaUtil.isNull(queryUser)) {
			queryUser = ((Key)httpSession.getAttribute("key")).getUsername();
		}
		
		Template[] allTemplates = DataManager.getInstance().queryUserReadableTemplates(queryUser);
		Map<String, String> allTemplateMap = new HashMap<String, String>();
		for (Template template : allTemplates) {
			if (!CynthiaUtil.isNull(isProTemplate) && isProTemplate.equals("true") && !template.getTemplateConfig().isProjectInvolve()) 
				continue;
			allTemplateMap.put(template.getId().getValue(), template.getName());
		}
		
		String jsonStr = JSONArray.toJSONString(allTemplateMap);
		String callback = request.getParameter("callback");
		if (callback != null && !callback.equals("")) {
			String jsonp = callback + "(" + jsonStr + ")";
			response.setContentType("application/javascript;charset=UTF-8");
			response.getWriter().print(jsonp);
			response.getWriter().flush();
			response.getWriter().close();
			return "";
		}else {
			return jsonStr;
		}
	}
	
	/**
	 * @description:get templates of user(can read or modify)
	 * @date:2014-5-5 下午8:43:09
	 * @version:v1.0
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getUserTemplate.do")
	public String getUserTemplate(HttpServletRequest request , HttpSession httpSession) throws Exception {
		Key key   = ((Key)httpSession.getAttribute("key"));
		String templateTypeId = request.getParameter("templateTypeId");
		UUID templateTypeUUID = null;
		if (templateTypeId != null && !templateTypeId.equals("")) {
			templateTypeUUID = DataAccessFactory.getInstance().createUUID(templateTypeId);
		}
		
		Template[] allTemplates = DataManager.getInstance().queryUserTemplates(key.getUsername());
		
		Set<Pair<String, String>> allTemplateSet = new HashSet<Pair<String, String>>();
			
		for (Template template : allTemplates) {
			if (templateTypeUUID != null) {
				if (!template.getTemplateTypeId().equals(templateTypeUUID)) {
					continue;
				}
			}
			allTemplateSet.add(new Pair<String, String>(template.getId().getValue(),template.getName()));
		}
		return JSONArray.toJSONString(allTemplateSet);
	}
	
	/**
	 * @description:get templates of user(can read or modify)
	 * @date:2014-5-5 下午8:43:09
	 * @version:v1.0
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getUserReadableTemplate.do")
	public String getUserReadableTemplate(HttpServletRequest request , HttpSession httpSession) throws Exception {
		Key key   = ((Key)httpSession.getAttribute("key"));
		String templateTypeId = request.getParameter("templateTypeId");
		UUID templateTypeUUID = null;
		if (templateTypeId != null && !templateTypeId.equals("")) {
			templateTypeUUID = DataAccessFactory.getInstance().createUUID(templateTypeId);
		}
		
		Template[] allTemplates = DataManager.getInstance().queryUserReadableTemplates(templateTypeUUID,key.getUsername());
		
		Set<Pair<String, String>> allTemplateSet = new HashSet<Pair<String, String>>();
			
		for (Template template : allTemplates) {
			if (templateTypeUUID != null) {
				if (!template.getTemplateTypeId().equals(templateTypeUUID)) {
					continue;
				}
			}
			allTemplateSet.add(new Pair<String, String>(template.getId().getValue(),template.getName()));
		}
		return JSONArray.toJSONString(allTemplateSet);
	}
	/**
	 * @description:get data id and title of template
	 * @date:2014-5-5 下午8:43:42
	 * @version:v1.0
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getTmpDataIdTitle.do")
	public String getTmpDataIdTitle(HttpServletRequest request , HttpSession httpSession) throws Exception {
		String templateIdStr = request.getParameter("templateId");
		if (templateIdStr == null || templateIdStr.equals("")) {
			return "";
		}
		
		Map<String, String> idAndTitleMap = new DataAccessSessionMySQL().queryIdAndFieldOfTemplate(templateIdStr, "title");
		String jsonString =  JSONArray.toJSONString(idAndTitleMap);
		return jsonString;
	}
	
	/**
	 * 获取该表单下该任务的bug字段 这种字段一般是多选引用类型
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getTemplateAllBugField.do")
	public String getTemplateAllBugField(HttpServletRequest request , HttpSession httpSession) throws Exception {
		String templateIdStr = request.getParameter("templateId");
		if (templateIdStr == null || templateIdStr.equals("")) {
			return "";
		}
		
		Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateIdStr));
		if (template == null) {
			return "";
		}
		
		Set<Pair<String, String>> allFields = new HashSet<Pair<String,String>>();
		for(Field field : template.getFields()){
			//多选引用类型字段
			if (field != null && field.getType() != null && field.getType().equals(Type.t_reference) && field.getDataType() != null
					&& field.getDataType().equals(DataType.dt_multiple)) {
				allFields.add(new Pair<String, String>(field.getId().getValue(),field.getName()));
			}
		}
		
		return JSONArray.toJSONString(allFields);
	}
	
	/**
	 * 获取表单邮件配置
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getTemplateMailConfig.do")
	public String getTemplateMailConfig(HttpServletRequest request , HttpSession httpSession) throws Exception {
		String templateIdStr = request.getParameter("templateId");
		Key key   = ((Key)httpSession.getAttribute("key"));
		if (templateIdStr == null || templateIdStr.equals("")) {
			return "";
		}
		
		Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateIdStr));
		if (template == null) {
			return "";
		}
		
		Flow flow = das.queryFlow(template.getFlowId());
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("actions", flow.getActions());
		returnMap.put("templateMailOptions", template.getTemplateMailOption());
		Map<String, String> usersMap = new HashMap<String, String>();
		
		if (template.getTemplateConfig().isProjectInvolve()) {
			List<UserInfo> allUsers = ProjectInvolveManager.getInstance().getCompanyUsersByMail(key.getUsername());
			for (UserInfo userInfo : allUsers) {
				usersMap.put(userInfo.getUserName(),userInfo.getNickName());
			}
			returnMap.put("roles", ProjectInvolveManager.getInstance().getAllRole(key.getUsername()));
		}else {
			for (Right right : flow.queryNodeUserRight(template.getId())) {
				usersMap.put(right.getUsername(), right.getNickname());
			}
			returnMap.put("roles", flow.getRoleMap().values().toArray(new Role[0]));
		}
		returnMap.put("users", usersMap);
		return JSONArray.toJSONString(returnMap);
	}

	/**
	 * 保存表单邮件配置
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/saveTemplateMailConfig.do")
	public String saveTemplateMailConfig(HttpServletRequest request , HttpSession httpSession) throws Exception {
		String templateIdStr = request.getParameter("templateId");
		if (templateIdStr == null || templateIdStr.equals("")) {
			return "";
		}
		
		Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateIdStr));
		if (template == null) {
			return "";
		}
		
		TemplateMailOption tmo = template.getTemplateMailOption();
		
		JSONObject templateMailOptions = JSONObject.parseObject(request.getParameter("templateMailOptions"));
		
		tmo.setSendMail(templateMailOptions.getString("sendMail").toString().equals("true"));
		tmo.setMailSubject(templateMailOptions.get("mailSubject").toString());
		tmo.getActionUsers().clear();
		
		JSONObject actionUsers = templateMailOptions.getJSONObject("actionUsers");
		for (String actionId : actionUsers.keySet()) {
			tmo.setActionUser(actionId, actionUsers.getString(actionId));
		}

		ErrorCode errorCode = das.updateTemplate(template);
		if(errorCode.equals(ErrorCode.success)){
			das.updateCache(DataAccessAction.update, template.getId().getValue(),template);
			return "true";
		}else{
			return "false";
		}
	}
}
