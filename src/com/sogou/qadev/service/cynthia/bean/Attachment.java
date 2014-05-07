package com.sogou.qadev.service.cynthia.bean;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @description:attachment interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午2:34:07
 * @version:v1.0
 */
public interface Attachment extends Serializable
{
	/**
	 * @description:get attachment creatTime
	 * @date:2014-5-6 下午2:34:20
	 * @version:v1.0
	 * @return
	 */
	public Timestamp getCreateTime();

	/**
	 * @description:set attachment creatTime
	 * @date:2014-5-6 下午2:34:38
	 * @version:v1.0
	 * @param createTime
	 */
	public void setCreateTime(Timestamp createTime);

	/**
	 * @description:get attachment create user
	 * @date:2014-5-6 下午2:34:44
	 * @version:v1.0
	 * @return
	 */
	public String getCreateUser();

	/**
	 * @description:set attachment create user
	 * @date:2014-5-6 下午2:34:56
	 * @version:v1.0
	 * @param createUser
	 */
	public void setCreateUser(String createUser);

	/**
	 * @description:get attachment id
	 * @date:2014-5-6 下午2:35:30
	 * @version:v1.0
	 * @return
	 */
	public UUID getId();

	/**
	 * @description:get file size
	 * @date:2014-5-6 下午2:35:39
	 * @version:v1.0
	 * @return
	 */
	public long getSize();

	/**
	 * @description:set file size
	 * @date:2014-5-6 下午2:35:48
	 * @version:v1.0
	 * @param size
	 */
	public void setSize(long size);

	/**
	 * @description:get attachment name
	 * @date:2014-5-6 下午2:35:55
	 * @version:v1.0
	 * @return
	 */
	public String getName();

	/**
	 * @description:set attachment name
	 * @date:2014-5-6 下午2:36:08
	 * @version:v1.0
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * @description:set file id()
	 * @date:2014-5-6 下午2:36:16
	 * @version:v1.0
	 * @param fileId
	 */
	public void setFileId(String fileId);
	
	/**
	 * @description:get file id
	 * @date:2014-5-6 下午2:36:36
	 * @version:v1.0
	 * @return
	 */
	public String getFileId();
	
	/**
	 * @description:get file data bytes
	 * @date:2014-5-6 下午2:36:47
	 * @version:v1.0
	 * @return
	 */
	public byte[] getData();

	/**
	 * @description:set file data bytes
	 * @date:2014-5-6 下午2:36:56
	 * @version:v1.0
	 * @param content
	 */
	public void setData(byte[] content);

}
