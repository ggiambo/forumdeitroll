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
		<c:if test="${loggedUser.wantsToHideThread(thread)}">
			<div class="${rowclass} threadBox row" id="threadWarning${thread.id}" style="padding:5px;">
				<img src="images/poop.png" style="float:right; margin-right:10px"/>
				Questa discussione contiene roba che non ti interessa.
				Clicka <a href="#" onClick="showHiddenThread(${thread.id});return false;">qui</a> per vederlo.
			</div>
			<c:set var="rowclass" value="${rowclass} threadInvisible"/>
		</c:if>
		<div class="${rowclass} threadBox row" id="thread${thread.id}">
			<div class=col-1 style=text-align:center>
				<c:choose>
					<c:when test="${empty thread.author.nick}">
						<img src=Misc?action=getAvatar class=avatarImgThreadlist>
					</c:when>
					<c:otherwise>
						<c:url value="Misc" var="avatarURL">
							<c:param name="action" value="getAvatar"/>
							<c:param name="nick" value="${thread.author.nick}"/>
						</c:url>
						<img src="${avatarURL}" class=avatarImgThreadlist>
					</c:otherwise>
				</c:choose>
			</div>
			<div class=col-11>
				<div class=row>
					<div class=col-12>
						<span  style="float:right; background-color: green; width: ${thread.numberOfMessages}px; height: 5px"></span>
					</div>
				</div>
				<div class=row>
					<div class=col-9>
						<span class=threadTitle>
							<c:choose>
								<c:when test="${param['action'] == 'getThreadsByLastPost' || param['action'] == 'getAuthorThreadsByLastPost'}">
									<a href="Threads?action=getByThread&threadId=${thread.id}#msg${thread.lastId}">${thread.subject}</a>
								</c:when>
								<c:when test="${param['action'] == 'getThreads' || param['action'] == 'getThreadsByLastPostGroupByUser'}">
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
								<span class=hide-me> messaggi</span>
							</c:when>
							<c:otherwise>
								<span class=hide-me> messaggio</span>
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
								<span class=hide-me>Ultimo messaggio di</span>
							</c:when>
							<c:when test="${param['action'] == 'getThreads'}">
								<span class=hide-me>Iniziato da</span>
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
