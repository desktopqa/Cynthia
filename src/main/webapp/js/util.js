var userPicUrl,  //用户头像地址
	WEB_ROOT_URL = WEB_ROOT_URL || readCookie('webRootDir'),
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
noSearchUrl.push('guide.html');

//不需要头部导航的页面
var noAddHeadUrl = new Array();
noAddHeadUrl.push('login.jsp');
noAddHeadUrl.push('register.jsp');

//全局的AJAX访问，处理AJAX请求时SESSION超时  
if (window.$ && $.ajaxSetup) {
	$.ajaxSetup({  
//		crossDomain : true,
	    xhrFields: {
	        withCredentials: true
	    },
	    beforeSend: function (xhr) {
            var match = window.document.cookie.match(/(?:^|\s|;)XSRF-TOKEN\s*=\s*([^;]+)(?:;|$)/);
            xhr.setRequestHeader("X-XSRF-TOKEN", match && match[1]);
        },
		statusCode: {
			401: function (data) {
				if(window.location.href.indexOf("login.jsp") == -1 ){
					window.location.href = data.responseText; 
				}
	        }
	    }
	});  
}


window.cynthia = {
		version : '1.0' //# cynthia 版本号
		, noop  : function() { //#空函数
			return function(){
				//空函数
			};
		}
		, isArray : Array.isArray || function( array ) {  //# 判断变量 是否为数组
			return '[object Array]' == Object.prototype.toString.call( array );
		}
		, config : {} //用户传入
};

