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
							<c:url var="banMessageUrl" value="">
								<c:param name="action" value="banMessage"/>
								<c:param name="m_id" value="${modInfo.m_id}"/>
								<c:param name="token" value="${anti_xss_token}"/>
							</c:url>
							<c:url var="banUserUrl" value="">
								<c:param name="action" value="banUser"/>
								<c:param name="m_id" value="${modInfo.m_id}"/>
								<c:param name="token" value="${anti_xss_token}"/>
							</c:url>
							<c:url var="banIPUrl" value="">
								<c:param name="action" value="banIP"/>
								<c:param name="m_id" value="${modInfo.m_id}"/>
								<c:param name="token" value="${anti_xss_token}"/>
							</c:url>
							<a class="modInfoPanelButton" href="${banMessageUrl}" title="Cancella Messaggio">Cancella</a>
							<a class="modInfoPanelButton" href="${banUserUrl}">Ban Utente</a>
							<a class="modInfoPanelButton" href="${banIPUrl}">Ban IP</a>
							<div style="clear: both;"></div>
						</p>
					</div>
				</div>
			</div>
		</div>
		<div id="footer"></div>
<jsp:include page="../incBottom.jsp"/>