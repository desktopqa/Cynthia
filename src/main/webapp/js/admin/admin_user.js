/*
 * @description template list
 * @autor liuyanlei
 * @date 2014.1.6
 * */

var users = null;
var userRole = null;  //当前用户角色
var userRoleMap = new Map();
var optionRightControl = false;
userRoleMap.put('normal','普通用户');
userRoleMap.put('admin','管理员');
userRoleMap.put('super_admin','系统管理员');

function initUserList(userStat,userName)
{
	showLoading(true);
	$.ajax({
		url : 'user/get_user_admin_xml.jsp',
		type : 'POST',
		data:{'initUser':true,'userStat':userStat,'userName':userName},
		success : onInitUserListAjax,
		error : function(data){
			showLoading(false);
		}
	});
}

function onInitUserListAjax(rootNode)
{
	userRole = $(rootNode).find("curUserRole").text();
	users = new Array();
	var gridHtml = "";
	$(rootNode).find("users").children("user").each(function(idx,node){
		var id = $(node).children("id").text();
		users[id] = new Object();
		users[id].id = id;
		users[id].name =$(node).children("name").text(); 
		users[id].email = $(node).children("email").text();
		users[id].pass = $(node).children("password").text();
		users[id].userRole = $(node).children("userRole").text();
		users[id].userStat = $(node).children("userStat").text();
		users[id].createTime = $(node).children("createTime").text();
		users[id].lastLoginTime = $(node).children("lastLoginTime").text();
		
		gridHtml += "<tr>";
		gridHtml += "<td>" + (idx+1) +"</td>";
		if((userRole === "super_admin" && users[id].email !== "admin") || users[id].email === readCookie('login_username'))
			gridHtml += "<td><a href=\"#\" onClick=\"displayModifyDiv('" + users[id].id + "');\">"+users[id].name+"</a></td>";
		else
			gridHtml += "<td>" + users[id].name+ "</td>";
			
		gridHtml += "<td><a href=\"mailto:"+ users[id].email + "\">" + users[id].email +"</a></td>";
		if(users[id].userRole === "normal")
			gridHtml += "<td></td>";
		else
			gridHtml += "<td>" + userRoleMap.get(users[id].userRole) +"</td>";
		gridHtml += "<td>" + users[id].createTime +"</td>";
		
		if(userRole === "super_admin")
		{
			//超级管理员有操作用户权限
			if(users[id].email !== readCookie("login_username"))
			{
				var iconClass = "icon-locked";
				var changToStat = "normal";
				var changText = "锁定";
				if(users[id].userStat === "normal")
					changToStat = "lock"; //正常变为锁定
				else if(users[id].userStat === "lock"){
					//锁定变为正常
					changToStat = "normal"; 
					iconClass = "icon-lock";
					changText = "解锁";
				}else if(users[id].userStat === "not_auth"){
					//未授权变为正常激活
					changToStat = "normal"; 
					iconClass = "icon-active";
					changText = "激活";
				}
				
				gridHtml += "<td><a href=\"#\" class=\"icon "+iconClass+"\" onclick=\"changeStat('"+users[id].email+"','"+changToStat+"');\">"+changText+"</a>" +
							"<a href=\"#\" class=\"icon icon-delete\" onclick=\"removeUser('"+users[id].id+"');\">删除</a>" +
							"</td>";
			}else{
				gridHtml += "<td></td>";
			}
		}else
		{
			gridHtml += "<td>-</td>";
		}
		gridHtml += "</tr>";
	});
	$("#userListGrid").find("tbody").html(gridHtml);
	showLoading(false);
	$("#userListGrid").trigger("update"); 
}

function changeStat(user,changeToStat)
{
	
	$.ajax({
		url : base_url + 'user/changeStat.do',
		type : 'POST',
		dataType:'text',
		data:{'user':user,'status':changeToStat},
		success : function(data){
			if(data === "true"){
				showInfoWin('success','状态修改成功');
				
				if(changeToStat === 'normal')
				{
					//如果帐户重新恢复正常,邮件通知
					var params = "sendMailReceivers=" + getSafeParam(user);
					params += "&sendMailSubject=" + getSafeParam("[Cynthia]帐户激活通知");
					params += "&sendMailContent=" + getSafeParam("您在Cynthia中的帐户己激活！<br/><a href='" + WEB_ROOT_URL+ "'>立即进入</a>");

					$.ajax({
						url : base_url + 'mail/executeSendMail.jsp',
						data : params,
						type : 'POST'
					});
				}	
				
				initUserList();
			}
			else
				showInfoWin('error','修改用户状态失败,请稍后再试!');
		},
		error : function(data){
		}
	});
}

