<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<%! static final long bootTime = System.currentTimeMillis(); %>
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
		<script type=text/javascript src=js/fdt-mobile.js?v=<%=bootTime%> defer=defer></script>
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
				<a href=Messages?action=newMessage class=btn onclick="alert('TODO'); return false">Nuovo</a>
			</li>
			<li class="col-1 menu">
				<span class=btn>Menu</span>
				<ul>
					<li><a href='javascript:classico()'>Classico</a></li>
					<li>
						<a href='javascript:toggleQuotes()'>Mostra/Nascondi<br>Quotes</a>
					</li>
				</ul>
			</li>
		</ul>
		<div class=main>
			<c:choose>
				<c:when test="${includeNoMobile == 'true'}">
					<jsp:include page="${fn:toLowerCase(servlet)}/${page}" />
				</c:when>
				<c:otherwise>
					<jsp:include page="${fn:toLowerCase(servlet)}/mobile/${page}" />
				</c:otherwise>
			</c:choose>
		</div>
	</body>
</html>