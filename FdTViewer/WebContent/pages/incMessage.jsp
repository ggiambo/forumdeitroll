<%@page import="com.forumdeitroll.servlets.User"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<c:choose>
	<c:when test="${msg.visible}">
		<c:set var="rowclass" value="msgVisible"/>
	</c:when>
	<c:otherwise>
		<c:set var="rowclass" value="msgInvisible"/>
		<div id="msgWarning${msg.id}" style="padding:5px;">
			<img src="images/warning.png" style="float:right; margin-right:10px"/>
			Questo messaggio e' stato catalogato come "Exiled Nigerian princess".<br/>
			Clicka <a href="#" onClick="showHIddenMessage(${msg.id});return false;">qui</a> per vederlo, e che Dio onnipotente possa aver piet&agrave; della tua anima.
		</div>
	</c:otherwise>
</c:choose>

<c:if test="${not empty loggedUser && loggedUser.preferences['msgMaxHeight'] == 'checked'}">
	<c:set var="rowclass" value="${rowclass} msgOptMaxHeight"/>
</c:if>

<a href="#msg${msg.id}"></a>

<div class="${rowclass}" id="msg${msg.id}">

	<div class="msgInfo">
		<div>
			<c:choose>
                <c:when test="${not empty msg.fakeAuthor}">
                    <img class="avatarImg" alt="Avatar" src="images/avatardefault.gif" />
                </c:when>
				<c:when test="${!empty msg.author.nick}">
					<a href="${msg.author.userInfoUrl}">
						<img class="avatarImg avatarImgLinkable" alt="Avatar" src="${msg.author.avatarUrl}" />
					</a>
				</c:when>
				<c:otherwise>
					<img class="avatarImg" alt="Avatar" src="Misc?action=getAvatar" />
				</c:otherwise>
			</c:choose>
			<fdt:nickcode nick="${msg.author.nick}"/>
		</div>
		<c:if test="${!empty msg.forum}">
			<div class="msgForum">${msg.forum}</div>
		</c:if>
		<div class="msgDetails">
			<div class="msgWrittenby">Scritto da</div>
			<div class="msgAuthor">
				<c:choose>
                    <c:when test="${not empty msg.fakeAuthor}">
                        <c:out value="${msg.fakeAuthor}" escapeXml="true"/>
                    </c:when>
					<c:when test="${empty msg.author.nick}">
						Non Autenticato
					</c:when>
					<c:otherwise>
						<a href="${msg.author.getMessagesUrl(specificParams['forum'])}">${msg.author.nick}</a>
					</c:otherwise>
				</c:choose>
                <c:choose>
                    <c:when test="${msg.author.preferences['super'] == 'yes'}">
                        <div style="color:#009090;font-size:0.8em;font-style:normal;font-weight:bold;" title="Vi naso gli IP">Utente privilegiato</div>
                    </c:when>
                    <c:otherwise>
                        <c:set var="userTitle" value="${msg.author.preferences['userTitle']}"/>
                        <c:if test="${not empty userTitle}">
                            <div style="color:#5B5BF0;font-size:0.8em;font-style:normal;font-weight:bold;">${userTitle}</div>
                        </c:if>
                    </c:otherwise>
                </c:choose>
			</div>
			<div class="msgDate">il <fmt:formatDate value="${msg.date}" pattern="dd.MM.yyyy"/> alle <fmt:formatDate value="${msg.date}" pattern="HH:mm"/></div>

			<c:if test="${not empty msg.author.nick}">
				<div class="msgTotalMsg">
					Trollate totali: ${msg.author.messages}
				</div>
			</c:if>
		</div>
	</div>

	<span style="width:100%; margin:5px;">
		<b>
			<a href="Threads?action=getByThread&amp;threadId=${msg.threadId}#msg${msg.id}">${msg.subject}</a>
		</b>
	</span>

	<span class=tags>
		<c:forEach var="tag" items="${msg.tags}">
			<span>
				<span class=tag title="aggiunto da ${tag.author}">
					<a href="Messages?action=getMessagesByTag&t_id=${tag.t_id}">
						<c:out value="${tag.value}" escapeXml="true"/>
					</a>
				</span>
				<c:if test="${not empty loggedUser && tag.author == loggedUser.nick || loggedUser.preferences['super'] == 'yes'}">
					<span class=del-tag onclick=deleteTag(event,${tag.t_id},${tag.m_id}) title="Elimina questo tag">
						&nbsp;&nbsp;
					</span>
				</c:if>
				&nbsp;
			</span>
		</c:forEach>
	</span>

	<c:if test="${not empty loggedUser}">
	<span class=add-tag onclick=openCloseAddTag(event) title="Aggiungi un tag">&nbsp;&nbsp;&nbsp;&nbsp;
		<input type=text onkeypress="saveTag(event,'${msg.id}')" style=display:none>
	</span>
	</c:if>

	<c:if test="${msg.searchRelevance >= 0}">
		<div class="searchInfo">
			<pre><!-- Ciao wakko :-) -->Rilevanza: <fmt:formatNumber value="${msg.searchRelevance}" pattern="#0.00" />. Messaggi nel thread: ${msg.searchCount - 1}</pre>
		</div>
	</c:if>

	<div style="padding: 10px;" class="message">
		<c:set var="message" value="${msg}"/>
		<fdt:render target="message"/>
	</div>

	<c:if test="${(not empty msg.author.preferences['signature'] || not empty msg.author.signatureImage) && (empty loggedUser || (not empty loggedUser && loggedUser.preferences['hideSignature'] != 'checked'))}">
		<c:choose>
			<c:when test="${ msg.author.preferences['compactSignature'] == 'checked'}">
				<c:set var="signatureClass" value="firmaCompatta"/>
				<div style="clear:both; height: <%=""+User.MAX_SIZE_SIGNATURE_HEIGHT %>px; width: 100%;"></div>
			</c:when>
			<c:otherwise>
				<c:set var="signatureClass" value="firma"/>
			</c:otherwise>
		</c:choose>
		<div class="${signatureClass}" id="firma_${msg.id}">
			<c:if test="${not empty msg.author.preferences['signature']}">
				<fdt:render target="signature"/>
			</c:if>
			<c:if test="${not empty msg.author.signatureImage}">
				<c:url value="Misc" var="signatureUrl">
					<c:param name="action" value="getUserSignatureImage"/>
					<c:param name="nick" value="${msg.author.nick}"/>
				</c:url>
				<img src="<c:out value="${signatureUrl}" escapeXml="true"/>"/>
			</c:if>
		</div>
	</c:if>
	<div style="clear:both; height: 1px; width: 100%;"></div>
