$(function(){
	$("#left_tree").jstree({
		"plugins" : [ "themes", "json_data", "ui", "crrm","hotkeys" ,"dnd","types", "contextmenu" ],
		"json_data" : {
			"ajax" : {
				"url" : 'tree/jsTree.jsp',
				"data" : function(n){
					return {
						"operation" : "get_children",
						"id" : n.attr ? n.attr("id").replace("node_","") : 1
					};
				}
			}
		},
		"search" : {
			"ajax" : {
				"url" : "",
				"data" :function(str) {
					return {
						"operation" : "search",
						"search_str" : str
					};
				}
			}
		},
		"core":{
			"animation" : 0
		},
		"types" : {
				// I set both options to -2, as I do not need depth and children count checking
				// Those two checks may slow jstree a lot, so use only when needed
				"max_depth" : -2,
				"max_children" : -2,
				"types" : {
					// The default type
					"default" : {
						// I want this type to have no children (so only leaf nodes)
						// In my case - those are files
						"valid_children" : "none",
						// If we specify an icon for the default type it WILL OVERRIDE the theme icons
						"icon" : {
							"image" : "images/folder_file.jpg"
						}
					},
					"others":{
						"valid_children":"none",
						"icon":{
							"image":"images/others.png"
						}
					},
					// The `folder` type
					"folder" : {
						// can have files and other folders inside of it, but NOT `drive` nodes
						"valid_children" : [ "default", "folder",'others' ],
						"icon" : {
							"image" : "images/folder_bg.gif"
						},
						'select_node':function (e) {
                			this.toggle_node(e);
                			return false;
          			  }
					}
				}
		
		}
        
	}).bind('loaded.jstree', function() {
   		//$("#left_tree").jstree("open_all");  //展开所有节点
		//只展开我的过滤器和最常访问
		$("#left_tree").jstree("open_node", $("#node_6"));
   		$("#left_tree").jstree("open_node", $("#node_favorite"));
   		$("#node_6").children("a").css("font-weight","bold");
   		//$("#node_6").children("a").css("background","#B1CAE9");
   		//$("#node_6").children("ins").css("background","#B1CAE9");
   		//$("#node_favorite").children("a").css("font-weight","bold");
   		//$("#node_favorite").children("a").css("background","#B1CAE9");
   		//$("#node_favorite").children("ins").css("background","#B1CAE9");
 	 }).bind("create.jstree",function(e,data){
		$.post(
			"tree/jsTree.jsp",
			{
				"operation" : "create_node",
				"id" : data.rslt.parent.attr("id").replace("node_",""),
				"position" : data.rslt.position,
				"title" : data.rslt.name,
				"type" : data.rslt.obj.attr("rel")
			},
			function(r){
				if(r.status)
				{
					if(data.rslt.obj.attr("rel")=='default')
					{
						$(data.rslt.obj).attr("id","node_filter_"+r.id);
						initFilterPageFirst(r.id,data.rslt.name);
					}else
					{
						$(data.rslt.obj).attr("id","node_"+r.id);
					}
					//initFilterMenu('folder');
				}else{
					$.jstree.rollback(data.rlbk);
				}
				
			}
		);	
	}).bind("remove.jstree",function(e,data){
		data.rslt.obj.each(function(i){
		var bool = window.confirm("您确定删除吗?");
		var tId = this.id;
		if(bool)
		{ 
			$.ajax({
				type : 'POST',
				url : 'tree/jsTree.jsp',
				data : {
					"operation" : "remove_node",
					"id" : this.id.replace("node_",""),
					"parentId" : data.rslt.parent.attr("id").replace("node_","")
				},
				success : function(r){
					if(!r.status){
						$.jstree.rollback(data.rlbk);
						alert(r.msg);
					}
					if(data.rslt.parent.attr("id").indexOf("favorite")>=0)
					{
					}else
					{
						$("#node_favorite").find("#"+tId).each(function(index,node){
							$(node).remove();
						});
					}
					//initFilterMenu();
				}
			});
		}else
		{
			$.jstree.rollback(data.rlbk);
		}
		});
	}).bind("rename.jstree", function (e, data) {
			var id = data.rslt.obj.attr("id");
			if(id.indexOf("119695")>=0)
			{
				alert("系统过滤器无法重命名");
				$.jstree.rollback(data.rlbk);
				return;
			}
			$.post(
				"tree/jsTree.jsp", 
				{ 
					"operation" : "rename_node", 
					"id" : data.rslt.obj.attr("id").replace("node_",""),
					"title" : data.rslt.new_name
				}, 
				function (r) {
					if(!r.status) {
						$.jstree.rollback(data.rlbk);
					}else
					{
						$("#left_tree").find("#"+id).each(function(index,node){
							$(node).children('a').html("<ins class='jstree-icon'>&nbsp;</ins>"+data.rslt.new_name);
						});
						
						if(id.indexOf("filter")>=0)
						{
							$("#filterFolders").find("#"+id.replace("node_filter_","")).each(function(index,node){
								$(node).children("span").text(data.rslt.new_name);
							});
							$("#toolbar_ul, #toolbar_more").find("#"+id.replace("node_filter_","")).each(function(index,node){
								$(node).find('a').text(data.rslt.new_name);
							});
							var currentFilterId = $("#filterId").val();
							if(currentFilterId!=null&&currentFilterId==id.replace("node_filter_",""))
							{
								$("#input_filter_name").val(data.rslt.new_name);
							}
						}else
						{
							$("#filterFolders").find("#"+id.replace("node_","")).each(function(index,node){
								$(node).children("span").text(data.rslt.new_name);
							});
						}
					}
				}
			);
	}).bind("move_node.jstree",function(e,data){
		data.rslt.o.each(function(i){
			var id =  $(this).attr("id").replace("node_","");
			var position =  data.rslt.cp+i;
			var parentId = data.rslt.op.attr("id").replace("node_","");
			var refId = data.rslt.np.attr("id").replace("node_","");
			
			if(refId == "left_tree")
			{
				$.jstree.rollback(data.rlbk);
				return;
			}
			if(parentId.indexOf("favorite")>=0&&refId.indexOf("favorite")<0)
			{
				$.jstree.rollback(data.rlbk);
				return;
			}
			if(refId.indexOf("filter")>=0)
			{
				alert("只能拖拽到文件夹下！");
				$.jstree.rollback(data.rlbk);
				return;
			}else if(refId.indexOf("favorite")>=0&&parentId.indexOf("favorite")>=0)
			{
				var childrenIds = "";
				$("#"+data.rslt.op.attr("id")).children("ul").children("li").each(function(index,node){
					if($(node).attr("id").indexOf("filter")>=0)
					{
						childrenIds = childrenIds+$(node).attr("id").replace("node_filter_","")+",";
					}
				});
				$.ajax({
					type:'POST',
					url : 'tree/jsTree.jsp',
					data : {
						'operation':'update_favorites',
						'id':id.replace("filter_",""),
						'position':position,
						'childrenIds':childrenIds
					}
				});
				
			}else if(refId==parentId){
				var childrenIds = "";
				$("#"+data.rslt.op.attr("id")).children("ul").children("li").each(function(index,node){
					if($(node).attr("id").indexOf("filter")>=0)
					{
						childrenIds = childrenIds+$(node).attr("id").replace("node_filter_","")+",";
					}
				});
				
				$.ajax({
					type:'POST',
					url : 'tree/jsTree.jsp',
					data : {
						'operation':'update_filter_order',
						'parentId' : data.rslt.op.attr("id").replace("node_",""),
						'childrenIds' : childrenIds,
						'id':id
					},
					success : function(r)
					{
						if(!r.status)
						{
							$.jstree.rollback(data.rlbk);
						}else
						{
							//initFilterMenu('folder');
						}
					}
				});
				
			}else{
				var parentNodeId = data.rslt.op.attr("id");
				var refNodeId = data.rslt.np.attr("id");
				var parentChildrenIds = "";
				var refChildrenIds = "";
				if(refNodeId.indexOf("favorite")>=0)
				{	
					if($("#"+refNodeId).find("#"+$(this).attr("id")).length>1)
					{
						$.jstree.rollback(data.rlbk);
						alert("该过滤器已经存在!");
						return;
					}
				}
				$("#"+parentNodeId).children("ul").children("li").each(function(index,node){
					if($(node).attr("id").indexOf("filter")>=0)
					{
						parentChildrenIds = parentChildrenIds+$(node).attr("id").replace("node_filter_","")+",";
					}
				});
				$("#"+refNodeId).children("ul").children("li").each(function(index,node){
					if($(node).attr("id").indexOf("filter")>=0)
					{
						refChildrenIds = refChildrenIds+$(node).attr("id").replace("node_filter_","")+",";
					}
				});
				$.ajax({
				type : 'POST',
				url : 'tree/jsTree.jsp',
				data : {
					"operation" : "move_node",
					"id" : $(this).attr("id").replace("node_",""),
					"ref" : refId,
					"position" : position,
					'parentChildrenIds':parentChildrenIds,
					'refChildrenIds':refChildrenIds,
					"parentId" : data.rslt.op.attr("id").replace("node_",""),
					"title" : data.rslt.name,
					"copy" : data.rslt.cy?1:0
				},
				success : function (r) {
						if(!r.status) {
							$.jstree.rollback(data.rlbk);
							alert(r.msg);
						}
						else {
							$(data.rslt.oc).attr("id", "node_" + r.id);
							if(data.rslt.cy && $(data.rslt.oc).children("UL").length) {
								data.inst.refresh(data.inst._get_parent(data.rslt.oc));
							}
							if(refId.indexOf("favorite")>=0&&parentId.indexOf("favorite")<0)
							{
								data.inst.refresh("#"+parentNodeId);
							}
							//initFilterMenu();
						}
				}
			});
			}
			
		});
		
	}).bind("select_node.jstree",function(e,data){
	}).bind("click.jstree",function(event){
		var eventNodeName = event.target.nodeName;
		if(eventNodeName=='INPUT')
			return;
		var node = event.target;
		var nodeId = $(node).parents('li').attr('id');
		if(nodeId.indexOf('filter')<0)
		{//展开下面的所有选项
			
		}else
		{
			var nodeId = $(event.target).parents('li').attr('id');
            if(nodeId.indexOf("filter">=0))
            {
            	var filterId = nodeId.replace("node_filter_","");
            	var params = {id:filterId,operation:'verify'};
				$.post("tree/jsTree.jsp",params,function(data,status){
					if(data.status){//可以修改
						if(location.href.indexOf("editFilter.html")>=0)
						{
							initFilterPage(filterId,'nomenu');						
						}else
						{
							window.open("editFilter.html?filterId="+filterId);
						}
					}else{
						cynthia.util.showInfoWin('error','您无权编辑该过滤器!');
					}
					
				},'json');
            }
		}
	});
	
});