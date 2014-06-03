var userPicUrl,  //用户头像地址
	WEB_ROOT_URL = getWebRootDir(),//项目部署主路径
	base_url = getRootDir(); //相对路径

var ESCAPE_APOS = "QADEV_ESCAPE_APOS";
var ESCAPE_QUOT = "QADEV_ESCAPE_QUOT";
var ESCAPE_LT = "QADEV_ESCAPE_LT";
var ESCAPE_GT = "QADEV_ESCAPE_GT";
var ESCAPE_AMP = "QADEV_ESCAPE_AMP";

var noSearchUrl = new Array();  //不需要搜索框的页面
noSearchUrl.push('editFilter.html');
noSearchUrl.push('previewFilterResult.jsp');
noSearchUrl.push('admin');
noSearchUrl.push('cplugin');
noSearchUrl.push('about.html');
noSearchUrl.push('statistic');
noSearchUrl.push('taskManagement.html');
noSearchUrl.push('userConfig.html');
noSearchUrl.push('feedback.html');

//不需要头部导航的页面
var noAddHeadUrl = new Array();
noAddHeadUrl.push('login.jsp');
noAddHeadUrl.push('register.jsp');

String.format = function (src) 
{
    if (arguments.length == 0) return null;
    var args = Array.prototype.slice.call(arguments, 1);
    return src.replace(/\{(\d+)\}/g, function (m, i) {
        return args[i];
    });
};

//map 类
var Map = function(){  
	
	 /** Map 大小 **/
  var size = 0;
  /** 对象 **/
  var entry = new Object();
  
  /** 存 **/
  this.put = function (key , value)
  {
      if(!this.containsKey(key))
      {
          size ++ ;
      }
      entry[key] = value;
  };
  
  /** 取 **/
  this.get = function (key)
  {
      if( this.containsKey(key) )
      {
          return entry[key];
      }
      else
      {
          return null;
      }
  };
  
  /** 删除 **/
  this.remove = function ( key )
  {
      if( delete entry[key] )
      {
          size --;
      }
  };
  
  /** 是否包含 Key **/
  this.containsKey = function ( key )
  {
      return (key in entry);
  };
  
  /** 是否包含 Value **/
  this.containsValue = function ( value )
  {
      for(var prop in entry)
      {
          if(entry[prop] == value)
          {
              return true;
          }
      }
      return false;
  };
  
  /** 所有 Value **/
  this.values = function ()
  {
      var values = new Array(size);
      for(var prop in entry)
      {
          values.push(entry[prop]);
      }
      return values;
  };
  
  /** 所有 Key **/
  this.keys = function ()
  {
      var keys = new Array(size);
      for(var prop in entry)
      {
          keys.push(prop);
      }
      return keys;
  };
  
  /** Map Size **/
  this.size = function ()
  {
      return size;
  };
  
  this.toJson = function(){
  	var json = "{";
	  	for(var item in entry){  
		  	json += "\"" + item + "\":\"" + entry[item] + "\",";
	  	}   
	  	if(json.indexOf(",") != -1)
		  	json = json.substring(0,json.length -1);
	  	json += "}";
	  	return json;
  };
};

/**
 * 对table 或 select 下的选项进行搜索
 * searchId : table中tbody或select Id
 * searchContent:要搜索的tr或者option
 * value:搜索的值
 */
function searchItem(searchId, searchContent,value)
{
	if(!value)  //搜索值为空全部显示
		$("#" + searchId + ">" + searchContent).show();
	else{
		value = value.toLowerCase();
		$.each($("#" + searchId + ">" + searchContent),function(index,node){
			if($(node).text() && $(node).text().toLowerCase().indexOf(value) != -1)
				$(node).show();
			else
				$(node).hide();
		});
	}
}

//解决ie下不能使用Object.keys方法
Object.keys = Object.keys || (function () {
    var hasOwnProperty = Object.prototype.hasOwnProperty,
        hasDontEnumBug = !{toString:null}.propertyIsEnumerable("toString"),
        DontEnums = [
            'toString',
            'toLocaleString',
            'valueOf',
            'hasOwnProperty',
            'isPrototypeOf',
            'propertyIsEnumerable',
            'constructor'
        ],
        DontEnumsLength = DontEnums.length;
  
    return function (o) {
        if (typeof o != "object" && typeof o != "function" || o === null)
            throw new TypeError("Object.keys called on a non-object");
     
        var result = [];
        for (var name in o) {
            if (hasOwnProperty.call(o, name))
                result.push(name);
        }
     
        if (hasDontEnumBug) {
            for (var i = 0; i < DontEnumsLength; i++) {
                if (hasOwnProperty.call(o, DontEnums[i]))
                    result.push(DontEnums[i]);
            }   
        }
     
        return result;
    };
})();

