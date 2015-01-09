<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@page import="javax.swing.filechooser.FileNameExtensionFilter"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@ page import="com.alibaba.fastjson.JSONArray"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Attachment"%>
<%@ page import="java.io.FileInputStream"%>
<%@ page import="java.io.File"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="org.apache.commons.fileupload.*"%>
<%@ page import="org.apache.commons.fileupload.servlet.*"%>
<%@ page import="org.apache.commons.fileupload.disk.*"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<%!
	private String getError(String message) {
		JSONObject obj = new JSONObject();
		obj.put("error", 1);
		obj.put("message", message);
		return obj.toJSONString();
	}
%>

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
	
	response.setContentType("text/html; charset=UTF-8");
	
	if(!ServletFileUpload.isMultipartContent(request)){
		out.println(getError("请选择文件。"));
		return;
	}
	
	//创建文件夹
	FileItemFactory factory = new DiskFileItemFactory();
	ServletFileUpload upload = new ServletFileUpload(factory);
	upload.setHeaderEncoding("UTF-8");
	List items = upload.parseRequest(request);
	Iterator itr = items.iterator();
	Map<String,String> fileIdNameMap = new HashMap<String,String>();
	while (itr.hasNext()) {
		FileItem item = (FileItem) itr.next();
		String fileName = item.getName();
		if(fileName == null)
			continue;
		fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
		long fileSize = item.getSize();
		File file = File.createTempFile("attachment_", ".attachment");
		File uploadedFile = new File(file.getParent(), file.getName());
		item.write(uploadedFile);
		byte[] bytes = new byte[(int)file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(bytes);
	    Attachment attachment = das.createAttachment(fileName, bytes);
		fileIdNameMap.put(attachment.getId().getValue(),attachment.getName());
		fis.close();
		file.delete();
		uploadedFile.delete();
	}

	out.println(JSONArray.toJSONString(fileIdNameMap));
	
%>
