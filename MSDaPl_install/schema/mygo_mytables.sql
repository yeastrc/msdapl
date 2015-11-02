USE mygo;

DROP TABLE IF EXISTS YRCMAP;
CREATE TABLE YRCMAP 
(
  goID int(11) NOT NULL DEFAULT '0',
  yrcID int(10) unsigned NOT NULL DEFAULT '0'
);
ALTER TABLE YRCMAP ADD PRIMARY KEY (goID,yrcID);
ALTER TABLE YRCMAP ADD UNIQUE INDEX (goID);
ALTER TABLE YRCMAP ADD INDEX (yrcID);


DROP TABLE IF EXISTS GOProteinLookup_Ref;
CREATE TABLE GOProteinLookup_Ref 
(
  proteinID int(10) unsigned NOT NULL,
  termID int(10) unsigned NOT NULL,
  exact smallint(6) DEFAULT NULL,
  ref_annot smallint(6) NOT NULL,
  speciesID int(10) unsigned NOT NULL,
  refDb varchar(25) DEFAULT NULL
);
ALTER TABLE GOProteinLookup_Ref ADD PRIMARY KEY (proteinID,termID);
ALTER TABLE GOProteinLookup_Ref ADD INDEX (termID,proteinID);
ALTER TABLE GOProteinLookup_Ref ADD INDEX (exact);


DROP TABLE IF EXISTS GOProteinLookup_Ref_EvidenceCodes;
CREATE TABLE GOProteinLookup_Ref_EvidenceCodes 
(
  proteinID int(10) unsigned NOT NULL,
  termID int(10) unsigned NOT NULL,
  exact smallint(6) DEFAULT NULL,
  evidenceCode smallint(6) NOT NULL DEFAULT '0',
  ref_annot smallint(6) NOT NULL,
  speciesID int(10) unsigned NOT NULL,
  refDb varchar(25) DEFAULT NULL
);
ALTER TABLE GOProteinLookup_Ref_EvidenceCodes ADD PRIMARY KEY (proteinID,termID,evidenceCode);
ALTER TABLE GOProteinLookup_Ref_EvidenceCodes ADD INDEX (termID,proteinID);
ALTER TABLE GOProteinLookup_Ref_EvidenceCodes ADD INDEX (evidenceCode);
ALTER TABLE GOProteinLookup_Ref_EvidenceCodes ADD INDEX (exact);

