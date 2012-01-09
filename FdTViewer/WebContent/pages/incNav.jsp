<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="nav">
	<ul>
		<c:if test="${navigationMessage != ''}">
			<li>
				<h4>${navigationMessage}</h4>
			</li>
		</c:if>
		<li><a href="Threads">Data inizio discussione</a></li>
		<li>|</li>
		<li><a href="Messages">Cronologicamente</a></li>
		<li>|</li>
		<li><a href="Messages?action=newMessage&forum=${param.forum }">Nuovo messaggio</a>
		<li>|</li>
			<c:choose>
				<c:when test="${not empty loggedUser}">
					Loggato come <a href="User">${loggedUser}</a>
					[<li><a href="Messages?action=logoutAction">Logout</a>]
				</c:when>
				<c:otherwise>
					<li><a href="User?action=loginAction">Login</a>
					<li>|</li>
					<li><a href="User?action=registerAction">Registrati</a>
				</c:otherwise>
			</c:choose>
		<li>|</li>
		<li><jsp:include page="incPrevNext.jsp"/></li>
	</ul>
</div>