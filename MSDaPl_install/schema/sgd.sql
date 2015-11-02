DROP DATABASE IF EXISTS sgd;
CREATE DATABASE IF NOT EXISTS sgd;
USE sgd;

DROP TABLE IF EXISTS tblFeature;
CREATE TABLE tblFeature (
  sgdID char(10) NOT NULL,
  name varchar(20) DEFAULT NULL,
  type varchar(40) DEFAULT NULL,
  qualifier varchar(20) DEFAULT NULL,
  description mediumtext,
  PRIMARY KEY (sgdID),
  UNIQUE KEY name (name),
  KEY type (type)
);


DROP TABLE IF EXISTS tblGOAnnotation;
CREATE TABLE tblGOAnnotation (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  sgdID char(10) NOT NULL,
  featureName varchar(20) DEFAULT NULL,
  GO_Aspect enum('P','F','C') NOT NULL,
  GO_TERM varchar(255) DEFAULT NULL,
  isNot smallint(6) DEFAULT NULL,
  GOID int(10) unsigned NOT NULL,
  Evidence_Code char(3) DEFAULT NULL,
  Evidence_Reference varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY sgdID (sgdID),
  KEY isNot (isNot),
  KEY featureName (featureName),
  KEY GOID (GOID)
);


DROP TABLE IF EXISTS tblGeneName;
CREATE TABLE tblGeneName (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  sgdID char(10) NOT NULL,
  featureName varchar(20) DEFAULT NULL,
  geneName varchar(10) NOT NULL,
  nameType enum('primary','alias') DEFAULT NULL,
  PRIMARY KEY (id),
  KEY sgdID (sgdID),
  KEY featureName (featureName),
  KEY geneName (geneName),
  KEY nameType (nameType),
  KEY geneName_2 (geneName,nameType)
);

