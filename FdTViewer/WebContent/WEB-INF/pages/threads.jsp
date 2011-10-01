<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
	<head>
		<style type="text/css"> 
			body {
				font-family: 'Helvetica';
				font-size: 15px;
			 }
		 </style>
	</head>
	
	<body>
	
		Messaggi ordinati per <a href="Threads">Data inizio discussione</a> o <a href="Messages">Cronologicamente</a>
		&nbsp;&nbsp;|&nbsp;&nbsp;
		<jsp:include page="prevNext.jsp"/>

		<c:forEach items="${threads}" var="thread" varStatus="index">
			<c:choose>
				<c:when test="${index.count % 2 == 0}">
					<c:set var="background" value="background-color: #FFFFFF"/>
				</c:when>
				<c:otherwise>
					<c:set var="background" value="background-color: #F6F6F6"/>
				</c:otherwise>
			</c:choose>
			<div style="margin: 5px; width:600px;border-bottom:1px solid black;${background}">
				<img src="?action=getAvatar&nick=${thread.author}"/>
				<c:if test="${!empty thread.forum}">
					<span style="color:#97A28A"><b>${thread.forum}</b></span>
				</c:if>
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
				alle <fmt:formatDate value="${thread.date}" pattern="dd.MM.yyyy HH:mm:ss"/><br/><br/>
			</div>
		</c:forEach>
		
		<jsp:include page="prevNext.jsp"/>		

	</body>
	
</html>
