/*
 * Projects.java
 *
 * Created February 4, 2004
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.project;

import java.sql.SQLException;

import org.yeastrc.data.InvalidIDException;



/**
 * This class provides static methods for obtaining Project objects
 *
 * @version 2004-02-04
 * @author Michael Riffle <mriffle@u.washington.edu>
 */
public class ProjectFactory {
	
	/**
	 * Get a Project object from the supplied project ID.  The true class for the Project returned
	 * is the type of Project it is (e.g. Collaboration or Training)
	 * @param projectID The project ID to return
	 * @return The project corresponding to that ID.
	 */
	public static Project getProject(int projectID) throws SQLException, InvalidIDException {
		return ProjectDAO.instance().load(projectID);
	}

}