
function hideSidebar() {
	var sidebar = $("#sidebar");
	sidebar.hide("slow", function() {
		var sidebarSmall = $("#sidebarSmall");
		sidebarSmall.show("slow");
		jQuery.ajax("Messages?action=updateSidebarStatus&sidebarStatus=hide");
	});
}

function showSidebar() {
	var sidebarSmall = $("#sidebarSmall");
	sidebarSmall.hide("slow", function() {
		var sidebar = $("#sidebar");
		sidebar.show("slow");
		jQuery.ajax("Messages?action=updateSidebarStatus&sidebarStatus=show");
	});
}

function showReplyDiv(type, parentId) {
	refreshable--;
	$("#buttons_" + parentId).hide();
	$("#firma_" + parentId).hide();
	$("body").css("cursor", "progress");
	$.get("Messages?action=showReplyDiv&type=" + type + "&parentId=" + parentId,
		function(data) {
			$("#msg" + parentId).removeClass("msgOptMaxHeight");
			$("#msg" + parentId).append(data);
			$("#reply_" + parentId + " :input[name='text']").focus();
			$("body").css("cursor", "auto");
	});
}

function closeReplyDiv(parentId) {
	$("#reply_" + parentId).remove();
	$("#buttons_" + parentId).show();
	$("#firma_" + parentId).show();
	refreshable++;
}

function preview(parentId) {
	// post data
	var data = { parentId: parentId };
	$("#reply_" + parentId + " :input").each(function() {
		var val = $(this);
		data[val.attr("name")] = val.val();
	});
	// preview message
	jQuery.ajax({
		type: "POST",
		url: "Messages?action=getMessagePreview",
		data: data,
		success: function(data) {
			if (data.resultCode == "OK") {
				// nascondi textArea
				var textArea = $("#reply_" + parentId + " :input[name='text']");
				textArea.hide();
				// mostra previewDiv
				var previewDiv = $("#preview_" + parentId);
				previewDiv.height(textArea.height());
				previewDiv.html(data.content);
				previewDiv.show();
				// swap bottoni
				$("#reply_" + parentId + " :input[name='preview']").hide();
				$("#reply_" + parentId + " :input[name='edit']").show();
				$("#reply_" + parentId + " .quote-container").on('click', openQuotes);
			} else if (data.resultCode == "MSG") {
				alert(data.content);
			} else if (data.resultCode == "ERROR") {
				$("html").html(data.content);
			}
		},
		beforeSend : function(jqXhr, settings) {
			jqXhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
		},
		dataType: "json"
	});

}

function edit(parentId) {
	// nascondi preview, mostra textArea, swap bottoni
	$("#preview_" + parentId).hide();
	$("#reply_" + parentId + " :input[name='text']").show();
	$("#reply_" + parentId + " :input[name='preview']").show();
	$("#reply_" + parentId + " :input[name='edit']").hide();
}

function send(parentId) {
	$("#reply_" + parentId + " :input[type='button']").attr("disabled", "disabled");
	$("body").css("cursor", "progress");
	var data = { parentId: parentId, submitLocation:  "" + window.location };
	$("#reply_" + parentId + " :input").each(function() {
		var val = $(this);
		data[val.attr("name")] = val.val();
	});
	// post messagge
	profiler(function(profileData) {
		data.jsonProfileData = JSON.stringify(profileData);
		jQuery.ajax({
			type: "POST",
			url: "Messages?action=insertMessage",
			data: data,
			success: function(data) {
				if (data.resultCode == "OK") {
					var wl = window.location;
					var newUrl = wl.protocol + "//" + wl.host + wl.pathname.substr(0, wl.pathname.lastIndexOf("/"));
					window.location.assign(newUrl + data.content);
				} else if (data.resultCode == "MSG") {
					alert(data.content);
				} else if (data.resultCode == "BAN") {
					alert(data.content);
					$('#reply_' + parentId).remove();
					$('#msg' + parentId).append("<a href='UserProfiler?action=snoop'>Sei stato bannato per errore?</a>");
				} else if (data.resultCode == "ERROR") {
					$("html").html(data.content);
				}
				$("#reply_" + parentId + " :input[type='button']").removeAttr("disabled");
				$("body").css("cursor", "auto");
			},
			beforeSend : function(jqXhr, settings) {
				jqXhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
			},
			dataType: "json"
		});
	});
}

