
<%
	response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
%>

<%@page import="java.net.*"%>
<%@page import="java.util.*"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Attachment"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.UUID"%>
<%@page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%
	//parse request
String param = request.getParameter("param");
String idStr = param.split("_")[1];
String type = param.split("_")[0];
UUID id = DataAccessFactory.getInstance().createUUID(idStr);


DataAccessSession das = null;
if(session == null || session.getAttribute("kid") == null || session.getAttribute("key") == null)
{
	 das = DataAccessFactory.getInstance().createDataAccessSession(ConfigUtil.sysEmail, ConfigUtil.magic);
}
else
{
	long keyId = (Long)session.getAttribute("kid");
	Key key = (Key)session.getAttribute("key");
	das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);
}

Attachment attachment = das.queryAttachment(id, true);
if ( attachment == null )
{
	response.setContentType("text/html; charset=utf-8");
%>
	ERROR:<br/>
	Sorry, can't get attachment file info<br/>
	Meybe you didn't pass the passport of qadev.rd.sogou or can't find attachment.
	<%
	return;
}
Map<String,String> mimeMap = new HashMap<String,String>();
 mimeMap.put("jpeg", "image/jpeg");
 mimeMap.put("jpg", "image/jpeg");
 mimeMap.put("jfif", "image/jpeg");
 mimeMap.put("jfif-tbnl", "image/jpeg");
 mimeMap.put("jpe", "image/jpeg");
 mimeMap.put("jfif", "image/jpeg");
 mimeMap.put("tiff", "image/tiff");
 mimeMap.put("tif", "image/tiff");
 mimeMap.put("gif", "image/gif");
 mimeMap.put("xls", "application/x-msexcel");
 mimeMap.put("doc", "application/msword");
 mimeMap.put("ppt", "application/x-mspowerpoint");
 mimeMap.put("zip", "application/x-zip-compressed");
 mimeMap.put("pdf", "application/pdf");
 mimeMap.put("apk", "application/vnd.android.package-archive");

if ( "image".equalsIgnoreCase(type) )
{
	String fileName = attachment.getName();
	String mimeType = fileName.substring(fileName.lastIndexOf(".")+1);

	response.setHeader( "Content-Disposition", "attachment;filename=" + new String( attachment.getName().getBytes("gb2312"), "ISO8859-1" ) );
	response.setContentType(mimeMap.get(mimeType.toLowerCase()));
	response.setContentLength((int)attachment.getSize());

	try
	{
		out.clear();
		response.getOutputStream().write(attachment.getData());
		out.flush();
	}catch(Exception e)
	{
		e.printStackTrace();
	}
}else
{
	response.setHeader( "Content-Disposition", "attachment;filename=" + new String( attachment.getName().getBytes("gb2312"), "ISO8859-1" ) );
	response.setContentType("application/x-attachment");
	try
	{
		out.clear();
		response.getOutputStream().write(attachment.getData());
		out.flush();
	}catch(Exception e)
	{
		e.printStackTrace();
	}

}
%>