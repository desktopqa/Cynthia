<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@ page language="java" contentType="text/xml; charset=UTF-8"%>

<%@page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.CommonField"%>
<%@page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@page import="java.util.*"%>
<%@page import="org.w3c.dom.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>

<%!private static Map<String, CommonField> allFieldMap = new LinkedHashMap<String, CommonField>();
	private static Map<String, CommonField> selectedFieldMap = new LinkedHashMap<String, CommonField>();
	
	static
	{
		CommonField titleField = new CommonField();
		titleField.setId("title");
		titleField.setName("标题");
		titleField.setType("title");
		allFieldMap.put("title", titleField);
		selectedFieldMap.put("title", titleField);
		
		CommonField descriptionField = new CommonField();
		descriptionField.setId("description");
		descriptionField.setName("描述");
		descriptionField.setType("description");
		allFieldMap.put("description", descriptionField);
		
		CommonField statusIdField = new CommonField();
		statusIdField.setId("status_id");
		statusIdField.setName("状态");
		statusIdField.setType("status_id");
		allFieldMap.put("status_id", statusIdField);
		selectedFieldMap.put("status_id", statusIdField);
		
		CommonField assignUserField = new CommonField();
		assignUserField.setId("assign_user");
		assignUserField.setName("指派人");
		assignUserField.setType("assign_user");
		allFieldMap.put("assign_user", assignUserField);
		selectedFieldMap.put("assign_user", assignUserField);
		
		CommonField lastModifyTimeField = new CommonField();
		lastModifyTimeField.setId("last_modify_time");
		lastModifyTimeField.setName("修改时间");
		lastModifyTimeField.setType("last_modify_time");
		allFieldMap.put("last_modify_time", lastModifyTimeField);
		selectedFieldMap.put("last_modify_time", lastModifyTimeField);
		
		CommonField createUserField = new CommonField();
		createUserField.setId("create_user");
		createUserField.setName("创建人");
		createUserField.setType("create_user");
		allFieldMap.put("create_user", createUserField);
		
		CommonField createTimeField = new CommonField();
		createTimeField.setId("create_time");
		createTimeField.setName("创建时间");
		createTimeField.setType("create_time");
		allFieldMap.put("create_time", createTimeField);
	}

	protected String getDisplayFieldHTML(String prefix, Map<String, CommonField> allFieldMap, Map<String, CommonField> selectedFieldMap)
	{
		StringBuffer strb = new StringBuffer(64);
		
		strb.append("<div id=\"").append(prefix).append("_display\" width=\"100%\" height=\"100%\">");
		
		strb.append("<table width=\"10%\">");
		
		strb.append("<tr>");
		
		//deal with all fields
		strb.append("<td class=\"tdNoBottom\">");
		strb.append("<select id=\"").append(prefix).append("_restDisplaySelect\" size=\"10\" style=\"width:20em\" multiple>");		
		if(allFieldMap != null)
		{
			for(String fieldId : allFieldMap.keySet())
			{
				if(selectedFieldMap != null && selectedFieldMap.containsKey(fieldId))
					continue;
				
				CommonField field = allFieldMap.get(fieldId);
				
				strb.append("<option value=\"").append(fieldId).append("\"");
				strb.append(" type=\"").append(field.getType()).append("\"");
				if(field.getDataType() != null)
					strb.append(" dataType=\"").append(field.getDataType()).append("\"");
				strb.append(">");
				strb.append(XMLUtil.toSafeXMLString(field.getName()));
				strb.append("</option>");
			}
		}
		strb.append("</select>");
		strb.append("</td>");
		
		//deal with operate buttons
		strb.append("<td class=\"tdNoBottom\">");
		strb.append("<table width=\"100%\">");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<input type=\"button\" value=\"添    加\" onclick=\"moveOptions('").append(prefix).append("_restDisplaySelect','").append(prefix).append("_selectedDisplaySelect')\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<input type=\"button\" value=\"移    除\" onclick=\"moveOptions('").append(prefix).append("_selectedDisplaySelect','").append(prefix).append("_restDisplaySelect')\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("</table>");
		strb.append("</td>");
		
		//deal with selected fields
		strb.append("<td class=\"tdNoBottom\">");
		strb.append("<select id=\"").append(prefix).append("_selectedDisplaySelect\" size=\"10\" style=\"width:20em\" onchange=\"checkHold('").append(prefix).append("_selectedDisplaySelect')\" multiple>");		
		if(selectedFieldMap != null)
		{
			for(String fieldId : selectedFieldMap.keySet())
			{
				CommonField field = selectedFieldMap.get(fieldId);
				
				strb.append("<option value=\"").append(fieldId).append("\"");
				strb.append(" type=\"").append(field.getType()).append("\"");
				if(field.getDataType() != null)
					strb.append(" dataType=\"").append(field.getDataType()).append("\"");
				strb.append(">");
				strb.append(XMLUtil.toSafeXMLString(field.getName()));
				strb.append("</option>");
			}
		}
		strb.append("</select>");
		strb.append("</td>");
		
		//deal with control buttons
		strb.append("<td class=\"tdNoBottom\">");
		strb.append("<table width=\"100%\">");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<input type=\"button\" value=\"上    移\" onclick=\"executeControl('").append(prefix).append("_selectedDisplaySelect',true)\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<input type=\"button\" value=\"下    移\" onclick=\"executeControl('").append(prefix).append("_selectedDisplaySelect',false)\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<input type=\"button\" value=\"预    览\" onclick=\"previewDisplay('").append(prefix).append("_selectedDisplaySelect')\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("</table>");
		strb.append("</td>");
		
		strb.append("</tr>");
		
		strb.append("</table>");
		
		strb.append("</div>");
		
		return strb.toString();
	}

	protected String getOrderFieldHTML(String prefix, Map<String, CommonField> allFieldMap, Map<String, CommonField> selectedFieldMap, int indent)
	{
		StringBuffer strb = new StringBuffer(64);
		
		strb.append("<div id=\"").append(prefix).append("_order\" width=\"100%\" height=\"100%\">");
		
		strb.append("<table width=\"10%\">");
		
		strb.append("<tr>");
		
		//deal with rest fields
		strb.append("<td class=\"tdNoBottom\">");
		strb.append("<select id=\"").append(prefix).append("_restOrderSelect\" size=\"10\" style=\"width:20em\" multiple>");		
		if(allFieldMap != null)
		{
			for(String fieldId : allFieldMap.keySet())
			{
				if(selectedFieldMap != null && selectedFieldMap.containsKey(fieldId))
					continue;
				
				CommonField field = allFieldMap.get(fieldId);
				
				strb.append("<option value=\"").append(fieldId).append("\"");
				strb.append(" type=\"").append(field.getType()).append("\"");
				if(field.getDataType() != null)
					strb.append(" dataType=\"").append(field.getDataType()).append("\"");
				strb.append(">");
				strb.append(XMLUtil.toSafeXMLString(field.getName()));
				strb.append("</option>");
			}
		}
		strb.append("</select>");
		strb.append("</td>");
		
		//deal with operate buttons
		strb.append("<td class=\"tdNoBottom\">");
		strb.append("<table width=\"100%\">");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<input type=\"button\" value=\"升序添加\" onclick=\"orderOperation('").append(prefix).append("_restOrderSelect','").append(prefix).append("_selectedOrderSelect',1)\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<input type=\"button\" value=\"降序添加\" onclick=\"orderOperation('").append(prefix).append("_restOrderSelect','").append(prefix).append("_selectedOrderSelect',2)\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<input type=\"button\" value=\"移    除\" onclick=\"orderOperation('").append(prefix).append("_selectedOrderSelect','").append(prefix).append("_restOrderSelect',3)\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("</table>");
		strb.append("</td>");
		
		//deal with selected fields
		strb.append("<td class=\"tdNoBottom\">");
		strb.append("<select id=\"").append(prefix).append("_selectedOrderSelect\" size=\"10\" style=\"width:20em\" multiple>");		
		if(selectedFieldMap != null)
		{
			for(String fieldId : selectedFieldMap.keySet())
			{
				CommonField field = selectedFieldMap.get(fieldId);
				
				strb.append("<option value=\"").append(fieldId).append("\"");
				strb.append(" type=\"").append(field.getType()).append("\"");
				if(field.getDataType() != null)
					strb.append(" dataType=\"").append(field.getDataType()).append("\"");
				strb.append(" desc=\"" + field.isDesc() + "\">");
				strb.append(XMLUtil.toSafeXMLString(field.getName())).append("(").append(field.isDesc() ? "降序" : "升序").append(")");
				strb.append("</option>");
			}
		}
		strb.append("</select>");
		strb.append("</td>");
		
		//deal with control buttons
		strb.append("<td class=\"tdNoBottom\">");
		strb.append("<table width=\"100%\">");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\" colspan=\"2\">");
		strb.append("<input type=\"button\" value=\"上    移\" onclick=\"orderMove('").append(prefix).append("_selectedOrderSelect',true)\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\" colspan=\"2\">");
		strb.append("<input type=\"button\" value=\"下    移\" onclick=\"orderMove('").append(prefix).append("_selectedOrderSelect',false)\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<br><br><br><br><br>");
		strb.append("分组字段数：");
		strb.append("</td>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<br><br><br><br><br>");
		strb.append("<input id=\"").append(prefix).append("_group\" size=\"5\" value=\"").append(indent).append("\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("</table>");
		strb.append("</td>");
		
		strb.append("</tr>");
		
		strb.append("</table>");
		
		strb.append("</div>");
		
		return strb.toString();
	}
	
	protected Map<String, CommonField> getDisplayFieldMap(Node displayNode)
	{
		Map<String, CommonField> displayFieldMap = new LinkedHashMap<String, CommonField>();
		
		List<Node> fieldNodeList = XMLUtil.getNodes(displayNode, "field");
		for(Node fieldNode : fieldNodeList)
		{
			CommonField field = getCommonField(fieldNode);
			displayFieldMap.put(field.getId(), field);
		}
		
		return displayFieldMap;
	}
	
	protected Map<String, CommonField> getOrderFieldMap(Node orderNode)
	{
		Map<String, CommonField> orderFieldMap = new LinkedHashMap<String, CommonField>();
		
		List<Node> fieldNodeList = XMLUtil.getNodes(orderNode, "field");
		for(Node fieldNode : fieldNodeList)
		{
			CommonField field = getCommonField(fieldNode);
			orderFieldMap.put(field.getId(), field);
		}
		
		return orderFieldMap;
	}
	
	private CommonField getCommonField(Node fieldNode)
	{
		CommonField field = new CommonField();
		
		field.setId(XMLUtil.getAttribute(fieldNode, "id").trim());
		field.setName(XMLUtil.getAttribute(fieldNode, "name").trim());
		field.setType(XMLUtil.getAttribute(fieldNode, "type").trim());
		String dataType = XMLUtil.getAttribute(fieldNode, "dataType");
		if(dataType != null)
			field.setDataType(dataType.trim());
		String method = XMLUtil.getAttribute(fieldNode, "method");
		if(method != null)
			field.setMethod(method.trim());
		field.setValue(fieldNode.getTextContent().trim());
		boolean isAll = false;
		String isAllStr = XMLUtil.getAttribute(fieldNode, "isAll");
		if(isAllStr != null)
			isAll = Boolean.parseBoolean(isAllStr.trim());
		field.setAll(isAll);
		
		if(field.getName().equals("title"))
			field.setName("标题");
		else if(field.getName().equals("node_id"))
			field.setName("项目");
		else if(field.getName().equals("description"))
			field.setName("描述");
		else if(field.getName().equals("create_user"))
			field.setName("创建人");
		else if(field.getName().equals("create_time"))
			field.setName("创建时间");
		else if(field.getName().equals("assign_user"))
			field.setName("指派人");
		else if(field.getName().equals("last_modify_time"))
			field.setName("修改时间");
		else if(field.getName().equals("status_id"))
			field.setName("状态");
			
		return field;
	}%>

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

	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
	
	String filterIdStr = request.getParameter("filterId");
	if(filterIdStr == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.param_error));
		return;
	}
	
	UUID filterId = DataAccessFactory.getInstance().createUUID(filterIdStr);
	
	Filter filter = das.queryFilter(filterId);
	
	String operation = request.getParameter("operation");
	if(operation == null || !operation.equals("create") && !operation.equals("modify"))
		return;
	
	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	xmlb.append("<root>");
	xmlb.append("<isError>false</isError>");
	
	Document doc = XMLUtil.string2Document(filter.getXml(), "UTF-8");
	
	Node queryNode = XMLUtil.getSingleNode(doc, "query");
	
	Node templateTypeNode = XMLUtil.getSingleNode(queryNode, "templateType");
	List<Node> templateNodeList = XMLUtil.getNodes(queryNode, "template");
	
	String templateTypeIdStr = XMLUtil.getAttribute(templateTypeNode, "id");
	String templateTypeName = XMLUtil.getAttribute(templateTypeNode, "name");
	
	xmlb.append("<templateType id=\"").append(templateTypeIdStr).append("\"");
	xmlb.append(" name=\"" + XMLUtil.toSafeXMLString(templateTypeName) + "\"");
	
	if(templateNodeList.size() == 0)
	{
		xmlb.append(">");
		
		Map<String, CommonField> templateTypeDisplaySelectedFeildMap = new LinkedHashMap<String, CommonField>();
		Map<String, CommonField> templateTypeOrderSelectedFeildMap = new LinkedHashMap<String, CommonField>();
		
		int indent = 0;
	
		if(operation.equals("create"))
	templateTypeDisplaySelectedFeildMap.putAll(selectedFieldMap);
		else
		{
	List<Node> displayFieldNodeList = XMLUtil.getNodes(templateTypeNode, "display/field");
	for(Node displayFieldNode : displayFieldNodeList)
	{
		CommonField commonField = new CommonField();
		commonField.setId(XMLUtil.getAttribute(displayFieldNode, "id"));
		commonField.setName(allFieldMap.containsKey(commonField.getId()) ? allFieldMap.get(commonField.getId()).getName() : XMLUtil.getAttribute(displayFieldNode, "name"));
		commonField.setType(XMLUtil.getAttribute(displayFieldNode, "type"));
		commonField.setDataType(XMLUtil.getAttribute(displayFieldNode, "dataType"));
			
		templateTypeDisplaySelectedFeildMap.put(commonField.getId(), commonField);
	}
	
	Node orderNode = XMLUtil.getSingleNode(templateTypeNode, "order");
	if(orderNode != null && XMLUtil.getAttribute(orderNode, "indent") != null)
		indent = Integer.parseInt(XMLUtil.getAttribute(orderNode, "indent"));
		
	List<Node> orderFieldNodeList = XMLUtil.getNodes(templateTypeNode, "order/field");
	for(Node orderFieldNode : orderFieldNodeList)
	{
		CommonField commonField = new CommonField();
		commonField.setId(XMLUtil.getAttribute(orderFieldNode, "id"));
		commonField.setName(allFieldMap.containsKey(commonField.getId()) ? allFieldMap.get(commonField.getId()).getName() : XMLUtil.getAttribute(orderFieldNode, "name"));
		commonField.setType(XMLUtil.getAttribute(orderFieldNode, "type"));
		commonField.setDataType(XMLUtil.getAttribute(orderFieldNode, "dataType"));
		
		templateTypeOrderSelectedFeildMap.put(commonField.getId(), commonField);
	}
		}
		
		String displayHtml = getDisplayFieldHTML("display_" + templateTypeIdStr, allFieldMap, templateTypeDisplaySelectedFeildMap);
		if(displayHtml.length() > 0)
	xmlb.append("<display>").append(XMLUtil.toSafeXMLString(displayHtml)).append("</display>");
		else
	xmlb.append("<display/>");
		
		String orderHtml = getOrderFieldHTML("order_" + templateTypeIdStr, allFieldMap, templateTypeOrderSelectedFeildMap, indent);
		if(orderHtml.length() > 0)
	xmlb.append("<order>").append(XMLUtil.toSafeXMLString(orderHtml)).append("</order>");
		else
	xmlb.append("<order/>");
		
		xmlb.append("</templateType>");
	}
	else
	{
		xmlb.append("/>");
		
		xmlb.append("<templates>");
		
		for(Node templateNode : templateNodeList)
		{	
	UUID templateId = DataAccessFactory.getInstance().createUUID(XMLUtil.getAttribute(templateNode, "id"));
	
	Template template = das.queryTemplate(templateId);
	if(template == null)
		continue;
	
	String isCloseStr = XMLUtil.getAttribute(templateNode, "close");
	
	xmlb.append("<template id=\"").append(templateId).append("\"");
	xmlb.append(" name=\"").append(XMLUtil.toSafeXMLString(template.getName())).append("\"");
	xmlb.append(" isClose=\"").append(isCloseStr != null ? isCloseStr : false).append("\">");
	
	Map<String, CommonField> templateAllFeildMap = new LinkedHashMap<String, CommonField>();
	templateAllFeildMap.putAll(allFieldMap);
	
	Set<Field> fieldSet = template.getFields();
	for(Field field : fieldSet)
	{
		CommonField commonField = new CommonField();
		commonField.setId(field.getId().toString());
		commonField.setName(field.getName());
		commonField.setType(field.getType().toString());
		if(field.getDataType() != null)
			commonField.setDataType(field.getDataType().toString());
		
		templateAllFeildMap.put(commonField.getId(), commonField);
	}
	
	Map<String, CommonField> templateDisplaySelectedFeildMap = new LinkedHashMap<String, CommonField>();
	Map<String, CommonField> templateOrderSelectedFeildMap = new LinkedHashMap<String, CommonField>();
	
	int indent = 0;
	
	if(operation.equals("create"))
		templateDisplaySelectedFeildMap.putAll(selectedFieldMap);
	else
	{
		List<Node> displayFieldNodeList = XMLUtil.getNodes(templateNode, "display/field");
		for(Node displayFieldNode : displayFieldNodeList)
		{
			CommonField commonField = new CommonField();
			commonField.setId(XMLUtil.getAttribute(displayFieldNode, "id"));
			commonField.setName(allFieldMap.containsKey(commonField.getId()) ? allFieldMap.get(commonField.getId()).getName() : XMLUtil.getAttribute(displayFieldNode, "name"));
			commonField.setType(XMLUtil.getAttribute(displayFieldNode, "type"));
			commonField.setDataType(XMLUtil.getAttribute(displayFieldNode, "dataType"));
			
			templateDisplaySelectedFeildMap.put(commonField.getId(), commonField);
		}
		
		Node orderNode = XMLUtil.getSingleNode(templateNode, "order");
		if(orderNode != null && XMLUtil.getAttribute(orderNode, "indent") != null)
			indent = Integer.parseInt(XMLUtil.getAttribute(orderNode, "indent"));
		
		List<Node> orderFieldNodeList = XMLUtil.getNodes(templateNode, "order/field");
		for(Node orderFieldNode : orderFieldNodeList)
		{
			CommonField commonField = new CommonField();
			commonField.setId(XMLUtil.getAttribute(orderFieldNode, "id"));
			commonField.setName(allFieldMap.containsKey(commonField.getId()) ? allFieldMap.get(commonField.getId()).getName() : XMLUtil.getAttribute(orderFieldNode, "name"));
			commonField.setType(XMLUtil.getAttribute(orderFieldNode, "type"));
			commonField.setDataType(XMLUtil.getAttribute(orderFieldNode, "dataType"));
			
			templateOrderSelectedFeildMap.put(commonField.getId(), commonField);
		}
	}
	
	String displayHtml = getDisplayFieldHTML("display_" + templateId, templateAllFeildMap, templateDisplaySelectedFeildMap);
	if(displayHtml.length() > 0)
		xmlb.append("<display>").append(XMLUtil.toSafeXMLString(displayHtml)).append("</display>");
	else
		xmlb.append("<display/>");
	
	String orderHtml = getOrderFieldHTML("order_" + templateId, templateAllFeildMap, templateOrderSelectedFeildMap, indent);
	if(orderHtml.length() > 0)
		xmlb.append("<order>").append(XMLUtil.toSafeXMLString(orderHtml)).append("</order>");
	else
		xmlb.append("<order/>");
	
	xmlb.append("</template>");
		}
		
		xmlb.append("</templates>");
	}
	
	xmlb.append("</root>");
	
	out.println(xmlb);
%>
