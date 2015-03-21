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
		<div class="${rowclass} row">
			<div class=col-1>
				<c:choose>
					<c:when test="${empty thread.author.nick}">
						<img src=Misc?action=getAvatar style=max-width:50px>
					</c:when>
					<c:otherwise>
						<c:url value="Misc" var="avatarURL">
							<c:param name="action" value="getAvatar"/>
							<c:param name="nick" value="${thread.author.nick}"/>
						</c:url>
						<img src="${avatarURL}" style=max-width:50px>
					</c:otherwise>
				</c:choose>
			</div>
			<div class=col-11>
				<div class=row>
					<div class=col-9>
						<span class=threadTitle>
							<c:choose>
								<c:when test="${not empty loggedUser && loggedUser.preferences['softv'] == 'checked'}">
									<a href="Threads?action=softvThread&threadId=${thread.id}">${thread.subject}</a>
								</c:when>
								<c:when test="${param['action'] == 'getThreadsByLastPost' || param['action'] == 'getAuthorThreadsByLastPost'}">
									<a href="Threads?action=getByThread&threadId=${thread.id}#msg${thread.lastId}">${thread.subject}</a>
								</c:when>
								<c:when test="${param['action'] == 'getThreads'}">
									<a href="Threads?action=getByThread&threadId=${thread.id}">${thread.subject}</a>
								</c:when>
								<c:otherwise></c:otherwise>
							</c:choose>
						</span>
						<c:if test="${!empty thread.forum}">
							<span class="tagForum">${thread.forum}</span>
						</c:if>
					</div>
					<div class="col-3 pull-right">
						${thread.numberOfMessages}
						<c:choose>
							<c:when test="${thread.numberOfMessages != 1}">
								messaggi
							</c:when>
							<c:otherwise>
								messaggio
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				<div class=row>
					<div class=col-9>
						<c:if test="${thread.numberOfMessages > 1}">
							<a id="plus_${thread.id}" href="javascript:openThreadTree('${thread.id}');"><img src="./images/plus_sign.gif" alt="Espandi Thread"></a>
							<a style="display:none" id="minus_${thread.id}" href="javascript:closeThreadTree('${thread.id}');"><img src="./images/minus_sign.gif" alt="Chiudi Thread"></a>
						</c:if>
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
								Non Autenticato
							</c:when>
							<c:otherwise>
								<c:url value="Messages" var="authorURL">
									<c:param name="action" value="getByAuthor"/>
									<c:param name="author" value="${thread.author.nick}"/>
								</c:url>
								<a href="<c:out value="${authorURL}" escapeXml="true" />">
									${thread.author.nick}
								</a>
							</c:otherwise>
						</c:choose>
					</div>
					<div class="col-3 pull-right">
						<fdt:lessPrettyDate date="${thread.date}"/>
					</div>
				</div>
				<div class="row sep"></div>
				<div class=row id="threadTree_${thread.id}">
					<div class="threadTreeEntries"></div>
					<div class="row sep"></div>
				</div>
			</div>
			<div class="row sep"></div>
		</div>
	</c:forEach>
</div>

<div id="footer">
	<c:if test="${!empty page}">
		<fdt:pager handler="Messages"></fdt:pager>
	</c:if>
</div>
