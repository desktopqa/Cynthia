/*
 *@description 表单编辑所涉及的js
 *@author liuyanlei
 */
var templateId = "";//3565 211744

/*main-container*/
function onWindowResize()
{
	var windowHeight = $(window).height();
	var windowWidth  = $(window).width();
	$(".main-container").height(windowHeight - 100);
	$(".main-container").width(windowWidth - 250);
}

function bindDragableEvents()
{
	//拖拽布局列表
	$(".sidebar-nav .lyrow").draggable({
		connectToSortable : '.main-container',
		helper:"clone",
		handle : ".drag",
		start: function(e,t) {
			$("#main-container").find("div[flagId='newFieldRow']").remove();
            $(e.target).attr("flagId","newFieldRow");
		},
		drag: function(e, t) {
			t.helper.width(400);
		},
		stop: function(e, t) {
		}
	});
	
	//拖拽普通元素
	$(".sidebar-nav .box").draggable({
		connectToSortable: ".column",
		helper: "clone",
		handle: ".drag",
		start: function(e,t) {
			$("#main-container").find("div[fieldId='newField']").remove();
            $(e.target).attr("fieldId","newField");
		},
		drag: function(e, t) {
			t.helper.width(200);
		},
        over : function(e,t)
        {
        },
		stop: function(e,t)
        {
			
		}
	});
	initContainer();
}

function initContainer(){
	$(".main-container, .main-container .column").sortable({
		connectWith: ".column",
		opacity: .35,
		handle: ".drag",
		start: function(e,t) {
			t.helper.width(200);
		},
		stop: function(e,t) {
			//接收到一个新拖入的字段时出发，所以要判断是新建的还是仅仅是位置的拖拽
	           var item = t.item;
	           var fieldId = $(item).attr("fieldId");
	           var fieldType = $(item).attr("type");
	           var fieldDataType = $(item).attr("dataType");

	           if(fieldId == "newField"&&$(item).hasClass("box"))
	           {
	               //新建一个field
	               $("#editTemplateFieldDiv").modal("show");
	               initCreateFieldModal(fieldType,fieldDataType);
	           }else if(fieldId != null)
	           {
	        	   moveField(fieldId);
	           }else
	           {
	        	   var newFieldRow = $("#main-container").find("div[flagId='newFieldRow']");
	        	   if($(newFieldRow).parents(".lyrow").length > 0)
	        	   {
	        		   alert("行无法嵌套!");
	        		   $(newFieldRow).remove();
	        		   return;
	        	   }
	        	   
	        	   var newFieldRowIndex = $(newFieldRow).prevAll().length;
	        	   var newFieldRowColumnCount = $(newFieldRow).find(".column").length;
	        	   addFieldRow(newFieldRowIndex,newFieldRowColumnCount);
	           }
		},
        receive : function(e,t)
        {
        }
	});
	
}

function bindClickEvents()
{
	/*左侧收起 展开*/
	$(".nav-header").on("click",function() {
		if($(this).find("i").hasClass("icon-plus")){
			$(this).find("i").removeClass("icon-plus").addClass("icon-minus");
			$(this).next().slideDown();
		}else{
			$(this).find("i").removeClass("icon-minus").addClass("icon-plus");
			$(this).next().slideUp();
		}
	});
	
	//绑定删除布局或者元素事件
	$(".main-container").delegate(".remove", "click", function(e) {
		e.preventDefault();
		
		if(!window.confirm("删除后将无法恢复，是否继续?"))
		{
			return;
		}
		
		if(!window.confirm("执行该操作后将删除该字段所有数据及日志，请问是否仍然继续?"))
		{
			return;
		}
		
		var clickFieldId = $(this).parent("div").attr("fieldId");
		if(clickFieldId != null)
		{
			//删除一个字段
			var controledField = getControlField(clickFieldId);
			if(controledField){
				alert(controledField.name + "受该字段的控制,请先删除" + controledField.name + "字段!");
				return;
			}
			removeField(clickFieldId);
			$(this).parent().remove();
			if (!$(".main-container .lyrow").length > 0) {
				cleanMainContainer();
			}
		}else
		{
			//删除某一行
			var parentDiv = $(this).parent("div");
			if($(parentDiv).find(".box").length > 0)
			{
				alert("该行下还有未移动的元素，请先删除或者移动这些元素!");
				return;
			}else
			{
				var curIndex = $(parentDiv).prevAll().length;
				removeFieldRow(curIndex);
				$(this).parent().remove();
			}
		}
		
	});

    //绑定编辑按钮
    $("#main-container").on("click",".btn-edit",function(){
        var clickFieldId = $(this).parent("div").attr("fieldId");
        initEditFieldModal(clickFieldId);
    });
    
    //编辑option的按钮
    $("#main-container").on('click','.btn-option',function(){
    	var clickFieldId = $(this).parent("div").attr("fieldId");
    	initEditFieldOptions(clickFieldId);
    });
}


function getControlField(fieldId){
	for(var i = 0 ; i< rows.length ; i ++){
		for(var j = 0 ; j<rows[i].length; j ++){
			for(var m = 0 ; m < rows[i][j].length ; m ++){
				if(rows[i][j][m] &&rows[i][j][m].id == fieldId)
					continue;
				if(rows[i][j][m] &&rows[i][j][m].controlFieldId == fieldId){
					return rows[i][j][m];
				}
			}
		}
	}
}
function cleanMainContainer()
{
	$(".main-container").empty();
}


