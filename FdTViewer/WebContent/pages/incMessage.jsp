<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<c:choose>
	<c:when test="${index.count % 2 == 0}">
		<c:set var="class" value="msgEven"/>
	</c:when>
	<c:otherwise>
		<c:set var="class" value="msgOdd"/>
	</c:otherwise>
</c:choose>

<div class="${class}">

	<div class="msgHeader">
		<div style="float:left">
			<img src="?action=getAvatar&nick=${msg.author}"/>
		</div>
		<c:if test="${!empty msg.forum}">
			<span class="forum"><b>${msg.forum}</b></span><br/>
		</c:if>
		<span class="author">
			Scritto da
			<i>
				<c:choose>
					<c:when test="${empty msg.author}">
						Non autenticato
					</c:when>
					<c:otherwise>
						<b><a href="Messages?action=getByAuthor&author=${msg.author}">${msg.author}</a></b>
					</c:otherwise>
				</c:choose>
			</i>
			alle <fmt:formatDate value="${msg.date}" pattern="dd.MM.yyyy HH:mm:ss"/>
		</span>

	</div>

	<span style="width:100%; margin:5px;">
		<b><a href="Threads?action=getByThread&threadId=${msg.threadId}"/>${msg.subject}</a></b>
	</span>

	<div style="padding: 10px;">
		<fdt:quote search="${param.search}">${msg.text}</fdt:quote>
		<%-- close open tags --%>
		<c:out escapeXml="false" value="</b></i></u>"/>
	</div>

</div>