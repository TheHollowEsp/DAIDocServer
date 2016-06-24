CREATE DATABASE IF NOT EXISTS hstestdb; 
DROP TABLE IF EXISTS `hstestdb`.`HTML`;
DROP TABLE IF EXISTS `hstestdb`.`XML`;
DROP TABLE IF EXISTS `hstestdb`.`XSLT`;
DROP TABLE IF EXISTS `hstestdb`.`XSD`;

GRANT ALL ON `hstestdb`.* to 'dai' identified by 'daipassword';
GRANT ALL ON `hstestdb`.* to 'hsdb' identified by 'hsdbpass';

CREATE TABLE `hstestdb`.`HTML` (
  `uuid` CHAR(36) NOT NULL,
  `content` TEXT NOT NULL,
  PRIMARY KEY (`uuid`));

CREATE TABLE `hstestdb`.`XML` (
  `uuid` CHAR(36) NOT NULL,
  `content` TEXT NOT NULL,
  PRIMARY KEY (`uuid`));
  
  CREATE TABLE `hstestdb`.`XSD` (
  `uuid` CHAR(36) NOT NULL,
  `content` TEXT NOT NULL,
  PRIMARY KEY (`uuid`));

CREATE TABLE `hstestdb`.`XSLT` (
  `uuid` CHAR(36) NOT NULL,
  `content` TEXT NOT NULL,
  `xsd` CHAR(36) NOT NULL,
  PRIMARY KEY (`uuid`));

ALTER TABLE `hstestdb`.`XSLT` ADD FOREIGN KEY (`xsd`) references `hstestdb`.`XSD`(`uuid`) ON DELETE CASCADE;

INSERT INTO `hstestdb`.`HTML` (`uuid`,`content`) VALUES (1234,"<h2>Soy una pagina predefinida: 1234</h2>"); 
INSERT INTO `hstestdb`.`XML` (`uuid`,`content`) VALUES (1235,"Soy un XML"); 
INSERT INTO `hstestdb`.`XSD` (`uuid`,`content`) VALUES (3521,"Soy un XSD"); 
INSERT INTO `hstestdb`.`XSLT` (`uuid`,`content`,`xsd`) VALUES (6666,"Soy un XSLT",3521);

#DELETE FROM `hstestdb`.`xsd` WHERE `uuid`='3521';