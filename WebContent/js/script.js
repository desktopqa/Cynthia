function initTemplates()
{
	$.ajax({url : "../mountScript/getAllTemplates.do?type=script",
			dataType : 'xml'
			})
		.done(function(response){
		$("#maintable > tbody > tr").eq(0).nextAll().remove();
		$(response).find("template").each(function(index,template){
			var templateId   = $(template).children("id").text();
			var templateName = $(template).children("name").text();
			var trHtml       = "<tr><td>"+(index+1)+"</td><td>"+templateName+"</td><td>";
			$(template).children("scripts").children("script").each(function(index,script){
				var scriptId = $(script).children("id").text();
				var scriptName = $(script).children("name").text();
				trHtml += scriptName+"<br />";
			});
			trHtml += "</td><td><a href='mountscript.jsp?templateId="+templateId+"'>挂载</a></td></tr>";
			$("#maintable > tbody").append(trHtml);
		});
		$("#maintable tr:odd").css("background","#f2eada");
	}).fail(function(response){
		//alert("加载失败，请检查网络连接!");
	});
}

function initMountTemplateScript(templateId)
{
	var params = "templateId="+templateId;
	$.ajax({
		url : "../mountScript/initTemplateScripts.do",
		data : params,
		dataType : 'xml'
	}).done(function(response){
		var root = $(response).children("root");
		//template
		var templateId = $(root).children("template").children("id").text();
		var templateName = $(root).children("template").children("name").text();
		$("#templateName").val(templateName);
		$("#templateName").attr("templateId",templateId);
		
		//stat and action
		$("#confInfo").hide();
		$("#stat").removeAttr("disabled");
		$("#stat").empty();
		$(root).children("template").children("stats").children("stat").each(function(index,stat){
			var statId = $(stat).children("id").text();
			var statName = $(stat).children("name").text();
			$("#stat").append("<option value='"+statId+"'>"+statName+"</option>");
		});
		
		$("#action").removeAttr("disabled");
		$("#action").empty();
		$(root).children("template").children("actions").children("action").each(function(index,action){
			var actionId = $(action).children("id").text();
			var actionName = $(action).children("name").text();
			$("#action").append("<option value='"+actionId+"'>"+actionName+"</option>");
		});
		
		//scripts
		
		$("#mountedScripts > tbody > tr:gt(0)").remove();
		$(root).children("mountscripts").children("script").each(function(index,script){
			var scriptId   = $(script).children("id").text();
			var scriptName = $(script).children("name").text();
			var trHtml = "<tr><td>"+scriptId+"</td><td><a href='#' onclick='initMountedScript("+scriptId+")'>"+scriptName+"</a></td><td><a href='#' onclick='unmountTemplateScript("+scriptId+")'>卸载</td></tr>";
			$("#mountedScripts > tbody").append(trHtml);
		});
		
		$("#allowedScript").empty();
		$("#allowedScript").append("<option value=''>--请选择--</option>");
		$(root).children("allowedscripts").children("script").each(function(index,script){
			var scriptId   = $(script).children("id").text();
			var scriptName = $(script).children("name").text();
			$("#allowedScript").append("<option value='"+scriptId+"'>"+scriptName+"</option>");
		});
		
	}).fail(function(response){
		alert("error!");
	});
}

function initAllowedScript()
{
	var scriptId = $("#allowedScript").val();
	if(scriptId == '')
		return;
	if($("#mountedScripts").find("td:contains("+scriptId+")").length>0)
	{
		alert("已经挂载过该脚本!");
		return;
	}
	var params = "scriptId="+scriptId;
	$.ajax({
		url : "../script/initScript.do",
		data : params,
		dataType : 'json'
	}).done(function(response){
		eval("var isStatEdit = "+response.statEdit);
		eval("var isActionEdit = "+response.actionEdit);
		eval("var isValid = "+response.valid);
		if(isStatEdit||isActionEdit)
		{
			$("#confInfo").show();
			if(!isStatEdit)
			{
				$("#stat").attr("disabled","true");
			}else
			{
				$("#stat").removeAttr("disabled");
			}
			if(!isActionEdit)
			{
				$("#action").attr("disabled","true");
			}else
			{
				$("#action").removeAttr("disabled");
			}
		}else
		{
			$("#confInfo").hide();
		}
		
	}).fail(function(){
		alert("系统错误!");
	});
}

