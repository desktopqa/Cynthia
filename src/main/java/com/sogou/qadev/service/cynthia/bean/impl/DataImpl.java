package com.sogou.qadev.service.cynthia.bean.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sogou.qadev.cache.impl.TemplateCache;
import com.sogou.qadev.service.cynthia.bean.BaseType;
import com.sogou.qadev.service.cynthia.bean.ChangeLog;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Field.DataType;
import com.sogou.qadev.service.cynthia.bean.Field.Type;
import com.sogou.qadev.service.cynthia.bean.Method;
import com.sogou.qadev.service.cynthia.bean.Pair;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.util.CommonUtil;
import com.sogou.qadev.service.cynthia.util.Date;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

/**
 * @description:data implements
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午3:01:21
 * @version:v1.0
 */
public final class DataImpl implements Data
{
	/**
	 * @Fields:serialVersionUID
	 * @Fields_Type:long
	 * @description:TODO
	 * @date:2014-5-6 下午3:01:12
	 */
	private static final long serialVersionUID = 6558956319932690247L;
	
	/**
	 * @description:get serial version id
	 * @date:2014-5-6 下午3:02:43
	 * @version:v1.0
	 * @return
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private String assignUser = null;
	private List<ChangeLog> changeLogs = new ArrayList<ChangeLog>();
	private Timestamp createTime = null;
	private String createUser = null;
	private String description = null;
	private UUID id = null;
	private Timestamp lastModifyTime = null;
	private Map<String, Object> objectMapName = new HashMap<String, Object>();
	private Map<UUID, Object> objectMapUUID = new HashMap<UUID, Object>();
	
	private UUID statusId = null;

	private UUID templateId = null;

	private UUID templateTypeId = null;

	private String title = null;
	
	public DataImpl(){
	}
	
	/**
	 * <h1> Title:</h1>
	 * <p> Description:init data from node</p>
	 * @date：2014-5-6 
	 * @param node
	 */
	public DataImpl(Node node){
		init(node);
	}
	
