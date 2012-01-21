<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<jsp:include page="incTop.jsp" />
		<div id="main">
			<div class="userPanel">
				<div class="userPanelCaption">Pannello Utente - Messaggi Privati</div>
				<div class="userPanelContent">
					<div class="userPanelSection">
						<h3>Invia Messaggio Privato</h3>
						<form action="Pvt" method="POST">
							<input type="hidden" name="action" value="sendPvt">
							<div><label for="subject">Oggetto:</label><br />
							<input type="text" name="subject" id="subject" /></div>
							<div><textarea name="text" id="text"></textarea></div>
							<div><label for="recipient1">Primo Destinatario:</label><br />
							<input type="text" name="recipient" id="recipient1" /></div>
							<div><label for="recipient2">Secondo Destinatario:</label><br />
							<input type="text" name="recipient" id="recipient2" /></div>
							<div><label for="recipient3">Terzo Destinatario:</label><br />
							<input type="text" name="recipient" id="recipient3" /></div>
							<div><label for="recipient4">Quarto Destinatario:</label><br />
							<input type="text" name="recipient" id="recipient4" /></div>
							<div><label for="recipient5">Quinto Destinatario:</label><br />
							<input type="text" name="recipient" id="recipient5" /></div>
							<input type="submit" value="Invia" class="sendPvt" />
						</form>
					</div> <%-- /Section --%>
					<div class="userPanelSection">
						<a href="Pvt?action=outbox">inviati</a>
						<h3>Ricevuti</h3>
						<c:if test="${not empty pvts}">
							<table width="100%">
								<c:forEach items="${pvts}" var="pvt" varStatus="index">
									<tr>
										<td>
											<a href="Pvt?action=show&amp;id=${pvt.id}">${pvt.fromNick}</a>
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
						<c:choose>
							<c:when test="${pvtdetail != null}">
								<p>${pvtdetail.subject}</p>
								<div style="padding: 10px;" class="pvtMessage">
									<fdt:msg search="">${pvtdetail.text}</fdt:msg>
								</div>
								<p>${pvtdetail.date}</p>
							</c:when>
						</c:choose>
					</div> <%-- /Section --%>
				</div> <%-- /Content --%>
			</div> <%-- /Panel --%>
		</div> <%-- /Main --%>

		<div id="footer">
			<jsp:include page="incPrevNext.jsp" />
		</div>
<jsp:include page="incBottom.jsp" />