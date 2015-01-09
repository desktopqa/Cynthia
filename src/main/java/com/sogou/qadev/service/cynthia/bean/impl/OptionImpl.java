package com.sogou.qadev.service.cynthia.bean.impl;

import org.w3c.dom.Document;

import com.sogou.qadev.service.cynthia.bean.Option;
import com.sogou.qadev.service.cynthia.bean.UUID;

/**
 * @description:option implements
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午4:01:16
 * @version:v1.0
 */
public class OptionImpl implements Option
{
	/**
	 * @Fields:serialVersionUID
	 * @Fields_Type:long
	 * @description:TODO
	 * @date:2014-5-6 下午4:01:25
	 */
	private static final long serialVersionUID = 3861219905584902915L;

	private UUID id = null;

	private UUID fieldId = null;

	private UUID controlOptionId = null;

	private UUID fatherOptionId = null;

	private Forbidden forbidden = Forbidden.f_permit;

	private int indexOrder = 0;

	private String name = null;

	private String description = null;

	/**
	 * <h1> Title:</h1>
	 * <p> Description: init option</p>
	 * @date：2014-5-6 
	 * @param id
	 * @param fieldId
	 */
	public OptionImpl(UUID id, UUID fieldId)
	{
		this.id = id;
		this.fieldId = fieldId;
	}
	
	public OptionImpl(UUID id, UUID fieldId, String name)
	{
		this.id = id;
		this.fieldId = fieldId;
		this.name = name;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:clone</p>
	 * @return
	 * @see java.lang.Object#clone()
	 */
	public Option clone()
	{
		OptionImpl optionImpl = new OptionImpl(this.id, this.fieldId);

		optionImpl.controlOptionId = controlOptionId;
		optionImpl.fatherOptionId = fatherOptionId;
		optionImpl.forbidden = forbidden;
		optionImpl.indexOrder = indexOrder;
		optionImpl.name = name;
		optionImpl.description = description;

		return optionImpl;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getControlOptionId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Option#getControlOptionId()
	 */
	public UUID getControlOptionId()
	{
		return this.controlOptionId;
	}
	
	/**
	 * (non-Javadoc)
	 * <p> Title:setControlOptionId</p>
	 * @param controlOptionId
	 * @see com.sogou.qadev.service.cynthia.bean.Option#setControlOptionId(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void setControlOptionId(UUID controlOptionId)
	{
		this.controlOptionId = controlOptionId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getFatherOptionId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Option#getFatherOptionId()
	 */
	public UUID getFatherOptionId()
	{
		return this.fatherOptionId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setFatherOptionId</p>
	 * @param fatherOptionId
	 * @see com.sogou.qadev.service.cynthia.bean.Option#setFatherOptionId(com.sogou.qadev.service.cynthia.bean.UUID)
	 */
	public void setFatherOptionId(UUID fatherOptionId)
	{
		this.fatherOptionId = fatherOptionId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getFieldId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Option#getFieldId()
	 */
	public UUID getFieldId()
	{
		return this.fieldId;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getId</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Option#getId()
	 */
	public UUID getId()
	{
		return this.id;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getName</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Option#getName()
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getForbidden</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Option#getForbidden()
	 */
	public Forbidden getForbidden()
	{
		return this.forbidden;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setForbidden</p>
	 * @param forbidden
	 * @see com.sogou.qadev.service.cynthia.bean.Option#setForbidden(com.sogou.qadev.service.cynthia.bean.Option.Forbidden)
	 */
	public void setForbidden(Forbidden forbidden)
	{
		this.forbidden = forbidden;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getIndexOrder</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Option#getIndexOrder()
	 */
	public int getIndexOrder()
	{
		return this.indexOrder;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setIndexOrder</p>
	 * @param indexOrder
	 * @see com.sogou.qadev.service.cynthia.bean.Option#setIndexOrder(int)
	 */
	public void setIndexOrder(int indexOrder)
	{
		this.indexOrder = indexOrder;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:getDescription</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Option#getDescription()
	 */
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setDescription</p>
	 * @param description
	 * @see com.sogou.qadev.service.cynthia.bean.Option#setDescription(java.lang.String)
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:setName</p>
	 * @param name
	 * @see com.sogou.qadev.service.cynthia.bean.Option#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @description:TODO
	 * @date:2014-5-6 下午4:02:37
	 * @version:v1.0
	 * @return
	 * @throws Exception
	 */
	public Document toXMLDocument() throws Exception
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * (non-Javadoc)
	 * <p> Title:toXMLString</p>
	 * @return
	 * @see com.sogou.qadev.service.cynthia.bean.Option#toXMLString()
	 */
	public String toXMLString()
	{
		throw new UnsupportedOperationException();
	}
}
