var jcrop_api;

//上传头像
function uploadImage()
{
	// 检查图片格式
    var fileId,url,f = $("#userPic").val();
    if(!/\.(gif|jpg|jpeg|png|JPG|PNG)$/.test(f))
    {
        alert("图片类型必须是.jpeg,jpg,png中的一种");
        return false;
    }
    
    // 利用ajaxFileUpload js 插件上传图片
    $.ajaxFileUpload({url: base_url + "attachment/upload.jsp",
       
    	secureuri:false,
        fileElementId:"userPic",
        dataType:"json",
       
        success:function (data , status) {
        	for(var key in data){
				fileId = key;
				break;
			}
        	$("#fileId").val(fileId);
        	if(fileId)
        		updateUserPicId(fileId);
        	var url = base_url + "attachment/download.jsp?method=download&id=" + fileId;
        	$("#target").attr("src",url);
        	$("#preview").attr("src",url);
        	initJcrops();
        },
        error:function (data, status, e) {
            alert("图片上传失败,请重新选择图片");
        }
    });
    return false;
}

//更新用户头像id
function updateUserPicId(fileId)
{
	$.ajax({
		url: base_url + 'user/updateUserInfo.do',
		type:'POST',
		dataType:'json',
		data:{'user':readCookie("login_username"),"picId":fileId},
		success:function(data){
		},error:function(data){
			alert("无法更新用户!");
		}
	});
}

function initJcrops()
{
	$('#target').Jcrop({
		onChange: showPreview,
		onSelect: showPreview,
		drawBorders:true,
		aspectRatio: 1,
	},function(){
		jcrop_api = this;
	});
}

function showPreview(coords)
{
	//源图像框宽高
	var boundx = $('#target').width();
	var boundy = $('#target').height();
	//预览框宽高
	var tarwidth = $('.preview-container').width();
	var tarheight = $('.preview-container').height();
	
	var rx = tarwidth / coords.w;
	var ry = tarheight / coords.h;
	var width = Math.round(rx * boundx);
	var height = Math.round(ry * boundy);
	var left = Math.round(rx * coords.x);
	var top = Math.round(ry * coords.y);
	
	$('#preview').css({
		width: width + 'px',
		height: height + 'px',
		marginLeft: '-' + left + 'px',
		marginTop: '-' + top + 'px'
	});
	
	$('#x').val(coords.x);
	$('#y').val(coords.y);
	$('#w').val(coords.w);
	$('#h').val(coords.h);
};

function cutImage()
{
	var data = 
	{
		fileId:$("#fileId").val(),
		x:$("#x").val(),
		y:$("#y").val(),
		w:$("#w").val(),
		h:$("#h").val()
	};
	
	$.ajax({
		url: base_url + 'user/cutImage.do',
		type:'POST',
		dataType:'text',
		data:data,
		success:function(data){
			if(data === "true"){
				showInfoWin("success","修改成功!");
				$("#add_user_photo").modal('hide');	
				window.location.reload();
			}else{
				showInfoWin("error","修改失败!");
			}
		}
	});
}


function modifyUserName()
{
	var nickName = $("#user_name_modify").val();
	$.ajax({
		url: base_url + 'user/updateUserInfo.do',
		type:'POST',
		dataType:'text',
		data:{'user':readCookie("login_username"),"nickName":nickName},
		success:function(data){
			if(data === "true"){
				$("#user_name_t").text(nickName);
				$("#user_name_modify").val(nickName);
				//修改cookie
				createCookie("login_nickname=" + encodeURIComponent(nickName));
				window.location.reload();
				showChange(false);
			}else{
				showInfoWin("error",data);
			}
		},error:function(data){
			alert("无法更新用户!");
		}
	});
}

function modifyPassword()
{
	var nowPass = $("#now_pass").val();
	var changePass = $("#change_pass").val();
	var changePassAgain = $("#change_pass_again").val();
	if(nowPass === ""){
		showInfoWin("error","请输入当前密码!");
		$("#now_pass").focus();
		return;
	}
	
	if(changePass === ""){
		showInfoWin("error","请输入新密码!");
		$("#change_pass").focus();
		return;
	}
	
	if(changePass !== changePassAgain){
		showInfoWin("error","两次输入密码不相等!");
		return;
	}
	
	nowPass = hex_md5(nowPass);
	changePass = hex_md5(changePass);
	
	$.ajax({
		url: base_url + 'user/updateUserInfo.do',
		type:'POST',
		dataType:'text',
		data:{'user':readCookie("login_username"),"nowPass":nowPass,"changePass":changePass},
		success:function(data){
			if(data === "true"){
				showInfoWin("success","密码修改成功!");
			}else{
				showInfoWin("error",data);
			}
		},error:function(data){
			alert("无法更新用户!");
		}
	});
}

function showChange(show)
{
	if(show){
		$("#showUser").hide();
		$("#modifyUser").show();
	}else{
		$("#showUser").show();
		$("#modifyUser").hide();
	}
}


function bindEvents()
{
	//修改用户名
	$("#user_name_modify").keydown(function(e){
		if(e.keyCode === 13)
			modifyUserName();
	});
}

$(function(){
	bindEvents();
	$('.nav-tabs a:first').tab('show');
	var user = queryUserInfo();
	$("#user_name_p").text(user.userName);
	$("#user_name_t").text(user.nickName);
	$("#user_name_modify").val(user.nickName);
	$("#userImg").attr("src",userPicUrl);
});