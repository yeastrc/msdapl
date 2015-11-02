/**
 * Emailer.java
 * @author Vagisha Sharma
 * Oct 20, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.yeastrc.www.user.User;

/**
 * 
 */
public class Emailer {

	private static final Logger log  = Logger.getLogger(Emailer.class);
	
	private static Emailer instance;
	
	private Emailer() {}
	
	public static synchronized Emailer getInstance() {
		if(instance == null)
			instance = new Emailer();
		return instance;
	}
	
	public void emailJobQueued(MsJob job, User user) {
		
		try {
            // set the SMTP host property value
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", "localhost");

            // create a JavaMail session
            javax.mail.Session mSession = javax.mail.Session.getInstance(properties, null);

            // create a new MIME message
            MimeMessage message = new MimeMessage(mSession);

            // set the from address
            Address fromAddress = new InternetAddress(ApplicationProperties.getNoreplySender());
            message.setFrom(fromAddress);

            // set the to address
            Address[] toAddress = InternetAddress.parse( user.getResearcher().getEmail() );
            message.setRecipients(Message.RecipientType.TO, toAddress);

            // set the subject
            message.setSubject(getEmailSubject(job));

            // set the message body
            String text = getEmailBody(job);

            message.setText(text);

            // send the message
            Transport.send(message);

        } catch (Exception e) { log.error("Error sending email", e); }
	}

	private String getEmailBody(MsJob job) {
		
		StringBuilder buf = new StringBuilder();
		buf.append("A MSDaPl data upload request has been submitted for you.\n");
		buf.append("If you did not make this request please contact the MSDaPl team.\n\n");
		
		buf.append("Job Details:\n");
		buf.append(job.toString()+"\n\n");

		buf.append("Thank you,\nMSDaPl Team\n");
		
		return buf.toString();
	}

	private String getEmailSubject(MsJob job) {
		return "MSDaPl data upload submitted from hermie. Job ID: "+job.getId();
	}
}
