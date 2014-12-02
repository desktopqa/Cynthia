package com.sogou.qadev.service.cynthia.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: template fields default Values processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午8:19:22
 * @version:v1.0
 */
@Controller
@RequestMapping("/defaultValue")
public class DefaultValueController extends BaseController{

	/**
	 * @description:set the default values of template 
	 * @date:2014-5-5 下午8:19:58
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/setdefaultValues.do")
	@ResponseBody
	public String setdefaultValues(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		
		String userName = session.getAttribute("userName").toString();
		if (userName == null) {
			return "error";
		}
		
		String templateId = request.getParameter("templateId");
		String defaultValueJson = request.getParameter("defaultValueJson");
		
		return String.valueOf(das.setDefaultValues(userName,templateId,defaultValueJson));
	}
	
	/**
	 * @description:get the default values of template
	 * @date:2014-5-5 下午8:20:26
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getDefaultValues.do")
	@ResponseBody
	public String getDefaultValues(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		
		String userName = session.getAttribute("userName").toString();
		if (userName == null) {
			return "error";
		}
		
		String templateId = request.getParameter("templateId");
		
		return das.getDefaultValues(userName, templateId);
	}
}
