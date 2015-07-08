//全局变量
var grid = null;
var actionModifyUser = null;
var curTagId = null;

var defaultHeader = null;


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

function onWindowResize()
{
	var topHeight     = 80;
	var mainGridHeaderHeight = 30;
	var leftTreeWidth = $("#left-tree").is(":hidden")?20:200;
	var windowWidth   = $(window).width();
	var windowHeight  = $(window).height();
	$('#left-tree').height(windowHeight - topHeight);
	$('#left-tree-expand').height(windowHeight - topHeight);
	$("#main-grid-div").height(windowHeight - topHeight - mainGridHeaderHeight);
	$("#main-grid-outer").height(windowHeight - topHeight);
	$("#main-grid-outer").css('left',leftTreeWidth);
	$("#main-grid-outer").width(windowWidth - leftTreeWidth);
	//$("#main-grid-div").width($("#main-grid-header").width());
}

/*窗口重新初始化*/
function bindWindowResize()
{
	$(window).resize(onWindowResize);
}

function bindEvents()
{
	bindWindowResize();
}
/******整体函数结束******/

function getDefaultHeader()
{
	var params={};
	$.ajax({
		url: '../filter/getDefaultHeader.do',
		type:'POST',
		data:params,
		success: function(data){
			defaultHeader = eval('(' + data + ')');
		}
	});
}


