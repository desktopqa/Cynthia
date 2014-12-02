<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<!-- <link href="css/center.css" style="text/css" rel="stylesheet">  -->
	<link href="../lib/bootstrap2/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="../css/top.css" rel="stylesheet" type="text/css">
	<link href="../css/user.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="../lib/jquery/jquery-1.9.3.min.js"></script>
	<script type="text/javascript" src="../js/util.js"></script>
	<script type="text/javascript" src="../js/user/userinfo.js"></script>
	<script type="text/javascript" src="../js/md5.js"></script>
	<title>脚本</title>
</head>
<body>
	<div class="container-fluid" style="margin-top:150px;">
		<div class="row-fluid">
			<div class="span2">
			</div>
			<div class="span6">
				<img alt="140x140" src="../images/team.jpg" class="img-rounded" />
			</div>
			<div class="span4">
			
			<div id="logo_header">
				<img alt="" src="../images/logo.png" />
				<b style="font-size:15px; padding: 10px 10px 0px;margin-left:0px;">专心、专注、专业</b>
			</div>
			
			<div class="control-group">
				    <div class="controls">
				      <input type="text" id="userMail" placeholder="邮箱"> &nbsp;&nbsp;<span id="mailInfo" style="color:red"><span>
				    </div>
			</div>
			
			<div class="control-group">
				    <div class="controls">
				      <input type="password" id="userPassword" placeholder="密码">
				    </div>
			</div>
				  
			<div class="control-group">
				    <div class="controls">
				      <input type="password" id="userPasswordAgain" placeholder="重复密码">
				    </div>
			</div>
				  
			<div class="control-group">
				    <div class="controls">
				      <input type="text" id="userAlias" placeholder="中文名">
				    </div>
			</div>
			<p id="register_info" style="color:red"><p>
			<div class="control-group">
				    <div class="controls">
				      <div id="login">
					      <button type="button" id="register" class="btn btn-danger">注册</button>
					      <button type="button" id="login_btn" style="margin-left:50px;" class="btn">登录</button>
				      </div>
				    </div>
			</div>
			</div>
		</div>
	</div>
</body>
</html>