package com.sogou.qadev.service.cynthia.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import com.sogou.qadev.cache.impl.TemplateCache;
import com.sogou.qadev.service.cynthia.bean.Action;
import com.sogou.qadev.service.cynthia.bean.Attachment;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Field.DataType;
import com.sogou.qadev.service.cynthia.bean.Field.Type;
import com.sogou.qadev.service.cynthia.bean.Option;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.service.DataAccessSession;
import com.sogou.qadev.service.cynthia.service.DataManager;

public abstract class BaseUtil {

	public static Map<String, String> baseFieldNameMap = new HashMap<String, String>();
	public static Map<String, String> baseFieldIdMap = new HashMap<String, String>();
	public static Map<String, String> baseFieldTypeMap = new HashMap<String, String>();
	public static String errorXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><error>true</error></root>";
	public static String correctXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><error>false</error></root>";
	public static String emptyDataXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><resultset><totalcount>0</totalcount></resultset>";
	static {
		baseFieldNameMap.put("title", "标题");
		baseFieldNameMap.put("description", "描述");
		baseFieldNameMap.put("status_id", "状态");
		baseFieldNameMap.put("create_user", "创建人");
		baseFieldNameMap.put("create_time", "创建时间");
		baseFieldNameMap.put("assign_user", "指派人");
		baseFieldNameMap.put("last_modify_time", "修改时间");
		baseFieldNameMap.put("node_id", "项目");
		baseFieldNameMap.put("action_id", "执行动作");
		baseFieldNameMap.put("action_user", "执行人");
		baseFieldNameMap.put("action_comment", "执行描述");
		baseFieldNameMap.put("action_index", "执行序号");
	}

	static {
		baseFieldTypeMap.put("title", "input");
		baseFieldTypeMap.put("description", "text");
		baseFieldTypeMap.put("status_id", "selection");
		baseFieldTypeMap.put("create_user", "selection");
		baseFieldTypeMap.put("create_time", "timestamp");
		baseFieldTypeMap.put("assign_user", "selection");
		baseFieldTypeMap.put("last_modify_time", "timestamp");
		baseFieldTypeMap.put("node_id", "selection");
		baseFieldTypeMap.put("action_id", "selection");
		baseFieldTypeMap.put("action_user", "selection");
		baseFieldTypeMap.put("action_comment", "text");
	}

	static {
		for (Map.Entry<String, String> entry : baseFieldNameMap.entrySet())
			baseFieldIdMap.put(entry.getValue(), entry.getKey());
	}

	/**
	 * @description:get all data value map
	 * @date:2014-5-6 下午6:34:43
	 * @version:v1.0
	 * @param task
	 * @param das
	 * @return
	 */
	protected Map<String, String> createMapByTaskFieldValue(Data task,DataAccessSession das) {
		Map<String, String> dataMap = new HashMap<String, String>();

		Template template = das.queryTemplate(task.getTemplateId());
		if (template == null)
			return dataMap;

		for (Field field : template.getFields()) {
			if (field.getType().equals(Type.t_selection)) {
				if (field.getDataType().equals(DataType.dt_single)) {
					UUID optionId = task.getSingleSelection(field.getId());
					if (optionId != null) {
						Option option = field.getOption(optionId);
						if (option != null)
							dataMap.put(field.getName(), option.getName());
					}
				} else {
					UUID[] optionIdArray = task
							.getMultiSelection(field.getId());
					if (optionIdArray != null && optionIdArray.length > 0) {
						StringBuffer valueStrb = new StringBuffer();
						for (UUID optionId : optionIdArray) {
							Option option = field.getOption(optionId);
							if (option != null) {
								if (valueStrb.length() > 0)
									valueStrb.append(",");

								valueStrb.append("[").append(option.getName())
										.append("]");
							}
						}

						if (valueStrb.length() > 0)
							dataMap.put(field.getName(), valueStrb.toString());
					}
				}
			} else if (field.getType().equals(Type.t_reference)) {
				if (field.getDataType().equals(DataType.dt_single)) {
					UUID dataId = task.getSingleReference(field.getId());
					if (dataId != null) {
						Data data = das.queryData(dataId);
						if (data != null)
							dataMap.put(field.getName(), data.getTitle());
					}
				} else {
					UUID[] dataIdArray = task.getMultiReference(field.getId());
					if (dataIdArray != null && dataIdArray.length > 0) {
						StringBuffer valueStrb = new StringBuffer();
						for (UUID dataId : dataIdArray) {
							Data data = das.queryData(dataId);
							if (data != null) {
								if (valueStrb.length() > 0)
									valueStrb.append(",");

								valueStrb.append("[").append(data.getTitle())
										.append("]");
							}
						}

						if (valueStrb.length() > 0)
							dataMap.put(field.getName(), valueStrb.toString());
					}
				}
			} else if (field.getType().equals(Type.t_attachment)) {
				UUID[] attachmentIdArray = task.getAttachments(field.getId());
				if (attachmentIdArray != null && attachmentIdArray.length > 0) {
					StringBuffer valueStrb = new StringBuffer();

					Attachment[] attachmentArray = das.queryAttachments(
							attachmentIdArray, false);
					for (Attachment attachment : attachmentArray) {
						if (valueStrb.length() > 0)
							valueStrb.append(",");

						valueStrb.append("[").append(attachment.getName())
								.append("]");
					}

					if (valueStrb.length() > 0)
						dataMap.put(field.getName(), valueStrb.toString());
				}
			} else if (field.getType().equals(Type.t_input)) {
				if (field.getDataType().equals(DataType.dt_integer)) {
					Integer value = task.getInteger(field.getId());
					if (value == null) {
						value = Integer.MIN_VALUE;
					}

					dataMap.put(field.getName(), value.toString());
				} else if (field.getDataType().equals(DataType.dt_long)) {
					Long value = task.getLong(field.getId());
					if (value == null) {
						value = Long.MIN_VALUE;
					}

					dataMap.put(field.getName(), value.toString());
				} else if (field.getDataType().equals(DataType.dt_float)) {
					Float value = task.getFloat(field.getId());
					if (value == null) {
						value = Float.MIN_VALUE;
					}

					dataMap.put(field.getName(), value.toString());
				} else if (field.getDataType().equals(DataType.dt_double)) {
					Double value = task.getDouble(field.getId());
					if (value == null) {
						value = Double.MIN_VALUE;
					}

					dataMap.put(field.getName(), value.toString());
				} else if (field.getDataType().equals(DataType.dt_string)
						|| field.getDataType().equals(DataType.dt_text)) {
					String value = task.getString(field.getId());
					if (value != null)
						dataMap.put(field.getName(), value);
				} else if (field.getDataType().equals(DataType.dt_timestamp)) {
					Date value = task.getDate(field.getId());
					if (value != null)
						dataMap.put(field.getName(), value.toString());
				}
			}
		}

		return dataMap;
	}

