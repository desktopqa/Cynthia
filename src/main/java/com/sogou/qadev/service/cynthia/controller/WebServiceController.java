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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Pair;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.controller.ExcelImportControllerNew.ErrorInfo;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;



/**
 * @description:template processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午8:42:42
 * @version:v1.0
 */
@Controller
@RequestMapping("/webservice")
public class WebServiceController extends BaseController{
	
	private static String getReturnJson(String success, Object object){
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("success", success);
		if (object != null) {
			returnMap.put("reason", object);
		}
		return JSONArray.toJSONString(returnMap);
	}
	
	@ResponseBody
	@RequestMapping("/importData.do")
	public String importData(HttpServletRequest request , HttpServletResponse response, HttpSession httpSession) throws Exception {
		List<Map<String, String>> allImportDataList = new ArrayList<Map<String,String>>();
		String jsonData = request.getParameter("importDatas");
		JSONArray jsonArray = JSONArray.parseArray(jsonData);
		for (Object object : jsonArray) {
			JSONObject jsonObject = JSONObject.parseObject(object.toString());
			Map<String, String> singleDataMap = new HashMap<String, String>();
			allImportDataList.add(singleDataMap);
			for (String key : jsonObject.keySet()) {
				singleDataMap.put(key, jsonObject.getString(key));
			}
		}

		if (allImportDataList.size() <= 0) {
			return getReturnJson("true","");
		}
		
		ExcelImportControllerNew eic = new ExcelImportControllerNew();
		
		Set<Field> allNeedFields = new HashSet<Field>();
		List< Pair<String, String>> errorList = new ArrayList<Pair<String,String>>();

		for(int i = 0 ; i < allImportDataList.size() ; i ++){
			try {
				Map<String, String> exportMapData = allImportDataList.get(i);
				String templateIdStr = exportMapData.get("templateId");
				if (CynthiaUtil.isNull(templateIdStr)) {
					errorList.add(new Pair<String, String>(exportMapData.get("title"), "templateId is not set!"));
					continue;
				}

				UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
				Template template = das.queryTemplate(templateId);  //得到表单
				if (template == null) {
					errorList.add(new Pair<String, String>(exportMapData.get("title"), "templateId is not set!"));
					continue;
				}
				Flow flow = das.queryFlow(template.getFlowId());

				Set<Field> allFields = eic.GetAllFields(template);//表单所有字段,除出废弃字段
				
				Pair<String, String> saveErrorPair = eic.saveSingleData(template, flow, allNeedFields, exportMapData);
				if (saveErrorPair != null) {
					errorList.add(saveErrorPair);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (errorList.size() > 0) {
			return getReturnJson("false", errorList);
		}else {
			return getReturnJson("true", null);
		}
		
	}
}
