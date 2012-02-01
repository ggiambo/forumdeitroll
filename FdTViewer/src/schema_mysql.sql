-- phpMyAdmin SQL Dump
-- version 3.3.10deb1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Oct 01, 2011 at 10:07 AM
-- Server version: 5.1.54
-- PHP Version: 5.3.5-1ubuntu7.2

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
  `subject` text NOT NULL,
  `threadId` int(11) NOT NULL,
  `parentId` int(11) NOT NULL,
  `author` text,
  `forum` text,
  PRIMARY KEY (`id`),
  KEY `parentId` (`parentId`),
  KEY `threadId` (`threadId`),
  FULLTEXT KEY `text` (`text`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

-- --------------------------------------------------------

--
-- Table structure for table `quote`
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
	PRIMARY KEY(`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

CREATE TABLE IF NOT EXISTS `pvt_recipient` (
	`pvt_id` int(11) NOT NULL,
	`recipient` text NOT NULL,
	`read` int(1) DEFAULT 0,
	`deleted` int(1) DEFAULT 0
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 ;

-- --------------------------------------------------------

--
-- preferences.sql -- tabella di salvataggio delle preferenze
--

CREATE TABLE IF NOT EXISTS `preferences` (
  `nick` tinytext NOT NULL,
  `key` tinytext NOT NULL,
  `value` tinytext NOT NULL
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 ;


