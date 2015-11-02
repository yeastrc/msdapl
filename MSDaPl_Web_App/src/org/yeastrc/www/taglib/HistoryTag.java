package org.yeastrc.www.taglib;

import java.io.IOException;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;
import org.yeastrc.www.user.*;

public final class HistoryTag extends TagSupport {

	// Whether or not to save the page containing this tag to the User's history.
	// Defaults to true
	private boolean save = true;
	
	// The number of links to show, including the current page, defaults to 5
	private int numLinks = 5;
	
	public static final String NO_HISTORY_ATTRIB = "HistoryTag_noHistory";
	
	/**
	 * Whether or not to save this page to the History
	 * Defaults to true
	 * @param save Whether or not to save this page to the History
	 */
	public void setSave(boolean save) { this.save = save; }
	
	/**
	 * The number of links to show on the page, including the current page
	 * @param num The number of links to show
	 */
	public void setNumber(int num) { this.numLinks = num; }
	

	public int doStartTag() throws JspException{
		try {
			// Get our Response & Request
			HttpServletResponse response = (HttpServletResponse)(pageContext.getResponse());
			HttpServletRequest request = (HttpServletRequest)(pageContext.getRequest());

			// Get our writer for the JSP page using this tag
			JspWriter writ = pageContext.getOut();

			// The string we're going to write to the page
			String retString = "";
			
			// Get our session
			HttpSession ses = pageContext.getSession();
			
			History history = (History)(ses.getAttribute("history"));
			if (history == null) {
				history = new History();
				ses.setAttribute("history", history);
			}			
			
			if (this.save) {
				String title = (String)(request.getAttribute("title"));
				if(request.getAttribute(NO_HISTORY_ATTRIB) != null)
					title = null;
				if (title != null) {
					String url = "";
					String uri = request.getRequestURI();
					String qstr = request.getQueryString();
					
					if (qstr != null) {
						url = uri + "?" + qstr + "&history=true";
					} else {
						
						// If there is an ID set, make sure to add that to the query string (if it's not already there)
						// I haven't decided if this is a hack or not yet, or a framework decision
						if (request.getParameter("ID") != null) {
							qstr = "ID=" + request.getParameter("ID");
							url = uri + "?" + qstr + "&history=true";
						} else {
							url = uri + "?history=true";
						}
					}
					
					// The "history" request param will only be set if the user clicked on a previous link
					// displayed on their history.  If this is the case, set the last link to that link and
					// do not save this current link (as it's already in the history!)
					if (request.getParameter("history") != null) {
						history.setLast(title, url);
					} else {
						history.addLink(url, title);
					}
				}
			}

			List historyList = history.getList(this.numLinks);
			Iterator iter = historyList.iterator();

			/* Treat the first node (most recent) differently **/
			if (iter.hasNext()) {
				History.HistoryNode hisNode = (History.HistoryNode)(iter.next());
				retString = "<B>";
				retString += hisNode.getTitle();
				retString += "</B>";
			}
			
			/* Do the rest of the links */
			while (iter.hasNext()) {
				History.HistoryNode hisNode = (History.HistoryNode)(iter.next());
				
				retString = "<A HREF=\"" + response.encodeURL(hisNode.getURL()) + "\">" + hisNode.getTitle() + "</A> -> " + retString;
			}

			retString = "<font style=\"font-size:10pt;\"><NOBR>Recent pages: " + retString + "</NOBR></font>";
			writ.print(retString);

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