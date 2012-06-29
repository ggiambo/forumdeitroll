<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="com.forumdeitroll.servlets.Polls"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<jsp:include page="../incTop.jsp" />

<fdt:delayedScript dump="false">
	$(document).ready(function() {
		$("#addQuestion").click(function() {
			var questions = $("#questionsDiv input").length;
			if (questions == 9) {
				$("#addQuestion").hide();
			}
			var text = "question_" + questions;
			var newInput = $("#questionsDiv input:last").clone();
			newInput.val("").attr("name", text).attr("id", text);
			var newLabel = $("<label></label>").attr("for", text).html("Domanda " + (questions + 1) + ":");
			$("#questionsDiv").append(newLabel);
			$("#questionsDiv").append(newInput);
		});
	});
</fdt:delayedScript>

<div id="main">
	<div class="userPanel">
		<div class="userPanelCaption">Nuovo sondaggio</div>
		<div class="userPanelContent">

			<form action="Polls" method="POST" id="poll"> 
				<input type="hidden" name="action" value="insertPoll"/>
				<label for="title">Titolo:</label>
				<br/>
				<input tabindex="1" name="title" id="title" maxlength="<%=Polls.MAX_TITLE_LENGTH %>" value="<c:out value="${title}" escapeXml="true"/>"/>
				<br/>
				<textarea name="text" id="text" rows="10">${text}</textarea>
			
				<div id="questionsDiv">
					<c:forEach items="${questions}" var="question" varStatus="index" >
						<label for="question_${index.index}">Domanda ${index.count}:</label>
						<input name="question_${index.index}" id="question_${index.index}" maxlength="<%=Polls.MAX_QUESTION_LENGTH %>" value="<c:out value="${question.text}" escapeXml="true"/>"/>
					</c:forEach>
				</div>
				<input type="submit" value="Invia"/>
			</form>
			
			<img src="images/add.png" id="addQuestion" alt="Aggiungi domanda"/>
		</div>
	</div>
 
</div>

<div id="footer">
	<c:if test="${!empty page}">
		<fdt:pager handler="Messages"></fdt:pager>
	</c:if>
</div>
<jsp:include page="../incBottom.jsp" />
