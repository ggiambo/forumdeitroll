<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="incTop.jsp" />
		<div id="main">
				
			<div class="userPanel">
				<div class="userPanelCaption">Pannello Utente</div>
				<div class="userPanelContent">
					<div class="userPanelSection">
						<img src="?action=getAvatar&nick=${author.nick}" alt="Avatar" class="avatar" />
						<h3>Informazioni</h3>
						<span class="lbl">Nome utente:</span> ${author.nick}<br/>
						<span class="lbl">Messaggi:</span> ${author.messages }
						<div style="clear: both;"></div>
					</div>
					<div class="userPanelSection">
						<h3>Cambio Password</h3>
						<form action="User?action=updatePass" method="post">
							<div>
								<div class="lblUserPanel">
									<label for="actualPass" class="lbl">Password attuale:</label>
								</div>
								<div class="inputUserPanel">
									<input type="password" name="actualPass" id="actualPass" />
								</div>
								<div style="clear: both;"></div>
							</div>
							<div>
								<div class="lblUserPanel">
									<label for="pass1" class="lbl">Nuova password:</label>
								</div>
								<div class="inputUserPanel">
									<input type="password" name="pass1" id="pass1" />
								</div>
								<div style="clear: both;"></div>
							</div>
							<div>
								<div class="lblUserPanel">
									<label for="pass2" class="lbl">Verifica password:</label>
								</div>
								<div class="inputUserPanel">
									<input type="password" name="pass2" id="pass2" />
								</div>
								<div style="clear: both;"></div>
							</div>
							<input type="submit" value="Modifica" class="sendUserPanel" />
							<div style="clear: both;"></div>
						</form>
						
					</div>
					<div class="userPanelSection">
						<h3>Cambio Avatar</h3>
						<form action="User?action=updateAvatar" method="post" enctype="multipart/form-data">
							<div>
								<label for="avatar" class="lbl">File:</label>
								<input type="file" name="avatar" id="avatar" />
								<div style="clear: both;"></div>
							</div>
							<input type="submit" value="Upload" class="sendUserPanel" />
							<div style="clear: both;"></div>
						</form>		
					</div>
					<div class="userPanelSection">
						<h3>Altre Azioni</h3>
						<a href="?action=getQuotes" class="userPanelButton">Frasi Celebri</a>
						<%-- TODO
						<a href="#" class="userPanelButton">Messaggi</a>  GESTIONE MESSAGGI PRIVATI QUI
						--%>
						<div style="clear: both;"></div>
					</div>
				</div>
			</div>

		</div>
		
		<div id="footer"></div>
<jsp:include page="incBottom.jsp" />
