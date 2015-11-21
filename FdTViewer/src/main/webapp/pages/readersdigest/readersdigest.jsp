<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<div id="main" style="width: 100%;">

	<p class="digestTitle">The Troll's Digest</p>
	<p class="digestSubtitle">updated daily</p>

	<div class="articleContainer vertical">
		<c:forEach items="${articles}" var="article" varStatus="articleStatus">
			<c:set var="index" value="${articleStatus.index}" scope="request"/>
			<c:set var="article" value="${article}" scope="request"/>
			<c:if test="${index % 3 == 0 }">
				<jsp:include page="incArticle.jsp"/>
			</c:if>
		</c:forEach>
	</div>
	<div class="articleContainer horizontal">
		<c:forEach items="${articles}" var="article" varStatus="articleStatus">
			<c:set var="index" value="${articleStatus.index}" scope="request"/>
			<c:set var="article" value="${article}" scope="request"/>
			<c:if test="${index % 3 == 1 || index % 3 == 2 }">
				<jsp:include page="incArticle.jsp"/>
			</c:if>
		</c:forEach>
	</div>

	
</div>