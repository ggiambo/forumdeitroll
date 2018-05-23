package com.forumdeitroll.lucene;

import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.dao.MessagesDAO;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Search {

	private static final Logger LOG = Logger.getLogger(Test.class);
	private static final Sort SORT_DATE = new Sort(new SortField("date", SortField.Type.STRING));

	private final MessagesDAO messagesDAO;
	private final IndexSearcher searcher;
	private final IndexReader reader;

	Search(MessagesDAO messagesDAO, IndexSearcher searcher, IndexReader reader) {
		this.searcher = searcher;
		this.messagesDAO = messagesDAO;
		this.reader = reader;
	}

	List<MessageDTO> doSearch(SearchParams searchParams) throws Exception {

		Query query = MessageQueryBuilder.build(searchParams);
		LOG.debug(query);
		TopDocs hits = searcher.search(query, 10, SORT_DATE);

		return Arrays.stream(hits.scoreDocs)
				.map(this::getDocument)
				.filter(Objects::nonNull)
				.map(this::loadMessage)
				.collect(Collectors.toList());
	}

	private Document getDocument(ScoreDoc scoreDoc) {
		try {
			return reader.document(scoreDoc.doc);
		} catch (IOException e) {
			return null;
		}
	}

	private MessageDTO loadMessage(Document document) {
		String messageId = document.get("id");
		Long id = Long.parseLong(messageId);
		return messagesDAO.getMessage(id);
	}

}
