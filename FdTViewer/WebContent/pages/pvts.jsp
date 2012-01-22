<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<jsp:include page="incTop.jsp" />
		<div id="main">
			<div class="userPanel">
				<div class="userPanelCaption">Pannello Utente - Messaggi Privati</div>
				<div class="userPanelContent">
					<a href="?action=inbox">Ricevuti</a>
					|
					<a href="?action=outbox">Inviati</a>
					|
					<a href="?action=sendNew">Scrivi nuovo</a>
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
								<p>Non c'è nessun messaggio qui <img src="images/emo/10.gif">
							</c:if>
							<c:if test="${not empty pvts}">
								<table width="100%">
									<c:forEach items="${pvts}" var="pvt" varStatus="index">
										<tr>
											<td>
												<c:if test="${from == 'inbox' }">
													<a href="Pvt?action=show&amp;id=${pvt.id}">${pvt.fromNick}</a>
												</c:if>
												<c:if test="${from == 'outbox' }">
													<c:forEach items="${pvt.toNick}" var="destNick" varStatus="index">
														<a href="User?action=getUserInfo&nick=${destNick }">
															<c:out value="${destNick }"/>
														</a>
													</c:forEach>
												</c:if>
											</td>
											<td>
												<a href="Pvt?action=show&amp;id=${pvt.id}">${pvt.subject}</a>
											</td>
											<td>
												<a href="Pvt?action=show&amp;id=${pvt.id}">${pvt.date}</a>
											</td>
											<td>
												<c:choose>
													<c:when test="${pvt.read}">
														letto
													</c:when>
													<c:otherwise>
														da leggere
													</c:otherwise>
												</c:choose>
											</td>
											<td>
												<a href="Pvt?action=delete&amp;id=${pvt.id}">cancella</a>
											</td>
										</tr>
									</c:forEach>
								</table>
							</c:if>
						</div>
					</c:if>
					<c:if test="${from == 'sendNew' }">
						<div class="userPanelSection">
							<h3>Invia Messaggio Privato</h3>
							<form action="Pvt" method="POST">
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
						</div> <%-- /Section --%>
					</c:if>
					<c:if test="${from == 'show' }">
						<div class="userPanelSection">
							<p>Scritto da <a href="User?action=getUserInfo&nick=${pvtdetail.fromNick }">${pvtdetail.fromNick }</a></p>
							<p>Inviato a
								<c:forEach items="${pvtdetail.toNick}" var="destNick" varStatus="index">
									<a href="User?action=getUserInfo&nick=${destNick }">
										<c:out value="${destNick }"/>
									</a>
								</c:forEach>
							<div style="padding: 10px;" class="message">
								<fdt:msg>${pvtdetail.text }</fdt:msg>
							</div>
						</div>
					</c:if>
				</div> <%-- /Content --%>
			</div> <%-- /Panel --%>
		</div> <%-- /Main --%>

		<div id="footer">
			<jsp:include page="incPrevNext.jsp" />
		</div>
<jsp:include page="incBottom.jsp" />