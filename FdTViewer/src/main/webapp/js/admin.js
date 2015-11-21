var fakeAdIndex = -1;
$(document).ready(function() {
	$("#addWebsiteTitle").click(function() {
		$("#websiteTitles").prepend('<input name="websiteTitles" maxlength="32" /><br/>');
	});
	$("#addFakeAd").click(function() {
		var div = $("<div>").attr("id", "fakeAd_" + fakeAdIndex);
		div.append('<input name="fakeAds[' + fakeAdIndex + '].title" maxlength="32" />').append(" ");
		div.append('<input name="fakeAds[' + fakeAdIndex + '].visurl" maxlength="32" />').append(" ");
		div.append('<input name="fakeAds[' + fakeAdIndex + '].content" maxlength="32" />').append(" ");
		var delButton = $('<img src="images/delete.png"/>');
		delButton.click(function() {
			$(this).parent("div").remove();
		});
		div.append(delButton);
		$("#fakeAds").prepend(div);
		fakeAdIndex--;
	});
});