package com.sogou.qadev.service.cynthia.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.sogou.qadev.cache.impl.FieldNameCache;
import com.sogou.qadev.service.cynthia.bean.Attachment;
import com.sogou.qadev.service.cynthia.bean.Data;
import com.sogou.qadev.service.cynthia.bean.Field;
import com.sogou.qadev.service.cynthia.bean.Field.DataType;
import com.sogou.qadev.service.cynthia.bean.Field.Type;
import com.sogou.qadev.service.cynthia.bean.Filter;
import com.sogou.qadev.service.cynthia.bean.Flow;
import com.sogou.qadev.service.cynthia.bean.Option;
import com.sogou.qadev.service.cynthia.bean.Pair;
import com.sogou.qadev.service.cynthia.bean.Stat;
import com.sogou.qadev.service.cynthia.bean.Template;
import com.sogou.qadev.service.cynthia.bean.UUID;
import com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL;
import com.sogou.qadev.service.cynthia.factory.DataAccessFactory;
import com.sogou.qadev.service.cynthia.service.impl.DataFilterMemory;
import com.sogou.qadev.service.cynthia.util.ConfigUtil;
import com.sogou.qadev.service.cynthia.util.CynthiaUtil;
import com.sogou.qadev.service.cynthia.util.XMLUtil;

public class BugTrendManager {

	/**
	 * @function：根据过滤器查询
	 * @modifyTime：2013-11-13 下午1:34:44
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param fieldIdStr
	 * @param template
	 * @param startTimestamp
	 * @param endTimestamp
	 * @param filter
	 * @param statMap
	 * @return
	 */
	public static Map<String, Map<String, Integer>> getBugMapByFilter(String fieldIdStr, Template template , Timestamp startTimestamp, Timestamp endTimestamp, Filter filter, Map<String, String> statMap){
		
		//数据库中统计字段列名
		String fieldStaticColName = FieldNameCache.getInstance().getFieldName(fieldIdStr, template.getId().getValue());
		//数据库名
		String dataTable = TableRuleManager.getInstance().getDataTableName(template.getId());
		String dataLogTable = TableRuleManager.getInstance().getDataLogTableName(template.getId());
		
		/*画图结果Map
		  -- String 日期
		  	-- Map String 统计字段名
		  	-- Map Integer 统计数据
		 */
		Map<String, Map<String, Integer>> resultMap = new LinkedHashMap<String, Map<String, Integer>>();
		
		for(long i = startTimestamp.getTime(); i <= endTimestamp.getTime(); i += 86400000l){
			String date = new Timestamp(i).toString().split(" ")[0];
			resultMap.put(date, new LinkedHashMap<String, Integer>());
		}
		
		//初始化
		for(String keyDate : resultMap.keySet()){
			for(String statName : statMap.values()){
				resultMap.get(keyDate).put(statName, 0);
			}
			resultMap.get(keyDate).put("总数", 0); 
		}
		
		
		String sqlFilter = DataFilterMemory.getFilterSql(filter);
		
		sqlFilter = CynthiaUtil.cancelGroupOrder(sqlFilter);
		
		String sqlId = "select id " + sqlFilter.substring(sqlFilter.indexOf("from"));
		
		StringBuffer sqlBuffer = new StringBuffer();
		
		if(fieldIdStr.equals("status_id")){
			
			sqlBuffer.append("SELECT dataId, ").append(fieldStaticColName).append(", date_format(logcreateTime,'%Y-%m-%d') as logcreateTime from ")
			.append(dataLogTable).append(" where dataId in (").append(sqlId).append(")").append(" and logcreateTime >= '").append(startTimestamp.toString().split(" ")[0])
			.append("' and logcreateTime <= '").append(endTimestamp.toString().split(" ")[0]).append("' order by dataid,logActionIndex");
			
			return getStatStaticMap(sqlBuffer.toString(), statMap , resultMap);
			
		}else{
			//按创建时间统计
			sqlBuffer.append("SELECT id, ").append(fieldStaticColName).append(", date_format(createTime,'%Y-%m-%d') as createTime from ")
			.append(dataTable).append(" where id in (").append(sqlId).append(")").append(" and createTime >= '").append(startTimestamp.toString().split(" ")[0])
			.append("' and createTime <= '").append(endTimestamp.toString()).append("'order by createTime ,").append(fieldStaticColName);
		
			return getCreateTimeStaticMap(sqlBuffer.toString(), statMap , fieldStaticColName, resultMap);
		}
		
	}
	
