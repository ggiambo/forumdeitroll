<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<div class=row>
	<div class=col-1-2>&nbsp;</div>
	<div class=col-1>
		<a href=Pvt class="btn btn-flat">Ricevuti</a>
	</div>
	<div class=col-1>&nbsp;</div>
	<div class=col-1>
		<a href=Pvt?action=outbox class="btn btn-flat">Inviati</a>
	</div>
	<div class=col-1>&nbsp;</div>
	<div class=col-1>
		<a href="Pvt?action=sendNew" class="btn btn-flat">Nuovo</a>
	</div>
	<div class=col-1-2>&nbsp;</div>
</div>
<div class=row>&nbsp;</div>

<c:if test="${from == 'inbox' || from == 'outbox'}">
	<c:forEach items="${pvts}" var="pvt" varStatus="index">
		<div class=pvtBox onclick="showPvt(${pvt.id})">
			<div class=row>
				<div class=col-1>
					<c:if test="${from == 'inbox' }">
						<img src="Misc?action=getAvatar&amp;&nick=${pvt.fromNick}" class=avatar>
					</c:if>
					<c:if test="${from == 'outbox'}">
						<img src="Misc?action=getAvatar&amp;&nick=${loggedUser.nick}" class=avatar>
					</c:if>
				</div>
				<div class=col-5>
					<c:if test="${from == 'inbox' }">
						da ${pvt.fromNick}
						<br>
						<c:if test="${pvt.read}">
							${pvt.subject}
						</c:if>
						<c:if test="${not pvt.read}">
							<b>${pvt.subject}</b>
						</c:if>
					</c:if>
					<c:if test="${from == 'outbox'}">
						Inviato a
						<c:forEach items="${pvt.toNick}" var="destNick" varStatus="index">
							<c:if test="${index.index > 0}">
								,
							</c:if>
							<c:if test="${destNick.read}">
								${destNick.nick}
							</c:if>
							<c:if test="${not destNick.read}">
								<b>${destNick.nick}</b>
							</c:if>
						</c:forEach>
						<br>
						${pvt.subject}
					</c:if>
				</div>
			</div>
			<span class=msgInfo>
				<i><fdt:prettyDate date="${pvt.date}"/></i>
			</span>
		</div>
	</c:forEach>
	<c:set scope="request" var="pagerHandler" value="pvt"/>
</c:if>

<c:if test="${from == 'show'}">
	<form method=post action=Pvt>
		<input type=hidden name=action value=>
		<input type=hidden name=id value="${pvtdetail.id}">
		<div class=pvtBox>
			<span class=msgInfo>
				<i><fdt:prettyDate date="${pvtdetail.date}"/></i>
			</span>
			<div class=row>
				<div class=col-1>
					<img src="Misc?action=getAvatar&amp;&nick=${pvtdetail.fromNick}" class=avatar>
				</div>
				<div class=col-5>
					${pvt.subject}
					<br>
					Da
					<c:choose>
						<c:when test="${pvtdetail.fromNick != loggedUser.nick and not pvtdetail.read}">
							<b>${pvtdetail.fromNick}</b>
						</c:when>
						<c:otherwise>
							${pvtdetail.fromNick}
						</c:otherwise>
					</c:choose>
					<br>
					A
					<c:forEach items="${pvtdetail.toNick}" var="destNick" varStatus="index">
						<c:if test="${index.index > 0}">
							,
						</c:if>
						<c:if test="${destNick.read}">
							${destNick.nick}
						</c:if>
						<c:if test="${not destNick.read}">
							<b>${destNick.nick}</b>
						</c:if>
					</c:forEach>
				</div>
			</div>
			<div class=row>
				<div class=col-6>
					<c:set var="privateMessage" value="${pvtdetail}" />
					<fdt:render target="privateMessage"/>
				</div>
			</div>
			<div class=row>&nbsp;</div>
			<div class=row>
				<div class=col-1-2>&nbsp;</div>
				<div class=col-2>
					<c:if test="${pvtdetail.fromNick == loggedUser.nick}">
						&nbsp;
					</c:if>
					<c:if test="${pvtdetail.fromNick != loggedUser.nick}">
						<a href="#" onclick="notifyUnread()" class="btn btn-flat">
							Da leggere
						</a>
					</c:if>
				</div>
				<div class=col-1>&nbsp;</div>
				<div class=col-2>
					<a href="Pvt?action=replyAll&id=${pvtdetail.id}" class="btn btn-flat">
						Rispondi
					</a>
				</div>
				<div class=col-1-2>&nbsp;</div>
			</div>
			<div class=row>&nbsp;</div>
		</div>
	</form>
</c:if>

<c:if test="${from == 'sendNew'}">
	<form method=post action=Pvt>
		<input type=hidden name=action value=>
		<input type=hidden name=toRemove value=>
		<div class=row>
			<div class=col-6>Destinatari</div>
		</div>
		<div class=row>
			<c:forEach var="recipient" items="${mobileRecipients}">
				<div class=col-1>
					<a href="#" onclick="removeRecipient('${recipient}')" class="btn btn-flat">
						${recipient} (Elimina)
					</a>
					<input type=hidden name=recipients value="${recipient}">
				</div>
				<div class=col-1-2>&nbsp;</div>
			</c:forEach>
			<div class=col-1>
				<c:if test="${fn:length(mobileRecipients) < 4}">
					<a href="#" onclick="addRecipient()" class="btn btn-flat">
						Aggiungi
					</a>
				</c:if>
			</div>
		</div>
		<div class=row>
			<div class=col-6>Titolo</div>
		</div>
		<div class=row>
			<div class=col-6>
				<c:choose>
					<c:when test="${not empty subject}">
						<input type=text name=subject value="${subject}">
					</c:when>
					<c:when test="${not empty mobileSubject}">
						<input type=text name=subject value="${mobileSubject}">
					</c:when>
					<c:otherwise>
						<input type=text name=subject>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class=row>
			<div class=col-6>Testo</div>
		</div>
		<div class=row>
			<div class=col-6>
				<c:choose>
					<c:when test="${not empty text}">
						<textarea name=text>${text}</textarea>
					</c:when>
					<c:when test="${not empty mobileText}">
						<textarea name=text>${mobileText}</textarea>
					</c:when>
					<c:otherwise>
						<textarea name=text></textarea>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class=row>
			<div class=col-1-2>&nbsp;</div>
			<div class=col-3>&nbsp;</div>
			<div class=col-2>
				<a href=# onclick="inviaPvt()" class="btn btn-flat">
					Invia
				</a>
			</div>
			<div class=col-1-2>&nbsp;</div>
		</div>
	</form>
</c:if>