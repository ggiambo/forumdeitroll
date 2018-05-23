<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>

<div class=userInfoBox>
	<div class=row>
		<div class=col-1>
			<img src="Misc?action=getAvatar&amp;&nick=${author.nick}" class=avatar>
		</div>
		<div class=col-5>
			<b>${author.nick}</b>
			<br>
			<a href="Messages?action=getByAuthor&amp;author=${author.nick}">${author.messages} messaggi</a>
		</div>
	</div>
	
	<c:if test="${not empty quotes}">
		<div class=row>
			<div class=col-6>
				Frasi celebri
			</div>
		</div>
		<div class=row>
			<div class=col-6>
				<ul class=quotes>
					<c:forEach items="${quotes}" var="item">
						<li>${fn:escapeXml(item.content)}</li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</c:if>
	<div class="row sep"></div>
</div>