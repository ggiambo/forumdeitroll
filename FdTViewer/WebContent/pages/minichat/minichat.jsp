<%@page import="com.forumdeitroll.servlets.Minichat"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!doctype html>
<html>
	<head>
		<title>la ciattina</title>
		<script src=//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js></script>
		<script type=text/javascript src=js/profiler.js></script>
		<script>
var send = function(event, element) {
	if (event.which !== 13) return;
	if (element.value === '') return;
	var content = element.value;
	element.disabled = true;
	profiler(function(profileData) {
		$.ajax({
			method : 'POST',
			url : 'Minichat',
			data : 'action=send&content=' +
					encodeURIComponent(content) +
					'&jsonProfileData=' +
					encodeURIComponent(JSON.stringify(profileData)),
			success : function() {
				element.disabled = false;
				element.value = '';
				refresh();
			}
		});
	});
};

var refresh = function() {
	var lastCheck = localStorage['ciattina.lastCheck'];
	$.ajax({
		method : 'POST',
		url : 'Minichat',
		data : 'action=refresh&lastCheck=' + lastCheck,
		success : function(messages) {
			localStorage['ciattina.lastCheck'] = new Date().getTime();
			var table = document.getElementById('scrollback');
			for (var idx in messages) {
				var message = messages[idx];
				if (table.rows.length == <%=Minichat.MAX_MESSAGE_NUMBER%>) {
					table.deleteRow(0);
				}
				var row = table.insertRow(-1);
				row.insertCell(0).appendChild(document.createTextNode(message.when));
				row.cells[0].className = 'when';
				if (!message.author) {
					message.author = '';
				}
				row.insertCell(1).appendChild(document.createTextNode(message.author));
				row.cells[1].className = 'who';
				row.insertCell(2).innerHTML = message.content;
			}
		}
	});
}
$(document).ready(function() {
	localStorage['ciattina.lastCheck'] = new Date().getTime();
});
setInterval(refresh, 30000);
		</script>
		<style>
input {
	width: 100%;
}
table#scrollback {
	width: 100%;
	overflow: auto;
}
td {
	font-family: Arial, Helvetica;
}
td.when {
	width: 1%;
}
td.who {
	width: 5%;
}
tr:nth-child(odd) {
	background-color: #efe;
}
tr:nth-child(even) {
	background-color: #eef;
}
		</style>
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