/***********************绘制数据********************/
function setFieldBack(showFields, backFields){
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
	widthHtml+="<col class='checkbox'></col>";
	for(var i = 0 ; i < showFields.length ; i ++)
	{
		var fieldId = showFields[i].fieldId;
		if(fieldId == "id" || fieldId == "assign_user" || fieldId == "create_user") //编号
			widthHtml+="<col class='mini-col'></col>";
		else if(fieldId == "priority" || fieldId == "status_id")
			widthHtml+="<col class='middle-mini-col'></col>";
		else if(fieldId == "title") //标题
			widthHtml+="<col class='x-large-col'></col>";
		else
			widthHtml+="<col class='common-col'></col>";
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
	html+="<th>序号</th>";
	
	for(var i = 0 ;i < showFields.length; i++)
	{
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
	
	var actualData = filterData.rows;
	
	actualData = actualData == undefined ? new Array() : actualData;
	
	if(groupField == null)
	{
		for(var i = 0 ; i < actualData.length; i ++ )
		{		
			html += "<tr>";
			html += "<td>" + (i+1) + "</td>";
			for(var j = 0 ;j < showFields.length; j++)
			{
				var content = getXMLStr(actualData[i][showFields[j].fieldId]);
					
				if(content == undefined || content == "")
					content = "-";
				else
					content = content.replace("<br>","").replace("<br />","").replace("<p>","").replace("</p>","").replace("\\r\\n","");
				if(showFields[j].fieldId == "id")
				{
					if(actualData[i].isNew == "true" )
						html += "<td>" + content + "</td>";
					else
						html += "<td>" + content + "</td>";
						
				}else if(showFields[j].fieldId == "title")
				{
					
					html += getTitleTd(actualData[i].id, actualData[i].templateId , content);
				}
				else
				{
					html += "<td><div class='td-content-nowrap'>" + content + "</div></td>";
				}
					
			}
			html += "</tr>";
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
				groupHtml += getDataTr(actualData[i],showFields);
				groupCount++;				
				groupHtml = getGroupHeader(actualData[i],showFields,groupField,groupCount) + groupHtml;
				html += groupHtml;
				groupHtml = "";
				groupCount = 0;
			}
			else
			{
				groupHtml += getDataTr(actualData[i],showFields);
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
	groupHeadHtml += "<td style='text-align:left !important' colspan='"+ (showFields.length+1) +"'>";
	groupHeadHtml += "<i class='icon-group-collapse group-button'></i>";
	groupHeadHtml += groupField.fieldName + ":" + data[groupField.fieldId] + "(" + groupCount + ")";
	groupHeadHtml += "</td></tr>";
	return groupHeadHtml;
}

//单条数据 tr
function getDataTr(data,showFields)
{	
	var trHtml = "";
	
	if(data.isNew != "true")
		trHtml += "<tr class='bug-read'>";
	else
		trHtml += "<tr>";
		
	trHtml += "<td><i class='i-checkbox icon-input-checkbox-unchecked'></i></td>";
	
					
	for(var j = 0 ;j < showFields.length; j++)
	{
		var content = getXMLStr(data[showFields[j].fieldId]);
		
		if(showFields[j].fieldId == "id")
		{
			if(data.isNew == "true" )
				trHtml += "<td>" + content + "</td>";
			else
				trHtml += "<td>" + content + "</td>";
		}
		else if(showFields[j].fieldId == "title")
		{
			
			trHtml += getTitleTd(data.id, data.templateId , content);
		}
		else
		{
			trHtml += "<td><div class='td-content-nowrap'>" + content + "</div></td>";
		}
	}
	trHtml += "</tr>";
	return trHtml;
}

function getTitleTd(id, templateId , content)
{
	content = content.replace('&amp;','&');
	var curFilterId = $("#filterId").val();
	if(curFilterId != "")
		return "<td style='text-align:left !important'><div class='td-content-nowrap'><a class = 'data-title' href='" + base_url + "taskManagement.html?operation=read&taskid=" +id + "&templateId=" + templateId + "&filterId=" + curFilterId + "'" +" target='_blank' onClick='cleanNewData(" + id + ", " +curFilterId+ ");' >" + content + "</a></div></td>";
	else
		return "<td style='text-align:left !important'><div class='td-content-nowrap'><a class = 'data-title' href='" + base_url + "taskManagement.html?operation=read&taskid=" +id + "&templateId=" + templateId + "'" +" target='_blank'>" + content + "</a></div></td>";
		
}

/***********************绘制数据结束********************/

//显示filter数据 绘制表格
function showFilterData(filterData,groupField,showFields,backFields, reDrawHead)
{
	if(reDrawHead == undefined || !reDrawHead == "false")
	{
		//画标头
		var dataHeadHtml = showFilterDataHead(showFields);
		$("#main-grid-header").html(dataHeadHtml);
		setFieldBack(showFields, backFields);
		//设置总页数 当前排序字段
		grid.setMaxPage(filterData.totalCount);
	}
	//画内容
	var dataHeadTable = showFilterDataTable(filterData,groupField,showFields);
	$("#main-grid-content").html(dataHeadTable);
	onWindowResize();
}




function initFilterData(filterId,page,sortField,sortType,reDrawHead)
{
	 var params = '';
	 params += searchCondition;
	 params += "&start=" + (page == undefined ? "0" : (page-1)*grid.getPageSize());
	 params += "&limit=" + grid.getPageSize();
	 params += "&sort=" + (sortField == undefined ? "" : sortField);
	 params += "&dir=" + (sortType == undefined ? "" : sortType);
	 $.ajax({
		url: base_url + 'search/searchData.jsp',
		type:'post',
		dataType:'json',
		data:params,
		success:function(data){
			showFilterData(data, null, defaultHeader, '');
		},
		error:function(data){
			showFilterData(eval("("+data.responseText+")"), null, defaultHeader, '');
		}
	});
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

//搜索数据
function searchDatas(){
	grid.setCurrentPage(1);
	var searchType = $("#searchType").val();
	var searchData = $("#searchWord").val();
	if(searchType=='id' && searchData != "")
	{
		if(isNaN(parseInt(searchData,10)))
		{	
			showInfoWin("error","请输入正确的ID号!");
			return ;
		}
	}
	searchCondition = originCondition;
	if(searchData != ""){
		if(searchCondition !== "")
			searchCondition += "&";
		searchCondition += searchType + "=" + searchData;
	}
	initFilterData('',1,undefined,undefined,"true","");  //查询第一页
}

function executeSubmit()
{
	//根据需要返回ID或者其他的数据
	var dataIds = new Array();
	$.each($("#main-grid-content").find('.icon-input-checkbox-checked') , function (i, node){
		dataIds[dataIds.length] = new Object();
		dataIds[dataIds.length - 1].id = $(node).parent().next().text();
		dataIds[dataIds.length - 1].title = $(node).parent().next().next().text();
	});

	if(dataIds.length == 0){
		showInfoWin("error","请选择相关任务!");
		return;
	}
	if($("#dataType").val() == "single" && dataIds.length > 1){
		showInfoWin("error","该字段只能添加一个任务!");
		return;
	}
	window.opener.executeAddReference(dataIds,"field" + $("#fieldId").val());
	window.close();
}

function executeCancel()
{
	window.opener=null;
	window.open('', '_self'); //IE7必需的.
	window.close();
}

function bindKeyDownEvent(){
	//搜索
	$("#searchWord").keydown(function(e){
		if(e.keyCode==13){
			searchDatas(); //处理事件
		}
	});
	$("#searchBtn").on('click',searchDatas);
}

var searchCondition = null;
var originCondition = null;

$(function(){
	bindKeyDownEvent();
	initMainGrid();	
	getDefaultHeader();
	setDefaultSearchType();
	var url = window.location.href;
	originCondition = searchCondition = url.split("?").length > 1 ? url.split("?")[1] : "";
	initFilterData('',1,undefined,undefined,"true","");  //查询第一页
});