function initMountedScript(scriptId)
{
	var params = "scriptId="+scriptId;
	$.ajax({
		url : "../script/initScript.do",
		data : params,
		dataType : 'json'
	}).done(function(response){
		eval("var isStatEdit = "+response.statEdit);
		eval("var isActionEdit = "+response.actionEdit);
		eval("var isValid = "+response.valid);
		$("#allowedScript").val(scriptId);
		if(isStatEdit||isActionEdit)
		{
			$("#confInfo").show();
			if(!isStatEdit)
			{
				$("#stat").attr("disabled","true");
			}else
			{
				$("#stat").removeAttr("disabled");
			}
			if(!isActionEdit)
			{
				$("#action").attr("disabled","true");
			}else
			{
				$("#action").removeAttr("disabled");
			}
			
			$("#stat").val(response.statIds);
			$(response.statIds).each(function(index,stat){
				
			});
			
			$("#action").val(response.actionIds);
			$(response.actionIds).each(function(index,action){
				
			});
		}else
		{
			$("#confInfo").hide();
		}
	}).fail(function(){
		alert("系统错误!");
	});
}


function unmountTemplateScript(scriptId)
{	
	var templateId = $("#templateName").attr("templateId");
	var params = "scriptId="+scriptId+"&templateId="+templateId;
	$.ajax({
		url : '../mountScript/unMountScriptTemplate.do',
		data : params,
		dataType : 'xml',
		success : function(response){
			initMountTemplateScript(templateId);
		},
		error : function(){alert("ERROR!");}
	});
}

function executeAddMountScript()
{
	var scriptId   = $("#allowedScript").val();
	var templateId = $("#templateName").attr("templateId");
	var actions    = $("#action").val();
	var stats      = $("#stat").val();
	var params = "scriptId="+scriptId;
	params += "&templateId="+templateId;
	params += "&actions="+actions;
	params += "&stats="+stats;
	$.ajax({
		url : '../mountScript/mountScriptTemplate.do',
		data : params,
		dataType : 'xml',
		success : onCompleteAddMountScript,
		error : function(){alert("ERROR");}
	});
}

function onCompleteAddMountScript(response)
{
	var templateId = $("#templateName").attr("templateId");
	initMountTemplateScript(templateId);
}




function initScriptList()
{
	$.ajax({
		url : '../script/getAllScript.do',
		dataType : 'json',
		success : onCompleteInitScriptList,
		error : function(){alert("ERROR!");}
	});
}

function onCompleteInitScriptList(response)
{
	$("#maintable > tbody > tr").eq(0).nextAll().remove();
	$(response).each(function(index,script){
		var scriptId = script.id.value;
		var scriptName = script.name;
		var createUser = script.createUser;
		var createTime = script.createTime;
		var isAsync = script.async;
		var isBeforeCommit = script.beforeCommit;
		var isAfterSuccess = script.afterSuccess;
		var isAfterFail = script.afterFail;
		var isAfterQuery = script.afterQuery;
		
		var trHtml = "<tr>";
		trHtml += "<td>"+(index+1)+"</td>";
		trHtml += "<td style='width:500px;'><a href='editscript.jsp?id="+scriptId+"' target='_blank'>"+scriptName+"</a></td>";
		trHtml += "<td>"+createUser+"</td>";
		
		trHtml += "<td>"+isAsync+"</td>";
		trHtml += "<td>"+isBeforeCommit+"</td>";
		trHtml += "<td>"+isAfterSuccess+"</td>";
		trHtml += "<td>"+isAfterFail+"</td>";
		trHtml += "<td>"+isAfterQuery+"</td>";
		
		trHtml += "<td>"+createTime+"</td>";
		trHtml += "<td><a href='mountscript.jsp?id="+scriptId+"' target='_blank'>挂载</a></td>";
		trHtml += "</td>";
		$("#maintable > tbody").append(trHtml);
	});
	
	$("#maintable tr:odd").css("background","#f2eada");
}


