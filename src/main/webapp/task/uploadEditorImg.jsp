<%@page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Attachment"%>
<%@page import="sun.misc.BASE64Decoder"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import=" com.alibaba.fastjson.JSON"%>
<%!
	String getUploadUrl(String imageStr){
		if (imageStr == null) {
			return "";
		}
		byte[] imgBytes = null;

		try {
			BASE64Decoder decoder = new BASE64Decoder();
			imgBytes = decoder.decodeBuffer(imageStr);
			if(imgBytes != null)
			{
				for (int i = 0; i < imgBytes.length; ++i) {
		            if (imgBytes[i] < 0) {// 调整异常数据
		            	imgBytes[i] += 256;
		            }
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
// 		String uploadUrl = "http://f.venus.sogou-inc.com/download?fileId=";
// 		uploadUrl += FileUpDownLoadHandler.postFile("editorimg.jpg", imgBytes);

		Attachment attachment = DataAccessFactory.getInstance().getSysDas().createAttachment("editorImg.jpg", imgBytes);
		String uploadUrl = ConfigUtil.getCynthiaWebRoot() + "attachment/download.jsp?method=download&id=" + attachment.getId().getValue();
		return uploadUrl;
	}

%>

<%
	response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
	response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
	response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
	response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility

	String imgData = request.getParameter("imgData");
	Map result = new HashMap();
	
	if(imgData == null){
		result.put("success",false);
	}else
	{
		String returnUrl = getUploadUrl(imgData);
		result.put("success",true);
		result.put("url",returnUrl);
	}
	out.println(JSON.toJSONString(result));
%>