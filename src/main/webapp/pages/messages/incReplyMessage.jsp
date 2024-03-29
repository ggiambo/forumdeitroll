<%@page import="com.forumdeitroll.markup.Emoticons"%>
<%@page import="com.forumdeitroll.servlets.Messages"%>
<%@page import="java.util.Enumeration"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<c:set var="maxMessageLength">
	<%=Messages.MAX_MESSAGE_LENGTH%>
</c:set>

<fdt:delayedScript dump="false">
	$(document).ready(function() {
		$("#reply_${message.parentId} :input[name='pass']").keydown(function(e) {
			if (e.which == 13) {
				send(${message.parentId});
			}
		});
		jscolor.init();
		// update numero caratteri, ogni secondo
		messageId = ${message.id};
		limit = ${maxMessageLength};

		var intervalID = setInterval(function() {
			// n.b. non furmigate, il controllo della lunghezza c'� anche lato server
			try {
				var counter = $('#counter_' + messageId);
				var textarea = $('#text_' + messageId);
				if (textarea.val().length > limit) {
					textarea.val(textarea.val().substring(0, limit));
					var container = counter.parent();
					container.css('backgroundColor', 'red');
					setTimeout(function() {
						container.css("backgroundColor", '');
					}, 200);
				}
				counter.html(limit - textarea.val().length);
			} catch (e) {
				clearInterval(intervalID);
			}
		}, 1000);
	});

	function showEmotiboxClassic() {
		var emotiboxes = $("#reply_${message.parentId} .emotibox .emo");
		$(emotiboxes[0]).show();
		$(emotiboxes[1]).hide();
		$(emotiboxes[2]).hide();
		var tabs = $("#reply_${message.parentId} ul li");
		$(tabs[0]).addClass("selectedTab")
		$(tabs[1]).removeClass("selectedTab")
		$(tabs[2]).removeClass("selectedTab")
	}

	function showEmotiboxExtended() {
		var emotiboxes = $("#reply_${message.parentId} .emotibox .emo");
		$(emotiboxes[0]).hide();
		$(emotiboxes[1]).show();
		$(emotiboxes[2]).hide();
		var tabs = $("#reply_${message.parentId} ul li");
		$(tabs[0]).removeClass("selectedTab")
		$(tabs[1]).addClass("selectedTab")
		$(tabs[2]).removeClass("selectedTab")
	}

	function showSnippets() {
		var emotiboxes = $("#reply_${message.parentId} .emotibox .emo");
		$(emotiboxes[0]).hide();
		$(emotiboxes[1]).hide();
		$(emotiboxes[2]).show();
		var tabs = $("#reply_${message.parentId} ul li");
		$(tabs[0]).removeClass("selectedTab")
		$(tabs[1]).removeClass("selectedTab")
		$(tabs[2]).addClass("selectedTab")
	}

</fdt:delayedScript>

<fdt:delayedScript dump="false">
	var token = "${anti_xss_token}";
</fdt:delayedScript>

<c:set var="isReply" value="${!isEdit && message.parentId > 0}"/>
<c:set var="isNewThread" value="${!isEdit && message.id == -1 && message.parentId == -1}"/>
<c:set var="isNewMessage" value="${!isEdit && message.id == -1}"/>
<c:set var="isEdit" value="${!empty isEdit && isEdit}"/>

