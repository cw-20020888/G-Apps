<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/customTagLibrary.tld" prefix="util" %>
<tiles:insertDefinition name="template/">
<tiles:putAttribute name="resource">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, target-densitydpi=medium-dpi">
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/monitor.css?nocache=${util:getServerDate()}" />" />
<%@ include file="/resources/tmpl/Monitor.tmpl" %>
<style>
.monitor .room .btm_area .time_graph table .td_time{position: relative;}
.monitor .room .btm_area .time_graph table .td_time.time_selected .time_box{position: relative;float:left;}

.td_time .currentTimeBar {width: 1px;height: 100%;z-index: 100;background: red;position: relative;top: 0;}
</style>
<script type="text/javascript">
var monitorInteval;
$(function() {
	var param = JSV.getParameter('resources') || (JSV.getParameter('multi') ? 'netmarble.com_323235343334373937@resource.calendar.google.com,netmarble.com_34303431333139323731@resource.calendar.google.com' : 'netmarble.com_323235343334373937@resource.calendar.google.com');
	var resourceEmails = decodeURIComponent(param).split(',');

	monitorAjax();

	monitorInteval = window.setInterval(function() {
		var time = new Date();
		var min = time.getMinutes();
		var sec = time.getSeconds();

		if (min % 30 != 0 && sec == 0) {
			monitorAjax();
		}
	}, 1000);

	function monitorAjax() {
		JSV.loadJSONRequest = {};
		JSV.loadJSON('/pub/reservation.json', null, {
			data: {jsonStr: JSON.stringify(resourceEmails), timeOffset: new Date().getTimezoneOffset()},
			success: function(data) {
				successAction(data);
			}
		});
	}

	function successAction(jsonData) {
		var monitorTmpl = $('#monitor').tmpl(jsonData).appendTo($('.content_area').empty());

		var startGap = 9 / 0.5;
		var nowDate = new Date();
		var nowMin = nowDate.getMinutes();
		var nowPoint = Math.floor((nowDate.getHours() / 0.5) + (nowMin / 30) - startGap);
		var nowLeft = ($('.td_time').innerWidth() / 30) * (nowMin - (nowPoint % 2 == 1 ? 30 : 0)) + 'px';

		var date = DateFormat.format(new Date(), 'yyyyMMdd');

		if (JSV.isNotEmpty(jsonData.result)) {
			$.each(jsonData.result, function() {
				var result = this;
				if (JSV.isNotEmpty(result.items)) {
					$.each(result.items, function(i) {
						var item = this;
						var sTime = this.startTime;
						var eTime = this.endTime;

						var sFormatTime = formatTime(sTime);
						var eFormatTime = formatTime(eTime);

						var resourceEmail = result.resourceEmail;
						var scheduleRoom = $.grep($('.room'), function(parents) {
							return $(parents).data('email').indexOf(resourceEmail) > -1;
						});

						// 종료시간이 오늘 9시 이전인 경우 그리지 않도록 설정.
						if (eFormatTime.full - (date + '0900') > 0 && (JSV.isNotEmpty(this.status) && this.status != 'declined')) {
							var $now = $(scheduleRoom).find('.now');

							drawScheduleInfo($now, this, result.building);

							var startHour = sFormatTime.hour;
							var startMin = sFormatTime.min;
							if (sFormatTime.day < date || (sFormatTime.day == date && sFormatTime.hour < 9)) {
								startHour = 9;
								startMin = 0;
							}
							var startPoint = Math.floor((startHour / 0.5) + (startMin / 30) - startGap);
							var timeGap = getTimeGap(sTime, eTime, date);
							var startTd = $(scheduleRoom).find('.td_time')[startPoint];
							var left = ($('.td_time').innerWidth() / 30) * (startMin - (startPoint % 2 == 1 ? 30 : 0)) + 'px';

							$(startTd).addClass('time_selected');

							var scheduleEvent = $('#scheduleEvent').tmpl({time: sTime, name: this.name}).click(function() {
								drawScheduleInfo($now, item, result.building, true);
							}).appendTo($(startTd));
							var per = timeGap / 30 * 100;

							$(scheduleEvent).css({'width' : per + '%', 'left': left, 'z-index': '1'});
						}
					});
				}
			});
		}

		$('.room').each(function() {
			$('<div class="currentTimeBar">').css({'left': nowLeft}).appendTo($(this).find('.td_time')[nowPoint]);
		});
	}

	function drawScheduleInfo($now, item, building, isClickEvent) {
		var name = JSV.isNotEmpty(item.person.restRoot) ? item.person.restRoot[0].orgUserName : item.email;
		var dateTime = DateFormat.format(new Date(), 'yyyyMMddHHmmss');

		if (item.isAllDay) {
			name && $now.find('.person').text(name);
			$now.find('.company').text(building);
			$now.find('.time').text('종일');
		} else if ((DateFormat.format(new Date(item.startTime), 'yyyyMMddHHmmss') <= dateTime && DateFormat.format(new Date(item.endTime), 'yyyyMMddHHmmss') >= dateTime) || isClickEvent) {
			name && $now.find('.person').empty().text(name);
			$now.find('.company').empty().text(building);
			$now.find('.time').empty().text(DateFormat.format(new Date(item.startTime), 'HH:mm') + ' ~ ' + DateFormat.format(new Date(item.endTime), 'HH:mm'));
		}
	}

	function getTimeGap(sTime, eTime, date) {
		var today = DateFormat.format(new Date(), 'yyyyMMdd');
		var sObj = formatTime(sTime);
		var eObj = formatTime(eTime);

		if (sObj.day < date || sObj.hour < 9) {
			sObj.hour = 9;
			sObj.min = 0;
		}
		if (eObj.day > date || (date == eObj.day && eObj.hour >= 23)) {
			eObj.hour = 23;
			eObj.min = 0;
		}

		var sTimeMin = (sObj.hour * 60) + parseInt(sObj.min);
		var eTimeMin = (eObj.hour * 60) + parseInt(eObj.min);

		return eTimeMin - sTimeMin;
	}

	function formatTime(time) {
		var df = DateFormat.format(new Date(time), 'yyyyMMdd:HH:mm').split(':');

		return {day: df[0], hour: df[1], min: df[2], full: df.toString().replace(/,/g, '')};
	}
});
var wStartTime;
var wNowTime;

function wStartTime() {
	wStartTime = new Date();
	wStartTime = wStartTime.getTime();

    countdown();
}

function countdown() {
	wNowTime = new Date();
	wNowTimeMin = wNowTime.getMinutes();
	wNowTimeSec = wNowTime.getSeconds();

	if (wNowTimeMin % 30 == 0 && wNowTimeSec == 0) {
		clearTimeout(timer);
		window.location.reload(true);
	} else {
		var timer = setTimeout('countdown()', 1000);
	}
}
window.onload=wStartTime;
</script>
<!-- &nbsp; -->
</tiles:putAttribute>
</tiles:insertDefinition>
