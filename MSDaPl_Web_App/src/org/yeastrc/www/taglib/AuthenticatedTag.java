package org.yeastrc.www.taglib;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public final class AuthenticatedTag extends TagSupport {

	public int doStartTag() throws JspException{
		try {
			HttpSession ses = pageContext.getSession();
			
			// They are not authenticated
			if (ses == null || ses.getAttribute("user") == null) {
				return SKIP_BODY;
			}
			
			// They are authenticated
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