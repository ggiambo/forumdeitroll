package com.forumdeitroll.test.servlets;

import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.NotificationDTO;
import com.forumdeitroll.servlets.Action;
import com.forumdeitroll.servlets.Messages;
import com.forumdeitroll.servlets.User;
import com.google.gson.stream.JsonReader;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringReader;
import java.util.List;

public class MessagesTest extends BaseServletsTest {

	@BeforeClass
	public static void initServlet() throws Exception {
		setServlet(new Messages());
	}

	@Test
	public void testDoBefore_refreshable() throws Exception {
		setAttribute("action", "getMessages")
				.executeDoBefore();

		String refreshable = getAttribute("refreshable");
		Assert.assertEquals("1", refreshable);
	}

	@Test
	public void testDoBefore_notRefreshable() throws Exception {
		executeDoBefore();

		String refreshable = getAttribute("refreshable");
		Assert.assertNull(refreshable);
	}

	@Test
	public void testGetMessages_noForum() throws Exception {
		executeAction("getMessages", Action.Method.GET)
				.checkNavigationMessage("Cronologia messaggi")
				.checkWebsiteTitleStartsWith("Forum ");

		List<MessageDTO> messages = getAttribute("messages");
		checkListSize(messages, 9);

		int totalSize = getAttribute("totalSize");
		Assert.assertEquals(9, totalSize);

		int resultSize = getAttribute("resultSize");
		Assert.assertEquals(9, resultSize);
	}

	@Test
	public void testGetMessages_forumEmpty() throws Exception {
		setParameter("forum", "")
				.executeAction("getMessages", Action.Method.GET)
				.checkNavigationMessage("Cronologia messaggi")
				.checkWebsiteTitleStartsWith("Forum principale @");

		List<MessageDTO> messages = getAttribute("messages");
		checkListSize(messages, 4);

		int totalSize = getAttribute("totalSize");
		Assert.assertEquals(4, totalSize);

		int resultSize = getAttribute("resultSize");
		Assert.assertEquals(4, resultSize);
	}

	@Test
	public void testGetMessages_forum() throws Exception {
		setParameter("forum", "Forum iniziale")
				.executeAction("getMessages", Action.Method.GET)
				.checkNavigationMessage("Cronologia messaggi")
				.checkWebsiteTitleStartsWith("Forum iniziale @");


		List<MessageDTO> messages = getAttribute("messages");
		checkListSize(messages, 3);

		int totalSize = getAttribute("totalSize");
		Assert.assertEquals(3, totalSize);

		int resultSize = getAttribute("resultSize");
		Assert.assertEquals(3, resultSize);
	}

	@Test
	public void testGetByAuthor() throws Exception {
		setParameter("forum", "Forum iniziale")
				.setParameter("author", "admin")
				.executeAction("getByAuthor", Action.Method.GET)
				.checkNavigationMessage("Messaggi scritti da <i>admin</i>")
				.checkWebsiteTitleStartsWith("Messaggi di admin @");


		List<MessageDTO> messages = getAttribute("messages");
		checkListSize(messages, 2);

		int totalSize = getAttribute("totalSize");
		Assert.assertEquals(3, totalSize);

		int resultSize = getAttribute("resultSize");
		Assert.assertEquals(2, resultSize);
	}

	@Test
	public void testGetByAuthor_noForum() throws Exception {
		setParameter("author", "admin")
				.executeAction("getByAuthor", Action.Method.GET)
				.checkNavigationMessage("Messaggi scritti da <i>admin</i>")
				.checkWebsiteTitleStartsWith("Messaggi di admin @");


		List<MessageDTO> messages = getAttribute("messages");
		checkListSize(messages, 3);

		int totalSize = getAttribute("totalSize");
		Assert.assertEquals(9, totalSize);

		int resultSize = getAttribute("resultSize");
		Assert.assertEquals(3, resultSize);
	}

	@Test
	public void testGetByAuthor_forumEmpty() throws Exception {
		setParameter("forum", "")
				.setParameter("author", "admin")
				.executeAction("getByAuthor", Action.Method.GET)
				.checkNavigationMessage("Messaggi scritti da <i>admin</i>")
				.checkWebsiteTitleStartsWith("Messaggi di admin @");


		List<MessageDTO> messages = getAttribute("messages");
		checkListSize(messages, 1);

		int totalSize = getAttribute("totalSize");
		Assert.assertEquals(4, totalSize);

		int resultSize = getAttribute("resultSize");
		Assert.assertEquals(1, resultSize);
	}

