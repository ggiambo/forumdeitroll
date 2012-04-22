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
	<form action="Messages" method="get" id="sidebarSearchForm">
		<input type="hidden" name="action" value="search"/>
		<input name="search" size="8" value="${param.search}"/>
		<div id="sortDiv">
			<label for="sort">ordina per: </label>
			<select name="sort" id="sort">
				<option value="date">pi&ugrave; recente</option>
				<option value="rdate">meno recente</option>
				<option value="rank" default>pi&ugrave; rilevante</option>
			</select>
		</div>
		<input type="submit" value="Cerca"/>
	</form>
	
	<label for="getById">Per id:</label>
	<input type="text" name="getById" id="getById" onkeyup="searchById(event)" size="7" value="${msgId}"/>
	<br/><br/>
	
	<ul>
		<c:choose>
			<c:when test="${navType == 'nthread'}">
				<c:url value="Threads" var="allUrl">
				</c:url>
				<c:url value="Threads" var="mainUrl">
					<c:param name="forum" value=""></c:param>
				</c:url>
			</c:when>
			<c:when test="${navType == 'cthread'}">
				<c:url value="Threads" var="allUrl">
					<c:param name="action" value="getThreadsByLastPost"></c:param>
				</c:url>
				<c:url value="Threads" var="mainUrl">
					<c:param name="action" value="getThreadsByLastPost"></c:param>
					<c:param name="forum" value=""></c:param>
				</c:url>
			</c:when>
			<c:otherwise>
				<c:url value="Messages" var="allUrl">
				</c:url>
				<c:url value="Messages" var="mainUrl">
					<c:param name="action" value="getByForum"></c:param>
					<c:param name="forum" value=""></c:param>
				</c:url>
			</c:otherwise>
		</c:choose>
		<li><a href="${allUrl}">Forum dei Troll / Tutto</a></li>
		<li><a href="${mainUrl}">Forum dei Troll / Principale</a></li>
		<c:forEach items="${forums}" var="forum">
			<li>
				<c:choose>
					<c:when test="${navType == 'nthread'}">
						<c:url value="Threads" var="forumUrl">
							<c:param name="action" value="init"></c:param>
							<c:param name="forum" value="${forum}"></c:param>
						</c:url>
					</c:when>
					<c:when test="${navType == 'cthread'}">
						<c:url value="Threads" var="forumUrl">
							<c:param name="action" value="getThreadsByLastPost"></c:param>
							<c:param name="forum" value="${forum}"></c:param>
						</c:url>
					</c:when>
					<c:otherwise>
						<c:url value="Messages" var="forumUrl">
							<c:param name="action" value="getByForum"></c:param>
							<c:param name="forum" value="${forum}"></c:param>
						</c:url>
					</c:otherwise>
				</c:choose>

				<a href="<c:out value="${forumUrl}" escapeXml="true"/>">${forum}</a>
			</li>
		</c:forEach>
	</ul>
</div>
