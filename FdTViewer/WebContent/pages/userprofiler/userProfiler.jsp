<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<jsp:include page="../incTop.jsp"/>

<fdt:delayedScript dump="false">
	$(document).ready(function() {
		$('.ip-container').click(function() {
			geodata(this, $(this).text());
		}).mouseenter(function() {
			$(this).css("color", "#007BDF").css("cursor", "pointer");
		}).mouseleave(function() {
			$(this).css("color", "black").css("cursor", "default");
		});
		
		$('#geoIpContainer').click(function() {
			$(this).html('').hide();
		}).mouseleave(function() {
			$(this).html('').hide();
		});
	});
</fdt:delayedScript>

<div id="geoIpContainer" style="display:none;position:absolute;border:1px solid black; background:#FFFFBF; padding:3px;"></div>

<c:set var="bannato" value="${profile.bannato}"/>

<div id="main">
	<div class="userPanelCaption">Profili utente riconosciuti</div>
	<c:forEach items="${profiles}" var="profile" varStatus="index">
		<div class="userPanel">
			<p>uuid: ${profile.uuid}</p>
			<form action="UserProfiler" method="post">
				<input type="hidden" name="action" value="switchBan">
				<input type="hidden" name="uuid" value="${profile.uuid }">
				<c:choose>
					<c:when test="${bannato}">
						<input type="submit" name="submit" value=" togli ban ">
					</c:when>
					<c:otherwise>
						<input type="submit" name="submit" value=" ban! ">
					</c:otherwise>
				</c:choose>
			</form>
			<c:if test="${bannato}">
				<c:set var="border" value="border: 2px solid red; border-radius: 5px 5px 5px 5px;"/>
			</c:if>
			<div class="userPanelContent" style="${border}">
				<p>nicknames: ${profile.nicknames }</p>
				<p>ip addresses:
				<c:forEach items="${profile.ipAddresses }" var="ip" varStatus="ipIdx">
					<span class="ip-container">${ip}</span>
				</c:forEach>
				</p>
				<p>userAgents: ${profile.userAgents }</p>
				<p>resolutions: ${profile.screenResolutions }</p>
				<p>plugin (hash): ${profile.pluginHashes }</p>
				<p>Ultimi 100 messaggi di questo utente: <c:forEach items="${profile.msgIds }" var="msgId" varStatus="indexMsg">
						<a href="Messages?action=getById&msgId=${msgId }">${msgId }</a>
					</c:forEach></p>
				<p>ultimo riconoscimento: <fmt:formatDate value="${profile.ultimoRiconoscimentoUtenteDate}" pattern="dd.MM.yyyy@HH:mm:ss"/></p>
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
<jsp:include page="../incBottom.jsp"/>