//阻止浏览器的默认行为 
function stopDefault( e ) { 
    if (e&&e.preventDefault )//IE中阻止函数器默认动作的方式  
        e.preventDefault(); 
    else
        window.event.returnValue = false; 
    return false; 
}

if( document.implementation.hasFeature("XPath", "3.0") )
{
   // prototying the XMLDocument
   XMLDocument.prototype.selectNodes = function(cXPathString, xNode)
   {
      if( !xNode ) { xNode = this; }
      var oNSResolver = this.createNSResolver(this.documentElement)
      var aItems = this.evaluate(cXPathString, xNode, oNSResolver,
                   XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null)
      var aResult = [];
      for( var i = 0; i < aItems.snapshotLength; i++)
      {
         aResult[i] =  aItems.snapshotItem(i);
      }
      return aResult;
   }

   // prototying the Element
   Element.prototype.selectNodes = function(cXPathString)
   {
      if(this.ownerDocument.selectNodes)
      {
         return this.ownerDocument.selectNodes(cXPathString, this);
      }
      else{throw "For XML Elements Only";}
   }
   
// prototying the XMLDocument
   XMLDocument.prototype.selectSingleNode = function(cXPathString, xNode)
   {
      if( !xNode ) { xNode = this; }
      var xItems = this.selectNodes(cXPathString, xNode);
      if( xItems.length > 0 )
      {
         return xItems[0];
      }
      else
      {
         return null;
      }
   }

   // prototying the Element
   Element.prototype.selectSingleNode = function(cXPathString)
   {
      if(this.ownerDocument.selectSingleNode)
      {
         return this.ownerDocument.selectSingleNode(cXPathString, this);
      }
      else{throw "For XML Elements Only";}
   }
}

/**
 * 返回val在arr中的索引值
 * @param val
 * @param arr:数组
 * @returns
 */
function inArrayIndex(val,arr)
{
	if(arr==null||arr==undefined)
		return -1;
	if(typeof arr === "string"){
		return arr == val ? 1: -1;
	}else{
		var idx = -1;
		for(var i=0; i<arr.length;i++)
		{
			if(val == arr[i])
			{
			     idx = i;
			     break;
			}
		}
		return idx;
	}
}  

function moveOptions( sourceSelObjId, targetSelObjId )
{
	sourceSelObj = document.getElementById(sourceSelObjId);
	targetSelObj = document.getElementById(targetSelObjId);

	if( sourceSelObj == null || targetSelObj == null
			|| sourceSelObj.tagName != "SELECT" || targetSelObj.tagName != "SELECT" )
		return;

	for( var is = 0; is < sourceSelObj.options.length; is++ )
	{
		if( !sourceSelObj.options[is].selected )
			continue;

		var option = sourceSelObj.options[is];

		sourceSelObj.remove( is );
		is--;

		targetSelObj.options[ targetSelObj.options.length ] = option;
	}
}

function checkIsInputNum(event)
{
	if( !( checkIsAllNum( event.keyCode ) )
			&& event.keyCode!=45
				&& event.keyCode!=189
					&& event.keyCode!=109
						&& event.keyCode != 190
							&& event.keyCode != 110
						)
	{
		alert("只能输入数字，\"-\"，\".\"号，请同时检查您输入的是否是一个数字！");
		return false;
	}

	return true;
}

function isLegalityNum( numString )
{
	var  newPar= /^(-|\+)?\d+(\.\d+)?$/ ;

	return newPar.test( numString );
}

function checkIsAllNum( kc )
{
	if( ( kc >= 48 && kc <= 57 ) || ( kc >= 96 && kc <= 105 ) || event.keyCode == 46  || event.keyCode == 8 || event.keyCode == 13)
		return true;
	else
		return false;

	return true;
}

