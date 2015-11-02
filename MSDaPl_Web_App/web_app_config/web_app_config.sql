
--   This file contains configuration for the web app 

--   This cofiguration is stored in the table mainDb.config_msdapl_webapp



use mainDb;

INSERT INTO config_msdapl_webapp ( config_key, config_value ) VALUES ( 'mail.smtp.host', 'localhost' );

--   For this, specify an email address to contact for user issues.
--   There is a default in the properties file YRCMessageResources.properties, currently set to mriffle@u.washington.edu
INSERT INTO config_msdapl_webapp ( config_key, config_value ) VALUES ( 'global.admin.email', '<email address>' );


--   Set this to 'true' if also set all the "forgot.password..." properties below
INSERT INTO config_msdapl_webapp ( config_key, config_value ) VALUES ( 'forgot.password.configured', 'false' );

-- INSERT INTO config_msdapl_webapp ( config_key, config_value ) VALUES ( 'forgot.password.configured', 'true' );

INSERT INTO config_msdapl_webapp ( config_key, config_value ) 
	VALUES ( 'forgot.password.from.email.address', 'do_not_reply@<your.domain>' );

INSERT INTO config_msdapl_webapp ( config_key, config_value ) 
	VALUES ( 'forgot.password.subject', 'MSDaPl Registration Info' );

INSERT INTO config_msdapl_webapp ( config_key, config_value ) 
	VALUES ( 'forgot.password.message.body.prefix', 'Here is your login information for MSDaPl at <URL to your MSDaPl>' );

INSERT INTO config_msdapl_webapp ( config_key, config_value ) 
	VALUES ( 'forgot.password.message.body.postfix', 'Thank you,\n<Your Organization>' );


