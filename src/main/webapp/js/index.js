//全局变量
var grid = null;
var actionModifyUser = null;
var curTagId = null;
var tagArray = null;
var favorateFilterArray = null;
var sysFilter = {'119695':'待处理','119891':'待跟踪','119892':'已处理[未关闭]','119893':'已处理[己关闭]'};
var filterArray = new Array();
var defaultHeader = null;

/********--中间常用功能相关函数---**********/
/*
 * 该模块主要定义首页表格中间常用功能按钮部分的函数
 */

/*************导出数据, 分为三种情况导出所有数据 所选数据和前1000条数据**************/
function exportFilterData(type,beforeNum)
{
	if(type == "all")
	{
		var localFilterId = $("#filterId").val();
		if(localFilterId != "")
			window.open("filter/exportFilter.jsp?filterId="+localFilterId);
		else if($("#curTagId").val() != "")
			window.open("filter/exportTag.jsp?type=all&tagId=" + $("#curTagId").val());
	}
	else if(type == "choosed")
	{
		var dataIds = grid.findAllSelectedRows();
		if(dataIds.length == 0)
		{
			showInfoWin("error","未选中任何数据！");
			return;
		}

		var localFilterId = $("#filterId").val();
		if(localFilterId != "")
			window.open("filter/exportFilter.jsp?filterId="+localFilterId + "&dataIds=" + dataIds);
		else if($("#curTagId").val() != "")
			window.open("filter/exportTag.jsp?type=choosed&tagId=" + $("#curTagId").val() + "&dataIds=" + dataIds);
	}else
	{
		var localFilterId = $("#filterId").val();
		window.open("filter/exportFilter.jsp?filterId="+localFilterId + "&beforeNum="+beforeNum);
	}
//	return false;
}

//自定义导出条数
function exportFilterDefine()
{
	var exportNum = $("#exportNum").val();
	if(isNumber(exportNum))
	{
		$("#inputExportNum").modal('hide');
		exportFilterData("defined",exportNum);
	}else
	{
		alert("请输入正确的数字！");
		return false;
	}
}

//发送过滤器过滤结果
function sendFilterResult()
{
	var receiver = trim($("#mailReciever").val());
	if(receiver.length == 0)
	{
		alert('请填写收件人！');
		return;
	}
	var params = "receiver=" + getSafeParam(receiver);

	var subject = trim($("#mailSubject").val());
	if(subject.length > 0)
		params += "&subject=" + getSafeParam(subject);

	var content = trim($("#mailContent").val());
	if(content.length > 0)
		params += "&content=" + getSafeParam(content);

	params += "&filterId=" + $("#filterId").val();

	$.ajax({
		url: 'frame/get_SendFilterResult_xml.jsp',
		type :'post',
		data:params,
		success: function(msg){
			showInfoWin("success","过滤器结果发送成功！");
		},
		error:function(msg){
			showInfoWin("error","服务器内部原因，过滤器结果发送失败！");
		}
	});

	$("#sendMailDiv").modal('hide');
}
/*************导出数据结束*******************************/

/*******************批量打开数据*************************/
function openChoosedData(){
	var dataIds = grid.findAllSelectedRows();

	if(!dataIds || dataIds.length == 0)
	{
		showInfoWin("error","请先选择要打开的数据！");
		return false;
	}
	for(var id in dataIds){
		window.open('taskManagement.html?operation=read&taskid=' + dataIds[id],id);
	}
}
/*******************批量打开数据结束*************************/


/*******************批量修改数据*************************/
function initModifyData()
{
	var dataIds = grid.findAllSelectedRows();

	if(!dataIds || dataIds.length == 0)
	{
		showInfoWin("error","请先选择批量修改的数据！");
		return false;
	}

	$.ajax({
		url: 'batchModify/getModifyActionAndUser.do',
		type :'GET',
		dataType : 'json',
		data: {'dataIds': dataIds, 'filterId': $("#filterId").val()},
		success: function(data){
			actionModifyUser = data ;
			setAllModifyAction();
			$('#modifyDataDiv .action_close').show();
			if(Object.keys(actionModifyUser).length > 0)
				setModifyActionUser(Object.keys(actionModifyUser)[0]);
			$("#myLabelModifyData").text("修改数据");
			$("#modifyDataDiv").modal('show');
		},
		error:function(msg){
			showInfoWin("error","服务器内部原因，批量修改失败！");
		}
	});
}

function initCloseData()
{
	var dataIds = grid.findAllSelectedRows();
	if(!dataIds || dataIds.length == 0)
	{
		showInfoWin("error","请先选择要关闭数据！！！");
		return false;
	}

	$.ajax({
		url: 'batchModify/getCloseActionAndUser.do',
		type :'POST',
		dataType : 'json',
		data: {'dataIds': dataIds, 'filterId': $("#filterId").val()},
		success: function(data){
			actionModifyUser = data;
			setAllModifyAction();
			$("#myLabelModifyData").text("关闭数据");
			$('#modifyDataDiv .action_close').hide();
			$("#modifyDataDiv").modal('show');
		},
		error:function(msg){
			showInfoWin("error","服务器内部原因，关闭失败！");
		}
	});

}

function alertClose()
{
	var dataIds = grid.findAllSelectedRows();
	if(!dataIds || dataIds.length == 0)
	{
		showInfoWin("error","请先选择要删除的数据！");
		return false;
	}

	var bool = window.confirm("此操作将无法恢复,您确定删除吗?");
    if(bool)
    	batchDeleteData(dataIds);
}

function batchDeleteData(dataIds)
{
	var param = "";
	for(var key in dataIds)
	{
		param += "&dataId=" + dataIds[key];
	}

	$.ajax({
		url: 'frame/get_DeleteDatas_xml.jsp',
		method: 'post',
		data: param,
		success: function(data){
			var success = $(data).find("success").text();
			if(success=='fail')
			{
				showInfoWin("error","数据删除失败！");
				return;
			}else if(success=='all')
			{
				showInfoWin("success","数据删除成功！");
				grid.refreshGrid();
			}else
			{
				showInfoWin("error","你有数据未删除成功，请检查是否有权限！");
				grid.refreshGrid();
			}
		}
	});
}

//设置所有修改动作
function setAllModifyAction()
{
	$("#modifyDataAction").empty();
	for(var action in actionModifyUser)
	{
		$("#modifyDataAction").append("<option value='" + action + "'> " + action + " </option>");
	}
}

//传入动作名称，改变指派人
function setModifyActionUser(actionName)
{
	var users = actionModifyUser[actionName];
	$("#modifyDataAssignUser").empty();
	//没有指派人则隐藏指派人框
	if(Object.keys(users).length > 0){
		$('#modifyDataDiv .action_close').show();
		for(var key in users){
			$("#modifyDataAssignUser").append("<option value='" + key + "'> " + users[key] + " </option>");
		}
	}else{
		$('#modifyDataDiv .action_close').hide();
	}
}

function onModifyActionChange()
{
	setModifyActionUser($("#modifyDataAction").val());
	return true;
}


