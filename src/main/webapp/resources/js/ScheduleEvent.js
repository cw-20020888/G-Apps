function ScheduleEvent() {
	this.init(arguments[0]);
	this.addEvent();
	this.datePickerInit();
}
ScheduleEvent.prototype = {
	init: function() {
		var _this = this;
		this.options = $.extend({
			date: null,
			hour: '09',
			min: '00',
			floor: null,
			rooms: null,
			messages: [],
			callbackFunc : null
		}, (arguments[0] || {}), {
		});
		
		this.widget = $('#addSchedule').tmpl(this.options).appendTo(document.body).css('z-index', 5001).end();

		$('.close', this.widget).click(function() {
			_this.close();
		});
	},
	addEvent: function() {
		var _this = this;
		$('.building', this.widget).click(function() {
			$(this).find('.layer_opt').toggle();
		});
		
		$('a.build_item', this.widget).click(function() {
			var buildingId = $(this).attr('id');
			selectedBuild = $.grep(_this.options.user.buildings, function(item) {
				return item.id == buildingId;
			})[0];
			
			_this.drawPopupFloorLi(selectedBuild);
		});
		
		$('.floor', this.widget).click(function() {
			$(this).find('.layer_opt', this.widget).toggle();
		});

		$('.floor_onok', this.widget).click(function() {
			var rooms = _this.drawPopupRooms();
			
			_this.drawReferenceRooms(rooms);
		});
		
		$('.room', this.widget).click(function() {
			$(this).find('.layer_opt').toggle();
		});
		
		$('#allTimeChk', this.widget).click(function() {
			$('.start .ipt_time', this.widget).val('').prop('disabled', function(i, v) { return !v; });
			$('.start .ipt_min', this.widget).val('').prop('disabled', function(i, v) { return !v; });
			$('.end .ipt_time', this.widget).val('').prop('disabled', function(i, v) { return !v; });
			$('.end .ipt_min', this.widget).val('').prop('disabled', function(i, v) { return !v; });
		});
		
		$('.onok', this.widget).click(function() {
			_this.onok();
		});
	},
	datePickerInit: function() {
		this.sDatePicker = this.makeDatePicker('sDatePicker');
		this.eDatePicker = this.makeDatePicker('eDatePicker');
	},
	makeDatePicker: function(id) {
		var picker = $('#' + id, this.widget).datepicker({
			minDate: 0,
			dateFormat : 'yy/mm/dd',
			showMonthAfterYear: true,
			monthNames: [ '1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월' ],
			dayNamesMin: [ '일','월','화','수','목','금','토' ]
		});
		picker.click(function() {
			picker.datepicker('show');
		});
		picker.datepicker('setDate', this.options.date ? new Date(this.options.date) : new Date());
		
		return picker;
	},
	loadData: function() {
		var _this = this;
		
		if (JSV.isNotEmpty(this.options.building)) {
			var build = $.grep(this.options.user.buildings, function(building) {
				return building.id == _this.options.building.id;
			})[0];
			
			this.drawPopupFloorLi(build);
		}
		
		if (JSV.isNotEmpty(this.options.floor)) {
			$.each($('.cnk_list li', this.widget), function() {
				var $chkLi = $(this);
				var roomData = $(this).find('input[name=floorBox]').data('room');
				
				$.each(_this.options.floor, function() {
					if (this == roomData) {
						$chkLi.find('input[name=floorBox]').prop('checked', true);
					}
				});
				/*
				if (_this.options.floor == roomData) {
					$(this).find('input[name=floorBox]').prop('checked', true);
				}
				*/
			});
		}
		
		this.drawPopupRooms();
		this.drawReferenceRooms(this.options.rooms);

		$('.start .ipt_time', this.widget).val(this.options.hour);
		$('.start .ipt_min', this.widget).val(this.options.min);

		var endHour = parseInt(this.options.hour);
		var endMin = parseInt(this.options.min + 30);
		
		if (endMin >= 60) {
			endHour = endHour + 1;
			endMin = '00';
		}
		$('.end .ipt_time', this.widget).val(JSV.appendZero(endHour));
		$('.end .ipt_min', this.widget).val(endMin);
		
		if (JSV.isNotEmpty(this.options.selectedRoom)) {
			$('.room .custom_select .slt .txt', _this.widget).text(this.options.selectedRoom.name).attr({'data': this.options.selectedRoom.email, 'val': this.options.selectedRoom.email});	
		}
	},
	drawPopupFloorLi: function(building) {
		var _this = this;
		$('.floor ul', this.widget).empty();
		
		if (JSV.isNotEmpty(building)) {
			$('.building .custom_select .slt .txt', this.widget).text(building.name);

			$.each(building.floorNames, function(i) {
				var li = '<li>\
					<p class="chkbox">\
						<input type="checkbox" name="floorBox" data-room="' + this + '" id="' + this + i +'" name="층선택/해제">\
						<label for="' + this + i +'"><span>층선택/해제</span></label>\
					</p>\
					<p class="txt">' + this + '층</p>\
				</li>';
				
				$(li).appendTo($('.floor ul', _this.widget));
			});
		}
	},
	drawPopupRooms: function() {
		var _this = this;
		var selectedFloorName = [];
		var selectedFloor = $('.cnk_list', this.widget).find('input:checkbox[name="floorBox"]:checked', this.widget);
		if (JSV.isNotEmpty(selectedFloor)) {
			var rooms = $.map(selectedFloor, function(elm) {
				selectedFloorName.push($(elm).data('room') + '층');
				
				return $.grep(_this.options.user.resources, function(resource) {
					return resource['floorName'] == $(elm).data('room');
				});
			});
			$('.floor .custom_select .slt .txt', this.widget).text(selectedFloorName);
		}
		return rooms;
	},
	drawReferenceRooms: function(rooms) {
		var _this = this;
		$('.room ul', this.widget).empty();
		
		if (JSV.isNotEmpty(rooms)) {
			$.each(rooms, function() {
				var li = '<li><a href="javascript:void(0);" class="room_item" id="' + this.email + '">' + this.name + '</a></li>';

				$(li).appendTo($('.room ul', _this.widget)).find('.room_item', _this.widget).click(function() {
					$('.room .custom_select .slt .txt', _this.widget).text($(this).text()).attr({'data': $(this).attr('id'), 'val': $(this).attr('id')});
				});
			});
		} else {
			$('.floor .custom_select .slt .txt', _this.widget).text('층 선택');
		}
	},
	onok: function() {
		if (this.validate()) {
			var summary = $('input[name=input_meeting_title]', this.widget).val();
			var selectedRoom = $('.room .custom_select .slt .txt', this.widget);
			var allDay = $('#allTimeChk', this.widget).prop('checked');
			var sDate = this.sDatePicker.datepicker('getDate');
			var eDate = this.eDatePicker.datepicker('getDate');
			var pwd = $('input[name=reserve_password]', this.widget).val() || null;
			var dateFormat = allDay ? 'yyyy/MM/dd' : 'yyyy/MM/dd HH:mm:ss';
	
			var sHour = $('.start .ipt_time', this.widget);
			var sMin = $('.start .ipt_min', this.widget);
			
			var eHour = $('.end .ipt_time', this.widget);
			var eMin = $('.end .ipt_min', this.widget);

			var sDateText = "" + sDate.getFullYear() + sDate.getMonth() + sDate.getDay();
			var eDateText = "" + eDate.getFullYear() + eDate.getMonth() + eDate.getDay();

			if(sDateText != eDateText){
				alert("공용 회의실은 반복 예약이 불가능합니다.");
				return false;
			}

			if (allDay) {
				sDate.setHours(0, 0);
				eDate.addDays(1).setHours(0, 0);
			} else {
				sDate.setHours(sHour.val(), sMin.val());
				eDate.setHours(eHour.val(), eMin.val());
			}
			var obj = {
				userName: this.options.user.fullName,
				userEmail: this.options.user.primaryEmail,
				summary: summary,
				allDay: allDay,
				sDate: DateFormat.format(sDate, dateFormat),
				eDate: DateFormat.format(eDate, dateFormat),
				resourceEmail: selectedRoom.attr('data'),
				displayName: selectedRoom.text().trim(),
				pwd: pwd,
			};
			this.options.callback.call(this, obj);
		}
		this.options.messages = [];
	},
	close: function() {
		this.widget.remove();
	},
	
	validate: function() {
		var _this = this;
		this.widget.find('.validate').each(function() {
			var $targetEl = $(this);
			var dataType = $targetEl.data('type');
			
			if (JSV.isNotEmpty(dataType)) {
				if (dataType == 'resource' && $targetEl.data('default') == $targetEl.text()) {
					_this.pushMessage($targetEl);
				}
				if (dataType == 'time' && !$('#allTimeChk', this.widget).prop('checked') && JSV.isEmpty($targetEl.val())) {
					_this.pushMessage($targetEl);
				}
			} else if (JSV.isEmpty($targetEl.val())) {
				_this.pushMessage($targetEl);
			}
		});
		
		if ($('#reservePW').is(':visible') && JSV.isEmpty($('#reservePW', _this.widget).val())) {
			_this.options.messages.push('[ 비밀번호 ]는 필수 입력사항입니다.');
		}
		
		if (JSV.isNotEmpty(this.options.messages)) {
			alert(this.options.messages.join('\r\n'));
			return false;
		}
		
		return true;
	},
	pushMessage: function($targetEl) {
		this.options.messages.push('[ ' + $targetEl.data('vdFieldname') + ' ]를 다시 확인하여 주세요.');
	}
};