/**
 * ApplicationProperties.java
 * @author Vagisha Sharma
 * Aug 18, 2009
 * @version 1.0
 */
package org.yeastrc.jqs.queue.ws;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 */
public class ApplicationProperties {

    private final static Logger log = Logger.getLogger(ApplicationProperties.class);
    private static String noreplySender = "";
    
    public static void load(InputStream propsFileStream) {
        
        Properties props = new Properties();
        try {
            props.load(propsFileStream);
            noreplySender = props.getProperty("noreply.sender");
        }
        catch (IOException e) {
            log.error("Error reading properties file: "+propsFileStream, e);
            e.printStackTrace();
        }
        finally {
        	if(propsFileStream != null) try {propsFileStream.close();} catch(IOException e){}
        }
    }

    public static String getNoreplySender() {
        return noreplySender;
    }
}
