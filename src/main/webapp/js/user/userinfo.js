

/********用户注册开始****/
function register(){
	var userMail = trim($("#userMail").val());
	var userPassword = trim($("#userPassword").val());
	checkLoginInfo(userMail,userPassword);
	var userPasswordAgain = trim($("#userPasswordAgain").val());
	if(userPassword != userPasswordAgain)
	{
		msgTips("两次密码不相同!");
		return;
	}
	
	var userAlias = trim($("#userAlias").val());
	if(userAlias === "")
	{
		$("#register_info").text("请输入中文名!");
		return;
	}
	
	userPassword = hex_md5(userPassword);
	
	$.ajax({
		url: base_url + 'user/register.do',
		data:{'userMail':userMail,'userPassword':userPassword,'userAlias':userAlias},
		type:'POST',
		dataType:'text',
		success:function(data){
			if(data === "true"){
				alert("注册成功,请待管理员审核!");
				window.open('login.jsp','_self');
			}
			else
				msgTips("注册失败,请稍后再试..");
		}
	});
}

function checkUser()
{
	if($("#userMail").val() == "")
		return;
	
	if(!valid_email($("#userMail").val()))
	{
		$("#mailInfo").text("邮箱格式不正确!");
		return;
	}
	$.ajax({
		url:base_url +'user/checkExist.do',
		data:{'userMail':$("#userMail").val()},
		type:'POST',
		dataType:'text',
		success:function(data){
			if(data === "true") {
				$("#mailInfo").text("该邮箱己被注册");
				$("#userMail").focus();
				return;
			}else {
				$("#mailInfo").text("邮箱名可用");
			}
		}
	});
}

function valid_email(email) {
	if(email === "admin")
		return true;
	var patten = new RegExp(/^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]+$/);
	return patten.test(email);
}

/********用户注册结束****/


/************用户登陆开始**********************/
function msgTips(msg)
{
	$("#register_info").text(msg);
}

function checkLoginInfo(userName,password)
{
	if(!userName || userName == "")
	{
		msgTips("用户名不能为空!");
		return false;
	}

	if(!password||password == "")
	{
		msgTips("密码不能为空");
		return false;
	}
	return true;
}

function initUser(targetUrl,errorLogMsg)
{
	if(errorLogMsg)
		msgTips(errorLogMsg);
	var userName = readCookie("login_username");
	var enPassword = readCookie("login_password");
	if(userName == "" || enPassword == "" || userName == null || userName == "null")
		return;
	var remember = readCookie("remember");
	if(remember)
	{
		loginCheck(userName,enPassword,targetUrl,remember);
	}
	else
	{
		//设置用户名密码
		$("#user_mail_login").val(userName);
	}
}

function login()
{
	var userName = trim($("#user_mail_login").val());
	var password = $("#user_password_login").val();
	var targetUrl = request('targetUrl');
	remember = $("#remerber_password")[0].checked;
	if(!checkLoginInfo(userName, password))
	{
		return false;
	}
	var enPassword = hex_md5(password);
	loginCheck(userName,enPassword,targetUrl,remember);
}

function loginCheck(userName,password,targetUrl,remember)
{
	var params = "userName=" + userName + "&password=" + password + "&targetUrl=" + encodeURIComponent(targetUrl) + "&remember=" + remember;
	$.ajax({
		url 	 : base_url + 'user/login.do',
		type 	 : 'POST',
		data     : params,
		success: function(data){
			window.open(data,"_self");
		},
		error : function(){
			msgTips('用户名或密码错误!请重新输入!');
		}
	});
	return false;
}
/************用户登陆结束**********************/

$(function(){
	//绑定enter键
	$("#user_password_login").keydown(function(e){ 
        e=e||window.event;
        if(e.keyCode==13){
           login();
    	   return false;
        }
	});
	
	$("#register").click(register);
	$("#login_btn").click(function(){
		window.open("login.jsp","_self");
	});
	$("#login_in_btn").click(login);
	$("#userMail").blur(checkUser);
	$("#user_mail_login").blur(function(){
		if($("#user_mail_login").val() === "") return;
		if(!valid_email($("#user_mail_login").val()))
		{
			showInfoWin("error","邮箱格式不正确,请重新输入!");
			$(this).val("");
			this.focus();
			return;
		}
	});
	
});