//修改过滤器数据
function modifyFilterData()
{
	var param = "";
	if($("#modifyDataAction").val() == "")
	{
		alert("请先选择执行动作！！！");
		return;
	}
	param += "actionName=" + encodeURI($("#modifyDataAction").val());
	if($("#modifyDataAssignUser").val() != "" && $("#modifyDataAssignUser").val() != null)
	{
		param += "&assignUser=" + encodeURI($("#modifyDataAssignUser").val());
	}
	else
	{
		if($("#myLabelModifyData").text() == "修改数据" && actionModifyUser[$("#modifyDataAction").val()].length > 0)
		{
			alert("请先选择指派人！！！");
			return;
		}
	}
	if(trim($("#modifyDataActionComment").val()).length > 0)
		param += "&actionDesc=" + encodeURI(trim($("#modifyDataActionComment").val()));

	var dataId = grid.findAllSelectedRows();

	for(var key in dataId)
	{
		param += "&dataId=" + dataId[key];
	}

	$.ajax({
		url: 'frame/get_ModifyDatas_xml.jsp',
		type :'POST',
		data: param,
		success: function(data){
			$("#modify-content").empty();
			var modifyHtml = "";

			$(data).find("root").find("results").children("result").each(function(index,node){
				var title = $(node).find("title").text();
				var status = $(node).find("status").text();
				var assignUser = $(node).find("assignUser").text();
				var success = $(node).find("success").text();
				modifyHtml += "<tr>";
				modifyHtml += "<td style=\"max-width:360px;\"><div class=\"td-content-nowrap\">" + title + "</div></td>";
				modifyHtml += "<td>" + status + "</td>";
				modifyHtml += "<td>" + (assignUser == '' ? "-" :assignUser) + "</td>";
				modifyHtml += "<td>" + (success == "true" ? "成功" : "失败") + "</td>";
				modifyHtml += "</tr>";
			});
			$("#modify-content").html(modifyHtml);
			$("#modifyResult").modal('show');
			grid.refreshGrid();
		},
		error:function(msg){
			showInfoWin("error","服务器内部原因，修改失败！");
		}
	});

	$("#modifyDataDiv").modal('hide');
}

/***************批量修改结束********************************/

//设置默认过滤器
function setHomeFilter()
{
	var curFilterId = $("#filterId").val();
	if(curFilterId == "")
	{
		showInfoWin("error","请先选择过滤器，设置默认过滤器失败！");
		return;
	}

	$.ajax({
		url:"frame/get_UpdateHomeFilters_xml.jsp",
		type:'POST',
		dataType:'text',
		data :{'filterId' : curFilterId},
		success : function(data){
			showInfoWin("success",data);
		},
		error:function(data){
			showInfoWin("error",data);
		}
	});
}

function refreshFilter()
{
	grid.refreshGrid('false'); //不重绘标题
	return true;
}


/************************配置过滤器显示*********************************/
function saveFilterWidth(filterId,fieldId,width)
{	
	$.ajax({
		url:"filter/saveFilterFieldWidth.do",
		type:'POST',
		data :{filterId:filterId,fieldId:fieldId,width:width},
		success : function(data){
		},
		error:function(responseXML){
		}
	});
}

function openCfgDisplayDiv()
{
	if(isSysFilter($("#filterId").val()))
	{
		showInfoWin("error","系统过滤器无法配置");
		return false;
	}
	$("#cfgDisplayDiv").modal('show');
}

//新增显示字段 从左边向右边移动
function addDispayField()
{
	$("#leftDisplayFields option:selected").appendTo("#rightDisplayFields");
}

//移除显示字段
//从右边向左边移动
function removeDisplayField()
{
	$("#rightDisplayFields option:selected").appendTo("#leftDisplayFields");
}

//上移一个显示字段
function moveUpDisplayField()
{
	if(null == $('#rightDisplayFields').val()){
        alert('请选择一项');
        return false;
    }

	if($('#rightDisplayFields option:selected').length > 1)
	{
		alert('每次只能选择一个!');
		return false;
	}

    //选中的索引,从0开始
    var optionIndex = $('#rightDisplayFields').get(0).selectedIndex;
    //如果选中的不在最上面,表示可以移动
    if(optionIndex > 0){
        $('#rightDisplayFields option:selected').insertBefore($('#rightDisplayFields option:selected').prev('option'));
    }
}

//下移一个显示字段
function moveDownDisplayField()
{
	if(null == $('#rightDisplayFields').val()){
        alert('请选择一项');
        return false;
    }
	if($('#rightDisplayFields option:selected').length > 1)
	{
		alert('每次只能选择一个!')
		return false;
	}
    //索引的长度,从1开始
    var optionLength = $('#rightDisplayFields')[0].options.length;
    //选中的索引,从0开始
    var optionIndex = $('#rightDisplayFields').get(0).selectedIndex;
    //如果选择的不在最下面,表示可以向下
    if(optionIndex < (optionLength-1)){
        $('#rightDisplayFields option:selected').insertAfter($('#rightDisplayFields option:selected').next('option'));
    }
}

//保存显示字段修改结果
function saveDisplayFieldModifyResult()
{
	var showFields = new Array();
	$.each($("#rightDisplayFields").find("option") , function (i ,node){
		showFields.push($(node).attr("value"));
	});
	$.ajax({
		url:"filterManage/filterShowCfg_update.jsp",
		type:'POST',
		data :{'showFields' : showFields, 'filterId' :$("#filterId").val()},
		success : function(responseXML){
			grid.refreshGrid();
		},
		error:function(responseXML){
			grid.refreshGrid();
		}
	});
	
	$("#cfgDisplayDiv").modal('hide');
}
/*配置显示字段相关结束*/


/*****---------左侧树相关函数--------*******/
/*隐藏所有过滤器菜单*/
function hideAllFilterMenu()
{
	$("#allFiltersList").hide();
	//$("#contextMenu").remove();
}

/*显示所有过滤器列表*/
function allFiltersIconClick()
{
	var allFiltersHeight      = $("#allFiltersList").height();
	var mainContentHeight     = $(window).height() - 80;
	if(mainContentHeight > allFiltersHeight)
	{
		var currentTop = $(this).offset().top;
		var calTop = (mainContentHeight - allFiltersHeight)/2+80;
		$("#allFiltersList").css('top',(currentTop > calTop)?calTop:currentTop);
	}

	if($("#allFiltersList").is(':hidden'))
	{
		$("#allFiltersList").show();
	}else
	{
		$("#allFiltersList").hide();
	}
	return false;
}


/*bind item click in navigation*/
function navItemClick(e)
{
	$('#left-tree , #allFiltersDiv').find('.filter a').removeClass('active_link');
	$(this).find("a").addClass("active_link");
	openFilter($(this).attr("id"));
	hideAllFilterMenu();
}


/*hide the left tree*/
function hideLeftTreeIconClick()
{
	createCookie("hideLeftTree=true");
	$('#left-tree').hide();
	$("#left-tree-expand").show();
	onWindowResize();
}

/*show left tree*/
function showLeftTreeIconClick()
{
	createCookie("hideLeftTree=false");
	$("#left-tree-expand").hide();
	$('#left-tree').show();
	onWindowResize();
}

/*左侧tag列表hover事件*/
function leftTreeTagHover(event)
{
}

/*click the expand icon*/
function tagExpandIconClik()
{
	if($("#my-tag").children('ul').is(":visible"))
	{
		$("#my-tag").children('ul').hide();
		$("#tagExpandIcon").removeClass('icon-arrow-down').addClass('icon-arrow-right');
		createCookie("displayTagDiv=false");
	}else
	{
		$("#my-tag").children('ul').show();
		$("#tagExpandIcon").removeClass('icon-arrow-right').addClass('icon-arrow-down');
		createCookie("displayTagDiv=true");
	}
	return true;
}

/**************标签相关******************/
/*标签设置相关函数*/
/*click the tag config icon*/
function tagConfigIconClick(e)
{
	$("#cfgTagDiv").modal('show');
	e.stopPropagation();
	return false;
}

//初始化所有标签
function initMyTag()
{
	var params={};
	$.ajax({
		url: 'tag/getAllTag.do',
		type:'POST',
		data:params,
		success: function(data){
			var tagData = eval('(' + data + ')');
			setMyTag(tagData);
		}
	});
}

