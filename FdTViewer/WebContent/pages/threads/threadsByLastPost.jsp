<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<div id="main">
	<c:forEach items="${messages}" var="msg" varStatus="index">
		<c:choose>
			<c:when test="${index.count % 2 == 0}">
				<c:set var="rowclass" value="msgEven"/>
			</c:when>
			<c:otherwise>
				<c:set var="rowclass" value="msgOdd"/>
			</c:otherwise>
		</c:choose>
		<div class="${rowclass} threadBox">
			<span class="threadTitle">
				<a href="Threads?action=getByThread&threadId=${msg.threadId}#msg${msg.id}">${msg.subject}</a>
			</span>
			<c:if test="${!empty msg.forum}">
				<span class="tagForum">${msg.forum}</span>
			</c:if>
			<div class="threadDetail">
			- Ultimo messaggio di
			<span class="msgAuthor">
				<c:choose>
					<c:when test="${empty msg.author.nick}">
						Non Autenticato
					</c:when>
						<c:otherwise>
							<c:url value="Messages" var="authorURL">
								<c:param name="action" value="getByAuthor"/>
								<c:param name="author" value="${msg.author.nick}"/>
							</c:url>
							<a href="<c:out value="${authorURL}" escapeXml="true" />">${msg.author.nick}</a>
						</c:otherwise>
				</c:choose>
			</span>
			il <fmt:formatDate value="${msg.date}" pattern="dd.MM.yyyy"/> alle <fmt:formatDate value="${msg.date}" pattern="HH:mm"/>
			</div>
		</div>
	</c:forEach>

</div>

<div id="footer">
	<c:if test="${!empty page}">
		<fdt:pager handler="Messages"></fdt:pager>
	</c:if>
</div>
