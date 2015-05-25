function initFilterPageFirst(filterId,filterName)
{
	if(filterId){
		$("#filterId").val(filterId);
	}
	
	$("#timerId").val("");
	$("#filter_manage_welcome").hide();
	$("#main_content").show();
	$("#input_filter_name").val(filterName);
	$("#templateTypeSelect").val("0");
	$("#templates,#fields").empty();
	$("#fields").append("<option value=''>---请选择---</option>");
	$("#conditions_table>tbody").empty();
	$("#template_user_restTemplateUserSelect").empty();
	$("#template_user_selectedTemplateUserSelect").empty();
	$("#timerName,#nameList,#ccList,#bccList").val("");
	$("#mailTitle").val("[Cynthia][" + filterName + "]");
	//初始化月份
	$("#timer_div").hide();	
	$("#timer_no").attr("checked",true);
	
	setTimeValue("month", 1, 12,new Array());
	setTimeValue("date", 1, 31,new Array());
	setTimeValue("day", 1, 7,new Array());
	setTimeValue("hour", 0, 23,new Array());
	setTimeValue("minute", 0, 59,new Array());
	
	$("#main_content").find("input,select,textarea").removeAttr('disabled');
	$("#topSubmitDiv").show();
	enableSelectSearch();
}

function initFilterPage(filterId,type)
{
	if(filterId!=null)
	{
		$("#filter_manage_welcome").find("span").text("数据加载中......");
		$("#main_content").hide();
		$("#filter_manage_welcome").show();
		var params = {filterId:filterId};
		$("#filterId").val(filterId);
		$.ajax({
			url:'filterManage/initFilterPage.jsp',
			data:params,
			dataType:'xml',
			success:onCompleteInitFilterPage,
			error:function(data){
				cynthia.util.showInfoWin('error','过滤器错误!');
			}
		});
	}else
	{
		$("#main_content").hide();
		$("#filter_manage_welcome").show();
		
		setTimeValue("month", 1, 12,new Array());
		setTimeValue("date", 1, 31,new Array());
		setTimeValue("day", 1, 7,new Array());
		setTimeValue("hour", 0, 23,new Array());
		setTimeValue("minute", 0, 59,new Array());
	}
	$("#topSubmitDiv").show();
}


