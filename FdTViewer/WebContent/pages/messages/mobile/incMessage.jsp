<%@page import="com.forumdeitroll.servlets.User"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<c:choose>
	<c:when test="${msg.visible}">
		<c:set var="rowclass" value="msgVisible"/>
	</c:when>
	<c:otherwise>
		<c:set var="rowclass" value="msgInvisible"/>
		<div id="msgWarning${msg.id}" style="padding:5px;">
			<img src="images/warning.png" style="float:right; margin-right:10px"/>
			Questo messaggio e' stato catalogato come "Exiled Nigerian princess".
		</div>
	</c:otherwise>
</c:choose>

<span class=msg-anchor id="msg${msg.id}"></span>
<div class="messageBox">
	<div class=row onclick=toggleMessageView(this,event,${msg.id}) style='height: 48px' id="msg-toggle-${msg.id}">
		<div class=col-1>
			<img src="Misc?action=getAvatar&amp;&nick=${msg.author.nick}" class=avatar>
		</div>
		<c:choose>
			<c:when test="${param['action'] == 'getByThread'}">
				<div class=col-5>
					${msg.subject}
					<span class=arrow>&#x25bc;</span>
				</div>
			</c:when>
			<c:otherwise>
				<div class=col-5>
					${msg.subject}
					<span class=arrow>&#x25bc;</span>
				</div>
			</c:otherwise>
		</c:choose>
		<span class=msgInfo>
			di
			<c:choose>
				<c:when test="${not empty msg.author.nick}">
					${msg.author.nick}
				</c:when>
				<c:otherwise>
					non autenticato
				</c:otherwise>
			</c:choose>
			<br>
			<i><fdt:prettyDate date="${msg.date}"/></i>
		</span>
	</div>
	<div class="msgContent">
		<div class=transparentOverlay onclick=toggleMessageView(this,event,${msg.id})></div>
		<div class="msgText">
			<c:set var="message" value="${msg}"/>
			<fdt:render target="message"/>
		</div>
		<div class=msgButtons>
			<div class="row">
				<div class=col-1-2>&nbsp;</div>
				<div class=col-2>
					<a href="Messages?action=mobileComposer&amp;replyToId=${msg.id}&type=quote" class="btn btn-flat">Quota</a>
				</div>
				<div class=col-1>&nbsp;</div>
				<div class=col-2>
					<c:if test="${msg.author.nick == loggedUser.nick}">
						<a href="Messages?action=mobileComposer&amp;replyToId=${msg.parentId}&amp;messageId=${msg.id}" class="btn btn-flat">Modifica</a>
					</c:if>
					<c:if test="${msg.author.nick != loggedUser.nick}">
						<a href="Messages?action=mobileComposer&amp;replyToId=${msg.id}" class="btn btn-flat">Rispondi</a>
					</c:if>
				</div>
				<div class=col-1-2>&nbsp;</div>
			</div>
			<div class=row>&nbsp;</div>
			<div class="row">
				<div class=col-1-2>&nbsp;</div>
				<div class=col-2>
					<c:if test="${not empty msg.author.nick}">
						<a href="Messages?action=getByAuthor&author=${msg.author.nick}" class="btn btn-flat">
							di ${msg.author.nick}
						</a>
					</c:if>
					<c:if test="${empty msg.author.nick}">
						&nbsp;
					</c:if>
				</div>
				<div class=col-1>&nbsp;</div>
				<div class=col-2>
					<a href="Threads?action=getByThread&threadId=${msg.threadId}#msg${msg.id}" class="btn btn-flat">
						Leggi Thread
					</a>
				</div>
				<div class=col-1-2>&nbsp;</div>
			</div>
			<div class=row>&nbsp;</div>
		</div>
	</div>
</div>