</div>
<div id="buttons_${msg.id}">
	<c:if test="${(msg.parentId != -1 and msg.parentId != msg.id and servlet == 'Threads') or (not empty loggedUser)}">
		<div class="messagesButtonBarLeft">
	</c:if>
	<c:if test="${msg.parentId != -1 and msg.parentId != msg.id and servlet == 'Threads'}">
		<%--pulsantino messaggio parent --%>
		<div class="buttonBarButton">
			<a class="buttonBarLink" href="#msg${msg.parentId}">
				<span class="buttonBarImg buttonBarImgParent"></span>
			</a>
		</div>
	</c:if>
	<c:if test="${not empty loggedUser}">
		<%-- Segnalibri --%>
		<div class="buttonBarButton">
			<a class="buttonBarLink" href="Bookmarks?action=add&amp;msgId=${msg.id}">
				<span class="buttonBarImg buttonBarImgBookmark"></span>
				Aggiungi ai segnalibri
			</a>
		</div>
	</c:if>
	<c:if test="${(msg.parentId != -1 and msg.parentId != msg.id and servlet == 'Threads') or (not empty loggedUser)}">
		</div>
	</c:if>
	<div class="messagesButtonBar">
		<c:if test="${not empty loggedUser}">
			<c:if test="${loggedUser.preferences['super'] == 'yes' || loggedUser.preferences['pedonizeThread'] == 'yes' || loggedUser.preferences['hideMessages'] == 'yes'}">
				<div class="buttonBarButton buttonBarButtonAdmin" id="OpenMod_${msg.id}" style="display: inline">
					<a class="buttonBarLink" href="#" onclick="showHideAdminButtons('${msg.id}'); return false">
						<span class="buttonBarImgAdmin buttonBarImgOpenMod"></span>
					</a>
				</div>
			</c:if>

			<%-- Moderazione --%>
			<c:if test="${loggedUser.preferences['super'] == 'yes'}">
				<c:url value="ModInfo" var="modUrl">
					<c:param name="m_id" value="${msg.id}"/>
				</c:url>
				<div class="buttonBarButton buttonBarButtonAdmin">
					<a class="buttonBarLink" href="${modUrl}">
						<span class="buttonBarImgAdmin buttonBarImgModerazione"></span>
						Moderazione
					</a>
				</div>
			</c:if>

			<%-- Pedonize ! --%>
			<c:if test="${loggedUser.preferences['pedonizeThread'] == 'yes'}">
				<c:if test="${msg.forum != 'Proc di Catania'}">
					<div class="buttonBarButton buttonBarButtonAdmin">
						<a class="buttonBarLink" href="#" onClick="pedonizeThreadTree('${msg.id}');return false;">
							<span class="buttonBarImgAdmin buttonBarImgPedonize"></span>
							Pedonize !
						</a>
					</div>
				</c:if>
			</c:if>

			<%-- Nascondi / Mostra --%>
			<c:if test="${loggedUser.preferences['hideMessages'] == 'yes'}">
				<div class="buttonBarButton buttonBarButtonAdmin">
					<c:choose>
						<c:when test="${msg.visible}">
							<a class="buttonBarLink" href="#" onClick="hideMessage('${msg.id}');return false;">
								<span class="buttonBarImgAdmin buttonBarImgHide"></span>
								Nascondi
							</a>
						</c:when>
						<c:otherwise>
							<a class="buttonBarLink" href="#" onClick="restoreHiddenMessage('${msg.id}');return false;">
								<span class="buttonBarImgAdmin buttonBarImgReveal"></span>
								Rendi visibile
							</a>
						</c:otherwise>
					</c:choose>
				</div>
			</c:if>

			<%-- Notifica --%>
			<div class="buttonBarButton">
				<span id="notify_${msg.id}" style="width: 100px">
					<a class="buttonBarLink" href="#" onClick="openNotifyInput('${msg.id}');return false;">
						<span class="buttonBarImg buttonBarImgNotifica"></span>
						Notifica
					</a>
					<input type="text" size="15" name="notifyToNick" class="notifyInput" />
				</span>
			</div>

			<%-- Modifica --%>
			<c:if test="${msg.author.nick == loggedUser.nick}">
				<div class="buttonBarButton">
					<a class="buttonBarLink" href="Messages?action=editMessage&amp;msgId=${msg.id}&amp;forum=${msg.forum}">
						<span class="buttonBarImg buttonBarImgModifica"></span>
						Modifica
					</a>
				</div>
			</c:if>
		</c:if>

		<div class="buttonBarButton buttonBarRispondi">
			<a class="buttonBarLink" href="#" onClick="showDropDownReply(event, '${msg.id}'); return false">
				<span class="buttonBarImg buttonBarImgRispondi"></span>
				Rispondi
			</a>
			<ul id="replyMenu${msg.id}" class="replyMenu">
				<li>
					<a href="#" onClick="showReplyDiv('reply', '${msg.id}'); return false;">
						Rispondi
					</a>
				</li>
				<li>
					<a href="#" onClick="showReplyDiv('quote', '${msg.id}'); return false;">
						Quota tutto
					</a>
				</li>
				<li>
					<a href="#" onClick="showReplyDiv('quote1', '${msg.id}'); return false;">
						Quota ultimo
					</a>
				</li>
				<li>
					<a href="#" onClick="showReplyDiv('quote4', '${msg.id}'); return false;">
						Quota ultimi 4
					</a>
				</li>
				<li>
					<a href="#" onClick="hideDropDownReply(event, ${msg.id}); return false">
						Chiudi
					</a>
				</li>
			</ul>
		</div>

		<div class="buttonBarButton">
			<c:if test="${not empty loggedUser}">
					<%-- +1 --%>
				<a class="buttonBarLink" href="#" onClick="like('${msg.id}', true);return false;" title="+1">
					<span class="buttonBarImg buttonBarImgUpvote"></span>
				</a>

				<%-- -1 --%>
				<a class="buttonBarLink" href="#" onClick="like('${msg.id}', false);return false;" title="-1">
					<span class="buttonBarImg buttonBarImgDownvote"></span>
				</a>
			</c:if>
			<span class="ranking" id="msg${msg.id}_ranking">${msg.rank}</span>
		</div>

	</div>

</div>

