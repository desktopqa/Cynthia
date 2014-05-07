/**
 * raphael.pan-zoom plugin 0.2.1
 * Copyright (c) 2012 @author Juan S. Escobar
 * https://github.com/escobar5
 *
 * licensed under the MIT license
 */
 
(function () {
    'use strict';
    /*jslint browser: true*/
    /*global Raphael*/
    
    function findPos(obj) {
        var posX = obj.offsetLeft, posY = obj.offsetTop, posArray;
        while (obj.offsetParent) {
            if (obj === document.getElementsByTagName('body')[0]) {
                break;
            } else {
                posX = posX + obj.offsetParent.offsetLeft;
                posY = posY + obj.offsetParent.offsetTop;
                obj = obj.offsetParent;
            }
        }
        posArray = [posX, posY];
        return posArray;
    }
    
    function getRelativePosition(e, obj) {
        var x, y, pos;
        if (e.pageX || e.pageY) {
            x = e.pageX;
            y = e.pageY;
        } else {
            x = e.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
            y = e.clientY + document.body.scrollTop + document.documentElement.scrollTop;
        }

        pos = findPos(obj);
        x -= pos[0];
        y -= pos[1];

        return { x: x, y: y };
    }

    var panZoomFunctions = {
        zoomEnable: function () {
            this.zoomEnabled = true;
        },

        zoomDisable: function () {
            this.zoomEnabled = false;
        },

        panEnable: function () {
            this.panEnabled = true;
        },

        panDisable: function () {
            this.panEnabled = false;
        },
        //放大
        zoomIn: function (steps) {
            this.applyZoom(steps);
        },
        //缩小
        zoomOut: function (steps) {
            this.applyZoom(steps > 0 ? steps * -1 : steps);
        },

        pan: function (deltaX, deltaY) {
            this.applyPan(deltaX * -1, deltaY * -1);
        },

        isDragging: function () {
            return this.dragTime > this.dragThreshold;
        },

        getCurrentPosition: function () {
            return this.currPos;
        },

        getCurrentZoom: function () {
            return this.currZoom;
        }
    },

    PanZoom = function (el, options) {
        var paper = el,
            container = paper.canvas.parentNode,
            me = this,
            settings = {},
            initialPos = { x: 0, y: 0 },
            deltaX = 0,
            deltaY = 0,
            mousewheelevt = (/Firefox/i.test(navigator.userAgent)) ? "DOMMouseScroll" : "mousewheel";

        this.zoomEnabled = true; //放大缩小打开
        this.panEnabled = true;  //平移打开
        this.dragThreshold = 5;
        this.dragTime = 0;

        options = options || {};

        settings.maxZoom = options.maxZoom || 9;
        settings.minZoom = options.minZoom || 0;
        settings.zoomStep = options.zoomStep || 0.1;
        settings.initialZoom = options.initialZoom || 0;
        settings.initialPosition = options.initialPosition || { x: 0, y: 0 };

        this.currZoom = settings.initialZoom;
        this.currPos = settings.initialPosition;
        
        function repaint() {
            me.currPos.x = me.currPos.x + deltaX;
            me.currPos.y = me.currPos.y + deltaY;

            var newWidth = paper.width * (1 - (me.currZoom * settings.zoomStep)),
                newHeight = paper.height * (1 - (me.currZoom * settings.zoomStep));
            
            //拖出边界控制
//            if (me.currPos.x < 0) {
//                me.currPos.x = 0;
//            } else if (me.currPos.x > (paper.width * me.currZoom * settings.zoomStep)) {
//                me.currPos.x = (paper.width * me.currZoom * settings.zoomStep);
//            }
//
//            if (me.currPos.y < 0) {
//                me.currPos.y = 0;
//            } else if (me.currPos.y > (paper.height * me.currZoom * settings.zoomStep)) {
//                me.currPos.y = (paper.height * me.currZoom * settings.zoomStep);
//            }
           
            paper.setViewBox(me.currPos.x, me.currPos.y, newWidth, newHeight,true);
        }
        
        function dragging(e) {
            if (!me.panEnabled) {
                return false;
            }
            
            container.className += " grabbing";
            var evt = window.event || e,
                newWidth = paper.width * (1 - (me.currZoom * settings.zoomStep)),
                newHeight = paper.height * (1 - (me.currZoom * settings.zoomStep)),
                newPoint = getRelativePosition(evt, container);

            deltaX = (newWidth * (newPoint.x - initialPos.x) / paper.width) * -1;
            deltaY = (newHeight * (newPoint.y - initialPos.y) / paper.height) * -1;
            initialPos = newPoint;

            repaint();
            me.dragTime += 1;
            if (evt.preventDefault) {
                evt.preventDefault();
            } else {
                evt.returnValue = false;
            }
            return false;
        }
        
        function applyZoom(val, centerPoint) {
            if (!me.zoomEnabled) {
                return false;
            }
            me.currZoom += val;
            if (me.currZoom < settings.minZoom) {
                me.currZoom = settings.minZoom;
            } else if (me.currZoom > settings.maxZoom) {
                me.currZoom = settings.maxZoom;
            } else {
                centerPoint = centerPoint || { x: paper.width / 2, y: paper.height / 2 };

                deltaX = ((paper.width * settings.zoomStep) * (centerPoint.x / paper.width)) * val;
                deltaY = (paper.height * settings.zoomStep) * (centerPoint.y / paper.height) * val;
                repaint();
            }
        }

        this.applyZoom = applyZoom;
        
        function handleScroll(e) {
            if (!me.zoomEnabled) {
                return false;
            }
            var evt = window.event || e,
                delta = evt.detail || evt.wheelDelta * -1,
                zoomCenter = getRelativePosition(evt, container);

            if (delta > 0) {
                delta = -1;
            } else if (delta < 0) {
                delta = 1;
            }
            
            applyZoom(delta, zoomCenter);
            if (evt.preventDefault) {
                evt.preventDefault();
            } else {
                evt.returnValue = false;
            }
            return false;
        }
        
        repaint();

        container.onmousedown = function (e) {
        	var evt = window.event || e;
        	
        	container.className = container.className.replace(/(?:^|\s)grabbing(?!\S)/g, '');
        	//判断点下位置是否有raphael节点,如果有则不响应
        	if(paper.getElementByPoint(evt.pageX,evt.pageY) != null){
        		me.panEnabled = false;
        	}else{
        		me.panEnabled = true;
        	}
            
            if (!me.panEnabled) {
                return false;
            }
            me.dragTime = 0;
            initialPos = getRelativePosition(evt, container);
            container.onmousemove = dragging;
            document.onmousemove = function () { return false; };
            if (evt.preventDefault) {
                evt.preventDefault();
            } else {
                evt.returnValue = false;
            }
            return false;
        };

        container.onmouseup = function (e) {
            //Remove class framework independent
            document.onmousemove = null;
            container.className = container.className.replace(/(?:^|\s)grabbing(?!\S)/g, '');
            container.onmousemove = null;
        };

        if (container.attachEvent) {//if IE (and Opera depending on user setting)
            container.attachEvent("on" + mousewheelevt, handleScroll);
        } else if (container.addEventListener) {//WC3 browsers
            container.addEventListener(mousewheelevt, handleScroll, false);
        }
        
        function applyPan(dX, dY) {
            deltaX = dX;
            deltaY = dY;
            repaint();
        }
        
        this.applyPan = applyPan;
    };

    PanZoom.prototype = panZoomFunctions;

    Raphael.fn.panzoom = {};

    Raphael.fn.panzoom = function (options) {
        var paper = this;
        return new PanZoom(paper, options);
    };

}());
