/**
 * MsJobSearcher.java
 * @author Vagisha Sharma
 * Sep 10, 2010
 */
package org.yeastrc.jqs.queue.ws;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;

import com.sun.jersey.api.NotFoundException;

/**
 * 
 */
public class ProjectSearcher {

	private ProjectSearcher() {}
	
	private static ProjectSearcher instance = null;
	
	private static final Logger log = Logger.getLogger(ProjectSearcher.class);
	
	public static synchronized ProjectSearcher getInstance() {
		if(instance == null)
			instance = new ProjectSearcher();
		
		return instance;
	}
	
	public Project getMsDaPlProject(int projectId) {
		
		try {
			return ProjectFactory.getProject(projectId);
			
		} catch (SQLException e1) {
			log.error("Error searching for project ID "+projectId, e1);
			throw new ServerErrorException("Error searching for project ID "+projectId+"\n"+e1.getMessage());
			
		} catch (InvalidIDException e1) {
			log.error("Project not found. ID: "+projectId, e1);
			throw new NotFoundException("Project not found. ID: "+projectId);
		}
	}
	
	public MsProject search(int projectId) {
		Project project = getMsDaPlProject(projectId);
		return MsProject.create(project);
	}
}
