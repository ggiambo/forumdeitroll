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