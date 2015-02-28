//全局变量
var grid = null;
var defaultHeader = null;
var searchXml = null;
var needCheckBox = false; // 是否需要check框

/********表格相关函数*******/
function initMainGrid()
{
	//1. 加载表格
	initinitMainGridSort();
}
function initinitMainGridSort()
{
	if(!grid){
		grid = $("#main-grid-outer").sortGrid();
	}
}
/**----表格相关函数结束----**/


/******整体函数******/

/******整体函数结束******/

/**************loading*******************/
function showLoading()
{
	$('#layout').fadeIn("fast");
}
function hideLoading()
{
	$('#layout').fadeOut("fast");
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
	initFilterData('',1,undefined,undefined,"true",searchXml);  //查询第一页
}

/***********************绘制数据********************/
function showFilterDataTable(filterData,groupField,showFields)
{
	var html = "";
	var actualData = filterData.rows;
	actualData = actualData == undefined ? new Array() : actualData;
	
	if(groupField == null)
	{
		for(var i = 0 ; i < actualData.length; i ++ )
		{		
			html += "<tr>";
			if(needCheckBox)
				html += "<td><i class='i-checkbox icon-input-checkbox-unchecked'></i></td>";
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

//单条数据 tr
function getDataTr(data,showFields)
{	
	var trHtml = "";
	
	if(data.isNew != "true")
		trHtml += "<tr class='bug-read'>";
	else
		trHtml += "<tr>";
		
	trHtml += "<td></td>";
					
	for(var j = 0 ;j < showFields.length; j++)
	{
		var content = getXMLStr(data[showFields[j].fieldId]);
		
		if(showFields[j].fieldId == "id")
		{
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
		return "<td style='text-align:left !important'><div class='td-content-nowrap'><a class = 'data-title' href='taskManagement.html?operation=read&taskid=" +id + "&templateId=" + templateId + "&filterId=" + curFilterId + "'" +" target='_blank'>" + content + "</a></div></td>";
	else
		return "<td style='text-align:left !important'><div class='td-content-nowrap'><a class = 'data-title' href='taskManagement.html?operation=read&taskid=" +id + "&templateId=" + templateId + "'" +" target='_blank'>" + content + "</a></div></td>";
}

/***********************绘制数据结束********************/

//显示filter数据 绘制表格
function showFilterData(filterData,groupField,showFields,backFields, reDrawHead)
{
	//画内容
	var dataHeadTable = showFilterDataTable(filterData,groupField,showFields);
	$("#main-grid-content").empty().html(dataHeadTable);
	//设置总页数 当前排序字段
	grid.setMaxPage(filterData.totalCount);
}

function initFilterData(filterId,page,sortField,sortType,reDrawHead)
{
	 var params = "filterId=" + filterId;
	 params += "&start=" + (page == undefined ? "0" : (page-1)*grid.getPageSize());
	 params += "&limit=" + grid.getPageSize();
	 params += "&sort=" + (sortField == undefined ? "" : sortField);
	 params += "&dir=" + (sortType == undefined ? "" : sortType);
	 var $searchType = $("#searchType");
	 if($searchType && $searchType.val())
		 params += "&searchType=" + $searchType.val();
	 
	 var $searchData = $("#searchWord");
	 if($searchData && $searchData.val())
		 params += "&searchData=" + $searchData.val();
	 params += "&searchConfig=" + encodeURIComponent(searchXml);
	 
	 var $templateId = $("#templateId");
	 if($templateId && $templateId.val())
		 params += "&templateId=" + $templateId.val();
	 
	 $.ajax({
		url:'filterManage/previewFilter.jsp',
		type:'post',
		dataType:'json',
		data:params,
		success:function(data){
			showFilterData(data, null, defaultHeader, false);
			showLoading(false);
		}
	});
}

function initTableHeader()
{
	var html = '<tr>';
	if(needCheckBox)
		html+='<th style="width: 20px;max-width: 20px;cursor: pointer;"><i class="i-checkbox icon-input-checkbox-unchecked"></i></th>';
	html += '<th style="width:5%" value="id">编号</th>';
	html += '<th style="width:30%" value="title">标题</th>';
	html += '<th style="width:10%" value="status">状态</th>';
	html += '<th style="width:10%" value="createUser">创建人</th>';
	html += '<th style="width:10%" value="assignUser">指派人</th>';
	html += '<th style="width:16%" value="createTime">创建时间</th>';
	html += '<th style="width:16%" value="createTime">修改时间</th>';
	html += '</tr>';
	$('#main-grid-header').html(html);
}

function queryFilterData(filterXml,cheekBox){
	needCheckBox = cheekBox;
	initMainGrid();	
	initTableHeader();
	getDefaultHeader();
	searchXml = filterXml;
	initFilterData('',1,undefined,undefined,"true",searchXml);  //查询第一页
}

function executeAddRefSubmit()
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
	
	$('#cfgRefQueryDiv').modal('hide');
	executeAddReference(dataIds,"field" + $("#fieldId").val());
}

function bindKeyDownEvent(){
	//搜索
	var $search = $("#searchWord");
	if($search){
		$search.keydown(function(e){
			if(e.keyCode==13){
				searchDatas(); //处理事件
			}
		});
	}
	
	var $searchTemplateId =  $("#searchTemplateId");
	if($searchTemplateId){
		$searchTemplateId.change(function(e){
			searchDatas();
		});
	}
}

$(function(){
	bindKeyDownEvent();
});
