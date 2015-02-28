
function setTypeDiv(callback){
	var type = $("#stat_type").val();
	$.each($("#stat_all .stat"),function(index,node){
		if($(node).hasClass("by_" + type)){
			$(node).show();
		}else{
			$(node).hide();
		}
	});
	
	if(type === "task"){
		$("#chartType").val('area');
	}else{
		$("#chartType").val('pie');
	}
	
	enableSelectSearch();
	changeTips();
	initTemplate(callback);
}

function initTemplate(callback)
{
	$("#stat_info_body").empty();
	clearConditionTable();
	var type = $("#stat_type").val();
	var param = "";
	if(type !== "" && type === "task"){
		param += "templateTypeId=3";
	}
	
	$.ajax({
		url: base_url + 'template/getAllTemplates.do',
		type:'POST',
		dataType:'json',
		data:param,
		success:function(data){
			$("#templates").empty();
			$("#templates").append("<option value=>--请选择--</option>");
			for(var key in data){
				$("#templates").append("<option value=" + key + ">" + data[key] + "</option>");
			}
			$("#templates").val('');
			$("#stat_options").empty();
			enableSelectSearch();
			if(callback){
				callback();
			}
		}
	});
}

function initAllTemplateTask(templateId)
{
	var data;
	var afterInit = function(data){
		$("#stat_task").empty();
		$("#stat_task").append("<option value=>--请选择--</option>");
		for(var key in data){
			$("#stat_task").append("<option value=" + key + "> " + data[key] + "</option>");
		}
		$("#stat_task").val('');
		enableSelectSearch();
	};
	
	$.ajax({
		url:base_url + 'template/getTmpDataIdTitle.do',
		type:'POST',
		dataType:'json',
		data:{'templateId':templateId},
		success: afterInit(response),
		error:afterInit(eval('(' + response.responseText + ')'))
	});
}

function initTemplateBugField(templateId,callback)
{
	$.ajax({
		url:base_url + 'template/getTemplateAllBugField.do',
		type:'POST',
		dataType:'json',
		data:{'templateId':templateId},
		success: function(data){
			$("#stat_task_bug_field").empty();
			if(data.length == 0){
				showInfoWin('error',"该表单下没有可统计趋势的字段!");
				return;
			}
			$("#stat_task_bug_field").append("<option value=''>--请选择bug字段--</option>");
			for(var key in data){
				$("#stat_task_bug_field").append("<option value='" + data[key].first + "' selected> " + data[key].second + "</option>");
			}
			$("#stat_task_bug_field").val('');
			enableSelectSearch();
			//选择表单下所有任务
			initAllTemplateTask(templateId);
			if(callback)
				callback();
		}
	});
}

function setTaskBugStatus()
{
	var taskBugField = $("#stat_task_bug_field").val();
	var templateId = $("#templates").val();
	
	$.ajax({
		url:base_url + 'bugstatistic/getTaskBugStatus.do',
		type:'POST',
		dataType:'json',
		data:{'templateId':templateId,'taskBugField':taskBugField},
		success: function(data){
			$("#stat_options").empty();
			if(data.length == 0){
				showInfoWin('error',"没有可统计项!");
				return;
			}
			for(var key in data){
				$("#stat_options").append("<option value=" + data[key].fieldId + "> " + data[key].fieldName + "</option>");
			}
		},
		error:function(data){
			alert("error");
		}
	});
}

//初始化统计字段选项
function initStatFieldOptions(fieldId)
{
	$("#stat_info_body").empty();
	if(!fieldId)
		fieldId = $("#stat_field").val();
	if(!fieldId)
		return;
	
	var templateId = $("#templates").val();
	if(!templateId)
		return;
	$.ajax({
		url:base_url + 'bugstatistic/getFieldOption.do',
		type:'POST',
		dataType:'json',
		data:{'templateId':templateId,'fieldId':fieldId},
		success: function(data){
			$("#stat_options").empty();
			if(data.length == 0){
				showInfoWin('error',"没有可统计项!");
				return;
			}
			for(var key in data){
				$("#stat_options").append("<option value=" + data[key].fieldId + "> " + data[key].fieldName + "</option>");
			}
		},
		error:function(data){
			alert("error");
		}
	});
}


function initTemplateFields(callback)
{
	var type = $("#stat_type").val();
	if(!type)
		return;
	var templateId = $("#templates").val();
	$("#stat_options").empty();
	$("#stat_info_body").empty();
	
	if( type === "task"){
		//设置该任务bug字段
		initTemplateBugField(templateId,function(){initFilterFields(callback);});
	}else if(type === "model"){
		initTemplateStatField(templateId,function(){initFilterFields(callback);});
	}else if(type === "person"){
		initTemplateRoles(function(){initFilterFields(callback);});
	}
}