<div style="clear: both"></div>
<div id="reply_${message.parentId}" class="msgReply">

	<c:if test="${isReply}">
		<c:set var="class" value="border:1px solid black; padding:2px; margin:2px;"/>
		<a style="float: right;" onClick="closeReplyDiv('${message.parentId}')"><img src="images/close.jpeg"></a>
	</c:if>

	<ul class="tabs">
		<li class="selectedTab" onClick="showEmotiboxClassic(); return false;"><a href="#">Serie classica</a></li>
		<li><a href="#" onClick="showEmotiboxExtended(); return false;">Serie estesa</a></li>
		<li><a href="#" onClick="showSnippets(); return false;">Snippets</a></li>
	</ul>

	<div class="emotibox">
		<div class="emo">
			<c:forEach items="<%=Emoticons.getInstance().serieClassica%>" var="emo" varStatus="index">
				<img onmousedown="insert('${emo.safeSequence}', '', '${message.parentId}')" title="${emo.altText}" src="images/emo/${emo.imgName}.gif" style="cursor: pointer;"/>
				<c:if test="${index.count % 13 == 0}"><br/></c:if>
			</c:forEach>
		</div>
		<div style="display:none" class="emo">
			<c:forEach items="<%=Emoticons.getInstance().serieEstesa%>" var="emo" varStatus="index">
				<img onmousedown="insert('${emo.sequence}', '', '${message.parentId}')" title="${emo.altText}" src="images/emoextended/${emo.imgName}.gif" style="cursor: pointer;"/>
				<c:if test="${index.count % 13 == 0}"><br/></c:if>
			</c:forEach>
		</div>
		<div style="display: none" class="emo">
		<br>
			<% for (com.forumdeitroll.markup.Snippet snippet : com.forumdeitroll.markup.Snippet.list) { %>
				<a href="#" style='text-decoration: none; color: black' onclick="insert('<%=snippet.sequence%>','','${message.parentId}'); return false;"><%=snippet.htmlReplacement%></a>
			<% } %>
		</div>
		<div style="margin:3px 0px 3px 0px ">
			<span onmousedown="insert('<b>', '</b>', '${message.parentId}')" class="msgButton btnBold" title="Grassetto (ma meno di Lich)">B</span>&nbsp;
			<span onmousedown="insert('<i>', '</i>', '${message.parentId}')" class="msgButton btnItalic" title="Corsivo">I</span>&nbsp;
			<span onmousedown="insert('<u>', '</u>', '${message.parentId}')" class="msgButton btnUnderline" title="Sottolineato">U</span>&nbsp;
			<span onmousedown="insert('<s>', '</s>', '${message.parentId}')" class="msgButton btnStrike" title="Barrato">S</span>&nbsp;
			<span class="fakeLink" onclick="insert('[img]', '[/img]', '${message.parentId}')">[immagine]</span>&nbsp;
			<span class="fakeLink" onclick="insert('[code]', '[/code]', '${message.parentId}')">[codice]</span>&nbsp;
			<span class="fakeLink" onclick="insert('[yt]', '[/yt]', '${message.parentId}')">[youtube]</span>&nbsp;
			<span class="fakeLink" onclick="insert('[video]', '[/video]', '${message.parentId}')">[video]</span>&nbsp;
			<span class="fakeLink" onclick="insert('[spoiler]', '[/spoiler]', '${message.parentId}')">[spoiler]</span>&nbsp;
			<span class="fakeLink" onclick="urlInput('${message.parentId}')">[url]</span>&nbsp;
			<span class="fakeLink" onclick="insert('[color #' + $('.color').val() + ']', '[/color]', '${message.parentId}')">[color]</span>&nbsp;
			<input type='text' class='color' value='66ff00' style='width:40px; font-size: 10px'>
		</div>
	</div>

	<c:if test="${isNewThread || isEdit || isReply}">
		<label for="subject">Oggetto:</label><br />
		<input tabindex="1" name="subject" id="subject" maxlength="<%=Messages.MAX_SUBJECT_LENGTH %>" size="<%=Messages.MAX_SUBJECT_LENGTH %>" class="msgReplyObj" value="<c:out value="${message.subject}" escapeXml="true"/>"/>
	</c:if>
	<c:if test="${not empty message.forum}">
		Forum <i>${message.forum}</i>
	</c:if>

	<%-- input area --%>
	<textarea tabindex="1" name="text" tabindex="2" rows="20" class="msgReplyTxt" id="text_${message.id}">${message.text}</textarea>

	<%-- preview area --%>
	<div id="preview_${message.parentId}" class="msgReplyTxt"></div>

	<input type="hidden" name="forum" value="${message.forum }"/>
	<input type="hidden" name="id" value="${message.id }"/>
	<div class="msgAnonBox">
		<div class='counter-container'>
			<div id='counter_${message.id}'>${maxMessageLength - fn:length(message.text)}</div>
		</div>
		<label for="nick">Nome:&nbsp;</label>
		<input tabindex="2" name="nick" id="nick" size="10" value="${loggedUser.nick }"/>&nbsp;&nbsp;
		<label for="password">Password:&nbsp;</label>
		<input tabindex="3" type="password" id="password" name="pass" size="10"/>
        <div class="msgCaptcha">
		    <div id="captcha_${message.parentId}" class="g-recaptcha" data-sitekey="${captchakey}"></div>
        </div>
	</div>
	<input style="float:left;font-size: 90%;" tabindex="5" type="button" name="preview" value="Preview" onClick="preview(${message.parentId})"/>&nbsp;
	<input style="display:none;float:left;font-size: 90%;" tabindex="5" type="button" name="edit" value="Edit" onClick="edit(${message.parentId})"/>&nbsp;
	<a href="Misc?action=getDisclaimer" style="font-size:80%;">disclaimer &amp; privacy policy</a>
	<c:if test="${warnTorUser == null}">
		<input tabindex="5" type="button" value="Invia" onClick="send(${message.parentId})" class="msgSendButton" />
	</c:if>
	<c:if test="${warnTorUser != null}">
		<input tabindex="5" type="button" value="BLOCCATO" onClick="alert('Post con Tor temporaneamente inibito.')" class="msgSendButton" style="background-color: red" />
	</c:if>
	<div style="clear: both;"></div>
</div>
<c:if test="${param.action != 'newMessage'}">
	<fdt:delayedScript dump="true">
	questa jsp non è inclusa in altre jsp, quindi gli script delayed
	vanno piazzati qua
	</fdt:delayedScript>
</c:if>
