var operation = "";
var templateTypeId = null;
var templateId = null;
var taskTemplateId = null;
var taskId = null;
var task = new Object();
var fields = new Array();
var rows   = new Array();
var roles = new Array();
var actions = new Array();
var needAssignUser = false;
var allDefaultValueMap = new Map();
var selectedTemplate = null;
var mailHeader = "";
var gridIndex = -1;
var url = null;
var editor = null;
var lastActionRoles = null;  //下一步的角色id
var isProTemplate = false;  //是否为项目管理表单
var productInvolvedId = null; //项目管理关联产品字段id
var projectInvolvedId = null; //项目管理关联项目字段id
var dataTagArray = new Array();
var base_url = getRootDir();

var filterId = null;
var selectNextActionHtml = "";

var nextActionName = "";

var options = {
	filterMode : true,
 	uploadJson: base_url + 'attachment/upload_json.jsp',
	allowFileManager : true,
	urlType : 'relative',
	formatUploadUrl : false,
	height: 277,
	resizeType : 0
};

function isExisted(arr,value)
{
	for(var i=0;i<arr.length;i++)
	{
		if(arr[i] == value)
			return true;
	}
	return false;
}

function getAllDefaultValues(templateId)
{
	if(templateId && templateId != ''){
		$.ajax({
			url:'defaultValue/getDefaultValues.do',
			async:false,
			data:{'templateId':templateId},
			dataType:'json',
			success:function(data){
				for(var key in data){
					allDefaultValueMap.put(key,data[key]);
				}
			}
		});
	}
}

function init_param(param_operation, param_templateTypeId, param_taskId, param_filterId, param_clean, param_selectedTemplate, param_gridIndex, param_url)
{
	getAllDefaultValues(param_selectedTemplate);
	operation = param_operation;
	templateTypeId = param_templateTypeId;
	taskId = param_taskId;
	filterId = param_filterId;
	clean = param_clean;
	selectedTemplate = param_selectedTemplate;
	gridIndex = param_gridIndex;
	url = param_url;
	task = new Object();
	fields = new Array();
	rows = new Array();
	roles = new Array();
	needAssignUser = false;
	$("#actionForm").hide();
	removeActionName();

	var params = "operation=" + operation;
	params += "&templateTypeId=" + getSafeParam(templateTypeId);
	params += "&taskId=" + getSafeParam(taskId);
	params += "&templateId=" + getSafeParam(selectedTemplate);

	return params;
}

function addLoading()
{
	$("#loading").show();
}
function removeLoading()
{
	$("#loading").hide();
}

function hideToolbar()
{
	$(".ke-toolbar").hide();
	$(".ke-edit").height(222);
	$(".ke-edit-iframe").height(222);
}

function showToolbar()
{
	$(".ke-toolbar").show();
	$(".ke-edit").height(277);
	$(".ke-edit-iframe").height(277);
}

function initTaskManagement(param_operation, param_templateTypeId, param_taskId, param_filterId, param_clean, param_selectedTemplate, param_gridIndex, param_url,type)
{
	
	addLoading();
	var params = init_param(param_operation, param_templateTypeId, param_taskId, param_filterId, param_clean, param_selectedTemplate, param_gridIndex, param_url);
	if(operation == '' || operation == 'create')
		$("#tagLinkTitle").hide();
	else
		$("#tagLinkTitle").show();
	
	$.ajax({
		url : 'task/initTaskManagement.jsp',
		type : 'post',
		dataType : 'xml',
		data: params,
		success : function(request){
			onCompleteInitTaskManagement(request,type);
		}
	});
}

//初始化数据的属性
function initTaskFields(rootNode)
{
	var taskNode = $(rootNode).children("task");
	task.templateId = $(taskNode).children("templateId").text();
	templateId = task.templateId;
	task.id = $(taskNode).children("id").text();
	task.title = $(taskNode).children("title").text();
	task.createUser = $(taskNode).children("createUser").text();
	task.createUserAlias = $(taskNode).children("createUser").attr("alias");
	task.createTime = formatDateStr($(taskNode).children("createTime").text());
	task.lastModifyTime = formatDateStr($(taskNode).children("lastModifyTime").text());
	task.assignUser = $(taskNode).children("assignUser").text();
	task.assignUserAlias = $(taskNode).children("assignUser").attr("alias");
	task.statusId = $(taskNode).children("statusId").text();
	task.flowId = $(taskNode).children("flowId").text();
	task.statusName = $(rootNode).children("statusName").text();
	task.templateName = $(taskNode).children("templateName").text();
	document.title = task.title;
	task.description = $(taskNode).children("description").text();
}

function onCompleteInitTaskManagement(request,type)
{
	var rootNode = $(request).children("root");
	var taskNode = $(rootNode).children("task");

	eval("var isError = " + $(rootNode).children("isError").text());
	isProTemplate = $(rootNode).children("isProTemplate").text() == 'true';
	productInvolvedId = $(rootNode).children("productInvolvedId").text();
	projectInvolvedId = $(rootNode).children("projectInvolvedId").text();
	lastActionRoles = $(rootNode).children("lastActionRoles").text();

	if(isError)
	{
		var errorInfo;
		var errorInfoNode = $(rootNode).children("errorInfo");
		if(errorInfoNode){
			errorInfo = errorInfoNode.text();
		}else{
			errorInfo = "该ID不存在或它的数据有异常!";
		}
		errorInfo += "确定后窗口将关闭..";
		alert(errorInfo);
		window.close();
		return;
	}

	var roleNodes = $(rootNode).children("roles").children("role");
	for(var i = 0; i < roleNodes.length; i++)
	{
		roles[i]      = new Object();
		roles[i].id   = $(roleNodes[i]).children("id").text();
		roles[i].name = $(roleNodes[i]).children("name").text();
	}
	
	var actionNodes = $(rootNode).children("actions").children("action");
	for(var i = 0; i < actionNodes.length; i++)
	{
		var actionId = $(actionNodes[i]).children("id").text();
		actions[actionId] = new Object();
		actions[actionId].id = actionId;
		actions[actionId].name = $(actionNodes[i]).children("name").text();
		actions[actionId].beginStatId = $(actionNodes[i]).children("beginStatId").text();
		actions[actionId].endStatId = $(actionNodes[i]).children("endStatId").text();
		actions[actionId].assignToMore = $(actionNodes[i]).children("assignToMore").text();
		actions[actionId].nextActionRoles = $(actionNodes[i]).children("nextActionRoles").text();
		actions[actionId].isEndAction = $(actionNodes[i]).children("isEndAction").text();
	}
	
	var baseFieldForm = "";
	var actionForm    = "";
	
	//新建任务
	if(operation == 'create')
	{
		//title
		baseFieldForm += "<div class='control-group'>";
		baseFieldForm += 	"<label class='control-label span2' for='input_taskTitle'>标题:</label>";
		baseFieldForm +=   "<div class='controls'>";
		baseFieldForm +=   	"<input type='text' id='input_taskTitle' placeholder='标题' class='span10' />";
		baseFieldForm +=    "</div>";
		baseFieldForm +=  "</div>";
		//项目名称
		baseFieldForm += "<div class='control-group'>";
		baseFieldForm += 	"<label class='control-label span2' for='select_template'>项目名称:</label>";
		baseFieldForm +=   "<div class='controls'>";
		baseFieldForm +=   	"<select id='select_template' class='span10' onChange='selectTemplate()'>";
		baseFieldForm +=       	"<option value='' "+(selectedTemplate == null ? " selected" : "")+">--请选择--</option>";
		var cookieTemplate = readCookie("template");
		var templateNodes = $(rootNode).children("templates").children("template");
		for(var i = 0 ; i < templateNodes.length; i++)
		{
			var templateId = $(templateNodes[i]).children("id").text();
			var templateName = $(templateNodes[i]).children("name").text();
			baseFieldForm +=      "<option value='" + templateId + "'" + ((templateId == selectedTemplate || templateId == cookieTemplate)? " selected" : "") + ">" + getXMLStr(templateName) + "</option>";
		}
		baseFieldForm += 		"</select>";
		baseFieldForm +=     "<i style='cursor:pointer'  class='icon-heart-empty' title='设为默认值' onClick='setDefaultTemplate()'></i>";
		baseFieldForm +=    "</div>";
		baseFieldForm +=  "</div>";
		
		//将要执行
		baseFieldForm += "<div class='control-group'>";
		baseFieldForm += 	"<label class='control-label span2' for='select_action' onClick='setDefaultAction()'>将要执行:</label>";
		baseFieldForm +=   "<div class='controls'>";
		baseFieldForm +=   	"<select id='select_action' class='span10' onChange='selectAction()' >";
		if(selectedTemplate != null)
			baseFieldForm += " style='display:none'";
		baseFieldForm += "/>";
		baseFieldForm +=       	"<option value=''>--请选择--</option>";
		baseFieldForm += 		"</select>";
		baseFieldForm +=     "<i style='cursor:pointer'  class='icon-heart-empty' title='设为默认值' onClick='setDefaultAction()'></i>";
		baseFieldForm +=    "</div>";
		baseFieldForm +=  "</div>";

		//将要指派
		baseFieldForm += "<div class='control-group'>";
		baseFieldForm += 	"<label class='control-label span2' for='select_taskAssignUser' onClick='setDefaultAssignUser()'>将要指派:</label>";
		baseFieldForm +=   "<div class='controls'>";
		baseFieldForm +=   	"<select id='select_taskAssignUser' class='span10'>";
		baseFieldForm +=       	"<option value='' selected>--请选择--</option>";
		baseFieldForm += 		"</select>";
		baseFieldForm +=     "<i style='cursor:pointer' class='icon-heart-empty' title='设为默认值' onClick='setDefaultAssignUser()'></i>";
		baseFieldForm +=    "</div>";
		baseFieldForm +=  "</div>";
		
	}else //修改 or 只读任务
	{
		eval("var isRead = " +$(rootNode).children("isRead").text());
		if(!isRead)
		{
			alert("您没有查看该数据的权限！");
			return;
		}
		
		eval("var isEdit = " + $(rootNode).children("isEdit").text());
		templateTypeId = $(rootNode).children("templateTypeId").text();
		
		var templateId = $(rootNode).children("templateId").text();
		//对应有些数据打开没有templateId
		if(allDefaultValueMap.size() == 0)
			getAllDefaultValues(templateId);
		
		initTaskFields(rootNode);
		$("#input_taskDescription").val(task.description);
		setTitleAndTag();  //设置标题和标签
		$("#baseFieldForm").html(getBaseFieldFormHtml(task));
		
		actionForm += "<div class='control-group' style='display:none;'>";
		actionForm += 	"<label class='control-label span2' for='select_action'>动作名称:</label>";
		actionForm +=   "<div class='controls'>";
		actionForm +=   	"<select id='select_action'>";
		
		if(!isEdit){  //创建人自身有编辑权限
			isEdit =  $(taskNode).children("createUser").text() == readCookie('login_username');
		}
		
		if(isEdit)
		{
			actionForm += "<option value='|" + task.statusId +"'>编辑</option>";
		}
		
		var actionNodes = $(rootNode).children("actions").children("action");
		for(var i = 0; i < actionNodes.length; i++)
		{
			var actionId    = $(actionNodes[i]).children("id").text();
			var actionName  = $(actionNodes[i]).children("name").text();
			var beginStatId = $(actionNodes[i]).children("beginStatId").text();
			var endStatId   = $(actionNodes[i]).children("endStatId").text();

			actionForm += "<option value='" + actionId + "|" + endStatId + "'>" + (beginStatId == "" ? "激活--" : "") + actionName + "</option>";
		}
		actionForm += 		"</select>";
		actionForm +=    "</div>";
		actionForm +=  "</div>";
		
		actionForm += "<div class='control-group' style='display:none;'>";
		actionForm += 	"<label class='control-label span2' for='input_action'>将要执行:</label>";
		actionForm +=   "<div class='controls'>";
		actionForm +=   	"<input type='text' id='input_action' class='span10' readOnly/>";
		actionForm +=    "</div>";
		actionForm +=  "</div>";
		
		actionForm += "<div class='control-group'>";
		actionForm += 	"<label class='control-label span2' for='select_taskAssignUser' onClick='setDefaultAssignUserRead()'>将要指派:</label>";
		actionForm +=   "<div class='controls'>";
		actionForm +=   	"<select id='select_taskAssignUser' class='span10'>";
		actionForm +=        	"<option value=''>--请选择--</option>";
		actionForm += 		"</select>";
		actionForm +=     "<i class='icon-heart-empty' realValue='设为默认值' onClick='setDefaultAssignUserRead()'></i>";
		actionForm +=    "</div>";
		actionForm +=  "</div>";
		
		actionForm += "<div class='control-group'>";
		actionForm += 	"<label class='control-label span2' for='input_actionDescription'>执行描述:</label>";
		actionForm +=   "<div class='controls'>";
		actionForm +=   	"<textarea id='input_actionDescription' class='span10'></textarea>";
		actionForm +=    "</div>";
		actionForm +=  "</div>";
	}
	
	if(operation == "create")
	{
		$("#baseFieldForm").html(baseFieldForm);
		$("#select_next_action_top").parent().hide();
		var templateId = $("#select_template").val();
		var descrition = unescape(allDefaultValueMap.get(templateId+"description")==null ? "" : allDefaultValueMap.get(templateId+"description"));
		$("#input_taskDescription").val(descrition);
	}else
	{
		$("#select_next_action_top").parent().show();
		$("#actionForm").html(actionForm);
	}

	if(operation == "create")
	{
		var topSubmitDiv = "";
		topSubmitDiv += "<div class='btn-group'>";
		topSubmitDiv += 	"<button class='btn  btn-danger' id='input_top_submit' onClick='executeSubmit(0)'>提交并查看</button>";
		topSubmitDiv += "</div>";
		
		topSubmitDiv += "<div class='btn-group'>";
		topSubmitDiv += 	"<button class='btn  btn-primary' id='input_top_submit_close' onClick='executeSubmit(1)'>提交并关闭页面</button>";
		topSubmitDiv += "</div>";
		
		topSubmitDiv += "<div class='btn-group'>";
		topSubmitDiv += 	"<button class='btn  btn-primary' id='input_top_submit_new' onClick='executeSubmit(2)'>提交并再新建1条</button>";
		topSubmitDiv += "</div>";
		
		topSubmitDiv += "<div class='btn-group'>";
		topSubmitDiv += 	"<button class='btn btn-primary' onClick='window.close()'>关闭页面</button>";
		topSubmitDiv += "</div>";
		
		topSubmitDiv += "<div class='btn-group'>";
		topSubmitDiv += 	"<button class='btn btn-primary' onClick='setUserDefaultTemplate()'>设为默认表单</button>";
		topSubmitDiv += "</div>";
		
		$("#topSubmitDiv").html(topSubmitDiv);
		$("#topSubmitDiv").show();
	}
	else
	{
		var topSubmitDiv = "";
		topSubmitDiv += "<div class='btn-group'>";
		topSubmitDiv += 	"<button class='btn  btn-danger' id='input_top_submit' onClick='executeSubmit(0)'>提交并查看</button>";
		topSubmitDiv += "</div>";
		
		topSubmitDiv += "<div class='btn-group'>";
		topSubmitDiv += 	"<button class='btn  btn-primary' id='input_top_submit_close' onClick='executeSubmit(1)'>提交并关闭页面</button>";
		topSubmitDiv += "</div>";
		
		topSubmitDiv += "<div class='btn-group'>";
		topSubmitDiv += 	"<button class='btn  btn-primary' id='input_top_cancel' onClick='executeCancel()'>取消</button>";
		topSubmitDiv += "</div>";
		
		$("#topSubmitDiv").html(topSubmitDiv);
		$("#topSubmitDiv").hide();
		
		var actionNodes = $(rootNode).children("actions").children("action");
		$("#select_next_action_top").empty();
		if(actionNodes.length > 0)
		{
			for(var i = 0; i < actionNodes.length; i++)
			{
				var actionId     = $(actionNodes[i]).children("id").text();
				var actionName   = $(actionNodes[i]).children("name").text();
				var beginStatId  = $(actionNodes[i]).children("beginStatId").text();
				var endStatId    = $(actionNodes[i]).children("endStatId").text();
				$("#select_next_action_top").append("<li><a href='#' onclick=executeUpdate('"+actionId+ "|" + endStatId+"')>"+(beginStatId == "" ? "激活--" : "") + actionName+"</a></li>");
			}
		}else{
			$("#actionButton").attr('disabled','disabled');
			$("#actionButton").attr('realValue','没有可操作权限');
		}

		if(isEdit)
		{
			$("#top_edit_li").show();
			$("#top_edit_li").click(function(){
				executeUpdate("'|" + task.statusId + "'");
			});
		}
	}
	
    selectNextActionHtml = $("#select_next_action_top").html();
	
	if(operation == "read")
	{
		drawFieldsArea(rootNode);
		try
		{
			//生成日志信息
			var logHtml = getLogFieldDivHtml(taskNode);
			$("#logContentDiv").html(logHtml);
			$("#logInfoDiv").show();
		}catch(e)
		{}
	}
	
	if(operation == "create" && selectedTemplate != null){
		selectTemplate();
	}
	
	if(/(msie\s|trident.*rv:)([\w.]+)/.test(navigator.userAgent.toLowerCase()))
	{
		$("#input_taskTitle").focus();
	}
	
	$(".ke-container").remove();
	editor = KindEditor.create('textarea[id="input_taskDescription"]', options);
	$(".ke-icon-removeformat").on('click',setDefaultDescription);
	
	if(operation == 'read')
	{
		disableEditor(true);
		if(!isEdit)
		{
			$("#top_edit_li").hide();
		}
		hideToolbar();
		appendFullScreen();
	}
	
	if(operation =='create')
	{
		$('#input_taskTitle')[0].focus();
		$("#top_edit_li").hide();
		showToolbar();
		removeFullScreen();
	}

	if(type!=null&&type=='edit')
	{
		if(isEdit)
			executeUpdate('|'+task.statusId);
		else
			alert('您无法进行编辑');
	}
	
	removeLoading();

	if(operation == 'read'){
		
		//收起 展开
		var showFieldTr = readCookie("fieldTrShow" + task.templateId);
		if(showFieldTr == null || showFieldTr == "true"){
			$("#layoutContentTitle").parent().next().show();
			$(".multipleRef").show();
		}else{
			$("#layoutContentTitle span").each(function(index,node){
				if($(node).text()== "收起")
					$(node).text("展开");
			});
			$("#layoutContentTitle").parent().next().hide();
			$(".multipleRef").hide();
		}

		//是否显示日志
		var isShowLog = readCookie(task.templateId + "showLog");
		if(isShowLog == null)
			isShowLog = "true";
		if(isShowLog == "true"){
			$("#displayLogDiv").find("span").text("隐藏日志");
			$("#logContentDiv").show();
		}
		else{
			$("#displayLogDiv").find("span").text("显示日志");
			$("#logContentDiv").hide();
		}
		//是否显示空日志
		var isCookieHide = readCookie(task.templateId + "hideLogDiv");
		if(isCookieHide == null)
			isCookieHide = "false";

		$("#logContentDiv>table>tbody>tr").each(function(index,node){
			var tdnode = $(node).find("td:eq(2)");
			var tdvalue = jQuery(tdnode).text();
			if(isCookieHide == "false")
			{
				$(tdnode).parent().show();
			}else
			{
				if(tdvalue == "" || tdvalue == "-")
					$(tdnode).parent().hide();
			}
		});

		if(isCookieHide == "true")
			$("#displayEmptyLogDiv").find("span").text("显示全部日志");
		else
			$("#displayEmptyLogDiv").find("span").text("隐藏空日志");
	}
}