	/**
	 * @function：statistic by task
	 * @modifyTime：2013-11-12 下午9:10:31
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param taskId 任务id
	 * @param fieldTaskStr  task statistic field id
	 * @param startTime
	 * @param endTime
	 * @param template
	 * @param statMap
	 * @return
	 */
	public static Map<String, Map<String, Integer>> getBugMapByTask(DataAccessSession das, Data taskData ,Timestamp startTimestamp, Timestamp endTimestamp, Map<String, String> statMap){
		/*画图结果Map
		  -- String 日期
		  	-- Map String 统计字段名
		  	-- Map Integer 统计数据
		 */
		Map<String, Map<String, Integer>> resultMap = new LinkedHashMap<String, Map<String, Integer>>();
		
		Template template = das.queryTemplate(taskData.getTemplateId());
		if (template == null) {
			return resultMap;
		}
		
		Field multiReferField = findMultiReferField(template);
		if (multiReferField == null) {
			return resultMap;
		}
		
		Template bugTemplate = null;
		
		UUID[] taskReferUUIDArray = taskData.getMultiReference(multiReferField.getId());
		if(taskReferUUIDArray == null || taskReferUUIDArray.length == 0)
			return resultMap;
		
		for (UUID uuid : taskReferUUIDArray) {
			Data data = das.queryData(uuid);
			if (data!=null) {
				bugTemplate = das.queryTemplate(data.getTemplateId());
			}
			if (bugTemplate != null) {
				break;
			}
		}
		
		if (bugTemplate == null) {
			System.out.println("can not find bugTemplate in BugTrendManager ! dataId :" + taskData.getId().getValue() );
			return resultMap;
		}
		
		StringBuffer allIdBuffer = new StringBuffer();
		for (UUID uuid : taskReferUUIDArray) {
			allIdBuffer.append(uuid.getValue()).append(",");
		}
		if (allIdBuffer.length() > 1) {
			allIdBuffer = allIdBuffer.deleteCharAt(allIdBuffer.length() -1);
		}
		//数据库名
		String dataLogTable = TableRuleManager.getInstance().getDataLogTableName(bugTemplate.getId());
		
		for(long i = startTimestamp.getTime(); i <= endTimestamp.getTime(); i += 86400000l){
			String date = new Timestamp(i).toString().split(" ")[0];
			resultMap.put(date, new LinkedHashMap<String, Integer>());
		}
		
		//初始化
		for(String keyDate : resultMap.keySet()){
			for(String statName : statMap.values()){
				resultMap.get(keyDate).put(statName, 0);
			}
			resultMap.get(keyDate).put("总数", 0); 
		}
		
		
		StringBuffer sqlBuffer = new StringBuffer();
		
		sqlBuffer.append("SELECT dataId, statusId, date_format(logcreateTime,'%Y-%m-%d') as logcreateTime from ")
		.append(dataLogTable).append(" where dataId in (").append(allIdBuffer.toString()).append(")").append(" and logcreateTime >= '").append(startTimestamp.toString().split(" ")[0])
		.append("' and logcreateTime <= '").append(endTimestamp.toString().split(" ")[0]).append("' order by dataid,logActionIndex");
		
		
		getStatStaticMap(sqlBuffer.toString(), statMap , resultMap);
		
		String whereString = " id in (" + allIdBuffer.toString() + ")" ;
		return getTotalOfStat(whereString , startTimestamp, endTimestamp, bugTemplate,resultMap);
	}
	

