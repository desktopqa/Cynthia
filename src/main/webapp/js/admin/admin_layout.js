function onWindowResize()
{
	var topHeight     = 50;
	var leftTreeWidth = 220;
	var windowWidth   = $(window).width();
	var windowHeight  = $(window).height();
	$("#main-grid-outer").height(windowHeight - topHeight);
	$("#main-grid-outer").css('left',leftTreeWidth);
	$("#main-grid-outer").width(windowWidth - leftTreeWidth);
	$("#main-iframe").height(windowHeight - topHeight - 5);
	$("#main-iframe").width(windowWidth - leftTreeWidth - 5);
}

function setUserRightAjax(rootNode)
{
	eval("var isSuperAdmin = " + $(rootNode).find("isSuperAdmin").text());
	
	$.each($("#left-tree .admin"),function(index,node){
		if(isSuperAdmin){
			$(node).show();
		}else{
			$(node).hide();
		}
	});
	
}

function initSystem(callback)
{
	$.ajax({
		url: base_url + 'backRight/getSystem.do',
		type:'POST',
		data:{'userMail':'system'},
		dataType:'json',
		success:function(data){
			$('#openRight').val(data.openRight);
			callback();
		},error:function(data){
			callback();
		}
	});
}

$(function(){
	onWindowResize();
	$(window).resize(onWindowResize);
	initSystem(function(){
		//设置权限
		$.ajax({
			url : 'user/get_user_right.jsp',
			type : 'POST',
			success : setUserRightAjax,
			data:{},
			error : function(){
				alert("对不起，你没有访问该页面的权限,确定后将跳转至首页!");
				window.location.href = base_url + "index.html";
			}
		});
	});
});