function setTitleAndTag(){
	$.ajax({
		url:'tag/getDataTags.do',
		data:{'dataId':task.id},
		dataType:'json',
		success:function(tags){
			for(var i in tags){
				if(!inArray(dataTagArray,tags[i].id))
					dataTagArray.push(tags[i].id);
			}
			changeTitleAndTag();
		}
	});
}

function changeTitleAndTag(){
	
	var titleValue = "标题(" + task.id + "):" + getXMLStr(task.title);
	var html = '';
	var spanLength = getLengthOfTagArr(dataTagArray);
	var allLength = spanLength + getLengthOfStr(task.title);
	var titleLength = 1050-spanLength;
	var spanTd = '';
	for(var i in dataTagArray){
		try
		{
			spanTd += getTagSpan(task.id,tagArray[dataTagArray[i]].id, tagArray[dataTagArray[i]].tagName, tagArray[dataTagArray[i]].tagColor);
		}catch(e){
		}
	}
	if(allLength > 150){
		html += "<div title="+task.title+" style=width:"+titleLength+"px;text-overflow:ellipsis;white-space:nowrap;overflow:hidden;margin-right:"+spanLength+"px;><b>(" + task.id+")</b>&nbsp;:&nbsp;" + getXMLStr(task.title) + "&nbsp;</div>";
		html += '<div style="top:7px;right:70px;position: absolute;">';
		html += spanTd;
		html += '</div>';
	}else{
		html += "<b>(" + task.id+")</b>&nbsp;:&nbsp;" + getXMLStr(task.title) + "&nbsp;";
		html += spanTd;
	}
	
	$("#titleSpan").html(html);
	$("#titleSpan").attr("value",titleValue);
}

function getLengthOfTagArr(dataTagArray)
{
	var length = 0;
	for(var i in dataTagArray){	
		if( i != 0)
			length += 20;//span之间间隔
		var tagTmp = tagArray[dataTagArray[i]].tagName;
		length += (getLengthOfStr(tagTmp) *7);
	}
	return length;
}

function getTagSpan(dataId,tagId,tagName,tagColor)
{
	var tagHtml = "";
	tagHtml += '<span class="label titleTag" style=" margin-right:2px;background-color:'+ tagColor +'" tagId='+tagId+'>';
	tagHtml += '<span class="label closeTag" dataId = '+dataId+' tagId='+tagId+' style="display:none; float:right; padding: 0px 2px; margin-left:2px;cursor:pointer; background-color:'+ tagColor +'"><b title="移出标签">x</b></span>';
	tagHtml += tagName;
	tagHtml += '</span>';
	return tagHtml;
}

function addActionName(actionName)
{
	$("#next_action_name").text(actionName);
}

function removeActionName()
{
	$("#next_action_name").text("");
}

function disableEditor(disable)
{
	if(disable)
	{
		editor.readonly(true);
		$("#descriptionKind span[data-name='fullscreen']").attr("class","ke-outline");
	}else
	{
		editor.readonly(false);
	}
}

//////////修改操作日志开始//////////////
function editLog(tagA){
	if($(tagA).attr('disabled') == 'disabled'){
		$(tagA).parent().parent().find("td:eq(2)").find("textarea")[0].focus();
		return;
	}
	else
		$(tagA).attr('disabled','disabled');
	var tdDiv = $(tagA).parent().parent().find("td:eq(2)");
	var curText = tdDiv.html();
	if(curText.indexOf("-") == 0)
		curText = curText.substring(1);
	tdDiv.html("<textarea beforeText=\"" +curText+ "\" class=\"span18\" style=\"width:95%;height:100px;\">" +replaceAll(curText, "<br>", "\r\n") + "</textarea><p>" 
			+"<input type=\"button\" class=\"btn btn-mini btn-danger\" onclick=\"saveLog(this);\" value=\"保存\"/>"
			+"<input type=\"button\" class=\"btn btn-mini\" onclick=\"cancelLogEdit(this);\" value=\"取消\"/></p>");
	$(tagA).parent().parent().find("td:eq(2)").find("textarea")[0].focus();
}

function cancelLogEdit(tagA)
{
	$(tagA).parent().parent().next().find("i").removeAttr('disabled');
	var tdDiv = $(tagA).parent().parent().find("textarea:eq(0)");
	var beforeText = tdDiv.attr("beforeText");
	beforeText = beforeText == "" ? "-" : beforeText;
	$(tdDiv).parent().html(beforeText);
}

function saveLog(tagA)
{
	$(tagA).parent().parent().next().find("i").removeAttr('disabled');
	var tdDiv = $(tagA).parent().parent();
	var curLogText = tdDiv.find("textarea:eq(0)").val();
	if(curLogText == "")
		curLogText = "-";
	var beforeText = tdDiv.find("textarea:eq(0)").attr('beforeText');
	//发往后台保存
	var taskId = task.id;
	var logIndex = tdDiv.parent().find("td:eq(0)").text();
	
	$.ajax({
		url:'data/updateLog.do',
		type:'POST',
		data:{'dataId':taskId,'logIndex':logIndex,'logComment':curLogText},
		success:function(data){
			if(data == 'true'){
				if(curLogText != "")
					curLogText = replaceAll(getXMLStr(curLogText), "\n", "<br>");
				else
					curLogText = '-';
				tdDiv.html(curLogText);
			}
			else{
				showInfoWin('error','服务器错误,修改失败!');
				tdDiv.html(beforeText);
			}
		}
	});
}
////////////修改操作日志结束/////////////////

function getLogFieldDivHtml(taskNode)
{
	var logTable = "<table width=\"100%\" style='margin-left:0px;' cellspacing='5' cellpadding=4>";
	logTable += "<tr>";
	logTable += "<td align=\"center\" width=\"6%\"><b>动作序号</b></td>";
	logTable += "<td align=\"center\" width=\"15%\"><b>动作名称</b></td>";
	logTable += "<td align=\"center\" width=\"50%\"><b>执行描述</b></td>";
	logTable += "<td align=\"center\" width=\"2%\"></td>";
	logTable += "<td align=\"center\" width=\"12%\"><b>执行人员</b></td>";
	logTable += "<td align=\"center\" width=\"20%\"><b>执行时间</b></td>";
	logTable += "</tr>";

	var logNodes = $(taskNode).children("logs").children("log");

	for(var i = 0; i < logNodes.length; i++)
	{
		var actionId = logNodes.length - i;

		var actionName = $(logNodes[logNodes.length -1 - i]).children("actionName").text();
		if(actionName == "")
		{
			if(i == logNodes.length - 1)
				actionName = "新建";
			else
				actionName = "编辑";
		}

		var actionComment = $(logNodes[logNodes.length -1 - i]).children("actionComment").text();
		var actionTime = formatDateStr($(logNodes[logNodes.length -1 - i]).children("createTime").text());
		var actionUser = $(logNodes[logNodes.length -1 - i]).children("createUser").text();
		var logCreateUserMail = $(logNodes[logNodes.length -1 - i]).children("createUserMail").text(); //日志创建人员邮箱
		
		logTable += "<tr style='border-top:1px solid #eeeeee;'>";
		logTable += "<td align=\"center\"><button type='button' class='btn btn-mini' onClick=\"displayExtFieldArea('" + (logNodes.length - 1 - i) + "')\">"+actionId+"</button></td>";
		logTable += "<td align=\"center\">" + getXMLStr(actionName) + "</td>";
		logTable += "<td align=\"left\">" + (actionComment != "" ? replaceAll(getXMLStr(actionComment), "\n", "<br>") : "-") + "</td>";
		//日志创建人员自身有编辑权限
		logTable += "<td align=\"center\">" + (logCreateUserMail == readCookie('login_username') ? "<i class=\"icon-edit\" title=\"编辑\" style=\"cursor:pointer\" onclick=\"editLog(this);\">" : "") + "</td>";
		logTable += "<td align=\"center\">" + actionUser + "</td>";
		logTable += "<td align=\"center\">" + actionTime + "</td>";
		logTable += "</tr>";

		logTable += "<tr id=\"extFieldArea" + (logNodes.length - 1 - i) + "\" style=\"display:none\">";
		logTable += "<td colspan=\"6\">";
		logTable += "<table width=\"95%\" cellpadding=0 cellspacing=0 style='table-layout:fixed; margin-left:25px;border:1px solid #B1CAE9;'>";

		var baseValueTable = "";
		baseValueTable += "<tr>";
		baseValueTable += "<td class='log_field_name' bgcolor=\"#D9E6F0\">基本信息</td>";
		baseValueTable += "<td class='log_field_content' bgcolor=\"#D9E6F0\">修改之前</td>";
		baseValueTable += "<td class='log_field_content' bgcolor=\"#D9E6F0\">修改之后</td>";
		baseValueTable += "</tr>";

		var baseValueAccount = 0;

		var baseValueNodes = $(logNodes[logNodes.length -1 - i]).children("baseValues").children("baseValue");
		for(var j = 0; j < baseValueNodes.length; j++)
		{
			var base     = $(baseValueNodes[j]).children("base").text();
			var previous = $(baseValueNodes[j]).children("previous").text();
			var current  = $(baseValueNodes[j]).children("current").text();
			if(base=='描述')
			{
				baseValueTable += "<tr>";
				baseValueTable += "<td>" + base + "</td>";
				baseValueTable += "<td class='log_field_content'>" + (previous.length > 0 ? previous : "-") + "</td>";
				baseValueTable += "<td class='log_field_content'>" + (current.length > 0 ? current : "-") + "</td>";
				baseValueTable += "</tr>";
			}else
			{
				baseValueTable += "<tr>";
				baseValueTable += "<td>" + getXMLStr(base) + "</td>";
				baseValueTable += "<td class='log_field_content'>" + (previous.length > 0 ? getXMLStr(previous) : "-") + "</td>";
				baseValueTable += "<td class='log_field_content'>" + (current.length > 0 ? getXMLStr(current) : "-") + "</td>";
				baseValueTable += "</tr>";
			}
			baseValueAccount++;
		}

		if(baseValueAccount > 0)
			logTable += baseValueTable;

		var extValueTable = "";
		extValueTable += "<tr>";
		extValueTable += "<td class='log_field_name' bgcolor=\"#D9E6F0\">字段信息</td>";
		extValueTable += "<td class='log_field_content' bgcolor=\"#D9E6F0\">修改之前</td>";
		extValueTable += "<td class='log_field_content' bgcolor=\"#D9E6F0\">修改之后</td>";
		extValueTable += "</tr>";

		var extValueAccount = 0;

		var extValueNodes = $(logNodes[logNodes.length -1 - i]).children("extValues").children("extValue");
		for(var j = 0; j < extValueNodes.length; j++)
		{
			var ext      = $(extValueNodes[j]).children("ext").text();
			var previous = $(extValueNodes[j]).children("previous").text();
			var current  = $(extValueNodes[j]).children("current").text();

			extValueTable += "<tr>";
			extValueTable += "<td>" + getXMLStr(ext) + "</td>";
			extValueTable += "<td calss='log_field_content'>" + (previous.length > 0 ? replaceAll(getXMLStr(previous), "VALUE_SPLIT", "<br>") : "-") + "</td>";
			extValueTable += "<td calss='log_field_content'>" + (current.length > 0 ? replaceAll(getXMLStr(current), "VALUE_SPLIT", "<br>") : "-") + "</td>";
			extValueTable += "</tr>";

			extValueAccount++;
		}
		if(extValueAccount > 0)
			logTable += extValueTable;
		logTable += "</table>";
		logTable += "</td>";
		logTable += "</tr>";
	}
	logTable += "</table>";
	return logTable;
}

