<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="incTop.jsp"/>

<script type="text/javascript">
	$(document).ready(function() {
		$(".avatarImg").hover(function() {
			$(this).removeClass("avatarImg").addClass("avatarImgOver");
		}, function() {
			$(this).removeClass("avatarImgOver").addClass("avatarImg");
		});
	});
</script>

<div id="main">

			<c:forEach items="${messages}" var="thread" varStatus="index">
				<c:choose>
					<c:when test="${index.count % 2 == 0}">
						<c:set var="class" value="msgEven"/>
					</c:when>
					<c:otherwise>
						<c:set var="class" value="msgOdd"/>
					</c:otherwise>
				</c:choose>
				<div class="${class} threadBox">
					<span class="threadTitle"><a href="Threads?action=getByThread&amp;threadId=${thread.id}">${thread.subject}</a></span>
					 (${thread.numberOfMessages}
					<c:choose>
						<c:when test="${thread.numberOfMessages > 1}">
							messaggi)
						</c:when>
						<c:otherwise>
							messaggio)
						</c:otherwise>
					</c:choose>
					<c:if test="${!empty thread.forum}">
						<span class="tagForum">${thread.forum}</span>
					</c:if>
					<div class="threadDetail">
					- Iniziato da
					<span class="msgAuthor">
						<c:choose>
							<c:when test="${empty thread.author.nick}">
								Non Autenticato
							</c:when>
							<c:otherwise>
								<c:url value="Messages" var="authorURL">
									<c:param name="action" value="getByAuthor"/>
									<c:param name="author" value="${thread.author.nick}"/>
								</c:url>
								<a href="<c:out value="${authorURL}" escapeXml="true" />">${thread.author.nick}</a>
							</c:otherwise>
						</c:choose>
					</span>
					il <fmt:formatDate value="${thread.date}" pattern="dd.MM.yyyy"/> alle <fmt:formatDate value="${thread.date}" pattern="HH:mm"/>
					</div>
				</div>
			</c:forEach>

		</div>

		<div id="footer">
			<jsp:include page="incPrevNext.jsp" />
		</div>
<jsp:include page="incBottom.jsp" />