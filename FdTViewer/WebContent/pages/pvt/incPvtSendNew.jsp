<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<fdt:delayedScript dump="false">
	$(document).ready(function() {
		initPvtSendNew(new Array(${recipients}));
	});
</fdt:delayedScript>

<div class="userPanelSection">
	<h3>Invia Messaggio Privato</h3>
	<form action="Pvt" method="POST" class="pvtSendMessage">
		<input type="hidden" name="action" value="sendPvt">
		<div><label for="subject">Oggetto:</label><br />
			<input type="text" name="subject" id="subject" value="${subject}"/>
		</div>
		<div><textarea name="text" id="text" rows="5" cols="32">${text}</textarea></div>
		<div><label for="recipients">Destinatari:</label><br />
			<div id="recipientsDiv" class="ui-helper-clearfix">
				<input type="text" id="recipients" type="text">
			</div>
		</div>
		<input type="submit" value="Invia" class="sendPvt" />
	</form>
	<div style="clear: both;"></div>
</div>

<script type="text/javascript" src="js/pvt.js"></script>