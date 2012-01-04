
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

function showReplyDiv(type, parentId, threadId) {
	$.get("Messages?action=showReplyDiv&type=" + type + "&parentId=" + parentId + "&threadId=" + threadId,
		function (data) {
			$("#msg" + parentId).append($(data));
	});
}

function send(parentId, threadId) {
	var text = $("#textarea_" + parentId).val();
	$.post("Messages?action=insertMessage", { parentId: parentId, threadId: threadId, text:  text});
}