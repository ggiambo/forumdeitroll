
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

function showIframe(type, msgId) {
	// remove previous iframe
	// create iframe
	var iframeString = "<iframe src='http://www.forumdeitroll.it/r.aspx?m_id=" + msgId;
	if (type == "quote") {
		iframeString += "&quote=1";
	}
	iframeString += "&m_rid=0'></iframe>";
	var iframe = $(iframeString);
	iframe.css("width", "750px");
	iframe.css("height", "680px");
	$("#msg" + msgId).append(iframe);
}
