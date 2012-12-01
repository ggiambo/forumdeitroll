<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<div id="main">
	<c:if test="${not empty loggedUser && loggedUser.preferences['super'] == 'yes'}">
		<c:if test="${not empty lastUnbanRequested }">
			<p>Ultima richiesta di sban</p>
			<p>permr: ${lastUnbanRequested.permr }</p>
			<p>etag: ${lastUnbanRequested.etag }</p>
			<p>plugins: ${lastUnbanRequested.plugins }</p>
			<p>ua: ${lastUnbanRequested.ua }</p>
			<p>screenres: ${lastUnbanRequested.screenres }</p>
			<p>ipAddress: ${lastUnbanRequested.ipAddress }</p>
			<p>nick: ${lastUnbanRequested.nick }</p>
			<form action="UserProfiler" method="POST">
				<input type="hidden" name="action" value="unban">
				<input type="hidden" name="jsonProfile" value='${jsonProfile}'>
				<input type="submit" name="btn" value="Sbanna">
			</form>
		</c:if>
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
