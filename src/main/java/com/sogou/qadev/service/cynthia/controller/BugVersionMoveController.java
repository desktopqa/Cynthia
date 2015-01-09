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
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.DataAccessAction;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Key;
import com.sogou.qadev.service.cynthia.bean.Pair;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode;
import com.sogou.qadev.service.cynthia.service.DataManager;
import com.sogou.qadev.service.cynthia.vo.DataVO;

/**
 * @description:bug move process
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午8:15:58
 * @version:v1.0
 */
@Controller
@RequestMapping("/bugMove")
public class BugVersionMoveController extends BaseController{
	@ResponseBody
	@RequestMapping("/bugVersionMove.do")
	public String bugVersionMove(HttpSession httpSession, HttpServletRequest request) throws Exception {
		
		String templateId = request.getParameter("templateId");
		String oldTaskId  = request.getParameter("oldTaskId");
		String newTaskId  = request.getParameter("newTaskId");
		String statsIdStr = request.getParameter("stats");
		String bugField   = request.getParameter("bugField");
		String taskField  = request.getParameter("taskField");
		String userName   = ((Key)httpSession.getAttribute("key")).getUsername();
		String[] statsIdArr = null;
		if(statsIdStr!=null)
			statsIdArr = statsIdStr.split(",");
		if(statsIdArr!=null)
		{
			Set<UUID> statusIdSet = new HashSet<UUID>();
			for(String statusIdStr : statsIdArr){
				if(statusIdStr!=null&&!"".equals(statusIdStr))
				statusIdSet.add(DataAccessFactory.getInstance().createUUID(statusIdStr));
			}
			Data oldTask = das.queryData(DataAccessFactory.getInstance().createUUID(oldTaskId) ,DataAccessFactory.getInstance().createUUID(templateId));
			String oldTaskTitle = oldTask.getTitle();
			UUID[] oldVersionBugIds = oldTask.getMultiReference(DataAccessFactory.getInstance().createUUID(bugField));
			Set<Data> oldVesionBugsSet = new HashSet<Data>();
			for(UUID uuid : oldVersionBugIds){
				Data tempData = das.queryData(uuid ,DataAccessFactory.getInstance().createUUID(templateId));
				if(tempData!=null)
					oldVesionBugsSet.add(tempData);
			}
			boolean success = true;
			Data[] dataArray = oldVesionBugsSet.toArray(new Data[oldVesionBugsSet.size()]);
			for(Data data : dataArray){
				if(data.getObject(DataAccessFactory.getInstance().createUUID(taskField)) == null|| !statusIdSet.contains(data.getStatusId())){
					continue;
				}
				data = (Data)data.clone();
				
				Map<String, Pair<Object, Object>> baseValueMap = new HashMap<String, Pair<Object, Object>>();
				Map<UUID, Pair<Object, Object>> extValueMap = new HashMap<UUID, Pair<Object, Object>>();
				
				if(!data.getTitle().startsWith("[")){
					String newTitle = "[" + oldTaskTitle + "]" + data.getTitle();
					baseValueMap.put("title", new Pair<Object, Object>(data.getTitle(), newTitle));
					data.setTitle(newTitle);
				}
				extValueMap.put(DataAccessFactory.getInstance().createUUID(taskField), new Pair<Object, Object>(DataAccessFactory.getInstance().createUUID(oldTaskId), DataAccessFactory.getInstance().createUUID(newTaskId)));
				data.setSingleReference(DataAccessFactory.getInstance().createUUID(taskField),DataAccessFactory.getInstance().createUUID(newTaskId));
				data.setObject("logCreateUser", userName);
				data.setObject("logActionId", null);
				data.setObject("logActionComment", null);
				data.setObject("logBaseValueMap", baseValueMap);
				data.setObject("logExtValueMap", extValueMap);
				Pair<ErrorCode, String> pair = das.modifyData(data);
				if (!pair.getFirst().equals(ErrorCode.success)) {
					success = false;
				}
				das.updateCache(DataAccessAction.delete, data.getId().getValue(), data);
			}
		}
		return correctJson;
	}
	
	/**
	 * @description:get tasks of template
	 * @date:2014-5-5 下午8:16:31
	 * @version:v1.0
	 * @param oldTaskId
	 * @param bugTaskFieldId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getTaskBugTemplate.do")
	public String getTaskBugTemplate(@RequestParam("oldTaskId") String oldTaskId ,@RequestParam("bugTaskField") String bugTaskFieldId ) throws Exception {
		
		Data oldTask = das.queryData(DataAccessFactory.getInstance().createUUID(oldTaskId));
		UUID [] bugs = oldTask.getMultiReference(DataAccessFactory.getInstance().createUUID(bugTaskFieldId));
		Map<String,Template> bugTemplateMap = new HashMap<String,Template>();
		Data bugData = null;
		
		Map<UUID, Template> allTemplateMap = new HashMap<UUID, Template>();
		
		if(bugs!=null&&bugs.length>0){
			for(UUID id : bugs){
			 bugData = das.queryData(id);	
			 if(bugData!=null){
				if (allTemplateMap.get(bugData.getTemplateId()) == null) {
					allTemplateMap.put(bugData.getTemplateId(), das.queryTemplate(bugData.getTemplateId()));
				} 
				Template template = allTemplateMap.get(bugData.getTemplateId());
				if(template!=null)
					bugTemplateMap.put(template.getId().toString(), template);
			 }
			}
		}
		
		return JSONArray.toJSONString(bugTemplateMap);
	}
	
	/**
	 * @description:get datas by template id
	 * @date:2014-5-5 下午8:17:05
	 * @version:v1.0
	 * @param templateId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getTemplateDatas.do")
	public String getTempalteDatas(@RequestParam("templateId") String templateId) throws Exception {
		Map<String, String> templateIdTitleMap = new DataAccessSessionMySQL().queryIdAndFieldOfTemplate(templateId, "title");
		
		List<DataVO> templateDataList = new ArrayList<DataVO>();
		for(String dataId : templateIdTitleMap.keySet())
		{
			DataVO dataVO = new DataVO();
			dataVO.setId(dataId);
			dataVO.setName(templateIdTitleMap.get(dataId));
			templateDataList.add(dataVO);
		}
		return JSONArray.toJSONString(templateDataList);
	}
	
	/**
	 * @description:get all Fields of template
	 * @date:2014-5-5 下午8:17:34
	 * @version:v1.0
	 * @param templateId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getTemplateFields.do")
	public String getTemplateFields(@RequestParam("templateId") String templateId) throws Exception {
		Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateId));
		Set<Field> fields = template.getFields();
		return JSONArray.toJSONString(fields);
	}
	
	/**
	 * @description:get all templates by template type
	 * @date:2014-5-5 下午8:17:51
	 * @version:v1.0
	 * @param templateType
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getTemplatesByTemplateType.do")
	public String getTemplatesByTemplateType(@RequestParam("templateType") String templateType, HttpSession httpSession) throws Exception {
		Key key   = ((Key)httpSession.getAttribute("key"));
		Template[] templates = DataManager.getInstance().queryUserTemplates(DataAccessFactory.getInstance().createUUID(templateType), key.getUsername());
		return JSONArray.toJSONString(templates);
	}

	/**
	 * @description:get all status of the template
	 * @date:2014-5-5 下午8:18:11
	 * @version:v1.0
	 * @param templateId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getTemplateStats.do")
	public String getTemplateStats(@RequestParam("templateId") String templateId) throws Exception {
		Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateId));
		Flow flow = das.queryFlow(template.getFlowId());
		Stat[] stats = flow.getStats();
		return JSONArray.toJSONString(stats);
	}
}
