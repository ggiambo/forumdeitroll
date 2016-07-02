ALTER TABLE `authors`
ADD `creationDate` DATE NULL,
ADD `enabled` BOOLEAN NOT NULL DEFAULT TRUE;

UPDATE authors
SET authors.creationDate =
(
	SELECT date
	FROM messages
	WHERE messages.author = authors.nick
	ORDER BY date ASC
	LIMIT 1
);

UPDATE authors
SET enabled = creationDate IS NOT NULL;