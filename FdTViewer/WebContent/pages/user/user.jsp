<%@page import="com.forumdeitroll.servlets.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
						<div class="lblUserPanel">
							<label for="avatar" class="lbl">File:</label>
						</div>
						<div class="inputUserPanel">
							<input type="file" name="avatar" id="avatar" class="file"/>
						</div>
						<div style="clear: both;"></div>
					</div>
					<input type="submit" value="Upload" class="sendUserPanel" />
					<div style="clear: both;"></div>
					<p>Limitazioni: dimensione massima consentita <%=""+User.MAX_SIZE_AVATAR_BYTES%> bytes, altezza <%=""+User.MAX_SIZE_AVATAR_HEIGHT %> pixels, larghezza <%=""+User.MAX_SIZE_AVATAR_WIDTH %> pixels</p>
				</form>
			</div>

			<div class="userPanelSection">
				<h3>Firma</h3>
				<form action="User?action=updateSignature" method="post" enctype="multipart/form-data">
					<input type="hidden" name="utf8" value="&#9731;">
					<div>
						<div class="lblUserPanel">
							<label for="signature" class="lbl">Firma:</label>
						</div>
						<div class="inputUserPanel">
							<textarea name="signature" id="signature">${fn:replace(fn:replace(loggedUser.preferences['signature'],'<br>',''), '<BR>','')}</textarea>
						</div>
						<div style="clear: both;"></div>
					</div>
					<div>
						<div class="lblUserPanel">
							<label for="signature_image" class="lbl">File:</label>
						</div>
						<div class="inputUserPanel">
							<input type="file" name="signature_image" class="file"/>
						</div>
						<div style="clear: both;"></div>
					</div>
					<p>Limitazioni: dimensione massima consentita <%=""+User.MAX_SIZE_SIGNATURE_BYTES%> bytes, altezza <%=""+User.MAX_SIZE_SIGNATURE_HEIGHT %> pixels, larghezza <%=""+User.MAX_SIZE_SIGNATURE_WIDTH %> pixels, testo 200 caratteri</p>
					<div style="clear: both;"></div>
					<input type="submit" name="submitBtn" value="Elimina" class="sendUserPanel" />
					<input type="submit" name="submitBtn" value="Modifica" class="sendUserPanel"/>
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
							<select name="<%=User.PREF_HIDDEN_FORUMS%>" multiple>
								<c:forEach items="${allForums}" var="forum">
									<c:set var="prefKey" value="hideForum.${forum}" />
									** ${loggedUser.preferences[prefKey]} **
									<c:choose>
										<c:when test="${not empty loggedUser.preferences[prefKey]}">
											<option value="${forum}" selected>${forum}</option>
										</c:when>
										<c:otherwise>
											<option value="${forum}">${forum}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>
						</div>
						<div class="lblUserPanel">
							<label for="<%=User.PREF_HIDDEN_FORUMS%>" class="lbl">Nascondi Thread di questi forum</label>
						</div>
						<div style="clear: both;"></div>
					</div>
					<div>
						<div class="inputUserPanel">
							<input type="checkbox" name="<%=User.PREF_HIDE_BANNERONE%>" id="<%=User.PREF_HIDE_BANNERONE%>" ${loggedUser.preferences['hideBannerone']} />
						</div>
						<div class="lblUserPanel">
							<label for="<%=User.PREF_HIDE_BANNERONE%>" class="lbl">Nascondi bannerone tette&amp;culi</label>
						</div>
						<div style="clear: both;"></div>
					</div>
					<div>
						<div class="inputUserPanel">
							<input type="checkbox" name="<%=User.PREF_MSG_MAX_HEIGHT%>" id="<%=User.PREF_MSG_MAX_HEIGHT%>" ${loggedUser.preferences['msgMaxHeight']} />
						</div>
						<div class="lblUserPanel">
							<label for="<%=User.PREF_MSG_MAX_HEIGHT%>" class="lbl">Messaggi con altezza massima</label>
						</div>
						<div style="clear: both;"></div>
					</div>
					<div>
						<div class="inputUserPanel">
							<input type="checkbox" name="<%=User.PREF_AUTO_REFRESH%>" id="<%=User.PREF_AUTO_REFRESH%>" ${loggedUser.preferences['autoRefresh']} />
						</div>
						<div class="lblUserPanel">
							<label for="<%=User.PREF_AUTO_REFRESH%>" class="lbl">Auto refresh ogni 2 min.</label>
						</div>
						<div style="clear: both;"></div>
					</div>
					<div>
						<div class="inputUserPanel">
							<input type="checkbox" name="<%=User.PREF_HIDE_SIGNATURE%>" id="<%=User.PREF_HIDE_SIGNATURE%>" ${loggedUser.preferences['hideSignature']} />
						</div>
						<div class="lblUserPanel">
							<label for="<%=User.PREF_HIDE_SIGNATURE%>" class="lbl">Nascondi le firme.</label>
						</div>
						<div style="clear: both;"></div>
					</div>
					<div>
						<div class="inputUserPanel">
							<input type="checkbox" name="<%=User.PREF_COMPACT_SIGNATURE%>" id="<%=User.PREF_COMPACT_SIGNATURE%>" ${loggedUser.preferences['compactSignature']} />
						</div>
						<div class="lblUserPanel">
							<label for="<%=User.PREF_COMPACT_SIGNATURE%>" class="lbl">Mostra firma compattata.</label>
						</div>
						<div style="clear: both;"></div>
					</div>
					<div>
						<div class="inputUserPanel">
							<input type="checkbox" name="<%=User.PREF_BLOCK_HEADER%>" id="<%=User.PREF_BLOCK_HEADER%>" ${loggedUser.preferences['blockHeader']} />
						</div>
						<div class="lblUserPanel">
							<label for="<%=User.PREF_BLOCK_HEADER%>" class="lbl">Header fisso.</label>
						</div>
						<div style="clear: both;"></div>
					</div>
					<div>
						<div class="inputUserPanel">
							<input type="checkbox" name="<%=User.PREF_LARGE_STYLE%>" id="<%=User.PREF_LARGE_STYLE%>" ${loggedUser.preferences['largeStyle']} />
						</div>
						<div class="lblUserPanel">
							<label for="<%=User.PREF_LARGE_STYLE%>" class="lbl">Stile largo.</label>
						</div>
						<div style="clear: both;"></div>
					</div>
					<div>
						<div class="inputUserPanel">
							<select name="<%=User.PREF_THEME%>" id="<%=User.PREF_THEME%>">
								<c:forEach items="<%=User.PREF_THEMES%>" var="theme">
								    <option ${loggedUser.preferences['theme'] == theme ? 'selected="selected"'
								             : ''
								             } value="<c:out value="${theme}"/>">
								        <c:out value="${theme}"/>
								    </option>
								</c:forEach>
							</select>
						</div>
						<div class="lblUserPanel">
							<label for="<%=User.PREF_THEME%>" class="lbl">Tema</label>
						</div>
						<div style="clear: both;"></div>
					</div>
					<c:if test="${loggedUser.preferences['super'] eq 'yes'}">
						<hr/>
						<div class="inputUserPanel">
							<input type="checkbox" name="<%=User.ADMIN_PREF_BLOCK_TOR%>" id="<%=User.ADMIN_PREF_BLOCK_TOR%>" ${blockTorExitNodes} />
						</div>
						<div class="lblUserPanel">
							<label for="<%=User.ADMIN_PREF_BLOCK_TOR%>" class="lbl">Blocca TOR exit nodes:</label>
						</div>
						<div style="clear: both;"></div>
						<div class="inputUserPanel">
							<input type="checkbox" name="<%=User.ADMIN_PREF_DISABLE_PROFILER%>" id="<%=User.ADMIN_PREF_DISABLE_PROFILER%>" ${disableUserProfiler} />
						</div>
						<div class="lblUserPanel">
							<label for="<%=User.ADMIN_PREF_DISABLE_PROFILER%>" class="lbl">Disattiva il profiler:</label>
						</div>
						<div style="clear: both;"></div>
						<div class="lblUserPanel">
							<label for="javascript" class="lbl">Javascript:</label>
						</div>
						<div class="inputUserPanel">
							<textarea name="javascript" id="javascript" maxlength="255">${javascript}</textarea>
						</div>
						<div style="clear: both;"></div>
						<div class="lblUserPanel">
							<label for="websiteTitle" class="lbl">Titoli:</label>
						</div>
						<div class="inputUserPanel" id="websiteTitles">
							<c:choose>
								<c:when test="${empty websiteTitles}">
								</c:when>
								<c:otherwise>
									<c:forEach items="${websiteTitles}" var="websiteTitle">
										<input name="<%=User.ADMIN_WEBSITE_TITLES%>" value="${websiteTitle}" maxlength="32" /><br/>
									</c:forEach>
								</c:otherwise>
							</c:choose>
							<img src="images/add.png" id="addWebsiteTitle" alt="Aggiungi titolo"/>
						</div>
						<div style="clear: both;"></div>
					</c:if>

					<input type="submit" value="Modifica" class="sendUserPanel" />
				</form>
				<div style="clear: both;"></div>
			</div>
			<div class="userPanelSection">
				<h3>Altre Azioni</h3>
				<a href="./User?action=getQuotes" class="userPanelButton">Frasi Celebri</a>
				<a href="./Pvt?action=inbox" class="userPanelButton">Posta</a>
				<a href="./User?action=getNotifications" class="userPanelButton">Notifiche</a>
				<a href="./Bookmarks?action=list" class="userPanelButton">Segnalibri</a>
				<div style="clear: both;"></div>
			</div>
		</div>
	</div>
</div>
<div id="footer"></div>
