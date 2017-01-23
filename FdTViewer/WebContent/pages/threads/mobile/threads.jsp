<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<c:forEach items="${messages}" var="thread" varStatus="index">
	<c:choose>
		<c:when test="${param['action'] == 'getThreadsByLastPost' || param['action'] == 'getAuthorThreadsByLastPost'}">
			<c:set var="threadId" value="${thread.id}"/>
			<c:set var="messageId" value="${thread.lastId}"/>
		</c:when>
		<c:when test="${param['action'] == 'getThreads'}">
			<c:set var="threadId" value="${thread.id}"/>
			<c:set var="messageId" value="${thread.id}"/>
		</c:when>
		<c:otherwise></c:otherwise>
	</c:choose>
	<c:set var="rowclass" value="msgVisible"/>
	<c:if test="${loggedUser.wantsToHideThread(thread)}">
		<c:set var="rowclass" value="msgInvisible"/>
		<div class="threadBox">
			<div class=row onclick="showMessage(this, '${threadId}')">
				<div class=col-1>
					<img src="images/poop.png">
				</div>
				<div class=col-5>
					Messaggio cacca
				</div>
			</div>
		</div>
	</c:if>
	<div class="${rowclass} threadBox" id="msgbox${threadId}" onclick="gotoThread(${threadId},${messageId})">
		<div class=row>
			<div class=col-1>
				<img src="Misc?action=getAvatar&amp;&nick=${thread.author.nick}" class=avatar>
			</div>
			<div class=col-5>
				${thread.subject}
				<c:if test="${!empty thread.forum}">
					<span class="tagForum">${thread.forum}</span>
				</c:if>
			</div>
		</div>
		<span class=msgInfo>
			<c:choose>
				<c:when test="${param['action'] == 'getThreadsByLastPost' || param['action'] == 'getAuthorThreadsByLastPost'}">
					Ultimo messaggio di
				</c:when>
				<c:when test="${param['action'] == 'getThreads'}">
					Iniziato da
				</c:when>
				<c:otherwise></c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${empty thread.author.nick}">
					non autenticato
				</c:when>
				<c:otherwise>
					${thread.author.nick}
				</c:otherwise>
			</c:choose>
			<br>
			<i><fdt:prettyDate date="${thread.date}"/></i>
		</span>
	</div>
</c:forEach>

<c:if test="${!empty page}">
	<c:set scope="request" var="pagerHandler" value="Messages"/>
</c:if>