function onCompleteInitFilterPage(data,textStatus)
{
	var queryNode = $(data).find("query");
	eval("readonly="+$(queryNode).find("readonly").text());
	if($(queryNode).find("init").length>0)
	{
		initFilterPageFirst($(queryNode).children("filterid").text(),$(queryNode).children("filtername").text());		
		return;
	}
	//设置filterName
	var filterNameNode = $(queryNode).find("filterName");
	$("#input_filter_name").val("");
	$("#input_filter_name").val($(filterNameNode).text());
	document.title = $(filterNameNode).text()+'-Cynthia';
	//设置templateType
	var templateTypeId = $(queryNode).find("templateType").attr("id");
	$("#templateTypeSelect").val(templateTypeId);
	
	//设置表单templates
	var templateId = $(queryNode).children("template").attr("id");
	$("#templates").empty();
	$(queryNode).find("templates").find("template").each(function(index,node){
		var selected = "";
		var localTemplateId = $(node).find("id").text();
		var localTemplateName = $(node).find("name").text();
		if(localTemplateId == templateId)
			selected = "selected";
		$("#templates").append("<option value='" + localTemplateId + "' " + selected + ">" + localTemplateName + "</option>");
		selected = "";
	});
	
	//设置表单fields
	$("#fields").empty();
	$("#fields").append("<option value=''>---请选择---</option>");
	$(queryNode).find("fields").find("field").each(function(index,node){
		var localFieldId = $(node).find("id").text();
		var localFieldName = $(node).find("name").text();
		$("#fields").append("<option value='"+localFieldId+"'>"+localFieldName+"</option>");
	});	
	
	//设置condition信息
	$("#conditions_table>tbody").empty();
	$(queryNode).find("template").find("conditions").find("field").each(function(index,node){
		var content = $(node).find("fieldContent").text();
		if(content!=null&&content!="")
		{
			$("#conditions_table>tbody").append("<tr><td>"+content+"</td><td class='filter_field_del' onclick='subCondition(this)'><span style=\"cursor:pointer;\"class=\"label label-important\">删除</span></td></tr>");
		}
	});
	enableSelectSearch();
	//设置默认关注人分为已经关注的（template_user_selectedTemplateUserSelect）和未关注的（template_user_restTemplateUserSelect）
	$("#template_user_restTemplateUserSelect").empty();
	$(queryNode).find("unselectedusers").find("user").each(function(index,node){
		var userName = $(node).find("username").text();
		var userAlias = $(node).find("useralias").text();
		$("#template_user_restTemplateUserSelect").append("<option value='"+userName+"'>"+userAlias+"</option>");
	});
	
	$("#template_user_selectedTemplateUserSelect").empty();
	$(queryNode).find("selectedusers").find("user").each(function(index,node){
		var userName = $(node).find("username").text();
		var userAlias = $(node).find("useralias").text();
		$("#template_user_selectedTemplateUserSelect").append("<option value='"+userName+"'>"+userAlias+"</option>");
	});
	
	//设置分组信息
	$("#order_td").empty();
	$("#order_td").append($(queryNode).find("order").text());
	
	//查找当前记录或者修改日志
	$("#input_is_history").attr("checked",false);
	$(queryNode).find("timerange").each(function(index,node){
		var nodeText = $(node).text();
		if(nodeText == "current"){
			$("#input_is_current").attr("checked",true);
		}
		if(nodeText == "history"){
			$("#input_is_history").attr("checked",true);
		}
	});
	
	//字段间关系
	eval("isAnd = "+$(queryNode).find("isAnd").text());
	if(isAnd) {
		$("#input_and").attr("checked", true);
	}else{
		$("#input_or").attr("checked", true);
	}
	
	//定时器管理
	$("#timer_div").hide();	
	$("#timer_no").attr("checked",true);
	$("#timerId,#timerName,#mailTitle,#nameList,#ccMailList,#bccMailList").val("");
	$("#month,#date,#day,#hour,#minute").empty();
	
	eval("isTimer = "+$(queryNode).find("istimer").text());
	if(isTimer){
		$("#timer_yes").attr("checked",true);
		$("#timer_no").attr("checked",false);
		//设置定时器参数
		var timerNode = $(queryNode).find("timer");
		var paramsNode = $(timerNode).find("params");
		$("#timerId").val($(timerNode).find("timerId").text());
		$("#timerName").val(timerNode.find("timername").text());
		$("#mailTitle").val(paramsNode.find("title").text());
		$("#nameList").val(paramsNode.find("mailList").text());
		$("#ccMailList").val(paramsNode.find("ccMailList").text());
		$("#bccMailList").val(paramsNode.find("bccMailList").text());
		//设置时间参数
		var monthStr = $(timerNode).find("month").text();
		var dateStr = $(timerNode).find("date").text();
		var dayStr = $(timerNode).find("day").text();
		var hourStr = $(timerNode).find("hour").text();
		var minuteStr = $(timerNode).find("minute").text();
		
		setTimeValue("month", 1, 12,monthStr.split(","));
		setTimeValue("date", 1, 31,dateStr.split(","));
		setTimeValue("day", 1, 7,dayStr.split(","));
		setTimeValue("hour", 0, 23,hourStr.split(","));
		setTimeValue("minute", 0, 59,minuteStr.split(","));
		
		$("#timer_div").css("display","block");	
	}else{	
		setTimeValue("month", 1, 12,new Array());
		setTimeValue("date", 1, 31,new Array());
		setTimeValue("day", 1, 7,new Array());
		setTimeValue("hour", 0, 23,new Array());
		setTimeValue("minute", 0, 59,new Array());
		
		$("#timer_yes").attr("checked",false);
		$("#timer_no").attr("checked",true);
		$("#timer_div").css("display","none");		
		$("#timerId").val("");
	}
	
	if(readonly){
		$("#filter_content").find("input,select,textarea,button").attr('disabled','disabled');
		$("#filter_content").find("li").removeAttr('onclick');
		$("#main_content").find("td").removeAttr('onclick');
	}else{
		$("#filter_content").find("input,select,textarea,button").removeAttr('disabled');
	}
	
	$("#filter_manage_welcome").find("span").text("欢迎使用过滤器管理界面。");
	$("#filter_manage_welcome").hide();
	$("#main_content").show();
}

function changeTimerDiv(param)
{
	if(param)
		$("#timer_div").show("slow");
	else
		$("#timer_div").hide("slow");
}


