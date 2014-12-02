package com.sogou.qadev.service.cynthia.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sogou.qadev.service.cynthia.bean.Filter;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

/**
 * @description:filter processor
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午12:09:30
 * @version:v1.0
 */
public class FilterAccessSession
{
	private static FilterAccessSession instance = null;
	
	public static FilterAccessSession getInstance()
	{
		if (instance == null)
		{
			instance = new FilterAccessSession();
		}

		return instance;
	}
	
	/**
	 * @description:return filter group field map
	 * @date:2014-5-6 下午12:09:49
	 * @version:v1.0
	 * @param filter
	 * @return
	 */
	public Map<String, String> getGroupFieldMap(Filter filter)
	{
		Map<String, String> groupFieldMap = new HashMap<String, String>();
		if (filter == null) {
			return null;
		}
		
		Document doc = null;
		try
		{
			doc = XMLUtil.string2Document(filter.getXml(),"UTF-8");
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		if(doc == null)
			return null;
		
		List<Node> orderFieldNodeList = XMLUtil.getNodes(doc,"query/template/order/field");
		if(orderFieldNodeList == null||orderFieldNodeList.size()==0)
		{
			return null;
		}
		
		int indent = 0;
		Node orderNode = XMLUtil.getSingleNode(doc, "query/template/order");
		if(orderNode != null){
			try{
				indent = Integer.parseInt(XMLUtil.getAttribute(orderNode, "indent"));
			}
			catch(Exception e){
			}
		}
		
		if (indent == 0 || orderFieldNodeList.size() < indent) {
			return groupFieldMap;
		}else {
			Node groupFieldNode = orderFieldNodeList.get(indent -1 );
			if (groupFieldNode != null) {
				String fieldIdStr = XMLUtil.getAttribute(groupFieldNode, "id");
				String fieldName = XMLUtil.getAttribute(groupFieldNode, "name");
				groupFieldMap.put(fieldIdStr, fieldName);
			}
		}
		return groupFieldMap;
	}
}