function getBaseFieldFormHtml(task)
{
	var baseFieldForm = "";
	//title
	if(operation == "read")
	{
		baseFieldForm += "<div class='control-group'>";
		baseFieldForm += 	"<label class='control-label span2' for='input_taskTitle'>标题("+task.id+"):</label>";
		baseFieldForm +=   "<div class='controls'>";
		baseFieldForm +=   	"<input type='text' id='input_taskTitle' placeholder='标题' class='span10 onlyRead' style='text-overflow:ellipsis; white-space:nowrap; overflow:hidden;' value='"+getXMLStr(task.title)+"' readOnly/>";
		baseFieldForm +=    "</div>";
		baseFieldForm +=  "</div>";
	}
	else
	{
		baseFieldForm += "<div class='control-group'>";
		baseFieldForm += 	"<label class='control-label span2' for='input_taskTitle'>标题:</label>";
		baseFieldForm +=   "<div class='controls'>";
		baseFieldForm +=   	"<input type='text' id='input_taskTitle' placeholder='标题' class='span10' value='"+getXMLStr(task.title)+"'/>";
		baseFieldForm +=    "</div>";
		baseFieldForm +=  "</div>";
	}
	
	//项目
	baseFieldForm += "<div class='control-group'>";
	baseFieldForm += 	"<label class='control-label span2' for='input_taskTemplateName'>项目:</label>";
	baseFieldForm +=   "<div class='controls'>";
	baseFieldForm +=   	"<input type='text' id='input_taskTemplateName' placeholder='项目' class='span10 onlyRead' value='"+getXMLStr(task.templateName)+"' readOnly/>";
	baseFieldForm +=    "</div>";
	baseFieldForm +=  "</div>";
	
	//创建人员
	baseFieldForm += "<div class='control-group'>";
	baseFieldForm += 	"<label class='control-label span2' for='input_taskCreateUser'>创建人员:</label>";
	baseFieldForm +=   "<div class='controls'>";
	baseFieldForm +=   	"<input type='text' id='input_taskCreateUser' placeholder='创建人员' class='span10 onlyRead' value='"+(task.createUserAlias ? task.createUserAlias : task.createUser) +"' readOnly/>";
	baseFieldForm +=    "</div>";
	baseFieldForm +=  "</div>";
	
	//创建时间
	baseFieldForm += "<div class='control-group'>";
	baseFieldForm += 	"<label class='control-label span2' for='input_taskCreateTime'>创建时间:</label>";
	baseFieldForm +=   "<div class='controls'>";
	baseFieldForm +=   	"<input type='text' id='input_taskCreateTime' class='span10 onlyRead' value='"+task.createTime +"' readOnly/>";
	baseFieldForm +=    "</div>";
	baseFieldForm +=  "</div>";
	
	//修改时间
	baseFieldForm += "<div class='control-group'>";
	baseFieldForm += 	"<label class='control-label span2' for='input_taskLastModifyTime'>修改时间:</label>";
	baseFieldForm +=   "<div class='controls'>";
	baseFieldForm +=   	"<input type='text' id='input_taskLastModifyTime' class='span10 onlyRead' value='"+task.lastModifyTime +"' readOnly/>";
	baseFieldForm +=    "</div>";
	baseFieldForm +=  "</div>";
	//状态
	baseFieldForm += "<div class='control-group'>";
	baseFieldForm += 	"<label class='control-label span2' for='input_taskStatus'>状态:</label>";
	baseFieldForm +=   "<div class='controls'>";
	baseFieldForm +=   	"<input type='text' id='input_taskStatus' class='span6 onlyRead' value='"+task.statusName +"' readOnly/><span class='label label-success' style='cursor:pointer' onclick='showInFlow(" + task.flowId + "," + task.statusId + ")'>查看流程</span>";
	baseFieldForm +=    "</div>";
	baseFieldForm +=  "</div>";
	
	//当前指派
	baseFieldForm += "<div class='control-group'>";
	baseFieldForm += 	"<label class='control-label span2' for='input_taskAssignUser'>当前指派:</label>";
	baseFieldForm +=   "<div class='controls'>";
	baseFieldForm +=   	"<input type='text' id='input_taskAssignUser' class='span10 onlyRead' value='"+(task.assignUserAlias ? task.assignUserAlias : task.assignUser)  +"' readOnly/>";
	baseFieldForm +=    "</div>";
	baseFieldForm +=  "</div>";
	
	return baseFieldForm;
}

//查看流程
function showInFlow(flowId,statusId)
{
	var url = base_url + 'admin/admin_flow_edit.html?flowId=' + flowId + '&statusId=' + statusId + '&editable=false&type=previewFlow';
	$('#flowIframe').attr('src',url);
	$('#flowReadDiv').modal('show');
}

function setDefaultAction()
{
	var templateId = $("#select_template").val();
	var action = $("#select_action").val();
	if(action == "")
		deleteCookie("action" + templateId);
	else
		createCookie("action" + templateId + "=" + action.split("|")[0]);
	showInfoWin('success','默认动作设置成功!');
}

function setDefaultTemplate()
{
	var templateId = $("#select_template").val();
	if(templateId == "")
		deleteCookie("template");
	else
		createCookie("template=" + templateId);
	showInfoWin('success','默认表单设置成功!');
}

function setDefaultAssignUserRead()
{
	var actionId = $("#select_action").val().split("|")[0];
	var templateId = task.templateId;
	var assignUser = $("#select_taskAssignUser").val();

	if(assignUser == "")
		deleteCookie("au" + templateId + actionId);
	else
		createCookie("au" + templateId + actionId + "=" + assignUser);

	showInfoWin('success','默认指派人设置成功!');
}

function setDefaultAssignUser()
{
	var templateId = $("#select_template").val();
	var actionId = $("#select_action").val().split("|")[0];

	var assignUser = $("#select_taskAssignUser").val();
	if(assignUser == "")
		deleteCookie("au" + templateId + actionId);
	else
		createCookie("au" + templateId + actionId + "=" + assignUser);

	showInfoWin('success','默认指派人设置成功!');
}

function setDefaultDescription()
{
	var description = escape(editor.html());
	var templateId = $("#select_template").val();
	allDefaultValueMap.put(templateId+"description" ,description);
	updateDefaultValue();
}

function executeUpdate_select(selectId)
{
	var selectValue = $("#"+selectId).val();
	if(selectValue != "")
	{
		executeUpdate(selectValue);
	}
}

function executeUpdate(actionValue)
{
	dealWithButton(true);
	window.focus();
	showToolbar();
	removeFullScreen();
	for(var i = 0; i < $("#select_action")[0].options.length; i++)
	{
		if($("#select_action")[0].options[i].value == actionValue)
		{
			$("#select_action")[0].options[i].selected = true;
			nextActionName = "(" + getTextContent($("#select_action")[0].options[i]) + ")";
			$("#input_action").val(getTextContent($("#select_action")[0].options[i]));
			break;
		}
	}

	$("#select_taskAssignUser")[0].options.length = 0;
	$("#select_taskAssignUser")[0].options[0] = new Option("--请选择--", "");
	
	if(isProTemplate)
	{
		var field = getFieldById(projectInvolvedId);
		if(field){
			var productId = field.datas[0];
			var actionId = actionValue.split('|')[0];
			var action = getActionById(actionId);
			if(action && action.isEndAction == 'true'){
				$('#select_taskAssignUser').attr("disabled","disabled");
			}else{
				$('#select_taskAssignUser').removeAttr("disabled");
			}
			setAssignUserByProjectIdAndRoles(productId,actionId);
		}
	}else{
		var params = "taskId=" + getSafeParam(task.id);
		params += "&statId=" + getSafeParam($("#select_action").val().split("|")[1]);
		params += "&actionId=" + getSafeParam($("#select_action").val().split("|")[0]);
		$.ajax({
			url : 'task/initTaskAssignUsers.jsp',
			type : 'post',
			data : params,
			dataType : 'xml',
			success : onCompleteInitTaskAssignUsers
		});
		
		$("#input_taskTitle").removeAttr("readonly").removeClass("onlyRead");
		$("#actionForm").show();
	}
}

function executeCancel()
{
	window.location.reload();
}

var g_initUploadFile = false;
function displayUploadFile(fieldId)
{
	if(!g_initUploadFile){
		g_fileUploadHandler.init();
		g_initUploadFile = true;
	}
	$("#uploadFileDiv input[type=file]").val("");
	$("#objId").val(fieldId);
	$("#uploadFileDiv").modal('show');
	return false;
}

function executeCancelUploadFile()
{
	$('#fileInput').fileUploadClearQueue();
	$("#uploadFileDiv").modal('hide');
}

function removeFile(fieldId)
{
	var fileReference = document.getElementById("field" + fieldId);
	if(fileReference.selectedIndex < 0)
	{
		return;
	}

	for(var i = 0; i < fileReference.options.length; i++)
	{
		if(fileReference.options[i].selected)
		{
			fileReference.remove(i);
			i--;
		}
	}

	var field = getFieldById(fieldId);
	if(field != null)
	{
		field.datas.length = 0;
		for(var i = 0; i < fileReference.options.length; i++)
		{
			field.datas[field.datas.length] = fileReference.options[i].value + "&|;" + getTextContent(fileReference.options[i]);
		}
	}

}

function readTask(fieldId)
{
	var fieldSelect = $("#field" + fieldId)[0];

	if(fieldSelect.selectedIndex < 0)
	{
		return;
	}
	for(var i = 0; i < fieldSelect.options.length; i++)
	{
		if(fieldSelect.options[i].selected)
		{
			window.open(getRootDir() + "taskManagement.html?operation=read&taskid=" + getSafeParam(fieldSelect.options[i].value));
		}
	}
}

function displayQueryTaskPage(fieldId)
{
	var field = getFieldById(fieldId);
	if(field.dataType == "single" && $("#field" + fieldId)[0].options.length == 1)
	{
		alert("该字段只允许添加1个条目！");
		return;
	}

	var alreadyIds = "";
	for(var i = 0; i < $("#field" + fieldId)[0].options.length; i++)
	{
		if(i > 0)
			alreadyIds += ",";
		alreadyIds += getSafeParam($("#field" + fieldId).val());
	}

	$("#fieldId").val(getSafeParam(fieldId));
	$("#dataType").val(field.dataType);
	$("#alreadyIds").val(alreadyIds);
	
	var filterId = field.defaultValues[0];
	
	$.ajax({
		url: base_url + 'template/getAllTemplates.do',
		dataType:'json',
		success: function(data){
			var $templateId = $('#searchTemplateId');
			$templateId.empty();
			for(var id in data){
				$templateId.append('<option value=' + id +  (id == selectedTemplate ? ' selected' : '' ) + '>' + data[id] + '</option>');
			}
			
			if(filterId){
				$.ajax({
					url: base_url + 'filter/getFilterXml.do',
					type :'POST',
					data: {'filterId':filterId},
					success: function(filterXml){
						filterXml = encodeAllUrl(filterXml);
						queryFilterData(filterXml,true);
						$("#cfgRefQueryDiv").modal('show');
					}
				});
			}else{
				queryFilterData('',true);
				$("#cfgRefQueryDiv").modal('show');
			}
		}
	});
}

function executeAddReference(rev, objId)
{
	var selObj = $("#" +objId)[0];
	var field = null;
	var dataType = "";
	
	for(var i =0; i< rows.length; i++)
	{
		var rowColumns = rows[i];
		for(var j = 0; j < rowColumns.length; j++)
		{
			var columnFields = rowColumns[j];
			for(var k = 0; k < columnFields.length; k++)
			{
				if(columnFields[k].id != null && "field" + parseInt(columnFields[k].id) == objId)
				{
					dataType = columnFields[k].dataType;
					field = columnFields[k];
					break;
				}
			}
		}
	}
	

	for( var i = 0; i < rev.length; i++ )
	{
		var contained = false;
	
		for(var j = 0; j < selObj.options.length; j++)
		{
			if(selObj.options[j].value == rev[i].id)
			{
				contained = true;
				break;
			}
		}

		if(!contained)
		{
			if(dataType == "single")
			{
				selObj.options.length = 0;
			}
			selObj.options[selObj.options.length] = new Option(rev[i].title, rev[i].id);
			if(rev[i].user != null)
			{
				for(var j = 0; j < $("#select_taskAssignUser")[0].options.length; j++)
				{
					if($("#select_taskAssignUser")[0].options[j].value == rev[i].user)
					{
						$("#select_taskAssignUser")[0].options[j].selected = true;
						break;
					}
				}
			}
		}
	}

	if(field != null)
	{
		field.datas.length = 0;
		for(var i = 0 ;i<selObj.options.length; i++)
		{
			field.datas[field.datas.length] =  selObj.options[i].value + "&|;" + getTextContent(selObj.options[i]);
		}
	}
}

function removeTask(fieldId)
{
	var taskReference = $("#field" + fieldId)[0];
	
	if(taskReference.selectedIndex < 0)
	{
		return;
	}

	for(var i = 0; i < taskReference.options.length; i++)
	{
		if(taskReference.options[i].selected)
		{
			taskReference.remove(i);
			i--;
		}
	}
	var field = getFieldById(fieldId);

	if(field != null)
	{
		field.datas.length = 0;
		for(var i = 0 ;i<taskReference.options.length; i++)
		{
			field.datas[field.datas.length] =  taskReference.options[i].value + "&|;" + getTextContent(taskReference.options[i]);
		}
	}
}

function getFieldInitValues(field)
{
	if(field.datas == null || field.datas.length == 0 || field.datas[0] == '0')
	{
		if(field.type == "attachment")
			return new Array();

		var controlFieldValue = null;
		if(field.controlFieldId != "")
		{
			var controlFieldSelect = $("#field" + field.controlFieldId)[0];
			if(controlFieldSelect != null)
				controlFieldValue = controlFieldSelect.options[controlFieldSelect.selectedIndex].value;
			else
			{
				var controlFieldInitValues = getFieldInitValues(getFieldById(field.controlFieldId));
				if(controlFieldInitValues != null && controlFieldInitValues.length > 0)
					controlFieldValue = controlFieldInitValues[0];
			}
		}

		if(field.type == "input")
		{
			var cookieValue = readCookie("field" + field.id);

			if(controlFieldValue != null && controlFieldValue.length > 0)
				cookieValue = readCookie("field" + field.id + "|" + controlFieldValue);

			if(cookieValue != null)
			{
				var cookieValues = new Array();
				cookieValues[0] = cookieValue;

				return cookieValues;
			}

			return field.defaultValues;
		}

		if(field.type == "selection")
		{
			var fieldInitValues = field.defaultValues;
			
			if(controlFieldValue != null && controlFieldValue.length > 0)
				defaultValues = allDefaultValueMap.get(field.id + "|" + controlFieldValue);
			else
				defaultValues = allDefaultValueMap.get(field.id);
			
			defaultValues = defaultValues || fieldInitValues;
			var newFieldInitValues = new Array();

			if(defaultValues != null && defaultValues != ""){
				fieldInitValues = defaultValues;
				if(fieldInitValues.indexOf("|") != -1) {
					fieldInitValues = fieldInitValues.split("|");
				}
				
				if(!(fieldInitValues instanceof Array)) {
					
					fieldInitValues = [fieldInitValues];
				}
				
				for(var i = 0; i < fieldInitValues.length; i++)
				{
					var contain = false;

					for(var j = 0; j < field.options.length; j++)
					{
						if(field.options[j].id == fieldInitValues[i])
						{
							contain = true;
							break;
						}
					}

					if(contain)
						newFieldInitValues[newFieldInitValues.length] = fieldInitValues[i];
				}
			}
			return newFieldInitValues;
		}

		if(field.type == "reference")
		{
			var newFieldInitValues = new Array();
			
			var defaultValues = allDefaultValueMap.get(field.id);
			if(defaultValues != null && defaultValues != ""){
				var fieldInitValues = defaultValues.split("&|;");
				for(var i = 0; i < fieldInitValues.length; i += 2)
					newFieldInitValues[newFieldInitValues.length] = fieldInitValues[i] + "&|;" + fieldInitValues[i + 1];

			}
			return newFieldInitValues;
		}
	}

	return field.datas;
}

function getFieldByName(fieldName)
{
	for(var i = 0; i < rows.length; i++)
	{
		var columns = rows[i];
		for(var j = 0; j < columns.length; j++)
		{
			var columnFields = columns[j];
			for(var k = 0; k < columnFields.length ; k++ )
			{
				var columnField = columnFields[k];
				if(columnField.name != null && columnField.name == fieldName)
				{
					return columnField;
				}
			}
		}
	}
	
	return null;
}

function getFieldById(fieldId)
{
	for(var i = 0; i < rows.length; i++)
	{
		var columns = rows[i];
		for(var j = 0; j < columns.length; j++)
		{
			var columnFields = columns[j];
			for(var k = 0; k < columnFields.length ; k++ )
			{
				var columnField = columnFields[k];
				if(columnField.id != null && columnField.id == fieldId)
				{
					return columnField;
				}
			}
		}
	}
	
	return null;
}

function setSubmitDivDisable(disable){
	if(disable)
	{
		$("#topSubmitDiv button").attr('disabled', disable);  
		showLoading(true);
	}	
	else
	{
		$("#topSubmitDiv button").removeAttr('disabled');
		showLoading(false);
		return;
	}
}

function showLoading(isShow)
{
	if(isShow){
		if($("#layout").length == 0){
			var info="<div id=\"layout\" style=\"display: none;position: absolute;top:40%;left: 40%;width: 20%;height: 20%;z-index: 999999;\"><img src=\"images/refresh.gif\"/></div>";
			$("body").append(info);
		}
		$('#layout').fadeIn("fast");
	}else{
		$('#layout').fadeOut("fast");
	}
}

