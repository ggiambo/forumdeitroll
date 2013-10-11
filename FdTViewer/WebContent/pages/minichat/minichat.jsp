<%@page import="com.forumdeitroll.servlets.Minichat"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%! static final long bootTime = System.currentTimeMillis(); %>

<!doctype html>
<html>
	<head>
		<title>la ciattina</title>
		<script src=//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js></script>
		<script type=text/javascript src=js/profiler.js></script>
		<script type=text/javascript src="js/minichat.js?v=<%=bootTime%>" ></script>
		<link type="text/css" href="css/minichat.css" rel="stylesheet" />
		<script>
			$(document).ready(function() {
				init(<%= System.currentTimeMillis() %>, <%= Minichat.MAX_MESSAGE_NUMBER %>);
			});
		</script>
	</head>
	<body>
		<input id=content type=text onkeypress=send(event,this) autofocus placeholder="Scrivi qualcosa...">
		<br>
		<table id=scrollback>
			<tbody>
				<c:forEach var="message" items="${messages}">
					<tr>
						<td class="when">
							<fmt:formatDate value="${message.when}" pattern="HH:mm"/>
						</td>
						<td class="who">${message.author}</td>
						<td><c:out value="${message.content}" escapeXml="false" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</body>
</html>