/*
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.util.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;

import org.yeastrc.project.*;
import org.yeastrc.www.user.*;

import javax.mail.*;
import javax.mail.internet.*;

/**
 * Controller class for editing a project.
 */
public class SaveResearcherAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {

		String firstName;
		String lastName;
		String email;
		String degree;
		String department;
		String organization;
		String state;
		String zip;
		String country;
		boolean sendEmail;
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}

		
		// The Researcher we're creating.
		Researcher researcher = new Researcher();

		
		// Set our variables from the form
		firstName = ((EditResearcherForm)form).getFirstName();
		lastName = ((EditResearcherForm)form).getLastName();
		email = ((EditResearcherForm)form).getEmail();
		degree = ((EditResearcherForm)form).getDegree();
		department = ((EditResearcherForm)form).getDepartment();
		organization = ((EditResearcherForm)form).getOrganization();
		state = ((EditResearcherForm)form).getState();
		zip = ((EditResearcherForm)form).getZipCode();
		country = ((EditResearcherForm)form).getCountry();
		sendEmail = ((EditResearcherForm)form).getSendEmail();
		
		// Set any empty variables to null
		// Only possible empty value is zip code
		if (zip.equals("")) { zip = null; }
		
		// If they're changing their email addy, make sure the NEW one isn't already in the database
		if (UserUtils.emailExists(email) != -1) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.researcher.emailtaken"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		// Now set the values in the researcher object
		researcher.setFirstName(firstName);
		researcher.setLastName(lastName);
		researcher.setEmail(email);
		researcher.setDegree(degree);
		researcher.setDepartment(department);
		researcher.setOrganization(organization);
		researcher.setState(state);
		researcher.setZipCode(zip);
		researcher.setCountry(country);
		
		// Prepare and send the email
		User newUser = null;
		if (sendEmail) {
			// Generate a new password
			String newPassword = UserUtils.generatePassword();
			String newUsername = email;
			newUser = new User();

			// Set the password in the user and save it.
			newUser.setUsername(newUsername);
			newUser.setPassword(newPassword);

			// Generate and send the email to the user.
			try {
			   // set the SMTP host property value
			   Properties properties = System.getProperties();
			   properties.put("mail.smtp.host", "localhost");

			   // create a JavaMail session
			   javax.mail.Session mSession = javax.mail.Session.getInstance(properties, null);

			   // create a new MIME message
			   MimeMessage message = new MimeMessage(mSession);

			   // set the from address
			   Address fromAddress = new InternetAddress("do_not_reply@yeastrc.org");
			   message.setFrom(fromAddress);

			   // set the to address
				Address[] toAddress = InternetAddress.parse(email);
				message.setRecipients(Message.RecipientType.TO, toAddress);

			   // set the subject
			   message.setSubject("YRC Registration Info");

			   // set the message body
				String text = "Greetings " + firstName + " " + lastName + ",\n\n";

				text += "A new account has been created for you at http://www.yeastrc.org/ by\n";
				text += ((Researcher)(user.getResearcher())).getFirstName() + " ";
				text += ((Researcher)(user.getResearcher())).getLastName() + ".\n\n";
				text += "You can log into the site using the username and password given below to\n";
				text += "manage data for project with which you are affiliated.\n\n";
				text += "You can also use this username and password to request new collaborations,\n";
				text += "request plasmids or request training from the YRC.\n\n";
				text += "Your login information:\n";
				text += "Username: " + newUsername + "\n";
				text += "Password: " + newPassword + "\n\n";
				text += "Thank you,\nThe Yeast Resource Center\n";

				message.setText(text);

			   // send the message
			   Transport.send(message);

		   }
			catch (AddressException e) {
				// Invalid email address format
				ActionErrors errors = new ActionErrors();
				errors.add("email", new ActionMessage("error.forgotpassword.sendmailerror"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
			catch (SendFailedException e) {
				// Invalid email address format
				ActionErrors errors = new ActionErrors();
				errors.add("email", new ActionMessage("error.forgotpassword.sendmailerror"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
			catch (MessagingException e) {
				// Invalid email address format
				ActionErrors errors = new ActionErrors();
				errors.add("email", new ActionMessage("error.forgotpassword.sendmailerror"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}		
		}



		// Save the researcher to the database
		try {
			researcher.save();
		} catch (Exception e) {
			// Invalid email address format
			ActionErrors errors = new ActionErrors();
			errors.add("email", new ActionMessage("error.researcher.saveerror"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}

		if (newUser != null) {
			newUser.setResearcher(researcher);
			try { newUser.save(); }
			catch (Exception e) {
				// Invalid email address format
				ActionErrors errors = new ActionErrors();
				errors.add("email", new ActionMessage("error.researcher.usersaveerror"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		}
		
		request.setAttribute("saved", "true");

		// Go!
		return mapping.findForward("Success");


	}
	
}