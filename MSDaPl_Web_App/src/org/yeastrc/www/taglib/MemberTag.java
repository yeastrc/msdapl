/* MemberTag.java
 * Created on Mar 25, 2004
 */
package org.yeastrc.www.taglib;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import org.yeastrc.www.user.*;
/**
 * Tag whose body will only be executed if the User is a member of the supplied group name.
 * Note, if the user is in the "administrators" group, the body will always be evaluated
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Mar 25, 2004
 *
 */
public final class MemberTag extends TagSupport {

	private String groupName;
	
	/**
	 * Set the group name to test
	 * To test membership in any of the groups use "any"
	 * @param The name of the group to test
	 */
	public void setGroup(String groupName) {
		this.groupName = groupName;
	}

	public int doStartTag() throws JspException{
		try {
			HttpSession ses = pageContext.getSession();
			
			// They are not authenticated
			if (ses == null || ses.getAttribute("user") == null || this.groupName == null) {
				return SKIP_BODY;
			}
			
			User user = (User)(ses.getAttribute("user"));
			Groups groupMan = Groups.getInstance();
			int rID = user.getResearcher().getID();
			
			// If the user is a member of "administrators", always execute the body
			if (groupMan.isMember(rID, "administrators"))
				return EVAL_BODY_INCLUDE;
			
			// "any" was supplied as a group name
			if (groupName.equals("any")) {
				if (groupMan.isInAGroup(rID))
					return EVAL_BODY_INCLUDE;
				else
					return SKIP_BODY;
			}


			if (groupMan.isMember(rID, this.groupName))
				return EVAL_BODY_INCLUDE;
			
			// Not a member of the group.
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
