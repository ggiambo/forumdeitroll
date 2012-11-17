<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@page import="com.forumdeitroll.servlets.Messages"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div id="main">
	<div class="userPanel">
		<div class="userPanelCaption">Pannello Utente - Segnalibri</div>
		<c:if test="${not empty msgId}">
			<div class="userPanelCaption">Segnalibro da aggiungere</div>
			<form action="Bookmarks" method="POST" enctype="application/x-www-form-urlencoded">
				<input type="hidden" name="action" value="confirmAdd">
				<input type="hidden" name="msgId" value="${msgId}">
				<input type="text" name="subject" value="${subject}" style="width:85%" maxlength="<%=Messages.MAX_SUBJECT_LENGTH %>" size="<%=Messages.MAX_SUBJECT_LENGTH %>">
				<input type="submit" name="btnSubmit" value="Conferma">
			</form>
		</c:if>
		<div class="userPanelContent">
			<c:if test="${empty bookmarks}">
				<div class="userPanelSection">
					Non hai ancora aggiunto nessun segnalibro.<img src="images/emoextended/piange.gif" alt="" class="emoticon" />	
				</div>
			</c:if>
			<c:if test="${not empty bookmarks}">
				<div class="userPanelSection">
					<table style="width:100%">
						<tbody>
							<c:forEach items="${bookmarks}" var="bookmark">
								<tr>
									<c:if test="${highlight == bookmark.msgId}">
										<td style="background-color: yellow;">
											<a id="link_bm_${bookmark.msgId}" href="Threads?action=getByMessage&msgId=${bookmark.msgId}#msg${bookmark.msgId}"
											><c:out value="${bookmark.subject}"/></a>
											<%-- il tag qui sopra è formattato cosi' per evitare di trimmare via js il testo --%>
										</td>
									</c:if>
									<c:if test="${highlight != bookmark.msgId}">
										<td>
											<a id="link_bm_${bookmark.msgId}" href="Threads?action=getByMessage&msgId=${bookmark.msgId}#msg${bookmark.msgId}"
											><c:out value="${bookmark.subject}"/></a>
											<%-- il tag qui sopra è formattato cosi' per evitare di trimmare via js il testo --%>
										</td>
									</c:if>
									<td width="1%">
										<img src="images/edit.png" alt="Modifica" style="cursor:pointer;" onclick="edit_bm(${bookmark.msgId},<%=Messages.MAX_SUBJECT_LENGTH %>)">
									</td>
									<td width="1%">
										<form action="Bookmarks" method="POST" enctype="application/x-www-form-urlencoded">
											<input type="hidden" name="action" value="delete">
											<input type="hidden" name="msgId" value="${bookmark.msgId}">
											<img src="images/delete.png" alt="Cancella" style="cursor: pointer;" onclick="if(confirm('Cancellare questo segnalibro?'))this.parentNode.submit()"/>
										</form>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</c:if>
			<div class="userPanelSection">
				<h3>Altre Azioni</h3>
				<a href="./User" class="userPanelButton">User Panel</a>
				<a href="./User?action=getQuotes" class="userPanelButton">Frasi Celebri</a>
				<a href="./Pvt?action=inbox" class="userPanelButton">Posta</a>
				<a href="./User?action=getNotifications" class="userPanelButton">Notifiche</a>
				<div style="clear: both;"></div>
			</div>
		</div>
	</div>
</div>