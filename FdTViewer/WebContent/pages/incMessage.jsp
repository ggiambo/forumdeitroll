<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<c:choose>
	<c:when test="${index % 2 == 0}">
		<c:set var="rowclass" value="msgEven"/>
	</c:when>
	<c:otherwise>
		<c:set var="rowclass" value="msgOdd"/>
	</c:otherwise>
</c:choose>

<a href="#msg${msg.id}"></a>

<c:choose>
	<c:when test="${msg.visible}">
		<c:set var="msgStyle" value="display:block"/>
	</c:when>
	<c:otherwise>
		<c:set var="msgStyle" value="display:none"/>
		<div id="msgWarning${msg.id}" style="padding:5px;">
			<img src="images/warning.png" style="float:right; margin-right:10px"/>
			Questo messaggio e' stato catalogato come "Exiled Nigerian princess".<br/>
			Clicka <a href="#" onClick="showHIddenMessage(${msg.id});return false;">qui</a> per vederlo, e che Dio onnipotente possa aver piet&agrave; della tua anima.
		</div>
	</c:otherwise>
</c:choose>

<div class="${rowclass}" id="msg${msg.id}" style="${msgStyle}">

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

	<c:if test="${msg.searchRelevance >= 0}">
		<div class="searchInfo">
			<tt><!-- Ciao wakko :-) -->Rilevanza: <fmt:formatNumber value="${msg.searchRelevance}" pattern="#0.00" />. Messaggi nel thread: ${msg.searchCount - 1}</tt>
		</div>
	</c:if>

	<div style="padding: 10px;" class="message">
		<fdt:msg search="${param.search}" author="${msg.author}">${msg.text}</fdt:msg>
	</div>

	<div id="buttons_${msg.id}" class="messagesButtonBar">
		<c:if test="${not empty loggedUser && loggedUser.preferences['pedonizeThread'] == 'yes'}">
			<c:if test="${msg.forum != 'Proc di Catania'}">
				<a href="#" onClick="pedonizeThreadTree('${msg.id}');return false;"><img alt="Pedonize!" style="vertical-align: middle;" src="images/pedonize.png" /></a>
			</c:if>
		</c:if>
		<c:if test="${not empty loggedUser && loggedUser.preferences['hideMessages'] == 'yes'}">
			<c:choose>
				<c:when test="${msg.visible}">
					<a href="#" onClick="hideMessage('${msg.id}');return false;"><img alt="Nascondi messaggio" style="vertical-align: middle;" src="images/hideMessage.png" /></a>
				</c:when>
				<c:otherwise>
					<a href="#" onClick="restoreHiddenMessage('${msg.id}');return false;"><img alt="Rendi messaggio visibile" style="vertical-align: middle;" src="images/restoreHiddenMessage.png" /></a>
				</c:otherwise>
			</c:choose>
		</c:if>
		<a href="#" onClick="showReplyDiv('reply', '${msg.id}');return false;"><img alt="Rispondi" style="vertical-align: middle;" src="images/rispondi.gif" /></a>
		<a href="#" onClick="showReplyDiv('quote', '${msg.id}');return false;"><img alt="Quota" style="vertical-align: middle;" src="images/quota.gif" /></a>
	</div>
</div>