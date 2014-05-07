$(function(){

});


function initUploadify()
{
	//检测是否安装flash并提示firefox
	$("#uploadFileDiv").append("<object id='SWFUpload_test' type='application/x-shockwave-flash' data='lib/fileUpload/uploadify.swf?preventswfcaching=1398169140621' width='120' height='30' class='swfupload' style='position: absolute; z-index: 1;display:none;'></object>");
	
	$('#fileInput').uploadify ({
		//以下参数均是可选
		'method'   : 'post',
		'buttonCursor' : 'hander',
		'swf'      : 'lib/fileUpload/uploadify.swf',  //指定上传控件的主体文件，默认‘uploader.swf’
		'uploader'    : '../../attachment/upload.jsp;jsessionid='+ readCookie('JSESSIONID'),       //指定服务器端上传处理文件，默认‘upload.php’
		'cancelImg' : 'lib/fileUpload/uploadify-cancel.png',   //指定取消上传的图片，默认‘cancel.png’
		'auto'      : true,               //选定文件后是否自动上传，默认false
//		'folder'    : '/uploads'          //要上传到的服务器路径，默认‘/’
		'multi'     : true,               //是否允许同时上传多文件，默认false
//		'fileDesc' : 'rar文件或zip文件'    //出现在上传对话框中的文件类型描述
//		'fileExt'   : '*.rar;*.zip',      //控制可上传文件的扩展名，启用本项时需同时声明fileDesc
		'fileSizeLimit':'120MB', 
		'simUploadLimit' :5,         //多文件上传时，同时上传文件数目限制
		
		'onUploadSuccess' : function(file, data, response) {
			data = eval('(' + data + ')');
			var selObjNode;
			for(var key in data){
				selObjNode = $("#field" + $("#objId").val());
				selObjNode.append("<option value="+ key+">" +data[key]+ "</option>");
			}

			var field = getFieldById($("#objId").val());
			
			if(field != null)
			{
				field.datas.length = 0;
				$.each(selObjNode.find("option"),function(index,node){
					field.datas[field.datas.length] = node.value + "&|;" + node.text;
				});
			}
	    },
	    //检测FLASH失败调用  
        'onFallback':function(){  
            alert("您未安装Flash控件,文件将无法上传,请安装Flash控件后再试！");  
        }, 
        
		'onQueueComplete' : function(queueData) {
			$("#uploadFileDiv").modal('hide');
	    },
	    //返回一个错误，选择文件的时候触发  
        'onSelectError':function(file, errorCode, errorMsg){  
            switch(errorCode) {  
                case -100:  
                    alert("上传的文件数量已经超出系统限制的"+$('#file_upload').uploadify('settings','queueSizeLimit')+"个文件！");  
                    break;  
                case -110:  
                    alert("文件 ["+file.name+"] 大小超出系统限制的"+$('#file_upload').uploadify('settings','fileSizeLimit')+"大小！");  
                    break;  
                case -120:  
                    alert("文件 ["+file.name+"] 大小异常！");  
                    break;  
                case -130:  
                    alert("文件 ["+file.name+"] 类型不正确！");  
                    break;  
            }  
        },  
	    'onUploadError' : function(file, errorCode, errorMsg, errorString) {
//            alert('The file ' + file.name + ' could not be uploaded: ' + errorString);
            return false;
        }
	});
}

function executeSubmitUploadFile()
{
	$('#fileInput').uploadify('upload');
}

