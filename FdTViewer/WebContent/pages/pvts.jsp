<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="incTop.jsp" />
		<div id="main">
			<div class="userPanel">
				<div class="userPanelCaption">Pannello Utente - Messaggi Privati</div>
				<div class="userPanelContent">
					<a href="Pvt?action=inbox">Ricevuti</a>
					|
					<a href="Pvt?action=outbox">Inviati</a>
					|
					<a href="Pvt?action=sendNew">Scrivi nuovo</a>
				</div>
				<div class="userPanelContent">
					<c:if test="${from == 'inbox' || from == 'outbox'}">
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
											<c:choose>
												<c:when test="${pvt.read}">
													<tr class="pvtRead">
												</c:when>
												<c:otherwise>
													<tr>
												</c:otherwise>
											</c:choose>
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
																<c:param name="nick" value="${destNick}"/>
															</c:url>
															<a href="<c:out value="${destNickURL}" escapeXml="true" />">
																<c:out value="${destNick }"/>
															</a>
														</c:forEach>
													</c:if>
												</td>
												<td class="pvtSubject">
													<a href="Pvt?action=show&amp;id=${pvt.id}">${pvt.subject}</a>
												</td>
												<td class="pvtDate">
													<a href="Pvt?action=show&amp;id=${pvt.id}"><fmt:formatDate value="${pvt.date}" pattern="dd/MM/yyyy"/></a>
												</td>
												<td class="pvtAction">
													<a href="Pvt?action=delete&amp;id=${pvt.id}&amp;from=${from}" title="Cancella messaggio"><img src="images/delete.png" alt="Cancella" /></a>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</c:if>
							<fdt:pager handler="pvt"/>
						</div>
					</c:if>
					<c:if test="${from == 'sendNew' }">
						<div class="userPanelSection">
							<h3>Invia Messaggio Privato</h3>
							<form action="Pvt" method="POST" class="pvtSendMessage">
								<input type="hidden" name="action" value="sendPvt">
								<div><label for="subject">Oggetto:</label><br />
								<input type="text" name="subject" id="subject" value="${subject }"/></div>
								<div><textarea name="text" id="text" rows="5" cols="32">${text }</textarea></div>
								<div><label for="recipient1">Primo Destinatario:</label><br />
								<input type="text" name="recipient" id="recipient1" value="${recipient[0] }" /></div>
								<div><label for="recipient2">Secondo Destinatario:</label><br />
								<input type="text" name="recipient" id="recipient2" value="${recipient[1] }"/></div>
								<div><label for="recipient3">Terzo Destinatario:</label><br />
								<input type="text" name="recipient" id="recipient3" value="${recipient[2] }"/></div>
								<div><label for="recipient4">Quarto Destinatario:</label><br />
								<input type="text" name="recipient" id="recipient4" value="${recipient[3] }"/></div>
								<div><label for="recipient5">Quinto Destinatario:</label><br />
								<input type="text" name="recipient" id="recipient5" value="${recipient[4] }"/></div>
								<input type="submit" value="Invia" class="sendPvt" />
							</form>
							<div style="clear: both;"></div>
						</div> <%-- /Section --%>
					</c:if>
					<c:if test="${from == 'show' }">
						<div class="userPanelSection">
							<p>Scritto da: 
								<c:url value="User" var="fromNickURL">
									<c:param name="action" value="getUserInfo"/>
									<c:param name="nick" value="${pvtdetail.fromNick}"/>
								</c:url>
								<a href="<c:out value="${fromNickURL}" escapeXml="true" />" class="pvtNickname">${pvtdetail.fromNick}</a>
							</p>
							<p>Inviato a:
								<c:forEach items="${pvtdetail.toNick}" var="destNick" varStatus="index">
									<c:url value="User" var="destNickURL">
										<c:param name="action" value="getUserInfo"/>
										<c:param name="nick" value="${destNick}"/>
									</c:url>
									<a href="<c:out value="${destNickURL}" escapeXml="true" />" class="pvtNickname">
										<c:out value="${destNick}"/>
									</a>
								</c:forEach>
							</p>
							<div class="pvtTextMessage">
								<fdt:msg author="${sender}">${pvtdetail.text }</fdt:msg>
							</div>
							<c:if test="${pvtdetail.fromNick != loggedUser.nick}">
								<a href="Pvt?action=reply&amp;id=${pvtdetail.id}" class="pvtRispondiBtn">Rispondi</a>
								<div style="clear: both;"></div>
							</c:if>
						</div>
					</c:if>
					<div class="userPanelSection">
						<h3>Altre Azioni</h3>
						<a href="./User" class="userPanelButton">User Panel</a>
						<a href="./User?action=getQuotes" class="userPanelButton">Frasi Celebri</a>
						<div style="clear: both;"></div>
					</div>
				</div> <%-- /Content --%>
			</div> <%-- /Panel --%>
		</div> <%-- /Main --%>

		<div id="footer">
			<jsp:include page="incPrevNext.jsp" />
		</div>
<jsp:include page="incBottom.jsp" />