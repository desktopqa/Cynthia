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

/**************loading*******************/
function showLoading()
{
	//$('#over').fadeIn();
	$('#layout').fadeIn("fast");
}
function hideLoading()
{
	//$('#over').fadeOut();
	$('#layout').fadeOut("fast");
}

function getDefaultHeader()
{
	var params={};
	$.ajax({
		url: 'filter/getDefaultHeader.do',
		type:'POST',
		async: true,
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
			html += "<td>" +(i+1)+ "</td>";
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
	var curFilterId = $("#filterId").val();
	if(curFilterId != "")
		return "<td style='text-align:left !important'><div class='td-content-nowrap'><a class = 'data-title' href='taskManagement.html?operation=read&taskid=" +id + "&templateId=" + templateId + "&filterId=" + curFilterId + "'" +" target='_blank' onClick='cleanNewData(" + id + ", " +curFilterId+ ");' >" + content + "</a></div></td>";
	else
		return "<td style='text-align:left !important'><div class='td-content-nowrap'><a class = 'data-title' href='taskManagement.html?operation=read&taskid=" +id + "&templateId=" + templateId + "'" +" target='_blank'>" + content + "</a></div></td>";
		
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


var searchXml = null;

function initFilterData(filterId,page,sortField,sortType,reDrawHead)
{
	 var params = "filterId=" + filterId;
	 params += "&start=" + (page == undefined ? "0" : (page-1)*grid.getPageSize());
	 params += "&limit=" + grid.getPageSize();
	 params += "&sort=" + (sortField == undefined ? "" : sortField);
	 params += "&dir=" + (sortType == undefined ? "" : sortType);
	 params += "&searchConfig=" + encodeURIComponent(searchXml);
	 
	 $.ajax({
		url:'filterManage/previewFilter.jsp',
		type:'post',
		dataType:'json',
		data:params,
		success:function(data){
			showFilterData(data, null, defaultHeader, '');
		},
		error:function(data){
			showFilterData(eval('(' + data.responseText + ')'), null, defaultHeader, '');
		}
	});
}

function queryFilterData(filterXml){
	getDefaultHeader();
	searchXml = filterXml;
	initFilterData('',1,undefined,undefined,"true",searchXml);  //查询第一页
}

$(function(){
	//1. 开始的时候设置一下窗口div的大小
	onWindowResize();
	//2. bind events
	bindEvents();
	//3. 加载表格
	initMainGrid();	
});