function displayCreateDiv()
{
	if(userRole !== "super_admin")
		$("#super_role").hide();
	$('#myModalLabel').text("新建用户");
	$("#input_user_id").val('');
	$("#input_name").val('');
	$("#input_email").removeAttr('disabled');
	$("#input_email").val('');
	$("input[name=input_role][value=normal]").attr("checked",true);
	$("#input_pass").val('');
	$("#input_pass_again").val('');
	$("#msgInfo").text('');
	$("#addOrModifyUserDiv").modal('show');
}

function setTemplateRight(userMail)
{
	$('#userMail').val(userMail);
	$.ajax({
		url : base_url + 'backRight/initUserTemplateRight.do',
		type : 'POST',
		data : {'userMail':userMail},
		dataType:'json',
		success :onCompleteInitTemplateRight,
		error:function(){
			alert('服务器内部错误');
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
			if( data.projectInvolved == 'true'){
				$('.project_involved_true').show();
				$('.project_involved_false').hide();
			}else{
				$('.project_involved_true').hide();
				$('.project_involved_false').show();
			}
			if(callback){
				callback();
			}
		}
	});
}

function onCompleteInitTemplateRight(data)
{
	var gridHtml = '';
	var index = 0;
	for(var templateId in data)
	{
		gridHtml += '<tr>';
		gridHtml += '<td>' + index++ + '</td>';
		gridHtml += '<td>' + data[templateId] + '</td>';
		gridHtml += "<td><a href='#' onclick =\"deleteTemplateRight("+templateId+");\">删除</a></td>";
		gridHtml += '</tr>';
	}
	$("#temRightBody").html(gridHtml);
	
	$('#userTemRightDiv').modal('show');
}

function deleteTemplateRight(templateId)
{
	if(!window.confirm("删除后无法恢复，确定删除吗?"))
		return;
	
	$.ajax({
		url : base_url + 'backRight/delUserTemplateRight.do',
		type : 'POST',
		data : {'userMail':$("#userMail").val(),'templateId':templateId},
		success :function(data){
			if(data == 'true')
				setTemplateRight($("#userMail").val());
			else
				alert(data);
		},
		error:function(){
			alert('服务器内部错误');
		}
	});
}

function initAllTemplate()
{
	$("#userTemMail").val($("#userMail").val());
	$("#initAllTitle").html("添加表单权限:&nbsp;&nbsp;<span class=\"label label-important\">(" +$("#userMail").val()+")</span>");
	$.ajax({
		url : base_url + 'backRight/getAllTemplate.do',
		type : 'POST',
		dataType:'json',
		success :function(data){
			var gridHtml = '';
			gridHtml += "<option value=''>选择权限表单</option>"; 
			for(templateId in data){
				gridHtml += "<option value= "+templateId+">" + data[templateId] + "</option>"; 
			}
			$("#chooseTemplate").html(gridHtml);
			enableSelectSearch();
		},
		error:function(){
			alert('服务器内部错误');
		}
	});
	
	$("#initAllTemDiv").modal('show');
}

function addUserTemplate()
{
	var addTemplateId = $("#chooseTemplate").val();
	if(addTemplateId == "" || addTemplateId == null)
	{
		alert("请选择表单!");
		return;
	}
	
	$.ajax({
		url : base_url + 'backRight/addUserTemplateRight.do',
		type : 'POST',
		data:{'userMail':$("#userTemMail").val(),'templateIds':$("#chooseTemplate").val()},
		success :function(data){
			if(data == 'true'){
				setTemplateRight($("#userMail").val());
			}else{
				alert(data);
			}
			
			$("#initAllTemDiv").modal('hide');
		},
		error:function(){
			alert('服务器内部错误');
		}
	});
}

function addUser()
{
	var name = $.trim($("#input_name").val());
	if(name == "")
	{
		alert("请输入用户姓名！");
		return;
	}
	var email = $.trim($("#input_email").val());
	if(email == "")
	{
		alert("请输入用户邮箱！");
		return;
	}
	
	for(var i in users){
		if(users[i].email == email)
		{
			alert("己存在该邮箱名的用户！");
			return;
		}
	}
	
	var password = $.trim($("#input_pass").val());
	var passwordAgain = $.trim($("#input_pass_again").val());
	if(password !== passwordAgain){
		alert("两次输入密码不一致!");
		return ;
	}
	var md5password = hex_md5(password);
	var userRole = $.trim($("input[type=radio][name=input_role]:checked").val());
	$.ajax({
		url : 'user/add_user_xml.jsp',
		type : 'POST',
		data : {'name':name,'email':email,'password':md5password,'userRole':userRole},
		success :function(response){
			eval("var isError = " + $(response).find('isError').text());
			if(isError)
			{
				alert("添加用户失败！");
				return;
			}
			
			//新建用户成功，发送邮件通知
			var params = "sendMailReceivers=" + getSafeParam(email);
			params += "&sendMailSubject=" + getSafeParam("[Cynthia]帐户新建通知");
			params += "&sendMailContent=" + getSafeParam("管理员为您新建了Cynthia帐户！<br/>用户名：" + email + "<br/>密码:" + password + "<br/><a href='" + WEB_ROOT_URL+ "'>点击进入Cynthia缺陷管理系统</a>");

			$.ajax({
				url : base_url + 'mail/executeSendMail.jsp',
				data : params,
				type : 'POST'
			});
			initUserList();
		}
	});
	
	$("#addOrModifyUserDiv").modal('hide');
}

