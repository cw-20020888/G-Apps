<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="/WEB-INF/tld/customTagLibrary.tld" prefix="util" %>
<%@ page import="org.springframework.web.servlet.support.RequestContext" %>
<%
	request.setAttribute("lang", new RequestContext(request).getLocale());
%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, target-densitydpi=medium-dpi">
<meta name="format-detection" content="telephone=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<title><fmt:message key="sys.title" /></title>
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/mobile/m.reset.css?nocache=${util:getServerDate()}" />" />
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/mobile/m.style.css?nocache=${util:getServerDate()}" />" />
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/mobile/m.netmarble.css?nocache=${util:getServerDate()}" />" />
<script src="<c:url value="/resources/lib/jquery-2.1.1.min.js?nocache=${util:getServerDate()}" />"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/mobile/jquery-ui.min.js?nocache=${util:getServerDate()}"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/mobile/jquery.mobile-1.4.5.min.js?nocache=${util:getServerDate()}"></script>
<script type="text/javascript" src="<c:url value="/resources/lib/jquery-ui-1.11.1/jquery-ui-datepicker.regional.js?nocache=${util:getServerDate()}" />"></script>
<script type="text/javascript" src="<c:url value="/resources/lib/JSV.js?nocache=${util:getServerDate()}" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/date/DateFormat.js?nocache=${util:getServerDate()}" />"></script>
<script type="text/javascript">
JSV.init('<%= request.getContextPath() %>', '', null, '<c:out value="${lang.language}" />', 'ko', true);
function equals(v1, v2) {
	return v1 == v2;
}

function blankGoogleCalendarInsert(startHours, startMin, addDays, resourceEmail) {
	var href = 'https://calendar.google.com/calendar/gpcal?tab=wc#~calendar:view=e&';
	
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
</script>
<tiles:insertAttribute name="resource" defaultValue="" />
</head>
<body>
	<tiles:insertAttribute name="body" defaultValue="" />
</body>
</html>