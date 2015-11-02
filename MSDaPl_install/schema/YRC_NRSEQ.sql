DROP DATABASE IF EXISTS YRC_NRSEQ;
CREATE DATABASE YRC_NRSEQ;
USE YRC_NRSEQ;


DROP TABLE IF EXISTS tblDatabase;
CREATE TABLE tblDatabase (
  id int(10) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name varchar(255) DEFAULT NULL,
  description varchar(255) DEFAULT NULL
);
ALTER TABLE tblDatabase ADD INDEX (name);

DROP TABLE IF EXISTS tblProtein;
CREATE TABLE tblProtein (
  id int(10) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  sequenceID int(10) unsigned NOT NULL DEFAULT '0',
  speciesID int(10) unsigned NOT NULL DEFAULT '0',
  UNIQUE KEY sequenceID (sequenceID,speciesID)
);
ALTER TABLE tblProtein ADD UNIQUE INDEX (sequenceID, speciesID);
ALTER TABLE tblProtein ADD INDEX (sequenceID);
ALTER TABLE tblProtein ADD INDEX (speciesID, id);


DROP TABLE IF EXISTS tblProteinDatabase;
CREATE TABLE tblProteinDatabase (
  id int(10) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  proteinID int(10) unsigned NOT NULL DEFAULT '0',
  databaseID int(10) unsigned NOT NULL DEFAULT '0',
  accessionString varchar(500) NOT NULL,
  description varchar(2500) DEFAULT NULL,
  URL varchar(255) DEFAULT NULL,
  timestamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  isCurrent enum('T','F') NOT NULL DEFAULT 'T'
);
ALTER TABLE tblProteinDatabase ADD UNIQUE KEY (databaseID, proteinID, accessionString);
ALTER TABLE tblProteinDatabase ADD INDEX (proteinID);
ALTER TABLE tblProteinDatabase ADD INDEX (databaseID);
ALTER TABLE tblProteinDatabase ADD INDEX (accessionString);


DROP TABLE IF EXISTS tblProteinSequence;
CREATE TABLE tblProteinSequence (
  id int(10) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
  sequence mediumtext
);
ALTER TABLE tblProteinSequence ADD INDEX (sequence(255));

