<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%! static final long bootTime = System.currentTimeMillis(); %>
<img id="headerimg" alt="" src="./images/2.0.png" />
<div id="header">
	<h1>
		<a title="Boot time: <%=new java.util.Date(bootTime)%>" href="Threads" id="titoloForum">
			Forum dei Troll
		</a>
	</h1>
	<c:if test="${not empty randomQuote }">
		<p id="quoteForum" title="${randomQuote.nick}" onclick="getRandomQuote()">
			${randomQuote.content}
		</p>
	</c:if>
</div>
<fdt:delayedScript dump="false">
	var enableAutoRefresh = '${enableAutoRefresh}';
</fdt:delayedScript>