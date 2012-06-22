function banMessage(m_id, anti_xss_token) {
	if (confirm("Vuoi bannare questo messaggio ?")) {
		window.location.assign("ModInfo?action=banMessage&m_id=" + m_id + "&token=" + anti_xss_token);
	}
}

function banUser(m_id, anti_xss_token) {
	if (confirm("Vuoi bannare questo utente ?")) {
		window.location.assign("ModInfo?action=banUser&m_id=" + m_id + "&token=" + anti_xss_token);
	}
}

function banIP(m_id, anti_xss_token) {
	if (confirm("Vuoi bannare questo IP ?")) {
		window.location.assign("ModInfo?action=banIP&m_id=" + m_id + "&token=" + anti_xss_token);
	}
}