function insert(openTag, closeTag, parentId) {
	return insertIntoTextArea(openTag, closeTag, $("#reply_" + parentId + " :input[name='text']").get(0));
}

function insertIntoTextArea(openTag, closeTag, textArea) {
	if (textArea.createTextRange) {
		textArea.focus(textArea.caretPos);
		textArea.caretPos = document.selection.createRange().duplicate();
		if (textArea.caretPos.text.length > 0) {
			var sel = textArea.caretPos.text;
			var fin = '';
			while (sel.substring(sel.length-1, sel.length) == ' ') {
				sel = sel.substring(0, sel.length-1);
				fin += ' ';
			}
			textArea.caretPos.text = sel + fin + openTag + closeTag;
		} else {
			textArea.caretPos.text = openTag + closeTag;
		}
	} else 	{
		//MOZILLA/NETSCAPE support
		if (textArea.selectionStart || textArea.selectionStart == "0") {
			var startPos = textArea.selectionStart;
			var endPos = textArea.selectionEnd;

			if (startPos != endPos) {
				if(openTag.length > 0) {
					textArea.value = textArea.value.substring(0, startPos) + openTag + textArea.value.substring(startPos, endPos) + closeTag + textArea.value.substring(endPos, textArea.value.length);
				} else {
					textArea.value = textArea.value.substring(0, endPos) + closeTag + textArea.value.substring(endPos, textArea.value.length);
				}
			} else {
				textArea.value = textArea.value.substring(0, startPos) + openTag + textArea.value.substring(endPos, textArea.value.length);
				textArea.selectionStart = (textArea.value.substring(0, startPos) + openTag).length;
				textArea.selectionEnd = (textArea.value.substring(0, startPos) + openTag).length;
			}
		} else {
			textArea.value += openTag + closeTag;
		}
	}
}

var urlInput = function(parentId) {
	var element = $("#reply_" + parentId + " :input[name='text']").get(0);
	var url = prompt('Inserisci l\'url','');
	if (!url) {
		return;
	}
	var desc = prompt('Inserisci il testo da visualizzare','');
	if (desc == null) {
		return;
	}
	var html = '';
	if (desc === '') {
		html = '[url]' + url + '[/url]';
	} else {
		html = '[url=' + url + ']' + desc + '[/url]';
	}
	//IE
	if (document.selection) {
		element.focus();
		var sel  = doucment.selection.createRange();
		sel.moveStart('character', -element.value.length);
		var pos = sel.text.length;
		element.value = element.value.substring(0, pos) + html + element.value.substring(pos, element.value.length);
	}
	//FF
	else if (element.selectionStart || element.selectionStart == '0') {
		var pos = element.selectionStart;
		element.value = element.value.substring(0, pos) + html + element.value.substring(pos, element.value.length);
	}
};

var checkRefresh = function(callback) {
	jQuery.get('JSon?action=getLastId', function(data) {
		if (lastId != data.content.id) {
			callback();
		};
	});
};

var openQuotes = function(evtObj) {
	$(this).removeClass('quote-container');
	$(this).addClass('quote-closer');
	$(this).off('click');
	$(this).on('click', function(evtObj) {
		$(this).removeClass('quote-closer');
		$(this).addClass('quote-container');
		$(this).off('click');
		$(this).on('click', openQuotes);
	});
}

