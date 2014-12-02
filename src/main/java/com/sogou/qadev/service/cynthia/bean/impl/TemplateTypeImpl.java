package com.sogou.qadev.service.cynthia.bean.impl;

import org.w3c.dom.Document;

import com.sogou.qadev.service.cynthia.bean.TemplateType;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

/**
 * @description:template type implements
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:44:57
 * @version:v1.0
 */
public class TemplateTypeImpl implements TemplateType
{
	/**
	 * @Fields:serialVersionUID
	 * @Fields_Type:long
	 * @description:TODO
	 * @date:2014-5-6 下午4:45:07
	 */
	private static final long serialVersionUID = 5813622550811870889L;
	
	private UUID id = null;
	private String name = null;
	private String description = null;
	private int displayIndex = 0;

	public TemplateTypeImpl(){

	}

	/**
	 * <h1> Title:</h1>
	 * <p> Description:init template type</p>
	 * @date：2014-5-6 
	 * @param id
	 */
	public TemplateTypeImpl(UUID id)
	{
		this.id = id;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:clone</p>
	 * @return
	 * @see java.lang.Object#clone()
	 */
	public TemplateType clone()
	{
		TemplateTypeImpl templateTypeImpl = new TemplateTypeImpl(this.id);
		templateTypeImpl.name = this.name;
		templateTypeImpl.description = this.description;
		templateTypeImpl.displayIndex = this.displayIndex;

		return templateTypeImpl;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getName</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.TemplateType#getName()
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setId</p>
	 * @param id
	 * @see com.sogou.qadev.service.cynthia.bean.TemplateType#setId(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	@Override
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.TemplateType#getId()
	 */
	public UUID getId()
	{
		return this.id;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getDescription</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.TemplateType#getDescription()
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getDisplayIndex</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.TemplateType#getDisplayIndex()
	 */
	public int getDisplayIndex()
	{
		return this.displayIndex;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setDescription</p>
	 * @param description
	 * @see com.sogou.qadev.service.cynthia.bean.TemplateType#setDescription(java.lang.String)
	 */
	public void setDescription(String description){
		this.description = description;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setDisplayIndex</p>
	 * @param displayIndex
	 * @see com.sogou.qadev.service.cynthia.bean.TemplateType#setDisplayIndex(int)
	 */
	public void setDisplayIndex(int displayIndex){
		this.displayIndex = displayIndex;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setName</p>
	 * @param name
	 * @see com.sogou.qadev.service.cynthia.bean.TemplateType#setName(java.lang.String)
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:toXMLDocument</p>
	 * @return
	 * @throws Exception
	 * @see com.sogou.qadev.service.cynthia.bean.BaseType#toXMLDocument()
	 */
	public Document toXMLDocument() throws Exception
	{
		return XMLUtil.string2Document(toXMLString(), "UTF-8");
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:toXMLString</p>
	 * @return
	 * @throws Exception
	 * @see com.sogou.qadev.service.cynthia.bean.BaseType#toXMLString()
	 */
	public String toXMLString() throws Exception
	{
		StringBuffer xmlb = new StringBuffer(64);
		xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");

		xmlb.append("<templateType>");

		xmlb.append("<id>").append(this.getId()).append("</id>");
		xmlb.append("<name>").append(XMLUtil.toSafeXMLString(this.getName())).append("</name>");
		xmlb.append("<description>").append(XMLUtil.toSafeXMLString(this.getDescription())).append("</description>");
		xmlb.append("<displayIndex>").append(this.getDisplayIndex()).append("</displayIndex>");

		xmlb.append("</templateType>");

		return xmlb.toString();
	}
}
