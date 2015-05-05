/*
 * @description template list
 * @autor liming
 * @date 2014.1.6
 * */

var templates = null;
var roles = null;
var allTemplateRight = new Array();
var optionRightControl = false;
var isProjectInvolved;

function initTemplateList()
{
	if(optionRightControl){
		$.ajax({
				url : base_url + 'backRight/initUserTemplateRight.do',
				dataType:'json',
				success :function(data){
					for(var templateId in data){
						allTemplateRight.push(templateId);
					}
					initAllTemplate();
				},
				error:initAllTemplate
		});
	}
}

function initAllTemplate(){
	$.ajax({
		url : 'template/get_InitInfo_xml.jsp',
		type : 'POST',
		success : onInitTemplateListAjax
	});
}

function onInitTemplateListAjax(rootNode)
{
	eval("var isError   = " + $(rootNode).find("isError").text());
	var userRole = $(rootNode).find("userRole").text();
	templates = new Array();
	$("#select_template").empty();
	var gridHtml = "";
	gridHtml += "<option value=\"\">--请选择表单--</option>";
	$(rootNode).find("templates").children("template").each(function(idx,node){
		templates[idx] = new Object();
		templates[idx].id = $(node).children("id").text();
		templates[idx].name = $(node).children("name").text();
		templates[idx].flowId = $(node).children("flowId").text();
		templates[idx].isProTemplate = $(node).children("isProTemplate").text();
		
		if((!optionRightControl  || userRole === "super_admin" || $.inArray(templates[idx].id, allTemplateRight) != -1) &&
				( !isProjectInvolved || (isProjectInvolved && !templates[idx].isProTemplate != 'true')))
			gridHtml += "<option value=\"" + idx+"\">" + templates[idx].name + "</option>";
	});
	
	$("#select_template").append(gridHtml);
	enableSearchSelect();
}

function initFlowRoles()
{
	$("#role_div").show();
	var index = $("#select_template").val();
	if(index == ""){
		$("#role_div").hide();
		alert("请选择表单");
		return;
	}
	var flowId = templates[index].flowId;
	$("#roleListGrid").find("tbody").html("");
	$.ajax({
		url: base_url + 'flow/getActionRole.do',
		type :'POST',
		dataType:'json',
		data: {'flowId':flowId},
		success: function(data){
			var gridHtml = "";
			roles = new Array();
			for(var i in data.allRole){
				roles[i] = new Object();
				roles[i].id = data.allRole[i].fieldId;
				roles[i].name = data.allRole[i].fieldName;
				gridHtml += "<tr>";
				gridHtml += "<td>" + i +"</td>";
				
				if(data.allRole[i].fieldName == "everyone"){
					gridHtml += "<td style=\"color:red\">" + data.allRole[i].fieldName +"</td>";
					gridHtml += "<td>-</td>";
					gridHtml += "<td>-</td>";
				}
				else{
//					gridHtml += "<td>" + data.allRole[i].fieldName +"</td>";
					gridHtml += "<td><a href=\"#\" onClick=\"modifyRoleDiv('" + data.allRole[i].fieldId + "','" + data.allRole[i].fieldName + "')\">" +data.allRole[i].fieldName+ "</a></td>";
					gridHtml += "<td><a href=\"#\" onClick=\"removeRole('" + data.allRole[i].fieldId + "')\">删除</a></td>";
					gridHtml += "<td><a href=\"#\" onClick=\"editRoleUsers('" + data.allRole[i].fieldId + "')\">管理</a></td>";
				}
				gridHtml += "</tr>";
			}
			$("#roleListGrid").find("tbody").html(gridHtml);
		},
		error:function(msg){
		}
	});
}

//修改角色名称初始化修改框
function modifyRoleDiv(roleId, roleName)
{
	$("#modify_role_id").val(roleId);
	$("#modify_role_name").val(roleName);
	$("#modifyRoleDiv").modal('show');
}