function bindFieldNameTip()
{
	$("#main-container").delegate('.control-label','hover',function(e){
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

function bindEvents()
{
	$(window).resize(onWindowResize);
	bindDragableEvents();
	bindClickEvents();
	bindFieldNameTip();
}

$(function(){
	templateId = request("templateId");
	bindEvents();
	onWindowResize();
	if(!templateId||templateId == "")
	{
		drawEmptyTemplatePage();
	}else
	{
		initTemplateEdit(templateId);
	}
	
    $("#editOptionsTable>tbody").sortable({
        revert: true,
        axis:'y',
        placeholder: "ui-state-highlight",
        cursor: 'move' //拖动的时候鼠标样式 
    });
});


/*template related*/
var rows = new Array();
var actions = new Array();
var roles = new Array();
var filters = new Array();
var stats = new Array();

var field = null;

function initTemplateEdit(templateId)
{
	showLoading(true);
	var param = "id="+templateId;
	rows = new Array();
	actions = new Array();
	roles = new Array();
	$.ajax({
		url : 'template/get_Template_xml.jsp',
		dataType:'xml',
		data:param,
		success : onInitTemplateEditComplete
	});
}

function onInitTemplateEditComplete(response)
{
	cleanMainContainer();
	var rootNode = $(response).find("root");
	eval("var isError = " + $(rootNode).find("isError").text());
	if(isError)
	{
		alert("表单初始化错误!");
		return;
	}
	
	rows = new Array();
	actions = new Array();
	roles = new Array();
	filters = new Array();
	stats = new Array();
	
	
	var templateNode = $(rootNode).find("template");
	var templateName = $(templateNode).children("name").text();
	document.title = templateName;
	var rowNodes     = $(templateNode).children("layout").children("rows").children("row");
	
	var actionNodes  = $(rootNode).children("actions").children("action");
	var statNodes    = $(rootNode).children("stats").children("stat");
	var roleNodes    = $(rootNode).children("roles").children("role");
	var filterNodes  = $(rootNode).children("filters").children("filter");
	
	$(filterNodes).each(function(idx,filterNode){
		filters[idx] = new Object();
		filters[idx].id = $(filterNode).children("id").text();
		filters[idx].name = $(filterNode).children("name").text();
	});

	
	$(statNodes).each(function(idx,statNode){
		stats[idx] = new Object();
		stats[idx].id = $(statNode).children("id").text();
		stats[idx].name = $(statNode).children("name").text();
	});
	
	$(roleNodes).each(function(idx,roleNode){
		if($(roleNode).children("id").text() != '82') {  //everyone取消
			roles[roles.length] = {
					id : $(roleNode).children("id").text(),
					name : $(roleNode).children("name").text()
			};
		}
	});
	
	$(actionNodes).each(function(idx,actionNode){
		actions[idx] = new Object();
		actions[idx].id = $(actionNode).children("id").text();
		actions[idx].name = $(actionNode).children("name").text();

		actions[idx].roles = new Array();
		var actionRoleNodes = $(actionNode).children("roles").children("role");
		$(actionRoleNodes).each(function(jdx,roleNode){
			actions[idx].roles[jdx] = new Object();
			actions[idx].roles[jdx].id = $(roleNode).children("id").text();
			actions[idx].roles[jdx].name = $(roleNode).children("name").text();
		});
	});
	
	//template and fields
	$(rowNodes).each(function(idx,rowNode){
		rows[idx] = new Array();
		var columnNodes = $(rowNode).children("column");
		$(columnNodes).each(function(cdx,columnNode){
			rows[idx][cdx] = new Array();
			var fieldNodes = $(columnNode).children("field");
			$(fieldNodes).each(function(fdx,fieldNode){
				rows[idx][cdx][fdx] = initFieldByNode(fieldNode);
			});
		});
	});

	drawTemplatePage();
	showLoading(false);
}


function initFieldByNode(fieldNode)
{
	var tempField = new Object();
	tempField.id = $(fieldNode).children("id").text();
	tempField.name = $(fieldNode).children("name").text();
	
	tempField.description = $(fieldNode).children("description").text();
	tempField.timestampFormat = $(fieldNode).children("timeFormat").text();
	tempField.dateCurTime = $(fieldNode).children("dateCurTime").text();
	tempField.fieldTip = $(fieldNode).children("fieldTip").text();
	tempField.fieldSize = $(fieldNode).children("fieldSize").text();
	tempField.type = $(fieldNode).children("type").text(); 
	tempField.dataType = $(fieldNode).children("dataType").text(); 
	tempField.controlFieldId =  $(fieldNode).children("controlFieldId").text(); 
	tempField.x = $(fieldNode).children("x").text();  
	tempField.y = $(fieldNode).children("y").text(); 
	tempField.height = $(fieldNode).children("height").text();  
	tempField.width = $(fieldNode).children("width").text();  
	tempField.align = $(fieldNode).children("align").text();  
	tempField.valign = $(fieldNode).children("valign").text();
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	tempField.controlHiddenFieldId = $(fieldNode).children("controlHiddenFieldId").text();
	tempField.controlHiddenFields = new Array();
	var controlHiddenFieldsNodes = $(fieldNode).children("controlHiddenFields").children("controlHiddenField"); 
	$(controlHiddenFieldsNodes).each(function(idx,controlHiddenFieldsNode){
		tempField.controlHiddenFields[idx] = $(controlHiddenFieldsNode).text();
	});
	
	tempField.controlHiddenStates = new Array();
	var controlHiddenStatesNodes = $(fieldNode).children("controlHiddenStates").children("controlHiddenState");
	$(controlHiddenStatesNodes).each(function(idx,controlHiddenStatesNode){
		tempField.controlHiddenStates[idx] = $(controlHiddenStatesNode).text();
	});
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	tempField.controlRoleIds = new Array();
	var controlRoleIdNodes = $(fieldNode).children("controlRoleIds").children("controlRoleId");
	$(controlRoleIdNodes).each(function(idx,controlRoleIdNode){
		tempField.controlRoleIds[idx] = $(controlRoleIdNode).text();
	});
	
	tempField.controlOptionIds = new Array();
	var controlOptionIdNodes = $(fieldNode).children("controlOptionIds").children("controlOptionId");
	$(controlOptionIdNodes).each(function(idx,controlOptionIdNode){
		tempField.controlOptionIds[idx] = $(controlOptionIdNode).text();
	});
	
	tempField.options = new Array();
	var optionNodes = $(fieldNode).children("options").children("option");
	$(optionNodes).each(function(idx,optionNode){
		tempField.options[idx] = new Object();
		tempField.options[idx].id = $(optionNode).children("id").text();
		tempField.options[idx].name = $(optionNode).children("name").text();
		tempField.options[idx].controlOptionId = $(optionNode).children("controlOptionId").text();
		tempField.options[idx].forbidden = $(optionNode).children("forbidden").text();
		tempField.options[idx].indexOrder = $(optionNode).children("indexOrder").text();
	});
	tempField.defaultValue = $(fieldNode).children("defaultValue").text();
	tempField.controlActionIds = new Array();
	var controlActionIdNodes =  $(fieldNode).children("controlActionIds").children("controlActionId");
	$(controlActionIdNodes).each(function(idx,controlActionIdNode){
		tempField.controlActionIds[idx] = $(controlActionIdNode).text();
	});
	
	tempField.actionIds = new Array();
	var actionIdNodes = $(fieldNode).children("actionIds").children("actionId");
	$(actionIdNodes).each(function(idx,actionIdNode){
		tempField.actionIds[idx] = $(actionIdNode).text();
	});
	
	return tempField;
}

//开始画页面
function drawEmptyTemplatePage()
{
	var layoutHtml = "<div class='lyrow ui-draggable' style='display: block;'>"
					+ 	"<a href='#close' class='remove label label-important'><i class='icon-remove icon-white'></i>删除</a>"
					+ 	"<div class='preview'>"
					+ 		"<span>一行三列</span>"
					+ 	"</div>"
					+ 	"<span class='drag label'>"
					+ 		"<i class='icon-move icon-white'></i>拖动"
					+ 	"</span>"
					+ 	"<div class='view'>"
					+ 		"<div class='row-fluid clearfix'>"
					+ 			"<div class='span4 column mini-col ui-sortable'></div>"
					+ 			"<div class='span4 column mini-col ui-sortable'></div>"
					+ 			"<div class='span4 column mini-col ui-sortable'></div>"
					+ 		"</div>"
					+ "	</div>"
					+ "</div>";
	//$("#main-container").html(layoutHtml);
	initContainer();
}

//初始化要生成的表单字段
function initSingleInputFieldHtml(field)
{
	var html = "<div class='box box-element ui-draggable' style='display: block;' fieldId="+field.id+">"
			+ 	"<a href='#close' class='remove label label-important'><i class='icon-remove icon-white'></i>删除</a> "
			+ 	"<span class='drag label'><i class='icon-move icon-white'></i>拖动</span>"
			+ 	"<span class='configuration btn-edit label label-success'>"
			+ 		"<i class='icon-edit icon-white'></i>编辑"	
			+ 	"</span>"
			+ 	"<div class='preview'></div>"
			+ 	"<div class='view control-group form-horizontal'>"
			+ 		"<label class='control-label'>"+field.name+"</label>"
			+ 		"<div class='controls'>"
			+ 			"<input type='text' id='field"+field.id+"' placeholder='' value='"+field.defaultValue+"' class='singleLine'>"
			+ 		"</div>"
			+ 	"</div>"
			+ "</div>";
	
	return html;
}

function initTextInputFieldHtml(field)
{
	var html = "<div class='box box-element ui-draggable' style='display: block;' fieldId="+field.id+">"
	+ 	"<a href='#close' class='remove label label-important'><i class='icon-remove icon-white'></i>删除</a> "
	+ 	"<span class='drag label'><i class='icon-move icon-white'></i>拖动</span>"
	+ 	"<span class='configuration btn-edit label label-success'>"
	+ 		"<i class='icon-edit icon-white'></i>编辑"	
	+ 	"</span>"
	+ 	"<div class='preview'></div>"
	+ 	"<div class='view control-group form-horizontal'>"
	+ 		"<label class='control-label'>"+field.name+"</label>"
	+ 		"<div class='controls'>";
	if(field.fieldSize == "2")
		html += "<textarea id='field"+field.id+"' rows='' cols='' class='multiLine large'>"+field.defaultValue+"</textarea>";
	else
		html += "<textarea id='field"+field.id+"' rows='' cols='' class='multiLine'>"+field.defaultValue+"</textarea>";
			
	html = html + "</div>"
	+ 	"</div>"
	+ "</div>";

	return html;
}

function initSingleSelectionFieldHtml(field)
{
	var html = "<div class='box box-element ui-draggable' style='display: block;' fieldId="+field.id+">"
	+ 	"<a href='#close' class='remove label label-important'><i class='icon-remove icon-white'></i>删除</a> "
	+ 	"<span class='drag label'><i class='icon-move icon-white'></i>拖动</span>"
	+ 	"<span class='configuration btn-edit label label-success'>"
	+ 		"<i class='icon-edit icon-white'></i>编辑"	
	+ 	"</span>"
	+ 	"<span class='configuration btn-option label label-info'>"
	+ 		"<i class='icon-cog icon-white'></i>配置选项"	
	+ 	"</span>"
	+ 	"<div class='preview'></div>"
	+ 	"<div class='view control-group form-horizontal'>"
	+ 		"<label class='control-label'>"+field.name+"</label>"
	+ 		"<div class='controls'>"
	+ 			"<select id='field"+field.id+"' onchange='checkSingleSelect(this)' class='singleLine'>"
    +               "<option value=''>--请选择--</option>";
				for(var i=0 ; i<field.options.length ; i++)
				{
					if(field.options[i].forbidden == "f_permit"){
	                    if(field.defaultValue && field.defaultValue == field.options[i].id)
						    html += "<option value='"+field.options[i].id+"' forbidden='"+field.options[i].forbidden+"' selected>"+field.options[i].name+"</option>";
	                    else
	                        html += "<option value='"+field.options[i].id+"' forbidden='"+field.options[i].forbidden+"'>"+field.options[i].name+"</option>";
					}
				}
	html +=		"</select>"
	+ 		"</div>"
	+ 	"</div>"
	+ "</div>";

	return html;
}

function initMultiSelectionFieldHtml(field)
{
    var defaultValueArr = new Array();
    if(field.defaultValue)
        defaultValueArr = field.defaultValue.split(",");

	var html = "<div class='box box-element ui-draggable' style='display: block;' fieldId="+field.id+">"
	+ 	"<a href='#close' class='remove label label-important'><i class='icon-remove icon-white'></i>删除</a> "
	+ 	"<span class='drag label'><i class='icon-move icon-white'></i>拖动</span>"
	+ 	"<span class='configuration btn-edit label label-success'>"
	+ 		"<i class='icon-edit icon-white'></i>编辑"	
	+ 	"</span>"
	+ 	"<span class='configuration btn-option label label-info'>"
	+ 		"<i class='icon-edit icon-white'></i>配置选项"	
	+ 	"</span>"
	+ 	"<div class='preview'></div>"
	+ 	"<div class='view control-group form-horizontal'>"
	+ 		"<label class='control-label'>"+field.name+"</label>"
	+ 		"<div class='controls'>"
	+ 			"<select multiple id='field"+field.id+"' onchange='returnZero(this)' class='singleLine'>"
    +            "<option value='' "+(defaultValueArr.length == 0?"selected":"")+">--请选择--</option>";
				for(var i=0 ; i<field.options.length ; i++)
				{
					if(field.options[i].forbidden == "f_permit"){
	                    var selected = false;
	                    for(var j = 0;j<defaultValueArr.length ; j++)
	                    {
	                        if(defaultValueArr[j] == field.options[i].id)
	                            selected = true;
	                    }
	                    if(selected)
						    html += "<option value='"+field.options[i].id+"' forbidden='"+field.options[i].forbidden+"' selected>"+field.options[i].name+"</option>";
	                    else
	                        html += "<option value='"+field.options[i].id+"' forbidden='"+field.options[i].forbidden+"'>"+field.options[i].name+"</option>";
					}
				}
	html +=		"</select>"
	+ 		"</div>"
	+ 	"</div>"
	+ "</div>";

	return html;
}

function initTimeStampFieldHtml(field)
{
	var html = "<div class='box box-element ui-draggable' style='display: block;' fieldId="+field.id+">"
	+ 	"<a href='#close' class='remove label label-important'><i class='icon-remove icon-white'></i>删除</a> "
	+ 	"<span class='drag label'><i class='icon-move icon-white'></i>拖动</span>"
	+ 	"<span class='configuration btn-edit label label-success'>"
	+ 		"<i class='icon-edit icon-white'></i>编辑"	
	+ 	"</span>"
	+ 	"<div class='preview'></div>"
	+ 	"<div class='view control-group form-horizontal'>"
	+ 		"<label class='control-label'>"+field.name+"</label>"
	+ 		"<div class='controls'>"
	+ 			"<input type='text' id='field"+field.id+"' placeholder='' class='singleLine'>"
	+ 		"</div>"
	+ 	"</div>"
	+ "</div>";
	
	return html;
}

function initAttachmentFieldHtml(field)
{
	var html = "<div class='box box-element ui-draggable' style='display: block;' fieldId="+field.id+">"
	+ 	"<a href='#close' class='remove label label-important'><i class='icon-remove icon-white'></i>删除</a> "
	+ 	"<span class='drag label'><i class='icon-move icon-white'></i>拖动</span>"
	+ 	"<span class='configuration btn-edit label label-success'>"
	+ 		"<i class='icon-edit icon-white'></i>编辑"	
	+ 	"</span>"
	+ 	"<div class='preview'></div>"
	+ 	"<div class='view control-group form-horizontal'>"
	+ 		"<label class='control-label'>"+field.name+"</label>"
	+ 		"<div class='controls'>"
	+ 			"<select id='field"+field.id+"' multiple class='multiLine'>"
	+    		"</select>"
	+ 		"</div>"
	+ 	"</div>"
	+ "</div>";
	
	return html;
}

function initSingleReferenceFieldHtml(field)
{
	var html = "<div class='box box-element ui-draggable' style='display: block;' fieldId="+field.id+">"
	+ 	"<a href='#close' class='remove label label-important'><i class='icon-remove icon-white'></i>删除</a> "
	+ 	"<span class='drag label'><i class='icon-move icon-white'></i>拖动</span>"
	+ 	"<span class='configuration btn-edit label label-success'>"
	+ 		"<i class='icon-edit icon-white'></i>编辑"	
	+ 	"</span>"
	+ 	"<div class='preview'></div>"
	+ 	"<div class='view control-group form-horizontal'>"
	+ 		"<label class='control-label'>"+field.name+"</label>"
	+ 		"<div class='controls'>"
	+ 			"<select id='field"+field.id+"' class='singleLine'>"
	+	        "</select>"
	+ 		"</div>"
	+ 	"</div>"
	+ "</div>";

	return html;
}

function initMultiReferenceFieldHtml(field)
{
	var html = "<div class='box box-element ui-draggable' style='display: block;' fieldId="+field.id+">"
	+ 	"<a href='#close' class='remove label label-important'><i class='icon-remove icon-white'></i>删除</a> "
	+ 	"<span class='drag label'><i class='icon-move icon-white'></i>拖动</span>"
	+ 	"<span class='configuration btn-edit label label-success'>"
	+ 		"<i class='icon-edit icon-white'></i>编辑"	
	+ 	"</span>"
	+ 	"<div class='preview'></div>"
	+ 	"<div class='view control-group form-horizontal'>"
	+ 		"<label class='control-label'>"+field.name+"</label>"
	+ 		"<div class='controls'>"
	+ 			"<select id='field"+field.id+"' multiple class='multiLine'>"
	+    		"</select>"
	+ 		"</div>"
	+ 	"</div>"
	+ "</div>";
	
	return html;
}

function initFieldColumnHtml(column)
{
	var columnHtml = "";
	for(var i = 0; i< column.length; i++)
	{
		var field = column[i];
		if(field.type == 't_input')
		{
			if(field.dataType == "dt_text")
			{
				columnHtml += initTextInputFieldHtml(field);
			}else
			{
				columnHtml += initSingleInputFieldHtml(field);
			}
		}else if(field.type == 't_selection')
		{
			if(field.dataType == "dt_single")
			{
				columnHtml += initSingleSelectionFieldHtml(field);
			}else
			{
				columnHtml += initMultiSelectionFieldHtml(field);
			}
		}else if(field.type == 't_reference')
		{
			if(field.dataType == 'dt_single')
			{
				columnHtml += initSingleReferenceFieldHtml(field);
			}else
			{
				columnHtml += initMultiReferenceFieldHtml(field);
			}
		}else if(field.type == 't_attachment')
		{
			columnHtml += initAttachmentFieldHtml(field);
		}
		
	}
	
	return columnHtml;
}

function initFieldRowHtml(row)
{
	var rowHtml = "<div class='lyrow ui-draggable' style='display: block;'>"
				+ 	"<a href='#close' class='remove label label-important'><i class='icon-remove icon-white'></i>删除</a>"
				+ "<div class='preview'></div>"
				+ 	"<span class='drag label'>"
				+ 		"<i class='icon-move  icon-white'></i>拖动"
				+ 	"</span>"
				+ 	"<div class='view'>"
				+ 		"<div class='row-fluid clearfix'>";
	var columnCount = 12/row.length;
	
	for(var i = 0;i < row.length; i++)
	{
		rowHtml += "<div class='span"+columnCount+" column large-col ui-sortable'>";
		rowHtml += 	initFieldColumnHtml(row[i]);
		rowHtml += "</div>";
	}
	
	rowHtml += 		"</div>"
			+  	"</div>"
			+  "</div>";
	return rowHtml;
}

function drawTemplatePage()
{
	if(rows.length == 0)
	{
		drawEmptyTemplatePage();
		return;
	}
	
	var containerHtml = "";
	for(var i = 0; i < rows.length;i++)
	{
		containerHtml += initFieldRowHtml(rows[i]);
	}
	$("#main-container").html(containerHtml);

    for(var i = 0; i < rows.length; i++)
    {
        var columns = rows[i];
        for(var j = 0; j < columns.length; j++)
        {
            var fields = columns[j];
            for(var k = 0; k < fields.length; k++)
            {
                var iField = fields[k];
                if(iField.type == 't_selection'&&iField.dataType == 'dt_single')
                {
                    checkSingleSelect($("#field"+iField.id));
                }
            }
        }
    }

	initContainer();
}

function getFieldById(fieldId)
{
    for(var i = 0;i<rows.length ;i++)
    {
        var row = rows[i];
        for(var j = 0;j < row.length;j++)
        {
            var column = row[j];
            for(var k = 0; k<column.length; k++)
            {
                if(column[k].id == fieldId)
                    return column[k];
            }
        }
    }
}

function getActionById(actionId)
{
    for(var i = 0; i<actions.length; i++)
    {
        if(actions[i].id == actionId)
            return actions[i];
    }
}

function getFieldOptionById(field,fieldOptionId)
{
	var options = field.options;
	for(var i = 0 ; i < options.length ; i++)
	{
		if(options[i].id == fieldOptionId)
			return options[i];
	}
	
	return null;
}

/*end of draw template*/

/*动态操作以及配置相关函数*/
function bindCancelEditFieldEvt(isCreate)
{
    if(isCreate)
        $("#editTemplateFieldDiv").on("click","#cancelEditField",cancelAddField);
    else
        $("#editTemplateFieldDiv").on("click","#cancelEditField",cancelEditField);
}

function clearFieldModal()
{
    $("#editTemplateFieldDiv .control_show").hide();
    $("#input_fieldName").val("");
    $("#input_fieldDescription").val("");
    $("#input_fieldTip").val("");
    $("#input_fieldSize").val(1);
    $("#input_defaultValue").val("");
    $('#date_curtime').val('false');
    $("#select_controlFieldId option:gt(0)").remove();
    $("#select_controlOptionId option:gt(0)").remove();
    $("#select_controlActionId").empty();
    $("#controlRoles_table").empty();
    $("#fieldType").val("");
    $("#fieldDataType").val("");
}

function cancelAddField()
{
    clearFieldModal();
    $("#main-container").find("div[fieldId='newField']").remove();
    $("#editTemplateFieldDiv").modal('hide');
}

function cancelEditField()
{
    clearFieldModal();
    $("#editTemplateFieldDiv").modal('hide');
}

function initCreateFieldModal(fieldType,fieldDataType)
{
    clearFieldModal();
    $('#editTemplateFieldDiv .' + fieldDataType).show();
    bindCancelEditFieldEvt(true);
    //初始化类型和数据类型字段
    $("#fieldType").val(fieldType);
    $("#fieldDataType").val(fieldDataType);
    $("#select_controlFieldId")[0].disabled = false;
    field = null;
    //初始化控制字段
    for(var i = 0;i<rows.length;i++)
    {
        var columns = rows[i];
        for(var j = 0; j < columns.length ; j++)
        {
            var columnFields = columns[j];
            for(var k = 0 ; k < columnFields.length ;k++)
            {
                var tempField = columnFields[k];
                if(tempField.id!=null&&tempField.type == 't_selection' && tempField.dataType == 'dt_single')
                {
                    $("#select_controlFieldId").append("<option value='"+tempField.id+"'>"+tempField.name+"</option>");
                }
            }
        }
    }
    initControlOptions();
    initDefaultValueTd();
}

function initEditFieldModal(fieldId)
{
    clearFieldModal();
    bindCancelEditFieldEvt(false);
    field = getFieldById(fieldId);
    if(field.dataType){
    	$('#editTemplateFieldDiv .' + field.dataType).show();
    }
    //初始化类型和数据类型字段
    $("#fieldType").val(field.type);
    $("#fieldDataType").val(field.dataType);
    $("#input_timestampFormat").val(field.timestampFormat);
    $("#date_curtime").val(field.dateCurTime);
    $("#fieldId").val(field.id);
    $("#input_fieldName").val(field.name);
    $("#input_fieldDescription").val(field.description);
    $("#input_fieldTip").val(field.fieldTip);
    $("#input_fieldSize").val(field.fieldSize);
    
    if(field.dataType == 'dt_text')
    {
    	$("#input_fieldSize").parent().parent().show();
    }else
    {
    	$("#input_fieldSize").parent().parent().hide();
    }
    
    initControlOptions();
    initDefaultValueTd();
    
    //初始化控制字段
    for(var i = 0;i<rows.length;i++)
    {
        var columns = rows[i];
        for(var j = 0; j < columns.length ; j++)
        {
            var columnFields = columns[j];
            for(var k = 0 ; k < columnFields.length ;k++)
            {
                var tempField = columnFields[k];
                if(tempField.id!=null&&tempField.type == 't_selection' && tempField.dataType == 'dt_single')
                {
                    if(tempField.id == field.controlFieldId)
                        $("#select_controlFieldId").append("<option value='"+tempField.id+"' selected>"+tempField.name+"</option>");
                    else
                        $("#select_controlFieldId").append("<option value='"+tempField.id+"'>"+tempField.name+"</option>");
                }
            }
        }

    }
    
    $("#select_controlFieldId")[0].disabled = true;
    if(field.controlFieldId != "" && field.controlOptionIds.length > 0)
    {
        select_controlOptionId.options[0].selected = false;
        for(var i = 1; i < select_controlOptionId.options.length ; i++)
        {
            for(var j = 0 ; j< field.controlOptionIds.length ; j++)
            {
                if(field.controlOptionIds[j] == select_controlOptionId.options[i].value)
                {
                    select_controlOptionId.options[i].selected = true;
                    break;
                }
            }
        }
    }

    if(field.controlFieldId == "")
    {
        for(var i = 0; i < field.controlRoleIds.length ; i++)
        {
            var roleId = field.controlRoleIds[i].split("_")[0];
            var must = 0;
            if(field.controlRoleIds[i].split("_").length > 1)
            {
                must = field.controlRoleIds[i].split("_")[1];
            }
            var editRoles = document.getElementsByName("edit_role_" + roleId);
            if(editRoles.length > 0)
                editRoles[parseInt(must) + 1].checked = true;
        }

        for(var i = 0; i < field.controlActionIds.length; i++)
        {
            var actionId = field.controlActionIds[i].split("_")[0];
            var roleId = field.controlActionIds[i].split("_")[1];
            var must = field.controlActionIds[i].split("_")[2];
            var actionRoles = document.getElementsByName("action_" + actionId + "_role_" + roleId);
            if(actionRoles.length > 0)
                actionRoles[parseInt(must) + 1].checked = true;
        }
    }
    $("#editTemplateFieldDiv").modal('show');
}

function initControlOptions()
{
    var fieldType = $("#fieldType").val();
    var fieldDataType = $("#fieldDataType").val();
    var controlFieldId = $("#select_controlFieldId").val();

    if(field!=null)
    {
        fieldType = field.type;
        fieldDataType = field.dataType;
        controlFieldId = field.controlFieldId;
    }

    if(controlFieldId == "")
    {
        $("#controlRoles_table").empty();
        $("#select_controlOptionId_div").hide();
        $("#select_controlActionId_div").show();
        clearControlOptions();
        for(var i = 0;i<actions.length; i++)
        {
            var action = actions[i];
            var isAdd = true;
            for(var j = 0; field != null && j < field.actionIds.length; j++)
            {
                if(field.actionIds[j] == actions[i].id)
                {
                    isAdd = false;
                    break;
                }
            }
            if(isAdd)
            {
                $("#select_controlActionId").append("<option value='"+action.id+"'>"+action.name+"</option>")
            }else
            {
                //初始化控制动作这一块
                var controlActionTr = "<tr id = 'tr_action_"+action.id+"'>";
                controlActionTr += "<td><a href='#' onclick='return displayActionRoles("+action.id+");'>"+action.name+"</a></td>";
                controlActionTr += "<td><a href='#' onclick='return removeControlAction("+action.id+");'>移除</a></td>";
                controlActionTr += "</tr>";

                controlActionTr += "<tr id='tr_action_"+action.id+"_roles' style='display:none'>";
                controlActionTr += "<td colspan='2'>";
                controlActionTr += "<table class='noborder'>";
                for(var j = 0; j < action.roles.length; j++)
                {
                    var actionRole = action.roles[j];
                    if(actionRole.id != '82'){  //everyone
	                    controlActionTr += "<tr>";
	                    controlActionTr += "<td>"+actionRole.name+"</td>";
	                    controlActionTr += "<td>";
	                    controlActionTr += "<input name='action_" + action.id + "_role_" + action.roles[j].id + "' type='radio' value='-1' checked>不可填";
	                    controlActionTr += "<input name='action_" + action.id + "_role_" + action.roles[j].id + "' type='radio' value='0'>选填";
	                    controlActionTr += "<input name='action_" + action.id + "_role_" + action.roles[j].id + "' type='radio' value='1'>必填";
	                    controlActionTr += "</td>";
	                    controlActionTr += "</tr>";
                    }
                }
                controlActionTr += "</table>";
                controlActionTr += "</td></tr>";
                $("#controlRoles_table").append(controlActionTr);
            }
        }

        //增加编辑动作
        var controlEditActionTr = "<tr id = 'tr_action_edit'>";
        controlEditActionTr += "<td><a href='#' onclick='return displayActionRoles(\"edit\");'>编辑</a></td>";
        controlEditActionTr += "<td>-</td>";
        controlEditActionTr += "</tr>";

        controlEditActionTr += "<tr id='tr_action_edit_roles' style='display:none'>";
        controlEditActionTr += "<td colspan='2'>";
        controlEditActionTr += "<table class='noborder'>";
        for(var j = 0; j < roles.length; j++)
        {
            var actionRole = roles[j];
            controlEditActionTr += "<tr>";
            controlEditActionTr += "<td>"+actionRole.name+"</td>";
            controlEditActionTr += "<td>";
            controlEditActionTr += "<input name='edit_role_" + actionRole.id + "' type='radio' value='-1'>不可填";
            controlEditActionTr += "<input name='edit_role_" + actionRole.id + "' type='radio' value='0' checked>选填";
            controlEditActionTr += "<input name='edit_role_" + actionRole.id + "' type='radio' value='1'>必填";
            controlEditActionTr += "</td>";
            controlEditActionTr += "</tr>";
        }
        controlEditActionTr += "</table>";
        controlEditActionTr += "</td></tr>";

        $("#controlRoles_table").prepend(controlEditActionTr);

    }else
    {
        $("#select_controlActionId").empty();
        $("#select_controlOptionId_div").show();
        $("#select_controlActionId_div").hide();
        $("#controlRoles_table").empty();

        clearControlOptions();
        if(fieldType == "t_selection")
        {
            $("#select_controlOptionId_div").hide();
            $("#select_controlActionId_div").hide();
        }else
        {
            var controlField = getFieldById(controlFieldId);
            if(controlField){
            	for(var i = 0;i<controlField.options.length; i++)
                {
                    var controlFieldOption = controlField.options[i];
                    if(controlFieldOption.forbidden == 'f_forbidden')
                        continue;
                    $("#select_controlOptionId").append("<option value='"+controlFieldOption.id+"'>"+controlFieldOption.name+"</option>");
                }
            	$("#select_controlOptionId_div").show();
            }else{
            	alert("对不起，它的控制字段已被删除!");
            }
        }
    }
}

function initDefaultValueTd()
{
    var fieldType = $("#fieldType").val();
    if(fieldType == 't_attachment')
    {
        $("#input_defaultValue_div").hide();
        return ;
    }
    var fieldDataType = $("#fieldDataType").val();
    if(field != null)
    {
        fieldType = field.type;
        fieldDataType = field.dataType;
    }

    var defaultValueHtml = "";
    if(fieldType == 't_selection')
    {
        if(fieldDataType == "dt_multiple")
        {
            defaultValueHtml += "<select id='select_defaultValue' multiple onchange='checkClear(select_defaultValue)'>";
        }else
        {
            defaultValueHtml += "<select id='select_defaultValue' onchange='checkClear(select_defaultValue)'>";
        }

        if(field == null)
        {
            defaultValueHtml += "<option value=''>无</option>";
        }else
        {
            var defaultValueArray = new Array();
            if(field.defaultValue != "")
            {
                defaultValueArray = field.defaultValue.split(",");
            }
            if(defaultValueArray.length == 0)
            {
                defaultValueHtml += "<option value='' selected>无</option>";
            }else
            {
                defaultValueHtml += "<option value=''>无</option>";
            }

            for(var i = 0 ; i<field.options.length ; i++)
            {
                defaultValueHtml += "<option value=\"" + field.options[i].id + "\"";
                var selected = false;
                for(var j = 0; j < defaultValueArray.length; j++)
                {
                    if(field.options[i].id == defaultValueArray[j])
                    {
                        selected = true;
                        break;
                    }
                }
                defaultValueHtml += (selected ? " selected" : "") + ">" + field.options[i].name + "</option>";
            }
        }
        defaultValueHtml += "</select>";

    }else if(fieldType == 't_reference')
    {
        defaultValueHtml += "<select id='select_defaultValue'>";
        defaultValueHtml += "<option value = '' selected></option>";
        for(var i = 0; i < filters.length ; i++)
        {
            defaultValueHtml += "<option value='" + filters[i].id + "'";
            defaultValueHtml += (field != null && field.defaultValue == filters[i].id ? " selected" : "") + ">";
            defaultValueHtml += filters[i].name + "</option>";
        }
        defaultValueHtml += "</select>";
    }else if(fieldType == 't_input')
    {
        if(fieldDataType == "dt_text")
        {
            defaultValueHtml += "<textarea id='input_defaultValue'>" + (field != null ? field.defaultValue : "") + "</textarea>";
        }
        else
        {
            defaultValueHtml += "<input type='text' id='input_defaultValue' " + (field != null ? " value='" + field.defaultValue + "'" : "") + "/>";
        }
    }

    $("#input_defaultValue_controls").html(defaultValueHtml);
    $("#input_defaultValue_div").show();
}

function clearControlOptions()
{
    $("#select_controlOptionId").empty();
    $("#select_controlOptionId").append("<option value=''>无</option>");
    $("#select_controlOptionId").val("");
}


//保存 更新一个field
function saveOrUpdateField()
{
	var fieldName = $.trim($("#input_fieldName").val());
	if(fieldName == "")
	{
		alert("请输入正确的字段名称!");
		return;
	}
	
	if(field == null)
	{
		for(var i = 0;i<rows.length;i++)
	    {
	        var columns = rows[i];
	        for(var j = 0; j < columns.length ; j++)
	        {
	            var columnFields = columns[j];
	            for(var k = 0 ; k < columnFields.length ;k++)
	            {
	                var tempField = columnFields[k];
	                if(tempField.name == fieldName)
	                {
	                	alert("该字段名称已经被占用!");
	                	return;
	                }
	            }
	        }
	    }
	}
	
	var fieldDescription =$("#input_fieldDescription").val();
	var fieldTip = $("#input_fieldTip").val();
	var fieldSize = $("#input_fieldSize").val();
	
	var fieldType = $("#fieldType").val();
	var fieldDataType = $("#fieldDataType").val();
	
	var params = "fieldName="+getSafeParam(fieldName);
	if(fieldDescription != "")
		params += "&fieldDescription="+getSafeParam(fieldDescription);
	
	if(fieldTip != "")
		params += "&fieldTip="+getSafeParam(fieldTip);
	
	if(fieldSize != "")
		params += "&fieldSize="+getSafeParam(fieldSize);
	
	params += "&templateId="+templateId;
	params += "&fieldType="+$("#fieldType").val();
	params += "&timestampFormat="+$("#input_timestampFormat").val();
	params += "&dateCurTime="+$("#date_curtime").val();
	params += "&fieldDataType="+$("#fieldDataType").val();
	var controlFieldId = $("#select_controlFieldId").val();
	if(controlFieldId != "")
	{
		params += "&controlFieldId="+controlFieldId;
	}
	
	//设置默认值
	var defaultValue = "";
	if(fieldType == "t_selection" || fieldType == "t_reference")
	{
		for(var i = 1; i < $("#select_defaultValue")[0].options.length; i++)
		{
			if($("#select_defaultValue")[0].options[i].selected)
			{
				defaultValue += (defaultValue != "" ? "," : "") + $("#select_defaultValue")[0].options[i].value;
			}
		}
	}else if(fieldType == "t_input")
	{
		defaultValue = $.trim($("#input_defaultValue").val());
	}
	
	if(defaultValue != null)
		params += "&defaultValue=" + getSafeParam(defaultValue);
	
	//设置控制字段
	if($("#select_controlFieldId")[0].selectedIndex == 0)
	{
		//设置编辑权限
		for(var i = 0; i < roles.length; i++)
		{
			params += "&controlRoleId=" + (roles[i].id) + "_" + ($("input[name=edit_role_" + roles[i].id+ "]:checked").val());
		}
		
		var table_controlRoles = $("#controlRoles_table")[0];
		for(var i = 2; i < table_controlRoles.rows.length; i += 2)
		{
			var action = getActionById(table_controlRoles.rows[i].id.split("_")[2]);
			for(var j = 0; j < action.roles.length; j++)
			{
				params += "&controlActionId=" + (action.id) + "_";
				params += (action.roles[j].id) + "_";
				params += ($("input[name=action_" + action.id + "_role_" + action.roles[j].id + "]:checked").val());
			}

			params += "&actionId=" + (action.id);
		}
	}
	//设置控制选项
	else
	{
		for(var i = 1; i < $("#select_controlOptionId")[0].options.length; i++)
		{
			if($("#select_controlOptionId")[0].options[i].selected)
			{
				params += "&controlOptionId=" + ($("#select_controlOptionId")[0].options[i].value);
			}
		}
	}
	
	
	if(field == null)
	{
		//表示是新建字段
		params += "&flag=add";
		var irows = $("#main-container").children(".lyrow");
		var rowIndex = 0;
		var columnIndex = 0;
		var positionIndex = 0;
		
		for(var i = 0 ; i < irows.length ; i++)
		{
			var row = irows[i];
			var columns = $(row).find(".column");
			for(var j = 0; j < columns.length ; j++)
			{
				var column = columns[j];
				var boxes = $(column).find(".box");
				for(var k = 0 ; k < boxes.length ; k++)
				{
					var box = boxes[k];
					var fieldId = $(box).attr("fieldId");
					if(fieldId == 'newField')
					{
						positionIndex = k;
						columnIndex = j;
						rowIndex = i;
					}
				}
			}
		}
		
		params += "&rowIndex="+rowIndex;
		params += "&columnIndex="+columnIndex;
		params += "&positionIndex="+positionIndex;
	}else
	{
		//表示是修改字段
		params += "&fieldId="+field.id;
		params += "&flag=update";
	}
		
	$.ajax({
		url : 'template/add_Or_Update_Field_xml.jsp',
		type : 'POST',
		dataType : 'xml',
		data : params,
		success : function(response){
			eval("var isError = "+$(response).find("isError").text());
			if(isError)
			{
				alert("新建字段失败!");
			}
			initTemplateEdit(templateId);
			$("#editTemplateFieldDiv").modal("hide");
			clearFieldModal();
		}
	});
}

function removeField(fieldId)
{
	var params = "templateId="+templateId;
	params += "&fieldId="+fieldId;
	params += "&flag=remove";
	$.ajax({
		url : 'template/add_Or_Update_Field_xml.jsp',
		type : 'POST',
		dataType : 'xml',
		data : params,
		success : function(response){
			initTemplateEdit(templateId);
		}
	});
}

function moveField(fieldId)
{
	var irows = $("#main-container").children(".lyrow");
	var rowIndex = 0;
	var columnIndex = 0;
	var positionIndex = 0;
	
	for(var i = 0 ; i < irows.length ; i++)
	{
		var row = irows[i];
		var columns = $(row).find(".column");
		for(var j = 0; j < columns.length ; j++)
		{
			var column = columns[j];
			var boxes = $(column).find(".box");
			for(var k = 0 ; k < boxes.length ; k++)
			{
				var box = boxes[k];
				var iFieldId = $(box).attr("fieldId");
				if(iFieldId == fieldId)
				{
					positionIndex = k;
					columnIndex = j;
					rowIndex = i;
				}
			}
		}
	}
	
	var params = "flag=move";
	params += "&templateId="+templateId;
	params += "&fieldId="+fieldId;
	params += "&rowIndex="+rowIndex;
	params += "&columnIndex="+columnIndex;
	params += "&positionIndex="+positionIndex;
	
	$.ajax({
		url : 'template/add_Or_Update_Field_xml.jsp',
		type : 'POST',
		dataType : 'xml',
		data : params,
		success : function(response){
			initTemplateEdit(templateId);
		}
	});
}


//编辑字段选项
function initEditFieldOptions(fieldId)
{
	$("#editFieldId").val(fieldId);
	var iField = getFieldById(fieldId);
	var iControlFieldId = iField.controlFieldId;
    var iControlFieldOptionId = null;
    if(iControlFieldId&&iControlFieldId!='')
    {
        var iControlFieldSelect = $("#field"+iControlFieldId);
        iControlFieldOptionId = $(iControlFieldSelect).val();
        if($(iControlFieldSelect).val() == "")
        {
            alert("上级字段没有选择!");
            return ;
        }
        $("#editFieldControlOptionId").val(iControlFieldOptionId);

    }else
    {
        $("#editFieldControlOptionId").val("");
    }
	
	var iFieldOptions = iField.options;
	var tbodyHtml = "";
	for(var i = 0 ; i< iFieldOptions.length ; i++)
	{
        var iFieldOption = iFieldOptions[i];
        if(!(iControlFieldOptionId)||iFieldOption.controlOptionId == iControlFieldOptionId)
        {
            tbodyHtml += "<tr class='ui-state-default' style='cursor:move' optionId='"+iFieldOption.id+"'>";
            tbodyHtml += "<td flag='optionName'><a href='#' onclick='return initEditFieldOption(this)'>" + iFieldOption.name + "</a></td>";
            var forbidden = iFieldOption.forbidden;
            
            tbodyHtml += "<td flag='optionForbidden'>";
            tbodyHtml += "<select style='margin-bottom: 0px;width: 80px;height: 22px;margin-top: -5px;' value='"+forbidden+"'>";
            tbodyHtml += "<option value='f_permit' " + (forbidden == "f_permit" ?  "selected" : "" )+ ">否</option>";
			tbodyHtml += "<option value='f_forbidden' " + (forbidden == "f_forbidden" ?  "selected" : "") + ">是</option>";		
            tbodyHtml += "</select></td>";

            tbodyHtml += "<td><a href='#' onclick='deleteOption(this)'>删除</a></td>";
            tbodyHtml += '</tr>';
        }
       
	}

	$("#editOptionsTable").children("tbody").html(tbodyHtml);
	$("#editFieldOptionsDiv").modal('show');
}

function initCreateFieldOption()
{
	var editFieldId = $("#editFieldId").val();
	if(editFieldId != "")
	{
		$("#input_modify_option_name").val("");
		$("#input_modify_option_forbidden").val("f_permit");
		$("#editFieldOptionNameDiv").modal('show');
	}
}

function initEditFieldOption(link)
{
    var curRow = $(link).parent().parent();
    var curRowIndex = $(curRow).prevAll().length + 1;
    $("#editFieldOptionIndex").val(curRowIndex);
    var fieldOptionName = $(curRow).children("td[flag='optionName']").children("a").text();
    var fieldOptionForbidden = $(curRow).children("td[flag='optionForbidden']").find('select').val();
	$("#input_modify_option_name").val(fieldOptionName);
	$("#input_modify_option_forbidden").val(fieldOptionForbidden);
	$("#editFieldOptionNameDiv").modal('show');
	return false;
}

function saveOrUpdateFieldOption()
{
    var newOptionName = $("#input_modify_option_name").val();
    var newForbidden     = $("#input_modify_option_forbidden").val();
    var curRowIndex = $("#editFieldOptionIndex").val();
	if(curRowIndex == "")
	{
		//表示是新建
		var trHtml = "";
        trHtml += "<tr class='ui-state-default' style='cursor:move' optionId=''>";
        trHtml += "<td flag='optionName'><a href='#' onclick='return initEditFieldOption(this)'>"+newOptionName + "</a></td>";
		
        trHtml += "<td flag='optionForbidden'>";
        trHtml += "<select style='margin-bottom: 0px;width: 80px;height: 22px;margin-top: -5px;' value='"+newForbidden+"'>";
        trHtml += "<option value='f_permit' " + (newForbidden == "f_permit" ?  "selected" : "" ) + ">否</option>";
        trHtml += "<option value='f_forbidden' " + (newForbidden == "f_forbidden" ?  "selected" : "" )+">是</option>";		
        	
        trHtml += "</select></td>";
        trHtml += "<td><a href='#' onclick='deleteOption(this)'>删除</a></td>";
        trHtml += "</tr>";

        $("#editOptionsTable").children("tbody").prepend(trHtml);
	}else
	{
		//表示是修改原来的选项
        var curRow = $("#editOptionsTable").find("tr").eq(curRowIndex);
		$(curRow).children("td[flag=optionName]").children("a").text(newOptionName);
		$(curRow).children("td[flag=optionForbidden]").find("select").val(newForbidden);
	}
	$("#editFieldOptionNameDiv").modal('hide');
	$("#editFieldOptionIndex").val("");
}

function saveOrUpdateFieldOptions()
{
    var optionItems = $("#editOptionsTable").find("tr:gt(0)");
    var paramXml = "<root>"; //"<?xml version='1.0' encoding='UTF-8'?><root>";
    paramXml += "<templateId>" + templateId+"</templateId>";
    paramXml += "<fieldId>" + $("#editFieldId").val() + "</fieldId>";
    paramXml += "<controlFieldOptionId>" + $("#editFieldControlOptionId").val()+"</controlFieldOptionId>";
    paramXml += "<options>";
    
    $(optionItems).each(function(idx,optionItem){
    	paramXml += "<option>";
    	paramXml += "<optionId>" +$(optionItem).attr("optionId") + "</optionId>";
    	paramXml += "<optionIndexOrder>" + (idx + 1) + "</optionIndexOrder>";
    	paramXml += "<optionName>" +encodeAll(encodeURI(getXMLStr($(optionItem).find("td[flag=optionName]").children("a").text())))+ "</optionName>";
    	paramXml += "<optionForbidden>" +$(optionItem).find("td[flag=optionForbidden]").find("select").val()+ "</optionForbidden>";
    	paramXml += "</option>";
    });
    paramXml += "</options>";
    paramXml += "</root>";
    
    var parm = "xml="+paramXml+"";
    
    $.ajax({
    	url : 'template/add_Or_Update_Option_xml.jsp',
    	dataType : 'xml',
    	type : 'POST',
    	data : parm,
    	success : function(jsonResponse)
    	{
    		eval("var isError = "+$(jsonResponse).find("isError").text());
    		if(!isError)
    		{
    			initTemplateEdit(templateId);
    		}else
    		{
    			showInfoWin("error","服务器错误,请稍后重试!");
    		}
    		$("#editFieldId").val("");
    		$("#editOptionsTable").children("tbody").empty();
    		$("#editFieldOptionsDiv").modal('hide');
    	}
    });
    
   
}

function cancelEditFieldOptions()
{
	$("#editFieldId").val("");
	$("#editOptionsTable").children("tbody").empty();
	$("#editFieldOptionsDiv").modal('hide');
}

function cancelEditFieldOption()
{
	$("#editFieldOptionId").val("");
	$("#input_modify_option_name").val("");
	$("#input_modify_option_forbidden").val("f_permit");
	$("#editFieldOptionsDiv").modal('show');
}

//utils
function displayActionRoles(actionId)
{
    $("#tr_action_"+actionId+"_roles").toggle();
    return false;
}

function removeControlAction(actionId)
{
    $("#tr_action_"+actionId).remove();
    $("#tr_action_"+actionId+"_roles").remove();
    $("#select_controlActionId").prepend("<option value='"+actionId+"'>"+getActionById(actionId).name+"</option>");
}


function  addControlActions()
{
    initActionRoles();
}

function initActionRoles()
{
    var selectControlActionId = $("#select_controlActionId")[0];
    for(var i = 0; i<selectControlActionId.options.length; i++)
    {
        var selectControlActionOption = selectControlActionId.options[i];
        var actionId = selectControlActionOption.value;
        var action = getActionById(actionId);
        if(selectControlActionOption.selected)
        {
            //初始化控制动作这一块
            var controlActionTr = "<tr id = 'tr_action_"+action.id+"'>";
            controlActionTr += "<td><a href='#' onclick='return displayActionRoles("+action.id+");'>"+action.name+"</a></td>";
            controlActionTr += "<td><a href='#' onclick='return removeControlAction("+action.id+");'>移除</a></td>";
            controlActionTr += "</tr>";

            controlActionTr += "<tr id='tr_action_"+action.id+"_roles' style='display:none'>";
            controlActionTr += "<td colspan='2'>";
            controlActionTr += "<table class='noborder'>";
            for(var j = 0; j < action.roles.length; j++)
            {
                var actionRole = action.roles[j];
                if(actionRole.id != '82') {  //everyone取消
	                controlActionTr += "<tr>";
	                controlActionTr += "<td>"+actionRole.name+"</td>";
	                controlActionTr += "<td>";
	                controlActionTr += "<input name='action_" + action.id + "_role_" + action.roles[j].id + "' type='radio' value='-1'>不可填";
	                controlActionTr += "<input name='action_" + action.id + "_role_" + action.roles[j].id + "' type='radio' value='0' checked>选填";
	                controlActionTr += "<input name='action_" + action.id + "_role_" + action.roles[j].id + "' type='radio' value='1'>必填";
	                controlActionTr += "</td>";
	                controlActionTr += "</tr>";
                }
            }
            $("#controlRoles_table").append(controlActionTr);
            selectControlActionId.remove(i);
        }
    }
}

function checkClear(selectId)
{
    if(selectId.options[0].selected)
    {
        for(var i = 1; i < selectId.options.length; i++)
        {
            selectId.options[i].selected = false;
        }
    }
}

function checkSingleSelect(item)
{
    var fieldId = $(item).attr("id").substring(5);
    var field   = getFieldById(fieldId);
    var selectOptionId = $(item).val();
    for(var i = 0; i < rows.length ; i++)
    {
        var columns = rows[i];
        for(var j = 0 ; j < columns.length ; j++)
        {
            var fields = columns[j];
            for(var k = 0; k < fields.length ; k++)
            {
                var tmpField = fields[k];
                if(tmpField.id == null||tmpField.controlFieldId != field.id)
                    continue;
                var controlFieldItem = $("#field"+tmpField.id);
                if(selectOptionId == "")
                {
                    if(tmpField.type == 't_selection'&&tmpField.dataType == 'dt_single')
                    {
                        $(controlFieldItem)[0].selectedIndex = 0;
                        checkSingleSelect(controlFieldItem);
                    }
                    controlFieldItem[0].style.visibility = "hidden";
                }else
                {
                    if(tmpField.type == 't_selection')
                    {
                        $(controlFieldItem).empty();
                        $(controlFieldItem).append("<option value=''>请选择</option>");
                        for(var m = 0; m < tmpField.options.length ; m++)
                        {
                            if(tmpField.options[m].controlOptionId == selectOptionId)
                            {
                                controlFieldItem[0].options[controlFieldItem[0].options.length] = new Option(tmpField.options[m].name, tmpField.options[m].id);
                                controlFieldItem[0].options[controlFieldItem[0].options.length - 1].setAttribute("forbidden", tmpField.options[m].forbidden);
                            }
                        }

                        controlFieldItem[0].selectedIndex = 0;

                        if(tmpField.dataType == "dt_single")
                            checkSingleSelect(controlFieldItem);

                        controlFieldItem[0].style.visibility = "visible";
                    }else
                    {
                        var l = 0;
                        while(l < tmpField.controlOptionIds.length)
                        {
                            if(tmpField.controlOptionIds[l] == selectOptionId)
                            {
                                controlFieldItem[0].style.visibility = "visible";
                                break;
                            }
                            l++;
                        }

                        if(l == tmpField.controlOptionIds.length)
                            controlFieldItem[0].style.visibility = "hidden";
                    }
                }
            }
        }
    }
}

function returnZero(item)
{
    if($(item)[0].options[0].selected)
    {
        for(var i = 1; i < $(item)[0].options.length; i++)
            $(item)[0].options[i].selected = false;
    }
}

function deleteOption(link)
{
	var bool = window.confirm("确定删除选项?");
    if(!bool)
    	 return false;
    var curRow      = $(link).parent().parent();
    $(curRow).remove();
}

//remove fieldRow
function removeFieldRow(rowIndex)
{
	var params = "templateId="+templateId;
	params += "&rowIndex="+rowIndex;
	params += "&flag=remove";
	
	$.ajax({
		url : 'template/add_Or_Update_Row_xml.jsp',
		type : 'post',
		data : params,
		dataType : 'xml',
		success : function(response)
		{
			initTemplateEdit(templateId);
		}
	});
}

function addFieldRow(rowIndex, rowColumnCount)
{
	var params = "templateId="+templateId;
	params += "&rowIndex="+rowIndex;
	params += "&columnCount="+rowColumnCount;
	params += "&flag=add";
	
	$.ajax({
		url : 'template/add_Or_Update_Row_xml.jsp',
		type : 'post',
		data : params,
		dataType : 'xml',
		success : function(response)
		{
			initTemplateEdit(templateId);
		}
	});
}

