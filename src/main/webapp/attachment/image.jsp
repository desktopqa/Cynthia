<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>My JSP 'image.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
	<script type="text/javascript" src='../lib/jquery/jquery-1.9.3.min.js'></script>
  <script type="text/javascript" src='../js/util.js'></script>
  
  <script type="text/javascript">
  	function displayImage(fileId)
  	{
  		document.getElementById("image").src = "./download.jsp?method=download&id="+fileId;
  	}
  </script>
  <body onload="displayImage(<%=request.getParameter("fileId") %>)">
   	<div style="width: 100%;height:100%;">
   		<img id='image' alt="" src="" />
   	</div>
  </body>
</html>
