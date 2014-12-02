package com.sogou.qadev.service.cynthia.controller;

import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;

@Controller
@RequestMapping("/guide")
public class GuideController extends BaseController{

	@RequestMapping("/getAllGuide.do")
	@ResponseBody
	public String getAllGuide(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		return JSONArray.toJSONString(das.queryAllGuide());
	}


	@RequestMapping("/getGuideHtml.do")
	@ResponseBody
	public String getGuideHtml(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {

		String guideId = request.getParameter("guideId");
		return das.queryGuideHtmlByGuideId(guideId);
	}

	@RequestMapping("/saveGuideHtml.do")
	@ResponseBody
	public String saveGuideHtml(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {

		String guideId = request.getParameter("guideId");
		String guideHtml = request.getParameter("guideHtml");
		
		guideHtml = URLDecoder.decode(guideHtml, "UTF-8");//解码
		
		return String.valueOf(das.saveGuideHtml(guideId, guideHtml));
	}
}
