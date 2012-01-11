<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<img id="headerimg" alt="" src="./images/2.0.png" />
<div id="header">
	<h1>
		<a style="text-decoration:none; color:#DDDDDD" href="Threads">
			Forum dei Troll
		</a>
	</h1>
	<c:if test="${not empty randomQuote }">
		<p style="font-style:italic;color:white;margin: 0px 0px 10px 35px">
			${randomQuote}
		</p>
	</c:if>
</div>