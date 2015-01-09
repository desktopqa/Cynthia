package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;
import java.util.Set;

/**
 * @description:field interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午3:14:51
 * @version:v1.0
 */
public interface Field extends Serializable{
	
	/**
	 * @description:get field id
	 * @date:2014-5-6 下午3:15:09
	 * @version:v1.0
	 * @return
	 */
	public UUID getId();
	
	/**
	 * @Title: getTimestampFormat
	 * @Description: get time format
	 * @return
	 * @return: String
	 */
	public String getTimestampFormat();

	/**
	 * @description:get field name
	 * @date:2014-5-6 下午3:15:17
	 * @version:v1.0
	 * @return
	 */
	public String getName();
	
	/**
	 * @description:get field show tip
	 * @date:2014-5-6 下午3:15:26
	 * @version:v1.0
	 * @return
	 */
	public String getFieldTip();
	
	/**
	 * @description:set field show tip
	 * @date:2014-5-6 下午3:15:36
	 * @version:v1.0
	 * @param fieldTip
	 */
	public void setFieldTip(String fieldTip);

	/**
	 * @description:get field size
	 * @date:2014-5-6 下午3:15:48
	 * @version:v1.0
	 * @return
	 */
	public String getFieldSize();
	
	/**
	 * @description:set field size
	 * @date:2014-5-6 下午3:16:05
	 * @version:v1.0
	 * @param fieldSize
	 */
	public void setFieldSize(String fieldSize);
	
	/**
	 * @description:get field template id
	 * @date:2014-5-6 下午3:16:23
	 * @version:v1.0
	 * @return
	 */
	public UUID getTemplateId();

	/**
	 * @description:return field type
	 * @date:2014-5-6 下午3:16:33
	 * @version:v1.0
	 * @return
	 */
	public Type getType();

	/**
	 * @description:return field data type
	 * @date:2014-5-6 下午3:16:42
	 * @version:v1.0
	 * @return
	 */
	public DataType getDataType();

	/**
	 * @description:return control field id
	 * @date:2014-5-6 下午3:16:52
	 * @version:v1.0
	 * @return
	 */
	public UUID getControlFieldId();

	/**
	 * @description:set field description
	 * @date:2014-5-6 下午3:18:13
	 * @version:v1.0
	 * @return
	 */
	public String getDescription();

	/**
	 * @description:get field hidden info
	 * @date:2014-5-6 下午3:18:25
	 * @version:v1.0
	 * @return
	 */
	public Hidden getHidden();

	/**
	 * @description:get field default value
	 * @date:2014-5-6 下午3:18:47
	 * @version:v1.0
	 * @return
	 */
	public String getDefaultValue();

	/**
	 * @description:get control hidden field id
	 * @date:2014-5-6 下午3:18:57
	 * @version:v1.0
	 * @return
	 */
	public UUID getControlHiddenFieldId();

	/**
	 * @description:set control hidden field id
	 * @date:2014-5-6 下午3:19:08
	 * @version:v1.0
	 * @param controlHiddenFieldId
	 */
	public void setControlHiddenFieldId(UUID controlHiddenFieldId);

	/**
	 * @description:get control hidden field values
	 * @date:2014-5-6 下午3:19:24
	 * @version:v1.0
	 * @return
	 */
	public Set<UUID> getControlHiddenFieldsIds();

	/**
	 * @description:set control hidden field values
	 * @date:2014-5-6 下午3:19:41
	 * @version:v1.0
	 * @param controlHiddenFieldsIds
	 */
	public void setControlHiddenFieldsIds(Set<UUID> controlHiddenFieldsIds);

	/**
	 * @description:get control hidden status ids
	 * @date:2014-5-6 下午3:19:54
	 * @version:v1.0
	 * @return
	 */
	public Set<UUID> getControlHiddenStatesIds();

	/**
	 * @description:set control hidden status ids
	 * @date:2014-5-6 下午3:20:09
	 * @version:v1.0
	 * @param controlHiddenStatesIds
	 */
	public void setControlHiddenStatesIds(Set<UUID> controlHiddenStatesIds);

	/**
	 * @description:get control option ids
	 * @date:2014-5-6 下午3:20:23
	 * @version:v1.0
	 * @return
	 */
	public Set<UUID> getControlOptionIds();

	/**
	 * @description:get control role ids
	 * @date:2014-5-6 下午3:20:45
	 * @version:v1.0
	 * @return
	 */
	public Set<String> getControlRoleIds();

	/**
	 * @description:get control action ids
	 * @date:2014-5-6 下午3:20:58
	 * @version:v1.0
	 * @return
	 */
	public Set<String> getControlActionIds();

