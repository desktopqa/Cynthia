<%@page import="com.sogou.qadev.service.cynthia.util.Date"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UserInfo"%>
<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@page import="com.sogou.qadev.cache.impl.TemplateCache"%>
<%@ page language="java" pageEncoding="UTF-8"%>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil" %>
<%@ page import="com.sogou.qadev.service.cynthia.util.URLUtil" %>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataManager" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Data" %>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory" %>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.TemplateType" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field.Type" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field.DataType" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Attachment" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Stat" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID" %>
<%@ page import="com.sogou.qadev.service.cynthia.util.Date" %>
<%@ page import="com.sogou.qadev.service.cynthia.util.CommonUtil" %>
<%@ page import="java.util.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="javax.xml.parsers.*"%>

<%
response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility
%>

<%!class SegmentTagBase
{
	public SegmentTagBase()
	{
	}

	void fillBySegmentTagBase(SegmentTagBase segmentTagBase)
	{
		this.indent = segmentTagBase.indent;
		this.indentFieldsName = segmentTagBase.indentFieldsName;
		this.displayFieldsName = segmentTagBase.displayFieldsName;
	}

	public int indent = 0;

	public String[] displayFieldsName = null;
	public String[] indentFieldsName = null;
}

class TemplateSegmentTag extends SegmentTagBase
{
	public TemplateSegmentTag()
	{
		super();
	}
	
	public UUID id = null;
	public String name = null;
}

class TemplateTypeSegmentTag extends SegmentTagBase
{
	public TemplateTypeSegmentTag()
	{
		super();
	}

	public UUID templateTypeId = null;
	
	public String templateTypeName = null;
}

List<Node> convertNodeList(NodeList nodeList)
{
	List<Node> list = new ArrayList<Node>(nodeList.getLength());
	for (int i = 0; i < nodeList.getLength(); i ++)
	{
		if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE)
			continue;

		list.add(nodeList.item(i));
	}
	
	return list;
}

// 传入templateType节点或者template节点，生成SegmentTagBase
SegmentTagBase createSegmentTagBase(Element element, DataAccessSession das)
{
	NodeList tempNodeList = element.getChildNodes();
	NodeList displayFields = null;
	NodeList indentFields = null;

	int indent = 0;

	// 获取显示字段和缩进字段列表
	for( int i = 0; i < tempNodeList.getLength(); i++ )
	{
		if ( tempNodeList.item( i ).getNodeName().equals( "display" ) )
		{
			displayFields = ((Element)tempNodeList.item( i )).getChildNodes();
		}
		else if ( tempNodeList.item( i ).getNodeName().equals( "order" ) )
		{
			String indentStr = ((Element)tempNodeList.item( i )).getAttribute("indent");
			if (indentStr != null && indentStr.length() > 0)
				indent = Integer.parseInt(indentStr);

			indentFields = ((Element)tempNodeList.item( i )).getChildNodes();
		}
	}

	SegmentTagBase segmentTagBase = new SegmentTagBase();

	// 生成显示字段和缩进字段名称集合
	Set<String> displayFieldsNameSet = new LinkedHashSet<String>();
	Set<String> indentFieldsNameSet = new LinkedHashSet<String>();

	//将所有字段都加到显示字段中去 add by liu 2012-06-15
	String templateIdStr = element.getAttribute("id");
	Template template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(templateIdStr));

	// 填充显示字段和缩进字段名称集合
	displayFieldsNameSet.addAll(Arrays.asList(new String[]{"标题","描述","创建时间","指派人","状态","修改时间","创建人"}));
	displayFieldsNameSet.addAll(Arrays.asList(createFieldsNameByFieldList(convertNodeList(displayFields), das , template)));
	
	if(template!=null)
	{
		Set<Field> fields = template.getFields();
		for(Field field : fields)
		{
			if(!displayFieldsNameSet.contains(field.getName()))
			{
				displayFieldsNameSet.add(field.getName());
			}
		}
	
	}
	//end of add
	

	if (indentFields != null)
		indentFieldsNameSet.addAll(Arrays.asList(createFieldsNameByFieldList(convertNodeList(indentFields), das , template )));

	// 安全性检查
	// 检查缩进字段的每一项是否都是显示字段
	if (indent > 0)
	{
		int nameIndex = 0;
		for (String name : indentFieldsNameSet)
		{
			if (displayFieldsNameSet.contains(name))
			{
				nameIndex ++;
				if (nameIndex < indent)
					continue;
			}
			
			indent = nameIndex;
			break;
		}
	}

	// 检查缩进字段长度是否超过全部可缩进字段数量

	indent = ((indent <= indentFieldsNameSet.size()) ? indent : indentFieldsNameSet.size());

	segmentTagBase.indent = indent;

	// 转换名称集合到名称数组
	segmentTagBase.displayFieldsName = displayFieldsNameSet.toArray(new String[0]);

	if (indent > 0)
	{
		segmentTagBase.indentFieldsName = new String[indent];
		System.arraycopy(indentFieldsNameSet.toArray(new String[0]), 0, segmentTagBase.indentFieldsName, 0, indent);
	}
	else
	{
		segmentTagBase.indentFieldsName = null;
	}
	
	return segmentTagBase;
}

