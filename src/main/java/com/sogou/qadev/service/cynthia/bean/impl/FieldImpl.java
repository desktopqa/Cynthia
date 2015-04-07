package com.sogou.qadev.service.cynthia.bean.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import bsh.This;

import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Option;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.ConfigManager;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.ProjectInvolveManager;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

/**
 * @description:field implements
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午3:42:05
 * @version:v1.0
 */
public final class FieldImpl implements Field
{
	/**
	 * @Fields:serialVersionUID
	 * @Fields_Type:long
	 * @description:TODO
	 * @date:2014-5-6 下午3:31:23
	 */
	private static final long serialVersionUID = -804631112737784632L;
	
	private UUID id = null;
	private String name = null;
	private String description = null;
	private String defaultValue = null;
	private String fieldTip = null;
	private String fieldSize = null;
	private UUID templateId = null;
	private UUID controlFieldId = null;
	private Hidden hidden = null;
	private Type type = null;
	private DataType dataType = null;
	private String timestampFormat = "yyyy-MM-dd HH:mm:ss";   //时间类型字段精度格式
	private boolean dateCurTime = false;
	private Set<Option> options = new LinkedHashSet<Option>();
	private Set<UUID> controlOptionIds = new LinkedHashSet<UUID>();
	private Set<String> controlRoleIds = new LinkedHashSet<String>();
	private Set<String> controlActionIds = new LinkedHashSet<String>();
	private Set<UUID> actionIds = new LinkedHashSet<UUID>();
	private UUID controlHiddenFieldId = null;
	private Set<UUID> controlHiddenFieldsIds =  new LinkedHashSet<UUID>();
	private Set<UUID> controlHiddenStatesIds =  new LinkedHashSet<UUID>();

