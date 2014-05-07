package com.sogou.qadev.service.cynthia.service;

import java.sql.Timestamp;
import java.util.List;

import com.sogou.qadev.service.cynthia.bean.Attachment;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.QueryCondition;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.UUID;

/**
 * @description:datafilter interface
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午6:19:58
 * @version:v1.0
 */
public interface DataFilter
{
	/**
	 * @description:return data process interface
	 * @date:2014-5-6 上午11:54:53
	 * @version:v1.0
	 * @return
	 */
	public DataAccessSession getDataAccessSession(); 

	/**
	 * @description:query data by data id
	 * @date:2014-5-6 上午11:55:08
	 * @version:v1.0
	 * @param id
	 * @return
	 */
	public Data queryData(UUID id); 
	
	/**
	 * @description:query datas by filter xml
	 * @date:2014-5-6 上午11:55:18
	 * @version:v1.0
	 * @param xml
	 * @return
	 */
	public Data[] queryDatas(String xml);
	
	/**
	 * @description:query data by filter xml and other query conditions
	 * @date:2014-5-6 上午11:55:28
	 * @version:v1.0
	 * @param xml
	 * @param queryConList
	 * @return
	 */
	public Data[] queryDatas(String xml,List<QueryCondition> queryConList);
	
	/**
	 * @description:query datas by xml and limit count
	 * @date:2014-5-6 上午11:55:46
	 * @version:v1.0
	 * @param xml
	 * @param pageNumber:page number
	 * @param lineAccount:count of page
	 * @param queryConList
	 * @return
	 */
	public Data[] queryDatas(String xml, int pageNumber, int lineAccount , List<QueryCondition> queryConList);
	
	/**
	 * @description:query datas by xml and limit count ,sort
	 * @date:2014-5-6 上午11:56:27
	 * @version:v1.0
	 * @param xml
	 * @param pageNumber
	 * @param lineAccount
	 * @param sort
	 * @param dir
	 * @param queryConList
	 * @return
	 */
	public Data[] queryDatas(String xml, int pageNumber, int lineAccount, String sort, String dir , List<QueryCondition> queryConList);

	/**
	 * @description:query all createUsers by template type
	 * @date:2014-5-6 上午11:56:41
	 * @version:v1.0
	 * @param templateTypeId
	 * @return
	 */
	public String[] queryTemplateTypeCreateUsers(UUID templateTypeId);
	
	/**
	 * @description:query all assignusers by template type
	 * @date:2014-5-6 上午11:57:01
	 * @version:v1.0
	 * @param templateTypeId
	 * @return
	 */
	public String[] queryTemplateTypeAssignUsers(UUID templateTypeId);
	
	/**
	 * @description:query all status names by template type
	 * @date:2014-5-6 上午11:57:13
	 * @version:v1.0
	 * @param templateTypeId
	 * @return
	 */
	public String[] queryTemplateTypeStats(UUID templateTypeId);
	
	/**
	 * @description:query datas by template id
	 * @date:2014-5-6 上午11:57:33
	 * @version:v1.0
	 * @param templateId
	 * @param needLog:if need log
	 * @param startTime:createTime start
	 * @param endTime:createTime end
	 * @return
	 */
	public Data[] queryTemplateDatas(UUID templateId , boolean needLog , Timestamp startTime , Timestamp endTime);
	
	/**
	 * @description:query datas by template id and other query conditions
	 * @date:2014-5-6 上午11:58:04
	 * @version:v1.0
	 * @param templateId
	 * @param needLog
	 * @param startTime:createTime start
	 * @param endTime:createTime end
	 * @param allQueryList
	 * @return
	 */
	public Data[] queryTemplateDatas(UUID templateId , boolean needLog , Timestamp startTime, Timestamp endTime , List<QueryCondition> allQueryList);
	
	/**
	 * @description:query all createusers by template
	 * @date:2014-5-6 上午11:58:29
	 * @version:v1.0
	 * @param templateId
	 * @return
	 */
	public String[] queryTemplateCreateUsers(UUID templateId);
	
	/**
	 * @description:query all assignUsers by template
	 * @date:2014-5-6 上午11:58:45
	 * @version:v1.0
	 * @param templateId
	 * @return
	 */
	public String[] queryTemplateAssignUsers(UUID templateId);
	
	/**
	 * @description:query all status by template
	 * @date:2014-5-6 上午11:58:53
	 * @version:v1.0
	 * @param templateId
	 * @return
	 */
	public Stat[] queryTemplateStats(UUID templateId);
	
	/**
	 * @description:query all reference datas of template and field
	 * @date:2014-5-6 上午11:59:04
	 * @version:v1.0
	 * @param templateId
	 * @param fieldId
	 * @return
	 */
	public Data[] queryTemplateFieldReferences(UUID templateId, UUID fieldId);
	
	/**
	 * @description:query all attachments of template
	 * @date:2014-5-6 上午11:59:23
	 * @version:v1.0
	 * @param templateId
	 * @param fieldId
	 * @return
	 */
	public Attachment[] queryTemplateFieldAttachments(UUID templateId, UUID fieldId);

	/**
	 * @description:query datas by template and lastmodifytime
	 * @date:2014-5-6 上午11:59:41
	 * @version:v1.0
	 * @param templateId
	 * @param needLog
	 * @param startTime:lastmodifytime start
	 * @param endTime:lastmodifytime end
	 * @return
	 */
	public Data[] queryTemplateDatasByLastModifyTime(UUID templateId,boolean needLog, Timestamp startTime, Timestamp endTime);

	/**
	 * @description:query datas by template id and other query conditions
	 * @date:2014-5-6 下午12:00:15
	 * @version:v1.0
	 * @param templateId
	 * @param queryConditions
	 * @param needLog
	 * @return
	 */
	public Data[] queryDatas(UUID templateId, List<QueryCondition> queryConditions, boolean needLog);
	
}
