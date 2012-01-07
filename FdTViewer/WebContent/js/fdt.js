
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
	});
}

function closeReplyDiv(parentId) {
	$("#reply_" + parentId).remove();
	$("#buttons_" + parentId).show();
}

function send(parentId) {
	var text = $("#reply_" + parentId + " :input[name='text']").val();
	var nick = $("#reply_" + parentId + " :input[name='nick']").val();
	var pass = $("#reply_" + parentId + " :input[name='pass']").val();
	var subject = $("#reply_" + parentId + " :input[name='subject']").val();
	var forum = $("#reply_" + parentId + " :input[name='forum']").val();
	var captcha = $("#reply_" + parentId + " :input[name='captcha']").val();
	jQuery.ajax({
		type: "POST",
        url: "Messages?action=insertMessage",
        data: { parentId: parentId, text:  text, nick: nick, pass: pass, subject: subject, forum: forum, captcha: captcha},
        success: function(data) {
			var wl = window.location;
			var newUrl = wl.protocol + "//" + wl.host + wl.pathname.substr(0, wl.pathname.lastIndexOf("/"));
			window.location = newUrl + data;
        	},
        error: function(data) {
        	alert(data.responseText);
        }
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