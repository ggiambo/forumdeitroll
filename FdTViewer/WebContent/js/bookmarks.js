var edit_bm = function(msgId, maxLen) {
	var link = document.getElementById('link_bm_' + msgId);
	var text = link.childNodes && link.childNodes[0] ? link.childNodes[0].nodeValue : "";
	var td = link.parentNode;
	var html =
		"<form action='Bookmarks' method='POST' enctype='application/x-www-form-urlencoded'>" +
			"<input type='hidden' name='action' value='edit'>" +
			"<input type='hidden' name='msgId' value='"+msgId+"'>" +
			"<input type='text' style='width: 75%;' name='subject' id='subject_edit_" +msgId + "' maxlength='"+maxLen+"' size='"+maxLen+"'>" +
			"&nbsp;"+
			"<input type='submit' name='btnConferma' value='Conferma' style='width: 18%'>"+
		"</form>";
	td.innerHTML = html;
	setTimeout(function() {
		document.getElementById('subject_edit_' + msgId).value = text;
	}, 100); // aspetta il reflow della pagina dopo l'innerHTML
}