package com.forumdeitroll.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.forumdeitroll.PasswordUtils;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.persistence.PersistenceFactory;

/**
 * Servlet implementation class Irc
 */
public class Irc extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static String Topic = null;
	private static String Lista_utenti = null;
	private static Date Mtime = null;

	protected void printCurrent(final PrintWriter out) {
		if (StringUtils.isEmpty(Topic) || StringUtils.isEmpty(Lista_utenti)) {
			out.print("nessuna informazione disponibile");
			return;
		}
		out.println("Topic: "+Topic);
		out.println("Lista utenti: "+Lista_utenti);
		out.println("Aggiornato alle "+new SimpleDateFormat("HH:mm").format(Mtime));
		return;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		printCurrent(response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		// se invocato senza parametri
		if (!request.getParameterNames().hasMoreElements()) {
			printCurrent(out);
			return;
		}
		try {

			String lista_utenti = request.getParameter("lista_utenti");
			String topic = request.getParameter("topic");
			String username = request.getParameter("username");
			String password = request.getParameter("password");

			if (StringUtils.isEmpty(username)) throw new Exception("username is null");
			if (StringUtils.isEmpty(password)) throw new Exception("password is null");
			if (StringUtils.isEmpty(lista_utenti)) throw new Exception("lista_utenti is null");
			if (StringUtils.isEmpty(topic)) throw new Exception("topic is null");

			AuthorDTO author = PersistenceFactory.getInstance().getAuthor(username);
			if (!PasswordUtils.hasUserPassword(author, password)) {
				throw new Exception("pazzword ezzere zbagliata");
			}
			String admin = PersistenceFactory.getInstance().getPreferences(author).get("super");
			if (!"yes".equals(admin)) {
				throw new Exception("utente non ezzere admin");
			}

			Lista_utenti = lista_utenti;
			Topic = topic;
			Mtime = new Date();

			out.println("OK");

		} catch (Exception e) {
			out.println(e.getMessage());
		}


	}

}
