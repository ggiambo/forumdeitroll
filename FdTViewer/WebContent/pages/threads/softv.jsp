<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<div id="main">
	<fdt:delayedScript dump="false">
		var token = "${anti_xss_token}";
		jQuery("document").ready(function() {
			var m = window.location.href.match(/threadId=(\d+).*?#softvMsg(\d+)/);
			if (m == null) {
				return;
			}
			softvSwap(parseInt(m[1]), parseInt(m[2]), null);
		});
	</fdt:delayedScript>
	
	<c:set var="msg" value="${show}" scope="request"/>
	<div class="messagesBox" id="showMsg">
		<jsp:include page="../incMessage.jsp"/>
	</div>
	
	<c:set var="message" value="${root}" scope="request"/>
	<c:set var="depth" value="0" scope="request"/>
	<c:set var="index" value="0" scope="request"/>
	
	<script>
		var nextMap = {};
		var prevMap = {};
	</script>
	
	<div id="softvThreads">
		<div id="softvNav">
			<a id="softvPrev" href="javascript:void(0);" onclick="softvMove(${show.threadId}, ${show.id}, prevMap)">&#8592;</a>
			<a id="softvNext" href="javascript:void(0);" onclick="softvMove(${show.threadId}, ${show.id}, nextMap)">&#8594;</a>
		</div>
		<jsp:include page="softvIncThread.jsp"/>
	</div>
</div>
