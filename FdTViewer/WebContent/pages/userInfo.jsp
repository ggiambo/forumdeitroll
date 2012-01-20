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
						<img src="?action=getAvatar&nick=${author.nick}" alt="Avatar" class="avatar" />
						<h3>Informazioni</h3>
						<span class="lbl">Nome utente:</span> ${author.nick}<br/>
						<span class="lbl">Messaggi:</span> ${author.messages}
						<div style="clear: both;"></div>
					</div>
					<div class="userPanelSection">
						<h3>Frasi Celebri</h3>
						<ul class="fraseCelebreUserPanel">
						<c:forEach items="${quotes}" var="item">
							<li class="fraseCelebreUserPanel">${fn:escapeXml(item.content)}</li>
						</c:forEach>
						</ul>
						<div style="clear: both;"></div>
					</div>	
				</div>	
			</div>
		</div>
		<div id="footer"></div>
<jsp:include page="incBottom.jsp" />
