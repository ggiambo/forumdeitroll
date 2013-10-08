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
	profiler(function(profileData) {
		$.ajax({
			method : 'POST',
			url : 'Minichat',
			data : 'action=send&content=' +
					encodeURIComponent(element.value) +
					'&jsonProfileData=' +
					encodeURIComponent(JSON.stringify(profileData)),
			success : window.location.reload.bind(window.location)
		});
	});
};
var counter = 29;
setInterval(function() {
	if (counter === 0) {
		window.location.reload();
		return;
	}
	$('#refresh').html("Refresh in "+counter+" secondi...");
	counter--;
}, 1000);
		</script>
		<style>
input {
	width: 100%;
}
table {
	width: 100%
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
#refresh {
	cursor: pointer;
}
		</style>
	</head>
	<body>
		<div id=refresh onclick=window.location.reload()>Refresh in 30 secondi...</div>
		<c:if test="${loggedUser != null}">
			<input type=text onkeypress=send(event,this) autofocus placeholder="Scrivi qualcosa...">
			<br>
		</c:if>
		<table>
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