<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript">
	var ON_READY_FUNCTION = function() {
		
		// porcheria che mischia scriptlet con javascript, brrrr :S
<c:forEach items="${recipients}" var="recipient">
		span = $("<span>").text("${recipient}");
		a = $("<a>").addClass("removeRecipient").attr({ href: "javascript:", title: "Remove ${recipient}"}).text("x").appendTo(span);
		span.insertBefore("#recipients");
		$("#recipients").val("");
</c:forEach>
		
		// autocomplete
		$("#recipients").autocomplete({
			// funzione per cercare i nomi
			source: function(req, add) {
				searchString = req['term'];
				// minimo due caratter
				if (searchString.length < 2) {
					return;
				}
				// chiamata ajax
				$.getJSON("Pvt?action=searchAuthorAjax", {searchString: searchString} , function(data) {
					// array con i risultati
					var suggestions = [];
	                $.each(data.content, function(i, val) {
						suggestions.push(val);
					});
					add(suggestions);
				});
			},
			// funzione per click sul nome dalla lista
			select: function(e, ui) {
				if ($("#recipientsDiv span").length > 4) {
					alert("Massimo 5 destinatari, spammone !");
					return;
				}
				var recipient = ui.item.value;
	        	// span che contiene il nome di questo recipient
				span = $("<span>").text(recipient);
				a = $("<a>").addClass("removeRecipient").attr({ href: "javascript:", title: "Remove " + recipient}).text("x").appendTo(span);
				span.insertBefore("#recipients");
				// cancella il contenuto del campo input
				$("#recipients").val("");
				var dummy = "dummy";
	        },
	        change: function() {
				// cancella il contenuto del campo input
				$("#recipients").val("");
	        },
	        appendTo: "#recipientsDiv"
	    });
	
	    // click sulla "x": rimuove lo <span>
	    $(".removeRecipient", document.getElementById("recipientsDiv")).live("click", function() {  
	        $(this).parent().remove();  
	    });
	    
	    // submit
	    $(".pvtSendMessage").submit(function() {
	    	// aggiunge tutti i recipients come hidden inputs
			$("#recipientsDiv span").each(function(i, val) {
				var recipient = $(val).text();
				// hack schifoso: piglia il contenuto dello span e leva la "x" come ultima lettera :p
				recipient = recipient.substring(0, recipient.length - 1);
		        $('<input />').attr('type', 'hidden')
		            .attr('name', "recipients")
		            .attr('value', recipient)
		            .appendTo($(this));
			 });
	        return true;
	    });
	}
</script>


<div class="userPanelSection">
	<h3>Invia Messaggio Privato</h3>
	<form action="Pvt" method="POST" class="pvtSendMessage">
		<input type="hidden" name="action" value="sendPvt2">
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