	@Test
	public void testGetByForum_forumEmpty() throws Exception {
		executeAction("getByForum", Action.Method.GET)
				.checkNavigationMessage("Cronologia messaggi")
				.checkWebsiteTitleStartsWith("Forum Principale @");

		List<MessageDTO> messages = getAttribute("messages");
		checkListSize(messages, 9);

		int totalSize = getAttribute("totalSize");
		Assert.assertEquals(9, totalSize);

		int resultSize = getAttribute("resultSize");
		Assert.assertEquals(9, resultSize);
	}

	@Test
	public void testGetByForum_forum() throws Exception {
		setParameter("forum", "Forum iniziale")
				.executeAction("getByForum", Action.Method.GET)
				.checkNavigationMessage("Cronologia messaggi")
				.checkWebsiteTitleStartsWith("Forum iniziale @");

		List<MessageDTO> messages = getAttribute("messages");
		checkListSize(messages, 3);

		int totalSize = getAttribute("totalSize");
		Assert.assertEquals(3, totalSize);

		int resultSize = getAttribute("resultSize");
		Assert.assertEquals(3, resultSize);

	}

	@Test
	public void testGetById() throws Exception {
		setParameter("msgId", "12")
				.executeAction("getById", Action.Method.GET)
				.checkWebsiteTitleStartsWith("Singolo messaggio @");

		List<MessageDTO> messages = getAttribute("messages");
		checkListSize(messages, 1);

		int resultSize = getAttribute("resultSize");
		Assert.assertEquals(1, resultSize);
	}

	@Test
	public void testGetById_login() throws Exception {

		List<NotificationDTO> notificationsBefore = miscDAO.getNotifications("admin", "Sfigato");
		checkListSize(notificationsBefore, 2);

		setUserSfigato()
				.setParameter("msgId", "12")
				.setParameter("notificationId", "1")
				.setParameter("notificationFromNick", "admin")
				.executeAction("getById", Action.Method.GET)
				.checkWebsiteTitleStartsWith("Singolo messaggio @");

		List<MessageDTO> messages = getAttribute("messages");
		checkListSize(messages, 1);

		int resultSize = getAttribute("resultSize");
		Assert.assertEquals(1, resultSize);

		List<NotificationDTO> notificationsAfter = miscDAO.getNotifications("admin", "Sfigato");
		checkListSize(notificationsAfter, 1);
	}

	@Test
	public void testNewMessage() throws Exception {
		setParameter("forum", "forum")
				.setParameter("subject", "subject")
				.setParameter("text", "text")
				.executeAction("newMessage", Action.Method.GET)
				.checkWebsiteTitleStartsWith("Nuovo messaggi @");

		MessageDTO message = getAttribute("message");
		Assert.assertNotNull(message);
		Assert.assertEquals("forum", "forum");
		Assert.assertEquals("subject", "subject");
		Assert.assertEquals("text", "text");
	}

	@Test
	public void testShowReplyDiv_typeQuote() throws Exception {
		setParameter("type", "quote")
				.setParameter("parentId", "9")
				.executeAction("showReplyDiv", Action.Method.GET);

		MessageDTO message = getAttribute("message");
		Assert.assertNotNull(message);
		Assert.assertEquals("Re: Ieri", message.getSubject());
		Assert.assertEquals("\r\n" +
				"Scritto da: admin\r\n" +
				"&gt; Scritto da: \r\n" +
				"&gt; &gt; Scritto da: Sfigato\r\n" +
				"&gt; &gt; &gt; Ho incontrato yoda. Che ragazzo fortunato :( ...\r\n" +
				"&gt; &gt; \r\n" +
				"&gt; &gt; Mi trovi un lavoro ?\r\n" +
				"&gt; &gt; \r\n" +
				"&gt; &gt; - idyoda -\r\n" +
				"&gt; \r\n" +
				"&gt; (rotfl)(rotfl)\r\n", message.getText());
	}