	/**
	 * @function：statistic by version
	 * @modifyTime：2013-11-12 下午9:10:31
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param taskId 任务id
	 * @param fieldTaskStr 任务统计字段id
	 * @param startTime
	 * @param endTime
	 * @param template
	 * @param statMap
	 * @return
	 */
	public static Map<String, Map<String, Integer>> getBugMapByVersion(DataAccessSession das, Template template, Field field, Option option ,Timestamp startTimestamp, Timestamp endTimestamp, Map<String, String> statMap){
		/*画图结果Map
		  -- String 日期
		  	-- Map String 统计字段名
		  	-- Map Integer 统计数据
		*/
		
		Map<String, Map<String, Integer>> resultMap = new LinkedHashMap<String, Map<String, Integer>>();
		
		if (field == null || template == null || option == null) {
			return resultMap;
		}
		
		Flow flow = das.queryFlow(template.getFlowId());
		
		Map<String, String> realStatMap = new HashMap<String, String>();
		for (Stat stat : flow.getStats()) {
			realStatMap.put(stat.getId().getValue(), stat.getName());
		}
		
		String fieldColName = FieldNameCache.getInstance().getFieldName(field.getId(), template.getId());
		
		String sqlStr = "select id from " + TableRuleManager.getInstance().getDataTableName(template.getId())
				                          + " where " + fieldColName + " = " + option.getId().getValue();
		
		String[] idArray = new DataAccessSessionMySQL().queryDataIdArray(sqlStr);
		
		StringBuffer allIdBuffer = new StringBuffer();
		for (String idStr : idArray) {
			allIdBuffer.append(idStr).append(",");
		}
		if (allIdBuffer.length() > 1) {
			allIdBuffer = allIdBuffer.deleteCharAt(allIdBuffer.length() -1);
		}
		//数据库名
		String dataLogTable = TableRuleManager.getInstance().getDataLogTableName(template.getId());
		
		for(long i = startTimestamp.getTime(); i <= endTimestamp.getTime(); i += 86400000l){
			String date = new Timestamp(i).toString().split(" ")[0];
			resultMap.put(date, new LinkedHashMap<String, Integer>());
		}
		
		//初始化
		for(String keyDate : resultMap.keySet()){
			for(String statName : statMap.values()){
				resultMap.get(keyDate).put(statName, 0);
			}
			resultMap.get(keyDate).put("总数", 0); 
		}
		
		
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("SELECT dataId, statusId, date_format(logcreateTime,'%Y-%m-%d') as logcreateTime from ")
		.append(dataLogTable).append(" where dataId in (").append(allIdBuffer.toString()).append(")").append(" and logcreateTime >= '").append(startTimestamp.toString().split(" ")[0]);
		if (endTimestamp != null) {
			sqlBuffer.append("' and logcreateTime<='").append(endTimestamp.toString());
		}
		sqlBuffer.append("' order by dataid,logActionIndex");
		
		getStatStaticMap(sqlBuffer.toString(), statMap , resultMap);
		String whereStr = " id in (" +allIdBuffer.toString()+")";
		return getTotalOfStat(whereStr,startTimestamp, endTimestamp, template,resultMap);
	}
	

	/**
	 * @function：find the multi reference field from template
	 * @modifyTime：2013-11-13 上午11:22:42
	 * @author：liming
	 * @email: liming@sogou-inc.com
	 * @param template
	 * @return
	 */
	private static Field findMultiReferField(Template template){
		for (Field field : template.getFields()) {
			if (field.getType() != null && field.getType().equals(Type.t_reference)) {
				if(field.getDataType() != null && field.getDataType().equals(DataType.dt_multiple))
					return field;
			}
		}
		return null;
	}
	
	/**
	 *   @description:save jfreechart as file
	 * @date:2014-5-6 上午10:02:01
	 * @version:v1.0
	 * @param chart
	 * @param outputPath
	 * @param weight
	 * @param height
	 */
    public static void saveAsFile(JFreeChart chart, String outputPath, int weight, int height) {      
        FileOutputStream out = null;      
        try {      
            File outFile = new File(outputPath);      
            if (!outFile.getParentFile().exists()) {      
                outFile.getParentFile().mkdirs();      
            }      
            out = new FileOutputStream(outputPath);      
            //保存为PNG      
            ChartUtilities.writeChartAsPNG(out, chart, weight, height);      
            //保存为JPEG      
            //ChartUtilities.writeChartAsJPEG(out, chart, weight, height);      
            out.flush();      
        } catch (FileNotFoundException e) {      
            e.printStackTrace();      
        } catch (IOException e) {      
            e.printStackTrace();      
        } finally {      
            if (out != null) {      
                try {      
                    out.close();      
                } catch (IOException e) {      
                    //do nothing      
                }      
            }      
        }      
    }      
    
