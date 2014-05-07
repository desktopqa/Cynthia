<%@page import="com.alibaba.fastjson.JSONArray"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.sogou.qadev.service.cynthia.bean.Attachment"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.File"%>
<%@page import="xiaoxiang.fileUpload.files"%>
<%@page import="com.sogou.qadev.service.cynthia.factory.DataAccessFactory"%>
<%@page import="com.sogou.qadev.service.cynthia.service.DataAccessSession"%>
<%@page import="com.sogou.qadev.service.cynthia.util.ConfigUtil"%>
<%@page import="com.sogou.qadev.service.login.bean.Key"%>
<%@ page contentType="text/html; charset=UTF-8"%>

<!-- 初始化一个upBean-->
<jsp:useBean id="myUpload" scope="page" class="xiaoxiang.fileUpload.upBean" />

<%
response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility
request.setCharacterEncoding("UTF-8");
out.clear();

Key key = (Key)session.getAttribute("key");
Long keyId = (Long)session.getAttribute("kid");

if(keyId == null || keyId <= 0 || key == null){
	response.sendRedirect(ConfigUtil.getCynthiaWebRoot());
	return;
}
DataAccessSession das = DataAccessFactory.getInstance().createDataAccessSession(key.getUsername(), keyId);

try
{
	//初始化工作
	myUpload.initialize(pageContext,"UTF-8");
	//设定允许的文件后缀名
	// myUpload.setAllowedExtList("zip");
	//设定是否允许覆盖服务器上的同名文件
	myUpload.setIsCover(false);

	//设定允许上传文件的总大小
// 	myUpload.setTotalMaxFileSize( ConfigUtil.maxUploadFileAccount * ConfigUtil.maxUploadFileSize );

	//设定单个文件大小的限制
// 	myUpload.setMaxFileSize(ConfigUtil.maxUploadFileSize );

	myUpload.setIsCover(true);

	//设定上传的物理路径
	// myUpload.setRealPath( Config.customizeDefaultIndexDir );
	try{
		//将所有数据导入组件的数据结构中
		myUpload.upload();
	}catch(Exception e){
		throw e;
	}

	//得到所有上传的文件
	files myFiles = myUpload.getFiles();
	long[] fileId = new long[ myFiles.getCount()];

	Map<String,String> fileIdNameMap = new HashMap<String,String>();
			
	String	alertInfo	= "";
	//将文件保存到服务器
	for(int i=0;i<myFiles.getCount();i++)
	{
		String fileName = myFiles.getFile(i).getName();

		File file = File.createTempFile("attachment_", ".attachment");

		myUpload.setRealPath(file.getParent());

		myFiles.getFile(i).setName( file.getName() );
		myFiles.getFile(i).saveAs();

		byte[] bytes = new byte[(int)file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(bytes);

		Attachment attachment = das.createAttachment(fileName, bytes);

		fileIdNameMap.put(attachment.getId().getValue(),attachment.getName());

		fis.close();
		file.delete();
	}
	
	out.println(JSONArray.toJSONString(fileIdNameMap));
	
}
catch( Exception e )
{
	
}
%>
