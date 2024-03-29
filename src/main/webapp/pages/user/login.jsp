<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="main">
	<div class="loginForm">
		<div class="loginFormCaption">Login Utente</div>
		<form action="User" method="post">
			<div>
				<div class="lblLoginForm">
					<label for="nick">Nome utente:</label>
				</div>
				<div class="inputLoginForm">
					<input name="nick" id="nick" />
				</div>
				<div style="clear: both;"></div>
			</div>
			<div>
				<div class="lblLoginForm">
					<label for="pass">Password:</label>
				</div>
				<div class="inputLoginForm">
					<input type="password" name="pass" id="pass" />
				</div>
				<div style="clear: both;"></div>
			</div>
			<input type="submit" value="Login" class="sendLoginForm" />
			<div style="clear: both;"></div>
		</form>
	</div>
</div>
<div id="footer"></div>
