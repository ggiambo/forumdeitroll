package com.forumdeitroll.servlets;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.forumdeitroll.profiler2.ProfilerAPI;
import com.forumdeitroll.profiler2.ProfilerLogger;
import com.forumdeitroll.profiler2.ProfilerRule;
import com.forumdeitroll.profiler2.ProfilerRules;
import com.forumdeitroll.profiler2.ProfilerStorage;
import com.forumdeitroll.profiler2.ReqInfo;
import com.google.common.base.Joiner;
import com.google.gson.Gson;

public class UserProfiler extends MainServlet {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(UserProfiler.class);

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ProfilerAPI.enabled =
				! "checked".equals(
						miscDAO
						.getSysinfoValue(Admin.ADMIN_PREF_DISABLE_PROFILER));
	}

	@Action(method=Action.Method.GET)
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return browse(req, res);
	}

	private String page(HttpServletRequest req) {
		req.setAttribute("rules", ProfilerRules.rules);
		req.setAttribute("records", ProfilerLogger.records);
		req.setAttribute("bannedIPs", Joiner.on("\n").join(Messages.BANNED_IPs));
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

	@Action(method=Action.Method.GET)
	String newRule(HttpServletRequest req, HttpServletResponse res) throws Exception {
		boolean isAdmin = "yes".equals(login(req).getPreferences().get("super"));
		if (!isAdmin) {
			return null;
		}
		req.setAttribute("rule", new ProfilerRule());
		return page(req);
	}

	@Action(method=Action.Method.GET)
	String editRule(HttpServletRequest req, HttpServletResponse res) throws Exception {
		boolean isAdmin = "yes".equals(login(req).getPreferences().get("super"));
		if (!isAdmin) {
			return null;
		}
		String uuid  = req.getParameter("uuid");
		for (ProfilerRule rule : ProfilerRules.rules) {
			if (uuid.equals(rule.getUuid())) {
				req.setAttribute("rule", rule);
			}
		}
		return page(req);
	}

	@Action(method=Action.Method.POST)
	String saveRule(HttpServletRequest req, HttpServletResponse res) throws Exception {
		boolean isAdmin = "yes".equals(login(req).getPreferences().get("super"));
		if (!isAdmin) {
			return null;
		}
		String uuid  = req.getParameter("uuid");
		String label = req.getParameter("label");
		String code  = req.getParameter("code");
		for (ProfilerRule rule : ProfilerRules.rules) {
			if (uuid.equals(rule.getUuid())) {
				rule.setLabel(label);
				rule.setCode(code);
				ProfilerStorage.save();
				return page(req);
			}
		}
		ProfilerRule rule = new ProfilerRule();
		rule.setUuid(UUID.randomUUID().toString());
		rule.setLabel(label);
		rule.setCode(code);
		ProfilerRules.rules.add(rule);
		ProfilerStorage.save();
		return page(req);
	}
	@Action(method=Action.Method.GET)
	String deleteRule(HttpServletRequest req, HttpServletResponse res) throws Exception {
		boolean isAdmin = "yes".equals(login(req).getPreferences().get("super"));
		if (!isAdmin) {
			return null;
		}
		String uuid  = req.getParameter("uuid");
		for (Iterator<ProfilerRule> it = ProfilerRules.rules.iterator(); it.hasNext();) {
			if (uuid.equals(it.next().getUuid())) {
				it.remove();
			}
		}
		ProfilerStorage.save();
		res.sendRedirect("UserProfiler");
		return null;
	}

	@Action(method=Action.Method.POST)
	String testRule(HttpServletRequest req, HttpServletResponse res) throws Exception {
		boolean isAdmin = "yes".equals(login(req).getPreferences().get("super"));
		if (!isAdmin) {
			return null;
		}
		String code = req.getParameter("code");
		ReqInfo reqInfo = new Gson().fromJson(req.getParameter("reqInfo"), ReqInfo.class);
		res.getWriter().print(ProfilerRules.checkRule(code, reqInfo));
		return null;
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
		}
		res.sendError(304); // not modified
		return null;
	}

	@Action(method = Action.Method.POST)
	String updateBannedIPs(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Messages.BANNED_IPs.clear();
		String bannedIPs = req.getParameter("bannedIPs");
		String[] ips = bannedIPs.split("\\r?\\n");
		for (String ip : ips) {
			Messages.banIP(ip.trim());
		}
		return page(req);
	}

	private static void printRequest(HttpServletRequest request) {
		logger.info(request.getMethod() + " " + request.getRequestURI() + " " + request.getProtocol());
		for (Enumeration<String> en = request.getParameterNames(); en.hasMoreElements();) {
			String name = en.nextElement();
			String value = request.getParameter(name);
			logger.info("Param  " + name + "=" + value);
		}
		for (Enumeration<String> en = request.getHeaderNames(); en.hasMoreElements();) {
			String name = en.nextElement();
			String value = request.getHeader(name);
			logger.info("Header " + name + ": " + value);
		}
	}
}
