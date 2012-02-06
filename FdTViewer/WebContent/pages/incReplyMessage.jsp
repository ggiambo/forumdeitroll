<%@page import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>

<script>
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


	
</script>

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
			<a href="javascript:void(0);" onmousedown="insert('[color #' + $('.color').val() + ']', '[/color]', '${message.parentId}')">[color]</a> <input type='text' class='color' value='66ff00' style='width:40px; font-size: 10px'>
			<input style="float:right" tabindex="5" type="button" name="preview" value="Preview" onClick="preview(${message.parentId})"/>&nbsp;
			<input style="display:none;float:right" tabindex="5" type="button" name="edit" value="Edit" onClick="edit(${message.parentId})"/>&nbsp;
		</div>
	</div>
	<c:if test="${isNewThread || isEdit}">
		<label for="subject">Oggetto:</label><br /> 
		<input tabindex="1" name="subject" id="subject" maxlength="40" size="40" class="msgReplyObj" value="${message.subject}"/>
	</c:if>
	<c:if test="${not empty message.forum}">
		Forum <i>${message.forum}</i>
	</c:if>
	
	<%-- input area --%>
	<textarea tabindex="1" name="text" tabindex="2" rows="20" class="msgReplyTxt">${message.text}</textarea>

	<%-- preview area --%>
	<div id="preview_${message.parentId}" class="msgReplyTxt"></div>
	
	<input type="hidden" name="forum" value="${message.forum }"/>
	<input type="hidden" name="id" value="${message.id }"/>
	<c:choose>
		<%--Mostra username password captcha solo se non autenticato --%>
		<c:when test="${empty loggedUser}">
			<div class="msgAnonBox">
				<label for="nick">Nome:&nbsp;</label><input tabindex="2" name="nick" id="nick" size="10"/>&nbsp;&nbsp;<label for="password">Password:&nbsp;</label><input tabindex="3" type="password" id="password" name="pass" size="10"/>
				<div class="msgCaptcha">
					<div><img src="Messages?action=getCaptcha&amp;v=<%=System.currentTimeMillis()%>" /></div><div><input tabindex="4" name="captcha" size="5" /><div class="msgCaptchaInput">Copia qui il testo dell'immagine</div></div>
					<div style="clear: both;"></div>
				</div>
			</div>
			<input tabindex="5" type="button" value="Invia" onClick="send(${message.parentId})" class="msgSendButton" />
		</c:when>
		<c:otherwise>
			<div style="margin: 3px">
				<input tabindex="5" type="button" value="Invia" onClick="send(${message.parentId})" class="msgSendButton" />&nbsp;
			</div>
		</c:otherwise>
	</c:choose>
	<div style="clear: both;"></div>
</div>