<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<jsp:include page="incTop.jsp" />
<div id="main">

	<div class="userPanel">
		<div class="userPanelCaption">
			<c:out value="${poll.title}" escapeXml="true" />
		</div>
		
		<%-- Mostra la chart se almeno una persona ha votato --%>
		<c:set var="votes" value="0" />
		<c:forEach items="${poll.pollQuestions}" var="question">
			<c:set var="votes" value="${votes + question.votes}"/>
		</c:forEach>
		<c:if test="${votes > 0}">
			<div class="userPanelSection">
				<img class="pollPie" src="Polls?action=getChart&pollId=${poll.id}"/>
			</div>
		</c:if>
		
		<div class="userPanelSection">
			${pollText}
		</div>
		
		<%-- Mostra la possibilita' di votare solo ai registrati che non hanno ancora votato--%>
		<div class="userPanelSection">
			<c:choose>
				<c:when test="${not empty loggedUser}">
					<c:set var="canVote" value="true" />
					<c:forEach items="${poll.voterNicks}" var="nick">
						<c:if test="${nick eq loggedUser.nick}">
					    	<c:set var="canVote" value="false" />
						</c:if>
					</c:forEach>
					<c:choose>
						<c:when  test="${canVote == true}">
							<form action="Polls" method="POST" id="poll"> 
								<input type="hidden" name="action" value="answerPoll"/>
								<input type="hidden" name="pollId" value="${poll.id}"/>
								<c:forEach items="${poll.pollQuestions}" var="question" varStatus="index">
									<input type="radio" name="pollSequence" value="${index.index}">
									&nbsp;<c:out value="${question.text}" escapeXml="true" /><br/>
								</c:forEach>
								<input type="submit" value="Vota !"/>
								<div style="clear: both;"></div>
							</form>
						</c:when>
						<c:otherwise>
						Hai gi&agrave; votato
						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
				Solo gli utenti registrati possono postare
				</c:otherwise>
			</c:choose>
		</div>
		
	</div>
	
</div>

<div id="footer">
	<jsp:include page="incPrevNext.jsp" />
</div>
<jsp:include page="incBottom.jsp" />