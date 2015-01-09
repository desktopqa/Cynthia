package com.sogou.qadev.service.cynthia.util;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

public class FilterDataAssembleUtil {

	/**
	 * @description:get data template field value map
	 * @date:2014-5-8 上午10:50:59
	 * @version:v1.0
	 * @param task
	 * @param das
	 * @return
	 */
	private static Map<String, String> createMapByTaskFieldValue(Data task,
			DataAccessSession das) {
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

	private static String getTaskBaseAttribute(Data task, String fieldName,
			DataAccessSession das) {
		Template template = das.queryTemplate(task.getTemplateId());

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
			if (template != null)
				return template.getName();
			return null;
		} else if ("action_id".equals(fieldName) || "执行动作".equals(fieldName)) {
			if (task.getActionId() == null)
				return "编辑";

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

	@SuppressWarnings("unused")
	private static Element getTaskBaseAttribute(Data task, String fieldName,
			DataAccessSession das, Document document) {
		Element element = null;
		String name = null;
		String value = null;
		if (!fieldName.equals("id")) {
			name = ConfigUtil.baseFieldNameMap.get(fieldName);
			value = getTaskBaseAttribute(task, fieldName, das);
		} else {
			name = "编号";
			value = task.getId().toString();
		}

		element = document.createElement("field");
		XMLUtil.setAttribute(element, "name", name);
		XMLUtil.setAttribute(element, "value", value);
		return element;
	}

	public static String createItemElementCompact(Element taskElement,
			boolean isNew, boolean hasAttachment, Long id,
			Map<String, String> userAliasMap, DataAccessSession das,
			Map<String, String> userClassifyDataMap) {
		NodeList fieldList = taskElement.getChildNodes();
		StringBuffer dataJson = new StringBuffer(64);
		dataJson.append("{");
		dataJson.append("\"uuid\":\"").append(id).append("\"");
		dataJson.append(",\"id\":\"").append(id).append("\"");
		dataJson.append(",\"isNew\":\"").append(isNew).append("\"");
		dataJson.append(",\"hasAttachment\":\"").append(hasAttachment)
				.append("\"");

		if (userClassifyDataMap.containsKey(id.toString())) {
			dataJson.append(",\"selected\":\"true\"");
			dataJson.append(",\"selectedName\":\"")
					.append(XMLUtil.toSafeXMLString(userClassifyDataMap.get(id
							.toString()))).append("\"");
		} else {
			dataJson.append(",\"selected\":\"false\"");
			dataJson.append(",\"selected\":\"-\"");
		}

		for (int i = 0; i < fieldList.getLength(); i++) {
			Node node = fieldList.item(i);
			if (!node.getNodeName().equals("field")
					|| XMLUtil.getAttribute(node, "uuid") == null)
				continue;

			String fieldId = XMLUtil.getAttribute(node, "uuid");
			if (fieldId == null || "".equals(fieldId.trim()))
				continue;
			if (CommonUtil.isPosNum(fieldId)) {
				dataJson.append(",\"FIEL-").append(fieldId);
			} else {
				dataJson.append(",\"").append(fieldId);
			}

			String fieldValue = XMLUtil.getAttribute(node, "value");
			if (!fieldValue.equals("")
					&& (fieldId.equals("create_user") || fieldId
							.equals("assign_user"))) {
				if (!userAliasMap.containsKey(fieldValue)) {
					userAliasMap.put(fieldValue,CynthiaUtil.getUserAlias(fieldValue));
				}
				if (userAliasMap.get(fieldValue) != null) {
					fieldValue = userAliasMap.get(fieldValue);
				}
			}
			dataJson.append("\":\"").append(CynthiaUtil.stringToJson(fieldValue))
					.append("\"");
		}
		dataJson.append("}");
		return dataJson.toString();
	}

	public static void createTaskElement(Document document, Data task,
			Element taskElement, String[] fieldsName, DataAccessSession das,
			boolean isPlain) throws Exception {
		Element element = null;

		element = document.createElement("uuid");
		element.setTextContent(isPlain ? task.getId().toString() : XMLUtil
				.toSafeXMLString(task.getId().toString()));
		taskElement.appendChild(element);

		Element idElement = document.createElement("field");
		idElement.setAttribute("name", "编号");
		idElement.setAttribute("value", task.getId().toString());
		taskElement.appendChild(idElement);

		Element IDElement = document.createElement("field");
		IDElement.setAttribute("name", "ID");
		IDElement.setAttribute("value", task.getId().toString());
		taskElement.appendChild(IDElement);

		Template template = das.queryTemplate(task.getTemplateId());
		if (template != null) {
			Element templateElement = document.createElement("field");
			templateElement.setAttribute("name", "表单");
			templateElement.setAttribute("value", template.getFlowId()
					.toString());
			taskElement.appendChild(templateElement);
		}

		Map<String, String> map = createMapByTaskFieldValue(task, das);

		boolean hasPriority = false;

		for (String fieldName : fieldsName) {
			if (fieldName.equals("修改优先级"))
				hasPriority = true;

			element = document.createElement("field");
			element.setAttribute("name", fieldName);

			Field field = template.getField(fieldName);
			String fieldId = (field == null ? ConfigUtil.baseFieldIdMap
					.get(fieldName) : field.getId().toString());

			element.setAttribute("uuid", fieldId);

			String value = getTaskBaseAttribute(task, fieldName, das);

			if (value == null)
				value = map.get(fieldName);

			if (value != null) {
				value = XMLUtil.toSafeXMLString(value);
				element.setAttribute("value", value);
				taskElement.appendChild(element);
			} else {
				element.setAttribute("value", " - ");
				taskElement.appendChild(element);
			}
		}

		if (!hasPriority) {
			element = document.createElement("field");
			element.setAttribute("name", "修改优先级");
			element.setAttribute("uuid", "priority");

			String value = map.get("修改优先级");
			if (value == null)
				element.setAttribute("value", " - ");
			else
				element.setAttribute("value", value);

			taskElement.appendChild(element);
		}

	}

}
