package com.sogou.qadev.service.cynthia.bean;

/**
 * @description:template type interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:43:08
 * @version:v1.0
 */
public interface TemplateType extends BaseType{

	/**
	 * @description:set template type id
	 * @date:2014-5-6 下午4:43:18
	 * @version:v1.0
	 * @param id
	 */
	public void setId(UUID id);

	/**
	 * (non-Javadoc)
	 * <p> Title:getId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.BaseType#getId()
	 */
	public UUID getId();

	/**
	 * @description:get template type name
	 * @date:2014-5-6 下午4:43:39
	 * @version:v1.0
	 * @return
	 */
	public String getName();

	/**
	 * @description:set template type name
	 * @date:2014-5-6 下午4:43:52
	 * @version:v1.0
	 * @param name
	 */
	public void setName(String name);

	/**
	 * @description: get template type description
	 * @date:2014-5-6 下午4:44:03
	 * @version:v1.0
	 * @return
	 */
	public String getDescription();

	/**
	 * @description:set template type description
	 * @date:2014-5-6 下午4:44:14
	 * @version:v1.0
	 * @param description
	 */
	public void setDescription(String description);

	/**
	 * @description:get template type display index
	 * @date:2014-5-6 下午4:44:26
	 * @version:v1.0
	 * @return
	 */
	public int getDisplayIndex();

	/**
	 * @description:set template type display index
	 * @date:2014-5-6 下午4:44:38
	 * @version:v1.0
	 * @param displayIndex
	 */
	public void setDisplayIndex(int displayIndex);
}
