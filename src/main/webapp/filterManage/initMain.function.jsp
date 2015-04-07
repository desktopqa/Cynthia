<%@page import="com.sogou.qadev.service.cynthia.service.ConfigManager"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ProjectInvolveManager"%>
<%@page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataManager"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Right"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UserInfo"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.CommonOption"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.CommonField"%>
<%@ page language="java" pageEncoding="UTF-8"%>

<%@page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Stat"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Option"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Data"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Attachment"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@page import="java.util.*"%>
<%@page import="org.w3c.dom.*"%>

<%!Map<String,String> getTemplateFields(DataAccessSession das,Template template)
	{
		Map<String,String> fields = new LinkedHashMap<String,String>();
		if(template == null)
			return fields;
		Set<Field> fieldSet = template.getFields();
		fields.put("title","标题");
		fields.put("description","描述");
		fields.put("status_id","状态");
		fields.put("assign_user","指派人");
		fields.put("last_modify_time","修改时间");
		fields.put("create_user","创建人");
		fields.put("create_time","创建时间");
		fields.put("action_time_range","动作时间范围");
		fields.put("action_id","动作");
		fields.put("log_create_user","执行人员");
		
		for(Field field : fieldSet)
		{
			if(field.getName()!=null&&field.getName().indexOf("废弃")>=0)
				continue;
			fields.put(field.getId().toString(),field.getName());
		}
		return fields;
	}

	private CommonField getCommonField(Template template,String fieldId,DataAccessSession das)
	{
		CommonField commonField = new CommonField();
		if(fieldId == null)
		{
			return null;
		}else if("title".equals(fieldId))
		{
			commonField.setId("title");
			commonField.setName("标题");
			commonField.setType("input");
			commonField.setDataType("string");
		}else if("description".equals(fieldId))
		{
			commonField.setId("description");
			commonField.setName("描述");
			commonField.setType("input");
			commonField.setDataType("string");
		}else if("status_id".equals(fieldId))
		{
			commonField.setId("status_id");
			commonField.setName("状态");
			commonField.setType("status_id");
		}else if("action_id".equals(fieldId))
		{
			commonField.setId("action_id");
			commonField.setName("动作");
			commonField.setType("action_id");
		}else if("assign_user".equals(fieldId))
		{
			commonField.setId("assign_user");
			commonField.setName("指派人");
			commonField.setType("assign_user");
		}else if("create_user".equals(fieldId))
		{
			commonField.setId("create_user");
			commonField.setName("创建人");
			commonField.setType("create_user");
		}else if("create_time".equals(fieldId))
		{
			commonField.setId("create_time");
			commonField.setName("创建时间");
			commonField.setType("timestamp");
		}else if("last_modify_time".equals(fieldId))
		{
			commonField.setId("last_modify_time");
			commonField.setName("修改时间");
			commonField.setType("timestamp");
		}else if("action_time_range".equals(fieldId))
		{
			commonField.setId("action_time_range");
			commonField.setName("动作时间范围");
			commonField.setType("timestamp");
		}else if("log_create_user".equals(fieldId))
		{
			commonField.setId("log_create_user");
			commonField.setName("执行人员");
			commonField.setType("log_create_user");
		}else
		{
			Set<Field> fieldSet = template.getFields();
			if(fieldSet == null)
				return null;

			for(Field field : fieldSet)
			{

				if(fieldId.equals(field.getId().toString()))
				{
					commonField.setId(field.getId().toString());
					commonField.setName(field.getName());
					commonField.setType(field.getType().toString().split("\\_")[1]);
					if(field.getDataType() != null)
						commonField.setDataType(field.getDataType().toString().split("\\_")[1]);
					break;
				}
			}
		}

		return commonField;
	}

	private Set<CommonOption> getCommonFieldOptionSet(Template template,String fieldId,DataAccessSession das)
	{
		Set<CommonOption> optionSet = new TreeSet<CommonOption>();
		if(template == null)
			return optionSet;
		Flow flow = das.queryFlow(template.getFlowId());
		if(flow == null)
			return optionSet;
		if("status_id".equals(fieldId))
		{
			Stat[] statusArray = das.queryTemplateStats(template.getId());
			for(Stat status : statusArray)
			{
				CommonOption option = new CommonOption(status.getId().toString(), status.getName());
				optionSet.add(option);
			}
			CommonOption logicalBeginOption = new CommonOption("[逻辑开始]", "[逻辑开始]");
			optionSet.add(logicalBeginOption);
			CommonOption logicalEndOption = new CommonOption("[逻辑关闭]", "[逻辑关闭]");
			optionSet.add(logicalEndOption);
		}else if("action_id".equals(fieldId))
		{
			Action[] actionsArray = flow.getActions();
			for(Action action : actionsArray){
				CommonOption option = new CommonOption(action.getId().toString(),action.getName());
				optionSet.add(option);
			}
			CommonOption editOption = new CommonOption(Action.editUUID.toString(),Action.editName);
			optionSet.add(editOption);
		}else if("assign_user".equals(fieldId))
		{
			
			List<UserInfo> allAssignUserList = new ArrayList<UserInfo>();
			
			if (template.getTemplateConfig().isProjectInvolve()) {
				allAssignUserList = ProjectInvolveManager.getInstance().getCompanyUsersByMail(das.getUsername());
			}else{
				Set<String> assignUserSet = new HashSet<String>();
				
				for(Right right : flow.getRightSet()){
					assignUserSet.add(right.getUsername());
				}
				
				//表单中的所有指派人员
				assignUserSet.addAll(Arrays.asList(das.queryTemplateAssignUsers(template.getId())));
				allAssignUserList = das.queryAllUserInfo(assignUserSet.toArray(new String[0]));
				
				//普通表单增加角色查询，项目表单不加
				Role[] rolesArray = flow.getRoles();
				for(Role role : rolesArray)
				{
					CommonOption option = new CommonOption("role_" + role.getId().getValue(), "[" + role.getName() + "]");
					optionSet.add(option);
				}
			}
			
			for(UserInfo userInfo : allAssignUserList)
			{
				CommonOption option = new CommonOption(userInfo.getUserName(), userInfo.getNickName());
				optionSet.add(option);
			}
			
		}else if("create_user".equals(fieldId))
		{
			
			Set<Right> allFlowRights = flow.getRightSet();
			Set<String> createUserSet = new HashSet<String>();
			
			for(Right right : allFlowRights){
				createUserSet.add(right.getUsername());
			}
			//表单中的所有创建人员
			createUserSet.addAll(Arrays.asList(das.queryTemplateCreateUsers(template.getId())));
			
			List<UserInfo> allCreateUserList = das.queryAllUserInfo(createUserSet.toArray(new String[0]));
			for(UserInfo userInfo : allCreateUserList)
			{
				CommonOption option = new CommonOption(userInfo.getUserName(), userInfo.getNickName());
				optionSet.add(option);
			}
			
			if(!ConfigManager.getProjectInvolved()){
				Role[] rolesArray = flow.getRoles();
				for(Role role : rolesArray)
				{
					CommonOption option = new CommonOption("role_" + role.getId().getValue(), "[" + role.getName() + "]");
					optionSet.add(option);
				}
			}
			
		}else if("log_create_user".equals(fieldId))
		{
			Set<Right> allFlowRights = flow.getRightSet();
			Set<String> assignUserSet = new HashSet<String>();
			
			for(Right right : allFlowRights){
				assignUserSet.add(right.getUsername());
			}
			
			List<UserInfo> allAssignUserList = das.queryAllUserInfo(assignUserSet.toArray(new String[0]));
			for(UserInfo userInfo : allAssignUserList)
			{
				CommonOption option = new CommonOption(userInfo.getUserName(), userInfo.getNickName());
				optionSet.add(option);
			}
			
			if(!ConfigManager.getProjectInvolved()){
				Role[] rolesArray = flow.getRoles();
				for(Role role : rolesArray)
				{
					CommonOption option = new CommonOption("role_" + role.getId().getValue(), "[" + role.getName() + "]");
					optionSet.add(option);
				}
			}
			
		}else
		{
			Set<Field> fieldSet = template.getFields();
			for(Field field : fieldSet)
			{
				if(fieldId.equals(field.getId().toString()))
				{
					if(field.getType().equals(Field.Type.t_selection))
					{
						Set<Option> allOptions = field.getOptions();
						
						if(allOptions != null && allOptions.size() > 0)
						{
							for(Option option : allOptions)
							{
								CommonOption commonOption = new CommonOption();
								commonOption.setId(option.getId().toString());
								commonOption.setName(option.getName());
								optionSet.add(commonOption);
							}
						}
					}
					else if(field.getType().equals(Field.Type.t_reference))
					{
						Data[] referenceArray = das.queryTemplateFieldReferences(template.getId(), field.getId());
						if(referenceArray != null && referenceArray.length > 0)
						{
							for(Data reference : referenceArray)
							{
								CommonOption commonOption = new CommonOption();
								commonOption.setId(reference.getId().toString());
								commonOption.setName(reference.getTitle());
								optionSet.add(commonOption);
							}
						}
					}
					else if(field.getType().equals(Field.Type.t_attachment))
					{
						Attachment[] attachmentArray = das.queryTemplateFieldAttachments(template.getId(), field.getId());
						if(attachmentArray != null && attachmentArray.length > 0)
						{
							for(Attachment attachment : attachmentArray)
							{
								CommonOption commonOption = new CommonOption();
								commonOption.setId(attachment.getId().toString());
								commonOption.setName(attachment.getName());
								optionSet.add(commonOption);
							}
						}
					}
					break;
				}
			}
		}

		return optionSet;
	}

	private Map<String, CommonField> getAllCommonFieldMap(Template template,DataAccessSession das)
	{
		Map<String,CommonField> fieldMap = new HashMap<String,CommonField>();
		//title field
		CommonField titleField = new CommonField();
		titleField.setId("title");
		titleField.setName("标题");
		titleField.setType("input");
		titleField.setDataType("string");
		fieldMap.put("title", titleField);

		//description field  取消描述排序
// 		CommonField descriptionField = new CommonField();
// 		descriptionField.setId("description");
// 		descriptionField.setName("描述");
// 		descriptionField.setType("input");
// 		descriptionField.setDataType("text");
// 		fieldMap.put("description", descriptionField);

		//create_user field
		CommonField createUserField = new CommonField();
		createUserField.setId("create_user");
		createUserField.setName("创建人");
		createUserField.setType("create_user");
		fieldMap.put("create_user", createUserField);

		//create_time field
		CommonField createTimeField = new CommonField();
		createTimeField.setId("create_time");
		createTimeField.setName("创建时间");
		createTimeField.setType("timestamp");
		fieldMap.put("create_time", createTimeField);

		//assign_user field
		CommonField assignUserField = new CommonField();
		assignUserField.setId("assign_user");
		assignUserField.setName("指派人");
		assignUserField.setType("assign_user");
		fieldMap.put("assign_user", assignUserField);


		//last_modify_time field
		CommonField lastModifyTimeField = new CommonField();
		lastModifyTimeField.setId("last_modify_time");
		lastModifyTimeField.setName("修改时间");
		lastModifyTimeField.setType("timestamp");
		fieldMap.put("last_modify_time", lastModifyTimeField);

		//status field
		CommonField statusField = new CommonField();
		statusField.setId("status_id");
		statusField.setName("状态");
		statusField.setType("status_id");
		fieldMap.put("status_id", statusField);

		//某个时间范围内做的动作
		CommonField actionTimeRangeField = new CommonField();
		actionTimeRangeField.setId("action_time_range");
		actionTimeRangeField.setName("动作时间范围");
		actionTimeRangeField.setType("timestamp");
		fieldMap.put("action_time_range",actionTimeRangeField);

		//该表单对应的动作
		CommonField actionField = new CommonField();
		actionField.setId("action_id");
		actionField.setName("动作");
		actionField.setType("action_id");
		fieldMap.put("action_id",actionField);

		//ext fields
		Set<Field> fieldSet = template.getFields();
		if(fieldSet != null)
		{
			for(Field field : fieldSet)
			{

				if(field.getName()!=null&&field.getName().indexOf("废弃")>=0)
					continue;

				CommonField commonField = new CommonField();
				commonField.setId(field.getId().toString());
				commonField.setName(field.getName());
				commonField.setType(field.getType().toString().split("\\_")[1]);
				if(field.getDataType() != null)
					commonField.setDataType(field.getDataType().toString().split("\\_")[1]);
				fieldMap.put(field.getId().toString(),commonField);
			}
		}
		return fieldMap;
	}

	String queryFieldHTML(DataAccessSession das,UUID templateId,String fieldId, Map<String, Set<Object>> whereFieldMap,
			Map<String, CommonField> displayFieldMap, Map<String, CommonField> orderFieldMap, int orderIndent)
	{

		if(displayFieldMap == null)
		{
			displayFieldMap = new LinkedHashMap<String, CommonField>();

			CommonField titleField = new CommonField();
			titleField.setId("title");
			titleField.setName("标题");
			titleField.setType("title");
			displayFieldMap.put("title", titleField);

			CommonField statusField = new CommonField();
			statusField.setId("status_id");
			statusField.setName("状态");
			statusField.setType("status_id");
			displayFieldMap.put("status_id", statusField);

			CommonField assignUserField = new CommonField();
			assignUserField.setId("assign_user");
			assignUserField.setName("指派人");
			assignUserField.setType("assign_user");
			displayFieldMap.put("assign_user", assignUserField);

			CommonField lastModifyTimeField = new CommonField();
			lastModifyTimeField.setId("last_modify_time");
			lastModifyTimeField.setName("修改时间");
			lastModifyTimeField.setType("last_modify_time");
			displayFieldMap.put("last_modify_time", lastModifyTimeField);

			CommonField createUserField = new CommonField();
			createUserField.setId("create_user");
			createUserField.setName("创建人");
			createUserField.setType("create_user");
			displayFieldMap.put("create_user", createUserField);

			CommonField createTimeField = new CommonField();
			createTimeField.setId("create_time");
			createTimeField.setName("创建时间");
			createTimeField.setType("create_time");
			displayFieldMap.put("create_time", createTimeField);
			
			CommonField logCreateUserField = new CommonField();
			createTimeField.setId("log_create_user");
			createTimeField.setName("执行人员");
			createTimeField.setType("log_create_user");
			displayFieldMap.put("log_create_user", logCreateUserField);
		}

		Template template = das.queryTemplate(templateId);
		CommonField field = this.getCommonField(template,fieldId,das);
	
		if(field == null||field.getType() == null)
			return "";
		
		if(field.getType().equals("status_id")
					|| field.getType().equals("create_user")
					|| field.getType().equals("assign_user")
					|| field.getType().equals("action_id")
					|| field.getType().equals("log_create_user"))
		{
			return (getWhereFieldBaseHTML(templateId + "_" + fieldId, field, this.getCommonFieldOptionSet(template,fieldId,das), (whereFieldMap == null ? null : whereFieldMap.get(fieldId))));
		}
		else if(field.getType().equals("selection")
					|| field.getType().equals("reference")
						|| field.getType().equals("attachment"))
		{
			return (getWhereFieldSelectionHTML(templateId + "_" + fieldId, field, this.getCommonFieldOptionSet(template,fieldId,das), (whereFieldMap == null ? null : whereFieldMap.get(fieldId))));
		}
		else if(field.getType().equals("input")
					|| field.getType().equals("timestamp"))
		{
			return (getWhereFieldInputHTML(templateId + "_" + fieldId, field, (whereFieldMap == null ? null : whereFieldMap.get(fieldId))));
		}
		return "";
	}

	String queryOrderContentHTML(DataAccessSession das,UUID templateId, Map<String, CommonField> orderFieldMap, int orderIndent)
	{
		return getOrderFieldHTML("order_" + templateId, this.getAllCommonFieldMap(das.queryTemplate(templateId),das), orderFieldMap, orderIndent);
	}

	String queryTemplateTypeHTML(DataAccessSession das, UUID templateTypeId, Map<String, Set<Object>> whereFieldMap,
			Map<String, CommonField> displayFieldMap, Map<String, CommonField> orderFieldMap, int orderIndent)
	{
		if(displayFieldMap == null)
		{
			displayFieldMap = new LinkedHashMap<String, CommonField>();

			CommonField titleField = new CommonField();
			titleField.setId("title");
			titleField.setName("标题");
			titleField.setType("title");
			displayFieldMap.put("title", titleField);

			CommonField statusField = new CommonField();
			statusField.setId("status_id");
			statusField.setName("状态");
			statusField.setType("status_id");
			displayFieldMap.put("status_id", statusField);

			CommonField assignUserField = new CommonField();
			assignUserField.setId("assign_user");
			assignUserField.setName("指派人");
			assignUserField.setType("assign_user");
			displayFieldMap.put("assign_user", assignUserField);

			CommonField lastModifyTimeField = new CommonField();
			lastModifyTimeField.setId("last_modify_time");
			lastModifyTimeField.setName("修改时间");
			lastModifyTimeField.setType("last_modify_time");
			displayFieldMap.put("last_modify_time", lastModifyTimeField);

			CommonField createUserField = new CommonField();
			createUserField.setId("create_user");
			createUserField.setName("创建人");
			createUserField.setType("create_user");
			displayFieldMap.put("create_user", createUserField);

			CommonField createTimeField = new CommonField();
			createTimeField.setId("create_time");
			createTimeField.setName("创建时间");
			createTimeField.setType("create_time");
			displayFieldMap.put("create_time", createTimeField);
		}

		Map<String, CommonField> fieldMap = new LinkedHashMap<String, CommonField>();
		Map<String, Set<CommonOption>> fieldOptionMap = new LinkedHashMap<String, Set<CommonOption>>();

		//title field
		CommonField titleField = new CommonField();
		titleField.setId("title");
		titleField.setName("标题");
		titleField.setType("input");
		titleField.setDataType("string");
		fieldMap.put("title", titleField);

		//description field
		CommonField descriptionField = new CommonField();
		descriptionField.setId("description");
		descriptionField.setName("描述");
		descriptionField.setType("input");
		descriptionField.setDataType("text");
		fieldMap.put("description", descriptionField);

		//create_user field
		CommonField createUserField = new CommonField();
		createUserField.setId("create_user");
		createUserField.setName("创建人");
		createUserField.setType("create_user");
		fieldMap.put("create_user", createUserField);
		fieldOptionMap.put("create_user", new TreeSet<CommonOption>());
		String[] createUserArray = das.queryTemplateTypeCreateUsers(templateTypeId);
		for(String createUser : createUserArray)
		{
			CommonOption option = new CommonOption(createUser, createUser);
			fieldOptionMap.get("create_user").add(option);
		}

		//create_time field
		CommonField createTimeField = new CommonField();
		createTimeField.setId("create_time");
		createTimeField.setName("创建时间");
		createTimeField.setType("timestamp");
		fieldMap.put("create_time", createTimeField);

		//assign_user field
		CommonField assignUserField = new CommonField();
		assignUserField.setId("assign_user");
		assignUserField.setName("指派人");
		assignUserField.setType("assign_user");
		fieldMap.put("assign_user", assignUserField);
		fieldOptionMap.put("assign_user", new TreeSet<CommonOption>());
		String[] assignUserArray = das.queryTemplateTypeAssignUsers(templateTypeId);
		for(String assignUser : assignUserArray)
		{
			CommonOption option = new CommonOption(assignUser, assignUser);
			fieldOptionMap.get("assign_user").add(option);
		}

		//last_modify_time field
		CommonField lastModifyTimeField = new CommonField();
		lastModifyTimeField.setId("last_modify_time");
		lastModifyTimeField.setName("修改时间");
		lastModifyTimeField.setType("timestamp");
		fieldMap.put("last_modify_time", lastModifyTimeField);

		//status field
		CommonField statusField = new CommonField();
		statusField.setId("status_id");
		statusField.setName("状态");
		statusField.setType("status_id");
		fieldMap.put("status_id", statusField);
		fieldOptionMap.put("status_id", new TreeSet<CommonOption>());
		String[] statusArray = das.queryTemplateTypeStats(templateTypeId);
		for(String status : statusArray)
		{
			CommonOption option = new CommonOption(status, status);
			fieldOptionMap.get("status_id").add(option);
		}
		CommonOption logicalBeginOption = new CommonOption("[逻辑开始]", "[逻辑开始]");
		fieldOptionMap.get("status_id").add(logicalBeginOption);
		CommonOption logicalEndOption = new CommonOption("[逻辑关闭]", "[逻辑关闭]");
		fieldOptionMap.get("status_id").add(logicalEndOption);


		//拼装返回值
		StringBuffer mainTableHTML = new StringBuffer(64);
		mainTableHTML.append("<table width=\"100%\">");

		mainTableHTML.append("<tr>");

		StringBuffer leftMainTableHTML = new StringBuffer(64);
		leftMainTableHTML.append("<table id=\"table_left_main_").append(templateTypeId).append("\" width=\"100%\">");

		StringBuffer rightMainTableHTML = new StringBuffer(64);
		rightMainTableHTML.append("<table id=\"table_right_main_").append(templateTypeId).append("\" width=\"100%\">");

		int index = 0;
		for(String fieldId : fieldMap.keySet())
		{
			if(fieldId.equals("id"))
				continue;

			StringBuffer trHTML = new StringBuffer(64);

			trHTML.append("<tr>");
			trHTML.append("<td class=\"tdNoBottom\">");

			CommonField field = fieldMap.get(fieldId);

			if(field.getType().equals("status_id")
					|| field.getType().equals("create_user")
					|| field.getType().equals("assign_user"))
			{
				trHTML.append(getWhereFieldBaseHTML(templateTypeId + "_" + fieldId, field, fieldOptionMap.get(fieldId), (whereFieldMap == null ? null : whereFieldMap.get(fieldId))));
			}
			else if(field.getType().equals("selection")
					|| field.getType().equals("reference"))
			{
				trHTML.append(getWhereFieldSelectionHTML(templateTypeId + "_" + fieldId, field, fieldOptionMap.get(fieldId), (whereFieldMap == null ? null : whereFieldMap.get(fieldId))));
			}
			else if(field.getType().equals("input")
					|| field.getType().equals("timestamp"))
			{
				trHTML.append(getWhereFieldInputHTML(templateTypeId + "_" + fieldId, field, (whereFieldMap == null ? null : whereFieldMap.get(fieldId))));
			}

			trHTML.append("</td>");
			trHTML.append("</tr>");

			if(index % 2 == 0)
			{
				leftMainTableHTML.append(trHTML);
			}
			else
			{
				rightMainTableHTML.append(trHTML);
			}

			index++;
		}

		leftMainTableHTML.append("</table>");
		rightMainTableHTML.append("</table>");

		mainTableHTML.append("<td class=\"tdNoBottom\">");
		mainTableHTML.append(leftMainTableHTML);
		mainTableHTML.append("</td>");

		mainTableHTML.append("<td class=\"tdNoBottom\">");
		mainTableHTML.append(rightMainTableHTML);
		mainTableHTML.append("</td>");

		mainTableHTML.append("</tr>");

		mainTableHTML.append("<tr>");
		mainTableHTML.append("<td class=\"tdNoBottom\" colspan=\"2\"><input id=\"input_display_").append(templateTypeId).append("\" type=\"button\" onClick=\"displayArea('").append(templateTypeId).append("')\" value=\"打开显示设置\"/></td>");
		mainTableHTML.append("</tr>");
		mainTableHTML.append("<tr id=\"tr_display_").append(templateTypeId).append("\" style=\"display:none\">");
		mainTableHTML.append("<td id=\"td_display_").append(templateTypeId).append("\" colspan=\"2\" class=\"tdNoBottom\">");
		mainTableHTML.append(getDisplayFieldHTML("display_" + templateTypeId, fieldMap, displayFieldMap));
		mainTableHTML.append("</td>");
		mainTableHTML.append("</tr>");

		mainTableHTML.append("<tr>");
		mainTableHTML.append("<td class=\"tdNoBottom\" colspan=\"2\"><input id=\"input_order_").append(templateTypeId).append("\" type=\"button\" onClick=\"orderArea('").append(templateTypeId).append("')\" value=\"打开排序设置\"/></td>");
		mainTableHTML.append("</tr>");
		mainTableHTML.append("<tr id=\"tr_order_").append(templateTypeId).append("\" style=\"display:none\">");
		mainTableHTML.append("<td id=\"td_order_").append(templateTypeId).append("\" colspan=\"2\" class=\"tdNoBottom\">");
		mainTableHTML.append(getOrderFieldHTML("order_" + templateTypeId, fieldMap, orderFieldMap, orderIndent));
		mainTableHTML.append("</td>");
		mainTableHTML.append("</tr>");

		mainTableHTML.append("</table>");

		return mainTableHTML.toString();
	}


	String queryTemplateNodeHTML(DataAccessSession das, UUID templateId, Map<String, Set<Object>> whereFieldMap,
			Map<String, CommonField> displayFieldMap, Map<String, CommonField> orderFieldMap, int orderIndent)
	{
		Map<String, CommonField> fieldMap = new LinkedHashMap<String, CommonField>();
		Map<String, Set<CommonOption>> fieldOptionMap = new LinkedHashMap<String, Set<CommonOption>>();

		if(displayFieldMap == null)
		{
			displayFieldMap = new LinkedHashMap<String, CommonField>();

			CommonField titleField = new CommonField();
			titleField.setId("title");
			titleField.setName("标题");
			titleField.setType("title");
			displayFieldMap.put("title", titleField);

			CommonField statusField = new CommonField();
			statusField.setId("status_id");
			statusField.setName("状态");
			statusField.setType("status_id");
			displayFieldMap.put("status_id", statusField);

			CommonField assignUserField = new CommonField();
			assignUserField.setId("assign_user");
			assignUserField.setName("指派人");
			assignUserField.setType("assign_user");
			displayFieldMap.put("assign_user", assignUserField);

			CommonField lastModifyTimeField = new CommonField();
			lastModifyTimeField.setId("last_modify_time");
			lastModifyTimeField.setName("修改时间");
			lastModifyTimeField.setType("last_modify_time");
			displayFieldMap.put("last_modify_time", lastModifyTimeField);

			CommonField createUserField = new CommonField();
			createUserField.setId("create_user");
			createUserField.setName("创建人");
			createUserField.setType("create_user");
			displayFieldMap.put("create_user", createUserField);

			CommonField createTimeField = new CommonField();
			createTimeField.setId("create_time");
			createTimeField.setName("创建时间");
			createTimeField.setType("create_time");
			displayFieldMap.put("create_time", createTimeField);
		}

		//title field
		CommonField titleField = new CommonField();
		titleField.setId("title");
		titleField.setName("标题");
		titleField.setType("input");
		titleField.setDataType("string");
		fieldMap.put("title", titleField);

		//description field
		CommonField descriptionField = new CommonField();
		descriptionField.setId("description");
		descriptionField.setName("描述");
		descriptionField.setType("input");
		descriptionField.setDataType("text");
		fieldMap.put("description", descriptionField);

		//create_user field
		CommonField createUserField = new CommonField();
		createUserField.setId("create_user");
		createUserField.setName("创建人");
		createUserField.setType("create_user");
		fieldMap.put("create_user", createUserField);
		fieldOptionMap.put("create_user", new TreeSet<CommonOption>());
		String[] createUserArray = das.queryTemplateCreateUsers(templateId);
		for(String createUser : createUserArray)
		{
			CommonOption option = new CommonOption(createUser, createUser);
			fieldOptionMap.get("create_user").add(option);
		}

		//create_time field
		CommonField createTimeField = new CommonField();
		createTimeField.setId("create_time");
		createTimeField.setName("创建时间");
		createTimeField.setType("timestamp");
		fieldMap.put("create_time", createTimeField);

		//assign_user field
		CommonField assignUserField = new CommonField();
		assignUserField.setId("assign_user");
		assignUserField.setName("指派人");
		assignUserField.setType("assign_user");
		fieldMap.put("assign_user", assignUserField);
		fieldOptionMap.put("assign_user", new TreeSet<CommonOption>());
		String[] assignUserArray = das.queryTemplateAssignUsers(templateId);
		for(String assignUser : assignUserArray)
		{
			CommonOption option = new CommonOption(assignUser, assignUser);
			fieldOptionMap.get("assign_user").add(option);
		}

		//last_modify_time field
		CommonField lastModifyTimeField = new CommonField();
		lastModifyTimeField.setId("last_modify_time");
		lastModifyTimeField.setName("修改时间");
		lastModifyTimeField.setType("timestamp");
		fieldMap.put("last_modify_time", lastModifyTimeField);

		//status field
		CommonField statusField = new CommonField();
		statusField.setId("status_id");
		statusField.setName("状态");
		statusField.setType("status_id");
		fieldMap.put("status_id", statusField);
		fieldOptionMap.put("status_id", new TreeSet<CommonOption>());
		Stat[] statusArray = das.queryTemplateStats(templateId);
		for(Stat status : statusArray)
		{
			CommonOption option = new CommonOption(status.getId().toString(), status.getName());
			fieldOptionMap.get("status_id").add(option);
		}
		CommonOption logicalBeginOption = new CommonOption("[逻辑开始]", "[逻辑开始]");
		fieldOptionMap.get("status_id").add(logicalBeginOption);
		CommonOption logicalEndOption = new CommonOption("[逻辑关闭]", "[逻辑关闭]");
		fieldOptionMap.get("status_id").add(logicalEndOption);

		//ext fields
		DataManager.getInstance().addExtFields(templateId, fieldMap, fieldOptionMap, das);

		//某个时间范围内做的动作
		CommonField actionTimeRangeField = new CommonField();
		actionTimeRangeField.setId("action_time_range");
		actionTimeRangeField.setName("动作时间范围");
		actionTimeRangeField.setType("timestamp");
		fieldMap.put("action_time_range",actionTimeRangeField);

		//该表单对应的动作
		CommonField actionField = new CommonField();
		actionField.setId("action_id");
		actionField.setName("动作");
		actionField.setType("action_id");
		fieldMap.put("action_id",actionField);
		fieldOptionMap.put("action_id",new TreeSet<CommonOption>());
		Template template = das.queryTemplate(templateId);
		Flow flow = das.queryFlow(template.getFlowId());
		Action[] actionsArray = flow.getActions();
		for(Action action : actionsArray){
			CommonOption option = new CommonOption(action.getId().toString(),action.getName());
			fieldOptionMap.get("action_id").add(option);
		}


		//拼装返回值
		StringBuffer mainTableHTML = new StringBuffer(64);
		mainTableHTML.append("<table width=\"100%\">");

		mainTableHTML.append("<tr>");

		StringBuffer leftMainTableHTML = new StringBuffer(64);
		leftMainTableHTML.append("<table id=\"table_left_main_").append(templateId).append("\" width=\"100%\">");

		StringBuffer rightMainTableHTML = new StringBuffer(64);
		rightMainTableHTML.append("<table id=\"table_right_main_").append(templateId).append("\" width=\"100%\">");

		int index = 0;
		for(String fieldId : fieldMap.keySet())
		{
			if(fieldId.equals("id"))
				continue;

			StringBuffer trHTML = new StringBuffer(64);

			trHTML.append("<tr>");
			trHTML.append("<td class=\"tdNoBottom\">");

			CommonField field = fieldMap.get(fieldId);

			if(field.getType().equals("status_id")
					|| field.getType().equals("create_user")
					|| field.getType().equals("assign_user")
					|| field.getType().equals("action_id"))
			{
				trHTML.append(getWhereFieldBaseHTML(templateId + "_" + fieldId, field, fieldOptionMap.get(fieldId), (whereFieldMap == null ? null : whereFieldMap.get(fieldId))));
			}
			else if(field.getType().equals("selection")
					|| field.getType().equals("reference")
						|| field.getType().equals("attachment"))
			{
				trHTML.append(getWhereFieldSelectionHTML(templateId + "_" + fieldId, field, fieldOptionMap.get(fieldId), (whereFieldMap == null ? null : whereFieldMap.get(fieldId))));
			}
			else if(field.getType().equals("input")
					|| field.getType().equals("timestamp"))
			{
				trHTML.append(getWhereFieldInputHTML(templateId + "_" + fieldId, field, (whereFieldMap == null ? null : whereFieldMap.get(fieldId))));
			}

			trHTML.append("</td>");
			trHTML.append("</tr>");

			if(index % 2 == 0)
			{
				leftMainTableHTML.append(trHTML);
			}
			else
			{
				rightMainTableHTML.append(trHTML);
			}

			index++;
		}

		leftMainTableHTML.append("</table>");
		rightMainTableHTML.append("</table>");

		mainTableHTML.append("<td class=\"tdNoBottom\">");
		mainTableHTML.append(leftMainTableHTML);
		mainTableHTML.append("</td>");

		mainTableHTML.append("<td class=\"tdNoBottom\">");
		mainTableHTML.append(rightMainTableHTML);
		mainTableHTML.append("</td>");

		mainTableHTML.append("</tr>");

		mainTableHTML.append("<tr>");
		mainTableHTML.append("<td class=\"tdNoBottom\" colspan=\"2\"><input id=\"input_display_").append(templateId).append("\" type=\"button\" onClick=\"displayArea('").append(templateId).append("')\" value=\"打开显示设置\"/></td>");
		mainTableHTML.append("</tr>");
		mainTableHTML.append("<tr id=\"tr_display_").append(templateId).append("\" style=\"display:none\">");
		mainTableHTML.append("<td id=\"td_display_").append(templateId).append("\" colspan=\"2\" class=\"tdNoBottom\">");
		mainTableHTML.append(getDisplayFieldHTML("display_" + templateId, fieldMap, displayFieldMap));
		mainTableHTML.append("</td>");
		mainTableHTML.append("</tr>");

		mainTableHTML.append("<tr>");
		mainTableHTML.append("<td class=\"tdNoBottom\" colspan=\"2\"><input id=\"input_order_").append(templateId).append("\" type=\"button\" onClick=\"orderArea('").append(templateId).append("')\" value=\"打开排序设置\"/></td>");
		mainTableHTML.append("</tr>");
		mainTableHTML.append("<tr id=\"tr_order_").append(templateId).append("\" style=\"display:none\">");
		mainTableHTML.append("<td id=\"td_order_").append(templateId).append("\" colspan=\"2\" class=\"tdNoBottom\">");
		mainTableHTML.append(getOrderFieldHTML("order_" + templateId, fieldMap, orderFieldMap, orderIndent));
		mainTableHTML.append("</td>");
		mainTableHTML.append("</tr>");

		mainTableHTML.append("</table>");

		return mainTableHTML.toString();
	}


	String getWhereFieldBaseHTML(String prefix, CommonField field, Set<CommonOption> optionSet, Set<Object> whereFieldSet)
	{
		String method = "";
		String values = "";
		boolean isAdvance = false;
		boolean isAll = false;
		
		if(whereFieldSet != null)
		{
			for(Object obj : whereFieldSet)
			{
				if(obj instanceof String)
				{
					isAdvance = true;
				}
				else if(obj instanceof CommonField)
				{
					CommonField tempField = (CommonField)obj;
					
					method = tempField.getMethod();

					if(tempField.getValue() != null && !tempField.getValue().trim().equals(""))
					{
						values += "[" + tempField.getValue().trim() + "]";
					}
					isAll = tempField.isAll();
				}
			}
		}

		if(isAll)
		{
			isAdvance = false;
			values = "[[all]]";
			method = "in";
		}

		if(method != null)
		{
			if(method.equals("="))
			{
				method = "in";
			}
			else if(method.equals("!="))
			{
				method = "not in";
			}
		}

		StringBuffer strb = new StringBuffer(64);

		strb.append("<div id=\"").append(prefix).append("_where\" fieldId=\"").append(field.getId()).append("\" fieldName=\"").append(field.getName()).append("\" type=\"selection\">");

		strb.append("<table width=\"100%\">");

		strb.append("<tr>");

		strb.append("<td width=\"116px\" noWrap>").append(field.getName()).append(":<br>");
		strb.append("<span id=\"").append(prefix).append("_span\" style=\"color:blue;cursor:pointer;width:70px\" onClick=\"changeOperateType_selection('").append(prefix).append("')\">");
		strb.append(isAdvance ? "普通设置" : "高级设置");
		strb.append("</span>");
		strb.append("</td>");

		strb.append("<td>");
		strb.append("<select id=\"").append(prefix).append("_method\" style=\"width:8em\" onChange=\"setXMLResult('").append(prefix).append("',false)\">");
		strb.append("<option></option>");
		strb.append("<option value=\"in\"").append(method.equals("in") ? " selected" : "").append(">in</option>");
		strb.append("<option value=\"not in\"").append(method.equals("not in") ? " selected" : "").append(">not in</option>");
		if(field.getId().equals("assign_user") || field.getId().equals("log_create_user") )
		{
			strb.append("<option value=\"is null\"").append(method.equals("is null") ? " selected" : "").append(">is null</option>");
			strb.append("<option value=\"is not null\"").append(method.equals("is not null")? " selected" : "").append(">is not null</option>");
		}
		strb.append("</select>");
		strb.append("</td>");

		strb.append("<td>");
		strb.append("<select id=\"").append(prefix).append("_value\" style=\"width:16em;\" onChange=\"setXMLResult('").append(prefix).append("',false)\"").append(isAdvance ? " size=\"5\" multiple" : "").append(">");
		strb.append("<option></option>");
		if(optionSet != null)
		{
			for(CommonOption option : optionSet)
			{
				strb.append("<option value=\"").append(option.getId()).append("\"").append(values.indexOf("[" + option.getId() + "]") != -1 ? " selected" : "").append(">").append(XMLUtil.toSafeXMLString(option.getName())).append("</option>");
			}
		}
		if(!isAdvance)
			strb.append("<option value=\"[all]\"").append(values.indexOf("[[all]]") != -1 ? " selected" : "").append(">[all]</option>");
		strb.append("</select>");
		strb.append("</td>");

		strb.append("</tr>");

		strb.append("</table>");

		strb.append("</div>");
		return strb.toString();
	}

	protected String getWhereFieldInputAdvanceHTML(String prefix, CommonField field, Set<Object> whereFieldSet)
	{
		StringBuffer strb = new StringBuffer(64);

		strb.append("<div id=\"").append(prefix).append("_where\" fieldId=\"").append(field.getId()).append("\" fieldName=\"").append(field.getName()).append("\" type=\"input\"");
		if(field.getDataType() != null)
			strb.append(" dataType=\"").append(field.getDataType()).append("\"");

		StringBuffer tableBuffer = new StringBuffer(64);
		tableBuffer.append("<table width=\"100%\">");

		tableBuffer.append("<tr>");

		tableBuffer.append("<td width=\"116px\" noWrap>").append(field.getName()).append(":<br>");
		tableBuffer.append("<span id=\"").append(prefix).append("_span\" style=\"color:blue;cursor:pointer;width:70px\" onClick=\"changeOperateType('").append(prefix).append("',false)\">");
		tableBuffer.append("普通设置");
		tableBuffer.append("</span>");
		tableBuffer.append("</td>");

		StringBuffer values = new StringBuffer(64);
		StringBuffer valuesForSetXMLResult = new StringBuffer();

		StringBuffer initXml = new StringBuffer();

		if(whereFieldSet != null)
		{
			for(Object whereObj : whereFieldSet)
			{
				if(whereObj instanceof CommonField)
				{
					CommonField whereField = (CommonField)whereObj;

					if(values.length() > 0)
						values.append(" ");
					values.append(whereField.getName());
					values.append(" ");
					values.append(whereField.getMethod());
					if(whereField.getValue() != null && whereField.getValue().length() > 0)
					{
						values.append(" ");
						values.append(whereField.getValue());
					}

					if(valuesForSetXMLResult.length() > 0)
						valuesForSetXMLResult.append("|");
					valuesForSetXMLResult.append("param:").append(whereField.getName());
					valuesForSetXMLResult.append("|");
					valuesForSetXMLResult.append("method:").append(whereField.getMethod());
					if(whereField.getValue() != null && whereField.getValue().length() > 0)
					{
						valuesForSetXMLResult.append("|");
						valuesForSetXMLResult.append("value:").append(whereField.getValue());
					}

					initXml.append("<field id=\"").append(whereField.getId()).append("\"");
					initXml.append(" name=\"").append(XMLUtil.toSafeXMLString(whereField.getName())).append("\"");
					if(whereField.getType() != null)
						initXml.append(" type=\"").append(whereField.getType()).append("\"");
					if(whereField.getDataType() != null)
						initXml.append(" dataType=\"").append(whereField.getDataType()).append("\"");
					initXml.append(" method=\"").append(XMLUtil.toSafeXMLString(whereField.getMethod())).append("\"");
					if(whereField.getValue() == null)
						initXml.append("/>");
					else
						initXml.append(">").append(XMLUtil.toSafeXMLString(whereField.getValue())).append("</field>");
				}
				else if(whereObj instanceof String)
				{
					if(values.length() > 0)
						values.append(" ");
					values.append((String)whereObj);

					if(valuesForSetXMLResult.length() > 0)
						valuesForSetXMLResult.append("|");
					valuesForSetXMLResult.append("condition:").append((String)whereObj);

					initXml.append("<condition>").append((String)whereObj).append("</condition>");
				}
			}
		}

		tableBuffer.append("<td>");
		tableBuffer.append(XMLUtil.toSafeXMLString(values.toString()));
		tableBuffer.append("</td>");

		tableBuffer.append("<td>");
		tableBuffer.append("<span id=\"").append(prefix).append("_span\" style=\"color:blue;cursor:pointer;width:35px\" onclick=\"editAdvance_input('").append(prefix).append("')\">");
		tableBuffer.append("编辑");
		tableBuffer.append("</span>");
		tableBuffer.append("</td>");

		tableBuffer.append("</tr>");

		tableBuffer.append("</table>");

		if(valuesForSetXMLResult.length() > 0)
			strb.append(" valuesForXml=\"").append(XMLUtil.toSafeXMLString(valuesForSetXMLResult.toString())).append("\"");

		if(initXml.length() > 0)
			strb.append(" xml=\"").append(XMLUtil.toSafeXMLString(initXml.toString())).append("\"");

		strb.append(">");

		strb.append(tableBuffer);

		strb.append("</div>");

		return strb.toString();
	}

	protected String getWhereFieldInputHTML(String prefix, CommonField field, Set<Object> whereFieldSet)
	{
		if(whereFieldSet != null && whereFieldSet.size() > 1)
		{
			return getWhereFieldInputAdvanceHTML(prefix, field, whereFieldSet);
		}

		String method = "";
		String value = "";

		if(whereFieldSet == null || whereFieldSet.size() == 0)
		{
			if(field.getId().equals("create_time") || field.getId().equals("last_modify_time")||field.getId().equals("action_time_range"))
			{
				value = "2013-01-01 00:00:00";
			}
		}
		else
		{
			CommonField tempField = (CommonField)whereFieldSet.iterator().next();
			if(tempField.getMethod() != null)
				method = tempField.getMethod().trim();
			if(tempField.getValue() != null)
				value = tempField.getValue().trim();
		}

		StringBuffer strb = new StringBuffer(64);

		strb.append("<div id=\"").append(prefix).append("_where\" fieldId=\"").append(field.getId()).append("\" fieldName=\"").append(field.getName()).append("\" type=\"input\"");
		if(field.getDataType() != null)
			strb.append(" dataType=\"").append(field.getDataType()).append("\"");
		strb.append(">");

		strb.append("<table width=\"100%\">");

		strb.append("<tr>");

		strb.append("<td width=\"116px\" noWrap>").append(field.getName()).append(":<br>");
		strb.append("<span id=\"").append(prefix).append("_span\" style=\"color:blue;cursor:pointer;width:70px\" onClick=\"changeOperateType('").append(prefix).append("',true)\">");
		strb.append("高级设置");
		strb.append("</span>");
		strb.append("</td>");

		strb.append("<td>");
		strb.append("<select id=\"").append(prefix).append("_method\" style=\"width:8em\" onChange=\"setXMLResult('").append(prefix).append("', false)\">");
		strb.append("<option></option>");
		strb.append("<option value=\"=\"").append(method.equals("=") ? " selected" : "").append(">=</option>");
		strb.append("<option value=\"!=\"").append(method.equals("!=") ? " selected" : "").append(">!=</option>");
		if(field.getId().equals("id"))
			;
		else if(field.getId().equals("create_time") || field.getId().equals("last_modify_time")||field.getId().equals("action_time_range")
				|| field.getDataType() != null && (field.getDataType().equals("integer")
						|| field.getDataType().equals("float")
						|| field.getDataType().equals("long")
						|| field.getDataType().equals("double")
						|| field.getDataType().equals("timestamp")))
		{
			strb.append("<option value=\"&gt;\"").append(method.equals(">") ? " selected" : "").append(">&gt;</option>");
			strb.append("<option value=\"&gt;=\"").append(method.equals(">=") ? " selected" : "").append(">&gt;=</option>");
			strb.append("<option value=\"&lt;\"").append(method.equals("<") ? " selected" : "").append(">&lt;</option>");
			strb.append("<option value=\"&lt;=\"").append(method.equals("<=") ? " selected" : "").append(">&lt;=</option>");
		}
		else
		{
			strb.append("<option value=\"like\"").append(method.equals("like") ? " selected" : "").append(">like</option>");
			strb.append("<option value=\"not like\"").append(method.equals("not like") ? " selected" : "").append(">not like</option>");
		}

		if(!field.getId().equals("id") && !field.getId().equals("title")
				&& !field.getId().equals("create_time") && !field.getId().equals("last_modify_time")&&!field.getId().equals("action_time_range"))
		{
			strb.append("<option value=\"is null\"").append(method.equals("is null") ? " selected" : "").append(">is null</option>");
			strb.append("<option value=\"is not null\"").append(method.equals("is not null") ? " selected" : "").append(">is not null</option>");
		}
		strb.append("</select>");
		strb.append("</td>");

		strb.append("<td>");
		strb.append("<input id=\"").append(prefix).append("_value\" type=\"text\" size=\"29\" value=\"").append(XMLUtil.toSafeXMLString(value)).append("\" onblur=\"setXMLResult('").append(prefix).append("',false)\"");
		if(field.getId().equals("id") || field.getDataType() != null
				&& (field.getDataType().equals("integer")
						|| field.getDataType().equals("float")
						|| field.getDataType().equals("long")
						|| field.getDataType().equals("double")))
		{
			strb.append(" onkeydown=\"return checkIsInputNum(event);\"");
		}
		strb.append("/>");

		// 时间字段
		if( "create_time".equals( field.getId() ) || "last_modify_time".equals( field.getId()) || "action_time_range".equals( field.getId()) || ( field.getDataType() != null && "timestamp".equals( field.getDataType() ) ) )
			strb.append("<img onclick=\"WdatePicker({el:'"+prefix+"_value',errDealMode:2,dateFmt:'yyyy-MM-dd HH:mm:ss'})\" src=\"/lib/My97DatePicker/skin/datePicker.gif\" width=\"16\" height=\"22\" align=\"absmiddle\" >");
			//strb.append( "<img onClick=\"displayCalendar( '" + prefix + "', event );event.cancelBubble=true;return false;\" src=\"/images/xinjian.gif\"/>" );

		strb.append("</td>");

		strb.append("</tr>");

		strb.append("</table>");

		strb.append("</div>");

		return strb.toString();
	}

	protected String getWhereFieldSelectionHTML(String prefix, CommonField field, Set<CommonOption> optionSet, Set<Object> whereFieldSet)
	{
		String method = "";
		String values = "";
		String condition = "";
		boolean isAdvance = false;
		boolean isAll = false;

		if(whereFieldSet != null)
		{
			for(Object obj : whereFieldSet)
			{
				if(obj instanceof String)
				{
					String tempCondition = (String)obj;
					if(tempCondition.equals("and") || tempCondition.equals("or"))
					{
						condition = tempCondition;
					}

					isAdvance = true;
				}
				else if(obj instanceof CommonField)
				{
					CommonField tempField = (CommonField)obj;

					method = tempField.getMethod();

					String[] valueArray = new String[0];
					if(tempField.getValue() != null && !tempField.getValue().trim().equals(""))
					{
						valueArray = tempField.getValue().trim().split("\\,");
					}

					for(String value : valueArray)
					{
						values += "[" + value + "]";
					}

					if(valueArray.length > 1)
					{
						isAdvance = true;
					}

					isAll = tempField.isAll();
				}
			}
		}

		if(isAll)
		{
			isAdvance = false;
			values = "[[all]]";
		}

		if(condition.equals("and"))
		{
			if(method.equals("like"))
			{
				method = "=";
			}
			else if(method.equals("not like"))
			{
				method = "!=";
			}
		}

		StringBuffer strb = new StringBuffer(64);

		strb.append("<div id=\"").append(prefix).append("_where\"");
		strb.append(" fieldId=\"").append(field.getId()).append("\"");
		strb.append(" fieldName=\"").append(field.getName()).append("\"");
		strb.append(" type=\"").append(field.getType()).append("\"");
		if(field.getDataType() != null)
			strb.append(" dataType=\"").append(field.getDataType()).append("\"");
		strb.append(">");

		strb.append("<table width=\"100%\">");

		strb.append("<tr>");

		strb.append("<td width=\"116px\" noWrap>").append(field.getName()).append(":<br>");
		strb.append("<span id=\"").append(prefix).append("_span\" style=\"color:blue;cursor:pointer;width:70px\" onClick=\"changeOperateType_selection('").append(prefix).append("')\">");
		strb.append(isAdvance ? "普通设置" : "高级设置");
		strb.append("</span>");
		strb.append("</td>");

		strb.append("<td>");
		strb.append("<select id=\"").append(prefix).append("_method\" style=\"width:8em\" onChange=\"setXMLResult('").append(prefix).append("',false)\">");
		strb.append("<option></option>");
		if(field.getDataType() != null && (field.getDataType().equals("multiple") || field.getDataType().equals("file")))
		{
			if(isAll)
			{
				method = "=";
			}

			strb.append("<option value=\"=\"").append(method.equals("=") ? " selected" : "").append(">=</option>");
			strb.append("<option value=\"!=\"").append(method.equals("!=") ? " selected" : "").append(">!=</option>");
			strb.append("<option value=\"like\"").append(method.equals("like") ? " selected" : "").append(">like</option>");
			strb.append("<option value=\"not like\"").append(method.equals("not like") ? " selected" : "").append(">not like</option>");
		}
		else
		{
			if(isAll)
			{
				method = "in";
			}

			strb.append("<option value=\"in\"").append(method.equals("in") ? " selected" : "").append(">in</option>");
			strb.append("<option value=\"not in\"").append(method.equals("not in") ? " selected" : "").append(">not in</option>");
		}
		strb.append("<option value=\"is null\"").append(method.equals("is null") ? " selected" : "").append(">is null</option>");
		strb.append("<option value=\"is not null\"").append(method.equals("is not null") ? " selected" : "").append(">is not null</option>");
		strb.append("</select>");
		strb.append("</td>");


		strb.append("<td>");
		strb.append("<select id=\"").append(prefix).append("_value\" style=\"width:16em;\" onChange=\"setXMLResult('").append(prefix).append("',false)\"").append(isAdvance ? " size=\"5\" multiple" : "").append(">");
		strb.append("<option></option>");
		if(optionSet != null)
		{
			for(CommonOption option : optionSet)
			{
				strb.append("<option value=\"").append(option.getId()).append("\"").append(values.indexOf("[" + option.getId() + "]") != -1 ? " selected" : "").append(">").append(XMLUtil.toSafeXMLString(option.getName())).append("</option>");
			}
		}
		if(!isAdvance)
			strb.append("<option value=\"[all]\"").append(values.indexOf("[[all]]") != -1 ? " selected" : "").append(">[all]</option>");
		strb.append("</select>");
		strb.append("</td>");

		strb.append("</tr>");

		strb.append("</table>");

		strb.append("</div>");

		return strb.toString();
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
				{
					continue;
				}

				CommonField field = allFieldMap.get(fieldId);

				strb.append("<option value=\"").append(fieldId).append("\"");
				strb.append(" type=\"").append(field.getType()).append("\"");
				if(field.getDataType() != null)
				{
					strb.append(" dataType=\"").append(field.getDataType()).append("\"");
				}
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
				{
					strb.append(" dataType=\"").append(field.getDataType()).append("\"");
				}
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

		strb.append("<table width=\"100%\">");

		strb.append("<tr>");

		//deal with rest fields
		strb.append("<td class=\"tdNoBottom\">");
		strb.append("<select class=\"noSearch\" id=\"").append(prefix).append("_restOrderSelect\" size=\"10\" style=\"width:20em\" multiple>");
		if(allFieldMap != null)
		{
			for(String fieldId : allFieldMap.keySet())
			{
				if(selectedFieldMap != null && selectedFieldMap.containsKey(fieldId))
				{
					continue;
				}

				CommonField field = allFieldMap.get(fieldId);

				strb.append("<option value=\"").append(fieldId).append("\"");
				strb.append(" type=\"").append(field.getType()).append("\"");
				if(field.getDataType() != null)
				{
					strb.append(" dataType=\"").append(field.getDataType()).append("\"");
				}
				strb.append(">");
				strb.append(XMLUtil.toSafeXMLString(field.getName()));
				strb.append("</option>");
			}
		}
		strb.append("</select>");
		strb.append("</td>");

		//deal with operate buttons
		strb.append("<td class=\"tdNoBottom\">");
		strb.append("<table width=\"100%\" cellpadding='5'>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<input type=\"button\" class=\"btn btn-primary\" value=\"升序添加\" onclick=\"orderOperation('").append(prefix).append("_restOrderSelect','").append(prefix).append("_selectedOrderSelect',1)\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<input type=\"button\" class=\"btn btn-primary\" value=\"降序添加\" onclick=\"orderOperation('").append(prefix).append("_restOrderSelect','").append(prefix).append("_selectedOrderSelect',2)\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<input type=\"button\" class=\"btn btn-danger\" value=\"移    除\" onclick=\"orderOperation('").append(prefix).append("_selectedOrderSelect','").append(prefix).append("_restOrderSelect',3)\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("</table>");
		strb.append("</td>");

		//deal with selected fields
		strb.append("<td class=\"tdNoBottom\">");
		strb.append("<select class=\"noSearch\" id=\"").append(prefix).append("_selectedOrderSelect\" size=\"10\" style=\"width:20em\" multiple>");
		if(selectedFieldMap != null)
		{
			for(String fieldId : selectedFieldMap.keySet())
			{
				CommonField field = selectedFieldMap.get(fieldId);

				strb.append("<option value=\"").append(fieldId).append("\"");
				strb.append(" type=\"").append(field.getType()).append("\"");
				if(field.getDataType() != null)
				{
					strb.append(" dataType=\"").append(field.getDataType()).append("\"");
				}
				strb.append(" desc=\"" + field.isDesc() + "\">");
				strb.append(XMLUtil.toSafeXMLString(field.getName())).append("(").append(field.isDesc() ? "降序" : "升序").append(")");
				strb.append("</option>");
			}
		}
		strb.append("</select>");
		strb.append("</td>");

		//deal with control buttons
		strb.append("<td class=\"tdNoBottom\">");
		strb.append("<table width=\"100%\" cellpadding='5'>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\" colspan=\"1\">");
		strb.append("<input type=\"button\" class=\"btn\" value=\"上    移\" onclick=\"orderMove('").append(prefix).append("_selectedOrderSelect',true)\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\" colspan=\"1\">");
		strb.append("<input type=\"button\" class=\"btn\" value=\"下    移\" onclick=\"orderMove('").append(prefix).append("_selectedOrderSelect',false)\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("分组字段数：");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("<tr>");
		strb.append("<td align=\"center\" class=\"tdNoBottom\">");
		strb.append("<input id=\"").append(prefix).append("_group\" style='width:50px;' value=\"").append(indent).append("\"/>");
		strb.append("</td>");
		strb.append("</tr>");
		strb.append("</table>");
		strb.append("</td>");

		strb.append("</tr>");

		strb.append("</table>");

		strb.append("</div>");

		return strb.toString();
	}

	protected Map<String, Set<Object>> getWhereFieldMap(Node whereNode)
	{
		
		Map<String, Set<Object>> whereFieldMap = new LinkedHashMap<String, Set<Object>>();
		List<Node> fieldNodeList = new LinkedList<Node>();
		for(int i = 0; i < whereNode.getChildNodes().getLength(); i++)
		{
			Node fieldNode = whereNode.getChildNodes().item(i);
			if(fieldNode.getNodeName().equals("field") || fieldNode.getNodeName().equals("condition"))
			{
				fieldNodeList.add(fieldNode);
			}
		}

		for(int i = 0; i < fieldNodeList.size(); i++)
		{
			
			Node fieldNode = fieldNodeList.get(i);

			if(fieldNode.getNodeName().equals("field"))
			{
				Set<Object> objSet = new LinkedHashSet<Object>();

				objSet.add(getCommonField(fieldNode));
				whereFieldMap.put(XMLUtil.getAttribute(fieldNode, "id"), objSet);
			}
			else if(fieldNode.getNodeName().equals("condition") && fieldNode.getTextContent().equals("("))
			{
				Set<Object> objSet = new LinkedHashSet<Object>();
				objSet.add("(");

				//是一个括号，则需要扫描一遍，检查是多选或者是单选或者是高级搜索
				i++;
				if(i == fieldNodeList.size())
					return null;

				fieldNode = fieldNodeList.get(i);
				String fieldId = XMLUtil.getAttribute(fieldNode, "id");
				String dataType = XMLUtil.getAttribute(fieldNode, "dataType");
				
				//创建人，指派人，指派类型，或者是多选的selection或者reference
				if(fieldNode.getNodeName().equals("field")
						&& (fieldId.equals("create_user") || fieldId.equals("assign_user") || fieldId.equals("log_create_user")
								|| fieldId.equals("assign_type") || fieldId.equals("status_id")
								|| dataType != null && (dataType.equals("multiple") || dataType.equals("file"))))
				{
					//一直找到下一个右括号为止
					for(int j = i; j < fieldNodeList.size(); j++)
					{
						fieldNode = fieldNodeList.get(j);

						if(fieldNode.getNodeName().equals("field"))
						{
							objSet.add(getCommonField(fieldNode));
						}
						else if(fieldNode.getTextContent().equals("and") || fieldNode.getTextContent().equals("or"))
						{
							objSet.add(fieldNode.getTextContent());
						}
						else if(fieldNode.getTextContent().equals(")"))
						{
							objSet.add(")");
							whereFieldMap.put(fieldId, objSet);

							i = j;
							break;
						}
					}
				}
				else
				{
					// 是input的高级搜索
					// 记算括号数量，以此来匹配
					int bracketNum = 1;

					//获取域ID
					for(int j = i; j < fieldNodeList.size(); j++)
					{
						fieldNode = fieldNodeList.get(j);

						if(fieldNode.getNodeName().equals("field"))
						{
							objSet.add(getCommonField(fieldNode));
						}
						else
						{
							objSet.add(fieldNode.getTextContent());
						}

						if(fieldNode.getNodeName().equals("field"))
							continue;

						if(fieldNode.getTextContent().equals(")"))
							bracketNum--;
						else if(fieldNode.getTextContent().equals("("))
							bracketNum++;

						if(bracketNum == 0)
						{
							i = j;
							break;
						}
					}

					whereFieldMap.put(fieldId, objSet);
				}
			}
		}

		return whereFieldMap;
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
		String type = XMLUtil.getAttribute(fieldNode, "type");
		if(type != null)
			field.setType(type.trim());
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
		boolean desc = false;
		String descStr = XMLUtil.getAttribute(fieldNode, "desc");
		if(descStr != null)
			desc = Boolean.parseBoolean(descStr);
		field.setDesc(desc);

		if(field.getName().equals("title"))
			field.setName("标题");
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
	}


	protected String[] queryFlowUsersByTemplateId(DataAccessSession das,UUID templateId){
		Template template = das.queryTemplate(templateId);
		Flow flow = das.queryFlow(template.getFlowId());
		String[] users = flow.queryNodeUsers(template.getId());
		return users;
	}%>