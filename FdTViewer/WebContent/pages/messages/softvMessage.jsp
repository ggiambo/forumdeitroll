<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<fdt:delayedScript dump="false">
	var token = "${anti_xss_token}";
</fdt:delayedScript>

<c:forEach items="${messages}" var="msg" varStatus="index">
	<c:set var="msg" value="${msg}" scope="request"/>
	<a name="softMsg${msg.id}"/>
	<jsp:include page="../incMessage.jsp"/>
</c:forEach>
