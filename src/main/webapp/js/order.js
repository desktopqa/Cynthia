//type: 1(ascAdd), 2(descAdd), 3(remove)
function orderOperation(sourceId, targetId, type)
{
	var source = document.getElementById(sourceId);
	var target = document.getElementById(targetId);
	
	for(var i = 0; i < source.options.length; i++)
	{
		if(source.options[i].selected)
		{
			if(type == 1 || type == 2)
			{
				source.options[i].setAttribute("desc", (type == 1 ? "false" : "true" ));
				setTextContent(source.options[i], getTextContent(source.options[i]) + "(" + (type == 1 ? "升序" : "降序") + ")");
			}
			else
				setTextContent(source.options[i], getTextContent(source.options[i]).split("(")[0]);
		}
	}
	
	moveOptions(sourceId, targetId);
}

function orderMove(objId, isUp)
{
	var obj = document.getElementById(objId);
	newAdjustPRI(obj, isUp);
}

function getOrderResultXML( prefix )
{
	var orderDivObj = document.getElementById( prefix + "_order" );
	
	if( orderDivObj == null || orderDivObj.tagName != "DIV" )
		return;
		
	var orderSelected = document.getElementById( prefix + "_selectedOrderSelect" );
	
	var xmlDoc = getXMLDoc();
	
	var orderNode = xmlDoc.createElement( "order" );
	var groupNum = document.getElementById( prefix + "_group" ).value;
	orderNode.setAttribute( "indent", groupNum );
	
	for( var ino = 0; ino < orderSelected.options.length; ino++ )
	{
		var itemNode = xmlDoc.createElement( "field" );
		itemNode.setAttribute( "id", orderSelected.options[ino].value );
		
		if(orderSelected.options[ino].value == "title")
			itemNode.setAttribute( "name", "标题");
		else if(orderSelected.options[ino].value == "node_id")
			itemNode.setAttribute( "name", "项目");
		else if(orderSelected.options[ino].value == "description")
			itemNode.setAttribute( "name", "描述");
		else if(orderSelected.options[ino].value == "create_user")
			itemNode.setAttribute( "name", "创建人");
		else if(orderSelected.options[ino].value == "assign_user")
			itemNode.setAttribute( "name", "指派人");
		else if(orderSelected.options[ino].value == "create_time")
			itemNode.setAttribute( "name", "创建时间");
		else if(orderSelected.options[ino].value == "last_modify_time")
			itemNode.setAttribute( "name", "修改时间");
		else if(orderSelected.options[ino].value == "status_id")
			itemNode.setAttribute( "name", "状态");
		else
			itemNode.setAttribute( "name", getTextContent(orderSelected.options[ino]).split("(")[0] );
		
		itemNode.setAttribute( "desc", orderSelected.options[ino].getAttribute("desc") );
		
		var type = orderSelected.options[ino].getAttribute("type");
		if(type != null)
			itemNode.setAttribute("type", type);
		
		var dataType = orderSelected.options[ino].getAttribute("dataType");
		if(dataType != null)
			itemNode.setAttribute("dataType", dataType);
		
		orderNode.appendChild( itemNode );
	}
	
	return orderNode;
}