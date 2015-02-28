var center_x = 594;
var center_y = 321;
var actions = null;
var stats = null;
var roles = null;
var isProFlow = false;

//添加状态
function executeAddStat()
{
	var statName = trim($("#statName").val());
	if(statName == "")
	{
		alert("状态名称不能为空！");
		return;
	}
	$('#myflow').drawStat($("#mouse_x").val(),$("#mouse_y").val(),statName,'state');
}

function addStat(statName,callback){
	var param = "name=" + getSafeParam(statName);
	param += "&flowId=" + $("#flowId").val();
	$.ajax({
		url: 'flow/add_Stat_xml.jsp',
		type :'POST',
		data: param,
		success: function(request){
			eval("var isError   = " + $(request).find("isError").text());
			var id = $(request).find("id").text();
			if(!isError && id != undefined){
				initFlowActionStat(function(){
					initActionStatSelection();
					if(callback){
						callback(id);
					}
				});
			}
		}
	});
}

function removeStat(statId)
{
	var result = true;
	$.ajax({
		url: 'flow/remove_Stat_xml.jsp',
		type :'POST',
		dataType:'xml',
		data: {'id':getSafeParam(statId),'flowId':$("#flowId").val()},
		success: function(request){
			eval("var isError   = " + $(request).find("isError").text());
			result = !isError;
			if(result)
				initFlowActionStat();
		}
	});
	
	return result;
}

function addOrModifyStat(){
	$("#cfgStateDiv").modal('hide');
	if($("#myModalStateTag").text() == "新建状态")
		executeAddStat();
	else
		executeModifyStats();
}
	
function executeModifyStats()
{
	var statId = $("#statId").val();
	var statName = trim($("#statName").val());
	if(statName == "")
	{
		alert("状态名称不能为空！");
		return;
	}
	
	var param = "id=" + getSafeParam(statId);
	param += "&name=" + statName;
	param += "&flowId=" + $("#flowId").val();
	
	$.ajax({
		url: 'flow/modify_Stat_xml.jsp',
		type :'POST',
		dataType:'xml',
		data: param,
		success: function(request){
			eval("var isError   = " + $(request).find("isError").text());
			if(!isError)
			{
				$('#myflow').changeStat(statId,statName);
				initFlowActionStat();
			}else{
				alert("修改失败!");
				return;
			}
		}
	});
}

//添加动作
function executeAddAction()
{
	var actionName = trim($("#actionName").val());
	if(actionName == "")
	{
		alert("动作名称不能为空!");
		return;
	}
	$('#myflow').drawAction(actionName);
	$("#myflow").cacelModifyAction();
	$("#cfgActionDiv").modal('hide');
}

function addAction(fromStatId,toStatId,actionName,callback,from_node,to_node){
	var param = "name=" + getSafeParam(actionName);
	param += "&flowId=" + $("#flowId").val();
	param += "&endStatId=" + getSafeParam(toStatId);
	if(fromStatId != "")
		param += "&beginStatId=" + getSafeParam(fromStatId);
	
	$.each($("input[name='actionRole']"), function(index,node){
		if($(node).prop('checked') == true)
			param += "&roleRight=" + $(node).val() + "|true";
		else
			param += "&roleRight=" + $(node).val() + "|false";
	});
	
	//添加指派多人配置
	param += "&assignToMore=" + $("input[type=radio][name='assignToMore']:checked").val();
	
	$.ajax({
		url: 'flow/add_Action_xml.jsp',
		type :'POST',
		dataType:'xml',
		data: param,
		success: function(request){
			eval("var isError   = " + $(request).find("isError").text());
			var id = $(request).find("id").text();
			if(!isError && id != undefined){
				initFlowActionStat(function(){
					if(callback){
						callback(id,from_node,to_node);
					}
				});
			}
		}
	});
}

