package org.yeastrc.www.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.HttpServletRequest;

import org.yeastrc.www.misc.BinaryToggleBean;

public final class ColorRowTag extends TagSupport {

	// Declare + initialize scheme w/ our default scheme
	private String scheme = null;
	
	// Flag to determine if the previous row color should be repeated.
    private boolean repeat = false;

	// Set the scheme (color scheme) to use for this content box area
	public void setScheme(String val) {
		this.scheme = val;
	}
	
	public void setRepeat(boolean repeat) {
	    this.repeat = repeat;
	}

	public int doStartTag() throws JspException {
		try{
			// Get our writer for the JSP page using this tag
			JspWriter writ = pageContext.getOut();
			
			// Get the ServletContext so that we can extract the path
			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			String path = req.getServletPath();

			// Get our BinaryToggleBean from the Request
			BinaryToggleBean toggler;
			
			toggler = (BinaryToggleBean)(req.getAttribute("toggler"));
			if (toggler == null) {
				toggler = new BinaryToggleBean();
				req.setAttribute("toggler", toggler);
			}
			
			// Get the scheme to use.
			if (this.scheme == null) {
				String[] splitPath = path.split("/");
				if (splitPath.length > 1) {
					String dir = splitPath[splitPath.length - 2];
				
					if (dir.equals("register")) { this.scheme = "register"; }
					else if(dir.equals("login")) { this.scheme = "login"; }
					else if(dir.equals("project")) { this.scheme = "project"; }
				}
			}
			
			writ.print("<TR CLASS=\"" + scheme + "_");
			
			if (toggler.getToggle()) {
			    if(repeat) {
			        writ.print("B");
			        toggler.getToggle();
			    }
			    else
			        writ.print("A");
			}
			else {
			    if(repeat) {
			        writ.print("A");
			        toggler.getToggle();
			    }
			    else
			        writ.print("B");
			}
						
			writ.print("\">");

		}
		catch (Exception e) {
			throw new JspException(e.toString());
		}
		return (EVAL_BODY_INCLUDE);
	}

	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().print("</TR>");
		}
		catch (Exception e) {
			throw new JspException(e.toString());
		}
		return (EVAL_PAGE);
	}

	public void release() {
		super.release();
	}

}