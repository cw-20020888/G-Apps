<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/customTagLibrary.tld" prefix="util" %>
<tiles:insertDefinition name="template/">
<tiles:putAttribute name="resource">
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/mail.css?nocache=${util:getServerDate()}" />" />
<%@ include file="/resources/tmpl/Mail.tmpl" %>
<style>
.mail{position: unset;}
</style>
<script type="text/javascript">
$(function() {
	loadingImage.init();

	var init = true;
	var listArray = [];
	var jsonData = null;

	$('#mail').tmpl({isAdmin: true}).appendTo($('.content_area'));
	
	$('.btn_search').click(function() {
		userLoadJson($('input[id="iSch"]').val());
	});
	
	$('input[id="iSch"]').keyup(function(e) {
		var name = $(this).val();
		JSV.executeOnEnter(e, function() {
			userLoadJson(name);
		});
	}).appendTo($('.snb_sec'));
	
	$('#mailChkAll').click(function(e) {
    	$(this).toggleClass('on');
    	$(this).parents('table').find('tbody label').trigger('click', [$(this).hasClass('on') ? 'on' : 'off']);
    });
	
	$('.more').click(function() {
		if (JSV.isEmpty(jsonData.mail)) {
			popup('defaultPopup', '사용자를 선택하여 주세요.');
			return false;
		}
		if (jsonData.items.length < jsonData.maxResults) {
			popup('defaultPopup', '더 이상 불러올 메일이 없습니다.');
			return false;
		}

		mailItemsJson({nextPageToken: jsonData.nextPageToken, mail: jsonData.mail}, true);
	});
	
	$('.withdraw').click(function() {
		var checkMsgItems = getCheckMsgItems();
		
		if (JSV.isEmpty(checkMsgItems)) {
			popup('defaultPopup', '회수할 메일을 선택하여 주세요.');
			return false;
		} else {
			var adminWithdrawPopup = popup('adminWithdrawPopup').find('.ok').click(function() {
				var textArea = $(this).parents('.cause_withdraw').find('textarea');
				
				JSV.loadJSON('/admin/mail/withdrawMail.json', null, {
					type: 'POST',
					async: true,
					data: {
						opinion: textArea.val(),
						jsonStr: JSON.stringify(checkMsgItems)
					}, success: function(data) {
						adminWithdrawPopup.remove();
						
						mailItemsJson({mail: jsonData.mail}, false);
					}
				});
			}).end();
		}
	});
	
	function userLoadJson(name) {
		if (JSV.isEmpty(name)) {
			popup('defaultPopup', '이름을 입력하여 주세요.');
			return false;
		}
		
		JSV.loadJSON('/admin/mail/search.json', 'name='+name, {
			success: function(data) {
				loadingImage.show();
				
				if (JSV.isNotEmpty(data.result)) {
					$('.search_list').empty();
					
					$.each(data.result, function(i) {
						var path = this.orgUnitPath.split('/');
						var obj = {
							name: this.name.fullName, 
							info: '[' + path[path.length - 1] + '/' + this.primaryEmail +']',
							primaryEmail: this.primaryEmail
						};
						
						var searchUserTmpl = $('#searchUser').tmpl(obj).appendTo($('.search_list')).find('.result').click(function() {
							init = true;
							
							$('.search_list').find('li').removeClass('selected');
							$(this).parents('li').addClass('selected');
							
							JSV.loadJSONRequest = {};
							mailItemsJson({mail: $(this).data('mail')}, false);
						});
						
						if (data.result.length -1 == i) {
							$('.search_list').find('li:first-child a').trigger('click');
						}
					});
				} else {
					popup('defaultPopup', '존재하지 않는 사용자 입니다.');
					$('input[id="iSch"]').val('');
					$('label.lbl_search').show();
					loadingImage.hide();
				}
			}
		});
	}
	
	function drawItems(items, maxResults) {
		$('#mailTbody').tmpl({items: items}).appendTo($('.tbl tbody')).find('label').click(function(e, type) {
			var $prev = $(this).prev();
			
			if (!$prev.is(':disabled')) {
				if (type == 'on') {
					$prev.prop('checked', true);
					$prev.addClass('on');
				} else if (type == 'off') {
					$prev.prop('checked', false);
					$prev.removeClass('on');
				} else {
					$prev.trigger('click', [$(this).prev().toggleClass('on')]);	
				}
			}
		}).end().find('.subject').click(function() {
			$(this).closest('tr').find('label').trigger('click');
		});
	}
	
	function mailItemsJson(obj, isMore) {
		if (!isMore) {
			$('.tbl tbody').empty();
		}

		JSV.loadJSON('/admin/mail/items.json', null, {
			data: obj,
			async: true,
			beforeSend: function(xhr) {
				loadingImage.show();
			}, success: function(data) {
				jsonData = data;
				
				if (JSV.isEmpty(jsonData.items) && !init) {
					popup('defaultPopup', '더 이상 불러올 메일이 없습니다.');
				} else {
					drawItems(convertItems(jsonData.items), jsonData.maxResults);	
					
					init = false;
				}
				loadingImage.hide();
			}
		});
	}
	
	function convertItems(items, array) {
		var convertItemArray = [];
		$.each(items, function() {
			var item = this;
			var headers = this.payload.headers;
			var obj = {
				users: null,
				id: this.id,
				status : null,
				date: DateFormat.format(new Date(getHeaderValue(headers, 'Date')), 'MM-dd HH:mm'),
				fullDate: DateFormat.format(new Date(getHeaderValue(headers, 'Date')), 'yyyy-MM-dd HH:mm:ss'),
				subject: getHeaderValue(headers, 'Subject'),
				msgId: getHeaderValue(headers, 'Message-ID'),
				from: getHeaderValue(headers, 'From', true),
				to: checkToUserDomain(getHeaderValue(headers, 'To', true)),
				cc: checkToUserDomain(getHeaderValue(headers, 'Cc', true)),
				bcc: checkToUserDomain(getHeaderValue(headers, 'Bcc', true)),
			};
			obj.users = JSV.isNotEmpty(obj.to) ? obj.to : '';
			obj.users = JSV.isNotEmpty(obj.cc) ? (JSV.isNotEmpty(obj.users) ? obj.users + ',' + obj.cc : obj.cc): obj.users;
			obj.users = JSV.isNotEmpty(obj.bcc) ? (JSV.isNotEmpty(obj.users) ? obj.users + ',' + obj.bcc : obj.bcc): obj.users;

			var logItem = $.grep(jsonData.jsonArray, function(json) {
				return json.ItemId == item.id;
			});
			if (!JSV.isEmpty(logItem)) {
				obj.status = logItem[0].Status;
			}
			
			convertItemArray.push(obj);
		});
		
		listArray = listArray.concat(convertItemArray);
		
		return convertItemArray;
	}
	
	function checkToUserDomain(value, attr) {
		var returnVal = '';
		if (value.indexOf(',') > -1) {
			value = value.trim();
			valueArr = value.split(',');
			
			$.each(valueArr, function() {
				if (this.match('<fmt:message key="sys.domains" />')) {
					returnVal += JSV.isNotEmpty(returnVal) ? ', ' + this : this;
				}
			});
		} else {
			returnVal = JSV.isNotEmpty(value.match('<fmt:message key="sys.domains" />')) ? value : '';
		}
		
		return returnVal;
	}

	function getHeaderValue(headers, name, removeTag) {
		return $.map(headers, function(header) {
			var value = header.name.toLowerCase() == name.toLowerCase() ? header.value : null;
			if (removeTag && JSV.isNotEmpty(value)) {
				if (value.indexOf('\,') > -1) {
					var tempArr = value.split(',');
					value = '';
					$.each(tempArr, function(i) {
						s = this.toString();
						value += regMatch(s) + (tempArr.length - 1 == i ? '' : ', ');
					});
				} else {
					value = regMatch(value);
				}
			}
			
			return value;
		}).join(' ');
	}
	
	function regMatch(value) {
		var matches = value.match(/\<[^].*\>/g);
		if (JSV.isNotEmpty(matches)) {
			for (var i = 0; i < matches.length; i++) {
			    var str = matches[i];
			    value = str.slice(1, str.length - 1);
			}
		}
		
		return value;
	}
	
	function getCheckMsgItems() {
		return $.map($('tbody .mail_chk.on'), function(item) {
			var val = $(item).val();
			
			return $.grep(listArray, function(item) {
				return item.msgId == val;
			});
		});
	}
	
	function popup(popupId, message) {
		var $popupWrap = $('.popup_wrap').length > 0 ? $('.popup_wrap') : $('#' + popupId).tmpl({message: message}).appendTo($('.content_area')).find('.close').click(function() {
			$(this).parents('.popup_wrap').remove();
		}).end();
		
		$popupWrap.css({'z-index' : '99'});
		
		return $popupWrap;
	}
});

function toUserString(users) {
	if (users.indexOf(',') > -1) {
		var toArray = users.split(',');
		
		return toArray[0] + '외 ' + (toArray.length - 1) + '명';
	}
	return users;
}
</script>
</tiles:putAttribute>
<tiles:putAttribute name="body">
<!-- &nbsp; -->
</tiles:putAttribute>
</tiles:insertDefinition>