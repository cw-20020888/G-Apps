<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/customTagLibrary.tld" prefix="util" %>
<tiles:insertDefinition name="template/mobile">
<tiles:putAttribute name="body">
	<div id="info" data-role="page" class="mobile rsv_main">
    <script>
    $(document).on('pageinit','#info', function() {
    	var jsonData = <c:out value="${jsonData}" default="null" escapeXml="false" />;
    	
    	setText(jsonData.timeInfo, $('.time', '#info'));
    	setText(jsonData.name, $('.name', '#info'));
    	setText(jsonData.cellPhone, $('.num', '#info'));
    	setText(jsonData.email, $('.email', '#info'));
    	
    	if (!jsonData.user.googleLogin && jsonData.user.intraEmail.toLowerCase() == jsonData.email.toLowerCase()) {
    		$('.btn_delete').show();
    	} else {
    		$('.btn_delete').hide();
    	}
    	
    	$('.btn_reserve', '#info').off('click');
    	$('.btn_reserve', '#info').on('click', function() {
			if (jsonData.user.googleLogin) {
	    		var today = new Date().setHours(0, 0, 0, 0);
				var addDays = (jsonData.date - today) / 86400000;
				
				blankGoogleCalendarInsert(null, null, addDays, jsonData.room);
			} else {
	    		$.mobile.changePage('/mobile/schedule/create', {
	    			data: {
	    				room: jsonData.room,
	    				floor: jsonData.floor,
	    				buildingId: jsonData.buildingId
	    			}
	    		});
			}
    	});
    	
    	$('.rsv_info .btn_delete').off('click');
	    $('.rsv_info .btn_delete').on('click', function(){
	    	$('.layer_password').show();
	    });
	    
	    $('.btn_cancle').off('click');
	    $('.btn_cancle').on('click', function(){
	    	$('.layer_password').hide();
	    });
	    
	    $('.btn_confirm').off('click');
	    $('.btn_confirm').on('click', function() {
	    	var pwd = $('#reservePW').val();
	    	
	    	if (JSV.isEmpty(pwd)) {
    			alert('[ 비밀번호 ]는 필수 입력사항입니다.');
    			return false;
    		}
	    	
	    	JSV.loadJSON('/mobile/schedule/delete.json', null, {
	    		data: {pwd : pwd, eventId: jsonData.id, resourceEmail: jsonData.room},
	    		success: function(data) {
					alert('삭제되었습니다.');
					$('.btn_prev').trigger('click');
				}, error: function(xhr) {
					alert(xhr.message);
	    		}, complete: function() {
    				JSV.loadJSON('/schedule/updateDataStore.json', 'resourceEmail=' + jsonData.room);
    			}
	    	});
	    });
    	
    	function setText(text, area) {
    		if (JSV.isNotEmpty(text)) {
    			area.text(text);
    		} else {
    			area.hide();
    		}
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
			<div class="rsv_info">
				<div class="tit_area">
					<a class="btn_delete"><img src="<c:url value="/resources/css/images/btn_delete.png" />" width="37" height="37"></a>
					<h2 class="tit">예약정보</h2>
				</div>
				<div class="info_area">
					<p class="time"></p>
					<p class="name"></p>
					<p class="num"></p>
					<p class="email"></p>
				</div>
			</div>
		</div>
		<div class="layer_wrap layer_password" style="top: 0;">
			<div class="layer_area">
				<div class="layer">
					<div class="form_area">
						<label for="reservePW" class="lbl_pw">비밀번호를 입력해주세요.</label>
						<input type="password" name="reserve_password" id="reservePW" class="ipt_pw">
					</div>
					<div class="btn_area">
						<a href="javascript:void(0);" class="btn btn_cancle">취소</a>
						<a href="javascript:void(0);" class="btn btn_confirm">확인</a>
					</div>
				</div>
			</div>
		</div><!--layer_password/e-->
	</div>
</tiles:putAttribute>
</tiles:insertDefinition>