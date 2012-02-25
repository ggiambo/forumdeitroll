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
	<ul>
		<li><a href="Threads">Forum dei Troll</a></li>
		<c:forEach items="${forums}" var="forum">
			<li>
				<c:url value="Messages" var="forumUrl">
					<c:param name="action" value="getByForum"></c:param>
					<c:param name="forum" value="${forum}"></c:param>
				</c:url>
				<a href="<c:out value="${forumUrl}" escapeXml="true"/>">${forum}</a>
			</li>
		</c:forEach>
	</ul>
</div>
