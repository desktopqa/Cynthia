<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager.ErrorType"%>
<%@page import="com.sogou.qadev.service.cynthia.service.ErrorManager"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Right"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.FieldRow" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.FieldColumn" %>
<%@page import="com.sogou.qadev.service.cynthia.dao.FieldNameAccessSessionMySQL"%>
<%@page import="com.sogou.qadev.cache.impl.FieldNameCache"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.FieldNameMapMySQL"%>


<%@ page import="java.util.*" %>

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
	
	String name = request.getParameter("name");
	
	String templateTypeIdStr = request.getParameter("templateTypeId");
	if(templateTypeIdStr == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.param_error));
		return;
	}
	
	UUID templateTypeId = DataAccessFactory.getInstance().createUUID(templateTypeIdStr);
	
	String flowIdStr = request.getParameter("flowId");
	if(flowIdStr == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.param_error));
		return;
	}
	
	UUID flowId = DataAccessFactory.getInstance().createUUID(flowIdStr);
	
	UUID copyTemplateId = null;
	
	String copyTemplateIdStr = request.getParameter("copyTemplateId");
	if(copyTemplateIdStr != null)
		copyTemplateId = DataAccessFactory.getInstance().createUUID(copyTemplateIdStr);
	
	String copyUserRightStr = request.getParameter("copyUserRight");
	boolean isCopyUserRight = false;	
	if(copyUserRightStr != null)
		isCopyUserRight = copyUserRightStr.equals("true") ? true : false;	
	
	Template template = das.createTemplate(templateTypeId);
	UUID templateId   = template.getId();
	if(template == null)
	{
		out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
		return;
	}
	boolean isSuccess = true;
	
	template.setName(name);
	template.setFlowId(flowId);
	template.setCreateUser(key.getUsername());
	template.getTemplateConfig().setIsProjectInvolve(request.getParameter("projectInvolved") != null && request.getParameter("projectInvolved").equals("true"));
	
	if(template.getTemplateConfig().isProjectInvolve()){
		//项目表单初始化 三列
		template.addFieldRow(0, 3);
		//添加字段 对应产品 对应项目
		Field field = template.addField(Field.Type.t_selection, Field.DataType.dt_single);
		template.addField(field, 0, 0, 0);
		field.setName("对应产品");
		String fieldColName = FieldNameMapMySQL.getInstance().getOneFieldName(field, template.getId().getValue());
		das.addFieldColName(template.getId().getValue(), fieldColName, field.getId().getValue(),  FieldNameMapMySQL.getInstance().getFieldColNameType(field));
		template.getTemplateConfig().setProductInvolveId(field.getId().getValue());
		
		Field field2 = template.addField(Field.Type.t_selection, Field.DataType.dt_single);
		template.addField(field2, 0, 1, 0);
		field2.setName("对应项目");
		String fieldColName2 = FieldNameMapMySQL.getInstance().getOneFieldName(field2, template.getId().getValue());
		das.addFieldColName(template.getId().getValue(), fieldColName2, field2.getId().getValue(),  FieldNameMapMySQL.getInstance().getFieldColNameType(field2));
		template.getTemplateConfig().setProjectInvolveId(field2.getId().getValue());
	}
	
	if(copyTemplateId != null)
	{
		Template copyTemplate = das.queryTemplate(copyTemplateId);
		if(copyTemplate == null)
		{
			out.println(ErrorManager.getErrorXml(ErrorType.param_error));
			return;
		}
		
		if(isCopyUserRight)
		{
			//添加复制表单人员信息
			Flow flow = das.queryFlow(copyTemplate.getFlowId());
			if(flow == null){
				out.println(ErrorManager.getErrorXml(ErrorType.flow_not_found));
				return;
			}
			
			Set<Right> allAddRigthSet = new HashSet<Right>();
			for(Right right : flow.getRightSet()){
				if(right.getTemplateId() != null && right.getTemplateId().getValue().equals(copyTemplateId.getValue()))
					allAddRigthSet.add(new Right(right.getUsername(), template.getId(), right.getRoleId(),right.getNickname()));
			}
			for(Right right : allAddRigthSet)
				flow.addRight(right);
			
			//更新流程
			ErrorCode errorCode = das.updateFlow(flow);
			if(errorCode.equals(ErrorCode.success)){
				das.updateCache(DataAccessAction.update, flow.getId().getValue(),flow);
			}else{
				out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
			}
		}
		
		template.setFlowId(copyTemplate.getFlowId());
		
		List<FieldRow> fieldRowList = copyTemplate.getFieldRowList();
		List<FieldRow> newFieldRowList = new ArrayList<FieldRow>();
		
		for(int i = 0; i < fieldRowList.size(); i++)
		{
			FieldRow fieldRow = new FieldRow();
			List<FieldColumn> fieldColumnList = fieldRowList.get(i).getFieldColumns();
			for(int j = 0; j < fieldColumnList.size(); j++)
			{
				List<Field> fieldList = fieldColumnList.get(j).getFields();
				FieldColumn fieldColumn = new FieldColumn();
				
				for(int k = 0; k < fieldList.size(); k++)
				{
					Field copyField = fieldList.get(k);
					Field field = template.addField(copyField.getType(), copyField.getDataType());
					
					field.setName(copyField.getName());
					field.setDescription(copyField.getDescription());
				
					Set<Option> allOptions = copyField.getOptions();
					if(allOptions != null)
					{
						for(Option copyOption : allOptions)
						{
							Option option = field.addOption();
							option.setName(copyOption.getName());
							option.setDescription(copyOption.getDescription());
							option.setForbidden(copyOption.getForbidden());
							option.setIndexOrder(copyOption.getIndexOrder());
						}
					}
					
					field.setActionIds(copyField.getActionIds());
					field.setControlActionIds(copyField.getControlActionIds());
					field.setControlRoleIds(copyField.getControlRoleIds());
					
					fieldColumn.addField(field);
					
					//复制控制字段
					if(copyField.getControlFieldId() != null){
						Field beforeControlField = copyTemplate.getField(copyField.getControlFieldId());
						if(beforeControlField != null){
							
							Field newControlField = template.getField(beforeControlField.getName());
							field.setControlFieldId(newControlField.getId());
							
							for(Option newOption:field.getOptions()){
								try{
									Option beforeControlOption = beforeControlField.getOption(copyField.getOption(newOption.getName()).getControlOptionId());
									newOption.setControlOptionId(newControlField.getOption(beforeControlOption.getName()).getId());
								}catch(Exception e){}
							}
						}
					}
					
					String fieldColName = FieldNameMapMySQL.getInstance().getOneFieldName(field, template.getId().getValue());
					if(fieldColName == null || fieldColName.length() == 0 )
					{
						isSuccess = false;
						out.println(ErrorManager.getErrorXml(ErrorType.fieldcolName_error));
						return;
					}
					else{
						if(new FieldNameAccessSessionMySQL().addFieldColName(template.getId().getValue(), fieldColName, field.getId().getValue(),  FieldNameMapMySQL.getInstance().getFieldColNameType(field))){
							FieldNameCache.getInstance().set(field.getId().getValue(), fieldColName);
						}else{
							isSuccess = false;
							out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
							return;
						}
					}
				}
				fieldRow.addColumn(fieldColumn);
			}
			newFieldRowList.add(fieldRow);
		}
		template.setFieldRowList(newFieldRowList);
	}
	
	ErrorCode errorCode = das.updateTemplate(template);
	if(errorCode.equals(ErrorCode.success)&&isSuccess){
		das.updateCache(DataAccessAction.insert, template.getId().getValue(),template);
		//创建表单人员添加权限
		das.addUserTemplateRight(new String[]{template.getId().getValue()}, key.getUsername());
		out.println(ErrorManager.getCorrectXml());
	}else{
		out.println(ErrorManager.getErrorXml(ErrorType.database_update_error));
	}
%>