	/**
	 * @function：draw statsitc image from result map
	 * @modifyTime：2013-11-13 下午1:35:05
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param resultMap
	 * @param graphType
	 * @param showName
	 * @return
	 */
	public static String drawImage(Map<String, Map<String, Integer>> resultMap, String graphType, String showName,String statisticId){
		if (graphType == null) {
			return "";
		}
		
		StringBuffer outBuffer = new StringBuffer();
		if (!graphType.equals("area") && statisticId != null) {
			outBuffer.append("<h3><a href=\"" + ConfigUtil.getCynthiaWebRoot() + "statistic/showMoreStatistic.html?statisticId=" + statisticId +"\">点此在网页中查看详细数据</a><h3>");
		}
		//画趋势图
		Set<String> resultStatSet = new LinkedHashSet<String>();
		for(String day : resultMap.keySet()){
			if (resultMap.get(day).keySet().size() == 1) {
				resultStatSet.add("总数");
			}else {
				resultStatSet.addAll(resultMap.get(day).keySet());
			}
		}
		
		String[] resultStatArray = resultStatSet.toArray(new String[resultStatSet.size()]);
		
		Map<String, Map<String, Integer>> graphResultMap = resultMap; 
		
		if(graphType.equals("area")){
			graphResultMap = new LinkedHashMap<String, Map<String, Integer>>();
			
			for(String date : resultMap.keySet()){
				graphResultMap.put(date, new LinkedHashMap<String, Integer>());
				String prevStat = null;
				if (resultMap.get(date).keySet().size() == 1) {
					for(String stat : resultMap.get(date).keySet()){
						graphResultMap.get(date).put("总数", resultMap.get(date).get(stat));
					}
				}else {
					for(String stat : resultMap.get(date).keySet()){
						if(stat.equals("总数")){
							graphResultMap.get(date).put(stat, resultMap.get(date).get(stat));
							continue;
						}
						
						if(prevStat == null){
							graphResultMap.get(date).put(stat, resultMap.get(date).get(stat));
						}
						else{
							graphResultMap.get(date).put(stat, graphResultMap.get(date).get(prevStat) + resultMap.get(date).get(stat));
						}
						
						prevStat = stat;
					}
				}
			}
		}else {
			graphResultMap = new LinkedHashMap<String, Map<String, Integer>>();
			
			for(String date : resultMap.keySet()){
				graphResultMap.put(date, new LinkedHashMap<String, Integer>());
				int length = resultMap.get(date).keySet().size();
				for(String stat : resultMap.get(date).keySet()){
					String mapKey = length == 1 ? "总数":stat;
					graphResultMap.get(date).put(mapKey, resultMap.get(date).get(stat));
				}
			}
		}
		
		double maxValue = 0;
		
		DefaultCategoryDataset categorySet = new DefaultCategoryDataset();
		DefaultPieDataset pieDataset = new DefaultPieDataset();
		
		if (graphType.equals("pie")) {
			if (resultStatArray.length == 1) {
				for(String day : graphResultMap.keySet()){
					Integer value = graphResultMap.get(day).get("总数");
					if(value == null){
						continue;
					}
					if (value > 0) {
						if(maxValue < value){
							maxValue = value;
						}
						pieDataset.setValue(day, value);
					}
				}
			}else {
				for(int i = resultStatArray.length - 1; i >= 0; i--){
					for(String day : graphResultMap.keySet()){
						Integer value = graphResultMap.get(day).get(resultStatArray[i]);
						if(value == null){
							continue;
						}
						if (value > 0) {
							if(maxValue < value){
								maxValue = value;
							}
							pieDataset.setValue(day, value);
						}
					}
				}
			}
			
		}else {
			if (resultStatArray.length == 1) {
				for(String day : graphResultMap.keySet()){
					Integer value = graphResultMap.get(day).get("总数");
					if(value == null){
						continue;
					}
					if (value > 0) {
						if(maxValue < value){
							maxValue = value;
						}
						categorySet.addValue(value, "总数", day);
					}
				}
			}else {
				for(int i = resultStatArray.length - 1; i >= 0; i--){
					for(String day : graphResultMap.keySet()){
						Integer value = graphResultMap.get(day).get(resultStatArray[i]);
						if(value == null){
							continue;
						}
						
						if(maxValue < value){
							maxValue = value;
						}
						categorySet.addValue(value, resultStatArray[i], day);
					}
				}
			}
			
		}
		
			
		JFreeChart chart = null;
		
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		if (graphType == null) {
			//默认线型
			chart = ChartFactory.createLineChart(null, null, null, categorySet, PlotOrientation.VERTICAL, true, true, true);
		}else {
			if (graphType.equals("area")) {
				//堆积图
//				chart = ChartFactory.createStackedAreaChart(null, null, null, categorySet, PlotOrientation.VERTICAL, false, true, true);
				chart = ChartFactory.createAreaChart(null, null, null, categorySet, PlotOrientation.VERTICAL, true, true, true);
			}else if (graphType.equals("pie")) {
				chart = ChartFactory.createPieChart(showName, pieDataset, true, true, false);
			}else if (graphType.equals("bar")) {
				chart = ChartFactory.createBarChart(null, null, null, categorySet, PlotOrientation.VERTICAL, true, true, true);
			}else {
				chart = ChartFactory.createLineChart(null, null, null, categorySet, PlotOrientation.VERTICAL, true, true, true);
			}
		}
		
		
		if (graphType.equals("pie")) {
			PiePlot plot = (PiePlot)chart.getPlot();
			plot.setCircular(false);
			
			//饼图显示比例
			plot.setNoDataMessage("无数据可供显示！"); // 没有数据的时候显示的内容  
			DecimalFormat df = new DecimalFormat("0.00%");//获得一个DecimalFormat对象，主要是设置小数问题,表示小数点后保留两位。  
			NumberFormat nf = NumberFormat.getNumberInstance();//获得一个NumberFormat对象  
			StandardPieSectionLabelGenerator sp = new StandardPieSectionLabelGenerator(  
			        "{0}:{2}", nf, df);//获得StandardPieSectionLabelGenerator对象,生成的格式，{0}表示section名，{1}表示section的值，{2}表示百分比。可以自定义  
			plot.setLabelGenerator(sp);//设置饼图显示百分比  
			
		}else {
			CategoryPlot plot = (CategoryPlot)chart.getPlot();
			plot.setDomainGridlinesVisible(true);
			
			//背景色　透明度      
	        plot.setBackgroundAlpha(0.5f);      
	        //前景色　透明度      
	        plot.setForegroundAlpha(0.5f);    
	        plot.setDomainGridlinesVisible(false);
	        
			CategoryAxis horizontalAxis = plot.getDomainAxis();
			horizontalAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
			horizontalAxis.setLowerMargin(-0.5d / (categorySet.getColumnCount() + (graphType != null && graphType.equals("area") ? -1 : 1)));
			horizontalAxis.setUpperMargin(-0.5d / (categorySet.getColumnCount() + (graphType != null && graphType.equals("area") ? -1 : 1)));
			
			NumberAxis verticalAxis = (NumberAxis)plot.getRangeAxis();
			verticalAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());
			verticalAxis.setTickUnit(new NumberTickUnit(maxValue % 30 == 0 ? maxValue / 30 : maxValue / 30 + 1));
			verticalAxis.setLowerMargin(0);
			verticalAxis.setUpperMargin(0);
		}
			
