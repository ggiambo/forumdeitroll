<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>

<c:if test="${!empty pageNr}">
	<c:if test="${pageNr gt 0}">
		<a href="?action=${action}&pageNr=${pageNr - 1}${specificParams}">&lt;&lt;</a>
	</c:if>
	 &nbsp;Pagina ${pageNr + 1}
	 <c:if test="${fn:length(messages) gt PAGE_SIZE - 1}">
		<a href="?action=${action}&pageNr=${pageNr + 1}${specificParams}">&gt;&gt;</a>
	</c:if>
</c:if>
