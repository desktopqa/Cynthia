var tagArray = null;

/* click the expand icon*/
function tagExpandIconClik()
{
	if($("#my-tag").children('ul').is(":visible"))
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
		
		$("#ul-tag").append("<li class='tag' value='" + tagData[key].id + "'><a href='#'><div class='tagColorSpan' style=\"background-color:"+tagData[key].tagColor+ "\">&nbsp;</div>" + tagData[key].tagName+ "</a></li>");
	
		$("#tag-cfg-table").append("<tr><td value='" + tagData[key].id + "'>"+ tagData[key].tagName + "</td><td><a class='nomargin' href='#' onclick='modifyTagClassify(this)'>修改</a>&nbsp;&nbsp;<a href='#' onclick='removeTagClassify(this)'>删除</a></td></tr>");
	}
	if(tagArray.length > 0)
		$("#ul-tag").append("<li class='divider'></li>");
	$("#ul-tag").append("<li><a href='#' data-toggle='modal' data-target='#cfgTagDiv'>管理标签</a></li>");
}

function OpenTagConfigDiv(){
	$('#cfgTagDiv').modal('show');
}

function tagIdMoveOut(){
	var dataIds = grid.findAllSelectedRows();
	if(dataIds.length == 0)
	{
		showInfoWin("error","未选中任何数据，移出失败！");
		return;
	}
	
	var tagId = $("#curTagId").val();
	
	$.ajax({
		url: 'tag/deleteTagData.do',
		type:'POST',
		data:{'dataIds': dataIds, 'tagId' : tagId},
		success: function(data){
			if(data != "" && data != "false")
			{
				showInfoWin("success","标签数据移出成功！");
				grid.refreshGrid('false');
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
	var toTagId = $(this).val();//移动到的tag
	var dataIds = new Array();
	dataIds.push(task.id);
	$.ajax({
		url: 'tag/addTagData.do',
		type:'POST',
		data:{'dataIds': dataIds,  'toTagId' : toTagId},
		success: function(data){
			if(data != "" && data != "false")
			{
				showInfoWin("success","标签数据移动成功！");
				if(!inArray(dataTagArray,toTagId)){
					dataTagArray.push(toTagId);
					changeTitleAndTag();
				}
				if(window.opener&&window.opener.grid)
				{
					window.opener.grid.refreshGrid();
				}
			}
			else
			{
				showInfoWin("error","标签数据移动失败！");
			}
		}
	});
}

//移出所有标签数据
function tagDataMoveOut()
{
	if(dataTagArray.length == 0)
	{
		showInfoWin('error','该数据目前没有任何标签!');
		return;
	}	
	var dataIds = new Array();
	dataIds.push(task.id);
	
	$.ajax({
		url: 'tag/deleteTagData.do',
		type:'POST',
		data:{'dataIds': dataIds},
		success: function(data){
			if(data != "" && data != "false")
			{
				showInfoWin("success","标签数据移除成功！");
				dataTagArray = new Array();
				changeTitleAndTag();
				if(window.opener&&window.opener.grid)
				{
					window.opener.grid.refreshGrid();
					window.opener.initFilterMenu();
				}
			}	
			else
				showInfoWin("error","标签数据移除失败！");
		}
	});
}

//移出数据单个标签
function tagDatasMoveOut(datas,tagId)
{
	$.ajax({
		url: 'tag/deleteTagData.do',
		type:'POST',
		data:{'dataIds': datas, 'tagId' : tagId},
		success: function(data){
			if(data != "" && data != "false")
			{
				if(inArray(dataTagArray,tagId)){
					dataTagArray.splice($.inArray(tagId,dataTagArray),1);
					changeTitleAndTag();
				}
				showInfoWin("success","标签数据移出成功！");
			}
			else
			{
				showInfoWin("error","标签数据移出失败！");
			}
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
}

//打开标签数据
function openTagFilter(tagId,page,sortField,sortType,reDrawHead)
{
	$("#curTagId").val(tagId);
	$("#filterId").val("");
	
	//隐藏过滤器相关菜单
	$.each($(".filter-show"),function(i,node){
		$(node).hide();
	});
	
	$("#tagMoveOut").show();
	document.title= tagArray[tagId].tagName;
	$('#left-tree , #allFiltersDiv').find('.filter').removeClass('active_link');
	$("#left-tag-list").find("li[value='"+tagId+"']").addClass('active_link');
	grid.refreshGrid();
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
			}else{
				queryData("", page,sortField,sortType,reDrawHead,searchType,dataArray);
			}
		},
		error: function(data){
			showInfoWin("error","服务器异常!");
		}
	});
	
}
/**---左侧树相关函数结束---**/