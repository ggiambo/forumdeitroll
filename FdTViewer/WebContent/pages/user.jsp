<%@page import="com.acmetoy.ravanator.fdt.servlets.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="incTop.jsp" />
		<div id="main">	
			<div class="userPanel">
				<div class="userPanelCaption">Pannello Utente</div>
				<div class="userPanelContent">
					<div class="userPanelSection">
						<c:url value="" var="avatarURL">
							<c:param name="action" value="getAvatar"/>
							<c:param name="nick" value="${loggedUser.nick}"/>
						</c:url>
						<img src="Misc<c:out value="${avatarURL}" escapeXml="true" />" alt="Avatar" class="avatar" />
						<h3>Informazioni</h3>
						<span class="lbl">Nome utente:</span> ${loggedUser.nick}<br/>
						<span class="lbl">Messaggi:</span> ${loggedUser.messages}
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
						<h3>Preferenze</h3>
						<form action="User?action=updatePreferences" method="post">
							<div>
								<div class="inputUserPanel">
									<input type="checkbox" name="<%=User.PREF_SHOWANONIMG%>" id="<%=User.PREF_SHOWANONIMG%>" ${loggedUser.preferences['showAnonImg']} />
								</div>
								<div class="lblUserPanel">
									<label for="<%=User.PREF_SHOWANONIMG%>" class="lbl">Immagini ANOnimo</label>
								</div>
								<div style="clear: both;"></div>
							</div>
							<div>
								<div class="inputUserPanel">
									<input type="checkbox" name="<%=User.PREF_EMBEDDYT%>" id="<%=User.PREF_EMBEDDYT%>" ${loggedUser.preferences['embeddYt']} />
								</div>
								<div class="lblUserPanel">
									<label for="<%=User.PREF_EMBEDDYT%>" class="lbl">Embedda youtube</label>
								</div>
								<div style="clear: both;"></div>
							</div>
							<div>
								<div class="inputUserPanel">
									<input type="checkbox" name="<%=User.PREF_COLLAPSE_QUOTES%>" id="<%=User.PREF_COLLAPSE_QUOTES%>" ${loggedUser.preferences['collapseQuotes']} />
								</div>
								<div class="lblUserPanel">
									<label for="<%=User.PREF_COLLAPSE_QUOTES%>" class="lbl">Collassa quotes</label>
								</div>
								<div style="clear: both;"></div>
							</div>
							<div>
								<div class="inputUserPanel">
									<input type="checkbox" name="<%=User.PREF_HIDE_PROC_CATANIA%>" id="<%=User.PREF_HIDE_PROC_CATANIA%>" ${loggedUser.preferences['hideProcCatania']} />
								</div>
								<div class="lblUserPanel">
									<label for="<%=User.PREF_HIDE_PROC_CATANIA%>" class="lbl">Nascondi Thread della Procura</label>
								</div>
								<div style="clear: both;"></div>
							</div>
							<div>
								<div class="inputUserPanel">
									<input type="checkbox" name="<%=User.PREF_ENABLE_AUTO_REFRESH%>" id="<%=User.PREF_ENABLE_AUTO_REFRESH%>" ${loggedUser.preferences['enableAutoRefresh']} />
								</div>
								<div class="lblUserPanel">
									<label for="<%=User.PREF_ENABLE_AUTO_REFRESH%>" class="lbl">Refresh ogni 2 minuti</label>
								</div>
								<div style="clear: both;"></div>
							</div>
							<input type="submit" value="Modifica" class="sendUserPanel" />
						</form>
						<div style="clear: both;"></div>
					</div>
					<div class="userPanelSection">
						<h3>Altre Azioni</h3>
						<a href="User?action=getQuotes" class="userPanelButton">Frasi Celebri</a>
						<a href="./Pvt?action=inbox" class="userPanelButton">Posta</a>
						<c:set var="showAnonImg" value="${loggedUser.preferences['showAnonImg']}"/>
						<div style="clear: both;"></div>
					</div>	
				</div>
			</div>
		</div>
		<div id="footer"></div>
<jsp:include page="incBottom.jsp" />
