package org.yeastrc.www.taglib;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;

public final class TitleTag extends TagSupport {

	private String localeKey = Globals.LOCALE_KEY;
	private String bundle = Globals.MESSAGES_KEY;

	public int doStartTag() throws JspException{
		try {
			String baseTitle = "";		// The base title
			String suffTitle = "";		// The title to add to the base title
			
			// Get the ServletContext so that we can extract the path
			HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();

			// Get our writer for the JSP page using this tag
			JspWriter writ = pageContext.getOut();

			baseTitle = RequestUtils.message(pageContext, this.bundle, this.localeKey, "title.global");

			if (baseTitle != null) {
				String property = "";
				String path = req.getServletPath();

				String[] splitPath = path.split("/");

				// Get the end of the URL
				if (splitPath.length > 0) {
					String dir = splitPath[splitPath.length - 1];
					property = dir;

					// Get the next to the end of the URL
					if (splitPath.length > 1) {
						dir = splitPath[splitPath.length - 2];
						property = dir + "." + property;
						
						// Set this dir as a request attribute, usable by other View objects
						// which could conceivably wish to know what directory we're in
						req.setAttribute("dir", dir);
					} else {
						req.setAttribute("dir", "");
					}

					property = "title." + property;

				} else {
					req.setAttribute("dir", "");
				}

				suffTitle = RequestUtils.message(pageContext, this.bundle, this.localeKey, property);

			}
			
			// Start the title.
			writ.print("<TITLE>");

			if (baseTitle != null) {
				writ.print(baseTitle);
				
				if (suffTitle != null) {
					writ.print(" - " + suffTitle);
					
					// Set the suffix title in the request for use by other interested classes
					req.setAttribute("title", suffTitle);
				}
			}


			writ.print("</TITLE>");

		}
		catch (IOException ioe) {
			throw new JspException("Error: IOException while writing to client" + ioe.getMessage());
		}
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public void release() {
		super.release();
	}

}