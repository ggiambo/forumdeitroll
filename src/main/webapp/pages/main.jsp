<%@page import="com.forumdeitroll.util.VisitorCounters"%>
<%@page import="java.util.Date"%>
<%@page import="com.forumdeitroll.util.UniqueVisitorsCounter"%>
<%@page import="java.util.Calendar"%>
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
		<% String tmst = ""; %>
		<c:if test="${not empty loggedUser && loggedUser.preferences['autoRefresh'] == 'checked' && refreshable == 1}">
			<% tmst = "[" + new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()) + "] ";%>
		</c:if>
		<c:choose>
			<c:when test="${websiteTitle != ''}">
				<title><%=tmst%>${websiteTitle}</title>
			</c:when>
			<c:otherwise>
				<title><%=tmst%>Forum dei Troll</title>
			</c:otherwise>
		</c:choose>
		<link rel="icon" href="favicon.ico" type="image/x-icon" />
		<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
		<link href='//fonts.googleapis.com/css?family=Frijole' rel='stylesheet' type='text/css' />
		<link href="css/sh/shCore.css" rel="stylesheet" type="text/css" />
		<link href="css/sh/shThemeEclipse.css" rel="stylesheet" type="text/css" />
		<link href="css/jquery-ui-1.8.17.custom.css" type="text/css" rel="stylesheet" />
		<link href="css/fdt.css?v=<%=bootTime%>" type="text/css" rel="stylesheet" />
		<c:if test="${not empty loggedUser && loggedUser.preferences['largeStyle'] == 'checked'}">
			<link href="css/fdt-large.css?v=<%=bootTime%>" type="text/css" rel="stylesheet" />
		</c:if>
		<c:if test="${not empty loggedUser && not empty loggedUser.preferences['theme']}">
			<link href="css/fdt-tema${loggedUser.preferences['theme']}.css?v=<%=bootTime%>" type="text/css" rel="stylesheet" />
		</c:if>
	</head>

	<body>
		<div id="body"> <!-- non mettere nulla prima del div #body -->

			<%-- banner --%>
			<c:if test="${empty loggedUser || loggedUser.preferences['hideBannerone'] != 'checked'}">
				<img id="headerimg" alt="" src="./images/2.1.png" />
				<% String banners[] = new String[]{
					"images/banners/banner.jpg",
					"images/banners/banner2.jpg"
				}; %>
				<div id="header" style="background-image: url(<%=banners[(int)(System.currentTimeMillis() % banners.length)]%>)">
					<h1>
						<a title="Boot time: <%=new java.util.Date(bootTime)%>" href="Threads" id="titoloForum">
							Forum dei Troll
						</a>
					</h1>
					<c:if test="${not empty randomQuote }">
						<c:if test="${fn:length(randomQuote.content) > 116}">
							<p id="quoteForum" title="${randomQuote.content} - ${randomQuote.nick}" onclick="getRandomQuote()">
								${fn:substring(randomQuote.content, 0, 114)}&#8230;
							</p>
						</c:if>
						<c:if test="${fn:length(randomQuote.content) <= 116}">
							<p id="quoteForum" title="${randomQuote.nick}" onclick="getRandomQuote()">
								${randomQuote.content}
							</p>
						</c:if>
					</c:if>
				</div>
			</c:if>
			<c:if test="${not empty loggedUser && loggedUser.preferences['hideBannerone'] == 'checked'}">
				<div id="quoteForum" class="quoteForumSmall" title="${randomQuote.nick}" onclick="getRandomQuote()">
					${randomQuote.content}
				</div>
			</c:if>

			<jsp:include page="incNav.jsp"/>

			<%-- notifiche --%>
			<c:if test="${not empty notifications && (servlet eq 'Messages' or servlet eq 'Threads')}">
				<input type="hidden" name="notificationId" id="notificationId" />
				 <div class="notifications">
 					<c:forEach items="${notifications}" var="item" varStatus="index">
						L'utente ${item.fromNick} ti invita a leggere questo <a href="Messages?action=getById&msgId=${item.msgId}&notificationId=${item.id}&notificationFromNick=${item.fromNick}">post</a>
						<c:if test="${not index.last}">
							<br/>
						</c:if>
					</c:forEach>
				</div>
			</c:if>

			<%-- se forum e' Proc di Catania, mostra pedobear --%>
			<c:set var="bodyContentClass" value="" scope="page" />
			<c:if test="${param.forum == 'Proc di Catania'}">
				<c:set var="bodyContentClass" value="pb" scope="page" />
			</c:if>
			<div id="bodyContent" class="<c:out value='${bodyContentClass}' />">

				<c:if test="${excludeSidebar != 'true'}">
					<%-- sidebar per la navigazione --%>
					<jsp:include page="incSidebar.jsp"/>
				</c:if>

				<%-- la pagina vera e propria --%>
				<jsp:include page="${fn:toLowerCase(servlet)}/${page}" />

				<div style="clear: both;"></div>
			</div>
		</div>

		<% VisitorCounters.add(request); %>

		<%-- bottom line --%>
		<div id="bottomLine">
			<p id="copyRight">Copyright &copy; 2012-<%= ""+Calendar.getInstance().get(Calendar.YEAR) %> Fondazione Gatto Selvaggio</p>
			<c:if test="${currentTimeMillis != null}">
				<p id="genTime">Pagina generata in <%=System.currentTimeMillis() - (Long)request.getAttribute("currentTimeMillis")%> millisecondi</p>
			</c:if>
			<p>Utenti attivi: <%=VisitorCounters.count1min.get()%> nell'ultimo minuto, <%=VisitorCounters.count5min.get()%> negli ultimi cinque minuti, <%=VisitorCounters.count15min.get()%> negli ultimi 15 minuti</p>
		</div>

		<%-- i vari scripts --%>
		<script type="text/javascript" src="js/jquery.min.js"></script>
		<script type="text/javascript" src="js/underscore.1.4.4.min.js"></script>
		<script type="text/javascript" src="js/json2.js"></script>
		<script type="text/javascript" src="js/fdt.js?v=<%=bootTime%>"></script>
		<script type="text/javascript" src="js/preview.js?v=<%=bootTime%>"></script>
		<script type="text/javascript" src="js/jquery-ui.min.js"></script>
		<script type="text/javascript" src="js/sh/shCore.js"></script>
		<script type="text/javascript" src="js/sh/shAutoloader.js"></script>
		<script type="text/javascript" src="js/jscolor/jscolor.js"></script>
		<script type="text/javascript" src="js/PluginDetect_All.js"></script>
		<script type="text/javascript" src="js/${fn:toLowerCase(servlet)}.js?v=<%=bootTime%>"></script>
		<script type="text/javascript" src="https://www.google.com/recaptcha/api.js" async defer></script>
		<fdt:delayedScript dump="false">
			<c:choose>
				<c:when test="${not empty loggedUser && loggedUser.preferences['autoRefresh'] == 'checked'}">
					var refreshable = parseInt('${refreshable}') || 0; // "|| 0" cosi' da avere "0" nel caso di NaN ;)
				</c:when>
				<c:otherwise>
					var refreshable = 0;
				</c:otherwise>
			</c:choose>
			var lastId = null;
			jQuery.get('JSon?action=getLastId', function(data) {
				lastId = data.content.id;
			});

			jQuery("document").ready(function() {
				<c:if test="${blockHeader == 'checked'}">
					blockHeader();
				</c:if>
				${javascript}
			});
		</fdt:delayedScript>
		<fdt:delayedScript dump="true">
			questo non verra' stampato, ma se lo togli la taglib non viene eseguita
		</fdt:delayedScript>
	</body>
</html>
