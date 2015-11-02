
#  ######################################################

#   !!!!!!!!!!!!!!!!!   WARNING   !!!!!!!!!!!!!!!!!!!!!!!!!

#    The contents of this file reside in TWO files

# 		MS_LIBRARY/trunk/schema/ms2DatabaseSQL.sql

#	   AND

#               MSDaPl_install/trunk/schema/msData.sql

#   If you update the file in one place, update the other to keep them in sync


DROP DATABASE IF EXISTS msData;
CREATE DATABASE msData;
USE msData;


# EXPERIMENT

CREATE TABLE msExperiment (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	serverAddress VARCHAR(500),
	serverDirectory VARCHAR(500),
	uploadDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	lastUpdate TIMESTAMP NOT NULL,
	comments TEXT,
	instrumentID INT UNSIGNED
);

CREATE TABLE msInstrument (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	description VARCHAR(255)
);

# SPECTRA SIDE

CREATE TABLE msExperimentRun (
	experimentID INT UNSIGNED NOT NULL,
	runID INT UNSIGNED NOT NULL
);
ALTER TABLE  msExperimentRun ADD PRIMARY KEY (runID, experimentID);

CREATE TABLE msRun (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   filename VARCHAR(255),
   sha1Sum CHAR(40),
   creationTime VARCHAR(255),
   extractor VARCHAR(255),
   extractorVersion VARCHAR(255),
   extractorOptions VARCHAR(255),
   instrumentVendor VARCHAR(255),
   instrumentType VARCHAR(255),
   instrumentSN VARCHAR(255),
   acquisitionMethod VARCHAR(255),
   originalFileType VARCHAR(10),
   separateDigestion ENUM('T','F'),
   uploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   comment TEXT
);
ALTER TABLE msRun ADD INDEX(filename);