//编辑脚本
function initScript(scriptId)
{
	if(scriptId)
	{
		initScriptModify(scriptId);
	}else
	{
		initScriptCreate();
	}
}

function initScriptCreate()
{
	
}

function initScriptModify(scriptId)
{
	$("#scriptId").val(scriptId);
	var params = "scriptId="+scriptId;
	$.ajax({
		url : '../script/initScript.do',
		dataType : 'json',
		data : params,
		success : onCompleteInitScript,
		error : function(){alert("系统内部错误,请稍后重试!");}
	});
}

function onCompleteInitScript(response)
{
	var scriptName      = response.name;
	var scriptId        = response.id.value;
	var mountTemplates  = response.mountTemplates;
	var scriptCode      = response.script;
	var isAsync 	    = response.async;
	var isAfterSuccess  = response.afterSuccess;
	var isBeforeCommit  = response.beforeCommit;
	var isAfterFail     = response.afterFail;
	var isStatEdit      = response.statEdit;
	var isActionEdit    = response.actionEdit;
	var isValid         = response.valid;
	var allowedTemplates= response.allowedTemplates;
	var allTemplates    = response.allTemplates;
	
	if(isAsync)
	{
		$("#isAsync").attr("checked",true);
	}else
	{
		$("#isAsync").attr("checked",false);
	}
	if(isBeforeCommit)
	{
		$("#isBeforeCommit").attr("checked",true);
	}else
	{
		$("#isBeforeCommit").attr("checked",false);
	}
	if(isAfterSuccess)
	{
		$("#isAfterSuccess").attr("checked",true);
	}else
	{
		$("#isAfterSuccess").attr("checked",false);
	}
	if(isAfterFail)
	{
		$("#isAfterFail").attr("checked",true);
	}else
	{
		$("#isAfterFail").attr("checked",false);
	}
	
	if(isStatEdit)
	{
		$("#isStatEdit").prop("checked",true);
	}else
	{
		$("#isStatEdit").prop("checked",false);
	}
	
	if(isActionEdit)
	{
		$("#isActionEdit").prop("checked",true);
	}else
	{
		$("#isActionEdit").prop("checked",false);
	}
	
	if(isValid)
	{
		$("#isValid").prop("checked",true);
	}else
	{
		$("#isValid").prop("checked",false);
	}
	
	$.each(allTemplates,function(templateId,templateName){
		$("#allowedTemplates").append("<option value='"+templateId+"'>"+templateName+"</option>");
	});
	$("#allowedTemplates").val(allowedTemplates);
	
	$("#scriptName").val(scriptName);
	$("#script").val(scriptCode);
}

function executeAddOrUpdateScript()
{
	var scriptId	     = $("#scriptId").val();
	var scriptName 	     = $("#scriptName").val();
	var scriptCode       = $("#script").val();
	var isAsync          = $("#isAsync")[0].checked;
	var isBeforeCommit   = $("#isBeforeCommit")[0].checked;
	var isAfterSuccess   = $("#isAfterSuccess")[0].checked;
	var isAfterFail      = $("#isAfterFail")[0].checked;
	var isStatEdit       = $("#isStatEdit")[0].checked;
	var isActionEdit     = $("#isActionEdit")[0].checked;
	var isValid          = $("#isValid")[0].checked;
	var allowedTemplates = $("#allowedTemplates").val();
	
	var params = "scriptName="+scriptName;
	if(script!=null&&script!="")
	{
		params += "&scriptId="+scriptId;
	}
	params += "&isAsync="+isAsync;
	params += "&isBeforeCommit="+isBeforeCommit;
	params += "&isAfterSuccess="+isAfterSuccess;
	params += "&isAfterFail="+isAfterFail;
	params += "&script="+encodeURIComponent(scriptCode);
	params += "&isStatEdit="+isStatEdit;
	params += "&isActionEdit="+isActionEdit;
	params += "&isValid="+isValid;
	params += "&allowedTemplates="+allowedTemplates;
	
	$.ajax({
		url : "../script/addOrUpdateScript.do",
		data : params,
		type : 'POST',
		dataType : 'json',
		success : function(response){
			if(response.success == true)
				alert("添加成功！");
			else
				alert("添加失败！");
			},
		error : function(response){
				alert("系统内部错误,请稍后重试！");
			}
	});
	
}

