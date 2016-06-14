package com.forumdeitroll.servlets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.forumdeitroll.persistence.AdDTO;
import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.profiler2.ProfilerAPI;
import com.forumdeitroll.util.IPMemStorage;
import com.forumdeitroll.util.Ratelimiter;

public class Admin extends MainServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(Admin.class);

	public static final String ADMIN_PREF_BLOCK_TOR = "blockTorExitNodes";
	public static final String ADMIN_PREF_DISABLE_PROFILER = "disableUserProfiler";
	public static final String ADMIN_PREF_CAPTCHA_LEVEL = "captchaLevel";
	public static final String ADMIN_WEBSITE_TITLES = "websiteTitles";
	public static final String ADMIN_FAKE_ADS = "fakeAds";

	public static final String ANTI_XSS_TOKEN = "anti-xss-token";

	public static final int LOGIN_TIME_LIMIT = 3 * 60 * 1000;
	public static final int LOGIN_NUMBER_LIMIT = 5;

	protected final Ratelimiter<String> loginRatelimiter = new Ratelimiter<String>(LOGIN_TIME_LIMIT, LOGIN_NUMBER_LIMIT);

	public static final Pattern FAKE_AD_REQ_PARAM = Pattern.compile("fakeAds\\[(-?\\d+)\\]\\.(.*)");

	@Override
	public void doBefore(HttpServletRequest req, HttpServletResponse res) {
		req.setAttribute(ADMIN_PREF_BLOCK_TOR, miscDAO.getSysinfoValue(ADMIN_PREF_BLOCK_TOR));
		req.setAttribute(ADMIN_PREF_DISABLE_PROFILER, miscDAO.getSysinfoValue(ADMIN_PREF_DISABLE_PROFILER));
		req.setAttribute(ADMIN_WEBSITE_TITLES, adminDAO.getTitles());
		req.setAttribute(ADMIN_FAKE_ADS, adminDAO.getAllAds());
	}

	@Action
	String init(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (loginRatelimiter.limited(IPMemStorage.requestToIP(req))) {
			setNavigationMessage(req, NavigationMessage.warn("Hai rotto il cazzo"));
			return loginAction(req, res);
		}

		AuthorDTO loggedUser = login(req);
		setWebsiteTitlePrefix(req, "");
		if (loggedUser == null || !loggedUser.isValid()) {
			loginRatelimiter.increment(IPMemStorage.requestToIP(req));
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		if (!"yes".equals(loggedUser.getPreferences().get("super"))) {
			setNavigationMessage(req, NavigationMessage.warn("Non sei un admin"));
			return loginAction(req,  res);
		}

		return "prefs.jsp";

	}

	/**
	 * Mostra la pagina di login
	 * @param req
	 * @param res
	 * @return
	 */
	@Action
	String loginAction(HttpServletRequest req, HttpServletResponse res) throws Exception {
		setWebsiteTitlePrefix(req, "Login");
		res.sendRedirect("User");
		return null;
	}

	/**
	 * Cambia le preferences
	 */
	@Action
	String updatePreferences(HttpServletRequest req, HttpServletResponse res) throws Exception {
		AuthorDTO loggedUser = (AuthorDTO)req.getAttribute(MainServlet.LOGGED_USER_REQ_ATTR);
		if (loggedUser == null || !loggedUser.isValid()) {
			setNavigationMessage(req, NavigationMessage.warn("Passuord ezzere sbaliata !"));
			return loginAction(req,  res);
		}

		if (!"yes".equals(loggedUser.getPreferences().get("super"))) {
			setNavigationMessage(req, NavigationMessage.warn("Non sei un admin"));
			return loginAction(req,  res);
		}

		String blockTorExitNodes = req.getParameter(ADMIN_PREF_BLOCK_TOR);
		if (!StringUtils.isEmpty(blockTorExitNodes)) {
			adminDAO.setSysinfoValue(ADMIN_PREF_BLOCK_TOR, "checked");
		} else {
			adminDAO.setSysinfoValue(ADMIN_PREF_BLOCK_TOR, "");
		}
		req.setAttribute(ADMIN_PREF_BLOCK_TOR, adminDAO.getSysinfoValue(ADMIN_PREF_BLOCK_TOR));
		String disableUserProfiler = req.getParameter(ADMIN_PREF_DISABLE_PROFILER);
		if (!StringUtils.isEmpty(disableUserProfiler)) {
			adminDAO.setSysinfoValue(ADMIN_PREF_DISABLE_PROFILER, "checked");
			ProfilerAPI.enabled = false;
		} else {
			adminDAO.setSysinfoValue(ADMIN_PREF_DISABLE_PROFILER, "");
			ProfilerAPI.enabled = true;
		}
		req.setAttribute(ADMIN_PREF_DISABLE_PROFILER, adminDAO.getSysinfoValue(ADMIN_PREF_DISABLE_PROFILER));

		String javascript = req.getParameter("javascript");
		if (javascript.length() > 255) {
			StringBuilder errMsg = new StringBuilder("javascript troppo lungo: ");
			errMsg.append(javascript.length()).append(" caratteri, max 255");
			setNavigationMessage(req, NavigationMessage.warn(errMsg.toString()));
		} else {
			adminDAO.setSysinfoValue("javascript", javascript);
		}
		req.setAttribute("javascript", javascript);

		String[] websiteTitles = req.getParameterValues(ADMIN_WEBSITE_TITLES);
		List<String> titles = new ArrayList<String>();
		if (websiteTitles != null) {
			for (String title : websiteTitles) {
				if (StringUtils.isNotEmpty(title)) {
					titles.add(title);
				}
			}
			adminDAO.setTitles(titles);
			cachedTitles.invalidate();
		}
		req.setAttribute(ADMIN_WEBSITE_TITLES, titles);

		// Es:
		// fakeAd[42].title
		// fakeAd[-2].visurl
		Map<Long, AdDTO> ads = new HashMap<Long, AdDTO>();
		Enumeration<String> paramNames = req.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			Matcher matcher = FAKE_AD_REQ_PARAM.matcher(paramName);
			if (matcher.matches()) {
				Long id = Long.parseLong(matcher.group(1));
				String segment = matcher.group(2);
				AdDTO adDTO = getAdDTO(id, ads);
				if ("title".equals(segment)) {
					adDTO.setTitle(req.getParameter(paramName));
				} else if ("visurl".equals(segment)) {
					adDTO.setVisurl(req.getParameter(paramName));
				} else if ("content".equals(segment)) {
					adDTO.setContent(req.getParameter(paramName));
				} else {
					LOG.warn("Cos'e' '" + segment + "' ?");
					ads.remove(id);
				}
			}
		}
		List<AdDTO> allAds = new ArrayList<AdDTO>();
		allAds.addAll(ads.values());
		// ordina cosi' come sono sulla GUI -> cosi' saranno salvati nel database
		Collections.sort(allAds, new Comparator<AdDTO>() {
			public int compare(AdDTO ad1, AdDTO ad2) {
				long res = ad1.getId() - ad2.getId();
				return (int) res;
			}
		});
		adminDAO.setAllAds(allAds);
		cachedAds.invalidate();
		req.setAttribute(ADMIN_FAKE_ADS, allAds);

		String captchaLevel = req.getParameter(ADMIN_PREF_CAPTCHA_LEVEL);
		try {
			int level = Integer.parseInt(captchaLevel);
			Misc.setCaptchaLevel(level);
		} catch (NumberFormatException e) {
			// chissenefrega !
		}

		return "prefs.jsp";
	}

	private AdDTO getAdDTO(Long id, Map<Long, AdDTO> ads) {
		AdDTO adDTO = ads.get(id);
		if (adDTO == null) {
			adDTO = new AdDTO();
			adDTO.setId(id);
			ads.put(id, adDTO);
		}
		return adDTO;
	}

}
