<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="incTop.jsp" />
		<div id="main">
			<div class="registrationForm">
				<div class="registrationFormCaption">Registrazione Nuovo Sockpuppet</div>
				<form action="User?action=registerNewUser" method="post">
					<div>
						<div class="lblRegistrationForm">
							<label for="nick">Nome utente:</label>
						</div>
						<div class="inputRegistrationForm">	
							<input tabindex="1" name="nick" value="${nick}"/>
						</div>
						<div style="clear: both;"></div>
					</div>
					<div>
						<div class="lblRegistrationForm">
							<label for="pass">Password:</label>
						</div>
						<div class="inputRegistrationForm">
							<input tabindex="2" type="password" name="pass"/>
						</div>	
						<div style="clear: both;"></div>
					</div>
					<div class="registrationCaptcha">		
						<div><img src="Messages?action=getCaptcha" /></div><div><input tabindex="3" name="captcha" size="5"/><div class="registrationCaptchaInput">Copia qui il testo dell'immagine</div></div>
						<div style="clear: both;"></div>
					</div>
					<input tabindex="4" type="submit" value="Registra" class="sendRegistrationForm" />
					<div style="clear: both;"></div>
				</form>
				
			</div>
		</div>
		<div id="footer"></div>
<jsp:include page="incBottom.jsp" />