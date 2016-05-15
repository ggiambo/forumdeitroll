package com.forumdeitroll.test.servlets;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.profiler2.ProfilerAPI;
import com.forumdeitroll.servlets.Action;
import com.forumdeitroll.servlets.MainServlet;
import com.forumdeitroll.test.BaseTest;
import com.mockrunner.mock.web.*;
import org.junit.Before;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.Assert.*;

public abstract class BaseServletsTest extends BaseTest implements SetupStep, CheckStep {

    private static MainServlet servlet;

    static private MockServletConfig servletConfig = new MockServletConfig();
    static private MockHttpServletRequest req = new MockHttpServletRequest();
    static private MockHttpServletResponse res = new MockHttpServletResponse();
    static private AuthorDTO loggedUser;

    @Before
    public void before() throws ServletException {

        assertNotNull("Servlet null", servlet);

	    HttpSession session = new MockHttpSession();
	    session.setAttribute(MainServlet.ANTI_XSS_TOKEN, "ANTI_XSS_TOKEN");

	    req.resetAll();
        req.setSession(session);
	    req.setupAddParameter("token", "ANTI_XSS_TOKEN");

        res.resetAll();

        servletConfig.setServletContext(new MockServletContext());
        servlet.init(servletConfig);

        ProfilerAPI.enabled = false;
        setParameter("jsonProfileData", "{}");

        loggedUser = new AuthorDTO(null);
    }

    public static void setServlet(MainServlet servlet) {
        BaseServletsTest.servlet = servlet;
    }

    public SetupStep setUserSfigato() {
        loggedUser.setNick("Sfigato");
        loggedUser.setMessages(1);
        loggedUser.setSalt("dummy");
        loggedUser.getPreferences().clear();
        req.setAttribute(MainServlet.LOGGED_USER_REQ_ATTR, loggedUser);

        return this;
    }

    public SetupStep setUserAdmin() {
        loggedUser.setNick("admin");
        loggedUser.setMessages(2);
        loggedUser.setSalt("dummy");
        loggedUser.getPreferences().put("super", "yes");
	    req.setAttribute(MainServlet.LOGGED_USER_REQ_ATTR, loggedUser);

	    return this;
    }

    public SetupStep setAttribute(String attributeName, Object attribute) {
        req.setAttribute(attributeName, attribute);

        return this;
    }

	public SetupStep setParameter(String parameterName, String parameter) {
		req.setupAddParameter(parameterName, parameter);

		return this;
	}

	public SetupStep setSessionAttribute(String attributeName, Object attribute) {
		req.getSession().setAttribute(attributeName, attribute);

		return this;
	}

    public CheckStep executeAction(String actionName, Action.Method actionMethod) throws Exception {
        req.setAttribute("action", actionName);
        switch (actionMethod) {
            case GET:
                servlet.doGet(req, res);
                break;
            case POST:
                servlet.doPost(req, res);
                break;
            default:
                throw new Exception("Unknowm Action.Method " + actionMethod);
        }

		checkNoServletException();
        return this;
    }

    public CheckStep executeDoBefore() {
        servlet.doBefore(req, res);
		checkNoServletException();

        return this;
    }

    public CheckStep executeInit() throws Exception {
        executeAction("init", Action.Method.GET);
		checkNoServletException();

        return this;
    }

    public CheckStep checkWebsiteTitle() {
        String websiteTitle = (String) req.getAttribute("websiteTitle");
        assertNotNull(websiteTitle);

        return this;
    }

    public CheckStep checkWebsiteTitleStartsWith(String webSiteTitle) {
		String actualWebsiteTitle = (String) req.getAttribute("websiteTitle");
		assertNotNull(actualWebsiteTitle);
		assertTrue(String.format("Webstite title '%s' doesn't start with '%s'", actualWebsiteTitle, webSiteTitle),
                actualWebsiteTitle.startsWith(webSiteTitle));

        return this;
    }

    public CheckStep checkNavigationMessage(String messageContent) {
        MainServlet.NavigationMessage navigationMessage = (MainServlet.NavigationMessage) req.getAttribute("navigationMessage");
        assertNotNull(navigationMessage);
        assertEquals(String.format("navigation message '%s' doesn't start with '%s'", messageContent, navigationMessage.getContent()),
                messageContent, navigationMessage.getContent());

        return this;
    }

    public CheckStep checkLocationHeader(String location) {
        assertEquals(location, res.getHeader("Location"));

        return this;
    }

    public CheckStep checkListSize(List<?> list, int size) {
        assertNotNull("List is null", list);
        assertEquals(size, list.size());

        return this;
    }

	public CheckStep checkNoServletException() {
		String exceptionStackTrace = getAttribute("exceptionStackTrace");
		assertNull("Error on handling request: " + exceptionStackTrace, exceptionStackTrace);

		return this;
	}

    <T> T getAttribute(String attributeName) {
        return (T) req.getAttribute(attributeName);
    }

	public String getResponseContent() {
		return res.getOutputStreamContent();
	}

}
