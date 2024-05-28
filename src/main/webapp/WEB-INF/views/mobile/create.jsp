﻿<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/customTagLibrary.tld" prefix="util" %>
<tiles:insertDefinition name="template/mobile">
<tiles:putAttribute name="body">
	<div id="create" data-role="page" class="mobile rsv_page">
    <script>
    $(document).on('pageinit','#create', function() {
    	var jsonData = <c:out value="${jsonData}" default="null" escapeXml="false" />;

       	var room = jsonData.room;
       	var floor = jsonData.floor;
       	var building = jsonData.buildingId;
       	var buildings = jsonData.user.buildings;
       	var resources = jsonData.user.resources;
       	
    	var $buildings = $('.building select', '#create');
    	var $floor = $('.floor select', '#create');
    	var $room = $('.room select', '#create');

		var $sHour = $('.start .ipt_time').val(jsonData.hour);
		var $sMin = $('.start .ipt_min').val(jsonData.min);
		
		var endHour = parseInt(jsonData.hour);
		var endMin = parseInt(jsonData.min + 30);
		
		if (endMin >= 60) {
			endHour = endHour + 1;
			endMin = '00';
		}
		
		var $eHour = $('.end .ipt_time', '#create').val(JSV.appendZero(endHour));
		var $eMin = $('.end .ipt_min', '#create').val(endMin);
    	
    	$buildings.change(function() {
    		createDrawFloor();
    	});
    	
    	$floor.change(function() {
    		createDrawRooms();
    	});
    	
    	$('#allTimeChk').off('click');
    	$('#allTimeChk').on('click', function() {
    		var disabled = $(this).is(':checked') ? 'disable' : 'enable';

    		$sHour.textinput(disabled);
    		$sMin.textinput(disabled);
    		$eHour.textinput(disabled);
    		$eMin.textinput(disabled);
    	});
    	
    	$('.btn_save').off('click');
    	$('.btn_save').on('click', function() {
    		var messages = [];
    		
    		var summary = $('.ipt_tit').val();
    		var allDay = $('#allTimeChk').is(':checked');
			var dateFormat = allDay ? 'yyyy/MM/dd' : 'yyyy/MM/dd HH:mm:ss';
			var sDate = $('#datepickerTimeStart').datepicker('getDate');
			var eDate = $('#datepickerTimeEnd').datepicker('getDate');
    		var googleLogin = '${sessionScope.userInfo.googleLogin}';

			var sDateText = sDate.getFullYear() + sDate.getMonth() + sDate.getDay();
			var eDateText = eDate.getFullYear() + eDate.getMonth() + eDate.getDay();

			if(sDateText != eDateText){
				alert("공용 회의실 예약은 당일만 가능합니다.");
				return false;
			}


    		if (allDay) {
				sDate.setHours(0, 0);
				eDate.addDays(1).setHours(0, 0);
    		} else {
				sDate.setHours($sHour.val(), $sMin.val());
				eDate.setHours($eHour.val(), $eMin.val());
    		}

    		if (JSV.isEmpty($sHour.val())) {
    			messages.push('[ 시작 시간 ] 는 필수 입력사항입니다.');
    		}
    		if (JSV.isEmpty($sMin.val())) {
    			messages.push('[ 시작 분 ] 는 필수 입력사항입니다.');
    		}
    		if (JSV.isEmpty($eHour.val())) {
    			messages.push('[ 종료 시간 ] 는 필수 입력사항입니다.');
    		}
    		if (JSV.isEmpty($eMin.val())) {
    			messages.push('[ 종료 분 ] 는 필수 입력사항입니다.');
    		}
    		if (DateFormat.format(new Date(sDate), 'yyyyMMddHHmmss') > DateFormat.format(new Date(eDate), 'yyyyMMddHHmmss')) {
    			messages.push('예약 시간을 다시 확인하여 주세요.');
    		}
    		if (!googleLogin && JSV.isEmpty($('#reservePW').val())) {
    			messages.push('[ 비밀번호 ] 는 필수 입력사항입니다.');
    		}
    		if (JSV.isEmpty(summary)) {
    			messages.push('[ 제목 ] 는 필수 입력사항입니다.');
    		}
    		
    		if (JSV.isNotEmpty(messages)) {
    			alert(messages.join('\r\n'));
    			return false;
    		}
    		
    		var obj = {
    			userName: $('.name').text(),
    			userEmail: jsonData.user.primaryEmail,
    			summary: summary,
    			allDay: allDay,
				sDate: DateFormat.format(sDate, dateFormat),
				eDate: DateFormat.format(eDate, dateFormat),
				resourceEmail: $room.find('option:selected').data('id'),
				displayName: $room.find('option:selected').text().trim(),
				pwd: $('#reservePW').val(),
				timeOffset: new Date().getTimezoneOffset()
    		}
    		
    		JSV.loadJSON('/mobile/schedule/insert.json', null, {
    			type:'POST',
    			data: obj,
    			success: function(data) {
    				alert('회의실이 예약되었습니다.');
					$('.btn_close').trigger('click');
    			}, error: function(xhr) {
    				alert(xhr.message);
    			}, complete: function() {
    				JSV.loadJSON('/schedule/updateDataStore.json', 'resourceEmail=' + obj.resourceEmail);
    			}
    		});
    	});
    	
    	createInit();
    	
    	function createInit() {
    		$.datepicker.setDefaults({
    	        showOtherMonths: true,
    	      	dateFormat: "yy-mm-dd",
    	      	showMonthAfterYear: true,
    	      	monthNames: [ "1월","2월","3월","4월","5월","6월","7월","8월","9월","10월","11월","12월" ],
    	      	dayNamesMin: [ "일","월","화","수","목","금","토" ]
    	    });
    		$('#datepickerTimeStart').datepicker('setDate', new Date());
    		$('#datepickerTimeEnd').datepicker('setDate', new Date());

    		createDrawBuilding();
			setDefaultOption($buildings, building);
			
			createDrawFloor();
			setDefaultOption($floor, floor);
			
			createDrawRooms();
			setDefaultOption($room, room);
    	};
    	
    	function setDefaultOption($elm, data) {
    		if (JSV.isNotEmpty(data)) {
    			$.each($elm.find('option'), function() {
    				if ($(this).data('id') == data) {
    					$(this).attr('selected', 'selected');
    					$elm.selectmenu('refresh', true);
    					
    					return false;
    				}
    			});
    		}
    	}
    	
		function createDrawBuilding() {
			$.each(buildings, function() {
				$('<option data-id="' + this.id + '">' + this.name + '</option>').appendTo($buildings);
			});
			$buildings.selectmenu();
			$buildings.selectmenu('refresh', true);	
		}
    	
    	function createDrawFloor() {
    		$floor.empty();
    		
    		var selectBuilding = $.grep(buildings, function(building) {
    			return building.id == $buildings.find('option:selected').val();
    		})[0];
    		
    		$.each(selectBuilding.floorNames, function() {
    			$('<option data-id="' + this + '">' + this + '층</option>').appendTo($floor);
    		});
    		$floor.selectmenu();
    		$floor.selectmenu('refresh', true);
    	}
    	
    	function createDrawRooms() {
    		$room.empty();
    		
    		var selectBuildingId = $buildings.find('option:selected').data('id');
    		var selectFloor = $floor.find('option:selected').data('id');

			var rooms = $.grep(jsonData.user.resources, function(resource) {
				return (resource.buildingId == selectBuildingId && resource.floorName == selectFloor);
			});
			
			$.each(rooms, function() {
    			$('<option data-id="' + this.email + '">' + this.name + '</option>').appendTo($room);
			});
    		$room.selectmenu();
    		$room.selectmenu('refresh', true);
    	}
    });

    function keyupFunction(inputBox, type) {
        $(inputBox).val($(inputBox).val().replace(/[^0-9]/g,""));

        var value = $(inputBox).val();
        
        if (type == 'time' && value >= 24) {
        	if (value > 24) {
           		$(inputBox).val('24');
        	}
        	$(inputBox).closest('div').find('.ipt_min').val('00');
        }
    	if (type == 'min') {
    		var timeVal = $(inputBox).closest('div').find('.ipt_time').val();
    		if (timeVal == '24' || value >= 60) {
    	   		$(inputBox).val('00');
    		}
    	}
    }
	</script>
		<div data-role="header" data-position="fixed" class="header" data-tap-toggle="false">
			<div class="left_btn">
				<a class="btn btn_close" data-rel="back"><img src="<c:url value="/resources/css/images/btn_close.gif" />" width="14" height="14"></a>
			</div>
			<div class="right_btn">
				<a class="btn btn_save">저장</a>
			</div>
			<div class="tit_area">
				<h1>회의실예약</h1>
			</div>
		</div>
		<div class="container">
			<div class="rsv">
				<div class="user_area">
					<div class="user">
						<span class="name">${sessionScope.userInfo.fullName }</span>
						<span class="email">${sessionScope.userInfo.intraEmail }</span>
					</div>
					<input type="text" class="ipt_tit">
				</div>
				<div class="set_area">
					<div class="choice_room">
						<p class="tit">장소</p>
						<div class="set_place">
							<div class="building">
								<select></select>
							</div>
							<div class="floor">
								<select></select>
							</div>
							<div class="room">
								<select></select>
							</div>
						</div>
					</div>
					<div class="choice_time">
						<p class="tit">종일</p>
						<div class="chk_all">
							<div class="chkbox chkx23">
								<input type="checkbox" id="allTimeChk" name="종일선택/해제">
								<label for="allTimeChk"></label>
							</div>
						</div>
						<div class="set_time">
							<div class="time start">
								<p class="txt">시작</p>
								<div class="ipt_area">
									<input type="text" class="datepicker" id="datepickerTimeStart" data-role="date" value="2018-10-15">
									<input type="text" class="ipt_time" onkeyup="keyupFunction(this, 'time')">
									<span class="dot">:</span>
									<input type="text" class="ipt_min" onkeyup="keyupFunction(this, 'min')">
								</div>
							</div>
							<div class="time end">
								<p class="txt">종료</p>
								<div class="ipt_area">
									<input type="text" class="datepicker" id="datepickerTimeEnd" data-role="date" value="2018-10-15">
									<input type="text" class="ipt_time" onkeyup="keyupFunction(this, 'time')">
									<span class="dot">:</span>
									<input type="text" class="ipt_min" onkeyup="keyupFunction(this, 'min')">
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="pw_area" <c:if test="${sessionScope.userInfo.googleLogin}">style="visibility: hidden;"</c:if>>
					<p class="tit">비밀번호</p>
					<div class="ipt_area">
						<input type="password" name="reserve_password" id="reservePW" class="ipt_pw">
					</div>
					<p class="explain">예약삭제 시 비밀번호가 필요합니다.</p>
				</div>
			</div>
		</div>
	</div>
</tiles:putAttribute>
</tiles:insertDefinition>