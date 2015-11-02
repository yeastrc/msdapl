/**
 * ProteinInferToProjectMapper.java
 * @author Vagisha Sharma
 * Mar 24, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.experiment.ProjectExperimentDAO;

/**
 * 
 */
public class ProteinInferToProjectMapper {

	private ProteinInferToProjectMapper() {}
	
	private static final Logger log = Logger.getLogger(ProteinInferToProjectMapper.class.getName());
	
	public static List<Integer> map (int pinferId) {
		
		Set<Integer> projectIds = new HashSet<Integer>();
		
		ProjectExperimentDAO projExptDao = ProjectExperimentDAO.instance();
		
		List<Integer> experimentIds = ProteinInferToExperimentMapper.map(pinferId);
		for(int experimentId: experimentIds) {
            int projectId = 0;
			try {
				projectId = projExptDao.getProjectIdForExperiment(experimentId);
			} catch (SQLException e) {
				log.error("Error getting projectId for experiment: "+experimentId, e);
			}
            if(projectId > 0)
                projectIds.add(projectId);
        }
		
		return new ArrayList<Integer>(projectIds);
	}
}
