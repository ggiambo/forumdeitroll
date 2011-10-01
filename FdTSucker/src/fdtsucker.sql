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
  `avatar` mediumblob
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE IF NOT EXISTS `messages` (
  `id` int(11) NOT NULL,
  `text` longtext NOT NULL,
  `date` datetime NOT NULL,
  `subject` tinytext NOT NULL,
  `threadId` int(11) NOT NULL,
  `parentId` int(11) NOT NULL,
  `author` tinytext,
  `forum` tinytext,
  PRIMARY KEY (`id`),
  FULLTEXT KEY `text` (`text`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `messagetags`
--

CREATE TABLE IF NOT EXISTS `messagetags` (
  `messageId` int(11) NOT NULL,
  `tagName` varchar(12) NOT NULL,
  KEY `messageId` (`messageId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `tags`
--

CREATE TABLE IF NOT EXISTS `tags` (
  `tagName` varchar(12) NOT NULL,
  `used` int(11) NOT NULL,
  UNIQUE KEY `tagName` (`tagName`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
