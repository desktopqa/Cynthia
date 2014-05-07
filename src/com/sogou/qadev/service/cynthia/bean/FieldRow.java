package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:field row
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午3:46:28
 * @version:v1.0
 */
public class FieldRow implements Serializable {
	
	/**
	 * @Fields:serialVersionUID
	 * @Fields_Type:long
	 * @description:TODO
	 * @date:2014-5-6 下午3:46:40
	 */
	private static final long serialVersionUID = 3649817054399364460L;
	
	/**
	 * all field column belong to one row
	 */
	private List<FieldColumn> columns;
	
	public FieldRow() {
		columns = new ArrayList<FieldColumn>();
	}
	
	/**
	 * @description:add field column to row
	 * @date:2014-5-6 下午3:46:58
	 * @version:v1.0
	 * @param column
	 */
	public void addColumn(FieldColumn column)
	{
		this.columns.add(column);
	}
	
	/**
	 * @description:get all field column of row
	 * @date:2014-5-6 下午3:47:22
	 * @version:v1.0
	 * @return
	 */
	public List<FieldColumn> getFieldColumns()
	{
		return this.columns;
	}

}
