<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class=row>
	<input type=text placeholder="Cerca..." autofocus="autofocus" id=authorsearch class=col-6 onkeyup="return authorsearch(this,event)">
</div>

<c:forEach items="${authors}" var="author" varStatus="index">
	<c:choose>
		<c:when test="${index.index % 2 == 0}">
			<c:set var="rowclass" value="msgEven"/>
		</c:when>
		<c:otherwise>
			<c:set var="rowclass" value="msgOdd"/>
		</c:otherwise>
	</c:choose>
	<a class="${rowclass}" href="Messages?action=getByAuthor&author=${author.nick}">
		<img src="Misc?action=getAvatar&amp;nick=${author.nick}" class=avatar>
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
	<br>
</c:forEach>