function subCondition(row)
{
	var parent = row.parentNode;
	var index = parent.rowIndex;
	document.getElementById("conditions_table").deleteRow(index);
	fieldId = $(parent).find("td:eq(0) div:eq(0)").attr("fieldid");
	fieldName = $(parent).find("td:eq(0) div:eq(0)").attr("fieldname");
	$("#fields").append("<option value=" + fieldId + "> " + fieldName + "</option>");
	setFindCurrent();
}
	
//查找当前记录或者修改日志
function setFindCurrent()
{
	var isCurrent = true;
	$.each($("#conditions_table div"),function(index,node){
		var fieldId = $(node).attr("fieldid");
		if(fieldId == "action_time_range" || fieldId == "action_id" || fieldId == "log_create_user"){
			isCurrent = false;
		}
	});
	
	$("#input_is_current").attr("checked",isCurrent);
	$("#input_is_history").attr("checked",!isCurrent);
}

function initTemplates()
{
	var templateTypeId = $("#templateTypeSelect").val();
	if(templateTypeId!="")
	{
		var params = {templateTypeId:templateTypeId};
		$.post('filterManage/initTemplates.jsp',params,onCompleteInitTemplates,'xml');
	}
}

function onCompleteInitTemplates(data,textStatus){
	var error = $(data).find("root").find("isError").text();
	if(error=="true")
	{
		alert("服务器内部错误,请稍后再试");
	}else
	{
		clearTemplates();
		clearFields();
		clearConditionTable();
		clearFocusUsers();
		$("#templates").append("<option value=''>---请选择---</option>");
		$(data).find("templates").find("template").each(function(index,node){
			var templateId = $(node).find("id").text();
			var templateName = $(node).find("name").text();
			$("#templates").append("<option value='"+templateId+"'>"+templateName+"</option>");
		});
	}
}

function initFields()
{	
	var templateId = $("#templates").val();
	if(templateId!="")
	{
		var params = {templateId : templateId};
		$.post('filterManage/initFields.jsp',params,onCompleteInitFields,'xml');
	}
}

function onCompleteInitFields(data,textStatus)
{
	eval("var error="+$(data).find("root").find("isError").text());
	if(error)
	{
		alert("服务器内部错误,请稍后再试");
	}else
	{
		clearFields();
		clearConditionTable();
		clearFocusUsers();
		$("#fields").append("<option value=''>---请选择---</option>");
		$(data).find("fields").find("field").each(function(index,node){
			var fieldId = $(node).find("id").text();
			var fieldName = $(node).find("name").text();
			$("#fields").append("<option value='"+fieldId+"'>"+fieldName+"</option>");
		});
		
		$(data).find("users").find("user").each(function(index,node){
			var user = $(node).text();
			$("#template_user_restTemplateUserSelect").append("<option value='"+user+"'>"+user+"</option>");
		});
		
		$("#order_td").empty();
		$("#order_td").append($(data).find("order").text());
	}
}

function addCondition()
{	
	var fieldId = $("#fields").val();
	var templateId = $("#templates").val();
	var params = {templateId:templateId,fieldId:fieldId};
	$.post('filterManage/addField.jsp',params,onCompleteAddCondition,'xml');
	$("#fields option[value="+ fieldId +"]").remove();
	$("#fields").val("");
}

function onCompleteAddCondition(data,textStatus)
{
	eval("var error="+$(data).find("root").find("isError").text());
	if(error)
	{
		alert("服务器内部错误,请稍后再试");
	}else
	{
		var content = $(data).find("field").text();
		if(content!="")
			$("#conditions_table>tbody").append("<tr><td>"+content+"</td><td class='filter_field_del' onclick='subCondition(this)'><span style=\"cursor:pointer;\"class=\"label label-important\">删除</span></td></tr>");
	}
	setFindCurrent();
	enableSelectSearch();
}

