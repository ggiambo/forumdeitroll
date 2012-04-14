<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<jsp:include page="incTop.jsp"/>
<div id="main">

	<c:forEach items="${messages}" var="thread" varStatus="index">
		<c:choose>
			<c:when test="${index.count % 2 == 0}">
				<c:set var="rowclass" value="msgEven"/>
			</c:when>
			<c:otherwise>
				<c:set var="rowclass" value="msgOdd"/>
			</c:otherwise>
		</c:choose>
		<div id="threadTree_${thread.id}" class="${rowclass} threadBox">
			<span class="threadTitle"><fdt:threadprettyurl subject="${thread.subject}" threadId="${thread.id}" msgId="${thread.id}"/></span>
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
				<c:choose>
					<c:when test="${thread.numberOfMessages > 1}">
						<a id="plus_${thread.id}" href="javascript:openThreadTree('${thread.id}');"><img src="./images/plus_sign.gif" alt="Espandi Thread"></a>
						<a style="display:none" id="minus_${thread.id}" href="javascript:closeThreadTree('${thread.id}');"><img src="./images/minus_sign.gif" alt="Chiudi Thread"></a>
					</c:when>
					<c:otherwise>
						-
					</c:otherwise>
				</c:choose>
				Iniziato da
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
			<div class="threadTreeEntries"></div>
		</div>
	</c:forEach>

</div>

<div id="footer">
	<jsp:include page="incPrevNext.jsp" />
</div>
<jsp:include page="incBottom.jsp" />