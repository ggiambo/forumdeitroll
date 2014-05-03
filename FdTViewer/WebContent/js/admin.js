var fakeAdIndex = -1;
$(document).ready(function() {
	$("#addWebsiteTitle").click(function() {
		$("#websiteTitles").prepend('<input name="websiteTitles" maxlength="32" /><br/>');
	});
	$("#addFakeAd").click(function() {
		$("#fakeAds").prepend('<input name="fakeAds[' + fakeAdIndex + '].content" maxlength="32" /><br/>');
		$("#fakeAds").prepend('<input name="fakeAds[' + fakeAdIndex + '].visurl" maxlength="32" />');
		$("#fakeAds").prepend('<input name="fakeAds[' + fakeAdIndex + '].title" maxlength="32" />');
		fakeAdIndex--;
	});
});