function getFilterParams(){
	var rootDoc = getXMLDoc();
	var queryNode = rootDoc.createElement("query");
				
	//deal with env
	var envNode = rootDoc.createElement("env");
	if(!$("#input_is_current").prop("checked")&&!$("#input_is_history").prop("checked"))
	{
		alert("查找当前记录或者历史记录选项必须选择一个.");
		return;
	}
				
	if($("#input_is_current").prop("checked") == true)
	{
		var timerangeNode = rootDoc.createElement("timerange");
		setTextContent(timerangeNode, "current");
		envNode.appendChild(timerangeNode);
	}
				
	if($("#input_is_history").prop("checked") == true)
	{
		var timerangeNode = rootDoc.createElement("timerange");
		setTextContent(timerangeNode, "history");
		envNode.appendChild(timerangeNode);
	}
				
	queryNode.appendChild(envNode);
				
	//deal with template type
	var templateTypeNode = rootDoc.createElement("templateType");
	var selectedTemplateTypeId = $("#templateTypeSelect").val();
	var selectedTemplateTypeName = $("#templateTypeSelect").find("option:selected").text();
				
	templateTypeNode.setAttribute("id", selectedTemplateTypeId);
	templateTypeNode.setAttribute("name", selectedTemplateTypeName);
				
	queryNode.appendChild(templateTypeNode);
	
	var templateNode = rootDoc.createElement("template");
	var templateId = $("#templates").val();
	var templateName = $("#templates").find("option:selected").text();
							
	templateNode.setAttribute("id", templateId);
	templateNode.setAttribute("name", templateName);
							
	//where node
	var whereNode = rootDoc.createElement("where");
			
	var mainTableRows = $("#conditions_table > tbody > tr").each(function(index,node){
		var td = $(node).find("td").first();
		if($(td).text()!=null&&$(td).text()!="")
		{
			var divId = $(td).find("div").attr("id");
			if($("#"+divId).html().indexOf("普通设置")<0)
			{
				setXMLResult(divId.substring(0, divId.length - 6), false);
			}else
			{
				if($("#"+divId).attr("xml")==null)
				{
					setXMLResult(divId.substring(0, divId.length - 6), true);
				}
			}
			var xml = $("#"+divId).attr("xml");
			if(xml==null)
				return;
			if(whereNode.childNodes.length>0)
			{
				var conditionNode = rootDoc.createElement("condition");
				setTextContent(conditionNode,($("#input_and").is(":checked") ?"AND":"OR"));
				whereNode.appendChild(conditionNode);
			}
			var whereInnerNode = rootDoc.createElement("whereInner");
			setTextContent(whereInnerNode,xml);
			whereNode.appendChild(whereInnerNode);
		}
	});
	
	templateNode.appendChild(whereNode);
	
	//display node
	var displayNode = rootDoc.createElement("display");
	
	var idNode = rootDoc.createElement("field");
	idNode.setAttribute("id","id");
	idNode.setAttribute("name","编号");
	idNode.setAttribute("type","id");
	displayNode.appendChild(idNode);
	
	var titleNode = rootDoc.createElement("field");
	titleNode.setAttribute("id","title");
	titleNode.setAttribute("name","标题");
	titleNode.setAttribute("type","title");
	displayNode.appendChild(titleNode);
	
	var statusNode = rootDoc.createElement("field");
	statusNode.setAttribute("id","status_id");
	statusNode.setAttribute("name","状态");
	statusNode.setAttribute("type","status_id");
	displayNode.appendChild(statusNode);
	
	var assignUserNode = rootDoc.createElement("field");
	assignUserNode.setAttribute("id","assign_user");
	assignUserNode.setAttribute("name","指派人");
	assignUserNode.setAttribute("type","assign_user");
	displayNode.appendChild(assignUserNode);
	
	var lastModifyTimeNode = rootDoc.createElement("field");
	lastModifyTimeNode.setAttribute("id","last_modify_time");
	lastModifyTimeNode.setAttribute("name","修改时间");
	lastModifyTimeNode.setAttribute("type","last_modify_time");
	displayNode.appendChild(lastModifyTimeNode);
	
	var createUserNode = rootDoc.createElement("field");
	createUserNode.setAttribute("id","create_user");
	createUserNode.setAttribute("name","创建人");
	createUserNode.setAttribute("type","create_user");
	displayNode.appendChild(createUserNode);
	
	var createTimeNode = rootDoc.createElement("field");
	createTimeNode.setAttribute("id","create_time");
	createTimeNode.setAttribute("name","创建时间");
	createTimeNode.setAttribute("type","create_time");
	displayNode.appendChild(createTimeNode);
	
	templateNode.appendChild(displayNode);
		
	//order node
	//templateNode.appendChild(rootDoc.createElement("order"));
	//order node
	var orderNode = getOrderResultXML("order_" + templateId);
	if(orderNode == null)
		templateNode.appendChild(rootDoc.createElement("order"));
	else
		templateNode.appendChild(orderNode);
	
	queryNode.appendChild(templateNode);
	rootDoc.appendChild(queryNode);
				
	var finalXml = getDocXML(rootDoc);
	
	while(true)
	{
		var beginIndex = finalXml.indexOf("<whereInner>");
		var endIndex = finalXml.indexOf("</whereInner>");
					
		if(beginIndex < 0 || endIndex < 0)
			break;
					
		var finalChildXml = finalXml.substring(beginIndex, endIndex + 13);
					
		var finalChildInnerXml = finalXml.substring(beginIndex + 12, endIndex);
		finalChildInnerXml = replaceAll(finalChildInnerXml, "&lt;", "<");
		finalChildInnerXml = replaceAll(finalChildInnerXml, "&gt;", ">");
		finalChildInnerXml = replaceAll(finalChildInnerXml, "&apos;", "'");
		finalChildInnerXml = replaceAll(finalChildInnerXml, "&quot;", "\"");
		finalChildInnerXml = replaceAll(finalChildInnerXml, "&amp;", "&");
					
		finalXml = finalXml.replace(finalChildXml, finalChildInnerXml);
	}
	
	var betweenField;
	
	var inputAnd = $("#input_and").is(":checked");
	if(inputAnd)
		betweenField = "and";
	
	var params;
	var filterId = $("#filterId").val();
	//定时器部分
	var isTimer = $("#timer_yes")[0].checked;
	if(isTimer)
	{
		var timerName = $("#timerName").val();
		var date = $("#date").val();
		var day = $("#day").val();
		if(timerName==null||timerName=="")
		{
			alert("请填写定时器名称!");
			return;
		}
		if((day==null&&date==null)||(day==""&&date==""))
		{
			alert("[日期]和[周几]至少要选中一个选项");
			return;
		}
		
		var	hourSL	= document.getElementById( "hour" );
		if ( hourSL!=null && hourSL.selectedIndex<0 )
		{
			//选中第一个hour
			hourSL.options[0].selected=true;
		}
		
		//minute
		var	minuteSL	= document.getElementById( "minute" );
		if ( minuteSL!=null && minuteSL.selectedIndex<0 )
		{
			//选中第一个minute
			minuteSL.options[0].selected=true;
		}
		var mailTitle = $("#mailTitle").val();
		var nameList = $("#nameList").val();
		var ccMailList = $("#ccMailList").val();
		var bccMailList = $("#bccMailList").val();
		var month = $("#month").val();
		var hour = $("#hour").val();
		var minute = $("#minute").val();
		var timerId = $("#timerId").val();
		date = $("#date").val();
		day = $("#day").val();
		params = {
			filterName:$("#input_filter_name").val(), 
			focusUsers:getFocusUsers(),
			unfocusUsers:getUnFocusUsers(), 
			betweenField:betweenField,
			searchConfig:finalXml,
			timerId : timerId,
			isTimer : true,
			id:filterId,
			nodeId : $("#nodeId").val(),
			title : $("#mailTitle").val(),
		    mailList : $("#nameList").val(),
		    ccMailList : $("#ccMailList").val(),
		    bccMailList : $("#bccMailList").val(),
		    month : $("#month").val(),
		    hour : $("#hour").val(),
		    minute : $("#minute").val(),
			timerName : $("#timerName").val(),
			date : $("#date").val(),
			day : $("#day").val()
		};
		
	}else
	{
		var timerId = $("#timerId").val();
		params = {
			filterName:$("#input_filter_name").val(), 
			focusUsers:getFocusUsers(),
			unfocusUsers:getUnFocusUsers(), 
			betweenField:betweenField,
			searchConfig:finalXml,
			id:filterId,
			timerId:timerId,
			nodeId : $("#nodeId").val(),
			isTimer : false
			};
	}
	return params;
}