List<TemplateSegmentTag> createTemplateSegmentTagList(NodeList nodeList, DataAccessSession das)
{
	List<TemplateSegmentTag> segmentTagList = new ArrayList<TemplateSegmentTag>(nodeList.getLength());

	// 依次显示各张表单
	for( int itemplate = 0; nodeList != null && itemplate < nodeList.getLength(); itemplate++ )
	{
		// 取得表单的节点
		Element element = (Element)nodeList.item( itemplate );
		if( element.getNodeName().equals( "template" ) )
		{
			boolean close = false;
			if( element.getAttribute( "close" ) != null )
			{
				try
				{
					close = Boolean.parseBoolean(element.getAttribute( "close" ));
				}
				catch(Exception e){}
			}
			
			if(close)
				continue;
			
			TemplateSegmentTag segmentTag = new TemplateSegmentTag();
			
			// 记录表单ID和NodeId
			if( element.getAttribute( "id" ) != null )
				segmentTag.id = DataAccessFactory.getInstance().createUUID( element.getAttribute( "id" ) );
			
			if( element.getAttribute( "name" ) != null )
				segmentTag.name = element.getAttribute( "name" );
			
			segmentTag.fillBySegmentTagBase(createSegmentTagBase(element, das));

			segmentTagList.add( segmentTag );
		}
	}
	
	return segmentTagList;
}

TemplateTypeSegmentTag createTemplateTypeSegmentTag(Element element, DataAccessSession das)
{
	TemplateTypeSegmentTag segmentTag = new TemplateTypeSegmentTag();

	String templateTypeId = element.getAttribute( "id" );
	if (templateTypeId != null)
		segmentTag.templateTypeId = DataAccessFactory.getInstance().createUUID(templateTypeId);

	segmentTag.templateTypeName = element.getAttribute( "name" );
	
	segmentTag.fillBySegmentTagBase(createSegmentTagBase(element, das));

	return segmentTag;
}

String convertFieldName(String fieldName)
{
	return ConfigUtil.baseFieldNameMap.get(fieldName);
}

String createTaskHeader( String[] fieldsName, int indent, int orderIndex, boolean isDesc )
{
	StringBuilder buffer = new StringBuilder();

	if (indent > 0)
	{
		buffer.append("<td class='ttbg' nowrap>");
		for (int i = 0; i < indent; i ++)
			buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		buffer.append("</td>");
	}

	buffer.append("<td nowrap></td>");
	buffer.append("<td nowrap>序号</td>");
	buffer.append("<td nowrap>ID</td>");
	
	int i = 0;
	for(String fieldName : fieldsName)
	{
		String image = "";
		if( orderIndex == i )
		{
			if( isDesc )
				image = "<img src='../images/bluedrop.gif'>";
			else
				image = "<img src='../images/blueup.gif'>";
		}
		buffer.append("<td style='cursor:pointer' nowrap displayIndex='" + ( i++ ) + "' onclick='insertOrder(event);'>").append(fieldName).append( image ).append("</td>");
	}
	
	return buffer.toString();
}

String createTaskHeader(String[] fieldsName, int indent)
{
	return createTaskHeader( fieldsName, indent, -1, false );
}

String createItemDescription(NodeList taskNodeList)
{
	StringBuilder buffer = new StringBuilder();
	for (int i = 0; i < taskNodeList.getLength(); i ++)
	{
		Node node = taskNodeList.item(i);
		
		if (!node.getNodeName().equals("field"))
			continue;

		String value = node.getAttributes().getNamedItem("value").getTextContent();
		String name = node.getAttributes().getNamedItem("name").getTextContent();
		
		if(name.equals("编号")
				|| name.equals("节点")
						|| name.equals("表单")
								|| name.equals("标题"))
			continue;

		buffer.append(name).append(": ").append(value).append("<br>");
	}
	
	return buffer.toString();
}

String[] getAllDisplayFields(Data data, DataAccessSession das)
{
	List<String> list = new LinkedList<String>();

	list.add(ConfigUtil.baseFieldNameMap.get("title"));
	list.add(ConfigUtil.baseFieldNameMap.get("description"));
	list.add(ConfigUtil.baseFieldNameMap.get("status_id"));
	list.add(ConfigUtil.baseFieldNameMap.get("create_user"));
	list.add(ConfigUtil.baseFieldNameMap.get("create_time"));
	list.add(ConfigUtil.baseFieldNameMap.get("assign_user"));
	list.add(ConfigUtil.baseFieldNameMap.get("last_modify_time"));
	list.add(ConfigUtil.baseFieldNameMap.get("node_id"));
	list.add(ConfigUtil.baseFieldNameMap.get("action_id"));
	list.add(ConfigUtil.baseFieldNameMap.get("action_user"));
	list.add(ConfigUtil.baseFieldNameMap.get("action_comment"));
// 	list.add(ConfigUtil.baseFieldNameMap.get("action_index"));

	Map<String, String> fieldMap = createMapByTaskFieldValue(data, das);
	for (Map.Entry<String, String> entry : fieldMap.entrySet())
		list.add(entry.getKey());

	return list.toArray(new String[list.size()]);
}

