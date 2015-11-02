
--  9-8-2014


use mainDb;


DROP TABLE IF EXISTS config_msdapl_webapp ;

CREATE TABLE IF NOT EXISTS config_msdapl_webapp (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  config_key VARCHAR(60) NOT NULL,
  config_value VARCHAR(4000) NULL,
  PRIMARY KEY (id))
ENGINE = MyISAM;

CREATE UNIQUE INDEX config_key_UNIQUE ON config_msdapl_webapp (config_key ASC);

