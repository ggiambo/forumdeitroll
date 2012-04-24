<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<%--
<ul class="threadlist${depth mod 4}">
--%>
<ul class="threadlist">
<c:set var="depth" value="${depth + 1}" scope="request"/>
	<li>
		<div class="messagesBox">
			<c:set var="msg" value="${message.content}" scope="request"/>
			<jsp:include page="incMessage.jsp"/>
		</div>
		<c:if test="${not empty message.children}">
			<c:forEach items="${message.children}" var="child">
				<c:set var="message" value="${child}" scope="request"/>
				<jsp:include page="incThread.jsp"/>
			</c:forEach>
		</c:if>
		<c:set var="depth" value="${depth - 1}" scope="request"/>
	</li>
</ul>
