
function hideSidebar() {
	var sidebar = $("#sidebar");
	sidebar.hide("slow", function() {
		var sidebarSmall = $("#sidebarSmall");
		sidebarSmall.show("slow");
		jQuery.ajax("?action=updateSidebarStatus&sidebarStatus=hide");
	});
}

function showSidebar() {
	var sidebarSmall = $("#sidebarSmall");
	sidebarSmall.hide("slow", function() {
		var sidebar = $("#sidebar");
		sidebar.show("slow");
		jQuery.ajax("?action=updateSidebarStatus&sidebarStatus=show");
	});
}

function showReplyDiv(type, parentId) {
	$.get("Messages?action=showReplyDiv&type=" + type + "&parentId=" + parentId,
		function(data) {
			$("#msg" + parentId).append($(data));
			$("#buttons_" + parentId).hide();
			$("#reply_" + parentId + " :input[name='text']").focus();
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

var sent = false;

function send(parentId) {
	// post data
	if (sent) return;
	sent = true;
	var data = { parentId: parentId };
	$("#reply_" + parentId + " :input").each(function() {
		var val = $(this);
		data[val.attr("name")] = val.val();
	});
	// post messagge
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
			sent = false;
		},
		beforeSend : function(jqXhr, settings) {
			jqXhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
		},
		dataType: "json"
	});
}

function insert(openTag, closeTag, parentId) {
	var element = $("#reply_" + parentId + " :input[name='text']").get(0);
	if (document.selection) {
		element.focus();
		sel = document.selection.createRange();
		sel.text = openTag + sel.text + closeTag;
	} else if (element.selectionStart || element.selectionStart == '0') {
		element.focus();
		var startPos = element.selectionStart;
		var endPos = element.selectionEnd;
		element.value = element.value.substring(0, startPos) + openTag + element.value.substring(startPos, endPos) + closeTag + element.value.substring(endPos, element.value.length);
	} else {
		element.value += openTag + closeTag;
	}
}

function openThreadTree(threadId) {
	jQuery.ajax({
		type: "GET",
		url: "Threads?action=openThreadTree&threadId=" + threadId,
		success: function(data) {
			$("#threadTree_" + threadId + " .threadTreeEntries").html(data);
			$("#plus_" + threadId).hide();
			$("#minus_" + threadId).show();
		},
	});
}

function closeThreadTree(threadId) {
	$("#threadTree_" + threadId + " .threadTreeEntries").html("");
	$("#plus_" + threadId).show();
	$("#minus_" + threadId).hide();
}

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
});

var YTgetInfo = function(myYtCounter) {
	return function(ytResponse) {
		var youcode = ytResponse.entry.media$group.yt$videoid.$t
		var title = ytResponse.entry.title.$t
		var youthumb = ytResponse.entry.media$group.media$thumbnail[0].url
		
		var youlink = document.getElementById('yt_'+myYtCounter)
		youlink.appendChild(document.createElement('br'))
		youlink.appendChild(document.createTextNode(title))
		youlink.appendChild(document.createElement('br'))
		var img = document.createElement('img')
		img.src = youthumb
		youlink.appendChild(img)
	}
}

function pedonizeThread(threadTitle, threadId) {
	if (confirm("Vuoi spostare il thread '" + threadTitle + "' in Procura ?")) {
		window.location.assign("Threads?action=pedonizeThread&threadId=" + threadId);
	}
}

var update_counter = function(messageId) {
	try {
	var counter = document.getElementById('counter_' + messageId)
	var textarea = document.getElementById('text_' + messageId)
	if (textarea.value.length > 10000) {
		textarea.value = textarea.value.substring(0, 10000)
		counter.style.backgroundColor = 'red'
		setTimeout(function() {
			counter.style.backgroundColor = null
		}, 200)
	}
	counter.value = 10000 - textarea.value.length
	
	} catch (e) {alert(e.message)}
}