/*
 *主要针对cynthia首页grid做特殊处理
 *1. 响应一些事件
 *2. 重新生成表格
 */
(function($) {

	var defaultOptions =
	{
	},curChoosedNodeIndex;

	$.fn.sortGrid = function(options) {
		var options = $.extend({}, defaultOptions, options);
		var element = $(this);
		var grid = new Grid(element,options);
		grid.init();
		return grid;
	};
	
	function Grid(element,options)
	{
		var header           = $(element).find("#main-grid-header");
		var mainContent      = $(element).find("#main-grid-content");
		var currentPageInput = $("#current-page");
		var nextPageButton   = $("#next-page");
		var prevPageButton   = $("#prev-page");
		var sortHeaders      = $(header).find(".sort-header");
		var groupRows        = $(element).find(".main-grid-grouprow");
		var sortField        = "";
		var sortType         = "";
		var currentPage      = 1;
		var pageSize         = 50;
		var maxPage          = 10;
	
		this.init = function()
		{
			bindEvents();
		};
	
		this.setCurrentPage = function(num)
		{
			currentPage = num;
			$("#current-page").val(num);
		};
		
		this.setMaxPage = function(totalCount)
		{
			if(totalCount == "" || totalCount == undefined)
			{
				$("#totalPage").html(1);  
				$("#totalCount").html(0);
			}
			//设置最大页数 总数
			maxPage = Math.ceil(totalCount/pageSize);
			maxPage = maxPage == 0 ? 1:maxPage;
			$("#totalPage").html(maxPage);  
			$("#totalCount").html(totalCount);
		};
		
		this.getSortField = function()
		{
			return sortField;
		};
		
		this.getSortType = function()
		{
			return sortType;
		};
		
		this.getPageSize = function()
		{
			return pageSize;
		};
		
		//重绘 reDrawHead代表是否重绘标题
		this.refreshGrid = function(reDrawHead)
		{
			showLoading(true);
			try{
				if($("#curTagId").length != 0 && $("#curTagId").val() !=""){
					//查询标签数据
					var curTagId = $("#curTagId").val();
					initTagData(curTagId,currentPage,sortField,sortType,reDrawHead);
				}else{
					var searchType = $.trim($("#searchType").val());
					var searchWord = $.trim($("#searchWord").val());
					if(searchType != "" && searchType !="id" && searchWord != ""){
						queryData($("#filterId").val(),currentPage,sortField,sortType,reDrawHead,searchType, searchWord);
					}else{
						//查询过滤器
						initFilterData($("#filterId").val(), currentPage,sortField,sortType,reDrawHead);
					}
				}
			}catch(e){}
		};
		
		this.refreshData = function()
		{
			initFilterData($("#filterId").val(), currentPage,sortField,sortType);
		};
		
		this.findAllSelectedRows = function()
		{
			//根据需要返回ID或者其他的数据
			var dataIds = new Array();
			
			$.each($(mainContent).find('.icon-input-checkbox-checked') , function (i, node){
				dataIds.push($.trim($(node).closest('tr').attr('value')));
			});
			return dataIds;
		};
		
		function bindEvents()
		{
			//跳转到某一页
			$(currentPageInput).bind("keypress",onCurrentPageInputEnter);
	
			//点击下一页
			$(nextPageButton).click(onNextPageButtonClick);
	
			//点击上一页
			$(prevPageButton).click(onPrevPageButtonClick);
	
			//选择所有
			$(header).on("click", ".i-checkbox", selectAllRows);
	
			//选择某一行
			$(mainContent).on("mousedown",".i-checkbox" ,selectCurrentRow);
	
			//隐藏显示Group
			$(mainContent).on("click", ".main-grid-grouprow" ,onGroupRowsClick);
			
			//排序字段点击事件
			$(header).on("click", "th", onSortHeaderClick);  
		}
	
		
		function onCurrentPageInputEnter(event)
		{
			if(event.keyCode == 13)
			{
				var currentPageValue = $(this).val();
				if(!currentPageValue||currentPageValue > maxPage || !isLegalityNum(currentPageValue))
				{
					showInfoWin("error","请输入合法的数字!");
					return false;
				}
				currentPage = currentPageValue;
				grid.refreshGrid();
				return false;
			}
		}
	
		function onNextPageButtonClick()
		{
			//todo
			if(currentPage == maxPage)
			{
				showInfoWin("error","已经到最后一页了!");
				return false;
			}
			currentPage++;
			$(currentPageInput).val(currentPage);
			grid.refreshGrid();
		}
	
		function onPrevPageButtonClick()
		{
			//todo
			if(currentPage == 1)
			{
				showInfoWin("error","已经到第一页了!");
				return false;
			}
			currentPage--;
			$(currentPageInput).val(currentPage);
			grid.refreshGrid();
		}
	
		function onSortHeaderClick(event)
		{
			var th = $(this);
	        var pos = th.offset();
	        if (event.clientX - pos.left < 8 || (th.width() - (event.clientX - pos.left)) < 8) {  //表示为拖动
				return false;
			}
			
			if($(this).hasClass("sort-down"))
			{
				$(this).removeClass("sort-down").addClass("sort-up");
				sortType = 'desc';
			}else if($(this).hasClass("sort-up"))
			{
				$(this).removeClass("sort-up").addClass("sort-down");
				sortType = 'asc';
			}else
			{
				$(sortHeaders).each(function(index,item){
					$(item).removeClass("sort-up").removeClass("sort-down");
				});
				$(this).addClass("sort-down");
				sortType = 'asc';
			}
			sortField = $(this).attr("value");
			grid.refreshGrid();
	
			return false;
		}
	
		//选择或者反选所有的行
		function selectAllRows()
		{
			if($(this).hasClass('icon-input-checkbox-unchecked'))
			{
				$(mainContent).find('.i-checkbox').removeClass('icon-input-checkbox-unchecked').addClass('icon-input-checkbox-checked');
				$(mainContent).find('tr').addClass('tr-checked');
				$(this).removeClass("icon-input-checkbox-unchecked").addClass('icon-input-checkbox-checked');
			}else
			{
				$(mainContent).find('.i-checkbox').removeClass('icon-input-checkbox-checked').addClass('icon-input-checkbox-unchecked');
				$(mainContent).find('tr').removeClass('tr-checked');
				$(this).removeClass("icon-input-checkbox-checked").addClass('icon-input-checkbox-unchecked');
			}
			return false;
		}
	
		//选择某一行 但是根据其他行的情况 定义头部全选按钮是否是选择状态
		function selectCurrentRow(e)
		{
			//阻止shift默认事件
			stopDefault(e);
			var max,min,thisIndex;
			var thisIndex = $(this).parent().next().text();
			
			var $thisCheckBox = $(this);
			if($thisCheckBox.hasClass('icon-input-checkbox-unchecked'))
			{
				$thisCheckBox.removeClass('icon-input-checkbox-unchecked').addClass("icon-input-checkbox-checked");
				$thisCheckBox.closest("tr").addClass("tr-checked");
			}else
			{
				$thisCheckBox.removeClass("icon-input-checkbox-checked").addClass('icon-input-checkbox-unchecked');
				$(this).closest("tr").removeClass("tr-checked");
			}
			
			if(e.shiftKey){
				//多选
				if (thisIndex !== curChoosedNodeIndex && curChoosedNodeIndex) {
					if(parseInt(thisIndex) > parseInt(curChoosedNodeIndex)){
						max = thisIndex;
						min = curChoosedNodeIndex;
					}else {
						max = curChoosedNodeIndex;
						min = thisIndex;
					}
				}
			}
			
			if(max && min){
				var index = 1;
				$.each($(mainContent).find("tr"), function(idx,node){
					if(!$(this).hasClass("main-grid-grouprow")){
						if(index >= min && index <= max && index != thisIndex && index != curChoosedNodeIndex){
							$thisCheckBox = $(this).find(".i-checkbox");
							if($thisCheckBox.hasClass('icon-input-checkbox-unchecked'))
							{
								$thisCheckBox.removeClass('icon-input-checkbox-unchecked').addClass("icon-input-checkbox-checked");
								$(this).addClass("tr-checked");
							}else
							{
								$thisCheckBox.removeClass("icon-input-checkbox-checked").addClass('icon-input-checkbox-unchecked');
								$(this).removeClass("tr-checked");
							}
						}
						index ++;
					}
				});
			}
			
			curChoosedNodeIndex = $(this).parent().next().text();
			
			var headerCheckBox = $(header).find(".i-checkbox");
			if($(mainContent).find('.icon-input-checkbox-unchecked').length != 0)
			{
				$(headerCheckBox).removeClass("icon-input-checkbox-checked").addClass('icon-input-checkbox-unchecked');
			}
			return false;
		}
	
		function onGroupRowsClick()
		{
			var groupButton = $(this).find(".group-button");
			if(groupButton&&groupButton.hasClass("icon-group-collapse"))//收缩子菜单
			{
				$(this).nextUntil(".main-grid-grouprow").hide();
				groupButton.removeClass("icon-group-collapse").addClass("icon-group-expand");
			}else if(groupButton&&groupButton.hasClass("icon-group-expand"))
			{
				$(this).nextUntil(".main-grid-grouprow").show();
				groupButton.removeClass("icon-group-expand").addClass("icon-group-collapse");
			}
			return false;
		}
	}	
	  
})(jQuery);