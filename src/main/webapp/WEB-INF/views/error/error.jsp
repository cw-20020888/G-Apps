<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="/WEB-INF/tld/customTagLibrary.tld" prefix="util" %>
<%@ page import="org.springframework.web.servlet.support.RequestContext, org.springframework.http.HttpStatus" %>
<%
	try 
	{
		String ex = (String) request.getAttribute("exception");
		if (ex != null)
		{
			request.setAttribute("errName", ex);
		} 
		else 
		{
			request.setAttribute("errName", "DEFAULT");
		}
	} 
	catch (Exception e) 
	{
		request.setAttribute("errName", "DEFAULT");
	}
	request.setAttribute("lang", new RequestContext(request).getLocale());
%>
<!doctype html>
<html lang="en">
    <head>
        <meta charset="UTF-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta http-equiv="Content-Script-Type" content="text/javascript" />
		<meta http-equiv="Content-Style-Type" content="text/css" />
		<title><fmt:message key="sys.title" /></title>
		<link href="<c:url value="/resources/css/common.css?nocache=${util:getServerDate()}" />" type="text/css" rel="stylesheet"/>
        <link href="<c:url value="/resources/css/view.css?nocache=${util:getServerDate()}" />" type="text/css" rel="stylesheet"/>
		<!--[if lt IE 9]>
		<script src="<c:url value="/resources/lib/jquery-1.12.4.min.js?nocache=${util:getServerDate()}" />"></script>
		<![endif]-->
		<!--[if gte IE 9]>
		<script src="<c:url value="/resources/lib/jquery-2.1.1.min.js?nocache=${util:getServerDate()}" />"></script>
		<![endif]-->
		<!--[if !IE]> -->
		<script src="<c:url value="/resources/lib/jquery-2.1.1.min.js?nocache=${util:getServerDate()}" />"></script>
		<!-- <![endif]-->        
        <script type="text/javascript" src="<c:url value="/resources/lib/JSV.js?nocache=${util:getServerDate()}" />"></script>
        <script type="text/javascript">
        JSV.init('<%= request.getContextPath() %>', '', null, '<c:out value="${lang.language}" />', 'ko');
        </script>
        <script type="text/javascript">
        function goRedirect() {
        	var redirectUrl = '<fmt:message key="sys.error.redirectUrl.${errName}" />';
        	if(redirectUrl) {
        		JSV.doGET(redirectUrl, parent ? parent : 'mainLayer');
        	}
        }
        </script>
    </head>
    <body>
        <div class="wrap">
            <div class="alertBox">
                <div class="alertBox_header">
                    <h1><fmt:message key="sys.error.header.${errName}" /></h1>
                </div>
                <div class="alertBox_body">
                    <p class="pg_q">
           				<fmt:message key="sys.error.body.${errName}" />
                    </p>
                    <div class="btn_area">
                    	<fmt:message key="sys.error.redirectUrl.${errName}" var="redirectUrl"/>
                       	<fmt:message key="sys.error.redirectBtn.${errName}" var="redirectBtn"/>
                       	<c:set var="redirectUrlNo" value="???sys.error.redirectUrl.${errName}???" />
                       	<c:set var="redirectBtnNo" value="???sys.error.redirectBtn.${errName}???" />
                    
                    	<c:choose>
                    	<c:when test="${redirectUrl != redirectUrlNo && redirectBtn != redirectBtnNo}">
                    	<span class="GroupButton">
                    		<a href="javascript:goRedirect()" class="btnItem showItem">
	                        	<span class="btnEdge" ></span>
	                        	<span class="btnTxt"><fmt:message key="sys.error.redirectBtn.${errName}" /></span>
	                        	<span class="btnEdge"></span>
	                        </a>
                       	</span>
                    	</c:when>
                    	<c:otherwise>
                    	<span class="GroupButton">
                    		<a href="javascript:history.back()" class="btnItem showItem">
	                        	<span class="btnEdge" ></span>
	                        	<span class="btnTxt"><fmt:message key="btn.051" /></span>
	                        	<span class="btnEdge"></span>
	                        </a>
                       	</span>
                    	</c:otherwise>
                    	</c:choose>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
<!-- 
	authorities: ${sessionScope.userInfo.authorities}
 -->
<!-- 
	exceptionStack: ${exceptionStack}
-->