	/**
	 * @description:get actions id
	 * @date:2014-5-6 下午3:21:32
	 * @version:v1.0
	 * @return
	 */
	public Set<UUID> getActionIds();

	/**
	 * @description:get all options id
	 * @date:2014-5-6 下午3:24:22
	 * @version:v1.0
	 * @return
	 */
	public Set<Option> getOptions();

	/**
	 * @description:get option by option id
	 * @date:2014-5-6 下午3:24:33
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public Option getOption(UUID id);

	/**
	 * @description:get option by option name
	 * @date:2014-5-6 下午3:24:43
	 * @version:v1.0
	 * @param name
	 * @return
	 */
	public Option getOption(String name);

	/**
	 * @description:set control field id
	 * @date:2014-5-6 下午3:24:56
	 * @version:v1.0
	 * @param controlFieldId
	 */
	public void setControlFieldId(UUID controlFieldId);

	/**
	 * @description:set field name 
	 * @date:2014-5-6 下午3:25:06
	 * @version:v1.0
	 * @param name
	 */
	public void setName(String name);

	/**
	 * @Title: setTimestampFormat
	 * @Description: set time format
	 * @param timestampFormat
	 * @return: void
	 */
	public void setTimestampFormat(String timestampFormat);
	
	/**
	 * @Title: setDateCurTime
	 * @Description: set default cur time
	 * @param dateCurTime
	 * @return: void
	 */
	public void setDateCurTime(boolean dateCurTime);
	
	/**
	 * @Title: getDateCurTime
	 * @Description: get if date curtime 
	 * @return
	 * @return: boolean
	 */
	public boolean getDateCurTime();
	
	/**
	 * @description:set field description
	 * @date:2014-5-6 下午3:25:18
	 * @version:v1.0
	 * @param descritpion
	 */
	public void setDescription(String descritpion);

	/**
	 * @description:set field hidden
	 * @date:2014-5-6 下午3:25:32
	 * @version:v1.0
	 * @param hidden
	 */
	public void setHidden(Hidden hidden);

	/**
	 * @description:set field default value
	 * @date:2014-5-6 下午3:25:44
	 * @version:v1.0
	 * @param defaultValue
	 */
	public void setDefaultValue(String defaultValue);

	/**
	 * @description:set field control option ids
	 * @date:2014-5-6 下午3:25:54
	 * @version:v1.0
	 * @param controlOptionIds
	 */
	public void setControlOptionIds(Set<UUID> controlOptionIds);

	/**
	 * @description:set field control roles ids
	 * @date:2014-5-6 下午3:26:05
	 * @version:v1.0
	 * @param controlRoleIds
	 */
	public void setControlRoleIds(Set<String> controlRoleIds);

	/**
	 * @description:set field control action ids
	 * @date:2014-5-6 下午3:26:18
	 * @version:v1.0
	 * @param controlActionIds
	 */
	public void setControlActionIds(Set<String> controlActionIds);

	/**
	 * @description:set field actions id
	 * @date:2014-5-6 下午3:26:30
	 * @version:v1.0
	 * @param actionIds
	 */
	public void setActionIds(Set<UUID> actionIds);

	/**
	 * @description:set field option 
	 * @date:2014-5-6 下午3:26:42
	 * @version:v1.0
	 * @param options
	 */
	public void setOptions(Set<Option> options);

	/**
	 * @description:add option to field
	 * @date:2014-5-6 下午3:26:55
	 * @version:v1.0
	 * @return
	 */
	public Option addOption();
	
	/**
	 * @description:field to xml string
	 * @date:2014-5-6 下午3:27:06
	 * @version:v1.0
	 * @return
	 */
	public String toXMLString();

	/**
	 * @description:remove option from field
	 * @date:2014-5-6 下午3:27:20
	 * @version:v1.0
	 * @param id
	 */
	public void removeOption(UUID id);

	/**
	 * @description:field clone
	 * @date:2014-5-6 下午3:27:32
	 * @version:v1.0
	 * @return
	 */
	public Field clone();


	/**
	 * @description:field type enum
	 * @author:liming
	 * @mail:liming@sogou-inc.com
	 * @date:2014-5-6 下午3:27:47
	 * @version:v1.0
	 */
	public enum Type{
		t_input, t_reference, t_selection, t_attachment;
	}

	public enum Hidden{
		h_hidden, h_display;
	}

	/**
	 * @description:field data type enum
	 * @author:liming
	 * @mail:liming@sogou-inc.com
	 * @date:2014-5-6 下午3:28:03
	 * @version:v1.0
	 */
	public enum DataType{
		dt_single, dt_multiple, dt_double, dt_float, dt_integer, dt_long, dt_string, dt_text, dt_timestamp,dt_editor;
	}
}
