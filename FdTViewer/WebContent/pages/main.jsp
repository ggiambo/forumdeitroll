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
				<img id="headerimg" alt="" src="./images/2.0.png" />
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

			<%--- notifiche per moderatori --%>
			<c:if test="${not empty loggedUser}">
				<c:if test="${loggedUser.preferences['super'] == 'yes'}">
					<% if (!com.forumdeitroll.servlets.UserProfiler.unbanRequests.isEmpty()) {%>
						<div class="notifications">
							<a href="UserProfiler?action=snoop">Lavoro per moderatori</a>
						</div>
					<% } %>
				</c:if>
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
		<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
		<script src="js/underscore.1.4.4.min.js"></script>
		<script src="js/json2.js" type="text/javascript"></script>
		<script type="text/javascript" src="js/fdt.js?v=<%=bootTime%>"></script>
		<script type="text/javascript" src="js/preview.js?v=<%=bootTime%>"></script>
		<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.1/jquery-ui.min.js"></script>
		<script src="js/sh/shCore.js" type="text/javascript"></script>
		<script src="js/sh/shAutoloader.js" type="text/javascript"></script>
		<script src="js/jscolor/jscolor.js" type="text/javascript"></script>
		<script src="js/PluginDetect_All.js" type="text/javascript"></script>
		<script src="js/profiler.js?v=<%=bootTime%>" type="text/javascript"></script>
		<script src="js/${fn:toLowerCase(servlet)}.js?v=<%=bootTime%>" type="text/javascript"></script>
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
		<fdt:delayedScript>
		$(document).ready(function() {
			// versione modificata di http://detectmobilebrowsers.com/
			(function(a,callback){if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(a)||/1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0,4)))callback()})(navigator.userAgent||navigator.vendor||window.opera,
				function() {
					if (!localStorage['mobileNagScreen']) {
						if (confirm('Sembra che tu stia usando un browser mobile. Vuoi passare alla versione del forum per dispositivi mobili?')) {
							forceMobileView();
						} else {
							localStorage['mobileNagScreen'] = 'true';
						}
					}
				}
			)
		});
		</fdt:delayedScript>
		<fdt:delayedScript dump="true">
			questo non verra' stampato, ma se lo togli la taglib non viene eseguita
		</fdt:delayedScript>
	</body>
</html>
