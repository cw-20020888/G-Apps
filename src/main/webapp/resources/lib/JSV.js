// ////////////////////////////////////////////////////////////
//  * jQuery resize event - v1.1 - 3/14/2010
//  * http://benalman.com/projects/jquery-resize-plugin/
// ////////////////////////////////////////////////////////////

(function($,window,undefined){
	  '$:nomunge';
	   var elems = $([]),
	    jq_resize = $.resize = $.extend( $.resize, {} ),
	    timeout_id,
	    str_setTimeout = 'setTimeout',
	    str_resize = 'resize',
	    str_data = str_resize + '-special-event',
	    str_delay = 'delay',
	    str_throttle = 'throttleWindow';
	  jq_resize[ str_delay ] = 10;
	  jq_resize[ str_throttle ] = false;
	  $.event.special[ str_resize ] = {
	    setup: function() {
	      if ( !jq_resize[ str_throttle ] && this[ str_setTimeout ] ) { return false; }
	      var elem = $(this);
	      elems = elems.add( elem );
	      $.data( this, str_data, { w: elem.width(), h: elem.height() } );
	      if ( elems.length === 1 ) {
	        loopy();
	      }
	    },
	    teardown: function() {
	      if ( !jq_resize[ str_throttle ] && this[ str_setTimeout ] ) { return false; }
	      var elem = $(this);
	      elems = elems.not( elem );
	      elem.removeData( str_data );
	      if ( !elems.length ) {
	        clearTimeout( timeout_id );
	      }
	    },
	    add: function( handleObj ) {
	      if ( !jq_resize[ str_throttle ] && this[ str_setTimeout ] ) { return false; }
	      var old_handler;
	      function new_handler( e, w, h ) {
	        var elem = $(this),
	          data = $.data( this, str_data );
		      data.w = w !== undefined ? w : elem.width();
			  data.h = h !== undefined ? h : elem.height();
	        old_handler.apply( this, arguments );
	      };
	      if ( $.isFunction( handleObj ) ) {
	        old_handler = handleObj;
	        return new_handler;
	      } else {
	        old_handler = handleObj.handler;
	        handleObj.handler = new_handler;
	      }
	    }
	  };
	  function loopy() {
	    timeout_id = window[ str_setTimeout ](function(){
	      elems.each(function(){
	        var elem = $(this),
	          width = elem.width(),
	          height = elem.height(),
	          data = $.data( this, str_data );
	        if ( width !== data.w || height !== data.h ) {
	          elem.trigger( str_resize, [ data.w = width, data.h = height ] );
	        }
	      });
	      loopy();
	    }, jq_resize[ str_delay ] );
	  };
})(jQuery,this);

//////////////////////////////////////////////////////////////
//* jQuery livequery-1.3.6  08/26/2013
//* https://github.com/hazzik/livequery/
//////////////////////////////////////////////////////////////
(function (factory) {
	if (typeof define === 'function' && define.amd) {
		define(['jquery'], factory);
	} else if (typeof exports === 'object') {
		factory(require('jquery'));
	} else {
		factory(jQuery);
	}
}(function ($, undefined) {

function _match(me, query, fn, fn2) {
	return me.selector == query.selector &&
		me.context == query.context &&
		(!fn || fn.$lqguid == query.fn.$lqguid) &&
		(!fn2 || fn2.$lqguid == query.fn2.$lqguid);
}

$.extend($.fn, {
	livequery: function(fn, fn2) {
		var me = this, q;

		// See if Live Query already exists
		$.each( $jQlq.queries, function(i, query) {
			if ( _match(me, query, fn, fn2) )
					// Found the query, exit the each loop
					return (q = query) && false;
		});

		// Create new Live Query if it wasn't found
		q = q || new $jQlq(me.selector, me.context, fn, fn2);

		// Make sure it is running
		q.stopped = false;

		// Run it immediately for the first time
		q.run();

		// Contnue the chain
		return me;
	},

	expire: function(fn, fn2) {
		var me = this;

		// Find the Live Query based on arguments and stop it
		$.each( $jQlq.queries, function(i, query) {
			if ( _match(me, query, fn, fn2) && !me.stopped)
					$jQlq.stop(query.id);
		});

		// Continue the chain
		return me;
	}
});

var $jQlq = $.livequery = function(selector, context, fn, fn2) {
	var me = this;

	me.selector = selector;
	me.context  = context;
	me.fn       = fn;
	me.fn2      = fn2;
	me.elements = $([]);
	me.stopped  = false;

	// The id is the index of the Live Query in $.livequiery.queries
	me.id = $jQlq.queries.push(me)-1;

	// Mark the functions for matching later on
	fn.$lqguid = fn.$lqguid || $jQlq.guid++;
	if (fn2) fn2.$lqguid = fn2.$lqguid || $jQlq.guid++;

	// Return the Live Query
	return me;
};

$jQlq.prototype = {
	stop: function() {
		var me = this;
		// Short-circuit if stopped
		if ( me.stopped ) return;

		if (me.fn2)
			// Call the second function for all matched elements
			me.elements.each(me.fn2);

		// Clear out matched elements
		me.elements = $([]);

		// Stop the Live Query from running until restarted
		me.stopped = true;
	},

	run: function() {
		var me = this;
		// Short-circuit if stopped
		if ( me.stopped ) return;

		var oEls = me.elements,
			els  = $(me.selector, me.context),
			newEls = els.not(oEls),
			delEls = oEls.not(els);

		// Set elements to the latest set of matched elements
		me.elements = els;

		// Call the first function for newly matched elements
		newEls.each(me.fn);

		// Call the second function for elements no longer matched
		if ( me.fn2 )
			delEls.each(me.fn2);
	}
};

$.extend($jQlq, {
	guid: 0,
	queries: [],
	queue: [],
	running: false,
	timeout: null,
	registered: [],

	checkQueue: function() {
		if ( $jQlq.running && $jQlq.queue.length ) {
			var length = $jQlq.queue.length;
			// Run each Live Query currently in the queue
			while ( length-- )
				$jQlq.queries[ $jQlq.queue.shift() ].run();
		}
	},

	pause: function() {
		// Don't run anymore Live Queries until restarted
		$jQlq.running = false;
	},

	play: function() {
		// Restart Live Queries
		$jQlq.running = true;
		// Request a run of the Live Queries
		$jQlq.run();
	},

	registerPlugin: function() {
		$.each( arguments, function(i,n) {
			// Short-circuit if the method doesn't exist
			if (!$.fn[n] || $.inArray(n, $jQlq.registered) > 0) return;

			// Save a reference to the original method
			var old = $.fn[n];

			// Create a new method
			$.fn[n] = function() {
				// Call the original method
				var r = old.apply(this, arguments);

				// Request a run of the Live Queries
				$jQlq.run();

				// Return the original methods result
				return r;
			};

			$jQlq.registered.push(n);
		});
	},

	run: function(id) {
		if (id !== undefined) {
			// Put the particular Live Query in the queue if it doesn't already exist
			if ( $.inArray(id, $jQlq.queue) < 0 )
				$jQlq.queue.push( id );
		}
		else
			// Put each Live Query in the queue if it doesn't already exist
			$.each( $jQlq.queries, function(id) {
				if ( $.inArray(id, $jQlq.queue) < 0 )
					$jQlq.queue.push( id );
			});

		// Clear timeout if it already exists
		if ($jQlq.timeout) clearTimeout($jQlq.timeout);
		// Create a timeout to check the queue and actually run the Live Queries
		$jQlq.timeout = setTimeout($jQlq.checkQueue, 20);
	},

	stop: function(id) {
		if (id !== undefined)
			// Stop are particular Live Query
			$jQlq.queries[ id ].stop();
		else
			// Stop all Live Queries
			$.each( $jQlq.queries, $jQlq.prototype.stop);
	}
});

// Register core DOM manipulation methods
$jQlq.registerPlugin('append', 'prepend', 'after', 'before', 'wrap', 'attr', 'removeAttr', 'addClass', 'removeClass', 'toggleClass', 'empty', 'remove', 'html', 'prop', 'removeProp');

// Run Live Queries when the Document is ready
$(function() { $jQlq.play(); });
}));

//////////////////////////////////////////////////////////////
//JSV
//////////////////////////////////////////////////////////////
function JSV() { 
}
// ////////////////////////////////////////////////////////////
// server constants
// ////////////////////////////////////////////////////////////
JSV.CONTEXT_PATH = '';
JSV.LIBRARY_PATH = '';
JSV.SKIN_COOKIE = 'com.kcube.jsv.skin';
JSV.LOCALE_COOKIE = 'com.kcube.jsv.locale';
JSV.STATE_KEY = 'com.kcube.jsv.state';
JSV.REDIRECT_KEY = 'com.kcube.jsv.redirect';
JSV.FORWARD_KEY = 'com.kcube.jsv.forward';
JSV.ERROR_FORWARD_KEY = 'com.kcube.jsv.error.forward';
JSV.ALERT_KEY = 'com.kcube.jsv.alert';
JSV.ALERT_SEQ = 'com.kcube.jsv.alert.seq';
JSV.DOMAIN_KEY = 'com.kcube.jsv.domain';
JSV.LICENSE = {};

