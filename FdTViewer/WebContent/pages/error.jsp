<%@page import="org.apache.commons.lang3.exception.ExceptionUtils"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
	<body>
	<h1 style="color:#FFFFFF; background-color:#AA1111; padding: 5px 5px 5px 20px; margin-left: 20px;">Errore !!!!1!</h1>
	Che cazzo e' successo <img src="images/emo/7.gif"/> ?!? Contatta subito <del>la suora</del> Giambo e mandagli questo messaggio:<br/>
	<pre style="border:1px solid black; padding:10px;"><c:out value="${exceptionStackTrace }"/></pre>
	</body>
</html>