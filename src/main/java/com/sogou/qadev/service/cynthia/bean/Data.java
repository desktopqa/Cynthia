package com.sogou.qadev.service.cynthia.bean;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.sogou.qadev.service.cynthia.util.Date;

/**
 * @description:data bean
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午2:46:35
 * @version:v1.0
 */
public interface Data extends BaseType{

	/**
	 * @description:return data title
	 * @date:2014-5-6 下午2:46:52
	 * @version:v1.0
	 * @return
	 */
	public String getTitle();
	
	/**
	 * @description:return data description
	 * @date:2014-5-6 下午2:47:02
	 * @version:v1.0
	 * @return
	 */
	public String getDescription();
	
	/**
	 * @description:return data createuser 
	 * @date:2014-5-6 下午2:47:16
	 * @version:v1.0
	 * @return
	 */
	public String getCreateUsername();

	/**
	 * @description:return data create time
	 * @date:2014-5-6 下午2:47:28
	 * @version:v1.0
	 * @return
	 */
	public Timestamp getCreateTime();

	/**
	 * @description:return data last modify time
	 * @date:2014-5-6 下午2:47:41
	 * @version:v1.0
	 * @return
	 */
	public Timestamp getLastModifyTime();

	/**
	 * @description:return data assign user
	 * @date:2014-5-6 下午2:47:53
	 * @version:v1.0
	 * @return
	 */
	public String getAssignUsername();

	/**
	 * @description:return templateid
	 * @date:2014-5-6 下午2:48:03
	 * @version:v1.0
	 * @return
	 */
	public UUID getTemplateId();

	/**
	 * @description:return data current status id
	 * @date:2014-5-6 下午2:48:16
	 * @version:v1.0
	 * @return
	 */
	public UUID getStatusId();

	/**
	 * @description:return data action id
	 * @date:2014-5-6 下午2:48:44
	 * @version:v1.0
	 * @return
	 */
	public UUID getActionId();

	/**
	 * @description:return data action user
	 * @date:2014-5-6 下午2:48:58
	 * @version:v1.0
	 * @return
	 */
	public String getActionUser();

	/**
	 * @description:return data action comment
	 * @date:2014-5-6 下午2:49:20
	 * @version:v1.0
	 * @return
	 */
	public String getActionComment();

	/**
	 * @description:return data last action index
	 * @date:2014-5-6 下午2:49:30
	 * @version:v1.0
	 * @return
	 */
	public int getActionIndex();

	/**
	 * @description:return all field ids
	 * @date:2014-5-6 下午2:49:45
	 * @version:v1.0
	 * @return
	 */
	public UUID[] getValidFieldIds();

	/**
	 * @description:return all field names
	 * @date:2014-5-6 下午2:50:20
	 * @version:v1.0
	 * @return
	 */
	public String[] getValidFieldNames();

	/**
	 * @description:get all change logs
	 * @date:2014-5-6 下午2:50:30
	 * @version:v1.0
	 * @return
	 */
	public ChangeLog[] getChangeLogs();

	/**
	 * @description:get single selection field value id
	 * @date:2014-5-6 下午2:50:42
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public UUID getSingleSelection(UUID field);

	/**
	 * @description:get multiple selection field value id
	 * @date:2014-5-6 下午2:50:57
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public UUID[] getMultiSelection(UUID field);

	/**
	 * @description:get single reference field value id
	 * @date:2014-5-6 下午2:51:12
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public UUID getSingleReference(UUID field);

	/**
	 * @description:get multiple reference field value id
	 * @date:2014-5-6 下午2:51:27
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public UUID[] getMultiReference(UUID field);

	/**
	 * @description:get attachment ids of field
	 * @date:2014-5-6 下午2:51:41
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public UUID[] getAttachments(UUID field);

	/**
	 * @description:get double value of double field value
	 * @date:2014-5-6 下午2:52:00
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public Double getDouble(UUID field);

	/**
	 * @description:get float value of field 
	 * @date:2014-5-6 下午2:52:17
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public Float getFloat(UUID field);

	/**
	 * @description:get int value of field 
	 * @date:2014-5-6 下午2:52:34
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public Integer getInteger(UUID field);

	/**
	 * @description:get long value of field 
	 * @date:2014-5-6 下午2:52:43
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public Long getLong(UUID field);

	/**
	 * @description:get string value of field id
	 * @date:2014-5-6 下午2:52:51
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public String getString(UUID field);

	/**
	 * @description:get String value of field name
	 * @date:2014-5-6 下午2:53:03
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public String getString(String field);

	/**
	 * @description:get object value of field 
	 * @date:2014-5-6 下午2:53:19
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public Object getObject(UUID field);

	/**
	 * @description:set single select field value
	 * @date:2014-5-6 下午2:53:35
	 * @version:v1.0
	 * @param field
	 * @param x
	 */
	public void setSingleSelection(UUID field, UUID x);

