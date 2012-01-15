
function initSidebarStatus() {
	jQuery.ajax({
		  url: "?action=sidebarStatus",
		  dataType: "html",
		  success: function(data) {
			if (data == "show") {
				$("#sidebarSmall").hide();
				$("#sidebar").show();
			} else {
				$("#sidebar").hide();
				$("#sidebarSmall").show();
			}
		  }
		});
}

function hideSidebar() {
	var sidebar = $("#sidebar");
	sidebar.hide("slow", function() {
		var sidebarSmall = $("#sidebarSmall");
		sidebarSmall.show("slow");
		jQuery.ajax("?action=sidebarStatus&sidebarStatus=hide");
	});
}

function showSidebar() {
	var sidebarSmall = $("#sidebarSmall");
	sidebarSmall.hide("slow", function() {
		var sidebar = $("#sidebar");
		sidebar.show("slow");
		jQuery.ajax("?action=sidebarStatus&sidebarStatus=show");
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

function send(parentId) {
	// post data
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
				if (wl.pathname.match(/Threads$/)) {
					window.location.reload(); // force reload, altrimenti non fa un piffero :\
				}
			} else if (data.resultCode == "MSG") {
				alert(data.content);
			} else if (data.resultCode == "ERROR") {
				$("html").html(data.content);
			}
		},
		beforeSend : function(jqXhr, settings) {
			jqXhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8')
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

jQuery("document").ready(function(){
	// Sidebar
	initSidebarStatus();
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