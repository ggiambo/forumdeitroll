<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%! static final long bootTime = System.currentTimeMillis(); %>

<style>

@media screen and (orientation:landscape) {
	.header {
		display: none;
	}
	.main {
		margin-top: .5em;
	}
}

</style>

<input type=hidden id=messageId value="${messageId}">
<input type=hidden id=replyToId value="${replyToId}">

<div class=row>
	<div class=col-1>Forum</div>
	<div class=col-5>
		<c:choose>
			<c:when test="${replyToId != -1 or messageId != -1}">
				<input type=hidden value="${forum}" id=forum>
				<c:if test="${not empty forum}">
					<input type=text value="${forum}" disabled="disabled">
				</c:if>
				<c:if test="${empty forum}">
					<input type=text value="Forum dei Troll" disabled="disabled">
				</c:if>
			</c:when>
			<c:otherwise>
				<select id=forum>
					<option value="">Forum dei Troll</option>
					<c:forEach var="forumName" items="${forums}">
						<option value="${forumName}">${forumName}</option>
					</c:forEach>
				</select>
			</c:otherwise>
		</c:choose>
	</div>
</div>
<div class=row>&nbsp;</div>
<div class=row>
	<div class=col-6>Oggetto</div>
</div>
<div class=row>
	<div class=col-6>
		<input id=subject type=text value="${subject}" maxlength="<%=com.forumdeitroll.servlets.Messages.MAX_SUBJECT_LENGTH%>">
	</div>
</div>
<div class=row>&nbsp;</div>
<div class=row>
	<div class=col-6>Testo</div>
</div>
<div class=row>
	<div class=col-6>
		<textarea id=text>${text}</textarea>
		<div id=preview></div>
	</div>
</div>
<div class=row>&nbsp;</div>
<div class=row>
	<div class=col-1>User</div>
	<div class=col-1>
		<input type=text id=username value="${username}">
	</div>
	<div class=col-1>&nbsp;</div>
	<div class=col-1>Pass</div>
	<div class=col-1>
		<input type=password id=password value="">
	</div>
	<div class=col-1>&nbsp;</div>
</div>
<div class=row>&nbsp;</div>
<div class=row>
	<div class=col-1>Captcha</div>
	<div class=col-5><img src="Misc?action=getCaptcha&amp;v=<%=System.currentTimeMillis()%>"></div>
</div>
<div class=row>
	<div class=col-1>&nbsp;</div>
	<div class=col-2><input type=text id=captcha></div>
	<div class=col-3>&nbsp;</div>
</div>
<div class=row>&nbsp;</div>
<div class=row>
	<div class=col-1-2>&nbsp;</div>
	<div class=col-2>
		<a href="javascript:previewMessage()" class="btn btn-flat">Preview</a>
	</div>
	<div class=col-1>&nbsp;</div>
	<div class=col-2>
		<c:if test="${warnTorUser != null}">
			<a href="javascript:alert('Post con Tor temporaneamente inibito.')" class="msgSendButton" style="background-color: red" >BLOCCATO</a>
		</c:if>
		<c:if test="${warnTorUser == null}">
			<a href="javascript:sendMessage()" class="btn btn-flat">Invia</a>
		</c:if>
	</div>
	<div class=col-1-2>&nbsp;</div>
</div>
<%-- profiler richiede jquery... --%>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script src="js/PluginDetect_All.js" type="text/javascript"></script>
<script src="js/profiler.js?v=<%=bootTime%>" type="text/javascript"></script>

<fdt:delayedScript dump="false">
	var token = "${anti_xss_token}";
</fdt:delayedScript>