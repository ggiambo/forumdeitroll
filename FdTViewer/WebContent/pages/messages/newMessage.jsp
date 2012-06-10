<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<jsp:include page="../incTop.jsp" />
	<div id="main">
		<jsp:include page="incReplyMessage.jsp"/>
	</div>

	<div id="footer">
		<c:if test="${!empty page}">
			<fdt:pager handler="Messages"></fdt:pager>
		</c:if>
	</div>
<jsp:include page="../incBottom.jsp" />