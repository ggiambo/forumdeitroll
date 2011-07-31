<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:forEach items="${messages}" var="msg">
	<div style="border:1px solid black; margin: 5px; padding: 5px;">
	<img src="<%=request.getContextPath()%>/Main?action=avatar&nick=${msg['author']}"/><br/>
	Scritto da <c:out value="${msg['author']}"/> alle <fmt:formatDate value="${msg['date']}" pattern="dd.MM.yyyy hh:mm:ss"/><br/>
	<b>${msg['subject']}</b><br/>
	${msg['text']}
	</div>
</c:forEach>