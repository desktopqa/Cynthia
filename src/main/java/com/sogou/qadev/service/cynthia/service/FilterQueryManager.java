package com.sogou.qadev.service.cynthia.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sogou.qadev.cache.impl.FieldNameCache;
import com.sogou.qadev.cache.impl.TemplateCache;
import com.sogou.qadev.service.cynthia.bean.Action;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Field.DataType;
import com.sogou.qadev.service.cynthia.bean.Field.Type;
import com.sogou.qadev.service.cynthia.bean.Filter;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Option;
import com.sogou.qadev.service.cynthia.bean.QueryCondition;
import com.sogou.qadev.service.cynthia.bean.SegmentTagBase;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.TemplateTypeSegmentTag;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.bean.UserInfo;
import com.sogou.qadev.service.cynthia.dao.AttachmentAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.util.CommonUtil;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;
import com.sogou.qadev.service.cynthia.util.Date;
import com.sogou.qadev.service.cynthia.util.XMLUtil;


public class FilterQueryManager {
	
	public enum ExportType{
		xml,json,excel,html;
	}
	
	private static List<Node> convertNodeList(NodeList nodeList)
	{
		if(nodeList == null){
			return new ArrayList<Node>();
		}
		List<Node> list = new ArrayList<Node>(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i ++)
		{
			if (nodeList.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
	
			list.add(nodeList.item(i));
		}
		
		return list;
	}
	
	private static String[] createFieldsNameByFieldList(List<Node> fieldList, DataAccessSession das , Template template)
	{
		Set<String> set = new LinkedHashSet<String>();
		
		for (Node fieldNode : fieldList)
		{
			String fieldIdStr = XMLUtil.getAttribute(fieldNode, "id");
			if(CommonUtil.isPosNum(fieldIdStr))
			{
				UUID fieldId = DataAccessFactory.getInstance().createUUID(fieldIdStr);
				Field field = null;
				if (template == null) {
					field = TemplateCache.getInstance().queryField(fieldId);
				}else {
					field = template.getField(fieldId);
				}
				if(field != null)
					set.add(field.getName());
			}else{
				String fieldName = ConfigUtil.baseFieldNameMap.get(fieldIdStr);
				if(fieldName != null)
					set.add(fieldName);
			}
		}
		
		return set.toArray(new String[0]);
	}
	
	/**
	 * @function：获取过滤器显示字段
	 * @modifyTime：2013-10-12 上午10:29:06
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param filterXml
	 * @return
	 */
	public static Map<String,String> getDisplayFieldAndWidth(String filterXml ,DataAccessSession das) {
		Map<String, String> displayFieldMap = new LinkedHashMap<String, String>();
		
		Document filterDoc = null;
		try {
			filterDoc = XMLUtil.string2Document(filterXml,"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(filterDoc == null)
			return displayFieldMap;

		Template template = null;
		
		try {
			Node indentNode = null;
			
			Node node = XMLUtil.getSingleNode(filterDoc, "query/template");
			if(node == null){
				node = XMLUtil.getSingleNode(filterDoc, "query/templateType");
				
			}else {
				template = das.queryTemplate(DataAccessFactory.getInstance().createUUID(XMLUtil.getAttribute(node, "id")));
			}
			
			List<Node> allShowFieldList = XMLUtil.getNodes(node, "display/field");

			for (Node fieldNode : allShowFieldList)
			{
				String fieldIdStr = XMLUtil.getAttribute(fieldNode, "id");
				if(CommonUtil.isPosNum(fieldIdStr))
				{
					UUID fieldId = DataAccessFactory.getInstance().createUUID(fieldIdStr);
					Field field = null;
					if (template == null) {
						field = TemplateCache.getInstance().queryField(fieldId);
					}else {
						field = template.getField(fieldId);
					}
					if(field != null)
						displayFieldMap.put(field.getName(), XMLUtil.getAttribute(fieldNode, "width"));
				}else{
					String fieldName = ConfigUtil.baseFieldNameMap.get(fieldIdStr);
					if(fieldName != null)
						displayFieldMap.put(fieldName, XMLUtil.getAttribute(fieldNode, "width"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return displayFieldMap;
	}
	
	
	/**
	 * @function：获取过滤器显示字段
	 * @modifyTime：2013-10-12 上午10:29:06
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param filterXml
	 * @return
	 */
	public static String[] getDisplayFields(String filterXml ,DataAccessSession das){
		return getDisplayFieldAndWidth(filterXml, das).keySet().toArray(new String[0]);
	}

	// 传入templateType节点或者template节点，生成SegmentTagBase
	private static SegmentTagBase createSegmentTagBase(Element element, DataAccessSession das , Template template)
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
	
		// 填充显示字段和缩进字段名称集合
		displayFieldsNameSet.addAll(Arrays.asList(createFieldsNameByFieldList(convertNodeList(displayFields), das , template)));
		
		if (template == null) {
			displayFieldsNameSet.add("修改优先级");
			displayFieldsNameSet.add("描述");
		}else {
			displayFieldsNameSet.add("标题");
			displayFieldsNameSet.add("状态");
			displayFieldsNameSet.add("创建人");
			displayFieldsNameSet.add("指派人");
			displayFieldsNameSet.add("创建时间");
		}
		
		if (indentFields != null)
			indentFieldsNameSet.addAll(Arrays.asList(createFieldsNameByFieldList(convertNodeList(indentFields), das , template)));
	
		displayFieldsNameSet.addAll(indentFieldsNameSet);  //分组排序字段也显示出来
		
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
	
	private static TemplateTypeSegmentTag createTemplateTypeSegmentTag(Element element, DataAccessSession das)
	{
		TemplateTypeSegmentTag segmentTag = new TemplateTypeSegmentTag();
		String templateTypeId =element.getAttribute("id");
		if (templateTypeId != null)
			segmentTag.templateTypeId = DataAccessFactory.getInstance().createUUID(templateTypeId);
		segmentTag.templateTypeName = element.getAttribute("name" );
		segmentTag.fillBySegmentTagBase(createSegmentTagBase(element, das , null));
	
		return segmentTag;
	}
	
	public static  void initFilterEnv(Filter filter, long kid, String username,UUID templateId, DataAccessSession das) throws Exception
	{
		Document document = XMLUtil.string2Document( filter.getXml(), "UTF-8" );
		initFilterEnv(filter, kid, username, templateId, document, das);
	}

	private static void initFilterEnv(Filter filter, long kid, String username, UUID templateId, Document document, DataAccessSession das) throws Exception
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
			if (userInfo != null) {
				Element userElement = document.createElement( "user_list" );
				userElement.setTextContent( userInfo.getUserName() );
				envNode.appendChild( userElement );
			}
			
			List<Node> typeNodeList = XMLUtil.getNodes( document, "/query/env/current_template_type");
			if( typeNodeList != null && !typeNodeList.isEmpty() )
			{
				//typeNodeList.get( 0 ).setTextContent( templateTypeId );
			}
			else
			{
				Element current_userNode = document.createElement( "current_template_type" );
				//current_userNode.setTextContent( templateTypeId );
				envNode.appendChild( current_userNode );
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
	
	public static boolean isSysFilter(String filterIdStr)
	{
		if (filterIdStr != null &&
				(filterIdStr.equals("119695") || filterIdStr.equals("119891") ||filterIdStr.equals("119892")||filterIdStr.equals("119893"))) {
			return true;
		}
		return false;
	}	
	
	public static List<Data> queryDataList(DataAccessSession das, Filter filter,String userName,long keyId,String sort,String dir,int pagenum, int count , List<QueryCondition> queryConList)
	{
		List<Data> dataList = new ArrayList<Data>();
		try
		{
			if (isSysFilter(filter.getId().getValue())) {
				initFilterEnv(filter,keyId, userName, null, das);
			}
			if(sort != null && dir != null){
				dataList.addAll(Arrays.asList(das.getDataFilter().queryDatas(filter.getXml(), pagenum, count, sort, dir , queryConList)));
			}else{
				dataList.addAll(Arrays.asList(das.getDataFilter().queryDatas(filter.getXml(),pagenum,count,queryConList)));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return dataList;
	}
	
	public static String[] getTemplateTypeDisplayFieldsName(Document filterDocument,DataAccessSession das)
	{
		Element templateTypeNode = (Element)filterDocument.getElementsByTagName("templateType").item( 0 );
		TemplateTypeSegmentTag segmentTag = createTemplateTypeSegmentTag(templateTypeNode, das);
		return segmentTag.displayFieldsName;
	}
	
	/**
	 * @function：查询过滤器分组字段名
	 * @modifyTime：2013-10-23 下午3:41:27
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param filterXml
	 * @param das
	 * @return
	 */
	public static String getFilterIndentFieldName(String filterXml , DataAccessSession das){
		String indentFieldName = "";
		Document filterDoc = null;
		try {
			filterDoc = XMLUtil.string2Document(filterXml,"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(filterDoc == null)
			return indentFieldName;
		
		try {
			Node templateNode = XMLUtil.getSingleNode(filterDoc, "query/template");
			if(templateNode == null){
				Element templateTypeNode = (Element)filterDoc.getElementsByTagName("templateType").item( 0 );
				TemplateTypeSegmentTag segmentTag = createTemplateTypeSegmentTag(templateTypeNode, das);
				if (segmentTag.indentFieldsName != null && segmentTag.indentFieldsName.length > 0) {
					return segmentTag.indentFieldsName[0];
				}
			}else{
				UUID templateId = DataAccessFactory.getInstance().createUUID(XMLUtil.getAttribute(templateNode, "id").toString());
				Template template = TemplateCache.getInstance().get(templateId);
				
				Element templateElement = (Element)filterDoc.getElementsByTagName("template").item(0);
				SegmentTagBase templateSegment = createSegmentTagBase(templateElement,das ,template);
				if (templateSegment.indentFieldsName != null && templateSegment.indentFieldsName.length > 0) {
					return templateSegment.indentFieldsName[0];
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return indentFieldName;
		
		
	}
	
	/**
	 * @description:TODO
	 * @date:2014-5-6 下午12:10:22
	 * @version:v1.0
	 * @param xml
	 * @return
	 */
	public static String getOrderField(String xml)
	{
		Document doc = null;
		try
		{
			doc = XMLUtil.string2Document(xml,"UTF-8");
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		if(doc == null)
			return null;
		List<Node> orderNodes = XMLUtil.getNodes(doc,"query/template/order/field");
		if(orderNodes == null||orderNodes.size()==0)
		{
			return null;
		}
		return XMLUtil.getAttribute(orderNodes.get(0),"id");
	}
	
	public static String[] getTemplateDisplayFieldsName(Document filterDocument,DataAccessSession das , Template template)
	{
		Element templateNode = (Element)filterDocument.getElementsByTagName("template").item(0);
		SegmentTagBase templateSegment = createSegmentTagBase(templateNode,das ,template);
		return templateSegment.displayFieldsName;
	}
	
	/**
	 * @function：获取过滤器显示字段
	 * @modifyTime：2013-10-12 上午10:29:06
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param filterXml
	 * @return
	 */
	public static String[] getDisplayNamesFilter(String filterXml ,DataAccessSession das){
		String[] displayNames = null;
		Document filterDoc = null;
		try {
			filterDoc = XMLUtil.string2Document(filterXml,"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(filterDoc == null)
			return new String[0];
		
		try {
			Node templateNode = XMLUtil.getSingleNode(filterDoc, "query/template");
			if(templateNode == null){
				displayNames = FilterQueryManager.getTemplateTypeDisplayFieldsName(filterDoc,das);
			}else{
				UUID templateId = DataAccessFactory.getInstance().createUUID(XMLUtil.getAttribute(templateNode, "id").toString());
				Template template = TemplateCache.getInstance().get(templateId);
				displayNames = FilterQueryManager.getTemplateDisplayFieldsName(filterDoc,das , template);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new String[0];
		}
		return displayNames;
	}
	
	/**
	 * @description:return json string of query datas
	 * @date:2014-5-6 下午12:10:29
	 * @version:v1.0
	 * @param displayFieldsName
	 * @param dataList
	 * @param notNewTaskIdSet
	 * @param userClassifyDataMap
	 * @param das
	 * @param isSysFilter
	 * @return
	 */
	public static String assembleFilterDataJson(String[] displayFieldsName, List<Data> dataList ,Set<String> notNewTaskIdSet ,Map<String,String> userClassifyDataMap ,DataAccessSession das , boolean isSysFilter){
		StringBuffer result = new StringBuffer();
		Map<UUID, Set<UUID>> attachmentFieldIdMap = new HashMap<UUID, Set<UUID>>();
		Map<UUID, Template> templateMap = new HashMap<UUID, Template>();
		Map<UUID, Flow> flowMap = new HashMap<UUID, Flow>();
		Map<String, String> userAliasMap = new HashMap<String, String>();
		int i=0;
		for(Data task : dataList)
		{
			if (templateMap.get(task.getTemplateId()) == null) {
				Template template = das.queryTemplate(task.getTemplateId());
				if (template != null) {
					templateMap.put(task.getTemplateId(), template);
				}
			}
			
			Template template = templateMap.get(task.getTemplateId());
			if(template == null)
				continue;
			if (flowMap.get(template.getFlowId()) == null) {
				Flow flow = das.queryFlow(template.getFlowId());
				if (flow != null) {
					flowMap.put(flow.getId(), flow);
				}
			}
			
			Flow flow = flowMap.get(template.getFlowId());
			
			if(i>0)
				result.append(",");
			
			boolean isNewTask = !notNewTaskIdSet.contains(task.getId().getValue());		
			
			result.append("{");
			result.append("\"uuid\":\"").append(task.getId().getValue()).append("\"");
			result.append(",\"id\":\"").append(task.getId().getValue()).append("\"");
			result.append(",\"isNew\":\"").append(isNewTask).append("\"");
			result.append(",\"templateId\":\"").append(task.getTemplateId().getValue()).append("\"");
			
			if(userClassifyDataMap.containsKey(task.getId().getValue())){
				result.append(",\"selected\":\"true\"");	
				result.append(",\"selectedName\":\"").append(XMLUtil.toSafeXMLString(userClassifyDataMap.get(task.getId().getValue()))).append("\"");
			}else{
				result.append(",\"selected\":\"false\"");	
				result.append(",\"selected\":\"-\"");	
			}
			
			Map<String, String> displayMap = getShowFieldValueMap(displayFieldsName , task , template ,flow ,das ,  ExportType.json , userAliasMap , isSysFilter); 
			
			for (String fieldName : displayMap.keySet()) {
				result.append(",\"").append(fieldName).append("\":\"").append(displayMap.get(fieldName)).append("\"");
			}
			
			result.append("}");
			
			i++;
		}
		return result.toString();
	}

	/**
	 * @function：获取过滤器表单
	 * @modifyTime：2013-11-11 下午9:15:52
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param filter
	 * @return
	 */
	public static UUID getFilterTemplateId(Filter filter){
		if (isSysFilter(filter.getId().getValue())) {
			return null;
		}
		UUID templateId = null;
		Document filterDocument = null;
		try {
			filterDocument = XMLUtil.string2Document(filter.getXml(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (filterDocument != null) {
			Node templateNode = XMLUtil.getSingleNode(filterDocument, "query/template");
			templateId = DataAccessFactory.getInstance().createUUID(XMLUtil.getAttribute(templateNode, "id"));
		}
		return templateId;
	}

	/**
	 * @function：返回某字段页面显示值
	 * @modifyTime：2013-9-25 下午9:08:19
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param displayFieldName
	 * @param task
	 * @param template
	 * @return
	 */
	public static Map<String, String> getShowFieldValueMap(String[] displayFieldsName, Data task, Template template , Flow flow , DataAccessSession das ,ExportType exportType, Map<String, String> userAliasMap , boolean isSysFilter) {
		
		Map<String, String> fieldValueMap = new HashMap<String, String>();

		for (String fieldName : displayFieldsName)
		{
			String fieldShowName = "";
			String fieldShowValue = "";
			
			if( "id".equals( fieldName ) || "编号".equals( fieldName ) ){
				fieldShowName = "id";
				fieldShowValue =  XMLUtil.toSafeXMLString(task.getId().getValue());
			}
			else if( "title".equals( fieldName ) || "标题".equals( fieldName ) ){
				fieldShowName = "title";
				fieldShowValue =  XMLUtil.toSafeXMLString(task.getTitle());
			}
			else if( "status_id".equals( fieldName ) || "状态".equals( fieldName ) ){
				fieldShowName = "status_id";
				Stat stat = flow.getStat(task.getStatusId());
				if (stat != null) {
					fieldShowValue =  stat.getName();
				}
			}
			else if( "create_user".equals( fieldName ) || "创建人".equals( fieldName ) ){
				fieldShowName = "create_user";
				fieldShowValue = task.getCreateUsername();
				if(!userAliasMap.containsKey(fieldShowValue)){
					userAliasMap.put(fieldShowValue, CynthiaUtil.getUserAlias(fieldShowValue));
				}
				if(userAliasMap.get(fieldShowValue) != null){
					fieldShowValue = userAliasMap.get(fieldShowValue);
				}
			}
			else if( "create_time".equals( fieldName ) || "创建时间".equals( fieldName ) )
			{
				fieldShowName = "create_time";
				String createTime = task.getCreateTime().toString();
				if(createTime.indexOf(".") > 0)
					createTime = createTime.split("\\.")[0];
				
				fieldShowValue = createTime;
			}
			else if( "description".equals( fieldName ) || "描述".equals( fieldName ) ){
				fieldShowName = "description";
				fieldShowValue = task.getDescription();
			}
			else if( "assign_user".equals( fieldName ) || "指派人".equals( fieldName ) ){
				fieldShowName = "assign_user";
				fieldShowValue = task.getAssignUsername();
				if(!userAliasMap.containsKey(fieldShowValue)){
					userAliasMap.put(fieldShowValue, CynthiaUtil.getAssignUserAlias(fieldShowValue));
				}
				if(userAliasMap.get(fieldShowValue) != null){
					fieldShowValue = userAliasMap.get(fieldShowValue);
				}
			}
			else if( "last_modify_time".equals( fieldName ) || "修改时间".equals( fieldName ) )
			{
				fieldShowName = "last_modify_time";
				String lastModifyTime = task.getLastModifyTime().toString();
				if(lastModifyTime.indexOf(".") > 0)
					lastModifyTime = lastModifyTime.split("\\.")[0];
				
				fieldShowValue = lastModifyTime;
			}
			else if( "node_id".equals( fieldName ) || "项目".equals( fieldName ) )
			{
				fieldShowName = "node_id";
				if(template != null)
					fieldShowValue = template.getName();
			}
			else if("action_id".equals( fieldName ) || "执行动作".equals( fieldName ))
			{
				fieldShowName = "action_id";
				if(task.getActionId() == null)
					fieldShowValue = "编辑";
				
				Action action = flow.getAction(task.getActionId());
				if(action != null)
					fieldShowValue = action.getName();
			}
			else if("action_user".equals( fieldName ) || "执行人".equals( fieldName )){
				fieldShowName = "action_user";
				fieldShowValue = task.getActionUser();
			}
			else if("action_comment".equals( fieldName ) || "执行描述".equals( fieldName )){
				fieldShowName = "action_comment";
				fieldShowValue = task.getActionComment();
			}
			else if("action_index".equals( fieldName ) || "执行序号".equals( fieldName )){
				fieldShowName = "action_index";
				fieldShowValue = Integer.toString(task.getActionIndex());
			}else {
				if(isSysFilter && "修改优先级".equals( fieldName )){
					fieldShowName = "priority";
				}
				
				Field field = template.getField(fieldName);
				
				if (field != null) {
					if (fieldShowName == null || fieldShowName == "") {
						fieldShowName = "FIEL-" + field.getId().getValue();
					}
					
					if(field.getType().equals(Type.t_selection))
					{
						if(field.getDataType().equals(DataType.dt_single))
						{
							UUID optionId = task.getSingleSelection(field.getId());
							if(optionId != null)
							{
								Option option = field.getOption(optionId);
								if(option != null)
									fieldShowValue =  option.getName();
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
									fieldShowValue =  valueStrb.toString();
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
								String[] titles = new DataAccessSessionMySQL().queryFieldByIds(new UUID[]{dataId} , "title" , null);
								if (titles != null && titles.length > 0) {
									fieldShowValue =  titles[0];
								}
							}
						}
						else
						{
							UUID[] dataIdArray = task.getMultiReference(field.getId());
							StringBuffer valueStrb = new StringBuffer();
							if(dataIdArray != null && dataIdArray.length > 0){
								String[] titles = new DataAccessSessionMySQL().queryFieldByIds(dataIdArray, "title" , null);
								for(String title : titles){
									if(valueStrb.length() > 0)
											valueStrb.append(",");
									valueStrb.append("[").append(title).append("]");
								}
							}
							if(valueStrb.length() > 0)
								fieldShowValue =  valueStrb.toString();
						}
					}
					else if(field.getType().equals(Type.t_attachment))
					{
						UUID[] attachmentIdArray = task.getAttachments(field.getId());
						if(attachmentIdArray != null && attachmentIdArray.length > 0)
						{
							StringBuffer valueStrb = new StringBuffer();
							Map<String, String> attachMap = new AttachmentAccessSessionMySQL().queryAttachmentIdNames(attachmentIdArray);
							
							for(String id : attachMap.keySet()){
								if(valueStrb.length() > 0)
									valueStrb.append(",");
								String attachName = attachMap.get(id);
								if (CynthiaUtil.isPicture(attachName)) {  //如果是图片则加上连接
									valueStrb.append("[").append("<a href = \"").append(ConfigUtil.getCynthiaWebRoot() + "attachment/image.jsp?fileId=" + id).append("\" target=\"_blank\">").append(attachMap.get(id)).append("</a>").append("]");
								}else {
									valueStrb.append("[").append("<a href = \"").append(ConfigUtil.getCynthiaWebRoot() + "attachment/download.jsp?method=download&id=" + id).append("\" target=\"blank\">").append(attachMap.get(id)).append("</a>").append("]");
								}
							}
							
							if(valueStrb.length() > 0)
								fieldShowValue = valueStrb.toString();
						}
					}
					else if(field.getType().equals(Type.t_input))
					{
						if(field.getDataType().equals(DataType.dt_integer))
						{
							Integer valueTmp = task.getInteger(field.getId());
							if(valueTmp == null){
								valueTmp = Integer.MIN_VALUE;
							}
							
							fieldShowValue =  valueTmp.toString();
						}
						else if(field.getDataType().equals(DataType.dt_long))
						{
							Long valueTmp = task.getLong(field.getId());
							if(valueTmp == null){
								valueTmp = Long.MIN_VALUE;
							}
							
							fieldShowValue =  valueTmp.toString();
						}
						else if(field.getDataType().equals(DataType.dt_float))
						{
							Float valueTmp = task.getFloat(field.getId());
							if(valueTmp == null){
								valueTmp = Float.MIN_VALUE;
							}
							
							fieldShowValue =  valueTmp.toString();
						}
						else if(field.getDataType().equals(DataType.dt_double))
						{
							Double valueTmp = task.getDouble(field.getId());
							if(valueTmp == null){
								valueTmp = Double.MIN_VALUE;
							}
							
							fieldShowValue =  valueTmp.toString();
						}
						else if(field.getDataType().equals(DataType.dt_string) || field.getDataType().equals(DataType.dt_text))
						{
							String valueTmp = task.getString(field.getId());
							if(valueTmp != null)
								fieldShowValue =  valueTmp;
						}
						else if(field.getDataType().equals(DataType.dt_timestamp))
						{
							Date valueTmp = task.getDate(field.getId());
							if(valueTmp != null)
								fieldShowValue = Date.formatDate(valueTmp.toString(), field.getTimestampFormat());
						}
					}
				
				}
			}
			if ("".equals(fieldShowValue) || null == fieldShowValue) {
				fieldShowValue = "";
			}
			
			if (exportType.equals(ExportType.json)) {
				if (!"".equals(fieldShowName)) {
					fieldValueMap.put(fieldShowName, CynthiaUtil.stringToJson(fieldShowValue));	
				}
			}else if (exportType.equals(ExportType.excel) || exportType.equals(ExportType.html)) {
				fieldShowValue = fieldShowValue.replace("\n", "<br>");
				fieldValueMap.put(fieldName, fieldShowValue);	
			}else if (exportType.equals(ExportType.xml)) {
				if (!"".equals(fieldShowName)) {
					fieldValueMap.put(fieldShowName, CynthiaUtil.getXMLStr(fieldShowValue));	
				}
			}
		}
		return fieldValueMap;
	}
	
	/**
	 * 查询跟项目管理相关的Bug数量
	 * @Title: queryProjectDataIds
	 * @Description: TODO
	 * @param projectIds
	 * @return
	 * @return: Map<String,Set<String>>
	 */
	public static Map<String, Set<String>> queryProjectDataIds(List<String> projectIds){
		
		Map<String, Set<String>> returnMap = new HashMap<String, Set<String>>();
		if (projectIds == null || projectIds.size() == 0 ) {
			return returnMap;
		}
		
		DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
		Template[] allTemplates = das.queryAllTemplates();
		for (Template template : allTemplates) {
			if (template.getTemplateConfig().isProjectInvolve() && template.getTemplateConfig().getProjectInvolveId() != null) {
				String projectInvolvedId = template.getTemplateConfig().getProjectInvolveId();
				String fieldColName = FieldNameCache.getInstance().getFieldName(projectInvolvedId, template.getId().getValue());
				
				Map<String, String> idAndProjectIdMap = new DataAccessSessionMySQL().queryIdAndFieldOfTemplate(template.getId().getValue(), fieldColName);
				for (String dataId : idAndProjectIdMap.keySet()) {
					String projectId = idAndProjectIdMap.get(dataId);
					if (projectIds.contains(projectId)) {
						if (returnMap.get(projectId) == null) {
							returnMap.put(projectId, new HashSet<String>());
						}

						returnMap.get(projectId).add(dataId);
					}
				}
			}
		}
		
		return returnMap;
	}
}
