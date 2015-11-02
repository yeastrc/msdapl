/**
 * 
 */
package org.yeastrc.www.proteinfer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.nrseq.CommonNameCacheDAO;

/**
 * ProteinCommonNameFilter.java
 * @author Vagisha Sharma
 * Jul 14, 2010
 * 
 */
public class ProteinCommonNameFilter {

	private ProteinferProteinDAO protDao = null; 
	
    private static ProteinCommonNameFilter instance;
    
    private ProteinCommonNameFilter() {
    	protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
    }
    
    public static ProteinCommonNameFilter getInstance() {
    	if(instance == null)
    		instance = new ProteinCommonNameFilter();
    	return instance;
    }

    public List<Integer> filterForProtInferByCommonName(int pinferId,
            List<Integer> allProteinIds, String commonNameLike) {
        
    	Set<String> reqNames = new HashSet<String>();
        String[] tokens = commonNameLike.split(",");
        for(String tok: tokens)
        	reqNames.add(tok.trim());
        
        List<Integer> nrseqProteinIds = CommonNameCacheDAO.getInstance().getMatches(new ArrayList<String>(reqNames));
        
        List<Integer> filtered = new ArrayList<Integer>();
        
        // get the corresponding protein inference protein ids
        if(nrseqProteinIds.size() > 0) {
            List<Integer> piProteinIds = protDao.getProteinIdsForNrseqIds(pinferId, new ArrayList<Integer>(nrseqProteinIds));
            Collections.sort(piProteinIds);
            for(int id: allProteinIds) {
                if(Collections.binarySearch(piProteinIds, id) >= 0)
                    filtered.add(id);
            }
        }
        
        return filtered;
    }
   
    public List<Integer> filterNrseqIdsByCommonName(List<Integer> allNrseqIds, String commonNameLike) throws SQLException {
        
    	Set<String> reqNames = new HashSet<String>();
        String[] tokens = commonNameLike.split(",");
        for(String tok: tokens)
        	reqNames.add(tok.trim());
        
        List<Integer> nrseqProteinIds = CommonNameCacheDAO.getInstance().getMatches(new ArrayList<String>(reqNames));
        
        if(nrseqProteinIds == null || nrseqProteinIds.size() == 0)
            return new ArrayList<Integer>(0); // no matching nrseq IDs found

        Collections.sort(nrseqProteinIds);
        
        List<Integer> matching = new ArrayList<Integer>();
        for(int nrseqId: allNrseqIds) {
            if(Collections.binarySearch(nrseqProteinIds, nrseqId) >= 0)
                matching.add(nrseqId);
        }
        return matching;
    }
}
