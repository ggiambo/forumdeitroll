<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	<head>
		<link href="css/fdt.css" type="text/css" rel="stylesheet"/>
	</head>

	<body>

		<div id="header"><h1>FdT Due Zero !</h1></div>

		<jsp:include page="incNav.jsp"/>

		<div id="main">
			<c:forEach items="${messages}" var="msg" varStatus="index">
				<c:set var="margin" value="${msg.indent * 15}"/>
				<div style="border:1px solid black; margin: 5px 5px; margin-left:${margin}px; width:500px;">

					<c:forEach begin="1" end="${msg.indent}">
						<div style="border:1px solid black; margin: 3px;">
					</c:forEach>

						<c:set var="msg" value="${msg}" scope="request"/>
						<c:set var="index" value="${index}" scope="request"/>
						<jsp:include page="incMessage.jsp"/>

					<c:forEach begin="1" end="${msg.indent}">
						</div>
					</c:forEach>

				</div>
			</c:forEach>
		</div>

	</body>

</html>
