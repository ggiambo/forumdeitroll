<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="incTop.jsp"/>
		<div id="main">
			<div class="modInfoForm">
				<h2>Moderazione ${modInfo.m_id}</h2>
				<p>Utente: ${modInfo.authorDescription}</p>
				<p>IP: ${modInfo.ip} TOR: ${modInfo.tor}</p>
				<!-- TODO: bottone per bannare utente e per cancellare messaggio  -->
			</div>
		</div>
		<div id="footer"></div>
<jsp:include page="incBottom.jsp"/>