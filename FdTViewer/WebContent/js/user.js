$(document).ready(function() {
	$("#addWebsiteTitle").click(function() {
		$("#websiteTitles").prepend('<input name="websiteTitles" maxlength="32" /><br/>');
	});
});