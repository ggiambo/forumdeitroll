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
<div class="messageBox ${rowclass}">
	<div class=row>
		<div class=col-3>
			<a href="Threads?action=getByThread&threadId=${msg.threadId}#msg${msg.id}">${msg.subject}</a>
		</div>
		<div class="col-2 msgInfo">
			Scritto da
			<c:choose>
				<c:when test="${not empty msg.author.nick}">
					<a href="Messages?action=getByAuthor&amp;author=${msg.author.nick}">${msg.author.nick}</a>
				</c:when>
				<c:otherwise>
					non autenticato
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${not empty msg.author.nick}">
					<img src="Misc?action=getAvatar&amp;&nick=${msg.author.nick}" class=avatar>
				</c:when>
				<c:otherwise></c:otherwise>
			</c:choose>
			<br>
			il <fmt:formatDate value="${msg.date}" pattern="dd.MM.yyyy"/> alle <fmt:formatDate value="${msg.date}" pattern="HH:mm"/>
		</div>
		<div class=col-1>
			<div class=row>
				<div class=col-1-2>&nbsp;</div>
				<div class=col-5><a href="#" onclick="toggleMessageView(this,event); return false" class="btn btn-flat">&#x25bc;</a></div>
				<div class=col-1-2>&nbsp;</div>
			</div>
		</div>
	</div>
	<div class=row>&nbsp;</div>
	<c:choose>
		<c:when test="${index % 2 == 0}">
			<c:set var="rowclass" value="msgEven"/>
		</c:when>
		<c:otherwise>
			<c:set var="rowclass" value="msgOdd"/>
		</c:otherwise>
	</c:choose>
	<div class="row msgContent">
		<c:set var="message" value="${msg}"/>
		<fdt:render target="message"/>
		<div class=row>
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
	</div>
</div>