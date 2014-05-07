package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:field column
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午3:44:25
 * @version:v1.0
 */
public class FieldColumn implements Serializable{
	
	/**
	 * @Fields:serialVersionUID
	 * @Fields_Type:long
	 * @description:TODO
	 * @date:2014-5-6 下午3:46:17
	 */
	private static final long serialVersionUID = -1132748374781195055L;
	
	/**
	 *	all fields belong to one column 
	 */
	private List<Field> fields;
	
	public FieldColumn() {
		fields = new ArrayList<Field>();
	}
	
	/**
	 * @description:add a field to column
	 * @date:2014-5-6 下午3:45:46
	 * @version:v1.0
	 * @param field
	 */
	public void addField(Field field)
	{
		this.fields.add(field);
	}
	
	/**
	 * @description:get all field of column
	 * @date:2014-5-6 下午3:46:00
	 * @version:v1.0
	 * @return
	 */
	public List<Field> getFields()
	{
		return this.fields;
	}
}