jQuery("document").ready(function(){
	// Eseguita quando il documento è pronto
	jQuery(".messagesBox").resizable({handles: 'e, w'}); // Solo lato EST e OVEST
	// Syntax Highlighter dei messaggi
	SyntaxHighlighter.autoloader(
			  'applescript            js/sh/shBrushAppleScript.js',
			  'actionscript3 as3      js/sh/shBrushAS3.js',
			  'bash shell             js/sh/shBrushBash.js',
			  'coldfusion cf          js/sh/shBrushColdFusion.js',
			  'cpp c                  js/sh/shBrushCpp.js',
			  'c# c-sharp csharp      js/sh/shBrushCSharp.js',
			  'css                    js/sh/shBrushCss.js',
			  'delphi pascal          js/sh/shBrushDelphi.js',
			  'diff patch pas         js/sh/shBrushDiff.js',
			  'erl erlang             js/sh/shBrushErlang.js',
			  'groovy                 js/sh/shBrushGroovy.js',
			  'java                   js/sh/shBrushJava.js',
			  'jfx javafx             js/sh/shBrushJavaFX.js',
			  'js jscript javascript  js/sh/shBrushJScript.js',
			  'perl pl                js/sh/shBrushPerl.js',
			  'php                    js/sh/shBrushPhp.js',
			  'text plain             js/sh/shBrushPlain.js',
			  'py python              js/sh/shBrushPython.js',
			  'ruby rails ror rb      js/sh/shBrushRuby.js',
			  'sass scss              js/sh/shBrushSass.js',
			  'scala                  js/sh/shBrushScala.js',
			  'sql                    js/sh/shBrushSql.js',
			  'vb vbnet               js/sh/shBrushVb.js',
			  'xml xhtml xslt html    js/sh/shBrushXml.js'
			);
	SyntaxHighlighter.defaults['toolbar'] = false;
	SyntaxHighlighter.all();

	// collassa quotes
	$('.quote-container').on('click', openQuotes);
	
	$("div.buttonBarButton").mouseenter(function() {
		var buttonBarButton = $(this);
		buttonBarButton.css("box-shadow", "0 0 8px #0A78FF");
		buttonBarButton.css("background", "white");
		var buttonBarLink = buttonBarButton.find("a.buttonBarLink");
		buttonBarLink.css("color", "#007BDF");
		var buttonBarImg = buttonBarLink.find("span.buttonBarImg");
		buttonBarImg.css('background-image', 'url("css/images/ui-icons_228ef1_256x240.png")');
	}).mouseleave(function() {
		var buttonBarButton = $(this);
		buttonBarButton.css("box-shadow", "none");
		buttonBarButton.css("background", "#F6F6F6");
		var buttonBarLink = buttonBarButton.find("a.buttonBarLink");
		buttonBarLink.css("color", "blue");
		var buttonBarImg = buttonBarLink.find("span.buttonBarImg");
		buttonBarImg.css('background-image', 'url("css/images/ui-icons_222222_256x240.png")');
	});
	
	$("div.buttonBarButtonAdmin").mouseenter(function() {
		var buttonBarButton = $(this);
		buttonBarButton.css("box-shadow", "0 0 8px orange");
		buttonBarButton.css("background", "white");
		var buttonBarLink = buttonBarButton.find("a.buttonBarLink");
		buttonBarLink.css("color", "#007BDF");
		var buttonBarImgAdmin = buttonBarLink.find("span.buttonBarImgAdmin");
		buttonBarImgAdmin.css('background-image', 'url("css/images/ui-icons_ffd27a_256x240.png")');
	}).mouseleave(function() {
		var buttonBarButton = $(this);
		//buttonBarButton.css("border", "1px solid #F6F6F6");
		buttonBarButton.css("box-shadow", "none");
		buttonBarButton.css("background", "#F6F6F6");
		var buttonBarLink = buttonBarButton.find("a.buttonBarLink");
		buttonBarLink.css("color", "blue");
		var buttonBarImgAdmin = buttonBarLink.find("span.buttonBarImgAdmin");
		buttonBarImgAdmin.css('background-image', 'url("css/images/ui-icons_ef8c08_256x240.png")');
	});
	
	// refresh ogni 2 minuti
	setInterval(function() {
		if (refreshable > 0) {
			checkRefresh(function() {
				location.reload();	
			});
		}
	}, 120000);
	
});

function setFloatingHeader() {
	// header svulazzante
	var nav = $('#nav');
	var start = $(nav).offset().top;
	$.event.add(window, "scroll", function() {
		var p = $(window).scrollTop();
		$(nav).css("position",( p > start) ? "fixed" : "static");
		$(nav).css("top",( p > start) ? "0px" : "");
	});
	nav.css("border-bottom", "2px solid #93998E");
}