function initMountTemplates(mountTemplates)
{
	$.each(mountTemplates,function(templateId,templateName){
		var thHtml = "<tr><td>"+templateName+"</td></tr>";
		$("#mountTemplates > table > tbody").append(thHtml);
	});
}

function initMountScript(scriptId)
{
	var params = "scriptId="+scriptId;
	$("#scriptId").val(scriptId);
	var params = "scriptId="+scriptId;
	$.ajax({
		url : '../script/initScript.do',
		dataType : 'json',
		data : params,
		success : onCompleteInitMountScript,
		error : function(){alert("系统内部错误,请稍后重试!");}
	});
}

function onCompleteInitMountScript(response)
{
	var mountTemplates = response.mountTemplates;
	var allTemplates   = response.allTemplates;
	var scriptId       = response.id;
	var scriptName     = response.name;
	$("#scriptId").val(scriptId);
	$("#scriptName").val(scriptName);
	
	$("#action").empty();
	$("#stat").empty();
	
	//初始化已经加载的表单
	$("#mountTemplates > tbody >tr").eq(0).nextAll().remove();
	$.each(mountTemplates,function(templateId,templateName){
		var trHTML = "<tr>";
		trHTML += "<td><a href='#' onclick=initTemplateActionAndStat("+templateId+","+scriptId+")>"+templateName+"</a></td>";
		trHTML += "<td><a href='#' onclick='unmountTemplateScript("+scriptId+","+templateId+")'>卸载</a>";
		trHTML += "</tr>";
		$("#mountTemplates > tbody").append(trHTML);
	});
	
	//初始化所有表单
	$("#template").empty();
	$("#template").append("<option value=''>--请选择--</option>");
	$.each(allTemplates,function(templateId,templateName){
		$("#template").append("<option value='"+templateId+"'>"+templateName+"</option>");
	});
}

function initTemplateActionAndStat(templateId,scriptId)
{
	if(templateId)
	{
		$("#template").val(templateId);
	}
	if(!templateId)
	{
		templateId = $("#template").val();
	}
	
	var params = "templateId="+templateId;
	if(scriptId)
	{
		params +="&scriptId="+scriptId;
	}
	
	$.ajax({
		url : '../script/initTemplateActionAndStat.do',
		data:params,
		dataType : 'xml',
		success : onCompleteInitTemplateActionAndStat,
		error:function(){}
	});
}
function onCompleteInitTemplateActionAndStat(response)
{
	$("#action").empty();
	$("#stat").empty();
	$(response).find("action").each(function(index,node){
		var actionId 	= $(node).children("id").text();
		var actionName  = $(node).children("name").text();
		var selected    = $(node).children("select").text();
		if(selected == 'true')
		{
			$("#action").append("<option value='"+actionId+"' selected=selected>"+actionName+"</option>");
		}else
		{
			$("#action").append("<option value='"+actionId+"'>"+actionName+"</option>");
		}
	});
	
	$(response).find("stat").each(function(index,node){
		var statId   = $(node).children("id").text();
		var statName = $(node).children("name").text();
		var selected = $(node).children("select").text();
		if(selected == 'true')
		{
			$("#stat").append("<option value='"+statId+"' selected=selected>"+statName+"</option>");
		}else
		{
			$("#stat").append("<option value='"+statId+"' >"+statName+"</option>");
		}
	});
}

