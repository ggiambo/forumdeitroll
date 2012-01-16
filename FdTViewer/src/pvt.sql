
CREATE TABLE IF NOT EXISTS `pvt_content` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`sender` text,
	`content` mediumtext NOT NULL,
	`senddate` datetime NOT NULL,
	`subject` text NOT NULL,
	`replyTo` int(11),
	`deleted` int(1) DEFAULT 0
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 ;

CREATE TABLE IF NOT EXISTS `pvt_recipient` (
	`pvt_id` int(11) NOT NULL,
	`recipient` text NOT NULL,
	`read` int(1) DEFAULT 0,
	`deleted` int(1) DEFAULT 0
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 ;