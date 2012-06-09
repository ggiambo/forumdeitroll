<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="incTop.jsp"/>
		<div id="main">
			<div class="modInfoForm">
				<h2>Moderazione ${modInfo.m_id}</h2>
				<p>Utente: ${modInfo.authorDescription}</p>
				<p>IP: ${modInfo.ip} TOR: ${modInfo.tor}</p>
				<p>
				<c:if test="${not empty comm}">
					<p>${comm}</p>
				</c:if>
				<c:url var="banMessageUrl" value="">
					<c:param name="action" value="banMessage"/>
					<c:param name="m_id" value="${modInfo.m_id}"/>
					<c:param name="token" value="${anti_xss_token}"/>
				</c:url>
				<c:url var="banUserUrl" value="">
					<c:param name="action" value="banUser"/>
					<c:param name="m_id" value="${modInfo.m_id}"/>
					<c:param name="token" value="${anti_xss_token}"/>
				</c:url>
				<c:url var="banIPUrl" value="">
					<c:param name="action" value="banIP"/>
					<c:param name="m_id" value="${modInfo.m_id}"/>
					<c:param name="token" value="${anti_xss_token}"/>
				</c:url>
				<a href="${banMessageUrl}">Cancella messaggio</a>
				<a href="${banUserUrl}">Ban utente</a>
				<a href="${banIPUrl}">Ban IP</a>
				</p>
			</div>
		</div>
		<div id="footer"></div>
<jsp:include page="incBottom.jsp"/>