function initTemplateStatField(templateId,callback)
{
	$.ajax({
		url:base_url + 'bugstatistic/getStatisticField.do',
		type:'POST',
		dataType:'json',
		data:{'templateId':templateId},
		success: function(data){
			$("#stat_field").empty();
			$("#stat_field").append("<option value=>--请选择--</option>");
			for(var key in data){
				$("#stat_field").append("<option value=" + data[key].fieldId + "> " + data[key].fieldName + "</option>");
			}
			if(callback){
				callback();
			}
		}
	});
}

//添加统计选项
function addOptions()
{
	var allNodes = $("#stat_options").find("option:checked");
	if(allNodes.length === 0)
		return;
	else if(allNodes.length == 1){
		//只选择一项
		var node = $(allNodes[0]);
		var id= node.val() + "|" + $.trim(node.text());
		addToRight(id,node.text(),node.text());
		node.remove();
	}else{
		var name = prompt("请输入统计显示列名", ""); //将输入的内容赋给变量 name ，  
        if (name)//如果返回的有内容  
        {  
        	var id = "";
        	var value = "";
        	for(var i = 0 ; i < allNodes.length; i ++){
        		//只选择一项
        		var node = $(allNodes[i]);
        		if(i != 0){
        			id += ",";
        			value += ",";
        		}
        		id += node.val() + "|" + $.trim(node.text());
        		value += "[" + $.trim(node.text()) + "]";
        		node.remove();
        	}
        	addToRight(id,name,value);
        }else
        {
        	alert("没有正确输入统计显示列名,操作返回!");
            return;
        }
	}
}

//添加统计选项
function addAllOptions()
{
	var allNodes = $("#stat_options").find("option");
	for(var i = 0 ; i < allNodes.length; i ++){
		var node = $(allNodes[i]);
		var id= node.val() + "|" + $.trim(node.text());
		addToRight(id,node.text(),node.text());
		node.remove();
	}
}

function addToRight(statId,statName,statContent){
	$("#stat_info_body").append("<tr id='" + statId + "'>" +
								"<td noWrap>" +statName +" </td>" +
								"<td>" +statContent +" </td>" +
								"<td noWrap><a href=\"#\" onclick=\"removeStatOptions(this);\">移除</a></td>" +
								"</tr>");
}

function removeStatOptions(node)
{
	var trNode = $(node).parent().parent();
	var trId = $(node).parent().parent().attr("id");
	var trIdArr = trId.split(",");
	if(trIdArr.length > 0 )
	{
		for(var i in trIdArr)
		{
			var sIdArr = trIdArr[i].split("|");
			appendOption("stat_options" , sIdArr[0], sIdArr[1]);
		}
	}
	
	trNode.remove();
}

function appendOption(appId,optId,optVal)
{
	$("#" + appId).append("<option value=" + optId + ">" + optVal + "</option>");	
}


////////////按角色解决统计////////////////////////
function initTemplateRoles(callback)
{
	var templateId = $("#templates").val();
	$.ajax({
		url:base_url + 'flow/getRoleByTemplate.do',
		type:'POST',
		dataType:'json',
		data:{'templateId':templateId},
		success: function(data){
			$("#stat_role").empty();
			$("#stat_role").append("<option value=>--请选择--</option>");
			for(var key in data){
				$("#stat_role").append("<option value=" + data[key].fieldId + "> " + data[key].fieldName + "</option>");
			}
			if(callback){
				callback();
			}
		}
	});
}