function getXMLStr(str)
{
	str = replaceAll(str, "&", "&amp;");
	str = replaceAll(str, "<", "&lt;");
	str = replaceAll(str, ">", "&gt;");
	str = replaceAll(str, "'", "&apos;");
	str = replaceAll(str, "\"", "&quot;");
	return str;
}

function getNoXMLStr(str)
{
	str = replaceAll(str, "&lt;", "<");
	str = replaceAll(str, "&gt;", ">");
	str = replaceAll(str, "&apos;", "'");
	str = replaceAll(str, "&quot;", "\"");
	str = replaceAll(str, "&amp;", "&");

	return str;
}

function getHTMLStr(str)
{
	str = replaceAll(str, "<", "&lt;");
	str = replaceAll(str, ">", "&gt;");
	return str;
}


function encodeAll(str)
{
	str = encodeAPOS(str);
	str = encodeQUOT(str);
	str = encodeLT(str);
	str = encodeGT(str);
	return encodeAMP(str);
}

function decodeAll(str)
{
	str = decodeAPOS(str);
	str = decodeQUOT(str);
	str = decodeLT(str);
	str = decodeGT(str);

	return decodeAMP(str);
}

function encodeAPOS(str)
{
	return replaceAll(str, "'", "QADEV_ESCAPE_APOS");
}

function decodeAPOS(str)
{
	return replaceAll(str, "QADEV_ESCAPE_APOS", "'");
}

function encodeQUOT(str)
{
	return replaceAll(str, "\"", "QADEV_ESCAPE_QUOT");
}

function decodeQUOT(str)
{
	return replaceAll(str, "QADEV_ESCAPE_QUOT", "\"");
}

function encodeLT(str)
{
	return replaceAll(str, "<", "QADEV_ESCAPE_LT");
}

function decodeLT(str)
{
	return replaceAll(str, "QADEV_ESCAPE_LT", "<");
}

function encodeGT(str)
{
	return replaceAll(str, ">", "QADEV_ESCAPE_GT");
}

function decodeGT(str)
{
	return replaceAll(str, "QADEV_ESCAPE_GT", ">");
}

function encodeAMP(str)
{
	return replaceAll(str, "&", "QADEV_ESCAPE_AMP");
}

function decodeAMP(str)
{
	return replaceAll(str, "QADEV_ESCAPE_AMP", "&");
}

function message(msg)
{
	alert(msg);
}

function errorInfo()
{
	alert("系统出现错误，请稍后刷新重试!");
}

function inArray(arr,value)
{
	if(arr==null||arr==undefined)
		return false;
	if(typeof arr === "string"){
		return arr == value ? true:false;
	}else{
		for(var i=0;i<arr.length;i++)
		{
			if(arr[i] == value)
				return true;
		}
		return false;
	}
}

function replaceAll(str, replaced, replacement)
{
	if(str == null)
		return "";
	var ret = "";

	var index = 0;
	while(str.indexOf(replaced, index) >= index)
	{
		ret += str.substring(index, str.indexOf(replaced, index)) + replacement;

		index = str.indexOf(replaced, index) + replaced.length;
	}

	ret += str.substring(index);

	return ret;
}

function getXMLDoc()
{
	if(document.implementation && document.implementation.createDocument)
	{
		return document.implementation.createDocument("", "", null);
	}

	if(window.ActiveXObject)
	{
		return new ActiveXObject("Msxml.DOMDocument");
	}

	return null;
}

function trim(str)
{
	if(str == null || str.length == 0)
		return "";

	var startIndex = 0;
	for(var i = 0; i < str.length; i++)
	{
		if(str.charAt(i) == " " || str.charAt(i) == "\t" || str.charAt(i) == "\n" || str.charAt(i) == "\r")
			startIndex++;
		else
			break;
	}

	if(startIndex == str.length)
		return "";

	var endIndex = str.length - 1;
	for(var i = str.length - 1; i > 0; i--)
	{
		if(str.charAt(i) == " " || str.charAt(i) == "\t" || str.charAt(i) == "\n" || str.charAt(i) == "\r")
			endIndex--;
		else
			break;
	}

	return str.substring(startIndex, endIndex + 1);
}

function setTextContent(node, text)
{
	while(node.childNodes.length > 0)
	{
		node.removeChild(node.firstChild);
	}

	if(text != "")
	{
		var textNode = node.ownerDocument.createTextNode("text");
		textNode.nodeValue = text;
		node.appendChild(textNode);
	}
}

