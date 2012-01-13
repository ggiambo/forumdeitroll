<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="incTop.jsp" />
		<div id="main">
			<c:forEach items="${messages}" var="msg" varStatus="index">
				<div class="messagesBox">
					<c:set var="msg" value="${msg}" scope="request"/>
					<c:set var="index" value="${index}" scope="request"/>
					<jsp:include page="incMessage.jsp"/>
				</div>
			</c:forEach>
		</div>

		<div id="footer">
			<jsp:include page="incPrevNext.jsp" />
		</div>
<jsp:include page="incBottom.jsp" />