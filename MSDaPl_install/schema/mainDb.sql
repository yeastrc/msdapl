DROP DATABASE IF EXISTS mainDb;
CREATE DATABASE mainDb;
USE mainDb;

DROP TABLE IF EXISTS NCBI_Taxonomy;
CREATE TABLE NCBI_Taxonomy (
  id int(10) unsigned NOT NULL DEFAULT '0',
  name varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (id),
  KEY name (name)
);


CREATE TABLE tblLabDirector (
  groupID int(10) unsigned NOT NULL,
  researcherID int(10) unsigned NOT NULL
);


DROP TABLE IF EXISTS grants;
CREATE TABLE grants (
  id int(11) NOT NULL AUTO_INCREMENT,
  PI mediumint(8) unsigned NOT NULL,
  sourceType enum('FEDERAL','FOUNDATION','INDUSTRY','PROFASSOC','LOCGOV','OTHER') DEFAULT NULL,
  sourceName varchar(255) DEFAULT NULL,
  grantNum varchar(255) DEFAULT NULL,
  grantAmount varchar(255) DEFAULT NULL,
  title varchar(255) NOT NULL,
  PRIMARY KEY (id)
);


DROP TABLE IF EXISTS projectGrant;
CREATE TABLE projectGrant (
  id int(11) NOT NULL AUTO_INCREMENT,
  projectID mediumint(8) unsigned NOT NULL,
  grantID mediumint(8) unsigned NOT NULL,
  PRIMARY KEY (id)
);


DROP TABLE IF EXISTS projectGroup;
CREATE TABLE projectGroup (
  projectID int(10) unsigned NOT NULL,
  groupID int(10) unsigned NOT NULL
);


DROP TABLE IF EXISTS projectResearcher;
CREATE TABLE projectResearcher (
  projectID int(10) unsigned NOT NULL,
  researcherID int(10) unsigned NOT NULL,
  PRIMARY KEY (projectID,researcherID),
  KEY researcherID (researcherID)
);


DROP TABLE IF EXISTS tblCollaboration;
CREATE TABLE tblCollaboration (
  projectID mediumint(8) unsigned NOT NULL DEFAULT '0',
  collGroups set('Yates','Aebersold','TwoHybrid','Microscopy','PSP','Informatics','MacCoss','Noble','Core') DEFAULT NULL,
  PRIMARY KEY (projectID)
);


DROP TABLE IF EXISTS tblExperiments;
CREATE TABLE tblExperiments (
  expID varchar(20) NOT NULL DEFAULT '',
  expDate date NOT NULL DEFAULT '0000-00-00',
  expType enum('msp','loc','2hy','psp') NOT NULL DEFAULT 'msp',
  expCollaborator smallint(6) NOT NULL DEFAULT '0',
  expDescription text,
  expPublic enum('Y','N') NOT NULL DEFAULT 'N',
  PRIMARY KEY (expID),
  UNIQUE KEY expID (expID)
);


DROP TABLE IF EXISTS tblProjectExperiment;
CREATE TABLE tblProjectExperiment (
  projectID int(10) unsigned NOT NULL,
  experimentID int(10) unsigned NOT NULL
);


DROP TABLE IF EXISTS tblProjectProteinInference;
CREATE TABLE tblProjectProteinInference (
  projectID int(10) unsigned NOT NULL,
  piRunID int(10) unsigned NOT NULL,
  researcherID int(10) unsigned NOT NULL,
  PRIMARY KEY (projectID,piRunID,researcherID)
);


DROP TABLE IF EXISTS tblProjects;
CREATE TABLE tblProjects (
  projectID mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  projectSubmitDate datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  projectTitle varchar(255) DEFAULT NULL,
  projectPI mediumint(8) unsigned DEFAULT NULL,
  projectAbstract text,
  projectProgress text,
  projectPublications text,
  projectComments text,
  lastChange timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  progressLastChange date DEFAULT NULL,
  PRIMARY KEY (projectID)
);


DROP TABLE IF EXISTS tblResearchers;
CREATE TABLE tblResearchers (
  researcherID mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  researcherFirstName varchar(30) DEFAULT NULL,
  researcherLastName varchar(30) DEFAULT NULL,
  researcherEmail varchar(50) DEFAULT NULL,
  researcherPhone varchar(20) DEFAULT NULL,
  researcherDegree varchar(15) DEFAULT NULL,
  researcherDept varchar(50) DEFAULT NULL,
  researcherOrganization varchar(60) DEFAULT NULL,
  researcherState char(2) DEFAULT NULL,
  researcherZip varchar(11) DEFAULT NULL,
  researcherCountry varchar(40) DEFAULT NULL,
  NCRR_ID int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (researcherID)
);


DROP TABLE IF EXISTS tblUsers;
CREATE TABLE tblUsers (
  researcherID int(11) NOT NULL DEFAULT '0',
  username varchar(255) NOT NULL DEFAULT '',
  password varchar(50) NOT NULL DEFAULT '',
  lastLoginTime datetime DEFAULT NULL,
  lastLoginIP varchar(20) DEFAULT NULL,
  lastPasswordChange datetime DEFAULT NULL,
  PRIMARY KEY (researcherID),
  UNIQUE KEY username (username)
);


