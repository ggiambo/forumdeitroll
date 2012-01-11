<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	
	<jsp:include page="incHead.jsp"/>

	<body>

		<jsp:include page="incHeader.jsp"/>
		
		<jsp:include page="incNav.jsp"/>
		
		<div id="main">
				
			<div style="margin:5px; padding:5px; width:100%">
				<table>
					<tr>
						<td>
							<img src="?action=getAvatar&nick=${author.nick}"/>
						</td>
						<td valign="top">
							Nick: ${author.nick}<br/>
							Messaggi: ${author.messages }
						</td>
					</tr>
				</table>
				<br/>
				<a href="?action=getQuotes">Gestisci frasi celebri</a>
<%-- TODO
				|
				<a href="#">Manda messagio privato</a>
--%>
				<br/>
				<br/>
				<form action="User?action=updatePass" method="post">
					<label for="actualPass">Password attuale:</label>
					<input type="password" name="actualPass"/>
					<br/>
					<label for="pass1">Nuova password:</label>
					<input type="password" name="pass1"/>
					<br/>
					<label for="pass2">Verifica password:</label>
					<input type="password" name="pass2"/>
					<input type="submit" value="Modifica"/>
				</form>
				<form action="User?action=updateAvatar" method="post" enctype="multipart/form-data">
					<label for="avatar">Avatar:</label>
					<input type="file" name="avatar">
					<input type="submit" value="Upload">
				</form>
			</div>

		</div>
		
		<div id="footer"></div>
		
		<c:if test="${currentTimeMillis != null}">
			<!--time: <%=System.currentTimeMillis() - (Long)request.getAttribute("currentTimeMillis")%> -->
		</c:if>
		
		<div style="clear: both;"></div>

	</body>
</html>