	/**
	 * @description:set multi select field value
	 * @date:2014-5-6 下午2:53:48
	 * @version:v1.0
	 * @param field
	 * @param x
	 */
	public void setMultiSelection(UUID field, UUID[] x);

	/**
	 * @description:set single ref field value
	 * @date:2014-5-6 下午2:53:58
	 * @version:v1.0
	 * @param field
	 * @param x
	 */
	public void setSingleReference(UUID field, UUID x);

	/**
	 * @description:set multi ref field value
	 * @date:2014-5-6 下午2:54:06
	 * @version:v1.0
	 * @param field
	 * @param x
	 */
	public void setMultiReference(UUID field, UUID[] x);

	/**
	 * @description:set double field value
	 * @date:2014-5-6 下午2:54:15
	 * @version:v1.0
	 * @param field
	 * @param x
	 */
	public void setDouble(UUID field, Double x);

	/**
	 * @description:set float field value
	 * @date:2014-5-6 下午2:54:26
	 * @version:v1.0
	 * @param field
	 * @param x
	 */
	public void setFloat(UUID field, Float x);

	/**
	 * @description:set int field value
	 * @date:2014-5-6 下午2:54:33
	 * @version:v1.0
	 * @param field
	 * @param x
	 */
	public void setInteger(UUID field, Integer x);

	/**
	 * @description:set long field value
	 * @date:2014-5-6 下午2:54:43
	 * @version:v1.0
	 * @param field
	 * @param x
	 */
	public void setLong(UUID field, Long x);

	/**
	 * @description:set string field value
	 * @date:2014-5-6 下午2:54:50
	 * @version:v1.0
	 * @param field
	 * @param x
	 */
	public void setString(UUID field, String x);

	/**
	 * @description:set double field value by field name
	 * @date:2014-5-6 下午2:55:01
	 * @version:v1.0
	 * @param field
	 * @param x
	 */
	public void setString(String field, String x);

	/**
	 * @description:set field value
	 * @date:2014-5-6 下午2:55:10
	 * @version:v1.0
	 * @param field
	 * @param x
	 */
	public void setObject(UUID field, Object x);

	/**
	 * @description:set data title
	 * @date:2014-5-6 下午2:55:19
	 * @version:v1.0
	 * @param title
	 */
	public void setTitle(String title);

	/**
	 * @description:set data create user
	 * @date:2014-5-6 下午2:55:26
	 * @version:v1.0
	 * @param username
	 */
	public void setCreateUsername(String username);

	/**
	 * @description:set data description
	 * @date:2014-5-6 下午2:55:37
	 * @version:v1.0
	 * @param description
	 */
	public void setDescription(String description);

	/**
	 * @description:set data assign user
	 * @date:2014-5-6 下午2:55:48
	 * @version:v1.0
	 * @param assignUsername
	 */
	public void setAssignUsername(String assignUsername);

	/**
	 * @description:set data status
	 * @date:2014-5-6 下午2:56:00
	 * @version:v1.0
	 * @param statusId
	 */
	public void setStatusId(UUID statusId);

	/**
	 * @description:set data create time
	 * @date:2014-5-6 下午2:56:08
	 * @version:v1.0
	 * @param createTime
	 */
	public void setCreateTime(Timestamp createTime);

