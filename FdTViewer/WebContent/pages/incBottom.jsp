<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page trimDirectiveWhitespaces="true" %>
			<div style="clear: both;"></div>
		</div> <!-- Chiusura div #bodyContent -->
		</div> <!-- Chiusura div #body -->
		<div id="bottomLine">
			<p id="copyRight">Copyright © 2012 Fondazione Gatto Selvaggio</p>
			<c:if test="${currentTimeMillis != null}">
				<p id="genTime">Pagina generata in <%=System.currentTimeMillis() - (Long)request.getAttribute("currentTimeMillis")%> millisecondi</p>
			</c:if>
		</div>
	<%! static final long bootTime = System.currentTimeMillis(); %>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
	<script type="text/javascript" src="js/fdt.js?v=<%=bootTime%>"></script>
	<script type="text/javascript" src="js/preview.js?v=<%=bootTime%>"></script>
	<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.17/jquery-ui.min.js"></script>
	<script src="js/sh/shCore.js" type="text/javascript"></script>
	<script src="js/sh/shAutoloader.js" type="text/javascript"></script>
	<script src="js/jscolor/jscolor.js" type="text/javascript"></script>
	</body>
</html>