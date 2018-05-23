package com.forumdeitroll.lucene;

import com.forumdeitroll.FdTConfig;
import com.forumdeitroll.persistence.MessageDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Date;
import java.util.Properties;

public class Index {

	private final IndexWriter writer;

	Index(IndexWriter writer) {
		this.writer = writer;
	}

	public void doFullIndex() throws Exception {

		Connection conn = getDatabaseConnection();

		String sql = "select id, date, author, subject, text from messages";
		ResultSet rs = conn.prepareStatement(sql).executeQuery();
		while (rs.next()) {
			doIndex(
					rs.getLong("id"),
					rs.getDate("date"),
					rs.getString("author"),
					rs.getString("subject"),
					rs.getString("text")
			);
		}
		writer.commit();
	}

	public void doIndex(MessageDTO messageDTO) throws Exception {
		doIndex(
				messageDTO.getId(),
				messageDTO.getDate(),
				messageDTO.getAuthor().getNick(),
				messageDTO.getSubject(),
				messageDTO.getText()
		);
	}

	private Connection getDatabaseConnection() throws Exception {
		String persistenceName = FdTConfig.getProperty("persistence.name");
		Properties databaseConfig = FdTConfig.getDatabaseConfig(persistenceName);

		String driver = databaseConfig.getProperty("driverclass");
		Class.forName(driver);

		String username = databaseConfig.getProperty("username");
		String password = databaseConfig.getProperty("password");
		String url = databaseConfig.getProperty("url");

		return DriverManager.getConnection(url, username, password);
	}


	private void doIndex(Long id, Date date, String author, String subject, String text) throws IOException {
		Document doc = new Document();
		doc.add(new TextField("id", Long.toString(id), Field.Store.YES));

		String dateString =  Lucene.dateToString(date);
		doc.add(new TextField("date", dateString, Field.Store.YES));
		doc.add(new SortedDocValuesField("date", new BytesRef(dateString)));

		doc.add(new TextField("author", StringUtils.defaultString(author), Field.Store.YES));
		doc.add(new TextField("subject", subject, Field.Store.YES));
		doc.add(new TextField("text", text, Field.Store.YES));
		writer.addDocument(doc);
	}

}
