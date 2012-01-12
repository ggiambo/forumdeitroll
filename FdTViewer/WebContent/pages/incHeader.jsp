<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<img id="headerimg" alt="" src="./images/2.0.png" />
<div id="header">
	<h1>
		<a href="Threads" id="titoloForum">
			Forum dei Troll
		</a>
	</h1>
	<c:if test="${not empty randomQuote }">
		<p id="quoteForum" title="${randomQuote.nick}">
			${randomQuote.content}
		</p>
	</c:if>
</div>