/*
 * @description template list
 * @autor liuyanlei
 * @date 2014.1.6
 * */

var flows = null;
var allFlowRight = new Array();
var optionRightControl = false;

function initFlowList()
{
	showLoading(true);
	if(optionRightControl){
		$.ajax({
				url : base_url + 'backRight/initUserFlowRight.do',
				dataType:'json',
				success :function(data){
					for(var key in data){
						allFlowRight.push(data[key]);
					}
					initAllFlow();
				},
				error:function(){
					initAllFlow();
				}
		});
	}else{
		initAllFlow();
	}
	
}

function initAllFlow()
{
	$.ajax({
		url : 'flow/get_Admin_xml.jsp',
		type : 'POST',
		success : onInitFlowListAjax,
		data:{'initFlow':true},
		error : function(){
			showLoading(false);
		}
	});
}

function onInitFlowListAjax(rootNode)
{
	eval("var isError = " + $(rootNode).find("isError").text());
	var userRole = $(rootNode).find("userRole").text();
	flows = new Array();
	var gridHtml = "";
	var index = 1;
	$(rootNode).find("flows").children("flow").each(function(idx,node){
		flows[idx] = new Object();
		flows[idx].id = $(node).children("id").text();
		flows[idx].name = $(node).children("name").text(); 
		flows[idx].isProFlow = $(node).children("isProFlow").text(); 
		if(!optionRightControl || flows[idx].isProFlow || userRole === "super_admin" ||  $.inArray(flows[idx].id, allFlowRight) != -1){
			gridHtml += "<tr>";
			gridHtml += "<td>" + index ++ +"</td>";
			gridHtml += "<td><a href=\"admin_flow_edit.html?flowId="+ flows[idx].id + "&flowName=" + encodeURIComponent(flows[idx].name) + "\" target=\"_blank\">"+ flows[idx].name +"</a></td>";
			gridHtml += "<td><a href=\"#\" onClick=\"displayModifyDiv('" + flows[idx].id + "','" +flows[idx].name+ "')\">修改</a></td>";
			gridHtml += "<td><a href=\"admin_flow_edit.html?flowId="+ flows[idx].id +"&flowName=" + encodeURIComponent(flows[idx].name) + "\" target=\"_blank\">编辑</a></td>";
			if(userRole === "super_admin")
				gridHtml += "<td><a href=\"#\" onClick=\"removeFlow('" + flows[idx].id + "')\">删除</a></td>";
			else
				gridHtml += "<td>-</td>";
		}
		
	});
	$("#flowListGrid").find("tbody").html(gridHtml);
	showLoading(false);
	$("#flowListGrid").trigger("update");
}


function displayCreateDiv(projectInvolve)
{
	$("#input_name").val('');
	$('#projectInvolve').val(projectInvolve);
	$("#addFlowDiv").modal('show');
	
}


function checkFlowName(name){
	for(var i in flows){
		if(flows[i].name == name){
			return true;
		}
	}
	return false;
}

function addFlow()
{
	var name = $.trim($("#input_name").val());
	if(name == "")
	{
		alert("请输入流程名称！");
		return;
	}
	
	if(checkFlowName(name)){
		alert('己存在该名称流程,请修改!');
		return;
	}
	
	$.ajax({
		url : 'flow/add_Flow_xml.jsp',
		type : 'POST',
		data : {'name':name,'projectInvolved' : $('#projectInvolve').val()},
		success :onCompleteAddFlow
	});
	
	$("#addFlowDiv").modal('hide');
}

function onCompleteAddFlow(response)
{
	eval("var isError = " + $(response).find('isError').text());
	if(isError)
	{
		showInfoWin("error","流程添加失败!");
		return;
	}else{
		showInfoWin("success","流程添加成功!");
		initFlowList();
	}
}

function displayModifyDiv(flowId,flowName)
{
	$("#input_id_m").val(flowId);
	$("#input_name_m").val(flowName);
	$("#modifyFlowDiv").modal('show');
}

function modifyFlow()
{
	var name = $.trim($("#input_name_m").val());
	if(name == "")
	{
		alert("请输入流程名称！");
		return;
	}
	var flowId = $("#input_id_m").val();
	
	for(var i = 0; i < flows.length; i++)
	{
		if(flows[i].id == flowId)
			continue;
		 
		if(flows[i].name == name)
		{
			alert("工作流的名称不能重复！");
			return;
		}
	}
	
	var params = "id=" + getSafeParam(flowId);
	params += "&name=" + getSafeParam(name);
	$.ajax({
		url : 'flow/modify_Flow_xml.jsp',
		data : params,
		type:'POST',
		success : onCompleteModifyFlow
	});
}

function onCompleteModifyFlow(response)
{
	var isErrorText = $(response).find("isError").text();
	eval("var isError = " + isErrorText);
	if(isError)
	{
		showInfoWin("error","流程修改修改!");
		return;
	}else{
		showInfoWin("success","流程修改成功!");
		initFlowList();
		$("#modifyFlowDiv").modal('hide');
	}
}

function removeFlow(id)
{
	if( !confirm( "您确定要删除吗?数据将不可恢复" ) )
		return;
	var parms = "id=" + getSafeParam(id);
	$.ajax({
		url : 'flow/remove_Flow_xml.jsp',
		data : parms,
		success : onCompleteRemoveFlow
	});
}

function onCompleteRemoveFlow(request)
{
	var isErrorText = $(request).find("isError").text();
	eval("var isError = " + isErrorText);
	
	if(isError)
	{
		showInfoWin("error","删除失败!");
		return;
	}else{
		showInfoWin("success","删除成功!");
		initFlowList();
	}
}

function initSystem(callback)
{
	$.ajax({
		url: base_url + 'backRight/getSystem.do',
		type:'POST',
		data:{'userMail':'system'},
		dataType:'json',
		success:function(data){
			optionRightControl = (data.openRight == 'true' ? true : false);
			if(data.projectInvolved == 'true'){
				$('.project_involved_true').show();
				$('.project_involved_false').hide();
			}else{
				$('.project_involved_true').hide();
				$('.project_involved_false').show();
			}
			callback();
		},error:function(data){
			callback();
		}
	});
}


function searchStat()
{
	var searchValue = $("#search_stat_word").val();
	searchItem("flowListGrid","tbody tr",searchValue);
}


function bindEvents()
{
	$("#search_stat_word").keydown(function(e){
		if(e.keyCode === 13)
			searchStat();
	});
	
	$("#search_stat_btn").click(function(){
		searchStat();
	});
	
	$("#input_name").keydown(function(e){
		if(e.keyCode === 13)
			addFlow();
	});
}

$(function(){
	bindEvents();
	//初始化表单列表
	$("#flowListGrid").tablesorter({
		headers: 
		{ 
	        2:{sorter: false}, 
	        3:{sorter: false},
	        4:{sorter: false} 
		}
	}); 
	initSystem(initFlowList);
});