function removeAction(actionId)
{
	var result = true;
	$.ajax({
		url: 'flow/remove_Action_xml.jsp',
		type :'POST',
		dataType:'xml',
		data: {'id':getSafeParam(actionId),'flowId':$("#flowId").val()},
		success: function(request){
			eval("var isError   = " + $(request).find("isError").text());
			result = !isError;
			if(result)
				initFlowActionStat();
		}
	});
	
	return result;
}

function addOrModifyAction(){
	if($("#myModalActionTag").text() == "新建动作")
		executeAddAction();
	else
		executeModifyAction();
}
	
//修改动作
function executeModifyAction()
{
	var beginStatId = $("#beginStatId").val();
	var endStatId = $("#endStatId").val();
	var actionId = $("#actionId").val();
	var actionName = $("#actionName").val();
	var param = "actionId=" + getSafeParam(actionId);
	
	if(actionId != '48' && actionId != '47' && actionId != '51'){
		param += "&beginStatId=" + getSafeParam(beginStatId);
		param += "&endStatId=" + getSafeParam(endStatId);
		param += "&actionName=" + getSafeParam(actionName);
		
		if(beginStatId == endStatId){
			alert('开始结束不能为同一状态!');
			return;
		}
		if(endStatId == ''){
			alert('不能回到开始状态!');
			return;
		}
		//判断状态之间是否己存在动作
		for(var i in actions)
		{
			if(actions[i].id != actionId && actions[i].fromStatId == beginStatId && actions[i].toStatId == endStatId)
			{
				alert('开始结束状态之间己存在其它动作!');
				return;
			}
		}
	}
	
	param += "&flowId=" + getSafeParam($("#flowId").val());

	$.each($("input[name='actionRole']"), function(index,node){
		if($(node).prop('checked') == true)
			param += "&roleRight=" + $(node).val() + "|true";
		else
			param += "&roleRight=" + $(node).val() + "|false";
	});
	
	//添加指派多人配置
	param += "&assignToMore=" + $("input[type=radio][name='assignToMore']:checked").val();
	
	$.ajax({
		url: 'flow/save_RoleRight_xml.jsp',
		type :'POST',
		dataType:'xml',
		data: param,
		success: function(request){
			eval("var isError   = " + $(request).find("isError").text());
			if(!isError)
			{
				initFlowActionStat();
				if(beginStatId == '')
					beginStatId = 'start';
				if(actionId != '48' && actionId != '47' && actionId != '51')
					$('#myflow').changeAction(actionId,actionName,beginStatId,endStatId);
			}
		}
	});
	$("#cfgActionDiv").modal('hide');
}

function initActionRole(actionId)
{
	roles = new Array();
	$.ajax({
		url: base_url + 'flow/getActionRole.do',
		type :'POST',
		dataType:'json',
		data: {'flowId':$("#flowId").val(),'actionId':actionId},
		success: function(data){
			$("#actionRoleDiv").html("");
			for(var i in data.allRole){
				roles[i] = new Object();
				roles[i].id = data.allRole[i].fieldId;
				roles[i].name = data.allRole[i].fieldName;
				if(roles[i].id == "82")  //everyone
					$("#actionRoleDiv").append("<label class=\"checkbox\" style=\"color:red;margin-right:15px;\">" +
							"<input type=\"checkbox\" name=\"actionRole\" value="+roles[i].id+">"+roles[i].name+"</label>");
				else
					$("#actionRoleDiv").append("<label class=\"checkbox\" style=\"margin-right:15px;\">" +
							"<input type=\"checkbox\" name=\"actionRole\" value="+roles[i].id+">"+roles[i].name+"</label>");
			}
			if(!isProFlow)
				$("#actionRoleDiv").append("<div class='addMoreRole' data-toggle='modal' data-target='#addRoleDiv'><a href='javascript:;' class='addComp' onclick=''><i class='icon-plus'></i><span>添加角色</span></a></div>");
			
			for(var i in data.actionRole){
				$("input[name='actionRole'][value='"+data.actionRole[i].fieldId+"']").attr("checked", 'true');
			}
		},
		error:function(msg){
		}
	});
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
	var flowId = $("#flowId").val();
	$.ajax({
		url:'flow/add_Role_xml.jsp',
		type:'post',
		data:{'name':roleName,'flowId':flowId},
		success:function(request){
			eval("var isError   = " + $(request).find("isError").text());
			if(!isError)
			{
				initActionRole($("#actionId").val());
				initWordActionRole($("#actionId").val());
			}
			else
			{
				alert("添加角色失败!");
				return;
			}
		},
		error:function(){
			alert("添加角色失败!");
			return;
		}
	});
}

