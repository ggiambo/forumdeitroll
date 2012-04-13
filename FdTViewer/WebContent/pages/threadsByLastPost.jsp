<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<jsp:include page="incTop.jsp"/>
<div id="main">
	<c:forEach items="${messages}" var="msg" varStatus="index">
		<c:choose>
			<c:when test="${index.count % 2 == 0}">
				<c:set var="class" value="msgEven"/>
			</c:when>
			<c:otherwise>
				<c:set var="class" value="msgOdd"/>
			</c:otherwise>
		</c:choose>
		<div class="${class} threadBox">
			<span class="threadTitle"><fdt:threadprettyurl subject="${msg.subject}" threadId="${msg.threadId}" msgId="${msg.id}"/></span>
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
<jsp:include page="incBottom.jsp" />