// tasto 'j' per saltare al prossimo messaggio, 'k' per quello precedente
$("body").on("keypress", function(e) {
	var target = $(e.target);
	if (!target.is("input") && !target.is("textarea")) {
		if (e.charCode == 106) {
			var scrollToMsgIndex = $.data(document.body, "scrollToMsgIndex");
			if (scrollToMsgIndex == undefined) {
				scrollToMsgIndex = -1;
			}
			$.data(document.body, "scrollToMsgIndex", ++scrollToMsgIndex);
			$("html, body").animate({
				scrollTop: $('div[id^="msg"]').eq(scrollToMsgIndex).offset().top
			}, 200);
		}
		if (e.charCode == 107) {
			var scrollToMsgIndex = $.data(document.body, "scrollToMsgIndex");
			if (scrollToMsgIndex == undefined) {
				scrollToMsgIndex = $('div[id^="msg"]').length;
			}
			$.data(document.body, "scrollToMsgIndex", --scrollToMsgIndex);
			$("html, body").animate({
				scrollTop: $('div[id^="msg"]').eq(scrollToMsgIndex).offset().top
			}, 200);
		}
	}
});

var YTCounter = 0;
var YTTitleCache = {};

var YTCreateScriptTag = function(elem, youcode) {
	if (YTTitleCache[youcode]) {
		elem.appendChild(document.createTextNode(YTTitleCache[youcode]));
		elem.onmouseover = null;
		return;
	}
	YTCounter++;
	var myId = YTCounter;
	elem.id = 'yt_' + myId;
	elem.onmouseover = null;
	window['YTCallback_' + myId] = function(ytResponse) {
		var title = ytResponse.entry.title.$t;
		window.YTTitleCache[youcode] = title;
		elem.appendChild(document.createTextNode(title));
	};
	var script = document.createElement('script');
	script.type = 'text/javascript';
	script.src = '//gdata.youtube.com/feeds/api/videos/' +
		youcode + '?v=2&alt=json-in-script&callback=YTCallback_' + myId;
	document.body.appendChild(script);
	
};

function pedonizeThreadTree(msgId) {
	if (confirm("Spostare il messaggio e relativa ramificazione in procura ?")) {
		window.location.assign("Messages?action=pedonizeThreadTree&rootMessageId=" + msgId + "&token=" + token);
	}
}

var getRandomQuote = function() {
	jQuery.ajax("Messages?action=getRandomQuote", {
		success : function(data, textStatus, jqXHR) {
			document.getElementById('quoteForum').innerHTML = data.split(/\n/)[0];
			document.getElementById('quoteForum').title = data.split(/\n/)[1];
		}
	});
};

function searchById(event) {
	if (event.which == 13) {
		var msgId = parseFloat($(event.target).val());
		if (isNaN(msgId)) {
		  alert($(event.target).val() + " non è un id valido");
		  return;
		}
		var wl = window.location;
		var newUrl = wl.protocol + "//" + wl.host + wl.pathname.substr(0, wl.pathname.lastIndexOf("/"));
		newUrl += "/Messages?action=getById&msgId=" + msgId;
		window.location.assign(newUrl);
	}
}

function hideMessage(msgId) {
	if (confirm("Vuoi rendere questo messaggio invisibile ?")) {
		window.location.assign("Messages?action=hideMessage&msgId=" + msgId + "&token=" + token);
	}
}

function restoreHiddenMessage(msgId) {
	if (confirm("Vuoi di nuovo rendere questo messaggio visibile ?")) {
		window.location.assign("Messages?action=restoreHiddenMessage&msgId=" + msgId + "&token=" + token);
	}
}

function showHIddenMessage(msgId) {
	$("#msg" + msgId).removeClass("msgInvisible");
	$("#msg" + msgId).addClass("msgVisible");
	$("#msgWarning" + msgId).hide();
}