//根据flow xml绘制svg图
function convertXmlToSvg(flowXml){
	//绘制开始节点
	$('#myflow').drawStat(center_x,center_y,'开始','start','start');
	
	$(flowXml).find("flow").find("stats").children("stat").each(function(idx,node){
		var statId = $(node).children("id").text();
		var statName = $(node).children("name").text();
		$('#myflow').drawStat(center_x,center_y,statName,'state',statId);
	});
	
	$(flowXml).find("flow").find("actions").children("action").each(function(idx,node){
		var actionId = $(node).find("id").text();
		var actionName = $(node).find("name").text();
		var fromStatId = $(node).find("startStatId").text();
		var toStatId = $(node).find("endStatId").text();
		
		if(fromStatId == undefined || fromStatId == null || fromStatId == ""){
			fromStatId = "start"; //开始状态
		}
		
		if(fromStatId != "" && toStatId != ""){
			$('#myflow').drawAction(actionName,actionId,fromStatId, toStatId);
		}
	});
}

function getFlowXml(callback){
	$.ajax({
		url: base_url + 'flow/getFlowXml.do',
		type :'POST',
		dataType:'xml',
		data: {'flowId':$("#flowId").val()},
		success: function(data){
			if(callback){
				callback(data);
			}
		}
	});
}

function getFlowSvg(callback){
	$.ajax({
		url:  base_url + 'flow/initFlowSvg.do',
		type :'POST',
		dataType:'text',
		data: {'flowId':$("#flowId").val()},
		success: function(data){
			if(callback){
				callback(data);
			}
		},
		error:function(msg){
			alert("新建失败！");
		}
	});
}

function initFlowSvg(callback)
{
	getFlowSvg(function(flowSvg){
		setSvg(flowSvg,callback);
	});
}

function setSvg(data,callback){
	var edit = request("edit") ? request("edit") === "true" : true;
	//新建流程
	if(data == ""){
		$('#myflow').myflow(
		{
			basePath : "",
			edit:edit
		});
		getFlowXml(function(flowXml){
			convertXmlToSvg(flowXml);
			if(callback){
				callback();
			}
		});
	}else{
		var svgCode = '(' + data + ')';
		$('#myflow').myflow(
		{
			basePath : "",
			edit:edit,
			restore : eval(svgCode)
		});
		if(callback){
			callback();
		}
	}
}

function saveFlowSvg(svgCode){
	var success = true;
	$.ajax({
		url: base_url + 'flow/saveFlowSvg.do',
		type :'POST',
		dataType:'text',
		data: {'flowId':$("#flowId").val(),'svgCode':svgCode},
		success: function(data){
			if(data == "success")
				success = true;
			else
				showInfoWin('error','保存失败!');
		},
		error:function(msg){
		}
	});
	return success;
}

