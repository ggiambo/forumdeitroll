package com.forumdeitroll.lucene;

import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.dao.MessagesDAO;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public class Lucene {

	private static final String INDEX_DIR = "/tmp/fdtLucene";
	private static final Logger LOG = Logger.getLogger(Lucene.class);

	private final Search search;
	private final Index index;

	Lucene(MessagesDAO messagesDAO) throws Exception {
		FSDirectory dir = FSDirectory.open(Paths.get(INDEX_DIR));
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		IndexWriter writer = new IndexWriter(dir, config);
		this.index = new Index(writer);

		if (!DirectoryReader.indexExists(dir)) {
			LOG.warn("No index found, starting full indexing ...");
			index.doFullIndex();
			LOG.warn("... full indexing finished.");
		}

		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);

		this.search = new Search(messagesDAO, searcher, reader);
	}

	List<MessageDTO> searchMessages(SearchParams searchParams) throws Exception {
		return search.doSearch(searchParams);
	}

	public void doIndex(MessageDTO messageDTO) throws Exception {
		index.doIndex(messageDTO);
	}

	static String dateToString(Date date) {
		return DateTools.timeToString(date.getTime(), DateTools.Resolution.MINUTE);
	}

}
