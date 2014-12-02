package com.sogou.qadev.service.cynthia.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.sogou.qadev.service.cynthia.bean.Action;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Key;
import com.sogou.qadev.service.cynthia.bean.Script;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;
import com.sogou.qadev.service.cynthia.util.DateJsonValueProcessor;
import com.sogou.qadev.service.cynthia.util.XMLUtil;
import com.sogou.qadev.service.cynthia.vo.ScriptVO;

/**
 * script processor
 * @description:TODO
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午8:37:49
 * @version:v1.0
 */
@Controller
@RequestMapping("/script")
public class ScriptController extends BaseController {
	
	/**
	 * @description:add or update a script
	 * @date:2014-5-5 下午8:37:56
	 * @version:v1.0
	 * @param request
	 * @param httpSession
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/addOrUpdateScript.do")
	@ResponseBody
	public String addOrUpdateScript(HttpServletRequest request , HttpSession httpSession) throws Exception {
		
		request.setCharacterEncoding("UTF-8");
		
		String scriptId 		 = request.getParameter("scriptId");
		String scriptName 		 = request.getParameter("scriptName");
		String isAsyncStr   	 = request.getParameter("isAsync");
		String isBeforeCommitStr = request.getParameter("isBeforeCommit");
		String isAfterSuccessStr = request.getParameter("isAfterSuccess");
		String isAfterFailStr    = request.getParameter("isAfterFail");
		String scriptCode        = request.getParameter("script");
		String isStatEditStr     = request.getParameter("isStatEdit");
		String isActionEditStr   = request.getParameter("isActionEdit");
		String isValidStr        = request.getParameter("isValid");
		String allowedTemplates  = request.getParameter("allowedTemplates");
		
		String userName  = ((Key)httpSession.getAttribute("key")).getUsername();
		Script script = null;
		if(scriptId==null||"".equals(scriptId)){
			script = das.createScript(userName);
		}else{
			script = das.queryScript(DataAccessFactory.getInstance().createUUID(scriptId));
		}
		
		script.setScript(scriptCode);
		script.setName(scriptName);
		script.setAfterFail(Boolean.valueOf(isAfterFailStr));
		script.setAsync(Boolean.valueOf(isAsyncStr));
		script.setBeforeCommit(Boolean.valueOf(isBeforeCommitStr));
		script.setAfterSuccess(Boolean.valueOf(isAfterSuccessStr));
		script.setStatEdit(Boolean.valueOf(isStatEditStr));
		script.setActionEdit(Boolean.valueOf(isActionEditStr));
		script.setValid(Boolean.valueOf(isValidStr));
		
		if(allowedTemplates!=null&&!"null".equals(allowedTemplates)&&!"".equals(allowedTemplates))
		{
			String[] allowedTemplateIdsStr = allowedTemplates.split(",");
			List<UUID> allowedTemplateIdsList = new ArrayList<UUID>();
			for(String allowedTemplateIdStr : allowedTemplateIdsStr)
			{
				allowedTemplateIdsList.add(DataAccessFactory.getInstance().createUUID(allowedTemplateIdStr));
			}
			script.setAllowedTemplateIds(allowedTemplateIdsList.toArray(new UUID[0]));
		}
		if(scriptId==null||"".equals(scriptId))
		{
			das.addScript(script);
		}else
		{
			das.updateScript(script);
		}
		
		return correctJson;
	}
	
	/**
	 * @description:init script info
	 * @date:2014-5-5 下午8:38:10
	 * @version:v1.0
	 * @param scriptId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/initScript.do")
	@ResponseBody
	public String initScript(@RequestParam("scriptId")String scriptId) throws Exception {
		
		DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
		Script script = das.queryScriptNoImport(DataAccessFactory.getInstance().createUUID(scriptId));
		ScriptVO scriptVO = new ScriptVO();
		scriptVO.setId(script.getId().toString());
		scriptVO.setAfterFail(script.isAfterFail());
		scriptVO.setAfterQuery(script.isAfterQuery());
		scriptVO.setAfterSuccess(script.isAfterSuccess());
		scriptVO.setAsync(script.isAsync());
		scriptVO.setCreateTime(script.getCreateTime());
		scriptVO.setCreateUser(script.getCreateUser());
		scriptVO.setName(script.getName());
		scriptVO.setScript(script.getScript());
		scriptVO.setActionEdit(script.isActionEdit());
		scriptVO.setStatEdit(script.isStatEdit());
		scriptVO.setValid(script.isValid());
		
		UUID[] templateIds = script.getTemplateIds();
		Map<String,String> mountTemplatesMap = new HashMap<String,String>();
		if(templateIds!=null)
		{	
			for(UUID templateId : templateIds)
			{
				Template template = das.queryTemplate(templateId);
				if(template!=null)
					mountTemplatesMap.put(templateId.toString(), template.getName());
			}
		}
		Map<String,String> allTemplateMap = new HashMap<String,String>();
		Template[] allTemplates = das.queryAllTemplates();
		if(allTemplates!=null)
		{
			for(Template template: allTemplates)
			{
				allTemplateMap.put(template.getId().toString(), template.getName());
			}
		}
		scriptVO.setAllTemplates(allTemplateMap);
		scriptVO.setMountTemplates(mountTemplatesMap);
		
		UUID[] statIds = script.getEndStatIds();
		List<String> statIdsList = new ArrayList<String>();
		if(statIds != null)
		{
			for(UUID statId : statIds)
			{
				statIdsList.add(statId.toString());
			}
		}
		UUID[] actionIds = script.getActionIds();
		List<String> actionIdsList = new ArrayList<String>();
		if(actionIds != null)
		{
			for(UUID actionId : actionIds)
			{
				actionIdsList.add(actionId.toString());
			}
		}
		
		UUID[] allowedTemplateIds = script.getAllowedTemplateIds();
		List<String> allowedTemplateIdsList = new ArrayList<String>();
		if(allowedTemplateIds != null)
		{
			for(UUID allowedTemplateId : allowedTemplateIds)
			{
				allowedTemplateIdsList.add(allowedTemplateId.toString());
			}
		}
		
		scriptVO.setAllowedTemplates(allowedTemplateIdsList);
		scriptVO.setActionIds(actionIdsList);
		scriptVO.setStatIds(statIdsList);
		
//		JsonConfig config = new JsonConfig();  
//        config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor()); 
//        JSONObject json = JSONObject.fromObject(scriptVO,config);
//        return json.toString();
        
        return JSONArray.toJSONString(scriptVO);
	}
	
	/**
	 * @description:return all scripts
	 * @date:2014-5-5 下午8:38:23
	 * @version:v1.0
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getAllScript.do")
	@ResponseBody
	public String getAllScript() throws Exception {
		List<Script> scripts = das.queryAllScripts();
//		JsonConfig config = new JsonConfig();  
//        config.registerJsonValueProcessor(Timestamp.class, new DateJsonValueProcessor()); 
//		JSONArray json = JSONArray.fromObject(scripts,config);
//		return json.toString();
		return JSONArray.toJSONString(scripts);
	}
	
	@RequestMapping("/initTemplateActionAndStat.do")
	@ResponseBody
	public String initTemplateActionAndStat(@RequestParam("templateId") String templateId, @RequestParam("scriptId") String scriptId) throws Exception {
		StringBuffer result = new StringBuffer(this.baseXml);
		
		Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateId));
		Flow flow 		  = das.queryFlow(template.getFlowId());
		Script script     = null;
		UUID[] actionIds  = null;
		UUID[] endStatIds = null;
		if(scriptId!=null&&!"".equals(scriptId))
		{
			script = das.queryScript(DataAccessFactory.getInstance().createUUID(scriptId));
		}
		if(script != null)
		{
			actionIds  = script.getActionIds();
			endStatIds = script.getEndStatIds();
		}
		
		Action[] flowActions = flow.getActions();
		Stat[]   flowStats   = flow.getStats();
		result.append("<template>");
		result.append("<actions>");
		for(Action action : flowActions)
		{
			result.append("<action>");
			result.append("<id>").append(action.getId()).append("</id>");
			result.append("<name>").append(XMLUtil.toSafeXMLString(action.getName())).append("</name>");
			result.append("<select>");
			if(CynthiaUtil.idInArray(actionIds,action.getId().getValue()))
			{
				result.append(true);
			}else
			{
				result.append(false);
			}
			result.append("</select>");
			result.append("</action>");
		}
		result.append("</actions>");
		
		result.append("<stats>");
		for(Stat stat : flowStats)
		{
			result.append("<stat>");
			result.append("<id>").append(stat.getId()).append("</id>");
			result.append("<name>").append(XMLUtil.toSafeXMLString(stat.getName())).append("</name>");
			result.append("<select>");
			if(CynthiaUtil.idInArray(endStatIds,stat.getId().getValue()))
				result.append(true);
			else
				result.append(false);
			result.append("</select>");
			result.append("</stat>");
		}
		result.append("</stats>");
		result.append("</template>");
		return result.toString();
	}
}
