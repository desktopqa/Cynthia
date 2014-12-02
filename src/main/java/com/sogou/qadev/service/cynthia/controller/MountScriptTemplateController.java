package com.sogou.qadev.service.cynthia.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sogou.qadev.service.cynthia.bean.Action;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Script;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

/**
 * @description:script mount processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-5 下午8:30:47
 * @version:v1.0
 */
@Controller
@RequestMapping("/mountScript")
public class MountScriptTemplateController extends BaseController {

	/**
	 * @description:get all templates with script info
	 * @date:2014-5-5 下午8:31:02
	 * @version:v1.0
	 * @param das
	 * @return
	 */
	public String getAllTemplateWithScriptXml(DataAccessSession das) {
		Template[] allTemplates = das.queryAllTemplates();
		StringBuffer result = new StringBuffer();
		result.append(baseXml);
		result.append("<templates>");
		for(Template template : allTemplates)
		{
			Script[] templateScripts = das.queryTemplateScripts(template.getId());
			result.append("<template>");
			result.append("<id>");
			result.append(template.getId().toString());
			result.append("</id>");
			result.append("<name>");
			result.append(XMLUtil.toSafeXMLString(template.getName()));
			result.append("</name>");
			result.append("<scripts>");
			for(Script templateScript : templateScripts)
			{
				result.append("<script>");
				result.append("<id>");
				result.append(templateScript.getId().toString());
				result.append("</id>");
				result.append("<name>");
				result.append(XMLUtil.toSafeXMLString(templateScript.getName()));
				result.append("</name>");
				result.append("</script>");
			}
			result.append("</scripts>");
			result.append("</template>");
			
			templateScripts = null;
		}
		result.append("</templates>");
		allTemplates = null;
		return result.toString();
	}

	/**
	 * @description:get all templates(id and name)
	 * @date:2014-5-5 下午8:31:26
	 * @version:v1.0
	 * @param das
	 * @return
	 */
	public String getAllTemplatesXml(DataAccessSession das) 
	{
		Template[] allTemplates = das.queryAllTemplates();
		StringBuffer result = new StringBuffer();
		result.append(baseXml);
		result.append("<templates>");
		for(Template template : allTemplates)
		{
			result.append("<template>");
			result.append("<id>");
			result.append(template.getId().toString());
			result.append("</id>");
			result.append("<name>");
			result.append(XMLUtil.toSafeXMLString(template.getName()));
			result.append("</name>");
			result.append("</template>");
		}
		result.append("</templates>");
		allTemplates = null;
		return result.toString();
	}
	
	@RequestMapping("/getAllTemplates.do")
	@ResponseBody
	public String getAllTemplate(@RequestParam("type")String type) throws Exception {
		
		if(type!=null&&"script".equals(type))
			return getAllTemplateWithScriptXml(das);
		else
			return getAllTemplatesXml(das);
	}
	
