DROP TABLE IF EXISTS HTML;
DROP TABLE IF EXISTS XML;
DROP TABLE IF EXISTS XSD;
DROP TABLE IF EXISTS XSLT;


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
