﻿﻿<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/customTagLibrary.tld" prefix="util" %>
<tiles:insertDefinition name="template/mobile">
<tiles:putAttribute name="body">
	<div id="reservation" data-role="page" class="mobile rsv_main">
    <script>
    $(document).on('pageinit','#reservation', function() {
    	$('.time_box').remove();
    	
    	var activePage = $.mobile.activePage;
    	var jsonData = <c:out value="${jsonData}" default="null" escapeXml="false" />;
    	var result = jsonData.result[0];
    	
    	$('.room_area .name').text(result.floorName + '-' + result.name + (result.capacity > 0 ? '(' + result.capacity + '인)' : ''));

    	$('.btn_reserve', '#reservation').off('click');
    	$('.btn_reserve', '#reservation').on('click', function() {
			if (jsonData.user.googleLogin) {
	    		var today = new Date().setHours(0, 0, 0, 0);
				var addDays = (jsonData.date - today) / 86400000;
				
				blankGoogleCalendarInsert(null, null, addDays, result.resourceEmail);
			} else {
	    		$.mobile.changePage('/mobile/schedule/create', {
	    			data: {
	    				room: result.resourceEmail,
	    				floor: result.floorName,
	    				buildingId: result.building 
	    			}
	    		});
			}
    	});
    	
    	$('.td_time').off('click');
    	$('.td_time').on('click', function() {
    		var _this = $(this);
			if (jsonData.user.googleLogin) {
	    		var today = new Date().setHours(0, 0, 0, 0);
				var addDays = (jsonData.date - today) / 86400000;
				
				blankGoogleCalendarInsert(_this.data('hour'), _this.data('min'), addDays, result.resourceEmail);
			} else {
	    		$.mobile.changePage('/mobile/schedule/create', {
	    			data: {
	    				hour: _this.data('hour'),
	    				min: _this.data('min'),
	    				room: result.resourceEmail,
	    				floor: result.floorName,
	    				buildingId: result.building
	    			}
	    		});
			}
    	});
    	
    	if (JSV.isNotEmpty(result.items)) {
    		var startGap = 9 / 0.5;
    		var date = DateFormat.format(new Date(jsonData.date), 'yyyyMMdd');
    		
    		$.each(result.items, function() {
    			var item = this;
    			var sTime = item.startTime;
    			var eTime = item.endTime;
    			
    			var sFormatTime = formatTime(sTime);
    			var eFormatTime = formatTime(eTime);

    			if (eFormatTime.full - (date + '0900') > 0) {
    				var startHour = sFormatTime.hour;
    				var startMin = sFormatTime.min;
    				if (sFormatTime.day < date || (sFormatTime.day == date && sFormatTime.hour < 9)) {
    					startHour = 9;
    					startMin = 0;
    				}
    				var startPoint = Math.floor((startHour / 0.5) + (startMin / 30) - startGap);
    				var timeGap = getTimeGap(sTime, eTime, date);
    				var startTd = $('.rsv_tbl').find('.td_time')[startPoint];
    				var top = ($('.td_time').innerHeight() / 30) * (startMin - (startPoint % 2 == 1 ? 30 : 0)) + 'px';
    				var per = timeGap / 30 * 100;
    				var timeInfo = item.isAllDay ? '종일' : DateFormat.format(new Date(item.startTime), 'HH:mm') + ' ~ ' + DateFormat.format(new Date(item.endTime), 'HH:mm');
    				
    				var divElm = '<div class="time_box">';
    					divElm+= '	<span class="name">' + (item.name || item.email) + '</span>';
    					divElm+= '	<p class="time">' + timeInfo + '</p>';
    					divElm+= '</div>';
    				
    				var $event = $(divElm).appendTo($(startTd).empty()).end().css({'z-index': '1'});
    				
   					$event.off('click');
    				$event.on('click', function(e) {
    					$.mobile.changePage('/mobile/schedule/info', {
    						data: {
    							id: item.id,
    							name: item.name,
    							email: item.email, 
    							timeInfo: timeInfo, 
    							cellPhone: item.cellPhone,
    		    				room: result.resourceEmail,
    		    				floor: result.floorName,
    		    				buildingId: result.building
    						}
    					});
						
				    	e.stopPropagation();
				    	e.preventDefault();
    				});
    				
    				$event.css({'height' : 'calc(' + per + '% + ' + per / 100 + 'px)', 'top' : top});
    			}
    		});
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
	</script>
		<div data-role="header" data-position="fixed" class="header" data-tap-toggle="false">
			<div class="left_btn">
				<a class="btn btn_prev" data-rel="back"><img src="<c:url value="/resources/css/images/btn_prev.gif" />" width="11" height="19"></a>
			</div>
			<div class="right_btn">
				<a class="btn btn_reserve"><img src="<c:url value="/resources/css/images/btn_reserve.png" />" width="36" height="29"></a>
			</div>
			<div class="tit_area">
				<h1>회의실예약</h1>
			</div>
		</div>
		<div class="container">
			<div class="rsv_view">
				<div class="room_area">
					<p class="name"></p>
				</div>
				<div class="tbl_area">
					<table class="rsv_tbl" cellpadding="0" cellspacing="0" border="0">
						<colgroup>
							<col width="63px">
							<col>
						</colgroup>
						<tbody>
							<tr>
								<th rowspan="2">09시</th>
								<td class="td_time" data-hour="09" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="09" data-min="30"></td>
							</tr>
							<tr>
								<th rowspan="2">10시</th>
								<td class="td_time" data-hour="10" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="10" data-min="30"></td>
							</tr>
							<tr>
								<th rowspan="2">11시</th>
								<td class="td_time" data-hour="11" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="11" data-min="30"></td>
							</tr>
							<tr>
								<th rowspan="2">12시</th>
								<td class="td_time" data-hour="12" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="12" data-min="30"></td>
							</tr>
							<tr>
								<th rowspan="2">13시</th>
								<td class="td_time" data-hour="13" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="13" data-min="30"></td>
							</tr>
							<tr>
								<th rowspan="2">14시</th>
								<td class="td_time" data-hour="14" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="14" data-min="30"></td>
							</tr>
							<tr>
								<th rowspan="2">15시</th>
								<td class="td_time" data-hour="15" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="15" data-min="30"></td>
							</tr>
							<tr>
								<th rowspan="2">16시</th>
								<td class="td_time" data-hour="16" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="16" data-min="30"></td>
							</tr>
							<tr>
								<th rowspan="2">17시</th>
								<td class="td_time" data-hour="17" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="17" data-min="30"></td>
							</tr>
							<tr>
								<th rowspan="2">18시</th>
								<td class="td_time" data-hour="18" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="18" data-min="30"></td>
							</tr>
							<tr>
								<th rowspan="2">19시</th>
								<td class="td_time" data-hour="19" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="19" data-min="30"></td>
							</tr>
							<tr>
								<th rowspan="2">20시</th>
								<td class="td_time" data-hour="20" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="20" data-min="30"></td>
							</tr>
							<tr>
								<th rowspan="2">21시</th>
								<td class="td_time" data-hour="21" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="21" data-min="30"></td>
							</tr>
							<tr>
								<th rowspan="2">22시</th>
								<td class="td_time" data-hour="22" data-min="00"></td>
							</tr>
							<tr>
								<td class="td_time" data-hour="22" data-min="30"></td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</tiles:putAttribute>
</tiles:insertDefinition>