<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="incTop.jsp"/>
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
<jsp:include page="incBottom.jsp" />