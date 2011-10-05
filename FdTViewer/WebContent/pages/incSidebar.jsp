<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="sidebar">
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