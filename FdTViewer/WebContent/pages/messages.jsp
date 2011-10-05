<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	<head>
		<link href="css/fdt.css" type="text/css" rel="stylesheet"/>
	</head>

	<body>

		<div id="header"><h1>FdT Due Zero !</h1></div>

		<jsp:include page="incNav.jsp"/>

		<jsp:include page="incSidebar.jsp"/>

		<div id="main">
			<c:forEach items="${messages}" var="msg" varStatus="index">
				<c:set var="msg" value="${msg}" scope="request"/>
				<c:set var="index" value="${index}" scope="request"/>
				<jsp:include page="incMessage.jsp"/>
				<hr/>
			</c:forEach>
		</div>

		<div id="footer">
			<jsp:include page="incPrevNext.jsp" />
		</div>

	</body>
</html>
