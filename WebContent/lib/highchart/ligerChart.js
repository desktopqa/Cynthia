(function () {
    $.fn.ligerChart = function () {
        //默认参数列表
        var param = {
            chart: { renderTo: this[0],
                zoomType: 'xy'
            },
            typeConverter: false,
            credits: {
                text: 'LigerChart',
                href: 'http://venus.desktopqa.com'
            }
        };
        //默认参数继承
        for (var key in param) {
            if (arguments[0][key] && param[key] instanceof Object) {
                $.extend(param[key], (arguments[0][key] || {}))
                delete arguments[0][key];
            }
        }
        $.extend(param, (arguments[0] || {}))
        param = _initHighChart(param).param;
        var chart = new Highcharts.Chart(param)
        $.extend(chart, new $.fn.ligerChart.prototype.init(param, chart));
        $(this).data("chart", chart);
        //回调函数
        if (typeof arguments[1] !== "undefined" && arguments[1] instanceof Function) {
            arguments[1].apply(chart);
        }
        //参数扩展
        _initLigerChart(param, chart)
        return chart;
    }
    //初始化ligerChart,并处理需要传入chart对象的参数
    $.fn.ligerChart.prototype.init = function () {
        return this;
    }
    $.fn.ligerChart.prototype.init.prototype = $.fn.ligerChart.prototype;
    //方法扩展
    $.extend($.fn.ligerChart.prototype, {
        //设置图表数据，会删除旧数据
        setData: function (param) {
            var that = this;
            var defaultParam = {
                series: null,
                xAxis: null
            }
            param = $.extend(defaultParam, param);
            if (param.xAxis) {
                this.xAxis[0].setCategories(param.xAxis.categories);
            }
            if (param.series && param.series instanceof Array) {
                this.clearData();
                $.each(param.series, function (i, n) {
                    that.addSeries(n);
                });
            }
            else if (param.series) {
                this.clearData();
                this.addSeries(param.series);
            }
        }
        ,
        //清除数据
        clearData: function () {
            while (this.series.length > 0) {
                try {
                    this.series[0].remove();
                }
                catch (e) {
                    this.series.shift();
                }
            }
        }
        ,
        //增加数据(所有对数据的操作，基于对series对象的改造)
        addData: function (param, id, name) {
            var defaultParam = {
                data: null,
                chartType: "column",
                format: null,
                name: name || null,
                id: id || null
            }
            param = $.extend(defaultParamm, param);
            this.addSeries(param);
        }
        ,
        //根据id删除数据
        removeDataByID: function (id) {
            this.get(id).remove();
        }
        ,
        //根据id隐藏数据行
        hideSeriesByID: function (id) {
            this.get(id).hide();
        }
        ,
        //根据id显示数据行
        showSeriesByID: function () {
            this.get(id).show();
        }
        ,
        //按图表类别删除数据行
        removeSeriesByType: function (type) {
            //highchart默认type为line，此时可能取不到type，故undefined作line处理
            var typeSeries = {};
            typeSeries.series = [];
            $.each(this.series, function (i, n) {
                if (n.options.type === type || (typeof n.options.type === "undefined" && type === "line")) {
                    typeSeries.series.push(n)
                }
            });
            this.clearData.call(typeSeries);
        }
        ,
        //按图表类别隐藏数据
        hideType: function (type) {
            $.each(this.series, function (i, n) {
                if (n.options.type === type || (typeof n.options.type === "undefined" && type === "line")) {
                    n.hide();
                }
            });
        }
        ,
        //按图表类别显示数据
        showType: function () {
            $.each(this.series, function (i, n) {
                if (n.options.type === type || (typeof n.options.type === "undefined" && type === "line")) {
                    n.show();
                }
            });
        }
        ,
        addTypeConverter: function () {
            _addTypeConverter(this);
        }
        ,
        /***创建选择图表显示方式的工具条
        *@param array<object<string,array>> name:types
        */
        createNavbox: function () {

        }
        ,
        showPage: function () {

        }
    });
    //事件扩展
    $.extend($.fn.ligerChart.prototype, _getEvents().ligerEvents);
    //私有函数和对象
    //循环绑定事件，其中，数据列事件都继承自series，需要单独类型数据列事件可在此处扩展
    function _getEvents(param) {
        var result = {};
        result.ligerEvents = {};
        var events = {
            "chart": ["addSeries", "click", "load", "redraw", "selection"],
            "area": ["click", "checkboxClick", "hide", "legendItemClick", "mouseOver", "mouseOut", "show"],
            "areaspline": ["click", "checkboxClick", "hide", "legendItemClick", "mouseOver", "mouseOut", "show"],
            "bar": ["click", "checkboxClick", "hide", "legendItemClick", "mouseOver", "mouseOut", "show"],
            "column": ["click", "checkboxClick", "hide", "legendItemClick", "mouseOver", "mouseOut", "show"],
            "line": ["click", "checkboxClick", "hide", "legendItemClick", "mouseOver", "mouseOut", "show"],
            "pie": ["click", "checkboxClick", "hide", "legendItemClick", "mouseOver", "mouseOut", "show"],
            "series": ["click", "checkboxClick", "hide", "legendItemClick", "mouseOver", "mouseOut", "show"],
            "scatter": ["click", "checkboxClick", "hide", "legendItemClick", "mouseOver", "mouseOut", "show"],
            "spline": ["click", "checkboxClick", "hide", "legendItemClick", "mouseOver", "mouseOut", "show"],
            "point": ["click", "mouseOver", "mouseOut", "remove", "select", "unselect", "update"]
        };
        for (var key in events) {
            $.each(events[key], function (i, n) {
                result.ligerEvents[key + n] = getLigerEvent(key + n);
                if (param && key === "chart") {
                    param.chart = param.chart || {};
                    param.plotOptions = param.plotOptions || {}
                    param.chart.events = param.chart.events || {};
                    param.chart.events[n] = getHighEvent(key + n, "chart");
                }
                else if (param && key !== "point") {
                    param.plotOptions[key] = param.plotOptions[key] || {};
                    param.plotOptions[key].events = param.plotOptions[key].events || {};
                    param.plotOptions[key]["events"][n] = getHighEvent(key + n);
                }
                else if (param) {
                    param.plotOptions.series = param.plotOptions.series || {};
                    param.plotOptions.series.point = param.plotOptions.series.point || {};
                    param.plotOptions.series.point.events = param.plotOptions.series.point.events || {};
                    param.plotOptions.series.point.events[n] = getHighEvent(key + n, "point");
                }
            });


        }
        function getLigerEvent(name) {
            return function (fuc) {
                if (fuc instanceof Function) this["on" + name] = fuc;
            }
        }
        function getHighEvent(name, type) {
            if (type === "chart") {
                return function () { (this["on" + name] || new Function()).call(this, arguments) }
            }
            else if (type === "point") {
                return function () { (this.series.chart["on" + name] || new Function()).call(this, arguments) }
            }
            else {
                return function () { (this.chart["on" + name] || new Function()).call(this, arguments) }
            }
        }
        result.param = param || {};
        result.eventList = ["chartaddSeries", "chartclick", "chartload", "chartredraw", "chartselection",
    "seriesclick", "seriescheckboxClick", "serieshide", "serieslegendItemClick", "seriesmouseOver", "seriesmouseOut", "seriesshow",
    "pointclick", "pointmouseOver", "pointmouseOut", "pointremove", "pointselect", "pointunselect", "pointupdate"];
        return result;
    }

    function _initHighChart(param) {
        if (!param) return;
        //设置highChart事件
        param = _getEvents(param);
        return param;
    }
    function _initLigerChart(param, chart) {
        //格式转换工具条参数
        if (param.typeConverter) {
            _addTypeConverter(chart);
        }
        //事件参数,舍弃原有复杂的绑定方式
        $.each(_getEvents().eventList, function (i, n) {
            if (param[n]) chart[n](param[n]);
        })
    }
    //添加图表样式转换工具条
    function _addTypeConverter(chart) {
        var renderer = chart.renderer;
        var group = renderer.g().add();
        /*var column = renderer.rect(80, 15, 20, 15, 2)
        .attr({
        //'stroke-width': 2,
        //stroke: 'red',
        fill: 'blue',
        zIndex: 3
        })
        .add(group);*/
        var column = renderer.image('../Images/column.png', 80, 15, 15, 15, 2)
        .attr({
            //'stroke-width': 2,
            //stroke: 'red',
            fill: 'blue',
            zIndex: 3
        })
        .add(group);
        var columnText = renderer.text('柱状图', 105, 27)
        /*.attr({
        rotation: -25
        })*/
    .css({
        color: '#4572A7',
        fontSize: '12px',
        cursor: "hand",
        fontFamily: "Microsoft YaHei"
    })
    .add(group);
        /*var line = renderer.rect(150, 15, 20, 15, 2)
        .attr({
        //'stroke-width': 2,
        //stroke: 'red',
        fill: 'red',
        zIndex: 3
        })
        .add(group);*/
        var line = renderer.image('../Images/line.png', 150, 15, 15, 15, 2)
        .attr({
            //'stroke-width': 2,
            //stroke: 'red',
            fill: 'red',
            zIndex: 3
        })
        .add(group);
        var lineText = renderer.text('折线图', 175, 27)
        /*.attr({
        rotation: -25
        })*/
        .css({
            color: '#4572A7',
            fontSize: '12px',
            cursor: "hand",
            fontFamily: "Microsoft YaHei"
        })
        .add(group);
        /*var pie = renderer.rect(220, 15, 20, 15, 2)
        .attr({
        //'stroke-width': 2,
        //stroke: 'red',
        fill: 'green',
        zIndex: 3
        })
        .add(group);*/
        var pie = renderer.image('../Images/pie.png', 220, 15, 15, 15, 2)
                .attr({
                    //'stroke-width': 2,
                    //stroke: 'red',
                    fill: 'green',
                    zIndex: 3
                })
                .add(group);
        var pieText = renderer.text('饼图', 245, 27)
        /*.attr({
        rotation: -25
        })*/
    .css({
        color: '#4572A7',
        fontSize: '12px',
        cursor: "hand",
        fontFamily: "Microsoft YaHei"
    })
    .add(group);
        //绑定形式转换工具条点击事件
        $(column.element).click(function () {
            $(columnText.element).click()
        });
        $(columnText.element).click(function () {
            _convertType("column", chart);
        });
        $(line.element).click(function () {
            $(lineText.element).click()
        });
        $(lineText.element).click(function () {
            _convertType("line", chart);
        });
        $(pie.element).click(function () {
            $(pieText.element).click()
        });
        $(pieText.element).click(function () {
            _convertType("pie", chart);
        });
    }
    //样式转换函数
    function _convertType(type, chart) {
        var xAxis = chart.xAxis[0].categories;
        var removeSeries = [];
        $.each(chart.series, function (i, n) {
            if (n.visible) {
                var newType = {};
                newType.name = n.name;
                newType.type = type;
                newType.data = [];
                $.each(n.yData, function (j, m) {
                    newType.data.push({ "name": xAxis[j], "y": m });
                });
                newType.showInLegend = type === "pie" ? false : true,
            removeSeries.push(n);
                chart.addSeries(newType);
            }
        });
        while (removeSeries.length > 0) {
            removeSeries.pop().remove();
        }
    }
})()