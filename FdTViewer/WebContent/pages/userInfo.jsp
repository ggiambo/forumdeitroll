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
				<b>Frasi Celebri:</b><br/>
				<c:forEach items="${quotes}" var="item">
					<i>${fn:escapeXml(item.content)}</i><br/>
				</c:forEach>
			</div>

		</div>
		
		<div id="footer"></div>
<jsp:include page="incBottom.jsp" />