	/**
	 * <h1> Title:</h1>
	 * <p> Description:init data</p>
	 * @date：2014-5-6 
	 * @param id
	 * @param templateId
	 * @param createUser
	 * @param createTime
	 */
	public DataImpl(UUID id, UUID templateId, String createUser, Timestamp createTime){
		this.id = id;
		this.templateId = templateId;
		this.createUser = createUser;
		this.createTime = createTime;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:addChangeLog</p>
	 * @param changeLog
	 * @see com.sogou.qadev.service.cynthia.bean.Data#addChangeLog(com.sogou.qadev.service.cynthia.bean.ChangeLog)
	 */
	public void addChangeLog(ChangeLog changeLog){
		this.changeLogs.add(changeLog);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:clone</p>
	 * @return
	 * @see java.lang.Object#clone()
	 */
	public Data clone(){
		DataImpl dataImpl = new DataImpl(this.id, this.templateId, this.createUser, this.createTime);
		dataImpl.title = this.title;
		dataImpl.description = this.description;
		dataImpl.assignUser = this.assignUser;
		dataImpl.lastModifyTime = this.lastModifyTime;
		dataImpl.statusId = this.statusId;

		for(Map.Entry<String, Object> entry : this.objectMapName.entrySet()){
			if(entry.getValue() instanceof BaseType){
				dataImpl.objectMapName.put(entry.getKey(), ((BaseType)entry.getValue()).clone());
			}
			else{
				dataImpl.objectMapName.put(entry.getKey(), entry.getValue());
			}
		}

		for(Map.Entry<UUID, Object> entry : this.objectMapUUID.entrySet()){
			if (entry.getValue() instanceof BaseType){
				dataImpl.objectMapUUID.put(entry.getKey(), ((BaseType) entry.getValue()).clone());
			}
			else{
				dataImpl.objectMapUUID.put(entry.getKey(), entry.getValue());
			}
		}

		for(ChangeLog changeLog : this.changeLogs){
			dataImpl.changeLogs.add(changeLog);
		}

		return dataImpl;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getActionComment</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getActionComment()
	 */
	public String getActionComment(){
		if(changeLogs.size() == 0){
			return null;
		}

		return changeLogs.get(changeLogs.size() - 1).getActionComment();
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getActionId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getActionId()
	 */
	public UUID getActionId(){
		if(changeLogs.size() == 0){
			return null;
		}

		return changeLogs.get(changeLogs.size() - 1).getActionId();
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getActionIndex</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getActionIndex()
	 */
	public int getActionIndex(){
		return changeLogs.size();
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getActionUser</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getActionUser()
	 */
	public String getActionUser(){
		if(changeLogs.size() == 0){
			return null;
		}

		return changeLogs.get(changeLogs.size() - 1).getCreateUser();
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getAssignUser</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getAssignUser()
	 */
	public String getAssignUser() {
		return assignUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getAssignUsername</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getAssignUsername()
	 */
	public String getAssignUsername(){
		return this.assignUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getAttachments</p>
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getAttachments(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public UUID[] getAttachments(UUID fieldId){
		return getNotNullUUID((UUID[])this.objectMapUUID.get(fieldId));
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getChangeLogs</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getChangeLogs()
	 */
	public ChangeLog[] getChangeLogs(){
		return this.changeLogs.toArray(new ChangeLog[this.changeLogs.size()]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getCreateTime</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getCreateTime()
	 */
	public Timestamp getCreateTime(){
		return this.createTime;
	}

	/**
	 * @description:TODO
	 * @date:2014-5-6 下午3:02:06
	 * @version:v1.0
	 * @return
	 */
	public String getCreateUser() {
		return createUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getCreateUsername</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getCreateUsername()
	 */
	public String getCreateUsername(){
		return this.createUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getDate</p>
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getDate(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Date getDate(UUID fieldId){
		return (Date)this.objectMapUUID.get(fieldId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getDescription</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getDescription()
	 */
	public String getDescription(){
		return this.description;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getDouble</p>
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getDouble(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Double getDouble(UUID fieldId){
		return (Double)this.objectMapUUID.get(fieldId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getFloat</p>
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getFloat(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Float getFloat(UUID fieldId){
		return (Float)this.objectMapUUID.get(fieldId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.BaseType#getId()
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getInteger</p>
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getInteger(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Integer getInteger(UUID fieldId){
		return (Integer)this.objectMapUUID.get(fieldId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getLastModifyTime</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getLastModifyTime()
	 */
	public Timestamp getLastModifyTime(){
		return this.lastModifyTime;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getLong</p>
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getLong(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Long getLong(UUID fieldId){
		return (Long)this.objectMapUUID.get(fieldId);
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getMultiReference</p>
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getMultiReference(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public UUID[] getMultiReference(UUID fieldId){
		return getNotNullUUID((UUID[])this.objectMapUUID.get(fieldId));
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getMultiSelection</p>
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getMultiSelection(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public UUID[] getMultiSelection(UUID fieldId){
		return getNotNullUUID((UUID[])this.objectMapUUID.get(fieldId));
	}

	/**
	 * @description:return not null uuids
	 * @date:2014-5-6 下午3:08:05
	 * @version:v1.0
	 * @param inputUUID
	 * @return
	 */
	public UUID[] getNotNullUUID(UUID[] inputUUID){
		Set<UUID> uuidSet = new HashSet<UUID>();
		if (inputUUID != null) {
			for (UUID uuid : inputUUID) {
				if (uuid!=null) {
					uuidSet.add(uuid);
				}
			}
		}
		return uuidSet.toArray(new UUID[0]);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getObject</p>
	 * @param fieldName
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getObject(java.lang.String)
	 */
	public Object getObject(String fieldName){
		return this.objectMapName.get(fieldName);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getObject</p>
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getObject(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public Object getObject(UUID fieldId){
		return this.objectMapUUID.get(fieldId);
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getObjectMapName</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getObjectMapName()
	 */
	public Map<String, Object> getObjectMapName() {
		return objectMapName;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getObjectMapUUID</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getObjectMapUUID()
	 */
	public Map<UUID, Object> getObjectMapUUID() {
		return objectMapUUID;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getSingleReference</p>
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getSingleReference(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public UUID getSingleReference(UUID fieldId){
		return (UUID)this.objectMapUUID.get(fieldId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getSingleSelection</p>
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getSingleSelection(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public UUID getSingleSelection(UUID fieldId){
		return (UUID)this.objectMapUUID.get(fieldId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getStatusId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getStatusId()
	 */
	public UUID getStatusId() {
		return statusId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getString</p>
	 * @param fieldName
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getString(java.lang.String)
	 */
	public String getString(String fieldName){
		return (String)this.objectMapName.get(fieldName);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getString</p>
	 * @param fieldId
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getString(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public String getString(UUID fieldId){
		return (String)this.objectMapUUID.get(fieldId);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getTemplateId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getTemplateId()
	 */
	public UUID getTemplateId() {
		return templateId;
	}


	/**
	 * @description:TODO
	 * @date:2014-5-6 下午3:01:48
	 * @version:v1.0
	 * @return
	 */
	public UUID getTemplateTypeId() {
		return templateTypeId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getTitle</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getValidFieldIds</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getValidFieldIds()
	 */
	public UUID[] getValidFieldIds(){
		Set<UUID> validFieldIdSet = new LinkedHashSet<UUID>();
		for(UUID fieldId : this.objectMapUUID.keySet()){
			if(this.objectMapUUID.get(fieldId) != null)
				validFieldIdSet.add(fieldId);
		}

		return validFieldIdSet.toArray(new UUID[validFieldIdSet.size()]);
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:getValidFieldNames</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#getValidFieldNames()
	 */
	public String[] getValidFieldNames(){
		Set<String> validFieldNameSet = new LinkedHashSet<String>();
		for(String fieldName : this.objectMapName.keySet()){
			if(this.objectMapName.get(fieldName) != null)
				validFieldNameSet.add(fieldName);
		}

		return validFieldNameSet.toArray(new String[validFieldNameSet.size()]);
	}

	/**
	 * @description:init data from node
	 * @date:2014-5-6 下午3:09:08
	 * @version:v1.0
	 * @param node
	 */
	private void init(Node node){
		try {
		Node taskNode = XMLUtil.getSingleNode(node, "task");
		DataAccessFactory dataAccessFactory = DataAccessFactory.getInstance();

		this.id = dataAccessFactory.createUUID(XMLUtil.getSingleNodeTextContent(taskNode, "id"));
		this.templateId = dataAccessFactory.createUUID(XMLUtil.getSingleNodeTextContent(taskNode, "templateId"));
		this.title = XMLUtil.getSingleNodeTextContent(taskNode, "title");
		this.description = XMLUtil.getSingleNodeTextContent(taskNode, "description");
		this.createUser = XMLUtil.getSingleNodeTextContent(taskNode, "createUser");
		this.createTime = Timestamp.valueOf(XMLUtil.getSingleNodeTextContent(taskNode, "createTime"));
		this.assignUser = XMLUtil.getSingleNodeTextContent(taskNode, "assignUser");
		this.lastModifyTime = Timestamp.valueOf(XMLUtil.getSingleNodeTextContent(taskNode, "lastModifyTime"));
		this.statusId = dataAccessFactory.createUUID(XMLUtil.getSingleNodeTextContent(taskNode, "statusId"));

		Template template = TemplateCache.getInstance().get(this.templateId);
		if(template == null){
			throw new RuntimeException();
		}

		this.templateTypeId = template.getTemplateTypeId();

		List<Node> fieldNodeList = XMLUtil.getNodes(taskNode, "fields/field");

		for(Node fieldNode : fieldNodeList){
			List<Node> fieldDataNodeList = XMLUtil.getNodes(fieldNode, "data");
			if (fieldDataNodeList.size() == 0){
				continue;
			}

			UUID fieldId = dataAccessFactory.createUUID(XMLUtil.getSingleNodeTextContent(fieldNode, "id"));
			Field field = template.getField(fieldId);
			if(field == null){
				continue;
			}

			if(field.getType().equals(Type.t_selection)){
				if(field.getDataType().equals(DataType.dt_single)){
					this.setSingleSelection(fieldId, dataAccessFactory.createUUID(fieldDataNodeList.get(0).getTextContent()));
				}
				else if(field.getDataType().equals(DataType.dt_multiple)){
					Set<UUID> fieldDataSet = new LinkedHashSet<UUID>();

					for(Node fieldDataNode : fieldDataNodeList){
						fieldDataSet.add(dataAccessFactory.createUUID(fieldDataNode.getTextContent()));
					}

					this.setMultiSelection(fieldId, fieldDataSet.toArray(new UUID[fieldDataSet.size()]));
				}
			}
			else if(field.getType().equals(Type.t_reference)){
				if (field.getDataType().equals(DataType.dt_single)){
					this.setSingleReference(fieldId, dataAccessFactory.createUUID(fieldDataNodeList.get(0).getTextContent()));
				}
				else if(field.getDataType().equals(DataType.dt_multiple)){
					Set<UUID> fieldDataSet = new LinkedHashSet<UUID>();

					for(Node fieldDataNode : fieldDataNodeList){
						fieldDataSet.add(dataAccessFactory.createUUID(fieldDataNode.getTextContent()));
					}

					this.setMultiReference(fieldId, fieldDataSet.toArray(new UUID[fieldDataSet.size()]));
				}
			}
			else if(field.getType().equals(Type.t_attachment)){
				Set<UUID> fieldDataSet = new LinkedHashSet<UUID>();

				for(Node fieldDataNode : fieldDataNodeList){
					fieldDataSet.add(dataAccessFactory.createUUID(fieldDataNode.getTextContent()));
				}

				this.setAttachments(fieldId, fieldDataSet.toArray(new UUID[fieldDataSet.size()]));
			}
			else if(field.getType().equals(Type.t_input)){
				String fieldData = fieldDataNodeList.get(0).getTextContent();
				if(fieldData != null){
					if(field.getDataType().equals(DataType.dt_integer)){
						this.setInteger(fieldId, Integer.valueOf(fieldData));
					}
					else if(field.getDataType().equals(DataType.dt_double)){
						this.setDouble(fieldId, Double.valueOf(fieldData));
					}
					else if(field.getDataType().equals(DataType.dt_float)){
						this.setFloat(fieldId, Float.valueOf(fieldData));
					}
					else if(field.getDataType().equals(DataType.dt_long)){
						this.setLong(fieldId, Long.valueOf(fieldData));
					}
					else if(field.getDataType().equals(DataType.dt_timestamp)){
						this.setDate(fieldId, Date.valueOf(fieldData));
					}
					else if(field.getDataType().equals(DataType.dt_string) || field.getDataType().equals(DataType.dt_text)){
						this.setString(fieldId, fieldData);
					}
				}
			}
		}

		List<Node> logNodeList = XMLUtil.getNodes(taskNode, "logs/log");
		for(Node logNode : logNodeList){
			String logCreateUser = XMLUtil.getSingleNodeTextContent(logNode, "createUser");
			if(logCreateUser.equals("script@sogou-inc.com")){
				continue;
			}

			Timestamp logCreateTime = Timestamp.valueOf(XMLUtil.getSingleNodeTextContent(logNode, "createTime"));

			UUID actionId = null;
			String actionIdStr = XMLUtil.getSingleNodeTextContent(logNode, "actionId");
			if(actionIdStr != null){
				actionId = DataAccessFactory.getInstance().createUUID(actionIdStr);
			}

			String actionComment = XMLUtil.getSingleNodeTextContent(logNode, "actionComment");

			Map<String, Pair<Object, Object>> baseValueMap = new LinkedHashMap<String, Pair<Object, Object>>();

			List<Node> baseValueNodeList = XMLUtil.getNodes(logNode, "baseValues/baseValue");
			for(Node baseValueNode : baseValueNodeList){
				String base = XMLUtil.getSingleNodeTextContent(baseValueNode, "base");
				String previous = XMLUtil.getSingleNodeTextContent(baseValueNode, "previous");
				String current = XMLUtil.getSingleNodeTextContent(baseValueNode, "current");

				Pair<Object, Object> value = new Pair<Object, Object>();

				if(base.equals("id") || base.equals("statusId")){
					if(previous != null){
						value.setFirst(DataAccessFactory.getInstance().createUUID(previous));
					}

					if(current != null){
						value.setSecond(DataAccessFactory.getInstance().createUUID(current));
					}
				}
				else if(base.equals("createTime") || base.equals("lastModifyTime")){
					if(previous != null){
						value.setFirst(Timestamp.valueOf(previous));
					}

					if(current != null){
						value.setSecond(Timestamp.valueOf(current));
					}
				}
				else{
					value.setFirst(previous);
					value.setSecond(current);
				}

				baseValueMap.put(base, value);
			}

			Map<UUID, Pair<Object, Object>> extValueMap = new LinkedHashMap<UUID, Pair<Object, Object>>();

			List<Node> extValueNodeList = XMLUtil.getNodes(logNode, "extValues/extValue");
			for(Node extValueNode : extValueNodeList){
				UUID ext = DataAccessFactory.getInstance().createUUID(XMLUtil.getSingleNodeTextContent(extValueNode, "ext"));
				String previous = XMLUtil.getSingleNodeTextContent(extValueNode, "previous");
				String current = XMLUtil.getSingleNodeTextContent(extValueNode, "current");

				Field field = template.getField(ext);
				if(field == null){
					continue;
				}

				Pair<Object, Object> value = new Pair<Object, Object>();

				if(field.getType().equals(Type.t_selection) || field.getType().equals(Type.t_reference) || field.getType().equals(Type.t_attachment)){
					if(field.getDataType() != null && field.getDataType().equals(DataType.dt_single)){
						if(previous != null){
							value.setFirst(DataAccessFactory.getInstance().createUUID(previous));
						}

						if(current != null){
							value.setSecond(DataAccessFactory.getInstance().createUUID(current));
						}
					}
					else if(field.getDataType() == null || field.getDataType().equals(DataType.dt_multiple)){
						if(previous != null){
							String[] previousElemArray = previous.split("\\,");
							UUID[] previousElemIdArray = new UUID[previousElemArray.length];
							for(int i = 0; i < previousElemArray.length; i++){
								previousElemIdArray[i] = DataAccessFactory.getInstance().createUUID(previousElemArray[i]);
							}

							value.setFirst(previousElemIdArray);
						}

						if(current != null){
							String[] currentElemArray = current.split("\\,");
							UUID[] currentElemIdArray = new UUID[currentElemArray.length];
							for(int i = 0; i < currentElemArray.length; i++){
								currentElemIdArray[i] = DataAccessFactory.getInstance().createUUID(currentElemArray[i]);
							}

							value.setSecond(currentElemIdArray);
						}
					}
				}
				else if(field.getType().equals(Type.t_input)){
					if(field.getDataType().equals(DataType.dt_integer)){
						if(previous != null){
							value.setFirst(Integer.valueOf(previous));
						}

						if(current != null){
							value.setSecond(Integer.valueOf(current));
						}
					}

					if(field.getDataType().equals(DataType.dt_long)){
						if(previous != null){
							value.setFirst(Long.valueOf(previous));
						}

						if(current != null){
							value.setSecond(Long.valueOf(current));
						}
					}

					if(field.getDataType().equals(DataType.dt_float)){
						if(previous != null){
							value.setFirst(Float.valueOf(previous));
						}

						if(current != null){
							value.setSecond(Float.valueOf(current));
						}
					}

					if(field.getDataType().equals(DataType.dt_double)){
						if(previous != null){
							value.setFirst(Double.valueOf(previous));
						}

						if(current != null){
							value.setSecond(Double.valueOf(current));
						}
					}

					if(field.getDataType().equals(DataType.dt_timestamp)){
						if(previous != null){
							value.setFirst(Date.valueOf(previous));
						}

						if(current != null){
							value.setSecond(Date.valueOf(current));
						}
					}

					if(field.getDataType().equals(DataType.dt_string) || field.getDataType().equals(DataType.dt_text)){
						value.setFirst(previous);
						value.setSecond(current);
					}
				}

				extValueMap.put(ext, value);
			}

			this.addChangeLog(new ChangeLogImpl(this.id, logCreateUser, logCreateTime, actionId, actionComment, baseValueMap, extValueMap));
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:isMatching</p>
	 * @param fieldId
	 * @param method
	 * @param c
	 * @param isCurrent
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Data#isMatching(java.lang.String, com.sogou.qadev.service.cynthia.bean.Method, java.lang.Object, boolean)
	 */
	public boolean isMatching(String fieldId, Method method, Object c, boolean isCurrent){
		if(!isCurrent){
			return false;
		}

		if(CommonUtil.isPosNum(fieldId)){
			return matchExt(DataAccessFactory.getInstance().createUUID(fieldId), method, c);
		}

		return matchBase(fieldId, method, c);
	}

	/**
	 * @description:TODO
	 * @date:2014-5-6 下午3:09:43
	 * @version:v1.0
	 * @param baseFieldId
	 * @param method
	 * @param c
	 * @return
	 */
	private boolean matchBase(String baseFieldId, Method method, Object c){
		//在这里添加状态转换代码
		if(baseFieldId.equals("id")){
			if(method.equals(Method.equals)){
				return this.getId().equals(c);
			}

			if(method.equals(Method.notequals)){
				return !this.getId().equals(c);
			}

			if(method.equals(Method.isnull)){
				return false;
			}

			if(method.equals(Method.isnotnull)){
				return true;
			}
		}
		else if(baseFieldId.equals("title")){
			if(method.equals(Method.equals)){
				return this.getTitle().equals(c);
			}

			if(method.equals(Method.notequals)){
				return !this.getTitle().equals(c);
			}

			if(method.equals(Method.like)){
				return (this.getTitle().indexOf(c.toString()) >= 0);
			}

			if(method.equals(Method.notlike)){
				return (this.getTitle().indexOf(c.toString()) < 0);
			}

			if(method.equals(Method.isnull)){
				return false;
			}

			if(method.equals(Method.isnotnull)){
				return true;
			}
		}
		else if(baseFieldId.equals("description"))
		{
			if(method.equals(Method.equals))
				return (this.getDescription() != null && this.getDescription().equals(c));

			if(method.equals(Method.notequals))
				return (this.getDescription() == null || !this.getDescription().equals(c));

			if(method.equals(Method.like))
				return (this.getDescription() != null && this.getDescription().indexOf(c.toString()) >= 0);

			if(method.equals(Method.notlike))
				return (this.getDescription() == null || this.getDescription().indexOf(c.toString()) < 0);

			if(method.equals(Method.isnull))
				return (this.getDescription() == null);

			if(method.equals(Method.isnotnull))
				return (this.getDescription() != null);
		}
		else if(baseFieldId.equals("create_user"))
		{
			if(method.equals(Method.equals))
				return this.getCreateUsername().equalsIgnoreCase(c.toString());

			if(method.equals(Method.notequals))
				return !this.getCreateUsername().equalsIgnoreCase(c.toString());

			if(method.equals(Method.in))
				return Arrays.asList((String[])c).contains(this.getCreateUsername());

			if(method.equals(Method.notin))
				return !Arrays.asList((String[])c).contains(this.getCreateUsername());

			if(method.equals(Method.isnull))
				return false;

			if(method.equals(Method.isnotnull))
				return true;
		}
		else if(baseFieldId.equals("create_time"))
		{
			if(method.equals(Method.equals))
				return this.getCreateTime().equals(c);

			if(method.equals(Method.notequals))
				return !this.getCreateTime().equals(c);

			if(method.equals(Method.gt))
				return (this.getCreateTime().getTime() > ((Timestamp)c).getTime());

			if(method.equals(Method.ge))
				return (this.getCreateTime().getTime() >= ((Timestamp)c).getTime());

			if(method.equals(Method.lt))
				return (this.getCreateTime().getTime() < ((Timestamp)c).getTime());

			if(method.equals(Method.le))
				return (this.getCreateTime().getTime() <= ((Timestamp)c).getTime());

			if(method.equals(Method.isnull))
				return false;

			if(method.equals(Method.isnotnull))
				return true;
		}
		else if(baseFieldId.equals("assign_user"))
		{
			if(method.equals(Method.equals))
				return (this.getAssignUsername() != null && this.getAssignUsername().equalsIgnoreCase(c.toString()));

			if(method.equals(Method.notequals))
				return (this.getAssignUsername() == null || !this.getAssignUsername().equalsIgnoreCase(c.toString()));

			if(method.equals(Method.in))
				return (this.getAssignUsername() != null && Arrays.asList((String[])c).contains(this.getAssignUsername()));

			if(method.equals(Method.notin))
				return (this.getAssignUsername() == null || !Arrays.asList((String[])c).contains(this.getAssignUsername()));

			if(method.equals(Method.isnull))
				return (this.getAssignUsername() == null);

			if(method.equals(Method.isnotnull))
				return (this.getAssignUsername() != null);
		}
		else if(baseFieldId.equals("last_modify_time"))
		{
			if(method.equals(Method.equals))
				return this.getLastModifyTime().equals(c);

			if(method.equals(Method.notequals))
				return !this.getLastModifyTime().equals(c);

			if(method.equals(Method.gt))
				return (this.getLastModifyTime().getTime() > ((Timestamp)c).getTime());

			if(method.equals(Method.ge))
				return (this.getLastModifyTime().getTime() >= ((Timestamp)c).getTime());

			if(method.equals(Method.lt))
				return (this.getLastModifyTime().getTime() < ((Timestamp)c).getTime());

			if(method.equals(Method.le))
				return (this.getLastModifyTime().getTime() <= ((Timestamp)c).getTime());

			if(method.equals(Method.isnull))
				return false;

			if(method.equals(Method.isnotnull))
				return true;
		}
		else if(baseFieldId.equals("status_id"))
		{
			if(method.equals(Method.equals))
				return this.getStatusId().equals(c);

			if(method.equals(Method.notequals))
				return !this.getStatusId().equals(c);

			if(method.equals(Method.in))
				return Arrays.asList((UUID[])c).contains(this.getStatusId());

			if(method.equals(Method.notin))
				return !Arrays.asList((UUID[])c).contains(this.getStatusId());

			if(method.equals(Method.isnull))
				return false;

			if(method.equals(Method.isnotnull))
				return true;
		}
		else if(baseFieldId.equals("action_id"))
		{
			if(method.equals(Method.equals))
				return (this.getActionId() != null && this.getActionId().equals(c));

			if(method.equals(Method.notequals))
				return (this.getActionId() == null || !this.getActionId().equals(c));

			if(method.equals(Method.in))
				return (this.getActionId() != null && Arrays.asList((UUID[])c).contains(this.getActionId()));

			if(method.equals(Method.notin))
				return (this.getActionId() == null || !Arrays.asList((UUID[])c).contains(this.getActionId()));

			if(method.equals(Method.isnull))
				return (this.getActionId() == null);

			if(method.equals(Method.isnotnull))
				return (this.getActionId() != null);
		}
		else if(baseFieldId.equals("action_user"))
		{
			if(method.equals(Method.equals))
				return (this.getActionUser() != null && this.getActionUser().equals(c));

			if(method.equals(Method.notequals))
				return (this.getActionUser() == null || !this.getActionUser().equals(c));

			if(method.equals(Method.in))
				return (this.getActionUser() != null && Arrays.asList((String[])c).contains(this.getActionUser()));

			if(method.equals(Method.notin))
				return (this.getActionUser() == null || !Arrays.asList((String[])c).contains(this.getActionUser()));

			if(method.equals(Method.isnull))
				return (this.getActionUser() == null);

			if(method.equals(Method.isnotnull))
				return (this.getActionUser() != null);
		}
		else if(baseFieldId.equals("action_comment"))
		{
			if(method.equals(Method.equals))
				return (this.getActionComment() != null && this.getActionComment().equals(c));

			if(method.equals(Method.notequals))
				return (this.getActionComment() == null || !this.getActionComment().equals(c));

			if(method.equals(Method.like))
				return (this.getActionComment() != null && this.getActionComment().indexOf(c.toString()) >= 0);

			if(method.equals(Method.notlike))
				return (this.getActionComment() == null || this.getActionComment().indexOf(c.toString()) < 0);

			if(method.equals(Method.isnull))
				return (this.getActionComment() == null);

			if(method.equals(Method.isnotnull))
				return (this.getActionComment() != null);
		}
		else if(baseFieldId.equals("action_index"))
		{
			if(method.equals(Method.equals))
				return this.getActionIndex() == ((Integer)c).intValue();

			if(method.equals(Method.notequals))
				return this.getActionIndex() != ((Integer)c).intValue();

			if(method.equals(Method.gt))
				return this.getActionIndex() > ((Integer)c).intValue();

			if(method.equals(Method.ge))
				return this.getActionIndex() >= ((Integer)c).intValue();

			if(method.equals(Method.lt))
				return this.getActionIndex() < ((Integer)c).intValue();

			if(method.equals(Method.le))
				return this.getActionIndex() <= ((Integer)c).intValue();

			if(method.equals(Method.isnull))
				return false;

			if(method.equals(Method.isnotnull))
				return true;
		}else if(baseFieldId.equals("action_time_range")){

			return true;
		}

		return false;
	}

	/**
	 * @description:TODO
	 * @date:2014-5-6 下午3:09:52
	 * @version:v1.0
	 * @param fieldId
	 * @param method
	 * @param c
	 * @return
	 */
	private boolean matchExt(UUID fieldId, Method method, Object c){
		switch (method.value)
		{
			case 1:// equals
				return (this.getObject(fieldId) != null && this.getObject(fieldId).equals(c));
			case 2:// notequals
				return (this.getObject(fieldId) == null || !this.getObject(fieldId).equals(c));
			case 3:// in
				return (this.getObject(fieldId) != null && Arrays.asList((UUID[])c).contains(this.getObject(fieldId)));
			case 4:// not in
				return (this.getObject(fieldId) == null || !Arrays.asList((UUID[])c).contains(this.getObject(fieldId)));
			case 5:// like
			{
				if(this.getObject(fieldId) == null)
					return false;

				if(this.getObject(fieldId) instanceof String)
					return (this.getObject(fieldId).toString().indexOf(c.toString()) >= 0);
				if(c instanceof UUID[])
				{
					UUID [] tempFieldValue = (UUID[])this.getObject(fieldId);
					List<UUID> tempFieldValueList =Arrays.asList((UUID[])c);
					for(UUID tempObject : tempFieldValue)
					{
						if(tempFieldValueList.contains(tempObject))
							return true;
					}
				}

				return Arrays.asList((UUID[])this.getObject(fieldId)).contains(c);
			}
			case 6:// not like
			{
				Object obj = this.getObject(fieldId);
				if(obj == null)
					return true;

				if(obj instanceof String)
					return (obj.toString().indexOf(c.toString()) < 0);

				return !Arrays.asList((UUID[])obj).contains(c);
			}
			case 7:// isnull
				return (this.getObject(fieldId) == null);
			case 8:// isnotnull
				return (this.getObject(fieldId) != null);
			case 9:// gt
				return (this.getObject(fieldId) != null && ((Comparable)this.getObject(fieldId)).compareTo(c) > 0);
			case 10:// ge
				return (this.getObject(fieldId) != null && ((Comparable)this.getObject(fieldId)).compareTo(c) >= 0);
			case 11:// lt
				return (this.getObject(fieldId) != null && ((Comparable)this.getObject(fieldId)).compareTo(c) < 0);
			case 12:// le
				return (this.getObject(fieldId) != null && ((Comparable)this.getObject(fieldId)).compareTo(c) <= 0);
		}

		return false;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setAssignUser</p>
	 * @param assignUser
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setAssignUser(java.lang.String)
	 */
	public void setAssignUser(String assignUser) {
		this.assignUser = assignUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setAssignUsername</p>
	 * @param assignUser
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setAssignUsername(java.lang.String)
	 */
	public void setAssignUsername(String assignUser){
		this.assignUser = assignUser;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:setAttachments</p>
	 * @param fieldId
	 * @param x
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setAttachments(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID[])
	 */
	public void setAttachments(UUID fieldId, UUID[] x){
		this.objectMapUUID.put(fieldId, x);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setChangeLogs</p>
	 * @param changeLogs
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setChangeLogs(java.util.List)
	 */
	public void setChangeLogs(List<ChangeLog> changeLogs) {
		this.changeLogs = changeLogs;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setCreateTime</p>
	 * @param createTime
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setCreateTime(java.sql.Timestamp)
	 */
	public void setCreateTime(Timestamp createTime){
		this.createTime = createTime;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setCreateUser</p>
	 * @param createUser
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setCreateUser(java.lang.String)
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setCreateUsername</p>
	 * @param username
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setCreateUsername(java.lang.String)
	 */
	public void setCreateUsername(String username) {
		this.createUser = username;

	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setDate</p>
	 * @param fieldId
	 * @param x
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setDate(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.util.Date)
	 */
	public void setDate(UUID fieldId, Date x){
		this.objectMapUUID.put(fieldId, x);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setDescription</p>
	 * @param description
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setDescription(java.lang.String)
	 */
	public void setDescription(String description){
		this.description = description;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setDouble</p>
	 * @param fieldId
	 * @param x
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setDouble(com.sogou.qadev.service.cynthia.bean.UUID, java.lang.Double)
	 */
	public void setDouble(UUID fieldId, Double x){
		this.objectMapUUID.put(fieldId, x);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setFloat</p>
	 * @param fieldId
	 * @param x
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setFloat(com.sogou.qadev.service.cynthia.bean.UUID, java.lang.Float)
	 */
	public void setFloat(UUID fieldId, Float x){
		this.objectMapUUID.put(fieldId, x);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setId</p>
	 * @param id
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setId(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setInteger</p>
	 * @param fieldId
	 * @param x
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setInteger(com.sogou.qadev.service.cynthia.bean.UUID, java.lang.Integer)
	 */
	public void setInteger(UUID fieldId, Integer x){
		this.objectMapUUID.put(fieldId, x);
	}

	/**
	 * @description:modify the last modify time of data  and save the data log
	 * @date:2014-5-6 下午3:10:31
	 * @version:v1.0
	 */
	public void setLastModifyTime(){
		if(this.lastModifyTime == null){
			this.lastModifyTime = this.createTime;
		}
		else{
			this.lastModifyTime = new Timestamp(System.currentTimeMillis());
		}

		String logCreateUser = (String)getObject("logCreateUser");
		if(logCreateUser.equals("script@sogou-inc.com")){
			return;
		}

		Timestamp logCreateTime = getLastModifyTime();
		UUID logActionId = (UUID)getObject("logActionId");
		String logActionComment = (String)getObject("logActionComment");

		Map<String, Pair<Object, Object>> logBaseValueMap = (Map<String, Pair<Object, Object>>)getObject("logBaseValueMap");
		Map<UUID, Pair<Object, Object>> logExtValueMap = (Map<UUID, Pair<Object, Object>>)getObject("logExtValueMap");
		
		addChangeLog(new ChangeLogImpl(getId(), logCreateUser, logCreateTime, logActionId, logActionComment, logBaseValueMap, logExtValueMap));
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setLastModifyTime</p>
	 * @param lastModifyTime
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setLastModifyTime(java.sql.Timestamp)
	 */
	public void setLastModifyTime(Timestamp lastModifyTime){
		this.lastModifyTime = lastModifyTime;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setLong</p>
	 * @param fieldId
	 * @param x
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setLong(com.sogou.qadev.service.cynthia.bean.UUID, java.lang.Long)
	 */
	public void setLong(UUID fieldId, Long x){
		this.objectMapUUID.put(fieldId, x);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setMultiReference</p>
	 * @param fieldId
	 * @param x
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setMultiReference(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID[])
	 */
	public void setMultiReference(UUID fieldId, UUID[] x){
		this.objectMapUUID.put(fieldId, x);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setMultiSelection</p>
	 * @param fieldId
	 * @param x
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setMultiSelection(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID[])
	 */
	public void setMultiSelection(UUID fieldId, UUID[] x){
		this.objectMapUUID.put(fieldId, x);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setObject</p>
	 * @param fieldName
	 * @param value
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setObject(java.lang.String, java.lang.Object)
	 */
	public void setObject(String fieldName, Object value){
		this.objectMapName.put(fieldName, value);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setObject</p>
	 * @param fieldId
	 * @param x
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setObject(com.sogou.qadev.service.cynthia.bean.UUID, java.lang.Object)
	 */
	public void setObject(UUID fieldId, Object x){
		this.objectMapUUID.put(fieldId, x);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setObjectMapName</p>
	 * @param objectMapName
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setObjectMapName(java.util.Map)
	 */
	public void setObjectMapName(Map<String, Object> objectMapName) {
		this.objectMapName = objectMapName;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setObjectMapUUID</p>
	 * @param objectMapUUID
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setObjectMapUUID(java.util.Map)
	 */
	public void setObjectMapUUID(Map<UUID, Object> objectMapUUID) {
		this.objectMapUUID = objectMapUUID;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setSingleReference</p>
	 * @param fieldId
	 * @param x
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setSingleReference(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void setSingleReference(UUID fieldId, UUID x){
		this.objectMapUUID.put(fieldId, x);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setSingleSelection</p>
	 * @param fieldId
	 * @param x
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setSingleSelection(com.sogou.qadev.service.cynthia.bean.UUID, com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void setSingleSelection(UUID fieldId, UUID x){
		this.objectMapUUID.put(fieldId, x);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setStatusId</p>
	 * @param statusId
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setStatusId(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void setStatusId(UUID statusId) {
		this.statusId = statusId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setString</p>
	 * @param fieldName
	 * @param x
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setString(java.lang.String, java.lang.String)
	 */
	public void setString(String fieldName, String x){
		this.objectMapName.put(fieldName, x);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setString</p>
	 * @param fieldId
	 * @param x
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setString(com.sogou.qadev.service.cynthia.bean.UUID, java.lang.String)
	 */
	public void setString(UUID fieldId, String x){
		this.objectMapUUID.put(fieldId, x);
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setTemplateId</p>
	 * @param templateId
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setTemplateId(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void setTemplateId(UUID templateId) {
		this.templateId = templateId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setTemplateTypeId</p>
	 * @param templateTypeId
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setTemplateTypeId(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void setTemplateTypeId(UUID templateTypeId) {
		this.templateTypeId = templateTypeId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setTitle</p>
	 * @param title
	 * @see com.sogou.qadev.service.cynthia.bean.Data#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:toXMLDocument</p>
	 * @return
	 * @throws Exception
	 * @see com.sogou.qadev.service.cynthia.bean.BaseType#toXMLDocument()
	 */
	public Document toXMLDocument() throws Exception{
		return XMLUtil.string2Document(toXMLString(), "UTF-8");
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:toXMLString</p>
	 * @return
	 * @throws Exception
	 * @see com.sogou.qadev.service.cynthia.bean.BaseType#toXMLString()
	 */
	public String toXMLString() throws Exception{
		StringBuffer xmlb = new StringBuffer(10240);
		Template template = DataAccessFactory.getInstance().getSysDas().queryTemplate(this.getTemplateId());
		xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");

		xmlb.append("<task>");

		xmlb.append("<id>").append(this.getId()).append("</id>");
		xmlb.append("<templateId>").append(this.getTemplateId()).append("</templateId>");
		xmlb.append("<flowId>").append(template.getFlowId()).append("</flowId>");
		xmlb.append("<title>").append(XMLUtil.toSafeXMLString(this.getTitle())).append("</title>");
		xmlb.append("<createUser>").append(this.getCreateUsername()).append("</createUser>");
		xmlb.append("<createTime>").append(this.getCreateTime()).append("</createTime>");
		xmlb.append("<lastModifyTime>").append(this.getLastModifyTime()).append("</lastModifyTime>");
		xmlb.append("<statusId>").append(this.getStatusId()).append("</statusId>");
		xmlb.append("<description>").append(XMLUtil.toSafeXMLString(this.getDescription())).append("</description>");
		xmlb.append("<assignUser>").append(XMLUtil.toSafeXMLString(this.getAssignUsername())).append("</assignUser>");

		Set<Field> validFieldSet = new LinkedHashSet<Field>();
		if(this.getValidFieldIds() != null)
		{
			if(template != null)
			{
				for(UUID validFieldId : this.getValidFieldIds())
				{
					Field validField = template.getField(validFieldId);
					if(validField != null)
						validFieldSet.add(validField);
				}
			}
		}

		if(validFieldSet.size() == 0)
			xmlb.append("<fields/>");
		else
		{
			xmlb.append("<fields>");

			for (Field validField : validFieldSet)
			{
				xmlb.append("<field>");
				xmlb.append("<id>").append(validField.getId()).append("</id>");

				if (validField.getType().equals(Type.t_selection))
				{
					if (validField.getDataType().equals(DataType.dt_single))
					{
						UUID fieldData = this.getSingleSelection(validField.getId());
						if (fieldData != null)
							xmlb.append("<data>").append(fieldData).append("</data>");
					}
					else if (validField.getDataType().equals(DataType.dt_multiple))
					{
						UUID[] fieldDataArray = this.getMultiSelection(validField.getId());
						if (fieldDataArray != null)
						{
							for (UUID fieldData : fieldDataArray)
								xmlb.append("<data>").append(fieldData).append("</data>");
						}
					}
				}
				else if (validField.getType().equals(Type.t_reference))
				{
					if (validField.getDataType().equals(DataType.dt_single))
					{
						UUID fieldData = this.getSingleReference(validField.getId());
						if (fieldData != null)
							xmlb.append("<data>").append(fieldData).append("</data>");
					}
					else if (validField.getDataType().equals(DataType.dt_multiple))
					{
						UUID[] fieldDataArray = this.getMultiReference(validField.getId());
						if (fieldDataArray != null)
						{
							for (UUID fieldData : fieldDataArray)
								xmlb.append("<data>").append(fieldData).append("</data>");
						}
					}
				}
				else if (validField.getType().equals(Type.t_input))
				{
					if (validField.getDataType().equals(DataType.dt_integer))
					{
						if(this.getInteger(validField.getId()) != null)
							xmlb.append("<data>").append(this.getInteger(validField.getId())).append("</data>");
					}
					else if (validField.getDataType().equals(DataType.dt_double))
					{
						if(this.getDouble(validField.getId()) != null)
							xmlb.append("<data>").append(this.getDouble(validField.getId())).append("</data>");
					}
					else if (validField.getDataType().equals(DataType.dt_float))
					{
						if(this.getFloat(validField.getId()) != null)
							xmlb.append("<data>").append(this.getFloat(validField.getId())).append("</data>");
					}
					else if (validField.getDataType().equals(DataType.dt_long))
					{
						if(this.getLong(validField.getId()) != null)
							xmlb.append("<data>").append(this.getLong(validField.getId())).append("</data>");
					}
					else if (validField.getDataType().equals(DataType.dt_string) || validField.getDataType().equals(DataType.dt_text) || validField.getDataType().equals(DataType.dt_editor))
					{
						if(this.getString(validField.getId()) != null)
							xmlb.append("<data>").append(XMLUtil.toSafeXMLString(this.getString(validField.getId()))).append("</data>");
					}
					else if(validField.getDataType().equals(DataType.dt_timestamp))
					{
						if(this.getDate(validField.getId()) != null)
							xmlb.append("<data>").append(this.getDate(validField.getId()) == null ?"": Date.formatDate(this.getDate(validField.getId()).toTimestamp().toString(),validField.getTimestampFormat())).append("</data>");
					}
				}
				else if (validField.getType().equals(Type.t_attachment))
				{
					UUID[] fieldDataArray = this.getAttachments(validField.getId());
					if (fieldDataArray != null)
					{
						for (UUID fieldData : fieldDataArray)
							xmlb.append("<data>").append(fieldData).append("</data>");
					}
				}

				xmlb.append("</field>");
			}

			xmlb.append("</fields>");
		}

		if(this.getChangeLogs() == null || this.getChangeLogs().length == 0)
			xmlb.append("<logs/>");
		else
		{
			xmlb.append("<logs>");

			for (ChangeLog log : this.getChangeLogs())
			{
				xmlb.append("<log>");

				xmlb.append("<createUser>").append(log.getCreateUser()).append("</createUser>");

				xmlb.append("<createTime>").append(log.getCreateTime()).append("</createTime>");

				if(log.getActionId() != null)
					xmlb.append("<actionId>").append(log.getActionId()).append("</actionId>");
				else
					xmlb.append("<actionId/>");

				if (log.getActionComment() != null)
					xmlb.append("<actionComment>").append(XMLUtil.toSafeXMLString(log.getActionComment())).append("</actionComment>");
				else
					xmlb.append("<actionComment/>");

				Map<String, Pair<Object, Object>> baseValueMap = log.getBaseValueMap();
				if(baseValueMap == null || baseValueMap.size() == 0)
					xmlb.append("<baseValues/>");
				else
				{
					xmlb.append("<baseValues>");

					for(String base : baseValueMap.keySet())
					{
						Object previous = baseValueMap.get(base).getFirst();
						Object current = baseValueMap.get(base).getSecond();

						xmlb.append("<baseValue>");

						xmlb.append("<base>").append(base).append("</base>");

						if(previous == null)
							xmlb.append("<previous/>");
						else
							xmlb.append("<previous>").append(XMLUtil.toSafeXMLString(previous.toString())).append("</previous>");

						if(current == null)
							xmlb.append("<current/>");
						else
							xmlb.append("<current>").append(XMLUtil.toSafeXMLString(current.toString())).append("</current>");

						xmlb.append("</baseValue>");
					}

					xmlb.append("</baseValues>");
				}

				Map<UUID, Pair<Object, Object>> extValueMap = log.getExtValueMap();
				if(extValueMap == null || extValueMap.size() == 0)
					xmlb.append("<extValues/>");
				else
				{
					xmlb.append("<extValues>");

					for(UUID ext : extValueMap.keySet())
					{
						Object previous = extValueMap.get(ext).getFirst();
						Object current = extValueMap.get(ext).getSecond();

						xmlb.append("<extValue>");

						xmlb.append("<ext>").append(ext).append("</ext>");

						if(previous == null)
							xmlb.append("<previous/>");
						else
						{
							if(previous instanceof UUID[])
							{
								xmlb.append("<previous>");

								UUID[] previousIdArray = (UUID[])previous;
								for(int i = 0; i < previousIdArray.length; i++)
								{
									if(i > 0)
										xmlb.append(",");

									xmlb.append(previousIdArray[i]);
								}

								xmlb.append("</previous>");
							}
							else
								xmlb.append("<previous>").append(XMLUtil.toSafeXMLString(previous.toString())).append("</previous>");
						}

						if(current == null)
							xmlb.append("<current/>");
						else
						{
							if(current instanceof UUID[])
							{
								xmlb.append("<current>");

								UUID[] currentIdArray = (UUID[])current;
								for(int i = 0; i < currentIdArray.length; i++)
								{
									if(i > 0)
										xmlb.append(",");

									xmlb.append(currentIdArray[i]);
								}

								xmlb.append("</current>");
							}
							else
								xmlb.append("<current>").append(XMLUtil.toSafeXMLString(current.toString())).append("</current>");
						}

						xmlb.append("</extValue>");
					}

					xmlb.append("</extValues>");
				}

				xmlb.append("</log>");
			}

			xmlb.append("</logs>");
		}

		xmlb.append("</task>");

		return xmlb.toString();
	}
}