DROP TABLE IF EXISTS tblYRCGroupMembers;
CREATE TABLE tblYRCGroupMembers (
  groupID int(11) NOT NULL DEFAULT '0',
  researcherID int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (groupID,researcherID)
);


DROP TABLE IF EXISTS tblYRCGroups;
CREATE TABLE tblYRCGroups (
  groupID mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  groupName varchar(20) NOT NULL DEFAULT '',
  groupDesc varchar(255) DEFAULT NULL,
  PRIMARY KEY (groupID),
  UNIQUE KEY groupName (groupName)
);


DROP TABLE IF EXISTS tblYatesCycleMS2Data;
CREATE TABLE tblYatesCycleMS2Data (
  cycleID int(10) unsigned NOT NULL DEFAULT '0',
  data longblob,
  PRIMARY KEY (cycleID)
);


DROP TABLE IF EXISTS tblYatesCycleSQTData;
CREATE TABLE tblYatesCycleSQTData (
  cycleID int(10) unsigned NOT NULL DEFAULT '0',
  data longblob,
  PRIMARY KEY (cycleID)
);


DROP TABLE IF EXISTS tblYatesCycles;
CREATE TABLE tblYatesCycles (
  cycleID int(10) unsigned NOT NULL AUTO_INCREMENT,
  runID int(10) unsigned NOT NULL DEFAULT '0',
  cycleFileName varchar(100) DEFAULT NULL,
  PRIMARY KEY (cycleID),
  KEY filename_idx (cycleFileName),
  KEY runID (runID)
);


DROP TABLE IF EXISTS tblYatesResultPeptide;
CREATE TABLE tblYatesResultPeptide (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  resultID int(10) unsigned NOT NULL DEFAULT '0',
  searchID int(10) unsigned DEFAULT NULL,
  scanID int(10) unsigned DEFAULT NULL,
  pepUnique enum('T','F') DEFAULT NULL,
  filename varchar(150) DEFAULT NULL,
  XCorr decimal(10,4) DEFAULT NULL,
  deltaCN decimal(10,4) DEFAULT NULL,
  MH decimal(10,4) DEFAULT NULL,
  calcMH decimal(7,2) DEFAULT NULL,
  totalIntensity decimal(10,1) DEFAULT NULL,
  spRank int(10) unsigned DEFAULT NULL,
  spScore decimal(10,2) DEFAULT NULL,
  ionProportion decimal(10,2) DEFAULT NULL,
  redundancy int(10) unsigned DEFAULT NULL,
  sequence varchar(255) DEFAULT NULL,
  pI decimal(12,8) DEFAULT NULL,
  confPercent double unsigned DEFAULT NULL,
  ZScore double unsigned DEFAULT NULL,
  ppm decimal(12,8) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY resultID (resultID),
  KEY confPercent (confPercent),
  KEY ZScore (ZScore)
);


DROP TABLE IF EXISTS tblYatesRun;
CREATE TABLE tblYatesRun (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  projectID int(10) unsigned NOT NULL DEFAULT '0',
  baitDesc varchar(40) DEFAULT NULL,
  targetSpecies int(10) unsigned DEFAULT NULL,
  runDate date DEFAULT NULL,
  directoryName varchar(255) DEFAULT NULL,
  DTASelectTXT longblob,
  DTASelectHTML mediumtext,
  DTASelectFilterTXT mediumtext,
  DTASelectParams varchar(100) DEFAULT NULL,
  comments text,
  protocolID int(10) unsigned DEFAULT NULL,
  databaseName varchar(100) DEFAULT NULL,
  baitProteinID int(10) unsigned DEFAULT NULL,
  uploadDate timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY projectID (projectID),
  KEY baitORF (baitDesc),
  KEY baitSpecies (targetSpecies),
  KEY databaseName (databaseName),
  KEY baitProteinID (baitProteinID),
  KEY directoryName (directoryName),
  KEY uploadDate (uploadDate),
  KEY runDate (runDate)
);


DROP TABLE IF EXISTS tblYatesRunResult;
CREATE TABLE tblYatesRunResult (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  runID int(10) unsigned NOT NULL DEFAULT '0',
  hitORF varchar(20) NOT NULL DEFAULT '',
  hitSpecies int(10) unsigned NOT NULL DEFAULT '0',
  sequenceCount int(10) unsigned DEFAULT NULL,
  spectrumCount int(10) unsigned DEFAULT NULL,
  sequenceCoverage decimal(6,3) DEFAULT NULL,
  length int(10) unsigned DEFAULT NULL,
  molecularWeight int(10) unsigned DEFAULT NULL,
  pI decimal(12,8) DEFAULT NULL,
  validationStatus char(1) DEFAULT NULL,
  description text,
  hitProteinID int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (id),
  KEY runID (runID),
  KEY hitORF (hitORF),
  KEY hitSpecies (hitSpecies),
  KEY hitORF_2 (hitORF,hitSpecies),
  KEY description (description(255)),
  KEY hitProteinID (hitProteinID)
);

DELIMITER |
CREATE TRIGGER tblYatesRunResult_bdelete BEFORE DELETE ON tblYatesRunResult 
FOR EACH ROW 
  BEGIN
    DELETE FROM tblYatesResultPeptide WHERE resultID = OLD.id;
  END;
|
DELIMITER ;
