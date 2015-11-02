package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.MsSearch;

public class ProteinInferToExperimentMapper {

	private ProteinInferToExperimentMapper() {}
	
	public static List<Integer> map (int pinferId) {
		
		Set<Integer> experimentIds = new HashSet<Integer>();
		
		List<Integer> searchIds = ProteinferDAOFactory.instance().getProteinferRunDao().loadSearchIdsForProteinferRun(pinferId);
		
		MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
		
        for(int searchId: searchIds) {
            MsSearch search = searchDao.loadSearch(searchId);
            int experimentId = search.getExperimentId();
            experimentIds.add(experimentId);
        }
		return new ArrayList<Integer>(experimentIds);
	}
}
