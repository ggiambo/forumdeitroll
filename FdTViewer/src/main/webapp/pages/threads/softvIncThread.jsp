<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<c:set var="level" value="${depth mod 16}"/>
<ul class="threadlist dl${level} largerspacing">
<c:set var="depth" value="${depth + 1}" scope="request"/>
	<li>
		<c:set var="msg" value="${message.content}" scope="request"/>
		<c:choose>
			<c:when test="${index % 2 == 0}">
				<c:set var="rowclass" value="msgEven"/>
			</c:when>
			<c:otherwise>
				<c:set var="rowclass" value="msgOdd"/>
			</c:otherwise>
		</c:choose>
		<script>
			nextMap[${msg.id}] = ${msg.nextId};
			prevMap[${msg.id}] = ${msg.prevId};
		</script>
		<c:choose>
			<c:when test="${show.id == msg.id}">
				<c:set var="softvSelectedClass" value="softvSelected"/>
			</c:when>
			<c:otherwise>
				<c:set var="softvSelectedClass" value=""/>
			</c:otherwise>
		</c:choose>
		<div id="softvEntry${message.content.id}" class="softvMsg ${rowclass} ${softvSelectedClass} ">
			<div class="msgVisible" id="msg${msg.id}">
				<a onclick="return softvSwap(${msg.threadId}, ${msg.id}, event);" href="Threads?action=softvThread&threadId=${msg.threadId}#softvMsg${msg.id}">
					${msg.subject} @ <fmt:formatDate value="${msg.date}" pattern="yyyy-MM-dd HH:mm"/>
				</a> di
				<c:choose>
					<c:when test="${empty msg.author.nick}">
						Non Autenticato
					</c:when>
					<c:otherwise>
						${msg.author.nick}&nbsp;
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<c:if test="${not empty message.children}">
			<c:forEach items="${message.children}" var="child">
				<c:set var="message" value="${child}" scope="request"/>
				<c:set var="index" value="${index + 1}" scope="request"/>
				<jsp:include page="softvIncThread.jsp"/>
			</c:forEach>
		</c:if>
		<c:set var="depth" value="${depth - 1}" scope="request"/>
	</li>
</ul>
