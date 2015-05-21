<%@page import="com.sogou.qadev.service.cynthia.util.XMLUtil"%>
<%@ page contentType="text/xml; charset=UTF-8" %>

<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataManager"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Data"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Template"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Flow"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Stat"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Action"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Role"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Attachment"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Field.Type"%>
<%@ page import="org.w3c.dom.Node"%>
<%@ page import="org.w3c.dom.Document"%>
<%@ page import="java.util.*"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Option.Forbidden"%>

<%!

static DataAccessFactory daf = DataAccessFactory.getInstance();

String checkXML(String xml, String encode) throws Exception{
	byte[] byteArray = xml.getBytes(encode);
	for(int i = 0; i < byteArray.length; i++){
		if(byteArray[i] == 0x1a || byteArray[i] == 0x19||byteArray[i] == 0x1||byteArray[i]==0x4){
			byteArray[i] = 78;
		}
	}
	return new String(byteArray, encode);
}
%>

<%
	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
	response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
	response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility
	
	out.clear();

	Long keyId = (Long)session.getAttribute("kid");
	Key key = (Key)session.getAttribute("key");

	if(keyId == null || keyId <= 0 || key == null){
		response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
		return;
	}
	
	DataAccessSession das = daf.createDataAccessSession(key.getUsername(), keyId);
	
	String xml = null;
		
	System.out.println(key.getUsername());
	
	Template[] templateArray  = DataManager.getInstance().queryUserTemplates(key.getUsername());
		
	StringBuffer xmlb = new StringBuffer(64);
	xmlb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		
	xmlb.append("<root>");
		
	xmlb.append("<isError>false</isError>");
		
	if(templateArray.length == 0){
		xmlb.append("<templates/>");
	}else{
		xmlb.append("<templates>");
	
		for(Template template : templateArray){
			xmlb.append("<template>");	
				
			xmlb.append("<id>").append(template.getId()).append("</id>");
			xmlb.append("<name>").append(XMLUtil.toSafeXMLString(template.getName())).append("</name>");
			xmlb.append("<templateType>").append(XMLUtil.toSafeXMLString(template.getTemplateTypeId().getValue())).append("</templateType>");	
			xmlb.append("</template>");
		}
		
		xmlb.append("</templates>");
	}
	
	xmlb.append("</root>");
	xml = xmlb.toString();
	out.println(xml);
%>