//初始化所有标签
function setMyTag(tagData)
{
	$("#left-tag-list").empty();
	$("#tag-cfg-table").empty();
	$("#ul-tag").empty();
	$("#ul-tag").append("<li><a href='#' onclick=\"tagDataMoveOut();\">取消所有标记</a></li>");
	$("#ul-tag").append("<li id=\"tagMoveOut\" style=\"display: none;\"><a href='#' onclick=\"tagIdMoveOut();\">移出当前标签</a></li><li class='divider'></li>");

	tagArray = new Array();
	for(var key in tagData)
	{
		tagArray[tagData[key].id] = new Object();
		tagArray[tagData[key].id].id = tagData[key].id;
		tagArray[tagData[key].id].tagName = tagData[key].tagName;
		tagArray[tagData[key].id].tagColor = tagData[key].tagColor;

		$("#left-tag-list").append("<li class='filter tag-filter' value='" + tagData[key].id + "'>" +
				"<a href='#' ><div class='tagColorSpan' style=\"background-color:"+tagData[key].tagColor+ "\">&nbsp;</div>" + tagData[key].tagName+"</a></li>");

		$("#ul-tag").append("<li class='tag' value='" + tagData[key].id + "'><a href='#'><div class='tagColorSpan' style=\"background-color:"+tagData[key].tagColor+ "\">&nbsp;</div>" + tagData[key].tagName+ "</a></li>");

		$("#tag-cfg-table").append("<tr><td value='" + tagData[key].id + "'>"+ tagData[key].tagName + "</td><td><a class='nomargin' href='#' onclick='modifyTagClassify(this)'>修改</a><a href='#' onclick='removeTagClassify(this)'>删除</a></td></tr>");
	}
	if(tagArray.length > 0)
		$("#ul-tag").append("<li class='divider'></li>");
	$("#ul-tag").append("<li><a href='#' data-toggle='modal' data-target='#cfgTagDiv'>管理标签</a></li>");
	
	
	var displayTagDiv = readCookie("displayTagDiv");  //是否默认展开cookie
	if(displayTagDiv === "false")
	{
		$("#my-tag").children('ul').hide();
		$("#tagExpandIcon").removeClass('icon-arrow-down').addClass('icon-arrow-right');
	}else
	{
		$("#my-tag").children('ul').show();
		$("#tagExpandIcon").removeClass('icon-arrow-right').addClass('icon-arrow-down');
	}
	return true;
	
}

function tagIdMoveOut(){
	var dataIds = grid.findAllSelectedRows();
	if(dataIds.length == 0)
	{
		showInfoWin("error","未选中任何数据，移出失败！");
		return;
	}

	var tagId = $("#curTagId").val();

	tagDataMoveOut(dataIds,tagId);
	grid.refreshGrid('false');
}

function tagDatasMoveOut(datas,tagId,refresh)
{
	$.ajax({
		url: 'tag/deleteTagData.do',
		type:'POST',
		data:{'dataIds': datas, 'tagId' : tagId},
		success: function(data){
			if(data != "" && data != "false")
			{
				showInfoWin("success","标签数据移出成功！");
			}
			else
			{
				showInfoWin("error","标签数据移出失败！");
			}
		}
	});
}
//移动标签数据
function myTagClick()
{
	var dataIds = grid.findAllSelectedRows();
	if(dataIds.length == 0)
	{
		showInfoWin("error","未选中任何数据，移动失败！");
		return;
	}
	var toTagId = $(this).val();//移动到的tag
//	var fromTagId = $("#curTagId").val();

//	if(toTagId == fromTagId)
//	{
//		showInfoWin("error","数据己在本标签中，移动失败！");
//		return;
//	}

	$.ajax({
		url: 'tag/addTagData.do',
		type:'POST',
		data:{'dataIds': dataIds,  'toTagId' : toTagId},
		success: function(data){
			if(data != "" && data != "false")
			{
				showInfoWin("success","标签数据移动成功！");
				grid.refreshGrid('false');
			}
			else
			{
				showInfoWin("error","标签数据移动失败！");
			}
		}
	});
}

//移出标签数据
function tagDataMoveOut()
{
	var curTagId = $("#curTagId").val();

	var dataIds = grid.findAllSelectedRows();

	if(dataIds.length == 0)
	{
		showInfoWin("error","未选中任何数据！");
		return;
	}

	$.ajax({
		url: 'tag/deleteTagData.do',
		type:'POST',
		data:{'dataIds': dataIds, 'tagId' : curTagId},
		success: function(data){
			if(data != "" && data != "false")
			{
				showInfoWin("success","标签数据移除成功！");
				grid.refreshGrid('false');
			}
			else
				showInfoWin("error","标签数据移除失败！");
		}
	});
}

//新建或修改一个标签
function addOrModifyClassify()
{
	if($("#tagName").val() == "")
	{
		alert("标签名字不能为空!");
		return;
	}
	if($("#myModalLabelTag").html() == "新增标签")
	{
		var tagName = $("#tagName").val();
		var tagColor = $("#tagColor").val();
		var params={'tagName' : tagName,'tagColor':tagColor};
		$.ajax({
			url: 'tag/addTag.do',
			type:'POST',
			data:params,
			success: function(data){
				if(data == "" || data == "0"){
					alert("新增失败！");
				}else{
					initMyTag();
				}
				$("#tagName").val("");
			}
		});
		$("#cfgNewTagDiv").modal('hide');
		return true;
	}
	else if($("#myModalLabelTag").html() == "修改标签")
	{
		var tagId = $("#modifyTagId").val();
		var tagName = $("#tagName").val();
		var tagColor = $("#tagColor").val();
		var params={'tagId' : tagId, 'tagName' : tagName,'tagColor':tagColor};
		$.ajax({
			url: 'tag/modifyTag.do',
			type:'POST',
			data:params,
			success: function(data){
				if(data == "" || data == "false"){
					alert("修改失败！");
				}else{
					initMyTag();
				}
				$("#tagName").val("");
			}
		});
		$("#cfgNewTagDiv").modal('hide');
		return true;
	}
}

//新增一个标签
function addTagClassify()
{
	//todo 修改一个标签
	$("#myModalLabelTag").html("新增标签");
	$("#tagName").val('');
	$("#tagColor").val('#990000');
	$(".colorPicker-picker").css('background-color','#990000');
	$("#cfgNewTagDiv").modal('show');
}

//修改一个标签
function modifyTagClassify(link)
{
	//todo 修改一个标签
	var curRow = $(link).parent().parent();

	var tagId = curRow.find("td:first-child").attr("value");
	var tagName = curRow.find("td:first-child").text();
	$("#myModalLabelTag").html("修改标签");
	$("#modifyTagId").val(tagId);
	$("#tagName").val(tagName);
	$("#tagColor").val(tagArray[tagId].tagColor);
	$(".colorPicker-picker").css('background-color',tagArray[tagId].tagColor);
	$("#cfgNewTagDiv").modal('show');
}

//删除一个标签
function removeTagClassify(link)
{
	var curRow = $(link).parent().parent();
	var tagId = curRow.find("td:first-child").attr("value");
	deleteTagById(tagId);
}

function deleteTagById(tagId)
{
	var bool = window.confirm("确定删除标签?");
    if(!bool)
    	 return false;

	var params={'tagId' : tagId};

	$.ajax({
		url: 'tag/removeTag.do',
		type:'POST',
		data:params,
		success: function(data){
			if(data == "" || data == "false")
			{
				showInfoWin("error","标签删除失败！");
			}else
			{
				initMyTag();
				showInfoWin("success","标签删除成功！");
			}
		},
		error: function(data){
			showInfoWin("error","标签删除失败！");
		}
	});
}

//向上移动一个标签
function moveUpTagClassify(link)
{
	var curRow      = $(link).parent().parent();
	var curRowIndex = $(curRow).prevAll().length + 1;
	if (curRowIndex == 1){
        alert("已经是第一行了!");
   } else {
        var preRow = curRow.prev();  //获取当前行的上一行
        var preRowClone = preRow.clone(true);
        curRow.after(preRowClone);  //在curTr后插入一行
        preRow.remove();
   }
}

