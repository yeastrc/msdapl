#DROP DATABASE IF EXISTS proteinfer_test;
#CREATE DATABASE proteinfer_test;
#USE proteinfer_test;

DROP TABLE IF EXISTS msProteinInferRun;
CREATE TABLE msProteinInferRun (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	program VARCHAR(255) NOT NULL,
	inputGenerator VARCHAR(255) NOT NULL,
	dateRun DATETIME,
	comments TEXT(10)
);



DROP TABLE IF EXISTS msProteinInferInput;
CREATE TABLE msProteinInferInput (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    piRunID INT UNSIGNED NOT NULL,
    inputID INT UNSIGNED NOT NULL,
    inputType ENUM('S','A')
);
ALTER TABLE msProteinInferInput ADD INDEX (piRunID);
ALTER TABLE msProteinInferInput ADD INDEX (inputID);



DROP TABLE IF EXISTS msProteinInferProtein;
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



DROP TABLE IF EXISTS msProteinInferPeptide;
CREATE TABLE msProteinInferPeptide (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	piRunID INT UNSIGNED NOT NULL,
	sequence VARCHAR(255) NOT NULL,
	uniqueToProtein TINYINT NOT NULL DEFAULT 0
);
ALTER TABLE  msProteinInferPeptide ADD INDEX (piRunID);



DROP TABLE IF EXISTS msProteinInferIon;
CREATE TABLE msProteinInferIon(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	piPeptideID INT UNSIGNED NOT NULL,
	charge INT UNSIGNED NOT NULL,
	modificationStateID INT UNSIGNED NOT NULL,
	sequence VARCHAR(255) NOT NULL
);
ALTER TABLE  msProteinInferIon ADD INDEX (piPeptideID);
ALTER TABLE  msProteinInferIon ADD INDEX (charge);
ALTER TABLE  msProteinInferIon ADD INDEX (modificationStateID);



DROP TABLE IF EXISTS msProteinInferProteinPeptideMatch;
CREATE TABLE msProteinInferProteinPeptideMatch (
    piProteinID INT UNSIGNED NOT NULL,
    piPeptideID INT UNSIGNED NOT NULL
);
ALTER TABLE msProteinInferProteinPeptideMatch ADD PRIMARY KEY (piProteinID, piPeptideID);



DROP TABLE IF EXISTS msProteinInferSpectrumMatch;
CREATE TABLE msProteinInferSpectrumMatch (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	piIonID INT UNSIGNED NOT NULL,
    msRunSearchResultID INT UNSIGNED NOT NULL,
    rank INT UNSIGNED
);
ALTER TABLE  msProteinInferSpectrumMatch ADD INDEX (msRunSearchResultID);
ALTER TABLE  msProteinInferSpectrumMatch ADD INDEX (piIonID);



#####################################################################
# IDPicker Tables
#####################################################################
DROP TABLE IF EXISTS IDPickerRunSummary;
CREATE TABLE IDPickerRunSummary (
	piRunID INT UNSIGNED NOT NULL PRIMARY KEY,
	numUnfilteredProteins INT UNSIGNED,
	numUnfilteredPeptides INT UNSIGNED
);



DROP TABLE IF EXISTS IDPickerInputSummary;
CREATE TABLE IDPickerInputSummary (
	piInputID INT UNSIGNED NOT NULL PRIMARY KEY,
	numTargetHits INT UNSIGNED,
    numDecoyHits INT UNSIGNED,
    numFilteredTargetHits INT UNSIGNED
);



DROP TABLE IF EXISTS IDPickerFilter;
CREATE TABLE IDPickerFilter (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    piRunID INT UNSIGNED NOT NULL,
    filterName VARCHAR(255) NOT NULL,
   	filterValue VARCHAR(255)
);
ALTER TABLE  IDPickerFilter ADD INDEX (piRunID);



DROP TABLE IF EXISTS IDPickerProtein;
CREATE TABLE IDPickerProtein (
	piProteinID INT UNSIGNED NOT NULL PRIMARY KEY,
	clusterID INT UNSIGNED NOT NULL,
    groupID INT UNSIGNED NOT NULL,
    isParsimonious TINYINT NOT NULL DEFAULT 0
);
ALTER TABLE IDPickerProtein ADD INDEX(clusterID);
ALTER TABLE IDPickerProtein ADD INDEX(groupID);



DROP TABLE IF EXISTS IDPickerProteinGroup;
CREATE TABLE IDPickerProteinGroup (
	piRunID INT UNSIGNED NOT NULL,
	groupID INT UNSIGNED NOT NULL,
	numPeptides_S INT UNSIGNED NOT NULL,
	numUniqPeptides_S INT UNSIGNED NOT NULL,
	numPeptides_SM INT UNSIGNED NOT NULL,
	numUniqPeptides_SM INT UNSIGNED NOT NULL,
	numPeptides_SC INT UNSIGNED NOT NULL,
	numUniqPeptides_SC INT UNSIGNED NOT NULL,
	numPeptides_SMC INT UNSIGNED NOT NULL,
	numUniqPeptides_SMC INT UNSIGNED NOT NULL
);
ALTER TABLE IDPickerProteinGroup ADD PRIMARY KEY(piRunID,  groupID);



DROP TABLE IF EXISTS IDPickerPeptide;
CREATE TABLE IDPickerPeptide (
	piPeptideID INT UNSIGNED NOT NULL PRIMARY KEY,
	groupID INT UNSIGNED NOT NULL
);
ALTER TABLE IDPickerPeptide ADD INDEX(groupID);


DROP TABLE IF EXISTS IDPickerGroupAssociation;
CREATE TABLE IDPickerGroupAssociation (
	piRunID INT UNSIGNED NOT NULL,
	proteinGroupID INT UNSIGNED NOT NULL,
	peptideGroupID INT UNSIGNED NOT NULL
);
ALTER TABLE IDPickerGroupAssociation ADD PRIMARY KEY(piRunID, proteinGroupID, peptideGroupID);


DROP TABLE IF EXISTS IDPickerSpectrumMatch;
CREATE TABLE IDPickerSpectrumMatch (
	piSpectrumMatchID INT UNSIGNED NOT NULL PRIMARY KEY,
	fdr DOUBLE UNSIGNED
);


#####################################################################
# TRIGGERS TO ENSURE CASCADING DELETES
#####################################################################

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
   	DELETE FROM msProteinInferProteinPeptideMatch WHERE piProteinID = OLD.id;
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
 	DELETE FROM IDPickerRunSummary WHERE piRunID = OLD.id;
  	DELETE FROM IDPickerFilter WHERE piRunID = OLD.id;
   	DELETE FROM msProteinInferInput WHERE piRunID = OLD.id;
  	DELETE FROM msProteinInferProtein WHERE piRunID = OLD.id;
  	DELETE FROM msProteinInferPeptide WHERE piRunID = OLD.id;
   	DELETE FROM IDPickerGroupAssociation WHERE piRunID = OLD.id;
 END;
|
DELIMITER ;
