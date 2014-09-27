<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<c:choose>
	<c:when test="${depth > 15}">
		<c:set var="level" value="0"/>
	</c:when>
	<c:otherwise>
		<c:set var="level" value="${depth mod 16}"/>
	</c:otherwise>
</c:choose>

<ul class="threadlist dl${level}">
<c:set var="depth" value="${depth + 1}" scope="request"/>
	<li>
		<c:choose>
			<c:when test="${message.content.visible}">
				<c:set var="messagesBoxClass" value="messagesBox"/>
			</c:when>
			<c:otherwise>
				<c:set var="messagesBoxClass" value="messagesBoxInvisible"/>
			</c:otherwise>
		</c:choose>
		<c:choose>
			<c:when test="${index % 2 == 0}">
				<c:set var="rowclass" value="msgEven"/>
			</c:when>
			<c:otherwise>
				<c:set var="rowclass" value="msgOdd"/>
			</c:otherwise>
		</c:choose>
		<div class="${messagesBoxClass} ${rowclass}">
			<c:set var="msg" value="${message.content}" scope="request"/>
			<c:set var="index" value="${index}" scope="request"/>
			<jsp:include page="../../messages/mobile/incMessage.jsp"/>
		</div>
		<c:if test="${not empty message.children}">
			<c:forEach items="${message.children}" var="child">
				<c:set var="message" value="${child}" scope="request"/>
				<c:set var="index" value="${index + 1}" scope="request"/>
				<jsp:include page="incThread.jsp"/>
			</c:forEach>
		</c:if>
		<c:set var="depth" value="${depth - 1}" scope="request"/>
	</li>
</ul>