CREATE TABLE msRunLocation (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   runID INT UNSIGNED NOT NULL,
   serverDirectory VARCHAR(500),
   createDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE msRunLocation ADD INDEX(runID);


CREATE TABLE msScan (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   runID INT UNSIGNED NOT NULL,
   startScanNumber INT UNSIGNED,
   endScanNumber INT UNSIGNED,
   level TINYINT UNSIGNED,
   preMZ DECIMAL(18,9),
   preScanID INT UNSIGNED,
   prescanNumber INT UNSIGNED,
   retentionTime DECIMAL(10,5),
   fragmentationType CHAR(3),
   isCentroid ENUM('T','F'),
   peakCount INT UNSIGNED
);
ALTER TABLE msScan ADD INDEX(runID);
ALTER TABLE msScan ADD INDEX(startScanNumber);
ALTER TABLE msScan ADD INDEX(runID, startScanNumber);

CREATE TABLE msScanData (
   scanID INT UNSIGNED NOT NULL PRIMARY KEY,
   type CHAR(1) NOT NULL,
   data LONGBLOB NOT NULL
)
PARTITION BY KEY()
PARTITIONS 100;
ALTER TABLE msScanData ADD INDEX(scanID);

CREATE TABLE MS2FileScanCharge (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   scanID INT UNSIGNED NOT NULL,
   charge TINYINT UNSIGNED NOT NULL,
   mass DECIMAL(18,9)
);
ALTER TABLE MS2FileScanCharge ADD INDEX(scanID);

CREATE TABLE MS2FileHeader (
   id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
   runID INTEGER NOT NULL,
   header VARCHAR(255) NOT NULL,
   value TEXT
);
ALTER TABLE MS2FileHeader ADD INDEX(runID, header);

CREATE TABLE MS2FileChargeIndependentAnalysis (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   scanID INT UNSIGNED NOT NULL,
   header VARCHAR(255),
   value TEXT
);
ALTER TABLE MS2FileChargeIndependentAnalysis ADD INDEX(scanID);

CREATE TABLE MS2FileChargeDependentAnalysis (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   scanChargeID INT UNSIGNED NOT NULL,
   header VARCHAR(255),
   value TEXT
);
ALTER TABLE MS2FileChargeDependentAnalysis ADD INDEX(scanChargeID);

CREATE TABLE msDigestionEnzyme (
   id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   sense TINYINT,
   cut VARCHAR(20),
   nocut VARCHAR(20),
   description TEXT
);
ALTER TABLE msDigestionEnzyme ADD INDEX(name);

CREATE TABLE msRunEnzyme (
   runID INT UNSIGNED NOT NULL,
   enzymeID INT UNSIGNED NOT NULL
);
ALTER TABLE msRunEnzyme ADD PRIMARY KEY (runID, enzymeID);
ALTER TABLE msRunEnzyme ADD INDEX (enzymeID);




# PEPTIDE ANALYSIS SIDE

CREATE TABLE msSearch (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   experimentID INT UNSIGNED NOT NULL,
   expDate DATE,
   serverDirectory VARCHAR(500),
   analysisProgramName VARCHAR(255),
   analysisProgramVersion VARCHAR(20),
   uploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE msSearch ADD INDEX(expDate);
ALTER TABLE msSearch ADD INDEX(experimentID);


CREATE TABLE SQTParams (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   param VARCHAR(255) NOT NULL,
   value TEXT
);
ALTER TABLE SQTParams ADD INDEX(searchID,param);

CREATE TABLE MascotParams (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   param VARCHAR(255) NOT NULL,
   value TEXT
);
ALTER TABLE MascotParams ADD INDEX(searchID,param);

CREATE TABLE XtandemParams (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   param VARCHAR(255) NOT NULL,
   value TEXT
);
ALTER TABLE XtandemParams ADD INDEX(searchID,param);

CREATE TABLE ProLuCIDParams (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   elementName VARCHAR(255) NOT NULL,
   value TEXT,
   parentID INT UNSIGNED
);
ALTER TABLE ProLuCIDParams ADD INDEX(searchID,elementName);


CREATE TABLE msRunSearch (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   runID INT UNSIGNED NOT NULL,
   searchID INT UNSIGNED NOT NULL,
   originalFileType VARCHAR(50) NOT NULL,
   searchDate DATE,
   searchDuration INT UNSIGNED,
   uploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
ALTER TABLE msRunSearch ADD INDEX(runID);
ALTER TABLE msRunSearch ADD INDEX(searchID);

CREATE TABLE msSearchEnzyme (
   searchID INT UNSIGNED NOT NULL,
   enzymeID INT UNSIGNED NOT NULL
);
ALTER TABLE msSearchEnzyme ADD PRIMARY KEY (searchID, enzymeID);
ALTER TABLE msSearchEnzyme ADD INDEX (enzymeID);


CREATE TABLE msRunSearchResult (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   runSearchID INT UNSIGNED NOT NULL,
   scanID INT UNSIGNED NOT NULL,
   charge TINYINT NOT NULL,
   observedMass DECIMAL(18,9),
   peptide VARCHAR(500) NOT NULL,
   preResidue CHAR(1),
   postResidue CHAR(1),
   validationStatus CHAR(1)
);

ALTER TABLE msRunSearchResult ADD INDEX(runSearchID);
ALTER TABLE msRunSearchResult ADD INDEX(scanID);
# ALTER TABLE msRunSearchResult ADD INDEX(charge);
ALTER TABLE msRunSearchResult ADD INDEX(peptide(10));
ALTER TABLE msRunSearchResult add index (runSearchID, scanID);
# DO I WANT ALL THESE INDICES?

CREATE TABLE msProteinMatch (
    resultID INT UNSIGNED NOT NULL,
    accession VARCHAR(500) NOT NULL
)
PARTITION BY KEY(resultID)
PARTITIONS 100;
ALTER TABLE msProteinMatch ADD PRIMARY KEY(resultID, accession);
ALTER TABLE msProteinMatch ADD INDEX(accession);


CREATE TABLE SQTSpectrumData (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   scanID INT UNSIGNED NOT NULL,
   runSearchID INT UNSIGNED NOT NULL,
   charge TINYINT UNSIGNED NOT NULL,
   observedMass DECIMAL(18,9) NOT NULL,
   processTime INT UNSIGNED,
   serverName VARCHAR(50),
   totalIntensity DOUBLE,
   lowestSp DECIMAL(10,5),
   sequenceMatches INT UNSIGNED
);
ALTER TABLE SQTSpectrumData ADD INDEX(scanID, runSearchID);
ALTER TABLE SQTSpectrumData ADD INDEX (runSearchID);

CREATE TABLE SQTFileHeader (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   runSearchID INT UNSIGNED NOT NULL,
   header VARCHAR(255) NOT NULL,
   value TEXT
);
ALTER TABLE SQTFileHeader ADD INDEX(runSearchID, header);

CREATE TABLE msSequenceDatabaseDetail (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   serverAddress VARCHAR(100),
   serverPath VARCHAR(500),
   sequenceDatabaseID INT UNSIGNED NOT NULL
);

CREATE TABLE msSearchDatabase (
   searchID INT UNSIGNED NOT NULL,
   databaseID INT UNSIGNED NOT NULL
);
ALTER TABLE msSearchDatabase ADD PRIMARY KEY(searchID, databaseID);
ALTER TABLE msSearchDatabase ADD INDEX(databaseID);

CREATE TABLE msSearchStaticMod (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   residue CHAR(1) NOT NULL,
   modifier DECIMAL(18,9) NOT NULL
);
ALTER TABLE msSearchStaticMod ADD INDEX(searchID);

CREATE TABLE msSearchTerminalStaticMod (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   terminus ENUM('N','C') NOT NULL,
   modifier DECIMAL(18,9) NOT NULL
);
ALTER TABLE msSearchTerminalStaticMod ADD INDEX(searchID);


CREATE TABLE msSearchDynamicMod (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   residue CHAR(1) NOT NULL,
   modifier DECIMAL(18,9) NOT NULL,
   symbol CHAR(1)
);
ALTER TABLE msSearchDynamicMod ADD INDEX(searchID);

CREATE TABLE msSearchTerminalDynamicMod (
   id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
   searchID INT UNSIGNED NOT NULL,
   terminus ENUM('N','C') NOT NULL,
   modifier DECIMAL(18,9) NOT NULL,
   symbol CHAR(1)
);
ALTER TABLE msSearchTerminalDynamicMod ADD INDEX(searchID);


CREATE TABLE msDynamicModResult (
   modID INT UNSIGNED NOT NULL,
   resultID INT UNSIGNED NOT NULL,
   position SMALLINT UNSIGNED NOT NULL
);
ALTER TABLE msDynamicModResult ADD PRIMARY KEY(modID, resultID, position);
ALTER TABLE msDynamicModResult ADD INDEX(resultID);

CREATE TABLE msTerminalDynamicModResult (
   modID INT UNSIGNED NOT NULL,
   resultID INT UNSIGNED NOT NULL
);
ALTER TABLE msTerminalDynamicModResult ADD PRIMARY KEY(modID, resultID);
ALTER TABLE msTerminalDynamicModResult ADD INDEX(resultID);


CREATE TABLE SQTSearchResult (
   resultID INT UNSIGNED NOT NULL PRIMARY KEY,
   XCorrRank INT UNSIGNED NOT NULL,
   spRank INT UNSIGNED NOT NULL,
   deltaCN DECIMAL(10,5) NOT NULL,
   deltaCNstar DECIMAL(10,5),
   XCorr DECIMAL(10,5) NOT NULL,
   sp DECIMAL(10,5),
   calculatedMass DECIMAL(18,9),
   matchingIons INT UNSIGNED,
   predictedIons INT UNSIGNED,
   evalue DOUBLE UNSIGNED
);
ALTER TABLE SQTSearchResult ADD INDEX(XCorrRank);
ALTER TABLE SQTSearchResult ADD INDEX(spRank);
ALTER TABLE SQTSearchResult ADD INDEX(deltaCN);
ALTER TABLE SQTSearchResult ADD INDEX(XCorr);
ALTER TABLE SQTSearchResult ADD INDEX(sp);

CREATE TABLE MascotSearchResult (
   resultID INT UNSIGNED NOT NULL PRIMARY KEY,
   rank INT UNSIGNED NOT NULL,
   ionScore DECIMAL(10,5) NOT NULL,
   identityScore DECIMAL(10,5) NOT NULL,
   homologyScore DECIMAL(10,5) NOT NULL,
   expect DECIMAL(10,5) NOT NULL,
   star INT UNSIGNED NOT NULL,
   calculatedMass DECIMAL(18,9),
   matchingIons INT UNSIGNED,
   predictedIons INT UNSIGNED
);
ALTER TABLE MascotSearchResult ADD INDEX(ionScore);
ALTER TABLE MascotSearchResult ADD INDEX(identityScore);
ALTER TABLE MascotSearchResult ADD INDEX(homologyScore);
ALTER TABLE MascotSearchResult ADD INDEX(expect);

CREATE TABLE XtandemSearchResult (
   resultID INT UNSIGNED NOT NULL PRIMARY KEY,
   rank INT UNSIGNED NOT NULL,
   hyperscore DECIMAL(10,5) NOT NULL,
   nextscore DECIMAL(10,5) NOT NULL,
   bscore DECIMAL(10,5) NOT NULL,
   yscore DECIMAL(10,5) NOT NULL,
   expect DECIMAL(10,5) NOT NULL,
   calculatedMass DECIMAL(18,9),
   matchingIons INT UNSIGNED,
   predictedIons INT UNSIGNED
);
ALTER TABLE XtandemSearchResult ADD INDEX(hyperscore);
ALTER TABLE XtandemSearchResult ADD INDEX(nextscore);
ALTER TABLE XtandemSearchResult ADD INDEX(bscore);
ALTER TABLE XtandemSearchResult ADD INDEX(yscore);
ALTER TABLE XtandemSearchResult ADD INDEX(expect);


CREATE TABLE ProLuCIDSearchResult (
   resultID INT UNSIGNED NOT NULL PRIMARY KEY,
   primaryScoreRank INT UNSIGNED NOT NULL,
   secondaryScoreRank INT UNSIGNED NOT NULL,
   deltaCN DECIMAL(10,5)  NOT NULL,
   primaryScore DOUBLE NOT NULL,
   secondaryScore DOUBLE NOT NULL,
   calculatedMass DECIMAL(18,9),
   matchingIons INT UNSIGNED,
   predictedIons INT UNSIGNED
);
ALTER TABLE ProLuCIDSearchResult ADD INDEX(primaryScoreRank);
ALTER TABLE ProLuCIDSearchResult ADD INDEX(secondaryScoreRank);
ALTER TABLE ProLuCIDSearchResult ADD INDEX(deltaCN);
ALTER TABLE ProLuCIDSearchResult ADD INDEX(primaryScore);
ALTER TABLE ProLuCIDSearchResult ADD INDEX(secondaryScore);


#####################################################################
# Search Analysis & Percolator tables
#####################################################################
CREATE TABLE msSearchAnalysis (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	   	programName VARCHAR(255) NOT NULL,
	   	programVersion VARCHAR(255),
	   	uploadDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	   	comments TEXT,
	   	filename VARCHAR(255)
);

CREATE TABLE msRunSearchAnalysis (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		searchAnalysisID INT UNSIGNED NOT NULL,
		runSearchID INT UNSIGNED NOT NULL,
	   	originalFileType VARCHAR(10) NOT NULL
);
ALTER TABLE msRunSearchAnalysis ADD INDEX(searchAnalysisID);
ALTER TABLE msRunSearchAnalysis ADD INDEX(runSearchID);


CREATE TABLE PercolatorParams (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		searchAnalysisID INT UNSIGNED NOT NULL,
		param VARCHAR(255) NOT NULL,
   		value TEXT
);
ALTER TABLE PercolatorParams ADD INDEX(searchAnalysisID);


CREATE TABLE PercolatorResult (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		resultID INT UNSIGNED NOT NULL,
		runSearchAnalysisID INT UNSIGNED NOT NULL,
		qvalue DOUBLE UNSIGNED NOT NULL,
		pep DOUBLE UNSIGNED,
		discriminantScore DOUBLE,
		pvalue DOUBLE UNSIGNED,
		predictedRetentionTime DECIMAL(10,5),
		peptideResultID INT UNSIGNED
);
ALTER TABLE PercolatorResult ADD INDEX(runSearchAnalysisID, qvalue);
ALTER TABLE PercolatorResult ADD INDEX(resultID);
ALTER TABLE PercolatorResult ADD INDEX(pep);
ALTER TABLE PercolatorResult ADD INDEX(peptideResultID);

CREATE TABLE BaristaPSMResult (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		baristaPsmID INT UNSIGNED NOT NULL,
		resultID INT UNSIGNED NOT NULL,
		runSearchAnalysisID INT UNSIGNED NOT NULL,
		qvalue DOUBLE UNSIGNED NOT NULL,
		score DOUBLE UNSIGNED NOT NULL,
		peptideResultID INT UNSIGNED NOT NULL
);
ALTER TABLE BaristaPSMResult ADD INDEX(runSearchAnalysisID, qvalue);
ALTER TABLE BaristaPSMResult ADD INDEX(runSearchAnalysisID, baristaPsmID);
ALTER TABLE BaristaPSMResult ADD INDEX(resultID);
ALTER TABLE BaristaPSMResult ADD INDEX(peptideResultID);

CREATE TABLE PercolatorPeptideResult (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		searchAnalysisID INT UNSIGNED NOT NULL,
		peptide VARCHAR(500) NOT NULL,
		qvalue DOUBLE UNSIGNED NOT NULL,
		pep DOUBLE UNSIGNED,
		discriminantScore DOUBLE,
		pvalue DOUBLE UNSIGNED
);
ALTER TABLE PercolatorPeptideResult ADD INDEX(searchAnalysisID);
ALTER TABLE PercolatorPeptideResult ADD INDEX(qvalue);
ALTER TABLE PercolatorPeptideResult ADD INDEX(pep);
ALTER TABLE PercolatorPeptideResult ADD UNIQUE INDEX (searchAnalysisID, peptide);

CREATE TABLE BaristaPeptideResult (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		searchAnalysisID INT UNSIGNED NOT NULL,
		peptide VARCHAR(500) NOT NULL,
		qvalue DOUBLE UNSIGNED NOT NULL,
		score DOUBLE UNSIGNED NOT NULL
);
ALTER TABLE BaristaPeptideResult ADD INDEX(searchAnalysisID, qvalue);

#####################################################################
# PeptideProphet tables
#####################################################################
CREATE TABLE PeptideProphetROC (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		searchAnalysisID INT UNSIGNED NOT NULL,
	   	sensitivity DOUBLE UNSIGNED NOT NULL,
	   	error DOUBLE UNSIGNED NOT NULL,
	   	minProbability DOUBLE UNSIGNED NOT NULL,
	   	numCorrect INT UNSIGNED NOT NULL,
	   	numIncorrect INT UNSIGNED NOT NULL
);
ALTER TABLE PeptideProphetROC ADD INDEX(searchAnalysisID);

CREATE TABLE PeptideProphetResult (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		resultID INT UNSIGNED NOT NULL,
		runSearchAnalysisID INT UNSIGNED NOT NULL,
		probability DOUBLE UNSIGNED NOT NULL,
		fVal DOUBLE NOT NULL,
		numEnzymaticTermini INT UNSIGNED NOT NULL,
		numMissedCleavages INT UNSIGNED NOT NULL,
		massDifference DOUBLE  NOT NULL,
		probabilityNet_0 DOUBLE UNSIGNED,
		probabilityNet_1 DOUBLE UNSIGNED,
		probabilityNet_2 DOUBLE UNSIGNED
);
ALTER TABLE PeptideProphetResult ADD INDEX(runSearchAnalysisID);
ALTER TABLE PeptideProphetResult ADD INDEX(resultID);
ALTER TABLE PeptideProphetResult ADD INDEX(probability);



#######################################################################################
# Protein Inference tables
#######################################################################################

CREATE TABLE msProteinInferRun (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	program VARCHAR(255) NOT NULL,
	programVersion VARCHAR(255) NOT NULL,
	inputGenerator VARCHAR(255) NOT NULL,
	dateRun DATETIME,
	comments TEXT(10),
	name VARCHAR(10)
);

CREATE TABLE msProteinInferInput (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    piRunID INT UNSIGNED NOT NULL,
    inputID INT UNSIGNED NOT NULL
);
ALTER TABLE msProteinInferInput ADD INDEX (piRunID);
ALTER TABLE msProteinInferInput ADD INDEX (inputID);


CREATE TABLE msProteinInferProtein (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	nrseqProteinID INT UNSIGNED NOT NULL,
	piRunID INT UNSIGNED NOT NULL,
    coverage DOUBLE UNSIGNED,
    userValidation CHAR(1),
    userAnnotation TEXT(10)
);
ALTER TABLE  msProteinInferProtein ADD INDEX (piRunID);
ALTER TABLE  msProteinInferProtein ADD INDEX (nrseqProteinID);
ALTER TABLE msProteinInferProtein add index (piRunID, nrseqProteinID);


CREATE TABLE msProteinInferPeptide (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	piRunID INT UNSIGNED NOT NULL,
	sequence VARCHAR(255) NOT NULL,
	uniqueToProtein TINYINT NOT NULL DEFAULT 0
);
ALTER TABLE  msProteinInferPeptide ADD UNIQUE INDEX (piRunID, sequence);


CREATE TABLE msProteinInferIon(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	piPeptideID INT UNSIGNED NOT NULL,
	charge INT UNSIGNED NOT NULL,
	modifiedSequence VARCHAR(255) NOT NULL,
	modificationStateID INT UNSIGNED NOT NULL
);
ALTER TABLE  msProteinInferIon ADD INDEX (piPeptideID);
ALTER TABLE  msProteinInferIon ADD INDEX (charge);
ALTER TABLE  msProteinInferIon ADD INDEX (modificationStateID);


CREATE TABLE msProteinInferProteinPeptideMatch (
    piProteinID INT UNSIGNED NOT NULL,
    piPeptideID INT UNSIGNED NOT NULL
);
ALTER TABLE msProteinInferProteinPeptideMatch ADD PRIMARY KEY (piProteinID, piPeptideID);
ALTER TABLE msProteinInferProteinPeptideMatch ADD INDEX (piPeptideID);


CREATE TABLE msProteinInferSpectrumMatch (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	piIonID INT UNSIGNED NOT NULL,
    resultID INT UNSIGNED NOT NULL,
    scanID INT UNSIGNED NOT NULL,
    rankForPeptide INT UNSIGNED
);
ALTER TABLE  msProteinInferSpectrumMatch ADD INDEX (resultID);
ALTER TABLE  msProteinInferSpectrumMatch ADD UNIQUE INDEX (piIonID, resultID);


#####################################################################
# IDPicker Tables
#####################################################################

CREATE TABLE IDPickerInputSummary (
	piInputID INT UNSIGNED NOT NULL PRIMARY KEY,
	numTargetHits INT UNSIGNED,
    numDecoyHits INT UNSIGNED,
    numFilteredTargetHits INT UNSIGNED,
    numFilteredDecoyHits INT UNSIGNED
);


CREATE TABLE IDPickerParam (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    piRunID INT UNSIGNED NOT NULL,
    name VARCHAR(255) NOT NULL,
   	value VARCHAR(255)
);
ALTER TABLE  IDPickerParam ADD INDEX (piRunID);


CREATE TABLE IDPickerProtein (
	piProteinID INT UNSIGNED NOT NULL PRIMARY KEY,
	clusterLabel INT UNSIGNED NOT NULL,
    proteinGroupLabel INT UNSIGNED NOT NULL,
    nsaf DOUBLE UNSIGNED,
	isParsimonious TINYINT NOT NULL DEFAULT 0,
    isSubset TINYINT NOT NULL DEFAULT 0
);
ALTER TABLE IDPickerProtein ADD INDEX(proteinGroupLabel);
ALTER TABLE IDPickerProtein ADD INDEX(clusterLabel);


CREATE TABLE IDPickerSubsetProtein (
	subsetProteinID INT UNSIGNED NOT NULL,
	superProteinID INT UNSIGNED NOT NULL 
);
ALTER TABLE IDPickerSubsetProtein ADD INDEX (subsetProteinID);
ALTER TABLE IDPickerSubsetProtein ADD INDEX (superProteinID);

CREATE TABLE IDPickerPeptide (
	piPeptideID INT UNSIGNED NOT NULL PRIMARY KEY,
	peptideGroupLabel INT UNSIGNED NOT NULL
);
ALTER TABLE IDPickerPeptide ADD INDEX(peptideGroupLabel);


CREATE TABLE IDPickerGroupAssociation (
	piRunID INT UNSIGNED NOT NULL,
	proteinGroupLabel INT UNSIGNED NOT NULL,
	peptideGroupLabel INT UNSIGNED NOT NULL
);
ALTER TABLE IDPickerGroupAssociation ADD PRIMARY KEY(piRunID, proteinGroupLabel, peptideGroupLabel);


CREATE TABLE IDPickerSpectrumMatch (
	piSpectrumMatchID INT UNSIGNED NOT NULL PRIMARY KEY,
	fdr DOUBLE UNSIGNED
);

#####################################################################
# ProteinProphet Tables
#####################################################################
CREATE TABLE ProteinProphetRun (
    piRunID INT UNSIGNED NOT NULL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL
);

CREATE TABLE ProteinProphetParam (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    piRunID INT UNSIGNED NOT NULL,
    name VARCHAR(255) NOT NULL,
   	value VARCHAR(255)
);
ALTER TABLE  ProteinProphetParam ADD INDEX (piRunID);

CREATE TABLE ProteinProphetROC (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		piRunID INT UNSIGNED NOT NULL,
	   	sensitivity DOUBLE UNSIGNED NOT NULL,
	   	falsePositiveErrorRate DOUBLE UNSIGNED NOT NULL,
	   	minProbability DOUBLE UNSIGNED NOT NULL,
	   	numCorrect INT UNSIGNED NOT NULL,
	   	numIncorrect INT UNSIGNED NOT NULL
);
ALTER TABLE ProteinProphetROC ADD INDEX(piRunID);


CREATE TABLE ProteinProphetProteinGroup (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	piRunID INT UNSIGNED NOT NULL,
    groupNumber INT UNSIGNED NOT NULL,
    probability DOUBLE UNSIGNED NOT NULL
);
ALTER TABLE ProteinProphetProteinGroup ADD INDEX(piRunID, probability);


CREATE TABLE ProteinProphetProtein (
	piProteinID INT UNSIGNED NOT NULL PRIMARY KEY,
	proteinProphetGroupID INT UNSIGNED NOT NULL,
    groupID INT UNSIGNED NOT NULL,
    probability DOUBLE UNSIGNED NOT NULL,
    confidence DOUBLE UNSIGNED,
    subsumed TINYINT NOT NULL DEFAULT 0,
    totalSpectrumCount INT UNSIGNED NOT NULL,
    pctSpectrumCount DOUBLE UNSIGNED
);
ALTER TABLE ProteinProphetProtein ADD INDEX(groupID);
ALTER TABLE ProteinProphetProtein ADD INDEX(proteinProphetGroupID);

CREATE TABLE ProteinProphetProteinIon (
	piProteinID INT UNSIGNED NOT NULL ,
	piIonID INT UNSIGNED NOT NULL ,
	weight DOUBLE UNSIGNED NOT NULL,
    initialProbability DOUBLE UNSIGNED NOT NULL,
    nspAdjProbability DOUBLE UNSIGNED NOT NULL,
    numSiblingPeptides DOUBLE UNSIGNED NOT NULL,
    isContributingEvidence TINYINT NOT NULL DEFAULT 1
);
ALTER TABLE ProteinProphetProteinIon ADD PRIMARY KEY (piProteinID, piIonID);
ALTER TABLE ProteinProphetProteinIon ADD INDEX (piIonID);

CREATE TABLE ProteinProphetSubsumedProtein (
	subsumedProteinID INT UNSIGNED NOT NULL ,
	subsumingProteinID INT UNSIGNED NOT NULL 
);
ALTER TABLE ProteinProphetSubsumedProtein ADD INDEX (subsumedProteinID);
ALTER TABLE ProteinProphetSubsumedProtein ADD INDEX (subsumingProteinID);

#####################################################################
# Barista Tables
#####################################################################

CREATE TABLE BaristaProteinGroup (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	piRunID INT UNSIGNED NOT NULL,
    proteinGroupLabel INT UNSIGNED NOT NULL,
    qvalue DOUBLE UNSIGNED NOT NULL,
    score DOUBLE UNSIGNED NOT NULL
);
ALTER TABLE BaristaProteinGroup ADD INDEX(piRunID, qvalue);

CREATE TABLE BaristaProtein (
	piProteinID INT UNSIGNED NOT NULL PRIMARY KEY,
	proteinGroupID INT UNSIGNED NOT NULL
);
ALTER TABLE BaristaProtein ADD INDEX(proteinGroupID);


#####################################################################
# Plot Tables
#####################################################################

CREATE TABLE mz_scan_count_plot_data (
  experiment_id int(10) unsigned NOT NULL,
  plot_data varchar(4000) NOT NULL,
  scan_count int(10) unsigned NOT NULL,
  create_time_in_seconds int(10) unsigned NOT NULL,
  data_version int(10) unsigned NOT NULL,
  PRIMARY KEY (experiment_id)
);

#######################################################################################
# TRIGGERS TO ENSURE CASCADING DELETES
#######################################################################################


#######################################################################################
# Protein Inference tables
#######################################################################################
DELIMITER |
CREATE TRIGGER msProteinInferSpectrumMatch_bdelete BEFORE DELETE ON msProteinInferSpectrumMatch
 FOR EACH ROW
 BEGIN
   DELETE FROM IDPickerSpectrumMatch WHERE piSpectrumMatchID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msProteinInferIon_bdelete BEFORE DELETE ON msProteinInferIon
 FOR EACH ROW
 BEGIN
   DELETE FROM msProteinInferSpectrumMatch WHERE piIonID = OLD.id;
   DELETE FROM ProteinProphetProteinIon WHERE piIonID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msProteinInferPeptide_bdelete BEFORE DELETE ON msProteinInferPeptide
 FOR EACH ROW
 BEGIN
   DELETE FROM msProteinInferIon WHERE piPeptideID = OLD.id;
   DELETE FROM IDPickerPeptide WHERE piPeptideID = OLD.id;
   DELETE FROM msProteinInferProteinPeptideMatch WHERE piPeptideID = OLD.id;
 END;
|
DELIMITER ;


DELIMITER |
CREATE TRIGGER msProteinInferProtein_bdelete BEFORE DELETE ON msProteinInferProtein
 FOR EACH ROW
 BEGIN
 	DELETE FROM IDPickerProtein WHERE piProteinID = OLD.id;
 	DELETE FROM IDPickerSubsetProtein WHERE subsetProteinID = OLD.id;
   	DELETE FROM IDPickerSubsetProtein WHERE superProteinID = OLD.id;
 	DELETE FROM ProteinProphetProtein WHERE piProteinID = OLD.id;
   	DELETE FROM msProteinInferProteinPeptideMatch WHERE piProteinID = OLD.id;
   	DELETE FROM ProteinProphetSubsumedProtein WHERE subsumedProteinID = OLD.id;
   	DELETE FROM ProteinProphetSubsumedProtein WHERE subsumingProteinID = OLD.id;
   	DELETE FROM ProteinProphetProteinIon WHERE piProteinID = OLD.id;
   	DELETE FROM IDPickerSubsetProtein WHERE subsetProteinID = OLD.id;
   	DELETE FROM IDPickerSubsetProtein WHERE superProteinID = OLD.id;
   	DELETE FROM BaristaProtein WHERE piProteinID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msProteinInferInput_bdelete BEFORE DELETE ON msProteinInferInput
 FOR EACH ROW
 BEGIN
 	DELETE FROM IDPickerInputSummary WHERE piInputID = OLD.id;
 END;
|
DELIMITER ;


DELIMITER |
CREATE TRIGGER msProteinInferRun_bdelete BEFORE DELETE ON msProteinInferRun
 FOR EACH ROW
 BEGIN
  	DELETE FROM IDPickerParam WHERE piRunID = OLD.id;
  	DELETE FROM IDPickerGroupAssociation WHERE piRunID = OLD.id;
   	DELETE FROM msProteinInferInput WHERE piRunID = OLD.id;
  	DELETE FROM msProteinInferProtein WHERE piRunID = OLD.id;
  	DELETE FROM msProteinInferPeptide WHERE piRunID = OLD.id;
  	DELETE FROM ProteinProphetRun WHERE piRunID = OLD.id;
  	DELETE FROM ProteinProphetParam WHERE piRunID = OLD.id;
  	DELETE FROM ProteinProphetROC WHERE piRunID = OLD.id;
  	DELETE FROM ProteinProphetProteinGroup WHERE piRunID = OLD.id;
  	DELETE FROM BaristaProteinGroup WHERE piRunID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER BaristaProteinGroup_bdelete BEFORE DELETE ON BaristaProteinGroup
 FOR EACH ROW
 BEGIN
  	DELETE FROM BaristaProtein WHERE proteinGroupID = OLD.id;
 END;
|
DELIMITER ;

#######################################################################################



#######################################################################################
# Search Analysis tables
#######################################################################################
DELIMITER |
CREATE TRIGGER msSearchAnalysis_bdelete BEFORE DELETE ON msSearchAnalysis
 FOR EACH ROW
 BEGIN
   	DELETE FROM PercolatorParams WHERE searchAnalysisID = OLD.id;
   	DELETE FROM PeptideProphetROC WHERE searchAnalysisID = OLD.id;
   	DELETE FROM PercolatorPeptideResult WHERE searchAnalysisID = OLD.id;
   	DELETE FROM BaristaPeptideResult WHERE searchAnalysisID = OLD.id;
	DELETE FROM msRunSearchAnalysis WHERE searchAnalysisID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msRunSearchAnalysis_bdelete BEFORE DELETE ON msRunSearchAnalysis
 FOR EACH ROW
 BEGIN
 	DELETE FROM PercolatorResult WHERE runSearchAnalysisID = OLD.id;
 	DELETE FROM PeptideProphetResult WHERE runSearchAnalysisID = OLD.id;
 	DELETE FROM BaristaPSMResult WHERE runSearchAnalysisID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER PercolatorPeptideResult_bdelete BEFORE DELETE ON PercolatorPeptideResult
 FOR EACH ROW
 BEGIN
 	DELETE FROM PercolatorResult WHERE peptideResultId = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER BaristaPeptideResult_bdelete BEFORE DELETE ON BaristaPeptideResult
 FOR EACH ROW
 BEGIN
 	DELETE FROM BaristaPSMResult WHERE peptideResultId = OLD.id;
 END;
|
DELIMITER ;

#######################################################################################



DELIMITER |
CREATE TRIGGER msRunSearchResult_bdelete BEFORE DELETE ON msRunSearchResult
 FOR EACH ROW
 BEGIN
   DELETE FROM msProteinMatch WHERE resultID = OLD.id;
   DELETE FROM SQTSearchResult WHERE resultID = OLD.id;
   DELETE FROM MascotSearchResult WHERE resultID = OLD.id;
   DELETE FROM XtandemSearchResult WHERE resultID = OLD.id;
   DELETE FROM ProLuCIDSearchResult WHERE resultID = OLD.id;
   DELETE FROM msDynamicModResult WHERE resultID = OLD.id;
   DELETE FROM msTerminalDynamicModResult WHERE resultID = OLD.id;
   DELETE FROM PercolatorResult WHERE resultID = OLD.id;
   DELETE FROM PeptideProphetResult WHERE resultID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msSearchDynamicMod_bdelete BEFORE DELETE ON msSearchDynamicMod
 FOR EACH ROW
 BEGIN
   DELETE FROM msDynamicModResult WHERE modID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msSearchTerminalDynamicMod_bdelete BEFORE DELETE ON msSearchTerminalDynamicMod
 FOR EACH ROW
 BEGIN
   DELETE FROM msTerminalDynamicModResult WHERE modID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msSequenceDatabaseDetail_bdelete BEFORE DELETE ON msSequenceDatabaseDetail
 FOR EACH ROW
 BEGIN
   DELETE FROM msSearchDatabase WHERE databaseID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msRunSearch_bdelete BEFORE DELETE ON msRunSearch
 FOR EACH ROW
 BEGIN
   DELETE FROM msRunSearchResult WHERE runSearchID = OLD.id;
   DELETE FROM SQTSpectrumData WHERE runSearchID = OLD.id;
   DELETE FROM SQTFileHeader WHERE runSearchID = OLD.id;
   DELETE FROM msRunSearchAnalysis WHERE runSearchID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msDigestionEnzyme_bdelete BEFORE DELETE ON msDigestionEnzyme
 FOR EACH ROW
 BEGIN
   DELETE FROM msSearchEnzyme WHERE enzymeID = OLD.id;
   DELETE FROM msRunEnzyme WHERE enzymeID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER MS2FileScanCharge_bdelete BEFORE DELETE ON MS2FileScanCharge
 FOR EACH ROW
 BEGIN
   DELETE FROM MS2FileChargeDependentAnalysis WHERE scanChargeID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msScan_bdelete BEFORE DELETE ON msScan
 FOR EACH ROW
 BEGIN
   DELETE FROM MS2FileScanCharge WHERE scanID = OLD.id;
   DELETE FROM msRunSearchResult WHERE scanID = OLD.id;
   DELETE FROM SQTSpectrumData WHERE scanID = OLD.id;
   DELETE FROM MS2FileChargeIndependentAnalysis WHERE scanID = OLD.id;
   DELETE FROM msScanData WHERE scanID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msRun_bdelete BEFORE DELETE ON msRun
 FOR EACH ROW
 BEGIN
   DELETE FROM msScan WHERE runID = OLD.id;
   DELETE FROM msRunSearch WHERE runID = OLD.id;
   DELETE FROM msRunEnzyme WHERE runID = OLD.id;
   DELETE FROM MS2FileHeader WHERE runID = OLD.id;
   DELETE FROM msRunLocation WHERE runID = OLD.id;
   DELETE FROM msExperimentRun WHERE runID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msSearch_bdelete BEFORE DELETE ON msSearch
 FOR EACH ROW
 BEGIN
   DELETE FROM msSearchDatabase WHERE searchID = OLD.id;
   DELETE FROM SQTParams WHERE searchID = OLD.id;
   DELETE FROM MascotParams WHERE searchID = OLD.id;
   DELETE FROM XtandemParams WHERE searchID = OLD.id;
   DELETE FROM ProLuCIDParams WHERE searchID = OLD.id;
   DELETE FROM msSearchEnzyme WHERE searchID = OLD.id;
   DELETE FROM msRunSearch WHERE searchID = OLD.id;
   DELETE FROM msSearchStaticMod WHERE searchID = OLD.id;
   DELETE FROM msSearchTerminalStaticMod WHERE searchID = OLD.id;
   DELETE FROM msSearchDynamicMod WHERE searchID = OLD.id;
   DELETE FROM msSearchTerminalDynamicMod WHERE searchID = OLD.id;
 END;
|
DELIMITER ;

DELIMITER |
CREATE TRIGGER msExperiment_bdelete BEFORE DELETE ON msExperiment
 FOR EACH ROW
 BEGIN
   DELETE FROM msSearch WHERE experimentID = OLD.id;
   DELETE FROM msExperimentRun WHERE experimentID = OLD.id;
 END;
|
DELIMITER ;
