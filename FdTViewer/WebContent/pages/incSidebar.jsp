<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="sidebarSmall">
	<span class="openCloseSidebar" onClick="showSidebar();" onMouseOver="this.style.cursor='pointer'">&raquo;</span>
</div>
<div id="sidebar">
	<span class="openCloseSidebar" onClick="hideSidebar();" onMouseOver="this.style.cursor='pointer'">&laquo;</span><br/>
	<form action="Messages" method="get" style="display: inline;">
		<input type="hidden" name="action" value="search"/>
		<input name="search" size="8" value="${param.search}"/><input type="submit" value="Cerca"/>
	</form>
	<ul>
		<c:forEach items="${forums}" var="forum">
			<li><a href="Messages?action=getByForum&forum=${forum}">${forum}</a></li>
		</c:forEach>
	</ul>
</div>