/**
 * 도메인과 CONTEXT-PATH가 포함된 주소를 돌려준다.
 */
JSV.getLocationPath = function(url, query) {
	return location.protocol + '//' + location.host + JSV.getContextPath(url, query);
}
/**
 * CONTEXT-RELATIVE PATH를 돌려준다. 
 * QUERY를 추가 할 수 있다.
 */
JSV.getContextPath = function(url, query) {
	if (!url) {
		// location.pathname은 context path와 url session을 이미 포함
		// prefix를 추가하면 안된다.
		return JSV.suffix(location.pathname, query);
	}
	url = JSV.suffix(url, query);
	url = JSV.merge(url, JSV.QUERY);
	if (url.charAt(0) != '/') {
		return url;
	}
	if (url.charAt(1) == '/') {
		url = JSV.prefix(JSV.LIBRARY_PATH, url.substring(1));
	}
	return JSV.prefix(JSV.CONTEXT_PATH, url);
}
JSV.suffix = function(url, query) {
	if (!query) {
		return url;
	} else if (url.indexOf('?') < 0) {
		return url + '?' + query;
	} else {
		return url + '&' + query;
	}
}
JSV.prefix = function(root, url) {
	if (!root) return url;
	if (!url) return root;
	var rl = root.charAt(root.length - 1);
	var uf = url.charAt(0);
	if (rl == '/' && uf == '/') {
		return root + url.substring(1);
	}
	if (rl != '/' && uf != '/') {
		return root + '/' + url;
	}
	return root + url;
}

// ////////////////////////////////////////////////////////////
// util
// ////////////////////////////////////////////////////////////
JSV.SEQUENCE = 1;
JSV.generateId = function() {
	return 'com.kcube.jsv.uuid.' + JSV.SEQUENCE++;
}
JSV.merge = function(html, map) {
	var s = html;
	for(var key in map) {
		var re = new RegExp('\\@\\{' + key + '\\}', 'g');
		s = s.replace(re, map[key]);
	}
	return s;
}
JSV.overlap = function(newMap, defaultMap) {
	var map = new Object();
	for (var key in defaultMap) {
		map[key] = defaultMap[key];
	}
	for (var key in newMap) {
		map[key] = newMap[key];
	}
	return map;
}
JSV.createElement = function(html) {
	return $('<div>').html(html).get(0).firstChild;
}
JSV.toMap = function(str, sep1, sep2) {
	var map = new Object();
	if (str == null) return map;
	if (sep1 == null) sep1 = ':';
	if (sep2 == null) sep2 = ';';
	var arr = str.split(sep2);
	for (var i = 0; i < arr.length; i++) {
		var j = arr[i].indexOf(sep1);
		if (j > 0) {
			map[arr[i].substring(0, j)] = arr[i].substring(j + 1);
		} else {
			map[arr[i]] = null;
		}
	}
	return map;
}
JSV.toString = function(map, sep1, sep2) {
	if (map == null) return '';
	if (sep1 == null) sep1 = ':';
	if (sep2 == null) sep2 = ';';
	var arr = new Array();
	for(var key in map) {
		if (key && map[key] != null) {
			arr[arr.length] = key + sep1 + map[key];
		}
	}
	return arr.join(sep2);
}
/**
 * 로그 창을 띄워서 로그를 남긴다.
 */
JSV.log = function(msg) {
	if (!JSV.logWindow) {
		JSV.logWindow = window.open();
		JSV.logWindow.document.writeln(location.pathname + '<br>');
	}
	JSV.logWindow.document.writeln(msg + '<br>');
}
/**
 * 콘솔에 log를 남긴다.
 */
JSV.consoleLog = function(msg) {
	if ( window.console && console.log ) {
		console.log(msg);
	}
}
/**
 * 값을 복제한다.
 */
JSV.clone = function(obj) {
	// 'number' 'string' 'boolean' 'object' 'function' 'undefined'
	if (typeof(obj) == 'number' || typeof(obj) == 'string' || typeof(obj) == 'boolean') {
		return obj;
	} else if (obj && typeof(obj) == 'object') {
		var ret;
		var isObj = true;
		if (obj.constructor.toString().indexOf('function Array()') < 0) {
			ret = new Object();
		} else {
			isObj = false;
			ret = new Array();
		}
		for (var key in obj) {
			if (!JSV.excludeFunction(isObj, key) && typeof(ret[key]) != 'function')
				ret[key] = JSV.clone(obj[key]);
		}
		return ret;
	} else {
		return null;
	}
}
/**
 * 10 이하의 경우 0을 붙여서 return 해준다.
 */
JSV.appendZero = function(val) {
	return val < 10 ? '0' + val : val;
}
/**
 * XQuared Editor에서 재정의한 함수들을 제거한다. Array나 Object 로 생성한 객체를 JSV.clone으로 복사할 경우 속성값의 이름에 아래의 이름은 반드시 피해서 만든다. Object =>
 * bind, bindAsEventListener, Array => find, findAll, first, last, flatten, includeElement ex) obj.bind (X), arr.find
 * (X)
 */
