/**
 * @description: change the the width
 * @author:liming
 * @last modify: 
 */

 var lineMove = false;
 var currTh = null;
 $(document).ready(function() {//function(event) { $(this).css({ 'cursor': '/web/Page/frameset/images/splith.cur' }

	$("body").bind("selectstart", function() { return !lineMove; });
	
    $("body").append("<div id=\"line\" style=\"width:1px;height:900px; z-index:999999; border-left:1px solid #00000000; position:absolute;display:none\" ></div> ");
	 
	$("body").on("mousemove","th",function(event){
		if (lineMove == true) {
			var pos = currTh.offset();
			var top = pos.top;
			var height = currTh.parent().parent().height();
			var index = currTh.prevAll().length;
			currTh.width(event.clientX - pos.left);
			currTh.css({'border-right':'2px solid #DAACAC'});
			$("#line").css({ "height": height, "top": top,"left":event.clientX,"display":"" });
			$("#main-grid-header").find("colgroup col:eq('"+index+"')").width(event.clientX - pos.left);
			$("#main-grid-content").find("colgroup col:eq('"+index+"')").width(event.clientX - pos.left);
        }else{
			var th = $(this);
			if (th.prevAll().length <= 1 || th.nextAll().length < 1) {
				 return;
			}
			var left = th.offset().left;
		
			$("#main-grid-header").find("thead th").css({'border-right':''});
			
			if (event.clientX - left < 8 ) {
				th.css({ 'cursor': 'w-resize' }).prev().css({ 'cursor': 'w-resize' , 'border-right':'2px solid #DAACAC'});
			}else if((th.width() - (event.clientX - left)) < 8){
				th.css({ 'cursor': 'w-resize' , 'border-right':'2px solid #DAACAC'});
			}else {
				th.css({ 'cursor': 'pointer' });
			}
		}
    }).on("mouseout","th",function(event){
		$.each($("#main-grid-header").find("thead th"),function(index,node){
			$(node).css({'border-right':''});
		});	
    }).on("mousedown","th",function(event){
        var th = $(this);
        if (th.prevAll().length <= 1 || th.nextAll().length < 1) {
             return;
        }
        var pos = th.offset();
        if (event.clientX - pos.left < 4 || (th.width() - (event.clientX - pos.left)) < 4) {
             var height = th.parent().parent().height();
             var top = pos.top;
             $("#line").css({ "height": height, "top": top,"left":event.clientX,"display":"" });
             lineMove = true;
             if (event.clientX - pos.left < th.width() / 2) {
            	 currTh = th.prev();
             }else {
                 currTh = th;
             }
         }
    });
	
     $("body").bind("mouseup", function(event) {
    	if(typeof saveFilterWidth == 'function' && currTh){
     		var filterId = $('#filterId').val();
     		var fieldId = currTh.attr('value');
     		var width = currTh.css('width');
     		saveFilterWidth(filterId,fieldId,width);
     	}
        if (lineMove == true) {
             $("#line").hide();
		     lineMove = false;		 
			 var pos = currTh.offset();
		     var index = currTh.prevAll().length;
		     currTh.width(event.clientX - pos.left);
		     $("#main-grid-header").find("colgroup col:eq('"+index+"')").width(event.clientX - pos.left);
			 $("#main-grid-content").find("colgroup col:eq('"+index+"')").width(event.clientX - pos.left);
			 onWindowResize();
        }
     });
 });