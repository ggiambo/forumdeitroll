var Req = function(method, url, headers, data, callback) {
	var xhr = new XMLHttpRequest;
	xhr.open(method, url, true);
	var hname = null;
	for (hname in headers) {
		xhr.setRequestHeader(hname, headers[hname]);
	}
	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4) {
			callback(xhr);
		}
	};
	xhr.send(data);
};

var GET = function(url, callback) {
	Req('GET', url, {}, null, callback);
};

var classico = function() {
	GET('Messages?action=updateMobileView', function(xhr) {
		location.reload();
	});
};

var authorsearch = function(element, event) {
	var search = element.value;
	search = search.toLowerCase();
	var nodelist = document.querySelectorAll('a>span.nickname');
	var i;
	for (i = 0; i < nodelist.length; i++) {
		var span = nodelist[i];
		if (!search) {
			span.parentNode.style.display = 'block';
		} else {
			if (span.childNodes[0].nodeValue.toLowerCase().indexOf(search) !== -1) {
				span.parentNode.style.display = 'block';
			} else {
				span.parentNode.style.display = 'none';
			}
		}
	}
	return true;
};

var domready = function(callback) {
	if (domready.called) {
		callback();
		return;
	}
	if (!domready.configured) {
		document.addEventListener("DOMContentLoaded", function(event) {
			domready.called = true;
			var i = null;
			for (i in domready.callbacks) {
				var callback = domready.callbacks[i];
				try {
					callback();
				} catch (e) {}
			}
		});
		domready.callbacks = [];
		domready.configured = true;
	}
	domready.callbacks.push(callback);
};

domready(function() {
	var value = localStorage['toggleQuotes'];
	if (value === 'true') {
		var style = document.createElement('style');
		style.appendChild(document.createTextNode("[class^='quoteLvl'], [class^='quoteLvl'] + br, .quote-container, .quote-container + br { visibility: collapse !important; display: none !important; }"));
		document.body.appendChild(style);
	}
});

var toggleQuotes = function() {
	var value = localStorage['toggleQuotes'];
	if (value === 'true') {
		localStorage['toggleQuotes'] = 'false';
	} else {
		localStorage['toggleQuotes'] = 'true';
	}
	location.reload();
};

var configureMessagesView = function() {
	document.addEventListener('click', function(event) {
		var div = event.target;
		if (div.tagName !== 'DIV') return;
		if (div.className !== 'msgHeader') {
			if (div.parentNode.className === 'msgHeader') {
				div = div.parentNode;
			} else if (div.className.indexOf('messageBox') !== -1){
				div = div.querySelector('.msgHeader');
			} else {
				return;
			}
		}
		var content = div.parentNode.querySelector('.msgContent');
		if (content.style.display) {
			if (content.style.display === 'block') {
				content.style.display = 'none';
			} else {
				content.style.display = 'block';
			}
		} else {
			content.style.display = 'block';
		}
	});
};

var previewMode = false;

var previewMessage = function() {
	var textarea = document.querySelector('#text');
	var preview = document.querySelector('#preview');
	if (previewMode) {
		preview.style.display = 'none';
		textarea.style.display = 'block';
		previewMode = false;
		return;
	}
	var text = textarea.value;
	Req('POST', "Messages?action=getMessagePreview",
		{'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
		'text=' + encodeURIComponent(text),
		function(xhr) {
			var response = JSON.parse(xhr.responseText);
			if (response.resultCode === "OK") {
				preview.style.height = textarea.clientHeight;
				preview.innerHTML = response.content;
				preview.style.display = 'block';
				textarea.style.display = 'none';
				previewMode = true;
			} else if (response.resultCode === "MSG") {
				alert(response.content);
			} else if (response.resultCode === "ERROR") {
				document.body.innerHTML = response.content;
			}
	});
};

var sendInProgress = false;

var sendMessage = function() {
	if (sendInProgress === true) {
		alert('Invio del messaggio in corso!');
		return;
	}
	sendInProgress = true;
	profiler(function(jsonProfileData) {
		var dataObj = {
			id : document.querySelector('#messageId').value,
			parentId : document.querySelector('#replyToId').value,
			submitLocation: String(window.location),
			subject : document.querySelector('#subject').value,
			text : document.querySelector('#text').value,
			forum : document.querySelector('#forum').value,
			jsonProfileData : JSON.stringify(jsonProfileData),
			token : token,
			captcha : document.querySelector('#captcha').value,
			nick : document.querySelector('#username').value,
			pass : document.querySelector('#password').value
		};
		var data = 'dummy=1';
		var k = null;
		for (k in dataObj) {
			data += '&' + k + '=' + encodeURIComponent(dataObj[k]);
		}
		Req('POST', 'Messages?action=insertMessage',{'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}, data, function(xhr) {
			var response = JSON.parse(xhr.responseText);
			if (response.resultCode === "OK") {
				var wl = window.location;
				var newUrl = wl.protocol + "//" + wl.host + wl.pathname.substr(0, wl.pathname.lastIndexOf("/"));
				window.location.assign(newUrl + response.content);
			} else if (response.resultCode === "MSG") {
				alert(response.content);
			} else if (response.resultCode === "ERROR") {
				document.body.innerHTML = response.content;
			}
			sendInProgress = false;
		});
	});
};