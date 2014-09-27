<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:forEach items="${messages}" var="msg" varStatus="index">
	<c:set var="index" value="${index.count}" scope="request"/>
	<div class=msgInfo>
		Scritto da
		<c:choose>
			<c:when test="${not empty msg.author.nick}">
				<img src="Misc?action=getAvatar&amp;&nick=${msg.author.nick}" class=avatar>
				<a href="Messages?action=getByAuthor&amp;author=${msg.author.nick}">${msg.author.nick}</a>
			</c:when>
			<c:otherwise>
				non autenticato
			</c:otherwise>
		</c:choose>
		il <fmt:formatDate value="${msg.date}" pattern="dd.MM.yyyy"/> alle <fmt:formatDate value="${msg.date}" pattern="HH:mm"/>
	</div>
	<c:choose>
		<c:when test="${index.index % 2 == 0}">
			<c:set var="rowclass" value="msgEven"/>
		</c:when>
		<c:otherwise>
			<c:set var="rowclass" value="msgOdd"/>
		</c:otherwise>
	</c:choose>
	<div class="messageBox ${rowclass}">
		<fdt:msg search="${param.search}" signature="false" author="${msg.author}">${msg.text}</fdt:msg>
	</div>
</c:forEach>