function initFlowActionStat(callback){
	getFlowXml(function(flowXml){
		stats = new Array();
		isProFlow=$(flowXml).find('flow').find('isProFlow').text() == 'true';
		if(isProFlow){
			$('.pro_involved_false').hide();  //项目相关则隐藏新建角色
		}else{
			$('.pro_involved_false').show();
		}
		
		$(flowXml).find("flow").find("stats").children("stat").each(function(idx,node){
			var statId = $(node).find("id").text();
			var statName = $(node).find("name").text();
			stats[statId] = new Object();
			stats[statId].id = statId;
			stats[statId].name = statName;
		});
		
		actions = new Array();
		var gridHtml = "";
		gridHtml += "<tr><td>0</td><td>-</td><td><a href=\"#\" onClick=\"showEditActionDiv('48','编辑')\">编辑</a></td><td>-</td><td>-</td><tr>";
		gridHtml += "<tr><td>1</td><td>-</td><td><a href=\"#\" onClick=\"showEditActionDiv('47','查看')\">查看</a></td><td>-</td><td>-</td><tr>";
		gridHtml += "<tr><td>2</td><td>-</td><td><a href=\"#\" onClick=\"showEditActionDiv('51','删除')\">删除</a></td><td>-</td><td>-</td><tr>";
		
		try{
			$(flowXml).find("flow").find("actions").children("action").each(function(idx,node){
				var actionId = $(node).find("id").text();
				var actionName = $(node).find("name").text();
				var fromStatId = $(node).find("startStatId").text();
				var assignToMore = $(node).find("assignToMore").text();
				if(fromStatId == undefined || fromStatId == null || fromStatId == ""){
					fromStatId = "start"; //开始状态
				}
				var toStatId = $(node).find("endStatId").text();
				actions[actionId] = new Object();
				actions[actionId].id = actionId;
				actions[actionId].name = actionName;
				actions[actionId].fromStatId = fromStatId;
				actions[actionId].toStatId = toStatId;
				actions[actionId].assignToMore = assignToMore;

				gridHtml += "<tr>";
				gridHtml += "<td>" + (idx+3) +"</td>";
				if(fromStatId == "start")
					gridHtml += "<td><font style=\"color:red\">开始</font></td>";
				else
					gridHtml += "<td><a href=\"#\" onClick=\"showEditStatDiv('" + fromStatId + "','" + stats[fromStatId].name + "')\">"+ stats[fromStatId].name +"</a></td>";
				gridHtml += "<td><a href=\"#\" onClick=\"showEditActionDiv('" + actionId + "','"+actions[actionId].name+"')\">"+ actions[actionId].name +"</a></td>";
				gridHtml += "<td><a href=\"#\" onClick=\"showEditStatDiv('" + toStatId + "','" +stats[toStatId].name+ "')\">"+ stats[toStatId].name +"</a></td>";
				gridHtml += "<td><a href=\"#\" onClick=\"removeWordAction('" + actionId + "')\">删除</a></td>";
				gridHtml += "</tr>";
			});
		}catch(e){
			alert("数据初始化有错,请联系管理员.");
		}
		$("#wordActionTable").html(gridHtml);
		if(callback) callback();
	});
}


function removeWordAction(actionId){
	if(!confirm("数据将不可恢复！您确定要删除吗？"))
		return;
	$("#myflow").removeAction(actionId);
}

function showEditStatDiv(statId,statName){
	$("#myModalStateTag").text('编辑状态');
	$("#statId").val(statId);
	$("#statName").val(statName);
	$("#cfgStateDiv").modal('show');
}

function showEditActionDiv(actionId,actionName){
	initActionRole(actionId);
	$(".action_high_right").hide();
	$(".send_mail_div").hide();
	$("input[type=radio][name=assignToMore][value='false']").attr("checked","checked");
	$("input[type=radio][name=sendMore][value='false']").attr("checked","checked");
	$("#actionId").val(actionId);
	$("#actionName").val(actionName).removeAttr("disabled");
	if(!actionId && !actionName){
		$("#myModalActionTag").text('新建动作');
	}else{
		var action = actions[actionId];
		initStatDiv(actionId);
		if(action && action.assignToMore == 'true'){
			$(".action_high_right").show();
			$("input[type=radio][name=assignToMore][value='true']").attr("checked","checked");
		}
		
		$("#myModalActionTag").text('编辑动作');
		if(actionId == '48' || actionId == '47' || actionId == '51'){
			$("#actionName").attr("disabled","true");
		}
	}
	$("#cfgActionDiv").modal('show');
}

