<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
	
	<jsp:include page="incHead.jsp"/>

	<body>

	<div id="header"><h1>FdT Due Zero !</h1></div>

		<jsp:include page="incNav.jsp"/>

		<jsp:include page="incSidebar.jsp"/>

		<div id="main">

			<c:forEach items="${messages}" var="thread" varStatus="index">
				<c:choose>
					<c:when test="${index.count % 2 == 0}">
						<c:set var="class" value="msgEven"/>
					</c:when>
					<c:otherwise>
						<c:set var="class" value="msgOdd"/>
					</c:otherwise>
				</c:choose>
				<div class="${class}">
					<c:if test="${!empty thread.forum}">
						<span style="color:#97A28A"><b>${thread.forum}</b></span>
					</c:if>
					<br/>
					${thread.numberOfMessages}
					<c:choose>
						<c:when test="${thread.numberOfMessages > 1}">
							messaggi
						</c:when>
						<c:otherwise>
							messaggio
						</c:otherwise>
					</c:choose>
					<br/>
					<b><a href="Threads?action=getByThread&threadId=${thread.id}"/>${thread.subject}</a></b>
					<br/>
					Iniziato da
					<i>
						<c:choose>
							<c:when test="${empty thread.author}">
								Non autenticato
							</c:when>
							<c:otherwise>
								<b><a href="Messages?action=getByAuthor&author=${thread.author}">${thread.author}</a></b>
							</c:otherwise>
						</c:choose>
					</i>
					alle <fmt:formatDate value="${thread.date}" pattern="dd.MM.yyyy HH:mm"/>
					<br/><br/>
				</div>
				<hr/>
			</c:forEach>

		</div>

		<div id="footer">
			<jsp:include page="incPrevNext.jsp" />
		</div>
		
		<c:if test="${currentTimeMillis != null}">
			<!--time: <%=System.currentTimeMillis() - (Long)request.getAttribute("currentTimeMillis")%> -->
		</c:if>

	</body>
</html>
