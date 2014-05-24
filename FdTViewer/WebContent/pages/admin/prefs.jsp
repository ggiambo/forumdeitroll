<%@page import="com.forumdeitroll.servlets.Admin"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div id="main">
	<div class="userPanel">
		<div class="userPanelCaption">Pannello Utente</div>
		<div class="userPanelContent">
			<div class="userPanelSection">
				<h3>Preferenze Admin</h3>
				<c:if test="${loggedUser.preferences['super'] eq 'yes'}">
					<form action="Admin?action=updatePreferences" method="post">
						<hr/>
						<div class="inputUserPanel">
							<input type="checkbox" name="<%=Admin.ADMIN_PREF_BLOCK_TOR%>" id="<%=Admin.ADMIN_PREF_BLOCK_TOR%>" ${blockTorExitNodes} />
						</div>
						<div class="lblUserPanel">
							<label for="<%=Admin.ADMIN_PREF_BLOCK_TOR%>" class="lbl">Blocca TOR exit nodes:</label>
						</div>
						<div style="clear: both;"></div>
						<div class="inputUserPanel">
							<input type="checkbox" name="<%=Admin.ADMIN_PREF_DISABLE_PROFILER%>" id="<%=Admin.ADMIN_PREF_DISABLE_PROFILER%>" ${disableUserProfiler} />
						</div>
						<div class="lblUserPanel">
							<label for="<%=Admin.ADMIN_PREF_DISABLE_PROFILER%>" class="lbl">Disattiva il profiler:</label>
						</div>
						<div style="clear: both;"></div>
						<div class="lblUserPanel">
							<label for="javascript" class="lbl">Javascript:</label>
						</div>
						<div class="inputUserPanel">
							<textarea name="javascript" id="javascript" maxlength="255">${javascript}</textarea>
						</div>
						<div style="clear: both;"></div>
						<div class="lblUserPanel">
							<label for="websiteTitle" class="lbl">Titoli:</label>
						</div>
						<div class="inputUserPanel" id="websiteTitles">
							<c:choose>
								<c:when test="${empty websiteTitles}">
								</c:when>
								<c:otherwise>
									<c:forEach items="${websiteTitles}" var="websiteTitle">
										<input name="<%=Admin.ADMIN_WEBSITE_TITLES%>" value="${websiteTitle}" maxlength="32" /><br/>
									</c:forEach>
								</c:otherwise>
							</c:choose>
							<img src="images/add.png" id="addWebsiteTitle" alt="Aggiungi titolo"/>
						</div>
						<div style="clear: both;"></div>
						<div class="lblUserPanel">
							<label for="websiteTitle" class="lbl">Fake Ads:</label>
						</div>
						<div class="inputUserPanel" id="fakeAds">
							<c:if test="${not empty fakeAds}">
								<c:forEach items="${fakeAds}" var="fakeAd">
									<div id="fakeAd_${fakeAd.id}">
										<input name="fakeAds[${fakeAd.id}].title" value="${fakeAd.title}" maxlength="32" />
										<input name="fakeAds[${fakeAd.id}].visurl" value="${fakeAd.visurl}" maxlength="32" />
										<input name="fakeAds[${fakeAd.id}].content" value="${fakeAd.content}" maxlength="32" />
										<img src="images/delete.png" onClick='$(this).parent("div").remove();'/>
									</div>
								</c:forEach>
							</c:if>
							<img src="images/add.png" id="addFakeAd" alt="Aggiungi fake Ads"/>
						</div>
						
						<input type="submit" value="Modifica" class="sendUserPanel" />
					</form>
				</c:if>
				<div style="clear: both;"></div>
			</div>
			<div class="userPanelSection">
				<h3>Altre Azioni</h3>
				<a href="./User" class="userPanelButton">User Panel</a>
				<a href="./User?action=getQuotes" class="userPanelButton">Frasi Celebri</a>
				<a href="./Pvt?action=inbox" class="userPanelButton">Posta</a>
				<a href="./User?action=getNotifications" class="userPanelButton">Notifiche</a>
				<a href="./Bookmarks?action=list" class="userPanelButton">Segnalibri</a>
				<div style="clear: both;"></div>
			</div>
		</div>
	</div>
</div>
<div id="footer"></div>