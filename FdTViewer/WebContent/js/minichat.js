var MAX_MESSAGE_NUMBER;

var send = function(event, element) {
	if (event.which !== 13) return;
	if (element.value === '') return;
	var content = element.value;
	element.disabled = true;
	document.getElementById('btnInvia').disabled = true;
	profiler(function(profileData) {
		$.ajax({
			method : 'POST',
			url : 'Minichat',
			data : 'action=send&content=' +
					encodeURIComponent(content) +
					'&jsonProfileData=' +
					encodeURIComponent(JSON.stringify(profileData)),
			success : function() {
				element.disabled = false;
				element.value = '';
				document.getElementById('btnInvia').disabled = false;
				element.focus();
				refresh();
			}
		});
	});
};

var sendBtn = function() {
	send({which:13}, document.getElementById("content"));
};

var set = function(key, value) {
	if (typeof localStorage === 'undefined') {
		window.ciattina_lastCheck = value;
	} else {
		localStorage[key] = value;
	}
};

var get = function(key) {
	if (typeof localStorage === 'undefined') {
		return window.ciattina_lastCheck;
	} else {
		return localStorage[key];
	}
};

var refresh = function() {
	var lastCheck = get('ciattina.lastCheck');
	$.ajax({
		method : 'POST',
		url : 'Minichat',
		data : 'action=refresh&lastCheck=' + lastCheck,
		success : function(response) {
			var messages = response.messages;
			set('ciattina.lastCheck',response.tstamp);
			var table = document.getElementById('scrollback');
			for (var idx in messages) {
				var message = messages[idx];
				if (table.rows.length == MAX_MESSAGE_NUMBER) {
					table.deleteRow(0);
				}
				var row = table.insertRow(-1);
				row.insertCell(0).appendChild(document.createTextNode(message.when));
				row.cells[0].className = 'when';
				if (!message.author) {
					message.author = '';
				}
				if (message.irc) {
					var text = document.createTextNode(message.author);
					var link = document.createElement('A');
					link.href = 'http://webchat.freenode.net/?channels=%23%23fdt';
					link.appendChild(text);
					row.insertCell(1).appendChild(link);
				} else {
					row.insertCell(1).appendChild(document.createTextNode(message.author));
				}
				row.cells[1].className = 'who';
				row.insertCell(2).innerHTML = message.content;
			}
		}
	});
};

function init(currentTimeMillis, maxMessageNumber) {
	MAX_MESSAGE_NUMBER = maxMessageNumber;
	set('ciattina.lastCheck', currentTimeMillis);
	setInterval(refresh, 30000);
}
