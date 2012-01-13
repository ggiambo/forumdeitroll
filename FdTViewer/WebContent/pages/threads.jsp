<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:include page="incTop.jsp"/>
		<div id="main">

			<c:forEach items="${messages}" var="thread" varStatus="index">
				<c:choose>
					<c:when test="${index.count % 2 == 0}">
						<c:set var="class" value="msgEven"/>
					</c:when>
					<c:otherwise>
						<c:set var="class" value="msgOdd"/>
					</c:otherwise>
				</c:choose>
				<div class="${class}">
					<c:if test="${!empty thread.forum}">
						<span style="color:#97A28A"><b>${thread.forum}</b></span>
					</c:if>
					<b><a href="Threads?action=getByThread&threadId=${thread.id}"/>${thread.subject}</a></b>
					 (${thread.numberOfMessages}
					<c:choose>
						<c:when test="${thread.numberOfMessages > 1}">
							messaggi)
						</c:when>
						<c:otherwise>
							messaggio)
						</c:otherwise>
					</c:choose>
					<div class="threadDetail">
					- Iniziato da
					<span class="msgAuthor">
						<c:choose>
							<c:when test="${empty thread.author}">
								Non Autenticato
							</c:when>
							<c:otherwise>
								<b><a href="Messages?action=getByAuthor&author=${thread.author}">${thread.author}</a></b>
							</c:otherwise>
						</c:choose>
					</span>
					alle <fmt:formatDate value="${thread.date}" pattern="dd.MM.yyyy HH:mm"/>
					</div>
				</div>
				<hr/>
			</c:forEach>

		</div>

		<div id="footer">
			<jsp:include page="incPrevNext.jsp" />
		</div>
<jsp:include page="incBottom.jsp" />