		String fileId = "";
		FileInputStream fin = null;
		try {
			File tempFile = File.createTempFile(System.currentTimeMillis() + showName , "png");
			ChartUtilities.saveChartAsPNG(tempFile, chart, 800, 400);
			//图片存储到图片服务器
			fin = new FileInputStream(tempFile);
			byte[] data = new byte[fin.available()];
			fin.read(data);
			fin.close();
			tempFile.delete();
			
			//图片上传到分布式文件系统
			//fileId = FileUpDownLoadHandler.postFile("cynthia"+showName+System.currentTimeMillis(), data);
			//图片上传到数据库,针对开源
			Attachment attachment = DataAccessFactory.getInstance().getSysDas().createAttachment(showName, data);
			fileId = ConfigUtil.getCynthiaWebRoot() + "attachment/download.jsp?method=download&id=" + attachment.getId().getValue(); 
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			StreamCloserManager.closeInputStream(fin);
		}
		
		//graph
		outBuffer.append("<h2 align=\"center\">" + XMLUtil.toSafeXMLString(showName) + "</h2>");
		outBuffer.append("<table align=\"center\" width='800' cellspacing='0' style='border-left:1px solid black;border-top:1px solid black'>");
		
		outBuffer.append("<tr>");
		outBuffer.append("<td colspan='" + (resultStatArray.length + 2) + "' style='border-right:1px solid black;border-bottom:1px solid black'>");
		
