<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="geoIpContainer"></div>

<div id="main">
	<div class="modInfoPanel">
		<h2>Moderazione</h2>
		<div class="modInfoPanelContent">
			<div class="modInfoPanelSection">
				<h3>Informazioni</h3>
				<p><span class="lbl">Messaggio:</span> ${modInfo.m_id}</p>
				<p><span class="lbl">Utente:</span> ${modInfo.authorDescription}</p>
				<p><span class="lbl">IP:</span> <span class="ip-container">${modInfo.ip}</span></p>
				<p><span class="lbl">TOR:</span> ${modInfo.tor}</p>
				<p><span class="lbl"><a href="UserProfiler#message-post-${modInfo.m_id}">Vai alle informazioni del profiler per questo messaggio</a></span></p>
			</div>
			<div class="modInfoPanelSection">
				<h3>Azioni</h3>
				<c:if test="${not empty comm}">
					<p>${comm}</p>
				</c:if>
				<a class="modInfoPanelButton" href="#" onClick="banMessage('${modInfo.m_id}', '${anti_xss_token}');return false;" title="Cancella Messaggio">Cancella</a>
				<a class="modInfoPanelButton" href="#" onClick="banUser('${modInfo.m_id}', '${anti_xss_token}');return false;" title="Ban Utente">Ban Utente</a>
				<a class="modInfoPanelButton" href="#" onClick="banIP('${modInfo.m_id}', '${anti_xss_token}');return false;" title="Ban IP">Ban IP</a>
				<div style="clear: both;"></div>
			</div>
		</div>
		<h2>Messaggio</h2>
		<div class="messagesBox msgEven">
			<c:set var="index" value="0" scope="request"/>
			<jsp:include page="../incMessage.jsp"/>
		</div>
	</div>
</div>
<div id="footer"></div>
