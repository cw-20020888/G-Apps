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
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="Content-Script-Type" content="text/javascript" />
<meta http-equiv="Content-Style-Type" content="text/css" />
<title><fmt:message key="sys.title" /></title>
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/view.css?nocache=${util:getServerDate()}" />" />
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/common.css?nocache=${util:getServerDate()}" />" />
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/net_common.css?nocache=${util:getServerDate()}" />" />
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/reset.css?nocache=${util:getServerDate()}" />" />
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/loading.css?nocache=${util:getServerDate()}" />
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/lib/jquery-ui-1.11.1/jquery-ui.css?nocache=${util:getServerDate()}" />" />
<!--[if lt IE 9]>
<script src="<c:url value="/resources/lib/jquery-1.12.4.min.js?nocache=${util:getServerDate()}" />"></script>
<![endif]-->
<!--[if gte IE 9]>
<script src="<c:url value="/resources/lib/jquery-2.1.1.min.js?nocache=${util:getServerDate()}" />"></script>
<![endif]-->
<!--[if !IE]> -->
<script src="<c:url value="/resources/lib/jquery-2.1.1.min.js?nocache=${util:getServerDate()}" />"></script>
<!-- <![endif]-->
<script type="text/javascript" src="<c:url value="/resources/lib/jquery-ui-1.11.1/jquery-ui.min.js?nocache=${util:getServerDate()}" />"></script>
<script type="text/javascript" src="<c:url value="/resources/lib/jquery.tmpl.js?nocache=${util:getServerDate()}" />"></script>
<script type="text/javascript" src="<c:url value="/resources/lib/JSV.js?nocache=${util:getServerDate()}" />"></script>
<script type="text/javascript" src="<c:url value="/resources/lib/jquery-ui-1.11.1/jquery-ui-datepicker.regional.js?nocache=${util:getServerDate()}" />"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/loading.js?nocache=${util:getServerDate()}"></script>
<script type="text/javascript">
JSV.init('<%= request.getContextPath() %>', '', null, '<c:out value="${lang.language}" />', 'ko', true);
function equals(v1, v2) {
	return v1 == v2;
}
</script>
<script type="text/javascript" src="<c:url value="/resources/js/date/DateFormat.js?nocache=${util:getServerDate()}" />"></script>
<tiles:insertAttribute name="resource" defaultValue="" />
<script type="text/javascript" defer="defer">
$(function() {
	JSV.tail();
	
	dynamicLayout($(window).width());
	
	$(window).on('resize', function() {
		dynamicLayout($(window).width());
	});
	
	function dynamicLayout(screenWidth) {
		if (screenWidth < 1330) {
			$('.layout_container').addClass('horizontal');
		} else {
			$('.layout_container').removeClass('horizontal');
		}
	}
});
</script>
</head>
<body class="main">
	<div>
		<div class="layout_container">
			<div class="content_area">
				<tiles:insertAttribute name="body" defaultValue="" />
			</div>
		</div>
	</div>
</body>
</html>