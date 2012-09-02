<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<div id="main">

	<div class="userPanel">
		<div class="userPanelCaption">
			<c:out value="${poll.title}" escapeXml="true" />
		</div>
		
		<%-- Mostra la chart se almeno una persona ha votato --%>
		<c:if test="${not empty poll.voterNicks}">
			<div class="userPanelSection">
				<img class="pollPie" src="Polls?action=getChart&pollId=${poll.id}"/>
			</div>
		</c:if>
		
		<div class="userPanelSection">
			${pollText}
		</div>
		
		<%-- Mostra la possibilita' di votare solo ai registrati che non hanno ancora votato --%>
		<div class="userPanelSection">
			<form action="Polls" method="POST" id="poll"> 
				<input type="hidden" name="action" value="answerPoll"/>
				<input type="hidden" name="pollId" value="${poll.id}"/>
				<c:forEach items="${poll.pollQuestions}" var="question" varStatus="index">
					<c:choose>
						<c:when test="${canVote == true}">
							<input type="radio" name="pollSequence" value="${index.index}">
						</c:when>
						<c:otherwise>
							-
						</c:otherwise>
					</c:choose>
					&nbsp;<c:out value="${question.text}" escapeXml="true" /><br/>
				</c:forEach>
				<c:if test="${canVote == true}">
					<input type="submit" value="Vota !"/>
				</c:if>
				<div style="clear: both;"></div>
			</form>
		</div>
		
	</div>
	
</div>

<div id="footer">
	<c:if test="${!empty page}">
		<fdt:pager handler="Messages"></fdt:pager>
	</c:if>
</div>