String createItemTableRow(Data data, DataAccessSession das, int index) throws Exception
{
	Template template = das.queryTemplate(data.getTemplateId());
	Set<UUID> attachmentFieldIdSet = new HashSet<UUID>();
	if(template.getFields() != null)
	{
		for(Field field : template.getFields())
		{
			if(field.getType().equals(Type.t_attachment))
				attachmentFieldIdSet.add(field.getId());
		}
	}

	boolean hasAttachment = false;
	for(UUID attachmentFieldId : attachmentFieldIdSet)
	{
		UUID[] attachmentIdArray = data.getAttachments(attachmentFieldId);
		if(attachmentIdArray != null && attachmentIdArray.length > 0)
		{
			hasAttachment = true;
			break;
		}
	}

	// 组织结果的XML文档
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
	DocumentBuilder builder = factory.newDocumentBuilder(); 

	// 初始化Document
	Document document = builder.newDocument();
	
	Element root = document.createElement("data");
	document.appendChild(root);

	root.appendChild(getTaskBaseAttribute(data, "id", das, document));
	
	Node idNode = document.createElement("field");
	XMLUtil.setAttribute(idNode, "name", "ID");
	XMLUtil.setAttribute(idNode, "value", data.getId().toString());
	root.appendChild(idNode);
	
	root.appendChild(getTaskBaseAttribute(data, "title", das, document));
	root.appendChild(getTaskBaseAttribute(data, "description", das, document));
	root.appendChild(getTaskBaseAttribute(data, "status_id", das, document));
	root.appendChild(getTaskBaseAttribute(data, "create_user", das, document));
	root.appendChild(getTaskBaseAttribute(data, "create_time", das, document));
	root.appendChild(getTaskBaseAttribute(data, "assign_user", das, document));
	root.appendChild(getTaskBaseAttribute(data, "last_modify_time", das, document));
	root.appendChild(getTaskBaseAttribute(data, "node_id", das, document));
	root.appendChild(getTaskBaseAttribute(data, "action_id", das, document));
	root.appendChild(getTaskBaseAttribute(data, "action_user", das, document));
	root.appendChild(getTaskBaseAttribute(data, "action_comment", das, document));
// 	root.appendChild(getTaskBaseAttribute(data, "action_index", das, document));

	Map<String, String> fieldMap = createMapByTaskFieldValue(data, das);
	for (Map.Entry<String, String> entry : fieldMap.entrySet())
	{
		Element element = document.createElement("field");
		XMLUtil.setAttribute(element, "name", entry.getKey());
		XMLUtil.setAttribute(element, "value", entry.getValue());

		root.appendChild(element);
	}
	
	return createItemTableRow(root.getChildNodes(), false, hasAttachment, index, 0, null);
}

String createItemTableRow(NodeList taskNodeList, boolean isNewTask, boolean hasAttachment, int index, int indent, UUID filterId)
{
	StringBuilder buffer = new StringBuilder();
	String id = null;
	String templateId = null;
	String status = null;
	String nodeId = null;

	if (indent > 0)
	{
		buffer.append("<td class='ttbg' nowrap indent='true'>");
		for (int i = 0; i < indent; i ++)
			buffer.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		buffer.append("</td>");
	}

	//组装isNew
	buffer.append("<td name=\"index\" align=\"left\" nowrap><image src=\"" + ConfigUtil.getCynthiaWebRoot() + "images/").append(isNewTask ? "new" : "old").append(".bmp\">").append(hasAttachment ? "&nbsp;<image src=\"" + ConfigUtil.getCynthiaWebRoot() + "images/file.bmp\">" : "").append("&nbsp;</td>");

	//组装序号
	buffer.append("<td name=\"index\" align=\"left\" nowrap>").append(index).append("</td>");

	// 组装具体内容
	for (int i = 0; i < taskNodeList.getLength(); i ++)
	{
		Node node = taskNodeList.item(i);
		
		if (!node.getNodeName().equals("field"))
			continue;

		String value = node.getAttributes().getNamedItem("value").getTextContent();
		String name = node.getAttributes().getNamedItem("name").getTextContent();
		
		if(name.equals("编号"))
		{
			id = value;
			continue;
		}
		
		if(name.equals("节点"))
		{
			nodeId = value;
			continue;
		}
		
		if(name.equals("表单"))
		{
			templateId = value;
			continue;
		}
		
		if (value == null || value.length() == 0)
			value = "-";
		
		String title = value;	
		
		value = XMLUtil.toSafeXMLString(value);
		
		String alignLeft	= " align='left' ";
		
		if ("标题".equals(name))
			value = "<a href='" + ConfigUtil.getCynthiaWebRoot() + "taskManagement.html?operation=read&taskid=" + URLUtil.toSafeURLString(id) + (filterId == null ? "" : ("&filterId="+URLUtil.toSafeURLString(filterId.toString())))+"' onclick='selectTask(this.parentNode, true, true, event)' target='_blank'>" + value + "</a>";
		
		if(name.equals("状态"))
			status = value;
		
		buffer.append("<td "+alignLeft+" name=\"").append(XMLUtil.toSafeXMLString(name)).append("\" title=\""+XMLUtil.toSafeXMLString(title)+"\" nowrap>");
		buffer.append(value);
		buffer.append("</td>");
	}

	// 组装结束标志
	buffer.append("</tr>");

	// 插入头内容
	buffer.insert(0, "<tr align='center' onclick='selectTask(this, false, false, event)' taskid='" + id + "' status='" + status + "' templateId='" + templateId + "' nodeId='" + nodeId + "' id='" + id + "'>");
	
	return buffer.toString();
}

