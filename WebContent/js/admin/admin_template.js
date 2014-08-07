/*
 * @description template list
 * @autor liuyanlei
 * @date 2014.1.6
 * */

var templates = null;
var templateTypes = null;
var flows = null;
var allTemplateRight = new Array();
var optionRightControl = false;
function initTemplateList()
{
	if(optionRightControl){
		allTemplateRight = new Array();
		$.ajax({
				url : base_url + 'backRight/initUserTemplateRight.do',
				dataType:'json',
				async:false,
				success :function(data){
					for(var templateId in data){
						if($.inArray(templateId,allTemplateRight) == -1)
							allTemplateRight.push(templateId);
					}
				},
				error:function(){
				}
		});
	}
	showLoading(true);
	$.ajax({
		url : 'template/get_InitInfo_xml.jsp',
		type : 'POST',
		success : onInitTemplateListAjax,
		error : function(){
			showLoading(false);
		}
	});
}

function onInitTemplateListAjax(rootNode)
{
	eval("var isError   = " + $(rootNode).find("isError").text());
	var userRole = $(rootNode).find("userRole").text();

	templates = new Array();
	templateTypes = new Array();
	flows = new Array();
	
	$(rootNode).find("templateTypes").children("templateType").each(function(idx,node){
		templateTypes[idx] = new Object();
		templateTypes[idx].id = $(node).children("id").text();
		templateTypes[idx].name = $(node).children("name").text();
	});
	
	$(rootNode).find("flows").children("flow").each(function(idx,node){
		flows[idx] = new Object();
		flows[idx].id = $(node).children("id").text();
		flows[idx].name = $(node).children("name").text();
	});
	
	var gridHtml = "";
	$(rootNode).find("templates").children("template").each(function(idx,node){
		templates[idx] = new Object();
		templates[idx].id = $(node).children("id").text();
		templates[idx].name =$(node).children("name").text(); 
		templates[idx].templateTypeId = $(node).children("templateTypeId").text();
		templates[idx].flowId = $(node).children("flowId").text();
		templates[idx].nodeId = $(node).children("nodeId").text();
		templates[idx].isFocused = $(node).children("isFocused").text();
		templates[idx].isNew = $(node).children("isNew").text();
		
		if(!optionRightControl  || userRole === "super_admin" || $.inArray(templates[idx].id, allTemplateRight) != -1){
			gridHtml += "<tr>";
			gridHtml += "<td>" + idx +"</td>";
			if(templates[idx].isNew == "true")
			{
				gridHtml += "<td><a href='admin_template_edit.html?templateId="+templates[idx].id+"' target='_blank'>" + templates[idx].name +"</a></td>";
			}else
			{
				gridHtml += "<td><a href='template/display_Template_html.jsp?id="+templates[idx].id+"' target='_blank'>" + templates[idx].name +"</a></td>";
			}
			gridHtml += "<td>" + getTemplateType(templates[idx].templateTypeId) +"</td>";
			gridHtml += "<td><a href=\"admin_flow_edit.html?flowId="+templates[idx].flowId+"\" target=\"_blank\">" + getFlow(templates[idx].flowId) +"</a></td>";
			
			gridHtml += "<td><a href='#' onclick='displayModifyDiv("+templates[idx].id+")'>修改</a></td>";
			
			gridHtml += "<td><a href='#' onclick='displayTemplateUserDiv("+templates[idx].id+")'>管理</a></td>";
			
			//超级管理员具体删除权限
			if(userRole === "super_admin")
			{
				gridHtml += "<td>"+"<a href=\"#\" onClick=\"removeTemplate('" + templates[idx].id + "')\">删除</a>"+"</td>";
			}else
			{
				gridHtml += "<td>-</td>";
			}
			gridHtml += "</tr>";
		}
		
	});
	$("#templateBodyDiv").empty();
	showLoading(false);
	$("#templateBodyDiv").html(gridHtml);
	$("#templateListGrid").trigger("update"); 
}

