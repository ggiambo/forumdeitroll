<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<div id="main">
	<fdt:delayedScript dump="false">
		var token = "${anti_xss_token}";
	</fdt:delayedScript>

	<c:forEach items="${messages}" var="msg" varStatus="index">
		<c:set var="index" value="${index.count}" scope="request"/>
		<c:choose>
			<c:when test="${index.index % 2 == 0}">
				<c:set var="rowclass" value="msgEven"/>
			</c:when>
			<c:otherwise>
				<c:set var="rowclass" value="msgOdd"/>
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${msg.visible}">
				<c:set var="messagesBoxClass" value="messagesBox"/>
			</c:when>
			<c:otherwise>
				<c:set var="messagesBoxClass" value="messagesBoxInvisible"/>
			</c:otherwise>
		</c:choose>
		<div class="${messagesBoxClass} ${rowclass}">
			<c:set var="msg" value="${msg}" scope="request"/>
			<jsp:include page="../incMessage.jsp"/>
		</div>
		<c:if test="${index.index % 4 == 0}">
			<c:if test="${empty loggedUser or loggedUser.preferences['hideFakeAds'] != 'checked'}">
				<jsp:include page="../incFakeAd.jsp"/>
			</c:if>
		</c:if>
	</c:forEach>
</div>

<div id="footer">
	<c:if test="${!empty page}">
		<fdt:pager handler="Messages"></fdt:pager>
	</c:if>
</div>
