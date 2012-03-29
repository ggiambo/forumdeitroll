function initPvtSendNew(recipients) {
	
	if (recipients != undefined) {
	// aggiungi i recipients esistenti
		$.each(recipients, function(index, recipient) {
			span = $("<span>").text(recipient);
			a = $("<a>").addClass("removeRecipient").attr({ href: "javascript:", title: "Remove ${recipient}"}).text("x").appendTo(span);
			span.insertBefore("#recipients");
			$("#recipients").val("");
		});
	}
	
	// autocomplete
	$("#recipients").autocomplete({
		// minimo 2 caratter
		minLength: 2,
		// funzione per cercare i nomi
		source: function(req, add) {
			// chiamata ajax
			$.getJSON("Pvt?action=searchAuthorAjax", {searchString: req['term']} , function(data) {
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
        },
        close: function() {
        	// cancella il contenuto del campo input
        	$("#recipients").val('');
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