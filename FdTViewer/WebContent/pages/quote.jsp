<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<jsp:include page="incTop.jsp" />
		<div id="main">
			<div class="userPanel">
				<div class="userPanelCaption">Pannello Utente - Frasi Celebri</div>
				<div class="userPanelContent">
					<div class="userPanelSection">
						<c:url value="" var="avatarURL">
							<c:param name="action" value="getAvatar"/>
							<c:param name="nick" value="${loggedUser.nick}"/>
						</c:url>
						<img src="<c:out value="${avatarURL}" escapeXml="true" />" alt="Avatar" class="avatar" />
						<h3>Informazioni</h3>
						<span class="lbl">Nome utente:</span> ${loggedUser.nick}<br/>
						<span class="lbl">Messaggi:</span> ${loggedUser.messages}
						<div style="clear: both;"></div>
					</div>
					<div class="userPanelSection">
						<h3>Frasi Celebri</h3>
						<form action="User?action=updateQuote" method="post" accept-charset="UTF-8">
							<input type="hidden" id="quoteId" name="quoteId" />
							<c:forEach items="${quote}" var="item" varStatus="i">
								<div class="fraseCelebreUserPanel">
									<c:choose>
										<c:when test="${item.id > 0}">
											<a href="User?action=removeQuote&amp;quoteId=${item.id}" class="deleteFraseCelebreUserPanel"><img src="images/delete.png"  alt="Chiudi" /></a>
										</c:when>
										<c:otherwise>
											<img src="images/delete.png" alt="Chiudi" />
										</c:otherwise>
									</c:choose>
									<input name="quote_${item.id}" value="${fn:escapeXml(item.content)}" maxlength="100" class="fraseCelebreUserPanel" />
									<input type="button" value="Salva" onClick="$('#quoteId').val(${item.id});submit();" class="sendFraseCelebreUserPanel" />
								</div>
							</c:forEach>
						</form>
						<div style="clear: both;"></div>
					</div>	
					<div class="userPanelSection">
						<h3>Altre Azioni</h3>
						<a href="./User" class="userPanelButton">User Panel</a>
						<a href="./Pvt?action=inbox" class="userPanelButton">Posta</a>
						<div style="clear: both;"></div>
					</div>
					
				</div>
			</div>
		</div>
		<div id="footer"></div>
<jsp:include page="incBottom.jsp" />