<%@page import="com.sogou.qadev.service.cynthia.bean.DataAccessAction"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Data"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.impl.DataImpl"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.ChangeLog"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field.Type"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field.DataType"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.sql.Timestamp"%>
<%@ page import="java.util.LinkedHashMap"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Pair"%>

<%
	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
	response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
	response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility

	out.clear();
	
	Long keyId = (Long)session.getAttribute("kid");
	Key key = (Key)session.getAttribute("key");

	if(keyId == null || keyId <= 0 || key == null){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	String taskIdStr = request.getParameter("taskId");
	UUID taskId = DataAccessFactory.getInstance().createUUID(taskIdStr);
	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(),keyId);
	Data data = das.queryData(taskId);
	
	data = (Data)data.clone();
	data.setCreateTime(new Timestamp(System.currentTimeMillis()));
	
	data.setObject("logCreateUser", "script");
	data.setObject("logActionComment", "reActicate person :"+key.getUsername());
	data.setObject("logActionId", null);
	
	Pair<ErrorCode, String> result = das.modifyData(data);
	if(result.getFirst().equals(ErrorCode.success)){
		das.commitTranscation();
		das.updateCache(DataAccessAction.update, data.getId().getValue(),data);
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><isError>false</isError>";
		xml += "<taskId>" + data.getId() + "</taskId></root>";
		out.println(xml);
	}
	else{
		das.rollbackTranscation();
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><isError>true</isError>");
		out.println(result.getSecond());
		out.println("</root>");
	}
%>