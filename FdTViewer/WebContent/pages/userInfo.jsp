<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<jsp:include page="incTop.jsp" />
		<div id="main">
			<div class="userPanel">
				<div class="userPanelCaption">Informazioni Utente</div>
				<div class="userPanelContent">
					<div class="userPanelSection">
						<c:url value="" var="avatarURL">
							<c:param name="action" value="getAvatar"/>
							<c:param name="nick" value="${author.nick}"/>
						</c:url>
						<img src="Misc<c:out value="${avatarURL}" escapeXml="true" />" alt="Avatar" class="avatar" />
						<h3>Informazioni</h3>
						<span class="lbl">Nome utente:</span> ${author.nick}<br/>
						<c:url value="Messages" var="userMsgsURL">
							<c:param name="action" value="getByAuthor"/>
							<c:param name="author" value="${author.nick}"/>
						</c:url>
						<span class="lbl">Messaggi:</span><a href="<c:out value="${userMsgsURL}" escapeXml="true" />">${author.messages}</a>
						<div style="clear: both;"></div>
					</div>
					<div class="userPanelSection">
						<h3>Frasi Celebri</h3>
						<ul class="fraseCelebreUserPanel">
						<c:forEach items="${quotes}" var="item">
							<li class="fraseCelebreUserPanel">${fn:escapeXml(item.content)}</li>
						</c:forEach>
						</ul>
					</div>
					<c:if test="${not empty loggedUser}">
						<div class="userPanelSection">
							<h3>Altre Azioni</h3>
							<c:url value="Pvt" var="sendPvt">
								<c:param name="action" value="sendNew"/>
								<c:param name="singleRecipient" value="${author.nick}"/>
							</c:url>
							<a href="<c:out value="${sendPvt}" escapeXml="true" />" class="userPanelButton">Manda PVT</a>
							<div style="clear: both;"></div>
						</div>
					</c:if>
				</div>	
			</div>
		</div>
		<div id="footer"></div>
<jsp:include page="incBottom.jsp" />