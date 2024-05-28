﻿<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/customTagLibrary.tld" prefix="util" %>
<tiles:insertDefinition name="template/mobile">
<tiles:putAttribute name="body">
<div id="schedule" data-role="page" class="mobile rsv_main">
    <script>
    $(document).on('pageinit','#schedule', function() {
    	var activePage = $.mobile.activePage;
    	var jsonData = <c:out value="${jsonData}" default="null" escapeXml="false" />;
    		
    	var buildings = jsonData.user.buildings;
    	var $buildings = $('.building select', '#schedule');
    	var $floor = $('.floor select', '#schedule');
    	
    	$buildings.change(function() {
    		drawFloor();
    	});
    	
    	$floor.change(function() {
    		drawResource();
    	});
    	
    	init();
    	
    	function init() {
    		$.datepicker.setDefaults({
    	        showOtherMonths: true,
    	      	dateFormat: "yy-mm-dd",
    	      	showMonthAfterYear: true,
    	      	monthNames: [ "1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월" ],
    	      	dayNamesMin: [ "일","월","화","수","목","금","토" ]
    	    });
    		$('.datepicker').datepicker('setDate', new Date());

    		var location = jsonData.user.location;
    		if (JSV.getCookie('build') && JSV.getCookie('floors')) {
    			location = {
    				buildingId: JSV.toJsonObj(JSV.getCookie('build')),
    				floorName: JSV.toJsonObj(JSV.getCookie('floors'))
    			};
    		}

    		drawBuilding(buildings, $buildings);
    		if (JSV.isNotEmpty(location) && JSV.isNotEmpty(location.buildingId) && JSV.isNotEmpty(location.floorName)) {
    			$.each($buildings.find('option'), function() {
    				if ($(this).data('id') == location.buildingId) {
    					$(this).attr('selected', 'selected');
    					$buildings.selectmenu('refresh', true);
    					
    					return false;
    				}
    			});
    		}
			drawFloor();
    	}
    	
    	function drawFloor() {
    		$floor.empty();
    		
    		var selectBuilding = $.grep(buildings, function(building) {
    			return building.id == $buildings.find('option:selected').val();
    		})[0];
    		
    		$.each(selectBuilding.floorNames, function() {
    			$('<option data-id="' + this + '">' + this + '층</option>').appendTo($floor);
    		});
    		$floor.selectmenu();
    		$floor.selectmenu('refresh', true);

    		drawResource();
    	}
    	
    	function drawResource() {
    		var selectBuildingId = $buildings.find('option:selected').data('id');
    		var selectFloor = $floor.find('option:selected').data('id');
			
			if (!jsonData.user.isGoogleLogin || (JSV.isNotEmpty(jsonData.user.location) && JSV.isEmpty(jsonData.user.location.buidingId) && JSV.isEmpty(jsonData.user.location.floorName))) {
				JSV.setCookie('build', JSON.stringify(JSV.clone(selectBuildingId)), 60 * 1000 * 60 * 24);
				JSV.setCookie('floors', JSON.stringify(JSV.clone(selectFloor)), 60 * 1000 * 60 * 24);
			}
    		
    		if (JSV.isNotEmpty(selectBuildingId)) {
    			var rooms = $.grep(jsonData.user.resources, function(resource) {
    				return (resource.buildingId == selectBuildingId && resource.floorName == selectFloor);
    			});
    			
    			drawRooms(rooms);
    		}
    	}

    	$('.btn_reserve', '#schedule').off('click');
    	$('.btn_reserve', '#schedule').on('click', function() {
    		var selectBuildingId = $buildings.find('option:selected').data('id');
    		var selectFloor = $floor.find('option:selected').data('id');

			if (jsonData.user.googleLogin) {
				blankGoogleCalendarInsert();
			} else {
	    		$.mobile.changePage('/mobile/schedule/create', {
	    			data: {buildingId: selectBuildingId, floor: selectFloor}
	    		});
			}
    	});
    	
    	function drawRooms(rooms) {
    		$('.rsv_list').empty();
    		
    		$.each(rooms, function() {
    			var room = this;
    			if (JSV.isNotEmpty(this.features)) {
    				var featureElm = '';
    				$.each(this.features, function() {
    					featureElm += '<span class="resource i_' + this.name.replace(/ /gi, "").toLowerCase() + '">' + this.name + '</span>';
    				});
    			}
    			var dispName = this.floorName + '-' + this.name + (this.capacity > 0 ? '(' + this.capacity + '인)' : '');
    			var roomLi = '<li data-id="' + this.email + '"><span class="txt">' + dispName + '</span>' + featureElm + '</li>';
    			
    			$(roomLi).off('click');
    			$(roomLi).on('click', function() {
    				var timeOffset = new Date().getTimezoneOffset();
    				var date = DateFormat.format($('.datepicker').datepicker('getDate'), 'yyyy/MM/dd');
    				
    				$.mobile.changePage('/mobile/schedule/reservation', {
    					data: {date: date, email: room.email, timeOffset: timeOffset}
    				});
    			}).appendTo($('.rsv_list'));
    		});
    	}
    	
    	function drawBuilding(buildings, $buildings) {
    		$buildings.empty();
    		
    		$.each(buildings, function() {
    			$('<option data-id="' + this.id + '">' + this.name + '</option>').appendTo($buildings);
    		});
    		$buildings.selectmenu();
    		$buildings.selectmenu('refresh', true);
    	}
    });
    </script>
	<div data-role="header" data-position="fixed" class="header" data-tap-toggle="false">
		<div class="left_btn">
			<a class="btn btn_prev" style="display:none;"><img src="<c:url value="/resources/css/images/btn_prev.gif" />" width="11" height="19"></a>
		</div>
		<div class="right_btn">
			<a class="btn btn_reserve"><img src="<c:url value="/resources/css/images/btn_reserve.png" />" width="36" height="29"></a>
		</div>
		<div class="tit_area">
			<h1>회의실예약</h1>
		</div>
	</div>
	<div class="container">
		<div class="rsv">
			<div class="set_area">
				<div class="set_date">
					<p class="tit">날짜</p>
					<div class="cal btn">
						<input type="text" class="datepicker" data-role="date">
					</div>
				</div>
				<div class="set_place">
					<p class="tit">장소</p>
					<div class="building"><select class="buildSel"></select></div>
					<span class="bar"></span>
					<div class="floor"><select></select></div>
				</div>
			</div>
			<div class="list_area">
				<ul class="rsv_list"></ul>
			</div>
		</div>
	</div>
</div>
</tiles:putAttribute>
</tiles:insertDefinition>