	/**
	 * @description:set data last modify time
	 * @date:2014-5-6 下午2:56:15
	 * @version:v1.0
	 * @param lastModifyTime
	 */
	public void setLastModifyTime(Timestamp lastModifyTime);

	/**
	 * @description:get field value of date type field
	 * @date:2014-5-6 下午2:56:25
	 * @version:v1.0
	 * @param fieldId
	 * @return
	 */
	public Date getDate(UUID fieldId);

	/**
	 * @description:set field value of date type field
	 * @date:2014-5-6 下午2:56:36
	 * @version:v1.0
	 * @param fieldId
	 * @param date
	 */
	public void setDate(UUID fieldId, Date date);

	/**
	 * @description:TODO
	 * @date:2014-5-6 下午2:57:05
	 * @version:v1.0
	 * @param fieldId
	 * @param method
	 * @param c
	 * @param isCurrent
	 * @return
	 */
	public boolean isMatching(String fieldId, Method method, Object c, boolean isCurrent);

	/**
	 * @description:set field value of attach  type field
	 * @date:2014-5-6 下午2:57:09
	 * @version:v1.0
	 * @param field
	 * @param x
	 */
	public void setAttachments(UUID field, UUID[] x);

	/**
	 * @description:get field value by field name
	 * @date:2014-5-6 下午2:57:30
	 * @version:v1.0
	 * @param field
	 * @return
	 */
	public Object getObject(String field);

	/**
	 * @description:set field value by field name
	 * @date:2014-5-6 下午2:57:40
	 * @version:v1.0
	 * @param field
	 * @param value
	 */
	public void setObject(String field, Object value);
	
	/**
	 * @description:set data id
	 * @date:2014-5-6 下午2:57:49
	 * @version:v1.0
	 * @param createUUID
	 */
	public void setId(UUID createUUID);
	
	/**
	 * @description:set data template id
	 * @date:2014-5-6 下午2:57:56
	 * @version:v1.0
	 * @param createUUID
	 */
	public void setTemplateId(UUID createUUID);
	
	/**
	 * @description:set data template type id 
	 * @date:2014-5-6 下午2:58:04
	 * @version:v1.0
	 * @param createUUID
	 */
	public void setTemplateTypeId(UUID createUUID);
	
	/**
	 * @description:set data create user
	 * @date:2014-5-6 下午2:58:13
	 * @version:v1.0
	 * @param string
	 */
	public void setCreateUser(String string);
	
	/**
	 * @description:set data assign user
	 * @date:2014-5-6 下午2:58:23
	 * @version:v1.0
	 * @param string
	 */
	public void setAssignUser(String string);
	
	/**
	 * @description:set data changelogs
	 * @date:2014-5-6 下午2:58:33
	 * @version:v1.0
	 * @param changeLogs
	 */
	public void setChangeLogs(List<ChangeLog> changeLogs);
	
	/**
	 * @description:set data object values map
	 * @date:2014-5-6 下午2:58:43
	 * @version:v1.0
	 * @param objectMapUUID
	 */
	public void setObjectMapUUID(Map<UUID, Object> objectMapUUID);
	
	/**
	 * @description:set data object names map
	 * @date:2014-5-6 下午2:58:59
	 * @version:v1.0
	 * @param objectMapName
	 */
	public void setObjectMapName(Map<String, Object> objectMapName);
	
	/**
	 * @description:return object id value map
	 * @date:2014-5-6 下午3:03:21
	 * @version:v1.0
	 * @return
	 */
	public Map<UUID, Object> getObjectMapUUID();
	
	/**
	 * @description:return object name value map
	 * @date:2014-5-6 下午3:03:55
	 * @version:v1.0
	 * @return
	 */
	public Map<String, Object> getObjectMapName();
	
	/**
	 * @description:add change log
	 * @date:2014-5-6 下午3:06:02
	 * @version:v1.0
	 * @param changeLog
	 */
	public void addChangeLog(ChangeLog changeLog);
	
	/**
	 * @description:get data assign user
	 * @date:2014-5-6 下午3:06:52
	 * @version:v1.0
	 * @return
	 */
	public String getAssignUser();
}
