package com.forumdeitroll.servlets;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.forumdeitroll.profiler.UserProfile;
import com.google.gson.Gson;

public class UserProfiler extends MainServlet {
	
	private static Logger logger = Logger.getLogger(UserProfiler.class);
	
	private com.forumdeitroll.profiler.UserProfiler profiler =
			com.forumdeitroll.profiler.UserProfiler.getInstance();
	
	@Action(method=Action.Method.GET)
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return browse(req, res);
	}
	
	/**
	 * La prima chiamata assegna un parametro permr usando una redirect 301, cachata dai browser (eccetto safari) anche in modalità incognito
	 * La seconda verifica la presenza dell'etag o lo assegna (cache normale dei browser) da qui in poi il browser riceverà sempre un 304
	 */
	@Action(method=Action.Method.GET)
	String prof(HttpServletRequest req, HttpServletResponse res) throws Exception {
		//printRequest(request);
		String permr = req.getParameter("permr");
		if (permr == null) {
			// send permanent redirect
			res.setHeader("Location", "UserProfiler?action=prof&permr=" + UUID.randomUUID().toString());
			res.sendError(301);
			return null;
		}
		String etag = req.getHeader("If-None-Match");
		if (etag == null) {
			etag = "\"" + UUID.randomUUID().toString() + "\"";
			res.setHeader("Content-Type", "application/json");
			res.setHeader("ETag", "\"" + etag + "\"");
			PrintWriter out = res.getWriter();
			out.print("{\"permr\":\"" + permr + "\", \"etag\":" + etag + "}\n");
			out.close();
			return null;
		} else {
			res.sendError(304); // not modified
			return null;
		}
	}
	
	private static void printRequest(HttpServletRequest request) {
		logger.info(request.getMethod() + " " + request.getRequestURI() + " " + request.getProtocol());
		for (Enumeration en = request.getParameterNames(); en.hasMoreElements();) {
			String name = (String) en.nextElement();
			String value = request.getParameter(name);
			logger.info("Param  " + name + "=" + value);
		}
		for (Enumeration en = request.getHeaderNames(); en.hasMoreElements();) {
			String name = (String) en.nextElement();
			String value = request.getHeader(name);
			logger.info("Header " + name + ": " + value);
		}
	}
	
	/**
	 * Verifica se il profilo dell'utente è in una banlist
	 */
	@Action(method=Action.Method.GET)
	String check(HttpServletRequest req, HttpServletResponse res) throws Exception {
		printRequest(req);
		UserProfile profile = new Gson().fromJson(req.getParameter("jsonProfileData"), UserProfile.class);
		profile.setIpAddress(req.getHeader("X-Forwarded-For") != null ? req.getHeader("X-Forwarded-For") : req.getRemoteAddr());
		profile.setNick(login(req).getNick());
		UserProfile response = profiler.guess(profile);
		PrintWriter out = res.getWriter();
		out.print("{\"input\":");
		out.print(new Gson().toJson(profile));
		out.print(", \"profile\": \"");
		out.print(response.getUuid());
		out.print("\"}");
		return null;
	}
	
	@Action(method=Action.Method.GET)
	String snoop(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return "snoop.jsp";
	}
	
	private String page(HttpServletRequest req) {
		req.setAttribute("profiles", profiler.profiles);
		return "userProfiler.jsp";
	}
	
	@Action(method=Action.Method.GET)
	String browse(HttpServletRequest req, HttpServletResponse res) throws Exception {
		boolean isAdmin = "yes".equals(login(req).getPreferences().get("super"));
		if (!isAdmin) {
			return null;
		}
		return page(req);
	}
	
	@Action(method=Action.Method.POST)
	String merge(HttpServletRequest req, HttpServletResponse res) throws Exception {
		boolean isAdmin = "yes".equals(login(req).getPreferences().get("super"));
		if (!isAdmin) {
			return null;
		}
		String one = req.getParameter("one");
		String two = req.getParameter("two");
		profiler.mergeKnownProfiles(one, two);
		return page(req);
	}
	
	@Action(method=Action.Method.POST)
	String switchBan(HttpServletRequest req, HttpServletResponse res) throws Exception {
		boolean isAdmin = "yes".equals(login(req).getPreferences().get("super"));
		if (!isAdmin) {
			return null;
		}
		String uuid = req.getParameter("uuid");
		UserProfile profile = profiler.lookup(uuid);
		profile.setBannato(!profile.isBannato());
		return page(req);
	}
	@Action(method=Action.Method.POST)
	String deleteProfile(HttpServletRequest req, HttpServletResponse res) throws Exception {
		boolean isAdmin = "yes".equals(login(req).getPreferences().get("super"));
		if (!isAdmin) {
			return null;
		}
		String uuid = req.getParameter("uuid");
		logger.info("L'utente "+login(req).getNick()+" ha richiesto la seguente cancellazione del profilo "+uuid);
		for (ListIterator<UserProfile> profIt = profiler.profiles.listIterator(); profIt.hasNext();) {
			UserProfile profile = profIt.next();
			if (uuid.equals(profile.getUuid())) {
				logger.info("Viene cancellato il seguente profilo: "+new Gson().toJson(profile));
				profIt.remove();
				return page(req);
			}
		}
		setNavigationMessage(req, NavigationMessage.warn("Nessun profilo trovato con uuid "+uuid));
		return page(req);
	}
	
	@Action(method=Action.Method.POST)
	String deleteAttribute(HttpServletRequest req, HttpServletResponse res) throws Exception {
		boolean isAdmin = "yes".equals(login(req).getPreferences().get("super"));
		if (!isAdmin) {
			return null;
		}
		logger.info("L'utente "+login(req).getNick()+" ha richiesto la seguente cancellazione dai dati di profilazione: ");
		String uuid = req.getParameter("uuid");
		logger.info("uuid = "+uuid);
		String attributeName = req.getParameter("attributeName");
		logger.info("attributeName = "+attributeName);
		String attributeValue = req.getParameter("attributeValue");
		logger.info("attributeValue = "+attributeValue);
		try {
			UserProfile profile = profiler.lookup(uuid);
			if (attributeName.equals("permr+etag")) {
				profile.setPermr("");
				profile.setEtag("");
				return page(req);
			}
			TreeSet<String> attributeList = null;
			if (attributeName.equals("ipAddress"))
				attributeList = profile.getIpAddresses();
			else if (attributeName.equals("nickname"))
				attributeList = profile.getNicknames();
			else if (attributeName.equals("userAgent"))
				attributeList = profile.getUserAgents();
			else if (attributeName.equals("screenRes"))
				attributeList = profile.getScreenResolutions();
			else if (attributeName.equals("pluginHash"))
				attributeList = profile.getPluginHashes();
			else if (attributeName.equals("msgId"))
				attributeList = profile.getMsgIds();
			attributeList.remove(attributeValue);
		} catch (Exception e) {
			setNavigationMessage(req, NavigationMessage.error("Hai scritto bene tutto quanto? "+e.getMessage()));
		}
		return page(req);
	}
}
