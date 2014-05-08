
function initUploadify()
{
	$('#fileInput').Huploadify({
		auto:true,
//		fileTypeExts:'*.jpg;*.png;*.exe',   //设置上传允许后缀的文件
		multi:true,
//		formData:{key:123456,key2:'vvvv'},
		//fileSizeLimit:9999,
		showUploadedPercent:true,//是否实时显示上传的百分比，如20%
//		showUploadedSize:true,
		removeTimeout:1000,
		cancelImg : 'lib/fileUpload/uploadify-cancel.png',   //指定取消上传的图片，默认‘cancel.png’
		fileSizeLimit:122880,   //120MB 
		simUploadLimit :5,         //多文件上传时，同时上传文件数目限制
		uploader : '../../attachment/upload.jsp;jsessionid='+ readCookie('JSESSIONID'),       //指定服务器端上传处理文件，默认‘upload.php’
		onUploadStart:function(){
			//alert('开始上传');
		},
		onInit:function(){
			//alert('初始化');
		},
		onUploadComplete:function(){
			$("#uploadFileDiv").modal('hide');
		},
		onDelete:function(file){
		},
		
		onUploadSuccess : function(file, data, response) {
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
	    
		onQueueComplete : function(queueData) {
			$("#uploadFileDiv").modal('hide');
	    },
	    //返回一个错误，选择文件的时候触发  
	    onSelectError:function(file, errorCode, errorMsg){  
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
	    onUploadError : function(file, errorCode, errorMsg, errorString) {
	        alert('The file ' + file.name + ' could not be uploaded: ' + errorString);
	        return false;
	    }
	});
	
}

function executeSubmitUploadFile()
{
	$('#fileInput').uploadify('upload');
}