function modifyUserRole()
{
	var roleName = $("#modify_role_name").val();
	var roleId = $("#modify_role_id").val();
	if(roleName === ""){
		alert("角色名称不能为空!");
		return;
	}
	var index = $("#select_template").val();
	var flowId = templates[index].flowId;
	
	$.ajax({
		url:'flow/modify_Role_xml.jsp',
		type:'post',
		data:{'id':roleId,'flowId':flowId,'name':roleName},
		success:onCompleteModifyRole,
		error:function(){
			alert("删除失败!");
			return;
		}
	});
}

function onCompleteModifyRole(request)
{
	var isErrorNode = $(request).find("isError");
	eval("var isError = " + $(isErrorNode).text());
	if(isError)
	{
		alert("修改失败！");
		return;
	}
	$("#modifyRoleDiv").modal('hide');
	initFlowRoles();
}

function removeRole(roleId){
	if(!confirm("数据将不可恢复！您确定要删除吗？"))
		return;
	var index = $("#select_template").val();
	var flowId = templates[index].flowId;
	
	$.ajax({
		url:'flow/remove_Role_xml.jsp',
		type:'post',
		data:{'id':roleId,'flowId':flowId},
		success:onCompleteRemoveRole,
		error:function(){
			alert("删除失败!");
			return;
		}
	});
}

function onCompleteRemoveRole(request)
{
	var isErrorNode = $(request).find("isError");
	eval("var isError = " + $(isErrorNode).text());
	if(isError)
	{
		alert("删除失败！");
		return;
	}
	initFlowRoles();
}

function editRoleUsers(roleId){
	
	var index = $("#select_template").val();
	var flowId = templates[index].flowId;
	if(flowId == "")
	{
		alert("请选择表单！");
		return;
	}
	$("#role_id").val(roleId);
	$.ajax({
		url : 'flow/init_RoleUser_xml.jsp',
		type : 'POST',
		success : onCompleteInitRoleUsers,
		data:{'templateId':templates[index].id,'roleId':roleId,'flowId':flowId},
		error : function(data){
		}
	});
}

function onCompleteInitRoleUsers(request){
	var rootNode = $(request).children("root");
	var isErrorNode = $(rootNode).children("isError");
	eval("var isError = " + $(isErrorNode).text());
	
	if(isError)
	{
		alert("数据加载失败...");
		return;
	}	
	
	users = new Array();
	var userNodes = $(rootNode).children("users").children("user");
	var roleUserTd2Html = "";
	$("#user-cfg-table").empty();
	for(var i = 0; i < userNodes.length; i++)
	{
		users[i] = new Object();
		users[i].name = $(userNodes[i]).children("name").text();
		users[i].showName = $(userNodes[i]).children("showName").text();
		users[i].right = $(userNodes[i]).children("right").text();
		
		roleUserTd2Html += "<tr>";
		roleUserTd2Html += "<td>" + (i + 1) + "</td>";
		roleUserTd2Html += "<td value=" + getXMLStr(users[i].name) + ">" + getXMLStr(users[i].showName) + "</td>";
		roleUserTd2Html += "<td><input type=\"checkBox\"" + (users[i].right == "yes" ? " checked" : "") + "/></td>";
		roleUserTd2Html += "</tr>";
	}
	$("#user-cfg-table").append(roleUserTd2Html);
	$("#modifyUserDiv").modal('show');
}


function addRole(){
	var roleName = trim($("#role_name").val());
	if(roleName == "")
	{
		alert("名字不能为空！");
		return;
	}
	
	for(var i = 0; i < roles.length; i++)
	{
		if(roles[i].name == roleName)
		{
			alert("角色名重复！");
			return;
		}
	}
	
	$("#addRoleDiv").modal('hide');	
	var index = $("#select_template").val();
	var flowId = templates[index].flowId;
	$.ajax({
		url:'flow/add_Role_xml.jsp',
		type:'post',
		data:{'name':roleName,'flowId':flowId},
		success:onCompleteAddRole,
		error:function(){
			alert("添加角色失败!");
			return;
		}
	});
}

