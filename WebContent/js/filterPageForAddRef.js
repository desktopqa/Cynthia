function initFilterPageFirst(filterId,filterName)
{
	if(filterId!=null)
	{
		$("#filterId").val(filterId);
		$("#timerId").val("");
	}
	
	$("#filter_manage_welcome").hide();
	$("#input_filter_name").val(filterName);
	$("#templateTypeSelect").val("0");
	$("#templates").empty();
	$("#fields").empty();
	$("#fields").append("<option value=''>---请选择---</option>");
	$("#conditions_table>tbody").empty();
	$("#template_user_restTemplateUserSelect").empty();
	$("#template_user_selectedTemplateUserSelect").empty();
	$("#timerName").val("");
	$("#mailTitle").val("[Cynthia]");
	$("#nameList").val("");
	$("#ccList").val("");
	$("#bccList").val("");
	//初始化月份
	$("#timer_div").css("display","none");	
	$("#timer_no").attr("checked",true);
	$("#month").empty();
	$("#date").empty();
	$("#day").empty();
	$("#hour").empty();
	$("#minute").empty();
	
	for(var i=1;i<=12;i++)
	{
		$("#month").append("<option value='"+i+"'>"+i+"</option>");
	}
	//初始化date
	for(var i=1;i<=31;i++)
	{
		$("#date").append("<option value='"+i+"'>"+i+"</option>");
	}
	
	//初始化周几
	for(var i=1;i<=7;i++)
	{
		$("#day").append("<option value='"+i+"'>"+i+"</option>");		
	}
	//初始化时间
	for(var i=0;i<24;i++)
	{
		$("#hour").append("<option value='"+i+"'>"+i+"</option>");
	}
	
	for(var i=0;i<60;i++)
	{
		$("#minute").append("<option value='"+i+"'>"+i+"</option>");
	}
}

function initFilterPage(filterId,type)
{
	if(filterId!=null)
	{
		$("#filter_manage_welcome").find("span").text("数据加载中......");
		$("#filter_manage_welcome").show();
		//$("#filter_manage_welcome").hide();
		var params = {filterId:filterId};
		$("#filterId").val(filterId);
		$.post("../filterManage/initFilterPage.jsp",params,onCompleteInitFilterPage,"xml");	
	}else
	{
		$("#filter_manage_welcome").show();
		//初始化月份
		for(var i=1;i<=12;i++)
		{
			$("#month").append("<option value='"+i+"'>"+i+"</option>");
		}
		//初始化date
		for(var i=1;i<=31;i++)
		{
			$("#date").append("<option value='"+i+"'>"+i+"</option>");
		}
	
		//初始化周几
		for(var i=1;i<=7;i++)
		{
			$("#day").append("<option value='"+i+"'>"+i+"</option>");		
		}
		//初始化时间
		for(var i=0;i<24;i++)
		{
			$("#hour").append("<option value='"+i+"'>"+i+"</option>");
		}
	
		for(var i=0;i<60;i++)
		{
			$("#minute").append("<option value='"+i+"'>"+i+"</option>");
		}
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
		$("#templates").append("<option value='"+localTemplateId+"' "+selected+">"+localTemplateName+"</option>");
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
			$("#conditions_table>tbody").append("<tr><td>"+content+"</td><td onclick='subCondition(this)'><span style=\"cursor:pointer;\"class=\"label label-important\">删除</span></td></tr>");
		}
	});
	
	//设置默认关注人分为已经关注的（template_user_selectedTemplateUserSelect）和未关注的（template_user_restTemplateUserSelect）
	$("#template_user_restTemplateUserSelect").empty();
	$(queryNode).find("unselectedusers").find("user").each(function(index,node){
		var userName = $(node).find("username").text();
		$("#template_user_restTemplateUserSelect").append("<option value='"+userName+"'>"+userName+"</option>");
	});
	
	$("#template_user_selectedTemplateUserSelect").empty();
	$(queryNode).find("selectedusers").find("user").each(function(index,node){
		var userName = $(node).find("username").text();
		$("#template_user_selectedTemplateUserSelect").append("<option value='"+userName+"'>"+userName+"</option>");
	});
	
	//设置分组信息
	$("#order_td").empty();
	$("#order_td").append($(queryNode).find("order").text());
	
	//查找当前记录或者修改日志
	$("#input_is_current").attr("checked",false);
	$("#input_is_history").attr("checked",false);
	$(queryNode).find("timerange").each(function(index,node){
		var nodeText = $(node).text();
		if(nodeText == "current")
		{
			$("#input_is_current").attr("checked",true);
		}
		if(nodeText == "history")
		{
			$("#input_is_history").attr("checked",true);
		}
	});
	
	//字段间关系
	$("#input_or").attr("checked",false);
	$("#input_and").attr("checked",false);
	eval("isAnd = "+$(queryNode).find("isAnd").text());
	
	if(isAnd)
	{
		$("#input_and").attr("checked",true);
		$("#input_or").attr("checked",false);
	}else
	{
		$("#input_and").attr("checked",false);
		$("#input_or").attr("checked",true);
	}
	
	$("#filter_manage_welcome").find("span").text("欢迎使用过滤器管理界面。");
	$("#filter_manage_welcome").hide();
}


