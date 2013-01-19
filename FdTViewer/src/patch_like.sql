ALTER TABLE `messages` ADD `rank` int(11) NOT NULL DEFAULT '0';

CREATE TABLE IF NOT EXISTS `likes` (
  `nick` tinytext NOT NULL,
  `msgId` int(11) NOT NULL,
  `vote` boolean NULL NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;