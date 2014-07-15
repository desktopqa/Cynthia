package com.sogou.qadev.service.cynthia.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.alibaba.fastjson.JSONArray;
import com.sogou.qadev.cache.impl.FieldNameCache;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Filter;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.FilterAccessSession;
import com.sogou.qadev.service.cynthia.service.FilterQueryManager;
import com.sogou.qadev.service.cynthia.util.CommonUtil;
import com.sogou.qadev.service.login.bean.Key;
import com.sohu.rd.td.util.xml.XMLUtil;

/**
 * @description:filter processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午8:25:51
 * @version:v1.0
 */
@Controller
@RequestMapping("/filter")
public class FilterController extends BaseController{

	/**
	 * base fields
	 */
	private static Map<String , String> baseFieldNameMap = new HashMap<String , String>();
	
	static 
	{
		baseFieldNameMap.put("title", "标题");
		baseFieldNameMap.put("id", "编号");
		baseFieldNameMap.put("description", "描述");
		baseFieldNameMap.put("status_id", "状态");
		baseFieldNameMap.put("create_user", "创建人");
		baseFieldNameMap.put("create_time", "创建时间");
		baseFieldNameMap.put("assign_user", "指派人");
		baseFieldNameMap.put("last_modify_time", "修改时间");
		baseFieldNameMap.put("node_id", "项目");
	}
	
	private class FilterField{
		private String fieldId;
		private String fieldName;
		
		public FilterField(String fieldId, String fieldName){
			this.fieldId = fieldId;
			this.fieldName = fieldName;
		}
		public String getFieldId() {
			return fieldId;
		}
		public void setFieldId(String fieldId) {
			this.fieldId = fieldId;
		}
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
	}
	
