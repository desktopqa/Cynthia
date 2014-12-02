<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.apache.commons.fileupload.FileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.FileItem" %>
<%@ page import="org.apache.commons.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.commons.fileupload.servlet.ServletFileUpload" %>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Attachment"%>
<%@ page import="com.sogou.qadev.service.cynthia.bean.Key"%>
<%@ page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@ page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@ page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>

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
	
	//定义允许上传的文件扩展名
	HashMap<String, String> extMap = new HashMap<String, String>();
	extMap.put("image", "gif,jpg,jpeg,png,bmp");
	extMap.put("flash", "swf,flv");
	extMap.put("media", "swf,flv,mp3,wav,wma,wmv,mid,avi,mpg,asf,rm,rmvb");
	extMap.put("file", "doc,docx,xls,xlsx,ppt,htm,html,txt,zip,rar,gz,bz2");
	
	//最大文件大小
	long maxSize = 1000000;
	
	response.setContentType("text/html; charset=UTF-8");
	
	if(!ServletFileUpload.isMultipartContent(request)){
		out.println(getError("请选择文件。"));
		return;
	}
	String dirName = request.getParameter("dir");
	if (dirName == null) {
	// 	dirName = "image";
	}
	if(!extMap.containsKey(dirName)){
		out.println(getError("目录名不正确。"));
		return;
	}
	//创建文件夹
	FileItemFactory factory = new DiskFileItemFactory();
	ServletFileUpload upload = new ServletFileUpload(factory);
	upload.setHeaderEncoding("UTF-8");
	List items = upload.parseRequest(request);
	Iterator itr = items.iterator();
	while (itr.hasNext()) {
		FileItem item = (FileItem) itr.next();
		String fileName = item.getName();
		if(fileName == null)
		{
			continue;
		}
		fileName = fileName.substring(fileName.lastIndexOf("\\")+1);
		long fileSize = item.getSize();
		if (!item.isFormField()) {
			//检查扩展名
			String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			if(!Arrays.<String>asList(extMap.get(dirName).split(",")).contains(fileExt)){
				out.println(getError("上传文件扩展名是不允许的扩展名。\n只允许" + extMap.get(dirName) + "格式。"));
				return;
			}
			
			Attachment attachment = null;
	
			try{
				File file = File.createTempFile("attachment_", ".attachment");
				File uploadedFile = new File(file.getParent(), file.getName());
				item.write(uploadedFile);
				byte[] bytes = new byte[(int)file.length()];
				FileInputStream fis = new FileInputStream(file);
				fis.read(bytes);
			    attachment = das.createAttachment(fileName, bytes);
				fis.close();
				file.delete();
			}catch(Exception e){
				out.println(getError("上传文件失败。"));
				return;
			}
			JSONObject obj = new JSONObject();
			obj.put("error", 0);
			if("image".equals(dirName))
			{
				obj.put("url", ConfigUtil.getCynthiaWebRoot() + "attachment/download_json.jsp?param=image_"+attachment.getId());
			}else
			{
				obj.put("url", ConfigUtil.getCynthiaWebRoot() + "attachment/download_json.jsp?param=file_"+attachment.getId());
			}
			obj.put("title",fileName);
			out.println(obj.toJSONString());
		}
	}
%>


