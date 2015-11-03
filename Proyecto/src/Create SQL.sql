CREATE DATABASE IF NOT EXISTS hstestdb;
GRANT ALL ON `hstestdb`.* to 'hsdb' identified by 'hsdbpass';
DROP TABLE IF EXISTS `hstestdb`.`HTML`;
CREATE TABLE `hstestdb`.`HTML` (
  `uuid` CHAR(36) NOT NULL,
  `content` TEXT NOT NULL,
  PRIMARY KEY (`uuid`));
INSERT INTO `hstestdb`.`HTML` (`uuid`,`content`) VALUES (1234,"<h2>Soy una pagina predefinida: 1234</h2>");
INSERT INTO `hstestdb`.`HTML` (`uuid`,`content`) VALUES (1235,"<h2>Soy una pagina predefinida: 1235</h2>");
INSERT INTO `hstestdb`.`HTML` (`uuid`,`content`) VALUES (3521,"<h2>Soy una pagina predefinida: 3521</h2>");
INSERT INTO `hstestdb`.`HTML` (`uuid`,`content`) VALUES (6666,"<h2>Soy una pagina predefinida: 6666</h2>");