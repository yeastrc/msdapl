package org.yeastrc.config_web;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class GlobalAdminEmailConfigCache {
	
	private final Logger log = Logger.getLogger(GlobalAdminEmailConfigCache.class);
	

	//  private constructor
	private GlobalAdminEmailConfigCache() {}
	
	private static final GlobalAdminEmailConfigCache instance = new GlobalAdminEmailConfigCache();
	
	
	public static GlobalAdminEmailConfigCache getInstance() {
		return instance;
	}

	
	public void initIfNotInitialized() {
		
		if ( ! configInitialized ) {
			
			getFromDB();
		}
	}


	public void getFromDB()  {
		
		try {
			
			ConfigWebDAO configWebDAO = ConfigWebDAO.getInstance();
			
			String propertyFile = "/" + ConfigWebKeyConstants.SECONDARY_CONFIG_PROPERTIES_FILE;


			try {

				globalAdminEmail = configWebDAO.getStringValueForKey( ConfigWebKeyConstants.GLOBAL_ADMIN_EMAIL );
				
			} catch ( Exception ex ) {
				
				log.error( "Database error getting key '" + ConfigWebKeyConstants.GLOBAL_ADMIN_EMAIL 
						+ "' from config table" );
			}

            
            if ( globalAdminEmail != null ) {
            	
            	globalAdminEmail = globalAdminEmail.trim();
            }
            
			if ( StringUtils.isNotEmpty( globalAdminEmail ) ) {
				
				log.info( "Using the value of '" + globalAdminEmail + "' for the key '" 
						+ ConfigWebKeyConstants.GLOBAL_ADMIN_EMAIL 
						+ "' from the config table.");
				
			} else {
				
		        Properties props = new Properties();
		        InputStream propertyInputStream = null;
		        
		        try {
		        	
					URL propURL = this.getClass().getResource( propertyFile );

					if ( propURL != null ) {

						log.info( "Config Dir file " + propertyFile + "  propURL.getFile() = " + propURL.getFile() );

					} else {
						
						log.info( "propURL == null" );
					}
		        	
		    		propertyInputStream = this.getClass().getResourceAsStream( propertyFile );
		    		

		    		if ( propertyInputStream == null ) {

		    			String msg = "file '" + propertyFile + "'  not found" ;
		    			
		    			log.error( msg );
		    			
		    			throw new RuntimeException( msg );

		    		} else {


		    			props.load(propertyInputStream);

		    			globalAdminEmail = props.getProperty( ConfigWebKeyConstants.GLOBAL_ADMIN_EMAIL );

		    			if ( StringUtils.isNotEmpty( globalAdminEmail ) ) {

		    				log.info( "Using the value of '" + globalAdminEmail + "' for the key '" 
		    						+ ConfigWebKeyConstants.GLOBAL_ADMIN_EMAIL 
		    						+ "' from the config table.");
		    			}

		    		}
		        }
		        catch (IOException e) {
		        	
		        	String msg = "Error reading properties file " + propertyFile;
		        	
		            log.error( msg, e);
		            
		            throw new RuntimeException( msg, e );
		        }
		        finally {
		        	if(propertyInputStream != null) try {propertyInputStream.close();} catch(IOException e){}
		        }
				
	            
	            if ( globalAdminEmail != null ) {
	            	
	            	globalAdminEmail = globalAdminEmail.trim();
	            }
	            
				if ( StringUtils.isEmpty( globalAdminEmail ) ) {

					String msg = "'" +  ConfigWebKeyConstants.GLOBAL_ADMIN_EMAIL 
							+ "' cannot be empty in both the config table and in the properties file '"
							+ ConfigWebKeyConstants.SECONDARY_CONFIG_PROPERTIES_FILE + "'.";
					log.error( msg );
					throw new RuntimeException( msg );
				}
			
			}
			
			configInitialized = true;

			
		} catch ( Exception ex ) {
			
			String msg = "Exception getting Global Admin Email Config from Database or from Properties file  '"
							+ ConfigWebKeyConstants.SECONDARY_CONFIG_PROPERTIES_FILE + "'.";
			
			log.error( msg, ex );
			
			throw new RuntimeException( msg, ex );
		}
		
		
	}

	

	private boolean configInitialized = false;



	
	private String globalAdminEmail;


	public String getGlobalAdminEmail() {
		checkIfConfigInitialized();
		return globalAdminEmail;
	}
	
	
	private void checkIfConfigInitialized() {
		
		if ( ! configInitialized ) {
			
			String msg = "retrieving a value from GlobalAdminEmailConfigCache before it is initialized";
			
			log.error( msg );
			
			throw new RuntimeException( msg );
		}
	}
	
}
