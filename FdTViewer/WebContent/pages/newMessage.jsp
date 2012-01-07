<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	
	<jsp:include page="incHead.jsp"/>

	<body>

		<div id="header"><h1>FdT Due Zero !</h1></div>
		
		<jsp:include page="incNav.jsp"/>
		
		<jsp:include page="incSidebar.jsp"/>

		<div id="main">
			<jsp:include page="incReplyMessage.jsp"/>
		</div>

		<div id="footer">
			<jsp:include page="incPrevNext.jsp" />
		</div>
		
		<c:if test="${currentTimeMillis != null}">
			<!--time: <%=System.currentTimeMillis() - (Long)request.getAttribute("currentTimeMillis")%> -->
		</c:if>

	</body>
</html>
