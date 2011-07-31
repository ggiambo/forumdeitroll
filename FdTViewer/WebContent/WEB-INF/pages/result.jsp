<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
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
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

 <c:if test="${!empty pageNr}">
	<c:if test="${pageNr gt 0}">
		<a href="${contextPath}/Main?action=page&pageNr=${pageNr - 1}">&lt;&lt;</a>
	</c:if>
	<a href="${contextPath}/Main?action=page&pageNr=${pageNr + 1}">&gt;&gt;</a>
</c:if>

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
		<img src="${contextPath}/Main?action=avatar&nick=${msg['author']}"/>
		Scritto da <i><c:out value="${msg['author']}"/></i>
		alle <fmt:formatDate value="${msg['date']}" pattern="dd.MM.yyyy hh:mm:ss"/><br/><br/>
		<b><a href="${contextPath}/Main?action=thread&threadId=${msg['threadId']}"/>${msg['subject']}</a></b><br/>
		<div style="padding: 15px;">
			${msg['text']}
		</div>
	</div>
</c:forEach>

<c:if test="${!empty pageNr}">
	<c:if test="${pageNr gt 0}">
		<a href="${contextPath}/Main?action=page&pageNr=${pageNr - 1}">&lt;&lt;</a>
	</c:if>
	<a href="${contextPath}/Main?action=page&pageNr=${pageNr + 1}">&gt;&gt;</a>
</c:if>
</html>
</body>