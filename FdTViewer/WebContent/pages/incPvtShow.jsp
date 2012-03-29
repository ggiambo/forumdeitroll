<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<div class="userPanelSection">
	<p>Scritto da: 
		<c:url value="User" var="fromNickURL">
			<c:param name="action" value="getUserInfo"/>
			<c:param name="nick" value="${pvtdetail.fromNick}"/>
		</c:url>
		<a href="<c:out value="${fromNickURL}" escapeXml="true" />" class="pvtNickname">${pvtdetail.fromNick}</a>
	</p>
	<p>Inviato a:
		<c:forEach items="${pvtdetail.toNick}" var="destNick" varStatus="index">
			<c:url value="User" var="destNickURL">
				<c:param name="action" value="getUserInfo"/>
				<c:param name="nick" value="${destNick}"/>
			</c:url>
			<a href="<c:out value="${destNickURL}" escapeXml="true" />" class="pvtNickname">
				<c:out value="${destNick}"/>
			</a>
		</c:forEach>
	</p>
	<div class="pvtTextMessage">
		<fdt:msg author="${sender}">${pvtdetail.text }</fdt:msg>
	</div>
	<c:if test="${pvtdetail.fromNick != loggedUser.nick}">
		<a href="Pvt?action=reply&amp;id=${pvtdetail.id}" class="pvtRispondiBtn">Rispondi</a>
		<a href="Pvt?action=replyAll&amp;id=${pvtdetail.id}" class="pvtRispondiBtn">Rispondi a tutti</a>
		<div style="clear: both;"></div>
	</c:if>
</div>