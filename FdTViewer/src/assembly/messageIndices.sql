ALTER TABLE `messages` CHANGE `author` `author` VARCHAR( 256 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL 
ALTER TABLE `messages` CHANGE `forum` `forum` VARCHAR( 256 ) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL 
ALTER TABLE `messages` ADD INDEX ( `author` ) 
ALTER TABLE `messages` ADD INDEX ( `forum` ) 