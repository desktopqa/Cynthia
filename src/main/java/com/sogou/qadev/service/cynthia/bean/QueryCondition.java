package com.sogou.qadev.service.cynthia.bean;

/**
 * @description:query condition bean
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:03:53
 * @version:v1.0
 */
public class QueryCondition {

	/**
	 * query field
	 */
	private String queryField;
	/**
	 * query condition(=,>=,in,like...)
	 */
	private String queryMethod;
	
	/**
	 * query value
	 */
	private String queryValue;
	
	public QueryCondition(){};
	
	public QueryCondition(String queryField, String queryMethod,String queryValue) {
		super();
		this.queryField = queryField;
		this.queryMethod = queryMethod;
		this.queryValue = queryValue;
	}
	
	public String getQueryField() {
		return queryField;
	}
	public void setQueryField(String queryField) {
		this.queryField = queryField;
	}
	public String getQueryMethod() {
		return queryMethod;
	}
	public void setQueryMethod(String queryMethod) {
		this.queryMethod = queryMethod;
	}
	public String getQueryValue() {
		return queryValue;
	}
	public void setQueryValue(String queryValue) {
		this.queryValue = queryValue;
	}
	
}
