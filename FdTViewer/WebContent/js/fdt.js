
function hideSidebar() {
	var sidebar = $("#sidebar");
	sidebar.hide("slow", function() {
		var sidebarSmall = $("#sidebarSmall");
		sidebarSmall.show("slow");
		jQuery.ajax("Messages?action=updateSidebarStatus&sidebarStatus=hide");
	});
}

function showSidebar() {
	var sidebarSmall = $("#sidebarSmall");
	sidebarSmall.hide("slow", function() {
		var sidebar = $("#sidebar");
		sidebar.show("slow");
		jQuery.ajax("Messages?action=updateSidebarStatus&sidebarStatus=show");
	});
}

function showReplyDiv(type, parentId) {
	$("#buttons_" + parentId).hide();
	$("body").css("cursor", "progress");
	$.get("Messages?action=showReplyDiv&type=" + type + "&parentId=" + parentId,
		function(data) {
			$("#msg" + parentId).removeClass("msgOptMaxHeight");
			$("#msg" + parentId).append($(data));
			$("#reply_" + parentId + " :input[name='text']").focus();
			$("body").css("cursor", "auto");
	});
}

function closeReplyDiv(parentId) {
	$("#reply_" + parentId).remove();
	$("#buttons_" + parentId).show();
}

function preview(parentId) {
	// post data
	var data = { parentId: parentId };
	$("#reply_" + parentId + " :input").each(function() {
		var val = $(this);
		data[val.attr("name")] = val.val();
	});
	// preview message
	jQuery.ajax({
		type: "POST",
		url: "Messages?action=getMessagePreview",
		data: data,
		success: function(data) {
			if (data.resultCode == "OK") {
				// nascondi textArea
				var textArea = $("#reply_" + parentId + " :input[name='text']");
				textArea.hide();
				// mostra previewDiv
				var previewDiv = $("#preview_" + parentId);
				previewDiv.height(textArea.height());
				previewDiv.html(data.content);
				previewDiv.show();
				// swap bottoni
				$("#reply_" + parentId + " :input[name='preview']").hide();
				$("#reply_" + parentId + " :input[name='edit']").show();
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

function edit(parentId) {
	// nascondi preview, mostra textArea, swap bottoni
	$("#preview_" + parentId).hide();
	$("#reply_" + parentId + " :input[name='text']").show();
	$("#reply_" + parentId + " :input[name='preview']").show();
	$("#reply_" + parentId + " :input[name='edit']").hide();
}

function send(parentId) {
	$("#reply_" + parentId + " :input[type='button']").attr("disabled", "disabled");
	$("body").css("cursor", "progress");
	var data = { parentId: parentId, submitLocation:  "" + window.location };
	$("#reply_" + parentId + " :input").each(function() {
		var val = $(this);
		data[val.attr("name")] = val.val();
	});
	// post messagge
	profiler(function(profileData) {
		data.jsonProfileData = JSON.stringify(profileData);
		jQuery.ajax({
			type: "POST",
			url: "Messages?action=insertMessage",
			data: data,
			success: function(data) {
				if (data.resultCode == "OK") {
					var wl = window.location;
					var newUrl = wl.protocol + "//" + wl.host + wl.pathname.substr(0, wl.pathname.lastIndexOf("/"));
					window.location.assign(newUrl + data.content);
				} else if (data.resultCode == "MSG") {
					alert(data.content);
				} else if (data.resultCode == "ERROR") {
					$("html").html(data.content);
				}
				$("#reply_" + parentId + " :input[type='button']").removeAttr("disabled");
				$("body").css("cursor", "auto");
			},
			beforeSend : function(jqXhr, settings) {
				jqXhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
			},
			dataType: "json"
		});
	});
}

function insert(openTag, closeTag, parentId) {
	return insertIntoTextArea(openTag, closeTag, $("#reply_" + parentId + " :input[name='text']").get(0));
}

function insertIntoTextArea(openTag, closeTag, textArea) {
	if (textArea.createTextRange) {
		textArea.focus(textArea.caretPos);
		textArea.caretPos = document.selection.createRange().duplicate();
		if (textArea.caretPos.text.length > 0) {
			var sel = textArea.caretPos.text;
			var fin = '';
			while (sel.substring(sel.length-1, sel.length) == ' ') {
				sel = sel.substring(0, sel.length-1)
				fin += ' ';
			}
			textArea.caretPos.text = sel + fin + openTag + closeTag;
		} else {
			textArea.caretPos.text = openTag + closeTag;
		}
	} else 	{
		//MOZILLA/NETSCAPE support
		if (textArea.selectionStart || textArea.selectionStart == "0") {
			var startPos = textArea.selectionStart;
			var endPos = textArea.selectionEnd;

			if (startPos != endPos) {
				if(openTag.length > 0) {
					textArea.value = textArea.value.substring(0, startPos) + openTag + textArea.value.substring(startPos, endPos) + closeTag + textArea.value.substring(endPos, textArea.value.length);
				} else {
					textArea.value = textArea.value.substring(0, endPos) + closeTag + textArea.value.substring(endPos, textArea.value.length);
				}
			} else {
				textArea.value = textArea.value.substring(0, startPos) + openTag + textArea.value.substring(endPos, textArea.value.length);
				textArea.selectionStart = (textArea.value.substring(0, startPos) + openTag).length;
				textArea.selectionEnd = (textArea.value.substring(0, startPos) + openTag).length;
			}
		} else {
			textArea.value += openTag + closeTag;
		}
	}
}

var urlInput = function(parentId) {
	var element = $("#reply_" + parentId + " :input[name='text']").get(0);
	var url = prompt('Inserisci l\'url','');
	if (!url) {
		return;
	}
	var desc = prompt('Inserisci il testo da visualizzare','');
	if (desc == null) {
		return;
	}
	var html = '';
	if (desc === '') {
		html = '[url]' + url + '[/url]';
	} else {
		html = '[url=' + url + ']' + desc + '[/url]';
	}
	//IE
	if (document.selection) {
		element.focus();
		var sel  = doucment.selection.createRange();
		sel.moveStart('character', -element.value.length);
		var pos = sel.text.length;
		element.value = element.value.substring(0, pos) + html + element.value.substring(pos, element.value.length);
	}
	//FF
	else if (element.selectionStart || element.selectionStart == '0') {
		var pos = element.selectionStart;
		element.value = element.value.substring(0, pos) + html + element.value.substring(pos, element.value.length);
	}
};

jQuery("document").ready(function(){
	// Eseguita quando il documento è pronto
	jQuery(".messagesBox").resizable({handles: 'e, w'}); // Solo lato EST e OVEST
	// Syntax Highlighter dei messaggi
	SyntaxHighlighter.autoloader(
			  'applescript            js/sh/shBrushAppleScript.js',
			  'actionscript3 as3      js/sh/shBrushAS3.js',
			  'bash shell             js/sh/shBrushBash.js',
			  'coldfusion cf          js/sh/shBrushColdFusion.js',
			  'cpp c                  js/sh/shBrushCpp.js',
			  'c# c-sharp csharp      js/sh/shBrushCSharp.js',
			  'css                    js/sh/shBrushCss.js',
			  'delphi pascal          js/sh/shBrushDelphi.js',
			  'diff patch pas         js/sh/shBrushDiff.js',
			  'erl erlang             js/sh/shBrushErlang.js',
			  'groovy                 js/sh/shBrushGroovy.js',
			  'java                   js/sh/shBrushJava.js',
			  'jfx javafx             js/sh/shBrushJavaFX.js',
			  'js jscript javascript  js/sh/shBrushJScript.js',
			  'perl pl                js/sh/shBrushPerl.js',
			  'php                    js/sh/shBrushPhp.js',
			  'text plain             js/sh/shBrushPlain.js',
			  'py python              js/sh/shBrushPython.js',
			  'ruby rails ror rb      js/sh/shBrushRuby.js',
			  'sass scss              js/sh/shBrushSass.js',
			  'scala                  js/sh/shBrushScala.js',
			  'sql                    js/sh/shBrushSql.js',
			  'vb vbnet               js/sh/shBrushVb.js',
			  'xml xhtml xslt html    js/sh/shBrushXml.js'
			);
	SyntaxHighlighter.defaults['toolbar'] = false;
	SyntaxHighlighter.all();

	// collassa quotes
	$('.quote-container').live('click', function() {
		$(this).removeClass('quote-container');
		$(this).addClass('quote-closer');
	});
	$('.quote-closer').live('click', function() {
		$(this).removeClass('quote-closer');
		$(this).addClass('quote-container');
	});

});

// tasto 'j' per saltare al prossimo messaggio, 'k' per quello precedente
$("body").live("keypress", function(e) {
	var target = $(e.target);
	if (!target.is("input") && !target.is("textarea")) {
		if (e.charCode == 106) {
			var scrollToMsgIndex = $.data(document.body, "scrollToMsgIndex");
			if (scrollToMsgIndex == undefined) {
				scrollToMsgIndex = -1;
			}
			$.data(document.body, "scrollToMsgIndex", ++scrollToMsgIndex);
			$("html, body").animate({
				scrollTop: $('div[id^="msg"]').eq(scrollToMsgIndex).offset().top
			}, 200);
		}
		if (e.charCode == 107) {
			var scrollToMsgIndex = $.data(document.body, "scrollToMsgIndex");
			if (scrollToMsgIndex == undefined) {
				scrollToMsgIndex = $('div[id^="msg"]').length;
			}
			$.data(document.body, "scrollToMsgIndex", --scrollToMsgIndex);
			$("html, body").animate({
				scrollTop: $('div[id^="msg"]').eq(scrollToMsgIndex).offset().top
			}, 200);
		}
	}
});

var YTgetInfo = function(myYtCounter, youcode) {
	var script = document.createElement('script');
	script.type = 'text/javascript';
	script.src = 'http://gdata.youtube.com/feeds/api/videos/' + youcode + '?v=2&alt=json-in-script&callback=YTgetInfo_' + myYtCounter;
	var youlink = document.getElementById('yt_'+myYtCounter);
	youlink.onmouseover = null;
	document.body.appendChild(script);
	return function(ytResponse) {
		var title = ytResponse.entry.title.$t;
		youlink.appendChild(document.createTextNode(title));
	};
};

function pedonizeThreadTree(msgId) {
	if (confirm("Spostare il messaggio e relativa ramificazione in procura ?")) {
		window.location.assign("Messages?action=pedonizeThreadTree&rootMessageId=" + msgId + "&token=" + token);
	}
}

// n.b. non furmigate, il controllo della lunghezza c'è anche lato server
var update_counter = function(messageId, limit) {
	try {
	var counter = document.getElementById('counter_' + messageId);
	var textarea = document.getElementById('text_' + messageId);
	if (textarea.value.length > limit) {
		textarea.value = textarea.value.substring(0, limit);
		counter.style.backgroundColor = 'red';
		setTimeout(function() {
			counter.style.backgroundColor = null;
		}, 200);
	}
	counter.value = limit - textarea.value.length;

	} catch (e) {alert(e.message);}
};

var getRandomQuote = function() {
	jQuery.ajax("Messages?action=getRandomQuote", {
		success : function(data, textStatus, jqXHR) {
			document.getElementById('quoteForum').innerHTML = data.split(/\n/)[0];
			document.getElementById('quoteForum').title = data.split(/\n/)[1];
		}
	});
};

function searchById(event) {
	if (event.which == 13) {
		var msgId = parseFloat($(event.target).val());
		if (isNaN(msgId)) {
		  alert($(event.target).val() + " non è un id valido");
		  return;
		}
		var wl = window.location;
		var newUrl = wl.protocol + "//" + wl.host + wl.pathname.substr(0, wl.pathname.lastIndexOf("/"));
		newUrl += "/Messages?action=getById&msgId=" + msgId;
		window.location.assign(newUrl);
	}
}

function hideMessage(msgId) {
	if (confirm("Vuoi rendere questo messaggio invisibile ?")) {
		window.location.assign("Messages?action=hideMessage&msgId=" + msgId + "&token=" + token);
	}
}

function restoreHiddenMessage(msgId) {
	if (confirm("Vuoi di nuovo rendere questo messaggio visibile ?")) {
		window.location.assign("Messages?action=restoreHiddenMessage&msgId=" + msgId + "&token=" + token);
	}
}

function showHIddenMessage(msgId) {
	$("#msg" + msgId).removeClass("msgInvisible");
	$("#msg" + msgId).addClass("msgVisible");
	$("#msgWarning" + msgId).hide();
}
