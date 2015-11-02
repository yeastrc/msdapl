/**
 * SpeciesChecker.java
 * @author Vagisha Sharma
 * Apr 20, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.www.compare.dataset.Dataset;

/**
 * 
 */
public class SpeciesChecker {

	private SpeciesChecker() {}
	
	public static boolean isSpeciesYeast(List<? extends Dataset> datasets) throws Exception {


		Set<Integer> notYeastExpts = new HashSet<Integer>();

		ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
		MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();

		for(Dataset dataset: datasets) {
			List<Integer> searchIds = runDao.loadSearchIdsForProteinferRun(dataset.getDatasetId());
			if(searchIds != null) {
				for(int searchId: searchIds) {

					MsSearch search = searchDao.loadSearch(searchId);

					if(notYeastExpts.contains(search.getExperimentId())) // if we have already seen this and it is not yeast go on looking
						continue;

					MSJob job = MSJobFactory.getInstance().getMsJobForExperiment(search.getExperimentId());

					if(job.getTargetSpecies() == 4932) {
						return true;
					}
					else 
						notYeastExpts.add(search.getExperimentId());
				}
			}
		}
		return false;
	}
}
