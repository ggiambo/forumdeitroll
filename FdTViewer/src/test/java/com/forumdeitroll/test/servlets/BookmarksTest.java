package com.forumdeitroll.test.servlets;

import com.forumdeitroll.persistence.BookmarkDTO;
import com.forumdeitroll.servlets.Action;
import com.forumdeitroll.servlets.Bookmarks;
import com.forumdeitroll.servlets.Messages;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class BookmarksTest extends BaseServletsTest {

    @BeforeClass
    public static void initServlet() throws Exception {
        setServlet(new Bookmarks());
    }

    @Test
    public void testList_noLogin() throws Exception {
        executeAction("list", Action.Method.GET);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        Assert.assertNull(bookmarks);
    }

    @Test
    public void testList_login() throws Exception {
        setUserSfigato()
                .executeAction("list", Action.Method.GET);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        checkListSize(bookmarks, 2);
    }

    @Test
    public void testAdd_noLogin() throws Exception {
        executeAction("add", Action.Method.GET);

        Long highlight = getAttribute("highlight");
        Assert.assertNull(highlight);
        Long msgId = getAttribute("msgId");
        Assert.assertNull(msgId);
        String subject = getAttribute("subject");
        Assert.assertNull(subject);
    }

    @Test
    public void testAdd_login_notExisting() throws Exception {
        setUserSfigato()
                .setParameter("msgId", "3")
                .executeAction("add", Action.Method.GET);

        Long highlight = getAttribute("highlight");
        Assert.assertNull(highlight);
        Long msgId = getAttribute("msgId");
        Assert.assertEquals(new Long(3), msgId);
        String subject = getAttribute("subject");
        Assert.assertEquals("benvenuto nel fdt !", subject);
    }

    @Test
    public void testAdd_login_existing() throws Exception {
        setUserSfigato()
                .setParameter("msgId", "4")
                .executeAction("add", Action.Method.GET);

        Long highlight = getAttribute("highlight");
        Assert.assertEquals(new Long(4), highlight);
        Long msgId = getAttribute("msgId");
        Assert.assertNull(msgId);
        String subject = getAttribute("subject");
        Assert.assertNull(subject);
    }

    @Test
    public void testConfirmAdd_noLogin() throws Exception {
        executeAction("confirmAdd", Action.Method.POST);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        Assert.assertNull(bookmarks);
    }

    @Test
    public void testConfirmAdd_login_longSubject() throws Exception {
        setUserSfigato()
                .setParameter("msgId", "42")
                .setParameter("subject", StringUtils.repeat("*", Messages.MAX_SUBJECT_LENGTH + 1))
                .executeAction("confirmAdd", Action.Method.POST);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        checkListSize(bookmarks, 3);
        for (BookmarkDTO bookmark : bookmarks) {
            if (bookmark.getMsgId() == 42L) {
                Assert.assertEquals(StringUtils.repeat("*", Messages.MAX_SUBJECT_LENGTH), bookmark.getSubject());
            }
        }

    }

    @Test
    public void testConfirmAdd_login() throws Exception {
        setUserSfigato()
                .setParameter("msgId", "42")
                .setParameter("subject", "subject")
                .executeAction("confirmAdd", Action.Method.POST);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        checkListSize(bookmarks, 3);
        for (BookmarkDTO bookmark : bookmarks) {
            if (bookmark.getMsgId() == 42L) {
                Assert.assertEquals("subject", bookmark.getSubject());
            }
        }

    }

    @Test
    public void testDelete_noLogin() throws Exception {
        executeAction("confirmAdd", Action.Method.POST);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        Assert.assertNull(bookmarks);
    }

    @Test
    public void testDelete_login_notExisting() throws Exception {
        setUserSfigato()
                .setParameter("msgId", "42")
                .setParameter("subject", "subject")
                .executeAction("delete", Action.Method.POST);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        checkListSize(bookmarks, 2);
    }

    @Test
    public void testDelete_login_notOwner() throws Exception {
        setUserSfigato()
                .setParameter("msgId", "1")
                .setParameter("subject", "subject")
                .executeAction("delete", Action.Method.POST);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        checkListSize(bookmarks, 2);
    }

    @Test
    public void testDelete_login_owner() throws Exception {
        setUserSfigato()
                .setParameter("msgId", "4")
                .setParameter("subject", "subject")
                .executeAction("delete", Action.Method.POST);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        checkListSize(bookmarks, 1);
    }

    @Test
    public void testEdit_noLogin() throws Exception {
        executeAction("edit", Action.Method.POST);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        Assert.assertNull(bookmarks);
    }

    @Test
    public void testEdit_login_notExisting() throws Exception {
        setUserSfigato()
                .setParameter("msgId", "42")
                .setParameter("subject", "subject")
                .executeAction("edit", Action.Method.POST);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        checkListSize(bookmarks, 2);
    }

    @Test
    public void testEdit_login_notOwner() throws Exception {
        setUserSfigato()
                .setParameter("msgId", "1")
                .setParameter("subject", "subject")
                .executeAction("edit", Action.Method.POST);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        checkListSize(bookmarks, 2);
        for (BookmarkDTO bookmark : bookmarks) {
            if (bookmark.getMsgId() == 1L) {
                Assert.assertEquals("xxxxxxxxxxxxx", bookmark.getSubject());
            }
        }
    }

    @Test
    public void testEdit_login_owner_longSubject() throws Exception {
        setUserSfigato()
                .setParameter("msgId", "4")
                .setParameter("subject", StringUtils.repeat("*", Messages.MAX_SUBJECT_LENGTH + 1))
                .executeAction("edit", Action.Method.POST);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        checkListSize(bookmarks, 2);
        for (BookmarkDTO bookmark : bookmarks) {
            if (bookmark.getMsgId() == 4L) {
                Assert.assertEquals(StringUtils.repeat("*", Messages.MAX_SUBJECT_LENGTH), bookmark.getSubject());
            }
        }
    }

    @Test
    public void testEdit_login_owner() throws Exception {
        setUserSfigato()
                .setParameter("msgId", "4")
                .setParameter("subject", "subject")
                .executeAction("edit", Action.Method.POST);

        List<BookmarkDTO> bookmarks = getAttribute("bookmarks");
        checkListSize(bookmarks, 2);
        for (BookmarkDTO bookmark : bookmarks) {
            if (bookmark.getMsgId() == 4L) {
                Assert.assertEquals("subject", bookmark.getSubject());
            }
        }
    }

}
