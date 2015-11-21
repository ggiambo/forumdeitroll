function initPvtSendNew(recipients) {
	
	if (recipients != undefined) {
	// aggiungi i recipients esistenti
		$.each(recipients, function(index, recipient) {
			span = $("<span>").text(recipient).addClass("recipientsSpan");
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
			if ($("#recipientsDiv span.recipientsSpan").length > 4) {
				alert("Massimo 5 destinatari, spammone !");
				return;
			}
			var recipient = ui.item.value;
        	// span che contiene il nome di questo recipient
			span = $("<span>").addClass("recipientsSpan").text(recipient);
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
	$(document).on("click", ".removeRecipient", function() {
        $(this).parent("span").remove();  
    });
    // submit
    $(".pvtSendMessage").submit(function() {
    	// aggiunge tutti i recipients come hidden inputs
		$("#recipientsDiv span.recipientsSpan").each(function(i, val) {
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

function showEmotiboxClassic() {
	var emotiboxes = $("#emotibox .emo");
	$(emotiboxes[1]).hide();
	$(emotiboxes[0]).show();
	var tabs = $("#tabs li");
	$(tabs[1]).removeClass("selectedTab");
	$(tabs[0]).addClass("selectedTab");
}

function showEmotiboxExtended() {
	var emotiboxes = $("#emotibox .emo");
	$(emotiboxes[0]).hide();
	$(emotiboxes[1]).show();
	var tabs = $("#tabs li");
	$(tabs[0]).removeClass("selectedTab");
	$(tabs[1]).addClass("selectedTab");
}

/**
 * Preview PVT
 * @return
 */
function previewPvt() {
	// post data
	var textArea = $(":input[name='text']");
	var data = { text: textArea.val() };
	// preview message
	jQuery.ajax({
		type: "POST",
		url: "Messages?action=getMessagePreview",
		data: data,
		success: function(data) {
		if (data.resultCode == "OK") {
			var height = textArea.height();
			var width = textArea.width();
			// nascondi textArea
			textArea.hide();
			// mostra previewDiv
			var previewDiv = $("#pvtPreview");
			previewDiv.height(height);
			previewDiv.width(width);
			previewDiv.html(data.content);
			previewDiv.show();
			// swap bottoni
			$(":input[name='preview']").hide();
			$(":input[name='edit']").show();
		} else if (data.resultCode == "MSG") {
			alert(data.content);
		} else if (data.resultCode == "ERROR") {
			$("html").html(data.content);
		}
	},
	beforeSend : function(jqXhr, settings) {
		jqXhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
	},
	dataType: "json"
	});
}

/**
 * Torna all'edit del PVT
 * @return
 */
function editPvt() {
	// nascondi preview, mostra textArea, swap bottoni
	$("#pvtPreview").hide();
	$(":input[name='text']").show();
	$(":input[name='preview']").show();
	$(":input[name='edit']").hide();
}