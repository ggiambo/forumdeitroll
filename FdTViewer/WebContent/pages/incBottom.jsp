<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
		<div style="clear: both;"></div>
		</div> <!-- Chiusura div #body -->
		<div id="bottomLine">
			<p id="copyRight">Copyright © 2012 Fondazione Gatto Selvaggio</p>
			<c:if test="${currentTimeMillis != null}">
				<p id="genTime">Pagina generata in <%=System.currentTimeMillis() - (Long)request.getAttribute("currentTimeMillis")%> millisecondi</p>
			</c:if>
		</div> 
	</body>
</html>