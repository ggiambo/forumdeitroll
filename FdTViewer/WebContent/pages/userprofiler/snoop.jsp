<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<div id="main">
	<c:if test="${not empty loggedUser && loggedUser.preferences['super'] == 'yes'}">
		<c:forEach items="${ unbanRequests }" var="unbanRequest">
			<p>Ultima richiesta di sban</p>
			<p>permr: ${unbanRequest.permr }</p>
			<p>etag: ${unbanRequest.etag }</p>
			<p>plugins: ${unbanRequest.plugins }</p>
			<p>ua: ${unbanRequest.ua }</p>
			<p>screenres: ${unbanRequest.screenres }</p>
			<p>ipAddress: ${unbanRequest.ipAddress }</p>
			<p>nick: ${unbanRequest.nick }</p>
			<form action="UserProfiler" method="POST">
				<input type="hidden" name="jsonProfile" value='${unbanRequest.JSON}'>
				<input type="submit" name="action" value="unban">
				<input type="submit" name="action" value="deleteRequest">
			</form>
		</c:forEach>
	</c:if>
	<form action="UserProfiler" method="POST">
		<input type="hidden" name="action" value="requestUnban">
		<input type="hidden" id="jsonProfile" name="jsonProfile" value="">
		<input type="submit" id="btn" name="btn" value="Sbannatemi!" disabled="disabled">
	</form>
</div>
<fdt:delayedScript dump="false">
$(document).ready(function() {
	profiler(function(profileData) {
		checkProfile(profileData, function(reply) {
			$('#jsonProfile').val(JSON.stringify(JSON.parse(reply).input));
			$('#btn').attr('disabled', false);
		})
	});
});
</fdt:delayedScript>
<div id="footer"></div>