void createItemElement(Document document, Element taskElement, Element itemElement, boolean isNewTask, boolean hasAttachment, String pre, String tail, int index, int indent, UUID filterId, boolean needItemHtml, boolean isPlain) throws Exception
{
	Element element = null;
	
	String id = null;
	String title = null;
	
	NodeList nodeList = taskElement.getChildNodes();
	for (int i = 0; i < nodeList.getLength(); i ++)
	{
		Node node = nodeList.item(i);
		
		if (!node.getNodeName().equals("field"))
			continue;

		String value = node.getAttributes().getNamedItem("value").getTextContent();
		String name = node.getAttributes().getNamedItem("name").getTextContent();

		if (name.equals("标题"))
			title = value;
		else if (name.equals("编号"))
			id = value;
	}

	element = document.createElement( "title" );
	element.setTextContent(isPlain ? title : XMLUtil.toSafeXMLString(title));
	itemElement.appendChild( element );
	
	element = document.createElement( "description" );
	element.setTextContent(isPlain ? createItemDescription(nodeList) : XMLUtil.toSafeXMLString(createItemDescription(nodeList)));
	itemElement.appendChild( element );

	if (needItemHtml)
	{
		element = document.createElement( "tablerow" );
		element.setTextContent(isPlain ? pre + createItemTableRow(nodeList, isNewTask, hasAttachment, index, indent, filterId) + tail : XMLUtil.toSafeXMLString(pre + createItemTableRow(nodeList, isNewTask, hasAttachment, index, indent, filterId) + tail));
		itemElement.appendChild( element );
	}

	element = document.createElement( "link" );
	element.setTextContent(ConfigUtil.getCynthiaWebRoot() + "task/taskManagement.jsp?operation=read&taskid=" + URLUtil.toSafeURLString(id));
	itemElement.appendChild( element );
}

Element createItemElementCompact(Document document, Element taskElement, boolean isNewTask, boolean hasAttachment, int index, boolean isPlain, Long id, int total, Map<String, String> userAliasMap, DataAccessSession das) throws Exception
{
	Element itemElement = document.createElement( "data" );
	NodeList fieldList = taskElement.getChildNodes();
	
	Element element = null;

	element = document.createElement("uuid");
	element.setTextContent(isPlain ? XMLUtil.getSingleNodeTextContent(taskElement, "uuid") : XMLUtil.toSafeXMLString(XMLUtil.getSingleNodeTextContent(taskElement, "uuid")));
	itemElement.appendChild(element);
	
	element = document.createElement("id");
	element.setAttribute("uuid", "编号");
	element.setTextContent(id.toString());
	itemElement.appendChild(element);

	element = document.createElement("isNew");
	element.setTextContent(Boolean.toString(isNewTask));
	itemElement.appendChild(element);

	element = document.createElement("hasAttachment");
	element.setTextContent(Boolean.toString(hasAttachment));
	itemElement.appendChild(element);
	
	element = document.createElement("total");
	element.setTextContent(Integer.toString(total));
	itemElement.appendChild(element);
	
	for (int i = 0; i < fieldList.getLength(); i ++)
	{
		Node node = fieldList.item(i);
		
		if (!node.getNodeName().equals("field") || XMLUtil.getAttribute(node, "uuid") == null)
			continue;

		String fieldId = XMLUtil.getAttribute(node, "uuid");
		if(fieldId==null||"".equals(fieldId.trim()))
			continue;
		if(CommonUtil.isPosNum(fieldId))
			element = document.createElement("FIEL-" + fieldId);
		else
		{
			element = document.createElement(fieldId);			
		}
		
		element.setAttribute("uuid", XMLUtil.getAttribute(node, "name"));
		
		String fieldValue = XMLUtil.getAttribute(node, "value");
		if(!fieldValue.equals("") && (fieldId.equals("create_user") || fieldId.equals("assign_user"))){
			if(!userAliasMap.containsKey(fieldValue)){
				userAliasMap.put(fieldValue, CynthiaUtil.getUserAlias(fieldValue));
			}
			
			if(userAliasMap.get(fieldValue) != null){
				fieldValue = userAliasMap.get(fieldValue);
			}
		}
		
		if(isPlain)
			fieldValue = fieldValue.replaceAll("\\r\\n", " ").replaceAll("\\n", " ");

		element.setTextContent(isPlain ? fieldValue : XMLUtil.toSafeXMLString(fieldValue));

		itemElement.appendChild(element);
	}

	return itemElement;
}

void createTaskElement(Document document, Data task, Element taskElement, String[] fieldsName, DataAccessSession das, boolean isPlain) throws Exception
{
	Element element = null;
	
	element = document.createElement("uuid");
	element.setTextContent(isPlain ? task.getId().toString() : XMLUtil.toSafeXMLString(task.getId().toString()));
	taskElement.appendChild( element );

	Element idElement = document.createElement("field");
	idElement.setAttribute("name", "编号");
	idElement.setAttribute("value", task.getId().toString());
	taskElement.appendChild( idElement );
	
	Element IDElement = document.createElement("field");
	IDElement.setAttribute("name", "ID");
	IDElement.setAttribute("value", task.getId().toString());
	taskElement.appendChild( IDElement );
	
	Template template = das.queryTemplate(task.getTemplateId());
	if(template != null)
	{
		Element templateElement = document.createElement("field");
		templateElement.setAttribute("name", "表单");
		templateElement.setAttribute("value", template.getFlowId().toString());
		taskElement.appendChild( templateElement );
	}
	
	Map<String, String> map = createMapByTaskFieldValue(task, das);

	boolean hasPriority = false;
	
	for (String fieldName : fieldsName)
	{
		if(fieldName.equals("修改优先级"))
			hasPriority = true;
		
		element = document.createElement("field");
		element.setAttribute("name", fieldName);

		Field field = template.getField(fieldName);
		String fieldId = (field == null ? ConfigUtil.baseFieldIdMap.get(fieldName) : field.getId().toString());
		
		element.setAttribute("uuid", fieldId);
		
		String value = getTaskBaseAttribute(task, fieldName, das);
		if (value == null)
			value = map.get(fieldName);
		
		if(value != null)
		{
			element.setAttribute("value", value);
			taskElement.appendChild( element );
		}
		else
		{
			element.setAttribute("value", " - ");
			taskElement.appendChild( element );
		}
	}
	
	if(!hasPriority)
	{
		element = document.createElement("field");
		element.setAttribute("name", "修改优先级");
		element.setAttribute("uuid", "priority");
		
		String value = map.get("修改优先级");
		if(value == null)
			element.setAttribute("value", " - ");
		else
			element.setAttribute("value", value);
		
		taskElement.appendChild(element);
	}
	
}

