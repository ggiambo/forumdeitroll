
var MAX_MESSAGE_NUMBER;

var send = function(event, element) {
	if (event.which !== 13) return;
	if (element.value === '') return;
	var content = element.value;
	element.disabled = true;
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
				refresh();
			}
		});
	});
};

var refresh = function() {
	var lastCheck = localStorage['ciattina.lastCheck'];
	$.ajax({
		method : 'POST',
		url : 'Minichat',
		data : 'action=refresh&lastCheck=' + lastCheck,
		success : function(response) {
			var messages = response.messages;
			localStorage['ciattina.lastCheck'] = response.tstamp;
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
				row.insertCell(1).appendChild(document.createTextNode(message.author));
				row.cells[1].className = 'who';
				row.insertCell(2).innerHTML = message.content;
			}
		}
	});
};

function init(currentTimeMillis, maxMessageNumber) {
	MAX_MESSAGE_NUMBER = maxMessageNumber;
	localStorage['ciattina.lastCheck'] = currentTimeMillis;
	setInterval(refresh, 30000);
}
