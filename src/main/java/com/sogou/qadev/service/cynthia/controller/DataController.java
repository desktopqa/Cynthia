package com.sogou.qadev.service.cynthia.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;

/**
 * @description:data process 
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午8:18:51
 * @version:v1.0
 */
@Controller
@RequestMapping("/data")
public class DataController extends BaseController{
	
	/**
	 * @description:update the data log
	 * @date:2014-5-5 下午8:18:45
	 * @version:v1.0
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/updateLog.do")
	public String updateLog(HttpServletRequest request, HttpSession httpSession) throws Exception {
		
		String dataId = request.getParameter("dataId");
		int logIndex = Integer.parseInt(request.getParameter("logIndex"));
		String logComment = request.getParameter("logComment");
		return String.valueOf(das.updateDataLog(DataAccessFactory.getInstance().createUUID(dataId), logIndex, logComment));
	}
}
