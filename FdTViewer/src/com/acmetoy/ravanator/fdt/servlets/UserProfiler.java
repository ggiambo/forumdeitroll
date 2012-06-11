package com.acmetoy.ravanator.fdt.servlets;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.acmetoy.ravanator.fdt.profiler.UserProfile;
import com.google.gson.Gson;

public class UserProfiler extends MainServlet {
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
		System.out.println(request.getMethod() + " " + request.getRequestURI() + " " + request.getProtocol());
		for (Enumeration en = request.getParameterNames(); en.hasMoreElements();) {
			String name = (String) en.nextElement();
			String value = request.getParameter(name);
			System.out.println("Param  " + name + "=" + value);
		}
		for (Enumeration en = request.getHeaderNames(); en.hasMoreElements();) {
			String name = (String) en.nextElement();
			String value = request.getHeader(name);
			System.out.println("Header " + name + ": " + value);
		}
		System.out.println();
	}
	
	/**
	 * Verifica se il profilo dell'utente è in una banlist
	 */
	@Action(method=Action.Method.GET)
	String check(HttpServletRequest req, HttpServletResponse res) throws Exception {
		//printRequest(request);
		UserProfile profile = new Gson().fromJson(req.getParameter("jsonProfileData"), UserProfile.class);
		profile.setIpAddress(req.getHeader("X-Forwarded-For") != null ? req.getHeader("X-Forwarded-For") : req.getRemoteAddr());
		profile.setNick(login(req).getNick());
		profile = com.acmetoy.ravanator.fdt.profiler.UserProfiler.getInstance().guess(profile);
		
		// TODO controllo banlist qua, un esempio di response attesa dal javascript
//		if (banlist.contains(profile.getUuid())) {
//			response.getWriter().print("true");
//		} else {
//			response.getWriter().print("false");
//		}
		
		res.getWriter().print("false");
		return null;
	}
	private String page(HttpServletRequest req) {
		req.setAttribute("profiles", com.acmetoy.ravanator.fdt.profiler.UserProfiler.getInstance().profiles);
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
		String one = req.getParameter("one");
		String two = req.getParameter("two");
		com.acmetoy.ravanator.fdt.profiler.UserProfiler.getInstance().mergeKnownProfiles(one, two);
		return page(req);
	}
	
	@Action(method=Action.Method.POST)
	String switchBan(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String uuid = req.getParameter("uuid");
		UserProfile profile = com.acmetoy.ravanator.fdt.profiler.UserProfiler.getInstance().lookup(uuid);
		profile.setBannato(!profile.isBannato());
		return page(req);
	}
}