	/**
	 * @description:get the home filter of user
	 * @date:2014-5-5 下午8:26:40
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getHomeFilter.do")
	@ResponseBody
	public String getHomeFilter(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		if (session.getAttribute("userName") == null) {
			return "119695";
		}
		
		String userName = session.getAttribute("userName").toString();
		DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
		String filterId = das.queryHomeFilter(userName);
		if (filterId == null || filterId.length() == 0) {
			return "119695";
		}else {
			return filterId;
		}
	}
	
	/**
	 * @description:get the default query fields
	 * @date:2014-5-5 下午8:27:04
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getDefaultHeader.do")
	@ResponseBody
	public String getDefaultHeader(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		
		List<FilterField> allDefaultList = new ArrayList<FilterController.FilterField>();
		
		allDefaultList.add(new FilterField("id", "编号"));
		allDefaultList.add(new FilterField("title", "标题"));
		allDefaultList.add(new FilterField("status_id", "状态"));
		allDefaultList.add(new FilterField("create_user", "创建人"));
		allDefaultList.add(new FilterField("create_time", "创建时间"));
		allDefaultList.add(new FilterField("assign_user", "指派人"));
		allDefaultList.add(new FilterField("last_modify_time", "修改时间"));
		
		return JSONArray.toJSONString(allDefaultList);
		
	}
	
	/**
	 * @description:get the show info of filter, all show fields and hidden fields
	 * @date:2014-5-5 下午8:27:20
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getFilterShowInfo.do")
	@ResponseBody
	public String getFilterShowInfo(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		
		String filterIdStr = request.getParameter("filterId");
		UUID filterId = DataAccessFactory.getInstance().createUUID(filterIdStr);
		DataAccessSession das = DataAccessFactory.getInstance().getSysDas();

		Filter filter = das.queryFilter(filterId);
		
		FilterField groupField = null;
		List<FilterField> showList = new ArrayList<FilterField>();
		List<FilterField> backList = new ArrayList<FilterField>();
		List<String> showFieldId = new ArrayList<String>();
		
		if(filter == null)
			return "";
		Document filterXmlDoc = XMLUtil.string2Document(filter.getXml(), "UTF-8");
		
		if (FilterQueryManager.isSysFilter(filter.getId().getValue())) {
			//系统过滤器
			List<Node> displayFieldNodes = XMLUtil.getNodes(filterXmlDoc, "query/templateType/display/field");
			showList.add(new FilterField("id", "编号"));
			showFieldId.add("id");
			for (Node node : displayFieldNodes) {
				if(XMLUtil.getAttribute(node,"id").equals("node_id"))
					continue;
				showFieldId.add(XMLUtil.getAttribute(node,"id"));
				showList.add(new FilterField(XMLUtil.getAttribute(node,"id"), XMLUtil.getAttribute(node,"name")));
			}
			
			groupField = new FilterField("node_id", "项目");
			
			for (String fieldId : baseFieldNameMap.keySet()) {
				if (!showFieldId.contains(fieldId)) {
					backList.add(new FilterField(fieldId, baseFieldNameMap.get(fieldId)));
				}
			}
		}else {
			Node templateNode = XMLUtil.getSingleNode(filterXmlDoc, "query/template");
			UUID templateId = DataAccessFactory.getInstance().createUUID(XMLUtil.getAttribute(templateNode,"id"));
			
			List<Node> displayFieldNodes = XMLUtil.getNodes(templateNode, "display/field");
			showList.add(new FilterField("id", "编号"));
			showFieldId.add("id");
			for (Node node : displayFieldNodes) {
				String fieldId = XMLUtil.getAttribute(node,"id");
				if(fieldId == null)
					continue;
				showFieldId.add(fieldId);
				if (CommonUtil.isPosNum(fieldId)) {
					//判断该字段目前存在
					if (FieldNameCache.getInstance().getFieldName(fieldId, templateId.getValue()) != null) {
						fieldId = "FIEL-" + fieldId;
					}else {
						continue;
					}
				}
				
				showList.add(new FilterField(fieldId, XMLUtil.getAttribute(node,"name")));
			}
			
			for (String fieldId : baseFieldNameMap.keySet()) {
				if (!showFieldId.contains(fieldId)) {
					backList.add(new FilterField(fieldId, baseFieldNameMap.get(fieldId)));
				}
			}
			
			Template template = das.queryTemplate(templateId);
			
			Set<Field> templateFields = template.getFields();
			
			for(Field field : templateFields)
			{
				if (!showFieldId.contains(field.getId().getValue())) {
					backList.add(new FilterField("FIEL-" + field.getId().getValue(), field.getName()));
				}
			}
			
			//分组字段
			Map<String, String> groupFieldMap = FilterAccessSession.getInstance().getGroupFieldMap(filter);
			if(groupFieldMap != null && groupFieldMap.keySet().size() > 0){
				for (String fieldId : groupFieldMap.keySet()) {
					String fieldName = groupFieldMap.get(fieldId);
					if(CommonUtil.isPosNum(fieldId)){
						if (FieldNameCache.getInstance().getFieldName(fieldId, templateId.getValue()) != null) {
							fieldId = "FIEL-" + fieldId;
						}else {
							continue;
						}
					}
					groupField = new FilterField(fieldId, fieldName);
					break;
				}
			}
		}
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("groupField", groupField);
		returnMap.put("showFields", showList);
		returnMap.put("backFields", backList);
		
		return JSONArray.toJSONString(returnMap);
		
	}
	
	
	/**
	 * @description:get filter xml
	 * @date:2014-5-5 下午8:28:20
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getFilterXml.do")
	@ResponseBody
	public String getFilterXml(HttpServletRequest request, HttpServletResponse response ,HttpSession session) throws Exception {
		
		Key key = (Key)session.getAttribute("key");
		String filterId = request.getParameter("filterId");
		Filter filter = das.queryFilter(DataAccessFactory.getInstance().createUUID(filterId));
		if (filter == null) {
			return "";
		}
		else {
			if(filter.getCreateUser().equals("admin@sohu-rd.com"))
   			{
   				Document filterDoc = XMLUtil.string2Document(filter.getXml(), "UTF-8");

   				Node envNode = XMLUtil.getSingleNode(filterDoc, "query/env");

   				XMLUtil.getSingleNode(envNode, "current_user").setTextContent(key.getUsername());

   				UserInfo userInfo = das.queryUserInfoByUserName(key.getUsername());
   				if (userInfo != null) {
   					Node userListNode = filterDoc.createElement("user_list");
   					userListNode.setTextContent(userInfo.getUserName());
   					envNode.appendChild(userListNode);
				}

   				filter.setXml(XMLUtil.document2String(filterDoc, "UTF-8"));
   			}
			
			return filter.getXml().replaceAll("\\\r", "").replaceAll("\\\n", "").replaceAll("\\\"", "\\\\\\\"").trim();
		}
	}
}
