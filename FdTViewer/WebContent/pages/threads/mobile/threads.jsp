<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<c:forEach items="${messages}" var="thread" varStatus="index">
	<c:choose>
		<c:when test="${index.count % 2 == 0}">
			<c:set var="rowclass" value="msgEven"/>
		</c:when>
		<c:otherwise>
			<c:set var="rowclass" value="msgOdd"/>
		</c:otherwise>
	</c:choose>
	<div id="threadTree_${thread.id}" class="${rowclass} threadBox" onclick="this.childNodes[1].click()">
		<c:choose>
			<c:when test="${param['action'] == 'getThreadsByLastPost' || param['action'] == 'getAuthorThreadsByLastPost'}">
				<a href="Threads?action=getByThread&threadId=${thread.id}#msg${thread.lastId}">${thread.subject}</a>
			</c:when>
			<c:when test="${param['action'] == 'getThreads'}">
				<a href="Threads?action=getByThread&threadId=${thread.id}">${thread.subject}</a>
			</c:when>
			<c:otherwise></c:otherwise>
		</c:choose>
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
				<c:when test="${param['action'] == 'getThreadsByLastPost' || param['action'] == 'getAuthorThreadsByLastPost'}">
					Ultimo messaggio di
				</c:when>
				<c:when test="${param['action'] == 'getThreads'}">
					Iniziato da
				</c:when>
				<c:otherwise></c:otherwise>
			</c:choose>
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
						<a href="<c:out value="${authorURL}" escapeXml="true" />">
							${thread.author.nick}
							<c:url value="Misc" var="avatarURL">
								<c:param name="action" value="getAvatar"/>
								<c:param name="nick" value="${thread.author.nick}"/>
							</c:url>
							<img src="${avatarURL}" class=avatar></a>
					</c:otherwise>
				</c:choose>
			</span>
			il <fmt:formatDate value="${thread.date}" pattern="dd.MM.yyyy"/> alle <fmt:formatDate value="${thread.date}" pattern="HH:mm"/>
		</div>
	</div>
</c:forEach>

<c:if test="${!empty page}">
	<fdt:pager handler="Messages"/>
</c:if>