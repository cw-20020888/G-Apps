<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/tld/customTagLibrary.tld" prefix="util"%>
<tiles:insertDefinition name="template/">
<tiles:putAttribute name="resource">
<%@ include file="/resources/tmpl/Popup.tmpl" %>
<%@ include file="/resources/tmpl/Schedule.tmpl" %>
<style>
.contents_wrap{overflow-y: hidden;}

.layer_reserve_info .header .tit{overflow: hidden;white-space: nowrap;text-overflow: ellipsis;}

.reserve .tbl_area {position: relative;z-index: 8;}
.reserve .resrve_tbl .rt_body td.time_selected .time_box{position: absolute;top:0;}
/*.reserve .resrve_tbl .rt_body td.time_selected .time_box{position: relative;float:left;}*/

.td_resource .tooltip{position: absolute;top: 20px;left: 50%;display: none;padding: 4px 9px 5px 8px;border-radius: 2px;background: rgba(71,71,71,0.75);color: #FFF;font-size: 12px;white-space: nowrap;z-index: 101;}

table.timeGrape {position: relative;table-layout: fixed;width: 100%;border-bottom: 2px solid #9e9e9e;border-left: 1px solid #e0e0e0;box-sizing: border-box;-webkit-box-sizing: border-box;background: #ededed;}
table.timeGrape th {height: 38px;border-top:1px solid #e0e0e0;border-right:1px solid #e0e0e0;background:#ededed;color:#000;font-size:13px;font-weight:normal;letter-spacing:-1px;}
table.timeGrape th:last-child{border-right:none;}
</style>
<script type="text/javascript">
$(function() {
	var jsonData = <c:out value="${jsonData}" default="null" escapeXml="false" />;
	var _rooms = null;
	var _datePicker = null;
	var _selectedBuild = null;

	init();

	$('.building').click(function() {
		$(this).find('.layer_opt').toggle();
	});

	$('a.build_item').click(function() {
		drawFloorLi($(this).attr('id'));

		drawRoomsFunction();
	});

	$('a.pub_reserve').click(function(){
		var feature = '_blank';
		var url = 'https://gappspub.coway.com';

		window.open(url, '', feature);
	});

	$('.floor').click(function() {
		$(this).find('.layer_opt').toggle();
	});

	$('input[name=floorBox]').click(function() {
		if ($(this).attr('id') == 0) {
			var isAllCheck = false;

			$(this).prop('checked', function(i, v) { isAllCheck = v; });
			$('input[name=floorBox]').prop('checked', isAllCheck);
		} else {
			$('input[id=0]').prop('checked', false);
			if ($('input[name=floorBox]:checked').length == $('input[name=floorBox]').length - 1) {
				$('input[id=0]').prop('checked', true);
			}
		}
	});

	$('.floor_onok').click(function() {
		drawRoomsFunction();
	});

	$('.btn_reserve').click(function() {
		if (jsonData.user.googleLogin) {
			blankGoogleCalendarInsert();
		} else {
			var $checkFloorBox = $('.cnk_list').find('input:checkbox[name="floorBox"]:checked');
			var isAll = $('input[id=0]').is(':checked') || $('input[name=floorBox]:checked').length == 0;

			var scheduleEvent = new ScheduleEvent($.extend(JSV.clone(jsonData), {
				floor: $.map($checkFloorBox, function(elm) { return $(elm).attr('id'); }),
				rooms: isAll ? null : _rooms,
				building: _selectedBuild,
				callback: schedulePopupCallback
			}));
			scheduleEvent.loadData();
		}
	});

	$('.btn_reserve_guide').click(function() {
		var feature = 'width=1320,height=768,toolbar=no,menubar=no,location=no,resizable=no';
		var url = 'https://docs.google.com/presentation/d/e/2PACX-1vTDHcS1GYRTDDIvZywqtizyT72xitE7qrUQ0QbsmnNSlS66hFdlvoRt-rgJU9TtBKm3Bq_2zi3B9CwG/pub?start=false&loop=false&delayms=3000';

		window.open(url, '', feature);
	});

	function blankGoogleCalendarInsert(startHours, startMin, addDays, resourceEmail) {
		var href = 'https://calendar.google.com/calendar/r/eventedit?';

		startHours = startHours || '09';
		startMin = startMin || '00';

		var start = new Date(new Date().setHours(startHours, startMin, '00'));

		var endHours = startHours;
		var endMin = parseInt(startMin + 30);
		if (endMin >= 60) {
			endHours++;
			endMin = '00';
		}
		var end = new Date(new Date().setHours(endHours, endMin, '00'));

		if (JSV.isNotEmpty(addDays)) {
			start.addDays(addDays);
			end.addDays(addDays);
		}

		var startUtc = new Date(start.getTime() + start.getTimezoneOffset() * 60000);
		var endUtc = new Date(end.getTime() + end.getTimezoneOffset() * 60000);

		startFormat = DateFormat.format(startUtc, 'yyyyMMddTHHmmssZ');
		endFormat = DateFormat.format(endUtc, 'yyyyMMddTHHmmssZ');

		href +='dates='+ startFormat +'/' + endFormat;
		if (JSV.isNotEmpty(resourceEmail)) {
			href += '&add=' + resourceEmail;
		}

		window.open(href, '_blank');
	}

	function init() {
		loadingImage.init();

		$('#scheduleWrap').tmpl(jsonData).appendTo($('.content_area'));

		_datePicker = $('#datepicker').datepicker({
			dateFormat : 'yy/mm/dd',
	      	showOn: 'button',
	      	buttonImage: "<c:url value="/resources/css/images/btn_cal.png" />",
			showMonthAfterYear: true,
			monthNames: [ '1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월' ],
			dayNamesMin: [ '일','월','화','수','목','금','토' ],
			onSelect: function() {
				drawRoomsFunction();
			}
		});
		_datePicker.datepicker('setDate', new Date());
		_datePicker.click(function() {_datePicker.datepicker('show');});

		var location = jsonData.user.location;
		if (JSV.getCookie('build') && JSV.getCookie('floors')) {
			location = {
				buildingId: JSV.toJsonObj(JSV.getCookie('build')),
				floorName: JSV.toJsonObj(JSV.getCookie('floors'))
			};
		}

		if (JSV.isNotEmpty(location) && JSV.isNotEmpty(location.buildingId) && JSV.isNotEmpty(location.floorName)) {
			drawFloorLi(location.buildingId);

			if (location.floorName.length != $('.cnk_list li').length -1) {
				$.each($('.cnk_list li'), function() {
					var _this = this;
					var roomData = $(this).find('input[name=floorBox]').attr('id');

					if (typeof location.floorName == 'string') {
						location.floorName = new Array(location.floorName);
					}

					$.each(location.floorName, function() {
						if (roomData == this) {
							$(_this).find('input[name=floorBox]').prop('checked', true);
							return false;
						}
					})
				});
			}

			drawRoomsFunction();
		}
	}

	function drawFloorLi(buildingId) {
		$('.floor ul').empty();

		_selectedBuild = $.grep(jsonData.user.buildings, function(item) {
			return item.id == buildingId;
		})[0];

		$('<li>\
			<p class="chkbox">\
				<input type="checkbox" name="floorBox" id="0" name="층선택/해제">\
				<label for="0"><span>층선택/해제</span></label>\
			</p>\
			<p class="txt">전체</p>\
		</li>').appendTo($('.floor ul'));

		if (JSV.isNotEmpty(_selectedBuild)) {
			$('.building .custom_select .slt .txt').text(_selectedBuild.name);
			$.each(_selectedBuild.floorNames, function() {
				var li = '<li>\
					<p class="chkbox">\
						<input type="checkbox" name="floorBox" id="' + this + '" name="층선택/해제">\
						<label for="' + this + '"><span>층선택/해제</span></label>\
					</p>\
					<p class="txt">' + this + '층</p>\
				</li>';

				$(li).appendTo($('.floor ul'));
			});
		}
	}

	// 전체층 = 층을 전부 선택하거나 전부 선택하지 않았거나.
	function drawRoomsFunction() {
		if (JSV.isNotEmpty(_selectedBuild)) {
			var $checkFloorBox = $('.cnk_list').find('input:checkbox[name="floorBox"]:checked');
			var isAll = $('input[id=0]').is(':checked') || $('input[name=floorBox]:checked').length == 0;

			var floorArray = [];
			var selectedFloorName = isAll ? '전체' : [];
			var floors = isAll ? _selectedBuild.floorNames : $checkFloorBox;

			_rooms = $.map(floors, function(floor) {
				var f = floor;
				if (typeof floor == 'object') {
					f = $(floor).attr('id');

					selectedFloorName.push(f + '층');
				}
				return $.grep(jsonData.user.resources, function(resource) {
					return resource['floorName'] == f && resource.buildingId == _selectedBuild.id;
				});
			}).reverse();
			$('.floor .custom_select .slt .txt').text(selectedFloorName);

			if (!jsonData.user.isGoogleLogin || (JSV.isNotEmpty(jsonData.user.location) && JSV.isEmpty(jsonData.user.location.buidingId) && JSV.isEmpty(jsonData.user.location.floorName))) {
				JSV.setCookie('build', JSON.stringify(JSV.clone(_selectedBuild.id)), 60 * 1000 * 60 * 24);
				JSV.setCookie('floors', JSON.stringify(JSV.clone(floorArray)), 60 * 1000 * 60 * 24);
			}

			drawRoomsByFloor();
		}

		if (JSV.hasVerticalScrollBar($('.tbl_area'))) {
			var scrollWidth = $('.tbl_area').width() - $('.tbl_area tbody').width();

			$('.timeGrape').css({'padding-right': parseInt(scrollWidth - 1) + 'px'});
		} else {
			$('.timeGrape').css({'padding': '0 0'});
		}
	}

	function floorAjax(items, date, i) {
		var calendarIds = items;
		JSV.loadJSON('/schedule/floor/reservationAjax.json', null, {
			data: {calendarId: calendarIds.toString(), date: date, timeOffset: new Date().getTimezoneOffset()},
			async: true,
			beforeSend: function() {
				$.each(calendarIds, function() {
					drawRoomTr(this);
				});
			}, success: function(data) {
				for(var key in data.result) {
					if ( key != "nmn.io_18846s33vpbe2hkcknsqud4f2uvmc@resource.calendar.google.com") {
						drawScheduleByRoom(data.result[key], key);
					}
				}
			}, error: function(xhr) {
				console.log(' xhr :', xhr);
			}
		});
	}

	function drawRoomsByFloor() {
		loadingImage.hide();

		if (JSV.isNotEmpty(_rooms)) {
			$('.rt_body').empty();

			var roomEmailByFloors = $.map(_rooms, function(r) { return r.email;});
			var date = DateFormat.format(_datePicker.datepicker('getDate'), 'yyyy/MM/dd');
			floorAjax(roomEmailByFloors, date, 0);
		}
	}

	function drawRoomTr(roomEmail) {
		var room = $.grep(_rooms, function(room) {
			return room.email == roomEmail;
		})[0];

		var obj = $.extend(room, {
			dispName: room.floorName + '-' + room.name + (room.capacity > 0 ? '(' + room.capacity + '인)' : ''),
			featureNames: null
		});

		if (JSV.isNotEmpty(obj.features)) {
			var featureNames = $.map(obj.features, function(feature) {
				return feature.name;
			});
			obj.featureNames = featureNames.toString();
		}
		drawRoomTemplate(room, obj);
	}

	function drawRoomTemplate(room, obj) {
		if ( obj.email != "nmn.io_18846s33vpbe2hkcknsqud4f2uvmc@resource.calendar.google.com") {
			var roomTmpl = $('#room').tmpl(obj).appendTo($('.rt_body')).find('.td_time').click(function(e) {
				var pickerDate = _datePicker.datepicker('getDate');
				var today = new Date().setHours(0, 0, 0, 0);

				if (today - pickerDate.getTime() > 0) {
					return false;
				}

				if (jsonData.user.googleLogin) {
					var addDays = (pickerDate.getTime() - today) / 86400000;
					var startHours = $(this).data('hour');
					var startMin = $(this).data('min');

					blankGoogleCalendarInsert(startHours, startMin, addDays, obj.email);
				} else {
					var addSchedulePopup = new ScheduleEvent($.extend(jsonData, {
						date: pickerDate,
						hour: $(this).data('hour'),
						min: $(this).data('min'),
						floor: [$(this).parents('tr').data('floor')],
						rooms: _rooms,
						selectedRoom: {
							name: $(this).parents('tr').data('name'),
							email: $(this).parents('tr').data('email')
						},
						building: _selectedBuild,
						callback: schedulePopupCallback
					}));
					addSchedulePopup.loadData();
				}
			}).end();

			$('.td_resource').mouseenter(function() {
				var tooltipWidth = $(this).children('.tooltip').outerWidth();
				$(this).children('.tooltip').show();
				$(this).children('.tooltip').css('margin-left',-tooltipWidth/2);
			});

			$('.td_resource').mouseleave(function() {
				$(this).children('.tooltip').hide();
			});

			roomTmpl.find('.td_resource .txt').click(function() {
				popup('roomImagePopup', {floor: room.floorName, roomEmail: obj.email}).find('.btn_close').click(function() {
					$(this).parents('.popup_wrap').remove();
				}).end().css('z-index', '10');
			});

			if ($('.td_build').length == 0) {
				$('.rt_body').find('tr:first-child').prepend($('<td class="td_build" rowspan="' + _rooms.length + '">' + _selectedBuild.name + '</td>'))
			}
		}
	}

	function drawScheduleByRoom(schedule, resourceEmail) {
		var zidx = 1;
		var startGap = 9 / 0.5;
		var date = DateFormat.format(_datePicker.datepicker('getDate'), 'yyyyMMdd');

		$.each(schedule, function() {
			var _this = this;
			var sTime = this.startTime;
			var eTime = this.endTime;
			var scheduleRoom = $('.roomTr[data-email="' + resourceEmail + '"]');

			if (JSV.isNotEmpty(scheduleRoom)) {
				var sFormatTime = formatTime(sTime);
				var eFormatTime = formatTime(eTime);

				if (sFormatTime.day > date && date < sFormatTime.day) {
					return false;
				}

				// 종료시간이 오늘 9시 이전인 경우 그리지 않도록 설정.
				if (eFormatTime.full - (date + '0900') > 0 && (JSV.isNotEmpty(this.status) && this.status != 'declined')) {
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
					var colorType = getColorType($(startTd).parents('tr').index() + 1);

					$(startTd).addClass('time_selected ' + colorType);

					//var mail = _this.email.split('@')[0];
					var mail = _this.email;
					//var scheduleEvent = $('#scheduleEvent').tmpl({time: sTime, name: this.name || mail}).appendTo($(startTd));
					var scheduleEvent = $('#scheduleEvent').tmpl({time: sTime, name: this.name || mail}).appendTo($(startTd));
					if ( this.name == "밥넷마블02") {
						scheduleEvent = $('#scheduleEvent').tmpl({time: sTime, name: this.email}).appendTo($(startTd));
					}
					$(startTd).find('.time_box').click(function(e) {
						var timeInfo = DateFormat.format(new Date(sTime), 'MM/dd HH:mm') + ' ~ ' + DateFormat.format(new Date(eTime), 'MM/dd HH:mm');
						var scheduleObj = {id: _this.id, user: jsonData.user, colorType: colorType, time: timeInfo, email: _this.email, creator: null, cellPhone: null};
						JSV.loadJSON('/schedule/person.json', null, {
							data: {'mail': mail},
							success: function(data) {
								if (JSV.isNotEmpty(data.result) && JSV.isNotEmpty(data.result)) {
									var person = data.result;

									scheduleObj.creator = person.orgUserName;
									scheduleObj.cellPhone = person.orgUserCellPhone;
								}
							}, error: function(xhr) {
								return false;
							}
						});

						$('.layer_reserve_info').remove();

						var scheduleInfo = $('#scheduleInfo').tmpl(scheduleObj).appendTo($('.content_area')).css($.extend($(this).offset(), {'z-index': 9}));

						scheduleInfo.mouseleave(function() {
					    	$(this).remove();
					    });

						$('.btn_delete', scheduleInfo).click(function() {
							$('.layer_password').toggle();
						});

						$('.btn_confirm', scheduleInfo).click(function() {
							var pwd = $('input[name=reserve_password]').val();

							if (JSV.isEmpty(pwd)) {
								alert('비밀번호를 입력하여 주세요.');
								return false;
							} else {
								JSV.loadJSON('/schedule/delete.json', null, {
									type: 'POST',
									data: {pwd: pwd, eventId: scheduleObj.id},
									async: true,
									beforeSend: function(xhr) {
										loadingImage.show();
									}, success: function() {
										alert('삭제되었습니다.');
										$('.layer_reserve_info').remove();
									}, error: function(xhr) {
										alert(xhr.message);

										loadingImage.hide();
									}, complete: function() {
										updateDataStore(data.resourceEmail);
									}
								});
							}
						});

						$('.btn_close', scheduleInfo).click(function() {
							$(this).parents('.layer_reserve_info').remove();
						});

						var scheduleInfoLeft = scheduleInfo.offset().left;
						if ($('.reserve').innerWidth() < (scheduleInfoLeft + scheduleInfo.width())) {
							scheduleInfo.css('left', scheduleInfoLeft - ((scheduleInfoLeft + scheduleInfo.width()) - $('.reserve').innerWidth()));
						}

						var scheduleInfoTop = scheduleInfo.offset().top;
						if (window.outerHeight < (e.clientY + scheduleInfoTop)) {
							scheduleInfo.css('top', e.clientY - scheduleInfo.height());
						}

				    	e.stopPropagation();
				    	e.preventDefault();
					}).css({'z-index': zidx++});

					var per = timeGap / 30 * 100;
					$(scheduleEvent).css({'width' : 'calc(' + per + '% + ' + per / 100 + 'px)', 'left': left});
				}
			}
		});
	}

	function getColorType(idx) {
		return 'color_type_' + JSV.appendZero(idx % 20 == 0? 20 : idx % 20);
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

	function schedulePopupCallback(data) {
		var _this = this;
		var sDateFormat = DateFormat.format(new Date(data.sDate), 'yyyyMMddHHmmss');
		var eDateFormat = DateFormat.format(new Date(data.eDate), 'yyyyMMddHHmmss');

		if (sDateFormat > eDateFormat) {
			alert('예약 시간을 다시 확인하여 주세요.');
			return false;
		}

		$.extend(data, {timeOffset: new Date().getTimezoneOffset()});

		JSV.loadJSON('/schedule/insert.json', null, {
			type:'POST',
			data: data,
			beforeSend: function(xhr) {
				loadingImage.show();
			}, success: function(successData) {
				alert('회의실이 예약되었습니다.');
				_this.close();
			}, error: function(xhr) {
				alert(xhr.message);
			}, complete: function() {
				updateDataStore(data.resourceEmail);
			}
		});
	}

	function updateDataStore(resourceEmail) {
		setTimeout(function() {
			JSV.loadJSON('/schedule/updateDataStore.json', 'resourceEmail=' + resourceEmail, {
				success: function() {
					drawRoomsByFloor();
				}
			});
		}, 1000);
	}

	function popup(popupId, data) {
		var $popupWrap = $('.popup_wrap');

		return $popupWrap.length > 0 ? $popupWrap : $('#' + popupId).tmpl(data).appendTo($('.content_area')).find('.close').click(function() {
			$(this).parents('.popup_wrap').remove();
		}).end();
	}
});

function convertFeatureName(name) {
	return name.replace(/ /gi, "").toLowerCase();
}

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
</tiles:putAttribute>
<tiles:putAttribute name="body">
	<!-- &nbsp; -->
</tiles:putAttribute>
</tiles:insertDefinition>
