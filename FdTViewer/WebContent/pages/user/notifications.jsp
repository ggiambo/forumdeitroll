<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>

<div id="main">
	<div class="userPanel">
		<div class="userPanelCaption">Pannello Utente - Notifiche</div>
		<div class="userPanelContent">
			<div class="userPanelSection">
				<c:url value="" var="avatarURL">
					<c:param name="action" value="getAvatar"/>
					<c:param name="nick" value="${loggedUser.nick}"/>
				</c:url>
				<img src="Misc<c:out value="${avatarURL}" escapeXml="true" />" alt="Avatar" class="avatar" />
				<h3>Informazioni</h3>
				<span class="lbl">Nome utente:</span> ${loggedUser.nick}<br/>
				<span class="lbl">Messaggi:</span> ${loggedUser.messages}
				<div style="clear: both;"></div>
			</div>
			<div class="userPanelSection">
				<c:choose>
					<c:when test="${empty notificationsFrom}">
						<h3>Non hai nessuna notifica spedita in sospeso</h3>
					</c:when>
					<c:otherwise>
						<h3>Hai spedito queste notifiche</h3>
						<form action="User?action=removeNotification" method="post" id="notificationsFromForm">
							<input type="hidden" name="notificationId" id="notificationIdFrom" />
							<table style="width: 100%;">
								<tbody>
								<tr>
									<th></th>
									<th style="font-weight: bold">All'utente</th>
									<th style="font-weight: bold">Messaggio</th>
								</tr>
									<c:forEach items="${notificationsFrom}" var="item" varStatus="i">
										<tr>
											<td>
												<a href="#" onClick="$('#notificationIdFrom').val('${item.id}');$('#notificationsFromForm').submit();">
													<img src="images/delete.png" alt="Cancella" />
												</a>
											</td>
											<td style="font-weight: normal">${item.toNick}</td>
											<td style="font-weight: normal"><a href="Messages?action=getById&msgId=${item.msgId}">messaggio</a></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</form>
					</c:otherwise>
				</c:choose>
				<div style="clear: both;"></div>
			</div>
			<div class="userPanelSection">
				<c:choose>
					<c:when test="${empty notificationsTo}">
						<h3>Non hai nessuna notifica ricevuta in sospeso</h3>
					</c:when>
					<c:otherwise>
						<h3>Hai ricevuto queste notifiche</h3>
						<form action="User?action=removeNotification" method="post" id="notificationsToForm">
							<input type="hidden" name="notificationId" id="notificationIdTo" />
							<table style="width: 100%;">
								<tbody>
								<tr>
									<th></th>
									<th style="font-weight: bold">Dall'utente</th>
									<th style="font-weight: bold">Messaggio</th>
								</tr>
									<c:forEach items="${notificationsTo}" var="item" varStatus="i">
										<tr>
											<td><!-- puff! --></td>
											<td style="font-weight: normal">${item.fromNick}</td>
											<td style="font-weight: normal"><a href="Messages?action=getById&msgId=${item.msgId}&notificationId=${item.id}&notificationFromNick=${item.fromNick}">messaggio</a></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</form>
					</c:otherwise>
				</c:choose>
				<div style="clear: both;"></div>
			</div>	
			<div class="userPanelSection">
				<h3>Altre Azioni</h3>
				<a href="./User" class="userPanelButton">User Panel</a>
				<a href="./Pvt?action=inbox" class="userPanelButton">Posta</a>
				<a href="User?action=getQuotes" class="userPanelButton">Frasi Celebri</a>
				<a href="./Bookmarks?action=list" class="userPanelButton">Segnalibri</a>
				<div style="clear: both;"></div>
			</div>
		</div>
	</div>
</div>
<div id="footer"></div>