Element getTaskBaseAttribute(Data task, String fieldName, DataAccessSession das, Document document)
{
	Element element = null;
	
	String name = null;
	String value = null;

	if (!fieldName.equals("id"))
	{
		name = ConfigUtil.baseFieldNameMap.get(fieldName);
		value = getTaskBaseAttribute(task, fieldName, das);
	}
	else
	{
		name = "编号";
		value = task.getId().toString();
	}

	element = document.createElement("field");
	XMLUtil.setAttribute(element, "name", name);
	XMLUtil.setAttribute(element, "value", value);
	
	return element;
}

String getTaskBaseAttribute(Data task, String fieldName, DataAccessSession das)
{
	Template template = das.queryTemplate(task.getTemplateId());
	if( "title".equals( fieldName ) || "标题".equals( fieldName ) )
		return task.getTitle();
	else if( "status_id".equals( fieldName ) || "状态".equals( fieldName ) )
		return DataManager.getInstance().getDataStatus(task, das);
	else if( "create_user".equals( fieldName ) || "创建人".equals( fieldName ) )
		return task.getCreateUsername();
	else if( "create_time".equals( fieldName ) || "创建时间".equals( fieldName ) )
	{
		String createTime = task.getCreateTime().toString();
		if(createTime.indexOf(".") > 0)
			createTime = createTime.split("\\.")[0];
		
		return createTime;
	}
	else if( "description".equals( fieldName ) || "描述".equals( fieldName ) )
		return task.getDescription();
	else if( "assign_user".equals( fieldName ) || "指派人".equals( fieldName ) )
		return task.getAssignUsername();
	else if( "last_modify_time".equals( fieldName ) || "修改时间".equals( fieldName ) )
	{
		String lastModifyTime = task.getLastModifyTime().toString();
		if(lastModifyTime.indexOf(".") > 0)
			lastModifyTime = lastModifyTime.split("\\.")[0];
		
		return lastModifyTime;
	}
	else if( "node_id".equals( fieldName ) || "项目".equals( fieldName ) )
	{
		if(template != null)
			return template.getName();
		
		return null;
	}
	else if("action_id".equals( fieldName ) || "执行动作".equals( fieldName ))
	{
		if(task.getActionId() == null)
			return "编辑";
		
		Action action = das.queryAction(task.getActionId(),template.getFlowId());
		if(action != null)
			return action.getName();
		
		return null;
	}
	else if("action_user".equals( fieldName ) || "执行人".equals( fieldName ))
		return task.getActionUser();
	else if("action_comment".equals( fieldName ) || "执行描述".equals( fieldName ))
		return task.getActionComment();
	else if("action_index".equals( fieldName ) || "执行序号".equals( fieldName ))
		return Integer.toString(task.getActionIndex());
	
	return null;
}

Map<String, String> createMapByTaskFieldValue(Data task, DataAccessSession das)
{
	Map<String, String> dataMap = new HashMap<String, String>();
	
	Template template = das.queryTemplate(task.getTemplateId());
	if(template == null)
		return dataMap;
	
	for(Field field : template.getFields())
	{
		if(field.getType().equals(Type.t_selection))
		{
			if(field.getDataType().equals(DataType.dt_single))
			{
				UUID optionId = task.getSingleSelection(field.getId());
				if(optionId != null)
				{
					Option option = field.getOption(optionId);
					if(option != null)
						dataMap.put(field.getName(), option.getName());
				}
			}
			else
			{
				UUID[] optionIdArray = task.getMultiSelection(field.getId());
				if(optionIdArray != null && optionIdArray.length > 0)
				{
					StringBuffer valueStrb = new StringBuffer();
					for(UUID optionId : optionIdArray)
					{
						Option option = field.getOption(optionId);
						if(option != null)
						{
							if(valueStrb.length() > 0)
								valueStrb.append(",");
							
							valueStrb.append("[").append(option.getName()).append("]");
						}
					}
					
					if(valueStrb.length() > 0)
						dataMap.put(field.getName(), valueStrb.toString());
				}
			}
		}
		else if(field.getType().equals(Type.t_reference))
		{
			if(field.getDataType().equals(DataType.dt_single))
			{
				UUID dataId = task.getSingleReference(field.getId());
				if(dataId != null)
				{
					Data data = das.queryData(dataId);
					if(data != null)
						dataMap.put(field.getName(), data.getTitle());
				}
			}
			else
			{
				UUID[] dataIdArray = task.getMultiReference(field.getId());
				if(dataIdArray != null && dataIdArray.length > 0)
				{
					StringBuffer valueStrb = new StringBuffer();
					for(UUID dataId : dataIdArray)
					{
						Data data = das.queryData(dataId);
						if(data != null)
						{
							if(valueStrb.length() > 0)
								valueStrb.append(",");
							
							valueStrb.append("[").append(data.getTitle()).append("]");
						}
					}
					
					if(valueStrb.length() > 0)
						dataMap.put(field.getName(), valueStrb.toString());
				}
			}
		}
		else if(field.getType().equals(Type.t_attachment))
		{
			UUID[] attachmentIdArray = task.getAttachments(field.getId());
			if(attachmentIdArray != null && attachmentIdArray.length > 0)
			{
				StringBuffer valueStrb = new StringBuffer();
				
				Attachment[] attachmentArray = das.queryAttachments(attachmentIdArray, false);
				for(Attachment attachment : attachmentArray)
				{
					if(valueStrb.length() > 0)
						valueStrb.append(",");
					
					valueStrb.append("[").append(attachment.getName()).append("]");
				}
				
				if(valueStrb.length() > 0)
					dataMap.put(field.getName(), valueStrb.toString());
			}
		}
		else if(field.getType().equals(Type.t_input))
		{
			if(field.getDataType().equals(DataType.dt_integer))
			{
				Integer value = task.getInteger(field.getId());
				if(value == null){
					value = Integer.MIN_VALUE;
				}
				
				dataMap.put(field.getName(), value.toString());
			}
			else if(field.getDataType().equals(DataType.dt_long))
			{
				Long value = task.getLong(field.getId());
				if(value == null){
					value = Long.MIN_VALUE;
				}
				
				dataMap.put(field.getName(), value.toString());
			}
			else if(field.getDataType().equals(DataType.dt_float))
			{
				Float value = task.getFloat(field.getId());
				if(value == null){
					value = Float.MIN_VALUE;
				}
				
				dataMap.put(field.getName(), value.toString());
			}
			else if(field.getDataType().equals(DataType.dt_double))
			{
				Double value = task.getDouble(field.getId());
				if(value == null){
					value = Double.MIN_VALUE;
				}
				
				dataMap.put(field.getName(), value.toString());
			}
			else if(field.getDataType().equals(DataType.dt_string) || field.getDataType().equals(DataType.dt_text))
			{
				String value = task.getString(field.getId());
				if(value != null)
					dataMap.put(field.getName(), value);
			}
			else if(field.getDataType().equals(DataType.dt_timestamp))
			{
				Date value = task.getDate(field.getId());
				if(value != null)
					dataMap.put(field.getName(), value.toString());
			}
		}
	}
	
	return dataMap;
}

