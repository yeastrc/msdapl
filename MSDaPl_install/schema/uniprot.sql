DROP DATABASE IF EXISTS uniprot;
CREATE DATABASE IF NOT EXISTS uniprot;
USE uniprot;

DROP TABLE IF EXISTS uniprot_name;

CREATE TABLE uniprot_name (
  uniprot_id varchar(20) NOT NULL,
  uniprot_accession varchar(20) NOT NULL
);
ALTER TABLE uniprot_name ADD PRIMARY KEY (uniprot_id);
ALTER TABLE uniprot_name ADD INDEX (uniprot_accession);