		outBuffer.append("<img src='" + fileId + "'/>");
		
		outBuffer.append("</td>");
		outBuffer.append("</tr>");
		
		//下载图片按钮
//		outBuffer.append("<tr>");
//		outBuffer.append("<td align=\"center\" colspan=\"" + (resultStatArray.length + 2) + "\" style=\"border-right:1px solid black;border-bottom:1px solid black\">");
//		outBuffer.append("<input type=\"button\" value=\"下载趋势图\" onClick=\"window.open('" + ConfigUtil.getCynthiaWebRoot() + "report/download.jsp?filename=" + filename + "&magic=jjzzwws&kid=" + ConfigUtil.magic + "')\"/>");
//		outBuffer.append("</td>");
//		outBuffer.append("</tr>");
		
		outBuffer.append("<tr>");
		
		outBuffer.append("<td style='border-right:1px solid black;border-bottom:1px solid black'>统计项</td>");
		
		for(int i = resultStatArray.length - 1; i >= 0; i--){
			outBuffer.append("<td style='border-right:1px solid black;border-bottom:1px solid black'>" + resultStatArray[i] + "</td>");
		}
		
		outBuffer.append("</tr>");
		
		String str = "";
		String tempStr = "";
		
		if (graphType.equals("area")) {
			for(String date : resultMap.keySet()){
				tempStr = "";
				tempStr+="<tr>";
				tempStr+="<td style='border-right:1px solid black;border-bottom:1px solid black'>" + date + "</td>";
				for(int i = resultStatArray.length - 1; i >= 0; i--){
					tempStr+="<td style='border-right:1px solid black;border-bottom:1px solid black'>" + resultMap.get(date).get(resultStatArray[i]) + "</td>";
				}
				tempStr+="</tr>";
				
				str = tempStr+str;
			}
		}else {
			for(String date : graphResultMap.keySet()){
				tempStr = "";
				tempStr+="<tr>";
				tempStr+="<td style='border-right:1px solid black;border-bottom:1px solid black'>" + date + "</td>";
				for(int i = resultStatArray.length - 1; i >= 0; i--){
					tempStr+="<td style='border-right:1px solid black;border-bottom:1px solid black'>" + graphResultMap.get(date).get(resultStatArray[i]) + "</td>";
				}
				tempStr+="</tr>";
				
				str = tempStr+str;
			}
		}
		
		
		outBuffer.append(str);
		outBuffer.append("</table>");
		return outBuffer.toString();
	}
	
	/**
	 * @function：通过状态统计结果
	 * @modifyTime：2013-11-13 下午1:30:45
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param sql
	 * @param statMap
	 * @return
	 */
	public static Map<String, Map<String, Integer>> getStatStaticMap(String sql, Map<String, String> statMap, Map<String, Map<String, Integer>> resultMap){
		
		//从日志动作中查询统计
		/*dataStatCreateAndEndMap
		  -- String dataId
		  	-- Map String 统计字段名
		  		-- Map String 字段开始时间
		  		-- Map String 字段结果时间
		 */
		Map<String,Map<String, List<Pair<String, String>>>> dataStatCreateAndEndMap = new LinkedHashMap<String,Map<String, List<Pair<String, String>>>>();
		
		List<Map<String,String>> resultList = DbPoolConnection.getInstance().getResultSetListBySql(sql);
		
		String currentDataId = "";
		String currentFieldValue = "";
		
		for(Map<String,String> map : resultList){
			if(dataStatCreateAndEndMap.get(map.get("dataId")) == null){
				Map<String,List<Pair<String,String>>> statCreateAndEndMap= new LinkedHashMap<String, List<Pair<String,String>>>();
				for(String statName: statMap.values()){
					statCreateAndEndMap.put(statName, new ArrayList<Pair<String,String>>());
				}
				dataStatCreateAndEndMap.put(map.get("dataId"), statCreateAndEndMap);
			}
			
			String currentStatStr = map.get("statusId") == null ? "": map.get("statusId").toString();
			
			if(currentDataId == ""){
				currentDataId = map.get("dataId");
				currentFieldValue = map.get("statusId");
				String currentStatName = statMap.get(currentFieldValue);
				if(currentStatName != null){
					//设置状态开始
					dataStatCreateAndEndMap.get(currentDataId).get(currentStatName).add(new Pair<String, String>(map.get("logcreateTime"),null));
				}
			}
			else if(!currentDataId.equals(map.get("dataId").toString())){
				//新数据开始
				currentDataId = map.get("dataId");
				currentFieldValue = map.get("statusId");
				String currentStatName = statMap.get(currentFieldValue);
				if(currentStatName != null){
					//设置状态开始
					dataStatCreateAndEndMap.get(currentDataId).get(currentStatName).add(new Pair<String, String>(map.get("logcreateTime"),null));
				}
				
			}else if((currentFieldValue == null && currentStatStr != null) ||(currentFieldValue != null && !currentFieldValue.equals(currentStatStr))){//状态更改
				//设置上一状态结果 
				String currentStatName = statMap.get(currentFieldValue);
				if(currentStatName != null){
					//设置状态开始
					List<Pair<String,String>> timeRangeList = dataStatCreateAndEndMap.get(currentDataId).get(currentStatName);
					Pair<String, String> lastPair = timeRangeList.get(timeRangeList.size() -1 );
					lastPair.setSecond(map.get("logcreateTime"));
				}
				//设置下一状态开始
				currentFieldValue = map.get("statusId");
				currentStatName = statMap.get(currentFieldValue);
				if(currentStatName != null){
					//设置状态开始
					dataStatCreateAndEndMap.get(currentDataId).get(currentStatName).add(new Pair<String, String>(map.get("logcreateTime"),null));
				}
			}
		}
		
		List<String> dateList = new ArrayList(resultMap.keySet());
		
		
		for(String dataId : dataStatCreateAndEndMap.keySet()){
			Map<String, List<Pair<String, String>>> statCreateAndEndMap = dataStatCreateAndEndMap.get(dataId);
			
			for(String statName : statCreateAndEndMap.keySet()){
				List<Pair<String,String>> createAndEndPair = statCreateAndEndMap.get(statName);
				for (int i = 0; i < createAndEndPair.size(); i++) {
					Pair<String, String> pair = createAndEndPair.get(i);
					if(pair.getFirst() == null)
						continue;
					
					if(pair.getSecond() == null)  //状态至今未结束
						pair.setSecond(dateList.get(dateList.size() -1)); //设为最后日期

					int fromDateIndex = dateList.indexOf(pair.getFirst());
					
				    int toDateIndex = dateList.indexOf(pair.getSecond());
					if(toDateIndex > dateList.size() -1)
						toDateIndex = dateList.size() -1;
				    
				    for(int j = fromDateIndex ; j <= toDateIndex ; j++){
				    	String keyDate = dateList.get(j);
				    	resultMap.get(keyDate).put(statName, resultMap.get(keyDate).get(statName) +1);
				    }
				}
			}
		}
		return resultMap;
	}
	
	/**
	 * @function：通过创建时间统计
	 * @modifyTime：2013-11-13 下午1:31:14
	 * @author：李明
	 * @email: liming@sogou-inc.com
	 * @param sql
	 * @param statMap
	 * @param fieldStaticColName 统计字段名
	 * @return
	 */
	public static Map<String, Map<String, Integer>> getCreateTimeStaticMap(String sql, Map<String, String> statMap , String fieldStaticColName , Map<String, Map<String, Integer>> resultMap){
		List<Map<String,String>> resultList = DbPoolConnection.getInstance().getResultSetListBySql(sql);
		for(Map<String,String> map : resultList){
			if(resultMap.get(map.get("createTime")) != null){
				String fieldValue = statMap.get(map.get(fieldStaticColName));
				if(fieldValue != null){
					int num = resultMap.get(map.get("createTime")).get(fieldValue);
					resultMap.get(map.get("createTime")).put(fieldValue, num +1);
				}
				resultMap.get(map.get("createTime")).put("总数", resultMap.get(map.get("createTime")).get("总数") +1);
			}
		}
		return resultMap;
	}
	
	
	public static Map<String, Map<String, Integer>> getTotalOfStat(String whereString, Timestamp startTimestamp , Timestamp endTimestamp , Template template, Map<String, Map<String, Integer>> resultMap) {
		
		String dataTable = TableRuleManager.getInstance().getDataTableName(template.getId());
		StringBuffer sqlBuffer = new StringBuffer();
		//查询初始值
		sqlBuffer.append("select count(id) from ").append(dataTable).append(" where ").append(whereString).append(" and createTime<'")
					.append(startTimestamp.toString().split(" ")[0]).append("' and templateId = ").append(template.getId().getValue());
		
		int currentTotal = DbPoolConnection.getInstance().getCountOfSQL(sqlBuffer.toString());
		
		sqlBuffer = new StringBuffer();
		
		sqlBuffer.append("SELECT B.createTime, @t:=@t+B.count as total from (" +
                         "SELECT @t:=0, count(DATE_FORMAT(createTime,'%Y-%m-%d')) as count,DATE_FORMAT(createTime,'%Y-%m-%d') as createTime " +
	                     " FROM ").append(dataTable).append(" where ").append(whereString).append(" and createTime >= '").append(startTimestamp.toString().split(" ")[0])
	         			.append("' and is_valid = 1 and  createTime <= '").append(endTimestamp.toString()).append("' group by DATE_FORMAT(createTime,'%Y-%m-%d')) as B");
		
		//按创建时间统计
//		sqlBuffer.append("SELECT COUNT(createTime) as total , date_format(createTime,'%Y-%m-%d') as createTime from ")
//			.append(dataTable).append(" where ").append(whereString).append(" and createTime >= '").append(startTimestamp.toString().split(" ")[0])
//			.append("' and createTime <= '").append(endTimestamp.toString()).append("' group by  date_format(createTime,'%Y-%m-%d')");
		
		Map<String, Integer> createTimeTotalMap = new LinkedHashMap<String, Integer>();
		Connection conn = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			conn = DbPoolConnection.getInstance().getReadConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sqlBuffer.toString());
			while(rs.next()){
				createTimeTotalMap.put(rs.getString("createTime"), rs.getInt("total"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DbPoolConnection.getInstance().closeAll(rs, stat, conn);
		}
		
		List<String> dateList = new ArrayList(resultMap.keySet());
		
		for (int i = 0 ; i < dateList.size() ; i ++) {
			if (createTimeTotalMap.get(dateList.get(i)) == null) {
				if (i == 0) {
					resultMap.get(dateList.get(i)).put("总数", currentTotal);
				}else {
					resultMap.get(dateList.get(i)).put("总数", resultMap.get(dateList.get(i-1)).get("总数"));
				}
			}else {
				resultMap.get(dateList.get(i)).put("总数", createTimeTotalMap.get(dateList.get(i)) + currentTotal);
			}
		}
		
		return resultMap;
	}
	
}