String[] createFieldsNameByFieldList(List<Node> fieldList, DataAccessSession das, Template template)
{
	Set<String> set = new LinkedHashSet<String>();
	
	for (Node fieldNode : fieldList)
	{
		String fieldIdStr = XMLUtil.getAttribute(fieldNode, "id");
		if(CommonUtil.isPosNum(fieldIdStr))
		{
			UUID fieldId = DataAccessFactory.getInstance().createUUID(fieldIdStr);
			Field field = null;
			if(template != null){
				field = das.queryField(fieldId , template.getId());
			}else{
				field = das.queryField(fieldId);
			}
			
			if(field != null)
				set.add(field.getName());
		}
		else
		{
			String fieldName = convertFieldName(fieldIdStr);
			if(fieldName != null)
				set.add(fieldName);
		}
	}
	
	return set.toArray(new String[0]);
}

String createErrorResultXml(String title, String description, String url)
{
	StringBuilder buffer = new StringBuilder();

	buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>").append("\r\n");
	buffer.append("<channel>").append("\r\n");
	buffer.append("	<title>Cynthia筛选器</title>").append("\r\n");
	buffer.append("	<description>Cynthia筛选器</description>").append("\r\n");
	buffer.append("	<link>" + ConfigUtil.getCynthiaWebRoot() + "search/filterManagement.jsp</link>").append("\r\n");
	buffer.append("	<language>zh-cn</language>").append("\r\n");
	buffer.append("	<lastBuildDate>" + new java.util.Date(System.currentTimeMillis()) + "</lastBuildDate>").append("\r\n");
	buffer.append("	<template>").append("\r\n");
	buffer.append("		<item>").append("\r\n");
	buffer.append("			<title>" + title + "</title>").append("\r\n");
	buffer.append("			<link>" + url + "</link>").append("\r\n");
	buffer.append("			<guid>" + url + "</guid>").append("\r\n");
	buffer.append("			<description>" + description + "</description>").append("\r\n");
	buffer.append("		</item>").append("\r\n");
	buffer.append("	</template>").append("\r\n");
	buffer.append("</channel>").append("\r\n");
	
	return buffer.toString();
}

String createParameterString(Map parameterMap, String[] exceptKey)
{
	StringBuilder buffer = new StringBuilder();
	Set<String> set = new HashSet<String>(Arrays.asList(exceptKey));

	for (Object key : parameterMap.keySet())
	{
		if (set.contains(key))
			continue;

		String[] values = (String[])parameterMap.get(key);
		for (String value : values)
			buffer.append("&").append(key).append("=").append(URLUtil.toSafeURLString(value));
	}

	return buffer.toString();
}

int getTotalPage(int currentPageNumber, int countPerPage, int taskCount, int totalAccount)
{
	return (totalAccount % countPerPage == 0 ? totalAccount / countPerPage : totalAccount / countPerPage + 1);
}


void initFilterEnv(Filter filter, long kid, String username, javax.servlet.http.HttpServletRequest request, UUID templateId, DataAccessSession das) throws Exception
{
	Document document = XMLUtil.string2Document( filter.getXml(), "UTF-8" );

	initFilterEnv(filter, kid, username, request, templateId, document, das);
}

