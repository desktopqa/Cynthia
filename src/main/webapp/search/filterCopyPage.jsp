<%@page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>

<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Filter"%>
<%@page import="com.sogou.qadev.service.cynthia.util.ArrayUtil"%>
<%@page import="org.w3c.dom.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>

<%
	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
	response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
	response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility
	
	out.clear();

	Key key = (Key)session.getAttribute("key");
	Long keyId = (Long)session.getAttribute("kid");

	if(keyId == null || keyId <= 0 || key == null){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}

	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
	
	Filter[] filterArray = (Filter[])ArrayUtil.format(das.queryFilters(key.getUsername()), new Filter[0]);
	
	Filter currentFilter = null;
	int limit = 0;
	
	String filterIdStr = request.getParameter("filterId");
	if(filterIdStr != null)
	{
		UUID currentFilterId = DataAccessFactory.getInstance().createUUID(filterIdStr);
		if(currentFilterId != null)
		{
			currentFilter = das.queryFilter(currentFilterId);
			if(currentFilter != null)
			{
				Document currentFilterDoc = XMLUtil.string2Document(currentFilter.getXml(), "UTF-8");
			
				org.w3c.dom.Node envNode = XMLUtil.getSingleNode(currentFilterDoc, "query/env");
			
				org.w3c.dom.Node topnNode = XMLUtil.getSingleNode(envNode, "topn");
				if(topnNode != null)
					limit = Integer.parseInt(topnNode.getTextContent());
			}
		}
	}
%>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<title></title>
		<link rel="stylesheet" href="/css/tms.css" type="text/css" media="screen">
		<script language="javascript" src="/js/lib/prototype.js"></script>
		<script language="javascript" src="/js/util.js"></script>
		<script language="javascript" src="/js/display.js"></script>
		<script language="javascript" src="/js/order.js"></script>
