<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="../incTop.jsp"/>
		<div id="main">
			<div class="userPanelCaption">Profili utente riconosciuti</div>
			<c:forEach items="${profiles}" var="profile" varStatus="index">
				<div class="userPanel">
					<p>uuid: ${profile.uuid}</p>
					<form action="UserProfiler" method="post">
						<input type="hidden" name="action" value="switchBan">
						<input type="hidden" name="uuid" value="${profile.uuid }">
						<c:if test="${profile.bannato }">
							<input type="submit" name="submit" value=" togli ban ">
						</c:if>
						<c:if test="${!profile.bannato }">
							<input type="submit" name="submit" value=" ban! ">
						</c:if>
					</form>
					<c:if test="${profile.bannato }">
						<div class="userPanelContent" style="border: 1px solid red">
					</c:if>
					<c:if test="${!profile.bannato }">
						<div class="userPanelContent">
					</c:if>
						<p>nicknames: ${profile.nicknames }</p>
						<p>ip addresses: ${profile.ipAddresses }</p>
						<p>userAgents: ${profile.userAgents }</p>
						<p>resolutions: ${profile.screenResolutions }</p>
						<p>plugin (hash): ${profile.pluginHashes }</p>
						<p>Ultimi 100 messaggi di questo utente: <c:forEach items="${profile.msgIds }" var="msgId" varStatus="indexMsg">
								<a href="Messages?action=getById&msgId=${msgId }">${msgId }</a>
							</c:forEach></p>
						<p>ultimo riconoscimento: <script type='text/javascript'>document.write(new Date(${profile.ultimoRiconoscimentoUtente }).toString())</script></p>
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