	/**
	 * @description:get base field values by field name
	 * @date:2014-5-6 下午6:34:56
	 * @version:v1.0
	 * @param task
	 * @param fieldName
	 * @param das
	 * @return
	 */
	protected String getTaskBaseAttribute(Data task, String fieldName,
			DataAccessSession das) {
		if ("title".equals(fieldName) || "标题".equals(fieldName))
			return task.getTitle();
		else if ("status_id".equals(fieldName) || "状态".equals(fieldName))
			return DataManager.getInstance().getDataStatus(task, das);
		else if ("create_user".equals(fieldName) || "创建人".equals(fieldName))
			return task.getCreateUsername();
		else if ("create_time".equals(fieldName) || "创建时间".equals(fieldName)) {
			String createTime = task.getCreateTime().toString();
			if (createTime.indexOf(".") > 0)
				createTime = createTime.split("\\.")[0];

			return createTime;
		} else if ("description".equals(fieldName) || "描述".equals(fieldName))
			return task.getDescription();
		else if ("assign_user".equals(fieldName) || "指派人".equals(fieldName))
			return task.getAssignUsername();
		else if ("last_modify_time".equals(fieldName)
				|| "修改时间".equals(fieldName)) {
			String lastModifyTime = task.getLastModifyTime().toString();
			if (lastModifyTime.indexOf(".") > 0)
				lastModifyTime = lastModifyTime.split("\\.")[0];

			return lastModifyTime;
		} else if ("node_id".equals(fieldName) || "项目".equals(fieldName)) {
			Template template = das.queryTemplate(task.getTemplateId());
			if (template != null)
				return template.getName();

			return null;
		} else if ("action_id".equals(fieldName) || "执行动作".equals(fieldName)) {
			if (task.getActionId() == null)
				return "编辑";
			Template template = TemplateCache.getInstance().get(
					task.getTemplateId());

			Action action = das.queryAction(task.getActionId(),
					template.getFlowId());
			if (action != null)
				return action.getName();

			return null;
		} else if ("action_user".equals(fieldName) || "执行人".equals(fieldName))
			return task.getActionUser();
		else if ("action_comment".equals(fieldName) || "执行描述".equals(fieldName))
			return task.getActionComment();
		else if ("action_index".equals(fieldName) || "执行序号".equals(fieldName))
			return Integer.toString(task.getActionIndex());

		return null;
	}

	/**
	 * @description:cut data 
	 * @date:2014-5-6 下午6:35:15
	 * @version:v1.0
	 * @param datas
	 * @param start
	 * @param limit
	 * @return
	 */
	protected List<Data> cutData(Data[] datas, int start, int limit) {
		List<Data> result = new ArrayList<Data>();
		if (datas == null || datas.length == 0)
			return result;
		int totalCount = datas.length;
		int begin = start;
		int end = start + limit > totalCount ? totalCount : start + limit;
		for (int i = begin; i < end; i++) {
			result.add(datas[i]);
		}
		return result;
	}

	protected Document getNewDocument() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		Document document = builder.newDocument();
		return document;
	}

}
