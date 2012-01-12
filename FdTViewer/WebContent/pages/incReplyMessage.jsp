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
	});

	
</script>

<div id="reply_${message.parentId}">

	<c:if test="${message.parentId != -1 || message.id != -1}">
		<c:set var="class" value="border:1px solid black; padding:2px; margin:2px;"/>
		<a style="float: right; padding: 5px;" onClick="closeReplyDiv('${message.parentId}')"><img src="images/close.jpeg"></a>
	</c:if>

	<div style="${class} background: Whitesmoke">
		<c:forEach items="${emoMap}" var="emo" varStatus="index">
			 <%-- caso speciale per la faccina :\  --%>
			<c:set var="emoValue" value="${fn:replace(emo.value, '\\\\', '\\\\\\\\')}"/>
			 <%-- caso speciale per la faccina :'(  --%>
			<c:set var="emoValue" value="${fn:replace(emoValue, '\\'', '\\\\\\'')}"/>
			<img onmousedown="insert('${emoValue}', '', '${message.parentId}')" title="${emoValue}" src="images/emo/${emo.key}.gif" style="cursor: pointer;"/>
			<c:if test="${index.count % 13 == 0}"><br/></c:if>
		</c:forEach>
		<br/>
		<div style="margin:3px 0px 3px 0px ">
			<b onmousedown="insert('<b>', '</b>', '${message.parentId}')" class="fakeButton">B</b>&nbsp;
			<i onmousedown="insert('<i>', '</i>', '${message.parentId}')" class="fakeButton">I</i>&nbsp;
			<u onmousedown="insert('<u>', '</u>', '${message.parentId}')" class="fakeButton">U</u>&nbsp;
			<s onmousedown="insert('<s>', '</s>', '${message.parentId}')" class="fakeButton">S</s>&nbsp;
			<a href="javascript:void(0);" onmousedown="insert('[img]', '[/img]', '${message.parentId}')">[immagine]</a>
			<a href="javascript:void(0);" onmousedown="insert('[code]', '[/code]', '${message.parentId}')">[codice]</a>
			<a href="javascript:void(0);" onmousedown="insert('[yt]', '[/yt]', '${message.parentId}')">[youtube]</a>
		</div>
		<c:if test="${message.parentId == -1 && message.id == -1}">
			Oggetto: <input tabindex="1" name="subject" maxlength="40" size="40"/>
		</c:if>
		<c:if test="${not empty message.forum}">
			Forum <i>${message.forum}</i>
		</c:if>
		
		<%-- input area --%>
		<textarea tabindex="1" name="text" tabindex="2" rows="20" style="font-size:13px; width:100%">${message.text}</textarea><br/>
		
		<input type="hidden" name="forum" value="${message.forum }"/>
		<input type="hidden" name="id" value="${message.id }"/>
		<c:choose>
			<%--Mostra username password captcha solo se non autenticato --%>
			<c:when test="${empty loggedUser}">
				Nome: <input tabindex="2" name="nick" size="10"/> Password: <input tabindex="3" type="password" name="pass" size="10"/>
				<div style="margin: 3px">
					<img src="Messages?action=getCaptcha" style="vertical-align:middle"/>&nbsp;<input tabindex="4" name="captcha" size="5"/> (Solo per ANOnimi)
					<input tabindex="5" type="button" value="Invia" onClick="send(${message.parentId})"/>
				</div>
			</c:when>
			<c:otherwise>
				<div style="margin: 3px">
					<input style="float:right" tabindex="5" type="button" value="Invia" onClick="send(${message.parentId})"/>&nbsp;
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>