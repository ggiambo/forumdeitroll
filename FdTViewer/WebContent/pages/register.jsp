<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	
	<jsp:include page="incHead.jsp"/>

	<body>

		<jsp:include page="incHeader.jsp"/>
		
		<jsp:include page="incNav.jsp"/>
		
		<div id="main">
			<div style="margin:5px; padding:5px; width:100%">
				<form action="User?action=registerNewUser" method="post">
					<label for="nick">Name:</label>
					<input tabindex="1" name="nick" value="${nick}"/>
					<br/>
					<label for="pass">Pass:</label>
					<input tabindex="2" type="password" name="pass"/>
					<br/>
					<label for="captcha">Captcha</label>
					<input tabindex="3" name="captcha" size="5"/>
					<input tabindex="4" type="submit" value="Registra"/>
				</form>
				<img style="margin-left:100px" src="Messages?action=getCaptcha"/>
			</div>
		</div>
		
		<div id="footer"></div>
		
		<c:if test="${currentTimeMillis != null}">
			<!--time: <%=System.currentTimeMillis() - (Long)request.getAttribute("currentTimeMillis")%> -->
		</c:if>

		<div style="clear: both;"></div>

	</body>
</html>