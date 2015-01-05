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
				<a href="javascript:showHideMenu()" class="btn">
					<c:if test="${hasPvts}">
						<img src='images/icona_pibox_a.gif'>
					</c:if>
					<c:if test="${not hasPvts}">
						Menu
					</c:if>
				</a>
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
			<c:choose>
				<c:when test="${not empty loggedUser}">
					<div>Loggato come ${loggedUser.nick}</div>
					<div class=linkwrapper><a href="Misc?action=logoutAction">Logout</a></div>
					<div class=linkwrapper>
						<c:if test="${hasPvts}">
							<a href="Pvt"><b>Messaggi privati</b></a>
						</c:if>
						<c:if test="${not hasPvts}">
							<a href="Pvt">Messaggi privati</a>
						</c:if>
					</div>
				</c:when>
				<c:otherwise>
					<div class=linkwrapper><a href="User?action=loginAction">Login</a></div>
				</c:otherwise>
			</c:choose>
			<div onclick="classico()"><a href="javascript:void(0)">Visualizzazione classica</a></div>
			<div onclick="toggleQuotes()"><a href="javascript:void(0)">Toggle Quotes</a></div>
		</div>
			<div class=footer>
				<c:if test="${not empty pagerHandler}">
					<fdt:pager handler="${pagerHandler}"/>
				</c:if>
			</div>
		<fdt:delayedScript dump="true">
			questo non verra' stampato, ma se lo togli la taglib non viene eseguita
		</fdt:delayedScript>
	</body>
</html>