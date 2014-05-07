<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <title>添加任务-Cynthia</title>
	<link href="../lib/bootstrap2/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="../lib/bootstrap2/css/bootstrap-responsive.min.css" rel="stylesheet" type="text/css">
	<link href="../lib/g_bootstrap/css/google-bootstrap.css" rel="stylesheet" type="text/css">
	<link href="../css/top.css" rel="stylesheet" type="text/css">
	<link href="../css/editFilter.css" rel="stylesheet" type="text/css">
	
	<script type="text/javascript" src='../lib/jquery/jquery-1.9.3.min.js'></script>
	<script type="text/javascript" src="../lib/jquery.plugins/jsTree/jquery.jstree.js"></script>
	<script type="text/javascript" src="../lib/jquery.plugins/hotkeys/jquery.hotkeys.js"></script>
	<script type="text/javascript" src="../lib/jquery.plugins/dragsort/jquery.dragsort.js"></script>
	<script type="text/javascript" src='../lib/bootstrap2/js/bootstrap.cynthia.min.js'></script>
	<script type="text/javascript" src="../lib/My97DatePicker/WdatePicker.js"></script>
	<script type="text/javascript" src='../lib/select2/select2.js'></script>
	
	<script type="text/javascript" src="../js/order.js"></script>
	<script type="text/javascript" src="../js/where.js"></script>
	<script type="text/javascript" src="../js/tree.js"></script>
	<script type="text/javascript" src='../js/util.js'></script>
	<script type="text/javascript" src='../js/filterPageForAddRef.js'></script>
  </head>
  
 	<body onload="initFilterPage(<%=request.getParameter("filterId") %>)" style="overflow: scroll;padding-left:50px">
 	
 	<form  class="form-horizontal" id="previewForm" method="post" action="../previewFilterResultForReference.jsp">
		<input type="hidden" id="finalXml" name="finalXml" value="" />
		<input type="hidden" id="fieldId" name="fieldId" value="<%=request.getParameter("fieldId") %>"/>
		<input type="hidden" id="dataType" name="dataType" value="<%=request.getParameter("dataType") %>"/>
		<input type="hidden" id="alreadyIds" name="alreadyIds" value="<%=request.getParameter("alreadyIds") %>"/>
		<div class="control-group">
		    <label class="control-label" for="templateTypeSelect">任务类型:</label>
		    <div class="controls">
		       <select class="span3" id="templateTypeSelect" onchange="initTemplates()" >
					<option value="0" selected="selected">---请选择---</option>
					<option value="1">缺陷</option>
					<option value="3">任务</option>
					<option value="2">日常管理</option>
				</select>
			 </div>
	 	 </div>
	 	 
	 	 <div class="control-group">
		    <label class="control-label" for="templates">表单名称:</label>
		    <div class="controls">
		      <select class="span3" id="templates" onchange="initFields()">
				  <option value="">---请选择---</option>
			  </select>
		    </div>
		  </div>
		  
		  <div class="control-group">
		    <label class="control-label" for="fields">选择字段:</label>
		    <div class="controls">
		      <select class="span3" id="fields" onchange="addCondition()">
				  <option value="">---请选择---</option>
			  </select>
			  <span style="color:red;">(多次选择可以添加多个条件)</span>
			</div>
		  </div>
	
		<table id="conditions_table">
			<tbody>
				
			</tbody>
		</table>
	    <hr />
	    <div class="control-group" style="display:none;">
		    <label class=" control-label">查找目标:</label>
		    <label class="radio">
			    <input id="input_is_current" type="radio" name='find_time' value="current" checked="checked"/>查找当前记录
			</label>
			<label class="radio">
			    <input id="input_is_history" type="radio" name='find_time' value="history"/>查找修改日志
			</label>
   	    </div>
		
		<div class="control-group">
		    <label class=" control-label">字段间关系:</label>
		       <input type="radio" style="margin:0px;" id="input_and" name="betweenField"  checked="checked" value ="and" />且&nbsp;&nbsp;
		       <input type="radio" style="margin:0px;" id="input_or"  name="betweenField" value ="or" />或
	    </div>
	   <div class="control-group" style="padding-left:50px; padding-top:10px;">
		<button class="btn btn-primary" onclick="executeSubmitQueryRefer()">提交</button>
 	   </div>
	</form>
 </body>
</html>
