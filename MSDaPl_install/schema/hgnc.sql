DROP DATABASE IF EXISTS hgnc;
CREATE DATABASE hgnc;
USE hgnc;

DROP TABLE IF EXISTS hgnc_names;
CREATE TABLE hgnc_names (
  id int(10) unsigned NOT NULL,
  symbol varchar(255) DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  previousSymbols varchar(255) DEFAULT NULL,
  previousNames varchar(500) DEFAULT NULL,
  aliases varchar(255) DEFAULT NULL,
  entrezGeneID varchar(255) DEFAULT NULL,
  omimID varchar(255) DEFAULT NULL,
  refseq varchar(255) DEFAULT NULL,
  swissprotID varchar(255) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY symbol (symbol),
  KEY name (name),
  KEY entrezGeneID (entrezGeneID),
  KEY omimID (omimID),
  KEY refseq (refseq),
  KEY swissprotID (swissprotID)
);
