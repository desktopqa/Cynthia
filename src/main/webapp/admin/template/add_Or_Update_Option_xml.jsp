<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.*"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.TemplateOperateLog"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.EscapeUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option.Forbidden"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="javax.xml.parsers.*"%>

<%
	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
	response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
	response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility

	out.clear();

	Key key = (Key)session.getAttribute("key");
	Long keyId = (Long)session.getAttribute("kid");

	if(keyId == null || keyId <= 0 || key == null){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
	String requestXml = request.getParameter("xml");
	if(requestXml==null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.param_error));
		return;
	}
	
	requestXml = xmlHeader + requestXml;
	
	Document doc = XMLUtil.string2Document(requestXml, "UTF-8");
	Node rootNode = XMLUtil.getSingleNode(doc, "root");
	Node templateIdNode = XMLUtil.getSingleNode(rootNode, "templateId");
	Node fieldIdNode    = XMLUtil.getSingleNode(rootNode, "fieldId");
	Node controlFieldOptionIdNode = XMLUtil.getSingleNode(rootNode, "controlFieldOptionId");
	String controlFieldOptionIdStr = controlFieldOptionIdNode.getTextContent();
	
	List<Node> optionNodes = XMLUtil.getNodes(rootNode, "options/option");
	
	
	UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdNode.getTextContent());
	
	Template template = das.queryTemplate(templateId);
	if(template == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.template_not_found));
		return;
	}
	
	//备份template
// 	template = (Template)template.clone();
	
	UUID fieldId = DataAccessFactory.getInstance().createUUID(fieldIdNode.getTextContent());
	
	Field field = template.getField(fieldId);
	
	String fieldXmlBefore = field.toXMLString();
	
	if(field == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.field_update_error));
		return;
	}
	
	Set<Option> oriOptions = field.getOptions();
	List<String> newOptionIds = new ArrayList<String>();
	
	for(Node optionNode : optionNodes)
	{
		String optionNodeId = XMLUtil.getSingleNodeTextContent(optionNode, "optionId");
		if(optionNodeId!=null&&!"".equals(optionNodeId))
		{
	newOptionIds.add(optionNodeId);
		}
	}
	
	
	//先删除数据 把不在新的id列表里面的删除掉
	List<Option> removedOriOptions = new ArrayList<Option>();
	for(Option oriOption : oriOptions)
	{
		if(controlFieldOptionIdStr!=null&&!"".equals(controlFieldOptionIdStr))
		{
	if(oriOption.getControlOptionId().getValue().equals(controlFieldOptionIdStr))
	{
		if(!newOptionIds.contains(oriOption.getId().toString()))
		{
	removedOriOptions.add(oriOption);
		}
	}
	
		}else
		{
	if(!newOptionIds.contains(oriOption.getId().toString()))
	{
		removedOriOptions.add(oriOption);
	}
		}
		
	}
	
	for(Option removedOption : removedOriOptions)
	{
		field.removeOption(removedOption.getId());
	}
	
	//删除完毕,重新设置indexOrder和新增选项
	for(Node optionNode : optionNodes)
	{
		String optionIdStr 		  = XMLUtil.getSingleNodeTextContent(optionNode, "optionId");
		String optionNameStr  	  = XMLUtil.getSingleNodeTextContent(optionNode, "optionName");
		String optionForbiddenStr = XMLUtil.getSingleNodeTextContent(optionNode, "optionForbidden");
		String optionIndexOrder   = XMLUtil.getSingleNodeTextContent(optionNode, "optionIndexOrder");
		
		if(optionIdStr == null || "".equals(optionIdStr)) //新增一个option
		{
	Option option = field.addOption();
	if(option == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.field_update_error));
		return;
	}
	option.setName(EscapeUtil.decodeAll(optionNameStr));
	option.setForbidden(Forbidden.valueOf(optionForbiddenStr));
	option.setIndexOrder(Integer.parseInt(optionIndexOrder));
	if(controlFieldOptionIdStr != null&&!"".equals(controlFieldOptionIdStr))
	{
		option.setControlOptionId(DataAccessFactory.getInstance().createUUID(controlFieldOptionIdStr));
	}
		}else
		{
	UUID optionId = DataAccessFactory.getInstance().createUUID(optionIdStr);
	for(Option oriOption : oriOptions)
	{
		if(oriOption.getId().equals(optionId))
		{
	oriOption.setName(EscapeUtil.decodeAll(optionNameStr));
	oriOption.setForbidden(Forbidden.valueOf(optionForbiddenStr));
	oriOption.setIndexOrder(Integer.parseInt(optionIndexOrder));
		}
	}
		}
	}
	
	ErrorCode errorCode = das.updateTemplate(template);
	
	if(errorCode.equals(ErrorCode.success)){
		das.updateCache(DataAccessAction.update, template.getId().getValue(),template);
		
		//记录修改日志
		TemplateOperateLog tol = new TemplateOperateLog();
		tol.setTemplateId(templateId.getValue());
		tol.setFieldId(field.getId().getValue());
		tol.setFieldName(field.getName());
		tol.setOperateType(TemplateOperateLog.MODIFY);
		tol.setCreateTime(Timestamp.valueOf(CynthiaUtil.toLocalDateString(null)));
		tol.setCreateUser(key.getUsername());
		tol.setBefore(fieldXmlBefore);
		tol.setAfter(field.toXMLString());
		das.addTemplateOpreateLog(tol);
		
		out.println(ErrorManager.getCorrectXml());
	}else{
		out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
	}
%>