function initRoleActions()
{
	var templateId = $("#templates").val();
	var roleId = $("#stat_role").val();
	$.ajax({
		url:base_url + 'flow/getActionByRole.do',
		type:'POST',
		dataType:'json',
		data:{'templateId':templateId,'roleId':roleId},
		success: function(data){
			$("#stat_action").empty();
			for(var key in data){
				$("#stat_action").append("<option value=" + data[key].fieldId + "> " + data[key].fieldName + "</option>");
			}
		},
		error:function(data){
			alert("error");
		}
	});
}
////////////按角色解决统计结束/////////////////////
//保存统计
function saveStat()
{
	var statName = $("#stat_name").val();
	if(statName === ""){
		alert("统计名称不能为空!");
		return;
	}
	
	var statId = $("#stat_id").val();
	
	var statType = $("#stat_type").val();
	if(statType === ""){
		alert("请选择统计类型!");
		return;
	}
	
	var templateId = $("#templates").val();
	if(templateId === ""){
		alert("请选择统计表单!");
		return;
	}
	
	var statTaskBugField = $("#stat_task_bug_field").val();
	if(statType === "task")
	{
		if($("#start_time").val() === "" && $("#time_type").val() === ""){
			alert("请至少选择起始时间范围或时间类型字段!");
			return;
		}
		if( statTaskBugField === ""){
			alert("请设置bug字段!");
			return;
		}
		
	}	
	
	var statRole = $("#stat_role").val();
	var statAction = $("#stat_action").val();
	if(statType === "person"){
		if(statRole === ""){
			alert("请选择角色!");
			return;
		}
		if(statAction === ""){
			alert("请定义解决动作!");
			return;
		}
	}
	
	var statField = $("#stat_field").val();
	if(statType === "model" && statField === ""){
		alert("请选择统计字段!");
		return;
	}
	
	
	var rootDoc = getXMLDoc();
	var rootNode = rootDoc.createElement("root");
	var typeNode = rootDoc.createElement("type");
	setTextContent(typeNode,statType);
	rootNode.appendChild(typeNode);
	
	var nameNode = rootDoc.createElement("name");
	setTextContent(nameNode,statName);
	rootNode.appendChild(nameNode);
	
	var templateIdNode = rootDoc.createElement("templateId");
	setTextContent(templateIdNode,templateId);
	rootNode.appendChild(templateIdNode);
	
	//时间范围
	var timeRangeNode = rootDoc.createElement("timeRange");
	var timeTypeNode = rootDoc.createElement("timeType");
	setTextContent(timeTypeNode,$("#time_type").val());
	timeRangeNode.appendChild(timeTypeNode);
	
	var startTimeNode = rootDoc.createElement("startTime");
	setTextContent(startTimeNode,$("#start_time").val());
	timeRangeNode.appendChild(startTimeNode);
	
	var endTimeNode = rootDoc.createElement("endTime");
	setTextContent(endTimeNode,$("#end_time").val());
	timeRangeNode.appendChild(endTimeNode);
	rootNode.appendChild(timeRangeNode);
	//时间范围结束 
	
	//按任务
	var taskNode = rootDoc.createElement("task");
	var taskIdNode = rootDoc.createElement("taskId");
	setTextContent(taskIdNode,$("#stat_task").val());
	taskNode.appendChild(taskIdNode);
	var statTaskBugFieldNode = rootDoc.createElement("taskFieldId");
	setTextContent(statTaskBugFieldNode,statTaskBugField);
	taskNode.appendChild(statTaskBugFieldNode);
	rootNode.appendChild(taskNode);
	
	//按人员角色 
	var personNode = rootDoc.createElement("person");
	var roleIdNode = rootDoc.createElement("roleId");
	setTextContent(roleIdNode,statRole);
	personNode.appendChild(roleIdNode);
	var roleActionIdsNode = rootDoc.createElement("roleActionIds");
	setTextContent(roleActionIdsNode,statAction);
	personNode.appendChild(roleActionIdsNode);
	var containCurAssignNode = rootDoc.createElement("containCurAssign");
	setTextContent(containCurAssignNode,$("input[type=radio][name=containCurAssign]:checked").attr("value"));
	personNode.appendChild(containCurAssignNode);
	rootNode.appendChild(personNode);
	
	//按模块
	var modelNode = rootDoc.createElement("model");
	var modelfieldIdNode = rootDoc.createElement("modelfieldId");
	setTextContent(modelfieldIdNode,statField);
	modelNode.appendChild(modelfieldIdNode);
	rootNode.appendChild(modelNode);
	
	
	//查询条件
	var queryConditionXml = getQueryConditionXml();
	var queryConditionNode = rootDoc.createElement("queryCondition");
	queryConditionNode.setAttribute("betweenField",$("input[type=radio][name=betweenField]:checked").val());
	setTextContent(queryConditionNode,queryConditionXml);
	rootNode.appendChild(queryConditionNode);
	
	//统计条件开始
	var statsNode = rootDoc.createElement("stats");
	$.each($("#stat_info_body").find("tr"),function(index,node){
		var statNode = rootDoc.createElement("stat");
		var statValue = "";
		statValue += $.trim($(node).find("td:eq(0)").text());
		
		var statId = $(node).attr("id");
		var statIdArr = statId.split(",");
		for(var i = 0 ; i < statIdArr.length ; i ++){
			statValue += "|";
			statValue += $.trim(statIdArr[i].split("|")[0]);
		}
		statNode.setAttribute("statId",statId);
		statNode.setAttribute("statOptions",$.trim($(node).find("td:eq(1)").text()));
		setTextContent(statNode,statValue);
		statsNode.appendChild(statNode);
	});
	
	rootNode.appendChild(statsNode);
	//统计条件结束
	
	//发信时间
	var mailTimeNode = rootDoc.createElement("mailTime");
	//是否定时发信
	var isSendMailNode = rootDoc.createElement("isSendMail");
	setTextContent(isSendMailNode,$("input[type=radio][name=sendMail]:checked").val());
	mailTimeNode.appendChild(isSendMailNode);
	//月份
	var monthNode = rootDoc.createElement("month");
	setTextContent(monthNode,$("#month").val());
	mailTimeNode.appendChild(monthNode);
	//日期
	var dateNode = rootDoc.createElement("date");
	setTextContent(dateNode,$("#date").val());
	mailTimeNode.appendChild(dateNode);
	//周几
	var weekNode = rootDoc.createElement("week");
	setTextContent(weekNode,$("#week").val());
	mailTimeNode.appendChild(weekNode);
	//时钟
	var hourNode = rootDoc.createElement("hour");
	setTextContent(hourNode,$("#hour").val());
	mailTimeNode.appendChild(hourNode);
	//分钟
	var minuteNode = rootDoc.createElement("minute");
	setTextContent(minuteNode,$("#minute").val());
	mailTimeNode.appendChild(minuteNode);
	
	rootNode.appendChild(mailTimeNode);
	
	
	//收信人
	var recieversNode = rootDoc.createElement("reciever");
	setTextContent(recieversNode,$("#recievers").val());
	rootNode.appendChild(recieversNode);
	
	
	//图表类型
	if($("#chartType").val() === ""){
		alert("请选择图表类型");
		return;
	}
	var chartTypeNode = rootDoc.createElement("graph");
	setTextContent(chartTypeNode,$("#chartType").val());
	rootNode.appendChild(chartTypeNode);
	rootDoc.appendChild(rootNode);
	
	var finalXml = getDocXML(rootDoc);
	finalXml = cynthia.xml.getXMLStr(finalXml);
	
	params = {
			statId:statId,
			statName:statName,
			params:finalXml,
			recievers:$("#recievers").val(),
			isSendMail:$("input[type=radio][name=sendMail]:checked").val(),
			month:$("#month").val(),
			date:$("#date").val(),
			week:$("#week").val(),
			hour:$("#hour").val(),
			minute:$("#minute").val()
    };
	
	$.ajax({
		url:base_url + 'statistic/update.do',
		type:'POST',
		dataType:'text',
		data:params,
		success:function(data){
			if(data === "true")
			{
				initAllStats();
				$("#statdiv").modal('hide');
				showInfoWin("success","保存成功!");
			}else
			{
				alert("保存失败");
			}
		}
	});
	
}

