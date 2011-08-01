<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<%@page import="java.util.Map"%>

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
		<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
		
		<c:forEach items="${messages}" var="msg" varStatus="index">
			<c:set var="margin" value="${msg['indent'] * 15}"/>
			<div style="border:1px solid black; margin: 5px 5px; margin-left:${margin}px; width:600px;">
				<c:forEach begin="1" end="${msg['indent']}">
					<div style="border:1px solid black; margin: 3px;">
				</c:forEach>
				<c:choose>
					<c:when test="${index.count % 2 == 0}">
						<c:set var="background" value="background-color: #F6F6F6"/>
					</c:when>
					<c:otherwise>
						<c:set var="background" value="background-color: #FFFFFF"/>
					</c:otherwise>
				</c:choose>
				<div style="margin: 5px 5px; margin-right: 5px; ${background}">
					<img src="${contextPath}/Main?action=avatar&nick=${msg['author']}"/>
					Scritto da <i><c:out value="${msg['author']}"/></i>
					alle <fmt:formatDate value="${msg['date']}" pattern="dd.MM.yyyy hh:mm:ss"/><br/><br/>
					<b><a href="${contextPath}/Main?action=thread&threadId=${msg['threadId']}"/>${msg['subject']}</a></b><br/>
					<div style="padding: 15px;">
						<fdt:quote>${msg['text']}</fdt:quote>
						<%-- close open tags --%>
						</b></i></u>
					</div>
				</div>
				<c:forEach begin="1" end="${msg['indent']}">
					</div>
				</c:forEach>
			</div>
		</c:forEach>
		
	</body>
	
</html>