//向下移动一个标签
function moveDownTagClassify(link)
{
	var rowCount    = $("#tagNameGrid tr").size();
	var curRow      = $(link).parent().parent();
	var curRowIndex = $(curRow).prevAll().length + 2;
	if (curRowIndex == rowCount){
        alert("已经是最后一行了!");
   } else {
        var nextRow = curRow.next();  //获取当前行的上一行
        var nextRowClone = nextRow.clone(true);
        curRow.before(nextRowClone);  //在curTr后插入一行
        nextRow.remove();
   }
}

//保存标签修改结果
function saveTagClassifyModifyResult()
{
	$("#cfgTagDiv").modal('hide');
	grid.refreshGrid();
}

//打开标签数据
function openTagFilter(tagId,page,sortField,sortType,reDrawHead)
{
	$('#left-tree , #allFiltersDiv, #left-tag-list').find('.filter a').removeClass('active_link');
	$("#left-tag-list li[value=\"" + tagId + "\"] a").addClass("active_link");
	
	grid.setCurrentPage(1);
	$("#curTagId").val(tagId);
	$("#filterId").val("");

	//隐藏过滤器相关菜单
	$.each($(".filter-show"),function(i,node){
		$(node).hide();
	});

	$("#tagMoveOut").show();
	document.title= tagArray[tagId].tagName;
	grid.refreshGrid();
	return false;
}

function initTagData(tagId,page,sortField,sortType,reDrawHead)
{
	$.ajax({
		url: 'tag/getTagDatas.do',
		type:'POST',
		data:{'tagId' : tagId},
		success: function(dataArray){
			var searchType = "id";
			if(dataArray == null || dataArray.length == 0){
				showInfoWin("warning","该标签没有任何数据!");
				showFilterData(null, null, defaultHeader, '', reDrawHead);
			}
			else
				queryData("", page,sortField,sortType,reDrawHead,searchType,dataArray);
			showLoading(false);
		},
		error: function(data){
			showInfoWin("error","服务器异常!");
			showLoading(false);
		}
	});

}
/**---左侧树相关函数结束---**/


/*********查询相关*********************/
//过滤器查询
function onDataSearch()
{
	grid.setCurrentPage(1);
	showLoading(true);
	try{
		$('#searchBtn').attr('disabled',"true");
		var filterId = $("#filterId").val();
		var searchType = $.trim($("#searchType").val());
		var keyword = $.trim($("#searchWord").val());

		if(searchType == 'id' && keyword !=''){
			window.open('taskManagement.html?operation=read&id='+keyword+'&taskid='+ keyword ,'_blank');
		}else{
			if(!filterId || filterId == "")
			{
				showInfoWin("error","请先选择过滤器，查询失败!");
				showLoading(false);
				return;
			}
			queryData(filterId, undefined, undefined,undefined,'true',searchType,keyword);
		}
		$('#searchBtn').removeAttr("disabled");
	}catch(e){}

	showLoading(false);
	return false;
}

//过滤器--标签查询
function queryData(filterId,page,sortField,sortType,reDrawHead,searchType, keyword)
{
	 var params = "filterId=" + filterId;
	 params += "&start=" + (page == undefined ? "0" : (page-1)*grid.getPageSize());
	 params += "&limit=" + grid.getPageSize();
	 params += "&sort=" + (sortField == undefined ? "" : sortField);
	 params += "&dir=" + (sortType == undefined ? "" : sortType);
	 params += "&searchType=" + searchType;
	 params += "&searchKey=" + keyword;
	 var searchData = '';
	$.ajax({
		url: 'task/searchTask.jsp',
		type:'POST',
		data:params,
		dataType:'json',
		success: function(queryDataResult){
			searchData = queryDataResult;
			if(filterId != ""){
				//过滤器搜索
				var params = "filterId=" + filterId;
				$.ajax({
					url: "filter/getFilterShowInfo.do",
					type:'POST',
					dataType:'json',
					data:params,
					success: function(filterField){
						showFilterData(searchData, filterField.groupField, filterField.showFields, filterField.backFields, reDrawHead);
					}
				});
			}else{
				//标签查询
				showFilterData(searchData, null, defaultHeader, '', reDrawHead);
			}
			showLoading(false);
		},
		error: function(data){
			showInfoWin('error','服务器内部错误!');
			showLoading(false);
		}
	});
}

/*********查询相关结束*****************/


/********表格相关函数*******/
function initMainGrid()
{
	//1. 加载表格
	initinitMainGridSort();
}
function initinitMainGridSort()
{
	grid = $("#main-grid-outer").sortGrid();
}
/**----表格相关函数结束----**/



/******整体函数******/

/*文档被点击时触发一些事件
 * 例如把一些菜单隐藏
 * */
function documentClick()
{
	hideAllFilterMenu();
}

function onWindowResize()
{
	var topHeight     = 80;
	var mainGridHeaderHeight = 30;
	var leftTreeWidth = $("#left-tree").is(":hidden")?20:240;
	var windowWidth   = $(window).width();
	var windowHeight  = $(window).height();
	$('#left-tree').height(windowHeight - topHeight);
	$('#left-tree-expand').height(windowHeight - topHeight);
	$("#main-grid-div").height(windowHeight - topHeight - mainGridHeaderHeight);
	$("#main-grid-outer").height(windowHeight - topHeight);
	$("#createBugMenu").css('max-height',windowHeight -topHeight -50);
	$("#main-grid-outer").css('left',leftTreeWidth);
	$("#main-grid-outer").width(windowWidth - leftTreeWidth);
	
	var hideLeftTree = readCookie("hideLeftTree");  //是否隐藏左侧树
	if(hideLeftTree === "true"){
		$('#left-tree').hide();
		$("#left-tree-expand").show();
	}else{
		$("#left-tree-expand").hide();
		$('#left-tree').show();
	}
	//$("#main-grid-div").width($("#main-grid-header").width());
}

/*窗口重新初始化*/
function bindWindowResize()
{
	$(window).resize(onWindowResize);
}

/*绑定过滤器的右键菜单*/
function bindContextMenu(){
	var menu_fav_filter =
		[
			{title: "编辑", cmd: "edit", uiIcon: "ui-icon-edit"},
			{title: "新窗口打开", cmd: "open-new-win", uiIcon: "ui-icon-copy"},
			{title: "移出常用栏", cmd: "remove-fav", uiIcon: "ui-icon-clipboard"}  //disabled: true
//		    			{title: "More", children: [
//						{title: "Sub 1 (using callback)", action: function(event, ui) { alert("action callback sub1");} },
//						{title: "Sub 2", cmd: "sub1"}
//						]}
		];

	$("#favorate_menu,#allFiltersDiv").contextmenu({
		delegate: ".filter",
		preventContextMenuForPopup: true,
		preventSelect: true,
		menu: menu_fav_filter,
		select: function(event, ui) {
			var $target = ui.target;
			var filterId = $target.parent().attr("id");
			if(!filterId) return;
			switch(ui.cmd){
				case "edit":
					window.open(getRootDir() + 'editFilter.html?filterId=' + filterId);
					break;
				case "open-new-win":
					openFilterInNewWindow(filterId);
					break;
				case "remove-fav":
					removeFavFilter(filterId);
					break;
			}
			return true;  //返回true立即关闭菜单
		},

		beforeOpen: function(event, ui) {
			var $target = ui.target;
			var isAllFilter = $target.parent().hasClass("ui-draggable");
			if(isAllFilter){
				$("#favorate_menu,#allFiltersDiv")
//				.contextmenu("replaceMenu", menu_all_filter);
				.contextmenu("setEntry", "remove-fav", {title: "移出常用栏", uiIcon: "ui-icon-clipboard", disabled: true});
			}
			return true;
		}
	});
}


