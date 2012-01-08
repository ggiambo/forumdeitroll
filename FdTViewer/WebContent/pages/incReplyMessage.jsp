<%@page import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>

<c:set var="isNewMessage" value="${parentId == -1}"/>

<div id="reply_${parentId}">

	<c:if test="${isNewMessage == false}">
		<c:set var="class" value="border:1px solid black; padding:2px; margin:2px;"/>
		<a style="float: right; padding: 5px;" onClick="closeReplyDiv('${parentId}')"><img src="images/close.jpeg"></a>
	</c:if>

	<div style="${class} background: Whitesmoke">
		<c:forEach items="${emoMap}" var="emo" varStatus="index">
			 <%-- caso speciale per la faccina :\  --%>
			<c:set var="emoValue" value="${fn:replace(emo.value, '\\\\', '\\\\\\\\')}"/>
			 <%-- caso speciale per la faccina :'(  --%>
			<c:set var="emoValue" value="${fn:replace(emoValue, '\\'', '\\\\\\'')}"/>
			<img onmousedown="insert('${emoValue}', '', '${parentId}')" title="${emoValue}" src="images/emo/${emo.key}.gif" style="cursor: pointer;"/>
			<c:if test="${index.count % 13 == 0}"><br/></c:if>
		</c:forEach>
		<br/>
		<div style="margin:3px 0px 3px 0px ">
			<b onmousedown="insert('<b>', '</b>', '${parentId}')" class="fakeButton">B</b>&nbsp;
			<i onmousedown="insert('<i>', '</i>', '${parentId}')" class="fakeButton">I</i>&nbsp;
			<u onmousedown="insert('<u>', '</u>', '${parentId}')" class="fakeButton">U</u>&nbsp;
			<s onmousedown="insert('<s>', '</s>', '${parentId}')" class="fakeButton">S</s>&nbsp;
			<a href="javascript:void(0);" onmousedown="insert('[img]', '[/img]', '${parentId}')">[immagine]</a>
			<a href="javascript:void(0);" onmousedown="insert('[code]', '[/code]', '${parentId}')">[codice]</a>
			<a href="javascript:void(0);" onmousedown="insert('[yt]', '[/yt]', '${parentId}')">[youtube]</a>
		</div>
		<c:if test="${isNewMessage == true}">
			Oggetto: <input name="subject"/>
		</c:if>
		<c:if test="${not empty param.forum}">
			Forum <i>${param.forum}</i>
		</c:if>
		<textarea tabindex="1" name="text" tabindex="2" rows="20" style="font-size:13px; width:100%">${message.text}</textarea><br/>
		<%--Mostra username password captcha solo se non autenticato --%>
		<c:choose>
			<c:when test="${empty loggedUser}">
				Nome: <input tabindex="2" name="nick" size="10"/> Password: <input tabindex="3" type="password" name="pass" size="10"/>
				<input type="hidden" name="forum" value="${forum }"/>
				<div style="margin: 3px">
					<img src="Messages?action=getCaptcha" style="vertical-align:middle"/>&nbsp;<input tabindex="4" name="captcha" size="5"/> (Solo per ANOnimi)
					<input tabindex="5" type="button" value="Invia" onClick="send(${parentId})"/>
				</div>
			</c:when>
			<c:otherwise>
				<input tabindex="5" type="button" value="Invia" onClick="send(${parentId})"/>
			</c:otherwise>
		</c:choose>
	</div>
</div>