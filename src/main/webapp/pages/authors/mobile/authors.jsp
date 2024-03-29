<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class=row>
	<input type=text placeholder="Cerca..." autofocus="autofocus" id=authorsearch class=col-6 onkeyup="return authorsearch(this,event)">
</div>

<c:forEach items="${authors}" var="author" varStatus="index">
	<div class="row author" onclick="this.childNodes[1].click()">
		<c:choose>
			<c:when test="${not empty param['callback']}">
				<c:set var="link" value="${param['callback']}${author.nick}"/>
			</c:when>
			<c:otherwise>
				<c:set var="link" value="Messages?action=getByAuthor&author=${author.nick}"/>
			</c:otherwise>
		</c:choose>
		<a href="${link}" class=col-6>
			<span class=nickname>${author.nick}</span>
			(${author.messages}
			<c:choose>
				<c:when test="${author.messages != 1}">
					messaggi)
				</c:when>
				<c:otherwise>
					messaggio)
				</c:otherwise>
			</c:choose>
		</a>
	</div>
</c:forEach>