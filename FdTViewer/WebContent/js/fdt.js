
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
	// close button
	var closeImg = $("<img/>");
	closeImg.attr("src", "images/close.jpeg");
	// link enclosing close button
	var link = $("<a/>");
	link.css("float", "right");
	link.css("padding", "5px");
	link.click(function() {
		$("#" + divId).remove();
	});
	
	link.append(closeImg);

	// iframe forumdeitroll
	var iframe = $("<iframe/>");
	var iframesrc = "http://www.forumdeitroll.it/r.aspx?m_id=" + msgId;
	if (type == "quote") {
		iframesrc += "&quote=1";
	}
	iframe.attr("src", iframesrc);
	iframe.css("width", "750px");
	iframe.css("height", "680px");
	
	// containerdiv
	var div = $("<div/>");
	var divId = "iframeId" + msgId;
	div.css("width", "750px");
	div.attr("id", divId);
	
	div.append(link);
	div.append(iframe);
	
	$("#msg" + msgId).append(div);
}

function closeIframe(divId) {
	$("#" + divId).remove();
}