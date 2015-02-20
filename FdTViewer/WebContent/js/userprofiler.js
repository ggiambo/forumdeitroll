var testRule = function(uuid) {
	var reqInfo = prompt("Inserisci l'oggetto reqInfo (JSON)");
	$.ajax({
		method : 'POST',
		url : 'UserProfiler',
		data : {
			action : 'testRule',
			reqInfo : reqInfo,
			code : document.getElementsByName("code")[0].value
		},
		success: function(data) {
			alert(data);
		},
		beforeSend : function(jqXhr, settings) {
			jqXhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
		},
	});
};