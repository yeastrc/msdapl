/**
 * ApplicationProperties.java
 * @author Vagisha Sharma
 * Aug 18, 2009
 * @version 1.0
 */
package org.yeastrc.jqs.queue.ws;

import java.io.InputStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * 
 */
public class ApplicationPropertiesInitializer implements ServletContextListener {

    private final static Logger log = Logger.getLogger(ApplicationPropertiesInitializer.class);
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // nothing to do
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        InputStream is = sce.getServletContext().getResourceAsStream("/WEB-INF/classes/application.properties");
        ApplicationProperties.load(is);
    }
}