void initFilterEnv(Filter filter, long kid, String username, javax.servlet.http.HttpServletRequest request, UUID templateId, Document document, DataAccessSession das) throws Exception
{
	try
	{
		// 处理其他环境变量
		List<Node> currentUserList = XMLUtil.getNodes( document, "/query/env/current_user");
		List<Node> envNodeList = XMLUtil.getNodes( document, "/query/env");
	
		Node envNode = null;
		if( envNodeList == null || envNodeList.isEmpty() )
		{
			envNode = document.createElement( "env" );
			XMLUtil.getNodes( document, "/query" ).get( 0 ).appendChild( envNode );
		}
		else
		{
			envNode = envNodeList.get( 0 );
		}
		
		// 处理current_user环境变量
		if( currentUserList != null && !currentUserList.isEmpty() )
		{
			currentUserList.get( 0 ).setTextContent( username );
		}
		else
		{
			Element current_userNode = document.createElement( "current_user" );
			current_userNode.setTextContent( username );
			envNode.appendChild( current_userNode );
		}
		
		//	 拼进当前执行人的相关人员列表
		UserInfo userInfo = das.queryUserInfoByUserName(username);
		if(userInfo != null){
			Element userElement = document.createElement( "user_list" );
			userElement.setTextContent( username );
			envNode.appendChild( userElement );
		}
		
		// 处理current_template_type环境变量
		if( request.getParameter( "type" ) != null ||request.getAttribute("type")!=null)
		{		
			List<Node> typeNodeList = XMLUtil.getNodes( document, "/query/env/current_template_type");
			if( typeNodeList != null && !typeNodeList.isEmpty() )
			{
				//typeNodeList.get( 0 ).setTextContent( request.getParameter( "type" ) );
				if(request.getParameter("type")!=null)
				{
					typeNodeList.get( 0 ).setTextContent( request.getParameter( "type" ) );
				}else
				{
					typeNodeList.get( 0 ).setTextContent((String)request.getAttribute( "type" ) );
				}
			}
			else
			{
				Element current_userNode = document.createElement( "current_template_type" );
				//current_userNode.setTextContent( request.getParameter( "type" ) );
				if(request.getParameter("type")!=null)
				{
					current_userNode.setTextContent( request.getParameter( "type" ) );
				}else
				{
					current_userNode.setTextContent( (String)request.getAttribute( "type" ) );
				}
				envNode.appendChild( current_userNode );
			}
		}
		
		if (templateId != null)
		{
			Node queryNode = XMLUtil.getSingleNode(document, "query");
			List<Node> templateNodes = XMLUtil.getNodes(document, "query/template");
			for (Node node : templateNodes)
			{
				String templateIdStr = XMLUtil.getAttribute(node, "id");
				if (templateIdStr != null && templateIdStr.equals(templateId.toString()))
					continue;
	
				queryNode.removeChild(node);
			}
		}
		
		String newXml = XMLUtil.document2String( document, "UTF-8" );

		// 将处理完环境变量的xml放回filter bean
		filter.setXml( newXml );
	}
	catch(Throwable t)
	{
		t.printStackTrace();
	}
}

