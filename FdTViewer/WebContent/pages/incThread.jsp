<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<ul>
	<c:set var="liStyle" value="margin-left:15px" />
	<c:if test="${index == 1}">
		<c:set var="liStyle" value="" />
	</c:if>
	<li style="${liStyle}">	
		<div class="messagesBox">
			<c:set var="msg" value="${message.content}" scope="request"/>
			<jsp:include page="incMessage.jsp"/>
		</div>
		<c:if test="${not empty message.children}">
			<c:forEach items="${message.children}" var="child">
				<c:set var="message" value="${child}" scope="request"/>
				<c:set var="index" value="${index + 1}" scope="request"/>
				<jsp:include page="incThread.jsp"/>
			</c:forEach>
		</c:if>
	</li>
</ul>