function executeSubmit(type)
{
	if($("#templateTypeSelect").val()==""){
		alert("请选择表单类型！");
		return;
	}
				
	if(trim($("#input_filter_name").val()) == ""){
		alert("请输入过滤器名称！");
		return;
	}
	if(trim($("#templates").val())==""){
		alert("请选择表单");
		return;
	}
	
	var params = getFilterParams();
	
	if(type==1){
		$.post("filterManage/createSearch_save.jsp",params,onCompleteAddFilter,"xml");
		
		//从首页跳转过来则要刷新首页数据
		if(window.opener && window.opener.grid){
			window.opener.grid.refreshGrid();
			window.opener.initFilterMenu();
		}
	}else if(type == 2){
		$('#preview-header').text('过滤器预览--' + params.filterName);
		$('#previewFilterDiv').modal('show');
		queryFilterData(params.searchConfig);
	}
}

function saveFilterConditions(){
	var params = getFilterParams();
	$.post("filterManage/createSearch_save.jsp",params);
}

function getUnFocusUsers(){
	var users = new Array(); 
    $("#template_user_restTemplateUserSelect option").each(function () {
        users.push($(this).val());
    });
    return users;
}

function getFocusUsers(){
	var users = new Array(); 
    $("#template_user_selectedTemplateUserSelect option").each(function () {
        users.push($(this).val());
    });
    return users;
}