function getTextContent(node)
{
	var value = "";

	for(var i = 0; i < node.childNodes.length; i++)
	{
		if(node.childNodes[i].nodeValue != null)
			value += node.childNodes[i].nodeValue;
	}

	return trim(value);
}

function getDocXML(doc)
{
	if(doc.xml)
	{
		return doc.xml;
	}

	return new XMLSerializer().serializeToString(doc);
}

function getSafeParam(param)
{
	param = encodeURI(param);
	param = replaceAll(param, "+", "%2B");
	param = replaceAll(param, "&", "%26");
	param = replaceAll(param, "#", "%23");

	return param;
}

function isNumber(numStr)
{
	if(trim(numStr).length == 0)
	{
		return false;
	}

	if(trim(numStr).length > 1 && trim(numStr).charAt(0) == "0")
	{
		return false;
	}

	for(var i = 0; i < trim(numStr).length; i++)
	{
		if(trim(numStr).charAt(i) != "0"
			&& trim(numStr).charAt(i) != "1"
			&& trim(numStr).charAt(i) != "2"
			&& trim(numStr).charAt(i) != "3"
			&& trim(numStr).charAt(i) != "4"
			&& trim(numStr).charAt(i) != "5"
			&& trim(numStr).charAt(i) != "6"
			&& trim(numStr).charAt(i) != "7"
			&& trim(numStr).charAt(i) != "8"
			&& trim(numStr).charAt(i) != "9")
		{
			return false;
		}
	}

	return true;
}

function newAdjustPRI( selObj, isUp )
{
	//check leagle
	if( selObj == null || selObj.options == null || selObj.options.length <= 1 || selObj.selectedIndex < 0 )
		return;


	if( isUp )
	{
		for ( var i=0; i<selObj.options.length; i++ )
		{
			if ( selObj.options[i].selected )
			{
				if ( i == 0 )
					return;

				var	option1 = selObj.options[i];
				var option2 = selObj.options[i - 1];

				changeOption(option1, option2);
			}
		}// for
	}
	else
	{
		for ( var i=selObj.options.length-1; i>=0; i-- )
		{
			if ( selObj.options[i].selected )
			{
				if ( i == (selObj.options.length-1) )
					return;

				var	option1 = selObj.options[i];
				var option2 = selObj.options[i + 1];

				changeOption(option1, option2);
			}
		}// for
	}
}

function addOption( selObj, option )
{
	selObj.options[selObj.length] = option;
}

function removeOption( selObj, index )
{
	for( var i = index; i < selObj.options.length - 1; i++ )
	{
		selObj.options[i] = selObj.options[i+1];
	}

	selObj.options.length--;
}

function adjustPRI( selObj, isUp )
{
	//check leagle
	if( selObj == null || selObj.options == null || selObj.options.length <= 1 || selObj.selectedIndex < 0 )
		return;

	if( isUp )
	{
		for ( var i=0; i<selObj.options.length; i++ )
		{
			if ( selObj.options[i].selected )
			{
				if ( i == 0 )
					return;

				var	t	= selObj.options[i];
				selObj.remove(i);
				selObj.add(t,i-1);
			}
		}// for
	}
	else
	{
		for ( var i=selObj.options.length-1; i>=0; i-- )
		{
			if ( selObj.options[i].selected )
			{
				if ( i == (selObj.options.length-1) )
					return;

				var	t	= selObj.options[i];
				selObj.remove(i);
				selObj.add(t,i+1);
			}
		}// for
	}
}

function changeOption(option1, option2)
{
	var value1 = option1.value;
	var text1 = getTextContent(option1);
	var type1 = option1.getAttribute("type");
	var dataType1 = option1.getAttribute("dataType");
	var desc1 = option1.getAttribute("desc");

	var value2 = option2.value;
	var text2 = getTextContent(option2);
	var type2 = option2.getAttribute("type");
	var dataType2 = option2.getAttribute("dataType");
	var desc2 = option2.getAttribute("desc");

	option1.value = value2;
	setTextContent(option1, text2);
	if(type2 != null)
		option1.setAttribute("type", type2);
	if(dataType2 != null)
		option1.setAttribute("dataType", dataType2);
	if(desc2 != null)
		option1.setAttribute("desc", desc2);

	option2.value = value1;
	setTextContent(option2, text1);
	if(type1 != null)
		option2.setAttribute("type", type1);
	if(dataType1 != null)
		option2.setAttribute("dataType", dataType1);
	if(desc1 != null)
		option2.setAttribute("desc", desc1);

	option1.selected = false;
	option2.selected = true;
}

