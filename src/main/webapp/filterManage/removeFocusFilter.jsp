<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="org.w3c.dom.Node"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>

<%@ include file="initMain.function.jsp"%>

<%
	out.clear();
	
	Key key = (Key)session.getAttribute("key");
	Long keyId = (Long)session.getAttribute("kid");
	
	if(keyId == null || keyId <= 0 || key == null){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
	
	String templateIdStr = request.getParameter("templateId");
	String fieldIdStr = request.getParameter("fieldId");
	if(templateIdStr==null||fieldIdStr==null)
	{
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	xmlb.append("<root>");
	xmlb.append("<isError>false</isError>");
	xmlb.append("</root>");

	Document doc = XMLUtil.string2Document(xmlb.toString(), "UTF-8");
	
	Node rootNode = XMLUtil.getSingleNode(doc, "root");
	
	Node fieldNode = doc.createElement("field");
	rootNode.appendChild(fieldNode);
		
	UUID templateId = DataAccessFactory.getInstance().createUUID(templateIdStr);
	String content = queryFieldHTML(das, templateId,fieldIdStr, null, null, null, 0);

	if(content != null)
		fieldNode.setTextContent(content);
		
	out.clear();
	out.println(XMLUtil.document2String(doc, "UTF-8"));
%>