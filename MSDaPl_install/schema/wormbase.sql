DROP DATABASE IF EXISTS wormbase;
CREATE DATABASE wormbase;
USE wormbase;

DROP TABLE IF EXISTS tblGOAnnotation;
CREATE TABLE tblGOAnnotation (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  WBGeneID varchar(255) NOT NULL,
  goAcc varchar(255) NOT NULL,
  isNot smallint(6) DEFAULT NULL,
  evidenceCode varchar(10) DEFAULT NULL,
  evidenceReference varchar(2000) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY WBGeneID (WBGeneID),
  KEY goAcc (goAcc),
  KEY isNot (isNot),
  KEY evidenceCode (evidenceCode)
);

DROP TABLE IF EXISTS tblProteinGenes;
CREATE TABLE tblProteinGenes (
  systematicName varchar(20) NOT NULL DEFAULT '',
  geneName varchar(20) NOT NULL DEFAULT '',
  WBGeneID varchar(255) DEFAULT NULL,
  PRIMARY KEY (systematicName,geneName),
  KEY systematicName (systematicName),
  KEY geneName (geneName),
  KEY WBGeneID (WBGeneID)
);

DROP TABLE IF EXISTS tblProteins;
CREATE TABLE tblProteins (
  systematicName varchar(20) NOT NULL DEFAULT '',
  description mediumtext,
  locusName varchar(10) DEFAULT NULL,
  trembleID varchar(20) DEFAULT NULL,
  PRIMARY KEY (systematicName),
  KEY locusName (locusName),
  KEY trembleID (trembleID)
);

