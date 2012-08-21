<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://ravanator.acmetoy.com/jsp/jstl/fdt" prefix="fdt" %>

<div id="main">
	<textarea id='snoopDataContainer' rows="10" cols="50"></textarea>
</div>
<fdt:delayedScript dump="false">
$(document).ready(function() {
	profiler(function(profileData) {
		checkProfile(profileData, function(reply) {
			$('#snoopDataContainer').html(reply);
		});
	});
});
</fdt:delayedScript>
<div id="footer"></div>
