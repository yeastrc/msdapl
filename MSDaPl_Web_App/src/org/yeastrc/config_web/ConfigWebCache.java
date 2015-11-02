package org.yeastrc.config_web;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

//import org.apache.log4j.Logger;

/**
 * Retrieve a value from the config web table and cache it
 *
 */
public class ConfigWebCache {

//	private static final Logger log = Logger.getLogger(ConfigWebCache.class);
			
	//  private constructor
	private ConfigWebCache() {}
	
	private static final ConfigWebCache instance = new ConfigWebCache();
	
	private static final Map<String,CachedDataHolder> cachedConfigStrings = new HashMap<String, CachedDataHolder>();
	
	
	public static ConfigWebCache getInstance() {
		return instance;
	}
	
	
	/**
	 * clear the cached values
	 */
	public void clearCache() {
		
		cachedConfigStrings.clear();
		
		ForgotPasswordConfigCache.getInstance().getFromDB();
	}
	
	/**
	 * Get and cache the value for the configKey
	 * @param configKey
	 * @return
	 * @throws SQLException
	 */
	public String getCachedStringForKey( String configKey ) throws SQLException {
		
		CachedDataHolder cachedDataHolder = cachedConfigStrings.get(configKey);
		
		if ( cachedDataHolder != null ) {
			
			return cachedDataHolder.cachedString;
		}
	
		String configValue = ConfigWebDAO.getInstance().getStringValueForKey( configKey );
		
		cachedDataHolder = new CachedDataHolder();
		
		cachedDataHolder.cachedString = configValue;
		
		cachedConfigStrings.put( configKey, cachedDataHolder );
		
		return configValue;
		
	}
	
	private class CachedDataHolder {
		
		String cachedString;
//		Integer cachedInteger;
	}
}
