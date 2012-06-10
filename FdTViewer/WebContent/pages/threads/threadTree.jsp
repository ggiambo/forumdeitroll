<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="message" value="${msg}" scope="request"/>
<ul>
	<c:set var="msg" value="${message.content}" scope="request"/>
	<li>	
		<div>
			<img class="threadMessageClosed" src="images/plus_sign.gif" onClick="showMessageInThread('${msg.id}', this)" onMouseOver="this.style.cursor='pointer'"/>
			<img class="threadMessageOpen" src="images/minus_sign.gif" onClick="hideMessageInThread('${msg.id}', this)" onMouseOver="this.style.cursor='pointer'"/>
			<a href="Threads?action=getByThread&amp;threadId=${msg.threadId}#msg${msg.id}">
				<c:choose>
					<c:when test="${empty msg.author.nick}">
						Non Autenticato
					</c:when>
					<c:otherwise>
						${msg.author.nick}
					</c:otherwise>
				</c:choose>
			</a>
			il <fmt:formatDate value="${msg.date}" pattern="dd.MM.yyyy"/> alle <fmt:formatDate value="${msg.date}" pattern="HH:mm"/>
		</div>
		<c:if test="${not empty message.children}">
			<c:forEach items="${message.children}" var="msg">
				<c:set var="msg" value="${msg}" scope="request"/>
				<jsp:include page="threadTree.jsp"/>
			</c:forEach>
		</c:if>
	</li>
</ul>