//通用url操作
cynthia.url = { 
		//#URL
		//参数：变量名，url为空则表从当前页面的url中取
		getQuery : function(name, url){
			var u = arguments[1] || window.location.search
				, reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)")
				, r = u.substr(u.indexOf("?")+1).match(reg)
			;
			return r != null ? r[2] : "";
		}
		, getHash : function(name, url){ //# 获取 hash值
			var u = arguments[1] || location.hash;
			var r = u.substr(u.indexOf("#") + 1);
			return r;
		}
		, parse : function(url) { //# 解析URL
			var a =  document.createElement('a');
			url = url || document.location.href;
			a.href = url ;
			return {
				source		: url
				, protocol	: a.protocol.replace(':','')
				, host		: a.hostname
				, port		: a.port
				, query		: a.search
				, file		: (a.pathname.match(/([^\/?#]+)$/i) || [,''])[1]
				, hash		: a.hash.replace('#','')
				, path		: a.pathname.replace(/^([^\/])/,'/$1')
				, relative	: (a.href.match(/tps?:\/\/[^\/]+(.+)/) || [,''])[1]
				, segments	: a.pathname.replace(/^\//,'').split('/')
			};
		}
};

//通用数组操作
cynthia.array = {
		//判断变量是否为数组
		isArray : Array.isArray || function( array ) {  //# 判断变量 是否为数组
			return '[object Array]' == Object.prototype.toString.call( array );
		}

		// inArray, 返回位置！ 不存在则返回 -1；
		,index : function(t, arr){ //# 返回当前值所在数组的位置
			if(arr.indexOf){
				return arr.indexOf(t);
			}
			for(var i = arr.length ; i--; ){
				if(arr[i]===t){
					return i*1;
				}
			};
			return -1;
		}
		
		//返回对象 的 键值！  返回值 类型为数组。
		, getKey : function( data ){ //# 返回对象所有的键值
			var arr = [], k;
			for( k in data) {
				arr.push( k );
			};
			return arr ;
		}
		// max , 数组中最大的项
		, max : function( array ){//#求数组中最大的项
			return Math.max.apply(null, array);
		}
		// min , 数组中最小的项
		, min : function( array ){ //#求数组中最小的项
			return Math.min.apply(null, array);
		}
		// remove ， 移除
		, remove : function( array, value ){ //#移除数组中某值
			var length = array.length;
			while( length-- ){
				if( value === array[ length ] ){
					array.splice(length, 1);
				}
			}
			return array;
		}
		
		//  removeAt ，删除指定位置的 值
		//@index , 索引. 不传递 index ，会删除第一个
		, removeAt : function( array, index ){ //#删除数组中 指定位置的值
			array.splice( index, 1);
			return array;
		}
};


cynthia.browser = { //#浏览器
		browsers : { //# 浏览器内核类别
			weixin		: /micromessenger(\/[\d\.]+)*/   //微信内置浏览器
			, mqq		: /mqqbrowser(\/[\d\.]+)*/       //手机QQ浏览器
			, uc		: /ucbrowser(\/[\d\.]+)*/            //UC浏览器
			, chrome	: /(?:chrome|crios)(\/[\d\.]+)*/  //chrome浏览器
			, firefox	: /firefox(\/[\d\.]+)*/          //火狐浏览器
			, opera		: /opera(\/|\s)([\d\.]+)*/     //欧朋浏览器
			, sougou	: /sogoumobilebrowser(\/[\d\.]+)*/   //搜狗手机浏览器
			, baidu		: /baidubrowser(\/[\d\.]+)*/          //百度手机浏览器
			, 360		: /360browser([\d\.]*)/                         //360浏览器
			, safari	: /safari(\/[\d\.]+)*/		//苹果浏览器
			, ie		: /msie\s([\d\.]+)*/    // ie 浏览器
		}
		//@errCall : 错误回调
		, addFav : function( url, title, errCall){ //#加入收藏夹
			try{
				window.external.addFavorite(url, title);
			}catch(e){
				try{
					window.sidebar.addPanel(title, url, '');
				}catch (e){
					errCall();
				}
			}
		},
		//浏览器版本
		coreInit : function(){ //#noadd
			var i 			= null
				, browsers 	= this.browsers
				, ua		= window.navigator.userAgent.toLowerCase()
				, brower	= ''
				, pos		= 1
			;
			for( i in browsers){
				if( brower = ua.match( browsers[i] ) ){
					if( i == 'opera'){
						pos = 2;
					}else{
						pos = 1;
					}
					this.version = (brower[ pos ] || '').replace(/[\/\s]+/, '');
					this.core = i ;
					return i;
				}
			}
		}
		// 检测IE版本 ！仅支持IE:  5,6,7,8,9 版本
		, ie : (function(){ //# 检测IE版本 ！仅支: ie5,6,7,8,9
			var v = 3, div = document.createElement('div'), all = div.getElementsByTagName('i');
			while (
				div.innerHTML = '<!--[if gt IE ' + (++v) + ']><i></i><![endif]-->',
					all[0]
				);
			return v > 4 ? v : false ;
		})()
		, isWebkit : /webkit/i.test(navigator.userAgent)
		, version : 0
		, core	: ''

};

cynthia.date = {
        format: function (formatType, time, weeks) { //格式化输出时间
            var pre = '0';
            var formatType = formatType || 'YYYY-MM-DD';
            //格式化时间
            var weeks = weeks || '日一二三四五六';
            var time = time || new Date();
            return (formatType || '')
                .replace(/yyyy|YYYY/g, time.getFullYear())
                .replace(/yy|YY/g, cynthia.string.addPre(pre, time.getFullYear() % 100), 2)
                .replace(/mm|MM/g, cynthia.string.addPre(pre, time.getMonth() + 1, 2))
                .replace(/m|M/g, time.getMonth() + 1)
                .replace(/dd|DD/g, cynthia.string.addPre(pre, time.getDate(), 2))
                .replace(/d|D/g, time.getDate())
                .replace(/hh|HH/g, cynthia.string.addPre(pre, time.getHours(), 2))
                .replace(/h|H/g, time.getHours())
                .replace(/ii|II/g, cynthia.string.addPre(pre, time.getMinutes(), 2))
                .replace(/i|I/g, time.getMinutes())
                .replace(/ss|SS/g, cynthia.string.addPre(pre, time.getSeconds(), 2))
                .replace(/s|S/g, time.getSeconds())
                .replace(/w/g, time.getDay())
                .replace(/W/g, weeks[time.getDay()])
                ;
        },

        convertTZtime: function (time) {
            time = time || "";
            time = time.replace("T", " ");
            time = time.replace("Z", "");
            return time;
        },

        getLastDayDate : function(day,formatType,date){
            var startTime;
            if(date){
                startTime = new Date(date).getTime();
            }else{
                startTime = new Date().getTime();
            }
            startTime = startTime - 1000 * 60 * 60 * 24 * parseInt(day);
            formatType = formatType || 'YYYY-mm-dd HH:ii:ss';
            return this.format(formatType, new Date(startTime));
        }
};

//字符串匹配通用
cynthia.regExp = {  
		//是否为 数字！整数，浮点数
		isNum : function( num ){ //# 是否为数组
			return ! isNaN( num ) ;
		}
		, isEmail : function( mail ){//# 是否为 邮箱
			return /^([a-z0-9]+[_\-\.]?)*[a-z0-9]+@([a-z0-9]+[_\-\.]?)*[a-z0-9]+\.[a-z]{2,5}$/i.test( mail );
		}
		, isIdCard : function( card ){ //# 是否为 身份证
			return /^(\d{14}|\d{17})(\d|[xX])$/.test( card );
		}
		, isMobile : function( mobile ){ //# 是否为 手机
			return /^0*1\d{10}$/.test( mobile ) ;
		}
		, isQQ : function( qq ){ //# 是否为 QQ
			return /^[1-9]\d{4,10}$/.test( qq );
		}
		, isTel:function( tel ){ //# 是否为 电话
			return /^\d{3,4}-\d{7,8}(-\d{1,6})?$/.text( tel ) ;
		}
		, isUrl : function( url ){ //# 是否为 URL
			return /https?:\/\/[a-z0-9\.\-]{1,255}\.[0-9a-z\-]{1,255}/i.test( url );
		}
		, isColor : function( color ){ //# 是否为 16进制颜色
			return /#([\da-f]{3}){1,2}$/i.test( color );
		}
		//@id ： 身份证 ，
		// @now : 当前时间 如：new Date('2013/12/12') , '2013/12/12'
		// @age ： 允许的年龄
		, isAdult : function( id, allowAge, now ){ //# 是否年龄是否成年
			var age = 0 // 用户 年月日
				, nowDate = 0  //当前年月日
			;
			allowAge = parseFloat( allowAge ) || 18;
			now = typeof now == 'string' ? new Date( now ) : ( now || new Date() );


			if( ! this.isIdCard( id ) ){
				return false ;
			}
			//15位身份证
			if( 15 == id.length ){
				age = '19'+ id.slice(6, 6);
			}else{
				age = id.slice(6, 14);
			}
			// 类型转换 整型
			age = ~~age;
			nowDate = ~~( julyJs.date.format('YYYYMMDD', now) );
			//比较年龄
			if( nowDate - age < allowAge * 1e4 ){
				return false ;
			}
			return true ;
		}
		//浮点数
		, isFloat : function( num ){ //# 是否为 浮点数
			return /^(([1-9]\d*)|(\d+\.\d+)|0)$/.test( num );
		}
		//正整数
		, isInt : function( num ){ //# 是否为 正整数
			return /^[1-9]\d*$/.test( num );
		}
		//是否全为汉字
		, isChinese : function( str ){ //# 是否全为 汉字
			return /^([\u4E00-\u9FA5]|[\uFE30-\uFFA0])+$/gi.test(str);
		}
};


//# 字符串
cynthia.string = {
		codeHtml : function(content){ //# 转义 HTML 字符
			return this.replace(content, {
				'&'			: "&amp;"
				, '"'		: "&quot;"
				, "'"		: '&#39;'
				, '<'		: "&lt;"
				, '>'		: "&gt;"
				, ' '		: "&nbsp;"
				, '\t'		: "&#09;"
				, '('		: "&#40;"
				, ')'		: "&#41;"
				, '*'		: "&#42;"
				, '+'		: "&#43;"
				, ','		: "&#44;"
				, '-'		: "&#45;"
				, '.'		: "&#46;"
				, '/'		: "&#47;"
				, '?'		: "&#63;"
				, '\\'		: "&#92;"
				, '\n'		: "<br>"
			});
		}, 
		//去除两边空格
		trim : function( text ){ //# 去除两边空格
			return ( text || '' ).replace(/^\s+|\s$/, '');
		}, 
		//字符串替换
		replace : function(str, re){ //# 字符串替换
			str = str || '';
			for(var key in re){
				replace(key,re[key]);
			};
			function replace(a,b){
				var arr = str.split(a);
				str = arr.join(b);
			};
			return str;
		},
		
		//格式化
		format : function(src)
		{
		    if (arguments.length == 0) return null;
		    var args = Array.prototype.slice.call(arguments, 1);
		    return src.replace(/\{(\d+)\}/g, function (m, i) {
		        return args[i];
		    });
		},
		
		//增加前缀
        addPre : function (pre, word, size) { //# 补齐。如给数字前 加 0
            pre = pre || '0';
            size = parseInt(size) || 0;
            word = String(word || '');
            var length = Math.max(0, size - word.length);
            return this.repeat(pre, length, word);
        },
        
        //重复字符串
        repeat : function (word, length, end) {
            end = end || ''; //加在末位
            length = ~~length;
            return new Array(length * 1 + 1).join(word) + '' + end;
        }
};


//其它一些通用方法
cynthia.util = {
		//阻止浏览器默认行为
		stopDefault : function( e ){
		    if ( e && e.preventDefault ){
		    	e.preventDefault(); 
		    }else{
		    	//IE中阻止函数器默认动作的方式 
		    	window.event.returnValue = false; 
		    }
		    return false; 
		},
		//给多选时间字段赋值
		//@timeSelectId: select id
		//@minTime:时间最小值
		//@maxTime：时间最大值
		//@choosedArr：选中的时间
		setTimeValue : function(timeSelectId, minTime, maxTime,choosedArr ){
			var $timeNode = $("#" + timeSelectId);
			$timeNode.empty();
			for(var i = minTime ; i <= maxTime; i ++ )
			{
				if(inArrayIndex(i,choosedArr)>=0)
					$timeNode.append("<option value='"+i+"' selected='selected'>"+i+"</option>");
				else
					$timeNode.append("<option value='"+i+"'>"+i+"</option>");
			}
		},
		
		moveOptions : function ( sourceSelObjId, targetSelObjId ){
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
		},
		
		showLoading : function (isShow)
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
		},

		showInfoWin : function (type, message , time)
		{
			$("#warning-info").remove();
			var info="<div class='alert alert-"+type+" hide' style='position:fixed;top:5px;left:500px;width:30%;height:20px; margin:0 auto; z-index:99999' id='warning-info'>";
			info += "<button type='button' class='close' data-dismiss='alert'>&times;</button>";
			info += "<p id='message'>"+message+"</p>";
			info += "</div>";
			$("body").append(info);
			$("#warning-info").show();
			if(time == undefined)
				window.setTimeout("this.closeInfoWin()", 2000);
			else
				window.setTimeout("this.closeInfoWin()", time);
		},
		
		closeInfoWin : function (type) {  
			$("#warning-info").hide();
		} 
};


//xml通用
cynthia.xml = {
		
		getXMLStr : function(str){
			str = replaceAll(str, "&", "&amp;");
			str = replaceAll(str, "<", "&lt;");
			str = replaceAll(str, ">", "&gt;");
			str = replaceAll(str, "'", "&apos;");
			str = replaceAll(str, "\"", "&quot;");
			return str;
		},

		getNoXMLStr : function(str){
			str = replaceAll(str, "&lt;", "<");
			str = replaceAll(str, "&gt;", ">");
			str = replaceAll(str, "&apos;", "'");
			str = replaceAll(str, "&quot;", "\"");
			str = replaceAll(str, "&amp;", "&");
			return str;
		},
		
		getXMLDoc : function(){
			if(document.implementation && document.implementation.createDocument){
				return document.implementation.createDocument("", "", null);
			}

			if(window.ActiveXObject){
				return new ActiveXObject("Msxml.DOMDocument");
			}
			return null;
		},
		
		getDocXML : function (doc)
		{
			if(doc.xml)
			{
				return doc.xml;
			}
			return new XMLSerializer().serializeToString(doc);
		},
		
		setTextContent : function (node, text)
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
		},

		getTextContent : function (node)
		{
			var value = "";

			for(var i = 0; i < node.childNodes.length; i++)
			{
				if(node.childNodes[i].nodeValue != null)
					value += node.childNodes[i].nodeValue;
			}

			return trim(value);
		}
};


String.format = function (src) 
{
    if (arguments.length == 0) return null;
    var args = Array.prototype.slice.call(arguments, 1);
    return src.replace(/\{(\d+)\}/g, function (m, i) {
        return args[i];
    });
};

//设置时间值
function setTimeValue(timeSelectId, minTime, maxTime,choosedArr)
{
	var $timeNode = $("#" + timeSelectId);
	$timeNode.empty();
	
	for(var i = minTime ; i <= maxTime; i ++ )
	{
		if(inArrayIndex(i,choosedArr)>=0)
			$timeNode.append("<option value='"+i+"' selected='selected'>"+i+"</option>");
		else
			$timeNode.append("<option value='"+i+"'>"+i+"</option>");
	}
}

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

if (!Array.prototype.indexOf) {
	Array.prototype.indexOf = function(obj, start) {
	     for (var i = (start || 0), j = this.length; i < j; i++) {
	         if (this[i] === obj) { return i; }
	     }
	     return -1;
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
		return decodeURIComponent(returnValue);
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
	deleteCookie('id');
	deleteCookie('login_username');
	deleteCookie('login_nickname');
	if(!window.confirm("确定要退出Cynthia吗？"))
		return;
	window.location = base_url + 'user/logout.do?isReturn=true&targetUrl=' + encodeURIComponent(window.location.href);
}

/**
 * 返回根目录路径,方便不同路径下的ajax请求
 * @returns {String}
 */
function getRootDir(){
	var contextPath='',count,webBaseUrl = WEB_ROOT_URL , webBaseCount;
	var pathName = document.location.pathname;
    pathName = pathName.substr(1);
    //求pathName中 / 的个数 多少个则向上返回多少级；
    count = pathName.replace(/[^\/]/g,'').length;
    webBaseUrl = trim(webBaseUrl).substring(8);
    webBaseCount = webBaseUrl.replace(/[^\/]/g,'').length - 1;
    //TODO改进
    if(isEffevo())
    	count -= 1;
    else
    	count -= webBaseCount;
    if(count > 0){
    	for(var i = 0; i < count; i++)
    		contextPath += "../";
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
	if(WEB_ROOT_URL) return WEB_ROOT_URL;
	$.ajax({
		url: base_url + 'backRight/getWebRootDir.do',
		success:function(data){
			WEB_ROOT_URL = data;
		}
	});
	return WEB_ROOT_URL;
}

function isEffevo(){
	return ( window.location.href.indexOf('www.effevo.com') != -1 || window.location.href.indexOf('effevo.mt.sogou.com') != -1 );
}

function queryUserInfo(callback)
{
	var userMail = readCookie("login_username");
	$.ajax({
		url: base_url + 'user/getUserInfo.do',
		type:'POST',
		dataType:'json',
		data:{'userMail':userMail,'userId':readCookie("id")},
		success:function(user){
			userPicUrl = user.picUrl || base_url + "images/default_user.png";
			if(callback){
				callback(user);
			}
		}
	});
}

function checkLogin()
{
	addCnzzStatic(); 
	if(window.location.href.indexOf('login.jsp') > 0 || window.location.href.indexOf('register.jsp') > 0) {
		return;
	}
	$.ajax({
		url: base_url + 'user/getUserInfo.do',
		type:'POST',
		dataType:'json',
		data:{userId:readCookie("id"),userMail:readCookie('login_username')},
		success:function(user){
			userPicUrl = user.picUrl || base_url + "images/default_user.png";
			if(user){
				addHeadHtml(user); //添加头部导航条
			}else{
				if(window.location.href.indexOf('/userInfo/login.jsp') == -1 || window.location.href.indexOf('/userInfo/register.jsp') == -1){
					//cookie失效重新跳转到登录页
					var url = base_url + 'user/logout.do?isReturn=false&targetUrl=' + encodeURIComponent( window.location.href );
					window.parent ? window.parent.location.href = url : window.location.href = url;
				}
			}
		}
	});
}

function addHeadHtml(userInfo)
{
	var headHtml = "";
	headHtml += "<div class=\"row-fluid navbar navbar-fixed-top\">";
	headHtml += "<div class=\"container\" style=\"width:100%;\">";
	//图片logo链接
	headHtml += "<a href='" + base_url + "index.html' class='pull-left'><img src='" + base_url + "images/logo.png' style='width:65px;min-width:65px;height:40px;margin-left: 20px;'/></a>"; 
	//专心专注专业
	headHtml += "<a class=\"brand\" href='" + base_url + "index.html' style=\"font-size:14px;color:#666;padding: 10px 10px 0px;margin-left:0px;\">专心、专注、专业</a>";
    
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
		headHtml += "<button class=\"btn\" type=\"button\" style='height:30px;' id='searchBtn'><img src=\"" + base_url + "images/search.png\" style=\"width: 16px; min-width:16px;vertical-align:text-bottom\"></button>";
		headHtml += "</div>";
	}
		
	//右部导航菜单
	headHtml += "<div class=\"nav-collapse collapse navbar-inverse-collapse\">";
	headHtml += "<ul class=\"nav pull-right\" style=\"margin-right:20px;\">";
	headHtml += "<li><a href=\"" + base_url + "editFilter.html\" target=\"_self\">过滤器</a></li>";
	headHtml += "<li><a href=\"#\">|</a></li>";
	
	//管理员增加系统配置菜单
	if(userInfo && (userInfo.userRole === "super_admin" || userInfo.userRole === "admin")){
		headHtml += "<li><a href=\"" + base_url + "admin/admin_index.html\" target=\"_self\">系统配置</a></li>";
		headHtml += "<li><a href=\"#\">|</a></li>";
	}	

	headHtml += "<li><a href=\"" + base_url + "cplugin/index.html\" target=\"_self\">插件</a></li>";
	headHtml += "<li><a href=\"#\">|</a></li>";
	
	//为统计添加new标识
	headHtml += "<li><a href=\"" + base_url + "statistic/index.html\" target=\"_self\">统计";
	//new样式
//		headHtml += "<img src=\"" + base_url + "images/new.gif\" style=\"margin-top: -15px;\"></a>";
	headHtml += "</li>";
	headHtml += "<li><a href=\"#\">|</a></li>";
	
	//更多
	headHtml += "<li class=\"dropdown\">"; 
	headHtml += "<a href='#' class='dropdown-toggle' data-toggle='dropdown'>更多<b class='caret'></b></a>";
	headHtml += "<ul class='dropdown-menu'>";
	headHtml += "<li><a href='" + base_url + "about.html' target='_self'>关于</a></li>";
	headHtml += "<li><a href='mailto:cynthiafb@sogou-inc.com&subject=cynthia使用反馈' target='_self'>反馈建议</a></li>";
	headHtml += "<li><a href='" + base_url + "guide.html?guideId=start' target='_self'>使用说明</a></li>";
	headHtml += "</ul>";
	headHtml += "</li>";
	
	//用户头像及信息
	headHtml += "<li class=\"dropdown\">"; 
	headHtml += "<a href='#' class='dropdown-toggle' data-toggle='dropdown' style='padding:6px 0 0 0px;margin-left:10px;'><img class='img-circle' style='width:25px;height:25px;border:2px solid #E7E0E0;' src='" + userPicUrl + "'></a>";
	headHtml += "<div class='dropdown-menu pull-right' style='white-space:nowrap; border-radius: 3px;line-height: 30px;width:250px;padding: 5px 10px 0px 10px;'>";
	
	headHtml += "<div class='row-fluid'>";
	
	headHtml += "<div class='span6'>";
	headHtml += "<img style='border:2px solid #E7E0E0; height: 100px; width: 100px;margin-bottom:10px;' src='" + userPicUrl + "'></a>";
	headHtml += "</div>";
	
	headHtml += "<div class='span6' style='padding: 0px 5px;'>";
	headHtml += "<p title='" + userInfo.nickName + "' style='font-weight:bold;margin:0;overflow:hidden;text-overflow:ellipsis;width:130px;' id='userName'>" + userInfo.nickName + "</p>";
	headHtml += "<p title='" + userInfo.userName + "' style='font-weight:bold;margin:0;overflow:hidden;text-overflow:ellipsis;width:130px;' id='userMail'>" + userInfo.userName + "</p>";
	headHtml += "<div style='margin-top:15px;'>";
	headHtml += "<a href='javascript:logout();'>退出</a>&nbsp;";
	if(!isEffevo())
		headHtml += "<a class='project_header' href=" + base_url + "userInfo/userConfig.html target='_blank'>修改资料</a>";
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
	checkLogin();
});
	