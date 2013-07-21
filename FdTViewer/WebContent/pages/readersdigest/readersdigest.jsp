<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<div id="main" style="width: 100%;">

	<p class="digestTitle">The Troll's Digest</p>

	<c:forEach items="${articles}" var="article" varStatus="articleStatus">
		<% String style = ""; %>
		<c:if test="${articleStatus.index % 3 == 0 }">
			<% style = "vertical"; %>
			<c:if test="${articleStatus.index % 2 == 0 }">
				<% style += " msgOdd"; %>
			</c:if>
			<c:if test="${articleStatus.index % 2 == 1 }">
				<% style += " msgEven"; %>
			</c:if>
		</c:if>
		<c:if test="${articleStatus.index % 3 == 1 || articleStatus.index % 3 == 2 }">
			<% style = "horizontal"; %>
			<c:if test="${articleStatus.index % 3 == 1 }">
				<% style += " msgOdd"; %>
			</c:if>
			<c:if test="${articleStatus.index % 3 == 2 }">
				<% style += " msgEven"; %>
			</c:if>	
		</c:if>
		
		<div class="articleBox <%=style%>">
			<c:if test="${article.author != null}">
				<a href="User?action=getUserInfo&nick=${article.author}">${article.author}</a>
			</c:if>
			<c:if test="${article.author == null}">
				Un anonimo
			</c:if>
			ha scritto il thread
			<b>
				<a href="Threads?action=getByThread&threadId=${article.threadId}">${article.subject}</a>
			</b>
			a cui hanno partecipato
			<% boolean almostOneNick = false; %> 
			<c:forEach var="nick" items="${article.participants}">
				<c:if test="${article.author != null}">
					<a href="User?action=getUserInfo&nick=${nick}">${nick}</a>
					<% almostOneNick = true; %>
				</c:if>
			</c:forEach>
			<% if (!almostOneNick) { %>
				degli utenti anonimi.
			<% } else {%>
				.
			<% } %>
			<br>
			
			Il thread ha avuto ${article.nrOfMessages } risposte.
			<br>
			<br>
			
			<i>Il messaggio di apertura:</i>
			<br>
			<fdt:msg signature="false">${article.openerText}</fdt:msg>
			<br>
			<br>
			
			<c:if test="${article.openerText != article.excerpt }">
				<i>Una delle risposte più popolari:</i>
				<br>
				<fdt:msg signature="false">${article.excerpt}</fdt:msg>
				<br>
			</c:if>
			
		</div>
	</c:forEach>
</div>