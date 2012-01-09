<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	
	<jsp:include page="incHead.jsp"/>

	<body>

		<jsp:include page="incHeader.jsp"/>
		
		<jsp:include page="incNav.jsp"/>
		
		<div id="main">
			<div style="margin:5px; padding:5px; width:100%">
				<form action="User" method="post">
					<label for="nick">Name:</label>
					<input name="nick" />
					<br/>
					<label for="pass">Pass:</label>
					<input type="password" name="pass"/>
					<input type="submit" value="Login"/>
				</form>
			</div>
		</div>
		
		<div id="footer"></div>
		
		<c:if test="${currentTimeMillis != null}">
			<!--time: <%=System.currentTimeMillis() - (Long)request.getAttribute("currentTimeMillis")%> -->
		</c:if>

		<div style="clear: both;"></div>

	</body>
</html>