function initStatDiv(actionId)
{
	var selectHtml = '';
	selectHtml += "<option value=''>开始</option>";
	for(var i in stats){
		selectHtml += "<option value='"+stats[i].id+"'>" +stats[i].name+ "</option>";
	}
	$('#beginStatId').html(selectHtml);
	$('#endStatId').html(selectHtml);
	$('#actionId').val(actionId);
	if(actionId == "47" || actionId == "48" || actionId == "51"){
		$('#beginStatId').val('');
		$('#endStatId').val('');		
		hideStatDiv(true);
	}else{
		$('#beginStatId').val(actions[actionId].fromStatId == 'start' ? '':actions[actionId].fromStatId);
		$('#endStatId').val(actions[actionId].toStatId);		
		hideStatDiv(false);
	}
}

function hideStatDiv(hidden)
{
	if(hidden)
		$("#cfgActionDiv select.stat").parent().hide();
	else
		$("#cfgActionDiv select.stat").parent().show();
}

//文字版新建动作
function creatWordAction(){
	hideStatDiv(true);
	initWordActionRole();
	initActionStatSelection();
	$("#cfgWordActionDiv").modal('show');
}

function initActionStatSelection(){
	var gridHtml = "";
	$("#actionFromId").html("");
	$("#actionToId").html("");
	gridHtml += "<option value=''>--请选择状态--</option>";
	for(var i in stats){
		gridHtml += "<option value='"+ stats[i].id +"'>"+stats[i].name+"</option>";
	}
	$("#actionFromId").html(gridHtml);
	$("#actionToId").html(gridHtml);
}

function creatWordStat(){
	$("#myModalStateTag").text('新建状态');
	$("#statId").val('');
	$("#mouse_x").val(center_x);
	$("#mouse_y").val(center_y);
	$("#statName").val('');
	$('#cfgStateDiv').modal('show');
}

function cacelModifyAction(){
	$("#myflow").cacelModifyAction();
}

function addWordAction(){
		var actionName = trim($("#wordActionName").val());
		if(actionName == "")
		{
			alert("动作名称不能为空!");
			return;
		}
		
		var fromStatId = $("#actionFromId").val();
		var toStatId = $("#actionToId").val();
		
		if(fromStatId == toStatId){
			alert('开始结束不能为同一状态!');
			return;
		}
		
		if(toStatId == ""){
			alert("结束状态不能为空!");
			return;
		}
		
		//判断状态之间是否己存在动作
		for(var i in actions)
		{
			if(actions[i] != null && actions[i].fromStatId == fromStatId && actions[i].toStatId == toStatId)
			{
				alert('开始结束状态之间己存在其它动作!');
				return;
			}
		}
		
		$("#cfgWordActionDiv").modal('hide');
		
		if(fromStatId == "")
			fromStatId = "start";
		
		var param = "name=" + getSafeParam(actionName);
		param += "&flowId=" + $("#flowId").val();
		param += "&endStatId=" + getSafeParam(toStatId);
		if(fromStatId != "")
			param += "&beginStatId=" + getSafeParam(fromStatId);
		
		$.each($("input[name='wordActionRole']"), function(index,node){
			if($(node).prop('checked') == true)
				param += "&roleRight=" + $(node).val() + "|true";
			else
				param += "&roleRight=" + $(node).val() + "|false";
		});
		
		//添加指派多人配置
		param += "&assignToMore=" + $("input[type=radio][name='assignToMore']:checked").val();
		
		$.ajax({
			url: 'flow/add_Action_xml.jsp',
			type :'POST',
			dataType:'xml',
			data: param,
			success: function(request){
				eval("var isError   = " + $(request).find("isError").text());
				var id = $(request).find("id").text();
				if(!isError && id != undefined)
				{
					$("#myflow").drawAction(actionName,id,fromStatId, toStatId);
					initFlowActionStat();
					showInfoWin("success","动作添加成功!");
				}
			},
			error:function(msg){
				showInfoWin("error","动作添加失败!");
			}
		});
}

