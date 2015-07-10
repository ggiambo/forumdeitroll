package com.forumdeitroll.test;

import com.forumdeitroll.persistence.DAOFactory;
import com.forumdeitroll.persistence.DAOFactoryForTest;
import com.forumdeitroll.persistence.dao.*;
import com.forumdeitroll.test.persistence.ScriptRunner;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseTest {

    protected static AdminDAO adminDAO;
    protected static AuthorsDAO authorsDAO;
    protected static BookmarksDAO bookmarksDAO;
    protected static DigestDAO digestDAO;
    protected static MessagesDAO messagesDAO;
    protected static MiscDAO miscDAO;
    protected static PollsDAO pollsDAO;
    protected static PrivateMsgDAO pvtDAO;
    protected static QuotesDAO quotesDAO;
    protected static ThreadsDAO threadsDAO;

    @BeforeClass
    public static void init() throws Exception {
        DAOFactory daoFactory = DAOFactoryForTest.getInstance();

        authorsDAO = daoFactory.getAuthorsDAO();
        threadsDAO = daoFactory.getThreadsDAO();
        messagesDAO = daoFactory.getMessagesDAO();
        pollsDAO = daoFactory.getPollsDAO();
        quotesDAO = daoFactory.getQuotesDAO();
        bookmarksDAO = daoFactory.getBookmarksDAO();
        adminDAO = daoFactory.getAdminDAO();
        miscDAO = daoFactory.getMiscDAO();
        pvtDAO = daoFactory.getPrivateMsgDAO();
        digestDAO = daoFactory.getDigestDAO();
    }

    @Before
    public void setupDatabase() throws Exception {
        // reset database
        loadData("schema_h2.sql");
        // load testdata
        loadData("com/forumdeitroll/test/persistence/testDatabase.sql");
    }

    private void loadData(String fileName) throws Exception {
        InputStream sqlFile = null;
        Reader isr = null;
        Connection conn = null;

        try {
            sqlFile = this.getClass().getClassLoader().getResourceAsStream(fileName);
            isr = new InputStreamReader(sqlFile);
            conn = DAOFactoryForTest.getInstance().getDataSource().getConnection();
            ScriptRunner scriptRunner = new ScriptRunner(conn, true, true);
            scriptRunner.setLogWriter(null);
            scriptRunner.runScript(isr);
        } finally {
            if (isr != null) {
                try {
                    isr.close();
                } catch (Exception e) {
                    // ignore
                }
            }
            if (sqlFile != null) {
                try {
                    sqlFile.close();
                } catch (Exception e) {
                    // ignore
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    /**
     * DatabaseString format: yyyy-MM-dd HH:mm:ss
     *
     * @return
     * @throws ParseException
     */
    public Date getDateFromDatabaseString(String databaseString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(databaseString);
    }

}