function openNotifyInput(msgId) {
	refreshable--;
	var span = $("#notify_" + msgId);
	span.children("a").hide();
	var input = span.children("input");
	input.show();
	input.autocomplete({
		// minimo 2 caratter
		minLength: 2,
		// funzione per cercare i nomi
		source: function(req, add) {
			// chiamata ajax
			$.getJSON("Pvt?action=searchAuthorAjax", {searchString: req['term']} , function(data) {
				// array con i risultati
				var suggestions = [];
                $.each(data.content, function(i, val) {
					suggestions.push(val);
				});
				add(suggestions);
			});
		},
		// funzione per click sul nome dalla lista
		select: function(e, ui) {
			var recipient = ui.item.value;
			// post della notifica
			jQuery.ajax({
				type: "POST",
				url: "User?action=notifyUser",
				data: {toNick:recipient, msgId:msgId},
				success: function(data) {
					if (data.resultCode == "OK") {
						alert(recipient + " notificato !");
						span.children("a").show();
						input.hide();
						input.val("");
						refreshable++;
					} else if (data.resultCode == "ERROR") {
						alert(data.content);
					}
				},
				beforeSend : function(jqXhr, settings) {
					jqXhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
				},
				dataType: "json"
			});
        },
        close: function() {
        	// cancella il contenuto del campo input
        	$("#recipients").val('');
        },
        open: function() {
        	$('.ui-autocomplete').css('width', '10em').css('font-size', '.8em');
        }
        
	});
	
}

function showHideAdminButtons(msgId) {
	var messagesButtonBar = $("#buttons_" + msgId + " > .messagesButtonBar");
	messagesButtonBar.hide("slide", { direction: "right" }, 500, function() {
		var modButtonsVisible = messagesButtonBar.data("modButtonsVisible") ? true : false;
		if (modButtonsVisible) {
			messagesButtonBar.find("div.buttonBarButton").show();
			messagesButtonBar.find("div.buttonBarButtonAdmin").hide();
			$("#OpenMod_" + msgId).show();
		} else {
			messagesButtonBar.find("div.buttonBarButton").hide();
			messagesButtonBar.find("div.buttonBarButtonAdmin").show();
		}
		messagesButtonBar.data("modButtonsVisible", !modButtonsVisible);
		messagesButtonBar.show("slide", { direction: "right" }, 500);
	});
}

function createIrcbox() {
	if ($('#ircbox').length == 0) {
		$('#nav').after('<div id="ircbox" style="display:none"></div>');
		setTimeout(function() {$('#ircbox').show('slow');}, 1);
	}
}

function ircbox() {
	createIrcbox();
	$.get('Irc', function(data) {
		var lines = data.split(/[\r\n]/).filter(function(l) {return l !== '';});
		if (lines.length == 1 && lines[0].match(/nessuna informazione disponibile/)) {
			$('#ircbox').html('<a href=\"http://webchat.freenode.net/?channels=%23%23fdt\">Vieni a trovarci!</a>');
		} else {
			var topic = lines[0];
			var users = lines[1];
			var mtime = lines[2];
			var html = '<a href=\"http://webchat.freenode.net/?channels=%23%23fdt\">' + topic + "</a><br>";
			html += "<small>" + users + "<br>" + mtime + "</small>";
			$('#ircbox').html(html);
		}
	});
}

function like(msgId, like) {
	jQuery.ajax({
		type: "GET",
		dataType: "json",
		url: "Messages?action=like",
		data: { msgId: msgId, token: token, like: like},
		success: function(data) {
			if (data.resultCode == "OK") {
				var strval = $('#msg' + msgId + '_ranking').html();
				var oldval = parseInt(strval);
				if (isNaN(oldval)) {
					$('#msg' + msgId + '_ranking').html("1");
				} else {
					var newval = oldval + data.voteValue;
					$('#msg' + msgId + '_ranking').html(""+newval);
				}
			} else if (data.resultCode == "MSG") {
				alert(data.content);
			} else if (data.resultCode == "ERROR") {
				$("html").html(data.content);
			}
		}
	});
}



//var endpointSearch = "Misc?action=searchAjax";
var endpointSearch = "/motorino/search?dummy=true";

var templateSearch = null;
var lockSearch = null;

