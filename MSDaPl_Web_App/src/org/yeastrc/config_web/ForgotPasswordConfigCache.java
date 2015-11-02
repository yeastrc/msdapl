package org.yeastrc.config_web;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class ForgotPasswordConfigCache {
	
	private final Logger log = Logger.getLogger(ForgotPasswordConfigCache.class);
	

	//  private constructor
	private ForgotPasswordConfigCache() {}
	
	private static final ForgotPasswordConfigCache instance = new ForgotPasswordConfigCache();
	
	
	public static ForgotPasswordConfigCache getInstance() {
		return instance;
	}

	
	public void initIfNotInitialized() {
		
		if ( ! configInitialized ) {
			
			getFromDB();
		}
	}


	public void getFromDB()  {
		
		try {
			
			forgotPasswordConfigured = false;

			ConfigWebDAO configWebDAO = ConfigWebDAO.getInstance();

			try {

				String forgotPasswordConfiguredString = configWebDAO.getStringValueForKey( ConfigWebKeyConstants.FORGOT_PASSWORD_CONFIGURED );
				

				if ( ConfigWebKeyConstants.FORGOT_PASSWORD_CONFIGURED_TRUE_VALUE.equalsIgnoreCase(forgotPasswordConfiguredString) ) {

					forgotPasswordConfigured = true;
				};
				
			} catch ( Exception ex ) {

				log.error( "Database error getting key '" + ConfigWebKeyConstants.FORGOT_PASSWORD_CONFIGURED 
						+ "' from config table" );
			}


			
			if ( forgotPasswordConfigured ) {
			
				mailSmtpHost = configWebDAO.getStringValueForKey( ConfigWebKeyConstants.MAIL_SMTP_HOST );



				forgotPasswordFromEmailAddress = configWebDAO.getStringValueForKey( ConfigWebKeyConstants.FORGOT_PASSWORD_FROM_EMAIL_ADDRESS );
				forgotPasswordSubject = configWebDAO.getStringValueForKey( ConfigWebKeyConstants.FORGOT_PASSWORD_SUBJECT );
				forgotPasswordMessageBodyPrefix = configWebDAO.getStringValueForKey( ConfigWebKeyConstants.FORGOT_PASSWORD_MESSAGE_BODY_PREFIX );
				forgotPasswordMessageBodyPostfix = configWebDAO.getStringValueForKey( ConfigWebKeyConstants.FORGOT_PASSWORD_MESSAGE_BODY_POSTFIX );

				if ( StringUtils.isEmpty( mailSmtpHost ) ) {
					
					String msg = "'" +  ConfigWebKeyConstants.MAIL_SMTP_HOST + "' cannot be empty if '" 
							+  ConfigWebKeyConstants.FORGOT_PASSWORD_CONFIGURED + "' is 'true'";
					log.error( msg );
					throw new RuntimeException( msg );
				}


				if ( StringUtils.isEmpty( forgotPasswordFromEmailAddress ) ) {
					
					String msg = "'" +  ConfigWebKeyConstants.FORGOT_PASSWORD_FROM_EMAIL_ADDRESS + "' cannot be empty if '" 
							+  ConfigWebKeyConstants.FORGOT_PASSWORD_CONFIGURED + "' is 'true'";
					log.error( msg );
					throw new RuntimeException( msg );
				}
				
				if ( StringUtils.isEmpty( forgotPasswordSubject ) ) {
					
					String msg = "'" +  ConfigWebKeyConstants.FORGOT_PASSWORD_SUBJECT + "' cannot be empty if '" 
							+  ConfigWebKeyConstants.FORGOT_PASSWORD_CONFIGURED + "' is 'true'";
					log.error( msg );
					throw new RuntimeException( msg );
				}

				if ( StringUtils.isEmpty( forgotPasswordMessageBodyPrefix ) ) {
					
					String msg = "'" +  ConfigWebKeyConstants.FORGOT_PASSWORD_MESSAGE_BODY_PREFIX + "' cannot be empty if '" 
							+  ConfigWebKeyConstants.FORGOT_PASSWORD_CONFIGURED + "' is 'true'";
					log.error( msg );
					throw new RuntimeException( msg );
				}

				if ( StringUtils.isEmpty( forgotPasswordMessageBodyPostfix ) ) {
					
					String msg = "'" +  ConfigWebKeyConstants.MAIL_SMTP_HOST + "' cannot be empty if '" 
							+  ConfigWebKeyConstants.FORGOT_PASSWORD_MESSAGE_BODY_POSTFIX + "' is 'true'";
					log.error( msg );
					throw new RuntimeException( msg );
				}
				
			}
			
			configInitialized = true;

			
		} catch ( Exception ex ) {
			
			String msg = "Exception getting Forgot Password Config from Database";
			
			log.error( msg, ex );
			
			throw new RuntimeException( msg, ex );
		}
		
		
	}

	

	private boolean configInitialized = false;




	private boolean forgotPasswordConfigured = false;
	
	private String mailSmtpHost;
	
	private String forgotPasswordFromEmailAddress;


	private String forgotPasswordSubject;
	private String forgotPasswordMessageBodyPrefix;
	private String forgotPasswordMessageBodyPostfix;
	
	

	public boolean getForgotPasswordConfigInitialized() {
		return configInitialized;
	}
	
	public boolean getForgotPasswordConfigured() {
		checkIfConfigInitialized();
		return forgotPasswordConfigured;
	}
	
	public boolean isForgotPasswordConfigInitialized() {
		return configInitialized;
	}
	public boolean isForgotPasswordConfigured() {
		checkIfConfigInitialized();
		return forgotPasswordConfigured;
	}
	public String getMailSmtpHost() {
		checkIfConfigInitialized();
		return mailSmtpHost;
	}
	public String getForgotPasswordFromEmailAddress() {
		checkIfConfigInitialized();
		return forgotPasswordFromEmailAddress;
	}
	public String getForgotPasswordSubject() {
		checkIfConfigInitialized();
		return forgotPasswordSubject;
	}
	public String getForgotPasswordMessageBodyPrefix() {
		checkIfConfigInitialized();
		return forgotPasswordMessageBodyPrefix;
	}
	public String getForgotPasswordMessageBodyPostfix() {
		checkIfConfigInitialized();
		return forgotPasswordMessageBodyPostfix;
	}
	
	
	private void checkIfConfigInitialized() {
		
		if ( ! configInitialized ) {
			
			String msg = "retrieving a value from ForgotPasswordConfigCache before it is initialized";
			
			log.error( msg );
			
			throw new RuntimeException( msg );
		}
	}
}
