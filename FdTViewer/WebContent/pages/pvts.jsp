<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<jsp:include page="incTop.jsp" />
		<div id="main">
			<form action="Pvt" method="POST">
				<input type="hidden" name="action" value="sendPvt">
				<input type="text" name="subject"><br>
				<textarea name="text"></textarea><br>
				<input type="text" name="recipient">
				<input type="text" name="recipient">
				<input type="text" name="recipient">
				<input type="text" name="recipient">
				<input type="text" name="recipient">
				<input type="submit" value="invia"/>
			</form>
			<a href="Pvt?action=outbox">inviati</a>
			<c:if test="${not empty pvts}">
				<table width="100%">
					<c:forEach items="${pvts}" var="pvt" varStatus="index">
						<tr>
							<td>
								<a href="Pvt?action=show&id=${pvt.id}">${pvt.fromNick}</a>
							</td>
							<td>
								<a href="Pvt?action=show&id=${pvt.id}">${pvt.subject}</a>
							</td>
							<td>
								<a href="Pvt?action=show&id=${pvt.id}">${pvt.date}</a>
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
								<a href="Pvt?action=delete&id=${pvt.id}">cancella</a>
							</td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
			<c:choose>
				<c:when test="${pvtdetail != null}">
					<p>${pvtdetail.subject}</p>
					<div style="padding: 10px;" class="message">
						<fdt:msg search="">${pvtdetail.text}</fdt:msg>
					</div>
					<p>${pvtdetail.date}</p>
				</c:when>
			</c:choose>
		</div>

		<div id="footer">
			<jsp:include page="incPrevNext.jsp" />
		</div>
<jsp:include page="incBottom.jsp" />