//cookie related
function readCookie(name)
{
	var cookieValue;
	var nameEQ=name+"=";
	var ca=document.cookie.split(';');
	for(var i=0;i<ca.length;i++)
	{
		var c=ca[i];
		while(c.charAt(0)==' ')
		{
			c=c.substring(1,c.length);
		}
		if(c.indexOf(nameEQ)==0){
			
			cookieValue = c.substring(nameEQ.length,c.length);
			//解决在tomcat下cookie前面带引号的问题
			if(cookieValue.indexOf("\"") == 0)
				cookieValue = cookieValue.substring(1,cookieValue.length -1);
			return cookieValue;  
		}
	}

	return null;
}
function createCookie( value )
{
	var expires="";
	var date=new Date();
	date.setTime(date.getTime()+( 3600 * 24 *1000 * 365 ) );
	expires="; expires=" +date.toGMTString();
	document.cookie=value+expires+"; path=/";
}

function deleteCookie(name)
{
	var expdate = new Date();
	expdate.setTime(expdate.getTime() - (86400 * 1000 * 1));

	document.cookie = name + "=" + escape("") + "; expires=" + expdate.toGMTString() + "; path=/";
}

function encodeCookie(value)
{
	value = replaceAll(value, "=", "$replace_equality$");
	value = replaceAll(value, ";", "$replace_semicolon$");

	return value;
}

function decodeCookie(value)
{
	value = replaceAll(value, "$replace_equality$", "=");
	value = replaceAll(value, "$replace_semicolon$", ";");

	return value;
}

function isLeapYear(year){
	if((year %4==0 && year %100!=0) || (year %400==0))
		return true;
	else
		return false;
}

function callDays(year,month){
	var days=0;
	switch(month){
	case 1: case 3: case 5: case 7: case 8: case 10: case 12: days=31;break;
	case 4: case 6: case 9: case 11: days=30;break;
	case 2: if(isLeapYear(year)) days=29;
	else days=28;
	break;
	}
	return days;
}

function arrayContainsString(arr,str){
	if(!arr||arr==null||arr.length==0)
		return false;
	for(var i=0;i<arr.length;i++)
		if(arr[i]==str)
			return true;
}

//发送邮件
function showSendMailWin()
{
	$("#send_mail_win").css('display','block');
	$("#send_mail_win_back").css('display','block');
	$("#sendMailReceivers").val("");
	$("sendMailContent").val("");
}


function executeSendMailCancel()
{
	$("#send_mail_win").hide();
	$("#send_mail_win_back").hide();
}


function executeSendMailSubmit()
{
	var sendMailReceivers = $("#sendMailReceivers").val();
	if(sendMailReceivers == "")
	{
		alert("请填写收件人");
		return;
	}
	var usrArray = sendMailReceivers.split(";");
	for(var i = 0 ; i< usrArray.length ; i++){
		if(usrArray[i].indexOf("@")!=-1)
			if(!isEmail(usrArray[i])){
				alert("邮箱格式不正确，请重新填写");
				$("#sendMailReceivers").focus();
				return;
			}
	}

	if($("#sendMailContent").val() == "")
	{
		alert("请填写邮件正文");
		return;
	}

	var sendMailContent = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=GBK\"/>";
	sendMailContent += "<style type=\"text/css\">table{border:1px #E1E1E1 solid;}td{border:1px #E1E1E1 solid;padding:10px;}</style></head>"
	sendMailContent += "<body><table>";
	sendMailContent += "<tr><td>邮件正文 </td><td>" + replaceAll(getXMLStr($("#sendMailContent").val()), "\n", "<br>") + "</td>";
	sendMailContent += "<tr><td>bug编号</td><td><a href=\"" + WEB_ROOT_URL + "taskManagement.html?operation=read&taskid="+taskId+"\">"+taskId+"</a></td>";
	sendMailContent += "<tr><td>bug描述</td><td>" + replaceAll($("#input_taskDescription").val(),"../attachment/download_json.jsp", WEB_ROOT_URL + "attachment/download_json.jsp") +"</td>";
	sendMailContent += "</table></body></html>";

	var params = "sendMailReceivers=" + getSafeParam(sendMailReceivers);
	params += "&sendMailSubject=" + getSafeParam("[Cynthia]有数据需要您的处理意见，请关注并处理");
	params += "&sendMailContent=" + getSafeParam(sendMailContent);

	$("#mail_send_ok").disabled = true;

	$.ajax({
		url : "../mail/executeSendMail.jsp",
		data : params,
		type : 'POST',
		success : send_mail_success
	});
}

