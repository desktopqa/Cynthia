function executeControl(prefix_objId, isUp)
{
	var prefix_obj = document.getElementById(prefix_objId);
	
	var selectedIndex = 0;
	for(var i = 0; i < prefix_obj.options.length; i++)
	{
		if(prefix_obj.options[i].selected)
		{
			selectedIndex = i;
			break;
		}
	}
	
	if(isUp && selectedIndex > 1 || !isUp && selectedIndex > 0)
	{
		newAdjustPRI(prefix_obj, isUp);
	}
}

var selectedRootDoc = null;
function previewDisplay(prefix_objId)
{
	var prefix_obj = document.getElementById(prefix_objId);
	
	selectedRootDoc = getXMLDoc();
	
	var tableNode = selectedRootDoc.createElement("table");
	tableNode.setAttribute("width", "100%");
	
	var trTitleNode = selectedRootDoc.createElement("tr");
	
	var trValueNode = selectedRootDoc.createElement("tr");
	
	for(var i = 0; i < prefix_obj.options.length; i++)
	{
		var tdTitleNode = selectedRootDoc.createElement("td");
		tdTitleNode.setAttribute("align", "center");
		tdTitleNode.setAttribute("class", "tdNoBottom");
		tdTitleNode.setAttribute("bgcolor", "#EEFFFF");
		tdTitleNode.setAttribute("nowrap", "true");
		
		var fontNode = selectedRootDoc.createElement("font");
		fontNode.setAttribute("color", "red");
		setTextContent(fontNode, getTextContent(prefix_obj.options[i]));
		tdTitleNode.appendChild(fontNode);
		
		trTitleNode.appendChild(tdTitleNode);
		
		var tdValueNode = selectedRootDoc.createElement("td");
		tdValueNode.setAttribute("align", "center");
		tdValueNode.setAttribute("class", "tdNoBottom");
		tdValueNode.setAttribute("nowrap", "true");
		tdValueNode.text = "-";
		
		trValueNode.appendChild(tdValueNode);
	}
	
	tableNode.appendChild(trTitleNode);
	tableNode.appendChild(trValueNode);
	
	selectedRootDoc.appendChild(tableNode);
	
	window.open("previewDisplay.jsp", null, "modal=yes,scrollbars=yes,top=300,height=150,left=200,width=600");
}

function checkHold(prefix_objId)
{
	var prefix_obj = document.getElementById(prefix_objId);
	
	for(var i = 0; i < prefix_obj.options.length; i++)
	{
		if(prefix_obj.options[i].selected && prefix_obj.options[i].value == "title")
		{
			prefix_obj.options[i].selected = false;
		}
	}
}

// 返回显示的XML段
function getDisplayResultXML( prefix )
{
	var disObj = document.getElementById( prefix + "_display" );
	if( disObj == null || disObj.tagName != "DIV" )
		return;
		
	var prefix = disObj.id.substring( 0, disObj.id.lastIndexOf( "_" ) );
	
	var displaySelected = document.getElementById( prefix + "_selectedDisplaySelect" );
	
	var xmlDoc = getXMLDoc();
	
	var displayNode = xmlDoc.createElement( "display" );
	
	for( var ino = 0; ino < displaySelected.options.length; ino++ )
	{
		var itemNode = xmlDoc.createElement( "field" );
		itemNode.setAttribute( "id", displaySelected.options[ino].value );
		
		if(displaySelected.options[ino].value == "title")
			itemNode.setAttribute( "name", "标题");
		else if(displaySelected.options[ino].value == "node_id")
			itemNode.setAttribute( "name", "项目");
		else if(displaySelected.options[ino].value == "description")
			itemNode.setAttribute( "name", "描述");
		else if(displaySelected.options[ino].value == "status_id")
			itemNode.setAttribute( "name", "状态");
		else if(displaySelected.options[ino].value == "create_user")
			itemNode.setAttribute( "name", "创建人");
		else if(displaySelected.options[ino].value == "create_time")
			itemNode.setAttribute( "name", "创建时间");
		else if(displaySelected.options[ino].value == "assign_user")
			itemNode.setAttribute( "name", "指派人");
		else if(displaySelected.options[ino].value == "last_modify_time")
			itemNode.setAttribute( "name", "修改时间");
		else
			itemNode.setAttribute( "name", getTextContent(displaySelected.options[ino]));
		
		var type = displaySelected.options[ino].getAttribute("type");
		if(type != null)
			itemNode.setAttribute("type", type);
		
		var dataType = displaySelected.options[ino].getAttribute("dataType");
		if(dataType != null)
			itemNode.setAttribute("dataType", dataType);
		
		displayNode.appendChild( itemNode );
	}
	
	return displayNode;
}
