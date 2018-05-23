<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="main">
	<div class="registrationForm">
		<div class="registrationFormCaption">Registrazione Nuovo Sockpuppet</div>
		<form action="User?action=registerNewUser" method="post">
			<div>
				<div class="lblRegistrationForm">
					<label for="nick">Nome utente:</label>
				</div>
				<div class="inputRegistrationForm">
					<input tabindex="1" name="nick" id="nick" value="${nick}"/>
				</div>
				<div style="clear: both;"></div>
			</div>
			<div>
				<div class="lblRegistrationForm">
					<label for="pass">Password:</label>
				</div>
				<div class="inputRegistrationForm">
					<input tabindex="2" type="password" id="pass" name="pass"/>
				</div>
				<div style="clear: both;"></div>
			</div>
            <div>
                <div class="lblRegistrationForm">
                    <label for="motivation">Motivazione:</label>
                </div>
                <div class="inputRegistrationForm">
                    <textarea name="motivation" id="motivation" rows="10">Perch&eacute, merito di essere accolto nella Grande Famiglia ?</textarea>
                </div>
                <div style="clear: both;"></div>
            </div>
			<div class="registrationCaptcha">
				<div class="g-recaptcha" data-sitekey="${captchakey}"></div>
			</div>
			<input tabindex="4" type="submit" value="Registra" class="sendRegistrationForm" />
			<div style="clear: both;"></div>
		</form>

	</div>
</div>
<div id="footer"></div>
