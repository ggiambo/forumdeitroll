<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<%! static final long bootTime = System.currentTimeMillis(); %>
<% com.forumdeitroll.util.VisitorCounters.add(request); %>
<!doctype html>
<html>
	<head>
		<c:choose>
			<c:when test="${websiteTitle != ''}">
				<title>${websiteTitle}</title>
			</c:when>
			<c:otherwise>
				<title>Forum dei Troll</title>
			</c:otherwise>
		</c:choose>
		<meta name=viewport content="width=device-width, initial-scale=1">
		<link href=css/fdt-mobile.css?v=<%=bootTime%> type=text/css rel=stylesheet />
		<script type=text/javascript src=js/fdt-mobile.js?v=<%=bootTime%>></script>
	</head>
	<body>
		<ul class="row header">
			<li class="col-1">
				<a href=Messages?action=getMessages class=btn>Crono<br>logia</a>
			</li>
			<li class="col-1">
				<a href=Threads?action=getThreads class=btn>Nuove</a>
			</li>
			<li class="col-1">
				<a href=Threads?action=getThreadsByLastPost class=btn>Ultime</a>
			</li>
			<li class="col-1">
				<a href=Authors?action=getAuthors class=btn>Autori</a>
			</li>
			<li class="col-1">
				<a href=Messages?action=mobileComposer class=btn>Nuovo</a>
			</li>
			<li class="col-1">
				<a href="javascript:showHideMenu()" class="btn">Menu</a>
			</li>
		</ul>
		<div class=main>
			<c:if test="${navigationMessage != null}">
				<div class=row>
					<span class="navigationMessage${navigationMessage.type}">
						${navigationMessage.content}
					</span>
				</div>
				<div class=row>&nbsp;</div>
			</c:if>
			<c:choose>
				<c:when test="${includeNoMobile == 'true'}">
					<jsp:include page="${fn:toLowerCase(servlet)}/${page}" />
				</c:when>
				<c:otherwise>
					<jsp:include page="${fn:toLowerCase(servlet)}/mobile/${page}" />
				</c:otherwise>
			</c:choose>
		</div>
		<div class=menu>
			<ul>
				<c:choose>
					<c:when test="${not empty loggedUser}">
						<li>Loggato come ${loggedUser.nick}</li>
						<li><a href="Misc?action=logoutAction">Logout</a></li>
					</c:when>
					<c:otherwise>
						<li><a href="User?action=loginAction">Login</a></li>
					</c:otherwise>
				</c:choose>
				<li onclick="classico()"><a href="javascript:void(0)">Classico</a></li>
				<li onclick="toggleQuotes()"><a href="javascript:void(0)">Toggle Quotes</a></li>
			</ul>
		</div>
		<fdt:delayedScript dump="true">
			questo non verra' stampato, ma se lo togli la taglib non viene eseguita
		</fdt:delayedScript>
	</body>
</html>