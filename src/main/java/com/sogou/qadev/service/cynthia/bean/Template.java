package com.sogou.qadev.service.cynthia.bean;

import java.util.List;
import java.util.Set;

/**
 * @description:template interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:28:11
 * @version:v1.0
 */
public interface Template extends BaseType{
	
	/**
	 * @description:set template id
	 * @date:2014-5-6 下午4:28:22
	 * @version:v1.0
	 * @param id
	 */
	public void setId(UUID id);
	
	/**
	 * @Title: getTemplateConfig
	 * @Description: TODO
	 * @return
	 * @return: TemplateConfig
	 */
	public TemplateConfig getTemplateConfig();
	
	/**
	 * @description:get template templatetype id
	 * @date:2014-5-6 下午4:28:38
	 * @version:v1.0
	 * @return
	 */
	public UUID getTemplateTypeId();
	
	/**
	 * @description:get template create user
	 * @date:2014-5-6 下午4:28:48
	 * @version:v1.0
	 * @return
	 */
	public String getCreateUser();
	
	/**
	 * @description:set template create user
	 * @date:2014-5-6 下午4:28:57
	 * @version:v1.0
	 * @param createUser
	 */
	public void setCreateUser(String createUser);
	
	/**
	 * @description:get template flow id
	 * @date:2014-5-6 下午4:29:08
	 * @version:v1.0
	 * @return
	 */
	public UUID getFlowId();
	
	/**
	 * @description:set template flow id
	 * @date:2014-5-6 下午4:29:18
	 * @version:v1.0
	 * @param flowId
	 */
	public void setFlowId(UUID flowId);
	
	/**
	 * @description:get template name
	 * @date:2014-5-6 下午4:29:27
	 * @version:v1.0
	 * @return
	 */
	public String getName();
	
	/**
	 * @description:set template name
	 * @date:2014-5-6 下午4:29:35
	 * @version:v1.0
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * @description:get template description
	 * @date:2014-5-6 下午4:29:44
	 * @version:v1.0
	 * @return
	 */
	public String getDescription();
	
	/**
	 * @description:set template description
	 * @date:2014-5-6 下午4:29:55
	 * @version:v1.0
	 * @param description
	 */
	public void setDescription(String description);
	
	/**
	 * @description:get field from template by field id
	 * @date:2014-5-6 下午4:30:04
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public Field getField(UUID id);
	
	/**
	 * @description:get field from template by field name(not recommended!!)
	 * @date:2014-5-6 下午4:30:18
	 * @version:v1.0
	 * @param name
	 * @return
	 */
	public Field getField(String name);
	
	/**
	 * @description:add field to template
	 * @date:2014-5-6 下午4:30:50
	 * @version:v1.0
	 * @param type
	 * @param dataType
	 * @return
	 */
	public Field addField(Field.Type type, Field.DataType dataType);
	
	/**
	 * @description:remove field from template
	 * @date:2014-5-6 下午4:31:00
	 * @version:v1.0
	 * @param id
	 * @param templateId
	 */
	public void removeField(UUID id , UUID templateId);
	
	/**
	 * @description:get all fields from template
	 * @date:2014-5-6 下午4:31:14
	 * @version:v1.0
	 * @return
	 */
	public Set<Field> getFields();
	
	/**
	 * @description:set template fields
	 * @date:2014-5-6 下午4:31:25
	 * @version:v1.0
	 * @param fieldSet
	 */
	public void setFields(Set<Field> fieldSet);
	
	/**
	 * @description:add field to template
	 * @date:2014-5-6 下午4:31:40
	 * @version:v1.0
	 * @param field
	 * @param rowIndex
	 * @param columnIndex
	 * @param positionIndex
	 */
	public void addField(Field field, int rowIndex, int columnIndex, int positionIndex);
	
	/**
	 * @description:move field to new position
	 * @date:2014-5-6 下午4:32:02
	 * @version:v1.0
	 * @param field
	 * @param rowIndex
	 * @param columnIndex
	 * @param positionIndex:index in column
	 * @return
	 */
	public boolean moveField(Field field, int rowIndex, int columnIndex, int positionIndex);

	/**
	 * @description:add field row
	 * @date:2014-5-6 下午4:32:57
	 * @version:v1.0
	 * @param rowIndex
	 * @param columnCount
	 */
	public void addFieldRow(int rowIndex, int columnCount);
	
	/**
	 * @description:remove field row from template
	 * @date:2014-5-6 下午4:33:25
	 * @version:v1.0
	 * @param rowIndex
	 */
	public void removeFieldRow(int rowIndex);
	
	/**
	 * @description:get all field rows
	 * @date:2014-5-6 下午4:33:39
	 * @version:v1.0
	 * @return
	 */
	public List<FieldRow> getFieldRowList();
	
	/**
	 * @description:set field rows of template
	 * @date:2014-5-6 下午4:33:48
	 * @version:v1.0
	 * @param fieldRowList
	 */
	public void setFieldRowList(List<FieldRow> fieldRowList);
	
	/**
	 * @Title: setTemplateMailOption
	 * @Description: TODO
	 * @param tmo
	 * @return: void
	 */
	public void setTemplateMailOption(TemplateMailOption tmo);
	
	/**
	 * @Title: getTemplateMailOption
	 * @Description: TODO
	 * @return
	 * @return: TemplateMailOption
	 */
	public TemplateMailOption getTemplateMailOption();
}