function onCompletePreviewFilter(data,textStatus)
{
	eval("var error="+$(data).find("root").find("isError").text());
	if(!error)
	{
		var filterId = $(data).find("filterId").text();
		$("#filterId").val(filterId);
		window.open("index.jsp?filterId="+filterId);
	}
}

function onCompleteAddFilter(data,textStatus)
{
	eval("var error="+$(data).find("root").find("isError").text());
	if(error)
	{
		alert("服务器内部错误，请稍后再试！");
	}else
	{
		if($("#filterId").val()!=null&&$("#filterId").val()!="")
			alert("过滤器修改成功");
		else
			alert("过滤器添加成功！");
		var filterId = $(data).find("filterId").text();
		window.location.href="editFilter.html?filterId="+filterId; 
	}
}

function clearTemplates()
{
	$("#templates").empty();
}

function clearFields()
{
	$("#fields").empty();
}

function clearConditionTable()
{
	$("#conditions_table > tbody").empty();
}

function dealWithButton()
{
	if($("#submitButton")[0].disabled)
	{
		$("#submitButton")[0].enable();
	}else
	{
		$("#submitButton")[0].disable();
	}
}

function clearFocusUsers()
{
	$("#template_user_restTemplateUserSelect").empty();
	$("#template_user_selectedTemplateUserSelect").empty();
}

function showAdvance()
{
	var display = $("#adv_tr").css("display");
	if(display == "none")
	{
		$("#adv_tr").css("display","block");
	}else
	{
		$("#adv_tr").css("display","none");
	}
}

function showOrderArea(show)
{
	if(show){
		$("#orderFieldDiv").modal('show');
	}else{
//		saveFilterConditions();
		$("#orderFieldDiv").modal("hide");
	}
}

function showDefaultUser(show)
{
	if(show){
		$("#defaultUserDiv").modal('show');
	}else{
//		saveFilterConditions();
		$("#defaultUserDiv").modal("hide");
	}
}

function showFilterTimer(show)
{
	var isTimer = $("#timer_yes")[0].checked;
	if(isTimer)
	{
		var timerName = $("#timerName").val();
		var date = $("#date").val();
		var day = $("#day").val();
		if(timerName==null||timerName=="")
		{
			alert("请填写定时器名称!");
			return;
		}
		if((day==null&&date==null)||(day==""&&date==""))
		{
			alert("[日期]和[周几]至少要选中一个选项");
			return;
		}
	}
	
	if(show)
	{
		$("#filterTimerDiv").modal('show');
	}else
	{
//		saveFilterConditions();
		$("#filterTimerDiv").modal("hide");
	}
}

function bindHoverEvent(){
	//a span hover 提示框
	$(document).delegate('a','hover',function(e){
		if(e.type=='mouseenter')
		   {
				if($(this).attr("realValue"))
				{
					this.myTitle = $(this).attr("realValue");
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
	  
	$(document).delegate("a,span","mousemove",function(e){
		  if($("#ttip").length>0)
		  {
			$("#ttip").css({"top":(e.pageY+18)+"px","left":(e.pageX+10)+"px"});
		  }
	});
	
}

function onWindowResize()
{
	$("#left_tree").height($(window).height() - 50);
}

$(function(){
	bindHoverEvent();
	var filterId = request("filterId");
	var nodeId   = request("nodeId");
	if(filterId != "")
	{
		$("#filterId").val(filterId);
		initFilterPage(filterId);
	}
	if(nodeId != "")
	{
		$("#nodeId").val(nodeId);
	}
	onWindowResize();
	$(window).resize(onWindowResize);
});