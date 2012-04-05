<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<jsp:include page="incTop.jsp" />
<div id="main">
	
	<c:forEach items="${polls}" var="poll" varStatus="index">
		<c:choose>
			<c:when test="${index.count % 2 == 0}">
				<c:set var="class" value="pollEven"/>
			</c:when>
			<c:otherwise>
				<c:set var="class" value="pollOdd"/>
			</c:otherwise>
		</c:choose>
		<div id="poll_${poll.id}" class="${class} pollBox">
			<span class="pollTitle">
				<a href="Polls?action=getPollContent&pollId=${poll.id}">${poll.title}</a>
			</span>
			- ${fn:length(poll.pollQuestions)} domande, 
			<c:set var="votes" value="${fn:length(poll.voterNicks)}" />
			${votes} 
			<c:choose>
				<c:when test="${votes == 1}">
					voto
				</c:when>
				<c:otherwise>
					voti
				</c:otherwise>
			</c:choose>
			<div class="pollDetail">
			Iniziato da ${poll.author}
			il <fmt:formatDate value="${poll.creationDate}" pattern="dd.MM.yyyy"/> alle <fmt:formatDate value="${poll.creationDate}" pattern="HH:mm"/>
			<br/>
			Ultimo aggiornamento il <fmt:formatDate value="${poll.updateDate}" pattern="dd.MM.yyyy"/> alle <fmt:formatDate value="${poll.updateDate}" pattern="HH:mm"/>
			</div>
		</div>
	</c:forEach> 
	
	
</div>

<div id="footer">
	<jsp:include page="incPrevNext.jsp" />
</div>
<jsp:include page="incBottom.jsp" />