
function initFilterFields(callback)
{	
	var templateId = $("#templates").val();
	if(templateId!="")
	{
		var params = {templateId : templateId};
		$.ajax({
			url: base_url + 'filterManage/initFields.jsp',
			data:params,
			dataType:'xml',
			success:function(data){
				eval("var error="+$(data).find("root").find("isError").text());
				if(error){
					alert("服务器内部错误,请稍后再试");
				}else{
					clearFields();
					clearConditionTable();
					$("#fields").append("<option value=''>---请选择---</option>");
					$(data).find("fields").find("field").each(function(index,node){
						var fieldId = $(node).find("id").text();
						var fieldName = $(node).find("name").text();
						$("#fields").append("<option value='"+fieldId+"'>"+fieldName+"</option>");
					});
				}
				if(callback) callback();
			}
		});
	}
}

function subCondition(row)
{
	var fieldId,fieldName;
	var parent = row.parentNode;
	var index = parent.rowIndex;
	document.getElementById("conditions_table").deleteRow(index);
	fieldId = $(parent).find("td:eq(0) div:eq(0)").attr("fieldid");
	fieldName = $(parent).find("td:eq(0) div:eq(0)").attr("fieldname");
	$("#fields").append("<option value=" + fieldId + "> " + fieldName + "</option>");
}

function addCondition()
{	
	var fieldId = $("#fields").val();
	var templateId = $("#templates").val();
	var params = {templateId:templateId,fieldId:fieldId};
	$.post(getRootDir() + 'filterManage/addField.jsp',params,onCompleteAddCondition,'xml');
	$("#fields option[value="+ fieldId +"]").remove();
	$("#fields").val("");
	enableSelectSearch();
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
	enableSelectSearch();
}


/**
 * 返回过滤字段结果xml
 * @returns
 */
function getQueryConditionXml()
{
	var rootDoc = getXMLDoc();
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
				setTextContent(conditionNode,($("input[type=radio][name=betweenField]:checked").val()));
				whereNode.appendChild(conditionNode);
			}
			var whereInnerNode = rootDoc.createElement("whereInner");
			setTextContent(whereInnerNode,xml);
			whereNode.appendChild(whereInnerNode);
		}
	});
	

	rootDoc.appendChild(whereNode);
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
				
	return finalXml;
}


function clearFields()
{
	$("#fields").empty();
}

function clearConditionTable()
{
	$("#conditions_table > tbody").empty();
}

function cleanNewData(dataId, filterId)
{	
	if(document.getElementById('image_' + dataId).src.indexOf('old.bmp') >= 0)
		return true;;
	
	document.getElementById('image_' + dataId).src = 'images/old.bmp';
	
	var params = { dataId: dataId, filterId: filterId };
	
	setNotifyValue('frame/click_Datas_xml.jsp',params);
}

function setNotifyValue(url, params, async)
{
	async = async || 'true';
	$.ajax({
		url: url,
		type: 'post',
		async:async,  
		data: params,
		success: function(data){
			if (!data)
				return;
	}});
}
