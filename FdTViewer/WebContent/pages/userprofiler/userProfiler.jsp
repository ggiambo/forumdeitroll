<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<fdt:delayedScript dump="false">
(function(w) {
	var hash = w.location.hash;
	if (hash && hash.match(/^#msgId=\d+$/)) {
		var id = hash.match(/^#msgId=(\d+)$/)[1];
		var url = 'Messages?action=getById&msgId=' + id;
		var selector = 'a[href="' + url + '"]';
		$(selector).focus();
	}
})(window);
</fdt:delayedScript>

<div id="geoIpContainer"></div>

<div id="main">
	<div class="userPanelCaption">Panopticon</div>
	<form action="UserProfiler" method="post">
		<input type="submit" name="action" value="cleanup">
	</form>
	<c:forEach items="${profiles}" var="profile" varStatus="index">
		<div class="userPanel">
			<p>uuid: ${profile.uuid}</p>
			<form action="UserProfiler" method="post">
				<input type="hidden" name="action" value="switchBan">
				<input type="hidden" name="uuid" value="${profile.uuid }">
				<c:choose>
					<c:when test="${profile.bannato}">
						<input type="submit" name="submit" value=" togli ban ">
						<c:set var="border" value="border: 2px solid red; border-radius: 5px 5px 5px 5px;"/>
					</c:when>
					<c:otherwise>
						<input type="submit" name="submit" value=" ban! ">
						<c:set var="border" value=""/>
					</c:otherwise>
				</c:choose>
			</form>
			<form action="UserProfiler" method="post" onsubmit="return confirm('Cancellare il profilo? Sei sicuro? E` irreversibile. Non sei Giambo che pastrugna un ipad, vero?')">
				<input type="hidden" name="action" value="deleteProfile">
				<input type="hidden" name="uuid" value="${profile.uuid }">
				<input type="submit" name="submit" value=" cancella ">
			</form>
			<div class="userPanelContent" style="${border}">
				<p>nicknames: ${profile.nicknames }</p>
				<p>ip addresses:
				<c:forEach items="${profile.ipAddresses }" var="ip" varStatus="ipIdx">
					<span class="ip-container">${ip}</span>
				</c:forEach>
				</p>
				<p>permr: ${profile.permr }</p>
				<p>etag: ${profile.etag }</p>
				<p>userAgents: ${profile.userAgents }</p>
				<p>resolutions: ${profile.screenResolutions }</p>
				<p>plugin (hash): ${profile.pluginHashes }</p>
				<p>Ultimi 100 messaggi di questo utente: <c:forEach items="${profile.msgIds }" var="msgId" varStatus="indexMsg">
						<a href="Messages?action=getById&msgId=${msgId }" onfocus="blink(this)">${msgId }</a>
					</c:forEach></p>
				<p>ultimo riconoscimento: <fmt:formatDate value="${profile.ultimoRiconoscimentoUtenteDate}" pattern="dd.MM.yyyy@HH:mm:ss"/></p>
				<p>Cancella un attributo:
				<form action="UserProfiler" method="post" onsubmit="return confirm('Ci vuoi ripensare? Lo cancelliamo?')">
					<input type="hidden" name="action" value="deleteAttribute">
					<input type="hidden" name="uuid" value="${profile.uuid }">
					<select name="attributeName">
						<option value="ipAddress">ipAddress</option>
						<option value="nickname">nickname</option>
						<option value="userAgent">userAgent</option>
						<option value="screenRes">screenRes</option>
						<option value="pluginHash">pluginHash</option>
						<option value="msgId">msgId</option>
						<option value="permr+etag">permr+etag</option>
					</select>
					<input type="text" name="attributeValue" value="">
					<input type="submit" name="submit" value=" cancella ">
				</form>
			</div>
		</div>
		<br>
	</c:forEach>
	<form action="UserProfiler" method="POST">
		<input type="hidden" name="action" value="merge">
		<input type="text" name="one">
		<input type="text" name="two">
		<input type="submit" name="submit" value=" unisci " onclick="return confirm('Unire i dati dei due profili(ua, ip, posts,..) in uno unico?\nNon sarà più possibile tornare indietro.')">
	</form>
</div>
<div id="footer"></div>