	@Test
	public void testShowReplyDiv_typeQuote1() throws Exception {
		setParameter("type", "quote1")
				.setParameter("parentId", "9")
				.executeAction("showReplyDiv", Action.Method.GET);

		MessageDTO message = getAttribute("message");
		Assert.assertNotNull(message);
		Assert.assertEquals("Re: Ieri", message.getSubject());
		Assert.assertEquals("Scritto da: admin\r\n" +
				"&gt; \r\n" +
				"&gt; (rotfl)(rotfl)\r\n", message.getText());
	}

	@Test
	public void testShowReplyDiv_typeQuote4() throws Exception {
		setParameter("type", "quote4")
				.setParameter("parentId", "9")
				.executeAction("showReplyDiv", Action.Method.GET);

		MessageDTO message = getAttribute("message");
		Assert.assertNotNull(message);
		Assert.assertEquals("Re: Ieri", message.getSubject());
		Assert.assertEquals("\r\n" +
				"Scritto da: admin\r\n" +
				"&gt; Scritto da: \r\n" +
				"&gt; &gt; Scritto da: Sfigato\r\n" +
				"&gt; &gt; &gt; Ho incontrato yoda. Che ragazzo fortunato :( ...\r\n" +
				"&gt; &gt; \r\n" +
				"&gt; &gt; Mi trovi un lavoro ?\r\n" +
				"&gt; &gt; \r\n" +
				"&gt; &gt; - idyoda -\r\n" +
				"&gt; \r\n" +
				"&gt; (rotfl)(rotfl)\r\n", message.getText());

	}

	@Test
	public void testShowReplyDiv_admin() throws Exception {
		setParameter("type", "quote4")
				.setParameter("parentId", "3")
				.executeAction("showReplyDiv", Action.Method.GET);

		MessageDTO message = getAttribute("message");
		Assert.assertNotNull(message);
		Assert.assertEquals("Re: benvenuto nel fdt !", message.getSubject());
		Assert.assertEquals("\r\nScritto da: admin\r\n" +
				"&gt; Scritto da: \r\n" +
				"&gt; &gt; Proot !\r\n" +
				"&gt; \r\n" +
				"&gt; :@ !\r\n", message.getText());
	}

	@Test
	public void testEditMessage_wrongUser() throws Exception {
		setUserSfigato()
				.setParameter("msgId", "1")
				.executeAction("editMessage", Action.Method.GET)
				.checkNavigationMessage("Non puoi editare un messaggio non tuo !");

		MessageDTO message = getAttribute("message");
		Assert.assertNull(message);

		Boolean isEdit = getAttribute("isEdit");
		Assert.assertNull(isEdit);
	}

	@Test
	public void testEditMessage() throws Exception {
		setUserAdmin()
				.setParameter("msgId", "3")
				.executeAction("editMessage", Action.Method.GET);

		MessageDTO message = getAttribute("message");
		Assert.assertNotNull(message);
		Assert.assertEquals("Scritto da: \r\n" +
				"&gt; Proot !\r\n" +
				"\r\n" +
				":@ !", message.getText());

		Boolean isEdit = getAttribute("isEdit");
		Assert.assertTrue(isEdit);
	}

