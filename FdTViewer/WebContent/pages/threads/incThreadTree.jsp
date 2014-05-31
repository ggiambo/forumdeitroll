<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:set var="message" value="${msg}" scope="request"/>
<c:set var="msg" value="${message.content}" scope="request"/>
<ul class="thread${msg.threadId}">
	<li>	
		<div>
			<img class="threadMessageClosed thread${msg.threadId}msgopener" src="images/plus_sign.gif" onClick="showMessageInThread('${msg.id}', this)" onMouseOver="this.style.cursor='pointer'"/>
			<img class="threadMessageOpen" src="images/minus_sign.gif" onClick="hideMessageInThread('${msg.id}', this)" onMouseOver="this.style.cursor='pointer'"/>
			<a href="Threads?action=getByThread&amp;threadId=${msg.threadId}#msg${msg.id}"> ${msg.subject} </a>
			di
			<c:choose>
					<c:when test="${empty msg.author.nick}">
						Non Autenticato
					</c:when>
					<c:otherwise>
						<a href="Messages?action=getByAuthor&author=${msg.author.nick}">${msg.author.nick}</a>
					</c:otherwise>
				</c:choose>
			il <fmt:formatDate value="${msg.date}" pattern="dd.MM.yyyy"/> alle <fmt:formatDate value="${msg.date}" pattern="HH:mm"/> Ranking: ${msg.rank}
			<c:if test="${msg.threadId == msg.id}">
				<a href="javascript:$('.thread${msg.threadId}msgopener').click(); return false;">Apri tutti</a>
				<a href='javascript:nascondiQuotes(${msg.threadId}); return false;'>Nascondi quotes</a>
			</c:if>
		</div>
		<c:if test="${not empty message.children}">
			<c:forEach items="${message.children}" var="msg">
				<c:set var="msg" value="${msg}" scope="request"/>
				<jsp:include page="incThreadTree.jsp"/>
			</c:forEach>
		</c:if>
	</li>
</ul>