function infiniteScroll(page) {
	return function() {
		try {
			var wst = $(window).scrollTop();
			var dh = $(document).height();
			var wh = $(window).height();
			if ((dh - wh) - wst < 500) {
				searchAjax(page);
				$(window).off('scroll');
			}
		} catch (e) {
			alert("infiniteScroll: "+e.message);
		}
	};
}
var proottOpts = {};

function runTemplate(data) {
	data.opts = proottOpts;
	var html = templateSearch(data);
	if (data.currentPage == 0) {
		document.getElementById('main').innerHTML = html;
	} else {
		document.getElementById('main').innerHTML += html;
	}
	$(window).on("scroll", infiniteScroll(data.nextPage));
	lockSearch = null;
}

function searchAjax(page) {
	refreshable = 0;
	if (page && lockSearch != null) return false;
	lockSearch = 1;
	try {
		if ($('#pager').length > 0) {
			$('#pager').remove();
		}
		if ($('#footer').length > 0) {
			$('#footer').remove();
		}
		var q = document.forms.sidebarSearchForm.search.value;
		var sort = document.forms.sidebarSearchForm.sort.value;
		if (sort == 'date') {
			sort = 'rdate';
		} else if (sort == 'rdate') {
			sort = 'date';
		}
		if (!page) {
			page = 0;
			document.getElementById("main").innerHTML = "Ricerca avviata...";
		}
		var query =
			endpointSearch +
			"&q=" + encodeURIComponent(q) +
			"&sort=" + encodeURIComponent(sort) +
			"&p=" + page;
		proottOpts.query = q;
		proottOpts.sort = sort;
		$.get(query, function(data) {
			if ($('#loading').length > 0) {
				$('#loading').remove();
			}
			if (data.results.length == 0 && page != 0) {
				lock = 1;
				return;
			}
			data.currentPage = page;
			data.nextPage = page + 1;
			try {
				if (!templateSearch) {
					$.get('templates/messages.tmpl?v=' + Math.random(), function(tmplSource) {
						try {
							templateSearch = _.template(tmplSource);
							runTemplate(data);
						} catch (e) {
							alert("searchAjax1: "+e.message);
						}
					});
				} else {
					runTemplate(data);
				}
			} catch (e) {
				alert("searchAjax2: "+e.message);
			}
		});
	} catch (e) {
		alert("searchAjax3: "+e.message);
	}
	return false;
}

var proottTmpl = null;

var proottBindings = {
	'asStart click' : function() {
		proottOpts.query = document.getElementById('asQ').value;
		document.forms.sidebarSearchForm.search.value = proottOpts.query;
		searchAjax();
	},
	'asStart2 click' : function() {
		proottOpts.query = document.getElementById('asQ').value;
		document.forms.sidebarSearchForm.search.value = proottOpts.query;
		searchAjax();
	},
	'asShowImages click' : function() {
		proottOpts.smegma = document.getElementById('asShowImages').checked;
	},
	'asQ keypress' : function(evt) {
		if (evt.which === 13) {
			proottOpts.query = document.getElementById('asQ').value;
			document.forms.sidebarSearchForm.search.value = proottOpts.query;
			searchAjax();
		}
	}
};

function addAdvancedSearchBindings() {
	for (var key in proottBindings) {
		var fun = proottBindings[key];
		var id = key.split(" ")[0];
		var event = key.split(" ")[1];
		$('#' + id).on(event, fun);
	}
}

function showAdvancedSearch() {
	if (proottTmpl) {
		try {
			document.getElementById('main').innerHTML = proottTmpl(proottOpts);
			addAdvancedSearchBindings();
		} catch (e) {
			alert("showAdvancedSearch1: "+e.message);
		}
	} else {
		$.get('templates/proott.tmpl?v=' + Math.random(), function(tmplSource) {
			try {
				proottTmpl = _.template(tmplSource);
				document.getElementById('main').innerHTML = proottTmpl(proottOpts);
				addAdvancedSearchBindings();
			} catch (e) {
				alert("showAdvancedSearch2: "+e.message);
			}
		});
	}
}