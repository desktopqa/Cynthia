<%@page import="com.sogou.qadev.service.cynthia.util.CynthiaUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link href="../lib/bootstrap2/css/bootstrap.min.css" rel="stylesheet" type="text/css">
	<link href="../css/top.css" rel="stylesheet" type="text/css">
	<link href="../css/user.css" rel="stylesheet" type="text/css">
	<script type="text/javascript" src="../lib/jquery/jquery-1.9.3.min.js"></script>
	<script type="text/javascript" src="../js/util.js"></script>
	<script type="text/javascript" src="../js/user/userinfo.js"></script>
	<script type="text/javascript" src="../js/md5.js"></script>
	<title>Cynthia-登陆</title>
</head>

<%
	String targetUrl = request.getParameter("targetUrl");
	targetUrl = CynthiaUtil.isNull(targetUrl) ? "" : targetUrl;
	String loginErrorInfo = (String)request.getSession().getAttribute("loginErrorInfo");
	loginErrorInfo = loginErrorInfo == null ? "" : loginErrorInfo;
%>

<body onload="initUser('<%=targetUrl%>','<%=loginErrorInfo %>');">
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
				      <input type="text" id="user_mail_login" placeholder="邮箱">
				    </div>
				  </div>
				  <div class="control-group">
				    <div class="controls">
				      <input type="password" id="user_password_login" placeholder="密码">
				    </div>
				  </div>
				  <label class="checkbox">
			        <input type="checkbox" id="remerber_password"> 记住登录状态
			      </label>
				  <p id="register_info" style="color:red"><p>
				  <div class="control-group">
					   <div class="controls">
					      <div id="login">
						      <button id="login_in_btn" class="btn btn-danger">登录</button>
						      <a href="register.jsp" style="margin-left:50px;">注册</a> 
				      	  </div>
				       </div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>