JSV.excludeObjectKey = {'bind':true, 'bindAsEventListener':true};
JSV.excludeArrayKey = {'find':true, 'findAll':true, 'first':true, 'last':true, 'flatten':true, 'includeElement':true};
JSV.excludeFunction = function(isObj, key) {
	if (isObj)
		return (JSV.excludeObjectKey[key]) ? true : false;
	else
		return (JSV.excludeArrayKey[key]) ? true : false;
}
JSV.setStyle = function(obj, style, defaultStyle) {
	for (var key in defaultStyle) {
		if (style && typeof(style[key]) != 'undefined') {
			obj[key] = style[key];
		} else {
			obj[key] = defaultStyle[key];
		}
	}
}
JSV.encode = function(value) {
	return encodeURIComponent(value);
}
JSV.decodeXSS = function(escape_str) {
	return escape_str.replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&#039;/g, '\'');
}
// ////////////////////////////////////////////////////////////
// component
// ////////////////////////////////////////////////////////////
JSV.include = function(url, callback, data) {
	if(/.css$/.test(url))
		JSV.include.loadCSS(url, callback, data);
	else if(/.js$/.test(url))
		JSV.include.loadJS(url, callback, data);
}
JSV.include.loadCSS = function(url, callback, data) {
	var css=document.createElement('link');
	css.setAttribute('type','text/css');
	css.setAttribute('rel','stylesheet');
	css.setAttribute('href',JSV.getContextPath(url));
	$('head').get(0).appendChild(css);
	if (callback && $.isFunction(callback)) {
		JSV.browser.msie ? JSV.include.IEonload(css, callback, data)
					: callback(data);
	}
}
JSV.include.loadJS = function(url, callback, data) {
	var js=document.createElement('script');
	js.setAttribute('type','text/javascript');
	js.setAttribute('src',JSV.getContextPath(url));
	if (callback && $.isFunction(callback)) {
		(JSV.browser.msie && !JSV.browser.msieEqualOrOver11) ? JSV.include.IEonload(js, callback, data) 
					: js.onload = function(){callback(data)};
	}
	$('head').get(0).appendChild(js);
}
JSV.include.IEonload = function(elm, callback, data) {
	var isFirst = true;
	elm.onreadystatechange = function() {
		if(isFirst && (this.readyState=='loaded'||this.readyState=='complete')){
			isFirst = false;
			callback(data);
		}
	}
}
// ////////////////////////////////////////////////////////////
// XML
// ////////////////////////////////////////////////////////////
// async = false를 주어 동기화 처리 한다.
JSV.loadXml = function(path, query) {
	var doc = null;
	var url = JSV.getContextPath(path, query);
	$.ajax({'url':url, 
			'dataType':'xml', 
			'async':false,
			'xhrFields': JSV.browser.msieEqualOrOver10 ? { responseType: 'msxml-document' } : null,
			'success':function(data, status) { doc = data.documentElement; },
			'error':function(xhr) {
				if (JSV.browser.msie)
					window.status = 'xml error: ' + url;
				else 
					alert('xml error: ' + url);
			}
	});
	return doc;
}
// JSV context내 동일한 ajax 요청을 취소한다.
JSV.loadJSONRequest = {};
// async = false를 주어 동기화 처리 한다.
JSV.loadJSON = function(path, query, options) {
	var obj = null;
	var url = JSV.getContextPath(path, query && encodeURI(query));
	$.ajax({
		'url' : url,
	    'type' : options ? (options.type ? options.type : 'get') : 'get',
	    'data' : options ? (options.data ? options.data : {}) : {},
		'dataType' : options ? (options.dataType ? options.dataType : 'json') : 'json',
		'contentType' : options ? (options.contentType ? options.contentType : 'application/x-www-form-urlencoded') : 'application/x-www-form-urlencoded',
		'async' : options ? (options.async ? options.async : false) : false,
		'jsonp' : options ? (options.jsonp ? options.jsonp : 'callback') : 'callback',
		'success' : function(data, status) { 
			obj = (typeof(data) === 'string') ? JSV.toJsonObj(data) : data;
			options && options.success && options.success.call(this, obj);
		},
		'beforeSend' : function(xhr) {			
			if (this.type.toLowerCase() == 'get' || this.url != JSV.loadJSONRequest['url'] || this.data != JSV.loadJSONRequest['data']) {
				JSV.loadJSONRequest = {'url': this.url, 'data': this.data};
				options && options.beforeSend && options.beforeSend.call(this, xhr);
				return true;
			} else {
				return false;
			}
		},
		'complete' : function(data) { 
			options && options.complete && options.complete.call(this, obj);
		},
		'error':function(xhr) {
			JSV.loadJSONRequest = {};
			obj = JSV.toJsonObj(xhr.responseText);
			if (options && options.error && obj.message) {
				options.error.call(this, obj);
			} else {
				JSV.setState('exception', obj.exception);
				JSV.doPOST('/error');
			}
		}
	});
	return obj;
}
JSV.loadTextXml = function(xmlStr) {
	var xmlDoc;
	if (JSV.browser.msie) {	
		xmlDoc = new ActiveXObject("Microsoft.XMLDOM")
		xmlDoc.async = false;
		xmlDoc.loadXML(xmlStr);
	} else {
		var parser = new DOMParser();
		xmlDoc = parser.parseFromString(xmlStr, "text/xml");
	}
	return xmlDoc;
}
JSV.parseXml = function(path, query) {
	var doc = JSV.loadXml(path, query);
	return doc ? JSV.toObject(doc) : null;
}
JSV.toObject = function(node) {
	var clazz = $(node).attr('class');
	if (clazz == 'Array') {
		var children = new Array();
		for (var i = 0; i < node.attributes.length; i++) {
			var n = node.attributes[i];
			children[n.nodeName] = n.value;
		}
		for (var i = 0; i < node.childNodes.length; i++) {
			var n = node.childNodes[i];
			if (n.nodeType == 1) {
				children[children.length] = JSV.toObject(n);
			}
		}
		return children;
	} else if (JSV.isObject(node)) {
		var obj = new Object();
		for (var i = 0; i < node.attributes.length; i++) {
			var n = node.attributes[i];
			// obj[n.nodeName] = n.nodeValue;
			// attribute nodeValue 와 text nodeValue의 MarkUp관련문자(&, <, >, ") 반환 형식이 다르다.
			obj[n.nodeName] = n.nodeValue.replace(/\&amp;/g, '&').replace(/\&lt;/g, '<').replace(/\&gt;/g, '>').replace(/\&quot;/g, '"');
		}
		for (var i = 0; i < node.childNodes.length; i++) {
			var n = node.childNodes[i];
			switch(n.nodeType) {
				case 1:// ELEMENT_NODE
					obj[n.nodeName] = JSV.toObject(n);
					break;
				case 3:// TEXT_NODE
					if(JSV.browser.msie && '#text' != n.nodeName && $(n).text().trim() != ''){
						obj.text = $(n).text();
					}else if(n.nodeValue.trim() != ''){
						obj.text = n.nodeValue;	
					}
					break;
			}
		}
		return obj;
	} else {
		if (node.childNodes.length <= 0)
			return '';	
		return node.firstChild.nodeValue;
	}
}
JSV.isObject = function(node) {
	if (node.attributes.length > 0) {
		return true;
	}
	for(var i = 0; i < node.childNodes.length; i++) {
		if (node.childNodes[i].nodeType == 1) {// ELEMENT_NODE
			return true;
		}
	}
	return false;
}
JSV.controlNode = function(node) {
	if (JSV.browser.msieUnder9)
		return false;
	for (var i = 0; i < node.childNodes.length; i++) {
		var child = node.childNodes[i];
		if (child.childNodes.length > 0) {
			JSV.controlNode(child);
		} else if (child.nodeType == 3 && child.nodeValue.trim() == '') {
			node.removeChild(child);
			i--;
		}
	}
}
JSV.toXml = function(obj, name, roundTrip) {
	if (!name) name = 'e';
	var xml = '<' + name;
	if (roundTrip && obj instanceof Array) xml += ' class="Array"';
	xml += '>';
	if (obj instanceof Array) {
		for(var i = 0; i < obj.length; i++) {
			xml += JSV.toXml(obj[i], obj.nodeName, roundTrip);
		}
	} else if (typeof(obj) == 'number' || typeof(obj) == 'boolean') {
		xml += obj;
	} else if (typeof(obj) == 'string') {
		xml += JSV.escapeHtml(obj);
	} else if (typeof(obj) == 'object') {
		for (var key in obj) {
			xml += JSV.toXml(obj[key], key, roundTrip);
		}
	}
	xml += '</' + name + '>';
	return xml;
}
/**
 * JAVA Script 객체를 JSON 객체로 변환한다. 
 */
JSV.toJSON = function(value, replacer) {
	return JSON.stringify(value, replacer);
}
/**
 * String 객체를 JSON 객체로 변환한다.
 */
