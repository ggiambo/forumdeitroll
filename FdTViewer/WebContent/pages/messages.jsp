<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<jsp:include page="incTop.jsp" />
<div id="main">
	<fdt:delayedScript dump="false">
		var token = "${anti_xss_token}";
	</fdt:delayedScript>
	
	<c:forEach items="${messages}" var="msg" varStatus="index">
		<c:choose>
			<c:when test="${msg.visible}">
				<c:set var="messagesBoxClass" value="messagesBox"/>
			</c:when>
			<c:otherwise>
				<c:set var="messagesBoxClass" value="messagesBoxInvisible"/>
			</c:otherwise>
		</c:choose>
		<div class="${messagesBoxClass}">
			<c:set var="msg" value="${msg}" scope="request"/>
			<c:set var="index" value="${index.count}" scope="request"/>
			<jsp:include page="incMessage.jsp"/>
		</div>
	</c:forEach> 
</div>

<div id="footer">
	<c:if test="${!empty page}">
		<fdt:pager handler="Messages"></fdt:pager>
	</c:if>
</div>
<jsp:include page="incBottom.jsp" />