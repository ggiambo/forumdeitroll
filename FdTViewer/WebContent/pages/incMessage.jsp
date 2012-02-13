<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<c:choose>
	<c:when test="${index.count % 2 == 0}">
		<c:set var="class" value="msgEven"/>
	</c:when>
	<c:otherwise>
		<c:set var="class" value="msgOdd"/>
	</c:otherwise>
</c:choose>

<a href="#msg${msg.id}"></a>

<div class="${class}" id="msg${msg.id}">

	<div class="msgInfo">
		<div>
			<c:url value="" var="avatarURL">
				<c:param name="action" value="getAvatar"/>
				<c:param name="nick" value="${msg.author.nick}"/>
			</c:url>
			<c:choose>
				<c:when test="${!empty msg.author.nick}">
					<c:url value="User" var="userInfoUrl">
						<c:param name="action" value="getUserInfo"/>
						<c:param name="nick" value="${msg.author.nick}"/>
					</c:url>
					<a href="<c:out value="${userInfoUrl}" escapeXml="true"/>">
						<img class="avatarImgLinkable" alt="Avatar" src="Misc<c:out value="${avatarURL}" escapeXml="true" />" />
					</a>
				</c:when>
				<c:otherwise>
					<img class="avatarImg" alt="Avatar" src="Misc<c:out value="${avatarURL}" escapeXml="true" />" />
				</c:otherwise>
			</c:choose>
		</div>
		<c:if test="${!empty msg.forum}">
			<div class="msgForum">${msg.forum}</div>
		</c:if>
		<div class="msgDetails"> 
			<div class="msgWrittenby">Scritto da</div>
			<div class="msgAuthor">
				<c:choose>
					<c:when test="${empty msg.author.nick}">
						Non Autenticato
					</c:when>
					<c:otherwise>
						<c:url value="Messages" var="messagesUrl">
							<c:param name="action" value="getByAuthor"/>
							<c:param name="author" value="${msg.author.nick}"/>
						</c:url>
						<a href="<c:out value="${messagesUrl}" escapeXml="true" />">${msg.author.nick}</a>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="msgDate">il <fmt:formatDate value="${msg.date}" pattern="dd.MM.yyyy"/> alle <fmt:formatDate value="${msg.date}" pattern="HH:mm"/></div>
			<c:if test="${not empty msg.author.nick}">
				<div class="msgTotalMsg">
					Trollate totali: ${msg.author.messages}
		</div>
			</c:if>
		</div>
		<c:if test="${not empty loggedUser && msg.author.nick == loggedUser.nick}">
			<div class="msgAction">
				<a href="Messages?action=editMessage&amp;msgId=${msg.id}&amp;forum=${msg.forum}">Modifica</a>
			</div>
		</c:if>	
	</div>

	<span style="width:100%; margin:5px;">
		<b>
			<fdt:threadprettyurl subject="${msg.subject}" threadId="${msg.threadId}" msgId="${msg.id}"/>
		</b>
	</span>

	<div style="padding: 10px;" class="message">
		<fdt:msg search="${param.search}" author="${msg.author}">${msg.text}</fdt:msg>
	</div>
	
	<div id="buttons_${msg.id}" class="messagesButtonBar">
		<a href="#" onClick="showReplyDiv('reply', '${msg.id}');return false;"><img alt="Rispondi" style="vertical-align: middle;" src="images/rispondi.gif" /></a>
		<a href="#" onClick="showReplyDiv('quote', '${msg.id}');return false;"><img alt="Quota" style="vertical-align: middle;" src="images/quota.gif" /></a>
	</div>
</div>