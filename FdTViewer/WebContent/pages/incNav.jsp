<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<div id="nav">
	<ul>
		<c:if test="${navigationMessage != ''}">
			<li>
				<div id="ordinamento">${navigationMessage}</div>
			</li>
		</c:if>
		<li><a href="Threads">Data inizio discussione</a></li>
		<li>|</li>
		<li><a href="Messages">Cronologicamente</a></li>
		<li>|</li>
		<li><a href="Messages?action=newMessage&amp;forum=${param.forum}">Nuovo messaggio</a></li>
		<li>|</li>
			<c:choose>
				<c:when test="${not empty loggedUser}">
					<li>Loggato come <a href="User">${loggedUser}</a><%-- | <fdt:pvt/> | scommentare per provare i pvt --%> [<a href="Messages?action=logoutAction">Logout</a>]</li>
				</c:when>
				<c:otherwise>
					<li><a href="User?action=loginAction">Login</a></li>
					<li>|</li>
					<li><a href="User?action=registerAction">Registrati</a></li>
				</c:otherwise>
			</c:choose>
		<li>|</li>
		<li><jsp:include page="incPrevNext.jsp"/></li>
	</ul>
</div>