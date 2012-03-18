-- phpMyAdmin SQL Dump
-- version 3.4.5deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Feb 11, 2012 at 08:59 PM
-- Server version: 5.1.58
-- PHP Version: 5.3.6-13ubuntu3.5

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `fdtsucker`
--

-- --------------------------------------------------------

--
-- Table structure for table `authors`
--

CREATE TABLE IF NOT EXISTS `authors` (
  `nick` tinytext NOT NULL,
  `ranking` int(11) NOT NULL,
  `messages` int(11) NOT NULL,
  `avatar` mediumblob,
  `password` tinytext NOT NULL,
  `salt` tinytext,
  `hash` tinytext
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE IF NOT EXISTS `messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `text` mediumtext NOT NULL,
  `date` datetime NOT NULL,
  `subject` tinytext NOT NULL,
  `threadId` int(11) NOT NULL,
  `parentId` int(11) NOT NULL,
  `author` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `forum` varchar(256) CHARCTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parentId` (`parentId`),
  KEY `threadId` (`threadId`),
  KEY `author` (`author`),
  KEY `forum` (`forum`)
  FULLTEXT KEY `text` (`text`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

-- --------------------------------------------------------

--
-- Table structure for table `quotes`
--

CREATE TABLE IF NOT EXISTS `quotes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nick` text NOT NULL,
  `content` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

-- --------------------------------------------------------

--
-- pvt.sql -- tabelle per i messaggi privati
--

CREATE TABLE IF NOT EXISTS `pvt_content` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sender` text NOT NULL,
  `content` mediumtext NOT NULL,
  `senddate` datetime NOT NULL,
  `subject` text NOT NULL,
	`replyTo` int(11),
	`deleted` int(1) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `pvt_recipient` (
  `pvt_id` int(11) NOT NULL,
  `recipient` text NOT NULL,
	`read` int(1) DEFAULT 0,
	`deleted` int(1) DEFAULT 0
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- preferences.sql -- tabella di salvataggio delle preferenze
--

CREATE TABLE IF NOT EXISTS `preferences` (
  `nick` tinytext NOT NULL,
  `key` tinytext NOT NULL,
  `value` tinytext NOT NULL
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 ;

-- --------------------------------------------------------

--
-- sysinfo.sql -- tabella per le informazioni di sistema
--

CREATE TABLE IF NOT EXISTS `sysinfo` (
  `key` tinytext NOT NULL,
  `value` tinytext NOT NULL
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 ;

-- --------------------------------------------------------
--
-- messageIndices.sql -- indici
--

ALTER TABLE `messages` ADD INDEX ( `author` )
ALTER TABLE `messages` ADD INDEX ( `forum` )

--
-- fulltext index su subject
--

CREATE FULLTEXT INDEX search ON messages(subject, text);