	/**
	 * @description:mount a script for template
	 * @date:2014-5-5 下午8:31:49
	 * @version:v1.0
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/mountScriptTemplate.do")
	public String mountScriptTemplate(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String scriptId   = request.getParameter("scriptId");
		String templateId = request.getParameter("templateId");
		String actions    = request.getParameter("actions");
		String stats      = request.getParameter("stats");
		Script script     = das.queryScript(DataAccessFactory.getInstance().createUUID(scriptId));

		UUID[] oldTemplateIds = script.getTemplateIds();
		if(!CynthiaUtil.idInArray(oldTemplateIds,templateId))
		{
			List<UUID> oldTemplateIdsList = new ArrayList<UUID>();
			if(oldTemplateIds!=null)
			{
				for(UUID oldTemplateId : oldTemplateIds)
				{
					oldTemplateIdsList.add(oldTemplateId);
				}
			}
			if(templateId!=null&&!templateId.equals("")&&!"null".equals(templateId))
				oldTemplateIdsList.add(DataAccessFactory.getInstance().createUUID(templateId));
			script.setTemplateIds(oldTemplateIdsList.toArray(new UUID[oldTemplateIdsList.size()]));
		}
		
		Template newTemplate = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateId));
		Flow     newFlow     = das.queryFlow(newTemplate.getFlowId());
		Stat[]   newFlowStats= newFlow.getStats();
		Action[] newFlowActions=newFlow.getActions();
		List<UUID> newFlowStatIds = new ArrayList<UUID>();
		List<UUID> newFlowActionIds = new ArrayList<UUID>();
		for(Stat stat : newFlowStats)
		{
			newFlowStatIds.add(stat.getId());
		}
		for(Action action : newFlowActions)
		{
			newFlowActionIds.add(action.getId());
		}
		
		if(actions!=null)
		{
			UUID[] oldActionIds = script.getActionIds();
			List<UUID> oldActionIdsList = new ArrayList<UUID>();
			if(oldActionIds != null)
			{
				for(UUID oldActionId : oldActionIds)
				{
					oldActionIdsList.add(oldActionId);
				}
			}
			UUID[] newActionIds = CynthiaUtil.stringToIdArray(actions);
			for(UUID actionId : newActionIds)
			{
				if(!oldActionIdsList.contains(actionId))
				{
					oldActionIdsList.add(actionId);
				}
				newFlowActionIds.remove(actionId);
			}
			
			for(UUID actionId : newFlowActionIds)
			{
				if(oldActionIdsList.contains(actionId))
					oldActionIdsList.remove(actionId);
			}
			script.setActionIds(oldActionIdsList.toArray(new UUID[oldActionIdsList.size()]));
		}
		
		if(stats != null)
		{
			UUID[] oldStatIds = script.getEndStatIds();
			List<UUID> oldStatIdsList = new ArrayList<UUID>();
			if(oldStatIds != null)
			{
				for(UUID oldStatId : oldStatIds)
				{
					oldStatIdsList.add(oldStatId);
				}
			}
			UUID[] newStatIds = CynthiaUtil.stringToIdArray(stats);
			for(UUID statId : newStatIds)
			{
				if(!oldStatIdsList.contains(statId))
				{
					oldStatIdsList.add(statId);
				}
				newFlowStatIds.remove(statId);
			}
			
			for(UUID statId : newFlowStatIds)
			{
				if(oldStatIdsList.contains(statId))
					oldStatIdsList.remove(statId);
			}
			
			script.setEndStatIds(oldStatIdsList.toArray(new UUID[oldStatIdsList.size()]));
		}
		das.updateScript(script);
		StringBuffer result = new StringBuffer(this.baseXml);
		result.append("<root>");
		result.append("<id>").append(script.getId()).append("</id>");
		result.append("</root>");
		return result.toString();
	}
	
	
	@ResponseBody
	@RequestMapping("/unMountScriptTemplate.do")
	public String unMountScriptTemplate(@RequestParam("scriptId") String scriptId, @RequestParam("templateId") String templateId) throws Exception {
		
		Script script     = das.queryScript(DataAccessFactory.getInstance().createUUID(scriptId));
		
		UUID[] oldTemplateIds = script.getTemplateIds();
		List<UUID> newTemplateIds = new ArrayList<UUID>();
		for(UUID oldTemplateId : oldTemplateIds)
		{
			if(!oldTemplateId.toString().equals(templateId))
			{
				newTemplateIds.add(oldTemplateId);
			}
		}
		script.setTemplateIds(newTemplateIds.toArray(new UUID[newTemplateIds.size()]));
		
		UUID[] oldStatIds = script.getEndStatIds();
		UUID[] oldActionIds = script.getActionIds();
		List<UUID> oldStatIdsList = new ArrayList<UUID>();
		List<UUID> oldActionIdsList =new ArrayList<UUID>();
		
		if(oldStatIds != null)
		{
			for(UUID oldStatId : oldStatIds)
			{
				oldStatIdsList.add(oldStatId);
			}
		}
		
		if(oldActionIds!=null)
		{
			for(UUID oldActionId : oldActionIds)
			{
				oldActionIdsList.add(oldActionId);
			}
		}
		
		List<UUID> newFlowIds = new ArrayList<UUID>();
		for(UUID tempId : newTemplateIds)
		{
			Template template = das.queryTemplate(tempId);
			if(template!=null)
				newFlowIds.add(template.getFlowId());
		}
		Template removedTemplate = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateId));
		if(!newFlowIds.contains(removedTemplate.getFlowId()))
		{
			Flow removedFlow = das.queryFlow(removedTemplate.getFlowId());
			Stat[] removedFlowStats = removedFlow.getStats();
			for(Stat stat : removedFlowStats)
			{
				if(oldStatIdsList.contains(stat.getId()))
					oldStatIdsList.remove(stat.getId());
			}
			
			Action[] removedFlowActions = removedFlow.getActions();
			for(Action action : removedFlowActions)
			{
				if(oldActionIdsList.contains(action.getId()))
				{
					oldActionIdsList.remove(action.getId());
				}
			}
		}
		script.setEndStatIds(oldStatIdsList.toArray(new UUID[oldStatIdsList.size()]));
		script.setActionIds(oldActionIdsList.toArray(new UUID[oldActionIdsList.size()]));
		das.updateScript(script);
		StringBuffer result = new StringBuffer(this.baseXml);
		result.append("<root>");
		result.append("<id>").append(script.getId()).append("</id>");
		result.append("</root>");
		return result.toString();
	}
	
	/**
	 * @description:init all scripts mounted on template
	 * @date:2014-5-5 下午8:32:06
	 * @version:v1.0
	 * @param templateIdStr
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/initTemplateScripts.do")
	public String initTemplateScripts(@RequestParam("templateId") String templateIdStr) throws Exception {
		
		UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
		
		Script[] templateScripts = das.queryTemplateScripts(templateId);
		Script[] allowedScripts  = das.queryAllowedTemplateScripts(templateId);
		Template template            = das.queryTemplate(templateId);
		Flow flow 		             = das.queryFlow(template.getFlowId());
		Stat[] stats                 = flow.getStats();
		Action [] actions            = flow.getActions();
		
		StringBuffer xml = new StringBuffer(this.baseXml);
		xml.append("<root>");
		xml.append("<template>");
		xml.append("<id>").append(template.getId()).append("</id>");
		xml.append("<name>").append(XMLUtil.toSafeXMLString(template.getName())).append("</name>");
		xml.append("<stats>");
		for(Stat stat : stats)
		{
			xml.append("<stat>");
			xml.append("<id>").append(stat.getId()).append("</id>");
			xml.append("<name>").append(XMLUtil.toSafeXMLString(stat.getName())).append("</name>");
			xml.append("</stat>");
		}
		xml.append("</stats>");
		
		xml.append("<actions>");
		for(Action action : actions)
		{
			xml.append("<action>");
			xml.append("<id>").append(action.getId()).append("</id>");
			xml.append("<name>").append(XMLUtil.toSafeXMLString(action.getName())).append("</name>");
			xml.append("</action>");
		}
		xml.append("</actions>");
		xml.append("</template>");
		
		xml.append("<mountscripts>");
		for(Script script : templateScripts)
		{
			xml.append("<script>");
			xml.append("<id>").append(script.getId()).append("</id>");
			xml.append("<name>").append(XMLUtil.toSafeXMLString(script.getName())).append("</name>");
			xml.append("</script>");
		}
		xml.append("</mountscripts>");
		
		xml.append("<allowedscripts>");
		for(Script script : allowedScripts)
		{
			xml.append("<script>");
			xml.append("<id>").append(script.getId()).append("</id>");
			xml.append("<name>").append(XMLUtil.toSafeXMLString(script.getName())).append("</name>");
			xml.append("</script>");
		}
		xml.append("</allowedscripts>");
		xml.append("</root>");
		return xml.toString();
	}
	
}