/*绑定页面上的一些点击事件*/
function bindClickEvents()
{
	/*bind document click events*/
	$(document).click(documentClick);
	/*bind click function to nav items*/
	$("#left-tree,#allFiltersDiv").delegate('.filter','click',navItemClick);
	/*display all filters*/
	$("#allFiltersIcon").click(allFiltersIconClick);
	$("#ul-tag").delegate('.tag', 'click', myTagClick);
	$(document).delegate('#searchBtn','click',onDataSearch);
	$("#my-tag-link").click(tagExpandIconClik);
	$("#tagConfigIcon").click(tagConfigIconClick);
	$("#openTemplate").click(openDefaultTemplate);

	//删除标签
	$("#left-tag-list").delegate('.icon-clear','click',function(){
		deleteTagById($(this).parent().attr("value"));
		return false;
	});
	
	//打开标签
	$("#left-tag-list").delegate('.tag-filter','click',function(){
		openTagFilter($(this).attr("value"));
		return false;
	});

	$(document).delegate('.closeTag','click',tagCloseIconClick);
	$(document).delegate('.titleTag','click',titleTagClick);
	
	//从常用里面删除一个过滤器
	$("#favorate_menu").delegate(".icon-clear",'click',function(e){
		var filterId = $(this).parent().attr("id");
		if(filterId != null)
		{
			removeFavFilter(filterId);
			e.stopPropagation();
			return false;
		}
	});

	//新窗口打开
	$("#favorate_menu,#allFiltersList").delegate(".icon-open-win",'click',function(e){
		var filterId = $(this).parent().attr("id");
		if(filterId != null)
		{
			openFilterInNewWindow(filterId);
			e.stopPropagation();
			return false;
		}
	});

	//过滤器编辑
	$("#favorate_menu,#allFiltersList").delegate(".icon-edit",'click',function(e){
		var filterId = $(this).parent().attr("id");
		if(filterId != null){
			window.open(getRootDir() + 'editFilter.html?filterId=' + filterId);
		}
	});

}

/*绑定modal出现时的一些事件，例如获取焦点*/
function bindShownEvents()
{
	//1. 发送邮件时自动定位到收件人
	$("#sendMailDiv").on('shown',function(){
		$("#mailSubject").val(filterArray[$("#filterId").val()].name);
		$("#mailReciever").focus();
	});

	//2. 新增标签时
	$("#cfgNewTagDiv").on('shown',function(){
		$("#tagName").focus();
	});
}

