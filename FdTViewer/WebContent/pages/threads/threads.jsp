<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

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
			<span class="threadTitle">
			<a href="Threads?action=getByThread&threadId=${thread.id}#msg${thread.id}">${thread.subject}</a>
			</span>
			 (${thread.numberOfMessages}
			<c:choose>
				<c:when test="${thread.numberOfMessages != 1}">
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
				il <fmt:formatDate value="${thread.date}" pattern="dd.MM.yyyy"/> alle <fmt:formatDate value="${thread.date}" pattern="HH:mm"/> Ranking: ${thread.rank}
			</div>
			<div class="threadTreeEntries"></div>
		</div>
	</c:forEach>

</div>

<div id="footer">
	<c:if test="${!empty page}">
		<fdt:pager handler="Messages"></fdt:pager>
	</c:if>
</div>