var allStats = new Array();

//初始化所有统计器
function initAllStats()
{
	$.ajax({
		url:base_url + 'statistic/queryAllStatistics.do',
		type:'POST',
		dataType:'json',
		success:function(data){
			setStatistic(data);
		},
		error:function(data){
			setStatistic(eval('(' + data.responseText + ')'));
		}
	});
}

function setStatistic(data)
{
	$("#all_statistic_div").empty();
	var curUser = readCookie('login_username');
	
	for(var i in data){
		var id = data[i].id.value;
		allStats[id] = new Object();
		allStats[id].id = id;
		allStats[id].name = data[i].name;
		allStats[id].isPublic = data[i].isPublic;
		var gridHtml = "";
		
		gridHtml += "<label class=\"checkbox\" >" +
					"<input type=\"checkbox\" name=\"statistic\" value=\"" +allStats[id].id +"\">";
		if(data[i].isPublic != true && data[i].createUser === curUser){
			gridHtml += "<i class=\"icon-stat icon-clear\" title=\"删除\" onclick=\"deleteStat( "+ id + ");\"></i>" +
						"<i class=\"icon-stat icon-edit\" title= \"编辑\" onclick=\"editStat( "+ id + ");\"></i>";
		}
		gridHtml += "<a href=\"showMoreStatistic.html?statisticId="+id+"\" target=\"_blank\">" + data[i].name + "</a>";
		gridHtml += "</label>";
		
		$("#all_statistic_div").append(gridHtml);
	}
}
function deleteStat(statId)
{
	if(!window.confirm("是否确定删除统计器?"))
		return false;
	$.ajax({
		url:base_url + 'statistic/deleteStatistic.do',
		type:'POST',
		dataType:'json',
		data:{'statId':statId},
		success:function(data){
			data = $.trim(data);
			if(data === "true"){
				showInfoWin('success','删除成功!');
				initAllStats();				
			}
			else
				showInfoWin('error','删除失败!');
		}
	});
	return false;
}

