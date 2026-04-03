CREATE DATABASE nba;

DROP DATABASE nba;

DROP TABLE IF EXISTS `player`;

CREATE TABLE `player`  (
  `player_id` int(11) NOT NULL AUTO_INCREMENT,
  `team_id` int(11) NOT NULL,
  `player_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL, -- 排序规则是utf8_general_ci，代表对大小写不敏感
  `height` float(3, 2) NULL DEFAULT 0.00,
  PRIMARY KEY (`player_id`) USING BTREE,
  UNIQUE INDEX `player_name`(`player_name`) USING BTREE -- 择BTREE或者HASH
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

ALTER TABLE player ADD (age int(11));
ALTER TABLE player RENAME COLUMN age to player_age;
ALTER TABLE player MODIFY player_age float(3,1);
ALTER TABLE player DROP COLUMN player_age;