function initWordActionRole(actionId)
{
	roles = new Array();
	$.ajax({
		url: base_url + 'flow/getActionRole.do',
		type :'POST',
		dataType:'json',
		data: {'flowId':$("#flowId").val(),'actionId':actionId},
		success: function(data){
			$("#wordActionRoleDiv").html("");
			for(var i in data.allRole){
				roles[i] = new Object();
				roles[i].id = data.allRole[i].fieldId;
				roles[i].name = data.allRole[i].fieldName;
				
				if(data.allRole[i].id == "82")  //everyone
					$("#wordActionRoleDiv").append("<label class=\"checkbox\" style=\"color:red;margin-right:15px;\"><input type=\"checkbox\" name=\"wordActionRole\" value="+data.allRole[i].fieldId+">"+data.allRole[i].fieldName+"</label>");
				else
					$("#wordActionRoleDiv").append("<label class=\"checkbox\" style=\"margin-right:15px;\"><input type=\"checkbox\" name=\"wordActionRole\" value="+data.allRole[i].fieldId+">"+data.allRole[i].fieldName+"</label>");
			}
			if(!isProFlow)
				$("#wordActionRoleDiv").append("<div class='addMoreRole' data-toggle='modal' data-target='#addRoleDiv'><a href='javascript:;' class='addComp' onclick=''><i class='icon-plus'></i><span>添加角色</span></a></div>");
			
		}
	});
}

function initFlowId(){
	var flowId = request('flowId');
	var flowName = request('flowName');
	window.document.title = decodeURIComponent(flowName);
	if(flowId){
		 $("#flowId").val(flowId);
	}else{
		 alert("初始化错误，没传递正确的流程id");
		 return;
	}
}
function bindKeyEvents()
{
	//搜索
	$("#searchValue").keydown(function(e){
		if(e.keyCode==13){
		   $('#myflow').searchNode($("#searchType").val(),$("#searchValue").val());
		}
	});
	
	//绑定enter键
	$("#cfgStateDiv").keydown(function(e){ 
        e=e||window.event;
        if(e.keyCode==13){
    	   addOrModifyStat();
    	   return false;
        }
	});
	
	$("#cfgActionDiv").keydown(function(e){ 
        e=e||window.event;
        if(e.keyCode==13){
        	stopDefault(e);  
        	addOrModifyAction();
    	    return false;
        }
	});
	
	$("#role_name").keydown(function(e){ 
        e=e||window.event;
        if(e.keyCode==13){
        	addRole();
    	    return false;
        }
	});
	
	$("#cfgWordActionDiv").keydown(function(e){ 
        e=e||window.event;
        if(e.keyCode==13){
        	addWordAction();
    	    return false;
        }
	});
	
	$("#action_high_btn").click(function(){
		$(".action_high_right").toggle();
	});
	
 	$(".type_change div").on('click',function(e){
 		$(".tab-panel").hide();
 		$(".type_change div").removeClass('active');
 		$(this).addClass('active');
 		$("#" + $(this).attr("value")).show();
 		if($(this).attr("value") == 'chart'){
 			showInfoWin("success","您己切换至图型版!");
	     	$('#myflow').clearMap();
	     	initFlowSvg();
 		}else{
 			showInfoWin("success","您己切换至基本版!");
	     	$("#myflow").saveFlowSvg();
	     	initFlowActionStat();
 		}
 	});
}

$(function(){
	initFlowId();
	bindKeyEvents();
	enableSelectSearch();
	//默认打开图型版
 	initFlowSvg(initFlowActionStat);
	$('#addRoleDiv').on('shown',function(e){
		$('#role_name').val('');
	});
});

