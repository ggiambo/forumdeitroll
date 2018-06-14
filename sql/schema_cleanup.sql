-- we don't know how to generate schema fdtsucker (class Schema) :(
create table authors
(
  nick tinytext not null,
  messages int not null,
  avatar mediumblob null,
  password tinytext not null,
  salt tinytext null,
  hash tinytext null,
  signature_image mediumblob null,
  creationDate date null,
  enabled tinyint(1) default '1' not null
)
  engine=MyISAM charset=utf8
;

create table bookmarks
(
  nick tinytext not null,
  msgId int not null,
  subject tinytext not null
)
  engine=MyISAM charset=utf8
;

create table likes
(
  nick tinytext not null,
  msgId int not null,
  vote tinyint(1) null
)
  engine=MyISAM charset=utf8
;

create table logins
(
  nick varchar(255) not null,
  loginKey varchar(255) not null,
  tstamp datetime not null
)
  engine=MyISAM charset=utf8
;

create table messages
(
  id int auto_increment
  primary key,
  text longtext not null,
  date datetime not null,
  subject tinytext not null,
  threadId int not null,
  parentId int not null,
  author varchar(256) null,
  forum varchar(256) null,
  visible tinyint(1) default '1' not null,
  rank int default '0' not null,
  fakeAuthor varchar(256) null
)
  engine=MyISAM charset=utf8
;

create index author
  on messages (author)
;

create index date
  on messages (date)
;

create index forum
  on messages (forum)
;

create index parentId
  on messages (parentId)
;

create fulltext index search
on messages (subject, text)
;

create fulltext index text
on messages (text)
;

create index threadId
  on messages (threadId)
;

create table notification
(
  id int auto_increment
  primary key,
  fromNick varchar(256) not null,
  toNick varchar(256) not null,
  msgId int not null
)
  charset=utf8
;

create table preferences
(
  nick tinytext not null,
  `key` tinytext not null,
  value tinytext not null
)
  engine=MyISAM charset=utf8
;

create table pvt_content
(
  id int auto_increment
  primary key,
  sender text not null,
  content mediumtext not null,
  senddate datetime not null,
  subject text not null,
  replyTo int null,
  deleted int(1) default '0' null
)
  engine=MyISAM charset=utf8
;

create table pvt_recipient
(
  pvt_id int not null,
  recipient text not null,
  `read` int(1) default '0' null,
  deleted int(1) default '0' null
)
  engine=MyISAM charset=utf8
;

create table quotes
(
  id int auto_increment
  primary key,
  nick text not null,
  content text not null
)
  engine=MyISAM charset=utf8
;

create table sysinfo
(
  `key` tinytext not null,
  value tinytext not null
)
  engine=MyISAM charset=utf8
;

create table threads
(
  threadId int null,
  lastId int null
)
  engine=MyISAM
;

create index idx_threads_lastId
  on threads (lastId)
;

create index idx_threads_threadId
  on threads (threadId)
;

