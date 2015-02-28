(function($){
	var guideEditor = null;
	var editorOptions = {
			filterMode : true,
			allowFileManager : true,
			urlType : 'relative',
			formatUploadUrl : false,
			height: 600,
			uploadImage:false,  //不上传图片，直接用base64
			resizeType : 0
	};
	
	function initAllGuide(callback){
		$.ajax({
			url: base_url + 'guide/getAllGuide.do',
			dataType:'json',
			success:function(data){
				var $allguide = $('#all_guide');
				$allguide.empty();
				
				var template = '<li><font>{0}</font>&nbsp;<a href="#" id={1}>{2}</a></li>';
				for(var i = 0,length = data.length; i < length; i++){
					$allguide.append(String.format(template,i+1 , data[i].guideId,data[i].guideName));
				}
				if(callback){
					callback();
				}
			}
		});
	}
	
	function bindEvents(){
		bindClick();
	}
	
	function bindClick(){
		$('#all_guide').on('click','a',guideAClick);
		//保存说明
		$('#save_editor').click(function(){
			var guideId = $('#guide_id').val();
			if(guideId === "")
				return;
			else{
				var guidHtml = getSafeParam(guideEditor.html());
				
				$.ajax({
					url:base_url + 'guide/saveGuideHtml.do',
					type:'post',
					data:{guideId:guideId,guideHtml:guidHtml},
					success:function(data){
						if(data === "true")
							showInfoWin('success','保存成功!');
						else
							showInfoWin('error','保存失败!');
					}
				});
			}
		});
	}
	
	function guideAClick(e){
		var guideId = $(this).attr('id');
		$('#guide_id').val(guideId);
		
		$('#all_guide li').removeClass("active");
		$(this).closest("li").addClass("active");
		
		if(guideId){
			$.ajax({
				url:base_url + 'guide/getGuideHtml.do',
				dataType:'text',
				data:{guideId:guideId},
				success:function(data){
					//是否需要data解析
					var guideHtmlNode = $('#guide');
					if(guideHtmlNode.length > 0){
						guideHtmlNode.empty();
						guideHtmlNode.html(data);
					}
					
					if(guideEditor)
						guideEditor.html(data);
				}
			});
		}
	}
	
	$(function(){
		var guideId = request('guideId');
		bindEvents();
		initAllGuide(function(){
			if(KindEditor)
				guideEditor = KindEditor.create('textarea[id="guide_editor"]', editorOptions);
			
			if(guideId){
				$("#all_guide a[id=" + guideId + "]").click();
			}
		});
	});
})(jQuery);