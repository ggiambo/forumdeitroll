<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html>
	<jsp:include page="incHead.jsp"/>
	<body>
		<div id="body"> <!-- non mettere nulla prima del div #body -->
		<jsp:include page="incHeader.jsp"/>
		<jsp:include page="incNav.jsp"/>
		<c:set var="bodyContentClass" value="" scope="page" />
		<c:if test="${param.forum == 'Proc di Catania'}">
			<c:set var="bodyContentClass" value="pb" scope="page" />  
		</c:if>
		<div id="bodyContent" class="<c:out value='${bodyContentClass}' />">
		<c:remove var="bodyContentClass" scope="page" />
			<jsp:include page="incSidebar.jsp"/>