// 构建分组，并根据情况更新分组依据值，后面数据列的数量应该是显示列加上序号列
String createPrefix( String[] prefixTitle, String[] prefixValue, Data task, int dataLength, DataAccessSession das )
{
	String prefix = "";
	
	if( prefixTitle == null || prefixTitle.length == 0 || task == null )
	{
		return prefix;
	}
	Map<String,String> dataMap = createTaskDataMap( task, das );
	if( dataMap == null || dataMap.isEmpty() )
	{
		return prefix;
	}
		
	for( int ipre = 0; ipre < prefixTitle.length; ipre++ )
	{
		String taskData = dataMap.get( prefixTitle[ipre] );
		if( taskData == null )
		{
			taskData = "-";
			dataMap.put( prefixTitle[ipre], taskData );
		}

		// 如果相等，则不分组
		if( taskData == prefixValue[ipre] || ( prefixValue[ipre] != null && prefixValue[ipre].equals( taskData ) ) )
			continue;
		
		for( int j = ipre; j < prefixTitle.length; j++ )
		{
			prefixValue[j] = dataMap.get( prefixTitle[j] );
			if( prefixValue[j] == null )
			{
				prefixValue[j] = "-";
				dataMap.put( prefixTitle[j], "-" );
			}
		}
		
		// 有一个分组字段不等，需要分组
		StringBuffer str = new StringBuffer( 256 );
		
		str.append( "<tr class='ttbg' istitle='true'>" );
		str.append( "<td class='ttbg' colspan='" + ( prefixTitle.length + dataLength + 1 ) + "'>" );
		
		for( int i = ipre; i < prefixTitle.length; i++ )
		{
			taskData = dataMap.get( prefixTitle[i] );
			
			if( i != ipre )
				str.append( "<br>" );

			for (int k = 0; k < i; k ++)
				str.append( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" );
			
			str.append( taskData );
		}
		str.append( "</td></tr>" );
		
		/*完全错位
		for( int i = ipre; i < prefixTitle.length; i++ )
		{		
			str.append( "<tr align='center' class='ttbg' istitle='true'>" );
			
			taskData = dataMap.get( prefixTitle[i] );
			
			for( int j = 0; j < i; j++ )
				str.append( "<td></td>" );
			
			str.append( "<td>" + taskData + "</td>" );
			
			for( int j = i + 1; j < prefixTitle.length; j++ )
			{
				str.append( "<td></td>" );
			}
			
			str.append( "<td colspan='" + ( dataLength + 1 ) + "'></td>" );
			
			str.append( "</tr>" );
		}
		*/

		return str.toString();
	}

	return prefix;
}

public Map<String, String> createTaskDataMap( Data task, DataAccessSession das )
{		
	Template template = das.queryTemplate(task.getTemplateId());
	if(template == null)
		return null;
	
	Flow flow = das.queryFlow(template.getFlowId());
	if(flow == null)
		return null;
	
	Stat stat = flow.getStat(task.getStatusId());
	if(stat == null)
		return null;
	
	String actionName = "编辑";
	if(task.getActionId() != null)
	{
		Action action = flow.getAction(task.getActionId());
		if(action == null)
			return null;
		
		actionName = action.getName();
	}
	
	HashMap<String, String> dataMap = new HashMap<String,String>();
	dataMap.put( "id", task.getId().toString() );
	dataMap.put( "title", task.getTitle() );
	dataMap.put( "description", task.getDescription() );
	dataMap.put( "assign_user", task.getAssignUsername() );
	dataMap.put( "create_user", task.getCreateUsername() );
	
	String createTime = task.getCreateTime().toString();
	if(createTime.indexOf(".") > 0)
		createTime = createTime.split("\\.")[0];
	dataMap.put( "create_time", createTime);
	
	String lastModifyTime = task.getLastModifyTime().toString();
	if(lastModifyTime.indexOf(".") > 0)
		lastModifyTime = lastModifyTime.split("\\.")[0];
	dataMap.put( "last_modify_time", lastModifyTime);
	
	dataMap.put( "status_id", stat.getName());
	dataMap.put("action_id", actionName);
	dataMap.put("action_user", task.getActionUser());
	dataMap.put("action_comment", task.getActionComment());
	dataMap.put("action_index", Integer.toString(task.getActionIndex()));
	
	if(template.getFields() == null)
		return dataMap;
	
	for(Field field : template.getFields())
	{
		if(field.getType().equals(Type.t_selection))
		{
			if(field.getDataType().equals(DataType.dt_single))
			{
				UUID optionId = task.getSingleSelection(field.getId());
				if(optionId != null)
				{
					Option option = field.getOption(optionId);
					if(option != null)
						dataMap.put(field.getId().toString(), option.getName());
				}
			}
			else
			{
				UUID[] optionIdArray = task.getMultiSelection(field.getId());
				if(optionIdArray != null && optionIdArray.length > 0)
				{
					StringBuffer valueStrb = new StringBuffer();
					for(UUID optionId : optionIdArray)
					{
						Option option = field.getOption(optionId);
						if(option != null)
						{
							if(valueStrb.length() > 0)
								valueStrb.append(",");
							
							valueStrb.append("[").append(option.getName()).append("]");
						}
					}
					
					if(valueStrb.length() > 0)
						dataMap.put(field.getId().toString(), valueStrb.toString());
				}
			}
		}
		else if(field.getType().equals(Type.t_reference))
		{
			if(field.getDataType().equals(DataType.dt_single))
			{
				UUID dataId = task.getSingleReference(field.getId());
				if(dataId != null)
				{
					Data data = das.queryData(dataId);
					if(data != null)
						dataMap.put(field.getId().toString(), data.getTitle());
				}
			}
			else
			{
				UUID[] dataIdArray = task.getMultiReference(field.getId());
				if(dataIdArray != null && dataIdArray.length > 0)
				{
					StringBuffer valueStrb = new StringBuffer();
					for(UUID dataId : dataIdArray)
					{
						Data data = das.queryData(dataId);
						if(data != null)
						{
							if(valueStrb.length() > 0)
								valueStrb.append(",");
							
							valueStrb.append("[").append(data.getTitle()).append("]");
						}
					}
					
					if(valueStrb.length() > 0)
						dataMap.put(field.getId().toString(), valueStrb.toString());
				}
			}
		}
		else if(field.getType().equals(Type.t_attachment))
		{
			UUID[] attachmentIdArray = task.getAttachments(field.getId());
			if(attachmentIdArray != null && attachmentIdArray.length > 0)
			{
				StringBuffer valueStrb = new StringBuffer();
				
				Attachment[] attachmentArray = das.queryAttachments(attachmentIdArray, false);
				for(Attachment attachment : attachmentArray)
				{
					if(valueStrb.length() > 0)
						valueStrb.append(",");
					
					valueStrb.append("[").append(attachment.getName()).append("]");
				}
				
				if(valueStrb.length() > 0)
					dataMap.put(field.getId().toString(), valueStrb.toString());
			}
		}
		else if(field.getType().equals(Type.t_input))
		{
			if(field.getDataType().equals(DataType.dt_integer))
			{
				Integer value = task.getInteger(field.getId());
				if(value != null)
					dataMap.put(field.getId().toString(), value.toString());
			}
			else if(field.getDataType().equals(DataType.dt_long))
			{
				Long value = task.getLong(field.getId());
				if(value != null)
					dataMap.put(field.getId().toString(), value.toString());
			}
			else if(field.getDataType().equals(DataType.dt_float))
			{
				Float value = task.getFloat(field.getId());
				if(value != null)
					dataMap.put(field.getId().toString(), value.toString());
			}
			else if(field.getDataType().equals(DataType.dt_double))
			{
				Double value = task.getDouble(field.getId());
				if(value != null)
					dataMap.put(field.getId().toString(), value.toString());
			}
			else if(field.getDataType().equals(DataType.dt_string) || field.getDataType().equals(DataType.dt_text))
			{
				String value = task.getString(field.getId());
				if(value != null)
					dataMap.put(field.getId().toString(), value);
			}
			else if(field.getDataType().equals(DataType.dt_timestamp))
			{
				Date value = task.getDate(field.getId());
				if(value != null)
					dataMap.put(field.getId().toString(), value.toString());
			}
		}
	}
	
	return dataMap;
}%>
