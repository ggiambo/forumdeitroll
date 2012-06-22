<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../incTop.jsp"/>
		<div id="main">
			<div class="modInfoPanel">
				<h2>Moderazione</h2>
				<div class="modInfoPanelContent">
					<div class="modInfoPanelSection">
						<h3>Informazioni</h3>
						<p><span class="lbl">Messaggio:</span> ${modInfo.m_id}</p>
						<p><span class="lbl">Utente:</span> ${modInfo.authorDescription}</p>
						<p><span class="lbl">IP:</span> ${modInfo.ip}</p>
						<p><span class="lbl">TOR:</span> ${modInfo.tor}</p>
					</div>
					<div class="modInfoPanelSection">
						<h3>Azioni</h3>
						<p>
							<c:if test="${not empty comm}">
								<p>${comm}</p>
							</c:if>
							<a class="modInfoPanelButton" href="#" onClick="banMessage('${modInfo.m_id}', '${anti_xss_token}');return false;" title="Cancella Messaggio">Cancella</a>
							<a class="modInfoPanelButton" href="#" onClick="banUser('${modInfo.m_id}', '${anti_xss_token}');return false;" title="Ban Utente">Ban Utente</a>
							<a class="modInfoPanelButton" href="#" onClick="banIP('${modInfo.m_id}', '${anti_xss_token}');return false;" title="Ban IP">Ban IP</a>
							<div style="clear: both;"></div>
						</p>
					</div>
				</div>
			</div>
		</div>
		<div id="footer"></div>
<jsp:include page="../incBottom.jsp"/>