//TODO
function executeSubmit(closeWindow)
{
	setSubmitDivDisable(true);
	
	var title = trim($("#input_taskTitle").val());
	if(title == "")
	{
		alert("标题不能为空！");
		return setSubmitDivDisable(false);
	}

	var description = editor.html();

	var params = "";

	if(operation == "create")
	{
		var templateId = $("#select_template").val();
		if(templateId == "")
		{
			alert("请选择项目名称！如果没有可以选择的项目，请先建立项目！");
			return setSubmitDivDisable(false);
		}

		var selectActionId = $("#select_action").val();
		if(selectActionId == "")
		{
			alert("请选择动作名称！如果没有可以选择的动作，表明您在当前项目当前状态下没有执行权限！");
			return setSubmitDivDisable(false);
		}

		var assignUser = $("#select_taskAssignUser").val();
		
		if((assignUser == "" || assignUser == "--请选择--")  && $("#select_taskAssignUser")[0].options.length > 1){
			alert("请选择指派人！");
			return setSubmitDivDisable(false);
		}

		params += "title=" + getSafeParam(title);

		if(assignUser != ""){
			params += "&assignUser=" + getSafeParam(assignUser);
		}

		if(description != "")
			params += "&description=" + getSafeParam(description);

		params += "&assignUser=" + assignUser;
		params += "&templateId=" + getSafeParam(templateId);
		params += "&actionId=" + getSafeParam(selectActionId.split("|")[0]);
		params += "&statusId=" + getSafeParam(selectActionId.split("|")[1]);
	}
	else if(operation == "modify")
	{
		var selectActionId = $("#select_action").val();

		var assignUser = $("#select_taskAssignUser").val();
		if(needAssignUser && assignUser == "")
		{
			alert("请选择指派人！");
			return setSubmitDivDisable(false);
		}

		var actionId = selectActionId.split("|")[0];

		var actionComment = trim($("#input_actionDescription").val());

		params += "id=" + getSafeParam(task.id);
		params += "&title=" + getSafeParam(title);

		if(description != "")
			params += "&description=" + getSafeParam(description);

		params += "&templateId=" + getSafeParam(task.templateId);

		if(assignUser != "")
			params += "&assignUser=" + assignUser;

		if(actionId != "")
			params += "&actionId=" + getSafeParam(actionId);

		params += "&statusId=" + getSafeParam(selectActionId.split("|")[1]);

		if(actionComment != "")
			params += "&actionComment=" + getSafeParam(actionComment);
	}
	
	for(var i = 0; i < rows.length; i++)
	{
		var rowColumns = rows[i];
		for(var j = 0; j < rowColumns.length; j ++)
		{
			var columnFields = rowColumns[j];
			for(var k = 0; k < columnFields.length; k++)
			{
				var tempField = columnFields[k];
				if(tempField.id == null || $("#field" + tempField.id).length == 0){
					if(tempField.controlFieldId != ""){
						params += "&field" + getSafeParam(tempField.id) + "=";
					}
					continue;
				}

				params += "&field" + getSafeParam(tempField.id) + "=";

				//判断是否必填
				var must = getFieldMust(tempField);

				var fieldObj = $("#field" + tempField.id)[0];

				if(tempField.type == "selection")
				{
					if(must == 1&&tempField.controlFieldId != ""&&fieldObj.options.length<=1)
					{
						//不做处理
					}else if(must == 1 && fieldObj.selectedIndex == 0&&fieldObj.options[0].value == "")
					{
						alert("请选择" + tempField.name + "！");
						return setSubmitDivDisable(false);
					}

					for(var m = 0; m < fieldObj.options.length; m++)
					{
						if(fieldObj.options[m].selected)
						{
							params += (params.substring(params.length - 1) == "=" ? "" : ",");
							params += getSafeParam(fieldObj.options[m].value);
						}
					}
				}
				else if(tempField.type == "reference")
				{
					if(must == 1 && fieldObj.options.length == 0)
					{
						alert("请添加" + tempField.name + "！");
						return setSubmitDivDisable(false);
					}
					
					for(var m = 0; m < fieldObj.options.length; m++)
					{
						params += (m == 0 ? "" : ",") + getSafeParam(fieldObj.options[m].value);
					}
				}
				else if(tempField.type == "attachment")
				{
					if(must == 1 && fieldObj.options.length == 0)
					{
						alert("请添加" + tempField.name + "！");
						return setSubmitDivDisable(false);
					}

					for(var m = 0; m < fieldObj.options.length; m++)
					{
						params += (m == 0 ? "" : ",") + getSafeParam(fieldObj.options[m].value);
					}
				}
				else if(tempField.type == "input")
				{
					var inputValue = "";

					if(tempField.dataType == "timestamp")
					{
						
						inputValue = trim(fieldObj.value);
						if(must == 1 && inputValue == ""){
							alert("请填写" + tempField.name + "！");
							return setSubmitDivDisable(false);
						}
					}

					else
					{
						inputValue = trim(fieldObj.value);

						if(must == 1 && inputValue == "")
						{
							alert("请填写" + tempField.name + "！");
							return setSubmitDivDisable(false);
						}

						if(inputValue != "")
						{
							if(tempField.dataType == "integer" || tempField.dataType == "long")
							{
								if(parseInt(inputValue).toString() == "NaN" || parseInt(inputValue) < 0)
								{
									alert(tempField.name + "必须是非负整数！");
									return setSubmitDivDisable(false);
								}

								inputValue = parseInt(inputValue);
							}
							else if(tempField.dataType == "float" || tempField.dataType == "double")
							{
								if(parseFloat(inputValue).toString() == "NaN" || parseFloat(inputValue) < 0)
								{
									alert(tempField.name + "必须是非负数！");
									return setSubmitDivDisable(false);
								}

								inputValue = parseFloat(inputValue);
							}
						}
					}
					params += getSafeParam(inputValue);
				}	
			}
		}
	}
	
	if(operation == "create")
	{
		if(closeWindow == 0)
		{
			$.ajax({
				url : 'task/addTask.jsp',
				type : 'post',
				dataType : 'xml',
				data : params,
				success : onCompleteAddTaskRead
			});
		}
		else if(closeWindow == 1)
		{
			$.ajax({
				url : 'task/addTask.jsp',
				type : 'post',
				dataType : 'xml',
				data : params,
				success : onCompleteAddTaskClose
			});
		}
		else if(closeWindow == 2)
		{
			selectedTemplate = $("#select_template").val();
			$.ajax({
				url : 'task/addTask.jsp',
				type : 'post',
				dataType : 'xml',
				data : params,
				success : onCompleteAddTaskNew
			});
		}
		
	}
	else
	{
		if(closeWindow == 0)
		{
			$.ajax({
				url : 'task/modifyTask.jsp',
				type : 'post',
				dataType : 'xml',
				data : params,
				success : onCompleteModifyTaskRead
			});
		}
		else if(closeWindow == 1)
		{
			$.ajax({
				url : 'task/modifyTask.jsp',
				type : 'post',
				dataType : 'xml',
				data : params,
				success : onCompleteModifyTaskClose
			});
		}
	}
}

function dealWithButton(disabled)
{

	if($("#input_top_edit")[0] != null)
	{
		$("#input_top_edit")[0].disabled = disabled;
	}

	if($("#input_top_send_mail")[0] != null)
	{
		$("#input_top_send_mail")[0].disabled = disabled;
	}

	if($("#input_top_create")[0] != null)
	{
		$("#input_top_create")[0].disabled = disabled;
	}

	if($("#input_top_submit")[0] != null)
	{
		$("#input_top_submit")[0].disabled = disabled;
		$("#input_top_submit_close")[0].disabled = disabled;
	}

	if($("#input_top_submit_new")[0]!=null)
	{
		$("#input_top_submit_new")[0].disabled = disabled;
	}

}

/**
 * 字段是否必填
 * @param field
 * @returns
 */
function getFieldMust(field)
{
	if(field.controlFieldId != "")
	{
		return getFieldMust(getFieldById(field.controlFieldId));
	}

	//项目管理表单 对应产品 与对应项目是必填项！！
	if(isProTemplate && ( field.id == productInvolvedId || field.id == projectInvolvedId )){
		return true;
	}
	
	var selectActionId = $("#select_action").val().split("|")[0];
	if(selectActionId == "")
	{
		for(var i = 0; i < field.controlRoleIds.length; i++)
		{
			var role = getRoleById(field.controlRoleIds[i].split("_")[0]);
			if(role != null)
			{
				if(field.controlRoleIds[i].split("_").length > 1 && field.controlRoleIds[i].split("_")[1] == 1)
				{
					return 1;
				}
			}
		}
	}
	else
	{
		for(var i = 0; i < field.controlActionIds.length; i++)
		{
			if(field.controlActionIds[i].split("_")[0] == selectActionId)
			{
				var role = getRoleById(field.controlActionIds[i].split("_")[1]);
				if(role != null)
				{
					if(field.controlActionIds[i].split("_")[2] == 1)
					{
						return 1;
					}
				}
			}
		}
	}

	return 0;
}

function getRoleById(roleId)
{
	for(var i = 0; i < roles.length; i++)
	{
		if(roles[i].id == roleId)
		{
			return roles[i];
		}
	}
	return null;
}

function onCompleteAddTaskClose(request)
{
	$("#topSubmitDiv").hide();
	var result = onCompleteAddTask(request);
	if(result.isError)
	{
		alert(result.errorMsg);
		dealWithButton(false);
		return;
	}else{
		refreshFilter();
	}
	setSubmitDivDisable(false);
	window.close();
}

function onCompleteAddTaskRead(request)
{
	var result = onCompleteAddTask(request);
	if(result.isError)
	{
		alert(result.errorMsg);
		dealWithButton(false);
	}
	else
	{
		initTaskManagement("read", null, result.taskId, filterId, "no", null, gridIndex, url);
		refreshFilter();
	}
	setSubmitDivDisable(false);
}

function onCompleteAddTaskNew(request)
{
	var result = onCompleteAddTask(request);
	if(result.isError)
	{
		alert(result.errorMsg);
		dealWithButton(false);
	}
	else
	{
		initTaskManagement("create", templateTypeId, null, filterId, "no", selectedTemplate, gridIndex, url);
		refreshFilter();
	}
	setSubmitDivDisable(false);
}

function onCompleteAddTask(request)
{
	var responseXML = request;

	var rootNode = $(responseXML).children("root");

	var isErrorNode = $(rootNode).children("isError");
	eval("var isError = " + $(isErrorNode).text());

	var result = new Object();
	result.isError = isError;

	if(isError)
	{
		var errorMsg = "操作失败，原因如下:";
		var errorInfoNode = $(rootNode).children("errorInfo");
		if(errorInfoNode){
			errorMsg += "\r\n" + errorInfoNode.text();
		}
		result.errorMsg = errorMsg;
	}
	else
	{
		result.taskId = $(rootNode).children("taskId").text();
		result.taskName = $(rootNode).children("taskName").text();
	}

	return result;
}

function refreshFilter(){
	//刷新过滤器
	try{
		if(window.opener&&window.opener.grid){
			//同步
			window.opener.setNotifyValue(base_url + 'frame/get_NotifyValue_xml.jsp', {dataId: taskId});
			window.opener.grid.refreshGrid();
			window.opener.initFilterMenu();
		}
	}catch(e){}
	setSubmitDivDisable(false);
}
function onCompleteModifyTaskClose(request)
{
	$("#topSubmitDiv").hide();
	var result = onCompleteModifyTask(request);
	if(result.isError)
	{
		alert(result.errorMsg);
		dealWithButton(false);
		return;
	}
	
	if(!result.isError)
		refreshFilter();
	window.close();
}

function onCompleteModifyTaskRead(request)
{
	dealWithButton(true);
	$("#topSubmitDiv").hide();
	$("#top_modify_li").show();
	$("#top_edit_li").show();
	$("#select_next_action_top").empty();
	
	var result = onCompleteModifyTask(request);
	if(result.isError)
	{
		alert(result.errorMsg);
		dealWithButton(false);
	}
	else
	{
		initTaskManagement("read", null, taskId, filterId, "no", null, gridIndex, url);
	}
	
	if(!result.isError)
		refreshFilter();
	setSubmitDivDisable(false);
}

function onCompleteModifyTask(request)
{
	var responseXML = request;

	var rootNode = $(responseXML).children("root");

	var isErrorNode = $(rootNode).children("isError");
	eval("var isError = " + $(isErrorNode).text());

	var result = new Object();
	result.isError = isError;

	if(isError)
	{
		var errorMsg = "操作失败，原因如下：";
		var errorInfoNode = $(rootNode).children("errorInfo");
		if(errorInfoNode){
			errorMsg += "\r\n" + errorInfoNode.text();
		}
		result.errorMsg = errorMsg;
	}
	return result;
}

function onCompleteInitBizAssignUsers(request)
{
	var responseXML = request;
	$("#select_taskAssignUser").empty();
	$("#selectinputnameselect_taskAssignUser").attr("value","");
	$("#select_taskAssignUser").append("<option>--请选择--</option>");
	$(responseXML).find("user").each(function(index,node){
		var email= $(node).text();
		var name = $(node).attr("alias");
		var first= $(node).attr("first");
		name  = name+"["+email.split("[")[1].split("]")[0]+"]";
		email = email.split("[")[0];
		if(first == "true")
		{
			$("#select_taskAssignUser").append("<option value="+email+" selectec=selected>"+name+"</option>");
		}else
		{
			$("#select_taskAssignUser").append("<option value="+email+">"+name+"</option>");
		}
	});
}

/**
 * 设置对应项目
 * @param data 
 */
function setProducts(data){
	var field = getFieldById(projectInvolvedId);
	field.options = [];
	if(field){
		var $field = $("#field" + field.id);
		$field.empty();
		$field.append('<option value=>--请选择--</option>');
		for(var id in data ){
			$field.append('<option value=' + id + '>' + data[id] + '</option>' );
			field.options.push({forbidden: "permit",id: id , name: data[id]});
		}
		
		var value = '';
		if(cynthia.url.getQuery('projectId')){
			value = cynthia.url.getQuery('projectId');
		}else if(field.datas && field.datas.length > 0){
			value = field.datas[0];
		}else if(allDefaultValueMap.get(field.id)){
			value = allDefaultValueMap.get(field.id);	
		}
		$('#field' + field.id).val(value);
		enableSelectSearch();
	}
}

/**
 * 设置对应项目
 * @param projectId
 */
function setProjects(productId){
	var field = getFieldById(productInvolvedId);
	if(field){
		$('#field' + field.id).val(productId);
		//更新对应项目
		$.ajax({
			url : base_url + 'project/getProjects.do',
			dataType : 'json',
			data : { productId:productId },
			success : setProducts,
		});
	}
}

/**
 * 项目管理设置指派人
 * @param projectId
 * @param actionId
 * @returns
 */
function setAssignUserByProjectIdAndRoles(projectId, actionId,redraw){
	var action = getActionById(actionId);
	var roles = action ? action.nextActionRoles : lastActionRoles;
	$.ajax({
		url : base_url + 'project/getAllUsersByRolesAndProductId.do',
		type : 'post',
		dataType : 'json',
		data : {roles:roles,projectId:projectId},
		success : function(data){
			onCompleteInitProAssignUsers(data,redraw);
		}
	});
}

function getActionById(actionId){
	for(var i in actions){
		if(actions[i] && actions[i].id == actionId)
			return actions[i];
	}
	return null;
}

function checkSingleSelect(fieldId)
{
	var field = getFieldById(fieldId);
	var optionId = $("#field" + fieldId).val();
	field.datas[0] = optionId;

	if(field.isControlHiddenField){
		drawFieldsArea();
	}
	else
	{
		if(isProTemplate && field.id == productInvolvedId){
			setProjects(optionId);
		}
		
		if(isProTemplate && field.id == projectInvolvedId){
			//更新对应指派人
			var actionId = $('#select_action').val().split('|')[0];
			setAssignUserByProjectIdAndRoles(optionId,actionId);
		}
		
		for(var i = 0; i < rows.length; i++)
		{
			var rowColumns = rows[i];
			for(var j = 0; j < rowColumns.length; j++)
			{
				var columnFields = rowColumns[j];
				for(var k = 0; k < columnFields.length; k++)
				{
					var tempField = columnFields[k];
					if($("#field_"+tempField.id + "_div").length <= 0)
					{
						continue;
					}
					
					if(tempField.id == null || tempField.controlFieldId != fieldId){
						continue;
					}
					
					if(optionId == "")
					{
						if(tempField.type == "selection")
						{
							if(tempField.dataType == "single" && $("#field" + tempField.id).length>0)
							{
								$("#field" + tempField.id)[0].selectedIndex = 0;
								checkSingleSelect(tempField.id);
							}

							$("#field_"+tempField.id+"_div").html(drawSelectionField(tempField));
							
						}
						else if(tempField.type == "reference")
							$("#field_"+tempField.id+"_div").html(drawReferenceField(tempField));
						else if(tempField.type == "attachment")
							$("#field_"+tempField.id+"_div").html(drawAttachmentField(tempField));
						else if(tempField.type == "input")
							$("#field_"+tempField.id+"_div").html(drawInputField(tempField));
					}
					else
					{
						tempField.datas = [];  //清除己有数据
						if(tempField.type == "selection")
						{
							$("#field_"+tempField.id+"_div").html(drawSelectionField(tempField));

							if(tempField.dataType == "single" && $("#field" + tempField.id).length>0)
								checkSingleSelect(tempField.id);
						}
						else if(tempField.type == "reference")
							$("#field_"+tempField.id+"_div").html(drawReferenceField(tempField));
						else if(tempField.type == "attachment")
							$("#field_"+tempField.id+"_div").html(drawAttachmentField(tempField));
						else if(tempField.type == "input")
							$("#field_"+tempField.id+"_div").html(drawInputField(tempField));
					}
					
				}	
			}
		}
		enableSelectSearch();
	}
}