function initTemplateUsers(templateId)
{
	$.ajax({
		url : base_url + 'backRight/getTemplateRightUser.do',
		type : 'GET',
		dataType:'json',
		async:false,
		data:{'templateId':templateId},
		success : function(data){
			var gridHtml = "";
			for(var key in data){
				gridHtml += "<tr>";
				gridHtml += "<td>" + (key) +"</td>";
				gridHtml += "<td>" + data[key].userName +"</td>";
				gridHtml += "<td>" + data[key].nickName +"</td>";
				gridHtml += "<td><a href=\"#\" onClick=\"deleteTemplateUser('" + data[key].userName + "')\">删除</a></td>";
				gridHtml += "</tr>";
			}
			$("#user-cfg-table").html(gridHtml);
			enableSelectSearch();
		},
		error : function(){
			alert("Server Error!");
		}
	});
}

function deleteTemplateUser(userMail)
{
	if(!window.confirm("确定删除 " + userMail + " 的删除权限?"))
	{
		return;
	}
	var templateId = $('#template_id').val();
	$.ajax({
		url : base_url + 'backRight/delTemplateRightUser.do',
		type : 'POST',
		data:{'templateId':templateId,'userMail':userMail},
		success : function(data){
			if(data == 'true'){
				showInfoWin('success','删除成功!');
				initTemplateUsers(templateId);
			}else{
				showInfoWin('error',data);
			}
		},
		error : function(){
			alert("Server Error!");
		}
	});
}

//添加
function addUserRight(){
	
	var userMail = $("#allUser").val();
	
	if(userMail == ""){
		alert('用户名不能为空!');
		return;
	}
	var templateId = $('#template_id').val();
	$.ajax({
		url : base_url + 'backRight/addTemplateRightUser.do',
		type : 'POST',
		data:{'userMails':userMail , 'templateId':templateId},
		success : function(data){
			if(data == "true"){
				showInfoWin('success','添加成功!');
				initTemplateUsers(templateId);
			}else{
				showInfoWin('error',data);
			}
		},
		error : function(){
			alert("Server Error!");
		}
	});
	$("#addUserDiv").modal('hide');
}

function displayTemplateUserDiv(templateId)
{
	$("#template_id").val(templateId);
	initTemplateUsers(templateId);
	$("#templateUserDiv").modal('show');
}


function showCopyUser()
{
	if($("#select_copyTemplateId").val() != "")
	{
		$("#copy_flow_div").hide();
		$("#user_right_div").show();
	}else
	{
		$("#copy_flow_div").show();
		$("#user_right_div").hide();
	}
}

function onSelectTemplateType()
{
	$("#select_copyTemplateId").empty();
	$("#select_copyTemplateId")[0].options[0] = new Option("请选择", "");
	var templateTypeId = $("#select_templateTypeId")[0].options[$("#select_templateTypeId")[0].selectedIndex].value;
	if(templateTypeId == "")
		return;
	for(var i = 0; i < templates.length; i++)
	{
		if(templates[i].templateTypeId != templateTypeId)
			continue;
				
		$("#select_copyTemplateId")[0].options[$("#select_copyTemplateId")[0].options.length] = new Option(templates[i].name, templates[i].id);
	}
}

function displayCreateDiv()
{
	$("#input_name").val("");
	
	$("#select_templateTypeId").empty();
	$("#select_templateTypeId")[0].options[0] = new Option("请选择", "");
	for(var i = 0; i < templateTypes.length; i++)
		$("#select_templateTypeId")[0].options[i + 1] = new Option(templateTypes[i].name, templateTypes[i].id);
	
	$("#select_copyTemplateId").empty();
	$("#select_copyTemplateId")[0].options[0] = new Option("请选择", "");
	
	$("#select_flowId").empty();
	$("#select_flowId")[0].options[0] = new Option("请选择", "");
	for(var i = 0; i < flows.length; i++)
		$("#select_flowId")[0].options[i + 1] = new Option(flows[i].name, flows[i].id);
	
	$("#addTemplateDiv").modal('show');
}

