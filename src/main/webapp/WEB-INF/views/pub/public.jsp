<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/customTagLibrary.tld" prefix="util" %>
<tiles:insertDefinition name="template/">
<tiles:putAttribute name="resource">
<c:set var="isappengine"><fmt:message key="sys.isappengine" /></c:set>
<c:if test="${isappengine == 'Y' }">
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/main.css?nocache=${util:getServerDate()}" />" />
</c:if>
<script type="text/javascript">
$(function() {
});
</script>
</tiles:putAttribute>
<tiles:putAttribute name="body">
	PUBLIC!!
</tiles:putAttribute>
</tiles:insertDefinition>