function initCreateDiv()
{
	$("#statdiv select").val('');
	clearConditionTable();
	$("#stat_id").val('');
	$("#stat_info_body").empty();
	$("#stat_task").val('');
	$("#my_statistic_h").text('新建统计项');
	$("#statdiv input[type!=button][type!=radio]").val('');
	$("input[type=radio][name=betweenField][value=and]").attr("checked","checked");
	$('#mail_cfg').hide();
	$("input[type=radio][name=sendMail][value=false]").attr("checked","checked");
	setTimeValue('month',1,12,new Array());
	setTimeValue('date',1,31,new Array());
	setTimeValue('week',1,7,new Array());
	setTimeValue('hour',0,23,new Array());
	setTimeValue('minute',0,59,new Array());
	enableSelectSearch();
	$("#statdiv").modal('show');
}

function editStat(statId)
{
	$.ajax({
		url:base_url + 'statistic/initStatisticParam.jsp',
		dataType:'xml',
		data:{'statId':statId},
		success:function(statParam){
			$("#statdiv select").val('');
			$("#statdiv input[type!=button][type!=radio]").val('');
			$("#my_statistic_h").text('编辑统计项');
			$("#stat_id").val(statId);
			var rootNode = $(statParam);
			$("#stat_name").val($(rootNode).find("name").text());
			$("#stat_type").val($(rootNode).find("type").text());
			setTypeDiv(function(){
				$("#templates").val($(rootNode).find("templateId").text());
				initTemplateFields(function(){
					$("#start_time").val($(rootNode).find("timeRange startTime").text());
					$("#end_time").val($(rootNode).find("timeRange endTime").text());
					$("#time_type").val($(rootNode).find("timeRange timeType").text());
					
					var statType = $(rootNode).find("type").text();
					//任务
					if(statType === "task"){
						$("#stat_task").val($(rootNode).find("task taskId").text());
						$("#stat_task_bug_field").val($(rootNode).find("task taskFieldId").text());
					}else if(statType === "person"){
						//人员
						$("#stat_role").val($(rootNode).find("person roleId").text());
						$("#stat_role").change();
						var actionIds = $(rootNode).find("person roleActionIds").text();
						if(actionIds && actionIds.length >0){
							$.each(actionIds.split(","),function(index,action){
								$("#stat_action option[value="+action+"]").attr("selected","selected");
							});
						};
						$("input[type=radio][name=containCurAssign][value="+$(rootNode).find("person containCurAssign").text()+"]").attr("checked","checked");
					}else if(statType === "model"){
						//模块
						$("#stat_field").val($(rootNode).find("model modelfieldId").text());
						$("#stat_field").change();
					}
					
					//设置统计项
					var choosedOption = new Array();
					$(rootNode).find("stats").find("stat").each(function(index,node){
						var statId = node.getAttribute("statId");
						var statValue = $(node).text();
						var statOptions = node.getAttribute("statOptions");
						var tmpArr = statValue.split("|");
						choosedOption = choosedOption.concat(tmpArr.slice(1));
						addToRight(statId,tmpArr[0],statOptions);
						//统计项名
					});

					$.each(choosedOption,function(index,value){
						$("#stat_options option[value='"+value+"']").remove();
					});
					
					$("#recievers").val($(rootNode).find("reciever").text());
					$("#chartType").val($(rootNode).find("graph").text());

					//设置过滤条件
					clearConditionTable();
					$(rootNode).find("conditions").find("field").each(function(index,node){
						
						var content = $(node).find("fieldContent").text();
						if(content!=null&&content!="")
						{
							$("#conditions_table>tbody").append("<tr><td>"+content+"</td><td onclick='subCondition(this)'><span style=\"cursor:pointer;\"class=\"label label-important\">删除</span></td></tr>");
						}
					});
					
					//设置发信时间
					var mailTimeNode = $(rootNode).find("mailTime");
					var isSendMail = mailTimeNode.find("isSendMail").text();
					setTimeValue('month',1,12,mailTimeNode.find("month").text().split(","));
					setTimeValue('date',1,31,mailTimeNode.find("date").text().split(","));
					setTimeValue('week',1,7,mailTimeNode.find("week").text().split(","));
					setTimeValue('hour',0,23,mailTimeNode.find("hour").text().split(","));
					setTimeValue('minute',0,59,mailTimeNode.find("minute").text().split(","));
					
					if(isSendMail === "true"){
						$('#mail_cfg').show();
						$("input[type=radio][name=sendMail][value=true]").attr("checked","checked")
					}else{
						$('#mail_cfg').hide();
						$("input[type=radio][name=sendMail][value=false]").attr("checked","checked");
					}
					
					$("#recievers").val($(rootNode).find("reciever").text());
					
					enableSelectSearch();	
					$("#statdiv").modal('show');
				});
			});
		}
	});
}


