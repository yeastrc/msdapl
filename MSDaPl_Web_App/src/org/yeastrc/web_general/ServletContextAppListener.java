package org.yeastrc.web_general;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import org.yeastrc.config_web.ForgotPasswordConfigCache;
import org.yeastrc.config_web.GlobalAdminEmailConfigCache;
import org.yeastrc.constants_web.MainWebConstants;


//MainWebConstants

/**
 * This class is loaded and the method "contextInitialized" is called when the web application is first loaded by the container
 *
 */
public class ServletContextAppListener  extends HttpServlet implements ServletContextListener {

	private static Logger log = Logger.getLogger( ServletContextAppListener.class );
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {

		ServletContext context = event.getServletContext();

		String contextPath = context.getContextPath();

		context.setAttribute( MainWebConstants.APP_CONTEXT_CONTEXT_PATH, contextPath );

		CurrentContext.setCurrentWebAppContext( contextPath );


		log.warn( "INFO:  !!!!!!!!!!!!!!!   web app  'MSDaPl'  Starting Up  !!!!!!!!!!!!!!!!!!!! " );

		log.warn( "INFO: Application context values set.  Key = " + MainWebConstants.APP_CONTEXT_CONTEXT_PATH + ": value = " + contextPath
				+ "" );

		
		try {

			ForgotPasswordConfigCache.getInstance().initIfNotInitialized();

			ForgotPasswordConfigCache forgotPasswordConfigCache = ForgotPasswordConfigCache.getInstance();

			forgotPasswordConfigCache.initIfNotInitialized();

			context.setAttribute( MainWebConstants.FORGOT_PASSWORD_CONFIG_CACHE_OBJECT , forgotPasswordConfigCache );

			GlobalAdminEmailConfigCache globalAdminEmailConfigCache = GlobalAdminEmailConfigCache.getInstance();

			globalAdminEmailConfigCache.initIfNotInitialized();

			context.setAttribute( MainWebConstants.GLOBAL_ADMIN_EMAIL_CONFIG_CACHE_OBJECT, globalAdminEmailConfigCache );
			
		} catch ( RuntimeException ex ) {
			
			log.error("Error in initializing MSDaPl Web App: Exception: " + ex.toString(), ex );
			
			throw ex;
		}

		log.warn( "INFO:  !!!!!!!!!!!!!!!   web app  'MSDaPl' Finished Starting Up  !!!!!!!!!!!!!!!!!!!! " );


	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {

		//ServletContext context = event.getServletContext();


	}
}
