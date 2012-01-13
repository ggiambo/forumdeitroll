<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>

<c:if test="${!empty pageNr}">
	<c:if test="${pageNr gt 0}">
		<a href="?action=${action}&pageNr=${pageNr - 1}${specificParams}"><img src="images/left_arrow.gif"/></a>&nbsp;
	</c:if>
	 Pagina ${pageNr + 1}
	 <c:if test="${fn:length(messages) gt PAGE_SIZE - 1}">
		&nbsp;<a href="?action=${action}&amp;pageNr=${pageNr + 1}${specificParams}"><img src="images/right_arrow.gif"/></a>
	</c:if>
</c:if>
