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