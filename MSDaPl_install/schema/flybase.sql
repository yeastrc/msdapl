DROP DATABASE IF EXISTS flybase;
CREATE DATABASE flybase;
USE flybase;

DROP TABLE IF EXISTS geneAnnotation;
CREATE TABLE geneAnnotation (
  geneAcc varchar(255) NOT NULL DEFAULT '',
  geneSymbol varchar(255) DEFAULT NULL,
  geneName varchar(255) DEFAULT NULL,
  geneDescription mediumtext,
  PRIMARY KEY (geneAcc(11))
);

DROP TABLE IF EXISTS proteinAnnotation;
CREATE TABLE proteinAnnotation (
  proteinAcc varchar(255) NOT NULL DEFAULT '',
  proteinName varchar(255) DEFAULT NULL,
  PRIMARY KEY (proteinAcc(11))
);

DROP TABLE IF EXISTS proteinGeneMapping;
CREATE TABLE proteinGeneMapping (
  proteinAcc varchar(255) NOT NULL DEFAULT '',
  geneAcc varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (proteinAcc(11),geneAcc(11))
);

DROP TABLE IF EXISTS tblGOAnnotation;
CREATE TABLE tblGOAnnotation (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  geneAcc varchar(255) NOT NULL,
  geneSymbol varchar(255) NOT NULL,
  goAcc varchar(255) NOT NULL,
  isNot smallint(6) DEFAULT NULL,
  evidenceReference varchar(255) DEFAULT NULL,
  evidenceCode varchar(10) NOT NULL,
  goAspect enum('P','F','C') NOT NULL,
  PRIMARY KEY (id)
);
