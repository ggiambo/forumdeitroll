package com.forumdeitroll.persistence.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.forumdeitroll.persistence.DigestArticleDTO;
import com.forumdeitroll.persistence.MessageDTO;
import com.forumdeitroll.persistence.SearchMessagesSort;

public class MySQLPersistence extends GenericSQLPersistence {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(MySQLPersistence.class);

	public void init(Properties databaseConfig) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		String host = databaseConfig.getProperty("host");
		String port = databaseConfig.getProperty("port");
		String username = databaseConfig.getProperty("username");
		String password = databaseConfig.getProperty("password");
		String dbname = databaseConfig.getProperty("dbname");
		String url = "jdbc:mysql://" + host + ":" + port + "/" + dbname + "?useUnicode=yes&characterEncoding=UTF-8";
		super.setupDataSource(url, username, password);
	}

	@Override
	protected List<MessageDTO> searchMessagesEx(String search, SearchMessagesSort sort, int limit, int page) {
		Connection conn = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<MessageDTO> result = new ArrayList<MessageDTO>();
		try {
			ps = conn.prepareStatement(
				"SELECT messages.*, gq.relevance AS relevance, gq.count AS count "
				+"FROM messages, "
					+"(SELECT threadId, MIN(id) AS mid, SUM(relevance) AS relevance, COUNT(id) AS count "
					+"FROM "
						+"(SELECT threadId, id, MATCH(subject, text) AGAINST(? IN NATURAL LANGUAGE MODE) AS relevance "
						+"FROM messages "
						+"WHERE MATCH(subject, text) AGAINST(? IN NATURAL LANGUAGE MODE) "
						+"HAVING relevance > 0.1 "
						+"ORDER BY relevance DESC "
						+"LIMIT 2048) AS mq "
					+"GROUP BY threadId) AS gq "
				+"WHERE gq.mid = messages.id "
				+"ORDER BY " + sort.orderBy() + " LIMIT ? OFFSET ?");
			/*
			La ricerca viene fatta in tre stadi.
			1. Viene creata la tabella temporanea "mq" ("match query") contenente i primi 2048 risultati (per rilevanza) della query sull'indice inverso con le colonne id, threadId e relevance (la rilevanza del messaggio matchato), i rislutati con rilevanza minore di 0.1 vengono eliminati subito -- il limite di 2048 e` una ottimizzazione
			2. Viene creata la tabella temporanea "gq" ("group query") riducendo la tabella mq sulla colonna threadId con GROUP BY. Questa tabella ha le colonne mid, primo messaggio matchante la query in ogni thread, threadId e relevance (ridefinito come somma della rilevanza di tutti i messaggi del thread) -> la motivazione e` mostrare un solo risultato per thread in modo da non avere la pagina dei risultati dominata da un singolo thread (magari perche' la frase che matcha la query e` quotata e riquotata mille volte nel thread) -- I passi 1 e 2 forse possono essere fusi ma non ho voglia di pensarci
			3. A questo punto facciamo un JOIN di gq con messages per ottenere i dettagli del messaggio mid da mostrare, a questo join viene applicato l'ordinamento scelto e applicata la paginazione
			-- sarrusofono 2012-02-25
			*/

			int i = 1;
			ps.setString(i++, search);
			ps.setString(i++, search);
			ps.setInt(i++, limit);
			ps.setInt(i++, limit * page);
			return getMessages(ps.executeQuery(), true);
		} catch (SQLException e) {
			LOG.error("Cannot get messages", e);
		} finally {
			close(rs, ps, conn);
		}
		return result;
	}
	
	/**
Da /opt/fdt/digest (riportata per versionamento):

DROP TABLE digest;

CREATE TABLE digest
(
    `threadId` INT(11),
    `author` TINYTEXT,
    `subject` TINYTEXT,
    `opener_text` LONGTEXT,
    `excerpt` LONGTEXT,
    `nrOfMessages` INT(6),
    `startDate` datetime,
    `lastDate` datetime
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO digest
SELECT id, author, subject, text, NULL, nrOfMessages, startdate, lastdate
FROM messages, (
    SELECT threadId, COUNT(threadId) as nrOfMessages, MIN(date) as startdate, MAX(date) as lastdate
    FROM messages
    WHERE `date` >= DATE_SUB(SYSDATE(), INTERVAL 1 MONTH)
    GROUP BY threadId
    ORDER BY COUNT(threadId) DESC
    LIMIT 0,50
) AS a
WHERE id = a.threadId;

UPDATE digest
SET excerpt = (
    SELECT `text`
    FROM messages
    WHERE messages.threadId = digest.threadId
    GROUP BY parentId
    ORDER BY parentId DESC
    LIMIT 0, 1
);

DROP TABLE digest_participant;

CREATE TABLE digest_participant
(
    `threadId` INT(11),
    `author` TINYTEXT
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO digest_participant
SELECT DISTINCT messages.`threadId`, messages.`author`
FROM messages, digest
WHERE messages.threadId = digest.threadId;


	 */
	
	@Override
	public List<DigestArticleDTO> getReadersDigest() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<DigestArticleDTO> results = new ArrayList<DigestArticleDTO>();
		DigestArticleDTO current = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement("SELECT digest.*, digest_participant.* FROM digest, digest_participant WHERE digest.threadId = digest_participant.threadId");
			rs = ps.executeQuery();
			while (rs.next()) {
				if (current == null || current.getThreadId() != rs.getLong("digest.threadId")) {
					current = new DigestArticleDTO();
					current.setThreadId(rs.getLong("digest.threadId"));
					current.setAuthor(rs.getString("digest.author"));
					current.setSubject(rs.getString("digest.subject"));
					current.setOpenerText(rs.getString("digest.opener_text"));
					current.setExcerpt(rs.getString("digest.excerpt"));
					current.setStartDate(rs.getDate("digest.startdate"));
					current.setLastDate(rs.getDate("digest.lastdate"));
					current.setNrOfMessages(rs.getInt("digest.nrOfMessages"));
					if (rs.getString("digest_participant.author") != null && !current.getParticipants().contains(rs.getString("digest_participant.author"))) {
						current.getParticipants().add(rs.getString("digest_participant.author"));	
					}
					results.add(current);
				} else {
					if (rs.getString("digest_participant.author") != null && !current.getParticipants().contains(rs.getString("digest_participant.author"))) {
						current.getParticipants().add(rs.getString("digest_participant.author"));	
					}
				}
			}
			
		} catch (SQLException e) {
			LOG.error("Impossibile leggere i dati dal db", e);
			return null;
		} finally {
			close(rs, ps, conn);
		}
		return results;
	}
}
