package com.sogou.qadev.service.cynthia.controller;


import java.util.ArrayList;
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

import bsh.Console;

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

/**
 * 
 * @ClassName: ProjectController
 * @Description: 与项目管理相关处理类 开源可以不用关注
 * @author: liming
 * @date: 2014-12-11 上午9:55:41
 */
@Controller
@RequestMapping("/project")
public class ProjectController extends BaseController{

	/**
	 * @Title: getAllTemplate
	 * @Description: 通过产品获取项目
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 * @return: String
	 */
	@RequestMapping("/getProjects.do")
	@ResponseBody
	public String getProjects(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		Map<String, String> allProjectsMap = new HashMap<String, String>();
		String userName = (String)request.getSession().getAttribute("userName");
		String productId = request.getParameter("productId");
		allProjectsMap = ProjectInvolveManager.getInstance().getProjectMap(userName, productId);
		return JSONArray.toJSONString(allProjectsMap);
	}
	
	/**
	 * @Title: getAllUsersByRolesAndProductId
	 * @Description: 能过角色Id和项目Id获取所有用户
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 * @return: String
	 */
	@RequestMapping("/getAllUsersByRolesAndProductId.do")
	@ResponseBody
	public String getAllUsersByRolesAndProductId(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		List<UserInfo> allUsers = new ArrayList<UserInfo>();
 		String userName = (String)request.getSession().getAttribute("userName");
		String projectId = request.getParameter("projectId");
		String roleIds = request.getParameter("roles");
		
		allUsers = ProjectInvolveManager.getInstance().getUserInfoByProjectAndRole(userName,projectId, roleIds);
		return JSONArray.toJSONString(allUsers);
	}
}
