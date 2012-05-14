<%@page import="java.util.Map"%>
<%@page import="com.acmetoy.ravanator.fdt.servlets.Messages"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<fdt:delayedScript dump="false">
	$(document).ready(function() {
		$("#reply_${message.parentId} :input[name='pass']").keydown(function(e) {
			if (e.which == 13) {
				send(${message.parentId});
			}
		});
		$("#reply_${message.parentId} :input[name='captcha']").keydown(function(e) {
			if (e.which == 13) {
				send(${message.parentId});
			}
		});
		jscolor.init()
	});
</fdt:delayedScript>

<c:set var="isReply" value="${!isEdit && message.parentId > 0}"/>
<c:set var="isNewThread" value="${!isEdit && message.id == -1 && message.parentId == -1}"/>
<c:set var="isNewMessage" value="${!isEdit && message.id == -1}"/>
<c:set var="isEdit" value="${!empty isEdit && isEdit}"/>

<div style="clear: both"></div>
<div id="reply_${message.parentId}" class="msgReply">

	<c:if test="${isReply}">
		<c:set var="class" value="border:1px solid black; padding:2px; margin:2px;"/>
		<a style="float: right; padding: 5px;" onClick="closeReplyDiv('${message.parentId}')"><img src="images/close.jpeg"></a>
	</c:if>

	<div class="emotibox">
		<c:forEach items="${emoMap}" var="emo" varStatus="index">
			 <%-- caso speciale per la faccina :\  --%>
			<c:set var="emoValue" value="${fn:replace(emo.value[0], '\\\\', '\\\\\\\\')}"/>
			 <%-- caso speciale per la faccina :'(  --%>
			<c:set var="emoValue" value="${fn:replace(emoValue, '\\'', '\\\\\\'')}"/>
			<img onmousedown="insert('${emoValue}', '', '${message.parentId}')" title="${emoValue}" src="images/emo/${emo.key}.gif" style="cursor: pointer;"/>
			<c:if test="${index.count % 13 == 0}"><br/></c:if>
		</c:forEach>
		<br/>
		<div style="margin:3px 0px 3px 0px ">
			<span onmousedown="insert('<b>', '</b>', '${message.parentId}')" class="msgButton btnBold" title="Grassetto (ma meno di Lich)">B</span>&nbsp;
			<span onmousedown="insert('<i>', '</i>', '${message.parentId}')" class="msgButton btnItalic" title="Corsivo">I</span>&nbsp;
			<span onmousedown="insert('<u>', '</u>', '${message.parentId}')" class="msgButton btnUnderline" title="Sottolineato">U</span>&nbsp;
			<span onmousedown="insert('<s>', '</s>', '${message.parentId}')" class="msgButton btnStrike" title="Barrato">S</span>&nbsp;
			<a href="javascript:void(0);" onmousedown="insert('[img]', '[/img]', '${message.parentId}')" class="msgButton">[immagine]</a>
			<a href="javascript:void(0);" onmousedown="insert('[code]', '[/code]', '${message.parentId}')" class="msgButton">[codice]</a>
			<a href="javascript:void(0);" onmousedown="insert('[yt]', '[/yt]', '${message.parentId}')" class="msgButton">[youtube]</a>
			<a href="javascript:void(0);" onmousedown="urlInput('${message.parentId}')" class="msgButton">[url]</a>
			<a href="javascript:void(0);" onmousedown="insert('[color #' + $('.color').val() + ']', '[/color]', '${message.parentId}')" class="msgButton">[color]</a> <input type='text' class='color' value='66ff00' style='width:40px; font-size: 10px'>
			<input style="float:right" tabindex="5" type="button" name="preview" value="Preview" onClick="preview(${message.parentId})"/>&nbsp;
			<input style="display:none;float:right" tabindex="5" type="button" name="edit" value="Edit" onClick="edit(${message.parentId})"/>&nbsp;
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
	<textarea tabindex="1" name="text" tabindex="2" rows="20" class="msgReplyTxt" id="text_${message.id}"
	onkeyup="update_counter(${message.id},<%=Messages.MAX_MESSAGE_LENGTH%>)"
	onchange="update_counter(${message.id},<%=Messages.MAX_MESSAGE_LENGTH%>)">${message.text}</textarea>

	<%-- preview area --%>
	<div id="preview_${message.parentId}" class="msgReplyTxt"></div>

	<input type="hidden" name="forum" value="${message.forum }"/>
	<input type="hidden" name="id" value="${message.id }"/>
	<div class="msgAnonBox">
		<div class='counter-container'>
			<input type='text' id='counter_${message.id}' disabled="disabled" value='${MAX_MESSAGE_LENGTH - fn:length(message.text)}'/>
		</div>
		<label for="nick">Nome:&nbsp;</label>
		<input tabindex="2" name="nick" id="nick" size="10" value="${loggedUser.nick }"/>&nbsp;&nbsp;
		<label for="password">Password:&nbsp;</label>
		<input tabindex="3" type="password" id="password" name="pass" size="10"/>
		<c:if test="${loggedUser != null}">(inserisci soltanto se vuoi postare con un utente diverso da quello con cui sei loggato)</c:if>
		<div class="msgCaptcha">
			<div><img src="Misc?action=getCaptcha&amp;v=<%=System.currentTimeMillis()%>" /></div><div><input tabindex="4" name="captcha" size="5" /><div class="msgCaptchaInput">
			<c:choose>
				<c:when test="${loggedUser != null}">
					Cancella il tuo nickname e copia qui il testo dell'immagine per postare come Non Autenticato.
				</c:when>
				<c:otherwise>
					Copia qui il testo dell'immagine
				</c:otherwise>
			</c:choose>
			</div></div>
			<div style="clear: both;"></div>
		</div>
	</div>
	<input tabindex="5" type="button" value="Invia" onClick="send(${message.parentId})" class="msgSendButton" />
	<div style="clear: both;"></div>
</div>
<c:if test="${param.action != 'newMessage'}">
	<fdt:delayedScript dump="true">
	questa jsp non è inclusa in altre jsp, quindi gli script delayed
	vanno piazzati qua
	</fdt:delayedScript>
</c:if>