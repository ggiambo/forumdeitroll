
$(document).ready(function() {
	$('.ip-container').click(function() {
		geodata(this, $(this).text());
	}).mouseenter(function() {
		$(this).css("color", "#007BDF").css("cursor", "pointer");
	}).mouseleave(function() {
		$(this).css("color", "black").css("cursor", "default");
	});
	
	$('#geoIpContainer').click(function() {
		$(this).html('').hide();
	}).mouseleave(function() {
		$(this).html('').hide();
	});
});

function geodata(elem, ip) {
	$("body").css("cursor", "progress");
	jQuery.getJSON("Misc?action=freegeoip&ip=" + ip + "&callback=?",
		function(result) {
			var box = $('#geoIpContainer');
			var position = $(elem).offset();
			var left = position.left + $(elem).width();
			var top = position.top - $(elem).height();
			box.css({left:left, top:top});
			var html = '';
			$.each(result, function() {
				html += "<b>" + arguments[0] + "</b>: " + arguments[1] + "<br/>";
			});
			box.html(html);
			box.slideDown();
			$("body").css("cursor", "auto");
		}
	);
}

var blink = function(elem) {
	elem.style.backgroundColor = 'red';
	setTimeout(function() {
		elem.style.backgroundColor = 'transparent';
	}, 300);
};