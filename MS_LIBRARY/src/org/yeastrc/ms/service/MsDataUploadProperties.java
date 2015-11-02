/**
 * MsDataUploadProperties.java
 * @author Vagisha Sharma
 * Jun 1, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.run.PeakStorageType;

import com.ibatis.common.resources.Resources;

/**
 * 
 */
public class MsDataUploadProperties {

    private static final Logger log = Logger.getLogger(MsDataUploadProperties.class);
    
    private static boolean doSqtBackup;
    private static String backupDirectory;
    
    private static PeakStorageType peakStorageType;
    
    private static boolean useIbatisDAO = true;
    
    private static boolean checkPeptideProteinMatches = false;
    
    static {
        Properties props = new Properties();
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("msDataDB.properties");
            props.load(reader);
            String value = props.getProperty("db.peakdata.storage");
            peakStorageType = PeakStorageType.instance(value);
            if(peakStorageType != null)
            	log.info("PeakStorageType is "+peakStorageType.name());
            
            value = props.getProperty("dao.ibatis", "true");
            if(value != null) {
            	useIbatisDAO = Boolean.parseBoolean(value);
            }
            log.info("Use Ibatis DAO classes: "+useIbatisDAO);
            
            backupDirectory = props.getProperty("backup.dir");
            doSqtBackup = Boolean.parseBoolean(props.getProperty("backup.sqt"));
            
            value = props.getProperty("interact.pepxml.checkpeptideproteinmatches");
            checkPeptideProteinMatches = Boolean.parseBoolean(value);
            
        }
        catch (IOException e) {
            log.error("Error reading properties file msDataDB.properties", e);
        }
        finally {
        	if(reader != null) try {reader.close();} catch(IOException e){}
        }
        
    }
    
    private MsDataUploadProperties() {}
    
    public static PeakStorageType getPeakStorageType() {
        return peakStorageType;
    }
    
    public static boolean useIbatisDAO() {
    	return useIbatisDAO;
    }
    
    public static String getBackupDirectory() {
        return backupDirectory;
    }
    
    public static boolean doSqtBackup() {
    	return doSqtBackup;
    }
    
    public static boolean getCheckPeptideProteinMatches() {
        return checkPeptideProteinMatches;
    }
    
}