/*绑定hover事件*/
function bindHoverEvents()
{
	//a span hover 提示框
	$(document).delegate('a,i,span','hover',function(e){
		  if($(this).attr("title"))
		  {
			  return;
		  }
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
					 $(this).attr("title",this.myTitle);
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

	//绑定表格下面的grid如果超出的话需要有提示
	$("#main-grid-div").delegate('div','hover',function(e){
		  if(e.type=='mouseenter')
		   {
				if(this.scrollWidth>this.offsetWidth)
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

	//绑定表格头部超过部分信息
	$("#main-grid-header").delegate('th','hover',function(e){
		  if(e.type=='mouseenter')
		   {
				if(this.scrollWidth>this.offsetWidth)
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

	$(document).delegate("a,span,th","mousemove",function(e){
		  if($("#ttip").length>0)
		  {
			$("#ttip").css({"top":(e.pageY+18)+"px","left":(e.pageX+10)+"px"});
		  }
	});

	//常用过滤器新窗口打开
	$("#allFiltersList").on('mouseover','.filter',function(){
		var link = $(this);
		var openIcon = link.find(".icon-open-win");
		var editFilterIcon = link.find(".icon-edit");
		if(openIcon.length > 0)
		{
			openIcon.show();
		}else
		{
			link.prepend("<i class='icon-open-win' title='新窗口打开'></i>");
		}
		if(editFilterIcon.length > 0)
		{
			editFilterIcon.show();
		}else
		{
			link.prepend("<i class='icon-edit' title='编辑'></i>");
		}
		return false;
	}).on('mouseout','.filter',function(){
		var link = $(this);
		link.find("i").hide();
		return false;
	});

	//常用过滤器移出常用栏
	$("#favorate_menu").on('mouseover','.filter',function(){
		var link = $(this);
		var removeFilterIcon = link.find(".icon-clear");
		var openIcon = link.find(".icon-open-win");
		var editFilterIcon = link.find(".icon-edit");
		if(openIcon.length > 0)
		{
			openIcon.show();
		}else
		{
			link.prepend("<i class='icon-open-win' title='新窗口打开'></i>");
		}

		if(editFilterIcon.length > 0)
		{
			editFilterIcon.show();
		}else
		{
			link.prepend("<i class='icon-edit' title='编辑'></i>");
		}

		if(removeFilterIcon.length > 0)
		{
			removeFilterIcon.show();
		}else
		{
			link.prepend("<i class='icon-clear' title='移出常用栏'></i>");
		}
		return false;
	}).on('mouseout','.filter',function(){
		var link = $(this);
		link.find("i").hide();
		return false;
	});

	/*左侧tag列表hover事件*/
	$("#left-tag-list").on('mouseover','.tag-filter',function(){
		var link = $(this);
		var removeFilterIcon = link.find(".icon-clear");
		if(removeFilterIcon.length > 0)
		{
			removeFilterIcon.show();
		}else
		{
			link.prepend("<i class='icon-clear' style='display:block;' title='删除'></i>");
		}
		return false;
	}).on('mouseout','.tag-filter',function(){
		var link = $(this);
		link.find("i").hide();
		return false;
	});
	
	//标签hover出现删除按纽
	$("#main-grid-div").on('mouseover','.titleTag',function(){
		$(this).find("span").show();
	}).on('mouseout','.titleTag',function(){
		$(this).find("span").hide();
	});

	$("#main-grid-div").on('mouseover',".closeTag",function(){
		$(this).addClass('tag-hover');
	}).on('mouseout',".closeTag",function(){
		$(this).removeClass('tag-hover');
	});
}


//绑定键盘事件
function bindKeyDown()
{
	//搜索
	$(document).on('keydown','#searchWord',function(e){
		if(e.keyCode==13){
		   onDataSearch(); //处理事件
		}
	});

	$("#tagName").keydown(function(e){
		if(e.keyCode==13){
		   addOrModifyClassify(); //处理事件
		}
	});
}


//过滤器列表可以直接在这边进行拖拽
function bindFilterDragEvent()
{
	//将过滤器列表拖拽到最常用列表
	$("#allFiltersList").find(".filter").draggable({
		connectToSortable : '#favorate_menu',
		helper:'clone',
		start: function(e,t) {
			var dragItem = t.helper.context;
			var filterId = dragItem.id;

			if(filterId != "")
			{
				var favFilter = $("#favorate_menu").find("li[id='"+filterId+"']");
				if(favFilter.length > 0)
				{
					showInfoWin("error","该过滤器已经在最常访问里面了!");
					return false;
				}else
				{
					$(e.target).attr("flag","newFilter");
				}
			}else
			{
				showInfoWin("error","该过滤器无法拖动!");
				return false;
			}
		},
		drag: function(e, t) {

		},
		stop: function(e, t) {
			var newFilter = $("#favorate_menu").find("li[flag='newFilter']");
			var filterId = t.helper.context.id;
			$(newFilter).attr("id",filterId);
			$(newFilter).removeAttr("flag");
			$(newFilter).removeClass("ui-draggable");
			updateFavFilters();
			if($("#favorate_menu").find("li[id='" +filterId +"']").length > 0){
				//确定是否正确移入
				$("#allFiltersList").find("li[id='" + filterId + "']").remove();
			}
		}
	});

	//最长访问的过滤器进行排序
	$("#favorate_menu").sortable({
		//connectWith: "#favorate_menu",
		opacity: .35,
		start: function(e,t) {
		},
		stop: function(e,t) {
			updateFavFilters();
		}
	});
}

function bindEvents()
{
	bindHoverEvents();
	bindClickEvents();
	bindShownEvents();
	bindWindowResize();
//	bindContextMenu();
	bindKeyDown();
	//bindContextMenuEvents();
}
/******整体函数结束******/

/***********数据处理******************/
function initFilterMenuData()
{
	//得到系统默认过滤器
	getHomeFilter(function(){
		initCreateBugMenu(); //初始菜单

		loadMenuAndData();
		window.setInterval(loadMenuAndData,600000);  //十分钟加载一次

		getDefaultHeader();
		setDefaultSearchType();
	});
}

function loadMenuAndData()
{
	initFilterMenu();
	grid.refreshGrid();
}

/*************菜单处理****************/
function initCreateBugMenu()
{
	$.ajax({
		url:'task/queryUserTemplates.jsp',
		dataType:'xml',
		params:{},
		success:function(data){
			var gridHtml = '';
			$("#createBugMenu").empty();
			$.each($(data).find("templates").children("template"),function(index,node){
				var id = $(node).find("id").text();
				var name = $(node).find("name").text();
				var templateTypeId = $(node).find("templateType").text();
				gridHtml += "<li><a tabindex=\"-1\" href=\"taskManagement.html?operation=create&templateTypeId="+ templateTypeId
				+"&templateId="+id+"\" target=\"_blank\">" +name+ "</a></li>";
			});

			$("#createBugMenu").html(gridHtml);
		}
	});
}

function getDefaultHeader()
{
	var params={};
	$.ajax({
		url: 'filter/getDefaultHeader.do',
		type:'POST',
		data:params,
		success: function(data){
			defaultHeader = eval('(' + data + ')');
		}
	});
}

function getHomeFilter(callback)
{
	var filterIdHash = window.location.hash;
	var filterId = "";
	if(filterIdHash){
		try{
			var filterIdStr = filterIdHash.substring(1, filterIdHash.length);
			if(isNumber(filterIdStr)){
				filterId = filterIdStr;
			}
		}catch(e){}
	}
	
	filterId = filterId || request("filterId");
	if(filterId == ""){
		$.ajax({
			url: 'filter/getHomeFilter.do',
			type:'POST',
			success: function(filterId){
				filterId = filterId || '119695';
				$("#filterId").val(trim(filterId || "" ));
				if(callback) {
					callback();
				}
			}
		});
	}else{
		$("#filterId").val(trim(filterId));
		if(callback) {
			callback();
		}
	}
}

function initFilterData(filterId,page,sortField,sortType,reDrawHead)
{
	 var params = "filterId=" + filterId;
	 params += "&start=" + (page == undefined ? "0" : (page-1)*grid.getPageSize());
	 params += "&limit=" + grid.getPageSize();
	 params += "&sort=" + (sortField == undefined ? "" : sortField);
	 params += "&dir=" + (sortType == undefined ? "" : sortType);
	 
	$.ajax({
		url: "filter/filter.jsp",
		type:'POST',
		data:params,
		dataType:'json',
		success: function(data){
			var filterData = data;
			var params = "filterId=" + filterId;
			$.ajax({
				url: "filter/getFilterShowInfo.do",
				type:'POST',
				data:params,
				dataType:'json',
				success: function(filterField){
					showFilterData(filterData, filterField.groupField, filterField.showFields, filterField.backFields, reDrawHead);
					showLoading(false);
				},error:function(filterField){
					showFilterData(filterData, filterField.groupField, filterField.showFields, filterField.backFields, reDrawHead);
					showInfoWin("error","过滤器定义出错，没查询到任何数据！");
					showLoading(false);
				}
			});
		}
	});
}

function setFieldBack(showFields, backFields){
	backFields = backFields || [];
	$("#leftDisplayFields").empty();
	for(var i = 0; i < backFields.length; i++)
	{
		$("#leftDisplayFields").append("<option value='" + backFields[i].fieldId + "'> " + backFields[i].fieldName + " </option>");
	}

	$("#rightDisplayFields").empty();
	for(var i = 0; i < showFields.length; i++)
	{
		$("#rightDisplayFields").append("<option value='" + showFields[i].fieldId + "'> " + showFields[i].fieldName + " </option>");
	}
}

//设置列头部宽度
function getFilterTableWidth(showFields)
{
	var widthHtml = "";
	widthHtml+="<colgroup>";
	widthHtml+="<col class='checkbox'></col><col style='width:40px;'></col>";
	for(var i = 0 ; i < showFields.length ; i ++)
	{
		var fieldId = showFields[i].fieldId;
		if(fieldId == "id" || fieldId == "assign_user" || fieldId == "create_user") //编号
			widthHtml+="<col class='mini-col'";
		else if(fieldId == "priority" || fieldId == "status_id" || showFields[i].fieldName.indexOf("优先级") != -1 )
			widthHtml+="<col class='middle-mini-col'";
		else if(fieldId == "title") //标题
			widthHtml+="<col class='x-large-col'";
		else
			widthHtml+="<col class='common-col'";
		
		if(showFields[i].fieldWidth)  //自定义了宽度
			widthHtml += ' style="width:' + showFields[i].fieldWidth + '" ';
		widthHtml += "></col>";
	}
	widthHtml+="</colgroup>";
	return widthHtml;
}

//设置列头部
function showFilterDataHead(showFields)
{
	//画列头
	var html = "";
	html += getFilterTableWidth(showFields);

	html+="<thead>";
	html+="<tr>";
	html+="<th class='checkbox'><i class='i-checkbox icon-input-checkbox-unchecked'></i></th><th>序号</th>";

	for(var i = 0 ;i < showFields.length; i++)
	{
		if(showFields[i].fieldId === "priority")
			continue;
		
		html+="<th class='sort-header mini-col ";
		if(showFields[i].fieldId == grid.getSortField() && grid.getSortType() == "asc")
			html += "sort-down";
		else if (showFields[i].fieldId == grid.getSortField() && grid.getSortType() == "desc")
			html += "sort-up";
		html += "'";

		if(showFields[i].fieldId == "title")
			html += "style='text-align:left;'";

		html+= "value='" + showFields[i].fieldId + "'>" + showFields[i].fieldName + "</th>";

	}

	html+="</tr>";
	html+="</thead>";
	return html;
}

function showFilterDataTable(filterData,groupField,showFields)
{
	var html = "";

	//定义列宽度
	html += getFilterTableWidth(showFields);

	html += "<tbody>";

	var actualData = filterData? filterData.rows : [];
	actualData = actualData ? actualData : [];
	if(groupField == null)
	{
		for(var i = 0 ; i < actualData.length; i ++ )
		{
			html += getDataTr((i+1),actualData[i],showFields);
		}
	}
	else
	{
		if(actualData.length == 0)
			return "";
		var currentGroupData = actualData[0][groupField.fieldId];  // 当前分组内容
		var groupHtml = "";
		var groupCount = 0;

		for(var i = 0 ; i < actualData.length; i ++ )
		{
			if(actualData[i][groupField.fieldId] != currentGroupData )
			{
					groupHtml = getGroupHeader(actualData[i-1],showFields,groupField,groupCount) + groupHtml;
					currentGroupData = actualData[i][groupField.fieldId];  // 当前分组内容
					html += groupHtml;
					groupHtml = "";
					groupCount = 0;
			}
			if(i == actualData.length-1)
			{
				groupHtml += getDataTr((i+1),actualData[i],showFields);
				groupCount++;
				groupHtml = getGroupHeader(actualData[i],showFields,groupField,groupCount) + groupHtml;
				html += groupHtml;
				groupHtml = "";
				groupCount = 0;
			}
			else
			{
				groupHtml += getDataTr((i+1),actualData[i],showFields);
				groupCount++;
			}
		}

	}
	return html;
}

//分组头
function getGroupHeader(data,showFields,groupField,groupCount)
{
	var groupHeadHtml = "";
	groupHeadHtml += "<tr class='main-grid-grouprow'>";
	groupHeadHtml += "<td style='text-align:left !important' colspan='"+ (showFields.length+2) +"'>";  // 2包括序号 和checkbox
	groupHeadHtml += "<i class='icon-group-collapse group-button'></i>";
	groupHeadHtml += groupField.fieldName + ":" + data[groupField.fieldId] + "(" + groupCount + ")";
	groupHeadHtml += "</td></tr>";
	return groupHeadHtml;
}

//单条数据 tr
function getDataTr(index,data,showFields)
{
	var trHtml = "";
	trHtml += "<tr value= " + data.id + ">";
	trHtml += "<td><i class='i-checkbox icon-input-checkbox-unchecked'></i></td><td>" + index + "</td>";
	for(var j = 0 ;j < showFields.length; j++)
	{
		if(showFields[j].fieldId === "priority")
			continue;
		
		var content = data[showFields[j].fieldId];
		if(content == undefined || content == "")
			content = "-";
		else if(content == '-2147483648')
			content = '0';
		else
			content = content.replace("<br>","").replace("<br />","").replace("<p>","").replace("</p>","").replace("\\r\\n","");

		if(content.indexOf("href") == -1)
			content = getHTMLStr(content);

		if(showFields[j].fieldId == "id")
		{
			if(data.isNew == "true" )
				trHtml += "<td><img class='id-img' id=image_" + data.id + "  src='images/new.bmp'/>&nbsp;" + content + "</td>";
			else
				trHtml += "<td><img class='id-img' id=image_" + data.id + "  src='images/old.bmp'/>&nbsp;" + content + "</td>";

		}else if(showFields[j].fieldId == "title")
		{
			trHtml += getTitleTd(data , content);
		}else
		{
			if(showFields[j].fieldId == "description")
			{
				content = getNoXMLStr(content).replace(/<[^>]+>/g,"");
			}

			if(content.indexOf("http://") == 0) //以http://开头
				content = "<a href=\"" + content + "\" target=\"_blank\">" + (content) + "</a>";
			trHtml += "<td><div class='td-content-nowrap'>" + (content) + "</div></td>";
		}
	}
	trHtml += "</tr>";
	return trHtml;
}

//bug标题
function getTitleTd(data, content)
{
	var titleTd = '';
	var spanTd = '';
	var spanLength = 0;
	try{
		if(data.selected && data.selectedName != ''){
			var tagId = data.selectedName;
			spanLength = getLengthOfTag(tagId);
			if(tagId && tagId.length > 0 ){
				var tagList = tagId.split(",");
				spanTd += '<div style="width:'+spanLength+'px;top:3px; float:right; right:0px;position:relative;">';
				for(var i in tagList){
					if(tagArray[tagList[i]].id == $("#curTagId").val()) //当前标签中打开不显示
						continue;
					spanTd += getTagSpan(data.id,tagArray[tagList[i]].id,tagArray[tagList[i]].tagName,tagArray[tagList[i]].tagColor);
				}
				spanTd += '</div>';
			}
		}
	}catch(e){
	}
	titleTd += "<td style='text-align:left !important;position:relative;'>";
	titleTd += spanTd;
	titleTd += "<div class='td-content-nowrap' style=margin-right:"+spanLength+"px;><a class = 'data-title' href='taskManagement.html?operation=read&taskid=" +data.id + "&templateId=" + data.templateId + "'" +" target='_blank' onClick='cleanNewData(" + data.id + ", " +$("#filterId").val()+ ");'>" + content + "</a></div>";
	titleTd += "</td>";
	return titleTd;
}

function getTagSpan(dataId,tagId,tagName,tagColor)
{
	var tagHtml = "";
	tagHtml += '<span class="label lableTag titleTag" style="float:right;background-color:'+ tagColor +'" tagId='+tagId+'>'+ tagName + '';
	tagHtml += '<span class="label lableTag closeTag" dataId = '+dataId+' tagId='+tagId+' style="display:none; padding: 0px 2px; margin-left:2px; cursor:pointer; background-color:'+ tagColor +'"><b title="移出标签">x</b></span></span>';
	return tagHtml;
}

function titleTagClick(e){
	openTagFilter($(this).attr("tagId"));
	return false;
}

function tagCloseIconClick(e){
	var dataArray = new Array();
	dataArray.push($(this).attr("dataId"));
	tagDatasMoveOut(dataArray,$(this).attr("tagId"));
	$(this).parent().remove();
	return false;
}

function getLengthOfTag(tagName){
	var tagList = tagName.split(",");
	if($.inArray($("#curTagId").val(),tagList) != -1)
		tagList.splice($.inArray($("#curTagId").val(),tagList),1);
	return getLengthOfTagArr(tagList);
}

function getLengthOfTagArr(dataTagArray)
{
	var length = 0;
	length += 25;
	for(var i in dataTagArray){
		if( i != 0)
			length += 10;//span之间间隔
		var tagTmp = tagArray[dataTagArray[i]].tagName;
		length += (getLengthOfStr(tagTmp) *8.3);
	}
	return length;
}

//显示filter数据 绘制表格
function showFilterData(filterData,groupField,showFields,backFields, reDrawHead)
{
	showFields = showFields || [];
	if(reDrawHead == undefined || !reDrawHead == "false"){
		//画标头
		var dataHeadHtml = showFilterDataHead(showFields);
		$("#main-grid-header").html(dataHeadHtml);
		setFieldBack(showFields, backFields);
		//设置总页数 当前排序字段
	}
	grid.setMaxPage(filterData == null ? 0 : filterData.totalCount);
	//画内容
	var dataHeadTable = showFilterDataTable(filterData,groupField,showFields);
	$("#main-grid-content").html(dataHeadTable);
	onWindowResize();
}

/*************菜单处理****************/
function initFilterMenu(type , filterId)
{
	//查询过滤器菜单
	var params = {};
	$.post('filterManage/initFilterMenu.jsp',params,function(data,status){
			onCompleteInitFilterMenu(data,status,type,filterId);
	},'xml');
}

function updateFavFilters(callback)
{
	var childrenIds = "";
	$("#favorate_menu").find(".filter").each(function(idx,item){
		var filterId = $(item).attr("id");
		if(filterId != "")
			childrenIds = childrenIds+filterId+",";
	});

	$.ajax({
		type:'POST',
		url : 'tree/jsTree.jsp',
		data : {
			'operation':'update_favorites',
			'id':"1",
			'position':1,
			'childrenIds':childrenIds
		},
		success : callback
	});
}

function onCompleteInitFilterMenu(data,textStatus,type,filterId)
{
	initFavoriteFilterMenu(data);

	initFolderMenu(data);

	intiSysFiltersNum();

	bindFilterDragEvent();

	//设置默认过滤器title
	var filterDefault = filterArray[$("#filterId").val()];
	if(filterDefault)
		document.title = filterDefault.name;
}

function intiSysFiltersNum()
{
	$.ajax({
		type : 'POST',
		url:'filterManage/initFilterDatas.jsp',
		dataType : 'xml',
		success : function(data)
		{
			$(data).find("filter").each(function(index,node){
				var filterId = $(node).children("id").text();
				var datas = $(node).children("datas").text();
				var filterName = $(node).children("name").text();

				var favorateMenuNode = $("#favorate_menu").find(filterId).children("a");
				var actValue = filterName+datas;
				if(favorateMenuNode != null)
				{
					$(favorateMenuNode).text(actValue);
				}

				var allFiltersListNode = $("#allFiltersList").find(filterId).children("a");
				if(allFiltersListNode != null)
				{
					$(allFiltersListNode).text(actValue);
				}
			});

			cutOffLongString("favorate_menu" , 33);
			cutOffLongString("allFiltersList" , 44);
		}
	});
}

function getFolderHTML(node)
{
	var folderId = $(node).children("id").text();
	var folderName = $(node).children("name").text();
	var html = "";

	html+="<li class='dropdown-submenu'>";
	html+="<a tabindex='-1' href='#' id='"+folderId+"'>"+folderName+"</a>";


	var folderHtml = "";
	var inFolder = $(node).children("folder");
	if(inFolder!=null)
	{
		$(node).children("folder").each(function(index,node){
			folderHtml+=getFolderHTML(node);
		});
	}

	$(node).children("filters").children("filter").each(function(index,node){
		var filterId = $(node).children("id").text();
		if(!inArray(favorateFilterArray,filterId)){
			var filterName = $(node).children("name").text();
			var datas = $(node).children("datas").text();
			folderHtml+="<li class='filter' id='"+filterId+"'><a tabindex='-1' href='#"+ filterId+"'  datas='"+datas+"'>"+filterName+datas+"</a></li>";
		}
	});

	if($.trim(folderHtml) != "" ){
		html+= "<ul class='dropdown-menu'>";
		html+= folderHtml;
		html+="</ul>";
	}

	html+="</li>";
	return html;
}

function initFolderMenu(data)
{
	var html = "";
	$("#allFiltersList").empty();

	$(data).find("folders").children("folder").each(function(index,node){
			var folderId = $(node).children("id").text();
			var folderName = $(node).children("name").text();

			html+="<li class='dropdown-submenu'>";
			html+="<a tabindex='-1' href='#' id='"+folderId+"'>"+folderName+"</a>";

			var folderHtml = "";

			var inFolder = $(node).children("folder");
			if(inFolder!=null)
			{
				$(node).children("folder").each(function(index,node){
					folderHtml+=getFolderHTML(node);
				});
			}

			$(node).children("filters").children("filter").each(function(index,node){
				var filterId = $(node).children("id").text();
				if(!inArray(favorateFilterArray,filterId)){
					var filterName = $(node).children("name").text();
					filterArray[filterId] = new Object();
					filterArray[filterId].id = filterId;
					filterArray[filterId].name = filterName;
					var datas = $(node).children("datas").text();
					folderHtml+="<li class='filter' id='"+filterId+"'><a tabindex='-1'  href='#"+ filterId+"' datas='"+datas+"' target='_self'>"+filterName+datas+"</a></li>";
				}
			});

			if($.trim(folderHtml) != ""){
				html += "<ul class='dropdown-menu'>";
				html += folderHtml;
				html+="</ul>";
			}
			html+="</li>";

	});

	$("#allFiltersList").append(html);

	appendSystemFiltersToFilterFolderMore();

	//初始化默认过滤器菜单
	$(data).find("defaultfilters").children("filter").each(function(index,node){
			var filterId = $(node).children("id").text();
			if(!inArray(favorateFilterArray,filterId)){
				var filterName = $(node).children("name").text();
				filterArray[filterId] = new Object();
				filterArray[filterId].id = filterId;
				filterArray[filterId].name = filterName;
				var datas = $(node).children("datas").text();
				$("#allFiltersList").append("<li class='filter' id='"+ filterId +"'><a href='#"+ filterId+"'   datas='"+datas+"' target='_self'>"+filterName+datas+"</a></li>");
			}
	});

	if($("#allFiltersList").find("li").length == 0){
		$("#allFiltersIcon").hide();
	}else{
		$("#allFiltersIcon").show();
	}
//	cutOffLongString("filterFolders" , 27);
//	cutOffLongString("allFiltersList" , 27);
}

function openFilterInNewWindow(filterId)
{
	window.open("index.html?filterId="+filterId, "_blank","",false);
}

function removeFavFilter(filterId)
{
	if(!window.confirm("是否要移出常用过滤器呢?")){
		return;
	}
	if(filterId && filterId != ""){
		$("#favorate_menu").find("li[id='"+filterId+"']").remove();
		updateFavFilters(initFilterMenu);
	}
}

/**
 * @param id
 * @param maxLen :最长能显示的中文个数  两个英文符占一个中文
 */
function cutOffLongString(id, maxLen)
{
	$("#" + id).find("a").each(function(index,node){
	var text = $(this).text();
	var totalLen = getLengthOfStr(text);
	$(this).attr("title",text);
	if( totalLen > maxLen){
		var datas = text.substring(text.lastIndexOf("("));
		var curIndex = getSubIndexOfLongStr(text, (maxLen - datas.length -3) );
		var prefix = text.substring(0,curIndex);
		$(this).text(prefix+"..."+datas);
	}
});

}

function getSubIndexOfLongStr(str,maxLen){
	var length = 0 ;
	for(var i = 0,max = str.length; i < max; i ++){
		if(isChinese(str.charAt(i)))
			length += 2;
		else
			length++;
		if(length > maxLen)
			return i;
	}
	return str.length -1;
}
//过滤器数量一栏显示不下 将系统过滤器加到更多选项里面
function appendSystemFiltersToFilterFolderMore()
{
	for(var filterId in sysFilter){
		if(!inArray(favorateFilterArray,filterId)){
			filterArray[filterId] = new Object();
			filterArray[filterId].id = filterId;
			filterArray[filterId].name = sysFilter[filterId];
			$("#allFiltersList").append("<li class='filter' id='"+filterId+"'><a href='#"+ filterId+"' >"+sysFilter[filterId]+"</a></li>");
		}
	}
}

//打开过滤器
function openFilter(filterId)
{
	grid.setCurrentPage(1);
	$("#searchWord").val(''); //清空搜索条件
	if(filterId == undefined)
		return;
	$("#filterId").val(filterId);
	$("#curTagId").val("");
	grid.refreshGrid();
	document.title= filterArray[filterId].name;
	//打开过滤器相关菜单
	$.each($(".filter-show"),function(i,node){
		$(node).show();
	});
	$("#tagMoveOut").hide();
}

//设置默认搜索类型
function setDefaultSearchType()
{
	var defaultSearchValue = readCookie('defalutSearchValue');  //设置默认搜索
	$("#searchType").val(defaultSearchValue);
	//修改搜索配置
	$("#searchWord").val("");
}

//搜索类型改变
function onSearchTypeChange()
{
	createCookie('defalutSearchValue' + "=" + $("#searchType").val());
}

//常用过滤器菜单处理
function initFavoriteFilterMenu(data)
{
	$("#favorate_menu").empty();
	var favoriteFiltersNode = $(data).find("favorites");
	var width = $(window).width();

	favorateFilterArray = new Array();
	$(favoriteFiltersNode).find("filter").each(function(index,node){
		var filterId = $(node).find("id").text();
		favorateFilterArray.push(filterId);
		var name  = $(node).find("name").text();
		filterArray[filterId] = new Object();
		filterArray[filterId].id = filterId;
		filterArray[filterId].name = name;
		var datas = $(node).find("datas").text();
		var value = name+datas;
		$("#favorate_menu").append("<li class='filter' id='"+filterId+"' ><a href='#"+ filterId+"'  datas='"+datas+"' target='_self'>"+value+"</a></li>");
	});

	$("#favorate_menu,#allFiltersDiv").find("li").each(function(index,node){
		var currentFilterId = $("#filterId").val();

		if($(node).attr("id")==currentFilterId)
		{
			$(node).find("a").addClass('active_link');
		}
	});
}

//新建默认表单
function openDefaultTemplate(e){
	var defaultTemplateId = null;
	var defaultTemplateTypeId = null;
	var $clickNode = $(this);
	$.ajax({
		url:'filterManage/initUserDefaultTemplate.jsp',
		success:function(data){
			defaultTemplateId = $(data).find("templateId").text();
			defaultTemplateTypeId = $(data).find("templateTypeId").text();
			if(defaultTemplateId != null && $.trim(defaultTemplateId) != ''){
				window.open('taskManagement.html?operation=create&templateId=' +defaultTemplateId + '&templateTypeId=' + defaultTemplateTypeId);
			}else{
				$clickNode.parent().toggleClass("open");
			}
		}
	});
	return false;
}

/*************菜单处理结束****************/


$(function(){
	//1. 开始的时候设置一下窗口div的大小
	onWindowResize();
	//2. bind events
	bindEvents();
	//3. 加载标签
	initMyTag();
	//4. 加载表格
	initMainGrid();
	//5. 加载数据
	initFilterMenuData();
	//6. 初始化标签颜色选项
	$('#tagColor').colorPicker({showHexField: false});   //showHexField是否显示Hex值
});