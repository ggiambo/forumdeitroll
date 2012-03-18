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
		<c:if test="${navForum != null}">
			<li>
				FdT/<c:out value="${navForum}"/>, visualizza:
				<select id='navSelect' onchange='javascript:navchange("${navForum}")'>
					<c:if test="${navType == ''}">
						<option value="nothing" selected>---</option>
					</c:if>

					<c:choose>
						<c:when test="${navType == 'crono'}">
							<option value="crono" selected>Cronologia messaggi</option>
						</c:when>
						<c:otherwise>
							<option value="crono">Cronologia messaggi</option>
						</c:otherwise>
					</c:choose>

					<c:choose>
						<c:when test="${navType == 'nthread'}">
							<option value="nthread" selected>Thread nuovi</option>
						</c:when>
						<c:otherwise>
							<option value="nthread">Thread nuovi</option>
						</c:otherwise>
					</c:choose>

					<c:choose>
						<c:when test="${navType == 'cthread'}">
							<option value="cthread" selected>Thread aggiornati</option>
						</c:when>
						<c:otherwise>
							<option value="cthread">Thread aggiornati</option>
						</c:otherwise>
					</c:choose>
				</select>
			</li>
			<li>|</li>
		</c:if>
		<c:if test="${navForum == null}">
			<li>
				<a href="Messages">Torna ai messaggi</a>
			</li>
			<li>|</li>
		</c:if>
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