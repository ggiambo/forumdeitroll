
function openThreadTree(threadId) {
	jQuery.ajax({
		type: "GET",
		url: "Threads?action=openThreadTree&threadId=" + threadId,
		success: function(data) {
			$("#threadTree_" + threadId + " .threadTreeEntries").html(data);
			$("#plus_" + threadId).hide();
			$("#minus_" + threadId).show();
		}
	});
}

function closeThreadTree(threadId) {
	$("#threadTree_" + threadId + " .threadTreeEntries").html("");
	$("#plus_" + threadId).show();
	$("#minus_" + threadId).hide();
}

function showMessageInThread(msgId, element) {
	$("body").css("cursor", "progress");
	var triangleClosed = $(element);
	var divContainer = triangleClosed.parent();
	var triangleOpen = divContainer.children("img.threadMessageOpen ");
	// se il div gia' presente, mostralo e bye bye
	var threadMessage = $("#threadMessage_" + msgId);
	if (threadMessage.length != 0) {
		threadMessage.show();
		triangleClosed.hide();
		triangleOpen.show();
		$("body").css("cursor", "auto");
		return;
	}
	// crea il div che conterra' il messaggio
	threadMessage = $("<div>").attr("id", "threadMessage_" + msgId).addClass("threadMessage");
	jQuery.ajax({
		type: "GET",
		url: "Messages?action=getSingleMessageContent&msgId=" + msgId,
		dataType: "json",
		success: function(data) {
			if (data.resultCode == "OK") {
				threadMessage.html(data.content);
				divContainer.append(threadMessage);
				triangleClosed.hide();
				triangleOpen.show();
			} else if (data.resultCode == "MSG") {
				alert(data.content);
			} else if (data.resultCode == "ERROR") {
				threadMessage.html(data.content);
			}
			$("body").css("cursor", "auto");
		}
	});
}

function hideMessageInThread(msgId, element) {
	var divContainer = $("#threadMessage_" + msgId);
	var triangleOpen = $(element);
	divContainer.hide();
	triangleOpen.hide();
	triangleOpen.parent().children("img.threadMessageClosed").show();
}