<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:forEach items="${messages}" var="msg" varStatus="index">
	<c:set var="margin" value="${msg.indent * 10}"/>
	<div style="margin-left:${margin}px;">
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
</c:forEach>
