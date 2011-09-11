<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

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
	
		<c:if test="${action == 'getByAuthor'}">
			<h4>Messaggi scritti da <i>${param.author}</i></h4>
		</c:if>
		<h4><a href="Messages">Inizio</a><br/></h4>
	
		<jsp:include page="prevNext.jsp"/>

		<c:forEach items="${messages}" var="msg" varStatus="index">
			<c:choose>
				<c:when test="${index.count % 2 == 0}">
					<c:set var="background" value="background-color: #F6F6F6"/>
				</c:when>
				<c:otherwise>
					<c:set var="background" value="background-color: #FFFFFF"/>
				</c:otherwise>
			</c:choose>
			<div style="margin: 5px; width:600px;border-bottom:1px solid black;${background}">
				<img src="?action=getAvatar&nick=${msg.author}"/>
				<c:if test="${!empty msg.forum}">
					<span style="color:#97A28A"><b>${msg.forum}</b></span>
				</c:if>
				<br/>
				Scritto da 
				<i>
					<c:choose>
						<c:when test="${empty msg.author}">
							Non autenticato
						</c:when>
						<c:otherwise>
							<b><a href="?action=getByAuthor&author=${msg.author}">${msg.author}</a></b><br/>
						</c:otherwise>
					</c:choose>
				</i>
				alle <fmt:formatDate value="${msg.date}" pattern="dd.MM.yyyy hh:mm:ss"/><br/><br/>
				<b><a href="Thread?action=getByThread&threadId=${msg.threadId}"/>${msg.subject}</a></b><br/>
				<div style="padding: 15px;">
					<fdt:quote>${msg.text}</fdt:quote>
					<%-- close open tags --%>
					<c:out escapeXml="false" value="</b></i></u>"/>
				</div>
			</div>
		</c:forEach>
		
		<jsp:include page="prevNext.jsp"/>		

	</body>
	
</html>
