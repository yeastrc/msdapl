/**
 * ApplicationProperties.java
 * @author Vagisha Sharma
 * Jun 30, 2011
 */
package org.yeastrc.properties;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.run.PeakStorageType;

import com.ibatis.common.resources.Resources;

/**
 * 
 */
public class ApplicationProperties {

	private static final Logger log = Logger.getLogger(ApplicationProperties.class);
    
    private static boolean runPercOption;
    private static String runPercDir;
    
    private static String pathToR;
    
    static {
        Properties props = new Properties();
        Reader reader = null;
        String file = "application.properties";
        
        try {
        	
            reader = Resources.getResourceAsReader(file);
            props.load(reader);
            
            String value = props.getProperty("run.perc.option", "false");
            runPercOption = Boolean.parseBoolean(value);
            if(runPercOption)
            	log.info("Option to run Percolator from the interface will be available");
            
            runPercDir = props.getProperty("run.perc.dir");
            if(runPercOption) {
            	log.info("Results from running Percolator will be stored in "+runPercDir);
            }
            
            pathToR = props.getProperty("path.R", "");
            if(pathToR.trim().length() > 0) {
            	log.info("R is located here: "+pathToR);
            }
            
        }
        catch (IOException e) {
            log.error("Error reading properties file "+file, e);
        }
        finally {
        	if(reader != null) try {reader.close();} catch(IOException e){}
        }
        
    }
    
    private ApplicationProperties() {}

	public static boolean canRunPercolator() {
		return runPercOption;
	}

	public static String getPercolatorResultDir() {
		return runPercDir;
	}

	public static String getPathToR() {
		return pathToR;
	}
	
	public static boolean hasPathToR() {
		return (pathToR != null && pathToR.trim().length() > 0);
	}
	
}