function showAllChart()
{
	$("#all_report_div").empty();
	if($("#all_statistic_div input[type=checkbox][name=statistic]:checked").length == 0){
		showInfoWin("error","请选择统计选项!");
		return;
	}
	$.each($("#all_statistic_div input[type=checkbox][name=statistic]:checked"),function(index,node){
		var statisticId = $(node).attr("value");
		var divId = "stat_" + statisticId;
		$("#all_report_div").append("<div class='statistic_div' id=\""+divId +"\"></div>");
		
		$.ajax({
			url:base_url + 'statistic/getStatisticInfo.do',
			data:{'statisticId':statisticId},
			type:'POST',
			dataType:'json',
			success:function(data){
				var chart = $.initChart(divId,data);
				chart.setChartData(data.name,data.data,data.chartType);
			}
		});
	});
}

function changeTips()
{
	$('#stat_priciple').popover('destroy');
	var title = '统计规则提示', content;
	var statType = $("#stat_type").val();
	
	if(statType === "task"){
		content = '1.该统计类型统计任务关联的缺陷状态趋势变化;<br/>2.开始时间和时间类型必选其一;<br/>3.可选择某一任务统计,也可设置过滤条件统计;<br/>4.必须设置统计的bug字段,该字段关联着缺陷数据';
	}else if(statType === "person"){
		content = '1.该统计类型统计相应角色解决bug或问题的数量;<br/>2.定义的解决动作为触发该动作即认为解决该问题;<br/>3.包含当前指派数量即是当前指派给该角色的数据;<br/>4.必须设置统计的bug字段,该字段关联着缺陷数据';
	}else if(statType === "model"){
		content = '1.该统计类型统计相应模块数据,一般统计的是单选项及常规项(如状态,创建人..);<br/> 2.必须选择统计字段';
	}else{
		content = "";
	}
	
	$('#stat_priciple').popover({
		title:title,
		html:true,
		trigger:'hover',
		content:content
	});	
}

function showPrincipleTips()
{
	var statType = $("#stat_type").val();
	if(statType === ""){
		alert("请先选择统计类型!");
		return;
	}
	$('#stat_priciple').popover('show');
	return;
}

//搜索统计项
function searchStat()
{
	var searchValue = $("#search_stat_word").val();
	if(searchValue === "")
		$("#stat_options span option").unwrap("<span>").show();
	else{
		//只显示包含搜索内容的option
		$("#stat_options>option").wrap("<span>").hide();
		$("#stat_options>span option:contains('" + searchValue + "')").unwrap("<span>").show();
	}
}

function bindEvents()
{
	$('#check-all').change(function(e){
		if($(this).prop('checked')){
			$("#all_statistic_div input[type=checkbox][name=statistic]").prop("checked",true);
		}else{
			$("#all_statistic_div input[type=checkbox][name=statistic]").prop("checked",false);
		}
	});
	
	$("#search_stat_word").keydown(function(e){
		if(e.keyCode === 13)
			searchStat();
	});
	
	$("#search_stat_btn").click(function(){
		searchStat();
	});
	
	//绑定发信是否
	$("input[name='sendMail']").bind("click",function(){
		var sendMail = $(this).val();
		if(sendMail === "true")
			$('#mail_cfg').show();
		else
			$('#mail_cfg').hide();
	});
}

$(function(){
	//contains忽略大小写
	jQuery.expr[':'].contains = function(a, i, m) {
		  return jQuery(a).text().toUpperCase()
		      .indexOf(m[3].toUpperCase()) >= 0;
	};
		
	bindEvents();
	initAllStats();
	enableSelectSearch();
});
