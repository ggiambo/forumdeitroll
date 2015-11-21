<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<c:set var="message" value="${root}" scope="request"/>
<c:set var="depth" value="0" scope="request"/>
<c:set var="index" value="0" scope="request"/>

<c:choose>
	<c:when test="${depth > 15}">
		<c:set var="level" value="0"/>
	</c:when>
	<c:otherwise>
		<c:set var="level" value="${depth mod 16}"/>
	</c:otherwise>
</c:choose>

<jsp:include page="incThread.jsp"/>
