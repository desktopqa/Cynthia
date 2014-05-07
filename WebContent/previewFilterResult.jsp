<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.EscapeUtil"%>
<!DOCTYPE html>
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
<script type="text/javascript" src='js/previewFilter.js'></script>
<script type="text/javascript" src='js/index_grid.js'></script>
<script type="text/javascript" src='js/changeTh.js'></script>
<script type="text/javascript">
<%
	String xml = EscapeUtil.encodeAll(request.getParameter("finalXml"));
	xml = xml.replaceAll("\n","").replaceAll("\r\n","").replaceAll("\r","").replaceAll("\t","");
%>
</script>
</head>
		
<body  onload="queryFilterData('<%=xml%>')">

<div class="container-fluid">
	<div id ="header-nav">
	</div>
	<div class="row-fluid mid-toolbar affix">
		<strong style="font-size:15px;">过滤器查询结果:</strong>
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
	<div class="row-fluid main-content affix">
		<div class="main-grid-outer affix" id='main-grid-outer' style="left:0px !important; width:100% !important;">
			<table id="main-grid-header" >
			</table>
<!-- 			<div style="padding-top:32px"> -->
<!-- 			</div> -->
			
			<div id="main-grid-div">
				<table id='main-grid-content' class="table table-hover">
			    </table>
			</div>
<!-- 			<table id='main-grid-content' class="table table-hover"> -->
<!-- 		    </table> -->
		</div>
	</div>
</div>

<!-- hidden field -->
<input type="hidden" id="filterId">

</body>
</html>