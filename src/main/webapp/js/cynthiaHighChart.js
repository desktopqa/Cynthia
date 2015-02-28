/**
 * @description:显示highChart图表
 * @author liming
 */

(function ($) {
		
	var COLORS = ['#46cbee', '#fec157', '#e57244', '#cfd17d', '#24CBE5', '#64E572', '#FF9655', '#FFF263', '#6AF9C4', '#6600FF','#669933','#993399', '#CCCC66'];
	var HOME_LINK = getRootDir() + "index.html";
	
	/**
	 * data 格式： [title:title,data:data,type:type]
	 */
	$.initChart = function (containerId,data) 
	{
		//定义一个HighCharts
		var chart = new Highcharts.Chart({
			//配置chart选项
			chart: {
				renderTo: containerId  //容器名，和body部分的div id要一致
			},
			//不同组数据的显示背景色，循环引用
			colors: COLORS,
			
			//配置链接及名称选项
			credits: {
				enabled : true,
				href : HOME_LINK,
				text : "返回cynthia首页"
			},
			//配置标题
			title: {
				text: '',
				y:10  //默认对齐是顶部，所以这里代表距离顶部10px
			},
			//配置副标题
			subtitle: {
				text: '数据来源：cynthia',
				y:30
			},

			events: {
				click: function(e) {
					//有需要时可写事件代码
				},
				mouseOver:function(){
					//有需要时可写事件代码
				},
				mouseOut:function(){
					   //有需要时可写事件代码
				}
            },

			plotOptions: {
				pie: {
					allowPointSelect: true, //允许选中，点击选中的扇形区可以分离出来显示
					cursor: 'pointer',  //当鼠标指向扇形区时变为手型（可点击）
					showInLegend: true,  //如果要显示图例，可将该项设置为true
					size: 220,    //设置大小
					dataLabels: {
						enabled: true,  //设置数据标签可见，即显示每个扇形区对应的数据
						color: '#000000',  //数据显示颜色
						connectorColor: '#999',  //设置数据域扇形区的连接线的颜色
						style:{
							fontSize: '8px'  //数据显示的大小
						},

						format: '<b>{point.name}</b>:{y}<br/>{point.percentage:.1f} %'
					}
				},
				area:{
					turboThreshold: 10000
				},
				//柱形图
				column: {
					pointWidth: 10,     //设置柱形的宽度
					borderWidth: 0,      //设置柱子的边框，默认是1
					pointPadding: 0.2
				},
				series: {
//					pointWidth: 10,   //设置柱形的宽度,设置为10即为固定宽度
					pointPadding: 0.2
				}
			},

			lang: {
	            noData: "没有统计数据!"
	        },
	        noData: {
	            style: {
	                fontWeight: 'bold',
	                fontSize: '15px',
	                color: 'red'
	            }
	        },
//		    //图例
//	        legend: {
//	            layout: 'vertical',
//	            backgroundColor: '#FFFFFF',
//	            align: 'right',
//	            verticalAlign: 'top',
//	            margin:50,
//	            y: 30,
//	            x: 10,
//	            borderWidth: 1,
//	            borderRadius: 1,
//	            title: {
//	                text: '::拖动我'
//	            },
//	            floating: true,
//	            draggable: true,
//	            zIndex: 20
//	        },

			//配置x轴
			xAxis: {
				tickInterval: getChartInterval(data.data),
				type: 'datetime',
				categories: ['0', '1', '2', '3', '4', '5','6', '7', '8', '9', '10', '11','12','13','14','15','16','17','18','19','20','21','22','23/点']
			},
			//配置y轴
			yAxis: {
				title: {
					text: '数量（个）'
				},
				labels: {
					formatter: function() {
						return this.value;
					}
				}
			},
			//配置数据点提示框
			tooltip: {
				crosshairs: true
			},
			//配置数据列
			series: []
		});
		
		//设置chart的间隔,面积图时间类型最多显示10条,其它间隔为1
		function getChartInterval(data){
			data = data||[];
			var isDataNumber,length,dataOne,totalLength = Object.keys(data).length;
			
			//x轴序列
			for(var key in data){
				if(!isDataNumber)
					isDataNumber = typeof data[key] !== 'object' ;  //只要不是object统一当数字处理，如果是object则可能有多条序列线
				if(data.hasOwnProperty(key)){
					if(!dataOne){
						dataOne = data[key];
						break;
					}
				}
			}
			if(isDataNumber){
				return 1;
			}else{	
				try{
					length = Object.keys(dataOne).length;
					if(length === 1){
						return 1;
					}
					else {
						var max = Math.ceil(totalLength/20);
						return max > 20 ? 20:max;
					}
				}catch(e){
					
				}
			}
			return 1;
		}
		
		//添加序列
		function addChartSeries (chartData,type,name) {
			this.chart.addSeries({
				data: chartData,
				type:type,
				name:name,
				marker: {
					enabled: false,  //不提示，hover上去再提示
	                states: {
	                    hover: {
	                        enabled: true
	                    }
	                }
				}
			});
		};
		
		//设置表格数据，标题，图表类型
		function setChartData(chartTitle,data,type){
			data = data||[];
			var keys = new Array(),dataOne = null,chartData = new Array(),i, property,value,isDataNumber;

			this.chart.setTitle({ text: chartTitle});  //设置标题
			if (this.chart.series.length) {
				this.chart.series[0].remove();
			}

			//x轴序列
			for(var key in data){
				if(!isDataNumber)
					isDataNumber = typeof data[key] !== 'object' ;  //只要不是object统一当数字处理，如果是object则可能有多条序列线
				if(data.hasOwnProperty(key)){
					if(!dataOne){
						dataOne = data[key];
					}
					keys.push(key);
				}
			}
			
			this.chart.xAxis[0].setCategories(keys);

			//y轴
			if(isDataNumber){
				//数值
				chartData = new Array();
				i = 0;
				for(var key in data)
				{
					if(data.hasOwnProperty(key)){
						i++;
						value = parseInt(data[key]);
						if(value > 0)  //只显示有数据部分
							chartData.push({name:key, y:parseInt(data[key]), color: COLORS[i%COLORS.length]});
					}
				}
				this.addChartSeries(chartData,type,'总数');

			}else{
				//Object
				if(dataOne){
					var length = Object.keys(dataOne).length;
					if(length === 1){
						//只有一个属性的object,对应有链接的情况处理
						chartData = new Array();
						i = 0;
						for(var key in data)
						{
							if(data.hasOwnProperty(key)){
								i++;
								var propertyKey = Object.keys(data[key])[0];
								value = parseInt(data[key][propertyKey]);
								if(value > 0)  //只显示有数据部分
									chartData.push({name:key, y:value, color: COLORS[i%COLORS.length]});
							}
						}
						this.addChartSeries(chartData,type,'总数');
					}else{
						//按趋势统计有多个object,无法提供链接
						for(var key in Object.keys(dataOne))
						{
							chartData = new Array();
							property = Object.keys(dataOne)[key];
							i = 0;
							for(var key in data)
							{
								if(data.hasOwnProperty(key)){
									i++;
									value = parseInt(data[key][property]);
									if(value > 0)  //只显示有数据部分
										chartData.push({name:key, y:value, color: COLORS[i% COLORS.length]});
								}
							}
							this.addChartSeries(chartData,type,property);
						}
					}
				}
			}
		}
		
		var cynthiaChart = {
			chart:chart,
			addChartSeries:addChartSeries,
			setChartData:setChartData
		};
	    // Return "this" so the object is chainable (jQuery-style)
	    return cynthiaChart;
	};
	
	function getStatisticInfo(statisticId,divId)
	{
		$.ajax({
			url:'../statistic/getStatisticInfo.do',
			data:{'statisticId':statisticId},
			type:'POST',
			dataType:'json',
			success:function(data){
				var chart = $.initChart(divId,data);
				chart.setChartData(data.name,data.data,data.chartType);
				if($("#tableData").length != 0)
				{
					initTableData(data.data,data.statisticId);
				}
			}
		});
	}

	function initTableData(data,statisticId)
	{
		data = data || [];
		var keys = new Array();
		var dataOne = null;
		var count_y;
		//x轴序列
		for(var key in data){
			if(!count_y){
				count_y = Object.keys(data[key]).length;
				dataOne = data[key];
			}
			keys.push(key);
		}

		$("#tableData").empty();

		if(dataOne){
			var length = Object.keys(dataOne).length;
			var gridHtml = "";
			gridHtml += "<table class=\"table table-striped table-bordered table-hover table-condensed\">";
			gridHtml += "<thead><tr>";
			gridHtml += "<th>统计项</th>";
			if(length === 1){
				gridHtml += "<th>总数</th>";
			}else{
				for(var key in Object.keys(dataOne)){
					var property = Object.keys(dataOne)[key];
					gridHtml += "<th>"+property+"</th>";
				}
			}

			gridHtml += "</tr></thead><tbody>";

			var url = getRootDir() + 'search/list.html?statisticId=' + statisticId + '&statisticVal=';
			if(length === 1){
				//需要加链接
				for(var i in data){
					gridHtml += "<tr>";
					gridHtml += "<td>"+i+"</td>";
					var proVal = Object.keys(data[i])[0];
					var urlVal = url + proVal;
					gridHtml += "<td><a href = '" +urlVal+ "' target='_blank'>" + data[i][proVal] + "</a></td>";
					gridHtml += "</tr>";
				}
			}else{
				for(var i in data){
					gridHtml += "<tr>";
					gridHtml += "<td>"+i+"</td>";
					for(var key in Object.keys(dataOne)){
						var property = Object.keys(dataOne)[key];
						gridHtml += "<td>"+data[i][property]+"</td>";
					}
					gridHtml += "</tr>";
				}
			}

			gridHtml += "</tbody></table>";
			$("#tableData").html(gridHtml);
		}
	}
	
	$(function(){
		var statisticId = request('statisticId');
		if(statisticId){
			getStatisticInfo(statisticId,'stat_div');
		}
	});
	
}(jQuery));


