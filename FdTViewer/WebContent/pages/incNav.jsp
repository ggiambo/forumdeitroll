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
				<div>
					<span style="color:darkred; font-weight: bold; margin: 0.2em 0;">
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
					<span class="navigationMessage${navigationMessage.type}">
						${navigationMessage.content}
					</span>
				</div>
			</li>
		</c:if>
		
		<li>
			<c:if test="${blockHeader == 'checked'}">
				<img src="images/icon-pin-color.png" id="unblockHeaderControl" onclick="unblockHeader()" style="" alt="">
				<img src="images/icon-pin-gray.png" id="blockHeaderControl" onclick="blockHeader()" style="display: none;" alt="">
			</c:if>
			<c:if test="${blockHeader == ''}">
				<img src="images/icon-pin-color.png" id="unblockHeaderControl" onclick="unblockHeader()" style="display: none;" alt="">
				<img src="images/icon-pin-gray.png" id="blockHeaderControl" onclick="blockHeader()" style="" alt="">
			</c:if>
			<a href="pages/faqs.html" title="FAQ" class="faq"><img src="images/info_icon.png" alt="FAQ" /></a>
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
		
		<%-- "Discussioni: Ultime per utente" --%>
		<c:url value="Threads" var="getThreadsByLastPostGroupByUser">
			<c:param name="action" value="getThreadsByLastPostGroupByUser"></c:param>
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
			<a id="Threads_getThreads" href="${getThreads}">Nuove</a>
			(<a id="Threads_getThreadsByLastPostGroupByUser" href="${getThreadsByLastPostGroupByUser}">Per utente</a>)
			<a id="Threads_getThreadsByLastPost" href="${getThreadsByLastPost}">Ultime</a>
			<c:if test="${not empty loggedUser}">
				&nbsp;<a id="Threads_getAuthorThreadsByLastPost" href="Threads?action=getAuthorThreadsByLastPost">Tue</a>
			</c:if>
		</li>
		<li>|</li>
		<li><a id="Messages_newMessage" href="${newMessage}">Nuovo messaggio</a></li>
		<li>|</li>
		<li><a id="Polls_" href="Polls">Sondaggi</a></li>
		<c:if test="${not empty loggedUser}">
			<li>|</li>
			<li><a id="Polls_createNewPoll" href="Polls?action=createNewPoll">Nuovo sondaggio</a></li>
		</c:if>
		
	</ul>
	<ul style="float: right; ">
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
	</ul>
	<c:if test="${hasPvts}">
		<div class=pvt-notification-enhancer>
			Nuovi messaggi in casella ! &#8593;
		</div>
	</c:if>
	<br>
	<ul>
		<c:if test="${!empty page}">
			<li id='pager'><fdt:pager handler="Messages"></fdt:pager></li>
		</c:if>
		<li>|</li>
		<li><a href="javascript:ircbox()">##fdt@freenode</a></li>
		<li><a id=ciattina href="Minichat" onclick="window.open('Minichat', 'la ciattina', 'left=100px,top=100px,height=600px,width=400px,menubar=no,toolbar=no,location=no,status=no,scrollbars=yes'); return false;">la ciattina</a></li>
		<c:if test="${not empty loggedUser}">
			<fdt:delayedScript>
				(function() {
					var lastCheck = localStorage['ciattina.lastCheck'];
					if (!lastCheck) {
						lastCheck = 0;
					}
					$.ajax({
						method : 'POST',
						url : 'Minichat',
						data : 'action=check&lastCheck=' + lastCheck,
						success : function(data) {
							if (data.inMessageToRead) {
								$('#ciattina').css('color', 'red');
							} else if (data.messageToRead) {
								$('#ciattina').css('color', '#07df90');
							}
						}
					})
				})();
			</fdt:delayedScript>
		</c:if>
	</ul>
</div>