	/**
	 * <h1> Title:</h1>
	 * <p> Description: init field</p>
	 * @date：2014-5-6 
	 * @param id
	 * @param templateId
	 * @param type
	 * @param dataType
	 */
	public FieldImpl(UUID id, UUID templateId, Type type, DataType dataType)
	{
		this.id = id;
		this.templateId = templateId;
		this.type = type;
		this.dataType = dataType;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:clone</p>
	 * @return
	 * @see java.lang.Object#clone()
	 */
	public Field clone()
	{
		FieldImpl fieldImpl = new FieldImpl(this.id, this.templateId, this.type, this.dataType);
		fieldImpl.name = this.name;
		fieldImpl.timestampFormat = this.timestampFormat;
		fieldImpl.fieldTip = this.fieldTip;
		fieldImpl.fieldSize = this.fieldSize;
		fieldImpl.description = this.description;
		fieldImpl.defaultValue = this.defaultValue;
		fieldImpl.templateId = this.templateId;
		fieldImpl.controlFieldId = this.controlFieldId;
		fieldImpl.hidden = this.hidden;
		fieldImpl.dateCurTime = this.dateCurTime;

		for (Option option : this.options){
			fieldImpl.options.add((Option) option.clone());
		}

		fieldImpl.controlOptionIds.addAll(this.controlOptionIds);
		fieldImpl.controlRoleIds.addAll(this.controlRoleIds);
		fieldImpl.controlActionIds.addAll(this.controlActionIds);
		fieldImpl.actionIds.addAll(this.actionIds);
		fieldImpl.controlHiddenFieldId = this.controlHiddenFieldId;
		fieldImpl.controlHiddenFieldsIds.addAll(this.controlHiddenFieldsIds);
		fieldImpl.controlHiddenStatesIds.addAll(this.controlHiddenStatesIds);

		return fieldImpl;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getFieldTip</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getFieldTip()
	 */
	public String getFieldTip() {
		return this.fieldTip;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setFieldTip</p>
	 * @param fieldTip
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setFieldTip(java.lang.String)
	 */
	public void setFieldTip(String fieldTip) {
		this.fieldTip = fieldTip;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getFieldSize</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getFieldSize()
	 */
	public String getFieldSize() {
		return this.fieldSize;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setFieldSize</p>
	 * @param fieldSize
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setFieldSize(java.lang.String)
	 */
	public void setFieldSize(String fieldSize) {
		this.fieldSize = fieldSize;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getControlHiddenFieldId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getControlHiddenFieldId()
	 */
	public UUID getControlHiddenFieldId() {
		return controlHiddenFieldId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setControlHiddenFieldId</p>
	 * @param controlHiddenFieldId
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setControlHiddenFieldId(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void setControlHiddenFieldId(UUID controlHiddenFieldId) {
		this.controlHiddenFieldId = controlHiddenFieldId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getControlHiddenFieldsIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getControlHiddenFieldsIds()
	 */
	public Set<UUID> getControlHiddenFieldsIds() {
		return controlHiddenFieldsIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setControlHiddenFieldsIds</p>
	 * @param controlHiddenFieldsIds
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setControlHiddenFieldsIds(java.util.Set)
	 */
	public void setControlHiddenFieldsIds(Set<UUID> controlHiddenFieldsIds) {
		this.controlHiddenFieldsIds = controlHiddenFieldsIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getControlHiddenStatesIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getControlHiddenStatesIds()
	 */
	public Set<UUID> getControlHiddenStatesIds() {
		return controlHiddenStatesIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setControlHiddenStatesIds</p>
	 * @param controlHiddenStatesIds
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setControlHiddenStatesIds(java.util.Set)
	 */
	public void setControlHiddenStatesIds(Set<UUID> controlHiddenStatesIds) {
		this.controlHiddenStatesIds = controlHiddenStatesIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getControlFieldId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getControlFieldId()
	 */
	public UUID getControlFieldId()
	{
		return this.controlFieldId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setControlFieldId</p>
	 * @param controlFieldId
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setControlFieldId(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void setControlFieldId(UUID controlFieldId)
	{
		this.controlFieldId = controlFieldId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getDataType</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getDataType()
	 */
	public DataType getDataType()
	{
		return this.dataType;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getDefaultValue</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getDefaultValue()
	 */
	public String getDefaultValue()
	{
		return this.defaultValue;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setDefaultValue</p>
	 * @param defaultValue
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setDefaultValue(java.lang.String)
	 */
	public void setDefaultValue(String defaultValue)
	{
		this.defaultValue = defaultValue;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getDescription</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getDescription()
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setDescription</p>
	 * @param description
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setDescription(java.lang.String)
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getHidden</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getHidden()
	 */
	public Hidden getHidden()
	{
		return this.hidden;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setHidden</p>
	 * @param hidden
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setHidden(com.sogou.qadev.service.cynthia.bean.Field.Hidden)
	 */
	public void setHidden(Hidden hidden)
	{
		this.hidden = hidden;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getName</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getName()
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setName</p>
	 * @param name
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getTemplateId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getTemplateId()
	 */
	public UUID getTemplateId()
	{
		return this.templateId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getType</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getType()
	 */
	public Type getType()
	{
		return this.type;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getId()
	 */
	public UUID getId()
	{
		return this.id;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:addOption</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#addOption()
	 */
	public Option addOption()
	{
		if (this.type.equals(Field.Type.t_selection))
		{
			UUID optionId = DataAccessFactory.getInstance().newUUID("OPTI");
			Option option = new OptionImpl(optionId, getId());

			this.options.add(option);

			return option;
		}

		return null;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getOptions</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getOptions()
	 */
	public Set<Option> getOptions()
	{
		if (ConfigManager.getProjectInvolved()) {
			Template template = DataAccessFactory.getInstance().getSysDas().queryTemplate(this.templateId);
			if (template.getTemplateConfig().isProjectInvolve()) {
				Set<Option> allOptions = new HashSet<Option>();
				if (this.getId().getValue().equals(template.getTemplateConfig().getProductInvolveId())) {
					Map<String, String> allProductsMap = ProjectInvolveManager.getInstance().getProductMap(template.getCreateUser());
					System.out.println("find projects length:" + allProductsMap.keySet().size());
					for (String productId : allProductsMap.keySet()) {
						allOptions.add(new OptionImpl(DataAccessFactory.getInstance().createUUID(productId), this.id, allProductsMap.get(productId)));
					}
					return allOptions;
				}
				else if(this.getId().getValue().equals(template.getTemplateConfig().getProjectInvolveId())){
					Map<String, String> allProjectsMap = ProjectInvolveManager.getInstance().getProjectMap(template.getCreateUser(),"");
					for (String projectId : allProjectsMap.keySet()) {
						allOptions.add(new OptionImpl(DataAccessFactory.getInstance().createUUID(projectId), this.id, allProjectsMap.get(projectId)));
					}
					return allOptions;
				}
			}
		}
		return this.options;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setOptions</p>
	 * @param options
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setOptions(java.util.Set)
	 */
	public void setOptions(Set<Option> options)
	{
		this.options = options;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getControlOptionIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getControlOptionIds()
	 */
	public Set<UUID> getControlOptionIds()
	{
		return this.controlOptionIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getControlRoleIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getControlRoleIds()
	 */
	public Set<String> getControlRoleIds()
	{
		return this.controlRoleIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getControlActionIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getControlActionIds()
	 */
	public Set<String> getControlActionIds()
	{
		return this.controlActionIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getActionIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getActionIds()
	 */
	public Set<UUID> getActionIds()
	{
		return this.actionIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setControlOptionIds</p>
	 * @param controlOptionIds
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setControlOptionIds(java.util.Set)
	 */
	public void setControlOptionIds(Set<UUID> controlOptionIds)
	{
		this.controlOptionIds = controlOptionIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setControlRoleIds</p>
	 * @param controlRoleIds
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setControlRoleIds(java.util.Set)
	 */
	public void setControlRoleIds(Set<String> controlRoleIds)
	{
		this.controlRoleIds = controlRoleIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setControlActionIds</p>
	 * @param controlActionIds
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setControlActionIds(java.util.Set)
	 */
	public void setControlActionIds(Set<String> controlActionIds)
	{
		this.controlActionIds = controlActionIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setActionIds</p>
	 * @param actionIds
	 * @see com.sogou.qadev.service.cynthia.bean.Field#setActionIds(java.util.Set)
	 */
	public void setActionIds(Set<UUID> actionIds)
	{
		this.actionIds = actionIds;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getOption</p>
	 * @param name
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getOption(java.lang.String)
	 */
	public Option getOption(String name)
	{
		if (this.type.equals(Field.Type.t_selection))
		{
			for (Option option : this.options)
			{
				if (option.getName().equals(name))
					return option;
			}
		}

		return null;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getOption</p>
	 * @param id
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#getOption(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Option getOption(UUID id)
	{
		Option foption = null;
		
		if (this.type.equals(Field.Type.t_selection))
		{
			if (ConfigManager.getEnableSso()) {
				DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
				Template template = das.queryTemplate(this.templateId);
				if (template.getTemplateConfig().isProjectInvolve()) {
					String name = null;
					if (this.getId().getValue().equals(template.getTemplateConfig().getProductInvolveId())) {
						name = ProjectInvolveManager.getInstance().getProjectNameById(id.getValue());
					}else if (this.getId().getValue().equals(template.getTemplateConfig().getProjectInvolveId())) {
						name = ProjectInvolveManager.getInstance().getProductNameById(id.getValue());
					}
					if (name != null) {
						foption = new OptionImpl(id, this.id,name);
					}
				}
			}
			
			if (foption != null) {
				return foption;
			}else {
				for (Option option : this.options){
					if (option.getId().equals(id))
						return option;
				}
			}
		}

		return null;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:removeOption</p>
	 * @param id
	 * @see com.sogou.qadev.service.cynthia.bean.Field#removeOption(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void removeOption(UUID id)
	{
		if (this.type.equals(Field.Type.t_selection))
		{
			Iterator<Option> optionSetItr = this.options.iterator();
			while(optionSetItr.hasNext())
			{
				Option option = optionSetItr.next();
				if(option.getId().equals(id))
					optionSetItr.remove();
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:toXMLString</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Field#toXMLString()
	 */
	public String toXMLString()
	{
		StringBuffer xmlb = new StringBuffer();
		xmlb.append("<field>");

		xmlb.append("<id>").append(this.getId()).append("</id>");
		xmlb.append("<name>").append(XMLUtil.toSafeXMLString(this.getName())).append("</name>");
		xmlb.append("<description>").append(XMLUtil.toSafeXMLString(this.getDescription())).append("</description>");
		xmlb.append("<timeFormat>").append(XMLUtil.toSafeXMLString(this.getTimestampFormat())).append("</timeFormat>");
		xmlb.append("<dateCurTime>").append(String.valueOf(this.dateCurTime)).append("</dateCurTime>");
		xmlb.append("<fieldTip>").append(XMLUtil.toSafeXMLString(this.getFieldTip())).append("</fieldTip>");
		xmlb.append("<fieldSize>").append(XMLUtil.toSafeXMLString(this.getFieldSize())).append("</fieldSize>");
		xmlb.append("<type>").append(this.getType()).append("</type>");
		xmlb.append("<dataType>").append(this.getDataType() != null ? this.getDataType() : "").append("</dataType>");
		xmlb.append("<controlFieldId>").append(this.getControlFieldId() != null ? this.getControlFieldId() : "")
				.append("</controlFieldId>");
		xmlb.append("<defaultValue>").append(XMLUtil.toSafeXMLString(this.getDefaultValue())).append("</defaultValue>");


		Set<Option> allOptions = this.getOptions();
		
		if (allOptions == null || allOptions.size() == 0)
			xmlb.append("<options/>");
		else
		{
			xmlb.append("<options>");

			Map<Integer, Set<Option>> optionMap = new TreeMap<Integer, Set<Option>>();

			for (Option option : allOptions)
			{
				if (!optionMap.containsKey(option.getIndexOrder()))
					optionMap.put(option.getIndexOrder(), new LinkedHashSet<Option>());

				optionMap.get(option.getIndexOrder()).add(option);
			}

			for (Set<Option> optionSet : optionMap.values())
			{
				for (Option option : optionSet)
				{
					xmlb.append("<option>");

					xmlb.append("<id>").append(option.getId()).append("</id>");
					xmlb.append("<name>").append(XMLUtil.toSafeXMLString(option.getName())).append("</name>");
					xmlb.append("<description>").append(XMLUtil.toSafeXMLString(option.getName())).append("</description>");
					xmlb.append("<controlOptionId>").append(option.getControlOptionId() != null ? option.getControlOptionId() : "").append(
							"</controlOptionId>");
					xmlb.append("<forbidden>").append(option.getForbidden()).append("</forbidden>");
					xmlb.append("<indexOrder>").append(option.getIndexOrder()).append("</indexOrder>");

					xmlb.append("</option>");
				}
			}

			xmlb.append("</options>");
		}

		if (this.getControlOptionIds() == null || this.getControlOptionIds().size() == 0)
			xmlb.append("<controlOptionIds/>");
		else
		{
			xmlb.append("<controlOptionIds>");

			for (UUID controlOptionId : this.getControlOptionIds())
				xmlb.append("<controlOptionId>").append(controlOptionId).append("</controlOptionId>");

			xmlb.append("</controlOptionIds>");
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		try{
			if (this.getControlHiddenFieldId() == null)
				xmlb.append("<controlHiddenFieldId/>");
			else {
				xmlb.append("<controlHiddenFieldId>").append(XMLUtil.toSafeXMLString(this.getControlHiddenFieldId().toString())).append("</controlHiddenFieldId>");
			}

			if (this.getControlHiddenFieldsIds() == null || this.getControlHiddenFieldsIds().size() == 0)
				xmlb.append("<controlHiddenFields/>");
			else {
				xmlb.append("<controlHiddenFields>");

				for (UUID controlHiddenField : this.getControlHiddenFieldsIds()) {
					xmlb.append("<controlHiddenField>").append(controlHiddenField).append("</controlHiddenField>");
				}
				xmlb.append("</controlHiddenFields>");
			}
			if (this.getControlHiddenStatesIds() == null || this.getControlHiddenStatesIds().size() == 0)
				xmlb.append("<controlHiddenStates/>");
			else {
				xmlb.append("<controlHiddenStates>");

				for (UUID controlHiddenState : this.getControlHiddenStatesIds()) {
					xmlb.append("<controlHiddenState>").append(controlHiddenState).append("</controlHiddenState>");
				}
				xmlb.append("</controlHiddenStates>");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		if (this.getControlRoleIds() == null || this.getControlRoleIds().size() == 0)
			xmlb.append("<controlRoleIds/>");
		else
		{
			xmlb.append("<controlRoleIds>");

			for (String controlRoleId : this.getControlRoleIds())
				xmlb.append("<controlRoleId>").append(controlRoleId).append("</controlRoleId>");

			xmlb.append("</controlRoleIds>");
		}

		if (this.getControlActionIds() == null || this.getControlActionIds().size() == 0)
			xmlb.append("<controlActionIds/>");
		else
		{
			xmlb.append("<controlActionIds>");

			for (String controlActionId : this.getControlActionIds())
				xmlb.append("<controlActionId>").append(controlActionId).append("</controlActionId>");

			xmlb.append("</controlActionIds>");
		}

		if (this.getActionIds() == null || this.getActionIds().size() == 0)
			xmlb.append("<actionIds/>");
		else
		{
			xmlb.append("<actionIds>");

			for (UUID actionId : this.getActionIds())
				xmlb.append("<actionId>").append(actionId).append("</actionId>");

			xmlb.append("</actionIds>");
		}

		xmlb.append("</field>");
		return xmlb.toString();
	}

	@Override
	public String getTimestampFormat() {
		return this.timestampFormat;
	}

	@Override
	public void setTimestampFormat(String timestampFormat) {
		this.timestampFormat = timestampFormat;
	}
	
	@Override
	public void setDateCurTime(boolean dateCurTime){
		this.dateCurTime = dateCurTime;
	}
	
	@Override
	public boolean getDateCurTime(){
		return this.dateCurTime;
	}
}
