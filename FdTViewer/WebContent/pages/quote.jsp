<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<jsp:include page="incTop.jsp" />
		<div id="main">
			<div style="margin:5px; padding:5px; width:100%">
				<table>
					<tr>
						<td>
							<img src="?action=getAvatar&nick=${author.nick}"/>
						</td>
						<td valign="top">
							Nick: ${author.nick}<br/>
							Messaggi: ${author.messages }
						</td>
					</tr>
				</table>
				<br/>
				<a href="?action=getQuotes">Gestisci frasi celebri</a>
<%-- TODO
				|
				<a href="#">Manda messagio privato</a>
--%>
				<br/>
				<br/>
				<form action="User?action=updateQuote" method="post">
					<input type="hidden" id="quoteId" name="quoteId"/>
					<c:forEach items="${quote}" var="item" varStatus="i">
						<c:choose>
						<c:when test="${item.id > 0}">
							<a href="User?action=removeQuote&quoteId=${item.id}" style="text-decoration:none;">
								<img src="images/close.jpeg"/>
							</a>
							</c:when>
							<c:otherwise>
								<img src="images/close.jpeg"/>
							</c:otherwise>
						</c:choose>
						<input name="quote_${item.id}" value="${fn:escapeXml(item.content)}" maxlength="100" size="50"/><input type="button" value="Salva" onClick="$('#quoteId').val(${item.id});submit();"><br/>
					</c:forEach>
				</form>
			</div>

		</div>
		
		<div id="footer"></div>
<jsp:include page="incBottom.jsp" />
