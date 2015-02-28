(function($)
{
	var a={};
	var rap = null;
	var to_node = null;   //动作连接点
	var from_node = null;
	var global_node={},global_path={}; //全局状态和动作
	//全局画布设置
	var viewBox_x = null;
	var viewBox_y = null;
	var viewBox_width = null;
	var viewBox_height = null;
	var pathBeforeAttr;
	var rectBeforeAttr;
	
	//画动作
	var is_start_line;
	//临时线
	var tmpPath = null;
	var $container = null;   //flow容器
	
	var panZoom = null;
 	a.config={
		editable:true,
		lineHeight:15,
		basePath:"",
		rect:{
			attr:{
				x:10,
				y:10,
				width:100,
				height:50,
				r:5,
				fill:"90-#fff-#C0C0C0",
				stroke:"#000",
				"stroke-width":1
				},
			activeAttr:{
					fill: "#660000", 
					stroke: "#000", 
					"stroke-width": 10, 
					"stroke-opacity": 0.5
				},
			showType:"image&text",
			type:"state",
			name:{
				text:"state",
				"font-style":"italic"
				},
			text:{
				text:"状态",
				"font-size":13,
				cursor:"hand"
			},
			margin:5,
			props:[],
			img:{}
		},
		path:{
			attr:{path:{path:"M10 10L100 100",stroke:"#808080",fill:"none","stroke-width":2},
			arrow:{path:"M10 10L10 10",stroke:"#808080",fill:"#808080","stroke-width":2,radius:5},
			fromDot:{width:15,height:15,stroke:"#fff",fill:"#000",cursor:"move","stroke-width":2},
			toDot:{width:15,height:15,stroke:"#fff",fill:"#000",cursor:"move","stroke-width":2},
			bigDot:{width:15,height:15,stroke:"#fff",fill:"#000",cursor:"move","stroke-width":2},
			smallDot:{width:15,height:15,stroke:"#fff",fill:"#000",cursor:"move","stroke-width":3}},
			text:{text:"{to}","font-size":11, cursor:"hand",background:"#000"},
			
			activeAttr:{
				"stroke-width": 20, 
				"stroke-opacity": 0.5
			},
			
			textPos:{x:0,y:-10},
			props:{text:{name:"text",label:"显示",value:"",editor:function(){return new a.editors.textEditor();}}}
		},
		tools:{
			attr:{left:10,top:0},
			pointer:{},
			path:{},
			states:{},
			save:{
				onclick:function(c){alert(c);}
			}
		},
		
		props:{
			attr:{top:10,right:30},props:{}
		},
		restore:"",
		activeRects:{
			rects:[],
			rectAttr:{stroke:"#ff0000","stroke-width":2}
		},
		
		historyRects:{
			rects:[],
			pathAttr:{
				path:{stroke:"#00ff00"},
				arrow:{stroke:"#00ff00",fill:"#00ff00"}
			}
		}
	};
	
	
	a.util={
		isLine:function(g,f,e){
			var d,c;
			if((g.x-e.x)==0){d=1;}
			else{d=(g.y-e.y)/(g.x-e.x);}
			c=(f.x-e.x)*d+e.y;
			if((f.y-c)<10&&(f.y-c)>-10){
				f.y=c;
				return true;
			}
			return false;
		},
		center:function(d,c){
			return{x:(d.x-c.x)/2+c.x,y:(d.y-c.y)/2+c.y};
		},
		
		connPoint:function(j,d){
			var c=d,e={x:j.x+j.width/2,y:j.y+j.height/2};
			var l=(e.y-c.y)/(e.x-c.x);l=isNaN(l)?0:l;
			var k=j.height/j.width;
			var h=c.y<e.y?-1:1,f=c.x<e.x?-1:1,g,i;
			if(Math.abs(l)>k&&h==-1){
				g=e.y-j.height/2;i=e.x+h*j.height/2/l;
			}else{
				if(Math.abs(l)>k&&h==1){
					g=e.y+j.height/2;i=e.x+h*j.height/2/l;
				}
				else{
					if(Math.abs(l)<k&&f==-1){
						g=e.y+f*j.width/2*l;i=e.x-j.width/2;
					}
					else{
						if(Math.abs(l)<k&&f==1){
							g=e.y+j.width/2*l;i=e.x+j.width/2;
						}
					}
				}
			}
			return{x:i,y:g};
		},
		
		arrow:function(l,k,d){
			var g=Math.atan2(l.y-k.y,k.x-l.x)*(180/Math.PI);
			var h=k.x-d*Math.cos(g*(Math.PI/180));
			var f=k.y+d*Math.sin(g*(Math.PI/180));
			var e=h+d*Math.cos((g+120)*(Math.PI/180));
			var j=f-d*Math.sin((g+120)*(Math.PI/180));
			var c=h+d*Math.cos((g+240)*(Math.PI/180));
			var i=f-d*Math.sin((g+240)*(Math.PI/180));
			return[k,{x:e,y:j},{x:c,y:i}];
		}
	};
	
	//画状态
	a.rect=function(p,m,statId){
		var raphaelId = new Array();
		var u=this;
		var g= "rect_"+statId,
		E=$.extend(true,{},a.config.rect,p),
		C = m,t,e,n,f,x,v;
		
		rap.setStart();
		t=C.rect(E.attr.x,E.attr.y,E.attr.width,E.attr.height,E.attr.r).hide().attr(E.attr);
		
		if(statId === "start")
			t.attr({stroke: '#660000',"stroke-width":2});  
		raphaelId.push(t.id);
		
		//图片
		e = C.image(a.config.basePath+E.img.src,E.attr.x+E.img.width/2,E.attr.y+(E.attr.height-E.img.height)/2,E.img.width,E.img.height).hide();
		n = C.text(E.attr.x+E.img.width+(E.attr.width-E.img.width)/2, E.attr.y+a.config.lineHeight/2,E.name.text).hide().attr(E.name);
		
		//文字
		f=C.text(E.attr.x+E.img.width+(E.attr.width-E.img.width)/2,E.attr.y+(E.attr.height-a.config.lineHeight)/2+a.config.lineHeight,E.text.text).hide()
				.attr({id:g,text:p.attr.name,cursor:"default","font-size":13});
		raphaelId.push(f.id);
		
		var wholeNode = rap.setFinish();
		var over = function () {
            this.c = this.c || this.attr("fill");
            this.stop().animate({fill: "#bacabd"}, 500);
        },
        out = function () {
            this.stop().animate({fill: this.c}, 500);
        };
		wholeNode.hover(over, out);
		
		t.drag(function(r,o){A(r,o);},function(){z();},function(){l();});
		e.drag(function(r,o){A(r,o);},function(){z();},function(){l();});
		n.drag(function(r,o){A(r,o);},function(){z();},function(){l();});
		f.drag(function(r,o){A(r,o);},function(){z();},function(){l();});
	
		//点下鼠标
		var z=function(){
			x= t.attr("x");v=t.attr("y"); //记录初始位置
			t.attr({opacity:0.5});  //半透明
			e.attr({opacity:0.5});
			f.attr({opacity:0.5});
		};
		
		//开始拖动
		var A=function(dx,dy){
			if(!a.config.editable){return;}
			
			var scaleX = rap.width  / rap._viewBox[2];//缩放比例
	        var scaleY = rap.height / rap._viewBox[3];
	        //按缩放比例编移坐标
	        dx /= scaleX;
	        dy /= scaleY;
			var o=(x+dx);
			var G=(v+dy);
			q.x=o-E.margin;
			q.y=G-E.margin;
			B();
		};
		
		//拖动结束
		var l=function(){
			t.attr({opacity:1});
			e.attr({opacity:1});
			f.attr({opacity:1});
			rap.renderfix();
		};
		
		var s,i={},h=5,
		q={x:E.attr.x-E.margin,y:E.attr.y-E.margin,width:E.attr.width+E.margin*2,height:E.attr.height+E.margin*2};
		
		if(p.showType =="image"){
			q.width=60;
			q.height=50;
		}
		
		//s为边界框
		s = C.path("M0 0L1 1").hide();
		
		//宽度长度改变框
		i.t=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"s-resize"}).hide().drag(function(r,o){D(r,o,"t");},
			function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"t");},function(){});
		i.lt=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"nw-resize"}).hide().drag(function(r,o){D(r,o,"lt");},
			function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"lt");},function(){});
		i.l=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"w-resize"}).hide().drag(function(r,o){D(r,o,"l");},
			function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"l");},function(){});
		i.lb=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"sw-resize"}).hide().drag(function(r,o){D(r,o,"lb");},
			function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"lb");},function(){});
		i.b=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"s-resize"}).hide().drag(function(r,o){D(r,o,"b");},
			function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"b");},function(){});
		i.rb=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"se-resize"}).hide().drag(function(r,o){D(r,o,"rb");},
			function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"rb");},function(){});
		i.r=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"w-resize"}).hide().drag(function(r,o){D(r,o,"r");},
			function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"r");},function(){});
		i.rt=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"ne-resize"}).hide().drag(function(r,o){D(r,o,"rt");},
			function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"rt");},function(){}
		);
		
		var D=function(F,r,G){
			if(!a.config.editable){return;}
			var o=_bx+F,H=_by+r;
			
			switch(G){
				case"t":q.height+=q.y-H;q.y=H;break;
				case"lt":q.width+=q.x-o;q.height+=q.y-H;q.x=o;q.y=H;break;
				case"l":q.width+=q.x-o;q.x=o;break;
				case"lb":q.height=H-q.y;q.width+=q.x-o;q.x=o;break;
				case"b":q.height=H-q.y;break;
				case"rb":q.height=H-q.y;q.width=o-q.x;break;
				case"r":q.width=o-q.x;break;
				case"rt":q.width=o-q.x;q.height+=q.y-H;q.y=H;break;
			}
			B();
		};
		
		var k=function(r,o,F){_bx=r;_by=o;};
		
		//鼠标单击执行
		var clickFunction = function(){
			if(to_node == null){
				from_node = u;
				to_node=u;
			}else{
				from_node=to_node;
				to_node=u;
			}
			if(!a.config.editable){return;}
			w();
			var o=$(C).data("mod");
			switch(o){
			case"pointer":break;
			case"path":
				if(from_node&&to_node&&from_node.getId()!=to_node.getId()&&from_node.getId().substring(0,4)=="rect")
				{
					
					is_start_line = false;
					if(to_node.getType() != "state"){
						showInfoWin("error","不能回到开始状态!");
						from_node = null;
						to_node = null;	
					}else{
						//图上绘制
						if(checkActionExist(from_node,to_node)){ //判断起点和终点是否存在动作
							showInfoWin("error","状态之间己存在动作!");
							from_node.hideEdit();
							to_node.hideEdit();
							from_node = null;
							to_node = null;
							return;
						}
						
						hideStatDiv(true);
						showEditActionDiv();
					}
				}else {
					is_start_line = true;
				}
				break;
			}
			$(C).trigger("click",u);
			$(C).data("currNode",u);
			return false;
		};
		
		//定义setTimeout执行方法
		var TimeFn = null;

		wholeNode.click(function(e){
			// 取消上次延时未执行的方法
		    clearTimeout(TimeFn);
		    //执行延时
		    TimeFn = setTimeout(function(){
		    	clickFunction(e);
		    },300);
		});
		
		wholeNode.dblclick(function(e){
		    //取消单击事件
		    clearTimeout(TimeFn);
		    $(C).trigger("click",u);
			$(C).data("currNode",u);
		    editElement();
		});
		
		var j=function(o,r){
			if(!a.config.editable){return;}
			if(r.getId && r.getId()==g){
//				clickFunction();
			}
			else{
				d();
			}
		};
		
		$(C).bind("click",j);
		
		function y()
		{
			var a = "M"+q.x+" "+q.y+"L"+q.x+" "+(q.y+q.height)+"L"+(q.x+q.width)+" "+(q.y+q.height)+"L"+(q.x+q.width)+" "+q.y+"L"+q.x+" "+q.y;
			return a;
		}
		
		//显示边界框
		function w(){
			s.show();
			for(var o in i){
				i[o].show();
			}
		}
		
		//隐藏边界框
		function d(){
			s.hide();
			for(var o in i){
				i[o].hide();
			}
		}
		
		function B(){
			
			var F=q.x+E.margin,r=q.y+E.margin,G=q.width-E.margin*2,o=q.height-E.margin*2;
			t.attr({x:F,y:r,width:G,height:o});
			switch(E.showType){
				case"image":e.attr({x:F+(G-E.img.width)/2,y:r+(o-E.img.height)/2}).show();break;
				case"text": t.show();f.attr({x:F+G/2,y:r+o/2}).show();break;
				case"image&text":t.show();n.attr({x:F+E.img.width+(G-E.img.width)/2,y:r+a.config.lineHeight/2}).show();
				f.attr({x:F+E.img.width+(G-E.img.width)/2,y:r+(o-a.config.lineHeight)/2+a.config.lineHeight}).show();
				e.attr({x:F+E.img.width/2,y:r+(o-E.img.height)/2}).show();break;
			}
			
			i.t.attr({x:q.x+q.width/2-h/2,y:q.y-h/2});
			i.lt.attr({x:q.x-h/2,y:q.y-h/2});
			i.l.attr({x:q.x-h/2,y:q.y-h/2+q.height/2});
			i.lb.attr({x:q.x-h/2,y:q.y-h/2+q.height});
			i.b.attr({x:q.x-h/2+q.width/2,y:q.y-h/2+q.height});
			i.rb.attr({x:q.x-h/2+q.width,y:q.y-h/2+q.height});
			i.r.attr({x:q.x-h/2+q.width,y:q.y-h/2+q.height/2});
			i.rt.attr({x:q.x-h/2+q.width,y:q.y-h/2});
			s.attr({path:y()});  //绘制边界框
			$(C).trigger("rectresize",u);
		}
		
		this.hideEdit=function(){
			d();
		};
		
		this.toJson=function(){
			var r="{type:'"+E.type+"',text:{text:'"+f.attr("text")+"'}, attr:{ x:"+Math.round(t.attr("x"))+", y:"+Math.round(t.attr("y"))+", width:"+Math.round(t.attr("width"))+", height:"+Math.round(t.attr("height"))+"}";
			if(r.substring(r.length-1,r.length)==","){
				r=r.substring(0,r.length-1);
			}
			r+="}";
			return r;
		};
		
		this.twinkle = function(){
			if(!rectBeforeAttr){
				rectBeforeAttr = {
					fill:t.attr("fill"),
					stroke:t.attr("stroke"),
					"stroke-width":t.attr("stroke-width"),
					"stroke-opacity":t.attr("stroke-opacity")
				};
			}
			
			t.stop().animate(a.config.rect.activeAttr , 200, function(){
				t.stop().animate(rectBeforeAttr, 500, "bounce");
			});
		};
		
		//返回raphaelid
		this.getRaphaelId = function(){
			return raphaelId;
		};
		
		//改变名字
		this.changeText = function(text){
			f.attr({text:text});
		};
		
		//从json中恢复
		this.restore=function(o){
			var r=o;
			E=$.extend(true,E,o);
			f.attr({text:r.text.text});
			B();
		};
		
		//图形范围
		this.getBBox=function(){
			return q;
		};
		
		this.getId=function(){
			return g;
		};
		
		this.getType=function(){
			return E.type;
		};
		
		this.remove=function(){
				t.remove();
				f.remove();
				n.remove();
				e.remove();
				s.remove();
				for(var o in i){
					i[o].remove();
				}
		};
		
		this.text=function(){
			return f.attr("text");
		};
		
		this.attr=function(o){
			if(o){
				t.attr(o);
			}
		};
		
		B();
	};

	//画动作
	a.path=function(q,n,u,e,actionName,actionId){
		var v=this,z=n,x,
		B=$.extend(true,{},a.config.path),i,t,f,h=B.textPos,k=u,s=e,g="path_"+actionId;
		
		var raphaelId = new Array();
		function p(G,H,D,L){
			var F=this,M=G,r,o=D,O=L,K,I,N=H;
			switch(M){
				case"from":r=z.rect(H.x-B.attr.fromDot.width/2,H.y-B.attr.fromDot.height/2,B.attr.fromDot.width,B.attr.fromDot.height).attr(B.attr.fromDot);break;
				case"big":r=z.rect(H.x-B.attr.bigDot.width/2,H.y-B.attr.bigDot.height/2,B.attr.bigDot.width,B.attr.bigDot.height).attr(B.attr.bigDot);break;
				case"small":r=z.rect(H.x-B.attr.smallDot.width/2,H.y-B.attr.smallDot.height/2,B.attr.smallDot.width,B.attr.smallDot.height).attr(B.attr.smallDot);break;
				case"to":r=z.rect(H.x-B.attr.toDot.width/2,H.y-B.attr.toDot.height/2,B.attr.toDot.width,B.attr.toDot.height).attr(B.attr.toDot);break;
			}
			
			//小点的拖动
			if(r&&(M=="big"||M=="small")){
				r.drag(function(Q,P){
						C(Q,P);
				       },
					   function(){J();}
					  );
			}
				
			//中点的拖动
			this.drag = function(dx,dy){
				J();
				C(dx,dy);
			};	
			
			var C=function(R,Q){
				var P=(K+R),S=(I+Q);F.moveTo(P,S);
			};
			
			var J=function(){
				if(M=="big"){K=r.attr("x")+B.attr.bigDot.width/2;I=r.attr("y")+B.attr.bigDot.height/2;}
				if(M=="small"){K=r.attr("x")+B.attr.smallDot.width/2;I=r.attr("y")+B.attr.smallDot.height/2;}
			};
			
			this.type=function(P){
				if(P){M=P;}
				else{return M;}
			};
			
			this.node=function(P){
				if(P){r=P;}
				else{return r;}
			};
			
			this.left=function(P){
				if(P){o=P;}
				else{return o;}
			};
			
			this.right=function(P){
				if(P){O=P;}
				else{return O;}
			};
			
			this.remove=function(){
				o=null;O=null;r.remove();
			};
			
			this.pos=function(P){
				if(P){
					N=P;
					r.attr({x:N.x-r.attr("width")/2,y:N.y-r.attr("height")/2});
					return this;
				}
				else{return N;}
			};
			
			this.moveTo=function(Q,T){
				this.pos({x:Q,y:T});
				switch(M){
						case"from":if(O&&O.right()&&O.right().type()=="to"){O.right().pos(a.util.connPoint(s.getBBox(),N));}if(O&&O.right()){O.pos(a.util.center(N,O.right().pos()));}break;
						case"big":if(O&&O.right()&&O.right().type()=="to"){O.right().pos(a.util.connPoint(s.getBBox(),N));}if(o&&o.left()&&o.left().type()=="from"){o.left().pos(a.util.connPoint(k.getBBox(),N));}if(O&&O.right()){O.pos(a.util.center(N,O.right().pos()));}if(o&&o.left()){o.pos(a.util.center(N,o.left().pos()));}var S={x:N.x,y:N.y};if(a.util.isLine(o.left().pos(),S,O.right().pos())){M="small";r.attr(B.attr.smallDot);this.pos(S);var P=o;o.left().right(o.right());o=o.left();P.remove();var R=O;O.right().left(O.left());O=O.right();R.remove();}break;
						case"small":if(o&&O&&!a.util.isLine(o.pos(),{x:N.x,y:N.y},O.pos())){M="big";r.attr(B.attr.bigDot);var P=new p("small",a.util.center(o.pos(),N),o,o.right());o.right(P);o=P;var R=new p("small",a.util.center(O.pos(),N),O.left(),O);O.left(R);O=R;}break;
						case"to":if(o&&o.left()&&o.left().type()=="from"){o.left().pos(a.util.connPoint(k.getBBox(),N));}if(o&&o.left()){o.pos(a.util.center(N,o.left().pos()));}break;
					}
				m();
			};
		}
		
		//所有拖动小点
		function j(){
			var D,C,E=k.getBBox(),F=s.getBBox(),r,o;
			r=a.util.connPoint(E,{x:F.x+F.width/2,y:F.y+F.height/2});
			o=a.util.connPoint(F,r);
			var midDot = new p("small",{x:(r.x+o.x)/2,y:(r.y+o.y)/2});
			D=new p("from",r, null, midDot);
			D.right().left(D);
			C=new p("to",o,D.right(),null);
			D.right().right(C);
			
			this.toPathString=function(){
				if(!D){return"";}var J=D,I="M"+J.pos().x+" "+J.pos().y,H="";
				while(J.right()){
					J=J.right();I+="L"+J.pos().x+" "+J.pos().y;
				}
				var G=a.util.arrow(J.left().pos(),J.pos(),B.attr.arrow.radius);
				H="M"+G[0].x+" "+G[0].y+"L"+G[1].x+" "+G[1].y+"L"+G[2].x+" "+G[2].y+"z";
				return[I,H];
			};
			
			this.toJson=function(){
				var G="[",H=D;
				while(H){
					if(H.type()=="big"){
						G+="{x:"+Math.round(H.pos().x)+",y:"+Math.round(H.pos().y)+"},";
					}
					H=H.right();
				}
				
				if(G.substring(G.length-1,G.length)==","){
					G=G.substring(0,G.length-1);
				}
				G+="]";
				return G;
			};
			
			this.restore=function(H){
				var I=H,J=D.right();
				for(var G=0;G<I.length;G++){
					J.moveTo(I[G].x,I[G].y);
					J.moveTo(I[G].x,I[G].y);
					J=J.right();}this.hide();
			};
			
			this.fromDot=function(){return D;};
			
			this.toDot=function(){return C;};
			
			this.midDot=function(){
				var H=D.right(),G=D.right().right();
				while(G.right()&&G.right().right()){
					G=G.right().right();
					H=H.right();
				}
				return H;
			};
			
			this.show=function(){
				var G=D;
				while(G){
					G.node().show();
					G=G.right();
				}
			};
			
			this.hide=function(){
				var G=D;
				while(G){
					G.node().hide();
					G=G.right();
					}
			};
			
			this.remove=function(){
				var G=D;
				while(G){
					if(G.right()){
						G=G.right();
						G.left().remove();
					}else{
						G.remove();G=null;
					}
				}
			};
			this.getActionMidDot=function(){
				return midDot;
			};
		}
		
		B=$.extend(true,B,q);
		
		rap.setStart();
		i=z.path(B.attr.path.path).attr(B.attr.path);  //直线
		raphaelId.push(i.id); 
		t=z.path(B.attr.arrow.path).attr(B.attr.arrow); //箭头
		var wholePath = rap.setFinish();
		
		x=new j();  //小点
		x.hide();
		//动作名字显示
		f=z.text(0,0,B.text.text).attr(B.text).attr({text:actionName});
		
//		f.drag(
//			function(r,o){
//				if(!a.config.editable){return;}
//				f.attr({x:y+r,y:w+o});
//			},
//			function(){y=f.attr("x");w=f.attr("y");},
//			function(){var o=x.midDot().pos();h={x:f.attr("x")-o.x,y:f.attr("y")-o.y};}
//		);
		
		//单击动作名称编辑
		f.click(function(){
			if(!a.config.editable){return;}
			var actionId = v.getId().split("_")[1];
			showEditActionDiv(actionId,v.text());
		});
		
		m();
		
		var clickEvent = function(r,C){
			if(!a.config.editable){return;}
			if(C&& C.getId && C.getId()==g){
				x.show();  //显示小点
			}else{
				x.hide();
			}
			
			var o=$(z).data("mod");
			switch(o){
				case"pointer":break;
				case"path":break;
			}
			return false;
		};
		
		var clickFunction = function(){
			if(!a.config.editable){return;}
			$(z).trigger("click",v);
			$(z).data("currNode",v);
			return false;
		};
		
		//定义setTimeout执行方法
		var TimeFn = null;

		wholePath.click(function(e){
			// 取消上次延时未执行的方法
		    clearTimeout(TimeFn);
		    //执行延时
		    TimeFn = setTimeout(function(){
		    	clickFunction();
		    },300);
		});
		
		wholePath.dblclick(function(e){
			//取消单击事件
		    clearTimeout(TimeFn);
		    $(rap).trigger("click",v);
			$(rap).data("currNode",v);
			editElement();
		});
		
		$(z).bind("click",clickEvent);
		
		var A=function(o,r){
			if(!a.config.editable){return;}
			if(r&&(r.getId()==k.getId()||r.getId()==s.getId()))
			{
				$(z).trigger("removepath",v);
			}
		};
		
		$(z).bind("removerect",A);
		var d=function(C,D){
			if(!a.config.editable){return;}
			if(k&&k.getId()==D.getId()){
				var o;
				if(x.fromDot().right().right().type()=="to"){
						o={x:s.getBBox().x+s.getBBox().width/2,y:s.getBBox().y+s.getBBox().height/2};
				}
				else{
					o=x.fromDot().right().right().pos();
				}
				var r=a.util.connPoint(k.getBBox(),o);
				x.fromDot().moveTo(r.x,r.y);
				m();
			}
			
			if(s&&s.getId()==D.getId()){
				var o;
				if(x.toDot().left().left().type()=="from"){
					o={x:k.getBBox().x+k.getBBox().width/2,y:k.getBBox().y+k.getBBox().height/2};
				}
				else{
					o=x.toDot().left().left().pos();
				}
				var r=a.util.connPoint(s.getBBox(),o);
				x.toDot().moveTo(r.x,r.y);
				m();
			}
		};
			
		$(z).bind("rectresize",d);
		$(z).bind("textchange",c);
		
		var c=function(r,o,C){
			if(C.getId()==g){
				f.attr({text:o});
			}
		};
		
		//返回raphaelid
		this.getRaphaelId = function(){
			return raphaelId;
		};
		
		this.changeText = function(text){
			f.attr({text:text});
		};
		
		this.getType = function(){return "path";};
		
		this.midDot = function(){
			return x.midDot();
		};
		
		this.fromDot = function(){
			return x.fromDot();
		};
		
		this.toDot = function(){
			return x.toDot();
		};
		
		this.from=function(){return k;};
		this.to=function(){return s;};
		
		this.toJson=function(){
			var r="{from:'"+k.getId()+"',to:'"+s.getId()+"', dots:"+x.toJson()+",text:{text:'"+f.attr("text")+"'},textPos:{x:"+Math.round(h.x)+",y:"+Math.round(h.y)+"}";
			if(r.substring(r.length-1,r.length)==","){
				r=r.substring(0,r.length-1);
			}
			r+="}";
			return r;
		};
		
		this.hideEdit = function(){
			x.hide();
		};
		
		this.restore=function(o){
			var r=o;B=$.extend(true,B,o);x.restore(r.dots);
		};
		
		this.remove=function(){
				x.remove();i.remove();t.remove();f.remove();
				try{$(z).unbind("click",l);}catch(o){}
				try{$(z).unbind("removerect",A);}catch(o){}
				try{$(z).unbind("rectresize",d);}catch(o){}
				try{$(z).unbind("textchange",c);}catch(o){}
		};
		
		this.twinkle = function(){
			if(!pathBeforeAttr){
				pathBeforeAttr = {
					"stroke-width":t.attr("stroke-width"),
					"stroke-opacity":t.attr("stroke-opacity")
				};
			}
			
			wholePath.stop().animate(a.config.path.activeAttr , 200, function(){
				wholePath.stop().animate(pathBeforeAttr, 500, "bounce");
			});
		};
		
		function m(){
			var r=x.toPathString(),o=x.midDot().pos();
			i.attr({path:r[0]});
			t.attr({path:r[1]});
			f.attr({x:o.x+h.x,y:o.y+h.y});  //动作位置
		}
		
		this.getId=function(){return g;};
		
		this.text=function(){return f.attr("text");};
		
		this.attr=function(o){
			if(o&&o.path){i.attr(o.path);}
			if(o&&o.arrow){t.attr(o.arrow);}
		};
	};//end a.path
	
	a.init=function(x,r){
		var svg_id = x;
		$container = $("#" + svg_id);
		if(rap == null)
			rap=Raphael(svg_id,$container.width(),$container.height());
		
		rap.safari();  //强制safari渲染
		rap.renderfix();
		
		//从保存的svgCode中恢复流程图
		if(r.restore){
			var B=r.restore;
			var z={};
			if(B.states){
				for(var s in B.states){
					var stat = new a.rect($.extend(true,{},a.config.tools.states[B.states[s].type],B.states[s]),rap,s.split("_")[1]);
					stat.restore(B.states[s]);
					z[s]=stat;
					global_node[stat.getId()]=stat;
				}
			}
			if(B.paths){
				for(var s in B.paths){
					var action=new a.path($.extend(true,{},a.config.tools.path,B.paths[s]),rap,z[B.paths[s].from],z[B.paths[s].to],B.paths[s].text.text,s.split("_")[1]);
					action.restore(B.paths[s]);
					global_path[action.getId()]= action;
				}
			}
			//画布设置
			viewBox_x = B.viewBox_x;
			viewBox_y = B.viewBox_y;
			viewBox_width = B.viewBox_width;
			viewBox_height = B.viewBox_height;
			panZoom = B.panZoom;
		}
		panZoom = rap.panzoom({minZoom:-10, maxZoom:5, initialZoom: panZoom, initialPosition: { x: viewBox_x, y: viewBox_y} });
		
		setMouseWheel($container.width(),$container.height());  //设置鼠标滚动
		bindEvent();
		$(rap).css("cursor","default");
		$.extend(true,a.config,r);
		
//		a.config.editable = r.edit || cynthia.url.getQuery('editable') != 'false';   //设置能否编辑
		a.config.editable = cynthia.url.getQuery('editable') != 'false';   //设置能否编辑

		$(rap).data("mod","point"); //设置当前功能键为选择
		
		var showStatus = cynthia.url.getQuery('statusId');
		if(showStatus){
			var statusNode = getNodeById(showStatus);
			if(statusNode){
				statusNode.attr({fill:'rgb(194, 138, 138)',stoke:'#EF8',"stroke-width":6,"stroke-opacity": 0.8});
			}
		}
		
		$("#myflow_tools .node").hover(
			function(){$(this).addClass("mover");},
			function(){$(this).removeClass("mover");}
		);
		
		$("#myflow_tools .selectable").click(function(){
			var choosedNode = $(rap).data("currNode");
			if(choosedNode){
				choosedNode.hideEdit();
			}
			$(rap).data("currNode",null);  
			from_node=null;
			to_node=null;
			$(".selected").removeClass("selected");
			$(this).addClass("selected");
			$(rap).data("mod",this.id);
			clearDrawLine();
			return false;
		});
		
		$("#pointer").click();
		
		var A=a.config.historyRects,l=a.config.activeRects;
		
		if(A.rects.length||l.rects.length){
			var z={};
			for(var h in g){
				if(!z[g[h].from().text()]){
					z[g[h].from().text()]={rect:g[h].from(),paths:{}};
				}
				z[g[h].from().text()].paths[g[h].text()]=g[h];
				if(!z[g[h].to().text()]){
					z[g[h].to().text()]={rect:g[h].to(),paths:{}};
				}
			}
			
			for(var u=0;u<A.rects.length;u++){
				if(z[A.rects[u].name]){
					z[A.rects[u].name].rect.attr(A.rectAttr);
				}
				for(var t=0;t<A.rects[u].paths.length;t++){
					if(z[A.rects[u].name].paths[A.rects[u].paths[t]]){
						z[A.rects[u].name].paths[A.rects[u].paths[t]].attr(A.pathAttr);
					}
				}
			}
			
			for(var u=0;u<l.rects.length;u++){
				if(z[l.rects[u].name]){
					z[l.rects[u].name].rect.attr(l.rectAttr);
				}
				for(var t=0;t<l.rects[u].paths.length;t++){
					if(z[l.rects[u].name].paths[l.rects[u].paths[t]]){
						z[l.rects[u].name].paths[l.rects[u].paths[t]].attr(l.pathAttr);
					}
				}
			}
		}
	};
	
	//清除动作移动连线
	function clearDrawLine()
	{
		is_start_line = false;
		if(tmpPath)
			tmpPath.remove();
	}
	
	//设置鼠标滚动调整画布大小
	function setMouseWheel(width,heigth){
		
		if(viewBox_x == null && viewBox_y == null && viewBox_width == null && viewBox_height == null){
			viewBox_x = 0;
			viewBox_y = 0;
			viewBox_width = width;
			viewBox_height = heigth;
		}
		
		rap.setViewBox(viewBox_x, viewBox_y, viewBox_width, viewBox_height,true);
		
		//缩放处理
	    function handle(delta) {
	        vBHo = viewBox_height;
	        vBWo = viewBox_width;
	        if (delta < 0) {
	        	panZoom.zoomOut(1);
	        }
	        else {
	        	panZoom.zoomIn(1);
	        }
	    }
	    
	    function wheel(event){
	        var delta = 0;
	        if (!event) 
	            event = parent.window.event;
	        if (event.wheelDelta) { /* IE/Opera. */
	            delta = event.wheelDelta/120;
	        } 
	        
	        if (delta)
	            handle(delta);
	        if (event.preventDefault)
	            event.preventDefault();
	        event.returnValue = false;
	    }
	    if (parent.window.addEventListener)
	    	parent.window.addEventListener('DOMMouseScroll', wheel, false);
	}
	
	//绑定事件
	function bindEvent(){
		bindKeyDown();
		bindClick();
		bindMouseMove();
	}
	
	//画动作时移动鼠标
	function bindMouseMove() {
		
		if (document.addEventListener) {
		    document.addEventListener("mousemove", global_mousemove, false);
		    document.addEventListener("mouseup", global_mouseup, false);
		} else {
		    document.attachEvent('onmousemove', global_mousemove);
		    document.attachEvent('onmouseup', global_mouseup);
		} 
	}
	
	function global_mousemove(event){
		var coords,fromPoint,toPoint,path,G,arrow;
		if(is_start_line == true){
			if(tmpPath != null)
				tmpPath.remove();
			
			coords = mouseCoordsConvert(event);
			var clickNode = rap.getElementByPoint(event.pageX,event.pageY);
			
			toPoint = {x:coords.x, y:coords.y}; //减去一点避免点击时老晌应箭头而不响应节点的click事件
			fromPoint = a.util.connPoint(from_node.getBBox(),toPoint);  
			
			if(clickNode){
				var rect = getNodeByRapealId(clickNode.id);
				if(rect){
					//直线，保证从矩形框的边沿画线，得到矩形画线的边沿点
					toPoint = a.util.connPoint(rect.getBBox(),from_node.getBBox());
				}
			}
			
			path = "M"+fromPoint.x+","+fromPoint.y+"L" + toPoint.x + "," + toPoint.y;
			//箭头
			G=a.util.arrow(fromPoint,toPoint,5);
			
			arrow ="M"+G[0].x+" "+G[0].y+"L"+G[1].x+" "+G[1].y+"L"+G[2].x+" "+G[2].y+"z";
			path += arrow;
			
			tmpPath = rap.path(path).attr({stroke:"#808080",fill:"#808080","stroke-width":2});
		}
	}
	

	//全局鼠标弹起
	function global_mouseup(e)
	{
		var clickNode = rap.getElementByPoint(e.pageX,e.pageY);
		if(clickNode != null && from_node != null){
		}else{
			clearDrawLine();
		}
	}
	
	function bindKeyDown(){
		//del删除
		$(document).keydown(function(i){
			//删除
			if(i.keyCode==46){
				deleteElement();
				return false;
			}
		});
		
		//ctrl +s 保存
		$(document).bind('keydown', 'ctrl+s', function (e) {
	        if (e.ctrlKey && (e.which == 83)) {
	            e.preventDefault();
	            if(saveSvgCode())
	            	showInfoWin("success","保存成功!");
	            else
	            	showInfoWin("error","保存失败!");
	            return false;
	        }
	    });
	}
	
	//缩放后坐标转化
	function mouseCoordsConvert(mouseEvent){
		var x  = 0;
        var y  = 0;
        var scaleX = rap.width  / rap._viewBox[2];//缩放比例
        var scaleY = rap.height / rap._viewBox[3];
        
//        // Chrome & Safari
//        if (jQuery.browser.webkit){ 
//        	x = mouseEvent.offsetX; 
//        	y = mouseEvent.offsetY;
//        } 
//        // Firefox
//        if (jQuery.browser.mozilla) 
//        {
//        	//由于firefox不支持offsetX/Y,需手动计算,减去父级窗口位置
//        	var offSetDom = mouseEvent.currentTarget.offsetParent;
//        	x = mouseEvent.pageX - offSetDom.offsetLeft;  
//        	y = mouseEvent.pageY - offSetDom.offsetTop;  
//        } 
//        // IE
//        if (jQuery.browser.msie){
//        	x = mouseEvent.x;
//        	y = mouseEvent.y;
//        } 
//       
//        if(mouseEvent.target.localName === "tspan"){
//        	var topFlowOffset = $('#' + svg_id).offset();
//        	x = mouseEvent.pageX - topFlowOffset.left;  
//        	y = mouseEvent.pageY - topFlowOffset.top;  
//        }
        
        
        var topFlowOffset = $container.offset();
    	x = mouseEvent.pageX - topFlowOffset.left;  
    	y = mouseEvent.pageY - topFlowOffset.top;  
    	
        x = (x/scaleX) + rap._viewBox[0];
        y = (y/scaleY) + rap._viewBox[1];
        
        return { x: x, y: y };
	}
	
	
	function bindClick()
	{
		$container.click(function(e){
			
			//点击空白处取消选中
			var choosedNode = $(rap).data("currNode");
			if(choosedNode){
				choosedNode.hideEdit();
			}
			
			$(rap).data("currNode",null);
			var clickNode = rap.getElementByPoint(e.pageX,e.pageY);
			
			if(!clickNode){
				clearDrawLine();
				//点击新建状态
				var coords = mouseCoordsConvert(e);
				if($(rap).data("mod") == "start"){
					$(rap).drawStat(coords.x,coords.y,'开始','start');
				}
				else if($(rap).data("mod") == "state"){ //添加状态
					$("#myModalStateTag").text('新建状态');
					$("#statId").val('');
					$("#mouse_x").val(coords.x);
					$("#mouse_y").val(coords.y);
					$("#statName").val('');
					$('#cfgStateDiv').modal('show');
				}else{
					//选择范围内的元素,增加path选中的灵敏度
					for(var i = -5 ; i < 5 ; i +=1){
						for(var j = -5 ; j < 5 ; j +=1){
							var node = rap.getElementByPoint((e.pageX+i),(e.pageY+j));
							if(node != null && node.type=="path"){
								for(var m in global_path){
									if(arrayContainsString(global_path[m].getRaphaelId(),node.id)){
										$(rap).trigger("click",global_path[m]);
										$(rap).data("currNode",global_path[m]);
										return;
									}
								}
							}
						}
					}
				}
			}else{
				if(clickNode.id){
					var node = getNodeByRapealId(clickNode.id);
					if(!node)
						clearDrawLine();
				}
			}
		});
		
		//保存
		$("#myflow_save").click(function(){
			if(!a.config.editable){return false;}
			if(saveSvgCode())
            	showInfoWin("success","保存成功!");
            else
            	showInfoWin("error","保存失败!");
		});
			
		//编辑
		$("#myflow_edit").click(function(){
			if(!a.config.editable){return false;}
			editElement();
		});
		
		//删除
		$("#myflow_delete").click(function(){
			if(!a.config.editable){return false;}
			deleteElement();
		});
		
		//放大
		$("#mapControls #up").click(function (e) {
	        panZoom.zoomIn(1);
	        e.preventDefault();
	    });
		
		//缩小
		$("#mapControls #down").click(function (e) {
	        panZoom.zoomOut(1);
	        e.preventDefault();
	    });

		//动作 编辑
	    $("#edit").click(function (e) {
	    	if(!a.config.editable){return false;}
	    	var actionId = "48";
	    	var actionName = "编辑";
	    	editNormalAction(actionId,actionName);
	    });
	    
	    //动作 查看
		$("#look").click(function (e) {
			if(!a.config.editable){return false;}
			var actionId = "47";
			var actionName = "查看";
	    	editNormalAction(actionId,actionName);
	    });

		//动作 删除
	    $("#del").click(function (e) {
	    	if(!a.config.editable){return false;}
	    	var actionId = "51";
	    	var actionName = "删除";
	    	editNormalAction(actionId,actionName);
	    });
	}
	
	function editNormalAction(actionId,actionName)
	{
		showEditActionDiv(actionId,actionName);
	}
	
	//编辑元素
	function editElement()
	{
		if(!a.config.editable){return;}
		var choosedNode=$(rap).data("currNode");
		if(choosedNode){
			if(choosedNode.getId().substring(0,4)=="rect")
			{
				//状态
				if(choosedNode.getType() == "start"){
					showInfoWin("error","开始节点不可编辑!");
					choosedNode.hideEdit();
					return;
				}else if(choosedNode.getType() == "state"){
					showEditStatDiv(choosedNode.getId().split("_")[1],choosedNode.text());
				}
			}
			else if(choosedNode.getId().substring(0,4)=="path")
			{   //动作
				showEditActionDiv(choosedNode.getId().split("_")[1],choosedNode.text());
			}
		}else{
			showInfoWin("error","未选中任何元素!");
		}
		return true;
	}
	
	//删除元素
	function deleteElement()
	{
		if(!a.config.editable){return;}
		//删除
		var choosedNode=$(rap).data("currNode");
		if(choosedNode){
			if(choosedNode.getType() == 'start'){
				showInfoWin("error","开始状态不能删除!");
				return;
			}
			
			if(!confirm("数据将不可恢复！您确定要删除吗？"))
				return;
		
			if(choosedNode.getId().substring(0,4)=="rect"){
				$(rap).removeStat(choosedNode.getId().split("_")[1]);
			}
			else{
				if(choosedNode.getId().substring(0,4)=="path"){
					$(rap).removeAction(choosedNode.getId().split("_")[1]);
				}
			}
			$(rap).removeData("currNode");
			showInfoWin("success","删除成功!");
			from_node = to_node = null;
			return false;
		}else{
			showInfoWin("error","未选中任何元素!");
		}
		return false;
	}
	
	function saveSvgCode(){
		//点击空白处取消选中
		var choosedNode = $(rap).data("currNode");
		if(choosedNode){
			choosedNode.hideEdit();
		}
		$(rap).data("currNode",null);
		
		//保存状态
		var svgCode="{";
		svgCode+= "states:{";
		for(var c in global_node){
			if(global_node[c]){
				svgCode += global_node[c].getId()+":"+global_node[c].toJson()+",";
			}
		}
		if(svgCode.substring(svgCode.length-1,svgCode.length)==","){
			svgCode = svgCode.substring(0,svgCode.length-1);
		}
		//保存动作
		svgCode+="},paths:{";
		
		for(var c in global_path){
			if(global_path[c]){
				svgCode += global_path[c].getId() + ":" + global_path[c].toJson() + ",";
			}
		}
		
		if(svgCode.substring(svgCode.length-1,svgCode.length)==","){
			svgCode = svgCode.substring(0,svgCode.length-1);
		}
		svgCode += "}";
		
		//保存画布设置
		svgCode += ",panZoom:" + (panZoom == null ? 1 : panZoom.getCurrentZoom());
		svgCode += ",viewBox_x:" + rap._viewBox[0];
		svgCode += ",viewBox_y:" + rap._viewBox[1];
		svgCode += ",viewBox_width:" + rap._viewBox[2];
		svgCode += ",viewBox_height:" + rap._viewBox[3];
		svgCode += "}";
		return saveFlowSvg(svgCode); //保存至数据库
	}
	
	function getNodeById(id){
		for(var i in global_node){
			if(global_node[i] && global_node[i].getId().indexOf(id) != -1){
				return global_node[i];  
			}
		}
		
		for(var i in global_path){
			if(global_path[i] && global_path[i].getId().indexOf(id) != -1){
				return global_path[i];  
			}
		}
		return null;
	}
	
	function getNodeByRapealId(id){
		for(var i in global_node){
			if(global_node[i] && global_node[i].getRaphaelId().indexOf(id) != -1){
				return global_node[i];  
			}
		}
		
		for(var i in global_path){
			if(global_path[i] && global_path[i].getRaphaelId().indexOf(id) != -1){
				return global_path[i];  
			}
		}
		return null;
	}
	
	//查找两个状态间动作数
	function checkStatActionNum(startNode,endNode){
		var action = new Array(); 
		
		for(var i in global_path){
			if(global_path[i]){
				if((global_path[i].from() == startNode && global_path[i].to() == endNode) || ((global_path[i].from() == endNode && global_path[i].to() == startNode))){
					action.push(global_path[i]);
				}
			}
		}
		return action;
	}
	
	function checkActionExist(startNode,endNode){
		for(var i in global_path){
			if(global_path[i] != null && global_path[i].from() == startNode && global_path[i].to() == endNode){
				return true;
			}
		}
		return false;
	}
	
	
	$.fn.checkExistStatName = function(statName)
	{
		for(var i in global_node){
			if(global_node[i] != null && global_node[i].text() == statName)
				return true;
		}
		return false;
	};
	
	$.fn.checkExistActionName = function(actionName)
	{
		for(var i in global_path){
			if(global_path[i] != null && global_path[i].text() == actionName)
				return true;
		}
		return false;
	};
	
	$.fn.changeStat = function(statId,statName)
	{
		if(global_node["rect_"+statId])
			global_node["rect_"+statId].changeText(statName);
		saveSvgCode();	
	};
	
	$.fn.changeAction = function(actionId,actionName,fromStatId,toStatId)
	{
		var action = global_path["path_"+actionId];
		if(action)
		{
			action.remove();
			global_path["path_" + actionId]=null;
		}
			
		$(rap).drawAction(actionName,actionId,fromStatId,toStatId);
		
		saveSvgCode();	
	};
	
	$.fn.saveFlowSvg = function(){
		saveSvgCode();	
	};
	
	//清空画布
	$.fn.clearMap = function(){
		if(rap != null){
			rap.clear();
		}
	};
	
	//删除状态
	$.fn.removeStat = function(statId){
		var stat = global_node["rect_" + statId];
		if(stat){
			//删除与该状态关联的动作
			for(var i in global_path){
				if(global_path[i] && (global_path[i].from() == stat || global_path[i].to() == stat)){
					removeAction(global_path[i].getId().split("_")[1]);
					global_path[i].remove();
					global_path[i]=null;
				}
			}
			
			stat.remove();
			global_node["rect_" + statId] = null;
			removeStat(statId);
			showInfoWin("success","删除成功!");
			saveSvgCode();
		}
		return true;
	};
	
	//删除动作
	$.fn.removeAction = function(actionId){
		var action = global_path["path_" + actionId];
		if(action != null){
			action.remove();
			global_path["path_" + actionId]=null;
		}
		removeAction(actionId);
		showInfoWin("success","删除成功!");
		saveSvgCode();	
		return true;
	};
	
	//添加状态
	$.fn.drawStat = function(point_x,point_y,statName,type,statId,width,height)
	{
		var properties = null;
		if(width!=undefined && height != undefined){
			properties = {attr:{x:point_x,y:point_y,name:statName,width:width,height:height}};
		}else{
			properties = {attr:{x:point_x,y:point_y,name:statName}};
		}
		
		var stat = null;
		if(type == 'start'){
			//判断是否己存在开始节点;
			for(var i in global_node){
				if(global_node[i].getType() == "start"){
					showInfoWin("error","己存在开始节点，新建失败!");
					//改为选择状态
					$(".selected").removeClass("selected");
					$("#pointer").addClass("selected");
					$(rap).data("mod","pointer");
					return;
				}
			}
			stat = new a.rect($.extend(true,{},a.config.tools.states[type], properties),rap,'start');
			global_node[stat.getId()] = stat;
			saveSvgCode();
		}
		else{
			if(statId == null || statId == undefined){
				//判断是否己存在该名
				if($('#myflow').checkExistStatName(statName)){
					showInfoWin("error","己存在该名字状态,添加失败!");
					return;
				}
				//获取状态id
				addStat(statName,function(statId){
					if(statId == "" || statId == undefined){
						showInfoWin("error","服务器原因,新建失败!");
						return null;
					}else{
						stat = new a.rect($.extend(true,{},a.config.tools.states[type], properties),rap,statId);
					}
					global_node[stat.getId()]=stat;
					saveSvgCode();	
				});
			}else{
				stat = new a.rect($.extend(true,{},a.config.tools.states[type], properties),rap,statId);
				global_node[stat.getId()]=stat;
				saveSvgCode();	
			}
		}
	};
	
	//添加动作
	$.fn.drawAction = function(actionName,actionId,fromStatId, toStatId)
	{
		if(actionId == null || actionId == undefined){
			//图上绘制
			if(checkActionExist(from_node,to_node)){ //判断起点和终点是否存在动作
				showInfoWin("error","状态之间己存在动作!");
				return;
			}
			if($container.checkExistActionName(actionName)){
				from_node = null;
				to_node = null;
				showInfoWin("error","己存在该名字动作,添加失败!");
				return;
			}
			
			var fromStatId = from_node.getId().split("_")[1],
				toStatId = to_node.getId().split("_")[1];
			
			if(from_node.getType() == "start"){
				fromStatId = "";
			}
			
			addAction(fromStatId,toStatId,actionName,function(actionId,from_node,to_node){
				var action = new a.path({},rap,from_node,to_node,actionName,actionId);
				global_path[action.getId()] = action;
				adjustAction(from_node,to_node);
			},from_node,to_node);
		}else{
			//转化生成
			from_node = global_node["rect_" + fromStatId];
			to_node = global_node["rect_" + toStatId];
			var action = new a.path({},rap,from_node,to_node,actionName,actionId);
			global_path[action.getId()] = action;
			adjustAction(from_node,to_node);
		}
	};
	
	function adjustAction(from_node,to_node){
		//判断两点间是否存在两种动作,如果存在则调整动作路径 使之分离
		var actionBtw = checkStatActionNum(from_node,to_node);
		if(actionBtw.length == 2){
			var moveDx = getMoveDx(actionBtw[0].fromDot(),actionBtw[0].toDot());
			actionBtw[0].midDot().drag(moveDx.x,moveDx.y);
			actionBtw[1].midDot().drag(0- moveDx.x,0-moveDx.y);
			actionBtw[0].hideEdit();
			actionBtw[1].hideEdit();
		}
		if(from_node != null){
			from_node.hideEdit();
			from_node=null;
		}
		if(to_node != null){
			to_node.hideEdit();
			to_node=null;
		}
		saveSvgCode();	
	}
	
	function getMoveDx(fromDot,toDot){
		
		var dx = fromDot.pos().x - toDot.pos().y;
		var dy = fromDot.pos().y - toDot.pos().y;
		var length = Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
//		length /=20;
		length = 30; // TODO
		var dgree = Math.abs((Math.atan2(dy,dx)*180)/Math.PI);
		dgree = Math.abs(dgree);
		if(dgree > 90);
		dgree = 180 - dgree;
		return {x: length * Math.sin(dgree), y:length*Math.cos(dgree)};
	}
	
	$.fn.myflow=function(c){
		return this.each(function(){a.init('myflow',c);});
	};
	
	//搜索节点
	$.fn.searchNode = function(type,name){
		var findNode = null;
		if(type == 'stat'){
			for(var i in global_node){
				if(global_node[i] && global_node[i].text() == name){
					findNode = global_node[i];
					break;
				}
			}
		}else if(type == 'action'){
				for(var i in global_path){
					if(global_path[i] && global_path[i].text() == name){
						findNode = global_path[i];
						break;
					}
				}
		}
		if(findNode != null){
			findNode.twinkle();
		}
	};
	
	$.fn.cacelModifyAction = function(){
		from_node = null;
		to_node = null;
		clearDrawLine();
	};
	
	$.fn.initFlow=function(container_id){
		$container = $("#" + container_id);
		if(rap == null)
			rap=Raphael(container_id,$container.width(),$container.height());
		rap.safari();
	};
	
	$.myflow=a;
	
})(jQuery);