<!-- 		<script language="javascript" src="../js/selectUtil.js"></script> -->
		<script>
			var templateType = null;
			var templates = null;
			
			function displayCopyType()
			{
				if($("select_copy_filter").selectedIndex > 0)
				{
					$("tr_copy_type").style.display = "";
					
					adjustDisplayOrderArea();
				}
				else
				{
					$("input_copy_where").checked = true;
					$("tr_copy_type").style.display = "none";
					$("tr_display_order_area").style.display = "none";
					
					tr_filter_name.style.display = "none";
					tr_filter_topn.style.display = "none";
					tr_filter_button.style.display = "none";
					tr_filter_button_all.style.display = "none";
				}
			}
			
			function adjustDisplayOrderArea(currentFilterId)
			{
				if($("input_copy_where").checked)
				{
					$("input_filter_name").value = getTextContent($("select_copy_filter").options[$("select_copy_filter").selectedIndex]);
					$("tr_filter_name").style.display = "none";
					
					var param = "";
					if(currentFilterId != null)
						param = "filterId=" + getSafeParam(currentFilterId) + "&operation=modify";
					else
					{
						var filterId = $("select_copy_filter").options[$("select_copy_filter").selectedIndex].value;
						param = "filterId=" + getSafeParam(filterId) + "&operation=create";
					}
					
					var xmlDoc = new Ajax.Request("getDisplayOrderHTML.jsp", {method:"post", parameters:param, onComplete:onCompleteGetDisplayOrderHTML});
				}
				else
				{
					eval("var isSys = " + $("select_copy_filter").options[$("select_copy_filter").selectedIndex].getAttribute("isSys"));
					if(isSys)
					{
						alert("系统筛选器禁止完全复制！");
						$("input_copy_where").checked = true;
						return;
					}
					
					$("input_filter_name").value = "";
					
					$("td_display_order_area").innerHTML = "";
					$("tr_display_order_area").style.display = "none";
					
					tr_filter_name.style.display = "";
					tr_filter_topn.style.display = "none";
					
					$("input_is_public").checked = false;
					$("input_is_focus").checked = false;
					tr_filter_button_all.style.display = "";
				}
			}
			
			function onCompleteGetDisplayOrderHTML(request)
			{				
				var responseXML = request.responseXML;
				
				var rootNode = responseXML.selectSingleNode("root");
				
				var templateTypeNode = rootNode.selectSingleNode("templateType");
				var templateNodes = rootNode.selectNodes("templates/template");
				
				templateType = new Object();
				templateType.id = templateTypeNode.getAttribute("id");
				templateType.name = templateTypeNode.getAttribute("name");
				
				var displayOrderAreaHTML = "";
				displayOrderAreaHTML += "<table width=\"100%\">";
					
				if(templateNodes.length == 0)
				{
					displayOrderAreaHTML += "<tr id=\"tr_" + templateType.id + "_display_title\">";
					displayOrderAreaHTML += "<td class=\"tdNoBottom\">显示设置</td>";
					displayOrderAreaHTML += "</tr>";
							
					displayOrderAreaHTML += "<tr id=\"tr_" + templateType.id + "_display\">";
					displayOrderAreaHTML += "<td class=\"tdNoBottom\">";
					displayOrderAreaHTML += getTextContent(templateTypeNode.selectSingleNode("display"));
					displayOrderAreaHTML += "</td>";
					displayOrderAreaHTML += "</tr>";
					
					displayOrderAreaHTML += "<tr id=\"tr_" + templateType.id + "_order_title\">";
					displayOrderAreaHTML += "<td class=\"tdNoBottom\">排序设置</td>";
					displayOrderAreaHTML += "</tr>";
					
					displayOrderAreaHTML += "<tr id=\"tr_" + templateType.id + "_order\">";
					displayOrderAreaHTML += "<td class=\"tdNoBottom\">";
					displayOrderAreaHTML += getTextContent(templateTypeNode.selectSingleNode("order"));
					displayOrderAreaHTML += "</td>";
					displayOrderAreaHTML += "</tr>";
				}
				else
				{
					templates = new Array();
					
					for(var i = 0; i < templateNodes.length; i++)
					{
						templates[i] = new Object();
						templates[i].id = templateNodes[i].getAttribute("id");
						templates[i].name = templateNodes[i].getAttribute("name");
						templates[i].isClose = false;
						if(templateNodes[i].getAttribute("isClose") != null)
							eval("templates[i].isClose = " + templateNodes[i].getAttribute("isClose"));
							
						displayOrderAreaHTML += "<tr" + (templates[i].isClose ? " style=\"display:none\"" : "") + ">";
						displayOrderAreaHTML += "<td class=\"tdNoBottom\">";
						displayOrderAreaHTML += "<span style=\"cursor:pointer;color:blue\" onclick=\"templateArea('" + templates[i].id + "')\">" + getXMLStr(templates[i].name) + "</span></td>";
						displayOrderAreaHTML += "</tr>";		
						
						displayOrderAreaHTML += "<tr" + (templates[i].isClose ? " style=\"display:none\"" : "") + " id=\"tr_" + templates[i].id + "_display_title\">";
						displayOrderAreaHTML += "<td class=\"tdNoBottom\">显示设置</td>";
						displayOrderAreaHTML += "</tr>";
		
						displayOrderAreaHTML += "<tr" + (templates[i].isClose ? " style=\"display:none\"" : "") + " id=\"tr_" + templates[i].id + "_display\">";
						displayOrderAreaHTML += "<td class=\"tdNoBottom\">";
						displayOrderAreaHTML += getTextContent(templateNodes[i].selectSingleNode("display"));
						displayOrderAreaHTML += "</td>";
						displayOrderAreaHTML += "</tr>";
						
						displayOrderAreaHTML += "<tr" + (templates[i].isClose ? " style=\"display:none\"" : "") + " id=\"tr_" + templates[i].id + "_order_title\">";
						displayOrderAreaHTML += "<td class=\"tdNoBottom\">排序设置</td>";
						displayOrderAreaHTML += "</tr>";
						
						displayOrderAreaHTML += "<tr" + (templates[i].isClose ? " style=\"display:none\"" : "") + " id=\"tr_" + templates[i].id + "_order\">";
						displayOrderAreaHTML += "<td class=\"tdNoBottom\">";
						displayOrderAreaHTML += getTextContent(templateNodes[i].selectSingleNode("order"));
						displayOrderAreaHTML += "</td>";
						displayOrderAreaHTML += "</tr>";
					}
				}
				
				displayOrderAreaHTML += "</table>";
					
				$("td_display_order_area").innerHTML = displayOrderAreaHTML;
				$("tr_display_order_area").style.display = "";
				
				tr_filter_topn.style.display = "";
				
				$("input_is_public").checked = false;
				$("input_is_focus").checked = false;
				tr_filter_button_all.style.display = "none";
			}
			
			function templateArea(prefix)
			{
				if($("tr_" + prefix + "_display").style.display == "")
				{
					$("tr_" + prefix + "_display_title").style.display = "none";
					$("tr_" + prefix + "_display").style.display = "none";
					$("tr_" + prefix + "_order_title").style.display = "none";
					$("tr_" + prefix + "_order").style.display = "none";
				}
				else
				{
					$("tr_" + prefix + "_display_title").style.display = "";
					$("tr_" + prefix + "_display").style.display = "";
					$("tr_" + prefix + "_order_title").style.display = "";
					$("tr_" + prefix + "_order").style.display = "";
				}
			}
			
			function executeSubmit()
			{
				if($("select_copy_filter").selectedIndex == 0)
				{
					alert("请选择要复制的筛选器！");
					return;
				}
				
				if($("input_copy_all").checked && $("select_copy_filter").options[$("select_copy_filter").selectedIndex].value < 164)
				{
					alert("系统筛选器禁止完全复制！");
					return;
				}
				
				if($("input_copy_all").checked && trim($("input_filter_name").value) == "")
				{
					alert("请输入筛选器名称！");
					return;
				}
				
				if($("input_copy_where").checked)
				{
					var rootDoc = getXMLDoc();
					
					var queryNode = rootDoc.createElement("query");
					
					//deal with relate
					var relateNode = rootDoc.createElement("relate");
					setTextContent(relateNode, $("select_copy_filter").options[$("select_copy_filter").selectedIndex].value);
					queryNode.appendChild(relateNode);
					
					//deal with env
					var envNode = rootDoc.createElement("env");
					
					if(trim($("input_top_n").value) != "")
					{
						var topnNode = rootDoc.createElement("topn");
						setTextContent(topnNode, trim($("input_top_n").value));
						envNode.appendChild(topnNode);
					}
					
					queryNode.appendChild(envNode);
					
					//deal with template type
					var templateTypeNode = rootDoc.createElement("templateType");	
					templateTypeNode.setAttribute("id", templateType.id);
					templateTypeNode.setAttribute("name", templateType.name);
					
					if(templates == null)
					{
						//template type
						templateTypeNode.appendChild(rootDoc.createElement("where"));
						
						var displayNode = getDisplayResultXML("display_" + templateType.id);
						if(displayNode == null)
							templateTypeNode.appendChild(rootDoc.createElement("display"));
						else
							templateTypeNode.appendChild(displayNode);
							
						var orderNode = getOrderResultXML("order_" + templateType.id);
						if(orderNode == null)
							templateTypeNode.appendChild(rootDoc.createElement("order"));
						else
							templateTypeNode.appendChild(orderNode);
							
						queryNode.appendChild(templateTypeNode);
					}
					else
					{
						//templates
						queryNode.appendChild(templateTypeNode);
						
						for(var i = 0; i < templates.length; i++)
						{
							var templateNode = rootDoc.createElement("template");
							templateNode.setAttribute("id", templates[i].id);
							templateNode.setAttribute("name", templates[i].name);
							templateNode.setAttribute("isClose", templates[i].isClose ? "true" : "false");
							
							templateNode.appendChild(rootDoc.createElement("where"));
							
							var displayNode = getDisplayResultXML("display_" + templates[i].id);
							if(displayNode == null)
								templateNode.appendChild(rootDoc.createElement("display"));
							else
								templateNode.appendChild(displayNode);
								
							var orderNode = getOrderResultXML("order_" + templates[i].id);
							if(orderNode == null)
								templateNode.appendChild(rootDoc.createElement("order"));
							else
								templateNode.appendChild(orderNode);
								
							queryNode.appendChild(templateNode);
						}
					}
					
					rootDoc.appendChild(queryNode);
					
					$("input_filter_xml").value = getDocXML(rootDoc);
					
					$("input_father_id").value = $("select_copy_filter").options[$("select_copy_filter").selectedIndex].value;
					
					$("form_filter").submit();
				}
				else
				{
					var param = "filterId=" + getSafeParam($("select_copy_filter").options[$("select_copy_filter").selectedIndex].value);
					param += "&filterName=" + getSafeParam(trim($("input_filter_name").value));
					
					if($("input_is_public").checked)
						param += "&isPublic=true";
					else
						param += "&isPublic=false";
					
					if($("input_is_focus").checked)
						param += "&isFocus=true";
					else
						param += "&isFocus=false";	
						
					var xmlDoc = new Ajax.Request("copyFilter.jsp", {method:"post", parameters:param, onComplete:onCompleteCopyFilter});
				}
			}
			
			function onCompleteCopyFilter(request)
			{
				var responseXML = request.responseXML;
				
				var isErrorNode = responseXML.selectSingleNode("root/isError");
				eval("var isError = " + getTextContent(isErrorNode));
				
				if(isError)
					alert("复制失败！");
				else
				{
					alert("复制成功！");
					window.location = "filterManagement.jsp?refresh=true";
				}
			}
		</script>
	</head>
	<body<%=currentFilter != null ? " onload=\"adjustDisplayOrderArea('" + currentFilter.getId() + "')\"" : ""%> topMargin="0">
		<form id="form_filter" action="createSearch_save.jsp" method="post">
			<input id="input_filter_xml" name="searchConfig" type="hidden"/>
			<input id="input_id" type="hidden" name="id" value="<%=currentFilter != null ? currentFilter.getId() : "" %>">
			<input id="input_father_id" type="hidden" name="fatherId"/>
			<table width="100%" cellspacing="0">
				<tr>
					<td align="right" class="tdNobottom">
						<input id="input_submit" type="button" value="提交" onClick="executeSubmit()"/>
					</td>
				</tr>
			</table>
			<table width="100%" cellspacing="0">
				<tr>
					<td class="tdNoBottom">筛选器：
						<select id="select_copy_filter" onchange="displayCopyType()" <%=currentFilter != null ? "disabled" : ""%>>
							<option value="">请选择</option>
