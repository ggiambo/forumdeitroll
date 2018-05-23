<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<div class="userPanelSection">
	<c:if test="${from == 'inbox' }">
		<h3>Ricevuti</h3>
	</c:if>
	<c:if test="${from == 'outbox' }">
		<h3>Inviati</h3>
	</c:if>
	<c:if test="${empty pvts}">
		<p>Non c'è nessun messaggio qui <img src="images/emo/10.gif" alt="" class="emoticon" /></p>
	</c:if>
	<c:if test="${not empty pvts}">
		<table class="pvtMessages">
			<tbody>
				<c:forEach items="${pvts}" var="pvt" varStatus="index">
					<c:if test="${pvt.read}">
						<c:set var="pvtTrClass" value="pvtRead" />
					</c:if>
					<tr class="${pvtTrClass}">
						<td class="pvtStatus">
							<c:choose>
								<c:when test="${pvt.read}">
									<img src="images/email_open.png" alt="Messaggio già letto" title="Old :(" />
								</c:when>
								<c:otherwise>
									<img src="images/email.png" alt="Messaggio da leggere" title="Leggimi!" />
								</c:otherwise>
							</c:choose>
						</td>
						<td class="pvtNickname">
							<c:if test="${from == 'inbox' }">
								<a href="Pvt?action=show&amp;id=${pvt.id}">${pvt.fromNick}</a>
							</c:if>
							<c:if test="${from == 'outbox' }">
								<c:forEach items="${pvt.toNick}" var="destNick" varStatus="index">
									<c:url value="User" var="destNickURL">
										<c:param name="action" value="getUserInfo"/>
										<c:param name="nick" value="${destNick.nick}"/>
									</c:url>
									<c:if test="${destNick.read }">
										<a href="<c:out value="${destNickURL}" escapeXml="true" />">
											<c:out value="${destNick.nick }"/>
										</a>
									</c:if>
									<c:if test="${! destNick.read }">
										<b>
											<a href="<c:out value="${destNickURL}" escapeXml="true" />">
												<c:out value="${destNick.nick }"/>
											</a>
										</b>
									</c:if>
								</c:forEach>
							</c:if>
						</td>
						<td class="pvtSubject">
							<a href="Pvt?action=show&amp;id=${pvt.id}"><c:out value="${pvt.subject}" escapeXml="true"/></a>
						</td>
						<td class="pvtDate">
							<a href="Pvt?action=show&amp;id=${pvt.id}"><fmt:formatDate value="${pvt.date}" pattern="dd.MM.yyyy"/>&nbsp;<fmt:formatDate value="${pvt.date}" pattern="HH:mm"/></a>
						</td>
						<td class="pvtAction">
							<a href="Pvt?action=delete&amp;id=${pvt.id}&amp;from=${from}&amp;page=${param['page']}" title="Cancella messaggio"><img src="images/delete.png" alt="Cancella" /></a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	<fdt:pager handler="pvt"/>
</div>