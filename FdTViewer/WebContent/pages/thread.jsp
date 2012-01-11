<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="incTop.jsp" />
		<div id="main">
			<c:forEach items="${messages}" var="msg" varStatus="index">
				<c:set var="margin" value="${msg.indent * 15}"/>
				<div style="border:1px solid black; margin: 5px 5px; margin-left:${margin}px; width:500px;">
					<c:set var="msg" value="${msg}" scope="request"/>
					<c:set var="index" value="${index}" scope="request"/>
					<jsp:include page="incMessage.jsp"/>
				</div>
			</c:forEach>
		</div>
<jsp:include page="incBottom.jsp" />