function addOrModifyUser()
{
	if($('#myModalLabel').text() === "新建用户")
		addUser();
	else
		modifyUser();
}

function displayModifyDiv(userId)
{
	$('#myModalLabel').text("修改用户");
	$("#input_user_id").val(userId);
	$("#input_name").val(users[userId].name);
	$("#input_email").val(users[userId].email);
	$("#input_email").attr('disabled','disabled');
	$("input[name=input_role][value="+users[userId].userRole+"]").attr("checked",true);
	$("#input_pass").val(users[userId].pass);
	$("#input_pass_again").val(users[userId].pass);
	$("#addOrModifyUserDiv").modal('show');
}

function modifyUser()
{
	var name = $.trim($("#input_name").val());
	if(name == "")
	{
		alert("姓名不能为空！");
		return;
	}
	var email = $.trim($("#input_email").val());
	var userRole = $.trim($("input[type=radio][name=input_role]:checked").val());
	var password = $.trim($("#input_pass").val());
	var passwordAgain = $.trim($("#input_pass_again").val());
	if(password !== passwordAgain){
		alert("两次输入密码不一致!");
		return ;
	}
	if(password !== users[$("#input_user_id").val()].pass)
		password = hex_md5(password);

//	//是否离职
//	var isquit = $.trim($("input[type=radio][name=input_isquit_m]:checked").val());
	
	$.ajax({
		url : 'user/modify_user_xml.jsp',
		type:'POST',
		data:{'name':name,'email':email,'userRole':userRole,'password':password},
		success : onCompleteModifyUser
	});
}

function onCompleteModifyUser(response)
{
	var isErrorText = $(response).find("isError").text();
	eval("var isError = " + isErrorText);
	if(isError)
	{
		alert("修改用户失败！");
		return;
	}
	initUserList();
	$("#addOrModifyUserDiv").modal('hide');
}

function removeUser(userId)
{
	if( !confirm( "您确定要删除吗?数据将不可恢复" ) )
		return;
	$.ajax({
		url : 'user/remove_user_xml.jsp',
		type:'POST',
		data : {'userId':userId},
		success : onCompleteRemoveUser
	});
}

function onCompleteRemoveUser(request)
{
	var isErrorText = $(request).find("isError").text();
	eval("var isError = " + isErrorText);
	
	if(isError)
	{
		alert("删除用户失败！");
		return;
	}
	initUserList();
}

function bindEvents()
{
	//搜索
	$("#searchWord").keydown(function(e){
		if(e.keyCode==13){
			searchItem("userListGrid","tbody tr",$('#searchWord').val());
		}
	});
	
	$("#searchUserBtn").on('click',function(){
		initUserList($('#searchType').val(),$('#searchWord').val());
	});
	
	$("#searchType").on('change',function(){
		initUserList($('#searchType').val(),'');
	});
	
	$("#input_email").blur(checkUser);
}

function checkUser()
{
	var userMail = $("#input_email").val();
	if(userMail == "")
		return;
	
	if(!isEmail(userMail))
	{
		$("#msgInfo").text("邮箱格式不正确!");
		return;
	}
	$.ajax({
		url: base_url + 'user/checkExist.do',
		data:{'userMail':userMail},
		type:'POST',
		dataType:'text',
		success:function(data){
			if(data === "true")
			{
				$("#msgInfo").text("该邮箱己被注册");
				$("#input_email").focus();
				return;
			}
			else
			{
				$("#msgInfo").text("");
			}
		}
	});
}
$(function(){
	
	bindEvents();
	//初始化表单列表
	$("#userListGrid").tablesorter({
		headers: 
		{ 
	        4:{sorter: false}, 
	        5:{sorter: false},
	        6:{sorter: false}
		}
	}); 
	initSystem(initUserList);
});