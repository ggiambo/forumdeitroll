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
			<c:if test="${not empty loggedUser}">
				Loggato come ${loggedUser} 
				<li><a href="Messages?action=logout">Logout</a>
				<li>|</li>
			</c:if>
		<li><jsp:include page="incPrevNext.jsp"/></li>
	</ul>
</div>