function addTemplate()
{
	var name = $.trim($("#input_name").val());
	if(name == "")
	{
		alert("请输入表单名称！");
		return;
	}
	
	var templateTypeId = $("#select_templateTypeId").val();
	if(templateTypeId == "")
	{
		alert("请选择表单类型！");
		return;
	}
	
	var copyTemplateId = $("#select_copyTemplateId").val();
	
	var flowId = $("#select_flowId").val();
	if(flowId == "" && copyTemplateId == "")
	{
		alert("请选择流程！");
		return;
	}
	
	var params = "name=" + getSafeParam(name);
	params += "&templateTypeId=" + getSafeParam(templateTypeId);
	params += "&flowId=" + getSafeParam(flowId);
	
	if(copyTemplateId != "")	
		params += "&copyTemplateId=" + getSafeParam(copyTemplateId);
	
	var copyUserRight = $("#select_copyUserRight").val();
	if(copyUserRight != "")
		params += "&copyUserRight=" + getSafeParam(copyUserRight);
	
	$.ajax({
		url : 'template/add_Template_xml.jsp',
		type : 'POST',
		data : params,
		dataType:'xml',
		success :onCompleteAddTemplate
	});
}

function onCompleteAddTemplate(response)
{
	eval("var isError = " + $(response).find('isError').text());
	if(isError)
	{
		alert("添加表单失败！");
		return;
	}
	$("#addTemplateDiv").modal('hide');
	showInfoWin('success','表单添加成功!');
	initTemplateList();
}

function displayModifyDiv(templateId)
{
	for(var i = 0; i < templates.length; i++)
	{
		if(templates[i].id != templateId)
			continue;
		$("#input_id_m").val(templates[i].id);
		$("#input_id_m").attr("uuid",templates[i].id);
		$("#input_name_m").val(getXMLStr(templates[i].name));
		$("#select_templateTypeId_m").append("<option value='"+templates[i].templateTypeId+"' selected='selected'>"+getXMLStr(getTemplateType(templates[i].templateTypeId)) +"</option>");
		$("#select_flowId_m").empty();
		for(var j = 0; j < flows.length; j++)
		{
			var flowOption = "<option value=\"" + flows[j].id + "\"";
			if(flows[j].id == templates[i].flowId)
				flowOption += " selected";
			flowOption += ">" + getXMLStr(flows[j].name) + "</option>";
			$("#select_flowId_m").append(flowOption);
		}
	}
	$("#modifyTemplateDiv").modal('show');
}

function modifyTemplate()
{
	var name = $.trim($("#input_name_m").val());
	if(name == "")
	{
		alert("请输入表单名称！");
		return;
	}
	
	var flowId = $("#select_flowId_m").val();
	if(flowId == "")
	{
		alert("请选择流程！");
		return;
	}
	
	var params = "id=" + getSafeParam($("#input_id_m").attr("uuid"));
	params += "&name=" + getSafeParam(name);
	params += "&templateTypeId=" + getSafeParam($("#select_templateTypeId_m").val());
	params += "&flowId=" + getSafeParam(flowId);
	$.ajax({
		url : 'template/modify_Template_xml.jsp',
		data : params,
		type:'POST',
		success : onCompleteModifyTemplate
	});
}

function onCompleteModifyTemplate(response)
{
	var isErrorText = $(response).find("isError").text();
	eval("var isError = " + isErrorText);
	if(isError)
	{
		alert("修改表单失败！");
		return;
	}
	initTemplateList();
	$("#modifyTemplateDiv").modal('hide');
}

function removeTemplate(id)
{
	if( !confirm( "您确定要删除吗?数据将不可恢复" ) )
		return;
	var parms = "id=" + getSafeParam(id);
	$.ajax({
		url : 'template/remove_Template_xml.jsp',
		data : parms,
		success : onCompleteRemoveTemplate
	});
}

