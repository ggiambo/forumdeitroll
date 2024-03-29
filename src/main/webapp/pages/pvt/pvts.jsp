<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div id="main">
	<div class="userPanel">
		<div class="userPanelCaption">Pannello Utente - Messaggi Privati</div>
		<div class="userPanelContent">
			<a href="Pvt?action=inbox">Ricevuti</a>
			|
			<a href="Pvt?action=outbox">Inviati</a>
			|
			<a href="Pvt?action=sendNew">Scrivi nuovo</a>
		</div>
		<div class="userPanelContent">
			<c:if test="${from == 'inbox' || from == 'outbox'}">
				<jsp:include page="incPvtInOutbox.jsp"/>
			</c:if>
			<c:if test="${from == 'sendNew' }">
				<jsp:include page="incPvtSendNew.jsp"/>
			</c:if>

			<c:if test="${from == 'show' }">
				<jsp:include page="incPvtShow.jsp"/>
			</c:if>
			<div class="userPanelSection">
				<h3>Altre Azioni</h3>
				<a href="./User" class="userPanelButton">User Panel</a>
				<a href="./User?action=getQuotes" class="userPanelButton">Frasi Celebri</a>
				<a href="./User?action=getNotifications" class="userPanelButton">Notifiche</a>
				<a href="./Bookmarks?action=list" class="userPanelButton">Segnalibri</a>
				<c:if test="${loggedUser.preferences['super'] eq 'yes'}">
					<a href="./Admin" class="userPanelButton" style="border-color: #FF910A;">Admin</a>
				</c:if>
				<div style="clear: both;"></div>
			</div>
		</div> <%-- /Content --%>
	</div> <%-- /Panel --%>
</div> <%-- /Main --%>

<div id="footer">
	<c:if test="${!empty page}">
		<fdt:pager handler="Messages"></fdt:pager>
	</c:if>
</div>
