CREATE TABLE `users` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`username` VARCHAR(100) NOT NULL COLLATE 'utf8_bin',
	`password` VARCHAR(34) NOT NULL COLLATE 'utf8_bin',
	`status` TINYINT(2) NOT NULL,
	`last_login` DATETIME NOT NULL,
	`created` DATETIME NOT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_bin'
ENGINE=InnoDB
;

CREATE TABLE `status` (
	`id` INT(11) NOT NULL DEFAULT '0',
	`name` VARCHAR(10) NOT NULL,
	PRIMARY KEY (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

INSERT INTO `status` (`id`, `name`) VALUES (0, 'offline');
INSERT INTO `status` (`id`, `name`) VALUES (1, 'online');
INSERT INTO `status` (`id`, `name`) VALUES (2, 'in-game');


