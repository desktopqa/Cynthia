<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="../css/tms.css" type="text/css" media="screen">

<style>
	.tdHeight { height:28; }
	.tdCenter { vertical-align:middle; align:center }
	td {noWrap: true; }
</style>


<%
response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility

%>

<title>高级设置</title>
<script language="javascript" src="../js/util.js"></script>
<script language="javascript" src="../js/where.js?v=1"></script>
<script type="text/javascript" src="../lib/My97DatePicker/WdatePicker.js"></script>

<script type="text/javascript">
	var paramObj = window.opener.paramObj;
	var bracketAccount = 0;
	
	function initTable()
	{
		setTextContent(fieldTitle, "[" + paramObj.fieldName + "]" + "高级筛选条件");
		
		var xml = paramObj.xml;
		
		var advanceFormulaRow = advance_table.rows[0];
		
		// 高级表达式
		if(xml != null)
		{
			while(true)
			{
				var beginIndex = xml.indexOf("<");
				var endIndex = xml.indexOf(">");
				
				if(beginIndex < 0 || endIndex < 0)
					break;
				
				var childXml = xml.substring(beginIndex, endIndex + 1);
				
				xml = xml.replace(childXml, "");
				
				if(childXml.indexOf("<field") == 0)
				{
					var fieldName = "";
					var fieldMethod = "";
					var fieldValue = "";
					
					childXml = childXml.replace("is null", "is_null");
					childXml = childXml.replace("is not null", "is_not_null");
					childXml = childXml.replace("not like", "not_like");
					
					var childXmlElems = childXml.split(" ");
					for(var i = 0; i < childXmlElems.length; i++)
					{
						if(childXmlElems[i].indexOf("name=\"") == 0)
						{
							fieldName = childXmlElems[i].substring(6);
							fieldName = fieldName.split("\"")[0];
						}
						else if(childXmlElems[i].indexOf("method=\"") == 0)
						{
							fieldMethod = childXmlElems[i].substring(8);
							fieldMethod = fieldMethod.split("\"")[0];
							fieldMethod = fieldMethod.replace("&lt;", "<");
							fieldMethod = fieldMethod.replace("&gt;", ">");
							fieldMethod = fieldMethod.replace("is_null", "is null");
							fieldMethod = fieldMethod.replace("is_not_null", "is not null");
							fieldMethod = fieldMethod.replace("not_like", "not like");
							
							if(fieldMethod != "is null" && fieldMethod != "is not null")
							{
								fieldValue = xml.split("<")[0];
								xml = xml.replace(fieldValue, "");
							}
						}
					}
					
					var tempTd = document.createElement( "td" );					
					tempTd.type = "param";
					tempTd.noWrap = true;
					setTextContent(tempTd, fieldName);
					advanceFormulaRow.appendChild( tempTd );
					
					tempTd = document.createElement( "td" );
					tempTd.type = "method";
					tempTd.noWrap = true;
					setTextContent(tempTd, fieldMethod);
					tempTd.value = fieldMethod;
					advanceFormulaRow.appendChild( tempTd );
					
					if(fieldMethod != "is null" && fieldMethod != "is not null")
					{
						tempTd = document.createElement( "td" );
						tempTd.type = "value";
						tempTd.noWrap = true;
						setTextContent(tempTd, fieldValue);
						tempTd.value = fieldValue;
						advanceFormulaRow.appendChild( tempTd );
					}
				}
				else if(childXml.indexOf("<condition") == 0)
				{
					var condition = xml.split("<")[0];
					xml = xml.replace(condition, "");
					
					var tempTd = document.createElement( "td" );
					tempTd.type = "condition";
					tempTd.noWrap = true;
					setTextContent(tempTd, condition);
					tempTd.value = condition;
					advanceFormulaRow.appendChild( tempTd );
					
					if( tempTd.value == "(" )
						bracketAccount++;
					else if( tempTd.value == ")" )
						bracketAccount--;
				}
			}
		}
		
		var demoStr = "";
		if( paramObj.fieldId.indexOf("FIEL-") == 0 && paramObj.type == "input" && (
			paramObj.dataType == "integer" 
			|| paramObj.dataType == "long"
			|| paramObj.dataType == "float"
			|| paramObj.dataType == "double" )
		)
		{
			demoStr = "( " + paramObj.fieldName + " >1000 and  " + paramObj.fieldName + " <2000)";
		}
		else if( paramObj.fieldId == "create_time" || paramObj.fieldId == "last_modify_time"||paramObj.fieldId == "action_time_range"
			|| paramObj.fieldId.indexOf("FIEL-") == 0 && paramObj.type == "input" && paramObj.dataType == "timestamp")
		{
			demoStr = "( " + paramObj.fieldName + " >2007-01-01 00:00:00 and " + paramObj.fieldName + " <2007-05-01 15:00:00)";
		}
		else
		{
			demoStr = "( " + paramObj.fieldName + " like abc and  " + paramObj.fieldName + " not like hello中国)";
		}
		
		setTextContent(demoTd, demoStr);		
	}
	
	function backspaceAdvanceFormula()
	{
		var advanceFormulaRow = advance_table.rows[0];
		
		if( advanceFormulaRow.cells.length == 0 )
			return;			
		
		if( advanceFormulaRow.cells[ advanceFormulaRow.cells.length - 1 ].type == "method" )
			advanceFormulaRow.deleteCell( advanceFormulaRow.cells.length - 1 );
		else if( advanceFormulaRow.cells[ advanceFormulaRow.cells.length - 1 ].type == "condition" )
		{
			if( advanceFormulaRow.cells[ advanceFormulaRow.cells.length - 1 ].value == "(" )
				bracketAccount--;
			else if( advanceFormulaRow.cells[ advanceFormulaRow.cells.length - 1 ].value == ")" )
				bracketAccount++;
		}
		
		advanceFormulaRow.deleteCell( advanceFormulaRow.cells.length - 1 );
	}
	
	function addValue()
	{
		var advanceFormulaRow = advance_table.rows[0];
		
		if( advanceFormulaRow.cells.length == 0 
			|| advanceFormulaRow.cells[ advanceFormulaRow.cells.length - 1 ].type != "method"
				|| advanceFormulaRow.cells[ advanceFormulaRow.cells.length - 1 ].value == "is null"
					|| advanceFormulaRow.cells[ advanceFormulaRow.cells.length - 1 ].value == "is not null"
				)
		{
			alert( "输入内容只能添加在运算符后面，且is null和is not null后不可以添加数值!" );
			return;
		}
		
		var tempTd = document.createElement( "td" );					
		tempTd.type = "value";
		tempTd.noWrap = true;
		setTextContent(tempTd, advance_value.value);
		tempTd.value = advance_value.value;
		advanceFormulaRow.appendChild( tempTd );
		
		advance_value.value = "";
	}
	
	function editAdvanceFormula_tag(value_param)
	{
		var value = value_param;
		var formulaRow = advance_table.rows[0];
		var lastType = null;
		var lastValue = null;
		
		if( formulaRow.cells.length != 0 )
		{
			lastType = formulaRow.cells[ formulaRow.cells.length - 1 ].type;
			lastValue = formulaRow.cells[ formulaRow.cells.length - 1 ].value;
		}		
		
		if( value != "and"
			&& value != "or"
				&& value != "("
				 && value != ")" 
					 )
		{
			if( lastType != null &&
				(
					( lastType == "condition" && lastValue != "and" && lastValue != "or" && lastValue != "(" )
					||
					( lastType == "method" || lastType == "value" )				
				)
			  )
			{
				alert( "友情提示：您将表达式符号添加在了错误的位置!" );
				return;
			}
		
			var tdObj = document.createElement( "td" );
			tdObj.type = "param";
			tdObj.noWrap = true;
			setTextContent(tdObj, paramObj.fieldName);
			tdObj.value = paramObj.fieldName;
			formulaRow.appendChild( tdObj );
			
			tdObj = document.createElement( "td" );
			tdObj.type = "method";
			tdObj.noWrap = true;
			tdObj.value = value;
			setTextContent(tdObj, value);
			
			formulaRow.appendChild( tdObj );
		}
		else
		{			
			// 语法检查
			if( value == "and" || value == "or" )
			{
				// 只能跟在)后面，或者value后面，或者is null , is not null后面
				if( 	lastType == null
						|| ( lastType ==  "condition" && lastValue != ")" )
						|| ( lastType == "method" && lastValue != "is null" && lastValue != "is not null" )
						)
				{
						alert( "友情提示：您将表达式符号添加在了错误的位置!" );
						return;
				}
			}
			else
			{
				// (只能添加在(,and ,or后面
				if( value == "(" && lastType != null && 
						(
							lastType != "condition" 
							||
							( lastType ==  "condition" && lastValue == ")" )							
						)
					)
				{
					alert( "友情提示：您将表达式符号添加在了错误的位置!" );
					return;
				}
				
				// 添加在value, is null ,is not null , )后面
				if( value == ")" &&
					(
						lastType == null 
						||
						( lastType == "condition" && lastValue != ")" )
						||
						( lastType == "method" && lastValue.indexOf( "null" ) == -1 )
						|| bracketAccount <= 0
					)
				  )
				  {
				  	alert( "友情提示：您将表达式符号添加在了错误的位置!" );
					return;
				  }
			}
			
			if( value == "(" )
				bracketAccount++;
			else if( value == ")" )
				bracketAccount--;
			
			var tdObj = document.createElement( "td" );
			tdObj.type = "condition";
			tdObj.noWrap = true;
			setTextContent(tdObj, value);
			tdObj.value = value;
			formulaRow.appendChild( tdObj );
		}
	}
	function setReturValue()
	{
		// 如果括号没有填对，则提示
		if( bracketAccount != 0 )
		{
			alert( "格式错误，请检查括号的匹配!" );
			return;
		}
	
		var nodeArray = new Array();
		var rootDoc = getXMLDoc();
		
		var rootNode = rootDoc.createElement("root");
		rootDoc.appendChild(rootNode);
			
		var startBracket = rootDoc.createElement( "condition" );
		setTextContent(startBracket, "(");
		rootNode.appendChild(startBracket);
		nodeArray[ nodeArray.length ] = startBracket;
		
		var valueCells = advance_table.rows[0].cells;
		for( var i = 0; i < valueCells.length; i++ )
		{
			if( valueCells[i].type == "param" )
			{
				var fieldNode = rootDoc.createElement( "field" );
				fieldNode.setAttribute( "id" , paramObj.fieldId );
				fieldNode.setAttribute( "name" , paramObj.fieldName );
				if(paramObj.type != null)
					fieldNode.setAttribute( "type" , paramObj.type );
				if(paramObj.dataType != null)
					fieldNode.setAttribute( "dataType" , paramObj.dataType );
				
				i++;
				
				if(  valueCells[i].type == "method" )
				{
					fieldNode.setAttribute( "method" , getTextContent(valueCells[i]) );
					
					if( (i+1) < valueCells.length && valueCells[i + 1].type == "value" )
					{
						i++;
						if( getTextContent(valueCells[i]) != null )
							setTextContent(fieldNode, getTextContent(valueCells[i]));
					}
				}
				else
				{
					alert( "高级表达式错误，请修改" );
					return;
				}
				
				rootNode.appendChild(fieldNode);
				nodeArray[ nodeArray.length ] = fieldNode;
			}
			else
			{
				var conditionNode = rootDoc.createElement( "condition" );
				if( getTextContent(valueCells[i]) != null )
					setTextContent(conditionNode, getTextContent(valueCells[i]));
				
				rootNode.appendChild(conditionNode);
				nodeArray[ nodeArray.length ] = conditionNode;
			}
		}
		
		var endBracket = rootDoc.createElement( "condition" );
		setTextContent(endBracket, ")");
		rootNode.appendChild(endBracket);
		nodeArray[ nodeArray.length ] = endBracket;
		
		var fieldNode = rootDoc.createElement( "field" );
		var typeNode =  rootDoc.createElement( "type" );
		setTextContent(typeNode, paramObj.type);
		
		var dataTypeNode =  rootDoc.createElement( "dataType" );
		if(paramObj.dataType != null)
			setTextContent(dataTypeNode, paramObj.dataType);
		
		var idNode =  rootDoc.createElement( "id" );
		setTextContent(idNode, paramObj.fieldId);
		
		var nameNode =  rootDoc.createElement( "name" );
		setTextContent(nameNode, paramObj.fieldName);
		
		fieldNode.appendChild( typeNode );
		fieldNode.appendChild( dataTypeNode );
		fieldNode.appendChild( idNode );
		fieldNode.appendChild( nameNode );
		
		var divElement = window.opener.document.getElementById(window.opener.targetPrefix + "_where");
		
		var xml = getDocXML(rootDoc);
		var beginIndex = xml.indexOf("<root>");
		var endIndex = xml.indexOf("</root>");
		xml = xml.substring(beginIndex + 6, endIndex);
		
		divElement.setAttribute("xml", xml);
		
		divElement.innerHTML = whereField_input_advance(window.opener.targetPrefix, fieldNode, nodeArray).innerHTML;
		
		window.close();
	}
	
	function checkIsInputNum()
	{
		if( paramObj.fieldId != "id" 
				&& paramObj.dataType != "integer"
					&& paramObj.dataType != "long"
						&& paramObj.dataType != "float"
							&& paramObj.dataType != "double"
			)
		return true;
	
		if( !( checkIsAllNum( event.keyCode ) )
				&& event.keyCode!=45 
					&& event.keyCode!=189 
						&& event.keyCode!=109 
							&& event.keyCode != 190
								&& event.keyCode != 110
							)
		{   
			alert("只能输入数字，\"-\"，\".\"号，请同时检查您输入的是否是一个数字！");
			return   false;
		}
	}
	
	function isLegalityNum( numString )
	{
		var  newPar= /^(-|\+)?\d+(\.\d+)?$/ ;
	
		return newPar.test( numString );		
	}
	
	function checkIsAllNum( kc )
	{
		if( ( kc >= 48 && kc <= 57 ) || ( kc >= 96 && kc <= 105 ) || event.keyCode == 46  || event.keyCode == 8 || event.keyCode == 13)
			return true;
		else 
			return false;
	}
