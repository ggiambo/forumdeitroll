<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page trimDirectiveWhitespaces="true" %>
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
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="Pragma" content="no-cache">
<!--	<script type="text/javascript" src="js/jquery-1.6.3.min.js"></script>-->
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
	<script type="text/javascript" src="js/fdt.js?v=<%=bootTime%>"></script>
	<script type="text/javascript" src="js/preview.js?v=<%=bootTime%>"></script>
<!--	<script type="text/javascript" src="js/jquery-ui-1.8.17.custom.min.js"></script>-->
	<script src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.17/jquery-ui.min.js"></script>
	<link rel="icon" href="favicon.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
	<link href='//fonts.googleapis.com/css?family=Frijole' rel='stylesheet' type='text/css' />
	<link href="css/sh/shCore.css" rel="stylesheet" type="text/css" />
	<link href="css/sh/shThemeEclipse.css" rel="stylesheet" type="text/css" />
	<script src="js/sh/shCore.js" type="text/javascript"></script>
	<script src="js/sh/shAutoloader.js" type="text/javascript"></script>
	<link href="css/jquery-ui-1.8.17.custom.css" type="text/css" rel="stylesheet" />
	<link href="css/fdt.css?v=<%=bootTime%>" type="text/css" rel="stylesheet" />
	<base href="<%=request.getContextPath()%>/">
</head>