function onCompleteRemoveTemplate(request)
{
	var isErrorText = $(request).find("isError").text();
	eval("var isError = " + isErrorText);
	
	if(isError)
	{
		alert("删除表单失败！");
		return;
	}
	initTemplateList();
}

//新旧界面
function addNewPage(templateId)
{
	$.ajax({
		url : '../task/newPage.jsp',
		data : "templateId="+templateId+"&type=addAll",
		success : function(request){
			onCompleteAddNewPage(request,templateId);
		}
	});
}

function onCompleteAddNewPage(request,templateId)
{
	eval("var isError = " + $(request).find("isError").text());
	if(isError)
	{
		document.getElementById("newPage"+templateId).innerHTML = "<a href=\"#\" onClick=\"removeNewPage('" + templateId + "')\">旧界面</a>";
	}
}

function removeNewPage(templateId)
{
	$.ajax({
		url : '../task/newPage.jsp',
		data : "templateId="+templateId+"&type=removeAll",
		success : function(request){
			onCompleteRemoveNewPage(request,templateId);
		}
	});
}

function onCompleteRemoveNewPage(request,templateId)
{
	eval("var isError = " + $(request).find("isError").text());
	if(isError)
	{
		document.getElementById("newPage"+templateId).innerHTML = "<a href=\"#\" onClick=\"addNewPage('" + templateId + "')\">新界面</a>";
	}
}

function getTemplateType(templateTypeId)
{
	for(var i = 0; i < templateTypes.length; i++)
	{
		if(templateTypes[i].id == templateTypeId)
			return templateTypes[i].name;
	}
}

function getFlow(flowId)
{
	for(var i = 0; i < flows.length; i++)
	{
		if(flows[i].id == flowId)
			return flows[i].name;
	}
}

function initSystem()
{
	$.ajax({
		url: base_url + 'backRight/getSystem.do',
		type:'POST',
		data:{'userMail':'system'},
		dataType:'json',
		async:false,
		success:function(data){
			data = eval('(' + data + ')');
			for(var key in data){
				if(key == 'openRight'){
					optionRightControl = (data[key] == 'true' ? true : false);
					break;
				}
			}
		}
	});
}


function searchStat()
{
	var searchValue = $("#search_stat_word").val();
	searchItem("templateBodyDiv","tr",searchValue);
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
	
	$("#addUserDiv").on('show',function(){
		initAllUsers();
	});
}

function initAllUsers()
{
	$.ajax({
		url : 'user/get_user_admin_xml.jsp',
		type : 'POST',
		data:{'initUser':true,'userRole':'admin'},
		success : onInitUserListAjax,
		error : function(data){
			alert("Server Error!");
		}
	});
}

function onInitUserListAjax(rootNode)
{
	$("#allUserDiv").empty();
	var gridHtml = "";
	gridHtml += "<select id =\"allUser\" class=\"select2\" placeholder=\"多选添加用户\" style=\"width:250px;\" multiple >";
	$(rootNode).find("users").children("user").each(function(idx,node){
		var userName = $(node).children("name").text(); 
		var userEmail = $(node).children("email").text();
		var userRole = $(node).children("userRole").text();
		if(userRole === "normal") //只初始化管理员或超级管理员
			return true;
		gridHtml += "<option value="+userEmail+">" + userName + "[" + userEmail + "]</option>";
	});
	gridHtml += "</select>";
    $("#allUserDiv").html(gridHtml); 
    enableSelectSearch();
}

$(function(){
	bindEvents();
	initSystem();
	//初始化表单列表
	$("#templateListGrid").tablesorter({
		headers: 
		{ 
	        5:{sorter: false}, 
	        6:{sorter: false},
	        7:{sorter: false} 
		}
	}); 
	initTemplateList();
});