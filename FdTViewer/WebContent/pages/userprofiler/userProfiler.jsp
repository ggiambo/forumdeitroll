<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../incTop.jsp"/>
		<div id="main">
			<div class="userPanelCaption">Pannello Utente - Messaggi Privati</div>
			<c:forEach items="${profiles}" var="profile" varStatus="index">
				<div class="userPanel">
					<p>uuid: ${profile.uuid}</p>
					<div class="userPanelContent">
						<p>nicknames: ${profile.nicknames }</p>
						<p>ip addresses: ${profile.ipAddresses }</p>
						<p>userAgents: ${profile.userAgents }</p>
						<p>resolutions: ${profile.screenResolutions }</p>
						<p>plugin (hash): ${profile.pluginHashes }</p>
						<p>ultimo accesso: <script type='text/javascript'>document.write(new Date(${profile.ultimoRiconoscimentoUtente }).toString())</script></p>
					</div>
				</div>
			</c:forEach>
		</div>
		<div id="footer"></div>
<jsp:include page="../incBottom.jsp"/>