JSV.toJsonObj = function(value, replacer) {
	try{
		return JSON.parse(value, replacer);
	}catch(e){
		if (/^[\],:{}\s]*$/
		.test(value.replace(/\\(?:['\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@')
		.replace(/'[^'\\\n\r]*'|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']')
		.replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {
			var j = eval('(' + value + ')');
			return typeof replacer === 'function' ?
                    walk({'': j}, '') : j;
		}
		throw new SyntaxError('JSON.parse');
	}
}
JSV.mergeXml = function(str, element) {
	while(true) {
		var i = str.indexOf('@{');
		if (i < 0) break;
		var j = str.indexOf('}', i);
		var key = str.substring(i + 2, j);
		var val = element[key];
		str = str.substring(0, i) + val + str.substring(j + 1);
	}
	return str;
}
JSV.escapeHtml = function(escape_str) {
	return escape_str.replace(/\&/g, '&amp;').replace(/\</g, '&lt;').replace(/\>/g, '&gt;').replace(/\"/g, '&quot;');
}
JSV.unEscapeHtml = function(escaped_str) {
	if (typeof escaped_str !== 'string') {
		return escaped_str;
	}
	return escaped_str.replace(/\&lt;/g, '<').replace(/\&gt;/g, '>').replace(/\&quot;/g, '"').replace(/\&amp;/g, '&');
}
JSV.xml = function(xmlDocument){
	if(typeof xmlDocument.nodeType === undefined)return null;
	if(JSV.browser.msie){
		return xmlDocument.xml;
	}else{
		var serializer = new XMLSerializer();
		var xml = serializer.serializeToString(xmlDocument);
		return xml;
	}
}
JSV.cachedScript = function (url, options){
	options = $.extend(options || {}, {
		dataType: "script",
	    cache: true,
	    url: JSV.getContextPath(url)
	});
	return $.ajax(options);
}

// ////////////////////////////////////////////////////////////
// bean property
// ////////////////////////////////////////////////////////////
JSV.setProperty = function(bean, property, value) {
	if (bean && property) {
		var i = property.lastIndexOf('.');
		if (i < 0) {
			JSV.setNamedProperty(bean, property, value);
		} else {
			var obj = JSV.getPropertyNotNull(bean, property.substring(0, i));
			JSV.setNamedProperty(obj, property.substring(i + 1), value);
		}
	}
}
JSV.setNamedProperty = function(obj, name, value) {
	var i = name.indexOf('[');
	var j = name.indexOf(']', i);
	if (i < 0 || j < 0 || j < name.length - 1) {
		obj[name] = value;
	} else {
		var objectName = name.substring(0, i);
		var attribName = name.substring(i + 1, j);
		if (obj[objectName] == null) {
			obj[objectName] = new Array();
		}
		obj[objectName][attribName] = value;
	}
}
JSV.getPropertyNotNull = function(bean, property) {
	var value = JSV.getProperty(bean, property);
	if (value != null) {
		return value;
	} else {
		var obj = new Object();
		JSV.setProperty(bean, property, obj);
		return obj;
	}
}
JSV.getProperty = function(bean, property) {
	if (property && property.indexOf(',') > 0) {
		var names = property.split(',');
		var value = [];
		for (var i = 0; i < names.length; i++) {
			var name = names[i];
			var prop = JSV.getSingleProperty(bean, name);
			value[name] = prop;
			value[i] = prop;
		}
		return value;
	} else {
		return JSV.getSingleProperty(bean, property);
	}
}
JSV.getSingleProperty = function(bean, property) {
	var value = null;
	try {
		if (bean && property) {
			if (property.charAt(0) == '[') {
				eval('value = bean' + property);
			} else {
				eval('value = bean.' + property);
			}
		}
	} catch(exception) {
	}
	return value;
}

// ////////////////////////////////////////////////////////////
// cookie
// ////////////////////////////////////////////////////////////
JSV.setCookie = function(name, value, expireMillis) {
	var expires = '';
	if (expireMillis) {
		expires = 'expires=' + new Date(new Date().getTime() + expireMillis).toGMTString() + ';';
	}
	var path = 'path=/;';
	value = value ? escape(value) : '';
	document.cookie = name + '=' + value + ';' + expires + 'path=/';
}
JSV.removeCookie = function(name) {
	JSV.setCookie(name, null, -1);
}
JSV.getCookie = function(name) {
	var arr = document.cookie.split(';');
	for (var i = 0; i < arr.length; i++) {
		var j = arr[i].indexOf(name + '=');
		if (j >= 0) {
			return unescape(arr[i].substring(j + name.length + 1));
		}
	}
	return null;
}

// ////////////////////////////////////////////////////////////
// JSV state(skin)
// ////////////////////////////////////////////////////////////
JSV.getSkin = function(candidateSkins, fallbackSkin) {
	var skin = JSV.getCookie(JSV.SKIN_COOKIE);
	return JSV.getMatch(skin, candidateSkins, fallbackSkin);
}
JSV.setSkin = function(skin, expireMillis) {
	JSV.setCookie(JSV.SKIN_COOKIE, skin, expireMillis);
}
JSV.getMatch = function(target, candidates, fallback) {
	if (candidates == null || candidates.length == 0) {
		return fallback;
	}
	if (!fallback) {
		fallback = candidates[0];
	}
	if (!target) {
		return fallback;
	}
	for(var i = 0; i < candidates.length; i++) {
		if (candidates[i] == target) return candidates[i];
	}
	for(var i = 0; i < candidates.length; i++) {
		if (target.indexOf(candidates[i]) == 0) return candidates[i];
	}
	for(var i = 0; i < candidates.length; i++) {
		if (candidates[i].indexOf(target) == 0) return candidates[i];
	}
	return fallback;
}
JSV.isValidLicense = function(license) {
	return JSV.LICENSE[license] || false;
}
JSV.setLicense = function(license) {
	var arr = license.split(',');
	for (var i = 0; i < arr.length; i++) {
		JSV.LICENSE[arr[i]] = true;
	}
}
// ////////////////////////////////////////////////////////////
// JSV locale
// ////////////////////////////////////////////////////////////
JSV.getLocale = function() {
	return JSV.LOCALE;
}
JSV.getDefaultLocale = function() {
	return JSV.DEFAULTLOCALE;
}
JSV.setLocale = function(userLocale, defaultLocale) {
	if(defaultLocale)JSV.DEFAULTLOCALE = defaultLocale;
	JSV.LOCALE = userLocale;
}
JSV.getLocaleStr = function(str) {
	if (str == null) 
		return str;
	var jsonStr = {};
	try{
		jsonStr = JSV.toJsonObj(str);
	}catch(e){}
	if( jsonStr[JSV.getLocale()] ){
		return jsonStr[JSV.getLocale()];
	}else if(jsonStr[JSV.getDefaultLocale()]){
		return jsonStr[JSV.getDefaultLocale()]; 
	}else if(jsonStr[JSV.getDefaultLocale()] === ''){
		return '';
	}else{
		return str ; 
	}
}
JSV.getLocaleImagePath = function(path) {
	var idx = path.lastIndexOf('.');
	var p = path.substr(0, idx) + '_' + JSV.getLocale() + path.substr(idx);
	return JSV.getContextPath(p);
}
// ////////////////////////////////////////////////////////////
// Javascript Language
// ////////////////////////////////////////////////////////////
JSV.lang = {};
JSV.addLang = function(module, resource) {
	JSV.lang[module] = resource;
}	
JSV.getLang = function(module, key) {
	return (JSV.lang[module] != null) ? JSV.lang[module][key] : '@' + module + '.' + key;
}	
// ////////////////////////////////////////////////////////////
// request parameter/state
// ////////////////////////////////////////////////////////////
JSV.PORTLET = 1;
/**
 * 현재 url의 query string에 포함된 parameter 값을 받아 온다.
 */
JSV.getParameter = function(name, ptlId) {
	if (ptlId) {
		var query = eval('JSV.QUERY' + ptlId);
		return (query && query[name]) ? query[name] : null;
	} else
		return JSV.QUERY[name];
}
JSV.getParameterMap = function(ptlId) {
	if (ptlId) {
		var query = eval('JSV.QUERY' + ptlId);
		return query || null;
	} else
		return JSV.QUERY;
}
JSV.removeQuery = function(name, ptlId) {
	if (ptlId) {
		var query = eval('JSV.QUERY' + ptlId);
		if (query)
			delete query[name];
	} else
		delete JSV.QUERY[name];
}
JSV.setState = function(name, value, ptlId) {
	if (ptlId) {
		var state = eval('JSV.STATE' + ptlId);
		if (state)
			state[name] = value;
	} else
		JSV.STATE[name] = value;
}
JSV.removeState = function(name, ptlId) {
	if (ptlId) {
		var state = eval('JSV.STATE' + ptlId);
		if (state)
			delete state[name];
	} else
		delete JSV.STATE[name];
}
JSV.removeStateNotInNames = function(name, ptlId) {
	if (name.constructor.toString().indexOf('function Array()') < 0)return;
	var state = ptlId ? eval('JSV.STATE' + ptlId) : JSV.STATE;
	if (state != null) {
		for(var key in state) {
			var found = false;
			$.each(name, function(i){
				if(this == key)found = true;
			});
			if(!found)delete state[key];
		}
	}
}
JSV.setViewPosition = function(acName) {
	JSV.VIEWSHARP = acName;
}
JSV.doLOAD = function(action, target, ptlId, disuseCtx, callback) {
	var param = {'PORTLET_ID':JSV.PORTLET++};
	var i = action.indexOf('?');
	if (i > 0) {
		var map = JSV.toMap(action.substring(i + 1), '=', '&');
		for (var key in map) {
			param[key] = map[key];
		}
		action = action.substring(0, i);
	}

	var arr = new Array();
	var state = ptlId ? eval('JSV.STATE' + ptlId) : JSV.STATE;
	if (state != null) {
		for(var key in state) {
			var value = state[key];
			if (value != null) {
				param[key] = value;
				arr.push(key);
			}
		}
	}
	if (arr.length > 0)
		param[JSV.STATE_KEY] = arr.join(',');
	if (callback)
		$('#' + target).load(disuseCtx ? action : JSV.getContextPath(action), param, callback);
	else
		$('#' + target).load(disuseCtx ? action : JSV.getContextPath(action), param);
}
/**
 * 상태를 GET 방식으로 전달한다
 */
JSV.doGET = function(action, target) {
	JSV.doSubmit(action, target, 'GET');
}
/**
 * 상태를 POST 방식으로 전달한다
 */
JSV.doPOST = function(action, target) {
	JSV.doSubmit(action, target, 'POST');
}
/**
 * 상태를 Submit으로 전달한다
 */
JSV.doSubmit = function(action, target, method) {
	var f = $('<form style="margin:0px;"></form>').attr('target', target || '_self').appendTo('body'), _action, _method;
	if (method == 'GET') {
		_action = JSV.setFormQuery(f, JSV.getContextPath(action)), _method = 'GET';
	} else {
		_action = JSV.getContextPath(action), _method = 'POST';
	}
	if(JSV.VIEWSHARP){
		_action = _action + '#' + JSV.VIEWSHARP;
	}
	f.attr({'action' : _action, 'method' : _method});
	JSV.setFormState(f);
	f.get(0).submit();
}
JSV.setFormQuery = function(f, action) {
	var i = action.indexOf('?');
	if (i > 0) {
		var map = JSV.toMap(action.substring(i + 1), '=', '&');
		for (var key in map) {
			JSV.addHidden(f, key, map[key]);
		}
		return action.substring(0, i);
	} else {
		return action;
	}
}
JSV.setFormState = function(f) {
	var arr = new Array();
	for(var key in JSV.STATE) {
		var value = JSV.STATE[key];
		if (value != null) {
			JSV.addHidden(f, key, value);
			arr.push(key);
		}
	}
	if (arr.length > 0) {
		JSV.addHidden(f, JSV.STATE_KEY, arr.join(','));
	}
}
JSV.addHidden = function(f, name, value) {
	$('<input type="hidden" name="' + name + '">').val(value).appendTo(f);
}
//////////////////////////////////////////////////////////////
//module parameter/state
//////////////////////////////////////////////////////////////
JSV.MODULEPARAM = {};
JSV.MODNAMES = {CLASSID : 'csId', MODULEID : 'mdId', SPACEID : 'spId', MENUID : 'menuId', DSID : 'dmSpaceId'};
JSV.MODMETHODNAMES = {CLASSID : 'classId', MODULEID : 'moduleId', SPACEID : 'spaceId', MENUID : 'menuId', DSID : 'dsId'};
JSV.getModuleUrl = function(url, ptlId) {
	var moduleParam = ptlId ? eval('JSV.MODULEPARAM' + ptlId) : JSV.MODULEPARAM;
	if (url && !$.isEmptyObject(moduleParam)) {
		var param = [];
		for(var key in moduleParam)	{
			if(typeof(moduleParam[key]) != 'undefined')
				param.push(JSV.MODNAMES[key] + '=' + moduleParam[key]); 
		}
		return (url.indexOf('?') > 0) ? url + '&' + param.join('&') : url + '?' + param.join('&'); 
	} else {
		return url;
	}
}
JSV.getClassId = function (ptlId) {
	return ptlId ? eval('JSV.MODULEPARAM' + ptlId).CLASSID || null : JSV.MODULEPARAM.CLASSID || null;
}
JSV.getModuleId = function (ptlId) {
	return ptlId ? eval('JSV.MODULEPARAM' + ptlId).MODULEID || null : JSV.MODULEPARAM.MODULEID || null;
}
JSV.getSpaceId = function (ptlId) {
	return ptlId ? eval('JSV.MODULEPARAM' + ptlId).SPACEID || null : JSV.MODULEPARAM.SPACEID || null;
}
JSV.getMenuId = function (ptlId) {
	return ptlId ? eval('JSV.MODULEPARAM' + ptlId).MENUID || null : JSV.MODULEPARAM.MENUID || null;
}
JSV.getDsId = function (ptlId) {
	return ptlId ? eval('JSV.MODULEPARAM' + ptlId).DSID || null : JSV.MODULEPARAM.DSID || null;
}
JSV.setDsId = function (dsId, ptlId) {
	var moduleParam = ptlId ? eval('JSV.MODULEPARAM' + ptlId) : JSV.MODULEPARAM;
	moduleParam.DSID = dsId;
	JSV.setState(JSV.MODNAMES.DSID, dsId, ptlId);
}
JSV.setModuleState = function (ptlId) {
	var moduleParam = ptlId ? eval('JSV.MODULEPARAM' + ptlId) : JSV.MODULEPARAM;
	
	$.each(JSV.MODNAMES, function(key){
		if(JSV.getParameter(this, ptlId)) {
			moduleParam[key] = JSV.getParameter(this, ptlId);
		}
	});
	if (!$.isEmptyObject(moduleParam)) {
		$.each(moduleParam, function(index) {
			JSV.setState(JSV.MODNAMES[index], moduleParam[index], ptlId);
		});
	}
}

JSV.getModuleParamState = function(ptlId){
	return (JSV.getClassId(ptlId)||'') + '.' + (JSV.getModuleId(ptlId)||'') + '.' + (JSV.getSpaceId(ptlId)||'') + '.' + (JSV.getMenuId(ptlId)||'')
}
// ////////////////////////////////////////////////////////////
// Observer/Observable
// ////////////////////////////////////////////////////////////
JSV.OBSERVATIONS = [];
JSV.register = function(observable, observer, notify) {
	var o = {
		'observable': observable,
		'observer': observer,
		'notify': (notify ? notify : 'notify')
	}
	JSV.OBSERVATIONS.push(o);
}
JSV.notify = function(value, observable, name) {
	for (var i = 0; i < JSV.OBSERVATIONS.length; i++) {
		var o = JSV.OBSERVATIONS[i];
		if (o.observable == observable) {
			var observer = o.observer;
			var notify = o.notify;
			if (name){
				if (notify == name) observer[name](value, observable);
			} else {
				if (observer[notify]) observer[notify](value, observable);
			}
		}
	}
}

// ////////////////////////////////////////////////////////////
// alert
// ////////////////////////////////////////////////////////////
JSV.setUrlAlert = function(url, msg) {
	if (msg) {
		var seq = JSV.getCookie(JSV.ALERT_SEQ);
		if (seq) {
			seq = parseInt(seq) + 1;
		} else {
			seq = 1;
		}
		JSV.setCookie(JSV.ALERT_SEQ, seq);
		var key = JSV.ALERT_SEQ + '.' + seq;
		JSV.setCookie(key, msg);
		var query = JSV.ALERT_KEY + '=' + key;
		var sep = (url.indexOf('?') < 0) ? '?' : '&';
		return url + sep + query;
	}
	return url;
}
JSV.alert = function() {
	var key = JSV.getParameter(JSV.ALERT_KEY);
	if (key) {
		var msg = JSV.getCookie(key);
		if (msg) {
			JSV.removeCookie(key);
			alert(msg);
		}
	}	
}
JSV.removeAlert = function() {
	var key = JSV.getParameter(JSV.ALERT_KEY);
	if (key) {
		JSV.removeCookie(key);
	}
}
//////////////////////////////////////////////////////////////
//browser
//////////////////////////////////////////////////////////////
JSV.userAgent = navigator.userAgent.toLowerCase();
JSV.appVersion = navigator.appVersion.toLowerCase();
JSV.browser = {
	msie : !!/msie [\w.]+/.exec( JSV.userAgent ) || (JSV.userAgent.indexOf('trident') > 0),
	mozilla : !!/(mozilla)(?:.*? rv:([\w.]+))?/.exec( JSV.userAgent ),
	firefox : !!/(firefox)(?:.*? rv:([\w.]+))?/.exec( JSV.userAgent ),
	opera : !!/(opera)(?:.*version)?[ \/]([\w.]+)/.exec( JSV.userAgent ) || !!/(opr)(?:.*? rv:([\w.]+))?/.exec( JSV.userAgent ),
	webkit : !!/(webkit)[ \/]([\w.]+)/.exec( JSV.userAgent ),
	android : JSV.userAgent.indexOf('android') > 0,
	iphone : JSV.userAgent.indexOf('iphone') > 0,
	ipad : JSV.userAgent.indexOf('ipad') > 0,
	ipod : JSV.userAgent.indexOf('ipod') > 0,
	msie6 : !!/msie [\w.]+/.exec( JSV.userAgent ) && document.documentMode <= 6,
	msie7 : !!/msie [\w.]+/.exec( JSV.userAgent ) && document.documentMode == 7,
	msieUnder9 : !!/msie [\w.]+/.exec( JSV.userAgent ) && document.documentMode < 9, 
	msie10 : !!/msie [\w.]+/.exec( JSV.userAgent ) && (JSV.appVersion.indexOf("msie 10") > 0 || document.documentMode == 10),
	msie11 : (JSV.userAgent.indexOf('trident') > 0 && JSV.appVersion.indexOf('rv:11') > 0) || (!!/msie [\w.]+/.exec( JSV.userAgent ) && document.documentMode == 11),
	msie12 : (JSV.userAgent.indexOf('trident') > 0 && JSV.appVersion.indexOf('rv:12') > 0) || (!!/msie [\w.]+/.exec( JSV.userAgent ) && document.documentMode == 12)
}
JSV.browser.msieEqualOrOver11 = ( JSV.browser.msie11 || JSV.browser.msie12) ;
JSV.browser.msieEqualOrOver10 = (JSV.browser.msie10 || JSV.browser.msie11 || JSV.browser.msie12) ;
JSV.browser.msieEqualOrOver9  = (JSV.browser.msie && (document.documentMode >= 9 || JSV.browser.msieEqualOrOver10)) ;
JSV.browser.msieEqualOrUnder9 = (JSV.browser.msie && document.documentMode <= 9) ;
JSV.browser.chrome = ( !!/(chrome)[ \/]([\w.]+)/.exec( JSV.userAgent ) && !JSV.browser.opera) ;
JSV.browser.safari = !JSV.browser.chrome && JSV.browser.webkit &&  !!/(safari)[ \/]([\w.]+)/.exec( JSV.userAgent ) ;
JSV.browser.mobile = (JSV.browser.android || JSV.browser.iphone || JSV.browser.ipad || JSV.browser.ipod); 
if(JSV.browser.msie6) {
	try {
		document.execCommand("BackgroundImageCache", false, true);
	} catch(err) {}
}
// ////////////////////////////////////////////////////////////
// JSV modalDialog
// ////////////////////////////////////////////////////////////
JSV.modal = {};
JSV.modal.args = null;
JSV.modal.layerArgs = null;
JSV.modal.callback = null;
JSV.modal.layerCallback = null;
JSV.modal.currentWindow = null;	// IE이외 Modal 대응용 추가
JSV.modal.lockWindow = function() {
	$(document).bind('keydown.modal', function(event) {
		if (event.which > 111 && event.which < 124)
			return false;
	});
	$(document).bind('contextmenu.modal', function(event) {
		return false;
	});
}
JSV.modal.releaseWindow = function() {
	JSV.modal.callback = null;
	$('#modalFadeLayer').hide();
	$(document).unbind('keydown.modal').unbind('contextmenu.modal').unbind('click.modal');
	$(window).unbind('resize.modal');
}
JSV.modal.createFadeLayer = function() {
	if (!$('*').is('#modalFadeLayer')) {
		$('<div>').attr('id','modalFadeLayer').css({'opacity':'0.0', 'zIndex':'9999', 'position':'absolute', 'top':'0px', 'left':'0px'}).appendTo('body');
	}
	$('#modalFadeLayer').show();
	JSV.modal.resizeFadeLayer();
	$(window).bind('resize.modal', function() {
		JSV.modal.resizeFadeLayer();
	});
}
JSV.modal.resizeFadeLayer = function() {
	setTimeout(function() {
		$('#modalFadeLayer')
			.width(document.body.offsetWidth + 'px')
			.height(($(document).height() > document.body.offsetHeight) ? $(document).height() + 'px': document.body.offsetHeight + 'px');
	}, 100);
}
JSV.modal.listenWindow = function(win) {
	if (!win) return;
	if (win.closed) {
		JSV.modal.releaseWindow();
	} else {
		setTimeout(function() {
			JSV.modal.listenWindow(win);
		}, 400);
	}
}
JSV.dialogArguments = null;
JSV.useModalDialogArguments = function(isCrossOrigin) {
	if (!isCrossOrigin) {
		if (JSV.browser.msie && window.dialogArguments) {
			JSV.dialogArguments = window.dialogArguments;
		} else if (!JSV.browser.msie && opener && opener.JSV) {
			JSV.dialogArguments = opener.JSV.modal.args;
			JSV.modal.lockWindow();
			$(function(){
				$(window).bind('beforeunload', function(event){
					opener.JSV.modal.releaseWindow();
				}).bind('unload', function(event){
					opener.JSV.modal.releaseWindow();
				});
			});
		}
	}
}
JSV.showModalDialog = function(url, args, feature, callback) {
	JSV.modal.callback = callback;
	if (JSV.browser.msie) {
		var value = window.showModalDialog(url, args, feature);
		if (typeof callback !== 'undefined' && typeof value !== 'undefined') {
			JSV.modal.callback(value);
		}
		JSV.modal.callback = null;
	} else {
		JSV.modal.args = args;
		JSV.modal.createFadeLayer();
		$(document).bind('keydown.modal', function(event) {
			return false;
		});
		$(document).bind('contextmenu.modal', function() {
			return false;
		});
		
		// IE이외 Modal 대응용 추가변경
		$(document).bind('click.modal', function(event) {
			if (!JSV.browser.msie && JSV.modal.currentWindow) {
				JSV.modal.currentWindow.focus();
			}
			return false;
		});
		
		var fobj = feature.split(';');
		var fstr = 'toolbar=no,menubar=no,location=no,resizable=no';
		for (var i = 0; i < fobj.length; i++) {
			var each = fobj[i].split(':');
			var key = each[0].toLowerCase();
			if (key.indexOf('width') > -1) {
				fstr += ',width=' + each[1];
				continue;
			}
			if (key.indexOf('height') > -1) {
				fstr += ',height=' + each[1];
				continue;
			}
			if (key.indexOf('left') > -1) {
				fstr += ',left=' + each[1];
				continue;
			}
			if (key.indexOf('top') > -1) {
				fstr += ',top=' + each[1];
				continue;
			}
			if (key.indexOf('scroll') > -1) {
				fstr += ',scrollbars=' + each[1];
				continue;
			}
			fstr += ',' + each[0] + '=' + each[1];
		}
//		var win = window.open(url, '', fstr);
		JSV.modal.currentWindow = window.open(url, '', fstr);
		
		if (JSV.browser.opera)
			JSV.modal.listenWindow(win);
	}
}
JSV.setModalReturnValue = function(value) {
	if (JSV.browser.msie) {
		window.returnValue = value;
	} else if (opener && opener.JSV.modal.callback && typeof opener.JSV.modal.callback === 'function') {
		opener.JSV.modal.callback(value);
		opener.JSV.modal.callback = null;
	}
}
JSV.layerModalDialog = null;
JSV.showLayerModalDialog = function(url, args, feature, callback) {
	JSV.modal.layerCallback = callback;
	JSV.modal.layerArgs = args;
	
	var fobj;
	if (!feature && feature == null) {
		fobj = {'width':800, 'height':'400', 'resizable': false};
	} else {
		fobj = feature;
		if (fobj.resizable == null || fobj.resizable == undefined)
			fobj.resizable = false;
	}
	fobj = $.extend(fobj, {'modal':true, 'autoOpen':false, 'dialogClass':'LayerModalDialog'});

	var existLayer = document.getElementById('layerModalDialog');
	var layerDiv = existLayer ? $(existLayer) : $('<div>').attr('id', 'layerModalDialog').appendTo('body');
	
	if(existLayer) {
		layerDiv.dialog(fobj);
		layerDiv.dialog('open');
	} else {
		JQueryUI.load(function(){
			layerDiv.bind('dialogclose', function(e, ui) {
				$(this).empty();
				JSV.layerModalDialog = null;
				JSV.modal.layerCallback = null;
				JSV.modal.layerArgs = null;
			}).bind('dialogdragstop', function(e, ui) {
				if (!fobj.resizable && (fobj.height == null || fobj.height == undefined)) {
					$(this).height('auto');
					$(this).parent().height('auto');
				}
			}).bind('dialogopen', function(e, ui) {
				if (!fobj.resizable && (fobj.height == null || fobj.height == undefined)) {
					$(this).parent().css({top:document.body.scrollTop + 100});
					$(this).height('auto');
				}
				$(this).parent().focus();
			});
			layerDiv.dialog(fobj);
			layerDiv.dialog('open');
		});
	}

	if (!fobj.resizable && (fobj.height == null || fobj.height == undefined)) {
		layerDiv.resize(function(e) {
		   var layerTop = $(this).offset().top;
		   var layerHeight = $(this).height();
		   var clientHeight = document.body.parentElement.clientHeight;
		   var layerBottom = layerTop + layerHeight;
		   if (layerBottom > clientHeight) {
			   $(this).height(clientHeight - layerTop - 3);
		   }
		});
	}
	JSV.layerModalDialog = layerDiv;
	JSV.doLOAD(url, 'layerModalDialog', false, true);
}
JSV.layerDialogClose = function(){
	JSV.layerModalDialog.dialog('close');
}
JSV.setLayerModalReturnValue = function(value) {
	JSV.modal.layerCallback(value);
}
// ////////////////////////////////////////////////////////////
// tree Resize
// 여유 margin을 주고 싶을 경우 margin값을 number type으로
// 세번째 인자에 세팅한다.
// ////////////////////////////////////////////////////////////
JSV.autoResizeElement = function(element, fixElement, margin) {
	if (JSV.browser.msie6) {
		$(function() {JSV.computeSizeElement(element, fixElement, margin);}); 
	} else {
		if (JSV.browser.webkit) {
			setTimeout(function() {
				JSV.computeSizeElement(element, fixElement, margin);
			}, 10);
		} else {
			JSV.computeSizeElement(element, fixElement, margin);
		}
	}
}
JSV.computeSizeElement = function(element, fixElement, margin) {
	var endResize = true;
	var endLoop = true;
	var fixHeight = (margin) ? margin : 0;
	if (fixElement && fixElement.length > 0) {
		for (var i = 0; i < fixElement.length; i++) {
			fixHeight +=  JSV.browser.msieEqualOrOver9 ? $('#' + fixElement[i]).height() : document.getElementById(fixElement[i]).clientHeight;
		}
	}
	$(window).unbind('resize.JSV').bind('resize.JSV', function(event) {
		if (JSV.browser.msie6) {
			endResize = false;
			if (endLoop) {
				var loop = setInterval(function() {
					if (endResize) {
						fixHeight = (margin) ? margin : 0;
						if (fixElement && fixElement.length > 0) {	
							for (var i = 0; i < fixElement.length; i++) {
								fixHeight += document.getElementById(fixElement[i]).clientHeight;
							}
						}
						clearInterval(loop)
						endLoop = true;
						JSV.setSizeElement(element, fixHeight);
					}
					endResize = true;
				}, 100);
				endLoop = false;
			}		
		} else {
			JSV.setSizeElement(element, fixHeight);
		}
	});
	JSV.setSizeElement(element, fixHeight);
}
JSV.setSizeElement = function(element, fixHeight) {
	var size = -6 - fixHeight;
	if (JSV.browser.msie6) {
		if (document.body.clientHeight < 10 - size)
			return false;
		$(element).hide();
		size += document.body.clientHeight;
		$(element).show();
	} else {
		size += document.body.clientHeight;
	}
	if (size < 0)
		size = 0;
	$(element).height(size);
}
// ////////////////////////////////////////////////////////////
// JSV block
// ////////////////////////////////////////////////////////////
JSV.Block = function(func, ptlId) {
	$(func);
}
// ////////////////////////////////////////////////////////////
// page
// ////////////////////////////////////////////////////////////
JSV.init = function(cp, lp, param, userLocale, defaultLocale, isCrossOrigin) {
	if (cp) JSV.CONTEXT_PATH = cp;
	if (lp) JSV.LIBRARY_PATH = lp;
	JSV.QUERY = param ? param : JSV.toMap(location.search.substring(1), '=', '&');
	JSV.STATE = new Object();
	if (JSV.getParameter(JSV.STATE_KEY)) {
		var keys = JSV.getParameter(JSV.STATE_KEY).split(',');
		for (var i = 0; i < keys.length; i++) {
			var key = keys[i];
			JSV.setState(key, JSV.getParameter(key));
		}
	}
	if (JSV.getParameter(JSV.DOMAIN_KEY)) {
		document.domain = JSV.getParameter(JSV.DOMAIN_KEY);
		JSV.setState(JSV.DOMAIN_KEY, document.domain);
	}
	JSV.setLocale(userLocale, defaultLocale);
	JSV.setModuleState();
	JSV.useModalDialogArguments(isCrossOrigin);
}
JSV.tail = function() {
	JSV.alert();
}
// ////////////////////////////////////////////////////////////
// prototypes
// ////////////////////////////////////////////////////////////
Array.prototype.indexOf = function(obj, equals) {
	for(var i = 0; i < this.length; i++) {
		if (equals && equals(obj, this[i])) {
			return i;
		}
		if (obj == this[i]) {
			return i;
		}
	}
	return -1;
}
Array.prototype.pop = function() {
	if(this.length > 0){
		var last = this[this.length - 1];
		this.length = this.length - 1;
		return last;
	}else{
		return null;
	}
}
Array.prototype.remove = function(obj, equals) {
	var i = this.indexOf(obj, equals);
	if (i < 0) {
		return false;
	}
	for(var j = i; j < this.length - 1; j++) {
		this[j] = this[j + 1];
	}
	this.length = this.length - 1;
	return true;
}
//////////////////////////////////////////////////////////////
// 현재 날짜를 n일 만큼 추가
//////////////////////////////////////////////////////////////
Date.prototype.addDays = function(n)
{
	this.setDate(this.getDate() + n);
	return this;
}
Date.prototype.addMonths = function(n)
{
	this.setMonth(this.getMonth() + n);
	return this;
}
String.prototype.find = function(str){
	return (this.indexOf(str) >= 0 ? true : false);
}
String.prototype.bytes = function() {
  	var str = this;
  	var l = 0;
  	for (var i = 0; i < this.length; i++) l += (this.charCodeAt(i) > 128) ? 3 : 1;
  	return l;
}
String.prototype.cutBytes = function(maxLength) {
	var str = this;
	var l = 0;
	for (var i = 0; i < str.length; i++) {
  		l += (str.charCodeAt(i) > 128) ? 3 : 1;
	  	if (l > maxLength) {
  			return str.substring(0, i);
	  	}
  	}	
  	return str;
}
String.prototype.trim = function() {
		return this.replace(/(^\s*)|(\s*$)/gi, "");
}
// ////////////////////////////////////////////////////////////
// Garbage Collection
// ////////////////////////////////////////////////////////////
if (window.attachEvent && !window.addEventListener) {
	window.attachEvent('onunload', function() {
		if (JSV.browser.msie6 || JSV.browser.msie7) {		
			for ( var id in $.cache ) {
				if ( $.cache[ id ].handle ) {
					try {
						$.event.remove( $.cache[ id ].handle.elem );
					} catch(e) {}
				}
			}
		} else {
			$('*').each(function() {
				if (this.nodeType === 1) {
					try{
						$.cleanData( this.getElementsByTagName("*") );
						$.cleanData( [ this ] );
					}catch(err){
					}
				}
				if (this.parentNode) {
					try{
						var jqGCID = 'jqGarbageCollector';           
						var jqGC = document.getElementById(jqGCID);           
						if (!jqGC) {               
							 jqGC = document.createElement('div');               
							 jqGC.id = jqGCID;               
							 jqGC.style.display = 'none';               
							 document.body.appendChild(jqGC);           
						}
						jqGC.appendChild(this);           
						jqGC.innerHTML = '';
					}catch(ee){
					}
				}
			});
		}
	});
} else if (JSV.browser.msieEqualOrOver9) {
	// 정의 필요
}
JSV.finalizeAll = function(a) {
	JSV.finalize(a);
	$(a).find('*').each(function (i) {
		JSV.finalize(this);
	});
}
JSV.finalize = function(a) {
	$(a).unbind();
	if (a.select) a.select = null;
	if (a.component) a.component = null;
	if (a.element) a.element = null;
	if (a.obj) a.obj = null;
	if (a.onmouseover) a.onmouseover = null;
	if (a.onmouseout) a.onmouseout = null;
	if (a.onclick) a.onclick = null;
	if (a.onpropertychange) a.onpropertychange = null;
}
JSV.replaceNode = function(n1, n2) {
	$(n1).replaceWith(n2);
}
JSV.removeElement = function(html, strTag, endTag){
	var index = 0;
	var origin = html;
	var orgnLower = origin.toLowerCase();
	var lStrTag = strTag.toLowerCase();
	var lEndTag = endTag.toLowerCase();
	var rtnVal = '';
	while (orgnLower.indexOf(lStrTag, index) > -1) { 
		var sIndex = orgnLower.indexOf(lStrTag, index);
		rtnVal += origin.substring(index, sIndex) ; 
		index = orgnLower.indexOf(lEndTag, index) + lEndTag.length; 
	}
	rtnVal += origin.substring(index, origin.length); 
	return rtnVal;
}
JSV.removeHtmlTag = function(content) {
	content = JSV.removeElement(content, "<style", "</style>");
	content = JSV.removeElement(content, "<script", "</script>");
	content = content.trim();
	var objStrip = new RegExp();
	objStrip = /[<][^>]*[>]/gi;
	return content.replace(objStrip, '');
}
//////////////////////////////////////////////////////////////
// User Thumbnail
//////////////////////////////////////////////////////////////
JSV.EMPTHUMB_LINKUSE = false;
JSV.EMPTHUMB_TYPE = 1300;
JSV.EMPTHUMB_SUB = '/empThumb';
JSV.EMPTHUMB_DEPTH = 3
JSV.getEmpThumbPath = function(userId) {
	var path = JSV.EMPTHUMB_SUB;
	var rest = userId % 10;
	for (var i = 0; i < JSV.EMPTHUMB_DEPTH; i++) {
		path += '/' + ((rest + i) % 10);
	}
	return path + '/' + userId;
}
//////////////////////////////////////////////////////////////
//ECM objectId를 통해 Content Type을 식별하여 준다.
//return value : 1 = document, 0 = folder, -1 = document file
//////////////////////////////////////////////////////////////
JSV.getEcmContentType = function(objectId) {
	var objectType = objectId.toString().charAt(0);
	if (objectType == '2') {
		return 1;
	} else if (objectType == '1') {
		return 0;
	} else if (objectType == '3') {
		return -1;
	}
}
//////////////////////////////////////////////////////////////
//첨부파일 사이즈 변환
//////////////////////////////////////////////////////////////
JSV.convertFileExt = function(fileName) {
	if (fileName == null)
	{
		return 'unknown';
	}
	var i = fileName.lastIndexOf('.');
	if (i < 0)
	{
		return 'unknown';
	}
	else
	{
		var fileExt = fileName.substring(i + 1);
		if (fileExt.length > 8)
		{
			return 'unknown';
		}
		return fileExt.toLowerCase();
	}
}
JSV.convertFileSize = function(size, decimal) {
	var bytes = ['B', 'KB', 'MB', 'GB'];
	var i = 0;
	var fraction = Math.pow(10, decimal) || 100;
	while (size >= 1024 && i + 1 < bytes.length) {
		i++;
		size = size / 1024;
	}
	size = Math.ceil(size * fraction) / fraction;
	return size + bytes[i];
}
//////////////////////////////////////////////////////////////
//값이 비어있는지 체크
//////////////////////////////////////////////////////////////
JSV.isEmpty = function( v ) {
	if (v == null || v == undefined || v === '' 
		|| ( $.isArray( v ) && v.length === 0 )
		|| ( $.isPlainObject( v ) && $.isEmptyObject( v ))) {
		return true;
	}
	return false;
}

JSV.isNotEmpty = function(v) {
	return !JSV.isEmpty(v);
}
//////////////////////////////////////////////////////////////
//입력필드값 타입 체크 (현재는 숫자만 체크)
//////////////////////////////////////////////////////////////
JSV.inputTypeCheck = function(target, chkType) {
	// 숫자만 입력처리
	if (chkType == 'number') {
		target.bind('keyup', function(e) {
			var thisObj = $(this);
			thisObj.css('imeMode', 'disabled');
			var value = thisObj.val().match(/[^0-9]/g);
			if (value != null) {
				thisObj.val(thisObj.val().replace(/[^0-9]/g, ''));
			}
		});
	}
};
//////////////////////////////////////////////////////////////
//문자열이 빈값이나 null값으로 들어온 경우 기본문자열을 돌려준다.
//////////////////////////////////////////////////////////////
JSV.defaultIfBlank = function( str, defaultStr ) {
	return JSV.isEmpty( str ) ? defaultStr : str;
}
//////////////////////////////////////////////////////////////
//제한글자수를 체크할 영역, 제한글자 수, 남은글자 수를 보여줄 영역 
//////////////////////////////////////////////////////////////
JSV.limitCharacter = function(chkField, limitCnt, displayField) {
	var inputText = $(chkField).val();
	var textLength = inputText.length;
	if (textLength > limitCnt) {
		$(chkField).val(inputText.substr(0,limitCnt));
		alert(JSV.getLang('msg', '045'));
		return false;
	} else if ($(displayField)) {
		$(displayField).html(' ( '+ (textLength) +' / '+ (limitCnt) +' ) ');
		return true;
	}
}
JSV.getCurrencyType = function(n) {
	var reg = /(^[+-]?\d+)(\d{3})/;
    n += '';                          
    while (reg.test(n)) {
    	n = n.replace(reg, '$1' + ',' + '$2');
    }
    return n;
}
JSV.isNumber = function(v) {
	return !/\D/.test(v);
}

JSV.isJsonStr = function(v) {
	try {
	    JSON.parse(v);
	}
	catch(e) {
	    return false;
	}
	return true;
}

JSV.wrapModalDialog = function(url, args, options, callback) {
	options = $.extend(options, {
		top : options.top ? options.top : (screen.height / 2) - (options.height / 2),
		left : options.left ? options.left : (screen.width / 2) - (options.width / 2) > screen.width ? (screen.width/2)-(options.width / 2) + (screen.width - (options.width / 2)) : (screen.width / 2) - (options.width / 2),
		width : options.width,
		height : options.height,
		scrollbars : (options.scrollbars ? options.scrollbars : 'yes')
	});
	
	var feature = 'dialogTop:'+options.top+'px;dialogLeft:'+options.left+'px;dialogWidth:'+options.width+'px;dialogHeight:'+options.height+'px;scroll:' + options.scrollbars;
	JSV.showModalDialog(url, args, feature, JSV.isEmpty(callback) ? function() {} : callback);
}

JSV.executeOnEnter = function(e, callback) {
	var keyCode = e.keyCode ? e.keyCode : e.which
	if (keyCode === 13) {
    	callback();
	}
}

/* 가로 스크롤 여부 */
JSV.hasHorizontalScrollBar = function(element) {
	return element.get(0) ? element.get(0).scrollWidth > element.innerWidth() : false;
}

/* 세로 스크롤 여부 */
JSV.hasVerticalScrollBar = function(element) {
	return element.get(0) ? element.get(0).scrollHeight > element.innerHeight() : false;
}

//////////////////////////////////////////////////////////////
//모바일 테스트용
//////////////////////////////////////////////////////////////
JSV.doTRANSITION = function(action) {
	var data = {};
	var arr = new Array();
	for(var key in JSV.STATE) {
		var value = JSV.STATE[key];
		if (value != null) {
			data[key] = value;
			arr.push(key);
		}
	}
	var url= JSV.getContextPath(action);
	var i = url.indexOf('?');
	if (i > 0) {
		var map = JSV.toMap(url.substring(i + 1), '=', '&');
		for (var key in map) {
			data[key] = map[key];
		}
		url = url.substring(0, i);
	}
	if(!JSV.isEmpty(data.division)) url += '?division='+data.division;
	JSV.doGET(url);
}

JSV.decodeURIComponent = function(str) {
	var result = decodeURIComponent(str);
	if (result === 'undefined') {
		return undefined;
	}
	return result;
};
//JSV.findListenFunc = function(w, key, msg) {
//	try {
//		if (key == 'JSV.WebDAVWaitClose' && w.JSV.WebDAVWait == null) {
//			throw 'NoDAV';
//		} else {
//			var func = eval('w.' + key + 'Listener');
//			func(msg);
//		}
//	} catch(e) {
//		try {
//			$(w.document).find('iframe').each(function() {
//				JSV.findListenFunc(this.contentWindow, key, msg);
//			});	
//		}	
//		catch (ee) {}
//	}	
//}
JSV.isWebDAVFile = function(filename) {
	var WEBDAV_EXT = 'hwp,docx,doc,docm,dot,dotm,dotx,odt,xltx,xltm,xlt,xlsx,xlsm,xlsb,xls,xll,xlam,xla,ods,pptx,pptm,ppt,ppsx,ppsm,pps,ppam,ppa,potx,potm,pot,odp,txt'.split(',');
	var idx = filename.lastIndexOf('.');
	var ext = filename.substring(idx + 1);
	
	return $.inArray(ext.toLowerCase(), WEBDAV_EXT) > -1;
}
JSV.loadWebDAVFile = function(tenantId, module, fileId, filename) {
	filename = encodeURI(encodeURIComponent(filename));
	
	if (JSV.browser.msie) {
		filename = filename.replace(/%/g, '%25');
	}
	
	JSV.loadJSON('/user/conf/generateWebDavAuthToken.json', null, {
		type: 'GET',
		success : function(data, status) {
			var url = 'kcubedav:w;' + JSV.getLocationPath('/webdav/' + tenantId + '-' + module + '-' + fileId + '-' + data.result + '/' + filename);
			
			var tmpFrame = $('#JSVWebDAVFrame');
			if (tmpFrame.length == 0) {
				tmpFrame = $('<iframe>').css({width:0, height:0, display:'none'}).attr({id:'JSVWebDAVFrame',src:url}).appendTo('body');
			} else {
				tmpFrame.attr('src', url);
			}
			
			JSV.WebDAVWait = setTimeout(function() {
				tmpFrame.remove();
				JSV.WebDAVWait = null;
				if (confirm(JSV.getLang('msg', '095'))) {
					$('<iframe>').css({width:0, height:0, display:'none'}).attr('src', JSV.getContextPath('/resources/download/KCUBEWebDAVSetup.exe')).appendTo('body');
				}
			}, 5000);
		}
	});	
}
JSV.WebDAVWait = null;
JSV.WebDAVListener = function(data) {
	setTimeout(function() {
		if (data.msg.method === 'READ') {
			clearTimeout(JSV.WebDAVWait);
		} else if (data.msg.method === 'UNLOCK') {
			var url = "kcubedav:d;" + JSV.getLocationPath('/webdav/' + data.msg.path);
			var tmpFrame = $('#JSVWebDAVFrame');
			if (tmpFrame.length == 0) {
				tmpFrame = $('<iframe>').css({width:0, height:0, display:'none'}).attr({id:'JSVWebDAVFrame',src:url}).appendTo('body');
			} else {
				tmpFrame.attr('src', url);
			}
			setTimeout(function() {
				tmpFrame.remove();
			}, 1000);
		} else if (data.msg.method === 'RELOAD') {
			location.reload();
		}
	}, 1000);
}