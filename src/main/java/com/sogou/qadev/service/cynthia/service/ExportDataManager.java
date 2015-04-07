package com.sogou.qadev.service.cynthia.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.Filter;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.QueryCondition;
import com.sogou.qadev.service.cynthia.bean.TagBean;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.FilterQueryManager.ExportType;
import com.sogou.qadev.service.cynthia.util.CommonUtil;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

/**
 * @description:export data processor (xml,excel,mail)
 * @author:liming
 * @mail:liming@sogou-inc.com
 * @date:2014-5-6 下午12:06:36
 * @version:v1.0
 */
public class ExportDataManager {
	
	private static String splitAndFilterString(String input) {
        if (input == null || input.trim().equals("")) {
            return "";
        }

        String str = input.replaceAll("<[a-zA-Z]+[1-9]?[^><]*>", "").replaceAll("</[a-zA-Z]+[1-9]?>", "");
        return str;
 	}

	/**
	 * @description:get mail header
	 * @date:2014-5-6 下午12:06:58
	 * @version:v1.0
	 * @return
	 */
	private static String getMailHtmlHeader(){
		
		StringBuffer header = new StringBuffer();
		
		header.append("<html>");
		header.append("<head>");
		header.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\"/>");
		header.append("<style type=\"text/css\">");
		header.append("table{border-collapse: collapse; border: 1px solid #CCCCCC; font-size: 100%;margin-top: 0em; margin-left: 5px; margin-bottom: 0em;width: 800px;table-layout:fixed;}");
		header.append("th{border-right: 1px solid #CCCCCC;text-align: center;	white-space:nowrap;	background: #4EA9E4; margin: .25em;vertical-align: center;}");
		header.append("tr{vertical-align: center;  background: #eeeeff;}");
		header.append("td{border-right: 1px solid #CCCCCC; margin: .25em;vertical-align: center;  border-bottom: 1px solid #CCCCCC; word-wrap: break-word;word-break:break-all;max-width: 120px;display : table-cell;}");
		header.append("body{margin: 0;padding: 0;background: #f6f6f6;}");
		header.append("body,div,p,span{margin-top:0px; margin-bottom:0px; color: #333;font-size: 12px;line-height: 150%;font-family: Verdana, Arial, Helvetica, sans-serif;}");
		header.append("</style>");
		header.append("</head>");
		header.append("<body>");
		return header.toString();
	}
	
	/**
	 * @description:get mail footer
	 * @date:2014-5-6 下午12:07:07
	 * @version:v1.0
	 * @return
	 */
	private static String getMailHtmlFooter(){
		StringBuffer footer = new StringBuffer();
		footer.append("</body>");
		footer.append("</html>");
		return footer.toString();
	}

	private static String getTableHeader(Map<String, String> displayNamesMap){
		StringBuffer tableHeaderBuffer = new StringBuffer();
		tableHeaderBuffer.append("<tr>");
		tableHeaderBuffer.append("<th style=\"width:60px;\">").append("序号").append("</th>");
		for (String fieldName : displayNamesMap.keySet()) {
			if(fieldName.equals("标题") || fieldName.equals("描述")){
				tableHeaderBuffer.append("<th style='width:" + (displayNamesMap.get(fieldName) != null && !displayNamesMap.get(fieldName).equals("") ? displayNamesMap.get(fieldName) : "500px" ) + ";'>").append(XMLUtil.toSafeXMLString(fieldName)).append("</th>");
			}else{
				tableHeaderBuffer.append("<th style='width:" + (displayNamesMap.get(fieldName) != null && !displayNamesMap.get(fieldName).equals("") ? displayNamesMap.get(fieldName) : "140px" ) + ";'>").append(XMLUtil.toSafeXMLString(fieldName)).append("</th>");
			}
		}
		tableHeaderBuffer.append("</tr>");
		return tableHeaderBuffer.toString();
	}

	private static String getMailHtmlData(Data[] allDatas , Map<String, String> displayNameMap , String indentFieldName, DataAccessSession das , boolean isSysFilter){
		
		String[] displayNames = displayNameMap.keySet().toArray(new String[0]);
		if (allDatas == null || allDatas.length == 0) {
			return "<p style=\"color:red\">过滤器没有筛选出任何数据</p>";
		}
		
		StringBuffer dataBuffer = new StringBuffer();
		String tableHeader = getTableHeader(displayNameMap);

		Map<UUID, Template> templateMap = new HashMap<UUID, Template>();
 		Map<UUID, Flow> flowMap = new HashMap<UUID, Flow>();
 		Map<String, String> userAliasMap = new HashMap<String, String>();
 		
 		if (indentFieldName == null || indentFieldName.length() == 0) {
 			dataBuffer.append("<table>");
 			dataBuffer.append(tableHeader);
		}
		
		String currentIndentFieldValue = "";  //当前分组字段值
		
		//内容
		for (int i = 0; i < allDatas.length; i++) {
			Data task = allDatas[i];
			if (task == null) {
				continue;
			}
			if (templateMap.get(task.getTemplateId()) == null) {
				Template template = das.queryTemplate(task.getTemplateId());
				if (template != null) {
					templateMap.put(task.getTemplateId(), template);
				}
			}
			
			Template template = templateMap.get(task.getTemplateId());
			
			if (flowMap.get(template.getFlowId()) == null) {
				Flow flow = das.queryFlow(template.getFlowId());
				if (flow != null) {
					flowMap.put(flow.getId(), flow);
				}
			}
			
			Flow flow = flowMap.get(template.getFlowId());
			
			List<String> allShowList = new ArrayList<String>();
			allShowList.addAll(Arrays.asList(displayNames));
			allShowList.add(indentFieldName);
			Map<String, String> displayMap = FilterQueryManager.getShowFieldValueMap(allShowList.toArray(new String[0]), task , template ,flow ,das ,ExportType.html , userAliasMap , isSysFilter); 
			
			//有分组字段情况 
			if (indentFieldName != null && indentFieldName.length() >0) {  
				if (i == 0) {
					currentIndentFieldValue = displayMap.get(indentFieldName);
					dataBuffer.append("<h5>").append(indentFieldName + " : " + currentIndentFieldValue).append("</h5>");
					dataBuffer.append("<table>");
		 			dataBuffer.append(tableHeader);
				}else {
					if (!currentIndentFieldValue.equals(displayMap.get(indentFieldName))) {
						currentIndentFieldValue = displayMap.get(indentFieldName);
						//下一个分组
						dataBuffer.append("</table>");
						dataBuffer.append("<h5>").append(indentFieldName + " : " + currentIndentFieldValue).append("</h5>");
						dataBuffer.append("<table>");
						dataBuffer.append(tableHeader);
					}
				}
			}
			
			dataBuffer.append("<tr>");
			dataBuffer.append("<td>").append(String.valueOf(i+1)).append("</td>");
			
			for (int j = 0; j < displayNames.length; j++) {
				if (displayNames[j] != null && displayNames[j].equals("标题")) {
					dataBuffer.append("<td align=\"left\">").append("<a href=\"" + ConfigUtil.getCynthiaWebRoot() + "taskManagement.html?operation=read&taskid=" + task.getId().getValue() + "\"")
					.append(">" + XMLUtil.toSafeXMLString(displayMap.get(displayNames[j])) + "</a>").append("</td>");
				}else {
					dataBuffer.append("<td>").append(XMLUtil.toSafeXMLString(displayMap.get(displayNames[j]))).append("</td>");
				}
			}
			dataBuffer.append("</tr>");
		}
		dataBuffer.append("</table>");
		return dataBuffer.toString();
	}
	
	/**
	 * @description:return mail string of filter
	 * @date:2014-5-6 下午12:07:44
	 * @version:v1.0
	 * @param das
	 * @param keyId
	 * @param filter
	 * @param userName
	 * @return
	 */
	public static String exportMailHtmlFilter(DataAccessSession das, Long keyId, Filter filter,String userName){
		boolean isSysFilter = FilterQueryManager.isSysFilter(filter.getId().getValue());
		if (isSysFilter) {
			try {
				FilterQueryManager.initFilterEnv(filter,keyId, userName, null, das);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		StringBuffer htmlBuffer = new StringBuffer();
		htmlBuffer.append(getMailHtmlHeader().toString());
		Map<String, String> displayNameMap = FilterQueryManager.getDisplayFieldAndWidth(filter.getXml(), das);
		Data[] allDatas = das.getDataFilter().queryDatas(filter.getXml(), 1, 100, null, null,null);  //取前100条
		String indentFieldName = FilterQueryManager.getFilterIndentFieldName(filter.getXml(), das);
		htmlBuffer.append(getMailHtmlData(allDatas, displayNameMap ,indentFieldName,  das ,isSysFilter));
		htmlBuffer.append("<br><a href=\"" + ConfigUtil.getCynthiaWebRoot() + "index.html?filterId=" + filter.getId().getValue() + "\">过滤器：" + filter.getName() +"</a><br/>");
//		htmlBuffer.append("<a href=\"" + ConfigUtil.getCynthiaWebRoot() + "filter/exportFilter.jsp?filterId=" + filter.getId().getValue() + "\">Excel下载地址</a>");
		htmlBuffer.append(getMailHtmlFooter().toString());
		String html = htmlBuffer.toString();
		html = html.replace("</td>", "</td>\n").replace("</tr>", "</tr>\n"); //邮件发送系统bug
		return html;
	}
	
	/**
	 * @description:return xml string of filter export
	 * @date:2014-5-6 下午12:07:59
	 * @version:v1.0
	 * @param filterIdStr
	 * @return
	 */
	public static String exportXmlDataFilter(String filterIdStr){
		DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
		Filter filter = das.queryFilter(DataAccessFactory.getInstance().createUUID(filterIdStr));
		boolean isSysFilter = FilterQueryManager.isSysFilter(filter.getId().getValue());
		if (isSysFilter) {
			try {
				FilterQueryManager.initFilterEnv(filter,ConfigUtil.magic, ConfigUtil.sysEmail, null, das);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
 		Map<UUID, Template> templateMap = new HashMap<UUID, Template>();
 		Map<UUID, Flow> flowMap = new HashMap<UUID, Flow>();
 		Map<String, String> userAliasMap = new HashMap<String, String>();
 		
 		Data[] allDatas = das.getDataFilter().queryDatas(filter.getXml(),null);  //取过滤器所有数据 
 		
 		String[] displayNames = FilterQueryManager.getDisplayNamesFilter(filter.getXml(), das);  //显示字段名
 		StringBuffer plainBuffer = new StringBuffer();
 		plainBuffer.append("<root>");
 		for (int i = 0; i < allDatas.length; i++) {
 			Data task = allDatas[i];
 			
			if (task == null) {
				continue;
			}
			plainBuffer.append("<data>");
			if (templateMap.get(task.getTemplateId()) == null) {
				Template template = das.queryTemplate(task.getTemplateId());
				if (template != null) {
					templateMap.put(task.getTemplateId(), template);
				}
			}
			
			Template template = templateMap.get(task.getTemplateId());
			
			if (flowMap.get(template.getFlowId()) == null) {
				Flow flow = das.queryFlow(template.getFlowId());
				if (flow != null) {
					flowMap.put(flow.getId(), flow);
				}
			}
			
			Flow flow = flowMap.get(template.getFlowId());
			
			Map<String, String> displayMap = FilterQueryManager.getShowFieldValueMap(displayNames , task , template ,flow ,das ,ExportType.excel , userAliasMap ,isSysFilter); 
			
			for(int j=0;j<displayNames.length;j++)
			{
				plainBuffer.append("<fieldname>").append(XMLUtil.toSafeXMLString(displayNames[j])).append("</fieldname>");
				plainBuffer.append("<fieldvalue>").append(XMLUtil.toSafeXMLString(displayMap.get(displayNames[j]))).append("</fieldvalue>");
			}
			plainBuffer.append("</data>");
		}
 		
 		plainBuffer.append("</root>");
 		return plainBuffer.toString();
	}
	
	/**
	 * @description:export excel of filter
	 * @date:2014-5-6 下午12:08:13
	 * @version:v1.0
	 * @param das
	 * @param filter
	 * @param keyId
	 * @param userName
	 * @param dataIds
	 * @param beforeNum
	 * @param outputStream
	 * @throws Exception
	 */
 	@SuppressWarnings("deprecation")
	public static void excelExport(DataAccessSession das, Filter filter, Long keyId , String userName, String[] dataIds, int beforeNum, OutputStream outputStream) throws Exception
 	{
 		boolean isSysFilter = FilterQueryManager.isSysFilter(filter.getId().getValue());
 		if (isSysFilter) {
			try {
				FilterQueryManager.initFilterEnv(filter,keyId, userName, null, das);
			} catch (Exception e) {
				e.printStackTrace();
			}
 		}
 		
 		Data[] allDatas = null;
 		if(dataIds != null && dataIds.length > 0){
 			List<QueryCondition> allQueryConditions = new ArrayList<QueryCondition>();
 			QueryCondition qc = new QueryCondition("id", "in", "(" + dataIds[0] +")");
 			allQueryConditions.add(qc);
 			allDatas = das.getDataFilter().queryDatas(filter.getXml(), 1, 10000, allQueryConditions);  //取选中的数据
 		}else if (beforeNum > 0) {
 			allDatas = das.getDataFilter().queryDatas(filter.getXml(), 1, beforeNum,null);  //取前beforeNum条
		}else {
			allDatas = das.getDataFilter().queryDatas(filter.getXml(), 1, 10000 ,null);  //默认取前10000条
		}
 		
 		String[] displayNames = FilterQueryManager.getDisplayFields(filter.getXml(), das);
 		
 		Map<String,String> userClassifyDataMap = das.getUserClassifyDataMap(userName);
 		List<TagBean> allTagList = das.getAllTag(userName);
 		Map<String, String> tagMap = new HashMap<String, String>();
 		for (TagBean tagBean : allTagList) {
			tagMap.put(tagBean.getId(), tagBean.getTagName());
		}
 		getExcelOutputStream(allDatas, displayNames, isSysFilter,userClassifyDataMap, tagMap,outputStream);
 		
 	}
 	
 	/**
 	 * @description:return outputstream of filter export
 	 * @date:2014-5-6 下午12:08:27
 	 * @version:v1.0
 	 * @param allDatas
 	 * @param displayNames
 	 * @param isSysFilter
 	 * @param userClassifyDataMap
 	 * @param tagMap
 	 * @param outputStream
 	 * @throws IOException
 	 */
 	public static void  getExcelOutputStream(Data[] allDatas, String[] displayNames, boolean isSysFilter,Map<String,String> userClassifyDataMap,Map<String, String> tagMap,OutputStream outputStream) throws IOException{
 		DataAccessSession das = DataAccessFactory.getInstance().getSysDas();
 		Map<UUID, Template> templateMap = new HashMap<UUID, Template>();
 		Map<UUID, Flow> flowMap = new HashMap<UUID, Flow>();
 		Map<String, String> userAliasMap = new HashMap<String, String>();
 		
 		HSSFWorkbook wb  =   new HSSFWorkbook();
		HSSFSheet sheet  =  wb.createSheet("sheet1");

		HSSFRow firstRow = sheet.createRow((short)0);
		HSSFCell cell = firstRow.createCell((short)0);
		cell.setCellValue("编号");
		for(int j=0;j<displayNames.length;j++)
		{
			HSSFCell tempCell = firstRow.createCell((short)(j+1));
			tempCell.setCellValue(displayNames[j]);
		}
		
		
		HSSFCellStyle linkStyle = wb.createCellStyle();
		HSSFFont cellFont= wb.createFont();
		cellFont.setUnderline((byte) 1);
		cellFont.setColor(HSSFColor.BLUE.index);
		linkStyle.setFont(cellFont);

		boolean hasTag = false;
		for(int i = 0 ;i < allDatas.length ; i++)
		{
			Data task = allDatas[i];
			if (task == null) {
				continue;
			}
			if (templateMap.get(task.getTemplateId()) == null) {
				Template template = das.queryTemplate(task.getTemplateId());
				if (template != null) {
					templateMap.put(task.getTemplateId(), template);
				}
			}
			
			Template template = templateMap.get(task.getTemplateId());
			
			if (flowMap.get(template.getFlowId()) == null) {
				Flow flow = das.queryFlow(template.getFlowId());
				if (flow != null) {
					flowMap.put(flow.getId(), flow);
				}
			}
			
			Flow flow = flowMap.get(template.getFlowId());
			
			
			Map<String, String> displayMap = FilterQueryManager.getShowFieldValueMap(displayNames , task , template ,flow ,das ,ExportType.excel , userAliasMap ,isSysFilter); 
			
			HSSFRow dataRow = sheet.createRow((short)(i+1));
			
			//编号
			HSSFCell displayCellId = dataRow.createCell((short)(0));
			displayCellId.setCellType(HSSFCell.CELL_TYPE_STRING);
			displayCellId.setCellValue(task.getId().getValue());
			
			for(int j=0;j<displayNames.length;j++)
			{
				try
				{
					if (displayNames[j] != null && displayNames[j].equals("标题")) {
						//标题以超链接形式展示
						HSSFHyperlink link = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
					    link.setAddress(ConfigUtil.getCynthiaWebRoot() + "taskManagement.html?operation=read&taskid=" + task.getId().getValue());
					    HSSFCell numberCell = dataRow.createCell((short)(j+1));
					    numberCell.setCellStyle(linkStyle);
					    numberCell.setCellValue(task.getTitle());
					    numberCell.setHyperlink(link);// 设定单元格的链接
					}else {
						HSSFCell displayCell = dataRow.createCell((short)(j+1));
						String cellValue = splitAndFilterString(CynthiaUtil.getXMLStr(displayMap.get(displayNames[j])));
						if(cellValue.length()>32760)
							continue;
						
						if (CommonUtil.isPosNum(cellValue) || (cellValue != null && cellValue.equals("0"))) {  //设置为数字格式
							try{
								displayCell.setCellValue(Integer.parseInt(cellValue));
							}catch(Exception e){
								displayCell.setCellValue(cellValue);
							}
						}else {
							displayCell.setCellValue(cellValue);
						}
					}
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
			
			if (userClassifyDataMap.get(task.getId().getValue()) != null) {
				hasTag = true;
				HSSFCell displayCell = dataRow.createCell((short)(displayNames.length+1));
				StringBuffer tagBuffer = new StringBuffer();
				String[] allTag = userClassifyDataMap.get(task.getId().getValue()).split(",");
				for (String tagId : allTag) {
					tagBuffer.append(tagBuffer.length() > 0 ? "," : "").append(tagMap.get(tagId));
				}
				displayCell.setCellValue(tagBuffer.toString());
			}
			
		}
		
		//有标签数据才添加标签列
		if (hasTag) {
			HSSFCell tempCell = firstRow.createCell((short)(displayNames.length+1));
			tempCell.setCellValue("标签");
		}
		
		wb.write(outputStream);
 		
 	}
 	
 	/**
 	 * @function：copy data to Clipbrd
 	 * @modifyTime：2013-10-23 下午7:35:22
 	 * @author：李明
 	 * @email: liming@sogou-inc.com
 	 * @param das
 	 * @param filter
 	 * @param keyId
 	 * @param userName
 	 * @param copyNumStr :copy data count,if null copy all data
 	 * @return
 	 */
 	public static String copyFilterDataToClipbrd(DataAccessSession das, Filter filter, Long keyId , String userName, String copyNumStr){
 		boolean isSysFilter = FilterQueryManager.isSysFilter(filter.getId().getValue());
 		if (isSysFilter) {
			try {
				FilterQueryManager.initFilterEnv(filter,keyId, userName, null, das);
			} catch (Exception e) {
				e.printStackTrace();
			}
 		}
 		
 		Data[] allDatas = null;
 		Map<UUID, Template> templateMap = new HashMap<UUID, Template>();
 		Map<UUID, Flow> flowMap = new HashMap<UUID, Flow>();
 		Map<String, String> userAliasMap = new HashMap<String, String>();
 		
 		if (copyNumStr == null) {
 			allDatas = das.getDataFilter().queryDatas(filter.getXml() , 1 , 10000,null);  //取过滤器所有数据 
		}else {
			int copyNum = 0;
			copyNum = Integer.valueOf(copyNumStr);
			if (copyNum == 0) {
				return "";
			}else {
				allDatas = das.getDataFilter().queryDatas(filter.getXml() , 1 , copyNum ,null);
			}
		}
 		
 		String[] displayNames = FilterQueryManager.getDisplayNamesFilter(filter.getXml(), das);  //显示字段名
 		StringBuffer plainBuffer = new StringBuffer();
 		
 		plainBuffer.append(filter.getName());
 		plainBuffer.append("\r\n");
 		
 		plainBuffer.append("编号");
 		for (int i = 0; i < displayNames.length; i++) {
			plainBuffer.append("	").append(displayNames[i]);
		}
 		plainBuffer.append("\r\n");
 		for (int i = 0; i < allDatas.length; i++) {
 			Data task = allDatas[i];
			if (task == null) {
				continue;
			}
			if (templateMap.get(task.getTemplateId()) == null) {
				Template template = das.queryTemplate(task.getTemplateId());
				if (template != null) {
					templateMap.put(task.getTemplateId(), template);
				}
			}
			
			Template template = templateMap.get(task.getTemplateId());
			
			if (flowMap.get(template.getFlowId()) == null) {
				Flow flow = das.queryFlow(template.getFlowId());
				if (flow != null) {
					flowMap.put(flow.getId(), flow);
				}
			}
			
			Flow flow = flowMap.get(template.getFlowId());
			
			Map<String, String> displayMap = FilterQueryManager.getShowFieldValueMap(displayNames , task , template ,flow ,das ,ExportType.excel , userAliasMap ,isSysFilter); 
			
			plainBuffer.append(task.getId().getValue());
			
			for(int j=0;j<displayNames.length;j++)
			{
				String cellValue = splitAndFilterString(CynthiaUtil.getXMLStr(displayMap.get(displayNames[j])));
				if(cellValue.equals("-2147483648") || cellValue.equals("-9223372036854775808") || cellValue.equals("1.4E-45") || cellValue.equals("4.9E-324")){
					cellValue = "-";
				}else{
					cellValue = cellValue.replaceAll("\\<.*?>","");
					cellValue = cellValue.replaceAll("\\s*", "");
				}
				plainBuffer.append("	").append(cellValue);
			}
			plainBuffer.append("\r\n");
		}
 		
 		return plainBuffer.toString();
 	}
}
