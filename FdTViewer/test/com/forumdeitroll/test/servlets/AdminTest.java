package com.forumdeitroll.test.servlets;

import com.forumdeitroll.persistence.AdDTO;
import com.forumdeitroll.servlets.Action;
import com.forumdeitroll.servlets.Admin;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import java.util.List;

public class AdminTest extends BaseServletsTest {

    @BeforeClass
    public static void initServlet() throws Exception {
        setServlet(new Admin());
    }

    @Test
    public void testInit_noLogin() throws Exception {
        executeInit()
                .checkNavigationMessage("Passuord ezzere sbaliata !")
                .checkWebsiteTitleStartsWith("Login @")
                .checkLocationHeader("User");
    }

    @Test
    public void testInit_loginNoAdmin() throws Exception {
        setUserSfigato()
                .executeInit()
                .checkNavigationMessage("Non sei un admin")
                .checkWebsiteTitleStartsWith("Login @")
                .checkLocationHeader("User");
    }

    @Test
    public void testInit_loginAdmin() throws Exception {
        setUserAdmin()
                .executeInit();

        String blockTorExitNodes = getAttribute(Admin.ADMIN_PREF_BLOCK_TOR);
        Assert.assertTrue(StringUtils.EMPTY, StringUtils.isEmpty(blockTorExitNodes));

        String disableUserProfiler = getAttribute(Admin.ADMIN_PREF_DISABLE_PROFILER);
        Assert.assertTrue(StringUtils.EMPTY, StringUtils.isEmpty(disableUserProfiler));
    }

    @Test
    public void testDoBefore() throws ServletException {
        executeDoBefore();

        Assert.assertNull(getAttribute(Admin.ADMIN_PREF_BLOCK_TOR));
        Assert.assertNull(getAttribute(Admin.ADMIN_PREF_DISABLE_PROFILER));
        List<String> websiteTitles = getAttribute(Admin.ADMIN_WEBSITE_TITLES);
        checkListSize(websiteTitles, 3);
        List<AdDTO> adminFakeAds = getAttribute(Admin.ADMIN_FAKE_ADS);
        checkListSize(adminFakeAds, 9);
    }

    @Test
    public void testLoginAction() throws Exception {
        executeAction("loginAction", Action.Method.GET)
                .checkWebsiteTitleStartsWith("Login @");
    }

    @Test
    public void testUpdatePreferences_noLogin() throws Exception {
        executeAction("updatePreferences", Action.Method.GET)
                .checkNavigationMessage("Passuord ezzere sbaliata !")
                .checkWebsiteTitleStartsWith("Login @")
                .checkLocationHeader("User");
    }

    @Test
    public void testUpdatePreferences_loginNoAdmin() throws Exception {
        setUserSfigato()
                .executeAction("updatePreferences", Action.Method.GET)
                .checkNavigationMessage("Non sei un admin")
                .checkWebsiteTitleStartsWith("Login @")
                .checkLocationHeader("User");
    }


    @Test
    public void testUpdatePreferences_loginAdmin() throws Exception {
        setUserAdmin()
                .executeAction("updatePreferences", Action.Method.GET);

        String blockTorExitNodes = getAttribute(Admin.ADMIN_PREF_BLOCK_TOR);
        Assert.assertTrue(StringUtils.EMPTY, StringUtils.isEmpty(blockTorExitNodes));

        String disableUserProfiler = getAttribute(Admin.ADMIN_PREF_DISABLE_PROFILER);
        Assert.assertTrue(StringUtils.EMPTY, StringUtils.isEmpty(disableUserProfiler));
    }

}
