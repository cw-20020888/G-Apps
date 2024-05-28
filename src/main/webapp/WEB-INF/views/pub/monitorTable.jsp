<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tld/customTagLibrary.tld" prefix="util" %>
<tiles:insertDefinition name="template/2">
<tiles:putAttribute name="resource">
<style type="text/css">
th, td {height:30px; border:1px solid #000;}
th {font-size:14px; color:#000;}
td {font-size:12px; color:#000;}
</style>
<script type="text/javascript">
$(function() {
	var jsonData = <c:out value="${jsonData}" default="null" escapeXml="false" />;
	$.each(jsonData.resources, function() {
		$('#resourcesTable > tbody:last').append('<tr><td class="name">'+ this.buildingId + '-' + this.floorName + '-' + this.name +'</td><td class="url"><a href="#">https://gapps.coway.com/pub/monitor?resources='+ this.email +'</a></td></tr>');
	});
	
	$('td.url a').click(function() {
		$(this).attr('href', $(this).text());
	});
});
</script>
<!-- &nbsp; -->
</tiles:putAttribute>
<tiles:putAttribute name="body">
<table id="resourcesTable" style="width:100%">
	<tbody>
		<tr>
			<th width="300px">건물-층-회의실</th>
			<th width="*">URL</th>
		</tr>
	</tbody>
</table>
</tiles:putAttribute>
</tiles:insertDefinition>