function send_mail_success()
{
	alert("邮件发送成功!");
	executeSendMailCancel();
	$("#mail_send_ok").disabled = false;
}


//end of send mail


function isEmail(strEmail)
{
	if (strEmail.search(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/) != -1)
		return true;
	else
		return false;
}

function isSysFilter(filterId)
{
	if((filterId == "119891")||(filterId == "119892")||(filterId == "119893")||(filterId=="119695"))
		return true;
	return false;
}

function addCnzzStatic()
{
	var cnzzScript = window.document.createElement("script");
    cnzzScript.setAttribute("src", "http://s22.cnzz.com/stat.php?id=5537061&web_id=5537061");
    cnzzScript.setAttribute("type", "text/javascript");
    document.body.appendChild(cnzzScript);
}

function request(paras){
	var url = window.location.href;
	if(url.indexOf("#") >=0)
		url = url.substring(0, url.indexOf("#"));
	var paraString = url.substring(url.indexOf("?")+1,url.length).split("&");
	var paraObj = {} ;
	for (var i=0; j=paraString[i]; i++){
		paraObj[j.substring(0,j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=")+1,j.length);
	}
	var returnValue = paraObj[paras.toLowerCase()];
	if(typeof(returnValue)=="undefined"){
		return "";
	}else{
		return returnValue;
	}
}

/*************信息提示框****************/
//loading框
function showLoading(isShow)
{
	if(isShow){
		if($("#layout").length == 0){
			var info="<div id=\"layout\" style=\"display: none;position: absolute;top:40%;left: 40%;width: 20%;height: 20%;z-index: 999999;\"><img src=\"" + base_url + "images/refresh.gif\"/></div>";
			$("body").append(info);
		}
		$('#layout').fadeIn("fast");
	}else{
		$('#layout').fadeOut("fast");
	}
}

function closeInfoWin(type) {  
	$("#warning-info").hide();
}  

function showInfoWin(type, message , time)
{
	$("#warning-info").remove();
	var info="<div class='alert alert-"+type+" hide' style='position:fixed;top:5px;left:500px;width:30%;height:20px; margin:0 auto; z-index:99999' id='warning-info'>";
	info += "<button type='button' class='close' data-dismiss='alert'>&times;</button>";
	info += "<p id='message'>"+message+"</p>";
	info += "</div>";
	$("body").append(info);
	$("#warning-info").show();
	if(time == undefined)
		window.setTimeout("closeInfoWin()", 2000);
	else
		window.setTimeout("closeInfoWin()", time);
}
/*************信息提示框结束****************/

function logout()
{
	if(!window.confirm("确定要退出Cynthia吗？"))
		return;
	window.location = base_url + 'logout.jsp';
}

/**
 * 返回根目录路径,方便不同路径下的ajax请求
 * @returns {String}
 */
function getRootDir()
{
	var contextPath='',count,webBaseUrl=WEB_ROOT_URL,webBaseCount;
	
	var pathName = document.location.pathname;
    pathName = pathName.substr(1);
    //求pathName中 / 的个数 多少个则向上返回多少级；
    count = pathName.replace(/[^\/]/g,'').length;
    webBaseUrl = trim(webBaseUrl).substring(8);
    webBaseCount = webBaseUrl.replace(/[^\/]/g,'').length - 1;
    
    count -= webBaseCount;

    if(count > 0){
    	for(var i = 0; i < count; i++)
    		contextPath += "../";
    }else{
    	//当前在根目录路径
    	contextPath += "";
    }
    return contextPath;
}

function isChinese(str)
{
 	var reg = /^[\u0391-\uFFE5]+$/;
 	return reg.test(str);
}

function getLengthOfStr(str)
{
	var length = 0;
	for(var i=0;i<str.length;i++)
	{
		if(isChinese(str.charAt(i)))
		{
			length += 2;
		}
		else
		{
			length++;
		}
	}
	return length;
}

function encodeAllUrl(str)
{
	str = encodeAPOS(str);
	str = encodeQUOT(str);
	str = encodeLT(str);
	str = encodeGT(str);
	str = encodeAMP(str);
	str = replaceAll(str, "\n", "");
	str = replaceAll(str, "\r\n", "");
	str = replaceAll(str, "\r", "");
	str = replaceAll(str, "\t", "");
	str = replaceAll(str, "\\", "");
	return str;
}
 
function enableSelectSearch()
{
	$("select").each(function(idx,select){
		if(!($(select).hasClass("multiLine")||$(select).hasClass('noSearch')))
		{
			$(select).select2({
				matcher: function(term, text, opt) {
				      return text.toUpperCase().indexOf(term.toUpperCase())>=0
				       || opt.val().toUpperCase().indexOf(term.toUpperCase())>=0;
				}
			});
		}else if($(select).hasClass("multiLine")&&!($(select).hasClass('noSearch')))
		{
			$(select).select2({
			});
		}
	});
}

//判断是否需要header
function judgeNeedHeader()
{
	var curUrl = document.location.pathname;  //当前地址路径 
	var need = true;
	for(var i = 0 ; i < noAddHeadUrl.length; i ++){
		if(curUrl.indexOf(noAddHeadUrl[i]) != -1){
			need = false;
			break;
		}
	}
	return need;
}

//判断是否需要搜索框
function judgeNeedSearch()
{
	var curUrl = document.location.pathname;  //当前地址路径 
	var need = true;
	for(var i = 0 ; i < noSearchUrl.length; i ++){
		if(curUrl.indexOf(noSearchUrl[i]) != -1){
			need = false;
			break;
		}
	}
	return need;
}

function getWebRootDir()
{
	if(!WEB_ROOT_URL){
		WEB_ROOT_URL = readCookie('webRootDir');
	}
		
	return WEB_ROOT_URL;
}

function queryUserInfo()
{
	var user;
	$.ajax({
		url: base_url + 'user/getUserInfo.do',
		type:'POST',
		async:false,
		dataType:'json',
		data:{'user':readCookie("login_username")},
		success:function(data){
			user = data;
			if(!data.picId)
				userPicUrl = base_url + "images/default_user.png";
			else
				userPicUrl = base_url + "attachment/download.jsp?method=download&id=" + data.picId;
		}
	});
	return user;
}

function addHeadHtml()
{
	var rootDir = base_url,userInfo;
	var userMail = readCookie("login_username");
	//用户头像id
	if(!userPicUrl)
		userInfo = queryUserInfo();
	
	if(!userMail)  
	{
		//cookie不存在重新登陆
		window.location = base_url + 'logout.jsp';
	}
	
	var userName = readCookie("login_nickname");
	userName = !userName ? userMail.substring(0,userMail.indexOf("@")):userName;
	userName = decodeURIComponent(userName); //中文名解码
	
	var headHtml = "";
	headHtml += "<div class=\"row-fluid navbar navbar-fixed-top\">";
	headHtml += "<div class=\"container\" style=\"width:100%;\">";
	//图片logo链接
	headHtml += "<a href='" + rootDir + "index.html' class='pull-left'><img src='" + rootDir + "images/logo.png' style='width:65px;height:40px;margin-left: 20px;'/></a>"; 
	//专心专注专业
	headHtml += "<a class=\"brand\" href='" + rootDir + "index.html' style=\"font-size:14px;color:#666;padding: 10px 10px 0px;margin-left:0px;\">专心、专注、专业</a>";
    
	//搜索框
	if(judgeNeedSearch())
	{
		headHtml += "<div class=\"input-append pull-left middle\">";
		headHtml += "<select id =\"searchType\" class=\"noSearch\" style=\"width:80px;line-height:24px;\" onchange=\"onSearchTypeChange();\">";
		headHtml += "<option value=\"id\">编号</option>";
		headHtml += "<option value='title'>标题</option>";
		headHtml += "<option value='description'>描述</option>";
		headHtml += " </select>";
		headHtml += "<input id=\"searchWord\" type=\"text\" placeholder=\"Search\">";
		headHtml += "<button class=\"btn\" type=\"button\" style='height:30px;' id='searchBtn'><img src=\"" + rootDir + "images/search.png\" style=\"width: 16px; vertical-align:text-bottom\"></button>";
		headHtml += "</div>";
	}
	
		
	//右部导航菜单
	headHtml += "<div class=\"nav-collapse collapse navbar-inverse-collapse\">";
	headHtml += "<ul class=\"nav pull-right\" style=\"margin-right:20px;\">";
	headHtml += "<li><a href=\"" + rootDir + "editFilter.html\" target=\"_blank\">过滤器</a></li>";
	headHtml += "<li><a href=\"#\">|</a></li>";
	
	//管理员增加系统配置菜单
	if(userInfo && (userInfo.userRole === "super_admin" || userInfo.userRole === "admin")){
		headHtml += "<li><a href=\"" + rootDir + "admin/admin_index.html\" target=\"_blank\">系统配置</a></li>";
		headHtml += "<li><a href=\"#\">|</a></li>";
	}	

	headHtml += "<li><a href=\"" + rootDir + "cplugin/index.jsp\" target=\"_blank\">插件</a></li>";
	headHtml += "<li><a href=\"#\">|</a></li>";
	
	//为统计添加new标识
	headHtml += "<li><a href=\"" + rootDir + "statistic/index.html\" target=\"_blank\">统计";
	//new样式
//	headHtml += "<img src=\"" + rootDir + "images/new.gif\" style=\"margin-top: -15px;\"></a>";
	headHtml += "</li>";
	
	headHtml += "<li><a href=\"#\">|</a></li>";
	
	//更多
	headHtml += "<li class=\"dropdown\">"; 
	headHtml += "<a href='#' class='dropdown-toggle' data-toggle='dropdown'>更多<b class='caret'></b></a>";
	headHtml += "<ul class='dropdown-menu'>";
	headHtml += "<li><a href='" + rootDir + "about.html' target='_blank'>关于</a></li>";
	headHtml += "<li><a href='mailto:cynthiafb@sogou-inc.com&subject=cynthia使用反馈' target='_self'>反馈建议</a></li>";
	headHtml += "</ul>";
	headHtml += "</li>";
	
	//用户头像及信息
	headHtml += "<li class=\"dropdown\">"; 
	headHtml += "<a href='#' class='dropdown-toggle' data-toggle='dropdown' style='padding:6px 0 0 0px;margin-left:10px;'><img class='img-circle' style='width:25px;height:25px;border:2px solid #E7E0E0;' src='" + userPicUrl + "'></a>";
	headHtml += "<div class='dropdown-menu pull-right' style='white-space:nowrap; border-radius: 3px;line-height: 30px;width:250px;padding: 5px 10px 0px 10px;'>";
	
	headHtml += "<div class='row-fluid'>";
	
	headHtml += "<div class='span6'>";
	headHtml += "<img style='border:2px solid #E7E0E0; margin-bottom:10px;' src='" + userPicUrl + "'></a>";
	headHtml += "</div>";
	
	headHtml += "<div class='span6' style='padding: 0px 5px;'>";
	headHtml += "<p title='" + userName + "' style='font-weight:bold;margin:0;overflow:hidden;text-overflow:ellipsis;width:130px;' id='userName'>" + userName + "</p>";
	headHtml += "<p title='" + userMail + "' style='font-weight:bold;margin:0;overflow:hidden;text-overflow:ellipsis;width:130px;' id='userMail'>" + userMail + "</p>";
	headHtml += "<div style='margin-top:15px;'>";
	headHtml += "<a href='javascript:logout();'>退出</a>&nbsp;<a href=" + rootDir + "userInfo/userConfig.html target='_blank'>修改资料</a>";
	headHtml += "</div>";
	headHtml += "</div>";
	
	headHtml += "</div>";
	headHtml += "</li>";
	
	headHtml += "</ul>";
        
	headHtml += "</div>";
	headHtml += "</div>";
	headHtml += "</div>";
	
	$("#header-nav").html(headHtml);

}

$(function(){
	deleteCookie('webRootDir');
	if(judgeNeedHeader())
		addHeadHtml(); //添加头部导航条
	addCnzzStatic(); 
});
	