</script>
</head>
<body onLoad="initTable();">
<%
	String fieldId = request.getParameter( "fieldId" );
	String dataType = request.getParameter( "dataType" );
%>
<h3 align='center' id="fieldTitle"></h3>

<table width="100%" align="center">
	<tr>
		<td>样例</td>
		<td colSpan="2" id="demoTd">
			
		</td>
	</tr>
	<tr>
		<td noWrap>
			<span style="cursor: pointer;color: blue;" onClick="backspaceAdvanceFormula();">回退</span>
		</td>
		<td noWrap>
			<table id="advance_table" cellspacing="5" cellpadding="5">
				<tr>					
				</tr>
			</table>
		</td>
		<td>
		
			<%
				if( "create_time".equals( fieldId )|| "last_modify_time".equals( fieldId )|| "action_time_range".equals( fieldId )|| "timestamp".equals( dataType ))
				{
			%>
				<input type="text" size=15 id="advance_value" onfocus="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" onkeydown="return checkIsInputNum();"><input type="button" value="添加" onClick="addValue();">
			<%
				}else{
			%>
				<input type="text" size=15 id="advance_value" onkeydown="return checkIsInputNum();"><input type="button" value="添加" onClick="addValue();">
			<%
				}
			%>
		</td>
	</tr>
	<tr align="center">
		<td colSpan=3>
			<input type="button" value="=" onClick="editAdvanceFormula_tag('=');">
			<input type="button" value="!=" onClick="editAdvanceFormula_tag('!=');">
			
			<%
				if( "id".equals( fieldId )
					|| "create_time".equals( fieldId )
						|| "last_modify_time".equals( fieldId )
						|| "action_time_range".equals( fieldId )
							|| "integer".equals( dataType )
								|| "long".equals( dataType )
									|| "float".equals( dataType )
										|| "double".equals( dataType )
											|| "timestamp".equals( dataType )
					)
				{
			%>
			<input type="button" value="&lt;" onClick="editAdvanceFormula_tag('&lt;');">
			<input type="button" value="&lt;=" onClick="editAdvanceFormula_tag('&lt;=');">
			<input type="button" value=">" onClick="editAdvanceFormula_tag('>');">
			<input type="button" value=">=" onClick="editAdvanceFormula_tag('>=');">
			<%
				}
				else
				{
			%>
			<input type="button" value="like" onClick="editAdvanceFormula_tag('like');">
			<input type="button" value="not like" onClick="editAdvanceFormula_tag('not like');">	
			<%
				}
			%>
			
			<input type="button" value="is null" onClick="editAdvanceFormula_tag('is null');">
			<input type="button" value="is not null" onClick="editAdvanceFormula_tag('is not null');">
			
			<input type="button" value="and" onClick="editAdvanceFormula_tag('and');">
			<input type="button" value="or" onClick="editAdvanceFormula_tag('or');">
			<input type="button" value="(" onClick="editAdvanceFormula_tag('(');">
			<input type="button" value=")" onClick="editAdvanceFormula_tag(')');">
		</td>
	</tr>
	<tr align="center">
		<td colSpan=3>
			<input type="button" value="提交" onClick="setReturValue();">
			<input type="button" value="取消" onClick="window.close();">
		</td>
	</tr>
</table>
</body>
</html>
