-- MySQL dump 10.13  Distrib 5.1.63, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: fdtsucker
-- ------------------------------------------------------
-- Server version	5.1.63-0+squeeze1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `authors`
--

DROP TABLE IF EXISTS `authors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authors` (
  `nick` tinytext NOT NULL,
  `messages` int(11) NOT NULL,
  `avatar` mediumblob,
  `password` tinytext NOT NULL,
  `salt` tinytext,
  `hash` tinytext,
  `signature_image` mediumblob
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `bookmarks`
--

DROP TABLE IF EXISTS `bookmarks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bookmarks` (
  `nick` tinytext NOT NULL,
  `msgId` int(11) NOT NULL,
  `subject` tinytext NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `text` longtext NOT NULL,
  `date` datetime NOT NULL,
  `subject` tinytext NOT NULL,
  `threadId` int(11) NOT NULL,
  `parentId` int(11) NOT NULL,
  `author` varchar(256) DEFAULT NULL,
  `forum` varchar(256) DEFAULT NULL,
  `visible` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `parentId` (`parentId`),
  KEY `threadId` (`threadId`),
  KEY `author` (`author`),
  KEY `forum` (`forum`),
  FULLTEXT KEY `text` (`text`),
  FULLTEXT KEY `search` (`subject`,`text`),
  `rank` int(11) NOT NULL DEFAULT '0'
) ENGINE=MyISAM AUTO_INCREMENT=2722091 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `fromNick` varchar(256) NOT NULL,
  `toNick` varchar(256) NOT NULL,
  `msgId` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `poll`
--

DROP TABLE IF EXISTS `poll`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `poll` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(256) NOT NULL,
  `text` mediumtext NOT NULL,
  `author` varchar(256) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `poll_question`
--

DROP TABLE IF EXISTS `poll_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `poll_question` (
  `pollId` int(11) NOT NULL,
  `sequence` int(11) NOT NULL,
  `text` varchar(256) NOT NULL,
  `votes` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `poll_user`
--

DROP TABLE IF EXISTS `poll_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `poll_user` (
  `nick` varchar(256) NOT NULL,
  `pollId` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `preferences`
--

DROP TABLE IF EXISTS `preferences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `preferences` (
  `nick` tinytext NOT NULL,
  `key` tinytext NOT NULL,
  `value` tinytext NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pvt_content`
--

DROP TABLE IF EXISTS `pvt_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pvt_content` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sender` text NOT NULL,
  `content` mediumtext NOT NULL,
  `senddate` datetime NOT NULL,
  `subject` text NOT NULL,
  `replyTo` int(11) DEFAULT NULL,
  `deleted` int(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1204 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pvt_recipient`
--

DROP TABLE IF EXISTS `pvt_recipient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pvt_recipient` (
  `pvt_id` int(11) NOT NULL,
  `recipient` text NOT NULL,
  `read` int(1) DEFAULT '0',
  `deleted` int(1) DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quotes`
--

DROP TABLE IF EXISTS `quotes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `quotes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nick` text NOT NULL,
  `content` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=398 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sysinfo`
--

DROP TABLE IF EXISTS `sysinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sysinfo` (
  `key` tinytext NOT NULL,
  `value` tinytext NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tags` (
  `tagName` varchar(12) NOT NULL,
  `used` int(11) NOT NULL,
  UNIQUE KEY `tagName` (`tagName`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-11-24 22:46:59

CREATE TABLE IF NOT EXISTS `tagnames` (
	`t_id` int(11) NOT NULL AUTO_INCREMENT,
	`value` tinytext NOT NULL,
	PRIMARY KEY (`t_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `tags_bind` (
	`t_id` int(11) NOT NULL,
	`m_id` int(11) NOT NULL,
	`author` tinytext NOT NULL,
	PRIMARY KEY (`t_id`, `m_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `likes` (
  `nick` tinytext NOT NULL,
  `msgId` int(11) NOT NULL,
  `vote` boolean NULL NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;


create table ads (
	id int(6) NOT NULL AUTO_INCREMENT PRIMARY KEY,
	title tinytext NOT NULL,
	visurl tinytext NOT NULL,
	content tinytext NOT NULL
) ENGINE=MyISAM  DEFAULT CHARSET=utf8;

insert into ads (id, title, visurl, content) values (1, 'Saune Gay', 'www.luino.it/stazione/', 'Trova la sauna gay ideale vicino a casa tua !');
insert into ads (id, title, visurl, content) values (2, 'Dildi Giganti', 'www.megadildo.it', 'Regala un sorriso al tuo bucio del cuxo !');
insert into ads (id, title, visurl, content) values (3, 'Suore per tutti', 'www.suoregratis.it', 'La tua Suora personalizzata, a prezzi imbattibili !');
insert into ads (id, title, visurl, content) values (4, 'M5S Blah Banf', 'www.m5sprot.it', 'Generatore automatico di fango da gettare sul M5S');
insert into ads (id, title, visurl, content) values (5, 'Foto pazzesche !', 'www.nuncipossocredere.it', 'Foto hot scattate dalla Sonda Yutsu');
insert into ads (id, title, visurl, content) values (6, 'MILF Svizzere di qualit&agrave;', 'www.milffornothing.it', 'MILF di qualit&agrave; svizzera, prezzo tailandese !');
insert into ads (id, title, visurl, content) values (7, 'Amuleti anti Wakko', 'www.amuletiwakkosi.it', 'Tieni lontani i wakki da questo forum, garantiti al 100%');
insert into ads (id, title, visurl, content) values (8, 'Cydonia', 'www.sitovuoto.it', 'Esplora l''incantanto mondo di Cydonia a bordo dell''Argent !');
insert into ads (id, title, visurl, content) values (9, 'Lozioni per capelli Yoda', 'www.lozionimiracolose.it', 'Per una pelata lucida e splendente !');
