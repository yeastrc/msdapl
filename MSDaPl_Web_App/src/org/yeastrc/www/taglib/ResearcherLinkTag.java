package org.yeastrc.www.taglib;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.util.ResponseUtils;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.project.Researcher;

public final class ResearcherLinkTag extends TagSupport {

	
	// ID of the researcher
	private int researcherId;
	
	public void setResearcherId(int researcherId) {
		this.researcherId = researcherId;
	}

	public int doStartTag() throws JspException{
		
		try {

			Researcher researcher = new Researcher();
			try { researcher.load(this.researcherId); }
			catch (InvalidIDException e) { researcher = null; }
			
			if(researcher == null) {
				ResponseUtils.write(pageContext, "Unknown researcher ID "+researcherId);
				return SKIP_BODY;
			}
			
			ServletContext context = pageContext.getServletContext();
	        String contextPath = context.getContextPath();
	        
			String output = "";
	        
	        try {
	        	output =  "<a href=\""+contextPath+"/viewResearcher.do?id=" + researcherId + "\" >";
	        	output += researcher.getFirstName()+"&nbsp;"+researcher.getLastName();
	        	output += "</a>";
	        	
	        } catch (Exception e) {
	        	output = "ERROR";
	        }
	        
	        JspWriter writer = pageContext.getOut();
	        writer.print(output);
			return SKIP_BODY;

		}
		catch (Exception e) {
			throw new JspException("Error: Exception while writing to client" + e.getMessage());
		}
	}
}