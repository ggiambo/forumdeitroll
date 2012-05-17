<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ page trimDirectiveWhitespaces="true" %>

<fdt:delayedScript dump="false">
	$(document).ready(function() {
		$('#${servlet}_${param.action}').addClass('selected');
	});
</fdt:delayedScript>

<div id="nav">

	<c:set var="forum" value="${specificParams['forum']}" />
	<ul>
		<%-- navigation message --%>
		<c:if test="${navigationMessage != null}">
			<li>
				<div class="navigationMessage${navigationMessage.type}">
				<span style="color:darkred">
					<c:choose>
						<c:when test="${forum == null}">
							Tutto il forum
						</c:when>
						<c:when test="${forum != ''}">
							<c:out value="${forum}"/> 
						</c:when>
					</c:choose>
				</span>
				<c:if test="${forum == null or forum != ''}">
					&mdash;
				</c:if>
				${navigationMessage.content}
				</div>
			</li>
		</c:if>
		
		<li>
			<a href="/faqs.html" title="FAQ"><img src="images/info_icon.png" alt="FAQ" /></a>
		</li>

		<%-- "Cronologia" --%>
		<c:url value="Messages" var="getMessages">
			<c:param name="action" value="getMessages"></c:param>
			<c:if test="${forum != null}">
				<c:param name="forum" value="${forum}"></c:param>
			</c:if>
		</c:url>

		<%-- "Discussioni: nuove" --%>
		<c:url value="Threads" var="getThreads">
			<c:param name="action" value="getThreads"></c:param>
			<c:if test="${forum != null}">
				<c:param name="forum" value="${forum}"></c:param>
			</c:if>
		</c:url>

		<%-- "Discussioni: Ultime" --%>
		<c:url value="Threads" var="getThreadsByLastPost">
			<c:param name="action" value="getThreadsByLastPost"></c:param>
			<c:if test="${forum != null}">
				<c:param name="forum" value="${forum}"></c:param>
			</c:if>
		</c:url>
		
		<%-- "Nuovo Messaggio" --%>
		<c:url value="Messages" var="newMessage">
			<c:param name="action" value="newMessage"></c:param>
			<c:if test="${forum != null}">
				<c:param name="forum" value="${forum}"></c:param>
			</c:if>
		</c:url>
		
		<li><a id="Messages_getMessages" href="${getMessages}">Cronologia</a></li>
		<li>|</li>
		<li>Discussioni: 
			<a id="Threads_getThreads" href="${getThreads}">Nuove</a>&nbsp;
			<a id="Threads_getThreadsByLastPost" href="${getThreadsByLastPost}">Ultime</a>
			<c:if test="${not empty loggedUser}">
				&nbsp;<a id="Threads_getAuthorThreadsByLastPost" href="Threads?action=getAuthorThreadsByLastPost">Tue</a>
			</c:if>
		</li>
		<li>|</li>
		<li><a id="Polls_" href="Polls">Sondaggi</a></li>
		<c:if test="${not empty loggedUser}">
			<li>|</li>
			<li><a id="Polls_createNewPoll" href="Polls?action=createNewPoll">Nuovo sondaggio</a></li>
		</c:if>
		<li>|</li>
		<li><a id="Messages_newMessage" href="${newMessage}">Nuovo messaggio</a></li>

		<li>|</li>
		<c:choose>
			<c:when test="${not empty loggedUser}">
				<li>Loggato come <a href="User">${loggedUser.nick}</a></li>
				 <li>|</li>
				 <li><fdt:pvt/></li>
				 <li>|</li>
				 <li>[<a href="Misc?action=logoutAction">Logout</a>]</li>
			</c:when>
			<c:otherwise>
				<li><a href="User?action=loginAction">Login</a></li>
				<li>|</li>
				<li><a href="User?action=registerAction">Registrati</a></li>
			</c:otherwise>
		</c:choose>
		
		<c:if test="${!empty page}">
			<li>|</li>
			<li><fdt:pager handler="Messages"></fdt:pager></li>
		</c:if>
	</ul>
</div>