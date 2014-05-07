package com.sogou.qadev.service.cynthia.controller;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Field.DataType;
import com.sogou.qadev.service.cynthia.bean.Field.Type;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataManager;
import com.sogou.qadev.service.login.bean.Key;
import com.sohu.rd.td.util.reference.Pair;

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
	public String getAllTemplate(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		Set<Pair<String, String>> allTemplateSet = new HashSet<Pair<String, String>>();
		
		Template[] allTemplates = das.queryAllTemplates();
		for (Template template : allTemplates) {
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
	@RequestMapping("/getUserTemplate.do")
	public String getUserTemplate(HttpServletRequest request , HttpSession httpSession) throws Exception {
		Key key   = ((Key)httpSession.getAttribute("key"));
		Long keyId = (Long)httpSession.getAttribute("kid");
		String templateTypeId = request.getParameter("templateTypeId");
		UUID templateTypeUUID = null;
		if (templateTypeId != null && !templateTypeId.equals("")) {
			templateTypeUUID = DataAccessFactory.getInstance().createUUID(templateTypeId);
		}
		das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
		Template[] allTemplates = DataManager.getInstance().queryUserTemplates(das);
		
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
	
}