<%
	for(Filter filter : filterArray)
	{
		if(currentFilter != null)
		{
			if(filter.getId().equals(currentFilter.getId()))
			{
				out.print("<option value=\"" + (filter.getFatherId() != null ? filter.getFatherId() : filter.getId()) + "\"");
				if(filter.getCreateUser().equals("admin@sohu-rd.com"))
					out.print(" isSys=\"true\"");
				else
					out.print(" isSys=\"false\"");
				out.print(" selected>");
				out.println(XMLUtil.toSafeXMLString(filter.getName()) + "</option>");
				break;
			}
		}
		else
		{
			if(filter.getFatherId() != null)
				continue;
		
			boolean isRelated = false;
			for(Filter _filter : filterArray)
			{
				if(_filter.getFatherId() != null && _filter.getFatherId().equals(filter.getId())
						&& _filter.getCreateUser().equals(key.getUsername()))
				{
					isRelated = true;
					break;
				}
			}
		
			if(isRelated)
				continue;
		
			out.print("<option value=\"" + filter.getId() + "\"");
			if(filter.getCreateUser().equals("admin@sohu-rd.com"))
				out.print(" isSys=\"true\"");
			else
				out.print(" isSys=\"false\"");
			out.print(">");
			out.println(XMLUtil.toSafeXMLString(filter.getName()) + "</option>");
		}
	}