function onCompleteInitProAssignUsers(data,redraw)
{
	$("#select_taskAssignUser").empty();
	for(var i in data){
		$("#select_taskAssignUser").append("<option value="+ data[i].userName +">" + data[i].nickName +"</option>");
	}
	
	if(redraw != false){
		afterCompleteInitTaskAssignUsers();
		drawFieldsArea();
		dealWithSelectAction();   //dealWithSelectAction()以后会重新drawInputField 所以得重新创建kindEditor
	}
	
	dealWithButton(false);
	disableEditor(false);
	enableSelectSearch();
}

function initField(fieldNode)
{
	var tempField = new Object();
	tempField.id = $(fieldNode).children("id").text();
	tempField.name = $(fieldNode).children("name").text();
	tempField.description = $(fieldNode).children("description").text();
	tempField.timestampFormat = $(fieldNode).children("timeFormat").text();
	tempField.dateCurTime = $(fieldNode).children("dateCurTime").text();
	tempField.type =  $(fieldNode).children("type").text();
	tempField.dataType = $(fieldNode).children("dataType").text();
	tempField.controlFieldId = $(fieldNode).children("controlFieldId").text();
	tempField.fieldSize = $(fieldNode).children("fieldSize").text();
	tempField.fieldTip = $(fieldNode).children("fieldTip").text();
	
	tempField.isControlHiddenField = false;
	//field control option ids
	tempField.controlOptionIds = new Array();
	var controlOptionIdNodes = $(fieldNode).children("controlOptionIds").children("controlOptionId");
	for(var i = 0; i < controlOptionIdNodes.length; i++)
	{
		tempField.controlOptionIds[i] = $(controlOptionIdNodes[i]).text();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////
	tempField.controlHiddenFieldId = $(fieldNode).children("controlHiddenFieldId").text();
	tempField.controlHiddenFields = new Array();
	var controlHiddenFieldStr = $(fieldNode).children("controlHiddenFields").text();

	if(controlHiddenFieldStr != "")
	{
		tempField.controlHiddenFields = controlHiddenFieldStr.split(",");
	}

	tempField.controlHiddenStates = new Array();
	var controlHiddenStateStr = $(fieldNode).children("controlHiddenStates").text();
	if(controlHiddenStateStr != "")
	{
		tempField.controlHiddenStates = controlHiddenStateStr.split(",");
	}
	/////////////////////////////////////////////////////////////////////////////////////////////

	//field default values
	tempField.defaultValues = new Array();
	var defaultValue = $(fieldNode).children("defaultValue").text();
	if(defaultValue != "")
	{
		if(tempField.type == "input")
			tempField.defaultValues[0] = defaultValue;
		else
			tempField.defaultValues = defaultValue.split(",");
	}

	//field datas
	tempField.datas = new Array();
	var dataNodes = $(fieldNode).children("datas").children("data");
	for(var i = 0; i < dataNodes.length; i++)
	{
		tempField.datas[i] = $(dataNodes[i]).text();
	}

	//field options
	tempField.options = new Array();
	var optionNodes = $(fieldNode).children("options").children("option");
	for(var i = 0; i < optionNodes.length; i++)
	{
		//option[i] is forbidden
		var optionId = $(optionNodes[i]).children("id").text();
		var optionForbidden = $(optionNodes[i]).children("forbidden").text();
		if(optionForbidden == "forbidden")
		{
			//if datas don't contain option[i], then continue
			var contained = false;
			for(var j = 0; j < tempField.datas.length; j++)
			{
				if(tempField.datas[j] == optionId)
				{
					contained = true;
					break;
				}
			}
			if(!contained)
				continue;
		}

		tempField.options[tempField.options.length] = new Object();
		tempField.options[tempField.options.length - 1].id = optionId;
		tempField.options[tempField.options.length - 1].name = $(optionNodes[i]).children("name").text();
		tempField.options[tempField.options.length - 1].controlOptionId = $(optionNodes[i]).children("controlOptionId").text();
		tempField.options[tempField.options.length - 1].forbidden = optionForbidden;
	}

	//field control action ids
	tempField.controlActionIds = new Array();
	var controlActionIds = $(fieldNode).children("controlActionIds").text();
	if(controlActionIds.length > 0)
		tempField.controlActionIds = controlActionIds.split(",");

	//field control role ids
	tempField.controlRoleIds = new Array();
	var controlRoleIds = $(fieldNode).children("controlRoleIds").text();
	if(controlRoleIds.length > 0)
		tempField.controlRoleIds = controlRoleIds.split(",");

	return tempField;
}

function isFieldHidden(field)
{
	if(!field)
		return false;
	if(field.controlFieldId != "")
	{
		return isFieldHidden(getFieldById(field.controlFieldId));
	}

	if(field.controlHiddenFieldId != "")
	{
		var controlHiddenField = getFieldById(field.controlHiddenFieldId);
		if(controlHiddenField == null)
			return true;
		var controlHiddenFields = field.controlHiddenFields;
		var defaultValues = new Array();

		if(controlHiddenField.type == "selection" && controlHiddenField.dataType == "single")
		{
			defaultValues = getFieldInitValues(controlHiddenField);
		}

		//控制字段隐藏
		for(var i = 0;i < controlHiddenFields.length; i++)
		{
			for(var j = 0 ;j<defaultValues.length; j++)
			{
			if(controlHiddenFields[i] == defaultValues[j])
			  {
			  	return true;
			  }
			}
		}
	}
	var controlHiddenStates = field.controlHiddenStates;
	if(controlHiddenStates!= null && typeof controlHiddenStates != "undefined" && controlHiddenStates.length>0 )
	{
		//状态隐藏
		for(var i = 0;i < controlHiddenStates.length; i++)
		{
			if(((operation=="create") && (controlHiddenStates[i] == 0))||controlHiddenStates[i] == task.statusId)
		  	{
				return true;
		  	}
		}
	}

	return false;
}

//检查所有field 设置对应控制隐藏字段
function setAllControlHiddenField()
{
	for(var i = 0; i < rows.length; i++)
	{
		var row = rows[i];
		for(var j = 0; j < row.length; j++)
		{
			var columns = row[j];
			for(var k = 0; k < columns.length ; k++)
			{
				var tempField = columns[k];
				if(tempField.id == null)
					continue;

				if(((tempField.name!=null)&&(tempField.name.indexOf("废弃"))>=0))
					continue;

				if(tempField.controlHiddenFieldId != null && tempField.controlHiddenFieldId != "" && tempField.controlHiddenFields != null && tempField.controlHiddenFields.length>0)
				{
					var controlHiddenField = getFieldById(tempField.controlHiddenFieldId);
					if(controlHiddenField != null)
					{
						controlHiddenField.isControlHiddenField = true;
					}
				}
			}
		}
	}
}

function drawFieldsArea(node)
{
	if(node)
	{
		var fieldRows = $(node).find("row");
		rows = new Array();
		$(fieldRows).each(function(i,fieldRow){
			var fieldRowColumns = new Array();
			var rowColumns = $(fieldRow).children("column");
			$(rowColumns).each(function(j,rowColumn){
				var rowColumnFields = new Array();
				var columnFields = $(rowColumn).children("field");
				$(columnFields).each(function(k,columnField){
					rowColumnFields[k] = initField(columnField);
				});
				fieldRowColumns[j] = rowColumnFields;
			});
			rows[i] = fieldRowColumns;
		});
		
		setAllControlHiddenField();
	}
	
	
	var layoutContent = "";
	var bugsOfCurrentTaskField = "";
	var bugsOfCurrentTask = new Array();
	
	for(var i = 0; i < rows.length; i++)
	{
		layoutContent += "<div class='row-fluid'>";
		var rowColumns = rows[i];
		var rowSpanCount = rowColumns.length;
		var rowSpanWidth = 12 / rowSpanCount;
		var rowSpan = "span"+rowSpanWidth;
		
		for(var j = 0; j < rowColumns.length; j++)
		{
			var columnFields = rowColumns[j];
			layoutContent += "<div class='"+rowSpan+"'>";
			layoutContent += "<form class='form-horizontal'>";
			
			for(var k = 0; k < columnFields.length; k++)
			{
				var columnField = columnFields[k];
				if(columnField.id == null)
				{
					continue;
				}

				if(((columnField.name!=null)&&(columnField.name.indexOf("废弃"))>=0))
				{
					continue;
				}
				
				if(isFieldHidden(columnField))
					continue;
				
				if(columnField.id != '641814'&&columnField.type == "reference"&&columnField.dataType == "multiple")
				{
					bugsOfCurrentTask.push(columnField);
					continue;
				}
				
			    layoutContent += "<div class='control-group' id='field_"+columnField.id+"_div'>";
			    layoutContent += getFieldContent(columnField);
			    layoutContent += "</div>";
			}
			
			layoutContent += "</form>";
			layoutContent += "</div>";
		}
		
		layoutContent += "</div>";
	}
	
	for(var i = 0; i < bugsOfCurrentTask.length ; i++)
	{
		bugsOfCurrentTaskField += "<div class='row-fluid multipleRef'>";
		bugsOfCurrentTaskField += "<div class='span12'>";
		bugsOfCurrentTaskField += "<form class='form-horizontal'>";
		bugsOfCurrentTaskField += "<div class='control-group' id='field_"+bugsOfCurrentTask[i].id+"_div'>";
		bugsOfCurrentTaskField += getFieldContent(bugsOfCurrentTask[i]);
		bugsOfCurrentTaskField += "</div>";
		bugsOfCurrentTaskField += "</form>";
		bugsOfCurrentTaskField += "</div>";
		bugsOfCurrentTaskField += "</div>";
	}
	
	layoutContent = layoutContent + bugsOfCurrentTaskField;
	$("#layoutContent").html(layoutContent);

	/***项目管理默认值设置时特殊处理************/
	if(isProTemplate && operation != 'read'){
		var fieldProduct = getFieldById(productInvolvedId);
		if(fieldProduct){
			setProjects($('#field' + fieldProduct.id).val());
		}
		var fieldProject = getFieldById(projectInvolvedId);
		var actionId = $('#select_action').val().split('|')[0];
		setAssignUserByProjectIdAndRoles($('#field' + fieldProject.id).val(),actionId,false);
	}

	/***项目管理默认值设置时特殊处理************/
	
	enableSelectSearch();
}

function getFieldContent(columnField)
{
	if(columnField.type == "selection")
	{
		return drawSelectionField(columnField);
	}
	else if(columnField.type == "reference")
	{
		return drawReferenceField(columnField);
	}
	else if(columnField.type == "attachment")
	{
		return drawAttachmentField(columnField);
	}
	else if(columnField.type == "input")
	{
		return drawInputField(columnField);
	}
}

//一键设置所有默认值
function setAllDefaultValues(){
	
	for(var i = 0; i < rows.length; i++)
	{
		var rowColumns = rows[i];
		for(var j = 0; j < rowColumns.length; j++)
		{
			var columnFields = rowColumns[j];
			
			for(var k = 0; k < columnFields.length; k++)
			{
				var columnField = columnFields[k];
				if(columnField.id == null || isFieldHidden(columnField))
					continue;
				if(((columnField.name!=null) && (columnField.name.indexOf("废弃"))>=0))
					continue;

				if(!isFieldDisplay(columnField)){  //字段没显示则跳过
					continue;
				}
				
				if(columnField.type == "selection")
				{
					var selectedField = $("#field" + columnField.id)[0];
					if(!selectedField || selectedField.selectedIndex < 0)
						continue;

					var controlFieldSelect = null;

					var field = getFieldById(columnField.id);
					if(field.controlFieldId != "")
						controlFieldSelect = $("#field" + field.controlFieldId)[0];

					if(selectedField.selectedIndex == 0&&selectedField.options[0].value == "")
					{
						if(controlFieldSelect != null)
							allDefaultValueMap.put(columnField.id + "|" + controlFieldSelect.options[controlFieldSelect.selectedIndex].value,"");
						else
							allDefaultValueMap.put(columnField.id,"");
					}
					else
					{
						var selectedIds = null;
						for(var i = 0; i < selectedField.options.length; i++)
						{
							if(selectedField.options[i].selected)
							{
								if(selectedIds == null)
								{
									selectedIds = selectedField.options[i].value;
								}
								else
								{
									selectedIds += "|" + selectedField.options[i].value;
								}
							}
						}
						
						if(controlFieldSelect != null){
							allDefaultValueMap.put(columnField.id + "|" + controlFieldSelect.options[controlFieldSelect.selectedIndex].value,selectedIds);
						}
							
						else{
							allDefaultValueMap.put(columnField.id,selectedIds);
						}
					}
				}
				else if(columnField.type == "reference")
				{
					var fieldSelect = $("#field" + columnField.id)[0];
					var values = "";
					if(fieldSelect && fieldSelect.options.length >= 0)
					{
						var selectedIds = "";
						for(var i = 0; i < fieldSelect.options.length; i++)
						{
							if(selectedIds.length > 0)
								values += "&|;";
							values += fieldSelect.options[i].value + "&|;" + getTextContent(fieldSelect.options[i]);
						}

						if(values && values != ""){
							allDefaultValueMap.put(columnField.id,values);
						}
					}
				}
			}
		}
	}
	updateDefaultValue();
	
}

function updateDefaultValue()
{
	var t_template_id = null;
	if(selectedTemplate != null && selectedTemplate != "" && selectedTemplate != 'null'){
		t_template_id = selectedTemplate;
	}else{
		t_template_id = task.templateId;
	}
	if(t_template_id == null || t_template_id == undefined)
		t_template_id = $("#select_template").val();
	$.ajax({
		url:'defaultValue/setdefaultValues.do',
		type:'POST',
		data:{'templateId':t_template_id, 'defaultValueJson' : allDefaultValueMap.toJson()},
		success:function(data){
			if(data == 'true')
				showInfoWin('success','默认值设置成功!');
			else
				showInfoWin('error','默认值设置失败!');
		}
	});
}

function getSelectionValue(fieldId)
{
	var selectedField = $("#field" + fieldId)[0];
	if(!selectedField || selectedField.selectedIndex < 0)
		return;

	if(selectedField.selectedIndex == 0&&selectedField.options[0].value == "")
	{
		return "";
	}
	else
	{
		var selectedIds = null;
		for(var i = 0; i < selectedField.options.length; i++)
		{
			if(selectedField.options[i].selected)
			{
				if(selectedIds == null)
				{
					selectedIds = selectedField.options[i].value;
				}
				else
				{
					selectedIds += "|" + selectedField.options[i].value;
				}
			}
		}
		return selectedIds;
	}
}

function setSelectionDefaultValues(fieldId)
{
	var selectedField = $("#field" + fieldId)[0];
	if(selectedField.selectedIndex < 0)
		return;

	var controlFieldSelect = null;

	var field = getFieldById(fieldId);
	if(field.controlFieldId != "")
		controlFieldSelect = $("#field" + field.controlFieldId)[0];

	if(selectedField.selectedIndex == 0&&selectedField.options[0].value == "")
	{
		if(controlFieldSelect != null)
			allDefaultValueMap.put(fieldId + "|" + controlFieldSelect.options[controlFieldSelect.selectedIndex].value,"");
		else
			allDefaultValueMap.put(fieldId,"");
	}
	else
	{
		var selectedIds = null;
		for(var i = 0; i < selectedField.options.length; i++)
		{
			if(selectedField.options[i].selected)
			{
				if(selectedIds == null)
				{
					selectedIds = selectedField.options[i].value;
				}
				else
				{
					selectedIds += "|" + selectedField.options[i].value;
				}
			}
		}
		
		if(controlFieldSelect != null){
			allDefaultValueMap.put(fieldId + "|" + controlFieldSelect.options[controlFieldSelect.selectedIndex].value,selectedIds);
		}
			
		else{
			allDefaultValueMap.put(fieldId,selectedIds);
		}
	}
	updateDefaultValue();
}

function setReferenceValues(fieldId)
{
	var fieldSelect = $("#field" + fieldId)[0];
	var field = getFieldById(fieldId);
    
	if(fieldSelect == undefined || fieldSelect.options.length == 0)
	{
		field.datas = new Array();
	}
	else
	{
		for(var i = 0; i < fieldSelect.options.length; i++)
		{
			field.datas[field.datas.length] = fieldSelect.options[i].value + "&|;" + getTextContent(fieldSelect.options[i]);
		}

	}
}
function setReferenceDefaultValues(fieldId)
{
	var fieldSelect = $("#field" + fieldId)[0];

	if(fieldSelect.options.length == 0)
	{
		allDefaultValueMap.put(fieldId,"");
	}
	else
	{
		var selectedIds = "";
		for(var i = 0; i < fieldSelect.options.length; i++)
		{
			if(selectedIds.length > 0)
				selectedIds += "&|;";
			selectedIds += fieldSelect.options[i].value + "&|;" + getTextContent(fieldSelect.options[i]);
		}

		allDefaultValueMap.put(fieldId,selectedIds);
	}

	updateDefaultValue();
}

function isActionControlled(field)
{
	var isControlled = true;  //默认为可填
	var selectedActionId = $("#select_action").val().split("|")[0];
	
	if(selectedActionId == ""){
		//编辑动作
		for(var i = 0; i < field.controlRoleIds.length; i++)
		{
			var controlVar = field.controlRoleIds[i].split("_");
			var role = getRoleById(controlVar[0]);
			if(role && controlVar && controlVar.length == 2 && controlVar[1] == "-1" ){
				isControlled = false;
				break;
			}
		}
	}else{
		//流程动作
		for(var i = 0; i < field.controlActionIds.length; i++)
		{
			if(field.controlActionIds[i].split("_")[0] == selectedActionId)
			{
				var controlVar = field.controlActionIds[i].split("_");
				var role = getRoleById(controlVar[1]);
				if(role && controlVar && controlVar.length == 3 && controlVar[2] == "-1" ){
					isControlled = false;
					break;
				}
			}
		}
	}

	return isControlled;
}

function isFieldDisplay(field)
{
	if(operation == "read")
		return false;

	var controlField = getFieldById(field.controlFieldId);
	
	if(controlField == null)
	{
		if(isActionControlled(field))
			return true;
		return false;
	}
	
	var controlFieldSelect = $("#field" + controlField.id)[0];
	if(controlFieldSelect != null)
	{
		var controlFieldValue = controlFieldSelect.options[controlFieldSelect.selectedIndex].value;
		if(controlFieldValue == "")
			return false;

		if(field.type == "selection")
		{
			for(var i = 0; i < field.options.length; i++)
			{
				if(field.options[i].controlOptionId == controlFieldValue)
					return true;
			}

			return false;
		}

		for(var i = 0; i < field.controlOptionIds.length; i++)
		{
			if(field.controlOptionIds[i] == controlFieldValue)
				return true;
		}

		return false;
	}

	var controlFieldValues = getFieldInitValues(controlField);
	if(controlFieldValues.length == 0)
		return false;

	if(field.type == "selection")
		return true;

	for(var i = 0; i < field.controlOptionIds.length; i++)
	{
		if(field.controlOptionIds[i] == controlFieldValues[0])
			return true;
	}

	return false;
}

function drawSelectionField(field)
{
	var fieldHtml = "";
	if(isFieldDisplay(field))
	{
		if(getFieldMust(field) == 1)
			fieldHtml += "<label class='control-label must_font span2 setDefault' for='field"+field.id+"'>";
		else
			fieldHtml += "<label class='control-label common_font span2 setDefault' for='field"+field.id+"'>";
		
		fieldHtml += getXMLStr(field.name) + ":</label>";
		
		var fieldInitValues = getFieldInitValues(field);
		fieldHtml += "<div class='controls'>";
		fieldHtml += "<select  id='field"+field.id+"' ";
		if(field.dataType == 'multiple')
		{
			fieldHtml += "class='span10 singleLine' multiple onChange='checkNoValue(" + field.id + ")'";
			fieldHtml += ">";
		}else
		{
			fieldHtml += "class='span10 singleLine' onChange=\"checkSingleSelect('"+field.id+"')\"";
			fieldHtml += ">";
			fieldHtml += "<option value='' " +(fieldInitValues.length == 0 ? " selected" : "") + ">--请选择--</option>";
		}
		
		var controlField = getFieldById(field.controlFieldId);

		var controlFieldValue = null;
		if(controlField != null)
		{
			var controlFieldSelect = $("#field" + controlField.id)[0];
			if(controlFieldSelect != null)
				controlFieldValue = controlFieldSelect.options[controlFieldSelect.selectedIndex].value;
			else
			{
				var controlFieldInitValues = getFieldInitValues(controlField);
				if(controlFieldInitValues.length > 0)
					controlFieldValue = controlFieldInitValues[0];
			}
		}
		
		for(var i = 0; i < field.options.length; i++)
		{
			if(controlField == null || field.options[i].controlOptionId == controlFieldValue)
			{
				var isSelected = false;
				for(var j = 0; j < fieldInitValues.length; j++)
				{
					if(field.options[i].id == fieldInitValues[j])
					{
						isSelected = true;
						break;
					}
				}

				fieldHtml += "<option value='" + field.options[i].id + "'" + (isSelected ? " selected" : "")+ ">" + getNoXMLStr(field.options[i].name) + "</option>";

			}
		}
		
		fieldHtml += "</select><i class='icon-heart-empty' realValue='设为默认值' onClick=\"setSelectionDefaultValues('" + field.id + "')\"></i>";
		fieldHtml += "</div>";
	}else
	{
		fieldHtml += "<label class='control-label span2 ";
		if(field.datas.length > 0)
		{
			fieldHtml += "noEmpty";
		}else
		{
			fieldHtml += "no_care_font";
		}
		
		fieldHtml += " for='field"+field.id+"_label' >"+getXMLStr(field.name)+":</label>";
		fieldHtml += "<div class='controls'>";
		
		if(field.dataType == 'multiple')
		{
			fieldHtml += "<select class='span10 singleLine' multiple id='field"+field.id+"_label' disabled>";
			for(var i = 0; i < field.options.length ; i++)
			{
				for (var j = 0; j < field.datas.length; j++)
				{
					if(field.options[i].id == field.datas[j])
					{
						fieldHtml += "<option value='" + field.options[i].id + "'" + " selected" + ">" + getXMLStr(field.options[i].name) + "</option>";
						break;
					}
				}
			}
			fieldHtml += "</select>";
		}else
		{
			fieldHtml += "<label class='span10 singleLine' id='field"+field.id+"_label'>";
			for(var i = 0; i < field.options.length ; i++)
			{
				for (var j = 0; j < field.datas.length; j++)
				{
					if(field.options[i].id == field.datas[j])
					{
						fieldHtml += getXMLStr(field.options[i].name);
						fieldHtml += "<br />";
						break;
					}
				}
			}
			fieldHtml += "</label>";
		}
		fieldHtml += "</div>";
	}
	return fieldHtml;
}

function drawAttachmentField(field)
{
	var fieldHtml = "";
	if(isFieldDisplay(field))
	{
		if(getFieldMust(field) == 1)
			fieldHtml += "<label class='control-label span2 must_font' for='field"+field.id+"' >";
		else
			fieldHtml += "<label class='control-label span2 common_font' for='field"+field.id+"' >";
		
		fieldHtml += getXMLStr(field.name) + ":</label>";
		
		fieldHtml += "<div class='controls'>";
		fieldHtml += "<select class='span10 multiLine noSearch' id='field"+field.id+"' multiple>";
		var fieldInitValues = getFieldInitValues(field);
		for(var i = 0; i < fieldInitValues.length; i++)
		{
			fieldHtml += "<option value='" + fieldInitValues[i].split("&|;")[0] + "'>" + getXMLStr(fieldInitValues[i].split("&|;")[1]) + "</option>";
		}
		
		fieldHtml += "</select>";
		
		fieldHtml += "<br/><button id='input_field" + field.id + "upload' class='btn btn-mini' type='button' onClick=\"displayUploadFile('" + field.id + "')\">上传</button>";
		fieldHtml += "&nbsp;<button id='input_field" + field.id + "download' class='btn btn-mini' type='button' onClick=\"displayDownloadFile('" + field.id + "')\">下载</button>";
		fieldHtml += "&nbsp;<button id='input_field" + field.id + "remove' class='btn btn-mini' type='button' onClick=\"removeFile('" + field.id + "')\">移除</button>";
		
		fieldHtml += "</div>";
	}else
	{
		fieldHtml += "<label class='control-label span2 ";
		if(field.datas.length > 0)
		{
			fieldHtml += "noEmpty";
		}else
		{
			fieldHtml += "no_care_font";
		}
		
		fieldHtml += " for='field"+field.id+"_label' >"+getXMLStr(field.name)+":</label>";
		
		fieldHtml += "<div class='controls'>";
		fieldHtml += "<label class='span10 multiLine' id='field"+field.id+"_label'>";
		for(var i = 0; i < field.datas.length; i++)
		{
			fieldHtml += "<img src='images/file.bmp'/>&nbsp;<a href='#' onClick=\"downloadFileById('" + field.datas[i].split("&|;")[0] + "',this)\">" + getXMLStr(field.datas[i].split("&|;")[1]) + "</a><br />";
		}
		
		fieldHtml += "</label>";
		fieldHtml += "</div>";
	}

	return fieldHtml;
}

function drawReferenceField(field)
{
	var fieldHtml = "";
	var fieldInitValues = getFieldInitValues(field);
	if(isFieldDisplay(field))
	{
		if(getFieldMust(field) == 1)
			fieldHtml += "<label class='control-label span2 must_font setDefault' for='field"+field.id+"' >";
		else
			fieldHtml += "<label class='control-label span2 common_font setDefault' for='field"+field.id+"' >";
		
		fieldHtml += getXMLStr(field.name) + ":</label>";
		
		fieldHtml += "<div class='controls'>";
		
		if(field.dataType == 'multiple')
		{
			fieldHtml += "<select id='field"+field.id+"' class='span10 " + field.type + " multiLine noSearch' multiple>";
		}
		else
		{
			fieldHtml += "<select id='field"+field.id+"' class='span10 singleLine noSearch'>";
		}	
		
		for(var i = 0; i < fieldInitValues.length; i++)
		{
			fieldHtml += "<option value='" + fieldInitValues[i].split("&|;")[0] + "'>" + getXMLStr(fieldInitValues[i].split("&|;")[1]) + "</option>";
		}
	
		fieldHtml += "</select>";
		fieldHtml += "<i class='icon-heart-empty' realValue='设为默认值' onClick=\"setReferenceDefaultValues('" + field.id + "')\"></i>"
		fieldHtml += "<br />&nbsp;<button id=\"input_field" + field.id + "add\" type='button' class='btn btn-mini' onClick=\"displayQueryTaskPage('" + field.id + "')\">添加</button>";
		fieldHtml += "&nbsp;<button id=\"input_field" + field.id + "open\" type='button' class='btn btn-mini' onClick=\"readTask('" + field.id + "')\">查看</button>";
		fieldHtml += "&nbsp;<button id=\"input_field" + field.id + "remove\" type='button' class='btn btn-mini' onClick=\"removeTask('" + field.id + "')\">移除</button>";
		
		fieldHtml += "</div>";
	}else
	{
		fieldHtml += "<label class='control-label span2 ";
		if(field.datas.length > 0)
		{
			fieldHtml += "noEmpty";
		}else
		{
			fieldHtml += "no_care_font";
		}
		
		fieldHtml += " for='field"+field.id+"_label' >"+getXMLStr(field.name)+":</label>";
		
		fieldHtml += "<div class='controls'>";
		
		if(field.dataType == 'multiple')
		{
			fieldHtml += "<label class='span10 multiLine "+ field.type+"'  id='field"+field.id+"_label'>";
		}else
		{
			fieldHtml += "<label class='span10 singleLine' id='field"+field.id+"_label'>";
		}
		
		var isFirst = true;
		for(var i = 0; i < field.datas.length; i++)
		{
			if(isFirst)
				isFirst = false;
			else
				fieldHtml += "<br>";

			referId = field.datas[i].split("&|;")[0];

			var statusName = fieldInitValues[i].split("&|;")[2];
			statusName = (statusName != 'null' ? '[' + getXMLStr(statusName) + ']' : '');

			var priorityName = fieldInitValues[i].split("&|;")[3];
			priorityName = (priorityName != 'null' ? '[' + getXMLStr(priorityName) + ']' : '');

			fieldHtml += referId + "&nbsp;&nbsp;<font color=\"red\">" + statusName + priorityName + "</font>&nbsp;&nbsp;";
			fieldHtml += getXMLStr(field.datas[i].split("&|;")[1]);

			if(field.dataType == "single" || field.dataType == "multiple")
				fieldHtml += "&nbsp;&nbsp;<input type=\"button\" class='btn btn-mini'  value=\"查看\" onClick=\"readTaskById('" + referId + "')\"/>";
		}
		
		fieldHtml += "</label>";
		fieldHtml += "</div>";
	}
	
	return fieldHtml;
}

function setInputValue(selectId)
{
	var inputSelectValue = $("#" +selectId).val();
	var fieldId = selectId.substring(5);
	var field = getFieldById(fieldId);
	field.datas.length = 0;

	if(field != null)
	{
		field.datas.length = 0;
		field.datas[0] = inputSelectValue;
	}
}

function drawInputField(field)
{
	var fieldHtml = "";
	if(isFieldDisplay(field))
	{
		var fieldInitValues = getFieldInitValues(field);
		if(getFieldMust(field) == 1)
			fieldHtml += "<label class='control-label span2 must_font' for='field"+field.id+"' >";
		else
			fieldHtml += "<label class='control-label span2 common_font' for='field"+field.id+"' >";
		
		fieldHtml += getXMLStr(field.name) + ":</label>";
		fieldHtml += "<div class='controls'>";
		
		if(field.dataType == 'text')
		{
			if(field.fieldSize == 2)
			{
				fieldHtml += "<textarea id='field"+field.id +"' class='span10 multiLine text-large' onblur=\"setInputValue('field"+field.id+"')\"";
			}else
			{
				fieldHtml += "<textarea id='field"+field.id +"' class='span10 multiLine' onblur=\"setInputValue('field"+field.id+"')\"";
			}
			
			if(field && field.fieldTip && field.fieldTip != '')
				fieldHtml += " placeholder='" + field.fieldTip +"'";
			
			if(field.id == "192099" || field.id == "192098") //bug产生的原因 || bug修改范围和方案
				fieldHtml += " style='height:225px;'";
			fieldHtml += ">";
			fieldHtml += (fieldInitValues.length > 0 ? getXMLStr(fieldInitValues[0]) : "");
			fieldHtml += "</textarea>";
		}else if(field.dataType == 'timestamp')
		{
			var timeFormat = field.timestampFormat || 'yyyy-MM-dd HH:mm:ss';
			fieldHtml += "<input class='Wdate span10 singleLine' type='text'  id='field" +  field.id + "'  onfocus=\"WdatePicker({dateFmt:'" + timeFormat + "'})\" value='";
			var defaultDateValue = fieldInitValues.length > 0 ? getXMLStr(fieldInitValues[0]) : '';
			if(!defaultDateValue && field.dateCurTime == 'true'){
				defaultDateValue = cynthia.date.format(timeFormat);
			}
			fieldHtml += defaultDateValue;
			fieldHtml += "'/>";
		}else if(field.dataType =='editor')
		{
			fieldHtml += "<textarea class='span10 multiLine' id=\"field" +  field.id + "\"  name=\"ued_content\" onblur=\"setInputValue('field" + field.id + "')\"";
			fieldHtml +=" >";
			fieldHtml += (field.datas.length > 0 ? field.datas[0] : "");
			fieldHtml += "</textarea>";
		}else
		{
			fieldHtml += "<input class='span10 singleLine' id='field" + field.id + "' type='text'  onblur=\"setInputValue('field" + field.id + "')\"  value='";
			fieldHtml += (fieldInitValues.length > 0 ? getXMLStr(fieldInitValues[0]) : "");
			fieldHtml += "'";
			if(field && field.fieldTip && field.fieldTip != '')
				fieldHtml += " placeholder='" + field.fieldTip +"'";
			fieldHtml += "/>";
		}
		fieldHtml += "</div>";
		
	}else
	{
		fieldHtml += "<label class='control-label span2 ";
		if(field.datas.length > 0)
		{
			fieldHtml += "noEmpty";
		}else
		{
			fieldHtml += "no_care_font";
		}
		
		fieldHtml += " for='field"+field.id+"_label' >"+getXMLStr(field.name)+":</label>";
		
		fieldHtml += "<div class='controls'>";
		
		if(field.dataType == 'text')
		{
			if(field.fieldSize == '2')
			{
				fieldHtml += "<label class='span10 multiLine text-large' id='field"+field.id+"_label' ";
			}else
			{
				fieldHtml += "<label class='span10 multiLine' id='field"+field.id+"_label' ";
			}
			fieldHtml += ">";
		}else if(field.dataType == 'editor')
		{
			fieldHtml += "<textarea class='span10 multiLine'  readonly ='true' name='ued_content' id='field"+field.id+"_label'>";
		}else
		{
			fieldHtml += "<label class='span10 singleLine' id='field"+field.id+"_label'>";
		}
		
		if(field.datas.length > 0)
		{
			if(field.dataType == "text" || field.dataType == "string")
			{
				if(field.datas[0] != undefined){
					var fieldDataValue = getXMLStr(field.datas[0]);
					if(fieldDataValue.indexOf("http://") == 0){
						var gridHtml = "";
						var httpValue = fieldDataValue;
						while(httpValue.indexOf("http://") !=-1){
							var hrefValue = httpValue.substring(httpValue.lastIndexOf("http:"));
							httpValue =  httpValue.substring(0,httpValue.lastIndexOf("http:"));
							gridHtml += gridHtml.length > 0 ? "<br/>" :"";
							gridHtml += "<a href=\""+hrefValue+"\" target=\"_blank\">" + hrefValue + "</a>";
						}
						fieldHtml += gridHtml;
					}else{
						if(fieldDataValue.length>200)
						{
							var realValue = fieldDataValue;
							realValue = replaceAll(realValue,"\n","<br/>");
							fieldDataValue = fieldDataValue.substring(0,200);
							fieldDataValue = replaceAll(fieldDataValue,"\n","<br/>");
							fieldHtml += "" + fieldDataValue+ " ...<input type='button' class='btn btn-mini' value='详情' realValue='"+(realValue)+"' onclick=showDetails(this) />";
						}else
						{
							fieldDataValue = replaceAll(fieldDataValue,"\n","<br/>");
							fieldHtml += ""+(fieldDataValue)+"";
						}
					}
				}
			}else{
				fieldHtml += field.datas[0];
			}
		}else{
			if(field.dataType == "text"||field.dataType == "editor"){
				fieldHtml += "";
			}
		}
		
		if(field.dataType =="editor")
			fieldHtml += "</textarea>";
		else
			fieldHtml += "</label>";
		
		fieldHtml += "</div>";
	}
	
	return fieldHtml;
}

function appendFullScreen()
{
	var img = "<img src=\"images/fullScreen.jpg\" title=\"全屏查看\" onclick=\"showDescription()\"/>";
	$("#header_img").append(img);
	$("#setAllValue").hide();
}
function removeFullScreen()
{
	$("#header_img").find("img").remove();
	$("#setAllValue").show();
}

function showDescription()
{
  var value = task.description;
  document.body.style.overflow = "hidden";
  if(document.getElementById("divWin"))
  {
	  $("#divWin").css('zIndex',100002);
	  $("#divWin").show();
  }
  else
  {
	  var objWin=document.createElement("div");
	  objWin.id="divWin";
	  objWin.style.position="absolute";
	  objWin.style.width="100%";
	  objWin.style.height="100%";
	  objWin.style.top="0px";
	  objWin.style.left="0px";
	  objWin.style.overflow="scroll";
	  objWin.style.bottom="0px";
	  objWin.style.border="2px solid #AEBBCA";
	  objWin.style.background="#FFF";
	  objWin.style.zIndex=10002;
	  document.body.appendChild(objWin);
  }

  if(document.getElementById("win_bg"))
  {
	  $("#win_bg").css('zIndex',1001);
	  $("#win_bg").show();
  }
  else
  {
	  var obj_bg=document.createElement("div");
	  obj_bg.id="win_bg";
	  obj_bg.className="win_bg";
	  document.body.appendChild(obj_bg);
  }

  var str="";
  str+='<div class="winTitle"><span class="title_left">详情</span><span class="title_right"><a href="javascript:closeWindow()" title="单击关闭此窗口">关闭</a></span><br style="clear:right"/></div>';  //标题栏
  str+='<div class="winContent">'+value+'</div>';  //窗口内容
  $("#divWin").html(str);
}

function showDetails(node)
{
  var value = $(node).attr("realValue");
  document.body.style.overflow = "auto";
  var top = $(window).scrollTop();
  if(document.getElementById("divWin"))
  {
	  $("#divWin").css('zIndex',1002);
	  $("#divWin").show();
  }
  else
  {
	  var objWin=document.createElement("div");
	  objWin.id="divWin";
	  objWin.style.position="absolute";
	  objWin.style.width="520px";
	  objWin.style.top= (220+top)+"px";
	  objWin.style.left="420px";
	  objWin.style.border="2px solid #AEBBCA";
	  objWin.style.background="#FFF";
	  objWin.style.zIndex=1002;
	  document.body.appendChild(objWin);
  }

  if(document.getElementById("win_bg"))
  {
	  $("#win_bg").css('zIndex',1001);
	  $("#win_bg").show();
  }
  else
  {
	  var obj_bg=document.createElement("div");
	  obj_bg.id="win_bg";
	  obj_bg.className="win_bg";
	  obj_bg.style.top= top+"px";
	  document.body.appendChild(obj_bg);
  }

  var str="";
  str+='<div class="winTitle" onMouseDown="startMove(this,event)" onMouseUp="stopMove(this,event)"><span class="title_left">详情</span><span class="title_right"><a href="javascript:closeWindow()" title="单击关闭此窗口">关闭</a></span><br style="clear:right"/></div>';  //标题栏
  str+='<div class="winContent">'+value+'</div>';  //窗口内容
  $("#divWin").html(str);
}
function closeWindow(){
  $("#divWin").remove();
  $("#win_bg").remove();
  document.body.style.overflow = "auto";
}

function startMove(o,e){
  var wb;
  if(document.all && e.button==1) wb=true;
  else if(e.button==0) wb=true;
  if(wb)
  {
    var x_pos=parseInt(e.clientX-o.parentNode.offsetLeft);
    var y_pos=parseInt(e.clientY-o.parentNode.offsetTop);
    if(y_pos<=o.offsetHeight)
    {
      document.documentElement.onmousemove=function(mEvent)
      {
        var eEvent=(document.all)?event:mEvent;
        o.parentNode.style.left=eEvent.clientX-x_pos+"px";
        o.parentNode.style.top=eEvent.clientY-y_pos+"px";
      }
    }
  }
}

function stopMove(o,e){
  document.documentElement.onmousemove=null;
}

function setCurrentDate(fieldId)
{
	var currentDate = new Date();
	//var year = (window.navigator.userAgent.indexOf("Firefox") >= 0 ? currentDate.getYear() + 1900 : currentDate.getYear());
	var year = currentDate.getFullYear();
	var month = currentDate.getMonth() + 1;
	var day = currentDate.getDate();
	$("#field" + fieldId).val((year - 2006));
	$("#field" + fieldId + "month").val(month);
	$("#field" + fieldId + "day").val(day);
	$("#field" + fieldId + "hour").val(0);

	$("#span_field" + fieldId + "month").show();
	$("#span_field" + fieldId + "day").show();
	$("#span_field" + fieldId + "hour").show();
	$("#span_field" + fieldId + "minute").hide();
}
function setCurrentTime(fieldId)
{
	var currentDate = new Date();
	var year = currentDate.getFullYear();
	var month = currentDate.getMonth() + 1;
	var day = currentDate.getDate();
	var hour = currentDate.getHours()+1;
	var minute = currentDate.getMinutes();
	$("#field" + fieldId).val((year - 2006));
	$("#field" + fieldId + "month").val(month);
	$("#field" + fieldId + "day").val(day);
	$("#field" + fieldId + "hour").val(hour);
	$("#field" + fieldId + "minute").val(minute);

	$("#span_field" + fieldId + "month").show();
	$("#span_field" + fieldId + "day").show();
	$("#span_field" + fieldId + "hour").show();
	$("#span_field" + fieldId + "minute").show();
}

function clearFieldsTables()
{
	$("#topLeftTable").html("");
	$("#topMiddleeftTable").html("");
	$("#topRightTable").html("");
	$("#bottomLeftTable").html("");
	$("#bottomMiddleTable").html("");
	$("#bottomRightTable").html("");
}


function selectTemplate()
{
	dealWithButton(true);
	$("#select_action")[0].options.length = 0;
	$("#select_action")[0].options[0] = new Option("--请选择--", "");

	$("#select_taskAssignUser")[0].options.length = 0;
	$("#select_taskAssignUser")[0].options[0] = new Option("--请选择--", "");

	clearFieldsTables();

	var templateId = $("#select_template").val();
	if(templateId == "")
	{
		if(operation == "create")
		{
			$("#input_setDefaultAction").hide();
			$("#input_setDefaultAssignUser").hide();
		}
		dealWithButton(false);
		return;
	}
	
	allDefaultValueMap = new Map();
	getAllDefaultValues(templateId);	
	
	var params = "templateId=" + getSafeParam(templateId);
	$.ajax({
		url : 'task/initStartActions.jsp',
		data : params,
		dataType : 'xml',
		success : onCompleteInitStartActions
	});
}

var drawRootNode = null;
function onCompleteInitStartActions(request)
{
	var responseXML = request;
	var rootNode = $(responseXML).children("root");
	var templateId = $("#select_template").val();

	var defaultAction = readCookie("action" + templateId);
	isProTemplate = $(rootNode).children("isProTemplate").text() == 'true';
	productInvolvedId = $(rootNode).children("productInvolvedId").text();
	projectInvolvedId = $(rootNode).children("projectInvolvedId").text();

	var actionNodes = $(rootNode).children("actions").children("action");
	
	for(var i = 0; i < actionNodes.length; i++)
	{
		var actionId = $(actionNodes[i]).children("id").text();
		var actionName = $(actionNodes[i]).children("name").text();
		var endStatId = $(actionNodes[i]).children("endStatId").text();
		var assignToMore = $(actionNodes[i]).children("assignToMore").text();
		var nextActionRoles = $(actionNodes[i]).children("nextActionRoles").text();
		var isEndAction = $(actionNodes[i]).children("isEndAction").text();
		
		actions[actionId] = new Object();
		actions[actionId].id = actionId;
		actions[actionId].name = actionName;
		actions[actionId].endStatId = endStatId;
		actions[actionId].assignToMore = assignToMore;
		actions[actionId].nextActionRoles = nextActionRoles;
		actions[actionId].isEndAction = isEndAction; 
		
		$("#select_action")[0].options[i + 1] = new Option(actionName, actionId + "|" + endStatId);

		if(actionId == defaultAction || actionNodes.length == 1)
		{
			changeAssignUserSelectDiv(actions[actionId] && actions[actionId].assignToMore);
			$("#select_action")[0].options[i + 1].selected = true;
			var defaultAssignUser = readCookie("au" + templateId + actionId);
			var userNodes = $(actionNodes[i]).children("users").children("user");
			for(var j = 0; j < userNodes.length; j++)
			{
				var user = $(userNodes[j]).text();
				var userAlias = $(userNodes[j]).attr("alias");
				$("#select_taskAssignUser")[0].options[j + 1] = new Option((userAlias ? userAlias + "[" + user.split("[")[1] : user), user.split("[")[0]);

				if(user.split("[")[0] == defaultAssignUser || userNodes.length == 1)
					$("#select_taskAssignUser")[0].options[j + 1].selected = true;
			}
			$("#input_setDefaultAssignUser").show();
		}

		$("#input_setDefaultAction").show();
	}
	
	var roleNodes = $(rootNode).children("roles").children("role");
	for(var i = 0; i < roleNodes.length; i++)
	{
		roles[i] = new Object();
		roles[i].id = $(roleNodes[i]).children("id").text();
		roles[i].name = $(roleNodes[i]).children("name").text();
	}

	drawRootNode = rootNode;

	if($("#select_action").val())
	{
		selectAction();
		drawFieldsArea(rootNode);
	}
	
	dealWithButton(false);
	enableSelectSearch();
}


/**
 * 根据actionId将指派人动态调整为多选
 */
function changeAssignUserSelectDiv(assignToMore){
	var html = "<select id='select_taskAssignUser' style='margin-bottom:5px;' class='span10' " + (assignToMore == 'true' || assignToMore == true ? "multiple='multiple'" : "") + ">";
	html += "</select>";
	html += "<i style='cursor:pointer' class='icon-heart-empty' title='设为默认值' onClick='setDefaultAssignUser()'></i>";
	var $parent = $("#select_taskAssignUser").parent();
	$parent.empty().html(html);
}

function hideSetDefaultAssignUser()
{
	$("#input_setDefaultAssignUser").show();
}

function afterCompleteInitTaskAssignUsers()
{
	if (nextActionName) {
		document.title = nextActionName.substring(1, nextActionName.length -1);
	}else {
		document.title = "修改";
	}
	
	$("#input_taskDescription")[0].readOnly = false;
	addActionName(nextActionName);
	$("#actionForm").show();
	$("#top_modify_li").hide();
	$("#top_edit_li").hide();
	$("#title_tr").html("<td style='width:85px;min-width:85px;text-align:left;'><b>标题("+task.id+"):</b></td><td><input type=\"text\" size=\"48\"  id=\"input_taskTitle\" value=\""+getXMLStr(task.title) + "\"></td>");
	
	$("#top_modify_li").attr("flag","1");
	$("#topSubmitDiv").show();
	if(operation != 'create')
		operation = "modify";
}

function enableSelectSearch()
{
	$("select").each(function(idx,select){
		if(!($(select).hasClass("multiLine")||$(select).hasClass('noSearch')))
		{
			$(select).select2({
				matcher: function(term, text, opt) {
				      return text.toUpperCase().indexOf(term.toUpperCase())>=0
				       || opt.val().toUpperCase().indexOf(term.toUpperCase())>=0;
				}
			});
		}else if($(select).hasClass("multiLine")&&!($(select).hasClass('noSearch')))
		{
			$(select).select2({});
		}
	});
}

function onCompleteInitTaskAssignUsers(request)
{
	var responseXML = request;

	var rootNode = $(responseXML).children("root");
	
	eval("needAssignUser = " + $(rootNode).children("needUser").text());
	var actionId = $("#select_action").val().split("|")[0];
	var assignToMore = $(rootNode).children("users").children("assignToMore").length;
	var userNodes = $(rootNode).children("users").children("user");
	if (needAssignUser) {
		if(assignToMore > 0){
			changeAssignUserSelectDiv(true);
		}else{
			changeAssignUserSelectDiv(actions[actionId] && actions[actionId].assignToMore);
		}
		$('#select_taskAssignUser').removeAttr("disabled");
		for(var i = 0; i < userNodes.length; i++)
		{
			var user = $(userNodes[i]).text();
			var userAlias = $(userNodes[i]).attr("alias");
			var optionName = (userAlias ? userAlias + "[" + user.split("[")[1] : user);
			var optionValue = user.split("[")[0];
			var isFirstUser = $(userNodes[i]).attr("first");
			$("#select_taskAssignUser")[0].options[i + 1] = new Option(optionName, optionValue);
			if(operation == "create")
			{
				var templateId = $("#select_template").val();
				var cookieValue = readCookie("au" + templateId + actionId);
				if( cookieValue== optionValue)
					$("#select_taskAssignUser")[0].options[i + 1].selected = true;
			}else if((operation == "read" || operation == "modify") && isFirstUser && isFirstUser == "true"){
				$("#select_taskAssignUser")[0].options[i + 1].selected = true;
			}else if(operation == "read" ){
				var templateId = task.templateId;
				var cookieValue = readCookie("au" + templateId + actionId);
				if( cookieValue== optionValue)
					$("#select_taskAssignUser")[0].options[i + 1].selected = true;
			}
		}
	}else {
		$('#select_taskAssignUser').attr("disabled","disabled");
	}

	if(operation == "create")
	{
		hideSetDefaultAssignUser();
		drawFieldsArea(drawRootNode);
		
		//项目管理新建bug
		if(operation == 'create'){
			var productId = cynthia.url.getQuery('productId');
			var projectId = cynthia.url.getQuery('projectId');
			if(productId && projectId){
				setProjects(productId);
			}
		}
	}else{
		afterCompleteInitTaskAssignUsers();
		drawFieldsArea();
		dealWithSelectAction();   //dealWithSelectAction()以后会重新drawInputField 所以得重新创建kindEditor
	}

	dealWithButton(false);
	disableEditor(false);
	enableSelectSearch();
}

function selectAction()
{
	dealWithButton(true);

	$("#select_taskAssignUser")[0].options.length = 0;
	$("#select_taskAssignUser")[0].options[0] = new Option("--请选择--", "");

	var actionId = $("#select_action").val().split("|")[0];
	if(actionId == "")
	{
		if(operation == "create")
			$("#input_setDefaultAssignUser").hide();

		dealWithButton(false);
		return;
	}

	changeAssignUserSelectDiv(actions[actionId] && actions[actionId].assignToMore);
	var templateId = getSafeParam($("#select_template").val());
	var params = "";
	params += "statId=" + getSafeParam($("#select_action").val().split("|")[1]);
	params += "&templateId=" + templateId;
	
	$.ajax({
		url : 'task/initTaskAssignUsers.jsp',
		data : params,
		dataType : 'xml',
		success : onCompleteInitTaskAssignUsers
	});
}

function returnZero(selectId)
{
	if($(selectId).options[0].selected)
	{
		for(var i = 1; i < $(selectId).options.length; i++)
		{
			$(selectId).options[i].selected = false;
		}
	}
}

function dealWithSelectAction()
{
	for(var i = 0; i< rows.length; i++)
	{
		var rowColumns = rows[i];
		for(var j = 0; j < rowColumns.length; j++)
		{
			var columnFields = rowColumns[j];
			for(var m = 0; m < columnFields.length ; m++)
			{
				var tempField = columnFields[m]; //临时变量存放当前操作的field
				if(tempField.id == null)
					continue;

				if(tempField.controlFieldId != "")
				{
					var controlField = getFieldById(tempField.controlFieldId);
					if(controlField != null)
						drawControlledFields(tempField);
				}
				else
				{
					var selectedActionId = $("#select_action").val().split("|")[0];
					if(selectedActionId == "")
					{
						for(var k = 0; k < tempField.controlRoleIds.length; k++)
						{
							if(getRoleById(tempField.controlRoleIds[k].split("_")[0]) != null)
							{
								drawControlledFields(tempField);
								break;
							}
						}
					}
					else
					{
						for(var k = 0; k < tempField.controlActionIds.length; k++)
						{
							if(tempField.controlActionIds[k].split("_")[0] == selectedActionId)
							{
								if(getRoleById(tempField.controlActionIds[k].split("_")[1]) != null)
								{
									drawControlledFields(tempField);
									break;
								}
							}
						}
					}
				}
			}
		}
	}
	
}

function drawControlledFields(field)
{
	var tempFiled = field;
	if(tempFiled.id == null)
	{
		return;
	}
	var tempField = $("#field_"+field.id+"_div")[0];
	if(tempField == null)
		return;
	if(tempFiled.type == "selection")
	{
		tempField.innerHTML = drawSelectionField(tempFiled);

		if(tempFiled.dateType == "single")
			checkSingleSelect(tempFiled.id);
	}
	else if(tempFiled.type == "reference")
	{
		var tempField = drawReferenceField(tempFiled);
		tempField.innerHTML = (tempFieldHtml==null?"":tempFieldHtml);
	}
	else if(tempFiled.type == "attachment")
	{
		tempField.innerHTML = drawAttachmentField(tempFiled);
	}
	else if(tempFiled.type == "input")
	{
		var tempFieldHtml = drawInputField(tempFiled);
		tempField.innerHTML = (tempFieldHtml==null?"":tempFieldHtml);
	}
}

function checkNoValue(fieldId)
{
	var field = getFieldById(fieldId);
	var fieldObj = document.getElementById("field" + fieldId);

	if(fieldObj.selectedIndex < 0)
	{
		for(var i = 1; i < fieldObj.options.length; i++)
		{
			fieldObj.options[i].selected = false;
		}
	}else{
		var j= 0;
		for(var i = 1; i < fieldObj.options.length; i++)
		{
			if(fieldObj.options[i].selected){
				field.datas[j++] = fieldObj[i].value;
			}
		}
	}
}

function displayDownloadFile(fieldId)
{
	var fileId = $("#field" + fieldId).val();

	if(fileId == null)
	{
		alert("请先选择下载文件");
		return;
	}
	window.open( base_url + "attachment/download.jsp?method=download&id=" + getSafeParam(fileId) );
}

function disolayTitleContent(span)
{
	var display = $("#title_content").css("display");
	if(display == "none")
	{
		$("#title_content").css("display","block");
	}else
	{
		$("#title_content").css("display","none");
	}
	return false;
}

function displayFieldsContent(span)
{
	var text = $(span).text();
	if(text == "收起")
	{
		$(span).text("展开");
		$(span).parent().parent().next().hide();
		$(".multipleRef").hide();
		createCookie("fieldTrShow"+ templateId + "=false");
	}else if(text == "展开")
	{
		$(span).text("收起");
		$(span).parent().parent().next().show();
		$(".multipleRef").show();
		createCookie("fieldTrShow"+ templateId + "=true");
	}
}

function displayLogDiv()
{
	var display = $("#logContentDiv").css("display");
	if(display == "none")
	{
		$("#logContentDiv").show();
		$("#displayLogDiv").find("span").text("隐藏日志");
		createCookie(templateId + "showLog=true");
	}else
	{
		$("#logContentDiv").hide();
		$("#displayLogDiv").find("span").text("显示日志");
		createCookie(templateId + "showLog=false");
	}
	return false;
}

function displayNullLogDiv()
{
	var isCookieHide = readCookie(templateId + "hideLogDiv");
	if(isCookieHide == null)
		isCookieHide = "false";

	$("#logContentDiv>table>tbody>tr").each(function(index,node){
		var tdnode = $(node).find("td:eq(2)");
		var tdvalue = jQuery(tdnode).text();
		if(isCookieHide == "true")
		{
			$(tdnode).parent().show();
		}else
		{
			if(tdvalue == "" || tdvalue == "-")
				$(tdnode).parent().hide();
		}

	});

	if(isCookieHide == "false"){
		$("#displayEmptyLogDiv").find("span").text("显示全部日志");
		createCookie(templateId + "hideLogDiv=true");
	}else{
		$("#displayEmptyLogDiv").find("span").text("隐藏空日志");
		createCookie(templateId + "hideLogDiv=false");
	}
}

function bindTipToFieldName()
{
	$("#layoutContent").delegate('.control-label','hover',function(e){
		var tipContent = "";
		var _self = jQuery(this)[0];
		if(_self.scrollWidth > _self.offsetWidth)
		{
			tipContent = jQuery(this).text() + "";
		}
		
		if(tipContent != "")
		{
			if(e.type=='mouseenter')
		    {
				$("#ttip").remove();
				var tip="<div id='ttip'>"+tipContent+"</div>";
	       	 $("body").append(tip);
	        	$("#ttip").css({"top":(e.pageY+20)+"px","left":(e.pageX+10)+"px"}).show(1);
		    }else
		    {
			  if($("#ttip").length>0)
			  {
				$("#ttip").text("");
			 	$("#ttip").remove();
			  }
		    }
		}
		
	  });
}

function setUserDefaultTemplate()
{
	var templateId = task.templateId||($("#select_template").val());
	var params = "templateId="+templateId;
	$.ajax({
		url : 'task/setUserDefaultTemplate.jsp',
		data : params,
		dataType : 'xml'
	}).done(function(response){
		alert("默认表单设置成功!");
	}).fail(function(response){
		alert("ERROR!");
	});
}

function displayExtFieldArea(extFieldAreaId)
{
	if(document.getElementById("extFieldArea" + extFieldAreaId).style.display == "none")
	{
		document.getElementById("extFieldArea" + extFieldAreaId).style.display = "";
	}
	else
	{
		document.getElementById("extFieldArea" + extFieldAreaId).style.display = "none";
	}
}

function readTaskById(taskId)
{
	window.open("taskManagement.html?operation=read&taskid=" + getSafeParam(taskId));
}

function downloadFileById(fileId,link)
{
	if(link)
	{
		var fileName = link.innerHTML;
		if(fileName&&(fileName.indexOf("JPEG")>=0||fileName.indexOf("GIF")>=0||fileName.indexOf("BMP")>=0||fileName.indexOf("bmp")>=0||fileName.indexOf("JPG")>=0||fileName.indexOf("PNG")>=0||fileName.indexOf("png")>=0||fileName.indexOf("gif")>=0||fileName.indexOf("jpg")>=0||fileName.indexOf("jpeg")>=0))
		{
			window.open( base_url + "attachment/image.jsp?fileId="+fileId);
			return;
		}
	}
	window.open(base_url + "attachment/download.jsp?method=download&id=" + getSafeParam(fileId) );
}

function onReadyLoad()
{
	var operation = request('operation');
	var taskId = null;
	var templateTypeId = null;
	
	if(operation == 'create')
	{
		templateTypeId = request('templateTypeId');
	}
	
	if(operation == "read")
	{
		if(request('taskid') != null && request('taskid') != 'null' && request('taskid') != "")
		{
			taskId = request('taskid');
		}else if(request('id') != null && request('id') != 'null' && request('id') != "")
		{
			taskId == request('id');
		}
		if(taskId == null)
		{
			alert("数据参数没有给出!");
		}
	}
	
	var filterId = request('filterId');
	var templateId = request('templateId');
	if(templateId == 'null')
		templateId == '';
	var gridIndex = request('gridIndex')|| -1;
	var url = (request('url')==""?null:request('url'));
	var type = request('type');
	
	initTaskManagement(operation, templateTypeId, taskId,filterId, 'yes',templateId, gridIndex, url, type);
}

function bindHover()
{
	//a span hover 提示框
	$(document).delegate('a,i,span','hover',function(e){
		   if(e.type=='mouseenter')
		   {
				if($(this).attr("realValue"))
				{
					this.myTitle = $(this).attr("realValue");
					$("#ttip").remove();
					var tip="<div id='ttip'>"+this.myTitle+"</div>";
					$("body").append(tip);
					$("#ttip").css({"top":(e.pageY+18)+"px","left":(e.pageX+10)+"px"}).show(1);
				}else if(this.scrollWidth>this.offsetWidth)
				{
					 this.myTitle=$(this).text();
					 $("#ttip").remove();
					 var tip="<div id='ttip'>"+this.myTitle+"</div>";
					 $("body").append(tip);
					 $("#ttip").css({"top":(e.pageY+18)+"px","left":(e.pageX+10)+"px"}).show(1);
				}
		   }else
			{
			   if($("#ttip").length>0)
			   {
				 $("#ttip").text("");
				 $("#ttip").remove();
			   }
			}
	});
	  
	$(document).delegate("a,span,i","mousemove",function(e){
		  if($("#ttip").length>0)
		  {
			$("#ttip").css({"top":(e.pageY+18)+"px","left":(e.pageX+10)+"px"});
		  }
	});
	
	//标签hover出现删除按纽
	$("#titleSpan").on('mouseover','.titleTag',function(){
		$(this).find("span").show();
	}).on('mouseout','.titleTag',function(){
		$(this).find("span").hide();
	});
	
	//Hover上关闭按纽
	$("#titleSpan").on('mouseover','.closeTag',function(){
		$(this).addClass('tag-hover');
	}).on('mouseout','.closeTag',function(){
		$(this).removeClass('tag-hover');
	});
}

//目前label被点击后会自动触发右侧的编辑功能取消这种绑定
function bindLabelClick()
{
	$(document).delegate(".titleContent label","click",function(){
		return false;
	});
	
	$(document).delegate(".control-label","dblclick",function(){
		//$(this).select();
		return false;
	});
	
	$(document).delegate(".reference","click",function(){
		return false;
	});
}

function bindButtonClick()
{
	$(document).delegate("#actionButton","click",function(e){
		if($("#select_next_action_top").find("li").length == 0){
			showInfoWin("error","没有权限操作动作!");
			$("#select_next_action_top").hide();
			return true;
		}
	});
	
	$(document).delegate('.closeTag','click',tagCloseIconClick);
	
//	//复制标题
//	$('a#titleCopyImg').zclip({
//		path:'lib/copy/ZeroClipboard.swf',
//		copy:function(){return $("#titleSpan").val();},
//		afterCopy:function(){
//			alert("标题己复制到粘贴板!");
//		}
//	});
}

function tagCloseIconClick(e){
	var dataArray = new Array();
	dataArray.push($(this).attr("dataId"));
	tagDatasMoveOut(dataArray,$(this).attr("tagId"));
	$(this).parent().remove();
	if(window.opener&&window.opener.grid)
	{
		window.opener.grid.refreshGrid();
	}
	return false;	
}


/*utils*/
/*send mail*/
function showSendMail()
{
	$("#sendMailReceivers").val("");
	$("#sendMailContent").val("");
	$("#send_mail_win").modal('show');
}

function returnMailHeader()
{
	if(mailHeader) return mailHeader;
	var htmlStr = "";
	htmlStr += "<html>";
	htmlStr += "<head>";
	htmlStr += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\"/>";
	htmlStr += "<style type=\"text/css\">";
	htmlStr += "table{border-collapse: collapse; border: 1px solid #CCCCCC; font-size: 100%;margin-top: 0em; margin-left: 5px; margin-bottom: 0em;width: 800px;table-layout:fixed;}";
	htmlStr += "th{border-right: 1px solid #CCCCCC;text-align: center;	white-space:nowrap;	background: #4EA9E4; margin: .25em;vertical-align: center;}";
	htmlStr += "tr{vertical-align: center;  background: #eeeeff;}";
	htmlStr += "td{border-right: 1px solid #CCCCCC; margin: .25em;vertical-align: center;  border-bottom: 1px solid #CCCCCC; word-wrap: break-word;word-break:break-all;max-width: 120px;display : table-cell;}";
	htmlStr += "body{margin: 0;padding: 0;background: #f6f6f6;}";
	htmlStr += "body,div,p,span{margin-top:0px; margin-bottom:0px; color: #333;font-size: 12px;line-height: 150%;font-family: Verdana, Arial, Helvetica, sans-serif;}";
	htmlStr += "</style>";
	htmlStr += "</head>";
	htmlStr += "<body>";
	mailHeader = htmlStr;
	return mailHeader;
}

function sendMailSubmit()
{
	var sendMailReceivers = $("#sendMailReceivers").val();
	if(sendMailReceivers == "")
	{
		alert("请填写收件人");
		return;
	}
	var usrArray = sendMailReceivers.split(";");
	for(var i = 0 ; i< usrArray.length ; i++){
		if(usrArray[i].indexOf("@")!=-1)
			if(!isEmail(usrArray[i])){
				alert("邮箱格式不正确，请重新填写");
				$("#sendMailReceivers").focus();
				return;
			}
	}

	if($("#sendMailContent").val() == "")
	{
		alert("请填写邮件正文");
		return;
	}
	
	var sendMailContent = returnMailHeader();
	sendMailContent += "<table>";
	sendMailContent += "<tr><td>邮件正文</td><td>" + replaceAll(getXMLStr($("#sendMailContent").val()), "\n", "<br>") + "</td></tr>";
	sendMailContent += "<tr><td>问题编号</td><td><a href=\"" + getWebRootDir() + "taskManagement.html?operation=read&taskid="+taskId+"\">"+taskId+"</a></td></tr>";
	sendMailContent += "<tr><td>问题描述</td><td>" + replaceAll($("#input_taskDescription").val(),"../attachment/download_json.jsp", getWebRootDir() + "attachment/download_json.jsp") +"</td></tr>";
	sendMailContent += "</table>";
	sendMailContent += "</body><html>";

	var params = "sendMailReceivers=" + getSafeParam(sendMailReceivers);
	params += "&sendMailSubject=" + getSafeParam("[Cynthia][" + taskId + "]有数据需要您的处理意见，请关注并处理");
	params += "&sendMailContent=" + getSafeParam(sendMailContent);

	$("#mail_send_ok").disabled = true;

	$.ajax({
		url : "mail/executeSendMail.jsp",
		data : params,
		type : 'POST',
		success : mailSendSuccess
	});
}

function mailSendSuccess()
{
	showInfoWin("success","邮件发送成功!");
	$("#send_mail_win").modal('hide');
	$("#mail_send_ok").disabled = false;
}

function formatDateStr(dateStr)
{
	if(dateStr&&dateStr.indexOf(".") >= 0)
	{
		dateStr = dateStr.substring(0,dateStr.lastIndexOf("."));
	}
	return dateStr;
}


$(function(){
	initMyTag();
	onReadyLoad();
	//标签颜色选项
	$('#tagColor').colorPicker({showHexField: false});   //showHexField是否显示Hex值
	bindTipToFieldName();
	$("#ul-tag").delegate('.tag', 'click', myTagClick);
	bindHover();
	//bindFieldTip();
	bindLabelClick();
	bindButtonClick();
	//获取默认新建表单
	$.ajax({
		url : 'filterManage/initUserDefaultTemplate.jsp',
		dataType : 'xml'
	}).done(function(response){
		var userDefaultTemplateId = $(response).find("templateId").text();
		var templateTypeId = $(response).find("templateTypeId").text();
		if(userDefaultTemplateId&&userDefaultTemplateId!="")
		{
			$("#newDataLink").attr("href","taskManagement.html?operation=create&templateId="+userDefaultTemplateId+"&templateTypeId="+templateTypeId);
		}
	});
	
});

