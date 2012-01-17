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
			<img alt="Avatar" src="?action=getAvatar&amp;nick=${msg.escapedAuthor}"/>
		</div>
		<c:if test="${!empty msg.forum}">
			<div class="msgForum">${msg.forum}</div>
		</c:if>
		<div class="msgDetails"> 
			<div class="msgWrittenby">Scritto da</div>
			<div class="msgAuthor">
				<c:choose>
					<c:when test="${empty msg.author}">
						Non Autenticato
					</c:when>
					<c:otherwise>
						<a href="Messages?action=getByAuthor&amp;author=${msg.escapedAuthor}">${msg.author}</a>
					</c:otherwise>
				</c:choose>
			</div>
			<div class="msgDate">il <fmt:formatDate value="${msg.date}" pattern="dd.MM.yyyy"/> alle <fmt:formatDate value="${msg.date}" pattern="HH:mm"/></div>
		</div>
		<c:if test="${not empty loggedUser && msg.author == loggedUser}">
			<div class="msgAction">
				<a href="Messages?action=editMessage&amp;msgId=${msg.id}&amp;forum=${msg.forum}">Modifica</a>
			</div>
		</c:if>	
	</div>

	<span style="width:100%; margin:5px;">
		<b><a href="Threads?action=getByThread&amp;threadId=${msg.threadId}#msg${msg.id}">${msg.subject}</a></b>
	</span>

	<div style="padding: 10px;" class="message">
		<fdt:msg search="${param.search}">${msg.text}</fdt:msg>
		<%-- close open tags --%>
		<c:out escapeXml="false" value="</b></i></u>"/> <%-- TODO: DA RIMUOVERE AL PIÙ PRESTO, ROMPE LA VALIDAZIONE ndachille: non ci muore nessuno --%>
	</div>
	
	<div id="buttons_${msg.id}" class="messagesButtonBar">
		<a href="#" onClick="showReplyDiv('reply', '${msg.id}');return false;"><img alt="Rispondi" style="vertical-align: middle;" src="images/rispondi.gif" /></a>
		<a href="#" onClick="showReplyDiv('quote', '${msg.id}');return false;"><img alt="Quota" style="vertical-align: middle;" src="images/quota.gif" /></a>
	</div>
</div>