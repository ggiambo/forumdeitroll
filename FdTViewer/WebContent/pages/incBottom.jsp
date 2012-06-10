<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
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
	<script src="js/PluginDetect_All.js" type="text/javascript"></script>
	<script src="js/profiler.js?v=<%=bootTime%>" type="text/javascript"></script>
	<fdt:delayedScript dump="true">
		questo non verra' stampato, ma se lo togli la taglib non viene eseguita
	</fdt:delayedScript>
	</body>
</html>