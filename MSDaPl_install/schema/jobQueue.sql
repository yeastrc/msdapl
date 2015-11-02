DROP DATABASE IF EXISTS jobQueue;
CREATE DATABASE jobQueue;
USE jobQueue;

DROP TABLE IF EXISTS tblJobErrors;
CREATE TABLE tblJobErrors (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  jobID int(10) unsigned NOT NULL,
  description varchar(65000) DEFAULT NULL,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS tblJobs;
CREATE TABLE tblJobs (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  submitter int(10) unsigned NOT NULL,
  type int(10) unsigned NOT NULL,
  submitDate date NOT NULL,
  lastUpdate timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  status int(11) NOT NULL,
  attempts int(11) NOT NULL,
  log mediumtext,
  PRIMARY KEY (id)
);

DELIMITER |
CREATE TRIGGER tblJobs_bdelete BEFORE DELETE ON tblJobs FOR EACH ROW 
  BEGIN 
     DELETE FROM tblMSJobs WHERE jobID = OLD.id;
     DELETE FROM tblProteinInferJobs  WHERE jobID = OLD.id;
     DELETE FROM tblMSAnalysisUploadJobs  WHERE jobID = OLD.id;
     DELETE FROM tblPercolatorJobs  WHERE jobID = OLD.id;
     DELETE FROM tblPercolatorJobInput  WHERE jobID = OLD.id;
     DELETE FROM tblPercolatorToProteinInferParams  WHERE jobID = OLD.id;
   END;
|
DELIMITER ;


DROP TABLE IF EXISTS tblMSAnalysisUploadJobs;
CREATE TABLE tblMSAnalysisUploadJobs (
  jobID int(10) unsigned NOT NULL,
  projectID int(10) unsigned NOT NULL,
  experimentID int(10) unsigned NOT NULL,
  searchAnalysisID int(10) unsigned DEFAULT NULL,
  serverDirectory varchar(2000) NOT NULL,
  runProteinInference tinyint(1) NOT NULL,
  comments mediumtext,
  PRIMARY KEY (jobID)
);


DROP TABLE IF EXISTS tblMSJobs;
CREATE TABLE tblMSJobs (
  jobID int(10) unsigned NOT NULL,
  projectID int(10) unsigned NOT NULL,
  serverDirectory varchar(2000) NOT NULL,
  runDate date NOT NULL,
  baitProtein int(10) unsigned DEFAULT NULL,
  baitDescription varchar(255) DEFAULT NULL,
  targetSpecies int(10) unsigned DEFAULT NULL,
  comments mediumtext,
  groupID int(10) unsigned NOT NULL,
  runID int(10) unsigned DEFAULT NULL,
  experimentID int(10) unsigned DEFAULT NULL,
  pipeline varchar(10) NOT NULL,
  instrumentID int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (jobID)
);

DROP TABLE IF EXISTS tblPercolatorJobInput;
CREATE TABLE tblPercolatorJobInput (
  jobID int(10) unsigned NOT NULL,
  runSearchID int(10) unsigned NOT NULL,
  fileName varchar(255) NOT NULL,
  PRIMARY KEY (jobID,runSearchID)
);


DROP TABLE IF EXISTS tblPercolatorJobs;
CREATE TABLE tblPercolatorJobs (
  jobID int(10) unsigned NOT NULL,
  projectID int(10) unsigned NOT NULL,
  experimentID int(10) unsigned NOT NULL,
  searchID int(10) unsigned NOT NULL,
  resultDirectory varchar(500) NOT NULL,
  runProteinInference tinyint(1) NOT NULL,
  comments text,
  PRIMARY KEY (jobID)
);


DROP TABLE IF EXISTS tblPercolatorToProteinInferParams;
CREATE TABLE tblPercolatorToProteinInferParams (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  jobID int(10) unsigned NOT NULL,
  name varchar(255) NOT NULL,
  value varchar(255) NOT NULL,
  PRIMARY KEY (id),
  KEY jobID (jobID)
);


DROP TABLE IF EXISTS tblProteinInferJobs;
CREATE TABLE tblProteinInferJobs (
  jobID int(10) unsigned NOT NULL,
  piRunID int(10) unsigned NOT NULL,
  PRIMARY KEY (jobID,piRunID)
);
