<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<div id="main">

	<fdt:delayedScript dump="false">
		var token = "${anti_xss_token}";
	</fdt:delayedScript>

	<c:set var="message" value="${root}" scope="request"/>
	<c:set var="depth" value="0" scope="request"/>
	<c:set var="index" value="0" scope="request"/>
	<jsp:include page="incThread.jsp"/>

</div>
