<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ page trimDirectiveWhitespaces="true" %>
<div id="nav">
	<ul>
		<c:if test="${navigationMessage != ''}">
			<li>
				<div class="navigationMessage${navigationMessage.type}">

				<c:choose>
					<c:when test="${navForum == ''}">
						Tutto il forum &mdash;
					</c:when>
					<c:when test="${navForum == null}">
					</c:when>
					<c:otherwise>
						<c:out value="${navForum}"/> &mdash;
					</c:otherwise>
				</c:choose>
				${navigationMessage.content}
				</div>
			</li>
			<span style="float: right"><a href="/faqs.html">FAQ</a></span>
		</c:if>
		<c:if test="${navForum != null}">
			<c:choose>
				<c:when test="${navForum == 'Principale'}">
					<c:url value="Messages" var="cronoUrl">
						<c:param name="action" value="getByForum"></c:param>
						<c:param name="forum" value=""></c:param>
					</c:url>
				</c:when>
				<c:when test="${navForum != ''}">
					<c:url value="Messages" var="cronoUrl">
						<c:param name="action" value="getByForum"></c:param>
						<c:param name="forum" value="${navForum}"></c:param>
					</c:url>
				</c:when>
				<c:otherwise>
					<c:url value="Messages" var="cronoUrl">
					</c:url>
				</c:otherwise>
			</c:choose>

			<c:choose>
				<c:when test="${navForum == 'Principale'}">
					<c:url value="Threads" var="nthreadUrl">
						<c:param name="action" value="init"></c:param>
						<c:param name="forum" value=""></c:param>
					</c:url>
				</c:when>
				<c:when test="${navForum != ''}">
					<c:url value="Threads" var="nthreadUrl">
						<c:param name="action" value="init"></c:param>
						<c:param name="forum" value="${navForum}"></c:param>
					</c:url>
				</c:when>
				<c:otherwise>
					<c:url value="Threads" var="nthreadUrl">
					</c:url>
				</c:otherwise>
			</c:choose>

			<c:choose>
				<c:when test="${navForum == 'Principale'}">
					<c:url value="Threads" var="cthreadUrl">
						<c:param name="action" value="getThreadsByLastPost"></c:param>
						<c:param name="forum" value=""></c:param>
					</c:url>
				</c:when>
				<c:when test="${navForum != ''}">
					<c:url value="Threads" var="cthreadUrl">
						<c:param name="action" value="getThreadsByLastPost"></c:param>
						<c:param name="forum" value="${navForum}"></c:param>
					</c:url>
				</c:when>
				<c:otherwise>
					<c:url value="Threads" var="cthreadUrl">
						<c:param name="action" value="getThreadsByLastPost"></c:param>
					</c:url>
				</c:otherwise>
			</c:choose>

			<li><a href="${cronoUrl}">Cronologia</a></li>
			<li>|</li>
			<li><a href="${nthreadUrl}">Thread nuovi</a></li>
			<li>|</li>
			<li><a href="${cthreadUrl}">Thread aggiornati</a></li>
			<li>|</li>
			<li><a href="Polls">Sondaggi</a></li>
			<li>|</li>
		</c:if>
		<c:if test="${navForum == null}">
			<li>
				<a href="Messages">Torna ai messaggi</a>
			</li>
			<li>|</li>
		</c:if>
		<c:if test="${navForum != null }">
			<li>
				<c:choose>
					<c:when test="${navForum == 'Principale'}">
						<a href="Messages?action=newMessage">Nuovo messaggio</a>
					</c:when>
					<c:otherwise>
						<a href="Messages?action=newMessage&amp;forum=${navForum}">Nuovo messaggio</a>
					</c:otherwise>
				</c:choose>
			</li>
			<li>|</li>
		</c:if>
		<c:choose>
			<c:when test="${not empty loggedUser}">
				<c:url value="Threads" var="tuoiThreadURL">
						<c:param name="action" value="getAuthorThreadsByLastPost"/>
						<c:param name="author" value="${loggedUser.nick}"/>
				</c:url>
				<li>Loggato come <a href="User">${loggedUser.nick}</a></li>
				 <li>|</li>
				 <li><fdt:pvt/></li>
				 <li>|</li>
				 <li><a href="<c:out value="${tuoiThreadURL}" escapeXml="true" />">Tuoi thread</a></li>
				 <li>|</li>
				 <li><a href="Polls?action=createNewPoll">Nuovo sondaggio</a></li>
				 <li>|</li>
				 <li>[<a href="Misc?action=logoutAction">Logout</a>]</li>
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