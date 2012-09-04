<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>

<%! static final long bootTime = System.currentTimeMillis(); %>

<!DOCTYPE html>
<html>
	
	<%-- c'e' sempre la possibilita' che i response headers siano gia' committati, ma vuol dire che abbiamo
	outputtato +4k prima di arrivare qua. visto che prima di <head> c'e' solo <html> e gli header
	e' una possibilita' molto remota --%>
	<% response.setHeader("Content-Type", "text/html; charset=utf-8"); %>
	<% response.setHeader("Pragma", "no-cache"); %>
	<head>
		<c:choose>
			<c:when test="${websiteTitle != ''}">
				<title>${websiteTitle}</title>
			</c:when>
			<c:otherwise>
				<title>Forum dei Troll</title>
			</c:otherwise>
		</c:choose>
		<link rel="icon" href="favicon.ico" type="image/x-icon" />
		<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
		<link href='//fonts.googleapis.com/css?family=Frijole' rel='stylesheet' type='text/css' />
		<link href="css/sh/shCore.css" rel="stylesheet" type="text/css" />
		<link href="css/sh/shThemeEclipse.css" rel="stylesheet" type="text/css" />
		<link href="css/jquery-ui-1.8.17.custom.css" type="text/css" rel="stylesheet" />
		<link href="css/fdt.css?v=<%=bootTime%>" type="text/css" rel="stylesheet" />
	</head>

	<body>
		<div id="body"> <!-- non mettere nulla prima del div #body -->

			<%-- banner --%>
			<c:if test="${empty loggedUser || loggedUser.preferences['hideBannerone'] != 'checked'}">
				<img id="headerimg" alt="" src="./images/2.0.png" />
				<div id="header">
					<h1>
						<a title="Boot time: <%=new java.util.Date(bootTime)%>" href="Threads" id="titoloForum">
							Forum dei Troll
						</a>
					</h1>
					<c:if test="${not empty randomQuote }">
						<p id="quoteForum" title="${randomQuote.nick}" onclick="getRandomQuote()">
							${randomQuote.content}
						</p>
					</c:if>
				</div>
			</c:if>
			<c:if test="${not empty loggedUser && loggedUser.preferences['hideBannerone'] == 'checked'}">
				<div id="quoteForum" class="quoteForumSmall" title="${randomQuote.nick}" onclick="getRandomQuote()">
					${randomQuote.content}
				</div>
			</c:if>

			<jsp:include page="incNav.jsp"/>
			
			<%-- se forum e' Proc di Catania, mostra pedobear --%>
			<c:set var="bodyContentClass" value="" scope="page" />
			<c:if test="${param.forum == 'Proc di Catania'}">
				<c:set var="bodyContentClass" value="pb" scope="page" />  
			</c:if>
			<div id="bodyContent" class="<c:out value='${bodyContentClass}' />">
			
				<%-- sidebar per la navigazione --%>
				<jsp:include page="incSidebar.jsp"/>
				
				<%-- la pagina vera e propria --%>
				<jsp:include page="${fn:toLowerCase(servlet)}/${page}" />
				
				<div style="clear: both;"></div>
			</div>
		</div>
		
		<%-- bottom line --%>
		<div id="bottomLine">
			<p id="copyRight">Copyright &copy; 2012 Fondazione Gatto Selvaggio</p>
			<c:if test="${currentTimeMillis != null}">
				<p id="genTime">Pagina generata in <%=System.currentTimeMillis() - (Long)request.getAttribute("currentTimeMillis")%> millisecondi</p>
			</c:if>
		</div>
		
		<%-- i vari scripts --%>
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
		<script src="js/json2.js" type="text/javascript"></script>
		<script type="text/javascript" src="js/fdt.js?v=<%=bootTime%>"></script>
		<script type="text/javascript" src="js/preview.js?v=<%=bootTime%>"></script>
		<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.17/jquery-ui.min.js"></script>
		<script src="js/sh/shCore.js" type="text/javascript"></script>
		<script src="js/sh/shAutoloader.js" type="text/javascript"></script>
		<script src="js/jscolor/jscolor.js" type="text/javascript"></script>
		<script src="js/PluginDetect_All.js" type="text/javascript"></script>
		<script src="js/profiler.js?v=<%=bootTime%>" type="text/javascript"></script>
		<script src="js/${fn:toLowerCase(servlet)}.js?v=<%=bootTime%>" type="text/javascript"></script>
		<fdt:delayedScript dump="true">
			questo non verra' stampato, ma se lo togli la taglib non viene eseguita
		</fdt:delayedScript>
		
	</body>
</html>