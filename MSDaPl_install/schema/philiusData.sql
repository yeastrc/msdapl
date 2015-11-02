DROP DATABASE IF EXISTS philiusData;
CREATE DATABASE philiusData;
USE philiusData;

DROP TABLE IF EXISTS nrseqDatabase;
CREATE TABLE nrseqDatabase (
  databaseID int(10) unsigned NOT NULL,
  PRIMARY KEY (databaseID)
);

DROP TABLE IF EXISTS philiusResult;
CREATE TABLE philiusResult (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  sequenceID int(10) unsigned NOT NULL,
  annotation varchar(50) NOT NULL,
  spProbabilitySum decimal(10,5) NOT NULL,
  tmProbabilitySum decimal(10,5) NOT NULL,
  typeScore decimal(10,5) NOT NULL,
  topologyConfidenceScore decimal(10,5) DEFAULT NULL,
  transMembrane smallint(5) unsigned NOT NULL,
  signalPeptide smallint(5) unsigned NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY sequenceID (sequenceID)
);
DELIMITER |
CREATE TRIGGER philiusResult_bdelete BEFORE DELETE ON philiusResult
 FOR EACH ROW
 BEGIN
   DELETE FROM philiusSegment WHERE resultID = OLD.id;
 END; 
|
DELIMITER ;

DROP TABLE IF EXISTS philiusSegment;
CREATE TABLE philiusSegment (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  resultID int(10) unsigned NOT NULL,
  segmentType varchar(3) DEFAULT NULL,
  confidence decimal(10,5) NOT NULL,
  start int(10) unsigned NOT NULL,
  end int(10) unsigned NOT NULL,
  PRIMARY KEY (id),
  KEY resultID (resultID)
);
DELIMITER |
CREATE TRIGGER philiusSegment_bdelete BEFORE DELETE ON philiusSegment
 FOR EACH ROW
 BEGIN
   DELETE FROM philiusSignalPeptideSegment WHERE segmentID = OLD.id;
 END;
|
DELIMITER ;

DROP TABLE IF EXISTS philiusSignalPeptideSegment;
CREATE TABLE philiusSignalPeptideSegment (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  segmentID int(10) unsigned NOT NULL,
  segmentType varchar(20) DEFAULT NULL,
  start int(10) unsigned NOT NULL,
  end int(10) unsigned NOT NULL,
  PRIMARY KEY (id),
  KEY segmentID (segmentID)
);

