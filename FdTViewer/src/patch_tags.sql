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