%>
						</select>
					</td>
				</tr>
				<tr id="tr_copy_type" style="display:<%=currentFilter != null ? "" : "none" %>">
					<td class="tdNoBottom">
						<input id="input_copy_where" name="copyType" type="radio" onclick="adjustDisplayOrderArea()" checked <%=currentFilter != null ? "disabled" : ""%>/>依赖复制
						<input id="input_copy_all" name="copyType" type="radio" onclick="adjustDisplayOrderArea()" <%=currentFilter != null ? "disabled" : ""%>/>完全复制
					</td>
			   	</tr>
			   	<tr id="tr_display_order_area" style="display:none">
					<td id="td_display_order_area" class="tdNoBottom">
					</td>
				</tr>
				<tr id="tr_filter_name" style="display:none">
					<td class="tdNobottom">
						筛选器名称：<input id="input_filter_name" name="filterName" type="text" size="30" value="<%=currentFilter != null ? XMLUtil.toSafeXMLString(currentFilter.getName()) : "" %>"/>
					</td>
				</tr>
				<tr id="tr_filter_topn" style="display:none">
					<td class="tdNobottom">
						筛选前&nbsp;<input id="input_top_n" type="text" size="10" value="<%=limit > 0 ? limit : "" %>" onKeyDown="return checkIsAllNum(event.keyCode);"/>&nbsp;条
					</td>
				</tr>
				<tr id="tr_filter_button_all" style="display:none">
					<td class="tdNobottom">
						<input id="input_is_public" name="isPublic" type="checkbox"/>公用&nbsp;&nbsp;&nbsp;
						<input id="input_is_focus" name="isFocus" type="checkbox" checked/>关注&nbsp;&nbsp;&nbsp;
					</td>
				</tr>
			</table>
			<table width="100%" cellspacing="0">
				<tr>
					<td align="right" class="tdNobottom">
						<input id="input_submit" type="button" value="提交" onClick="executeSubmit()"/>
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>
