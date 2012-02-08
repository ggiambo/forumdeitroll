<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<c:if test="${!empty pageNr}">
	<fdt:pager handler="Messages"></fdt:pager>
</c:if>
<%--
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>

<c:if test="${!empty pageNr}">
	<c:if test="${pageNr gt 0}">
		<c:url value="" var="prevLink">
			<c:param name="action" value="${action}"/>
			<c:param name="pageNr" value="${pageNr - 1}"/>
			<c:forEach items="${specificParams}" var="sParam">
				<c:param name="${sParam.key}" value="${sParam.value}"/>
			</c:forEach>
		</c:url>
		<a title="Pagina Precedente" href="<c:out value="${prevLink}" escapeXml="true"/>"><img src="images/left_arrow.gif" alt="Pagina Precedente" /></a>&nbsp;
	</c:if>
	
	Pagina ${pageNr + 1}
	
	<c:if test="${fn:length(messages) gt PAGE_SIZE - 1}">
		<c:url value="" var="nextLink">
			<c:param name="action" value="${action}"/>
			<c:param name="pageNr" value="${pageNr + 1}"/>
			<c:forEach items="${specificParams}" var="sParam">
				<c:param name="${sParam.key}" value="${sParam.value}"/>
			</c:forEach>
		</c:url>
		&nbsp;<a title="Pagina Successiva" href="<c:out value="${nextLink}" escapeXml="true"/>"><img src="images/right_arrow.gif" alt="Pagina Successiva" /></a>
	</c:if>
	
</c:if>
--%>