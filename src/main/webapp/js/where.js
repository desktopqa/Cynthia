var __DEBUG__ = false;
var time_field_array = ["create_time","last_modify_time","action_time_range"];  //时间字段

function checkIsInputNum(event)
{
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

// 拼装出status的where域的内容
// optionsXML内容为status的所有可能性
// whereXML内容为，需要其中记录的选项选上
function whereField_status( prefix, fieldNode, whereXML )
{
	return whereField_user_status( prefix, fieldNode, whereXML, "status" );
}

// 拼装用户域
function whereField_create_user( prefix, fieldNode, whereXML )
{
	return whereField_user_status( prefix, fieldNode, whereXML, "create_user" );
}

function whereField_assign_user( prefix, fieldNode, whereXML )
{
	return whereField_user_status( prefix, fieldNode, whereXML, "assign_user" );
}

function whereField_user_status( prefix, fieldNode, whereXML, suffix )
{
	if( fieldNode == null )
		return "";
		
	var selectedStatus = "";
	var method = null;
	var isAdvance = false;
	var isAll = false;
	
	if( whereXML != null )
	{
		if( whereXML.length > 1 )
			isAdvance = true;
			
		for( var i = 0; i < whereXML.length; i++ )
		{
			if( whereXML[i].nodeName == "field" )
			{
				selectedStatus += ( "[" + getTextContent(whereXML[i]) + "]," );
				method = whereXML[i].getAttribute( "method" );
			}
			
			if( !isAll && whereXML[i].getAttribute( "isAll" ) == "true" )
				isAll = true;
		}
		
		if( isAll )
		{
			isAdvance = false;
			selectedStatus = "[[all]]";
			method = "in";
		}
	}
	
	// 检查被选中的方法
	if( method != null )
	{
		if( method == "=" )
			method = "in";
		else if( method == "!="  )
			method = "not in";
	}

	var divElement = document.createElement( "div" );
	divElement.id = prefix + "_" + suffix + "_where";
	divElement.fieldId = suffix;
	divElement.fieldName = getChinese_title( suffix );
	divElement.type = "selection";
	
	var tableElement = document.createElement("table");
	tableElement.width = "100%";
	
	var trElement = document.createElement("tr");
	
	// 拼上表头
	var tdElement = document.createElement( "td" );

	tdElement.width = "25%";
	setTextContent(tdElement, getChinese_title( suffix ) + ":");
	tdElement.noWrap = true;
	
	var brDom = document.createElement( "br" );
	tdElement.appendChild( brDom );
	
	var spanDom = document.createElement( "span" );
	spanDom.style.color = "blue";
	spanDom.style.cursor = "pointer";
	spanDom.style.width = "70px";
	spanDom.setAttribute("onclick", "changeOperateType_selection(\"" + prefix + "_" + suffix + "\")");

	setTextContent(spanDom, isAdvance?"普通设置":"高级设置");
	tdElement.appendChild( spanDom );
	
	
	trElement.appendChild( tdElement );
	// 表头结束
	
	// 拼上方法的格子
	tdElement = document.createElement( "td" );
	var methodSelectElement = document.createElement( "select");
	methodSelectElement.id = prefix + "_" + suffix + "_method";
	methodSelectElement.setAttribute("onchange", "setXMLResult('" + prefix + "_" + suffix + "', false )");
	
	var optionElement = document.createElement("option");
	methodSelectElement.appendChild( optionElement );	

	optionElement = document.createElement( "option" );
	optionElement.value = "in";
	if( method == "in" )
		optionElement.selected = true;
	setTextContent(optionElement, "in");	
	methodSelectElement.appendChild( optionElement );
	
	optionElement = document.createElement( "option" );
	optionElement.value = "not in";
	if( method == "not in" )
		optionElement.selected = true;
	setTextContent(optionElement, "not in");	
	methodSelectElement.appendChild( optionElement );
	
	optionElement = document.createElement( "option" );
	optionElement.value = "is null";
	if( method == "is null" )
		optionElement.selected = true;
	setTextContent(optionElement, "is null");	
	methodSelectElement.appendChild( optionElement );
	
	optionElement = document.createElement( "option" );
	optionElement.value = "is not null";
	if( method == "is not null" )
		optionElement.selected = true;
	setTextContent(optionElement, "is not null");	
	methodSelectElement.appendChild( optionElement );
	
	tdElement.appendChild( methodSelectElement );
	trElement.appendChild( tdElement );
	// 条件格子结束
	
	// 候选项的拼装开始
	tdElement = document.createElement( "td" );
	
	var valueSelectElement = document.createElement( "select" );
	valueSelectElement.id = prefix + "_" + suffix + "_value";
	valueSelectElement.style.width= "16em";
	valueSelectElement.setAttribute("onchange", "setXMLResult('" + prefix + "_" + suffix + "', false )");
	if(isAdvance)
	{
		valueSelectElement.multiple = true;
		valueSelectElement.size = 5;
	}
	
	optionElement = document.createElement( "option" );
	optionElement.value = "";
	valueSelectElement.appendChild( optionElement );
	
	var optionNodes = fieldNode.selectNodes("options/option");	

	for( var i = 0; i < optionNodes.length; i++ )
	{
		optionElement = document.createElement( "option" );
		optionElement.value = optionNodes[i].getAttribute( "value" );
			
		setTextContent(optionElement, getTextContent(optionNodes[i]));
		
		valueSelectElement.appendChild( optionElement );
	}
	
	// 非高级设置，则需要加上All选项
	if( !isAdvance )
	{
		optionElement = document.createElement( "option" );
		optionElement.value = "[all]";
			
		setTextContent(optionElement, "[all]");
		
		if( selectedStatus.indexOf( "[[all]]" ) != -1 )
			optionElement.selected = true;			
		
		valueSelectElement.appendChild( optionElement );
	}
	
	for( var i = 0; i < optionNodes.length; i++ )
	{
		if( selectedStatus.indexOf( "[" + optionNodes[i].getAttribute( "value" ) + "]" ) != -1 )
		{			
			valueSelectElement.options[i + 1].selected = true;
		}
	}
	
	tdElement.appendChild( valueSelectElement );
	trElement.appendChild( tdElement );

	// 候选项的拼装结束
	
	tableElement.appendChild(trElement);
	
	divElement.appendChild(tableElement);
	
	return divElement;
}

//selection
function whereField_selection( prefix, fieldNode, whereXML )
{
	if( fieldNode == null )
		return "";
	
	var selectedOptions = "";
	var selectedMethod = null;
	var selectedCondition = null;
	var fieldName = getChinese_title($(fieldNode).find("name").text());
	var fieldId = getTextContent($(fieldNode).find("id").text());
	var dataType = getTextContent($(fieldNode).find("dataType").text());
	var type = "selection";
	
	var typeNode = $(fieldNode).find("type");
	if(typeNode)
		type = typeNode.text();
	
	var isAdvance = false;
	var isAll = false;
	
	for( var i = 0; whereXML != null && i < whereXML.length; i++ )
	{
		if( !isAll && whereXML[i].getAttribute( "isAll" ) == "true" )
			isAll = true;
		
		if( whereXML[i].nodeName == "field" )
		{
			if( fieldId == null )
				fieldId = whereXML[i].getAttribute( "id" );
			
			if( fieldId == null )
				fieldId = whereXML[i].getAttribute( "name" );
				
			if( dataType == null )
				dataType = whereXML[i].getAttribute( "dataType" );
			
			var selectedValue = getTextContent(whereXML[i]).split(",");
			if( selectedValue.length > 1 )
				isAdvance = true;
			
			for(var j = 0; selectedValue != null && j < selectedValue.length; j++)
			{
				selectedOptions += ( "[" + selectedValue[j] + "]" );
			}
			
			if(selectedMethod == null)
			{
				selectedMethod = whereXML[i].getAttribute( "method" );
			}
			
			if(fieldName == null)
			{
				fieldName = whereXML[i].getAttribute("name");
			}
		}
		else if(whereXML[i].nodeName == "condition")
		{
			if(selectedCondition == null)
			{
				if(getTextContent(whereXML[i]) == "and" || getTextContent(whereXML[i]) == "or")
				{
					selectedCondition = getTextContent(whereXML[i]);
				}
			}
			
			isAdvance = true;
		}
	}
	
	if( isAll )
	{
		isAdvance = false;
		selectedOptions = "[[all]]";
	}
	
	
	if(selectedCondition == "and")
	{
		if(selectedMethod == "like")
		{
			selectedMethod = "=";
		}
		else if(selectedMethod == "not like")
		{
			selectedMethod = "!=";
		}
	}
	
	var divElement = document.createElement( "div" );
	divElement.id = prefix + "_where";
	divElement.fieldId = fieldId;
	divElement.fieldName = getChinese_title( fieldName );
	
	if( dataType != null )
		divElement.dataType = dataType;
	
	divElement.type = type;
	
	var tableElement = document.createElement("table");
	tableElement.width = "100%";
	
	var trElement = document.createElement("tr");
	
	// 拼上表头
	var tdElement = document.createElement( "td" );

	tdElement.width = "25%";
	setTextContent(tdElement, getChinese_title( fieldName ));
	tdElement.noWrap = true;
	
	var brDom = document.createElement( "br" );
	tdElement.appendChild( brDom );
	
	var spanDom = document.createElement( "span" );
	spanDom.style.color = "blue";
	spanDom.style.cursor = "pointer";
	spanDom.style.width = "70px";
	spanDom.setAttribute("onclick", "changeOperateType_selection(\"" + prefix + "\")");
	
	if( isAdvance )
		setTextContent(spanDom, "普通设置");
	else
		setTextContent(spanDom, "高级设置");
		
	tdElement.appendChild( spanDom );
	trElement.appendChild( tdElement );

	// 表头结束
	
	// 拼上方法的格子
	tdElement = document.createElement( "td" );
	var methodSelectElement = document.createElement( "select" );
	methodSelectElement.id = prefix + "_method";
	methodSelectElement.style.width = "8em";
	methodSelectElement.setAttribute("onchange", "setXMLResult('" + prefix + "', false )");
	
	var optionElement = document.createElement("option");
	methodSelectElement.appendChild( optionElement );
	
	if( dataType == "multiple" || type == "attachment" )
	{
		if( isAll )
			selectedMethod = "=";
		
		optionElement = document.createElement( "option" );
		optionElement.value = "=";
		if( selectedMethod == "=" )
			optionElement.selected = true;
		setTextContent(optionElement, "=");	
		methodSelectElement.appendChild( optionElement );
		
		optionElement = document.createElement( "option" );
		optionElement.value = "!=";
		if( selectedMethod == "!=" )
			optionElement.selected = true;
		setTextContent(optionElement, "!=");	
		methodSelectElement.appendChild( optionElement );
		
		optionElement = document.createElement( "option" );
		optionElement.value = "like";
		if( selectedMethod == "like" )
			optionElement.selected = true;
		setTextContent(optionElement, "like");	
		methodSelectElement.appendChild( optionElement );
		
		optionElement = document.createElement( "option" );
		optionElement.value = "not like";
		if( selectedMethod == "not like" )
			optionElement.selected = true;
		setTextContent(optionElement, "not like");	
		methodSelectElement.appendChild( optionElement );
		
		optionElement = document.createElement( "option" );
		optionElement.value = "in";
		if( selectedMethod == "in" )
			optionElement.selected = true;
		setTextContent(optionElement, "in");	
		
		optionElement = document.createElement( "option" );
		optionElement.value = "not in";
		if( selectedMethod == "not in" )
			optionElement.selected = true;
		setTextContent(optionElement, "not in");	
	}
	else
	{	
		if( isAll )
			selectedMethod = "in";
		
		optionElement = document.createElement( "option" );
		optionElement.value = "in";
		if( selectedMethod == "in" )
			optionElement.selected = true;
		setTextContent(optionElement, "in");	
		methodSelectElement.appendChild( optionElement );
		
		optionElement = document.createElement( "option" );
		optionElement.value = "not in";
		if( selectedMethod == "not in" )
			optionElement.selected = true;
		setTextContent(optionElement, "not in");	
		methodSelectElement.appendChild( optionElement );
	}
	
	
	optionElement = document.createElement( "option" );
	optionElement.value = "is null";
	if( selectedMethod == "is null" )
		optionElement.selected = true;
	setTextContent(optionElement, "is null");	
	methodSelectElement.appendChild( optionElement );
	
	optionElement = document.createElement( "option" );
	optionElement.value = "is not null";
	if( selectedMethod == "is not null" )
		optionElement.selected = true;
	setTextContent(optionElement, "is not null");	
	methodSelectElement.appendChild( optionElement );
	
	tdElement.appendChild( methodSelectElement );
	trElement.appendChild( tdElement );
	
	// 条件格子结束
	
	// 候选项的拼装开始
	tdElement = document.createElement( "td" );
	
	var valueSelectElement;
	valueSelectElement = document.createElement( "select" );
	valueSelectElement.id = prefix + "_value";
	valueSelectElement.style.width = "16em";
	valueSelectElement.setAttribute("onchange", "setXMLResult('" + prefix + "', false )");

	if(isAdvance)
	{	
		valueSelectElement.multiple = true;
		valueSelectElement.size = 5;
	}
	
	var optionNodes = fieldNode.selectNodes("options/option");
	
	optionElement = document.createElement( "option" );
	optionElement.value = "";
	valueSelectElement.appendChild( optionElement );
	
	
	for( var i = 0; i < optionNodes.length; i++ )
	{
		optionElement = document.createElement( "option" );
		optionElement.value = optionNodes[i].getAttribute( "value" );
		
		if( !isAll && selectedOptions.indexOf( "[" + optionNodes[i].getAttribute( "value" ) + "]" ) != -1 )
			optionElement.selected = true;
			
		setTextContent(optionElement, getTextContent(optionNodes[i]));	
		valueSelectElement.appendChild( optionElement );
	}
	
	
	// 非高级设置，则需要加上All选项
	if( !isAdvance )
	{
		optionElement = document.createElement( "option" );
		optionElement.value = "[all]";
			
		setTextContent(optionElement, "[all]");
		
		if( selectedOptions.indexOf( "[[all]]" ) != -1 )
			optionElement.selected = true;
		
		valueSelectElement.appendChild( optionElement );
	}	
	
	tdElement.appendChild( valueSelectElement );
	trElement.appendChild( tdElement );
	
	tableElement.appendChild(trElement);
	
	divElement.appendChild(tableElement);
	
	return divElement;
}

//reference
function whereField_reference( prefix, fieldNode, whereXML )
{
	return whereField_selection( prefix, fieldNode, whereXML );
}

// 输入框的输入条件的拼装
// TODO 先实现了基本的显示，高级的往后再添加
function whereField_input( prefix, fieldNode, whereXML )
{	
	// 是否要高级
	if( whereXML != null && whereXML.length > 1 )
	{
		return whereField_input_advance( prefix, fieldNode, whereXML );
	}
	
	// 数据安全检查
	if( prefix == null || fieldNode == null )
		return;
	
	// 获取数据类型
	var dataType = null;
	var fieldName = null;
	var type = null;
	
	// 域的ID，基本字段可能没有ID，则从name中取
	var fieldId = null;
	
	for( var i = 0; i < fieldNode.childNodes.length; i++ )
	{
		if( fieldNode.childNodes[i].nodeName == "id" )
			fieldId = getTextContent(fieldNode.childNodes[i]);
		else if( fieldNode.childNodes[i].nodeName == "name" )
			fieldName = getTextContent(fieldNode.childNodes[i]);
		else if( fieldNode.childNodes[i].nodeName == "dataType" )
			dataType = getTextContent(fieldNode.childNodes[i]);
		else if( fieldNode.childNodes[i].nodeName == "type" )
			type = getTextContent(fieldNode.childNodes[i]);
	}
	
	if( fieldId == null )
		fieldId = fieldName;
	
	fieldName = getChinese_title( fieldName );
	
	// 结果的Div容器，需要将fieldId和数据类型等信息记录在div对象上
	// var rootDoc = new ActiveXObject("Msxml.DOMDocument");
	var divElement = document.createElement( "div" );
	divElement.fieldId = fieldId;
	divElement.fieldName = fieldName;
	
	if( dataType != null )
		divElement.dataType = dataType;
	
	divElement.type = "input";
	divElement.id = prefix + "_where";	
	
	var tableElement = document.createElement( "table" );
	tableElement.width = "100%";
	
	var trElement = document.createElement( "tr" );
	
	// 拼上表头
	var titleTdElement = document.createElement( "td" );
	
	titleTdElement.width= "116px";
	titleTdElement.noWrap = true;
	
	setTextContent(titleTdElement, fieldName + ":");
	
	var brDom = document.createElement( "br" );
	titleTdElement.appendChild( brDom );
	
	var spanDom = document.createElement( "span" );
	spanDom.style.color = "blue";
	spanDom.style.cursor = "pointer";
	spanDom.style.width = "70px";
	spanDom.setAttribute("onclick", "changeOperateType(\"" + prefix + "\", true)");
	setTextContent(spanDom, "高级设置");
	titleTdElement.appendChild( spanDom );
	
	// 非高级的设置
	var method = "";
	var value = "";
	if( whereXML == null || whereXML.length == 1 )
	{	
		if( whereXML != null )
		{
			method = whereXML[0].getAttribute( "method" );
			value =  getTextContent(whereXML[0]);
		}
		else
		{
			if( $.inArray(fieldId,time_field_array) != -1)
				value = "2007-01-01 00:00:00";
		}
	}
	
	var methodTdElement = document.createElement( "td" );
	
	var methodSelectElement = document.createElement( "select" );
	methodSelectElement.style.width = "8em";
	methodSelectElement.id = prefix + "_method";
	methodSelectElement.setAttribute("onchange", "setXMLResult(\"" + prefix + "\", false, 'onchange')");
	
	var optionElement = document.createElement( "option" );
	methodSelectElement.appendChild( optionElement );
	
	optionElement = document.createElement( "option" );
	optionElement.value = "=";
	if( method == "=" )
		optionElement.selected = true;
	setTextContent(optionElement, "=");	
	methodSelectElement.appendChild( optionElement );
	
	optionElement = document.createElement( "option" );
	optionElement.value = "!=";
	if( method == "!=" )
		optionElement.selected = true;
	setTextContent(optionElement, "!=");	
	methodSelectElement.appendChild( optionElement );
	
	// 数字字段
	if( fieldId == "id" 
		|| dataType == "integer"
			|| dataType == "float" 
				|| dataType == "long"
					|| dataType == "double"
						|| dataType == "timestamp"
							|| $.inArray(fieldId,time_field_array) != -1
						)
	{
		optionElement = document.createElement( "option" );
		optionElement.value = ">";
		if( method == ">" )
			optionElement.selected = true;
		setTextContent(optionElement, ">");	
		methodSelectElement.appendChild( optionElement );
		
		optionElement = document.createElement( "option" );
		optionElement.value = ">=";
		if( method == ">=" )
			optionElement.selected = true;
		setTextContent(optionElement, ">=");	
		methodSelectElement.appendChild( optionElement );
		
		optionElement = document.createElement( "option" );
		optionElement.value = "<";
		if( method == "<" )
			optionElement.selected = true;
		setTextContent(optionElement, "<");	
		methodSelectElement.appendChild( optionElement );
		
		optionElement = document.createElement( "option" );
		optionElement.value = "<=";
		if( method == "<=" )
			optionElement.selected = true;
		setTextContent(optionElement, "<=");	
		methodSelectElement.appendChild( optionElement );		
	}
	else
	{
		optionElement = document.createElement( "option" );
		optionElement.value = "like";
		if( method == "like" )
			optionElement.selected = true;
		setTextContent(optionElement, "like");	
		methodSelectElement.appendChild( optionElement );
		
		optionElement = document.createElement( "option" );
		optionElement.value = "not like";
		if( method == "not like" )
			optionElement.selected = true;
		setTextContent(optionElement, "not like");	
		methodSelectElement.appendChild( optionElement );		
	}
	
	optionElement = document.createElement( "option" );
	optionElement.value = "is null";
	if( method == "is null" )
		optionElement.selected = true;
	setTextContent(optionElement, "is null");	
	methodSelectElement.appendChild( optionElement );
	
	optionElement = document.createElement( "option" );
	optionElement.value = "is not null";
	if( method == "is not null" )
		optionElement.selected = true;
	setTextContent(optionElement, "is not null");	
	methodSelectElement.appendChild( optionElement );		
	
	methodTdElement.appendChild( methodSelectElement );

	// 输入框的段
	var valueTdElement = document.createElement( "td" );
	var inputElement = document.createElement( "input" );
	inputElement.id = prefix + "_value";
	inputElement.type = "text";
	inputElement.size = 29;
	inputElement.value = value;
	inputElement.setAttribute("blur", "setXMLResult(\"" + prefix + "\", false, 'keyup')");
	
	if( fieldId == "id" 
		|| dataType == "integer"
			|| dataType == "float" 
				|| dataType == "long"
					|| dataType == "double" 
		)
		inputElement.setAttribute("onkeydown", "return checkIsInputNum(event)");
	
	
	valueTdElement.appendChild( inputElement );
	
	// 检查是否是时间字段，需要添加时间控件
	if( dataType == "timestamp" || $.inArray(fieldId,time_field_array) != -1)
	{
		
		var timeTitleSpan = $("<img onclick=\"WdatePicker({el:'"+prefix+"_value',errDealMode:'2', dateFmt:'yyyy-MM-dd HH:mm:ss'})\" src='/lib/My97DatePicker/skin/datePicker.gif' width='16' height='22' align='absmiddle'>");
		
//		var timeTitleSpan = document.createElement("img");
//		timeTitleSpan
		$(valueTdElement).append(timeTitleSpan);
		
		
/*		// var timeTitleSpan = document.createElement( "<span style='color: blue;cursor: hand;' onClick='dropit2( " + prefix + "displayTd );event.cancelBubble=true;return false'></span>" );
		var imgDom = document.createElement( "<img onClick='dropit2( \"" + prefix + "displayTd\", event );event.cancelBubble=true;return false' src='../images/xinjian.gif'/>" );
		valueTdElement.appendChild( imgDom );
		
		var timeDIV = document.createElement( "<div onClick='event.cancelBubble=true;return false;' width='100%' height='100%'></div>" );
		
		var timeMemuDiv = document.createElement( "<div id='" + prefix + "displayTd' class='dropmenu03' onClick='hidemenu2( this );' style='position:absolute;left:10;top:0;layer-background-color:seashell;background-color:seashell;width:100;visibility:hidden;border:1px solid black;padding:0px;z-index:105'></div>" );
		
		var wwsCalendar = new WWSCalender();
		
		wwsCalendar.setWidth( 370 );
		wwsCalendar.setHeight( 250 );
		
		wwsCalendar.createCalendar( timeMemuDiv, prefix );
		
		timeMemuDiv.op.registerValueObj( inputElement );
		
		inputElement.onpropertychange = new Function( "setXMLResult(\"" + prefix + "\", false)" ); 
		
		setTimeout( "try{document.getElementById( '" + prefix + "displayTd' ).op.initial()}catch( e ){}", 2000 );
		// timeMemuDiv.op.initial();
		
		timeDIV.appendChild( timeMemuDiv );
		
		// timeDIV.appendChild( timeDIV.createElement( "" ) );
		
		valueTdElement.appendChild( timeDIV );*/
	}

	// 总的拼装
	trElement.appendChild(titleTdElement);
	trElement.appendChild(methodTdElement);
	trElement.appendChild(valueTdElement);
	
	tableElement.appendChild(trElement);
		
	divElement.appendChild( tableElement );
	
	return divElement;
}

function changeOperateType_selection( prefix )
{
	var spanObj = document.getElementById( prefix + "_span" );
	var valueObj = document.getElementById( prefix + "_value" );
	var methodObj = document.getElementById( prefix + "_method" );
	
	if( valueObj.multiple )
	{
		valueObj.multiple = false;
		valueObj.size = 1;
		setTextContent(spanObj, "高级设置");
		methodObj.selectedIndex = 0;		
			
		var optionElement = document.createElement( "option" );
		optionElement.value = "[all]";
			
		setTextContent(optionElement, "[all]");
		
		valueObj.appendChild( optionElement );
		
		setXMLResult( prefix , false );		
	}
	else
	{
		valueObj.multiple = true;
		valueObj.size = 5;
		setTextContent(spanObj, "普通设置");
		
		valueObj.options.length--;
		
		methodObj.selectedIndex = 0;
		setXMLResult( prefix , true );
	}
	enableSelectSearch();
}

// 切换高级和普通设置
function changeOperateType( prefix, isAdvance, isClose )
{
	var divObj = document.getElementById( prefix + "_where" );
	
	divObj.removeAttribute("xml");
	
	var fieldId = divObj.getAttribute("fieldId");
	var fieldName = getChinese_title( divObj.getAttribute("fieldName") );
	var dataType = divObj.getAttribute("dataType");
	var type = divObj.getAttribute("type");
	
	var rootDoc = getXMLDoc();
	var fieldNode = rootDoc.createElement( "field" );
	var fieldIdNode = rootDoc.createElement( "id" );
	setTextContent(fieldIdNode, fieldId);
	fieldNode.appendChild( fieldIdNode );
	
	var fieldNameNode = rootDoc.createElement( "name" );
	setTextContent(fieldNameNode, fieldName);
	fieldNode.appendChild( fieldNameNode );
	
	if( dataType != null )
	{
		var fieldDataTypeNode = rootDoc.createElement( "dataType" );
		setTextContent(fieldDataTypeNode, dataType);
		fieldNode.appendChild( fieldDataTypeNode );
	}
	
	if( type != null )
	{
		var fieldTypeNode = rootDoc.createElement( "type" );
		setTextContent(fieldTypeNode, type);
		fieldNode.appendChild( fieldTypeNode );
	}
	
	if( isAdvance )
	{	
		divObj.innerHTML = whereField_input_advance(prefix, fieldNode, null).innerHTML;
		if(!isClose)
			editAdvance_input( prefix );
	}
	else
	{
		divObj.innerHTML = whereField_input(prefix, fieldNode, null).innerHTML;
		hiddenAdvanceDiv();
	}
	enableSelectSearch();
}

// 输入框的高级
function whereField_input_advance( prefix, fieldNode, whereXML )
{
	// 数据安全检查
	if( prefix == null || fieldNode == null )
		return null;
	
	// 获取数据类型
	var dataType = null;
	var fieldName = null;
	
	// 域的ID，基本字段可能没有ID，则从name中取
	var fieldId = null;	
	
	for( var i = 0; i < fieldNode.childNodes.length; i++ )
	{
		if( fieldNode.childNodes[i].nodeName == "id" )
			fieldId = getTextContent(fieldNode.childNodes[i]);
		else if( fieldNode.childNodes[i].nodeName == "name" )
			fieldName = getTextContent(fieldNode.childNodes[i]);
		else if( fieldNode.childNodes[i].nodeName == "dataType" )
			dataType = getTextContent(fieldNode.childNodes[i]);
	}
	
	if( fieldId == null )
		fieldId = fieldName;
	
	fieldName = getChinese_title( fieldName );
	
	// var rootDoc = new ActiveXObject("Msxml.DOMDocument");
	var divObj = document.createElement( "div" );
	divObj.fieldId = fieldId;
	divObj.fieldName = fieldName;
	
	if( dataType != null )
		divObj.dataType = dataType;
	
	divObj.type = "input";
	divObj.id = prefix + "_where";
	
	var tableObj = document.createElement("table");
	tableObj.width = "100%";
	
	var trObj = document.createElement("tr");
	
	var tdElement = document.createElement( "td" );
	
	tdElement.width= "25%";
	tdElement.noWrap = true;
	
	setTextContent(tdElement, fieldName + ":");
	
	var brDom = document.createElement( "br" );
	tdElement.appendChild( brDom );
	
	var spanDom = document.createElement( "span" );
	spanDom.style.color = "blue";
	spanDom.style.cursor = "pointer";
	spanDom.style.width = "70px";
	spanDom.setAttribute("onclick", "changeOperateType(\"" + prefix + "\", false)" );
	setTextContent(spanDom, "普通设置");
	tdElement.appendChild( spanDom );

	trObj.appendChild( tdElement );
	
	tdElement = document.createElement( "td" );
	var str = "";
	
	for( var i = 0; whereXML != null && i < whereXML.length; i++ )
	{
		if( whereXML[i].nodeName == "field" )
		{
			str += whereXML[i].getAttribute( "name" );
			str += " ";
			str += whereXML[i].getAttribute( "method" );
			str += " ";
			str += getTextContent(whereXML[i]);
			
			str += " ";
		}
		else
			str += getTextContent(whereXML[i]);

		str += " ";
	}
	setTextContent(tdElement, str);
	// rowElement.appendChild( tdElement );
	trObj.appendChild( tdElement );
	
	// 编辑高级表达式
	tdElement = document.createElement( "td" );
	var spanElement = document.createElement( "span" );
	spanElement.style.color = "blue";
	spanElement.style.cursor = "pointer";
	spanElement.style.width = "35px";
	spanElement.setAttribute("onclick", "editAdvance_input(\"" + prefix + "\")" );

	setTextContent(spanElement, "编辑");
	tdElement.appendChild( spanElement );
	trObj.appendChild( tdElement );
	
	tableObj.appendChild(trObj);
	
	divObj.appendChild(tableObj);
	
	return divObj;
}

var targetPrefix = null;
var paramObj = null;
function editAdvance_input( prefix )
{
	var url;
	targetPrefix = prefix;
	
	var divElement = document.getElementById( prefix + "_where" );
	var xml = divElement.getAttribute("xml");
	var fieldName = getChinese_title( divElement.getAttribute("fieldName") );
	var type = divElement.getAttribute("type");
	var dataType = divElement.getAttribute("dataType");
	var fieldId = divElement.getAttribute("fieldId");
	
	paramObj = new Object();
	paramObj.xml = xml;
	paramObj.fieldName = fieldName;
	paramObj.type = type;
	paramObj.dataType = dataType;
	paramObj.fieldId = fieldId;
	
	var rootDir = getRootDir();
	url = rootDir + "filterManage/whereField_advance.jsp?fieldId=" + getSafeParam(fieldId) + "&dataType=" + dataType;
	
	window.open(url, null,
			      "toolbar=no,menubar=no,personalbar=no,left=300,top=300,width=400,height=200," +
			      "scrollbars=yes,resizable=yes");	
}

// 把指定的高级的表达式回退一格
function backspaceAdvanceFormula( advanceTableObj )
{
	if( advanceTableObj.rows[0].cells.length > 0 )
	{
		if( advanceTableObj.rows[0].cells[advanceTableObj.rows[0].cells.length -1 ].type == "method" )
			advanceTableObj.rows[0].deleteCell( advanceTableObj.rows[0].cells.length - 1 );
		
		advanceTableObj.rows[0].deleteCell( advanceTableObj.rows[0].cells.length - 1 );
	}
}

// 将输入框的高级搜索的输入框的内容添加到高级表达式中
function editAdvance_value( prefix )
{
	var valueInput = document.getElementById( prefix + "_formulaInput" );
	var formulaTable = document.getElementById( prefix + "_advance_table" );
	
	var tdObj = document.createElement( "td" );
	tdObj.type = "value";
	tdObj.noWrap = true;
	setTextContent(tdObj, valueInput.value);
	
	formulaTable.rows[0].appendChild( tdObj );
	
	valueInput.value = "";
}

// 把符号输入到高级表达式中
function editAdvanceFormula_tag()
{
	var buttonObj = event.srcElement;
	var prefix = buttonObj.prefix;
	var divObj = document.getElementById( prefix + "_where" );
	var value = buttonObj.value;
	var fieldName = divObj.fieldName;
	
	var formulaRow = document.getElementById( prefix + "_advance_table" ).rows[0];
	
	if( value != "and"
			&& value != "or"
				&& value != "("
				 && value != ")" 
				 )
	{
		var tdObj = document.createElement( "td" );
		tdObj.type = "param";
		tdObj.noWrap = true;
		setTextContent(tdObj, fieldName);
		formulaRow.appendChild( tdObj );
		
		tdObj = document.createElement( "td" );
		tdObj.type = "method";
		tdObj.noWrap = true;
		setTextContent(tdObj, value);
		formulaRow.appendChild( tdObj );
	}
	else
	{
		var tdObj = document.createElement( "td" );
		tdObj.type = "condition";
		tdObj.noWrap = true;
		setTextContent(tdObj, value);
		formulaRow.appendChild( tdObj );
	}
}


function setXMLResult( prefix , isAdvance, msg )
{	
	var divObj = document.getElementById( prefix + "_where" );
	
	if( divObj == null )
		return;
	
	var fieldId = divObj.getAttribute("fieldId");
	var fieldName = divObj.getAttribute("fieldName");
	var type = divObj.getAttribute("type");
	var dataType = divObj.getAttribute("dataType");
	
	var methodSel = document.getElementById( prefix + "_method" );
	var valueObj = document.getElementById( prefix + "_value" );
	var rootDoc = getXMLDoc();
	
	var rootNode = rootDoc.createElement("root");
	rootDoc.appendChild(rootNode);
	
	if( type == "input" )
	{
		// 非高级
		if( !isAdvance )
		{
			if( methodSel == null || methodSel.selectedIndex <= 0 )
			{
				divObj.removeAttribute("xml");
				return;
			}
			
			var fieldNode = rootDoc.createElement( "field" );
			
			fieldNode.setAttribute( "id", fieldId );
			fieldNode.setAttribute( "name", fieldName );
			
			if( dataType != null )
				fieldNode.setAttribute( "dataType", dataType );
			
			fieldNode.setAttribute( "type", type );
			
			var methodValue = methodSel.options[methodSel.selectedIndex].value;		
			
				
			fieldNode.setAttribute( "method", methodValue );
			
			if( methodValue == "is null" || methodValue == "is not null" )
			{
				if( valueObj.value != "" )
					valueObj.value = "";
				valueObj.disabled = true;
			}
			else
				valueObj.disabled = false;
			
			if( valueObj.value != null )
				setTextContent(fieldNode, valueObj.value);
			
			if($.inArray(fieldId,time_field_array) != -1)
			{
				if(checkTimeError(valueObj.value)){
					alert(fieldName + " 不支持此格式时间查询,目前支持的格式有 ： 今天|昨天|本周|上周|本月|上月|本季|上季|本年|去年|(过去|未来)[1-9][0-9]*(天|周|月|季|年)");
					return;
				}
			}
			
			rootNode.appendChild(fieldNode);
			
			var xml = getDocXML(rootDoc);
			var beginIndex = xml.indexOf("<root>");
			var endIndex = xml.indexOf("</root>");
			xml = xml.substring(beginIndex + 6, endIndex);
			
			
			divObj.setAttribute("xml", xml);
		}
		else
		{
			var valuesForXml = divObj.getAttribute("valuesForXml");
			
			if(valuesForXml == null)
			{
				divObj.removeAttribute("xml");
				return;
			}
			
			var valueArray = valuesForXml.split("|");
			
			var isAdd = true;
			
			if(valueArray[0].split(":")[0] == "condition" && valueArray[0].split(":")[1] == "("
				&& valueArray[valueArray.length - 1].split(":")[0] == "condition" && valueArray[valueArray.length - 1].split(":")[1] == ")")
			{
				var isContained = false;
				for(var i = 1; i < valueArray.length - 1; i++)
				{
					if(valueArray[i].split(":")[0] == "condition" && valueArray[i].split(":")[1] == "(")
					{
						isContained = true;
						break;
					}
				}
				
				if(!isContained)
					isAdd = false;
			}
					
			if(isAdd)
			{
				var startBracket = rootDoc.createElement( "condition" );
				setTextContent(startBracket, "(");
				rootNode.appendChild(startBracket);
			}
			
			for(var i = 0; i < valueArray.length; i++) 
			{
				if(valueArray[i].split(":")[0] == "param")
				{
					var fieldNode = rootDoc.createElement( "field" );
					fieldNode.setAttribute( "id" , fieldId );
					fieldNode.setAttribute( "name" , fieldName );
					fieldNode.setAttribute( "dataType" , dataType );
					fieldNode.setAttribute( "type" , type );
					i++;
					
					if(  valueArray[i].split(":")[0] == "method" )
					{
						fieldNode.setAttribute( "method" , valueArray[i].split(":")[1] );
						
						if( (i+1) < valueArray.length && valueArray[i + 1].split(":")[0] == "value" )
						{
							i++;
							if( valueArray[i].split(":")[1] != null )
								setTextContent(fieldNode, valueArray[i].split(":")[1]);
						}
					}
					else
					{
						alert( "高级表达式错误，请修改" );
						return;
					}
					
					rootNode.appendChild(fieldNode);
				}
				else
				{
					var conditionNode = rootDoc.createElement( "condition" );
					if( valueArray[i].split(":")[1] != null )
						setTextContent(conditionNode, valueArray[i].split(":")[1]);
					
					rootNode.appendChild(conditionNode);
				}
			}
			
			if(isAdd)
			{
				var endBracket = rootDoc.createElement( "condition" );
				setTextContent(endBracket, ")");
				rootNode.appendChild(endBracket);
			}
			
			var xml = getDocXML(rootDoc);
			var beginIndex = xml.indexOf("<root>");
			var endIndex = xml.indexOf("</root>");
			xml = xml.substring(beginIndex + 6, endIndex);
			divObj.setAttribute("xml", xml);
		}
	}
	else
	{
		divObj.removeAttribute("xml");
		
		if( methodSel.selectedIndex <= 0 ){
			if(valueObj.multiple && valueObj.selectedIndex === 0){
				valueObj.selectedIndex = -1;
			}
			return;
		}
		
		var methodVal = methodSel.options[methodSel.selectedIndex].value;
		
		// 处理all的情况
		if( ( !valueObj.multiple ) && ( valueObj.selectedIndex == valueObj.options.length - 1 ) && ( methodVal != "is null" && methodVal != "is not null" ) )
		{
			if( methodVal == "=" || methodVal == "in" || methodVal == "like" )
			{
				var conditionNode = rootDoc.createElement ( "condition" );
				setTextContent(conditionNode, "(");
				conditionNode.setAttribute( "isAll", "true" );
				rootNode.appendChild(conditionNode);
				
				var whereFieldNode = rootDoc.createElement ( "field" );
				whereFieldNode.setAttribute( "isAll", "true" );
				
				if( type != null )			
					whereFieldNode.setAttribute( "type", type );
				
				if( dataType != null )
					whereFieldNode.setAttribute( "dataType", dataType );
					
				if( fieldName != null )
					whereFieldNode.setAttribute( "name", fieldName );
					
				whereFieldNode.setAttribute( "method", "is null" );
				whereFieldNode.setAttribute( "id", fieldId );
				rootNode.appendChild(whereFieldNode);
				
				conditionNode = rootDoc.createElement ( "condition" );
				conditionNode.setAttribute( "isAll", "true" );
				setTextContent(conditionNode, "or");
				rootNode.appendChild(conditionNode);
				
				whereFieldNode = rootDoc.createElement ( "field" );
				whereFieldNode.setAttribute( "isAll", "true" );
				
				if( type != null )			
					whereFieldNode.setAttribute( "type", type );
				
				if( dataType != null )
					whereFieldNode.setAttribute( "dataType", dataType );
					
				if( fieldName != null )
					whereFieldNode.setAttribute( "name", fieldName );
					
				whereFieldNode.setAttribute( "method", "is not null" );
				whereFieldNode.setAttribute( "id", fieldId );
				
				rootNode.appendChild(whereFieldNode);
				
				conditionNode = rootDoc.createElement ( "condition" );
				setTextContent(conditionNode, ")");
				conditionNode.setAttribute( "isAll", "true" );				
				rootNode.appendChild(conditionNode);
				
				var xml = getDocXML(rootDoc);
				var beginIndex = xml.indexOf("<root>");
				var endIndex = xml.indexOf("</root>");
				xml = xml.substring(beginIndex + 6, endIndex);
			
				divObj.setAttribute("xml", xml);
			}
			else
			{
				alert( "只有=，in，like时，all值才有效，请修改!" );
				
				valueObj.options[ valueObj.selectedIndex ].selected = false;
			}
			
			return;
		}
					
		// 对基本字段中的创建人和指派人作特殊处理
		var whereFieldNode = rootDoc.createElement ( "field" );
		
		if( type != null )					
			whereFieldNode.setAttribute( "type", type );
		
		if( dataType != null )
			whereFieldNode.setAttribute( "dataType", dataType );
			
		if( name != null )
			whereFieldNode.setAttribute( "name", name );
		
		
		// 对multiple的selection或者Reference作特殊处理
		if( fieldId == 'status_id' || fieldId == 'create_user' || fieldId == 'log_create_user' || fieldId == 'action_id'  || fieldId == 'assign_user' || dataType != null && dataType == 'multiple' || type == 'attachment' )
		{
			var conditionNode = rootDoc.createElement ( "condition" );
			setTextContent(conditionNode, "(");
			rootNode.appendChild(conditionNode);
			
			var methodCondition = "or";
			
			if( methodVal == "in" )
				methodVal = "=";
			else if( methodVal == "not in" )
			{
				methodVal = "!=";
				methodCondition = "and";
			}
			else if( methodVal == 'like' )
			{
				methodCondition = "or";
			}
			else if( methodVal == 'not like' )
			{
				methodCondition = "and";
			}
			else if( methodVal == "=" )
			{
				methodVal = "like";
				methodCondition = "or";
			}
			else if( methodVal == "!=" )
			{
				methodVal = "not like";
				methodCondition = "and";
			}
			
			var isAddCondition = false;
			
			// 是否选择了选项
			var isExistOption = false;
		
			for( var ifv = 1; ifv < valueObj.options.length; ifv++ )
			{
				if( !valueObj.options[ifv].selected )
					continue;
				
				if( !isAddCondition )
				{											
					isAddCondition = true;
				}
				else
				{
					conditionNode = rootDoc.createElement ( "condition" );
					setTextContent(conditionNode, methodCondition);
					rootNode.appendChild(conditionNode);
				}
				
				whereFieldNode = rootDoc.createElement ( "field" );
				isExistOption = true;
				
				if( type != null )			
					whereFieldNode.setAttribute( "type", type );
				
				if( dataType != null )
					whereFieldNode.setAttribute( "dataType", dataType );
					
				if( fieldName != null )
					whereFieldNode.setAttribute( "name", fieldName );
					
				whereFieldNode.setAttribute( "method", methodVal );
				whereFieldNode.setAttribute( "id", fieldId );
				
				if( valueObj.options[ifv].value != null )
					setTextContent(whereFieldNode, valueObj.options[ifv].value);
				
				rootNode.appendChild(whereFieldNode);
			}
		
			if( !isExistOption )
			{
				whereFieldNode = rootDoc.createElement ( "field" );
				whereFieldNode.setAttribute( "method", methodVal );
				whereFieldNode.setAttribute( "id", fieldId );
				
				if( type != null )
					whereFieldNode.setAttribute( "type", type );
				
				if( dataType != null )
					whereFieldNode.setAttribute( "dataType", dataType );
					
				if( fieldName != null )
					whereFieldNode.setAttribute( "name", fieldName );
				
				rootNode.appendChild(whereFieldNode);
			}
			
			conditionNode = rootDoc.createElement( "condition" );
			setTextContent(conditionNode, ")");
			rootNode.appendChild(conditionNode);
		}
		// 对single的reference或者selection作处理
		else
		{
			whereFieldNode.setAttribute( "id", fieldId );
			whereFieldNode.setAttribute( "method", methodVal );
			
			if( type != null )					
				whereFieldNode.setAttribute( "type", type );
			
			if( dataType != null )
				whereFieldNode.setAttribute( "dataType", dataType );
				
			if( fieldName != null )
				whereFieldNode.setAttribute( "name", fieldName );
				
			var isAddTag = false;
			// 检查哪些选项被选上了
			var value = null;
			for( var ifv = 1; ifv < valueObj.options.length; ifv++ )
			{
				if( !valueObj.options[ifv].selected )
					continue;
	
				if( !isAddTag )
				{
					value = valueObj.options[ifv].value;
					isAddTag = true;
				}
				else
				{
					value += "," + valueObj.options[ifv].value;
				}
			}
			if( value != null )
				setTextContent(whereFieldNode, value);
				
			rootNode.appendChild(whereFieldNode);
		}
		
		var xml = getDocXML(rootDoc);
		var beginIndex = xml.indexOf("<root>");
		var endIndex = xml.indexOf("</root>");
		xml = xml.substring(beginIndex + 6, endIndex);
	
		divObj.setAttribute("xml", xml);
	}
}

function checkTimeError(time){
	//判断 time 是否为中文
	if(checkChinaStr(time) == false)
		return false;
	
	var patrn = "今天|昨天|本周|上周|本月|上月|本季|上季|本年|去年|(过去|未来)[1-9][0-9]*(天|周|月|季|年)";
	if(time.match(patrn))
		return false;
	else 
		return true;
	
}

//判断是否包含中语言
function checkChinaStr(time){ 
	var patrn=/[\u4E00-\u9FA5]|[\uFE30-\uFFA0]/gi;
    if(!patrn.exec(time))
    	return false;
    else
    	return true;
} 


function getChinese_title( fieldName )
{
	if( fieldName == "title" )
		return "标题";
	else if( fieldName == "description" )
		return "描述";
	else if( fieldName == "create_user" )
		return "创建人";
	else if( fieldName == "create_time" )
		return "创建时间";
	else if( fieldName == "assign_user" )
		return "指派人";
	else if( fieldName == "last_modify_time" )
		return "最后修改时间";
	else if( fieldName == "status_id" )
		return "状态";
	else
		return fieldName;
}

function hiddenAdvanceDiv()
{
	//advance_div.style.display = "none";
}
function unitTest_alertNodeArray( array )
{
	if( array == null && __DEBUG__ )
		alert( "null" );
	else if( __DEBUG__ )
	{
		var str = "";
		for( var i = 0; i < array.length; i++ )
			str += getDocXML(array[i]);
			
		alert( str );
	}
}