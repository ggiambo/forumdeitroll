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

<a href="#msg${msg.id}"></a>
<div class="messageBox ${rowclass}">
	<div class=msgHeader>
		<div class=msgTitle>
			<a href="Threads?action=getByThread&threadId=${msg.threadId}#msg${msg.id}">${msg.subject}</a>
		</div>
		<div class=msgInfo>
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
	</div>
	<c:choose>
		<c:when test="${index % 2 == 0}">
			<c:set var="rowclass" value="msgEven"/>
		</c:when>
		<c:otherwise>
			<c:set var="rowclass" value="msgOdd"/>
		</c:otherwise>
	</c:choose>
	<div class=msgContent>
		<fdt:msg search="${param.search}" signature="false" author="${msg.author}">${msg.text}</fdt:msg>
		<div class=row>
			<div class=col-1-2>&nbsp;</div>
			<div class=col-2>
				<a href="Messages?action=mobileComposer&amp;replyToId=${msg.id}&type=quote" class=btn>Quota</a>
			</div>
			<div class=col-1>&nbsp;</div>
			<div class=col-2>
				<a href="Messages?action=mobileComposer&amp;replyToId=${msg.id}" class=btn>Rispondi</a>
			</div>
			<div class=col-1-2>&nbsp;</div>
		</div>
		<div class=row>&nbsp;</div>
	</div>
</div>