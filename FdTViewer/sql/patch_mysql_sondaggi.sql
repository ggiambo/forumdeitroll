CREATE TABLE IF NOT EXISTS `poll` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(256) NOT NULL,
  `text` mediumtext NOT NULL,
  `author` varchar(256) NOT NULL,
  `creationDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `poll_question` (
  `pollId` int(11) NOT NULL,
  `sequence` int(11) NOT NULL,
  `text` varchar(256) NOT NULL,
  `votes` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

CREATE TABLE IF NOT EXISTS `poll_user` (
  `nick` varchar(256) NOT NULL,
  `pollId` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
