package com.sogou.qadev.service.cynthia.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/*
 方便json在转换timestamp类型时出现的 

 */
public class DateJsonValueProcessor implements JsonValueProcessor {
	public static final String Default_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private DateFormat dateFormat;

	public DateJsonValueProcessor() {
		try {
			dateFormat = new SimpleDateFormat(Default_DATE_PATTERN);
		} catch (Exception e) {
			dateFormat = new SimpleDateFormat(Default_DATE_PATTERN);
			e.printStackTrace();
		}
	}

	public DateJsonValueProcessor(String datePattern) {
		try {
			dateFormat = new SimpleDateFormat(datePattern);
		} catch (Exception e) {
			dateFormat = new SimpleDateFormat(Default_DATE_PATTERN);
		}
	}

	public Object processArrayValue(Object value, JsonConfig jsonConfig) {
		return process(value);
	}

	public Object processObjectValue(String key, Object value,
			JsonConfig jsonConfig) {
		return process(value);
	}

	private Object process(Object value) {
		if (value != null) {
			Date tempDate = new Date(((Timestamp) value).getTime());
			return dateFormat.format(tempDate);
		}
		return value;
	}
}