
DROP ALL OBJECTS;

CREATE SCHEMA fdtsucker;

CREATE CACHED TABLE fdtsucker.ads (
	id INT DEFAULT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	title TINYTEXT NOT NULL,
	visurl TINYTEXT NOT NULL,
	content TINYTEXT NOT NULL
);

CREATE CACHED TABLE fdtsucker.authors (
	nick TINYTEXT NOT NULL,
	messages INT NOT NULL,
	avatar MEDIUMBLOB,
	password TINYTEXT NOT NULL,
	salt TINYTEXT,
	hash TINYTEXT,
	signature_image MEDIUMBLOB
);

CREATE CACHED TABLE fdtsucker.bookmarks (
	nick TINYTEXT NOT NULL,
	msgid INT NOT NULL,
	subject TINYTEXT NOT NULL
);

CREATE CACHED TABLE fdtsucker.digest (
	threadid INT DEFAULT NULL,
	author VARCHAR(256),
	subject TINYTEXT,
	opener_text MEDIUMTEXT,
	excerpt MEDIUMTEXT,
	nrofmessages INT DEFAULT NULL,
	startdate DATETIME DEFAULT NULL,
	lastdate DATETIME DEFAULT NULL
);

CREATE CACHED TABLE fdtsucker.digest_participant (
	threadid INT DEFAULT NULL,
	author VARCHAR(256)
);

CREATE CACHED TABLE fdtsucker.likes (
	nick TINYTEXT NOT NULL,
	msgid INT NOT NULL,
	vote TINYINT DEFAULT NULL
);

CREATE CACHED TABLE fdtsucker.messages(
	id INT DEFAULT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	text MEDIUMTEXT NOT NULL,
	date DATETIME NOT NULL,
	subject TINYTEXT NOT NULL,
	threadid INT NOT NULL,
	parentid INT NOT NULL,
	author VARCHAR(256) DEFAULT NULL,
	forum VARCHAR(256) DEFAULT NULL,
	visible BOOLEAN DEFAULT '1' NOT NULL,
	rank INT NOT NULL DEFAULT '0'
);

CREATE CACHED TABLE fdtsucker.notification(
	id INT DEFAULT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	fromnick VARCHAR(256) NOT NULL,
	tonick VARCHAR(256) NOT NULL,
	msgid INT NOT NULL
);

CREATE CACHED TABLE fdtsucker.poll(
	id INT DEFAULT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	title VARCHAR(256) NOT NULL,
	text MEDIUMTEXT NOT NULL,
	author VARCHAR(256) NOT NULL,
	creationdate DATETIME NOT NULL,
	updatedate DATETIME NOT NULL
);

CREATE CACHED TABLE fdtsucker.poll_question(
	pollid INT NOT NULL,
	sequence INT NOT NULL,
	text VARCHAR(256) NOT NULL,
	votes INT NOT NULL
);

CREATE CACHED TABLE fdtsucker.poll_user(
	nick VARCHAR(256) NOT NULL,
	pollid INT NOT NULL
);

CREATE CACHED TABLE fdtsucker.preferences(
	nick TINYTEXT NOT NULL,
	key TINYTEXT NOT NULL,
	value TINYTEXT NOT NULL
);

CREATE CACHED TABLE fdtsucker.pvt_content(
	id INT DEFAULT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	sender TEXT NOT NULL,
	content MEDIUMTEXT NOT NULL,
	senddate DATETIME NOT NULL,
	subject TEXT NOT NULL,
	replyto INT,
	deleted INT DEFAULT 0
);

CREATE CACHED TABLE fdtsucker.pvt_recipient(
	pvt_id INT NOT NULL,
	recipient TEXT NOT NULL,
	read INT DEFAULT 0,
	deleted INT DEFAULT 0
);

CREATE CACHED TABLE fdtsucker.quotes(
	id INT DEFAULT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	nick TEXT NOT NULL,
	content TEXT NOT NULL
);

CREATE CACHED TABLE fdtsucker.sysinfo(
	key TINYTEXT NOT NULL,
	value TINYTEXT NOT NULL
);

CREATE CACHED TABLE fdtsucker.tagnames (
	t_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	value TINYTEXT NOT NULL,
);

CREATE CACHED TABLE fdtsucker.tags_bind (
	t_id INT NOT NULL,
	m_id INT NOT NULL,
	author VARCHAR(256) NOT NULL,
	primary KEY(T_ID, M_ID)
);

CREATE CACHED TABLE fdtsucker.threads (
	threadid INT NOT NULL,
	lastid INT NOT NULL,
);

CREATE INDEX id_idx ON fdtsucker.messages(id);

CREATE INDEX parentid_idx ON fdtsucker.messages(parentid);

CREATE INDEX threadid_idx ON fdtsucker.messages(threadid);

CREATE INDEX author_idx ON fdtsucker.messages(author);

CREATE INDEX forum_idx ON fdtsucker.messages(forum);

-- native fulltext search
CREATE ALIAS IF NOT EXISTS FT_INIT FOR "org.h2.fulltext.FullText.init";

CALL FT_INIT();

CALL FT_CREATE_INDEX('FDTSUCKER', 'MESSAGES', 'TEXT,SUBJECT');