function subCondition(row)
{
	var parent = row.parentNode;
	var index = parent.rowIndex;
	if(parent.innerText.indexOf("动作")!=-1)
	{
		//查找当前记录或者修改日志
		$("#input_is_current").attr("checked",true);
		$("#input_is_history").attr("checked",false);
	}
	document.getElementById("conditions_table").deleteRow(index);
}
	
function initTemplates()
{
	var templateTypeId = $("#templateTypeSelect").val();
	if(templateTypeId!="")
	{
		var params = {templateTypeId:templateTypeId};
		$.post('../filterManage/initTemplates.jsp',params,onCompleteInitTemplates,'xml');
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
		$.post('../filterManage/initFields.jsp',params,onCompleteInitFields,'xml');
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
	if(fieldId == "action_id" || fieldId == "action_time_range")  //选择动作默认选中查找修改日志
	{
		$("#input_is_history").attr("checked",true);
		$("#input_is_current").attr("checked",false);
	}
	var templateId = $("#templates").val();
	var params = {templateId:templateId,fieldId:fieldId};
	$.post('../filterManage/addField.jsp',params,onCompleteAddCondition,'xml');
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
			$("#conditions_table>tbody").append("<tr><td>"+content+"</td><td onclick='subCondition(this)'><span style=\"cursor:pointer;\"class=\"label label-important\">删除</span></td></tr>");
	}
}

function executeSubmitQueryRefer()
{
	if($("#templateTypeSelect").val()=="")
	{
		alert("请选择表单类型！");
		return;
	}
				
	if(trim($("#templates").val())=="")
	{
		alert("请选择表单");
		return;
	}
	
	var rootDoc = getXMLDoc();
	var queryNode = rootDoc.createElement("query");
				
	//deal with env
	var envNode = rootDoc.createElement("env");
	if(!$("#input_is_current").attr("checked")&&!$("#input_is_history").attr("checked"))
	{
		alert("查找当前记录或者历史记录选项必须选择一个.");
		return;
	}
				
	if($("#input_is_current").attr("checked") == "checked")
	{
		var timerangeNode = rootDoc.createElement("timerange");
		setTextContent(timerangeNode, "current");
		envNode.appendChild(timerangeNode);
	}
				
	if($("#input_is_history").attr("checked") == "checked")
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
				
	if(finalXml.length > 3000){
		alert('您的筛选条件过多！');
		return;
	}
	
//	filterXml = encodeAll(filterXml);
//	queryFilterData(finalXml);
//	$("#cfgRefQueryDiv").modal('show');
	$("#finalXml").val(finalXml);
	$("#previewForm").submit();
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
	if(show)
	{
		$("#orderFieldDiv").modal('show');
	}else
	{
		$("#orderFieldDiv").modal('hide');
	}
}

function showDefaultUser(show)
{
	if(show)
	{
		$("#defaultUserDiv").modal('show');
	}else
	{
		$("#defaultUserDiv").modal("hide");
	}
}

function showFilterTimer(show)
{
	if(show)
	{
		$("#filterTimerDiv").modal('show');
	}else
	{
		$("#filterTimerDiv").modal("hide");
	}
}

function enableSelectSearch()
{
	$("select").each(function(idx,select){
		if(!($(select).hasClass("multiLine")||$(select).hasClass('noSearch')))
		{
			$(select).select2();
		}
	});
}

$(function(){
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
});