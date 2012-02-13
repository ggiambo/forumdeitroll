<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ page trimDirectiveWhitespaces="true" %>
<div id="nav">
	<ul>
		<c:if test="${navigationMessage != ''}">
			<li>
				<div class="navigationMessage${navigationMessage.type}">${navigationMessage.content}</div>
			</li>
		</c:if>
		<li>
		Discussioni ordinate per <a href="Threads">inizio</a> / <a href="Threads?action=getThreadsByLastPost">ultimo post</a></li>
		<li>|</li>
		<li><a href="Messages">Cronologia</a></li>
		<li>|</li>
		<li><a href="Messages?action=newMessage&amp;forum=${param.forum}">Nuovo messaggio</a></li>
		<li>|</li>
			<c:choose>
				<c:when test="${not empty loggedUser}">
					<c:url value="Threads" var="tuoiThreadURL">
							<c:param name="action" value="getAuthorThreadsByLastPost"/>
							<c:param name="author" value="${loggedUser.nick}"/>
					</c:url>
					<li>Loggato come <a href="User">${loggedUser.nick}</a> | <fdt:pvt/> | <a href="<c:out value="${tuoiThreadURL}" escapeXml="true" />">Tuoi thread</a> |  [<a href="Misc?action=logoutAction">Logout</a>]</li>
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