	@Test
	public void testGetMessagePreview_noLogin() throws Exception {
		setParameter("text", "Ambarabacici coco :) > \r\n &trade; \n Quack üöä")
				.executeAction("getMessagePreview", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
	}

	@Test
	public void testGetMessagePreview() throws Exception {
		setParameter("text", "Ambarabacici <u>c</u><i>o<i>c<b>o :) > \r\n &trade; \n Quack üöä")
				.executeAction("getMessagePreview", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("OK", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("Ambarabacici <u>c</u><i>o<i>c<b>o <img alt='Sorride' title='Sorride' class='emoticon' src='images/emo/1.gif'> &gt; \r" +
				"<BR> &trade; <BR> Quack üöä</b></i></i>", reader.nextString());
	}

	@Test
	public void testGetSingleMessageContent() throws Exception {
		setUserAdmin()
				.setParameter("msgId", "3")
				.executeAction("getSingleMessageContent", Action.Method.GET);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("OK", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("<span class='quoteLvl1'>Scritto da: " +
				"<BR>&gt; Proot !<BR></span><BR>" +
				"<img alt='Arrabbiato' title='Arrabbiato' class='emoticon' src='images/emo/7.gif'> !", reader.nextString());
	}

	@Test
	public void testGetSingleMessageContent_PREF_COLLAPSE_QUOTES_yes() throws Exception {
		setUserAdmin()
				.setParameter("msgId", "3")
				.setParameter(User.PREF_COLLAPSE_QUOTES, "yes")
				.executeAction("getSingleMessageContent", Action.Method.GET);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("OK", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("<span class='quoteLvl1'>Scritto da: <BR></span>" +
				"<div class='quote-container'>" +
				"<div><span class='quoteLvl1'>&gt; Proot !<BR></span></div>" +
				"</div><BR>" +
				"<img alt='Arrabbiato' title='Arrabbiato' class='emoticon' src='images/emo/7.gif'> !", reader.nextString());
	}

	@Test
	public void testGetSingleMessageContent_PREF_COLLAPSE_QUOTES_no() throws Exception {
		setUserAdmin()
				.setParameter("msgId", "3")
				.setParameter(User.PREF_COLLAPSE_QUOTES, "no")
				.executeAction("getSingleMessageContent", Action.Method.GET);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("OK", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("<span class='quoteLvl1'>Scritto da: <BR>" +
				"&gt; Proot !<BR></span><BR>" +
				"<img alt='Arrabbiato' title='Arrabbiato' class='emoticon' src='images/emo/7.gif'> !", reader.nextString());
	}

	@Test
	public void testGetSingleMessageContent_PREF_EMBEDDYT_yes() throws Exception {
		setUserAdmin()
				.setParameter("msgId", "6")
				.setParameter(User.PREF_EMBEDDYT, "yes")
				.executeAction("getSingleMessageContent", Action.Method.GET);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("OK", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("Nel Forum Principale<BR>" +
				"<div class=youtube-video-box style=\"background-image: url(http://img.youtube.com/vi/cA9gUspn6gc/0.jpg)\">" +
				"<div class=youtube-video-title>03. Sade - Smooth Operator</div><div class='youtube-buttons'>" +
				"<a href=\"http://www.youtube.com/watch?v=cA9gUspn6gc\" target=_blank>Vai alla pagina</a><br>" +
				"<a href=# onclick=\"youtube_embed(this, 'cA9gUspn6gc', '?start=0'); return false\">Visualizza embed</a></div></div>", reader.nextString());
	}

	@Test
	public void testGetSingleMessageContent_PREF_EMBEDDYT_no() throws Exception {
		setUserAdmin()
				.setParameter("msgId", "6")
				.setParameter(User.PREF_EMBEDDYT, "no")
				.executeAction("getSingleMessageContent", Action.Method.GET);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("OK", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("Nel Forum Principale<BR>" +
				"<div class=youtube-video-box style=\"background-image: url(http://img.youtube.com/vi/cA9gUspn6gc/0.jpg)\">" +
				"<div class=youtube-video-title>03. Sade - Smooth Operator</div><div class='youtube-buttons'>" +
				"<a href=\"http://www.youtube.com/watch?v=cA9gUspn6gc\" target=_blank>Vai alla pagina</a>" +
				"<br><a href=# onclick=\"youtube_embed(this, 'cA9gUspn6gc', '?start=0'); return false\">Visualizza embed</a></div></div>", reader.nextString());
	}

	@Test
	public void testGetSingleMessageContent_PREF_SHOWANONIMG_yes() throws Exception {
		setUserAdmin()
				.setParameter("msgId", "5")
				.setParameter(User.PREF_SHOWANONIMG, "yes")
				.executeAction("getSingleMessageContent", Action.Method.GET);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("OK", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("<img alt='Triste' title='Triste' class='emoticon' src='images/emo/10.gif'><BR>" +
				"<a rel='nofollow noreferrer' target='_blank' href=\"http://caravanpark.it/imgNoCrop/600/600/1-1/mediaDB/TUB1238_1.jpg\">Immagine postata da ANOnimo</a>" +
				"<a href=\"https://www.google.com/searchbyimage?image_url=http://caravanpark.it/imgNoCrop/600/600/1-1/mediaDB/TUB1238_1.jpg\" title='Ricerca immagini simili' rel='nofollow noreferrer' target='_blank'>" +
				"<img src=\"https://www.google.com/favicon.ico\" alt='' style='width: 16px; height: 16px;'></a>", reader.nextString());
	}

	@Test
	public void testGetSingleMessageContent_PREF_SHOWANONIMG_no() throws Exception {
		setUserAdmin()
				.setParameter("msgId", "5")
				.setParameter(User.PREF_SHOWANONIMG, "no")
				.executeAction("getSingleMessageContent", Action.Method.GET);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("OK", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("<img alt='Triste' title='Triste' class='emoticon' src='images/emo/10.gif'><BR>" +
				"<a rel='nofollow noreferrer' target='_blank' class='preview' href=\"http://caravanpark.it/imgNoCrop/600/600/1-1/mediaDB/TUB1238_1.jpg\">" +
				"<img class='userPostedImage' alt='Immagine postata dall&#39;utente' src=\"http://caravanpark.it/imgNoCrop/600/600/1-1/mediaDB/TUB1238_1.jpg\"></a>" +
				"<a href=\"https://www.google.com/searchbyimage?image_url=http://caravanpark.it/imgNoCrop/600/600/1-1/mediaDB/TUB1238_1.jpg\" title='Ricerca immagini simili' rel='nofollow noreferrer' target='_blank'>" +
				"<img src=\"https://www.google.com/favicon.ico\" alt='' style='width: 16px; height: 16px;'></a>", reader.nextString());
	}

	@Test
	public void testInsertMessage_noForum() throws Exception {
		setUserSfigato()
				.setParameter("id", "-1")
				.setParameter("parentId", "1")
				.setParameter("subject", "subject")
				.setParameter("text", "testo del messaggio")
				.executeAction("insertMessage", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("OK", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		String content = reader.nextString();
		Assert.assertTrue("'" + content + "' doesn't match regexp", content.matches("/Messages\\?action=init&rnd=\\d{13}#msg10"));
	}

	@Test
	public void testInsertMessage_emptyForum() throws Exception {
		setUserSfigato()
				.setParameter("forum", "")
				.setParameter("parentId", "1")
				.setParameter("subject", "subject")
				.setParameter("text", "testo del messaggio")
				.executeAction("insertMessage", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("Sono consufo, non riesco a interpretare l'id 'null'", reader.nextString());
	}

	@Test
	public void testInsertMessage_wrongForum() throws Exception {
		setUserSfigato()
				.setParameter("forum", "wrong")
				.setParameter("parentId", "1")
				.setParameter("subject", "subject")
				.setParameter("text", "testo del messaggio")
				.executeAction("insertMessage", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("Ma che cacchio di forum e' 'wrong' ?!?", reader.nextString());
	}

	@Test
	public void testInsertMessage_noPrentId() throws Exception {
		setUserSfigato()
				.setParameter("forum", "Forum iniziale")
				.setParameter("subject", "subject")
				.setParameter("text", "testo del messaggio")
				.executeAction("insertMessage", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("Il valore null assomiglia poco a un numero?", reader.nextString());
	}

	@Test
	public void testInsertMessage_wrongPrentId() throws Exception {
		setUserSfigato()
				.setParameter("forum", "Forum iniziale")
				.setParameter("parentId", "100")
				.setParameter("subject", "subject")
				.setParameter("text", "testo del messaggio")
				.executeAction("insertMessage", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("Sono consufo, non riesco a interpretare l'id 'null'", reader.nextString());
	}

	@Test
	public void testInsertMessage_noSubject() throws Exception {
		setUserSfigato()
				.setParameter("forum", "Forum iniziale")
				.setParameter("parentId", "1")
				.setParameter("text", "testo del messaggio")
				.executeAction("insertMessage", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("Oggetto di almeno di 3 caratteri, cribbio !", reader.nextString());
	}

	@Test
	public void testInsertMessage_subjectTooLong() throws Exception {
		setUserSfigato()
				.setParameter("forum", "Forum iniziale")
				.setParameter("parentId", "1")
				.setParameter("subject", StringUtils.repeat("*", Messages.MAX_SUBJECT_LENGTH + 1))
				.setParameter("text", "testo del messaggio")
				.executeAction("insertMessage", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("LOL oggetto piu' lungo di 80 caratteri !", reader.nextString());
	}

	@Test
	public void testInsertMessage_subjectTooShort() throws Exception {
		setUserSfigato()
				.setParameter("forum", "Forum iniziale")
				.setParameter("parentId", "100")
				.setParameter("subject", "*")
				.setParameter("text", "testo del messaggio")
				.executeAction("insertMessage", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("Oggetto di almeno di 3 caratteri, cribbio !", reader.nextString());
	}

	@Test
	public void testInsertMessage_noText() throws Exception {
		setUserSfigato()
				.setParameter("forum", "Forum iniziale")
				.setParameter("parentId", "1")
				.setParameter("subject", "subject")
				.executeAction("insertMessage", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("Un po di fantasia, scrivi almeno 5 caratteri ...", reader.nextString());
	}

	@Test
	public void testInsertMessage_textTooLong() throws Exception {
		setUserSfigato()
				.setParameter("forum", "Forum iniziale")
				.setParameter("parentId", "1")
				.setParameter("subject", "subject")
				.setParameter("text", StringUtils.repeat("*", Messages.MAX_MESSAGE_LENGTH + 1))
				.executeAction("insertMessage", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("Sei piu' logorroico di una Wakka, stai sotto i 40000 caratteri !", reader.nextString());
	}

	@Test
	public void testInsertMessage_textTooShort() throws Exception {
		setUserSfigato()
				.setParameter("forum", "Forum iniziale")
				.setParameter("parentId", "1")
				.setParameter("subject", "subject")
				.setParameter("text", "*")
				.executeAction("insertMessage", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("Un po di fantasia, scrivi almeno 5 caratteri ...", reader.nextString());
	}

	@Test
	public void testInsertMessage_noLogin_wrongCaptcha() throws Exception {

		setParameter("forum", "Forum iniziale")
				.setParameter("parentId", "1")
				.setParameter("subject", "subject")
				.setParameter("text", "testo del messaggio")
				.setParameter("captcha", "proooot")
				.setSessionAttribute("captcha", "FdT")
				.executeAction("insertMessage", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		Assert.assertEquals("Autenticazione/verifica captcha fallita", reader.nextString());
	}

	@Test
	public void testInsertMessage_noLogin_rightCaptcha() throws Exception {

		setParameter("forum", "Forum iniziale")
				.setParameter("parentId", "1")
				.setParameter("id", "-1")
				.setParameter("subject", "subject")
				.setParameter("text", "testo del messaggio")
				.setParameter("captcha", "proooot")
				.setSessionAttribute("captcha", "proooot")
				.executeAction("insertMessage", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("OK", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		String content = reader.nextString();
		Assert.assertTrue("'" + content + "' doesn't match regexp", content.matches("/Messages\\?action=init&rnd=\\d{13}#msg10"));
	}

	@Test
	public void testPedonizeThreadTree_noLogin() throws Exception {

		setParameter("rootMessageId", "8")
				.executeAction("pedonizeThreadTree", Action.Method.GET);

		checkNavigationMessage("Non furmigare !");
	}

	@Test
	public void testPedonizeThreadTree_noAdmin() throws Exception {

		setUserSfigato()
				.setParameter("rootMessageId", "8")
				.executeAction("pedonizeThreadTree", Action.Method.GET);

		checkNavigationMessage("Non furmigare Sfigato !!!");
	}

	@Test
	public void testPedonizeThreadTree() throws Exception {

		setUserAdmin()
				.setParameter("rootMessageId", "8")
				.executeAction("pedonizeThreadTree", Action.Method.GET);

		checkNavigationMessage("Pedonization completed.")
				.checkLocationHeader("Threads");

		List<MessageDTO> messages = messagesDAO.getMessagesByThread(Long.parseLong("8"));
		for (MessageDTO message : messages) {
			Assert.assertEquals("Proc di Catania", message.getForum());
		}

	}

	@Test
	public void testHideMessage_noLogin() throws Exception {

		setParameter("msgId", "8")
				.executeAction("hideMessage", Action.Method.GET);

		checkNavigationMessage("Non furmigare !");

	}

	@Test
	public void testHideMessage_noAdmin() throws Exception {

		setUserSfigato()
				.setParameter("msgId", "8")
				.executeAction("hideMessage", Action.Method.GET);

		checkNavigationMessage("Non furmigare Sfigato !!!");
	}

	@Test
	public void testHideMessage() throws Exception {

		setUserAdmin()
				.setParameter("msgId", "8")
				.executeAction("hideMessage", Action.Method.GET);

		checkNavigationMessage("Messaggio infernale nascosto agli occhi dei giovini troll.");
	}

	@Test
	public void testRestoreHiddenMessage() throws Exception {

		setUserAdmin()
				.setParameter("msgId", "8")
				.executeAction("restoreHiddenMessage", Action.Method.GET);

		// si vabbeh ...
		checkNavigationMessage("Messaggio infernale nascosto agli occhi dei giovini troll.");
	}

	@Test
	public void testGetRandomQuote() throws Exception {
		executeAction("getRandomQuote", Action.Method.GET);

		String responseContent = getResponseContent();
		Assert.assertNotNull(responseContent);
		String[] split = responseContent.split("\n");
		Assert.assertEquals(2, split.length);
	}

	@Test
	public void testLike_noLogin() throws Exception {
		setParameter("msgId", "8")
				.executeAction("like", Action.Method.GET);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		String content = reader.nextString();
		Assert.assertEquals("Non furmigare !", content);
	}

	@Test
	public void testLike_noMsg() throws Exception {
		setUserSfigato()
				.executeAction("like", Action.Method.GET);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		String content = reader.nextString();
		Assert.assertEquals("Nessun messaggio selezionato", content);
	}

	@Test
	public void testLike_noSelect() throws Exception {
		setUserSfigato()
				.setParameter("msgId", "8")
				.executeAction("like", Action.Method.GET);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("MSG", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		String content = reader.nextString();
		Assert.assertEquals("+1 o -1, deciditi cribbio !", content);
	}

	@Test
	public void testLike() throws Exception {
		setUserSfigato()
				.setParameter("msgId", "8")
				.setParameter("like", "true")
				.executeAction("like", Action.Method.GET);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("OK", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		String content = reader.nextString();
		Assert.assertEquals("Hai espresso il tuo inalienabile diritto di voto !", content);
	}

	@Test
	public void testSaveTag_noLogin() throws Exception {
		executeAction("saveTag", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertEquals("", out);
	}

	@Test
	public void testSaveTag_noMsgId() throws Exception {
		setUserSfigato()
				.setParameter("value", "prot")
				.executeAction("saveTag", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("KO", reader.nextString());
	}

	@Test
	public void testSaveTag_noValue() throws Exception {
		setUserSfigato()
				.setParameter("msgId", "8")
				.executeAction("saveTag", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("KO", reader.nextString());
	}

	@Test
	public void testSaveTag() throws Exception {
		setUserSfigato()
				.setParameter("msgId", "8")
				.setParameter("value", "prot")
				.executeAction("saveTag", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("OK", reader.nextString());
		Assert.assertEquals("content", reader.nextName());
		String content = reader.nextString();
		Assert.assertEquals("3", content);
	}

	@Test
	public void testDeleTag_noLogin() throws Exception {
		setParameter("t_id", "2")
				.setParameter("m_id", "9")
				.executeAction("deleTag", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertEquals("", out);
	}

	@Test
	public void testDeleTag_noAdmim() throws Exception {
		setUserSfigato()
				.setParameter("t_id", "2")
				.setParameter("m_id", "9")
				.executeAction("deleTag", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("KO", reader.nextString());
	}

	@Test
	public void testDeleTag_no_t_id() throws Exception {
		setUserAdmin()
				.setParameter("m_id", "9")
				.executeAction("deleTag", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("KO", reader.nextString());
	}


	@Test
	public void testDeleTag_no_m_id() throws Exception {
		setUserAdmin()
				.setParameter("t_id", "2")
				.executeAction("deleTag", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("KO", reader.nextString());
	}

	@Test
	public void testDeleTag() throws Exception {
		setUserAdmin()
				.setParameter("t_id", "2")
				.setParameter("m_id", "9")
				.executeAction("deleTag", Action.Method.POST);

		String out = getResponseContent();
		Assert.assertNotNull(out);
		JsonReader reader = new JsonReader(new StringReader(out));
		reader.beginObject();
		Assert.assertEquals("resultCode", reader.nextName());
		Assert.assertEquals("OK", reader.nextString());
	}

	@Test
	public void testGetMessagesByTag() throws Exception {
		setParameter("t_id", "2")
				.executeAction("getMessagesByTag", Action.Method.GET);

		checkWebsiteTitleStartsWith("Ricerca per tag");

		List<MessageDTO> messages = getAttribute("messages");
		checkListSize(messages, 1);

		int totalSize = getAttribute("totalSize");
		Assert.assertEquals(1, totalSize);

		int resultSize = getAttribute("resultSize");
		Assert.assertEquals(1, resultSize);
	}


	@Test
	public void testMobileComposer() throws Exception {
		// TODO
	}

}
