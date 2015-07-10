package com.forumdeitroll.test.servlets;

import com.forumdeitroll.persistence.AuthorDTO;
import com.forumdeitroll.servlets.Action;
import com.forumdeitroll.servlets.Authors;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class AuthorsTest extends BaseServletsTest {

    @BeforeClass
    public static void initServlet() throws Exception {
        setServlet(new Authors());
    }

    @Test
    public void testGetAuthors() throws Exception {
        executeAction("getAuthors", Action.Method.GET)
                .checkWebsiteTitle()
                .checkNavigationMessage("Autori");

        List<AuthorDTO> authors = getAttribute("authors");
        checkListSize(authors, 2);
    }

}
