/**
 * options
 *	- success 	: 유효성 검증성공시 후처리 (type: Function)
 *	- fail		: 유효성 검증실패시 후처리 (type: Function)
 *	- addition 	: 공통으로 처리하지 못하는 (커스텀한)유효성 검증로직 추가 (type: Function)
 */
(function($) {
	$.fn.validate = function(options) {
		var target = 'input:text:visible,select:visible,textarea:visible';
		
		var validator = new Validator();
		var messageManager = new MessageManager();
		var formInfo = new FormInfo(this);
		
		var messages = messageManager.messages;
		
		this.find(target).each(function() {
			var $targetEl = $(this);

			if (!$targetEl.is('[data-vd-validation]')) {
				return true;
			}
			
			formInfo.addFieldName($targetEl.attr('name'), $targetEl.data('vdFieldname'));
			
			var validations = $targetEl.data('vdValidation').split(' ');
			
			$.each(validations, function() {
				var method = this;
				validator.methods[method]($targetEl)
					? ''
					: messageManager.addMessage($targetEl.data('vdFieldname'), validator.message[method]);
			});
		});
		
		if (options.addition) {
			options.addition(messageManager, formInfo.getFormInfoData());
		}
		
		if (!JSV.isEmpty(messages)) {
			if (options.fail) {
				options.fail(messages);				
			} else {
				alert(messages.join('\r\n'));
			}
		} else {
			options.success(formInfo.getFormInfoData());
		}
	};
	
	function Validator() {
		this.methods = {
			required: function(el) {
				if (el.prop("tagName") === 'SELECT') {
					return el.find('option').length > 0 || el.find('option:selected').length > 0;
				} else {
					return $.trim(el.val()).length > 0;
				}
			},
 			number: function(el) {
 				var reg = /^\-?[0-9]+$/;
 				if (JSV.isEmpty(el.val())) {
					return true;
				}
 				if (el.attr('pattern')) {
 					reg = new RegExp(el.attr('pattern'));
 				}
 				return reg.test(el.val());
 			},
 			decimal: function(el) {
 				if (JSV.isEmpty(el.val())) {
					return true;
				}
 				return /^\-?[0-9]+(|\.?[0-9]+)$/.test(el.val());
 			},
			email: function(el) {
				if (JSV.isEmpty(el.val())) {
					return true;
				}
				return /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i.test(el.val());
			},
			url: function(el) {
				if (JSV.isEmpty(el.val())) {
					return true;
				}
				return /^[a-zA-Z0-9]+$/g.test(el.val());
			},
			phonenumber: function(el) {
				if (JSV.isEmpty(el.val())) {
					return true;
				}
				return /^((\+([0-9]+))?[-.) ])?\(?([0-9])+\)?[-. ]?([0-9])+[-. ]?([0-9])+$/.test(el.val());
			}
		};
		
		this.message = {
			required: JSV.getLang('msg', '047'),
			email: JSV.getLang('msg', '048'),
			url: JSV.getLang('msg', '049'),
			number: JSV.getLang('msg', '010'),
			decimal: JSV.getLang('msg', '010'),
			phonenumber: JSV.getLang('msg', '054'),
		};
	}
	
	function MessageManager(fieldName, message) {
		this.messages = [];
		this.addMessage = function(fieldName, message) {
			this.messages.push('[ ' + fieldName + ' ] ' + message);
		};
	}
	
	function FormInfo($form) {
		var formInfoData = _makeFormInfoData($form.serializeObject());
		
		this.addFieldName = function(key, fieldName) {
			if (formInfoData.hasOwnProperty(key)) {
				formInfoData[key].fieldName = fieldName;				
			} else {
				formInfoData = {
					key: key,
					fieldName: fieldName
				};
			}
		};
		
		this.getFormInfoData = function() {
			return formInfoData;
		};
		
		function _makeFormInfoData(formObj) {
			var result = {};
			for(key in formObj) {
				result[key] = {
					key: key,
					value: formObj[key],
					fieldName: null
				};
			}
			return result;
		}
	}
}(jQuery));