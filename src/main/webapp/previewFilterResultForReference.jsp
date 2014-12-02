<%@page import="com.sogou.qadev.service.cynthia.bean.Field.DataType"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Field.DataType"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.EscapeUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%
	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
	response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
	response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility

	out.clear();

	Key key = (Key)session.getAttribute("key");
	Long keyId = (Long)session.getAttribute("kid");

	if(keyId == null || keyId <= 0 || key == null){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}

	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);

   	String fieldId = request.getParameter("fieldId");
   	
   	String xml = "";
   	if(request.getParameter("finalXml") != null)
   		xml = EscapeUtil.encodeAll(request.getParameter("finalXml"));
   	else if(request.getParameter("defaultValue") != null){
   		UUID filterId = DataAccessFactory.getInstance().createUUID(request.getParameter("defaultValue"));

   		String alreadyIds = request.getParameter("alreadyIds");
   		if(alreadyIds == null)
   			alreadyIds = "";

   		String user = request.getParameter("user");

   		Filter filter = das.queryFilter(filterId);
   		if(filter == null)
   		{
		
   		}else{
   			if(filter.getCreateUser().equals("admin@sohu-rd.com"))
   			{
   				Document filterDoc = XMLUtil.string2Document(filter.getXml(), "UTF-8");

   				Node envNode = XMLUtil.getSingleNode(filterDoc, "query/env");

   				XMLUtil.getSingleNode(envNode, "current_user").setTextContent(key.getUsername());

				Node userListNode = filterDoc.createElement("user_list");
				userListNode.setTextContent(key.getUsername());
				envNode.appendChild(userListNode);

   				filter.setXml(XMLUtil.document2String(filterDoc, "UTF-8"));
   			}
   			xml = filter.getXml().replaceAll("\\\r", "").replaceAll("\\\n", "").replaceAll("\\\"", "\\\\\\\"").trim();
   		}
   		
   		xml = EscapeUtil.encodeAll(xml);
   	}
    
	xml = xml.replaceAll("\n","").replaceAll("\r\n","").replaceAll("\r","").replaceAll("\t","");
    String dataType = request.getParameter("dataType");
%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="Description" content="Cynthia项目缺陷管理系统，拥有表单流程化设计，可视化拖动布局等功能，提供项目管理，缺陷管理，，统计，查询等服务，是您项目上的好帮手！">
	<meta name="Keywords" content="Cynthia,BUG管理,项目管理 ,缺陷管理,任务管理,BUG,缺陷,开源">
    <title>Cynthia 欢迎您!</title>
    
	<link href="lib/bootstrap2/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="lib/bootstrap2/css/bootstrap-responsive.min.css" rel="stylesheet" type="text/css">
	<link href="lib/g_bootstrap/css/google-bootstrap.css" rel="stylesheet" type="text/css">
	<link href="css/top.css" rel="stylesheet" type="text/css">
	<link href="css/index.css" rel="stylesheet" type="text/css">
	
	<script type="text/javascript" src='lib/jquery/jquery-1.9.3.min.js'></script>
	<script type="text/javascript" src='lib/bootstrap2/js/bootstrap.cynthia.min.js'></script>
	<script type="text/javascript" src='js/util.js'></script>
	<script type="text/javascript" src='js/index_grid.js'></script>
	<script type="text/javascript" src='js/changeTh.js'></script>
	<script type="text/javascript" src='js/previewFilterForReference.js'></script>
  </head>
  
		
<body>
<div class="container-fluid">
	<div id ="header-nav">
	</div>
	
	<div class="row-fluid mid-toolbar affix">
		<div class="span12 middle">
			<div class="pull-left" style="padding-left:600px;">
				<div>
					<button class="btn btn-danger btn-primary" onclick="executeSubmit();">添加</button>
					<button class="btn" onclick="executeCancel();">取消</button>
				</div>
			</div>
			<div class="pull-right" style="margin-top:5px;">
				<div>
					<form class="form-inline">
						<input type="text" id="current-page" class="input-mini input-super-mini" value=1 />
						<label>/</label>
						<label id="totalPage"></label>页 共
						<label id="totalCount"></label>条
						<button class="btn btn-mini" type="button" id="prev-page"><i class=" icon-chevron-left"></i></button>
						<button class="btn btn-mini" type="button" id='next-page'><i class=" icon-chevron-right"></i></button>
					</form>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid main-content affix">
		<div class="main-grid-outer affix" id='main-grid-outer' style="left:0px !important; width:100% !important;">
			<table id="main-grid-header" >
			</table>
			<div id="main-grid-div">
				<table id='main-grid-content' class="table table-hover">
			    </table>
			</div>
		</div>
	</div>
</div>

<!-- hidden field -->
<input type="hidden" id="filterId"/>
<input type="hidden" id="filterXml" value="<%=xml%>"/>
<input type="hidden" id="fieldId" value="<%=fieldId%>"/>
<input type="hidden" id="dataType" value="<%=dataType%>"/>
</body>
</html>
