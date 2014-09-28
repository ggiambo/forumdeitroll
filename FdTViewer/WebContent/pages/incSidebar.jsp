<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page trimDirectiveWhitespaces="true" %>
<c:choose>
	<c:when test="${sidebarStatus == 'show'}">
		<c:set var="sidebarSmallStyle" value="display:none"/>
	</c:when>
	<c:otherwise>
		<c:set var="sidebarStyle" value="display:none"/>
	</c:otherwise>
</c:choose>

<div id="sidebarSmall" style="${sidebarSmallStyle}">
	<span class="openCloseSidebar" onClick="showSidebar();" onMouseOver="this.style.cursor='pointer'">&raquo;</span>
</div>
<div id="sidebar" style="${sidebarStyle}">
	<span class="openCloseSidebar" onClick="hideSidebar();" onMouseOver="this.style.cursor='pointer'">&laquo;</span><br/>
	<c:if test="${not empty loggedUser}">
		<a href="Bookmarks">Segnalibri</a><br>
	</c:if>
	<a href="ReadersDigest">The Troll's Digest</a>
	<br>
	<a href="javascript:forceMobileView()">Visualizzazione Mobile</a>
	<br>
	<br>
	<form action="Messages" method="get" id="sidebarSearchForm" onsubmit="return searchAjax();">
		<input type="hidden" name="action" value="search"/>
		<input name="search" size="8" value="<c:out value="${param.search}"/>"/>
		<div id="sortDiv">
			<label for="sort">ordina per: </label>
			<select name="sort" id="sort">
				<option value="date">pi&ugrave; recente</option>
				<option value="rdate">meno recente</option>
				<option value="rank" selected>pi&ugrave; rilevante</option>
			</select>
		</div>
		<input type="submit" value="Cerca"/>
		<br>
		<a href="javascript:showAdvancedSearch()">Ricerca avanzata</a>
	</form>
	
	<label for="getById">Per id:</label>
	<input type="text" name="getById" id="getById" onkeyup="searchById(event)" size="7" value="${msgId}"/>
	<br/><br/>
	
	<ul>
		<%-- navigazione fa senso solo per Messages e Threads --%>
		<c:set var="urlServlet" value="${servlet}" />
		<c:set var="action" value="${param.action}" />
		<c:if test="${urlServlet != 'Messages' and  urlServlet != 'Threads'}">
			<c:set var="urlServlet" value="Messages" />
			<c:set var="action" value="getMessages" />
		</c:if>

		<%-- casi speciali --%>
		<c:if test="${action == 'getAuthorThreadsByLastPost' || action == 'getByThread'}">
			<c:set var="action" value="getThreads" />
		</c:if>
		<c:if test="${action == 'getById' || action == 'getByAuthor'}">
			<c:set var="action" value="getMessages" />
		</c:if>
	
		<c:url value="${urlServlet}" var="allUrl" >
			<c:param name="action" value="${action}" />
		</c:url>
		<c:url value="${urlServlet}" var="mainUrl">
			<c:param name="action" value="${action}" />
			<c:param name="forum" value="" />
		</c:url>
		<li><a href="${allUrl}">Forum dei Troll / Tutto</a></li>
		<li><a href="${mainUrl}">Forum dei Troll / Principale</a></li>
		<c:forEach items="${forums}" var="forum">
			<li>
				<c:url value="${urlServlet}" var="forumUrl">
					<c:param name="action" value="${action}"></c:param>
					<c:param name="forum" value="${forum}"></c:param>
				</c:url>
				<a href="<c:out value="${forumUrl}" escapeXml="true"/>">
					<c:choose>
						<c:when test="${specificParams['forum'] == forum}">
							<b>${forum}</b>
						</c:when>
						<c:otherwise>
							${forum}
						</c:otherwise>
					</c:choose>
				</a>
			</li>
		</c:forEach>
	</ul>
</div>
