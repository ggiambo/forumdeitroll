<%@page import="java.util.Map"%>
<%@page import="com.forumdeitroll.servlets.Messages"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<fdt:delayedScript dump="false">
	$(document).ready(function() {
		initPvtSendNew(new Array(${recipients}));
	});

</fdt:delayedScript>

 <div class="userPanelSection">
	<h3>Invia Messaggio Privato</h3>
	<form action="Pvt" method="POST" class="pvtSendMessage">
		<input type="hidden" name="action" value="sendPvt">
 
		 <div class="msgReply">
		
			<ul class="tabs" id="tabs">
				<li class="selectedTab" onClick="showEmotiboxClassic(); return false;"><a href="#">Serie classica</a></li>
				<li><a href="#" onClick="showEmotiboxExtended(); return false;">Serie estesa</a></li>
			</ul>
		
			<div class="emotibox" id="emotibox">
				<div class="emo">
					<c:forEach items="${emoMap}" var="emo" varStatus="index">
						 <%-- caso speciale per la faccina :\  --%>
						<c:set var="emoValue" value="${fn:replace(emo.value[0], '\\\\', '\\\\\\\\')}"/>
						 <%-- caso speciale per la faccina :'(  --%>
						<c:set var="emoValue" value="${fn:replace(emoValue, '\\'', '\\\\\\'')}"/>
						<img onmousedown="insertIntoTextArea('${emoValue}', '', $('#text').get(0))" title="${emoValue}" src="images/emo/${emo.key}.gif" style="cursor: pointer;"/>
						<c:if test="${index.count % 13 == 0}"><br/></c:if>
					</c:forEach>
				</div>
				<div style="display:none" class="emo">
					<c:forEach items="${extendedEmos}" var="emo" varStatus="index">
						<img onmousedown="insertIntoTextArea('${emo.value[0]}', '', $('#text').get(0))" title="${emo.value[1]}" src="images/emoextended/${emo.key}.gif" style="cursor: pointer;"/>
						<c:if test="${index.count % 13 == 0}"><br/></c:if>
					</c:forEach>
				</div>
				<div style="margin:3px 0px 3px 0px ">
					<span onmousedown="insertIntoTextArea('<b>', '</b>', $('#text').get(0))" class="msgButton btnBold" title="Grassetto (ma meno di Lich)">B</span>&nbsp;
					<span onmousedown="insertIntoTextArea('<i>', '</i>', $('#text').get(0))" class="msgButton btnItalic" title="Corsivo">I</span>&nbsp;
					<span onmousedown="insertIntoTextArea('<u>', '</u>', $('#text').get(0))" class="msgButton btnUnderline" title="Sottolineato">U</span>&nbsp;
					<span onmousedown="insertIntoTextArea('<s>', '</s>', $('#text').get(0))" class="msgButton btnStrike" title="Barrato">S</span>&nbsp;
					<a href="javascript:void(0);" onmousedown="insertIntoTextArea('[img]', '[/img]', $('#text').get(0))" class="msgButton">[immagine]</a>
					<a href="javascript:void(0);" onmousedown="insertIntoTextArea('[code]', '[/code]', $('#text').get(0))" class="msgButton">[codice]</a>
					<a href="javascript:void(0);" onmousedown="insertIntoTextArea('[yt]', '[/yt]', $('#text').get(0))" class="msgButton">[youtube]</a>
				</div>
			</div>
		
			<div style="margin-bottom:5px"><label for="subject">Oggetto:</label>
				<input type="text" name="subject" id="subject" value="${subject}"/>
			</div>
		
			<%-- input area --%>
			<textarea tabindex="1" name="text" tabindex="2" rows="20" class="msgReplyTxt" id="text">${text}</textarea>
		
			<%-- preview area --%>
			<%--
			<div id="pvtPreview" style="background:white; border: 1px solid #7BAAE7; display:none; padding:3px; margin-bottom: 5px; overflow:auto"></div>
			 --%>
			<div id="pvtPreview" class="msgReplyTxt"></div>
		
			
			<div style="margin-bottom: 5px;">
				<label for="recipients">Destinatari:</label><br />
				<div id="recipientsDiv" class="ui-helper-clearfix">
					<input type="text" id="recipients" type="text">
				</div>
			</div>
			
			<input type="submit" value="Invia" class="sendPvt" />
			<input class="sendPvt" style="float:left;font-weight:normal;" tabindex="5" type="button" name="preview" value="Preview" onClick="previewPvt()"/>&nbsp;
			<input class="sendPvt" style="display:none;float:left;font-weight:normal;" tabindex="5" type="button" name="edit" value="Edit" onClick="editPvt()"/>&nbsp;

			<div style="clear: both;"></div>
		</div>
		
	</form>
	<div style="clear: both;"></div>
</div>
<script type="text/javascript" src="js/pvt.js"></script>