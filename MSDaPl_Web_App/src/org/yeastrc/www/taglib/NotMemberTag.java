/* NotMemberTag.java
 * Created on Mar 25, 2004
 */
package org.yeastrc.www.taglib;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import org.yeastrc.www.user.*;
/**
 * Tag whose body will only be executed if the User is NOT a member of the supplied group name.
 * If the user is in the "administrators" group, the body will never be evaluated
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Mar 25, 2004
 *
 */
public final class NotMemberTag extends TagSupport {

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
				return EVAL_BODY_INCLUDE;
			}
			
			User user = (User)(ses.getAttribute("user"));
			Groups groupMan = Groups.getInstance();
			int rID = user.getResearcher().getID();
			
			// If the user is a member of "administrators", never execute the body
			if (groupMan.isMember(rID, "administrators"))
				return SKIP_BODY;
			
			// "any" was supplied as a group name
			if (groupName.equals("any")) {
				if (groupMan.isInAGroup(rID))
					return SKIP_BODY;
				else
					return EVAL_BODY_INCLUDE;
			}


			if (groupMan.isMember(rID, this.groupName))
				return SKIP_BODY;
			
			// Not a member of the group.
			return EVAL_BODY_INCLUDE;

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
