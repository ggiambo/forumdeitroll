<%@page import="com.forumdeitroll.servlets.User"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<c:choose>
	<c:when test="${loggedUser.wantsToHideMessage(msg)}">
		<c:set var="rowclass" value="msgInvisible"/>
		<div class="messageBox">
			<div class=row onclick="showMessage(this, '${msg.id}')">
				<div class=col-1>
					<img src="images/poop.png"/>
				</div>
				<div class=col-5>
					Messaggio cacca
				</div>
			</div>
		</div>
	</c:when>
	<c:when test="${msg.visible}">
		<c:set var="rowclass" value="msgVisible"/>
	</c:when>
	<c:otherwise>
		<c:set var="rowclass" value="msgInvisible"/>
		<div class="messageBox">
			<div class=row onclick="showMessage(this, '${msg.id}')">
				<div class=col-1>
					<img src="images/warning.png"/>
				</div>
				<div class=col-5>
					Exiled Nigerian princess
				</div>
			</div>
		</div>
	</c:otherwise>
</c:choose>

<span class=msg-anchor id="msg${msg.id}"></span>
<div class="${rowclass} messageBox" id="msgbox${msg.id}">
	<div class=row onclick=toggleMessageView(this,event,${msg.id}) style='height: 48px' id="msg-toggle-${msg.id}">
		<div class=col-1>
            <c:choose>
                <c:when test="${not empty msg.fakeAuthor}">
                    <img src="images/avatardefault.gif" class="avatar">
                </c:when>
                <c:otherwise>
			        <img src="Misc?action=getAvatar&amp;&nick=${msg.author.nick}" class=avatar>
                </c:otherwise>
            </c:choose>
		</div>
		<c:choose>
			<c:when test="${param['action'] == 'getByThread'}">
				<div class=col-5>
					${msg.subject}
					<span class=arrow>&#x25bc;</span>
				</div>
			</c:when>
			<c:otherwise>
				<div class=col-5>
					${msg.subject}
					<span class=arrow>&#x25bc;</span>
				</div>
			</c:otherwise>
		</c:choose>
        <div class=col-5>
            <c:if test="${msg.rank gt 0}">
                <c:forEach begin="1" end="${msg.rank}">
                    <div class="rankingClassPositive">
                    </div>
                </c:forEach>
            </c:if>
            <c:if test="${msg.rank lt 0}">
                <c:forEach begin="1" end="${-1*msg.rank}">
                    <div class="rankingClassNegative">
                    </div>
                </c:forEach>
            </c:if>
        </div>
		<span class=msgInfo>
			di
			<c:choose>
                <c:when test="${not empty msg.fakeAuthor}">
                    <i><c:out value="${msg.fakeAuthor}" escapeXml="true"/></i>
                </c:when>
				<c:when test="${not empty msg.author.nick}">
					${msg.author.nick}
				</c:when>
				<c:otherwise>
					non autenticato
				</c:otherwise>
			</c:choose>
			<br>
			<i><fdt:prettyDate date="${msg.date}"/></i>
		</span>
	</div>
	<div class="row sep"></div>
	<div class="msgContent">
		<div class=transparentOverlay onclick=toggleMessageView(this,event,${msg.id})></div>
		<div class="msgText">
			<c:set var="message" value="${msg}"/>
			<fdt:render target="message"/>
		</div>
		<div class=msgButtons>
			<div class="row">
				<div class=col-1-2>&nbsp;</div>
				<div class=col-2>
					<a href="Messages?action=mobileComposer&amp;replyToId=${msg.id}&type=quote" class="btn btn-flat">Quota</a>
				</div>
				<div class=col-1>&nbsp;</div>
				<div class=col-2>
					<c:if test="${msg.author.nick == loggedUser.nick}">
						<a href="Messages?action=mobileComposer&amp;replyToId=${msg.parentId}&amp;messageId=${msg.id}" class="btn btn-flat">Modifica</a>
					</c:if>
					<c:if test="${msg.author.nick != loggedUser.nick}">
						<a href="Messages?action=mobileComposer&amp;replyToId=${msg.id}" class="btn btn-flat">Rispondi</a>
					</c:if>
				</div>
				<div class=col-1-2>&nbsp;</div>
			</div>
			<div class=row>&nbsp;</div>
			<div class="row">
				<div class=col-1-2>&nbsp;</div>
				<div class=col-2>
					<c:if test="${not empty msg.author.nick}">
						<a href="Messages?action=getByAuthor&author=${msg.author.nick}" class="btn btn-flat">
							di ${msg.author.nick}
						</a>
					</c:if>
					<c:if test="${empty msg.author.nick}">
						&nbsp;
					</c:if>
				</div>
				<div class=col-1>&nbsp;</div>
				<div class=col-2>
					<a href="Threads?action=getByThread&threadId=${msg.threadId}#msg${msg.id}" class="btn btn-flat">
						Leggi Thread
					</a>
				</div>
				<div class=col-1-2>&nbsp;</div>
			</div>
			<div class=row>&nbsp;</div>
		</div>
	</div>
</div>