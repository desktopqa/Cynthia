package com.sogou.qadev.service.cynthia.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Key;
import com.sogou.qadev.service.cynthia.bean.Right;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.bean.impl.UserInfoImpl;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.ProjectInvolveManager;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;

/**
 * 后台权限操作管理类
 * @author liming
 *
 */
@Controller
@RequestMapping("/backRight")
public class BackRightController extends BaseController{
	
	/**
	 * @Title:getBackRight
	 * @Type:BackRightController
	 * @description:return the users who has the background rights
	 * @date:2014-5-5 下午7:59:26
	 * @version:v1.0
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getBackRight.do")
	public String getBackRight(HttpSession httpSession) throws Exception {
		
		List<UserInfo> allBackUsers = das.getBackRightUsers();
		return JSONArray.toJSONString(allBackUsers);
	}

	/**
	 * 
	 * @Title:addBackRightUser
	 * @Type:BackRightController
	 * @description:add backright for user
	 * @date:2014-5-5 下午8:00:11
	 * @version:v1.0
	 * @param userMail
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/addBackRightUser.do")
	public String addBackRightUser(@RequestParam("userMail") String userMail,HttpSession httpSession) throws Exception {
		if (userMail == null || userMail.length() == 0) {
			return "邮箱名为空!";
		}else {
			if (das.addBackRightUser(userMail)) {
				return "true";
			}else {
				return "服务器内部错误,添加失败!";
			}
		}
	}
	
	/**
	 * @Title:delBackRightUser
	 * @Type:BackRightController
	 * @description:delete the backgroud right by userMail
	 * @date:2014-5-5 下午8:00:31
	 * @version:v1.0
	 * @param userMail
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/delBackRightUser.do")
	public String delBackRightUser(@RequestParam("userMail") String userMail, HttpSession httpSession) throws Exception {
		if (userMail == null || userMail.length() == 0) {
			return "邮箱名为空!";
		}else {
			if (das.delBackRightUser(userMail)) {
				return "true";
			}else {
				return "服务器内部错误,删除失败!";
			}
		}
	}
	
	/**
	 * @Title:getTemplateRightUser
	 * @Type:BackRightController
	 * @description:return the users who has the template rights by templateId
	 * @date:2014-5-5 下午8:01:08
	 * @version:v1.0
	 * @param templateId
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getTemplateRightUser.do")
	public String getTemplateRightUser(@RequestParam("templateId") String templateId, HttpSession httpSession) throws Exception {
		if (templateId == null || templateId.length() == 0) {
			return "";
		}else {
			List<UserInfo> allTemplateUsers = das.getTemplateRightUser(templateId);
			return JSONArray.toJSONString(allTemplateUsers);
		}
	}
	
	/**
	 * @Title:initTemplateRightUser
	 * @Type:BackRightController
	 * @description:init the users for template rights
	 * @date:2014-5-5 下午8:02:28
	 * @version:v1.0
	 * @param templateId
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/initTemplateRightUser.do")
	public String initTemplateRightUser(@RequestParam("templateId") String templateId, HttpSession httpSession) throws Exception {
		if (templateId == null || templateId.length() == 0) {
			return "";
		}else {
			Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateId));
			if (template == null) {
				return "";
			}
			Flow flow = das.queryFlow(template.getFlowId());
			List<UserInfo> allUserInfoList = new ArrayList<UserInfo>();
			
			UserInfo ui = new UserInfoImpl();
			ui.setUserName("*");
			ui.setNickName("所有人");
			allUserInfoList.add(ui);
			
			List<String> userArray = new ArrayList<String>();
			
			for (Right right : flow.getRightSet()) {
				userArray.add(right.getUsername());
			}
			
			allUserInfoList.addAll(das.queryAllUserInfo(userArray.toArray(new String[0])));
			
			return JSONArray.toJSONString(allUserInfoList);
		}
	}
	
	/**
	 * @Title:addUserTemplateRight
	 * @Type:BackRightController
	 * @description:add template right for user
	 * @date:2014-5-5 下午8:03:08
	 * @version:v1.0
	 * @param userMail
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/addUserTemplateRight.do")
	public String addUserTemplateRight(@RequestParam("userMail") String userMail, HttpServletRequest request, HttpSession httpSession) throws Exception {
		if (userMail == null || userMail.length() == 0) {
			return "邮箱名为空!";
		}else {
			String[] templateIds = request.getParameterValues("templateIds[]");
			if (das.addUserTemplateRight(templateIds, userMail)) {
				return "true";
			}else {
				return "服务器内部错误,添加失败!";
			}
		}
	}
	
	/**
	 * @Title:addTemplateRightUser
	 * @Type:BackRightController
	 * @description:add user right for template
	 * @date:2014-5-5 下午8:03:31
	 * @version:v1.0
	 * @param templateId
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/addTemplateRightUser.do")
	public String addTemplateRightUser(@RequestParam("templateId") String templateId, HttpServletRequest request, HttpSession httpSession) throws Exception {
		if (templateId == null || templateId.length() == 0) {
			return "表单为空,添加失败!";
		}else {
			String[] userMails = request.getParameterValues("userMails[]");
			if (das.addtemplateUserRight(templateId, userMails)) {
				return "true";
			}else {
				return "服务器内部错误,添加失败!";
			}
		}
	}
	
	/**
	 * @Title:initUserTemplateRight
	 * @Type:BackRightController
	 * @description:init user template rights
	 * @date:2014-5-5 下午8:04:13
	 * @version:v1.0
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/initUserTemplateRight.do")
	public String initUserTemplateRight(HttpServletRequest request,HttpSession httpSession) throws Exception {
		String userMail = request.getParameter("userMail");
		
		if (userMail == null || userMail.length() == 0) {
			Key key = (Key)httpSession.getAttribute("key");
			userMail = key.getUsername();
		}
		Map<String, String> temMap = new HashMap<String, String>();
		
		temMap = das.queryUserTemplateRights(userMail);
		
		return JSONArray.toJSONString(temMap);
	}
	
	/**
	 * @Title:initUserFlowRight
	 * @Type:BackRightController
	 * @description:init user flow rights
	 * @date:2014-5-5 下午8:04:34
	 * @version:v1.0
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/initUserFlowRight.do")
	public String initUserFlowRight(HttpServletRequest request,HttpSession httpSession) throws Exception {
		
		Key key = (Key)httpSession.getAttribute("key");
		String userMail = key.getUsername();
		
		Map<String, String> temMap = das.queryUserTemplateRights(userMail);
		Set<String> flowSet = new HashSet<String>();
		for (String templateId : temMap.keySet()) {
			Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateId));
			if (template != null) {
				flowSet.add(template.getFlowId().getValue());
			}
		}
		
		//自己创建的表单具有编辑权限
		for (Flow flow : das.queryAllFlows()) {
			if (flow != null && flow.getCreateUser() != null && flow.getCreateUser().equals(key.getUsername())) {
				flowSet.add(flow.getId().getValue());
			}
		}
		return JSONArray.toJSONString(flowSet);
	}
	
	/**
	 * @Title:delUserTemplateRight
	 * @Type:BackRightController
	 * @description:delete the template right of user
	 * @date:2014-5-5 下午8:04:46
	 * @version:v1.0
	 * @param userMail
	 * @param templateId
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/delUserTemplateRight.do")
	public String delUserTemplateRight(@RequestParam("userMail") String userMail, @RequestParam("templateId") String templateId , HttpSession httpSession) throws Exception {
		if (das.delUserTemplateRight(templateId, userMail)) {
			return "true";
		}else {
			return "服务器内部错误,删除失败!";
		}
	}
	
	/**
	 * @Title:delTemplateRightUser
	 * @Type:BackRightController
	 * @description:delete user rights of template
	 * @date:2014-5-5 下午8:05:09
	 * @version:v1.0
	 * @param userMail
	 * @param templateId
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/delTemplateRightUser.do")
	public String delTemplateRightUser(@RequestParam("userMail") String userMail, @RequestParam("templateId") String templateId , HttpSession httpSession) throws Exception {
		if (das.delTemplateUserRight(templateId,userMail)) {
			return "true";
		}else {
			return "服务器内部错误,删除失败!";
		}
	}
	
	@ResponseBody
	@RequestMapping("/getWebRootDir.do")
	public String getWebRootDir(HttpServletRequest request,HttpSession httpSession) throws Exception {
		return ConfigUtil.getCynthiaWebRoot();
	}
	
	/**
	 * @Title:getAllTemplate
	 * @Type:BackRightController
	 * @description:return all templates (id and name)
	 * @date:2014-5-5 下午8:05:23
	 * @version:v1.0
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getAllTemplate.do")
	public String getAllTemplate(HttpSession httpSession) throws Exception {
		Map<String, String> allTemplateMap = new HashMap<String, String>();
		for (Template template : das.queryAllTemplates()) {
			if (template == null) {
				continue;
			}
			allTemplateMap.put(template.getId().getValue(),template.getName());
		}
		return JSONArray.toJSONString(allTemplateMap);
	}
	
	/**
	 * @Title:setSystem
	 * @Type:BackRightController
	 * @description:set the system setting(json string)
	 * @date:2014-5-5 下午8:05:45
	 * @version:v1.0
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/setSystem.do")
	public String setSystem(HttpServletRequest request, HttpSession httpSession) throws Exception {
		String systemJson = request.getParameter("systemJson");
		return String.valueOf(das.setSystemOption(systemJson));
	}
	
	/**
	 * 
	 * @Title:getSystem
	 * @Type:BackRightController
	 * @description:return the system setting(json string)
	 * @date:2014-5-5 下午8:06:06
	 * @version:v1.0
	 * @param userMail
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getSystem.do")
	public String getSystem(@RequestParam("userMail") String userMail ,HttpServletRequest request, HttpSession httpSession) throws Exception {
		JSONObject jsonObject = JSONArray.parseObject(das.getSystemOption(userMail));
		Map<String, Object> sysMap = new HashMap<String, Object>();
		for (String key : jsonObject.keySet()) {
			sysMap.put(key, jsonObject.get(key));
		}
		sysMap.put("projectInvolved", String.valueOf(ProjectInvolveManager.getInstance().isProjectInvolved()));
		return JSONArray.toJSONString(sysMap);
	}
}
