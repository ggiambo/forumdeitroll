<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%-- c'e' sempre la possibilita' che i response headers siano gia' committati, ma vuol dire che abbiamo
outputtato +4k prima di arrivare qua. visto che prima di <head> c'e' solo <html> e gli header
e' una possibilita' molto remota --%>
<% response.setHeader("Content-Type", "text/html; charset=utf=8"); %>
<% response.setHeader("Pragma", "no-cache"); %>
<head>
	<%! static final long bootTime = System.currentTimeMillis(); %>
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
	<base href="<%=request.getContextPath()%>/">
</head>