<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="userPanelSection">
	<h3>Invia Messaggio Privato</h3>
	<form action="Pvt" method="POST" class="pvtSendMessage">
		<input type="hidden" name="action" value="sendPvt">
		<div><label for="subject">Oggetto:</label><br />
		<input type="text" name="subject" id="subject" value="${subject }"/></div>
		<div><textarea name="text" id="text" rows="5" cols="32">${text }</textarea></div>
		<div><label for="recipient1">Primo Destinatario:</label><br />
		<input type="text" name="recipient" id="recipient1" value="${recipient[0] }" /></div>
		<div><label for="recipient2">Secondo Destinatario:</label><br />
		<input type="text" name="recipient" id="recipient2" value="${recipient[1] }"/></div>
		<div><label for="recipient3">Terzo Destinatario:</label><br />
		<input type="text" name="recipient" id="recipient3" value="${recipient[2] }"/></div>
		<div><label for="recipient4">Quarto Destinatario:</label><br />
		<input type="text" name="recipient" id="recipient4" value="${recipient[3] }"/></div>
		<div><label for="recipient5">Quinto Destinatario:</label><br />
		<input type="text" name="recipient" id="recipient5" value="${recipient[4] }"/></div>
		<input type="submit" value="Invia" class="sendPvt" />
	</form>
	<div style="clear: both;"></div>
</div> <%-- /Section --%>