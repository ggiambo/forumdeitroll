<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%! static final long bootTime = System.currentTimeMillis(); %>
<c:if test="${empty loggedUser || loggedUser.preferences['hideBannerone'] != 'checked'}">
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
</c:if>
<c:if test="${not empty loggedUser && loggedUser.preferences['hideBannerone'] == 'checked'}">
	<div id="quoteForum" class="quoteForumSmall" title="${randomQuote.nick}" onclick="getRandomQuote()">
		${randomQuote.content}
	</div>
</c:if>