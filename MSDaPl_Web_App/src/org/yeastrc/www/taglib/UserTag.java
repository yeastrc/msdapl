package org.yeastrc.www.taglib;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.yeastrc.www.user.User;
import org.yeastrc.project.Researcher;

public final class UserTag extends TagSupport {

	// The attribute in which we're interested in displaying about this user
	private String attr;
	
	/**
	 * Which attribute about this user do we want to display.
	 * @param attr The attribute (e.g. firstname, lastname, username, ip)
	 */
	public void setAttribute(String attr) {
		this.attr = attr;
	}

	/**
	 * It is assumed the session exists, and the user is authenticated
	 */
	public int doStartTag() throws JspException{
		try {
			// Get our writer for the JSP page using this tag
			JspWriter writ = pageContext.getOut();

			// Get our session and User objects
			HttpSession ses = pageContext.getSession();
			if (ses == null) { return SKIP_BODY; }

			User user = (User)(ses.getAttribute("user"));
			if (user == null) { return SKIP_BODY; }
			
			Researcher res = user.getResearcher();
			if (res == null) { return SKIP_BODY; }
			
			// Print out the attribute
			if (this.attr == null) { return SKIP_BODY; }
			else if (this.attr.equals("lastname")) { writ.print(res.getLastName()); }
			else if (this.attr.equals("firstname")) { writ.print(res.getFirstName()); }
			else if (this.attr.equals("username")) { writ.print(user.getUsername()); }
			else if (this.attr.equals("ip")) { writ.print(user.getLastLoginIP()); }
			
			// They are authenticated
			return SKIP_BODY;

		}
		catch (Exception e) {
			throw new JspException("Error: Exception while writing to client" + e.getMessage());
		}
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public void release() {
		super.release();
	}

}