
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