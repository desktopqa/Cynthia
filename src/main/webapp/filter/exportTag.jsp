<%@page import="com.sogou.qadev.service.cynthia.service.ExportDataManager"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.TagBean"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL"%>
<%@page import="com.sogou.qadev.service.cynthia.service.TableRuleManager"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Data"%>
<%@page import="com.sogou.qadev.service.cynthia.dao.TagAccessSessionMySQL"%>
<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@ page language="java" contentType="application/vnd.ms-excel; charset=UTF-8"%>

<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.text.SimpleDateFormat"%>

<%@ page import="org.apache.commons.httpclient.HttpClient"%>
<%@ page import="org.apache.commons.httpclient.NameValuePair"%>
<%@ page import="org.apache.commons.httpclient.methods.PostMethod"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFWorkbook"%>
<%@page import="org.apache.poi.hssf.usermodel.HSSFCell"%>
<%@page import="org.apache.poi.hssf.usermodel.HSSFRow"%>
<%@page import="org.apache.poi.hssf.usermodel.HSSFSheet"%>
<%@page import="org.apache.poi.hssf.usermodel.HSSFWorkbook"%>

<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Attachment"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.TemplateType"%>
<%
response.setContentType("application/vnd.ms-excel;charset=UTF-8");
response.setHeader("Content-Disposition","attachment; filename="+CynthiaUtil.getToday()+".xls");

out.clear();

request.setCharacterEncoding("utf-8");

Key key = (Key)session.getAttribute("key");
Long keyId = (Long)session.getAttribute("kid");
String username = request.getParameter("username");

if(keyId == null || keyId <= 0 || key == null){
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}

if(key!=null)
	username = key.getUsername();

String tagId = request.getParameter("tagId");
if(tagId == null || tagId.length() == 0){
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}

String type = request.getParameter("type");

String[] dataIdArray = null;

if(type.trim().equals("all")){
	dataIdArray = new TagAccessSessionMySQL().getTagDataById(tagId);
}else{
	dataIdArray = request.getParameterValues("dataIds");
}


StringBuffer dataIdBuffer = new StringBuffer();
for(String dataId : dataIdArray){
	dataIdBuffer.append(dataIdBuffer.length() > 0 ? ",":"").append(dataId);
}
String dataIds = dataIdBuffer.toString();

Set<String> displayFieldsName = new HashSet(Arrays.asList(new String[]{"标题","描述","创建时间","指派人","状态","修改时间","创建人"}));
List<Data> dataList = new ArrayList<Data>();
Set<String> notNewTaskIdSet = new HashSet<String>();
boolean isSysFilter = true;
DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(username,keyId);

//标签数据
Map<String,String> userClassifyDataMap = das.getUserClassifyDataMap(username);

//标签信息
List<TagBean> allTagList = das.getAllTag(username);
Map<String, String> tagMap = new HashMap<String, String>();
for (TagBean tagBean : allTagList) {
	tagMap.put(tagBean.getId(), tagBean.getTagName());
}
	
//没设置过滤器，则全局查询
if( (dataIds == null || dataIds.length() == 0)){
	
}else{
	List<String> dataTable = TableRuleManager.getInstance().getAllDataTables();
	StringBuffer sqlBuffer = new StringBuffer();
	StringBuffer whereBuffer = new StringBuffer();
	whereBuffer.append(" where id in (").append(dataIds).append(")");
	
	for(String tableName : dataTable){
		sqlBuffer.append(sqlBuffer.length() > 0 ? " union " :"").append(" select id , templateId,title,description,createTime, assignUser,statusId,lastModifyTime,createUser from ").append(tableName)
		.append(whereBuffer);
	}
	
	dataList = new DataAccessSessionMySQL().queryDatas(sqlBuffer.toString(), false, null);
}


ExportDataManager.getExcelOutputStream(dataList.toArray(new Data[0]), displayFieldsName.toArray(new String[0]) ,false, userClassifyDataMap, tagMap,response.getOutputStream());	
response.getOutputStream().flush();
response.getOutputStream().close();
%>





















