package org.yeastrc.www.taglib;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.util.ResponseUtils;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;

public final class ProjectLinkTag extends TagSupport {

	
	// ID of the project
	private int projectId;
	
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int doStartTag() throws JspException{
		
		try {

			Project project = null;
			try { project = ProjectFactory.getProject(projectId); }
			catch (InvalidIDException e) { project = null; }
			
			if(project == null) {
				ResponseUtils.write(pageContext, "Unknown project ID "+projectId);
				return SKIP_BODY;
			}
			
			ServletContext context = pageContext.getServletContext();
	        String contextPath = context.getContextPath();
	        
			String output = "";
	        
	        try {
	        	output =  "<a href=\""+contextPath+"/viewProject.do?ID=" + projectId + "\" >";
	        	output += project.getTitle();
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