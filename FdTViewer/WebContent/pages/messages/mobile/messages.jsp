<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fn" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:forEach items="${messages}" var="msg" varStatus="index">
	<c:set var="msg" value="${msg}" scope="request"></c:set>
	<c:set var="index" value="${index.count}" scope="request"></c:set>
	<jsp:include page="incMessage.jsp"></jsp:include>
</c:forEach>

<fdt:pager handler="Messages"/>