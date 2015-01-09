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

function initSystem()
{
	$.ajax({
		url: base_url + 'backRight/getSystem.do',
		type:'POST',
		data:{'userMail':'system'},
		dataType:'json',
		async:false,
		success:function(data){
			$('#openRight').val(data.openRight);
		}
	});
}

$(function(){
	onWindowResize();
	$(window).resize(onWindowResize);
	initSystem();
	//设置权限
	$.ajax({
		url : 'user/get_user_right.jsp',
		type : 'POST',
		success : setUserRightAjax,
		data:{},
		error : function(){
			alert("Server Error!");
		}
	});
});