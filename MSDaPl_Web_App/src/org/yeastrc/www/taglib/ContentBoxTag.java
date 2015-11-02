package org.yeastrc.www.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.HttpServletRequest;

public final class ContentBoxTag extends TagSupport {
	// Declare + initialize width w/ our default
	private int width = 0;
	
	private boolean useRelativeWidth = false;

	// Declare + initialize scheme w/ our default scheme
	private String scheme = null;

	// The title to use in this text area
	private String title = null;
	
	// Whether or not this content area is wholly centered, defaults to true
	private boolean centered = true;

	// Set the width of this content area
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setWidthRel(String val) {
	    useRelativeWidth = Boolean.valueOf(val);
	}

	// Set the title to display for this text area
	public void setTitle(String title) {
		this.title = title;
	}

	// Set whether or not this entire content are is centered on the page (or its context)
	public void setCentered(String val) {
		if (val.equals("true")) { this.centered = true; }
		else if (val.equals("false")) { this.centered = false; }
	}

	// Set the scheme (color scheme) to use for this content box area
	public void setScheme(String val) {
		this.scheme = val;
	}

	public int doStartTag() throws JspException {
		try{
			// Get our writer for the JSP page using this tag
			JspWriter writ = pageContext.getOut();
			
			// Get the ServletContext so that we can extract the path
			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
			String path = req.getServletPath();
			
			// Get the scheme to use.
			if (this.scheme == null) {
				String[] splitPath = path.split("/");
				if (splitPath.length > 1) {
					String dir = splitPath[splitPath.length - 2];
				
					if (dir.equals("register")) { this.scheme = "register"; }
					else if(dir.equals("login")) { this.scheme = "login"; }
					else if(dir.equals("project")) { this.scheme = "project"; }
					else if(dir.equals("about")) { this.scheme = "about"; }
					else if(dir.equals("account")) { this.scheme = "account"; }
					else { this.scheme = "search"; }
				} else {
					this.scheme = "search";
				}
			}
			
			// If this is centered, do center tags.
			if (this.centered) {
				writ.print("<CENTER>");
			}

			// If we were supplied w/ a title, print the title box area
			if (this.title != null) {
				writ.print("<DIV CLASS=\"" + scheme + "_header\"");
				if (this.width > 0) {
				    String w = String.valueOf(this.width);
				    if(useRelativeWidth)    w = w + "%";
					writ.print(" STYLE=\"WIDTH:" + w + "\"");
				}
				writ.print(">");
				writ.print("<CENTER>" + this.title + "</CENTER>");
				writ.print("</DIV>");
			}

			
			writ.print("<DIV CLASS=\"" + scheme + "\"");
			if (this.width > 0) {
			    String w = String.valueOf(this.width);
                if(useRelativeWidth)    w = w + "%";
				writ.print(" STYLE=\"WIDTH:" + w + "\"");
			}
			writ.print(" ALIGN=\"left\">");

		}
		catch (Exception e) {
			throw new JspException(e.toString());
		}
		return (EVAL_BODY_INCLUDE);
	}

	public int doEndTag() throws JspException {
		try {
			pageContext.getOut().print("</DIV>");
			if (this.centered) { pageContext.getOut().print("</CENTER>"); }
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