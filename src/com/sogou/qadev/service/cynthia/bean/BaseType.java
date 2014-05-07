package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;

import org.w3c.dom.Document;

/**
 * @description:base Type 
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午2:38:53
 * @version:v1.0
 */
public interface BaseType extends Serializable{
	
	/**
	 * @description:return data id
	 * @date:2014-5-6 下午2:39:04
	 * @version:v1.0
	 * @return
	 */
	public UUID getId();
	
	/**
	 * @description:clone data
	 * @date:2014-5-6 下午2:39:13
	 * @version:v1.0
	 * @return
	 */
	public BaseType clone();
	
	/**
	 * @description:convert data to document
	 * @date:2014-5-6 下午2:39:23
	 * @version:v1.0
	 * @return
	 * @throws Exception
	 */
	public Document toXMLDocument() throws Exception;
	
	/**
	 * @description:convert data to xml string
	 * @date:2014-5-6 下午2:39:38
	 * @version:v1.0
	 * @return
	 * @throws Exception
	 */
	public String toXMLString() throws Exception;
}