function onCompleteAddRole(request)
{
	var rootNode = $(request).children("root");
	
	var isErrorNode = $(rootNode).children("isError");
	eval("var isError = " + $(isErrorNode).text());
	
	if(isError)
	{
		alert("添加角色失败！");
		return;
	}
	initFlowRoles();
}

function addUserRight()
{
	var user = $("#allUser").val();
	var index = $("#select_template").val();
	var flowId = templates[index].flowId;
	var roleId = $("#role_id").val();
	if(flowId == "")
	{
		alert("请选择表单！");
		return;
	}
	$("#addUserDiv").modal('hide');
	$.ajax({
		url : 'flow/add_UserRight_xml.jsp',
		data : {'user':user,'flowId':flowId,'roleId':roleId,'templateId':templates[index].id },
		type:'POST',
		success : onCompleteAddUserRight,
		error:function(){
			alert("添加失败!");
		}
	});
	
}

function onCompleteAddUserRight(request)
{
	var rootNode = $(request).children("root");
	var isErrorNode = $(rootNode).children("isError");
	eval("var isError = " + $(isErrorNode).text());
	if(isError)
	{
		alert("添加失败！");
		return;
	}
	
	editRoleUsers($("#role_id").val());
}


function modifyRoleUser(){
	
	if(users.length != 0)
	{
		var index = $("#select_template").val();
		var param = "roleId=" + getSafeParam($("#role_id").val());
		param += "&flowId=" + getSafeParam(templates[index].flowId);
		param += "&templateId=" + getSafeParam(templates[index].id);
		
		$.each($("#user-cfg-table").find("tr"),function(index,node){
			var userRight = $(node).find("td:eq(1)").attr("value");
			if($(node).find("td:eq(2)").find("input").prop("checked")){
				param += "&userRight=" + userRight + "|true";
			}else{	
				param += "&userRight=" + userRight + "|false";
			}
		});
		$.ajax({
			url:'flow/save_UserRight_xml.jsp',
			type:'post',
			data:param,
			success:onCompleteSaveUserRight,
			error:function(){
				alert("保存失败!");
				return;
			}
		});
	}
	$("#modifyUserDiv").modal('hide');
}

function onCompleteSaveUserRight(request)
{
	var rootNode = $(request).children("root");
	var isErrorNode = $(rootNode).children("isError");
	eval("var isError = " + $(isErrorNode).text());
	
	if(isError)
		alert("保存失败！");
	else
		showInfoWin('success','保存成功!');
}

function enableSearchSelect(){
	$("select").each(function(idx,select){
		if(!($(select).hasClass("multiLine")||$(select).hasClass('noSearch')))
		{
			$(select).select2();
		}
	});
}

function initSystem(callback)
{
	$.ajax({
		url: base_url + 'backRight/getSystem.do',
		type:'POST',
		data:{'userMail':'system'},
		dataType:'json',
		success:function(data){
			isProjectInvolved = data.projectInvolved == 'true';
			optionRightControl = (data.openRight == 'true' ? true : false);
			if(callback) callback();
		}
	});
}

function initAllUsers()
{
	$.ajax({
		url : 'user/get_user_admin_xml.jsp',
		type : 'POST',
		data:{'initUser':true},
		success : onInitUserListAjax,
		error : function(data){
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
		gridHtml += "<option value="+userEmail+">[" + userName + "]-" + userEmail + "</option>";
	});
	gridHtml += "</select>";
    $("#allUserDiv").html(gridHtml); 
    enableSearchSelect();
}
function bindEvents()
{
	$("#addUserDiv").on('show',function(){
		initAllUsers();
	});
}
$(function(){
	bindEvents();
	initSystem(function(){
		initTemplateList();
	});
});