package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;


/**
 * @description:data process entry
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午3:12:34
 * @version:v1.0
 * @param <T>
 */
final public class DataAccessEntry<T> implements Serializable
{
	/**
	 * @Fields:serialVersionUID
	 * @Fields_Type:long
	 * @description:TODO
	 * @date:2014-5-6 下午3:12:31
	 */
	private static final long serialVersionUID = 2385067230092320047L;

	private DataAccessAction action = null;

	private T data = null;

	/**
	 * @description:return process action
	 * @date:2014-5-6 下午3:12:49
	 * @version:v1.0
	 * @return
	 */
	public DataAccessAction getAction()
	{
		return action;
	}

	public void setAction(DataAccessAction action)
	{
		this.action = action;
	}

	public T getData()
	{
		return data;
	}

	public void setData(T data)
	{
		this.data = data;
	}
}